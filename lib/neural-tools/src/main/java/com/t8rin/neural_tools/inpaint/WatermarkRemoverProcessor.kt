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

package com.t8rin.neural_tools.inpaint

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.graphics.Bitmap
import android.util.Log
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.awxkee.aire.ScaleColorSpace
import com.t8rin.neural_tools.DownloadProgress
import com.t8rin.neural_tools.NeuralTool
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.io.readByteArray
import java.io.File
import java.io.FileOutputStream
import java.nio.FloatBuffer

object WatermarkRemoverProcessor : NeuralTool() {

    const val WATERMARK_MODEL_NAME = "watermark_mit_b5_sigmoid.onnx"

    private const val TAG = "WatermarkRemoverProcessor"

    private const val TRAINED_SIZE = 512

    private const val THRESHOLD = 0.18f

    private val MODEL_DOWNLOAD_LINK
        get() = baseUrl.replace(
            oldValue = "*",
            newValue = WATERMARK_MODEL_NAME
        )

    private val directory: File
        get() = File(context.filesDir, "onnx").apply {
            mkdirs()
        }

    val modelFile: File
        get() = File(directory, WATERMARK_MODEL_NAME)

    private var sessionHolder: OrtSession? = null

    private val session: OrtSession
        get() = sessionHolder ?: run {
            val options = OrtSession.SessionOptions().apply {
                runCatching { addCUDA() }
                runCatching { setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT) }
                runCatching { setInterOpNumThreads(8) }
                runCatching { setIntraOpNumThreads(8) }
                runCatching { setMemoryPatternOptimization(true) }
            }

            OrtEnvironment.getEnvironment().createSession(
                modelFile.absolutePath,
                options
            )
        }.also { sessionHolder = it }

    private val _isDownloaded = MutableStateFlow(modelFile.exists())

    val isDownloaded: StateFlow<Boolean> = combine(
        _isDownloaded,
        LaMaProcessor.isDownloaded
    ) { watermarkDownloaded, lamaDownloaded ->
        watermarkDownloaded && lamaDownloaded
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = modelFile.exists() && LaMaProcessor.isDownloaded.value
    )

    fun startDownload(): Flow<DownloadProgress> = callbackFlow {
        startWatermarkModelDownload().collect { progress ->
            send(
                progress.copy(
                    currentPercent = progress.currentPercent * if (LaMaProcessor.isDownloaded.value) 1f else 0.5f,
                    currentTotalSize = progress.currentTotalSize
                )
            )
        }

        if (!LaMaProcessor.isDownloaded.value) {
            LaMaProcessor.startDownload().collect { progress ->
                send(
                    progress.copy(
                        currentPercent = 0.5f + progress.currentPercent * 0.5f,
                        currentTotalSize = progress.currentTotalSize
                    )
                )
            }
        }

        send(
            DownloadProgress(
                currentPercent = 1f,
                currentTotalSize = 0L
            )
        )
    }.flowOn(Dispatchers.IO)

    private fun startWatermarkModelDownload(): Flow<DownloadProgress> = callbackFlow {
        httpClient.prepareGet(MODEL_DOWNLOAD_LINK).execute { response ->
            val total = response.contentLength() ?: -1L

            val tmp = File(modelFile.parentFile, modelFile.name + ".tmp")

            val channel = response.bodyAsChannel()
            var downloaded = 0L

            FileOutputStream(tmp).use { fos ->
                try {
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            downloaded += bytes.size
                            fos.write(bytes)

                            trySend(
                                DownloadProgress(
                                    currentPercent = if (total > 0) {
                                        downloaded.toFloat() / total
                                    } else {
                                        0f
                                    },
                                    currentTotalSize = total
                                )
                            )
                        }
                    }

                    tmp.renameTo(modelFile)
                    _isDownloaded.update { modelFile.exists() }

                    close()
                } catch (e: Throwable) {
                    tmp.delete()
                    close(e)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    fun removeWatermark(
        image: Bitmap,
        onMaskFound: () -> Unit = {}
    ): Bitmap? {
        val mask = findWatermarkMask(image = image)
            ?: return null

        onMaskFound()

        return LaMaProcessor.inpaint(
            image = image,
            mask = mask
        )
    }

    fun findWatermarkMask(
        image: Bitmap
    ): Bitmap? = runCatching {
        if (!modelFile.exists()) {
            _isDownloaded.update { false }
            return null
        }

        val inputImage = if (image.width != TRAINED_SIZE || image.height != TRAINED_SIZE) {
            Aire.scale(
                bitmap = image,
                dstWidth = TRAINED_SIZE,
                dstHeight = TRAINED_SIZE,
                scaleMode = ResizeFunction.Lanczos3,
                colorSpace = ScaleColorSpace.LAB
            )
        } else {
            image
        }

        val tensorImg = bitmapToWatermarkTensor(bitmap = inputImage)

        session.run(
            mapOf("input" to tensorImg)
        ).use { res ->
            val outValue = res[0]
            val outTensor = outValue as? OnnxTensor
                ?: throw IllegalStateException("Model output is not OnnxTensor")

            val mask512 = outputTensorToBinaryMaskBitmap(outTensor)

            tensorImg.close()

            if (image.width != TRAINED_SIZE || image.height != TRAINED_SIZE) {
                return Aire.scale(
                    bitmap = mask512,
                    dstWidth = image.width,
                    dstHeight = image.height,
                    scaleMode = ResizeFunction.Nearest,
                    colorSpace = ScaleColorSpace.SRGB
                )
            }

            return mask512
        }
    }.onFailure {
        Log.e(TAG, "findWatermarkMask failure", it)
    }.getOrNull()

    private fun bitmapToWatermarkTensor(
        bitmap: Bitmap
    ): OnnxTensor {
        val env = OrtEnvironment.getEnvironment()

        val w = bitmap.width
        val h = bitmap.height

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val channelSize = w * h
        val data = FloatArray(3 * channelSize)

        /*
         * ВАЖНО:
         * ONNX экспортнут без встроенной нормализации.
         * Поэтому тут делаем ту же нормализацию, что в Python preview.
         */
        val meanR = 0.485f
        val meanG = 0.456f
        val meanB = 0.406f

        val stdR = 0.229f
        val stdG = 0.224f
        val stdB = 0.225f

        for (i in 0 until channelSize) {
            val p = pixels[i]

            val r = ((p shr 16) and 0xFF) / 255f
            val g = ((p shr 8) and 0xFF) / 255f
            val b = (p and 0xFF) / 255f

            data[i] = (r - meanR) / stdR
            data[channelSize + i] = (g - meanG) / stdG
            data[2 * channelSize + i] = (b - meanB) / stdB
        }

        return OnnxTensor.createTensor(
            env,
            FloatBuffer.wrap(data),
            longArrayOf(
                1,
                3,
                h.toLong(),
                w.toLong()
            )
        )
    }

    private fun outputTensorToBinaryMaskBitmap(
        tensor: OnnxTensor
    ): Bitmap {
        val buffer = tensor.floatBuffer
        val data = FloatArray(buffer.capacity())
        buffer.get(data)

        val width = TRAINED_SIZE
        val height = TRAINED_SIZE
        val size = width * height

        val pixels = IntArray(size)

        /*
         * model output:
         * [1, 1, 512, 512]
         *
         * output уже sigmoid probability [0..1],
         * поэтому просто threshold -> black/white.
         */
        for (i in 0 until size) {
            val v = data[i]
            val c = if (v >= THRESHOLD) 255 else 0

            pixels[i] = (0xFF shl 24) or
                    (c shl 16) or
                    (c shl 8) or
                    c
        }

        return Bitmap.createBitmap(
            pixels,
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }

    fun deleteDownloadedModels() {
        modelFile.delete()
        close()
        _isDownloaded.update { false }
    }

    fun close() {
        sessionHolder?.close()
        sessionHolder = null
    }
}