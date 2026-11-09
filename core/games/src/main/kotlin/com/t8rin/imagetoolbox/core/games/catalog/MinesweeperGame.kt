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

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs

@Composable
fun MinesweeperGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::MinesweeperRun
    )
    val measurer = rememberTextMeasurer(cacheSize = 24)
    ArcadeBoard(
        game = ArcadeGame.Minesweeper,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver,
        turnBased = true
    ) {
        ArcadeField(
            run = run,
            onTap = run::tap,
            onLongPress = run::hold
        ) {
            val cell = run.cell
            for (index in run.board.indices) {
                val point =
                    run.origin + Offset(index % run.columns * cell, index / run.columns * cell)
                val opened = run.cover[index] == 1
                val mine =
                    run.board[index] == -1 && (opened || run.phase == ArcadePhase.Over)
                val color = when {
                    index == run.exploded -> style.hazardColor.copy(alpha = 0.3f)
                    run.transition > 0f -> style.accentColor.copy(alpha = 0.2f)
                    opened -> style.detailColor.copy(alpha = 0.05f)
                    else -> style.playerColor.copy(alpha = 0.14f)
                }
                drawRoundRect(
                    color = color,
                    topLeft = point + Offset(1f, 1f),
                    size = Size(cell - 2f, cell - 2f),
                    cornerRadius = CornerRadius(3f)
                )
                if (!opened) drawRoundRect(
                    color = style.playerColor.copy(alpha = 0.38f),
                    topLeft = point + Offset(1f, 1f),
                    size = Size(cell - 2f, cell - 2f),
                    cornerRadius = CornerRadius(3f),
                    style = Stroke(1f)
                )
                val center = point + Offset(cell / 2f, cell / 2f)
                when {
                    mine -> {
                        drawCircle(
                            color = style.hazardColor,
                            radius = cell * 0.17f,
                            center = center
                        )
                        repeat(4) {
                            val delta = when (it) {
                                0 -> Offset(cell * 0.26f, 0f)
                                1 -> Offset(0f, cell * 0.26f)
                                2 -> Offset(cell * 0.19f, cell * 0.19f)
                                else -> Offset(cell * 0.19f, -cell * 0.19f)
                            }
                            drawLine(
                                color = style.hazardColor,
                                start = center - delta,
                                end = center + delta,
                                strokeWidth = 2f
                            )
                        }
                        drawCircle(
                            color = style.playerDetailColor,
                            radius = cell * 0.04f,
                            center = center - Offset(cell * 0.05f, cell * 0.05f)
                        )
                    }

                    run.cover[index] == 2 -> minesweeperFlag(
                        style.accentColor,
                        center,
                        cell * 0.25f
                    )

                    opened && run.board[index] > 0 -> {
                        val ink = when (run.board[index]) {
                            1 -> style.playerColor
                            2 -> style.obstacleColor
                            3 -> style.accentColor
                            else -> style.hazardColor
                        }
                        val label = measurer.measure(
                            text = run.board[index].toString(),
                            style = style.scoreTextStyle.copy(
                                fontSize = (cell * 0.48f).sp,
                                fontWeight = FontWeight.Bold
                            ),
                            density = Density(1f)
                        )
                        drawText(
                            textLayoutResult = label,
                            color = ink,
                            topLeft = center - Offset(label.size.width / 2f, label.size.height / 2f)
                        )
                    }

                    !opened -> drawCircle(
                        color = style.playerColor.copy(alpha = 0.25f),
                        radius = 1f,
                        center = center
                    )
                }
                if (run.revealAge[index] in 0f..0.3f) {
                    drawRoundRect(
                        color = style.playerColor.copy(alpha = (1f - run.revealAge[index] / 0.3f) * 0.35f),
                        topLeft = point + Offset(1f, 1f),
                        size = Size(cell - 2f, cell - 2f),
                        cornerRadius = CornerRadius(3f)
                    )
                }
            }
            minesweeperFlag(style.accentColor, Offset(22f, run.topInset / 2f), 7f)
            val remaining = measurer.measure(
                text = (run.mineCount - run.cover.count { it == 2 }).toString(),
                style = style.scoreTextStyle,
                density = Density(1f)
            )
            drawText(
                textLayoutResult = remaining,
                color = style.textColor,
                topLeft = Offset(35f, (run.topInset - remaining.size.height) / 2f)
            )
        }
    }
}

private fun DrawScope.minesweeperFlag(
    color: Color,
    center: Offset,
    radius: Float
) {
    val path = Path().apply {
        moveTo(center.x - radius * 0.6f, center.y - radius)
        lineTo(center.x + radius, center.y - radius * 0.35f)
        lineTo(center.x - radius * 0.6f, center.y + radius * 0.25f)
        close()
    }
    drawPath(
        path = path,
        color = color
    )
    drawLine(
        color = color,
        start = center + Offset(-radius * 0.6f, -radius),
        end = center + Offset(-radius * 0.6f, radius),
        strokeWidth = 2f
    )
    drawLine(
        color = color,
        start = center + Offset(-radius, radius),
        end = center + Offset(radius * 0.3f, radius),
        strokeWidth = 2f
    )
}

internal class MinesweeperRun : ArcadeRun() {
    val columns = 8
    var rows = 5
    var board = IntArray(columns * rows)
    var cover = IntArray(board.size)
    var revealAge = FloatArray(board.size) { 1f }
    var round = 1
    var mineCount = 6
    var generated = false
    var exploded = -1
    var transition = 0f
    val cell get() = minOf((width - 28f) / columns, (height - topInset - 24f) / rows)
    val boardWidth get() = cell * columns
    val origin
        get() = Offset(
            (width - boardWidth) / 2f,
            topInset + 12f + (height - topInset - 24f - cell * rows) / 2f
        )

    override fun reset() {
        round = 1
        newRound()
    }

    private fun newRound() {
        rows = ((height - topInset - 24f) / 36f).toInt().coerceIn(4, 12)
        board = IntArray(columns * rows)
        cover = IntArray(board.size)
        revealAge = FloatArray(board.size) { 1f }
        mineCount =
            (board.size * (0.14f + (round - 1).coerceAtMost(12) * 0.006f)).toInt().coerceAtLeast(5)
        generated = false
        exploded = -1
        transition = 0f
        isAnimating = false
    }

    internal fun neighbors(index: Int): List<Int> {
        val result = ArrayList<Int>(8)
        for (y in -1..1) for (x in -1..1) {
            val column = index % columns + x
            val row = index / columns + y
            if ((x != 0 || y != 0) && column in 0 until columns && row in 0 until rows) result += row * columns + column
        }
        return result
    }

    private fun generate(first: Int) {
        val safe = neighbors(first) + first
        val mines = board.indices.filterNot { it in safe }.shuffled().take(mineCount)
        mines.forEach { board[it] = -1 }
        for (index in board.indices) if (board[index] != -1) board[index] =
            neighbors(index).count { board[it] == -1 }
        generated = true
    }

    private fun indexAt(point: Offset): Int {
        val column = ((point.x - origin.x) / cell).toInt()
        val row = ((point.y - origin.y) / cell).toInt()
        if (point.x < origin.x || point.y < origin.y || column !in 0 until columns || row !in 0 until rows) return -1
        return row * columns + column
    }

    fun tap(point: Offset) {
        open(indexAt(point))
    }

    fun hold(point: Offset) {
        flag(indexAt(point))
    }

    internal fun flag(index: Int) {
        if (index in cover.indices && cover[index] != 1 && transition <= 0f) {
            cover[index] = if (cover[index] == 2) 0 else 2
            redraw()
        }
    }

    private fun updateAnimation() {
        isAnimating =
            transition > 0f || revealAge.indices.any { cover[it] == 1 && revealAge[it] < 0.4f }
    }

    internal fun open(index: Int) {
        if (index !in board.indices || transition > 0f || phase != ArcadePhase.Running) return
        if (cover[index] == 2) return
        if (!generated) generate(index)
        if (cover[index] == 1) {
            val adjacent = neighbors(index)
            if (board[index] <= 0 || adjacent.count { cover[it] == 2 } != board[index]) return
            for (other in adjacent) {
                if (cover[other] == 0) reveal(other)
                if (phase == ArcadePhase.Over) break
            }
        } else reveal(index)
        if (phase == ArcadePhase.Running && board.indices.all { board[it] == -1 || cover[it] == 1 }) {
            score += 50 + round * 25
            transition = 1.1f
            isAnimating = true
        }
    }

    private fun reveal(index: Int) {
        if (board[index] == -1) {
            cover[index] = 1
            exploded = index
            phase = ArcadePhase.Over
            isAnimating = false
            return
        }
        val queue = ArrayDeque<Int>()
        queue.addLast(index)
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (cover[next] != 0 || board[next] == -1) continue
            cover[next] = 1
            revealAge[next] =
                -0.025f * (abs(index % columns - next % columns) + abs(index / columns - next / columns))
            score++
            if (board[next] == 0) neighbors(next).filter { cover[it] == 0 }.forEach(queue::addLast)
        }
        isAnimating = true
    }

    override fun advance(seconds: Float) {
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newRound()
                return
            }
        }
        for (index in revealAge.indices) if (cover[index] == 1 && revealAge[index] < 0.4f) revealAge[index] += seconds
        updateAnimation()
    }

    override fun onResize(previous: Size) {
        if (!generated && cover.all { it == 0 }) newRound()
    }

    override fun save(state: Bundle) {
        state.putIntArray("board", board.copyOf())
        state.putIntArray("cover", cover.copyOf())
        state.putFloatArray("reveal", revealAge.copyOf())
        state.putIntArray("run", intArrayOf(rows, round, mineCount, exploded))
        state.putBoolean("generated", generated)
        state.putFloat("transition", transition)
    }

    override fun restore(state: Bundle) {
        state.getIntArray("board")?.let { board = it.copyOf() }
        state.getIntArray("cover")?.let { cover = it.copyOf() }
        state.getFloatArray("reveal")?.let { revealAge = it.copyOf() }
        state.getIntArray("run")?.let {
            rows = it[0]
            round = it[1]
            mineCount = it[2]
            exploded = it[3]
        }
        generated = state.getBoolean("generated")
        transition = state.getFloat("transition")
        updateAnimation()
    }
}