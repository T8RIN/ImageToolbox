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
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ViewQuilt
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.pow

@RegisterArcadeGame
internal data object BlockStackGame : ArcadeGameDefinition {
    override val title = R.string.arcade_blockstack
    override val description = R.string.arcade_blockstack_sub
    override val icon get() = Icons.Outlined.ViewQuilt
    override val order = 7

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BlockStackRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::beginDrag,
                onTap = { run.tap() },
                onDrag = run::drag,
                onRelease = run::stopInput
            ) {
                drawBlockStackBackground(style, run.width, run.height)
                val cell = run.cellSize
                val origin = Offset(
                    x = (run.width - cell * BlockStackColumns) / 2,
                    y = run.topInset + (run.height - run.topInset - cell * BlockStackRows) / 2
                )
                val boardSize =
                    Size(width = cell * BlockStackColumns, height = cell * BlockStackRows)
                drawRect(brush = style.background, topLeft = origin, size = boardSize)
                drawRect(
                    color = style.obstacleColor.copy(alpha = 0.035f),
                    topLeft = origin,
                    size = boardSize
                )
                for (column in 1 until BlockStackColumns) {
                    val x = origin.x + column * cell
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.08f),
                        start = Offset(x = x, y = origin.y),
                        end = Offset(x = x, y = origin.y + boardSize.height)
                    )
                }
                for (row in 1 until BlockStackRows) {
                    val y = origin.y + row * cell
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.08f),
                        start = Offset(x = origin.x, y = y),
                        end = Offset(x = origin.x + boardSize.width, y = y)
                    )
                }
                for (x in listOf(origin.x - 3f, origin.x + boardSize.width + 1f)) {
                    drawRect(
                        color = style.obstacleColor.copy(alpha = 0.35f),
                        topLeft = Offset(x = x, y = origin.y),
                        size = Size(width = 2f, height = boardSize.height + 3f)
                    )
                }
                drawRect(
                    color = style.obstacleColor.copy(alpha = 0.35f),
                    topLeft = origin + Offset(x = -3f, y = boardSize.height + 1f),
                    size = Size(width = boardSize.width + 6f, height = 2f)
                )

                fun block(
                    point: Offset,
                    size: Float,
                    type: Int,
                    ghost: Boolean = false,
                    scale: Float = 1f
                ) {
                    if (scale <= 0) return
                    val color = when (type) {
                        0 -> style.playerColor
                        1 -> style.accentColor
                        2 -> style.obstacleColor
                        3 -> lerp(
                            start = style.playerColor,
                            stop = style.accentColor,
                            fraction = 0.35f
                        )

                        4 -> lerp(
                            start = style.obstacleColor,
                            stop = style.accentColor,
                            fraction = 0.6f
                        )

                        5 -> lerp(
                            start = style.playerColor,
                            stop = style.obstacleColor,
                            fraction = 0.55f
                        )

                        else -> lerp(
                            start = style.accentColor,
                            stop = style.playerColor,
                            fraction = 0.2f
                        )
                    }
                    val gap = size * 0.045f
                    val topLeft = point + Offset(x = gap, y = gap + size * (1 - scale) / 2)
                    val blockSize = Size(width = size - gap * 2, height = (size - gap * 2) * scale)
                    if (ghost) {
                        drawRect(
                            color = color.copy(alpha = 0.06f),
                            topLeft = topLeft,
                            size = blockSize
                        )
                        drawRect(
                            color = color.copy(alpha = 0.4f),
                            topLeft = topLeft,
                            size = blockSize,
                            style = Stroke(width = size * 0.06f)
                        )
                    } else {
                        val edge = size * 0.12f
                        val edgeHeight = edge * scale
                        drawRect(
                            color = lerp(
                                start = color,
                                stop = style.obstacleColor,
                                fraction = 0.3f
                            ),
                            topLeft = topLeft,
                            size = blockSize
                        )
                        drawRect(
                            color = lerp(
                                start = color,
                                stop = style.playerDetailColor,
                                fraction = 0.3f
                            ),
                            topLeft = topLeft + Offset(x = edge, y = edgeHeight),
                            size = Size(
                                width = blockSize.width - edge,
                                height = blockSize.height - edgeHeight
                            )
                        )
                        drawRect(
                            color = color,
                            topLeft = topLeft + Offset(x = edge, y = edgeHeight),
                            size = Size(
                                width = blockSize.width - edge * 2f,
                                height = blockSize.height - edgeHeight * 2f
                            )
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
                            val row = index / BlockStackColumns
                            val scale =
                                if (row in run.clearing) 1f - run.clearTime / ClearDuration else 1f
                            block(
                                point = origin + Offset(
                                    x = index % BlockStackColumns * cell,
                                    y = row * cell
                                ),
                                size = cell,
                                type = value - 1,
                                scale = scale
                            )
                        }
                    }
                    if (run.clearing.isEmpty()) {
                        val ghostY = run.landingY()
                        for ((x, y) in run.cells()) {
                            if (ghostY + y >= 0) {
                                block(
                                    point = origin + Offset(
                                        x = (run.x + x) * cell,
                                        y = (ghostY + y) * cell
                                    ),
                                    size = cell,
                                    type = run.piece.ordinal,
                                    ghost = true
                                )
                            }
                            block(
                                point = origin + Offset(
                                    x = (run.x + x) * cell,
                                    y = (run.drawY + y) * cell
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
                for ((x, y) in preview) {
                    block(
                        point = Offset(
                            x = 14f + (x - left) * previewCell,
                            y = (run.topInset - previewHeight) / 2 + (y - top) * previewCell
                        ),
                        size = previewCell,
                        type = run.next.ordinal
                    )
                }
                val holdLeft = 82f
                drawRect(
                    style.detailColor.copy(alpha = if (run.holdUsed) 0.12f else 0.3f),
                    Offset(holdLeft, 4f), Size(60f, run.topInset - 8f),
                    style = Stroke(1f)
                )
                val held = run.held
                if (held != null) {
                    val shape = held.cells(0)
                    val left = shape.minOf { it.x }
                    val top = shape.minOf { it.y }
                    val shapeWidth = (shape.maxOf { it.x } - left + 1) * previewCell
                    val shapeHeight = (shape.maxOf { it.y } - top + 1) * previewCell
                    for ((x, y) in shape) block(
                        point = Offset(
                            holdLeft + (60f - shapeWidth) / 2f + (x - left) * previewCell,
                            (run.topInset - shapeHeight) / 2f + (y - top) * previewCell
                        ),
                        size = previewCell,
                        type = held.ordinal,
                        ghost = run.holdUsed
                    )
                } else {
                    val center = Offset(holdLeft + 30f, run.topInset / 2f)
                    for (side in listOf(-1f, 1f)) {
                        val point = center + Offset(0f, side * 4f)
                        drawLine(
                            style.accentColor,
                            point - Offset(side * 8f, 0f),
                            point + Offset(side * 8f, 0f),
                            1.5f
                        )
                        drawLine(
                            style.accentColor,
                            point + Offset(side * 8f, 0f),
                            point + Offset(side * 4f, -side * 4f),
                            1.5f
                        )
                    }
                }
            }
        }
    }
}

private const val BlockStackColumns = 10
private const val BlockStackRows = 20
private const val ClearDuration = 0.2f
private const val HoldDelay = 0.35f

private fun DrawScope.drawBlockStackBackground(style: ArcadeStyle, width: Float, height: Float) {
    val shapes = listOf(
        listOf(0 to 0, 0 to 1, 1 to 1, 2 to 1),
        listOf(1 to 0, 2 to 0, 0 to 1, 1 to 1),
        listOf(0 to 0, 1 to 0, 2 to 0, 1 to 1),
        listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1)
    )
    val count = (width * height / 21000f).toInt().coerceAtLeast(6)
    repeat(count) { index ->
        val left = (index + 0.32f) * width / count
        val top = height * (0.12f + (index * 37 % 73) / 100f)
        val unit = 8f + index % 2
        for ((x, y) in shapes[index % shapes.size]) {
            val point = Offset(left + x * unit, top + y * unit)
            drawRect(
                style.detailColor.copy(alpha = 0.025f),
                point + Offset(1f, 2f),
                Size(unit - 1f, unit - 1f)
            )
            drawRect(style.detailColor.copy(alpha = 0.025f), point, Size(unit - 1f, unit - 1f))
            drawRect(
                style.detailColor.copy(alpha = 0.12f),
                point,
                Size(unit - 1f, unit - 1f),
                style = Stroke(0.7f)
            )
        }
    }
}

@Serializable
private data class BlockStackCell(
    val x: Int,
    val y: Int
)

@Serializable
private enum class Tetromino(val shape: List<BlockStackCell>) {
    I(
        listOf(
            BlockStackCell(x = 0, y = 1),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1),
            BlockStackCell(x = 3, y = 1)
        )
    ),
    O(
        listOf(
            BlockStackCell(x = 1, y = 0),
            BlockStackCell(x = 2, y = 0),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1)
        )
    ),
    T(
        listOf(
            BlockStackCell(x = 1, y = 0),
            BlockStackCell(x = 0, y = 1),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1)
        )
    ),
    S(
        listOf(
            BlockStackCell(x = 1, y = 0),
            BlockStackCell(x = 2, y = 0),
            BlockStackCell(x = 0, y = 1),
            BlockStackCell(x = 1, y = 1)
        )
    ),
    Z(
        listOf(
            BlockStackCell(x = 0, y = 0),
            BlockStackCell(x = 1, y = 0),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1)
        )
    ),
    J(
        listOf(
            BlockStackCell(x = 0, y = 0),
            BlockStackCell(x = 0, y = 1),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1)
        )
    ),
    L(
        listOf(
            BlockStackCell(x = 2, y = 0),
            BlockStackCell(x = 0, y = 1),
            BlockStackCell(x = 1, y = 1),
            BlockStackCell(x = 2, y = 1)
        )
    );

    fun cells(rotation: Int): List<BlockStackCell> {
        if (this == O) return shape
        val edge = if (this == I) 3 else 2
        return shape.map { cell ->
            var point = cell
            repeat(rotation % 4) { point = BlockStackCell(x = edge - point.y, y = point.x) }
            point
        }
    }
}

@Serializable
private class BlockStackRun : ArcadeRun() {
    val board = IntArray(BlockStackColumns * BlockStackRows)
    val clearing = mutableListOf<Int>()
    var clearTime = 0f
        private set
    var piece = Tetromino.T
        private set
    var next = Tetromino.I
        private set
    var held: Tetromino? = null
        private set
    var holdUsed = false
        private set
    var x = 3
        private set
    var y = 0
        private set
    var drawY = 0f
        private set
    val cellSize
        get() = minOf(
            (width - 28f) / BlockStackColumns,
            (height - topInset - 24f) / BlockStackRows
        )
    private val bag: ArcadeDeque<Tetromino> = ArrayDeque()
    private var rotation = 0
    private var lines = 0
    private var combo = -1
    private var backToBack = false
    private var lastRotation = false
    private var elapsed = 0f
    private var lockTime = 0f
    private var lockResets = 0

    @Transient
    private var dragOrigin: Offset? = null

    @Transient
    private var dragX = 0f

    @Transient
    private var horizontalDrag: Boolean? = null

    @Transient
    private var dropped = false

    @Transient
    private var pressTime = 0f
    private var dropTarget = -1
    private var dropFrom = 0f
    private var dropTime = 0f
    private var dropDuration = 0f

    override fun isValid(): Boolean = super.isValid() &&
            board.size == BlockStackColumns * BlockStackRows && clearing.all { it in 0 until BlockStackRows }

    override fun reset() {
        board.fill(0)
        bag.clear()
        clearing.clear()
        lines = 0
        combo = -1
        backToBack = false
        clearTime = 0f
        dropTarget = -1
        held = null
        holdUsed = false
        stopInput()
        next = takePiece()
        spawn()
    }

    private fun takePiece(): Tetromino {
        if (bag.isEmpty()) bag.addAll(Tetromino.entries.shuffled())
        return bag.removeFirst()
    }

    private fun spawn() {
        val incoming = next
        next = takePiece()
        holdUsed = false
        spawn(incoming)
    }

    private fun spawn(incoming: Tetromino) {
        stopInput()
        piece = incoming
        rotation = 0
        x = 3
        y = 0
        drawY = 0f
        elapsed = 0f
        lockTime = 0f
        lockResets = 0
        lastRotation = false
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
        px in 0 until BlockStackColumns && py in -2 until BlockStackRows &&
                (py < 0 || board[py * BlockStackColumns + px] == 0)
    }

    fun beginDrag(point: Offset) {
        if (clearing.isNotEmpty() || dropTarget >= 0) return
        if (point.y < topInset && point.x in 78f..146f) {
            hold()
            return
        }
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
            repeat(abs(amount).coerceAtMost(BlockStackColumns)) {
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
                    lastRotation = false
                    if (grounded) resetLock()
                }
            }
            dragX += amount * cellSize
        } else if (pressTime < HoldDelay && dy < -maxOf(cellSize * 1.5f, 24f)) {
            dropped = true
            hold()
        } else if (pressTime < HoldDelay && dy > maxOf(cellSize * 1.5f, 24f)) {
            dropped = true
            drop()
        }
    }

    override fun stopInput() {
        dragOrigin = null
        horizontalDrag = null
        pressTime = 0f
    }

    fun tap() {
        if (dragOrigin != null && pressTime < HoldDelay && !dropped) rotate()
    }

    fun hold() {
        if (holdUsed || clearing.isNotEmpty() || dropTarget >= 0 || phase != ArcadePhase.Running) return
        val saved = held
        held = piece
        if (saved == null) spawn() else spawn(saved)
        holdUsed = true
        redraw()
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
                lastRotation = true
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
        if (dropTarget != y) lastRotation = false
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
                var destination = BlockStackRows - 1
                for (row in BlockStackRows - 1 downTo 0) {
                    if (row !in clearing) {
                        for (column in 0 until BlockStackColumns) {
                            board[destination * BlockStackColumns + column] =
                                board[row * BlockStackColumns + column]
                        }
                        destination--
                    }
                }
                while (destination >= 0) {
                    for (column in 0 until BlockStackColumns) board[destination * BlockStackColumns + column] =
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
                lastRotation = false
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
        val spin = piece == Tetromino.T && lastRotation &&
                listOf(-1 to -1, 1 to -1, -1 to 1, 1 to 1).count { (dx, dy) ->
                    val column = x + 1 + dx
                    val row = y + 1 + dy
                    column !in 0 until BlockStackColumns || row !in 0 until BlockStackRows ||
                            board[row * BlockStackColumns + column] != 0
                } >= 3
        cells.forEach { board[(y + it.y) * BlockStackColumns + x + it.x] = piece.ordinal + 1 }
        for (row in 0 until BlockStackRows) {
            if ((0 until BlockStackColumns).all { board[row * BlockStackColumns + it] != 0 }) clearing += row
        }
        if (clearing.isEmpty()) {
            if (spin) score += 400 * (lines / 10 + 1)
            combo = -1
            spawn()
        } else {
            combo++
            val level = lines / 10 + 1
            val difficult = clearing.size == 4 || spin
            val base = if (spin) listOf(400, 800, 1200, 1600)[clearing.size]
            else listOf(0, 100, 300, 500, 800)[clearing.size]
            val bonus = if (difficult && backToBack) base / 2 else 0
            score += (base + bonus + combo * 50) * level
            backToBack = difficult
            lines += clearing.size
            clearTime = 0f
        }
    }
}