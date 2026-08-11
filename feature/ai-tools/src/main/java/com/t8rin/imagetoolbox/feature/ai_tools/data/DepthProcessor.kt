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
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.saving.KeepAliveService
import com.t8rin.imagetoolbox.core.domain.saving.track
import com.t8rin.imagetoolbox.core.domain.saving.updateProgress
import com.t8rin.imagetoolbox.core.utils.extractMessage
import com.t8rin.imagetoolbox.feature.ai_tools.domain.AiProgressListener
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthEffect
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.DepthParams
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
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    suspend fun process(
        session: OrtSession,
        source: Bitmap,
        params: DepthParams,
        listener: AiProgressListener
    ): Bitmap? = withContext(defaultDispatcher) {
        service.track(
            onFailure = { listener.onError(it.extractMessage()) },
            action = {
                reportProgress(listener, 0)
                val depth = inferDepth(session, source)
                reportProgress(listener, 1)
                renderEffect(
                    source = source,
                    depth = depth,
                    params = params
                ).also {
                    reportProgress(listener, 2)
                }
            }
        )
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
        source: Bitmap
    ): DepthField = coroutineScope {
        val inputSize = depthInputSize(source.width, source.height)
        val inputBitmap = if (
            source.width == inputSize.width && source.height == inputSize.height
        ) {
            source
        } else {
            Bitmap.createScaledBitmap(
                source,
                inputSize.width,
                inputSize.height,
                true
            )
        }

        try {
            val input = normalizedInput(inputBitmap)
            val inputName = session.inputNames.firstOrNull()
                ?: error("Depth model has no input tensor")
            val shape = longArrayOf(
                1,
                3,
                inputSize.height.toLong(),
                inputSize.width.toLong()
            )

            OnnxTensor.createTensor(
                OrtEnvironment.getEnvironment(),
                FloatBuffer.wrap(input),
                shape
            ).use { tensor ->
                session.run(mapOf(inputName to tensor)).use { result ->
                    val values = flattenFloatOutput(result[0].value)
                    check(values.size == inputSize.pixelCount) {
                        "Unexpected depth output size ${values.size}, " +
                                "expected ${inputSize.pixelCount}"
                    }
                    normalizeDepth(values)
                    DepthField(
                        values = values,
                        width = inputSize.width,
                        height = inputSize.height
                    )
                }
            }
        } finally {
            if (inputBitmap !== source) inputBitmap.recycle()
        }
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

    private fun normalizeDepth(values: FloatArray) {
        var minimum = Float.POSITIVE_INFINITY
        var maximum = Float.NEGATIVE_INFINITY

        values.forEach { value ->
            if (value.isFinite()) {
                minimum = min(minimum, value)
                maximum = max(maximum, value)
            }
        }

        val range = maximum - minimum
        if (!range.isFinite() || range <= 1e-6f) {
            values.fill(0f)
            return
        }

        values.indices.forEach { index ->
            values[index] = ((values[index] - minimum) / range)
                .takeIf(Float::isFinite)
                ?.coerceIn(0f, 1f)
                ?: 0f
        }
    }

    private suspend fun renderEffect(
        source: Bitmap,
        depth: DepthField,
        params: DepthParams
    ): Bitmap = when (params.effect) {
        DepthEffect.Map -> renderDepthMap(source, depth)
        DepthEffect.LensBlur -> renderLensBlur(source, depth, params)
        DepthEffect.Fog -> renderFog(source, depth, params.strength / 100f)
        DepthEffect.Relight -> renderRelight(source, depth, params)
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

    private suspend fun renderRows(
        source: Bitmap,
        field: DepthField,
        transform: (sourceColor: Int, value: Float, x: Int, y: Int) -> Int
    ): Bitmap = coroutineScope {
        val output = createOutputBitmap(source)
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

    private fun createOutputBitmap(source: Bitmap): Bitmap =
        createBitmap(source.width, source.height).apply {
            density = source.density
            setHasAlpha(source.hasAlpha())
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
            Bitmap.createScaledBitmap(source, width, height, true)
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
        height: Int
    ): InputSize {
        val scale = DEPTH_INPUT_MAX_SIDE / max(width, height).toFloat()

        fun aligned(value: Int): Int = ((value * scale / MODEL_ALIGNMENT).roundToInt() *
                MODEL_ALIGNMENT).coerceIn(MODEL_ALIGNMENT, DEPTH_INPUT_MAX_SIDE)

        return InputSize(
            width = aligned(width),
            height = aligned(height)
        )
    }

    private data class InputSize(
        val width: Int,
        val height: Int
    ) {
        val pixelCount: Int = width * height
    }

    private data class DepthField(
        val values: FloatArray,
        val width: Int,
        val height: Int
    ) {
        operator fun get(x: Int, y: Int): Float = values[
            y.coerceIn(0, height - 1) * width + x.coerceIn(0, width - 1)
        ]
    }

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
            val verticalPosition = y * (field.height - 1f) /
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
    }

    private companion object {
        const val TOTAL_STEPS = 2
        const val MODEL_ALIGNMENT = 14
        const val DEPTH_INPUT_MAX_SIDE = 518
        const val BLUR_MAX_SIDE = 1536f
        const val MAX_BLUR_RADIUS = 36f
        val IMAGE_MEAN = floatArrayOf(0.485f, 0.456f, 0.406f)
        val IMAGE_STD = floatArrayOf(0.229f, 0.224f, 0.225f)
        val FOG_COLOR = Color.rgb(222, 232, 242)
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
