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

package com.t8rin.imagetoolbox.feature.gradient_maker.data

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.VertexMode
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.dispatchers.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientMaker
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientState
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.MeshGradientState
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidGradientMaker @Inject constructor(
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder,
    GradientMaker<Bitmap, ShaderBrush, Size, Color, TileMode, Offset> {

    override suspend fun createGradient(
        integerSize: IntegerSize,
        gradientState: GradientState<ShaderBrush, Size, Color, TileMode, Offset>
    ): Bitmap? = createGradient(
        src = integerSize.toSize().run {
            createBitmap(
                width = width.toInt(),
                height = height.toInt()
            )
        },
        gradientState = gradientState,
        gradientAlpha = 1f
    )

    override suspend fun createGradient(
        src: Bitmap,
        gradientState: GradientState<ShaderBrush, Size, Color, TileMode, Offset>,
        gradientAlpha: Float
    ): Bitmap? = withContext(defaultDispatcher) {
        val size = IntegerSize(
            src.width,
            src.height
        ).toSize()
        gradientState.getBrush(size)?.let { brush ->
            src.copy(src.safeConfig, true).apply {
                setHasAlpha(true)

                Canvas(asImageBitmap()).apply {
                    drawImage(asImageBitmap(), Offset.Zero, Paint())
                    drawRect(
                        paint = Paint().apply {
                            shader = brush.createShader(size)
                            alpha = gradientAlpha
                        },
                        rect = Rect(offset = Offset.Zero, size = size)
                    )
                }
            }
        }
    }

    override suspend fun createMeshGradient(
        integerSize: IntegerSize,
        gradientState: MeshGradientState<Color, Offset>
    ): Bitmap? = createMeshGradient(
        src = integerSize.toSize().run {
            createBitmap(
                width = width.toInt(),
                height = height.toInt()
            )
        },
        gradientState = gradientState,
        gradientAlpha = 1f
    )

    override suspend fun createMeshGradient(
        src: Bitmap,
        gradientState: MeshGradientState<Color, Offset>,
        gradientAlpha: Float
    ): Bitmap? = withContext(defaultDispatcher) {
        src.copy(src.safeConfig, true).apply {
            setHasAlpha(true)

            val paint = Paint().apply {
                alpha = gradientAlpha
            }

            Canvas(asImageBitmap()).apply {
                drawImage(asImageBitmap(), Offset.Zero, Paint())
                drawMeshGradient(
                    pointData = PointData(
                        points = gradientState.points,
                        stepsX = gradientState.resolutionX,
                        stepsY = gradientState.resolutionY
                    ),
                    size = Size(width.toFloat(), height.toFloat()),
                    paint = paint
                )
            }
        }
    }

    private fun IntegerSize.toSize(): Size = Size(
        width.coerceAtLeast(1).toFloat(),
        height.coerceAtLeast(1).toFloat(),
    )


    private fun Canvas.drawMeshGradient(
        pointData: PointData,
        indicesModifier: (List<Int>) -> List<Int> = { it },
        size: Size,
        paint: Paint
    ) {
        CanvasDrawScope().apply {
            drawContext.canvas = this@drawMeshGradient
            drawContext.size = size

            with(drawContext.canvas) {
                scale(
                    scaleX = size.width,
                    scaleY = size.height,
                    pivot = Offset.Zero
                ) {
                    drawVertices(
                        vertices = Vertices(
                            vertexMode = VertexMode.Triangles,
                            positions = pointData.offsets,
                            textureCoordinates = pointData.offsets,
                            colors = pointData.colors,
                            indices = indicesModifier(pointData.indices)
                        ),
                        blendMode = BlendMode.Dst,
                        paint = paint,
                    )
                }
            }
        }
    }

}

internal class PointData(
    private val points: List<List<Pair<Offset, Color>>>,
    private val stepsX: Int,
    private val stepsY: Int
) {
    val offsets: MutableList<Offset>
    val colors: MutableList<Color>
    val indices: List<Int>
    private val xLength: Int = (points[0].size * stepsX) - (stepsX - 1)
    private val yLength: Int = (points.size * stepsY) - (stepsY - 1)
    private val measure = PathMeasure()

    private val indicesBlocks: List<IndicesBlock>

    init {
        offsets = buildList {
            repeat((xLength - 0) * (yLength - 0)) {
                add(Offset(0f, 0f))
            }
        }.toMutableList()

        colors = buildList {
            repeat((xLength - 0) * (yLength - 0)) {
                add(Color.Transparent)
            }
        }.toMutableList()

        indicesBlocks =
            buildList {
                for (y in 0..yLength - 2) {
                    for (x in 0..xLength - 2) {

                        val a = (y * xLength) + x
                        val b = a + 1
                        val c = ((y + 1) * xLength) + x
                        val d = c + 1

                        add(
                            IndicesBlock(
                                indices = buildList {
                                    add(a)
                                    add(c)
                                    add(d)

                                    add(a)
                                    add(b)
                                    add(d)
                                },
                                x = x, y = y
                            )
                        )

                    }
                }
            }

        indices = indicesBlocks.flatMap { it.indices }
        generateInterpolatedOffsets()
    }

    private fun generateInterpolatedOffsets() {
        for (y in 0..points.lastIndex) {
            for (x in 0..points[y].lastIndex) {
                this[x * stepsX, y * stepsY] = points[y][x].first
                this[x * stepsX, y * stepsY] = points[y][x].second

                if (x != points[y].lastIndex) {
                    val path = cubicPathX(
                        point1 = points[y][x].first,
                        point2 = points[y][x + 1].first,
                        when (x) {
                            0 -> 0
                            points[y].lastIndex - 1 -> 2
                            else -> 1
                        }
                    )
                    measure.setPath(path, false)

                    for (i in 1..<stepsX) {
                        measure.getPosition(i / stepsX.toFloat() * measure.length).let {
                            this[(x * stepsX) + i, (y * stepsY)] = Offset(it.x, it.y)
                            this[(x * stepsX) + i, (y * stepsY)] =
                                lerp(
                                    points[y][x].second,
                                    points[y][x + 1].second,
                                    i / stepsX.toFloat(),
                                )
                        }
                    }
                }
            }
        }

        for (y in 0..<points.lastIndex) {
            for (x in 0..<this.xLength) {
                val path = cubicPathY(
                    point1 = this[x, y * stepsY].let { Offset(it.x, it.y) },
                    point2 = this[x, (y + 1) * stepsY].let { Offset(it.x, it.y) },
                    when (y) {
                        0 -> 0
                        points[y].lastIndex - 1 -> 2
                        else -> 1
                    }
                )
                measure.setPath(path, false)
                for (i in (1..<stepsY)) {
                    val point3 = measure.getPosition(i / stepsY.toFloat() * measure.length).let {
                        Offset(it.x, it.y)
                    }

                    this[x, ((y * stepsY) + i)] = point3

                    this[x, ((y * stepsY) + i)] = lerp(
                        this.getColor(x, y * stepsY),
                        this.getColor(x, (y + 1) * stepsY),
                        i / stepsY.toFloat(),
                    )

                }
            }
        }
    }

    data class IndicesBlock(
        val indices: List<Int>,
        val x: Int,
        val y: Int
    )

    operator fun get(
        x: Int,
        y: Int
    ): Offset {
        val index = (y * xLength) + x
        return offsets[index]
    }

    private fun getColor(
        x: Int,
        y: Int
    ): Color {
        val index = (y * xLength) + x
        return colors[index]
    }

    private operator fun set(
        x: Int,
        y: Int,
        offset: Offset
    ) {
        val index = (y * xLength) + x
        offsets[index] = Offset(offset.x, offset.y)
    }

    private operator fun set(
        x: Int,
        y: Int,
        color: Color
    ) {
        val index = (y * xLength) + x
        colors[index] = color
    }
}

private fun cubicPathX(
    point1: Offset,
    point2: Offset,
    position: Int
): Path {
    val path = Path().apply {
        moveTo(point1.x, point1.y)
        val delta = (point2.x - point1.x) * .5f
        when (position) {
            0 -> cubicTo(
                point1.x, point1.y,
                point2.x - delta, point2.y,
                point2.x, point2.y
            )

            2 -> cubicTo(
                point1.x + delta, point1.y,
                point2.x, point2.y,
                point2.x, point2.y
            )

            else -> cubicTo(
                point1.x + delta, point1.y,
                point2.x - delta, point2.y,
                point2.x, point2.y
            )
        }

        lineTo(point2.x, point2.y)
    }
    return path
}

private fun cubicPathY(
    point1: Offset,
    point2: Offset,
    position: Int
): Path {
    val path = Path().apply {
        moveTo(point1.x, point1.y)
        val delta = (point2.y - point1.y) * .5f
        when (position) {
            0 -> cubicTo(
                point1.x, point1.y,
                point2.x, point2.y - delta,
                point2.x, point2.y
            )

            2 -> cubicTo(
                point1.x, point1.y + delta,
                point2.x, point2.y,
                point2.x, point2.y
            )

            else -> cubicTo(
                point1.x, point1.y + delta,
                point2.x, point2.y - delta,
                point2.x, point2.y
            )
        }

        lineTo(point2.x, point2.y)
    }
    return path
}