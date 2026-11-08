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

package com.t8rin.imagetoolbox.core.ui.widget.game

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

@Composable
fun BubblePopGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::BubblePopRun)
    ArcadeBoard(
        ArcadeGame.BubblePop, run, style, modifier, initialState, onHighScoreChanged, onGameOver
    ) {
        ArcadeField(run, onTap = run::pop, onSwipe = { if (it.y < -12f) run.rush() }) {
            val cell = run.cell
            val origin = run.origin
            repeat(16) { index ->
                val radius = 12f + index % 4 * 9f
                val x = ((index * 73 + index * index * 17 + 7) % 347) / 347f * run.width
                val y = ((index * 113 + index * index * 29 + 31) % 431) / 431f * run.height
                val color = if (index % 2 == 0) style.playerColor else style.accentColor
                drawCircle(color.copy(alpha = 0.025f), radius, Offset(x, y))
                drawCircle(color.copy(alpha = 0.09f), radius, Offset(x, y), style = Stroke(1f))
                drawCircle(
                    color.copy(alpha = 0.1f),
                    1.5f,
                    Offset(x + radius + 6f, y - radius * 0.4f)
                )
            }
            val danger =
                (run.board.size - run.columns until run.board.size).any { run.board[it] >= 0 }
            if (danger) {
                drawLine(
                    style.hazardColor.copy(alpha = 0.4f + run.rise * 0.6f),
                    origin, origin + Offset(cell * run.columns, 0f), 1.5f
                )
            }
            clipRect(top = run.topInset, bottom = run.height) {
                for (index in run.board.indices) {
                    val color = run.board[index]
                    if (color >= 0) drawBubble(style, run.center(index), cell * 0.43f, color)
                }
                for (column in 0 until run.columns) {
                    val center =
                        origin + Offset((column + 0.5f) * cell, (run.rows + 0.5f - run.rise) * cell)
                    drawBubble(style, center, cell * 0.43f, run.next[column])
                }
                for (burst in run.bursts) {
                    drawCircle(
                        style.accentColor.copy(alpha = burst.life * 2f),
                        cell * (0.7f - burst.life * 0.5f), burst.point, style = Stroke(1.5f)
                    )
                }
                run.effects.draw(this, style.accentColor)
            }
            repeat(minOf(run.combo, 4)) { index ->
                pixelRect(style.accentColor, 14f + index * 10f, run.topInset / 2f - 2f, 6f, 4f)
            }
        }
    }
}

private fun DrawScope.drawBubble(style: ArcadeStyle, point: Offset, radius: Float, kind: Int) {
    val color = when (kind) {
        0 -> style.playerColor
        1 -> style.accentColor
        2 -> style.obstacleColor
        else -> style.textColor
    }
    drawCircle(color.copy(alpha = 0.18f), radius, point)
    drawCircle(color, radius, point, style = Stroke(1.8f))
    drawArc(
        color.copy(alpha = 0.55f),
        205f,
        55f,
        false,
        point - Offset(radius * 0.68f, radius * 0.68f),
        Size(radius * 1.36f, radius * 1.36f),
        style = Stroke(1.5f)
    )
    val r = radius * 0.25f
    when (kind) {
        0 -> drawCircle(color, r, point)
        1 -> pixelRect(color, point.x - r, point.y - r, r * 2f, r * 2f)
        2 -> {
            drawLine(color, point - Offset(r, 0f), point + Offset(r, 0f), 2f)
            drawLine(color, point - Offset(0f, r), point + Offset(0f, r), 2f)
        }

        else -> {
            drawLine(color, point - Offset(r, r), point + Offset(r, r), 2f)
            drawLine(color, point + Offset(r, -r), point + Offset(-r, r), 2f)
        }
    }
}

internal data class BubbleBurst(val point: Offset, var life: Float = 0.35f)

internal class BubblePopRun : ArcadeRun() {
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
    val effects = ArcadeEffects()
    val bursts = mutableListOf<BubbleBurst>()
    val cell get() = minOf((width - 24f) / columns, (height - topInset) / rows)
    val origin get() = Offset((width - cell * columns) / 2f, height - cell * rows)
    var rise = 0f
    var combo = 0
    private var comboTime = 0f
    private var hasSize = false

    fun center(index: Int) = origin + Offset(
        (index % columns + 0.5f) * cell,
        (rows - 0.5f - index / columns - rise + fall[index]) * cell
    )

    override fun reset() {
        fitBoard(emptyList())
        effects.clear()
        bursts.clear()
        rise = 0f
        combo = 0
        comboTime = 0f
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
        val group = group(index)
        if (group.size < 2) return
        combo = if (comboTime > 0f) minOf(4, combo + 1) else 1
        comboTime = 1.8f
        score += group.size * (group.size - 1) * combo
        for (item in group) {
            val point = center(item)
            if (bursts.size >= 48) bursts.removeAt(0)
            bursts += BubbleBurst(point)
            effects.burst(point, 3)
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
        redraw()
    }

    fun rush() {
        rise = maxOf(rise, 0.92f)
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        for (burst in bursts) burst.life -= seconds
        bursts.removeAll { it.life <= 0f }
        for (index in fall.indices) fall[index] = (fall[index] + seconds * 10f).coerceAtMost(0f)
        comboTime -= seconds
        if (comboTime <= 0f) combo = 0
        rise += seconds * rows / 6f / maxOf(2.8f, 7f - score * 0.004f)
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
            fillNext()
        }
    }

    private fun fitBoard(previous: List<Int>) {
        val preferred = minOf((width - 24f) / 6f, (height - topInset) / 6f)
        columns = ceil((width - 24f) / preferred).toInt().coerceAtLeast(6)
        val occupied = previous.filter { it >= 0 }
        rows = maxOf(
            6, ((height - topInset) / ((width - 24f) / columns)).toInt(),
            ceil(occupied.size.toFloat() / columns).toInt()
        )
        board = IntArray(columns * rows) { -1 }
        occupied.forEachIndexed { index, color -> board[index] = color }
        fall = FloatArray(board.size)
        next = IntArray(columns)
        fillNext()
    }

    override fun onResize(previous: Size) {
        val untouched = score == 0 && rise == 0f && board.count { it >= 0 } == columns * 4
        if (phase == ArcadePhase.Ready || !hasSize && untouched) {
            reset()
        } else {
            val oldColumns = columns
            val oldBoard = board.copyOf()
            fitBoard(board.toList())
            if (oldColumns == columns && oldBoard.size <= board.size) {
                board.fill(-1)
                oldBoard.copyInto(board)
            }
        }
        hasSize = true
        bursts.clear()
        effects.clear()
    }

    override fun save(state: Bundle) {
        state.putIntArray("layout", intArrayOf(columns, rows))
        state.putIntArray("bubbles", board.copyOf())
        state.putIntArray("next", next.copyOf())
        state.putFloatArray("fall", fall.copyOf())
        state.putFloatArray("rise", floatArrayOf(rise, combo.toFloat(), comboTime))
    }

    override fun restore(state: Bundle) {
        state.getIntArray("layout")?.let {
            columns = it[0]
            rows = it[1]
        }
        state.getIntArray("bubbles")?.let { board = it.copyOf() }
        state.getIntArray("next")?.let { next = it.copyOf() }
        state.getFloatArray("fall")?.let { fall = it.copyOf() }
        state.getFloatArray("rise")?.let {
            rise = it[0]
            combo = it[1].toInt()
            comboTime = it[2]
        }
    }
}
