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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
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
import kotlin.math.pow

private const val TetrisColumns = 10
private const val TetrisRows = 20
private const val ClearDuration = 0.2f
private const val HoldDelay = 0.35f

@Composable
fun TetrisGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::TetrisRun
    )
    ArcadeBoard(
        game = ArcadeGame.Tetris,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::beginDrag,
            onTap = { run.tap() },
            onDrag = run::drag,
            onRelease = run::stopInput
        ) {
            val cell = run.cellSize
            val origin = Offset(
                (run.width - cell * TetrisColumns) / 2,
                run.topInset + (run.height - run.topInset - cell * TetrisRows) / 2
            )
            val boardSize = Size(cell * TetrisColumns, cell * TetrisRows)
            drawRect(style.detailColor.copy(alpha = 0.08f), origin, boardSize)
            for (column in 1 until TetrisColumns) {
                val x = origin.x + column * cell
                drawLine(
                    color = style.detailColor.copy(alpha = 0.12f),
                    start = Offset(x, origin.y),
                    end = Offset(x, origin.y + boardSize.height)
                )
            }
            for (row in 1 until TetrisRows) {
                val y = origin.y + row * cell
                drawLine(
                    color = style.detailColor.copy(alpha = 0.12f),
                    start = Offset(origin.x, y),
                    end = Offset(origin.x + boardSize.width, y)
                )
            }
            drawRect(style.detailColor, origin, boardSize, style = Stroke(1f))

            fun block(
                point: Offset,
                size: Float,
                type: Int,
                ghost: Boolean = false,
                scale: Float = 1f
            ) {
                if (scale <= 0) return
                val color = when (type % 3) {
                    0 -> style.playerColor
                    1 -> style.obstacleColor
                    else -> style.accentColor
                }
                val gap = size * 0.055f
                val topLeft = point + Offset(gap, gap + size * (1 - scale) / 2)
                val blockSize = Size(size - gap * 2, (size - gap * 2) * scale)
                if (ghost) {
                    drawRect(color.copy(alpha = 0.28f), topLeft, blockSize, style = Stroke(1f))
                } else {
                    drawRect(color, topLeft, blockSize)
                    val edge = size * 0.08f
                    drawRect(
                        Color.White.copy(alpha = 0.18f),
                        topLeft,
                        Size(blockSize.width, minOf(edge, blockSize.height))
                    )
                    drawRect(
                        Color.Black.copy(alpha = 0.12f),
                        topLeft + Offset(0f, (blockSize.height - edge).coerceAtLeast(0f)),
                        Size(blockSize.width, minOf(edge, blockSize.height))
                    )
                }
            }
            clipRect(
                left = origin.x,
                top = origin.y,
                right = origin.x + boardSize.width,
                bottom = origin.y + boardSize.height
            ) {
                run.board.forEachIndexed { index, value ->
                    if (value > 0) {
                        val row = index / TetrisColumns
                        val scale =
                            if (row in run.clearing) 1f - run.clearTime / ClearDuration else 1f
                        block(
                            point = origin + Offset(index % TetrisColumns * cell, row * cell),
                            size = cell,
                            type = value - 1,
                            scale = scale
                        )
                    }
                }
                if (run.clearing.isEmpty()) {
                    val ghostY = run.landingY()
                    for (point in run.cells()) {
                        if (ghostY + point.y >= 0) {
                            block(
                                point = origin + Offset(
                                    (run.x + point.x) * cell,
                                    (ghostY + point.y) * cell
                                ),
                                size = cell,
                                type = run.piece.ordinal,
                                ghost = true
                            )
                        }
                        block(
                            point = origin + Offset(
                                (run.x + point.x) * cell,
                                (run.drawY + point.y) * cell
                            ),
                            size = cell,
                            type = run.piece.ordinal
                        )
                    }
                }
            }
            val preview = run.next.cells(0)
            val previewCell = minOf(cell, 12f, run.topInset / 3)
            val left = preview.minOf { it.x }
            val top = preview.minOf { it.y }
            val previewHeight = (preview.maxOf { it.y } - top + 1) * previewCell
            for (point in preview) {
                block(
                    point = Offset(
                        14f + (point.x - left) * previewCell,
                        (run.topInset - previewHeight) / 2 + (point.y - top) * previewCell
                    ),
                    size = previewCell,
                    type = run.next.ordinal
                )
            }
        }
    }
}

private data class TetrisCell(
    val x: Int,
    val y: Int
)

private enum class Tetromino(val shape: List<TetrisCell>) {
    I(listOf(TetrisCell(0, 1), TetrisCell(1, 1), TetrisCell(2, 1), TetrisCell(3, 1))),
    O(listOf(TetrisCell(1, 0), TetrisCell(2, 0), TetrisCell(1, 1), TetrisCell(2, 1))),
    T(listOf(TetrisCell(1, 0), TetrisCell(0, 1), TetrisCell(1, 1), TetrisCell(2, 1))),
    S(listOf(TetrisCell(1, 0), TetrisCell(2, 0), TetrisCell(0, 1), TetrisCell(1, 1))),
    Z(listOf(TetrisCell(0, 0), TetrisCell(1, 0), TetrisCell(1, 1), TetrisCell(2, 1))),
    J(listOf(TetrisCell(0, 0), TetrisCell(0, 1), TetrisCell(1, 1), TetrisCell(2, 1))),
    L(listOf(TetrisCell(2, 0), TetrisCell(0, 1), TetrisCell(1, 1), TetrisCell(2, 1)));

    fun cells(rotation: Int): List<TetrisCell> {
        if (this == O) return shape
        val edge = if (this == I) 3 else 2
        return shape.map { cell ->
            var point = cell
            repeat(rotation % 4) { point = TetrisCell(edge - point.y, point.x) }
            point
        }
    }
}

private class TetrisRun : ArcadeRun() {
    val board = IntArray(TetrisColumns * TetrisRows)
    val clearing = mutableListOf<Int>()
    var clearTime = 0f
        private set
    var piece = Tetromino.T
        private set
    var next = Tetromino.I
        private set
    var x = 3
        private set
    var y = 0
        private set
    var drawY = 0f
        private set
    val cellSize
        get() = minOf(
            (width - 28f) / TetrisColumns,
            (height - topInset - 24f) / TetrisRows
        )
    private val bag = ArrayDeque<Tetromino>()
    private var rotation = 0
    private var lines = 0
    private var elapsed = 0f
    private var lockTime = 0f
    private var lockResets = 0
    private var dragOrigin: Offset? = null
    private var dragX = 0f
    private var horizontalDrag: Boolean? = null
    private var dropped = false
    private var pressTime = 0f
    private var dropTarget = -1
    private var dropFrom = 0f
    private var dropTime = 0f
    private var dropDuration = 0f

    override fun reset() {
        board.fill(0)
        bag.clear()
        clearing.clear()
        lines = 0
        clearTime = 0f
        dropTarget = -1
        stopInput()
        next = takePiece()
        spawn()
    }

    private fun takePiece(): Tetromino {
        if (bag.isEmpty()) bag.addAll(Tetromino.entries.shuffled())
        return bag.removeFirst()
    }

    private fun spawn() {
        stopInput()
        piece = next
        next = takePiece()
        rotation = 0
        x = 3
        y = 0
        drawY = y.toFloat()
        elapsed = 0f
        lockTime = 0f
        lockResets = 0
        if (!fits(
                x = x,
                y = y,
                rotation = rotation
            )
        ) phase = ArcadePhase.Over
    }

    fun cells() = piece.cells(rotation)

    private fun fits(
        x: Int,
        y: Int,
        rotation: Int
    ): Boolean = piece.cells(rotation).all {
        val px = x + it.x
        val py = y + it.y
        px in 0 until TetrisColumns && py in -2 until TetrisRows &&
                (py < 0 || board[py * TetrisColumns + px] == 0)
    }

    fun beginDrag(point: Offset) {
        if (clearing.isNotEmpty() || dropTarget >= 0) return
        dragOrigin = point
        dragX = point.x
        horizontalDrag = null
        dropped = false
        pressTime = 0f
    }

    fun drag(point: Offset) {
        if (clearing.isNotEmpty() || dropTarget >= 0 || dropped) return
        val origin = dragOrigin ?: return
        val dx = point.x - origin.x
        val dy = point.y - origin.y
        if (horizontalDrag == null) {
            if (maxOf(abs(dx), abs(dy)) < maxOf(cellSize * 0.35f, 8f)) return
            horizontalDrag = abs(dx) >= abs(dy)
        }
        if (horizontalDrag == true) {
            if (pressTime >= HoldDelay) elapsed = 0f
            pressTime = 0f
            val amount = ((point.x - dragX) / cellSize).toInt()
            val direction = if (amount > 0) 1 else -1
            repeat(abs(amount).coerceAtMost(TetrisColumns)) {
                val grounded = !fits(
                    x = x,
                    y = y + 1,
                    rotation = rotation
                )
                if (fits(
                        x = x + direction,
                        y = y,
                        rotation = rotation
                    )
                ) {
                    x += direction
                    if (grounded) resetLock()
                }
            }
            dragX += amount * cellSize
        } else if (pressTime < HoldDelay && dy > maxOf(cellSize * 1.5f, 24f)) {
            dropped = true
            drop()
        }
    }

    override fun stopInput() {
        if (pressTime >= HoldDelay) elapsed = 0f
        dragOrigin = null
        horizontalDrag = null
        pressTime = 0f
    }

    fun tap() {
        if (dragOrigin != null && pressTime < HoldDelay && !dropped) rotate()
    }

    fun rotate() {
        if (clearing.isNotEmpty() || dropTarget >= 0 || piece == Tetromino.O) return
        val nextRotation = (rotation + 1) % 4
        val kicks = if (piece == Tetromino.I) {
            when (rotation) {
                0 -> listOf(-2 to 0, 1 to 0, -2 to 1, 1 to -2)
                1 -> listOf(-1 to 0, 2 to 0, -1 to -2, 2 to 1)
                2 -> listOf(2 to 0, -1 to 0, 2 to -1, -1 to 2)
                else -> listOf(1 to 0, -2 to 0, 1 to 2, -2 to -1)
            }
        } else {
            when (rotation) {
                0 -> listOf(-1 to 0, -1 to -1, 0 to 2, -1 to 2)
                1 -> listOf(1 to 0, 1 to 1, 0 to -2, 1 to -2)
                2 -> listOf(1 to 0, 1 to -1, 0 to 2, 1 to 2)
                else -> listOf(-1 to 0, -1 to 1, 0 to -2, -1 to -2)
            }
        }
        val grounded = !fits(
            x = x,
            y = y + 1,
            rotation = rotation
        )
        for ((dx, dy) in listOf(0 to 0) + kicks) {
            if (fits(
                    x = x + dx,
                    y = y + dy,
                    rotation = nextRotation
                )
            ) {
                x += dx
                y += dy
                drawY = y.toFloat()
                rotation = nextRotation
                if (grounded) resetLock()
                return
            }
        }
    }

    private fun resetLock() {
        if (lockResets < 15) {
            lockTime = 0f
            lockResets++
        }
    }

    fun landingY(): Int {
        var result = y
        while (fits(
                x = x,
                y = result + 1,
                rotation = rotation
            )
        ) result++
        return result
    }

    fun drop() {
        if (clearing.isNotEmpty() || dropTarget >= 0) return
        dropTarget = landingY()
        dropFrom = drawY
        dropTime = 0f
        dropDuration = ((dropTarget - dropFrom) * 0.009f).coerceIn(0.08f, 0.18f)
    }

    override fun advance(seconds: Float) {
        if (dropTarget >= 0) {
            dropTime += seconds
            val progress = (dropTime / dropDuration).coerceAtMost(1f)
            drawY = dropFrom + (dropTarget - dropFrom) * progress * progress
            if (progress == 1f) {
                score += (dropTarget - y) * 2
                y = dropTarget
                dropTarget = -1
                lock()
            }
            return
        }
        if (clearing.isNotEmpty()) {
            clearTime += seconds
            if (clearTime >= ClearDuration) {
                var destination = TetrisRows - 1
                for (row in TetrisRows - 1 downTo 0) {
                    if (row !in clearing) {
                        for (column in 0 until TetrisColumns) {
                            board[destination * TetrisColumns + column] =
                                board[row * TetrisColumns + column]
                        }
                        destination--
                    }
                }
                while (destination >= 0) {
                    for (column in 0 until TetrisColumns) board[destination * TetrisColumns + column] =
                        0
                    destination--
                }
                clearing.clear()
                clearTime = 0f
                spawn()
            }
            return
        }
        val wasHeld = pressTime >= HoldDelay
        if (dragOrigin != null && horizontalDrag != true) pressTime += seconds
        val held = pressTime >= HoldDelay
        if (held && !wasHeld) elapsed = 0f
        elapsed += seconds
        val interval = if (held) 0.045f else (0.72f * 0.82f.pow(lines / 10)).coerceAtLeast(0.08f)
        while (elapsed >= interval) {
            elapsed -= interval
            if (fits(
                    x = x,
                    y = y + 1,
                    rotation = rotation
                )
            ) {
                y++
                if (held) score++
            }
        }
        drawY = y.toFloat()
        if (!fits(
                x = x,
                y = y + 1,
                rotation = rotation
            )
        ) {
            lockTime += seconds
            if (lockTime >= 0.5f) lock()
        } else lockTime = 0f
    }

    private fun lock() {
        val cells = cells()
        if (cells.any { y + it.y < 0 }) {
            phase = ArcadePhase.Over
            return
        }
        cells.forEach { board[(y + it.y) * TetrisColumns + x + it.x] = piece.ordinal + 1 }
        for (row in 0 until TetrisRows) {
            if ((0 until TetrisColumns).all { board[row * TetrisColumns + it] != 0 }) clearing += row
        }
        if (clearing.isEmpty()) {
            spawn()
        } else {
            score += listOf(0, 100, 300, 500, 800)[clearing.size] * (lines / 10 + 1)
            lines += clearing.size
            clearTime = 0f
        }
    }

    override fun save(state: Bundle) {
        state.putIntArray("board", board.copyOf())
        state.putIntArray(
            "piece",
            intArrayOf(piece.ordinal, next.ordinal, x, y, rotation, lines, lockResets)
        )
        state.putFloatArray("timing", floatArrayOf(elapsed, lockTime, clearTime))
        state.putIntArray("bag", bag.map { it.ordinal }.toIntArray())
        state.putIntArray("clearing", clearing.toIntArray())
        state.putInt("dropTarget", dropTarget)
        state.putFloatArray("drop", floatArrayOf(dropFrom, dropTime, dropDuration, drawY))
    }

    override fun restore(state: Bundle) {
        state.getIntArray("board")?.copyInto(board)
        state.getIntArray("piece")?.let {
            piece = Tetromino.entries[it[0]]
            next = Tetromino.entries[it[1]]
            x = it[2]
            y = it[3]
            drawY = y.toFloat()
            rotation = it[4]
            lines = it[5]
            lockResets = it[6]
        }
        state.getFloatArray("timing")?.let {
            elapsed = it[0]
            lockTime = it[1]
            clearTime = it[2]
        }
        bag.clear()
        state.getIntArray("bag")?.forEach { bag.addLast(Tetromino.entries[it]) }
        clearing.clear()
        state.getIntArray("clearing")?.let { clearing.addAll(it.toList()) }
        dropTarget = state.getInt("dropTarget", -1)
        state.getFloatArray("drop")?.let {
            dropFrom = it[0]
            dropTime = it[1]
            dropDuration = it[2]
            drawY = it[3]
        }
    }
}