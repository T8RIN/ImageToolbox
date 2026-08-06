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
import kotlinx.coroutines.ensureActive
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
import kotlin.coroutines.coroutineContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class OptimizationStyleTransferProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadManager: DownloadManager,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private val mutex = Mutex()
    private val environment = OrtEnvironment.getEnvironment()
    private var cachedStyleTargets: List<TensorData>? = null

    private val modelsDir: File
        get() = File(context.filesDir, NeuralConstants.DIR).apply(File::mkdirs)

    fun supports(model: NeuralModel): Boolean = model.name == ARCHIVE_NAME

    fun isDownloaded(model: NeuralModel): Boolean = supports(model) && MODEL_FILES.all {
        File(modelsDir, it.name).isValidModel(
            expectedSize = it.size,
            expectedChecksum = it.checksum
        )
    }

    fun startDownload(model: NeuralModel): Flow<DownloadProgress> = channelFlow {
        check(supports(model))
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
            MODEL_FILES.forEach { File(modelsDir, it.name).delete() }
            error("Optimization style transfer model checksum mismatch")
        }
    }.flowOn(ioDispatcher)

    fun isModelFile(name: String?): Boolean = MODEL_FILES.any { it.name == name }

    fun deleteModels(model: NeuralModel) {
        if (!supports(model)) return
        close()
        MODEL_FILES.forEach { File(modelsDir, it.name).delete() }
    }

    suspend fun process(
        content: Bitmap,
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

            val preparedContent = content.prepareForOptimization()
            val totalSteps = OPTIMIZATION_STEPS + TARGET_STEPS
            listener.onProgress(0, totalSteps)
            val targets = createTargets(
                content = preparedContent,
                styleUri = styleUri,
                listener = listener,
                totalSteps = totalSteps
            ) ?: return@withLock null

            val optimized = optimize(
                content = preparedContent,
                targets = targets,
                strength = (params.strength / 100f).coerceIn(0f, 1f),
                listener = listener,
                totalSteps = totalSteps
            )
            val workingResult = optimized.toBitmap(
                width = preparedContent.width,
                height = preparedContent.height
            )
            listener.onProgress(totalSteps, totalSteps)
            if (workingResult.width == content.width && workingResult.height == content.height) {
                workingResult
            } else {
                workingResult.scale(content.width, content.height).also {
                    workingResult.recycle()
                }
            }
        }
    }

    fun close() {
        cachedStyleTargets = null
    }

    private suspend fun createTargets(
        content: PreparedImage,
        styleUri: String,
        listener: AiProgressListener,
        totalSteps: Int
    ): OptimizationTargets? {
        createSession(TARGETS_MODEL_NAME).use { session ->
            val styleTargets = cachedStyleTargets ?: run {
                val style = imageGetter.getImage(styleUri)?.image ?: return null.also {
                    listener.onError(context.getString(R.string.style_image_not_selected))
                }
                val preparedStyle = style.prepareForOptimization()
                extractTargets(
                    session = session,
                    image = preparedStyle,
                    outputs = STYLE_OUTPUTS
                ).also { cachedStyleTargets = it.map(TensorData::copyData) }
            }
            listener.onProgress(1, totalSteps)
            val contentTarget = extractTargets(
                session = session,
                image = content,
                outputs = listOf(contentOutput(content))
            ).single()
            listener.onProgress(TARGET_STEPS, totalSteps)
            return OptimizationTargets(
                content = contentTarget,
                style = styleTargets.map(TensorData::copyData)
            )
        }
    }

    private fun extractTargets(
        session: OrtSession,
        image: PreparedImage,
        outputs: List<TensorOutput>
    ): List<TensorData> {
        val inputBuffer = image.data.toDirectFloatBuffer()
        val outputBuffers = outputs.map { directFloatBuffer(it.size) }
        val outputTensors = outputs.mapIndexed { index, output ->
            OnnxTensor.createTensor(environment, outputBuffers[index], output.shape)
        }
        try {
            OnnxTensor.createTensor(
                environment,
                inputBuffer,
                longArrayOf(1, RGB_CHANNELS.toLong(), image.height.toLong(), image.width.toLong())
            ).use { inputTensor ->
                session.run(
                    mapOf(IMAGE_INPUT_NAME to inputTensor),
                    outputs.mapIndexed { index, output -> output.name to outputTensors[index] }
                        .toMap()
                ).close()
            }
        } finally {
            outputTensors.forEach(OnnxTensor::close)
        }
        return outputs.mapIndexed { index, output ->
            val buffer = outputBuffers[index].apply(FloatBuffer::rewind)
            TensorData(
                data = FloatArray(output.size).also(buffer::get),
                shape = output.shape
            )
        }
    }

    private suspend fun optimize(
        content: PreparedImage,
        targets: OptimizationTargets,
        strength: Float,
        listener: AiProgressListener,
        totalSteps: Int
    ): FloatArray = createSession(GRADIENT_MODEL_NAME).use { session ->
        val image = content.data.copyOf()
        val imageBuffer = directFloatBuffer(image.size)
        val gradientBuffer = directFloatBuffer(image.size)
        val lossBuffer = directFloatBuffer(1)
        val inputTensors = mutableMapOf<String, OnnxTensor>()
        val targetData = listOf(targets.content) + targets.style

        try {
            inputTensors[IMAGE_INPUT_NAME] = OnnxTensor.createTensor(
                environment,
                imageBuffer,
                longArrayOf(
                    1,
                    RGB_CHANNELS.toLong(),
                    content.height.toLong(),
                    content.width.toLong()
                )
            )
            TARGET_INPUT_NAMES.zip(targetData).forEach { (name, target) ->
                inputTensors[name] = OnnxTensor.createTensor(
                    environment,
                    target.data.toDirectFloatBuffer(),
                    target.shape
                )
            }
            inputTensors[STRENGTH_INPUT_NAME] = OnnxTensor.createTensor(
                environment,
                floatArrayOf(strength).toDirectFloatBuffer(),
                longArrayOf(1)
            )

            OnnxTensor.createTensor(
                environment,
                gradientBuffer,
                longArrayOf(
                    1,
                    RGB_CHANNELS.toLong(),
                    content.height.toLong(),
                    content.width.toLong()
                )
            ).use { gradientTensor ->
                OnnxTensor.createTensor(
                    environment,
                    lossBuffer,
                    longArrayOf()
                ).use { lossTensor ->
                    optimizeLbfgs(
                        image = image,
                        evaluate = { candidate ->
                            coroutineContext.ensureActive()
                            imageBuffer.clear()
                            imageBuffer.put(candidate)
                            imageBuffer.rewind()
                            gradientBuffer.clear()
                            lossBuffer.clear()
                            session.run(
                                inputTensors,
                                mapOf(
                                    GRADIENT_OUTPUT_NAME to gradientTensor,
                                    LOSS_OUTPUT_NAME to lossTensor
                                )
                            ).close()
                            gradientBuffer.rewind()
                            lossBuffer.rewind()
                            OptimizationValue(
                                loss = lossBuffer.get().toDouble(),
                                gradient = FloatArray(candidate.size).also(gradientBuffer::get)
                            )
                        },
                        onIteration = { iteration ->
                            listener.onProgress(iteration + TARGET_STEPS, totalSteps)
                        }
                    )
                }
            }
        } finally {
            inputTensors.values.forEach(OnnxTensor::close)
        }
        image
    }

    private suspend fun optimizeLbfgs(
        image: FloatArray,
        evaluate: suspend (FloatArray) -> OptimizationValue,
        onIteration: (Int) -> Unit
    ) {
        var current = evaluate(image)
        var loss = current.loss
        var gradient = current.gradient
        var direction = FloatArray(image.size)
        var previousGradient: FloatArray? = null
        var previousStep = 0.0
        val oldDirections = ArrayDeque<FloatArray>()
        val oldSteps = ArrayDeque<FloatArray>()
        val inverseCurvatures = ArrayDeque<Double>()
        var hessianScale = 1.0

        repeat(OPTIMIZATION_STEPS) { iteration ->
            coroutineContext.ensureActive()
            if (iteration == 0) {
                direction = FloatArray(image.size) { -gradient[it] }
            } else {
                val oldGradient = checkNotNull(previousGradient)
                val y = FloatArray(image.size) { gradient[it] - oldGradient[it] }
                val s = FloatArray(image.size) { direction[it] * previousStep.toFloat() }
                val curvature = y.dot(s)
                if (curvature > MIN_CURVATURE) {
                    if (oldDirections.size == LBFGS_HISTORY_SIZE) {
                        oldDirections.removeFirst()
                        oldSteps.removeFirst()
                        inverseCurvatures.removeFirst()
                    }
                    oldDirections.addLast(y)
                    oldSteps.addLast(s)
                    inverseCurvatures.addLast(1.0 / curvature)
                    hessianScale = curvature / y.dot(y)
                }

                val directions = oldDirections.toList()
                val steps = oldSteps.toList()
                val curvatures = inverseCurvatures.toList()
                val alpha = DoubleArray(directions.size)
                val q = FloatArray(image.size) { -gradient[it] }
                for (index in directions.indices.reversed()) {
                    alpha[index] = steps[index].dot(q) * curvatures[index]
                    q.addScaled(directions[index], -alpha[index])
                }
                direction = FloatArray(image.size) { (q[it] * hessianScale).toFloat() }
                for (index in directions.indices) {
                    val beta = directions[index].dot(direction) * curvatures[index]
                    direction.addScaled(steps[index], alpha[index] - beta)
                }
            }

            previousGradient = gradient.copyOf()
            val directionalDerivative = gradient.dot(direction)
            if (directionalDerivative >= -MIN_DIRECTIONAL_DERIVATIVE) return
            val initialStep = if (iteration == 0) {
                min(1.0, 1.0 / gradient.sumOfAbsoluteValues())
            } else {
                1.0
            }
            val lineSearch = strongWolfe(
                image = image,
                initialStep = initialStep,
                direction = direction,
                loss = loss,
                gradient = gradient,
                directionalDerivative = directionalDerivative,
                evaluate = evaluate
            )
            image.addScaled(direction, lineSearch.step)
            previousStep = lineSearch.step
            loss = lineSearch.value.loss
            gradient = lineSearch.value.gradient
            onIteration(iteration + 1)
        }
    }

    private suspend fun strongWolfe(
        image: FloatArray,
        initialStep: Double,
        direction: FloatArray,
        loss: Double,
        gradient: FloatArray,
        directionalDerivative: Double,
        evaluate: suspend (FloatArray) -> OptimizationValue
    ): LineSearchResult {
        suspend fun evaluateAt(step: Double): OptimizationValue = evaluate(
            FloatArray(image.size) { image[it] + direction[it] * step.toFloat() }
        )

        var step = initialStep
        var value = evaluateAt(step)
        var derivative = value.gradient.dot(direction)
        var previousStep = 0.0
        var previousValue = OptimizationValue(loss, gradient.copyOf())
        var previousDerivative = directionalDerivative
        var bracket: List<LineSearchPoint>? = null
        var iteration = 0

        while (iteration < MAX_LINE_SEARCH_STEPS) {
            if (
                value.loss > loss + WOLFE_C1 * step * directionalDerivative ||
                (iteration > 1 && value.loss >= previousValue.loss)
            ) {
                bracket = listOf(
                    LineSearchPoint(previousStep, previousValue, previousDerivative),
                    LineSearchPoint(step, value, derivative)
                )
                break
            }
            if (abs(derivative) <= -WOLFE_C2 * directionalDerivative) {
                return LineSearchResult(step, value)
            }
            if (derivative >= 0.0) {
                bracket = listOf(
                    LineSearchPoint(previousStep, previousValue, previousDerivative),
                    LineSearchPoint(step, value, derivative)
                )
                break
            }

            val nextStep = cubicInterpolate(
                x1 = previousStep,
                f1 = previousValue.loss,
                g1 = previousDerivative,
                x2 = step,
                f2 = value.loss,
                g2 = derivative,
                minimum = step + 0.01 * (step - previousStep),
                maximum = step * 10.0
            )
            previousStep = step
            previousValue = value
            previousDerivative = derivative
            step = nextStep
            value = evaluateAt(step)
            derivative = value.gradient.dot(direction)
            iteration++
        }

        val points = bracket?.toMutableList() ?: mutableListOf(
            LineSearchPoint(0.0, OptimizationValue(loss, gradient.copyOf()), directionalDerivative),
            LineSearchPoint(step, value, derivative)
        )
        var insufficientProgress = false
        while (iteration < MAX_LINE_SEARCH_STEPS) {
            val lowIndex = if (points[0].value.loss <= points[1].value.loss) 0 else 1
            val highIndex = 1 - lowIndex
            val low = points[lowIndex]
            val high = points[highIndex]
            if (abs(high.step - low.step) * direction.maxAbsoluteValue() < MIN_STEP_CHANGE) break

            step = cubicInterpolate(
                x1 = points[0].step,
                f1 = points[0].value.loss,
                g1 = points[0].derivative,
                x2 = points[1].step,
                f2 = points[1].value.loss,
                g2 = points[1].derivative
            )
            val minimum = min(points[0].step, points[1].step)
            val maximum = max(points[0].step, points[1].step)
            val epsilon = 0.1 * (maximum - minimum)
            if (min(maximum - step, step - minimum) < epsilon) {
                if (insufficientProgress || step >= maximum || step <= minimum) {
                    step = if (abs(step - maximum) < abs(step - minimum)) {
                        maximum - epsilon
                    } else {
                        minimum + epsilon
                    }
                    insufficientProgress = false
                } else {
                    insufficientProgress = true
                }
            } else {
                insufficientProgress = false
            }

            value = evaluateAt(step)
            derivative = value.gradient.dot(direction)
            iteration++
            if (
                value.loss > loss + WOLFE_C1 * step * directionalDerivative ||
                value.loss >= low.value.loss
            ) {
                points[highIndex] = LineSearchPoint(step, value, derivative)
            } else {
                if (abs(derivative) <= -WOLFE_C2 * directionalDerivative) {
                    return LineSearchResult(step, value)
                }
                if (derivative * (high.step - low.step) >= 0.0) {
                    points[highIndex] = low
                }
                points[lowIndex] = LineSearchPoint(step, value, derivative)
            }
        }

        return points.minBy { it.value.loss }.let {
            LineSearchResult(it.step, it.value)
        }
    }

    private fun cubicInterpolate(
        x1: Double,
        f1: Double,
        g1: Double,
        x2: Double,
        f2: Double,
        g2: Double,
        minimum: Double = min(x1, x2),
        maximum: Double = max(x1, x2)
    ): Double {
        val d1 = g1 + g2 - 3.0 * (f1 - f2) / (x1 - x2)
        val square = d1 * d1 - g1 * g2
        if (square < 0.0 || !square.isFinite()) return (minimum + maximum) / 2.0
        val d2 = sqrt(square)
        val position = if (x1 <= x2) {
            x2 - (x2 - x1) * ((g2 + d2 - d1) / (g2 - g1 + 2.0 * d2))
        } else {
            x1 - (x1 - x2) * ((g1 + d2 - d1) / (g1 - g2 + 2.0 * d2))
        }
        return position.coerceIn(minimum, maximum)
    }

    private fun FloatArray.dot(other: FloatArray): Double {
        var result = 0.0
        indices.forEach { result += this[it].toDouble() * other[it].toDouble() }
        return result
    }

    private fun FloatArray.addScaled(other: FloatArray, scale: Double) {
        indices.forEach { this[it] += (other[it].toDouble() * scale).toFloat() }
    }

    private fun FloatArray.sumOfAbsoluteValues(): Double {
        var result = 0.0
        forEach { result += abs(it.toDouble()) }
        return result
    }

    private fun FloatArray.maxAbsoluteValue(): Double {
        var result = 0.0
        forEach { result = max(result, abs(it.toDouble())) }
        return result
    }

    private fun createSession(modelName: String): OrtSession {
        val options = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(INFERENCE_THREADS)
            setInterOpNumThreads(1)
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }
        return environment.createSession(File(modelsDir, modelName).absolutePath, options)
    }

    private fun Bitmap.prepareForOptimization(): PreparedImage {
        val scale = sqrt(WORKING_PIXEL_BUDGET.toFloat() / (width.toLong() * height).toFloat())
            .coerceAtMost(1f)
        val targetWidth = (width * scale).roundToInt()
            .coerceAtLeast(IMAGE_MULTIPLE)
            .roundUpTo(IMAGE_MULTIPLE)
        val targetHeight = (height * scale).roundToInt()
            .coerceAtLeast(IMAGE_MULTIPLE)
            .roundUpTo(IMAGE_MULTIPLE)
        val prepared = if (targetWidth == width && targetHeight == height) {
            this
        } else {
            this.scale(targetWidth, targetHeight)
        }
        return PreparedImage(
            data = prepared.toNchwFloatArray(),
            width = prepared.width,
            height = prepared.height
        ).also {
            if (prepared !== this) prepared.recycle()
        }
    }

    private fun Bitmap.toNchwFloatArray(): FloatArray {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        val area = pixels.size
        return FloatArray(area * RGB_CHANNELS).also { output ->
            pixels.forEachIndexed { index, pixel ->
                output[index] = Color.red(pixel) - IMAGE_NET_MEAN[0]
                output[area + index] = Color.green(pixel) - IMAGE_NET_MEAN[1]
                output[area * 2 + index] = Color.blue(pixel) - IMAGE_NET_MEAN[2]
            }
        }
    }

    private fun FloatArray.toBitmap(width: Int, height: Int): Bitmap {
        val area = width * height
        val pixels = IntArray(area) { index ->
            Color.rgb(
                (this[index] + IMAGE_NET_MEAN[0]).coerceIn(0f, 255f).roundToInt(),
                (this[area + index] + IMAGE_NET_MEAN[1]).coerceIn(0f, 255f).roundToInt(),
                (this[area * 2 + index] + IMAGE_NET_MEAN[2]).coerceIn(0f, 255f).roundToInt()
            )
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

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

    private data class PreparedImage(
        val data: FloatArray,
        val width: Int,
        val height: Int
    )

    private data class OptimizationTargets(
        val content: TensorData,
        val style: List<TensorData>
    )

    private data class OptimizationValue(
        val loss: Double,
        val gradient: FloatArray
    )

    private data class LineSearchPoint(
        val step: Double,
        val value: OptimizationValue,
        val derivative: Double
    )

    private data class LineSearchResult(
        val step: Double,
        val value: OptimizationValue
    )

    private data class TensorData(
        val data: FloatArray,
        val shape: LongArray
    ) {
        fun copyData(): TensorData = copy(data = data.copyOf(), shape = shape.copyOf())
    }

    private data class TensorOutput(
        val name: String,
        val shape: LongArray
    ) {
        val size: Int = shape.fold(1L, Long::times).toInt()
    }

    private data class ModelFile(
        val name: String,
        val size: Long,
        val checksum: String
    )

    private companion object {
        const val ARCHIVE_NAME = "vgg19_optimization_style_transfer_onnx.zip"
        const val TARGETS_MODEL_NAME = "vgg19_targets.onnx"
        const val GRADIENT_MODEL_NAME = "vgg19_gradient.onnx"
        const val IMAGE_INPUT_NAME = "image"
        const val STRENGTH_INPUT_NAME = "strength"
        const val GRADIENT_OUTPUT_NAME = "gradient"
        const val LOSS_OUTPUT_NAME = "total_loss"
        const val RGB_CHANNELS = 3
        const val IMAGE_MULTIPLE = 16
        const val WORKING_PIXEL_BUDGET = 256 * 256
        const val OPTIMIZATION_STEPS = 240
        const val TARGET_STEPS = 2
        const val INFERENCE_THREADS = 2
        const val LBFGS_HISTORY_SIZE = 10
        const val MAX_LINE_SEARCH_STEPS = 25
        const val MIN_CURVATURE = 1e-10
        const val MIN_DIRECTIONAL_DERIVATIVE = 1e-9
        const val MIN_STEP_CHANGE = 1e-9
        const val WOLFE_C1 = 1e-4
        const val WOLFE_C2 = 0.9
        val IMAGE_NET_MEAN = floatArrayOf(123.675f, 116.28f, 103.53f)

        val TARGET_INPUT_NAMES = listOf(
            "content_target",
            "gram1",
            "gram2",
            "gram3",
            "gram4",
            "gram5"
        )
        val STYLE_OUTPUTS = listOf(
            TensorOutput("gram1", longArrayOf(1, 64, 64)),
            TensorOutput("gram2", longArrayOf(1, 128, 128)),
            TensorOutput("gram3", longArrayOf(1, 256, 256)),
            TensorOutput("gram4", longArrayOf(1, 512, 512)),
            TensorOutput("gram5", longArrayOf(1, 512, 512))
        )
        val MODEL_FILES = listOf(
            ModelFile(
                name = TARGETS_MODEL_NAME,
                size = 17_514L,
                checksum = "26b13aa5138a71dcccbbb7dba8feedc113747d957c7337eb0df01275680dc7e2"
            ),
            ModelFile(
                name = GRADIENT_MODEL_NAME,
                size = 87_034L,
                checksum = "33ec918993161e77d6b29833eee4c873da593f666212c0b4936e056ae3a60c62"
            ),
            ModelFile(
                name = "vgg19_weights.bin",
                size = 51_779_840L,
                checksum = "4ab1c9682b08a6c52c0c043020a710debec2c6257641b3db95081c365897341e"
            ),
            ModelFile(
                name = "NEURAL_STYLE_TRANSFER_LICENSE.txt",
                size = 1_068L,
                checksum = "9aa9ce866af978993a88052e7e3fa6822433239a92fe85428345b889bfe53ca1"
            ),
            ModelFile(
                name = "TORCHVISION_LICENSE.txt",
                size = 1_517L,
                checksum = "6502f676851cfe25f8af75531dfb32375b7325b73c37e7b43741fa422893e71d"
            )
        )

        fun contentOutput(content: PreparedImage): TensorOutput = TensorOutput(
            name = "target_content",
            shape = longArrayOf(
                1,
                512,
                (content.height / 8).toLong(),
                (content.width / 8).toLong()
            )
        )
    }
}
