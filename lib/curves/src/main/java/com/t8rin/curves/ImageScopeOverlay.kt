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

package com.t8rin.curves

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.os.Process
import android.view.View
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import com.t8rin.histogram.ImageScope
import com.t8rin.histogram.ImageScopeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun ImageScopeOverlay(
    bitmap: android.graphics.Bitmap,
    type: ImageCurvesEditorScope,
    colors: ImageCurvesEditorColors,
    modifier: Modifier = Modifier
) {
    var scopeFrame by remember {
        mutableStateOf<ScopeFrame?>(null)
    }

    LaunchedEffect(bitmap, type) {
        val scopeType = when (type) {
            ImageCurvesEditorScope.Waveform -> ImageScopeType.Waveform
            ImageCurvesEditorScope.Vectorscope -> ImageScopeType.Vectorscope
            else -> return@LaunchedEffect
        }
        val updatedScope = withContext(Dispatchers.Default) {
            val threadId = Process.myTid()
            val originalPriority = Process.getThreadPriority(threadId)
            try {
                Process.setThreadPriority(
                    threadId,
                    Process.THREAD_PRIORITY_BACKGROUND
                )
                ImageScope.from(
                    bitmap = bitmap,
                    type = scopeType,
                    size = if (scopeType == ImageScopeType.Waveform) {
                        WaveformScopeResolution
                    } else {
                        VectorscopeScopeResolution
                    }
                )
            } finally {
                Process.setThreadPriority(threadId, originalPriority)
            }
        }
        coroutineContext.ensureActive()
        scopeFrame = ScopeFrame(
            type = type,
            scope = updatedScope
        )
    }

    AnimatedContent(
        targetState = scopeFrame,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = modifier
    ) { frame ->
        if (frame != null) {
            Box(Modifier.fillMaxSize()) {
                val waveformColors = intArrayOf(
                    colors.redCurveColor.toArgb(),
                    colors.greenCurveColor.toArgb(),
                    colors.blueCurveColor.toArgb()
                )
                val hueColors = colors.hueCurveColors
                    .ifEmpty { FallbackHueColors }
                    .map { it.toArgb() }
                    .toIntArray()
                AndroidView(
                    factory = { ScopeDataView(it) },
                    update = { view ->
                        view.setScope(
                            scope = frame.scope,
                            type = frame.type,
                            waveformColors = waveformColors,
                            hueColors = hueColors,
                            neutralColor = colors.lumaCurveColor.toArgb()
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
                if (frame.type == ImageCurvesEditorScope.Vectorscope) {
                    VectorscopeGuides(
                        color = colors.guidelinesColor
                            .copy(alpha = colors.gridLineAlpha),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private class ScopeDataView(context: Context) : View(context) {

    private var scope: ImageScope? = null
    private var type = ImageCurvesEditorScope.None
    private var waveformColors = IntArray(0)
    private var hueColors = IntArray(0)
    private var neutralColor = AndroidColor.WHITE
    private var renderedScope: Bitmap? = null
    private var renderFuture: Future<*>? = null
    private var renderGeneration = 0

    fun setScope(
        scope: ImageScope,
        type: ImageCurvesEditorScope,
        waveformColors: IntArray,
        hueColors: IntArray,
        neutralColor: Int
    ) {
        val colorsChanged = !this.waveformColors.contentEquals(waveformColors) ||
                !this.hueColors.contentEquals(hueColors) ||
                this.neutralColor != neutralColor
        if (this.scope === scope && this.type == type && !colorsChanged) return

        this.scope = scope
        this.type = type
        this.waveformColors = waveformColors.copyOf()
        this.hueColors = hueColors.copyOf()
        this.neutralColor = neutralColor
        scheduleRender()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        scheduleRender()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (renderedScope == null) scheduleRender()
    }

    override fun onDetachedFromWindow() {
        renderGeneration++
        renderFuture?.cancel(true)
        renderFuture = null
        renderedScope?.recycle()
        renderedScope = null
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: AndroidCanvas) {
        super.onDraw(canvas)
        renderedScope?.takeUnless(Bitmap::isRecycled)?.let { bitmap ->
            canvas.drawBitmap(
                bitmap,
                0f,
                0f,
                null
            )
        }
    }

    private fun scheduleRender() {
        val scope = scope
        if (scope == null || width <= 0 || height <= 0) {
            replaceRenderedScope(null)
            return
        }

        val generation = ++renderGeneration
        val request = ScopeRenderRequest(
            scope = scope,
            type = type,
            width = width,
            height = height,
            density = resources.displayMetrics.density,
            waveformColors = waveformColors.copyOf(),
            hueColors = hueColors.copyOf(),
            neutralColor = neutralColor
        )
        renderFuture?.cancel(true)
        renderFuture = RenderExecutor.submit {
            val bitmap = request.render()
            post {
                if (generation == renderGeneration && isAttachedToWindow) {
                    replaceRenderedScope(bitmap)
                } else {
                    bitmap?.recycle()
                }
            }
        }
    }

    private fun replaceRenderedScope(bitmap: Bitmap?) {
        if (renderedScope === bitmap) return
        renderedScope?.recycle()
        renderedScope = bitmap
        invalidate()
    }

    private companion object {
        val RenderExecutor = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "ImageCurvesScopeRenderer").apply {
                priority = Thread.MIN_PRIORITY
                isDaemon = true
            }
        }
    }
}

private data class ScopeRenderRequest(
    val scope: ImageScope,
    val type: ImageCurvesEditorScope,
    val width: Int,
    val height: Int,
    val density: Float,
    val waveformColors: IntArray,
    val hueColors: IntArray,
    val neutralColor: Int
) {

    fun render(): Bitmap? = when (type) {
        ImageCurvesEditorScope.Waveform -> renderWaveform()
        ImageCurvesEditorScope.Vectorscope -> renderVectorscope()
        else -> null
    }

    private fun renderWaveform(): Bitmap? {
        val groupCount = WaveformColorCombinationCount * DensityLevels
        val counts = IntArray(groupCount)
        val binCount = scope.size * scope.size
        repeat(binCount) { index ->
            if (index % CancellationCheckInterval == 0 && Thread.interrupted()) {
                return null
            }
            val dataIndex = index * ScopeChannelCount
            val red = scope.data.unsigned(dataIndex)
            val green = scope.data.unsigned(dataIndex + 1)
            val blue = scope.data.unsigned(dataIndex + 2)
            val density = max(red, max(green, blue))
            if (density == 0) return@repeat
            val colorGroup = waveformColorMask(red, green, blue) - 1
            counts[colorGroup * DensityLevels + density.bucket()]++
        }

        val pointGroups = Array(groupCount) {
            FloatArray(counts[it] * CoordinateCount)
        }
        val pointGroupColors = IntArray(groupCount) { group ->
            val colorMask = group / DensityLevels + 1
            val bucket = group % DensityLevels
            combinedWaveformColor(colorMask)
                .withAlpha(bucket.alpha())
        }
        val offsets = IntArray(groupCount)
        repeat(binCount) { index ->
            if (index % CancellationCheckInterval == 0 && Thread.interrupted()) {
                return null
            }
            val dataIndex = index * ScopeChannelCount
            val red = scope.data.unsigned(dataIndex)
            val green = scope.data.unsigned(dataIndex + 1)
            val blue = scope.data.unsigned(dataIndex + 2)
            val density = max(red, max(green, blue))
            if (density == 0) return@repeat
            val x = index % scope.size
            val y = index / scope.size
            val colorGroup = waveformColorMask(red, green, blue) - 1
            val group = colorGroup * DensityLevels + density.bucket()
            val offset = offsets[group]
            pointGroups[group][offset] = (x + 0.5f) / scope.size * width
            pointGroups[group][offset + 1] = (y + 0.5f) / scope.size * height
            offsets[group] += CoordinateCount
        }
        return renderPointGroups(
            pointGroups = pointGroups,
            pointGroupColors = pointGroupColors,
            drawGlow = false
        )
    }

    private fun waveformColorMask(red: Int, green: Int, blue: Int): Int {
        val maximum = max(red, max(green, blue))
        val threshold = maximum * WaveformOverlapThreshold
        var mask = 0
        if (red >= threshold) mask = mask or RedMask
        if (green >= threshold) mask = mask or GreenMask
        if (blue >= threshold) mask = mask or BlueMask
        return mask.coerceAtLeast(RedMask)
    }

    private fun combinedWaveformColor(mask: Int): Int {
        var red = 0
        var green = 0
        var blue = 0
        waveformColors.forEachIndexed { index, color ->
            if (mask and (1 shl index) != 0) {
                red = max(red, AndroidColor.red(color))
                green = max(green, AndroidColor.green(color))
                blue = max(blue, AndroidColor.blue(color))
            }
        }
        return AndroidColor.rgb(red, green, blue)
    }

    private fun renderVectorscope(): Bitmap? {
        val colorGroupCount = HueBuckets + NeutralHueGroupCount
        val groupCount = colorGroupCount * DensityLevels
        val counts = IntArray(groupCount)
        val binCount = scope.size * scope.size
        repeat(binCount) { index ->
            if (index % CancellationCheckInterval == 0 && Thread.interrupted()) {
                return null
            }
            val dataIndex = index * ScopeChannelCount
            val density = scope.data.unsigned(dataIndex + DensityChannel)
            if (density == 0) return@repeat
            val colorGroup = vectorColorGroup(
                red = scope.data.unsigned(dataIndex),
                green = scope.data.unsigned(dataIndex + 1),
                blue = scope.data.unsigned(dataIndex + 2)
            )
            counts[colorGroup * DensityLevels + density.bucket()]++
        }

        val pointGroups = Array(groupCount) {
            FloatArray(counts[it] * CoordinateCount)
        }
        val pointGroupColors = IntArray(groupCount) { group ->
            val colorGroup = group / DensityLevels
            val bucket = group % DensityLevels
            val baseColor = if (colorGroup == HueBuckets) {
                neutralColor
            } else {
                hueColors.getOrElse(
                    colorGroup * hueColors.size / HueBuckets
                ) {
                    AndroidColor.HSVToColor(
                        floatArrayOf(colorGroup * 360f / HueBuckets, 0.8f, 1f)
                    )
                }
            }
            baseColor.withAlpha(bucket.alpha())
        }
        val offsets = IntArray(groupCount)
        val diameter = min(width, height) * VectorscopeDiameterFraction
        val left = (width - diameter) / 2f
        val top = (height - diameter) / 2f
        repeat(binCount) { index ->
            if (index % CancellationCheckInterval == 0 && Thread.interrupted()) {
                return null
            }
            val dataIndex = index * ScopeChannelCount
            val density = scope.data.unsigned(dataIndex + DensityChannel)
            if (density == 0) return@repeat
            val colorGroup = vectorColorGroup(
                red = scope.data.unsigned(dataIndex),
                green = scope.data.unsigned(dataIndex + 1),
                blue = scope.data.unsigned(dataIndex + 2)
            )
            val group = colorGroup * DensityLevels + density.bucket()
            val offset = offsets[group]
            val x = index % scope.size
            val y = index / scope.size
            pointGroups[group][offset] = left + x / (scope.size - 1f) * diameter
            pointGroups[group][offset + 1] = top + y / (scope.size - 1f) * diameter
            offsets[group] += CoordinateCount
        }
        return renderPointGroups(
            pointGroups = pointGroups,
            pointGroupColors = pointGroupColors,
            drawGlow = true
        )
    }

    private fun vectorColorGroup(red: Int, green: Int, blue: Int): Int {
        val maximum = max(red, max(green, blue))
        val minimum = min(red, min(green, blue))
        val range = maximum - minimum
        if (range < NeutralThreshold) return HueBuckets

        val hue = when (maximum) {
            red -> (green - blue).toFloat() / range
            green -> 2f + (blue - red).toFloat() / range
            else -> 4f + (red - green).toFloat() / range
        }.let { value ->
            (value / 6f).let { if (it < 0f) it + 1f else it }
        }
        return (hue * HueBuckets).toInt().coerceIn(0, HueBuckets - 1)
    }

    private fun renderPointGroups(
        pointGroups: Array<FloatArray>,
        pointGroupColors: IntArray,
        drawGlow: Boolean
    ): Bitmap? {
        if (Thread.interrupted()) return null
        val bitmap = createBitmap(width, height)
        val canvas = AndroidCanvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
        }
        val pointWidth = max(density * 0.9f, 1f)

        pointGroups.forEachIndexed { index, points ->
            if (Thread.interrupted()) {
                bitmap.recycle()
                return null
            }
            if (points.isEmpty()) return@forEachIndexed
            val color = pointGroupColors[index]
            val alpha = AndroidColor.alpha(color)

            if (drawGlow) {
                paint.strokeWidth = pointWidth * 2.6f
                paint.color = color.withAlpha(max(alpha / 4, 8))
                canvas.drawPoints(points, paint)
            }

            paint.strokeWidth = pointWidth
            paint.color = color
            canvas.drawPoints(points, paint)
        }
        return bitmap
    }
}

@Composable
private fun VectorscopeGuides(
    color: Color,
    modifier: Modifier
) {
    Canvas(modifier) {
        val strokeWidth = 0.75.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * VectorscopeRadiusFraction
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(strokeWidth)
        )
        drawLine(
            color = color,
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius),
            strokeWidth = strokeWidth
        )
        ReferenceAngles.forEach { angle ->
            val point = Offset(
                x = center.x + kotlin.math.cos(angle) * radius,
                y = center.y + kotlin.math.sin(angle) * radius
            )
            drawCircle(
                color = color,
                radius = 2.dp.toPx(),
                center = point,
                style = Stroke(strokeWidth)
            )
        }
    }
}

private fun ByteBuffer.unsigned(index: Int): Int = get(index).toInt() and 0xFF

private fun Int.bucket(): Int = ((this - 1) * DensityLevels / 255)
    .coerceIn(0, DensityLevels - 1)

private fun Int.alpha(): Int = MinPointAlpha +
        this * (MaxPointAlpha - MinPointAlpha) / (DensityLevels - 1)

private fun Int.withAlpha(alpha: Int): Int = AndroidColor.argb(
    alpha,
    AndroidColor.red(this),
    AndroidColor.green(this),
    AndroidColor.blue(this)
)

private data class ScopeFrame(
    val type: ImageCurvesEditorScope,
    val scope: ImageScope
)

private const val WaveformScopeResolution = 256
private const val VectorscopeScopeResolution = 384
private const val ScopeChannelCount = 4
private const val DensityChannel = 3
private const val DensityLevels = 6
private const val WaveformColorCombinationCount = 7
private const val WaveformOverlapThreshold = 0.55f
private const val RedMask = 1
private const val GreenMask = 2
private const val BlueMask = 4
private const val HueBuckets = 18
private const val NeutralHueGroupCount = 1
private const val NeutralThreshold = 16
private const val CoordinateCount = 2
private const val CancellationCheckInterval = 1024
private const val MinPointAlpha = 46
private const val MaxPointAlpha = 224
private const val VectorscopeRadiusFraction = 0.46f
private const val VectorscopeDiameterFraction = VectorscopeRadiusFraction * 2f
private val ReferenceAngles = List(6) { index ->
    (Math.PI * 2.0 * index / 6.0).toFloat()
}
private val FallbackHueColors = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red
)
