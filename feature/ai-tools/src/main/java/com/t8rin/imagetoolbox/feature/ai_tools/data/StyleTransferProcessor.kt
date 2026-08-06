/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.ai_tools.data

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.remote.DownloadManager
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralConstants
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.security.MessageDigest
import javax.inject.Inject

internal class StyleTransferProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private val mutex = Mutex()
    private val environment = OrtEnvironment.getEnvironment()

    private var predictionSession: OrtSession? = null
    private var transformSession: OrtSession? = null
    private var sessionModel: StyleTransferModel? = null
    private var cachedStyleData: List<FloatArray>? = null

    private val modelsDir: File
        get() = File(context.filesDir, NeuralConstants.DIR).apply(File::mkdirs)

    fun isDownloaded(model: NeuralModel): Boolean = StyleTransferModel.from(model)?.files?.all {
        File(modelsDir, it.name).isValidModel(
            expectedSize = it.size,
            expectedChecksum = it.checksum
        )
    } == true

    fun startDownload(model: NeuralModel): Flow<DownloadProgress> = channelFlow {
        val styleModel = checkNotNull(StyleTransferModel.from(model))
        var failure: Throwable? = null
        downloadManager.downloadZip(
            url = model.downloadLink,
            destinationPath = modelsDir.absolutePath,
            onStart = {},
            onProgress = { trySend(it) },
            onFailure = { failure = it },
            downloadOnlyNewData = false
        )
        failure?.let { throw it }
        if (!isDownloaded(model)) {
            styleModel.files.forEach { File(modelsDir, it.name).delete() }
            error("Style transfer model checksum mismatch")
        }
    }.flowOn(ioDispatcher)

    fun isModelFile(name: String?): Boolean = StyleTransferModel.entries.any { model ->
        model.files.any { it.name == name }
    }

    fun deleteModels(model: NeuralModel) {
        val styleModel = StyleTransferModel.from(model) ?: return
        if (sessionModel == styleModel) close()
        styleModel.files.forEach { File(modelsDir, it.name).delete() }
    }

    suspend fun process(
        content: Bitmap,
        model: NeuralModel,
        params: NeuralParams,
        listener: AiProgressListener
    ): Bitmap? = withContext(defaultDispatcher) {
        mutex.withLock {
            if (params.strength <= 0f) {
                return@withLock content.copy(Bitmap.Config.ARGB_8888, true)
            }

            val styleUri = params.auxiliaryImage ?: return@withLock null.also {
                listener.onError(context.getString(R.string.style_image_not_selected))
            }
            val styleModel = checkNotNull(StyleTransferModel.from(model))

            ensureSessions(styleModel)
            val prediction = checkNotNull(predictionSession)
            val transformer = checkNotNull(transformSession)

            listener.onProgress(0, 1)
            val amount = (params.strength / 100f).coerceIn(0f, 1f)
            val result = when (styleModel) {
                StyleTransferModel.Arbitrary -> processArbitrary(
                    content = content,
                    styleUri = styleUri,
                    prediction = prediction,
                    transformer = transformer,
                    amount = amount,
                    listener = listener
                )

                StyleTransferModel.MicroAst -> processMicroAst(
                    content = content,
                    styleUri = styleUri,
                    prediction = prediction,
                    transformer = transformer,
                    amount = amount,
                    listener = listener
                )

                StyleTransferModel.AesFa -> processAesFa(
                    content = content,
                    styleUri = styleUri,
                    prediction = prediction,
                    transformer = transformer,
                    amount = amount,
                    listener = listener
                )
            }
            result?.also {
                listener.onProgress(1, 1)
            }
        }
    }

    fun close() {
        predictionSession?.close()
        transformSession?.close()
        predictionSession = null
        transformSession = null
        sessionModel = null
        cachedStyleData = null
    }

    private suspend fun processArbitrary(
        content: Bitmap,
        styleUri: String,
        prediction: OrtSession,
        transformer: OrtSession,
        amount: Float,
        listener: AiProgressListener
    ): Bitmap? {
        val styleEmbedding = getStyleData(styleUri) { style ->
            listOf(predictStyle(prediction, style.scaledForPrediction(STYLE_IMAGE_SIZE)))
        }?.single() ?: return null.also {
            listener.onError(context.getString(R.string.style_image_not_selected))
        }
        val mixedEmbedding = if (amount >= 1f) {
            styleEmbedding
        } else {
            val preparedContent = content.scaledForPrediction(STYLE_IMAGE_SIZE)
            val contentEmbedding = try {
                predictStyle(prediction, preparedContent)
            } finally {
                if (preparedContent !== content) preparedContent.recycle()
            }
            FloatArray(STYLE_EMBEDDING_SIZE) { index ->
                contentEmbedding[index] +
                        (styleEmbedding[index] - contentEmbedding[index]) * amount
            }
        }

        listener.onProgress(0, 1)
        return transformImage(transformer, content, mixedEmbedding)
    }

    private suspend fun processMicroAst(
        content: Bitmap,
        styleUri: String,
        prediction: OrtSession,
        transformer: OrtSession,
        amount: Float,
        listener: AiProgressListener
    ): Bitmap? {
        val styleCode = getStyleData(styleUri) { style ->
            val preparedStyle = style.scaledForPrediction(STYLE_IMAGE_SIZE)
            listOf(
                predictNchwStyle(
                    session = prediction,
                    image = preparedStyle,
                    outputSizes = intArrayOf(MICRO_AST_STYLE_CODE_SIZE),
                    normalize = false
                ).single()
            )
        }?.single() ?: return null.also {
            listener.onError(context.getString(R.string.style_image_not_selected))
        }

        listener.onProgress(0, 1)
        return transformNchw(
            session = transformer,
            image = content,
            styleData = listOf(styleCode),
            amount = amount,
            multiple = MICRO_AST_IMAGE_MULTIPLE,
            normalize = false,
            modelUsesStrength = true
        )
    }

    private suspend fun processAesFa(
        content: Bitmap,
        styleUri: String,
        prediction: OrtSession,
        transformer: OrtSession,
        amount: Float,
        listener: AiProgressListener
    ): Bitmap? {
        val styleData = getStyleData(styleUri) { style ->
            val preparedStyle = style.centerCrop(STYLE_IMAGE_SIZE)
            predictNchwStyle(
                session = prediction,
                image = preparedStyle,
                outputSizes = intArrayOf(AES_FA_STYLE_FEATURE_SIZE, AES_FA_STYLE_FEATURE_SIZE),
                normalize = true
            )
        } ?: return null.also {
            listener.onError(context.getString(R.string.style_image_not_selected))
        }

        listener.onProgress(0, 1)
        return transformNchw(
            session = transformer,
            image = content,
            styleData = styleData,
            amount = amount,
            multiple = AES_FA_IMAGE_MULTIPLE,
            normalize = true,
            modelUsesStrength = false
        )
    }

    private suspend fun getStyleData(
        styleUri: String,
        create: (Bitmap) -> List<FloatArray>
    ): List<FloatArray>? {
        cachedStyleData?.let { return it.map(FloatArray::copyOf) }

        val style = imageGetter.getImage(styleUri)?.image ?: return null
        return create(style).also { cachedStyleData = it.map(FloatArray::copyOf) }
    }

    private fun ensureSessions(model: StyleTransferModel) {
        if (
            sessionModel == model &&
            predictionSession != null &&
            transformSession != null
        ) return

        close()
        val options = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(INFERENCE_THREADS)
            setInterOpNumThreads(1)
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }
        predictionSession = environment.createSession(
            File(modelsDir, model.files[0].name).absolutePath,
            options
        )
        transformSession = environment.createSession(
            File(modelsDir, model.files[1].name).absolutePath,
            options
        )
        sessionModel = model
    }

    private fun predictStyle(session: OrtSession, image: Bitmap): FloatArray {
        val inputBuffer = image.toRgbBuffer()
        val outputBuffer = directFloatBuffer(STYLE_EMBEDDING_SIZE)
        val inputName = session.inputNames.single()
        val outputName = session.outputNames.single()

        OnnxTensor.createTensor(
            environment,
            inputBuffer,
            longArrayOf(1, image.height.toLong(), image.width.toLong(), RGB_CHANNELS.toLong())
        ).use { inputTensor ->
            OnnxTensor.createTensor(
                environment,
                outputBuffer,
                longArrayOf(1, 1, 1, STYLE_EMBEDDING_SIZE.toLong())
            ).use { outputTensor ->
                session.run(
                    mapOf(inputName to inputTensor),
                    mapOf(outputName to outputTensor)
                ).close()
            }
        }

        outputBuffer.rewind()
        return FloatArray(STYLE_EMBEDDING_SIZE).also(outputBuffer::get)
    }

    private fun predictNchwStyle(
        session: OrtSession,
        image: Bitmap,
        outputSizes: IntArray,
        normalize: Boolean
    ): List<FloatArray> {
        val inputBuffer = image.toNchwBuffer(
            paddedWidth = image.width,
            paddedHeight = image.height,
            normalize = normalize
        )
        val outputNames = session.outputNames.toList()
        val outputBuffers = outputSizes.map(::directFloatBuffer)
        val outputTensors = outputBuffers.mapIndexed { index, buffer ->
            val shape = (session.outputInfo.getValue(outputNames[index]).info as TensorInfo).shape
            OnnxTensor.createTensor(environment, buffer, shape)
        }

        try {
            OnnxTensor.createTensor(
                environment,
                inputBuffer,
                longArrayOf(1, RGB_CHANNELS.toLong(), image.height.toLong(), image.width.toLong())
            ).use { inputTensor ->
                session.run(
                    mapOf(session.inputNames.single() to inputTensor),
                    outputNames.zip(outputTensors).toMap()
                ).close()
            }
        } finally {
            outputTensors.forEach(OnnxTensor::close)
        }

        return outputBuffers.mapIndexed { index, buffer ->
            buffer.rewind()
            FloatArray(outputSizes[index]).also(buffer::get)
        }
    }

    private fun transformNchw(
        session: OrtSession,
        image: Bitmap,
        styleData: List<FloatArray>,
        amount: Float,
        multiple: Int,
        normalize: Boolean,
        modelUsesStrength: Boolean
    ): Bitmap {
        val paddedWidth = image.width.roundUpTo(multiple)
        val paddedHeight = image.height.roundUpTo(multiple)
        val contentBuffer = image.toNchwBuffer(
            paddedWidth = paddedWidth,
            paddedHeight = paddedHeight,
            normalize = normalize
        )
        val outputBuffer = directFloatBuffer(paddedWidth * paddedHeight * RGB_CHANNELS)
        val tensors = mutableMapOf<String, OnnxTensor>()

        try {
            tensors[CONTENT_INPUT_NAME] = OnnxTensor.createTensor(
                environment,
                contentBuffer,
                longArrayOf(1, RGB_CHANNELS.toLong(), paddedHeight.toLong(), paddedWidth.toLong())
            )
            when (styleData.size) {
                1 -> tensors[MICRO_AST_STYLE_INPUT_NAME] = OnnxTensor.createTensor(
                    environment,
                    styleData.single().toDirectFloatBuffer(),
                    longArrayOf(1, MICRO_AST_STYLE_CODE_SIZE.toLong(), 1, 1)
                )

                2 -> {
                    val styleShape = longArrayOf(1, AES_FA_STYLE_CHANNELS.toLong(), 3, 3)
                    tensors[AES_FA_STYLE_HIGH_INPUT_NAME] = OnnxTensor.createTensor(
                        environment,
                        styleData[0].toDirectFloatBuffer(),
                        styleShape
                    )
                    tensors[AES_FA_STYLE_LOW_INPUT_NAME] = OnnxTensor.createTensor(
                        environment,
                        styleData[1].toDirectFloatBuffer(),
                        styleShape
                    )
                }
            }
            if (modelUsesStrength) {
                tensors[STRENGTH_INPUT_NAME] = OnnxTensor.createTensor(
                    environment,
                    floatArrayOf(1f).toDirectFloatBuffer(),
                    longArrayOf(1)
                )
            }

            OnnxTensor.createTensor(
                environment,
                outputBuffer,
                longArrayOf(1, RGB_CHANNELS.toLong(), paddedHeight.toLong(), paddedWidth.toLong())
            ).use { outputTensor ->
                session.run(
                    tensors,
                    mapOf(session.outputNames.single() to outputTensor)
                ).close()
            }
        } finally {
            tensors.values.forEach(OnnxTensor::close)
        }

        outputBuffer.rewind()
        return outputBuffer.toNchwBitmap(
            source = image,
            width = image.width,
            height = image.height,
            tensorWidth = paddedWidth,
            tensorHeight = paddedHeight,
            normalize = normalize,
            amount = amount
        )
    }

    private fun transformImage(
        session: OrtSession,
        image: Bitmap,
        styleEmbedding: FloatArray
    ): Bitmap {
        val contentName = session.inputNames.first { name ->
            (session.inputInfo.getValue(name).info as TensorInfo).shape.last() == RGB_CHANNELS.toLong()
        }
        val styleName = session.inputNames.first { it != contentName }
        val outputName = session.outputNames.single()
        val contentBuffer = image.toRgbBuffer()
        val styleBuffer = directFloatBuffer(STYLE_EMBEDDING_SIZE).apply {
            put(styleEmbedding)
            rewind()
        }
        val outputBuffer = directFloatBuffer(image.width * image.height * RGB_CHANNELS)

        OnnxTensor.createTensor(
            environment,
            contentBuffer,
            longArrayOf(1, image.height.toLong(), image.width.toLong(), RGB_CHANNELS.toLong())
        ).use { contentTensor ->
            OnnxTensor.createTensor(
                environment,
                styleBuffer,
                longArrayOf(1, 1, 1, STYLE_EMBEDDING_SIZE.toLong())
            ).use { styleTensor ->
                OnnxTensor.createTensor(
                    environment,
                    outputBuffer,
                    longArrayOf(
                        1,
                        image.height.toLong(),
                        image.width.toLong(),
                        RGB_CHANNELS.toLong()
                    )
                ).use { outputTensor ->
                    session.run(
                        mapOf(contentName to contentTensor, styleName to styleTensor),
                        mapOf(outputName to outputTensor)
                    ).close()
                }
            }
        }

        outputBuffer.rewind()
        return outputBuffer.toBitmap(image.width, image.height)
    }

    private fun Bitmap.scaledForPrediction(maxSize: Int): Bitmap {
        val scale = maxSize.toFloat() / maxOf(width, height)
        val targetWidth = (width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (height * scale).toInt().coerceAtLeast(1)
        return this.scale(targetWidth, targetHeight)
    }

    private fun Bitmap.centerCrop(size: Int): Bitmap {
        val cropSize = minOf(width, height)
        val cropped = Bitmap.createBitmap(
            this,
            (width - cropSize) / 2,
            (height - cropSize) / 2,
            cropSize,
            cropSize
        )
        return cropped.scale(size, size).also {
            if (cropped !== this && cropped !== it) cropped.recycle()
        }
    }

    private fun Bitmap.toRgbBuffer(): FloatBuffer {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        return directFloatBuffer(pixels.size * RGB_CHANNELS).apply {
            pixels.forEach { pixel ->
                put(Color.red(pixel) / 255f)
                put(Color.green(pixel) / 255f)
                put(Color.blue(pixel) / 255f)
            }
            rewind()
        }
    }

    private fun Bitmap.toNchwBuffer(
        paddedWidth: Int,
        paddedHeight: Int,
        normalize: Boolean
    ): FloatBuffer {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        return directFloatBuffer(paddedWidth * paddedHeight * RGB_CHANNELS).apply {
            repeat(RGB_CHANNELS) { channel ->
                for (y in 0 until paddedHeight) {
                    val sourceY = y.coerceAtMost(height - 1)
                    for (x in 0 until paddedWidth) {
                        val pixel = pixels[sourceY * width + x.coerceAtMost(width - 1)]
                        val value = when (channel) {
                            0 -> Color.red(pixel)
                            1 -> Color.green(pixel)
                            else -> Color.blue(pixel)
                        } / 255f
                        put(
                            if (normalize) {
                                (value - IMAGE_NET_MEAN[channel]) / IMAGE_NET_STD[channel]
                            } else {
                                value
                            }
                        )
                    }
                }
            }
            rewind()
        }
    }

    private fun FloatBuffer.toBitmap(width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height) {
            val red = (get().coerceIn(0f, 1f) * 255).toInt()
            val green = (get().coerceIn(0f, 1f) * 255).toInt()
            val blue = (get().coerceIn(0f, 1f) * 255).toInt()
            Color.rgb(red, green, blue)
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun FloatBuffer.toNchwBitmap(
        source: Bitmap,
        width: Int,
        height: Int,
        tensorWidth: Int,
        tensorHeight: Int,
        normalize: Boolean,
        amount: Float
    ): Bitmap {
        val sourcePixels = IntArray(width * height)
        source.getPixels(sourcePixels, 0, width, 0, 0, width, height)
        val tensorArea = tensorWidth * tensorHeight
        val pixels = IntArray(width * height) { index ->
            val x = index % width
            val y = index / width
            val tensorIndex = y * tensorWidth + x
            val sourcePixel = sourcePixels[index]
            val red = mixChannel(
                output = outputChannel(0, tensorArea, tensorIndex, normalize),
                source = Color.red(sourcePixel) / 255f,
                amount = amount
            )
            val green = mixChannel(
                output = outputChannel(1, tensorArea, tensorIndex, normalize),
                source = Color.green(sourcePixel) / 255f,
                amount = amount
            )
            val blue = mixChannel(
                output = outputChannel(2, tensorArea, tensorIndex, normalize),
                source = Color.blue(sourcePixel) / 255f,
                amount = amount
            )
            Color.rgb(red, green, blue)
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun FloatBuffer.outputChannel(
        channel: Int,
        tensorArea: Int,
        index: Int,
        normalize: Boolean
    ): Float = get(channel * tensorArea + index).let {
        if (normalize) it * IMAGE_NET_STD[channel] + IMAGE_NET_MEAN[channel] else it
    }

    private fun mixChannel(output: Float, source: Float, amount: Float): Int =
        ((source + (output - source) * amount).coerceIn(0f, 1f) * 255).toInt()

    private fun FloatArray.toDirectFloatBuffer(): FloatBuffer = directFloatBuffer(size).apply {
        put(this@toDirectFloatBuffer)
        rewind()
    }

    private fun Int.roundUpTo(multiple: Int): Int = (this + multiple - 1) / multiple * multiple

    private fun directFloatBuffer(size: Int): FloatBuffer = ByteBuffer
        .allocateDirect(size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    private fun File.isValidModel(expectedSize: Long, expectedChecksum: String): Boolean {
        if (!isFile || length() != expectedSize) return false
        val digest = MessageDigest.getInstance("SHA-256")
        inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) } == expectedChecksum
    }

    private enum class StyleTransferModel(
        val archiveName: String,
        val files: List<StyleModelFile>
    ) {
        Arbitrary(
            archiveName = "arbitrary_style_transfer_onnx.zip",
            files = listOf(
                StyleModelFile(
                    name = "style_mobilenet.onnx",
                    size = 9_409_794L,
                    checksum = "25522b33eb518dd7ab996d88dbecc898f5f063d50ad8676a959a3fa268a0a605"
                ),
                StyleModelFile(
                    name = "transformer_separable.onnx",
                    size = 2_415_224L,
                    checksum = "0d022efe00dd5615b32c6be864de0a21637981fd29d722a3abd8039258ca8262"
                )
            )
        ),
        MicroAst(
            archiveName = "microast_style_transfer_onnx.zip",
            files = listOf(
                StyleModelFile(
                    name = "microast_style.onnx",
                    size = 684_869L,
                    checksum = "eb4003ad377c1afd18f377f96201b6337447ca473efb9455b519fa3193f6489d"
                ),
                StyleModelFile(
                    name = "microast_transform.onnx",
                    size = 1_315_160L,
                    checksum = "3da41e9a9fa68f14729e59323cb89bd0ff8acb2071213ed9e852563ea637d51c"
                ),
                StyleModelFile(
                    name = "MICROAST_LICENSE.txt",
                    size = 1_070L,
                    checksum = "e8f0b2c00854430149f8af45b20ac30320634cda110eaa490b9ff577ba4c6c54"
                )
            )
        ),
        AesFa(
            archiveName = "aesfa_style_transfer_onnx.zip",
            files = listOf(
                StyleModelFile(
                    name = "aesfa_style.onnx",
                    size = 3_375_395L,
                    checksum = "df7dc84a6776c6533ce8578ab8f462075c52c0c3311ef97ef8206ba51d404b89"
                ),
                StyleModelFile(
                    name = "aesfa_transform.onnx",
                    size = 9_773_334L,
                    checksum = "b1a946b51703df0eddb48f4735dee943854abc53f2177279dc4d80937d00c99f"
                ),
                StyleModelFile(
                    name = "AESFA_LICENSE.txt",
                    size = 1_069L,
                    checksum = "d26971c16786f3cf45465deebc9ec6cadd3bd4db6602af24accfb246374833ef"
                )
            )
        );

        companion object {
            fun from(model: NeuralModel): StyleTransferModel? = entries.find {
                it.archiveName == model.name
            }
        }
    }

    private data class StyleModelFile(
        val name: String,
        val size: Long,
        val checksum: String
    )

    private companion object {
        const val STYLE_IMAGE_SIZE = 256
        const val STYLE_EMBEDDING_SIZE = 100
        const val MICRO_AST_STYLE_CODE_SIZE = 512
        const val AES_FA_STYLE_CHANNELS = 128
        const val AES_FA_STYLE_FEATURE_SIZE = AES_FA_STYLE_CHANNELS * 3 * 3
        const val MICRO_AST_IMAGE_MULTIPLE = 4
        const val AES_FA_IMAGE_MULTIPLE = 8
        const val RGB_CHANNELS = 3
        const val INFERENCE_THREADS = 2
        const val CONTENT_INPUT_NAME = "content"
        const val MICRO_AST_STYLE_INPUT_NAME = "style_code"
        const val AES_FA_STYLE_HIGH_INPUT_NAME = "style_high"
        const val AES_FA_STYLE_LOW_INPUT_NAME = "style_low"
        const val STRENGTH_INPUT_NAME = "strength"
        val IMAGE_NET_MEAN = floatArrayOf(0.485f, 0.456f, 0.406f)
        val IMAGE_NET_STD = floatArrayOf(0.229f, 0.224f, 0.225f)
    }
}
