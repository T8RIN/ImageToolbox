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

import android.graphics.Matrix
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.ui.utils.helper.rotate
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class PathHelper(
    val drawDownPosition: Offset,
    val currentDrawPosition: Offset,
    val onPathChange: (Path) -> Unit,
    val strokeWidth: Pt,
    val canvasSize: IntegerSize,
    val drawPathMode: DrawPathMode,
    val isEraserOn: Boolean,
    val drawMode: DrawMode
) {
    private val strokeWidthSized = strokeWidth.toPx(canvasSize)

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
                        arrowSize = sizeScale,
                        arrowAngle = angle.toDouble()
                    )

                    drawStartArrow(
                        drawPath = drawPath,
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
                            arrowSize = drawPathMode.sizeScale,
                            arrowAngle = drawPathMode.angle.toDouble()
                        )
                    }

                    is DrawPathMode.LinePointingArrow -> {
                        drawEndArrow(
                            drawPath = drawPath,
                            arrowSize = drawPathMode.sizeScale,
                            arrowAngle = drawPathMode.angle.toDouble()
                        )
                    }

                    else -> Unit
                }
            }

            private fun drawEndArrow(
                drawPath: Path,
                arrowSize: Float = 3f,
                arrowAngle: Double = 150.0
            ) {
                val (preLastPoint, lastPoint) = PathMeasure().apply {
                    setPath(drawPath, false)
                }.let {
                    Pair(
                        it.getPosition(it.length - strokeWidthSized * arrowSize)
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

                if (abs(arrowVector.x) < arrowSize * strokeWidthSized &&
                    abs(arrowVector.y) < arrowSize * strokeWidthSized &&
                    preLastPoint != Offset.Zero
                ) {
                    drawArrow()
                }
            }

            private fun drawStartArrow(
                drawPath: Path,
                arrowSize: Float = 3f,
                arrowAngle: Double = 150.0
            ) {
                val (firstPoint, secondPoint) = PathMeasure().apply {
                    setPath(drawPath, false)
                }.let {
                    Pair(
                        it.getPosition(0f).takeOrElse { Offset.Zero },
                        it.getPosition(strokeWidthSized * arrowSize)
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

                if (abs(arrowVector.x) < arrowSize * strokeWidthSized &&
                    abs(arrowVector.y) < arrowSize * strokeWidthSized &&
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
                            centerX + width / 2f * cos(Math.toRadians(angle)).toFloat()
                        val y =
                            centerY + height / 2f * sin(Math.toRadians(angle)).toFloat()
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
        rotationDegrees: Int,
        cornerRadius: Float
    ) {
        if (!drawDownPosition.isSpecified || !currentDrawPosition.isSpecified) return

        val left = min(drawDownPosition.x, currentDrawPosition.x)
        val right = max(drawDownPosition.x, currentDrawPosition.x)
        val top = min(drawDownPosition.y, currentDrawPosition.y)
        val bottom = max(drawDownPosition.y, currentDrawPosition.y)

        val width = right - left
        val height = bottom - top
        if (width <= 0f || height <= 0f) return

        val radius = min(width, height) * cornerRadius.coerceIn(0f, 0.5f)
        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(left, top, right, bottom),
                    radius, radius
                )
            )
        }

        val matrix = Matrix().apply {
            setRotate(rotationDegrees.toFloat(), centerX, centerY)
        }


        onPathChange(
            path.asAndroidPath().apply { transform(matrix) }.asComposePath()
        )
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
        currentDrawPath: Path? = null,
        onDrawFreeArrow: DrawArrowsScope.() -> Unit = {},
        onBaseDraw: () -> Unit = {},
        onFloodFill: (tolerance: Float) -> Unit = {}
    ) {
        if (!isEraserOn && drawMode !is DrawMode.Warp) {
            when (drawPathMode) {
                is DrawPathMode.PointingArrow,
                is DrawPathMode.DoublePointingArrow -> onDrawFreeArrow(drawArrowsScope)

                is DrawPathMode.DoubleLinePointingArrow,
                DrawPathMode.Line,
                is DrawPathMode.LinePointingArrow -> drawLine()

                is DrawPathMode.Rect -> drawRect(
                    rotationDegrees = drawPathMode.rotationDegrees,
                    cornerRadius = drawPathMode.cornerRadius
                )

                is DrawPathMode.OutlinedRect -> drawRect(
                    rotationDegrees = drawPathMode.rotationDegrees,
                    cornerRadius = drawPathMode.cornerRadius
                )

                is DrawPathMode.Triangle,
                is DrawPathMode.OutlinedTriangle -> drawTriangle()

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

                is DrawPathMode.Oval,
                is DrawPathMode.OutlinedOval -> drawOval()

                is DrawPathMode.Free,
                is DrawPathMode.Lasso -> onBaseDraw()

                is DrawPathMode.FloodFill -> onFloodFill(drawPathMode.tolerance)

                is DrawPathMode.Spray -> {
                    currentDrawPath?.let {
                        val path = currentDrawPath.copy().apply {
                            repeat(drawPathMode.density) {
                                val angle = Random.nextFloat() * PI_2
                                val radius = sqrt(Random.nextFloat()) * strokeWidthSized
                                val x = currentDrawPosition.x + radius * cos(angle)
                                val y = currentDrawPosition.y + radius * sin(angle)

                                val rect = Rect(
                                    left = x,
                                    top = y,
                                    right = x + drawPathMode.pixelSize,
                                    bottom = y + drawPathMode.pixelSize
                                )

                                if (drawPathMode.isSquareShaped) addRect(rect) else addOval(rect)
                            }
                        }

                        onPathChange(path)
                    }
                }
            }
        } else onBaseDraw()
    }
}

private const val PI_2 = (Math.PI * 2).toFloat()

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
    drawMode: DrawMode
): State<PathHelper> = remember(
    drawDownPosition,
    currentDrawPosition,
    onPathChange,
    strokeWidth,
    canvasSize,
    drawPathMode,
    isEraserOn,
    drawMode
) {
    derivedStateOf {
        PathHelper(
            drawDownPosition = drawDownPosition,
            currentDrawPosition = currentDrawPosition,
            onPathChange = onPathChange,
            strokeWidth = strokeWidth,
            canvasSize = canvasSize,
            drawPathMode = drawPathMode,
            isEraserOn = isEraserOn,
            drawMode = drawMode
        )
    }
}