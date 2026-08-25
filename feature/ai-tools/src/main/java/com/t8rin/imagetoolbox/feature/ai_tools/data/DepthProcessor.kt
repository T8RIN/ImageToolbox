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
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.utils.extractMessage
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthEffect
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthParams
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel.Type
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralParams
import com.t8rin.neural_tools.runCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer
import javax.inject.Inject
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

internal class DepthProcessor @Inject constructor(
    private val service: KeepAliveService,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private var cachedDepthMap: CachedDepthMap? = null

    suspend fun process(
        session: OrtSession?,
        model: NeuralModel,
        source: Bitmap,
        params: NeuralParams,
        listener: AiProgressListener
    ): Bitmap? = withContext(defaultDispatcher) {
        service.track(
            onFailure = { listener.onError(it.extractMessage()) },
            action = {
                reportProgress(listener, 0)
                val depth = params.customDepthMap?.let { loadDepthMap(it) }
                    ?: inferDepth(
                        session = requireNotNull(session),
                        model = model,
                        source = source
                    )
                reportProgress(listener, 1)
                renderEffect(
                    source = source,
                    depth = depth,
                    params = params.depthParams
                ).also {
                    reportProgress(listener, 2)
                }
            }
        )
    }

    private suspend fun loadDepthMap(uri: String): DepthField {
        cachedDepthMap
            ?.takeIf { it.uri == uri }
            ?.let { return it.field }

        val bitmap = imageGetter.getImage(
            data = uri,
            size = CUSTOM_DEPTH_MAP_MAX_SIDE
        )
            ?: error("Failed to open the custom depth map")
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(
            pixels,
            0,
            bitmap.width,
            0,
            0,
            bitmap.width,
            bitmap.height
        )

        val spectral = pixels.count { color ->
            val minimum = min(Color.red(color), min(Color.green(color), Color.blue(color)))
            val maximum = max(Color.red(color), max(Color.green(color), Color.blue(color)))
            maximum - minimum > GRAYSCALE_CHANNEL_TOLERANCE
        } > pixels.size * SPECTRAL_PIXEL_THRESHOLD

        return DepthField(
            values = FloatArray(pixels.size) { index ->
                if (spectral) spectralDepth(pixels[index])
                else grayscaleDepth(pixels[index])
            },
            width = bitmap.width,
            height = bitmap.height
        ).also { cachedDepthMap = CachedDepthMap(uri, it) }
    }

    private fun grayscaleDepth(color: Int): Float = (
            Color.red(color) * LUMA_RED +
                    Color.green(color) * LUMA_GREEN +
                    Color.blue(color) * LUMA_BLUE
            ) / 255f

    private fun spectralDepth(color: Int): Float {
        val red = Color.red(color).toFloat()
        val green = Color.green(color).toFloat()
        val blue = Color.blue(color).toFloat()
        var bestPosition = 0f
        var bestDistance = Float.POSITIVE_INFINITY

        for (index in 0 until SPECTRAL_R.lastIndex) {
            val from = SPECTRAL_R[index]
            val to = SPECTRAL_R[index + 1]
            val deltaRed = (Color.red(to) - Color.red(from)).toFloat()
            val deltaGreen = (Color.green(to) - Color.green(from)).toFloat()
            val deltaBlue = (Color.blue(to) - Color.blue(from)).toFloat()
            val lengthSquared = deltaRed * deltaRed +
                    deltaGreen * deltaGreen +
                    deltaBlue * deltaBlue
            val amount = (
                    (red - Color.red(from)) * deltaRed +
                            (green - Color.green(from)) * deltaGreen +
                            (blue - Color.blue(from)) * deltaBlue
                    ).div(lengthSquared)
                .coerceIn(0f, 1f)
            val projectedRed = Color.red(from) + deltaRed * amount
            val projectedGreen = Color.green(from) + deltaGreen * amount
            val projectedBlue = Color.blue(from) + deltaBlue * amount
            val distance = (red - projectedRed).pow(2) +
                    (green - projectedGreen).pow(2) +
                    (blue - projectedBlue).pow(2)

            if (distance < bestDistance) {
                bestDistance = distance
                bestPosition = (index + amount) / SPECTRAL_R.lastIndex
            }
        }

        return bestPosition
    }

    private fun reportProgress(
        listener: AiProgressListener,
        step: Int
    ) {
        listener.onProgress(step, TOTAL_STEPS)
        service.updateProgress(
            done = step,
            total = TOTAL_STEPS
        )
    }

    private suspend fun inferDepth(
        session: OrtSession,
        model: NeuralModel,
        source: Bitmap
    ): DepthField = coroutineScope {
        val inputName = session.inputNames.firstOrNull()
            ?: error("Depth model has no input tensor")
        val inputSpec = depthInputSpec(session, inputName, model)
        val input = prepareInput(source, inputSpec)

        try {
            val values = normalizedInput(input.bitmap)
            val shape = inputSpec.tensorShape(input.bitmap)

            OnnxTensor.createTensor(
                OrtEnvironment.getEnvironment(),
                FloatBuffer.wrap(values),
                shape
            ).use { tensor ->
                session.runCancellable(mapOf(inputName to tensor)).use { result ->
                    val outputIndex = session.outputNames
                        .indexOfFirst { it in DEPTH_OUTPUT_NAMES }
                        .takeIf { it >= 0 }
                        ?: 0
                    val depthValues = flattenFloatOutput(result[outputIndex].value)
                    check(depthValues.size == input.bitmap.width * input.bitmap.height) {
                        "Unexpected depth output size ${depthValues.size}, " +
                                "expected ${input.bitmap.width * input.bitmap.height}"
                    }
                    if (
                        inputSpec.outputContract ==
                        DepthOutputContract.DirectWithSky
                    ) {
                        val skyIndex = result.size() - 1
                        val skyValues = flattenFloatOutput(result[skyIndex].value)
                        check(skyValues.size == depthValues.size) {
                            "Unexpected sky output size ${skyValues.size}, " +
                                    "expected ${depthValues.size}"
                        }
                        applySkyDepth(depthValues, skyValues)
                    } else if (
                        inputSpec.outputContract == DepthOutputContract.Direct
                    ) {
                        val confidenceIndex = session.outputNames.indexOf("confidence")
                        if (confidenceIndex >= 0) {
                            val confidence = flattenFloatOutput(
                                result[confidenceIndex].value
                            )
                            if (confidence.size == depthValues.size) {
                                suppressUncertainBackground(
                                    depth = depthValues,
                                    confidence = confidence,
                                    width = input.bitmap.width,
                                    height = input.bitmap.height
                                )
                            }
                        }
                    }
                    cropDepth(depthValues, input).also { depth ->
                        normalizeDepth(
                            values = depth.values,
                            directDepth = inputSpec.outputContract !=
                                    DepthOutputContract.Disparity
                        )
                    }
                }
            }
        } finally {
            if (input.bitmap !== source) input.bitmap.recycle()
        }
    }

    private fun depthInputSpec(
        session: OrtSession,
        inputName: String,
        model: NeuralModel
    ): DepthInputSpec {
        val shape = (session.inputInfo[inputName]?.info as? TensorInfo)?.shape
            ?: error("Depth model input is not a tensor")
        check(shape.size == 4 || shape.size == 5) {
            "Unsupported depth input rank ${shape.size}"
        }

        return DepthInputSpec(
            rank = shape.size,
            fixedWidth = shape.last().takeIf { it > 0 }?.toInt(),
            fixedHeight = shape[shape.lastIndex - 1].takeIf { it > 0 }?.toInt(),
            outputContract = model.depthOutputContract
                ?: error("Unknown depth output contract for ${model.name}")
        )
    }

    private fun prepareInput(
        source: Bitmap,
        spec: DepthInputSpec
    ): InputFrame {
        val fixedWidth = spec.fixedWidth
        val fixedHeight = spec.fixedHeight

        if (fixedWidth != null && fixedHeight != null) {
            val scale = min(
                fixedWidth / source.width.toFloat(),
                fixedHeight / source.height.toFloat()
            )
            val contentWidth = (source.width * scale).roundToInt().coerceIn(1, fixedWidth)
            val contentHeight = (source.height * scale).roundToInt().coerceIn(1, fixedHeight)
            val left = (fixedWidth - contentWidth) / 2
            val top = (fixedHeight - contentHeight) / 2
            val scaled = source.scale(contentWidth, contentHeight)
            val bitmap = createBitmap(fixedWidth, fixedHeight).apply {
                density = source.density
                val shader = BitmapShader(
                    scaled,
                    Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP
                ).apply {
                    setLocalMatrix(Matrix().apply {
                        setTranslate(left.toFloat(), top.toFloat())
                    })
                }
                Canvas(this).drawRect(
                    0f,
                    0f,
                    fixedWidth.toFloat(),
                    fixedHeight.toFloat(),
                    Paint(Paint.FILTER_BITMAP_FLAG).apply { this.shader = shader }
                )
            }
            if (scaled !== source) scaled.recycle()

            return InputFrame(
                bitmap = bitmap,
                contentLeft = left,
                contentTop = top,
                contentWidth = contentWidth,
                contentHeight = contentHeight,
                edgeInset = if (
                    spec.outputContract == DepthOutputContract.DirectWithSky
                ) {
                    MONO_EDGE_INSET
                } else 0
            )
        }

        val size = depthInputSize(
            width = source.width,
            height = source.height,
            maxSide = if (
                spec.outputContract == DepthOutputContract.Disparity
            ) DA2_INPUT_MAX_SIDE else DA3_INPUT_MAX_SIDE
        )
        val bitmap = if (source.width == size.width && source.height == size.height) {
            source
        } else {
            source.scale(size.width, size.height)
        }

        return InputFrame(
            bitmap = bitmap,
            contentLeft = 0,
            contentTop = 0,
            contentWidth = bitmap.width,
            contentHeight = bitmap.height,
            edgeInset = 0
        )
    }

    private fun cropDepth(
        values: FloatArray,
        input: InputFrame
    ): DepthField {
        if (
            input.edgeInset == 0 &&
            input.contentLeft == 0 && input.contentTop == 0 &&
            input.contentWidth == input.bitmap.width &&
            input.contentHeight == input.bitmap.height
        ) {
            return DepthField(values, input.bitmap.width, input.bitmap.height)
        }

        val croppedWidth = input.contentWidth - input.edgeInset * 2
        val croppedHeight = input.contentHeight - input.edgeInset * 2
        val startX = input.contentLeft + input.edgeInset
        val startY = input.contentTop + input.edgeInset
        val cropped = FloatArray(croppedWidth * croppedHeight)
        repeat(croppedHeight) { row ->
            values.copyInto(
                destination = cropped,
                destinationOffset = row * croppedWidth,
                startIndex = (startY + row) * input.bitmap.width + startX,
                endIndex = (startY + row) * input.bitmap.width + startX + croppedWidth
            )
        }
        return DepthField(cropped, croppedWidth, croppedHeight)
    }

    private suspend fun normalizedInput(bitmap: Bitmap): FloatArray = coroutineScope {
        val width = bitmap.width
        val height = bitmap.height
        val planeSize = width * height
        val pixels = IntArray(planeSize)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        FloatArray(planeSize * 3).also { input ->
            pixels.forEachIndexed { index, color ->
                ensureActive()
                input[index] = (Color.red(color) / 255f - IMAGE_MEAN[0]) / IMAGE_STD[0]
                input[planeSize + index] =
                    (Color.green(color) / 255f - IMAGE_MEAN[1]) / IMAGE_STD[1]
                input[planeSize * 2 + index] =
                    (Color.blue(color) / 255f - IMAGE_MEAN[2]) / IMAGE_STD[2]
            }
        }
    }

    private fun flattenFloatOutput(value: Any): FloatArray {
        val size = countFloatValues(value)
        val output = FloatArray(size)
        var index = 0

        fun append(item: Any?) {
            when (item) {
                is FloatArray -> item.copyInto(output, index).also { index += item.size }
                is Array<*> -> item.forEach(::append)
                is Number -> output[index++] = item.toFloat()
                else -> error("Unsupported depth output ${item?.javaClass}")
            }
        }

        append(value)
        return output
    }

    private fun countFloatValues(value: Any?): Int = when (value) {
        is FloatArray -> value.size
        is Array<*> -> value.sumOf(::countFloatValues)
        is Number -> 1
        else -> error("Unsupported depth output ${value?.javaClass}")
    }

    private fun normalizeDepth(
        values: FloatArray,
        directDepth: Boolean
    ) {
        val normalizedValues = if (directDepth) {
            FloatArray(values.size) { index ->
                values[index]
                    .takeIf { it.isFinite() && it > MIN_VALID_DEPTH }
                    ?.let { 1f / it }
                    ?: Float.NaN
            }
        } else {
            values
        }

        var minimum = Float.POSITIVE_INFINITY
        var maximum = Float.NEGATIVE_INFINITY

        if (directDepth) {
            val finiteValues = FloatArray(normalizedValues.size)
            var finiteCount = 0
            normalizedValues.forEach { value ->
                if (value.isFinite()) finiteValues[finiteCount++] = value
            }
            if (finiteCount > 0) {
                val sorted = finiteValues.copyOf(finiteCount).apply { sort() }
                minimum = sorted.percentile(DEPTH_PERCENTILE)
                maximum = sorted.percentile(1f - DEPTH_PERCENTILE)
            }
        } else {
            normalizedValues.forEach { value ->
                if (value.isFinite()) {
                    minimum = min(minimum, value)
                    maximum = max(maximum, value)
                }
            }
        }

        val range = maximum - minimum
        if (!range.isFinite() || range <= 1e-6f) {
            values.fill(0f)
            return
        }

        values.indices.forEach { index ->
            values[index] = ((normalizedValues[index] - minimum) / range)
                .takeIf(Float::isFinite)
                ?.coerceIn(0f, 1f)
                ?: 0f
        }
    }

    private fun applySkyDepth(
        depth: FloatArray,
        sky: FloatArray
    ) {
        var nonSkyCount = 0
        var skyCount = 0
        val nonSkyDepth = FloatArray(depth.size)

        depth.indices.forEach { index ->
            if (sky[index] < SKY_THRESHOLD) {
                val value = depth[index]
                if (value.isFinite()) nonSkyDepth[nonSkyCount++] = value
            } else {
                skyCount++
            }
        }
        if (nonSkyCount <= MIN_SKY_PIXELS || skyCount <= MIN_SKY_PIXELS) return

        val farDepth = nonSkyDepth.copyOf(nonSkyCount)
            .apply { sort() }
            .percentile(SKY_DEPTH_PERCENTILE)
        depth.indices.forEach { index ->
            if (sky[index] >= SKY_THRESHOLD) depth[index] = farDepth
        }
    }

    private fun suppressUncertainBackground(
        depth: FloatArray,
        confidence: FloatArray,
        width: Int,
        height: Int
    ) {
        if (width * height != depth.size) return

        val finiteConfidence = confidence.filter(Float::isFinite).toFloatArray()
        if (finiteConfidence.size <= MIN_SKY_PIXELS) return
        finiteConfidence.sort()
        val threshold = finiteConfidence.percentile(BACKGROUND_CONFIDENCE_PERCENTILE)
        val background = BooleanArray(depth.size)
        val queue = IntArray(depth.size)
        var queueStart = 0
        var queueEnd = 0

        fun enqueue(index: Int) {
            if (!background[index] && confidence[index] <= threshold) {
                background[index] = true
                queue[queueEnd++] = index
            }
        }

        repeat(width) { x ->
            enqueue(x)
            enqueue((height - 1) * width + x)
        }
        repeat(height) { y ->
            enqueue(y * width)
            enqueue(y * width + width - 1)
        }
        while (queueStart < queueEnd) {
            val index = queue[queueStart++]
            val x = index % width
            if (index >= width) enqueue(index - width)
            if (index < depth.size - width) enqueue(index + width)
            if (x > 0) enqueue(index - 1)
            if (x < width - 1) enqueue(index + 1)
        }
        if (queueEnd <= MIN_SKY_PIXELS) return

        val foregroundDepth = FloatArray(depth.size - queueEnd)
        var foregroundCount = 0
        depth.indices.forEach { index ->
            val value = depth[index]
            if (!background[index] && value.isFinite()) {
                foregroundDepth[foregroundCount++] = value
            }
        }
        if (foregroundCount <= MIN_SKY_PIXELS) return

        val farDepth = foregroundDepth.copyOf(foregroundCount)
            .apply { sort() }
            .percentile(SKY_DEPTH_PERCENTILE)
        background.indices.forEach { index ->
            if (background[index]) depth[index] = farDepth
        }
    }

    private fun FloatArray.percentile(fraction: Float): Float {
        val position = (lastIndex * fraction.coerceIn(0f, 1f))
        val lowerIndex = position.toInt()
        val upperIndex = (lowerIndex + 1).coerceAtMost(lastIndex)
        val amount = position - lowerIndex
        return this[lowerIndex] * (1f - amount) + this[upperIndex] * amount
    }

    private suspend fun renderEffect(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = when (params.effect) {
        DepthEffect.Map -> renderDepthMap(source, depth)
        DepthEffect.LensBlur -> renderLensBlur(source, depth, params)
        DepthEffect.BackgroundBlur -> renderBackgroundBlur(source, depth, params)
        DepthEffect.Portrait -> renderPortrait(source, depth, params)
        DepthEffect.ColorGrade -> renderColorGrade(source, depth, params.strength / 100f)
        DepthEffect.DepthEdges -> renderDepthEdges(source, depth, params.strength / 100f)
        DepthEffect.Fog -> renderFog(source, depth, params.strength / 100f)
        DepthEffect.Relight -> renderRelight(source, depth, params)
        DepthEffect.NormalMap -> renderNormalMap(source, depth, params.strength / 100f)
        DepthEffect.Stereo -> renderStereo(source, depth, params)
        DepthEffect.DepthMask -> renderDepthMask(source, depth, params)
        DepthEffect.Cutout -> renderCutout(source, depth, params)
        DepthEffect.FocusColor -> renderFocusColor(source, depth, params)
    }

    private suspend fun renderDepthMap(
        source: Bitmap,
        depth: DepthField
    ): Bitmap = renderRows(source, depth) { sourceColor, depthValue, _, _ ->
        spectralColor(
            value = depthValue,
            alpha = Color.alpha(sourceColor)
        )
    }

    private suspend fun renderFog(
        source: Bitmap,
        depth: DepthField,
        strength: Float
    ): Bitmap = renderRows(source, depth) { sourceColor, depthValue, _, _ ->
        val amount = (1f - depthValue)
            .pow(1.35f)
            .times(strength.coerceIn(0f, 1f))
            .times(0.9f)
        mixRgb(
            from = sourceColor,
            to = FOG_COLOR,
            amount = amount,
            alpha = Color.alpha(sourceColor)
        )
    }

    private suspend fun renderLensBlur(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = coroutineScope {
        val blurFrame = createBlurFrame(
            source = source,
            radius = (params.strength / 100f * MAX_BLUR_RADIUS)
                .roundToInt()
                .coerceAtLeast(1)
        )
        val output = createOutputBitmap(source)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val depthRow = FloatArray(source.width)
        val sampler = FieldSampler(depth, source.width, source.height)
        val focus = params.focus / 100f
        val focusRange = (params.focusRange / 100f).coerceAtLeast(0.01f)
        val strength = params.strength / 100f

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y, depthRow)

            for (x in sourceRow.indices) {
                val distance = abs(depthRow[x] - focus)
                val blurAmount = smoothStep(
                    edge0 = focusRange * 0.2f,
                    edge1 = focusRange,
                    value = distance
                ) * strength
                outputRow[x] = mixRgb(
                    from = sourceRow[x],
                    to = blurFrame.sample(x, y, source.width, source.height),
                    amount = blurAmount,
                    alpha = Color.alpha(sourceRow[x])
                )
            }

            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private suspend fun renderBackgroundBlur(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = coroutineScope {
        val strength = (params.strength / 100f).coerceIn(0f, 1f)
        val blurFrame = createBlurFrame(
            source = source,
            radius = (strength * MAX_BLUR_RADIUS).roundToInt().coerceAtLeast(1)
        )
        val output = createOutputBitmap(source)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val depthRow = FloatArray(source.width)
        val sampler = FieldSampler(depth, source.width, source.height)

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y, depthRow)

            for (x in sourceRow.indices) {
                val amount = (1f - depthRow[x]).pow(1.45f) * strength
                outputRow[x] = mixRgb(
                    from = sourceRow[x],
                    to = blurFrame.sample(x, y, source.width, source.height),
                    amount = amount,
                    alpha = Color.alpha(sourceRow[x])
                )
            }

            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private suspend fun renderPortrait(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap {
        val strength = (params.strength / 100f).coerceIn(0f, 1f)
        return renderRows(source, depth) { sourceColor, depthValue, _, _ ->
            val selection = depthSelection(depthValue, params)
            val background = (1f - selection) * strength
            val grayscale = (
                    Color.red(sourceColor) * 0.2126f +
                            Color.green(sourceColor) * 0.7152f +
                            Color.blue(sourceColor) * 0.0722f
                    ).roundToInt().coerceIn(0, 255)
            val muted = mixRgb(
                from = sourceColor,
                to = Color.rgb(grayscale, grayscale, grayscale),
                amount = background * 0.6f,
                alpha = Color.alpha(sourceColor)
            )
            val light = (1f - background * 0.22f + selection * strength * 0.08f)
            Color.argb(
                Color.alpha(sourceColor),
                (Color.red(muted) * light).roundToInt().coerceIn(0, 255),
                (Color.green(muted) * light).roundToInt().coerceIn(0, 255),
                (Color.blue(muted) * light).roundToInt().coerceIn(0, 255)
            )
        }
    }

    private suspend fun renderColorGrade(
        source: Bitmap,
        depth: DepthField,
        strength: Float
    ): Bitmap = renderRows(source, depth) { sourceColor, depthValue, _, _ ->
        val grade = mixRgb(
            from = FAR_DEPTH_COLOR,
            to = NEAR_DEPTH_COLOR,
            amount = smoothStep(0.08f, 0.92f, depthValue),
            alpha = 255
        )
        mixRgb(
            from = sourceColor,
            to = grade,
            amount = strength.coerceIn(0f, 1f) * 0.38f,
            alpha = Color.alpha(sourceColor)
        )
    }

    private suspend fun renderDepthEdges(
        source: Bitmap,
        depth: DepthField,
        strength: Float
    ): Bitmap = coroutineScope {
        val output = createOutputBitmap(source)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val topRow = FloatArray(source.width)
        val middleRow = FloatArray(source.width)
        val bottomRow = FloatArray(source.width)
        val sampler = FieldSampler(depth, source.width, source.height)
        val horizontalScale = source.width / depth.width.toFloat()
        val verticalScale = source.height / depth.height.toFloat()

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y - 1, topRow)
            sampler.sampleRow(y, middleRow)
            sampler.sampleRow(y + 1, bottomRow)

            for (x in sourceRow.indices) {
                val left = middleRow[(x - 1).coerceAtLeast(0)]
                val right = middleRow[(x + 1).coerceAtMost(middleRow.lastIndex)]
                val horizontal = (right - left) * horizontalScale
                val vertical = (bottomRow[x] - topRow[x]) * verticalScale
                val edge = smoothStep(
                    edge0 = 0.008f,
                    edge1 = 0.075f,
                    value = sqrt(horizontal * horizontal + vertical * vertical)
                ) * strength.coerceIn(0f, 1f)
                val sourceColor = sourceRow[x]
                val luminance = (
                        Color.red(sourceColor) * 0.2126f +
                                Color.green(sourceColor) * 0.7152f +
                                Color.blue(sourceColor) * 0.0722f
                        ) / 255f
                outputRow[x] = mixRgb(
                    from = sourceColor,
                    to = if (luminance > 0.55f) EDGE_DARK_COLOR else EDGE_LIGHT_COLOR,
                    amount = edge,
                    alpha = Color.alpha(sourceColor)
                )
            }

            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private suspend fun renderRelight(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap {
        val angle = params.lightAngle / 180f * PI.toFloat()
        val lightX = cos(angle)
        val lightY = sin(angle)
        val lightField = FloatArray(depth.values.size)

        for (y in 0 until depth.height) {
            currentCoroutineContext().ensureActive()
            for (x in 0 until depth.width) {
                val left = depth[x - 1, y]
                val right = depth[x + 1, y]
                val top = depth[x, y - 1]
                val bottom = depth[x, y + 1]
                val nx = (left - right) * 2.5f
                val ny = (top - bottom) * 2.5f
                val inverseLength = 1f / sqrt(nx * nx + ny * ny + 1f)
                val diffuse = (nx * lightX + ny * lightY + 0.65f) * inverseLength
                lightField[y * depth.width + x] = diffuse.coerceIn(-1f, 1f)
            }
        }

        val lighting = depth.copy(values = lightField)
        val strength = params.strength / 100f
        return renderRows(source, lighting) { sourceColor, light, _, _ ->
            val factor = (1f + light * strength * 0.8f).coerceIn(0.25f, 1.8f)
            Color.argb(
                Color.alpha(sourceColor),
                (Color.red(sourceColor) * factor).roundToInt().coerceIn(0, 255),
                (Color.green(sourceColor) * factor).roundToInt().coerceIn(0, 255),
                (Color.blue(sourceColor) * factor).roundToInt().coerceIn(0, 255)
            )
        }
    }

    private suspend fun renderNormalMap(
        source: Bitmap,
        depth: DepthField,
        strength: Float
    ): Bitmap = coroutineScope {
        val output = createOutputBitmap(source)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val topRow = FloatArray(source.width)
        val middleRow = FloatArray(source.width)
        val bottomRow = FloatArray(source.width)
        val sampler = FieldSampler(depth, source.width, source.height)
        val relief = 2f + strength.coerceIn(0f, 1f) * 22f
        val horizontalScale = source.width / depth.width.toFloat()
        val verticalScale = source.height / depth.height.toFloat()

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y - 1, topRow)
            sampler.sampleRow(y, middleRow)
            sampler.sampleRow(y + 1, bottomRow)

            for (x in outputRow.indices) {
                val left = middleRow[(x - 1).coerceAtLeast(0)]
                val right = middleRow[(x + 1).coerceAtMost(middleRow.lastIndex)]
                val nx = (left - right) * relief * horizontalScale
                val ny = (topRow[x] - bottomRow[x]) * relief * verticalScale
                val inverseLength = 1f / sqrt(nx * nx + ny * ny + 1f)
                outputRow[x] = Color.argb(
                    Color.alpha(sourceRow[x]),
                    ((nx * inverseLength * 0.5f + 0.5f) * 255f).roundToInt()
                        .coerceIn(0, 255),
                    ((ny * inverseLength * 0.5f + 0.5f) * 255f).roundToInt()
                        .coerceIn(0, 255),
                    (inverseLength * 255f).roundToInt().coerceIn(0, 255)
                )
            }

            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private suspend fun renderStereo(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = coroutineScope {
        val output = createOutputBitmap(source)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val depthRow = FloatArray(source.width)
        val sampler = FieldSampler(depth, source.width, source.height)
        val maximumShift = min(source.width * 0.035f, MAX_STEREO_SHIFT) *
                (params.strength / 100f).coerceIn(0f, 1f)
        val zeroPlane = params.focus / 100f

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y, depthRow)

            for (x in outputRow.indices) {
                val shift = ((depthRow[x] - zeroPlane) * maximumShift).roundToInt()
                val left = sourceRow[(x + shift).coerceIn(sourceRow.indices)]
                val right = sourceRow[(x - shift).coerceIn(sourceRow.indices)]
                outputRow[x] = Color.argb(
                    Color.alpha(sourceRow[x]),
                    Color.red(left),
                    Color.green(right),
                    Color.blue(right)
                )
            }

            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private suspend fun renderDepthMask(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = renderRows(source, depth) { sourceColor, depthValue, _, _ ->
        val selection = depthSelection(depthValue, params)
        val channel = (selection * 255f).roundToInt().coerceIn(0, 255)
        Color.argb(Color.alpha(sourceColor), channel, channel, channel)
    }

    private suspend fun renderCutout(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = renderRows(
        source = source,
        field = depth,
        forceAlpha = true
    ) { sourceColor, depthValue, _, _ ->
        val alpha = (Color.alpha(sourceColor) * depthSelection(depthValue, params))
            .roundToInt()
            .coerceIn(0, 255)
        Color.argb(
            alpha,
            Color.red(sourceColor),
            Color.green(sourceColor),
            Color.blue(sourceColor)
        )
    }

    private suspend fun renderFocusColor(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap {
        val strength = (params.strength / 100f).coerceIn(0f, 1f)
        return renderRows(source, depth) { sourceColor, depthValue, _, _ ->
            val grayscale = (
                    Color.red(sourceColor) * 0.2126f +
                            Color.green(sourceColor) * 0.7152f +
                            Color.blue(sourceColor) * 0.0722f
                    ).roundToInt().coerceIn(0, 255)
            mixRgb(
                from = sourceColor,
                to = Color.rgb(grayscale, grayscale, grayscale),
                amount = (1f - depthSelection(depthValue, params)) * strength,
                alpha = Color.alpha(sourceColor)
            )
        }
    }

    private fun depthSelection(
        depthValue: Float,
        params: DepthParams
    ): Float {
        val range = (params.focusRange / 100f).coerceAtLeast(0.01f)
        return 1f - smoothStep(
            edge0 = range * 0.55f,
            edge1 = range,
            value = abs(depthValue - params.focus / 100f)
        )
    }

    private suspend fun renderRows(
        source: Bitmap,
        field: DepthField,
        forceAlpha: Boolean = false,
        transform: (sourceColor: Int, value: Float, x: Int, y: Int) -> Int
    ): Bitmap = coroutineScope {
        val output = createOutputBitmap(source, forceAlpha)
        val sourceRow = IntArray(source.width)
        val outputRow = IntArray(source.width)
        val fieldRow = FloatArray(source.width)
        val sampler = FieldSampler(field, source.width, source.height)

        for (y in 0 until source.height) {
            ensureActive()
            source.getPixels(sourceRow, 0, source.width, 0, y, source.width, 1)
            sampler.sampleRow(y, fieldRow)
            for (x in sourceRow.indices) {
                outputRow[x] = transform(sourceRow[x], fieldRow[x], x, y)
            }
            output.setPixels(outputRow, 0, source.width, 0, y, source.width, 1)
        }

        output
    }

    private fun createOutputBitmap(
        source: Bitmap,
        forceAlpha: Boolean = false
    ): Bitmap =
        createBitmap(source.width, source.height).apply {
            density = source.density
            setHasAlpha(forceAlpha || source.hasAlpha())
        }

    private suspend fun createBlurFrame(
        source: Bitmap,
        radius: Int
    ): BlurFrame = coroutineScope {
        val scale = min(1f, BLUR_MAX_SIDE / max(source.width, source.height).toFloat())
        val width = (source.width * scale).roundToInt().coerceAtLeast(1)
        val height = (source.height * scale).roundToInt().coerceAtLeast(1)
        val bitmap = if (width == source.width && height == source.height) {
            source
        } else {
            source.scale(width, height)
        }

        try {
            val pixels = IntArray(width * height)
            val temporary = IntArray(pixels.size)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            boxBlur(
                input = pixels,
                output = temporary,
                width = width,
                height = height,
                radius = radius.coerceAtMost(min(width, height) / 2).coerceAtLeast(1),
                horizontal = true
            )
            boxBlur(
                input = temporary,
                output = pixels,
                width = width,
                height = height,
                radius = radius.coerceAtMost(min(width, height) / 2).coerceAtLeast(1),
                horizontal = false
            )
            BlurFrame(pixels, width, height)
        } finally {
            if (bitmap !== source) bitmap.recycle()
        }
    }

    private suspend fun boxBlur(
        input: IntArray,
        output: IntArray,
        width: Int,
        height: Int,
        radius: Int,
        horizontal: Boolean
    ) = coroutineScope {
        val lineCount = if (horizontal) height else width
        val lineLength = if (horizontal) width else height
        val divisor = radius * 2 + 1

        repeat(lineCount) { line ->
            ensureActive()
            var alpha = 0
            var red = 0
            var green = 0
            var blue = 0

            fun colorAt(position: Int): Int {
                val clamped = position.coerceIn(0, lineLength - 1)
                val index = if (horizontal) line * width + clamped else clamped * width + line
                return input[index]
            }

            for (offset in -radius..radius) {
                val color = colorAt(offset)
                alpha += Color.alpha(color)
                red += Color.red(color)
                green += Color.green(color)
                blue += Color.blue(color)
            }

            repeat(lineLength) { position ->
                val index = if (horizontal) {
                    line * width + position
                } else {
                    position * width + line
                }
                output[index] = Color.argb(
                    alpha / divisor,
                    red / divisor,
                    green / divisor,
                    blue / divisor
                )

                val removed = colorAt(position - radius)
                val added = colorAt(position + radius + 1)
                alpha += Color.alpha(added) - Color.alpha(removed)
                red += Color.red(added) - Color.red(removed)
                green += Color.green(added) - Color.green(removed)
                blue += Color.blue(added) - Color.blue(removed)
            }
        }
    }

    private fun spectralColor(
        value: Float,
        alpha: Int
    ): Int {
        val position = value.coerceIn(0f, 1f) * (SPECTRAL_R.size - 1)
        val fromIndex = position.toInt().coerceIn(0, SPECTRAL_R.lastIndex)
        val toIndex = (fromIndex + 1).coerceAtMost(SPECTRAL_R.lastIndex)
        return mixRgb(
            from = SPECTRAL_R[fromIndex],
            to = SPECTRAL_R[toIndex],
            amount = position - fromIndex,
            alpha = alpha
        )
    }

    private fun mixRgb(
        from: Int,
        to: Int,
        amount: Float,
        alpha: Int
    ): Int {
        val mix = amount.coerceIn(0f, 1f)
        val keep = 1f - mix
        return Color.argb(
            alpha,
            (Color.red(from) * keep + Color.red(to) * mix).roundToInt(),
            (Color.green(from) * keep + Color.green(to) * mix).roundToInt(),
            (Color.blue(from) * keep + Color.blue(to) * mix).roundToInt()
        )
    }

    private fun smoothStep(
        edge0: Float,
        edge1: Float,
        value: Float
    ): Float {
        val x = ((value - edge0) / (edge1 - edge0).coerceAtLeast(1e-6f))
            .coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    private fun depthInputSize(
        width: Int,
        height: Int,
        maxSide: Int
    ): InputSize {
        val scale = maxSide / max(width, height).toFloat()

        fun aligned(value: Int): Int = ((value * scale / MODEL_ALIGNMENT).roundToInt() *
                MODEL_ALIGNMENT).coerceIn(MODEL_ALIGNMENT, maxSide)

        return InputSize(
            width = aligned(width),
            height = aligned(height)
        )
    }

    private data class InputSize(
        val width: Int,
        val height: Int
    )

    private data class DepthInputSpec(
        val rank: Int,
        val fixedWidth: Int?,
        val fixedHeight: Int?,
        val outputContract: DepthOutputContract
    ) {
        fun tensorShape(bitmap: Bitmap): LongArray = if (rank == 5) {
            longArrayOf(1, 1, 3, bitmap.height.toLong(), bitmap.width.toLong())
        } else {
            longArrayOf(1, 3, bitmap.height.toLong(), bitmap.width.toLong())
        }
    }

    private data class InputFrame(
        val bitmap: Bitmap,
        val contentLeft: Int,
        val contentTop: Int,
        val contentWidth: Int,
        val contentHeight: Int,
        val edgeInset: Int
    )

    private data class DepthField(
        val values: FloatArray,
        val width: Int,
        val height: Int
    ) {
        operator fun get(x: Int, y: Int): Float = values[
            y.coerceIn(0, height - 1) * width + x.coerceIn(0, width - 1)
        ]

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DepthField

            if (width != other.width) return false
            if (height != other.height) return false
            if (!values.contentEquals(other.values)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + values.contentHashCode()
            return result
        }
    }

    private data class CachedDepthMap(
        val uri: String,
        val field: DepthField
    )

    private class FieldSampler(
        private val field: DepthField,
        targetWidth: Int,
        private val targetHeight: Int
    ) {
        private val left = IntArray(targetWidth)
        private val right = IntArray(targetWidth)
        private val horizontalMix = FloatArray(targetWidth)

        init {
            val denominator = (targetWidth - 1).coerceAtLeast(1)
            for (x in 0 until targetWidth) {
                val position = x * (field.width - 1f) / denominator
                left[x] = position.toInt().coerceIn(0, field.width - 1)
                right[x] = (left[x] + 1).coerceAtMost(field.width - 1)
                horizontalMix[x] = position - left[x]
            }
        }

        fun sampleRow(y: Int, output: FloatArray) {
            val verticalPosition = y.coerceIn(0, targetHeight - 1) * (field.height - 1f) /
                    (targetHeight - 1).coerceAtLeast(1)
            val top = verticalPosition.toInt().coerceIn(0, field.height - 1)
            val bottom = (top + 1).coerceAtMost(field.height - 1)
            val verticalMix = verticalPosition - top

            output.indices.forEach { x ->
                val topValue = lerp(
                    field[left[x], top],
                    field[right[x], top],
                    horizontalMix[x]
                )
                val bottomValue = lerp(
                    field[left[x], bottom],
                    field[right[x], bottom],
                    horizontalMix[x]
                )
                output[x] = lerp(topValue, bottomValue, verticalMix)
            }
        }

        private fun lerp(from: Float, to: Float, amount: Float): Float =
            from + (to - from) * amount
    }

    private data class BlurFrame(
        val pixels: IntArray,
        val width: Int,
        val height: Int
    ) {
        fun sample(
            x: Int,
            y: Int,
            targetWidth: Int,
            targetHeight: Int
        ): Int {
            val sourceX = (x.toLong() * width / targetWidth).toInt().coerceIn(0, width - 1)
            val sourceY = (y.toLong() * height / targetHeight).toInt().coerceIn(0, height - 1)
            return pixels[sourceY * width + sourceX]
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BlurFrame

            if (width != other.width) return false
            if (height != other.height) return false
            if (!pixels.contentEquals(other.pixels)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + pixels.contentHashCode()
            return result
        }
    }

    private enum class DepthOutputContract {
        Disparity,
        Direct,
        DirectWithSky
    }

    private val NeuralModel.depthOutputContract: DepthOutputContract?
        get() = when {
            name.startsWith("depth_anything_v2_") -> DepthOutputContract.Disparity
            name.startsWith("depth_anything_v3_mono_") -> DepthOutputContract.DirectWithSky
            name.startsWith("depth_anything_v3_") -> DepthOutputContract.Direct
            type == Type.DEPTH -> DepthOutputContract.Disparity
            else -> null
        }


    private companion object {
        val DEPTH_OUTPUT_NAMES = setOf("predicted_depth", "depth", "output")
        const val TOTAL_STEPS = 2
        const val MODEL_ALIGNMENT = 14
        const val DA2_INPUT_MAX_SIDE = 518
        const val DA3_INPUT_MAX_SIDE = 504
        const val CUSTOM_DEPTH_MAP_MAX_SIDE = DA2_INPUT_MAX_SIDE
        const val DEPTH_PERCENTILE = 0.02f
        const val MIN_VALID_DEPTH = 1e-6f
        const val MONO_EDGE_INSET = 7
        const val SKY_THRESHOLD = 0.3f
        const val SKY_DEPTH_PERCENTILE = 0.99f
        const val BACKGROUND_CONFIDENCE_PERCENTILE = 0.3f
        const val MIN_SKY_PIXELS = 10
        const val BLUR_MAX_SIDE = 1536f
        const val MAX_BLUR_RADIUS = 36f
        const val MAX_STEREO_SHIFT = 96f
        const val GRAYSCALE_CHANNEL_TOLERANCE = 12
        const val SPECTRAL_PIXEL_THRESHOLD = 0.05f
        const val LUMA_RED = 0.2126f
        const val LUMA_GREEN = 0.7152f
        const val LUMA_BLUE = 0.0722f
        val IMAGE_MEAN = floatArrayOf(0.485f, 0.456f, 0.406f)
        val IMAGE_STD = floatArrayOf(0.229f, 0.224f, 0.225f)
        val FOG_COLOR = Color.rgb(222, 232, 242)
        val FAR_DEPTH_COLOR = Color.rgb(35, 72, 104)
        val NEAR_DEPTH_COLOR = Color.rgb(245, 151, 79)
        val EDGE_DARK_COLOR = Color.rgb(18, 21, 28)
        val EDGE_LIGHT_COLOR = Color.rgb(237, 242, 248)
        val SPECTRAL_R = intArrayOf(
            Color.rgb(94, 79, 162),
            Color.rgb(50, 136, 189),
            Color.rgb(102, 194, 165),
            Color.rgb(171, 221, 164),
            Color.rgb(230, 245, 152),
            Color.rgb(255, 255, 191),
            Color.rgb(254, 224, 139),
            Color.rgb(253, 174, 97),
            Color.rgb(244, 109, 67),
            Color.rgb(213, 62, 79),
            Color.rgb(158, 1, 66)
        )
    }
}
