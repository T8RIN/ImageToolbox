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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Flag
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

@RegisterArcadeGame
internal data object MinesweeperGame : ArcadeGameDefinition {
    override val title = R.string.arcade_minesweeper
    override val description = R.string.arcade_minesweeper_sub
    override val icon get() = Icons.Outlined.Flag
    override val order = 21

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::MinesweeperRun
        )
        val measurer = rememberTextMeasurer(cacheSize = 24)

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier,
            turnBased = true
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onTap = run::tap,
                onLongPress = run::hold
            ) {
                drawMinesweeperBackground(style, run.width, run.height)
                val cell = run.cell
                drawRoundRect(
                    brush = style.background,
                    topLeft = run.origin,
                    size = Size(cell * run.columns, cell * run.rows),
                    cornerRadius = CornerRadius(3f)
                )
                for (index in run.board.indices) {
                    val point =
                        run.origin + Offset(
                            x = index % run.columns * cell,
                            y = index / run.columns * cell
                        )
                    val opened = run.cover[index] == 1
                    val mine =
                        run.board[index] == -1 && (opened || run.phase == ArcadePhase.Over)
                    val color = when {
                        index == run.exploded -> lerp(
                            style.playerDetailColor,
                            style.hazardColor,
                            0.55f
                        )

                        run.transition > 0f -> style.accentColor.copy(alpha = 0.12f)
                        opened -> style.textColor.copy(alpha = 0.045f)
                        else -> lerp(style.playerDetailColor, style.obstacleColor, 0.32f)
                    }
                    pixelPanel(
                        color = color,
                        ink = if (opened) style.detailColor.copy(alpha = 0.3f) else style.obstacleColor,
                        topLeft = point + Offset(x = 1f, y = 1f),
                        size = Size(width = cell - 2f, height = cell - 2f),
                        inset = opened
                    )
                    val center = point + Offset(x = cell / 2f, y = cell / 2f)
                    when {
                        run.cover[index] == 2 -> {
                            minesweeperFlag(style.accentColor, center, cell * 0.25f)
                            if (run.phase == ArcadePhase.Over && run.board[index] != -1) {
                                val r = cell * 0.24f
                                drawLine(
                                    style.hazardColor,
                                    center - Offset(r, r),
                                    center + Offset(r, r),
                                    2f
                                )
                                drawLine(
                                    style.hazardColor,
                                    center + Offset(-r, r),
                                    center + Offset(r, -r),
                                    2f
                                )
                            }
                        }

                        mine -> {
                            pixelCircle(
                                color = style.hazardColor,
                                radius = cell * 0.17f,
                                center = center
                            )
                            repeat(4) {
                                val delta = when (it) {
                                    0 -> Offset(x = cell * 0.26f, y = 0f)
                                    1 -> Offset(x = 0f, y = cell * 0.26f)
                                    2 -> Offset(x = cell * 0.19f, y = cell * 0.19f)
                                    else -> Offset(x = cell * 0.19f, y = -cell * 0.19f)
                                }
                                drawLine(
                                    color = style.hazardColor,
                                    start = center - delta,
                                    end = center + delta,
                                    strokeWidth = 2f
                                )
                            }
                            pixelCircle(
                                color = style.playerDetailColor,
                                radius = cell * 0.04f,
                                center = center - Offset(x = cell * 0.05f, y = cell * 0.05f)
                            )
                        }

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
                                topLeft = center - Offset(
                                    x = label.size.width / 2f,
                                    y = label.size.height / 2f
                                )
                            )
                        }

                    }
                    if (run.revealAge[index] in 0f..0.3f) {
                        drawRect(
                            color = style.textColor.copy(alpha = (1f - run.revealAge[index] / 0.3f) * 0.08f),
                            topLeft = point + Offset(x = 1f, y = 1f),
                            size = Size(width = cell - 2f, height = cell - 2f)
                        )
                    }
                }
                minesweeperFlag(style.accentColor, Offset(x = 22f, y = run.topInset / 2f), 7f)
                val flags = run.remainingFlags
                val remaining = measurer.measure(
                    text = flags.toString(),
                    style = style.scoreTextStyle,
                    density = Density(1f)
                )
                drawText(
                    textLayoutResult = remaining,
                    color = if (flags == 0) style.hazardColor else style.textColor,
                    topLeft = Offset(x = 35f, y = (run.topInset - remaining.size.height) / 2f)
                )
            }
        }
    }
}

private fun DrawScope.minesweeperFlag(
    color: Color,
    center: Offset,
    radius: Float
) {
    scale(scale = radius / 6f, pivot = center) {
        drawArcadeSprite(
            sprite = MinesweeperFlag,
            tint = color,
            x = center.x - 6f,
            y = center.y - 6f
        )
    }
}

private val MinesweeperFlag = ArcadeSprite(
    width = 12,
    height = 12,
    data = "M2 0H8V1H10V2H12V4H10V5H8V6H4V10H7V12H0V10H2Z"
)

private fun DrawScope.drawMinesweeperBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 215f).toInt()
    val rows = ceil(height / 167f).toInt()
    val ink = style.detailColor.copy(alpha = 0.15f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val center = Offset(column * 215f + 53f + row % 2 * 37f, row * 167f + 41f)
        for (ring in 0..3) {
            val radius = 21f + ring * 12f
            val path = Path().apply {
                repeat(16) { point ->
                    val angle = point * PI.toFloat() / 8f
                    val rough = 0.8f + ((point * 17 + column * 7 + row * 11) % 9) * 0.045f
                    val p = center + Offset(
                        cos(angle) * radius * rough,
                        sin(angle) * radius * rough * 0.71f
                    )
                    if (point == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            if (ring == 0) drawPath(path, ink.copy(alpha = 0.045f))
            drawPath(path, ink, style = Stroke(0.8f))
        }
        val cross = center + Offset(83f, 47f)
        drawLine(ink, cross - Offset(3f, 0f), cross + Offset(3f, 0f), 0.8f)
        drawLine(ink, cross - Offset(0f, 3f), cross + Offset(0f, 3f), 0.8f)
    }
}
@Serializable
private class MinesweeperRun : ArcadeRun() {
    val columns = 8
    var rows = 5
    var board = IntArray(columns * rows)
    var cover = IntArray(board.size)
    var revealAge = FloatArray(board.size) { 1f }
    var round = 1
    var mineCount = 6
    var mineOrder = intArrayOf()
    var generated = false
    var exploded = -1
    var transition = 0f
    val cell get() = minOf((width - 28f) / columns, (height - topInset - 24f) / rows)
    val remainingFlags get() = mineCount - cover.count { it == 2 }
    val boardWidth get() = cell * columns
    val origin
        get() = Offset(
            x = (width - boardWidth) / 2f,
            y = topInset + 12f + (height - topInset - 24f - cell * rows) / 2f
        )

    override fun isValid(): Boolean = super.isValid() &&
            rows in 4..12 &&
            board.size == columns * rows &&
            cover.size == board.size &&
            revealAge.size == board.size &&
            mineOrder.size == board.size &&
            mineOrder.all { it in board.indices }

    override fun reset() {
        round = 1
        newRound()
    }

    private fun newRound() {
        animateLevel()
        rows = ((height - topInset - 24f) / 36f).toInt().coerceIn(4, 12)
        board = IntArray(columns * rows)
        mineOrder = board.indices.shuffled().toIntArray()
        cover = IntArray(board.size)
        revealAge = FloatArray(board.size) { 1f }
        mineCount =
            (board.size * (0.14f + (round - 1).coerceAtMost(12) * 0.006f)).toInt().coerceAtLeast(5)
        generated = false
        exploded = -1
        transition = 0f
        isAnimating = false
    }

    fun neighbors(index: Int): List<Int> {
        val result = ArrayList<Int>(8)
        for (y in -1..1) for (x in -1..1) {
            val column = index % columns + x
            val row = index / columns + y
            if ((x != 0 || y != 0) && column in 0 until columns && row in 0 until rows) result += row * columns + column
        }
        return result
    }

    private fun generate(first: Int) {
        val safe = (neighbors(first) + first).toSet()
        var attempts = 0
        do {
            board.fill(0)
            mineOrder.filterNot { it in safe }.take(mineCount).forEach { board[it] = -1 }
            for (index in board.indices) if (board[index] != -1) board[index] =
                neighbors(index).count { board[it] == -1 }
            if (solvable(first)) break
            mineOrder.shuffle()
            attempts++
            if (attempts % 64 == 0) mineCount--
        } while (mineCount > 0)
        if (mineCount == 0) board.fill(0)
        generated = true
    }

    private fun solvable(first: Int): Boolean {
        val known = IntArray(board.size)
        fun reveal(index: Int) {
            val queue = ArrayDeque<Int>()
            queue.addLast(index)
            while (queue.isNotEmpty()) {
                val cell = queue.removeFirst()
                if (known[cell] != 0) continue
                known[cell] = 1
                if (board[cell] == 0) neighbors(cell).filter { known[it] == 0 }
                    .forEach(queue::addLast)
            }
        }
        reveal(first)
        while (true) {
            val constraints = mutableListOf<Pair<Set<Int>, Int>>()
            for (index in board.indices) if (known[index] == 1) {
                val adjacent = neighbors(index)
                val hidden = adjacent.filter { known[it] == 0 }.toSet()
                if (hidden.isNotEmpty()) constraints += hidden to (board[index] - adjacent.count { known[it] == 2 })
            }
            val hidden = known.indices.filter { known[it] == 0 }.toSet()
            if (hidden.isEmpty()) return true
            constraints += hidden to (mineCount - known.count { it == 2 })
            var changed = false
            fun deduce(cells: Set<Int>, mines: Int) {
                if (mines != 0 && mines != cells.size) return
                for (cell in cells) if (known[cell] == 0) {
                    if (mines == 0) reveal(cell) else known[cell] = 2
                    changed = true
                }
            }
            constraints.forEach { (cells, mines) -> deduce(cells, mines) }
            if (!changed) for ((small, count) in constraints) for ((large, total) in constraints) {
                if (small.size < large.size && large.containsAll(small)) deduce(
                    large - small,
                    total - count
                )
            }
            if (!changed) return false
        }
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

    fun flag(index: Int) {
        if (index !in cover.indices || cover[index] == 1 || transition > 0f) return
        if (cover[index] == 2) cover[index] = 0
        else if (remainingFlags > 0) cover[index] = 2
        else return
        redraw()
    }

    private fun updateAnimation() {
        isAnimating =
            transition > 0f || revealAge.indices.any { cover[it] == 1 && revealAge[it] < 0.4f }
    }

    fun open(index: Int) {
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

}