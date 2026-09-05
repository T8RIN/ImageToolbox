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
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import com.awxkee.aire.Aire
import com.awxkee.aire.EdgeMode
import com.awxkee.aire.GaussianPreciseLevel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun Canvas.drawPathWithGradient(
    path: Path,
    paint: Paint,
    palette: GradientPalette?,
    isFilled: Boolean,
    canvasSize: IntegerSize,
    softnessRadius: Float = 0f,
    cache: GradientStrokeCache? = null
) {
    if (palette == null) {
        drawPath(path, paint)
    } else if (isFilled) {
        drawPath(path, paint.withPathGradient(path, palette))
    } else {
        drawGradientStroke(
            path = path,
            paint = paint,
            palette = palette,
            canvasSize = canvasSize,
            softnessRadius = softnessRadius,
            cache = cache
        )
    }
}

internal fun Paint.withPathGradient(
    path: Path,
    palette: GradientPalette
): Paint = Paint(this).apply {
    shader = path.createGradient(palette)
}

/**
 * Colour and coverage are independent: overlapping colour segments must never accumulate
 * opacity or blur. One native stroke outline supplies the antialiased caps and joins.
 */
private fun Canvas.drawGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    canvasSize: IntegerSize,
    softnessRadius: Float,
    cache: GradientStrokeCache?
) {
    if (path.isEmpty || paint.alpha == 0) return

    if (softnessRadius > 0f) {
        drawSoftGradientStroke(path, paint, palette, canvasSize, softnessRadius, cache)
        return
    }

    val cycleLength = canvasSize.gradientCycleLength()
    val outline = Path()
    val outlinePaint = Paint(paint).apply { maskFilter = null }
    if (!outlinePaint.getFillPath(path, outline)) {
        drawPath(path, paint.withPathGradient(path, palette))
        return
    }
    val bounds = if (cache != null) RectF(clipBounds) else {
        RectF().also { outline.computeBounds(it, true) }.apply { inset(-2f, -2f) }
    }
    if (!bounds.intersect(RectF(clipBounds))) return
    val pixels = Rect().also { bounds.roundOut(it) }
    val joinReach = when {
        paint.strokeJoin == Paint.Join.MITER -> paint.strokeMiter.coerceAtLeast(1.5f)
        paint.strokeCap == Paint.Cap.SQUARE -> 1.5f
        else -> 1f
    }
    // The colour strokes extend one pixel beyond the silhouette. Their antialiasing
    // blends self-crossings, while the native outline applies the outer coverage once.
    val colourWidth = paint.strokeWidth * joinReach + 2f
    val renderer = cache ?: GradientStrokeCache()
    try {
        val bitmap = renderer.render(path, palette, cycleLength, colourWidth, pixels)
        val colourShader =
            BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
                setLocalMatrix(Matrix().apply {
                    setTranslate(pixels.left.toFloat(), pixels.top.toFloat())
                })
            }
        // Filling the stroked outline applies coverage once, including overlapping joins.
        // The colour texture is opaque; only this full-resolution outline controls alpha.
        drawPath(outline, Paint(paint).apply {
            style = Paint.Style.FILL
            pathEffect = null
            shader = colourShader
            isFilterBitmap = true
            maskFilter = null
            clearShadowLayer()
        })
    } finally {
        if (cache == null) renderer.clear()
    }
}

/** Blur the premultiplied stroke once, so colour crossings feather with the silhouette. */
private fun Canvas.drawSoftGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    canvasSize: IntegerSize,
    radius: Float,
    cache: GradientStrokeCache?
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

    // Feathering hides subpixel detail. Keep at least two pixels per sigma and anchor
    // the sampling grid to the canvas, so growing the path never shifts the texture.
    val scale = (256f / maxOf(canvasSize.width, canvasSize.height))
        .coerceIn(min(1f, 2f / sigma), 1f)
    val left = floor(bounds.left * scale) / scale
    val top = floor(bounds.top * scale) / scale
    val bitmap = Bitmap.createBitmap(
        ceil((bounds.right - left) * scale).toInt().coerceAtLeast(1),
        ceil((bounds.bottom - top) * scale).toInt().coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
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
            }, palette, canvasSize, 0f, cache
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

/**
 * One colour surface for the current stroke. Only complete, fixed-distance segments
 * are retained. Restore the unfinished tip before extending it, so it cannot leave
 * ghosts, change earlier colours, or accumulate antialiasing between input events.
 */
internal class GradientStrokeCache {
    private var bitmap: Bitmap? = null
    private var tip: Bitmap? = null
    private var tipLeft = 0
    private var tipTop = 0
    private var key: ColourKey? = null
    private var segments: List<ColourSegment> = emptyList()

    internal fun render(
        path: Path,
        palette: GradientPalette,
        cycleLength: Float,
        width: Float,
        bounds: Rect
    ): Bitmap {
        val nextKey = ColourKey(palette, cycleLength, width, Rect(bounds))
        val nextSegments = path.colourSegments(cycleLength)
        val completeCount = (nextSegments.size - 1).coerceAtLeast(0)
        val canAppend = key == nextKey && segments.size <= completeCount &&
                segments.indices.all { segments[it] == nextSegments[it] }
        val colours = palette.colors.map { it.colorInt }.let {
            if (it.first() == it.last()) it else it + it.first()
        }.toIntArray()
        if (!canAppend) {
            clear()
            bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
            bitmap!!.eraseColor(colours.first())
            key = nextKey
        }
        val target = bitmap!!
        val canvas = Canvas(target)
        tip?.let {
            canvas.drawBitmap(it, tipLeft.toFloat(), tipTop.toFloat(), null)
            it.recycle()
            tip = null
        }
        canvas.translate(-bounds.left.toFloat(), -bounds.top.toFloat())
        val gradient =
            LinearGradient(0f, 0f, cycleLength, 0f, colours, null, Shader.TileMode.REPEAT)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = width
            strokeCap = Paint.Cap.ROUND
            shader = gradient
        }
        val matrix = Matrix()
        val values = FloatArray(9).apply { this[8] = 1f }
        fun draw(segment: ColourSegment) {
            val dx = segment.endX - segment.startX
            val dy = segment.endY - segment.startY
            val length = sqrt(dx * dx + dy * dy)
            if (length == 0f) return
            val tx = dx / length
            val ty = dy / length
            values[0] = tx / segment.distanceScale
            values[1] = -ty
            values[2] = segment.startX - tx * segment.distance
            values[3] = ty / segment.distanceScale
            values[4] = tx
            values[5] = segment.startY - ty * segment.distance
            matrix.setValues(values)
            gradient.setLocalMatrix(matrix)
            canvas.drawLine(segment.startX, segment.startY, segment.endX, segment.endY, paint)
        }
        for (index in segments.size until completeCount) draw(nextSegments[index])
        segments = nextSegments.subList(0, completeCount)
        nextSegments.lastOrNull()?.let { last ->
            val radius = width / 2f + 1f
            val tipBounds = Rect(
                floor(min(last.startX, last.endX) - radius).toInt(),
                floor(min(last.startY, last.endY) - radius).toInt(),
                ceil(maxOf(last.startX, last.endX) + radius).toInt(),
                ceil(maxOf(last.startY, last.endY) + radius).toInt()
            )
            if (tipBounds.intersect(bounds)) {
                tipLeft = tipBounds.left - bounds.left
                tipTop = tipBounds.top - bounds.top
                // createBitmap may return its input for a full-size subset; always copy.
                tip = Bitmap.createBitmap(
                    tipBounds.width(),
                    tipBounds.height(),
                    Bitmap.Config.ARGB_8888
                )
                Canvas(tip!!).drawBitmap(target, -tipLeft.toFloat(), -tipTop.toFloat(), null)
            }
            draw(last)
        }
        return target
    }

    fun clear() {
        bitmap?.recycle()
        tip?.recycle()
        bitmap = null
        tip = null
        key = null
        segments = emptyList()
    }
}

private data class ColourKey(
    val palette: GradientPalette,
    val cycleLength: Float,
    val width: Float,
    val bounds: Rect
)

private data class ColourSegment(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val distance: Float,
    val distanceScale: Float
)

private fun Path.colourSegments(cycleLength: Float): List<ColourSegment> {
    // PathMeasure's tolerance is in pixels. Normalize before measuring so the same
    // curve has the same arc lengths and colours in the preview and the saved image.
    val scale = cycleLength / MEASURE_CYCLE_LENGTH
    val measuredPath = Path(this).apply {
        transform(Matrix().apply { setScale(1f / scale, 1f / scale) })
    }
    val measure = PathMeasure(measuredPath, false)
    val position = FloatArray(2)
    return buildList {
        do {
            val length = measure.length
            if (length <= 0f || !measure.getPosTan(0f, position, null)) continue
            val distanceScale = if (measure.isClosed) {
                (length / MEASURE_CYCLE_LENGTH).roundToInt().coerceAtLeast(1) *
                        MEASURE_CYCLE_LENGTH / length
            } else 1f
            var previousX = position[0] * scale
            var previousY = position[1] * scale
            var distance = 0f
            while (distance < length) {
                val end = min(distance + COLOUR_SAMPLE_STEP, length)
                measure.getPosTan(end, position, null)
                val x = position[0] * scale
                val y = position[1] * scale
                add(ColourSegment(previousX, previousY, x, y, distance * scale, distanceScale))
                previousX = x
                previousY = y
                distance = end
            }
        } while (measure.nextContour())
    }
}

private fun Path.createGradient(palette: GradientPalette): LinearGradient {
    val bounds = RectF().also { computeBounds(it, true) }
    var endX = bounds.right
    var endY = bounds.bottom
    if (bounds.left == endX && bounds.top == endY) {
        endX += 1f
    }

    return LinearGradient(
        bounds.left,
        bounds.top,
        endX,
        endY,
        palette.colors.map { it.colorInt }.toIntArray(),
        null,
        Shader.TileMode.CLAMP
    )
}

private fun IntegerSize.gradientCycleLength(): Float = (
        min(width, height).coerceAtLeast(1) * GRADIENT_CYCLE_SIZE_FRACTION
        ).coerceAtLeast(MIN_CYCLE_LENGTH)

private const val GRADIENT_CYCLE_SIZE_FRACTION = 0.5f
private const val MIN_CYCLE_LENGTH = 1f
private const val MEASURE_CYCLE_LENGTH = 512f
private const val COLOUR_SAMPLE_STEP = 4f
