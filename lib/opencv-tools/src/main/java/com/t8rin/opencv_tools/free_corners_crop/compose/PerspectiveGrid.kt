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

package com.t8rin.opencv_tools.free_corners_crop.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs

internal fun DrawScope.drawPerspectiveGrid(
    points: List<Offset>,
    color: Color,
    strokeWidth: Float
) {
    if (points.size != QUAD_POINTS_COUNT) return

    val transform = PerspectiveQuadTransform.from(points)

    fun pointAt(horizontalFraction: Float, verticalFraction: Float): Offset {
        return transform
            ?.map(horizontalFraction, verticalFraction)
            ?: points.bilinearPoint(horizontalFraction, verticalFraction)
    }

    for (index in 1 until GRID_DIVISIONS) {
        val fraction = index.toFloat() / GRID_DIVISIONS

        drawLine(
            color = color,
            start = pointAt(fraction, 0f),
            end = pointAt(fraction, 1f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = pointAt(0f, fraction),
            end = pointAt(1f, fraction),
            strokeWidth = strokeWidth
        )
    }
}

private data class PerspectiveQuadTransform(
    val horizontalX: Float,
    val verticalX: Float,
    val originX: Float,
    val horizontalY: Float,
    val verticalY: Float,
    val originY: Float,
    val horizontalPerspective: Float,
    val verticalPerspective: Float,
    val fallbackPoints: List<Offset>
) {
    fun map(horizontalFraction: Float, verticalFraction: Float): Offset {
        val denominator = horizontalPerspective * horizontalFraction +
                verticalPerspective * verticalFraction + HOMOGENEOUS_ORIGIN
        if (abs(denominator) <= TRANSFORM_EPSILON) {
            return fallbackPoints.bilinearPoint(horizontalFraction, verticalFraction)
        }

        val point = Offset(
            x = (horizontalX * horizontalFraction + verticalX * verticalFraction + originX) /
                    denominator,
            y = (horizontalY * horizontalFraction + verticalY * verticalFraction + originY) /
                    denominator
        )
        return point.takeIf { it.x.isFinite() && it.y.isFinite() }
            ?: fallbackPoints.bilinearPoint(horizontalFraction, verticalFraction)
    }

    companion object {
        fun from(points: List<Offset>): PerspectiveQuadTransform? {
            val topLeft = points[TOP_LEFT_INDEX]
            val topRight = points[TOP_RIGHT_INDEX]
            val bottomRight = points[BOTTOM_RIGHT_INDEX]
            val bottomLeft = points[BOTTOM_LEFT_INDEX]

            val rightEdgeX = topRight.x - bottomRight.x
            val rightEdgeY = topRight.y - bottomRight.y
            val bottomEdgeX = bottomLeft.x - bottomRight.x
            val bottomEdgeY = bottomLeft.y - bottomRight.y
            val perspectiveX = topLeft.x - topRight.x + bottomRight.x - bottomLeft.x
            val perspectiveY = topLeft.y - topRight.y + bottomRight.y - bottomLeft.y
            val determinant = rightEdgeX * bottomEdgeY - bottomEdgeX * rightEdgeY

            if (abs(determinant) <= TRANSFORM_EPSILON) return null

            val horizontalPerspective =
                (perspectiveX * bottomEdgeY - bottomEdgeX * perspectiveY) / determinant
            val verticalPerspective =
                (rightEdgeX * perspectiveY - perspectiveX * rightEdgeY) / determinant
            val topRightDenominator = HOMOGENEOUS_ORIGIN + horizontalPerspective
            val bottomLeftDenominator = HOMOGENEOUS_ORIGIN + verticalPerspective
            val bottomRightDenominator =
                HOMOGENEOUS_ORIGIN + horizontalPerspective + verticalPerspective
            val minimumDenominator = minOf(
                topRightDenominator,
                bottomLeftDenominator,
                bottomRightDenominator
            )

            if (minimumDenominator <= TRANSFORM_EPSILON) return null

            return PerspectiveQuadTransform(
                horizontalX = topRight.x - topLeft.x + horizontalPerspective * topRight.x,
                verticalX = bottomLeft.x - topLeft.x + verticalPerspective * bottomLeft.x,
                originX = topLeft.x,
                horizontalY = topRight.y - topLeft.y + horizontalPerspective * topRight.y,
                verticalY = bottomLeft.y - topLeft.y + verticalPerspective * bottomLeft.y,
                originY = topLeft.y,
                horizontalPerspective = horizontalPerspective,
                verticalPerspective = verticalPerspective,
                fallbackPoints = points
            )
        }
    }
}

private fun List<Offset>.bilinearPoint(
    horizontalFraction: Float,
    verticalFraction: Float
): Offset {
    val top = this[TOP_LEFT_INDEX] +
            (this[TOP_RIGHT_INDEX] - this[TOP_LEFT_INDEX]) * horizontalFraction
    val bottom = this[BOTTOM_LEFT_INDEX] +
            (this[BOTTOM_RIGHT_INDEX] - this[BOTTOM_LEFT_INDEX]) * horizontalFraction
    return top + (bottom - top) * verticalFraction
}

private const val QUAD_POINTS_COUNT = 4
private const val GRID_DIVISIONS = 3
private const val TRANSFORM_EPSILON = 0.0001f
private const val HOMOGENEOUS_ORIGIN = 1f
private const val TOP_LEFT_INDEX = 0
private const val TOP_RIGHT_INDEX = 1
private const val BOTTOM_RIGHT_INDEX = 2
private const val BOTTOM_LEFT_INDEX = 3
