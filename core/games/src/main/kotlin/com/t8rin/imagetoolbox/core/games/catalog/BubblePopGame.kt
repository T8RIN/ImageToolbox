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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BubbleChart
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

@RegisterArcadeGame
internal data object BubblePopGame : ArcadeGameDefinition {
    override val title = R.string.arcade_bubblepop
    override val description = R.string.arcade_bubblepop_sub
    override val icon get() = Icons.Outlined.BubbleChart
    override val order = 17

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BubblePopRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onTap = run::pop,
                onSwipe = { if (it.y < 0f && abs(it.y) > abs(it.x)) run.raise() }
            ) {
                val cell = run.cell
                val origin = run.origin
                val backdropCount = (run.width * run.height / 6500f).toInt().coerceAtLeast(16)
                repeat(backdropCount) { index ->
                    val radius = 12f + index % 4 * 9f
                    val x = ((index * 73 + index * index * 17 + 7) % 347) / 347f * run.width
                    val y = ((index * 113 + index * index * 29 + 31) % 431) / 431f * run.height
                    val color = if (index % 2 == 0) style.playerColor else style.accentColor
                    pixelCircle(
                        color = color.copy(alpha = 0.025f),
                        radius = radius,
                        center = Offset(x = x, y = y)
                    )
                    pixelCircle(
                        color = color.copy(alpha = 0.09f),
                        radius = radius,
                        center = Offset(x = x, y = y),
                        style = Stroke(width = 1f)
                    )
                    pixelCircle(
                        color = color.copy(alpha = 0.1f),
                        radius = 1.5f,
                        center = Offset(x = x + radius + 6f, y = y - radius * 0.4f)
                    )
                }
                pixelRect(style.detailColor.copy(alpha = 0.2f), 14f, run.topInset - 5f, 70f, 2f)
                pixelRect(style.accentColor, 14f, run.topInset - 5f, 70f * run.charge / 18f, 2f)
                val danger =
                    (run.board.size - run.columns until run.board.size).any { run.board[it] >= 0 }
                if (danger) {
                    drawLine(
                        color = style.hazardColor.copy(alpha = 0.4f + run.rise * 0.6f),
                        start = origin,
                        end = origin + Offset(x = cell * run.columns, y = 0f),
                        strokeWidth = 1.5f
                    )
                }
                clipRect(top = run.topInset, bottom = run.height) {
                    for (index in run.board.indices) {
                        val color = run.board[index]
                        if (color >= 0) drawBubble(
                            style = style,
                            point = run.center(index),
                            radius = cell * 0.43f,
                            kind = color
                        )
                    }
                    for (column in 0 until run.columns) {
                        val center =
                            origin + Offset(
                                x = (column + 0.5f) * cell,
                                y = (run.rows + 0.5f - run.rise) * cell
                            )
                        drawBubble(
                            style = style,
                            point = center,
                            radius = cell * 0.43f,
                            kind = run.next[column]
                        )
                    }
                    for ((point, life) in run.bursts) {
                        pixelCircle(
                            color = style.accentColor.copy(alpha = life * 2f),
                            radius = cell * (0.7f - life * 0.5f),
                            center = point,
                            style = Stroke(width = 1.5f)
                        )
                    }
                    run.effects.draw(
                        scope = this,
                        color = style.accentColor
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawBubble(
    style: ArcadeStyle,
    point: Offset,
    radius: Float,
    kind: Int
) {
    val color = when (kind) {
        0 -> style.playerColor
        1 -> style.accentColor
        2 -> style.obstacleColor
        else -> style.textColor
    }
    pixelCircle(
        color = color.copy(alpha = 0.24f),
        radius = radius,
        center = point
    )
    pixelCircle(
        color = color,
        radius = radius,
        center = point,
        style = Stroke(width = 1.8f)
    )
    pixelRect(
        color = color.copy(alpha = 0.7f),
        x = point.x - radius * 0.45f,
        y = point.y - radius * 0.65f,
        width = radius * 0.5f,
        height = 2f
    )
    pixelRect(
        color = color.copy(alpha = 0.7f),
        x = point.x - radius * 0.65f,
        y = point.y - radius * 0.45f,
        width = 2f,
        height = radius * 0.3f
    )
    val r = radius * 0.25f
    when (kind) {
        0 -> pixelCircle(
            color = color,
            radius = r,
            center = point
        )

        1 -> pixelRect(
            color = color,
            x = point.x - r,
            y = point.y - r,
            width = r * 2f,
            height = r * 2f
        )

        2 -> {
            drawLine(
                color = color,
                start = point - Offset(x = r, y = 0f),
                end = point + Offset(x = r, y = 0f),
                strokeWidth = 2f
            )
            drawLine(
                color = color,
                start = point - Offset(x = 0f, y = r),
                end = point + Offset(x = 0f, y = r),
                strokeWidth = 2f
            )
        }

        else -> {
            drawLine(
                color = color,
                start = point - Offset(x = r, y = r),
                end = point + Offset(x = r, y = r),
                strokeWidth = 2f
            )
            drawLine(
                color = color,
                start = point + Offset(x = r, y = -r),
                end = point + Offset(x = -r, y = r),
                strokeWidth = 2f
            )
        }
    }
}

@Serializable
private data class BubbleBurst(
    val point: ArcadeOffset,
    var life: Float = 0.35f
)

@Serializable
private class BubblePopRun : ArcadeRun() {
    var columns = 6
        private set
    var rows = 6
        private set
    var board = IntArray(columns * rows) { -1 }
        private set
    var next = IntArray(columns)
        private set
    var fall = FloatArray(board.size)
        private set

    @Transient
    val effects = ArcadeEffects()
    val bursts = mutableListOf<BubbleBurst>()
    val cell get() = minOf((width - 24f) / columns, (height - topInset) / rows)
    val origin get() = Offset(x = (width - cell * columns) / 2f, y = height - cell * rows)
    var rise = 0f
    var charge = 0
        private set
    var combo = 0
    private var comboTime = 0f
    private var advanceRow = false
    private var received = 0

    fun center(index: Int) = origin + Offset(
        x = (index % columns + 0.5f) * cell,
        y = (rows - 0.5f - index / columns - rise + fall[index]) * cell
    )

    override fun isValid(): Boolean = super.isValid() &&
            columns >= 6 &&
            rows >= 6 &&
            board.size == columns * rows &&
            fall.size == board.size &&
            next.size == columns

    override fun reset() {
        fitBoard()
        effects.clear()
        bursts.clear()
        rise = 0f
        combo = 0
        charge = 0
        comboTime = 0f
        advanceRow = false
        received = 0
        repeat(4) { row ->
            fillNext()
            next.copyInto(board, row * columns)
        }
        fillNext()
    }

    private fun fillNext() {
        for (column in next.indices) {
            next[column] = if (column > 0 && Random.nextFloat() < 0.4f) next[column - 1]
            else Random.nextInt(if (score < 100) 3 else 4)
        }
        received++
        if (received % 7 == 0) {
            val length = (columns * 0.65f).toInt().coerceAtLeast(3)
            val start = Random.nextInt(columns - length + 1)
            val color = Random.nextInt(if (score < 100) 3 else 4)
            for (column in start until start + length) next[column] = color
        }
        // Every arriving row offers at least one useful tap.
        val pair = Random.nextInt(columns - 1)
        next[pair + 1] = next[pair]
    }

    fun group(index: Int): List<Int> {
        if (index !in board.indices || board[index] < 0) return emptyList()
        val result = mutableListOf(index)
        var cursor = 0
        while (cursor < result.size) {
            val current = result[cursor++]
            for (other in intArrayOf(
                current - columns,
                current + columns,
                current - 1,
                current + 1
            )) {
                if (other in board.indices && board[other] == board[index] && other !in result &&
                    abs(other / columns - current / columns) + abs(other % columns - current % columns) == 1
                ) result += other
            }
        }
        return result
    }

    fun pop(point: Offset) {
        val index = board.indices.firstOrNull {
            board[it] >= 0 && (center(it) - point).getDistance() < cell * 0.48f
        } ?: return
        val connected = group(index)
        val powered = charge >= 18
        if (connected.size < 2 && !powered) return
        val group = if (powered) board.indices.filter { board[it] == board[index] } else connected
        charge = if (powered) 0 else (charge + connected.size).coerceAtMost(18)
        combo = if (comboTime > 0f) minOf(4, combo + 1) else 1
        comboTime = 1.8f
        score += group.size * (group.size - 1) * combo
        for (item in group) {
            val point = center(item)
            if (bursts.size >= 48) bursts.removeAt(0)
            bursts += BubbleBurst(point = point)
            effects.burst(
                point = point,
                count = 3
            )
            board[item] = -1
        }
        for (column in 0 until columns) {
            var target = 0
            for (row in 0 until rows) {
                val source = row * columns + column
                if (board[source] < 0) continue
                val destination = target * columns + column
                if (destination != source) {
                    board[destination] = board[source]
                    fall[destination] = target - row + fall[source]
                    board[source] = -1
                    fall[source] = 0f
                }
                target++
            }
        }
        val cleared = board.none { it >= 0 }
        if (cleared) {
            score += 50 + combo * 10
            rise = (rise - 0.4f).coerceAtLeast(0f)
            effects.burst(Offset(width / 2f, height - cell), 20)
        }
        advanceRow = true
        redraw()
    }

    fun raise() {
        advanceRow = true
        redraw()
    }

    private fun hasMoves(): Boolean = board.indices.any { index ->
        board[index] >= 0 && (
                index % columns < columns - 1 && board[index + 1] == board[index] ||
                        index + columns < board.size && board[index + columns] == board[index]
                )
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        for (burst in bursts) burst.life -= seconds
        bursts.removeAll { it.life <= 0f }
        for (index in fall.indices) fall[index] = (fall[index] + seconds * 10f).coerceAtMost(0f)
        comboTime -= seconds
        if (comboTime <= 0f) combo = 0
        if (board.none { it >= 0 }) {
            advanceRow = true
        } else if (!hasMoves()) {
            charge = 18
            advanceRow = false
            rise = rise.coerceAtMost(0.5f)
        }
        val speed = rows / 6f / maxOf(4.2f, 8f - score * 0.0025f)
        rise += seconds * if (advanceRow && comboTime < 1.4f && fall.none { it < 0f }) {
            maxOf(0.85f, speed)
        } else speed
        if (rise >= 1f) {
            if ((board.size - columns until board.size).any { board[it] >= 0 }) {
                phase = ArcadePhase.Over
                return
            }
            for (index in board.lastIndex downTo columns) {
                board[index] = board[index - columns]
                fall[index] = fall[index - columns]
            }
            next.copyInto(board)
            for (column in 0 until columns) fall[column] = 0f
            rise -= 1f
            advanceRow = !hasMoves()
            fillNext()
        }
    }

    private fun fitBoard() {
        val preferred = minOf((width - 24f) / 6f, (height - topInset) / 6f)
        columns = ceil((width - 24f) / preferred).toInt().coerceAtLeast(6)
        rows = maxOf(
            6, ((height - topInset) / ((width - 24f) / columns)).toInt()
        )
        board = IntArray(columns * rows) { -1 }
        fall = FloatArray(board.size)
        next = IntArray(columns)
        fillNext()
    }

    override fun onResize(previous: Size) {
        bursts.clear()
        effects.clear()
    }
}