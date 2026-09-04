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

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

internal fun Canvas.drawPathWithGradient(
    path: Path,
    paint: Paint,
    palette: GradientPalette?,
    isFilled: Boolean,
    canvasSize: IntegerSize,
    softnessRadius: Float = 0f
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
            softnessRadius = softnessRadius
        )
    }
}

internal fun Paint.withPathGradient(
    path: Path,
    palette: GradientPalette
): Paint = Paint(this).apply {
    shader = path.createGradient(palette)
}

private fun Canvas.drawGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    canvasSize: IntegerSize,
    softnessRadius: Float
) {
    val cycleLength = canvasSize.gradientCycleLength()
    val saveCount = save()
    if (softnessRadius > 0f) {
        drawSoftGradientStroke(
            path = path,
            paint = paint,
            palette = palette,
            cycleLength = cycleLength
        )
    } else {
        val strokeOutline = Path()
        if (!paint.getFillPath(path, strokeOutline)) {
            restoreToCount(saveCount)
            drawPath(path, paint)
            return
        }

        val gradientPaint = Paint(paint).apply {
            pathEffect = null
            shader = null
            style = Paint.Style.FILL
            color = Color.White.toArgb()
            maskFilter = null
        }
        clipPath(strokeOutline)
        drawGradientMesh(
            path = path,
            paint = gradientPaint,
            palette = palette,
            cycleLength = cycleLength,
            halfWidth = paint.strokeWidth / 2f + MESH_OVERDRAW
        )
    }

    restoreToCount(saveCount)
}

private fun Canvas.drawSoftGradientStroke(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    cycleLength: Float
) {
    val segmentPaint = Paint(paint).apply {
        pathEffect = null
        strokeCap = Paint.Cap.BUTT
        strokeJoin = Paint.Join.ROUND
    }
    val capPaint = Paint(paint).apply {
        pathEffect = null
        shader = null
        style = Paint.Style.FILL
    }
    val measure = PathMeasure(path, false)
    val segmentPath = Path()
    val startPosition = FloatArray(2)
    val endPosition = FloatArray(2)
    var passedDistance = 0f

    do {
        val contourLength = measure.length
        var startDistance = 0f
        while (startDistance < contourLength) {
            val endDistance = min(startDistance + SOFT_SEGMENT_LENGTH, contourLength)
            val drawStart = (startDistance - SOFT_SEGMENT_OVERLAP).coerceAtLeast(0f)
            val drawEnd = (endDistance + SOFT_SEGMENT_OVERLAP).coerceAtMost(contourLength)

            segmentPath.rewind()
            if (
                measure.getSegment(drawStart, drawEnd, segmentPath, true) &&
                measure.getPosTan(drawStart, startPosition, null) &&
                measure.getPosTan(drawEnd, endPosition, null)
            ) {
                segmentPaint.shader = LinearGradient(
                    startPosition[0],
                    startPosition[1],
                    endPosition[0],
                    endPosition[1],
                    palette.colorAtDistance(passedDistance + drawStart, cycleLength),
                    palette.colorAtDistance(passedDistance + drawEnd, cycleLength),
                    Shader.TileMode.CLAMP
                )
                drawPath(segmentPath, segmentPaint)
            }
            startDistance = endDistance
        }

        if (!measure.isClosed && paint.strokeCap != Paint.Cap.SQUARE && contourLength > 0f) {
            if (measure.getPosTan(0f, startPosition, null)) {
                capPaint.color = palette.colorAtDistance(passedDistance, cycleLength)
                drawCircle(
                    startPosition[0],
                    startPosition[1],
                    paint.strokeWidth / 2f,
                    capPaint
                )
            }
            if (measure.getPosTan(contourLength, endPosition, null)) {
                capPaint.color = palette.colorAtDistance(
                    passedDistance + contourLength,
                    cycleLength
                )
                drawCircle(
                    endPosition[0],
                    endPosition[1],
                    paint.strokeWidth / 2f,
                    capPaint
                )
            }
        }

        passedDistance += contourLength
    } while (measure.nextContour())
}

private fun Canvas.drawGradientMesh(
    path: Path,
    paint: Paint,
    palette: GradientPalette,
    cycleLength: Float,
    halfWidth: Float
) {
    val measure = PathMeasure(path, false)
    val position = FloatArray(2)
    val tangent = FloatArray(2)
    var passedDistance = 0f

    do {
        val contourLength = measure.length
        if (contourLength > 0f) {
            val bodySampleCount = ceil(contourLength / MESH_SAMPLE_STEP)
                .toInt()
                .coerceAtLeast(1) + 1
            val hasRoundCaps = !measure.isClosed && paint.strokeCap != Paint.Cap.SQUARE
            val capSectionCount = if (hasRoundCaps) ROUND_CAP_SEGMENTS else 0
            val crossSectionCount = bodySampleCount + capSectionCount * 2
            val vertices = FloatArray(crossSectionCount * VALUES_PER_CROSS_SECTION)
            val colors = IntArray(crossSectionCount * VERTICES_PER_CROSS_SECTION)
            val capExtension = if (paint.strokeCap == Paint.Cap.SQUARE) {
                halfWidth
            } else {
                0f
            }

            fun setCrossSection(
                index: Int,
                centerX: Float,
                centerY: Float,
                tangentX: Float,
                tangentY: Float,
                sectionHalfWidth: Float,
                color: Int
            ) {
                val normalX = -tangentY * sectionHalfWidth
                val normalY = tangentX * sectionHalfWidth
                val vertexOffset = index * VALUES_PER_CROSS_SECTION
                vertices[vertexOffset] = centerX + normalX
                vertices[vertexOffset + 1] = centerY + normalY
                vertices[vertexOffset + 2] = centerX - normalX
                vertices[vertexOffset + 3] = centerY - normalY

                val colorOffset = index * VERTICES_PER_CROSS_SECTION
                colors[colorOffset] = color
                colors[colorOffset + 1] = color
            }

            if (hasRoundCaps && measure.getPosTan(0f, position, tangent)) {
                val color = palette.colorAtDistance(passedDistance, cycleLength)
                repeat(ROUND_CAP_SEGMENTS) { capIndex ->
                    val progress = capIndex.toFloat() / ROUND_CAP_SEGMENTS
                    val extension = -halfWidth * (1f - progress)
                    val capHalfWidth = sqrt(
                        (halfWidth * halfWidth - extension * extension).coerceAtLeast(0f)
                    )
                    setCrossSection(
                        index = capIndex,
                        centerX = position[0] + tangent[0] * extension,
                        centerY = position[1] + tangent[1] * extension,
                        tangentX = tangent[0],
                        tangentY = tangent[1],
                        sectionHalfWidth = capHalfWidth,
                        color = color
                    )
                }
            }

            repeat(bodySampleCount) { index ->
                val distance = min(
                    index * MESH_SAMPLE_STEP,
                    contourLength
                )
                if (!measure.getPosTan(distance, position, tangent)) return@repeat

                val extension = when (index) {
                    0 -> -capExtension
                    bodySampleCount - 1 -> capExtension
                    else -> 0f
                }
                val centerX = position[0] + tangent[0] * extension
                val centerY = position[1] + tangent[1] * extension
                val color = palette.colorAtDistance(
                    distance = passedDistance + distance,
                    cycleLength = cycleLength
                )
                setCrossSection(
                    index = capSectionCount + index,
                    centerX = centerX,
                    centerY = centerY,
                    tangentX = tangent[0],
                    tangentY = tangent[1],
                    sectionHalfWidth = halfWidth,
                    color = color
                )
            }

            if (hasRoundCaps && measure.getPosTan(contourLength, position, tangent)) {
                val color = palette.colorAtDistance(
                    passedDistance + contourLength,
                    cycleLength
                )
                repeat(ROUND_CAP_SEGMENTS) { capIndex ->
                    val progress = (capIndex + 1f) / ROUND_CAP_SEGMENTS
                    val extension = halfWidth * progress
                    val capHalfWidth = sqrt(
                        (halfWidth * halfWidth - extension * extension).coerceAtLeast(0f)
                    )
                    setCrossSection(
                        index = capSectionCount + bodySampleCount + capIndex,
                        centerX = position[0] + tangent[0] * extension,
                        centerY = position[1] + tangent[1] * extension,
                        tangentX = tangent[0],
                        tangentY = tangent[1],
                        sectionHalfWidth = capHalfWidth,
                        color = color
                    )
                }
            }

            drawVertices(
                Canvas.VertexMode.TRIANGLE_STRIP,
                vertices.size,
                vertices,
                0,
                null,
                0,
                colors,
                0,
                null,
                0,
                0,
                paint
            )
        }

        passedDistance += contourLength
    } while (measure.nextContour())
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

private fun GradientPalette.colorAtDistance(
    distance: Float,
    cycleLength: Float
): Int {
    val transitions = transitionCount()
    val wrappedDistance = ((distance % cycleLength) + cycleLength) % cycleLength
    val scaled = wrappedDistance / cycleLength * transitions
    val startIndex = floor(scaled).toInt().coerceAtMost(transitions - 1)
    val endIndex = if (startIndex + 1 < colors.size) startIndex + 1 else 0
    return lerp(
        start = Color(colors[startIndex].colorInt),
        stop = Color(colors[endIndex].colorInt),
        fraction = scaled - floor(scaled)
    ).toArgb()
}

private fun GradientPalette.transitionCount(): Int = if (
    colors.size > 1 && colors.first().colorInt == colors.last().colorInt
) {
    colors.lastIndex
} else {
    colors.size
}

private fun IntegerSize.gradientCycleLength(): Float = (
        min(width, height).coerceAtLeast(1) * GRADIENT_CYCLE_SIZE_FRACTION
        ).coerceAtLeast(MIN_CYCLE_LENGTH)

private const val GRADIENT_CYCLE_SIZE_FRACTION = 0.5f
private const val MIN_CYCLE_LENGTH = 1f
private const val MESH_SAMPLE_STEP = 4f
private const val MESH_OVERDRAW = 2f
private const val ROUND_CAP_SEGMENTS = 12
private const val SOFT_SEGMENT_LENGTH = 32f
private const val SOFT_SEGMENT_OVERLAP = 1f
private const val VERTICES_PER_CROSS_SECTION = 2
private const val VALUES_PER_CROSS_SECTION = VERTICES_PER_CROSS_SECTION * 2
