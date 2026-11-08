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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs

private const val SnakeCells = 20
private val SnakeHead = ArcadeSprite(
    "SnakeHead", 14, 14,
    "M2 0H12V2H14V12H12V14H2V12H0V2H2Z"
)
private val SnakeEyes = ArcadeSprite("SnakeEyes", 14, 14, "M9 2H11V5H9Z M9 9H11V12H9Z")
private val SnakeFood = ArcadeSprite(
    "Apple", 14, 16,
    "M3 4H6V5H8V4H11V5H13V8H14V11H12V14H10V16H8V15H6V16H4V14H2V11H1V8H2V5H3Z"
)
private val SnakeLeaf = ArcadeSprite("AppleLeaf", 14, 16, "M7 2H9V5H7Z M9 0H13V2H11V3H9Z")

@Composable
fun SnakeGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::SnakeRun
    )
    ArcadeBoard(
        game = ArcadeGame.Snake,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onSwipe = run::steer,
            swipeOnDrag = true
        ) {
            val side = minOf(run.width - 28f, run.height - run.topInset - 28f)
            val cell = side / SnakeCells
            val origin = Offset(
                (run.width - side) / 2,
                run.topInset + (run.height - run.topInset - side) / 2
            )
            drawRect(
                color = style.obstacleColor.copy(alpha = 0.06f),
                topLeft = origin,
                size = Size(side, side)
            )
            for (row in 0 until SnakeCells) {
                for (column in 0 until SnakeCells) {
                    if ((row + column) % 2 == 0) {
                        drawRect(
                            color = style.obstacleColor.copy(alpha = 0.07f),
                            topLeft = origin + Offset(column * cell, row * cell),
                            size = Size(cell, cell)
                        )
                    }
                }
            }
            drawRect(
                color = style.detailColor,
                topLeft = origin,
                size = Size(side, side),
                style = Stroke(1f)
            )
            fun center(cellIndex: Int) = origin + Offset(
                (cellIndex % SnakeCells + 0.5f) * cell,
                (cellIndex / SnakeCells + 0.5f) * cell
            )

            if (run.food >= 0) {
                val food = center(run.food)
                translate(food.x - cell * 0.42f, food.y - cell * 0.48f) {
                    scale(cell * 0.06f, Offset.Zero) {
                        drawArcadeSprite(
                            sprite = SnakeFood,
                            tint = style.accentColor,
                            x = 0f,
                            y = 0f
                        )
                        drawArcadeSprite(
                            sprite = SnakeLeaf,
                            tint = style.obstacleColor,
                            x = 0f,
                            y = 0f
                        )
                    }
                }
            }

            val fraction = run.progress
            val head = center(run.previous.first()) +
                    (center(run.body.first()) - center(run.previous.first())) * fraction
            val tail = center(run.previous.last()) +
                    (center(run.body.last()) - center(run.previous.last())) * fraction
            val body = Path().apply {
                moveTo(head.x, head.y)
                for (index in 1..run.body.lastIndex) {
                    val point = center(run.body[index])
                    lineTo(point.x, point.y)
                }
                lineTo(tail.x, tail.y)
            }
            drawPath(
                path = body,
                color = style.playerColor,
                style = Stroke(
                    width = cell * 0.68f,
                    cap = StrokeCap.Square,
                    join = StrokeJoin.Miter
                )
            )
            rotate(run.direction.angle, head) {
                translate(head.x - cell * 0.45f, head.y - cell * 0.45f) {
                    scale(cell * 0.9f / 14f, Offset.Zero) {
                        drawArcadeSprite(
                            sprite = SnakeHead,
                            tint = style.playerColor,
                            x = 0f,
                            y = 0f
                        )
                        drawArcadeSprite(
                            sprite = SnakeEyes,
                            tint = style.playerDetailColor,
                            x = 0f,
                            y = 0f
                        )
                    }
                }
            }
        }
    }
}

private enum class SnakeDirection(
    val dx: Int,
    val dy: Int,
    val angle: Float
) {
    Right(1, 0, 0f),
    Down(0, 1, 90f),
    Left(-1, 0, 180f),
    Up(0, -1, 270f);

    fun opposite(other: SnakeDirection) = dx == -other.dx && dy == -other.dy
}

private class SnakeRun : ArcadeRun() {
    val body = mutableListOf<Int>()
    val previous = mutableListOf<Int>()
    var food = 0
        private set
    var direction = SnakeDirection.Right
        private set
    val progress get() = (elapsed / interval).coerceIn(0f, 1f)
    private val interval get() = (0.16f / (1f + score * 0.035f)).coerceAtLeast(0.075f)
    private var elapsed = 0f
    private val turns = ArrayDeque<SnakeDirection>()

    override fun reset() {
        body.clear()
        repeat(5) { body += SnakeCells * 10 + 8 - it }
        previous.clear()
        previous += body
        direction = SnakeDirection.Right
        food = SnakeCells * 10 + 14
        elapsed = 0f
        turns.clear()
    }

    fun steer(delta: Offset) {
        val next = if (abs(delta.x) > abs(delta.y)) {
            if (delta.x > 0) SnakeDirection.Right else SnakeDirection.Left
        } else {
            if (delta.y > 0) SnakeDirection.Down else SnakeDirection.Up
        }
        val last = turns.lastOrNull() ?: direction
        if (turns.size < 2 && next != last && !next.opposite(last)) turns.addLast(next)
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        if (elapsed < interval) return
        elapsed -= interval
        if (turns.isNotEmpty()) direction = turns.removeFirst()
        val head = body.first()
        val x = head % SnakeCells + direction.dx
        val y = head / SnakeCells + direction.dy
        val next = y * SnakeCells + x
        val growing = next == food
        val occupied = if (growing) body else body.dropLast(1)
        if (x !in 0 until SnakeCells || y !in 0 until SnakeCells || next in occupied) {
            previous.clear()
            previous += body
            phase = ArcadePhase.Over
            return
        }
        previous.clear()
        previous += body
        body.add(0, next)
        if (growing) {
            score++
            food = (0 until SnakeCells * SnakeCells).filterNot { it in body }.randomOrNull() ?: -1
            if (food == -1) phase = ArcadePhase.Over
        } else {
            body.removeAt(body.lastIndex)
        }
    }

    override fun save(state: Bundle) {
        state.putIntArray("body", body.toIntArray())
        state.putIntArray("previous", previous.toIntArray())
        state.putInt("food", food)
        state.putInt("direction", direction.ordinal)
        state.putFloat("elapsed", elapsed)
        state.putIntArray("turns", turns.map { it.ordinal }.toIntArray())
    }

    override fun restore(state: Bundle) {
        state.getIntArray("body")?.let {
            body.clear()
            body += it.toList()
        }
        state.getIntArray("previous")?.let {
            previous.clear()
            previous += it.toList()
        }
        food = state.getInt("food")
        direction = SnakeDirection.entries[state.getInt("direction")]
        elapsed = state.getFloat("elapsed")
        turns.clear()
        state.getIntArray("turns")?.forEach { turns.addLast(SnakeDirection.entries[it]) }
    }
}