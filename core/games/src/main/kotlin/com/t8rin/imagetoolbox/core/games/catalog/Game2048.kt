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

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Filter2
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object Game2048 : ArcadeGameDefinition {
    override val title = R.string.arcade_2048
    override val description = R.string.arcade_2048_sub
    override val icon get() = Icons.Outlined.Filter2
    override val order = 5

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::MergeRun
        )
        val measurer = rememberTextMeasurer(cacheSize = 32)

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier,
            turnBased = true
        ) {
            ArcadeField(
                run = run,
                onSwipe = run::swipe
            ) {
                draw2048Background(style, run.width, run.height)
                val side = minOf(run.width - 24f, run.height - run.topInset - 24f)
                val gap = side * 0.018f
                val cell = (side - gap * (GridSize + 1)) / GridSize
                val origin = Offset(
                    x = (run.width - side) / 2,
                    y = run.topInset + (run.height - run.topInset - side) / 2
                )

                fun position(index: Int) = origin + Offset(
                    x = gap + index % GridSize * (cell + gap),
                    y = gap + index / GridSize * (cell + gap)
                )

                drawRect(brush = style.background, topLeft = origin, size = Size(side, side))
                pixelPanel(
                    color = style.borderColor.copy(alpha = 0.25f),
                    ink = style.detailColor,
                    topLeft = origin,
                    size = Size(width = side, height = side),
                    inset = true
                )
                for (index in run.tiles.indices) {
                    pixelPanel(
                        color = style.textColor.copy(alpha = 0.035f),
                        ink = style.detailColor.copy(alpha = 0.45f),
                        topLeft = position(index),
                        size = Size(width = cell, height = cell),
                        inset = true
                    )
                }

                fun tile(
                    value: Int,
                    point: Offset,
                    scale: Float = 1f
                ) {
                    if (value == 0 || scale <= 0f) return
                    val (color, ink) = when (value) {
                        2 -> lerp(
                            style.playerDetailColor,
                            style.obstacleColor,
                            0.3f
                        ) to style.textColor

                        4 -> lerp(
                            style.playerDetailColor,
                            style.playerColor,
                            0.45f
                        ) to style.textColor

                        8 -> lerp(
                            style.playerDetailColor,
                            style.accentColor,
                            0.55f
                        ) to style.textColor

                        16 -> style.obstacleColor to style.playerDetailColor
                        32 -> style.playerColor to style.playerDetailColor
                        64 -> style.accentColor to style.playerDetailColor
                        128 -> lerp(
                            style.playerColor,
                            style.obstacleColor,
                            0.5f
                        ) to style.playerDetailColor

                        256 -> lerp(
                            style.playerColor,
                            style.accentColor,
                            0.5f
                        ) to style.playerDetailColor

                        512 -> lerp(
                            style.obstacleColor,
                            style.accentColor,
                            0.5f
                        ) to style.playerDetailColor

                        1024 -> style.textColor to style.playerDetailColor
                        else -> style.hazardColor to style.playerDetailColor
                    }
                    val inset = cell * (1 - scale) / 2
                    pixelPanel(
                        color = color,
                        ink = ink,
                        topLeft = point + Offset(x = inset, y = inset),
                        size = Size(width = cell * scale, height = cell * scale)
                    )
                    val digits = value.toString().length
                    val label = measurer.measure(
                        text = value.toString(),
                        style = TextStyle(
                            fontSize = (cell * scale.coerceAtMost(1f) * if (digits < 3) 0.42f else if (digits < 5) 0.32f else 0.25f).sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        density = Density(1f)
                    )
                    drawText(
                        textLayoutResult = label,
                        color = ink.copy(alpha = scale.coerceIn(0f, 1f)),
                        topLeft = point + Offset(
                            x = (cell - label.size.width) / 2,
                            y = (cell - label.size.height) / 2
                        )
                    )
                }

                if (run.isAnimating && run.time < SlideDuration) {
                    val fraction =
                        FastOutSlowInEasing.transform((run.time / SlideDuration).coerceIn(0f, 1f))
                    run.slides.forEach {
                        val from = position(it.from)
                        tile(
                            value = it.value,
                            point = from + (position(it.to) - from) * fraction
                        )
                    }
                } else {
                    val fraction =
                        ((run.time - SlideDuration) / (MoveDuration - SlideDuration)).coerceIn(
                            0f,
                            1f
                        )
                    run.tiles.forEachIndexed { index, value ->
                        val scale = when {
                            !run.isAnimating -> 1f
                            index == run.spawned -> FastOutSlowInEasing.transform(fraction)
                            index in run.merged -> 1f + sin(fraction * Math.PI).toFloat() * 0.1f
                            else -> 1f
                        }
                        tile(
                            value = value,
                            point = position(index),
                            scale = scale
                        )
                    }
                }
            }
        }
    }
}

private const val GridSize = 4
private const val SlideDuration = 0.13f
private const val MoveDuration = 0.27f

private fun DrawScope.draw2048Background(style: ArcadeStyle, width: Float, height: Float) {
    val count = (width * height / 22000f).toInt().coerceAtLeast(5)
    repeat(count) { index ->
        val point =
            Offset((index + 0.3f) * width / count, height * (0.1f + (index * 31 % 77) / 100f))
        val edge = 21f + index % 2 * 5f
        for (layer in 1 downTo 0) {
            val origin = point + Offset(layer * 4f, layer * 5f)
            drawRoundRect(
                style.detailColor.copy(alpha = 0.018f),
                origin,
                Size(edge, edge),
                CornerRadius(2f)
            )
            drawRoundRect(
                style.detailColor.copy(alpha = 0.12f),
                origin,
                Size(edge, edge),
                CornerRadius(2f),
                style = Stroke(0.7f)
            )
        }
    }
}

@Serializable
private data class TileSlide(
    val value: Int,
    val from: Int,
    val to: Int
)

@Serializable
private class MergeRun : ArcadeRun() {
    val tiles = IntArray(GridSize * GridSize)
    val slides = mutableListOf<TileSlide>()
    val merged = mutableSetOf<Int>()
    var spawned = -1
        private set
    var time = MoveDuration
        private set
    private var queuedSwipe: ArcadeOffset? = null

    override fun isValid(): Boolean = super.isValid() &&
            tiles.size == GridSize * GridSize && tiles.all { it >= 0 }

    override fun reset() {
        tiles.fill(0)
        slides.clear()
        merged.clear()
        queuedSwipe = null
        isAnimating = false
        time = MoveDuration
        addTile()
        addTile()
    }

    fun swipe(delta: Offset) {
        if (isAnimating) {
            queuedSwipe = delta
            return
        }
        val horizontal = abs(delta.x) >= abs(delta.y)
        val reversed = if (horizontal) delta.x > 0f else delta.y > 0f
        val before = tiles.copyOf()
        slides.clear()
        merged.clear()
        var gained = 0
        for (line in 0 until GridSize) {
            val indices = List(GridSize) { position ->
                val step = if (reversed) GridSize - 1 - position else position
                if (horizontal) line * GridSize + step else step * GridSize + line
            }
            val occupied = indices.filter { before[it] != 0 }
            indices.forEach { tiles[it] = 0 }
            var source = 0
            var destination = 0
            while (source < occupied.size) {
                val from = occupied[source++]
                val value = before[from]
                val to = indices[destination++]
                slides += TileSlide(value = value, from = from, to = to)
                if (source < occupied.size && before[occupied[source]] == value) {
                    slides += TileSlide(value = value, from = occupied[source++], to = to)
                    tiles[to] = value * 2
                    gained += value * 2
                    merged += to
                } else {
                    tiles[to] = value
                }
            }
        }
        if (before.contentEquals(tiles)) {
            if (!hasMoves()) phase = ArcadePhase.Over
            return
        }
        score += gained
        addTile()
        time = 0f
        isAnimating = true
        redraw()
    }

    override fun advance(seconds: Float) {
        if (!isAnimating) return
        time += seconds
        if (time >= MoveDuration) {
            isAnimating = false
            slides.clear()
            merged.clear()
            if (!hasMoves()) phase = ArcadePhase.Over
            val next = queuedSwipe
            queuedSwipe = null
            if (next != null && phase == ArcadePhase.Running) swipe(next)
        }
    }

    private fun addTile() {
        val empty = tiles.indices.filter { tiles[it] == 0 }
        spawned = empty.randomOrNull() ?: return
        tiles[spawned] = if (Random.nextInt(10) == 0) 4 else 2
    }

    private fun hasMoves(): Boolean = tiles.indices.any { index ->
        tiles[index] == 0 ||
                index % GridSize < GridSize - 1 && tiles[index] == tiles[index + 1] ||
                index / GridSize < GridSize - 1 && tiles[index] == tiles[index + GridSize]
    }
}