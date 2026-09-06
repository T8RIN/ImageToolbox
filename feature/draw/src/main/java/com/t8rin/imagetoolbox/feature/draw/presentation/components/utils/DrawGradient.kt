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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.utils

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.LruCache
import androidx.core.graphics.createBitmap
import com.awxkee.aire.Aire
import com.awxkee.aire.EdgeMode
import com.awxkee.aire.GaussianPreciseLevel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

internal fun Canvas.drawPathWithGradient(
    path: Path,
    paint: Paint,
    palette: GradientPalette?,
    isFilled: Boolean,
    canvasSize: IntegerSize,
    softnessRadius: Float = 0f,
    cache: GradientStrokeCache? = null,
    gradientLength: Float = 1f,
    isGradientMirrored: Boolean = false
) {
    if (palette == null) {
        drawPath(path, paint)
    } else if (isFilled) {
        drawPath(path, paint.withPathGradient(path, palette, gradientLength, isGradientMirrored))
    } else {
        drawGradientStroke(
            path = path,
            paint = paint,
            palette = palette,
            canvasSize = canvasSize,
            softnessRadius = softnessRadius,
            cache = cache,
            gradientLength = gradientLength,
            isGradientMirrored = isGradientMirrored
        )
    }
}

internal fun Paint.withPathGradient(
    path: Path,
    palette: GradientPalette,
    gradientLength: Float = 1f,
    isGradientMirrored: Boolean = false
): Paint = Paint(this).apply {
    shader = path.createGradient(palette, gradientLength, isGradientMirrored)
}

private fun Canvas.drawGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    canvasSize: IntegerSize,
    softnessRadius: Float,
    cache: GradientStrokeCache?,
    gradientLength: Float,
    isGradientMirrored: Boolean
) {
    if (path.isEmpty || paint.alpha == 0) return

    if (softnessRadius > 0f) {
        drawSoftGradientStroke(
            path,
            paint,
            palette,
            canvasSize,
            softnessRadius,
            cache,
            gradientLength,
            isGradientMirrored
        )
        return
    }

    val cycleLength = canvasSize.gradientCycleLength() * gradientLength.normalizedGradientLength()
    val measurementLength = canvasSize.gradientCycleLength()
    val outline = Path()
    val outlinePaint = Paint(paint).apply { maskFilter = null }
    if (!outlinePaint.getFillPath(path, outline)) {
        drawPath(path, paint.withPathGradient(path, palette, gradientLength, isGradientMirrored))
        return
    }
    val bounds = if (cache != null) RectF(clipBounds) else {
        RectF().also { outline.computeBounds(it, true) }.apply { inset(-2f, -2f) }
    }
    if (!bounds.intersect(RectF(clipBounds))) return
    val pixels = Rect().also { bounds.roundOut(it) }
    val colourWidth = paint.strokeWidth + 2f
    val interiorWidth = if (
        paint.pathEffect == null && paint.strokeCap == Paint.Cap.ROUND && paint.strokeJoin == Paint.Join.ROUND
    ) {
        (paint.strokeWidth - COLOUR_SAMPLE_STEP * measurementLength / MEASURE_CYCLE_LENGTH - 2f)
            .coerceAtLeast(0f)
    } else 0f
    val renderer = cache ?: GradientStrokeCache()
    try {
        val bitmap = renderer.render(
            path,
            palette,
            cycleLength,
            measurementLength,
            colourWidth,
            interiorWidth,
            pixels,
            isGradientMirrored,
            paint.strokeCap,
            paint.strokeJoin,
            paint.strokeMiter
        )
        val colourShader =
            BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
                setLocalMatrix(Matrix().apply {
                    setTranslate(pixels.left.toFloat(), pixels.top.toFloat())
                })
            }
        val strokePaint = Paint(paint).apply {
            style = Paint.Style.FILL
            pathEffect = null
            shader = colourShader
            isFilterBitmap = true
            maskFilter = null
            clearShadowLayer()
        }
        val interior = renderer.interiorCoverage
        if (interior == null) {
            drawPath(path, Paint(paint).apply {
                shader = colourShader
                maskFilter = null
                isFilterBitmap = true
                clearShadowLayer()
            })
        } else {
            val coverage = interior.copy(Bitmap.Config.ALPHA_8, true)
            try {
                Canvas(coverage).apply {
                    translate(-pixels.left.toFloat(), -pixels.top.toFloat())
                    drawPath(outline, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
                }
                val coverageShader =
                    BitmapShader(coverage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
                        setLocalMatrix(Matrix().apply {
                            setTranslate(
                                pixels.left.toFloat(),
                                pixels.top.toFloat()
                            )
                        })
                    }
                strokePaint.shader =
                    ComposeShader(colourShader, coverageShader, PorterDuff.Mode.DST_IN)
                drawRect(RectF(pixels), strokePaint)
            } finally {
                coverage.recycle()
            }
        }
    } finally {
        if (cache == null) renderer.clear()
    }
}

private fun Canvas.drawSoftGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    canvasSize: IntegerSize,
    radius: Float,
    cache: GradientStrokeCache?,
    gradientLength: Float,
    isGradientMirrored: Boolean
) {
    val sigma = radius * 0.57735f + 0.5f
    val feather = ceil(sigma * 3f) + 2f
    val outline = Path()
    val outlinePaint = Paint(paint).apply { maskFilter = null }
    outlinePaint.getFillPath(path, outline)
    val bounds = if (cache != null) RectF(clipBounds) else {
        RectF().also { outline.computeBounds(it, true) }
    }
    bounds.inset(-feather, -feather)
    val clip = RectF(clipBounds).apply { inset(-feather, -feather) }
    if (!bounds.intersect(clip)) return

    val scale = (256f / maxOf(canvasSize.width, canvasSize.height))
        .coerceIn(min(1f, 2f / sigma), 1f)
    val left = floor(bounds.left * scale) / scale
    val top = floor(bounds.top * scale) / scale
    val bitmap = createBitmap(
        ceil((bounds.right - left) * scale).toInt().coerceAtLeast(1),
        ceil((bounds.bottom - top) * scale).toInt().coerceAtLeast(1)
    )
    try {
        val strokeCanvas = Canvas(bitmap)
        strokeCanvas.scale(scale, scale)
        strokeCanvas.translate(-left, -top)
        strokeCanvas.drawGradientStroke(
            path, Paint(paint).apply {
                alpha = 255
                colorFilter = null
                xfermode = null
            }, palette, canvasSize, 0f, cache, gradientLength, isGradientMirrored
        )
        val scaledSigma = sigma * scale
        val kernelSize = ceil(scaledSigma * 3f).toInt() * 2 + 1
        val blurred = Aire.gaussianBlur(
            bitmap = bitmap,
            horizontalKernelSize = kernelSize,
            verticalKernelSize = kernelSize,
            horizontalSigma = scaledSigma,
            verticalSigma = scaledSigma,
            gaussianPreciseLevel = GaussianPreciseLevel.EXACT,
            edgeMode = EdgeMode.CLAMP
        )
        try {
            val destination =
                RectF(left, top, left + bitmap.width / scale, top + bitmap.height / scale)
            drawBitmap(blurred, null, destination, Paint(Paint.FILTER_BITMAP_FLAG).apply {
                alpha = paint.alpha
                colorFilter = paint.colorFilter
                xfermode = paint.xfermode
            })
        } finally {
            if (blurred !== bitmap) blurred.recycle()
        }
    } finally {
        bitmap.recycle()
    }
}

internal class GradientStrokeCache {
    private var bitmap: Bitmap? = null
    private var tip: Bitmap? = null
    internal var interiorCoverage: Bitmap? = null
        private set
    private var interiorTip: Bitmap? = null
    private val replacePaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC) }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private var tipLeft = 0
    private var tipTop = 0
    private var key: ColourKey? = null
    private var segments: List<ColourSegment> = emptyList()

    internal fun render(
        path: Path,
        palette: GradientPalette,
        cycleLength: Float,
        measurementLength: Float,
        width: Float,
        interiorWidth: Float,
        bounds: Rect,
        isGradientMirrored: Boolean,
        strokeCap: Paint.Cap,
        strokeJoin: Paint.Join,
        strokeMiter: Float
    ): Bitmap {
        val nextKey =
            ColourKey(
                palette,
                cycleLength,
                measurementLength,
                width,
                interiorWidth,
                Rect(bounds),
                isGradientMirrored,
                strokeCap,
                strokeJoin,
                strokeMiter
            )
        val repeatLength = cycleLength * if (isGradientMirrored) 2f else 1f
        val sampleStep = (COLOUR_SAMPLE_STEP * cycleLength / measurementLength)
            .coerceIn(1f, COLOUR_SAMPLE_STEP)
        val nextSegments = path.colourSegments(repeatLength, measurementLength, width, sampleStep)
        val completeCount = (nextSegments.size - 1).coerceAtLeast(0)
        val canAppend = key == nextKey && segments.size <= completeCount &&
                segments.indices.all { segments[it] == nextSegments[it] }
        val colours = if (isGradientMirrored) palette.mirroredColours() else {
            palette.colors.map { it.colorInt }.let {
                if (it.first() == it.last()) it else it + it.first()
            }.toIntArray()
        }
        if (!canAppend) {
            clear()
            bitmap = createBitmap(bounds.width(), bounds.height())
            bitmap!!.eraseColor(colours.first())
            if (interiorWidth > 0f) {
                interiorCoverage =
                    createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ALPHA_8)
            }
            key = nextKey
        }
        val target = bitmap!!
        val canvas = Canvas(target)
        val interiorCanvas = interiorCoverage?.let(::Canvas)
        tip?.let {
            canvas.drawBitmap(it, tipLeft.toFloat(), tipTop.toFloat(), replacePaint)
            it.recycle()
            tip = null
        }
        interiorTip?.let {
            interiorCanvas?.apply {
                drawRect(
                    tipLeft.toFloat(), tipTop.toFloat(),
                    (tipLeft + it.width).toFloat(), (tipTop + it.height).toFloat(), clearPaint
                )
                drawBitmap(it, tipLeft.toFloat(), tipTop.toFloat(), null)
            }
            it.recycle()
            interiorTip = null
        }
        canvas.translate(-bounds.left.toFloat(), -bounds.top.toFloat())
        interiorCanvas?.translate(-bounds.left.toFloat(), -bounds.top.toFloat())
        val interiorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            strokeWidth = interiorWidth
            this.strokeCap = Paint.Cap.ROUND
        }
        val gradient =
            LinearGradient(
                0f, 0f, cycleLength, 0f, colours, null,
                if (isGradientMirrored) Shader.TileMode.MIRROR else Shader.TileMode.REPEAT
            )
        val useStrokeShape = strokeCap != Paint.Cap.ROUND || strokeJoin != Paint.Join.ROUND
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = width
            this.strokeCap = if (strokeCap == Paint.Cap.SQUARE) Paint.Cap.BUTT else strokeCap
            this.strokeJoin = strokeJoin
            this.strokeMiter = strokeMiter
            shader = gradient
            xfermode = replacePaint.xfermode
        }
        val segmentPath = Path()
        fun strokeShape(index: Int): Path = segmentPath.apply {
            rewind()
            val segment = nextSegments[index]
            val previous = nextSegments.getOrNull(index - 1)?.takeIf {
                it.endX == segment.startX && it.endY == segment.startY
            }
            val next = nextSegments.getOrNull(index + 1)?.takeIf {
                it.startX == segment.endX && it.startY == segment.endY
            }
            val dx = segment.endX - segment.startX
            val dy = segment.endY - segment.startY
            val capScale = if (strokeCap == Paint.Cap.SQUARE) {
                width / (2f * hypot(dx, dy))
            } else 0f
            // Extend only contour endpoints; internal square caps would repaint nearby ink.
            if (previous != null) {
                moveTo(previous.startX, previous.startY)
                lineTo(segment.startX, segment.startY)
            } else {
                moveTo(segment.startX - dx * capScale, segment.startY - dy * capScale)
                lineTo(segment.startX, segment.startY)
            }
            lineTo(segment.endX, segment.endY)
            if (next == null) {
                lineTo(segment.endX + dx * capScale, segment.endY + dy * capScale)
            }
        }
        val matrix = Matrix()
        val values = FloatArray(9).apply { this[8] = 1f }
        fun draw(index: Int) {
            val segment = nextSegments[index]
            val tx = segment.tangentX
            val ty = segment.tangentY
            values[0] = tx / segment.distanceScale
            values[1] = -ty
            values[2] = segment.startX - tx * segment.distance
            values[3] = ty / segment.distanceScale
            values[4] = tx
            values[5] = segment.startY - ty * segment.distance
            matrix.setValues(values)
            gradient.setLocalMatrix(matrix)
            if (useStrokeShape) canvas.drawPath(strokeShape(index), paint)
            else canvas.drawLine(segment.startX, segment.startY, segment.endX, segment.endY, paint)
            interiorCanvas?.drawLine(
                segment.startX,
                segment.startY,
                segment.endX,
                segment.endY,
                interiorPaint
            )
        }
        for (index in segments.size until completeCount) draw(index)
        segments = nextSegments.subList(0, completeCount)
        nextSegments.lastOrNull()?.let { last ->
            val radius = width / 2f + 1f
            val tipBounds = if (useStrokeShape) {
                val outline = Path()
                paint.getFillPath(strokeShape(nextSegments.lastIndex), outline)
                val extent = RectF().also { outline.computeBounds(it, true) }
                extent.inset(-1f, -1f)
                Rect().also(extent::roundOut)
            } else Rect(
                floor(min(last.startX, last.endX) - radius).toInt(),
                floor(min(last.startY, last.endY) - radius).toInt(),
                ceil(maxOf(last.startX, last.endX) + radius).toInt(),
                ceil(maxOf(last.startY, last.endY) + radius).toInt()
            )
            if (tipBounds.intersect(bounds)) {
                tipLeft = tipBounds.left - bounds.left
                tipTop = tipBounds.top - bounds.top
                tip = createBitmap(tipBounds.width(), tipBounds.height())
                Canvas(tip!!).drawBitmap(target, -tipLeft.toFloat(), -tipTop.toFloat(), null)
                interiorCoverage?.let {
                    interiorTip =
                        createBitmap(tipBounds.width(), tipBounds.height(), Bitmap.Config.ALPHA_8)
                    Canvas(interiorTip!!).drawBitmap(
                        it,
                        -tipLeft.toFloat(),
                        -tipTop.toFloat(),
                        null
                    )
                }
            }
            draw(nextSegments.lastIndex)
        }
        return target
    }

    fun clear() {
        bitmap?.recycle()
        tip?.recycle()
        interiorCoverage?.recycle()
        interiorTip?.recycle()
        interiorCoverage = null
        interiorTip = null
        bitmap = null
        tip = null
        key = null
        segments = emptyList()
    }
}

private data class ColourKey(
    val palette: GradientPalette,
    val cycleLength: Float,
    val measurementLength: Float,
    val width: Float,
    val interiorWidth: Float,
    val bounds: Rect,
    val isGradientMirrored: Boolean,
    val strokeCap: Paint.Cap,
    val strokeJoin: Paint.Join,
    val strokeMiter: Float
)

private data class ColourSegment(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val distance: Float,
    val distanceScale: Float,
    val tangentX: Float,
    val tangentY: Float
)

private fun Path.colourSegments(
    cycleLength: Float,
    measurementLength: Float,
    width: Float,
    sampleStep: Float
): List<ColourSegment> {
    val scale = measurementLength / MEASURE_CYCLE_LENGTH
    val measuredPath = Path(this).apply {
        transform(Matrix().apply { setScale(1f / scale, 1f / scale) })
    }
    val measure = PathMeasure(measuredPath, false)
    val position = FloatArray(2)
    val headingSpan = maxOf(COLOUR_SAMPLE_STEP, width / (2f * scale))
    return buildList {
        do {
            val length = measure.length
            if (length <= 0f || !measure.getPosTan(0f, position, null)) continue
            val distanceScale = if (measure.isClosed) {
                (length * scale / cycleLength).roundToInt().coerceAtLeast(1) *
                        cycleLength / (length * scale)
            } else 1f
            var previousX = position[0] * scale
            var previousY = position[1] * scale
            var distance = 0f
            var heading: Float? = null
            fun addSegment(start: Float, end: Float, x: Float, y: Float) {
                val dx = x - previousX
                val dy = y - previousY
                val middle = (start + end) / 2f
                if (middle > start && middle < end) {
                    measure.getPosTan(middle, position, null)
                    val midX = position[0] * scale
                    val midY = position[1] * scale
                    val squaredLength = dx * dx + dy * dy
                    val projection = if (squaredLength > 0f) {
                        ((midX - previousX) * dx + (midY - previousY) * dy) / squaredLength
                    } else 0f
                    val errorX = midX - previousX - dx * projection.coerceIn(0f, 1f)
                    val errorY = midY - previousY - dy * projection.coerceIn(0f, 1f)
                    if (errorX * errorX + errorY * errorY > 0.25f) {
                        addSegment(start, middle, midX, midY)
                        addSegment(middle, end, x, y)
                        return
                    }
                }
                if (dx != 0f || dy != 0f) {
                    val direction = atan2(dy, dx)
                    val previousHeading = heading ?: direction
                    val turn =
                        atan2(sin(direction - previousHeading), cos(direction - previousHeading))
                    heading = if (measure.isClosed) direction else {
                        previousHeading + turn * (1f - exp(-(end - start) / headingSpan))
                    }
                    add(
                        ColourSegment(
                            previousX, previousY, x, y, start * scale, distanceScale,
                            cos(heading), sin(heading)
                        )
                    )
                }
                previousX = x
                previousY = y
            }
            while (distance < length) {
                val end = min(distance + sampleStep, length)
                measure.getPosTan(end, position, null)
                addSegment(distance, end, position[0] * scale, position[1] * scale)
                distance = end
            }
        } while (measure.nextContour())
    }
}

private fun Path.createGradient(
    palette: GradientPalette,
    gradientLength: Float,
    isGradientMirrored: Boolean
): LinearGradient {
    val bounds = RectF().also { computeBounds(it, true) }
    var endX = bounds.right
    val endY = bounds.bottom
    if (bounds.left == endX && bounds.top == endY) {
        endX += 1f
    }

    val length = gradientLength.normalizedGradientLength()
    return LinearGradient(
        bounds.left,
        bounds.top,
        bounds.left + (endX - bounds.left) * length,
        bounds.top + (endY - bounds.top) * length,
        if (isGradientMirrored) palette.mirroredColours()
        else palette.colors.map { it.colorInt }.toIntArray(),
        null,
        when {
            isGradientMirrored -> Shader.TileMode.MIRROR
            length < 1f -> Shader.TileMode.REPEAT
            else -> Shader.TileMode.CLAMP
        }
    )
}

private fun GradientPalette.mirroredColours(): IntArray = MIRRORED_COLOURS[this]

private val MIRRORED_COLOURS = object : LruCache<GradientPalette, IntArray>(32) {
    override fun create(palette: GradientPalette): IntArray {
        val stops = palette.colors.map { it.colorInt }
        val samplesPerStop = 8
        return IntArray(stops.lastIndex * samplesPerStop + 1) { index ->
            val left = (index / samplesPerStop).coerceAtMost(stops.lastIndex - 1)
            val right = left + 1
            val t = (index - left * samplesPerStop).toFloat() / samplesPerStop
            fun channel(shift: Int): Int {
                fun value(stop: Int): Float = (stops[stop] ushr shift and 255).toFloat()
                fun slope(before: Float, after: Float): Float =
                    if (before * after > 0f) 2f * before * after / (before + after) else 0f

                val start = value(left)
                val end = value(right)
                val delta = end - start
                val startSlope = if (left == 0) 0f else slope(start - value(left - 1), delta)
                val endSlope = if (right == stops.lastIndex) 0f
                else slope(delta, value(right + 1) - end)
                return (start + t * (startSlope + t * (3f * delta - 2f * startSlope - endSlope +
                        t * (-2f * delta + startSlope + endSlope))))
                    .roundToInt().coerceIn(0, 255)
            }
            (channel(24) shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
        }
    }
}

private fun IntegerSize.gradientCycleLength(): Float = (
        min(width, height).coerceAtLeast(1) * GRADIENT_CYCLE_SIZE_FRACTION
        ).coerceAtLeast(MIN_CYCLE_LENGTH)

private fun Float.normalizedGradientLength(): Float =
    if (isFinite()) coerceIn(0.1f, 4f) else 1f

private const val GRADIENT_CYCLE_SIZE_FRACTION = 1f
private const val MIN_CYCLE_LENGTH = 1f
private const val MEASURE_CYCLE_LENGTH = 512f
private const val COLOUR_SAMPLE_STEP = 4f
