/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.draw.presentation.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.ui.utils.helper.rotate
import ru.tech.imageresizershrinker.feature.draw.domain.DrawPathMode
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

data class PathHelper(
    val drawDownPosition: Offset,
    val currentDrawPosition: Offset,
    val onPathChange: (Path) -> Unit,
    val strokeWidth: Pt,
    val canvasSize: IntegerSize,
    val drawPathMode: DrawPathMode,
    val isEraserOn: Boolean,
) {

    private val drawArrowsScope by lazy {
        object : DrawArrowsScope {
            override fun drawArrowsIfNeeded(
                drawPath: Path,
            ) {
                fun drawStartEndArrows(
                    sizeScale: Float = 3f,
                    angle: Float = 150f
                ) {
                    drawEndArrow(
                        drawPath = drawPath,
                        strokeWidth = strokeWidth,
                        canvasSize = canvasSize,
                        arrowSize = sizeScale,
                        arrowAngle = angle.toDouble()
                    )

                    drawStartArrow(
                        drawPath = drawPath,
                        strokeWidth = strokeWidth,
                        canvasSize = canvasSize,
                        arrowSize = sizeScale,
                        arrowAngle = angle.toDouble()
                    )
                }


                when (drawPathMode) {
                    is DrawPathMode.DoublePointingArrow -> {
                        drawStartEndArrows(
                            sizeScale = drawPathMode.sizeScale,
                            angle = drawPathMode.angle
                        )
                    }

                    is DrawPathMode.DoubleLinePointingArrow -> {
                        drawStartEndArrows(
                            sizeScale = drawPathMode.sizeScale,
                            angle = drawPathMode.angle
                        )
                    }

                    is DrawPathMode.PointingArrow -> {
                        drawEndArrow(
                            drawPath = drawPath,
                            strokeWidth = strokeWidth,
                            canvasSize = canvasSize,
                            arrowSize = drawPathMode.sizeScale,
                            arrowAngle = drawPathMode.angle.toDouble()
                        )
                    }

                    is DrawPathMode.LinePointingArrow -> {
                        drawEndArrow(
                            drawPath = drawPath,
                            strokeWidth = strokeWidth,
                            canvasSize = canvasSize,
                            arrowSize = drawPathMode.sizeScale,
                            arrowAngle = drawPathMode.angle.toDouble()
                        )
                    }

                    else -> Unit
                }
            }

            private fun drawEndArrow(
                drawPath: Path,
                strokeWidth: Pt,
                canvasSize: IntegerSize,
                arrowSize: Float = 3f,
                arrowAngle: Double = 150.0
            ) {
                val (preLastPoint, lastPoint) = PathMeasure().apply {
                    setPath(drawPath, false)
                }.let {
                    Pair(
                        it.getPosition(it.length - strokeWidth.toPx(canvasSize) * arrowSize)
                            .takeOrElse { Offset.Zero },
                        it.getPosition(it.length).takeOrElse { Offset.Zero }
                    )
                }

                val arrowVector = lastPoint - preLastPoint

                fun drawArrow() {
                    val (rx1, ry1) = arrowVector.rotate(arrowAngle)
                    val (rx2, ry2) = arrowVector.rotate(360 - arrowAngle)

                    drawPath.apply {
                        relativeLineTo(rx1, ry1)
                        moveTo(lastPoint.x, lastPoint.y)
                        relativeLineTo(rx2, ry2)
                    }
                }

                if (abs(arrowVector.x) < arrowSize * strokeWidth.toPx(canvasSize) &&
                    abs(arrowVector.y) < arrowSize * strokeWidth.toPx(canvasSize) &&
                    preLastPoint != Offset.Zero
                ) {
                    drawArrow()
                }
            }

            private fun drawStartArrow(
                drawPath: Path,
                strokeWidth: Pt,
                canvasSize: IntegerSize,
                arrowSize: Float = 3f,
                arrowAngle: Double = 150.0
            ) {
                val (firstPoint, secondPoint) = PathMeasure().apply {
                    setPath(drawPath, false)
                }.let {
                    Pair(
                        it.getPosition(0f).takeOrElse { Offset.Zero },
                        it.getPosition(strokeWidth.toPx(canvasSize) * arrowSize)
                            .takeOrElse { Offset.Zero }
                    )
                }

                val arrowVector = firstPoint - secondPoint

                fun drawArrow() {
                    val (rx1, ry1) = arrowVector.rotate(arrowAngle)
                    val (rx2, ry2) = arrowVector.rotate(360 - arrowAngle)

                    drawPath.apply {
                        moveTo(firstPoint.x, firstPoint.y)
                        relativeLineTo(rx1, ry1)
                        moveTo(firstPoint.x, firstPoint.y)
                        relativeLineTo(rx2, ry2)
                    }
                }

                if (abs(arrowVector.x) < arrowSize * strokeWidth.toPx(canvasSize) &&
                    abs(arrowVector.y) < arrowSize * strokeWidth.toPx(canvasSize) &&
                    secondPoint != Offset.Zero
                ) {
                    drawArrow()
                }
            }
        }
    }

    fun drawPolygon(
        vertices: Int,
        rotationDegrees: Int,
        isRegular: Boolean,
    ) {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val top = max(drawDownPosition.y, currentDrawPosition.y)
            val left = min(drawDownPosition.x, currentDrawPosition.x)
            val bottom = min(drawDownPosition.y, currentDrawPosition.y)
            val right = max(drawDownPosition.x, currentDrawPosition.x)

            val width = right - left
            val height = bottom - top
            val centerX = (left + right) / 2f
            val centerY = (top + bottom) / 2f
            val radius = min(width, height) / 2f

            val newPath = Path().apply {
                if (isRegular) {
                    val angleStep = 360f / vertices
                    val startAngle = rotationDegrees - 270.0
                    moveTo(
                        centerX + radius * cos(Math.toRadians(startAngle)).toFloat(),
                        centerY + radius * sin(Math.toRadians(startAngle)).toFloat()
                    )
                    for (i in 1 until vertices) {
                        val angle = startAngle + i * angleStep
                        lineTo(
                            centerX + radius * cos(Math.toRadians(angle)).toFloat(),
                            centerY + radius * sin(Math.toRadians(angle)).toFloat()
                        )
                    }
                } else {
                    for (i in 0 until vertices) {
                        val angle = i * (360f / vertices) + rotationDegrees - 270.0
                        val x =
                            centerX + width / 2f * cos(Math.toRadians(angle.toDouble())).toFloat()
                        val y =
                            centerY + height / 2f * sin(Math.toRadians(angle.toDouble())).toFloat()
                        if (i == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                }
                close()
            }
            onPathChange(newPath)
        }
    }

    fun drawStar(
        vertices: Int,
        innerRadiusRatio: Float,
        rotationDegrees: Int,
        isRegular: Boolean,
    ) {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val top = max(drawDownPosition.y, currentDrawPosition.y)
            val left = min(drawDownPosition.x, currentDrawPosition.x)
            val bottom = min(drawDownPosition.y, currentDrawPosition.y)
            val right = max(drawDownPosition.x, currentDrawPosition.x)

            val centerX = (left + right) / 2f
            val centerY = (top + bottom) / 2f
            val width = right - left
            val height = bottom - top

            val newPath = Path().apply {
                if (isRegular) {
                    val outerRadius = min(width, height) / 2f
                    val innerRadius = outerRadius * innerRadiusRatio

                    val angleStep = 360f / (2 * vertices)
                    val startAngle = rotationDegrees - 270.0

                    for (i in 0 until (2 * vertices)) {
                        val radius = if (i % 2 == 0) outerRadius else innerRadius
                        val angle = startAngle + i * angleStep
                        val x = centerX + radius * cos(Math.toRadians(angle)).toFloat()
                        val y = centerY + radius * sin(Math.toRadians(angle)).toFloat()
                        if (i == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                } else {
                    for (i in 0 until (2 * vertices)) {
                        val angle = i * (360f / (2 * vertices)) + rotationDegrees - 270.0
                        val radiusX =
                            (if (i % 2 == 0) width else width * innerRadiusRatio) / 2f
                        val radiusY =
                            (if (i % 2 == 0) height else height * innerRadiusRatio) / 2f

                        val x = centerX + radiusX * cos(Math.toRadians(angle)).toFloat()
                        val y = centerY + radiusY * sin(Math.toRadians(angle)).toFloat()
                        if (i == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                }
                close()
            }

            onPathChange(newPath)
        }
    }

    fun drawTriangle() {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val newPath = Path().apply {
                moveTo(drawDownPosition.x, drawDownPosition.y)

                lineTo(currentDrawPosition.x, drawDownPosition.y)
                lineTo(
                    (drawDownPosition.x + currentDrawPosition.x) / 2,
                    currentDrawPosition.y
                )
                lineTo(drawDownPosition.x, drawDownPosition.y)
                close()
            }
            onPathChange(newPath)
        }
    }

    fun drawRect(
        rotationDegrees: Int
    ) {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val top = max(drawDownPosition.y, currentDrawPosition.y)
            val left = min(drawDownPosition.x, currentDrawPosition.x)
            val bottom = min(drawDownPosition.y, currentDrawPosition.y)
            val right = max(drawDownPosition.x, currentDrawPosition.x)

            val centerX = (left + right) / 2
            val centerY = (top + bottom) / 2

            val radians = Math.toRadians(rotationDegrees.toDouble())

            val corners = listOf(
                Offset(left, top),
                Offset(right, top),
                Offset(right, bottom),
                Offset(left, bottom)
            )

            val rotatedCorners = corners.map { corner ->
                val translatedX = corner.x - centerX
                val translatedY = corner.y - centerY
                val rotatedX = translatedX * cos(radians) - translatedY * sin(radians)
                val rotatedY = translatedX * sin(radians) + translatedY * cos(radians)
                Offset(rotatedX.toFloat() + centerX, rotatedY.toFloat() + centerY)
            }

            val newPath = Path().apply {
                moveTo(rotatedCorners[0].x, rotatedCorners[0].y)
                lineTo(rotatedCorners[1].x, rotatedCorners[1].y)
                lineTo(rotatedCorners[2].x, rotatedCorners[2].y)
                lineTo(rotatedCorners[3].x, rotatedCorners[3].y)
                close()
            }
            onPathChange(newPath)
        }
    }

    fun drawOval() {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val newPath = Path().apply {
                addOval(
                    Rect(
                        top = max(
                            drawDownPosition.y,
                            currentDrawPosition.y
                        ),
                        left = min(
                            drawDownPosition.x,
                            currentDrawPosition.x
                        ),
                        bottom = min(
                            drawDownPosition.y,
                            currentDrawPosition.y
                        ),
                        right = max(
                            drawDownPosition.x,
                            currentDrawPosition.x
                        ),
                    )
                )
            }
            onPathChange(newPath)
        }
    }

    fun drawLine() {
        if (drawDownPosition.isSpecified && currentDrawPosition.isSpecified) {
            val newPath = Path().apply {
                moveTo(drawDownPosition.x, drawDownPosition.y)
                lineTo(currentDrawPosition.x, currentDrawPosition.y)
            }
            drawArrowsScope.drawArrowsIfNeeded(newPath)

            onPathChange(newPath)
        }
    }

    fun drawPath(
        onDrawFreeArrow: DrawArrowsScope.() -> Unit,
        onBaseDraw: () -> Unit,
    ) = if (!isEraserOn) {
        when (drawPathMode) {
            is DrawPathMode.PointingArrow,
            is DrawPathMode.DoublePointingArrow -> onDrawFreeArrow(drawArrowsScope)

            is DrawPathMode.DoubleLinePointingArrow,
            DrawPathMode.Line,
            is DrawPathMode.LinePointingArrow -> drawLine()

            is DrawPathMode.Rect -> drawRect(drawPathMode.rotationDegrees)

            is DrawPathMode.OutlinedRect -> drawRect(drawPathMode.rotationDegrees)

            DrawPathMode.Triangle,
            DrawPathMode.OutlinedTriangle -> drawTriangle()

            is DrawPathMode.Polygon -> {
                drawPolygon(
                    vertices = drawPathMode.vertices,
                    rotationDegrees = drawPathMode.rotationDegrees,
                    isRegular = drawPathMode.isRegular
                )
            }

            is DrawPathMode.OutlinedPolygon -> {
                drawPolygon(
                    vertices = drawPathMode.vertices,
                    rotationDegrees = drawPathMode.rotationDegrees,
                    isRegular = drawPathMode.isRegular
                )
            }

            is DrawPathMode.Star -> {
                drawStar(
                    vertices = drawPathMode.vertices,
                    innerRadiusRatio = drawPathMode.innerRadiusRatio,
                    rotationDegrees = drawPathMode.rotationDegrees,
                    isRegular = drawPathMode.isRegular
                )
            }

            is DrawPathMode.OutlinedStar -> {
                drawStar(
                    vertices = drawPathMode.vertices,
                    innerRadiusRatio = drawPathMode.innerRadiusRatio,
                    rotationDegrees = drawPathMode.rotationDegrees,
                    isRegular = drawPathMode.isRegular
                )
            }

            DrawPathMode.Oval,
            DrawPathMode.OutlinedOval -> drawOval()

            DrawPathMode.Free,
            DrawPathMode.Lasso -> onBaseDraw()
        }
    } else onBaseDraw()
}

interface DrawArrowsScope {
    fun drawArrowsIfNeeded(
        drawPath: Path,
    )
}

@Composable
fun rememberPathHelper(
    drawDownPosition: Offset,
    currentDrawPosition: Offset,
    onPathChange: (Path) -> Unit,
    strokeWidth: Pt,
    canvasSize: IntegerSize,
    drawPathMode: DrawPathMode,
    isEraserOn: Boolean,
): State<PathHelper> = remember(
    drawDownPosition,
    currentDrawPosition,
    onPathChange,
    strokeWidth,
    canvasSize,
    drawPathMode,
    isEraserOn
) {
    derivedStateOf {
        PathHelper(
            drawDownPosition = drawDownPosition,
            currentDrawPosition = currentDrawPosition,
            onPathChange = onPathChange,
            strokeWidth = strokeWidth,
            canvasSize = canvasSize,
            drawPathMode = drawPathMode,
            isEraserOn = isEraserOn
        )
    }
}