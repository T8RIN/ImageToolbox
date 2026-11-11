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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Snake
import kotlinx.serialization.Serializable
import kotlin.math.abs

@RegisterArcadeGame
internal data object SnakeGame : ArcadeGameDefinition {
    override val title = R.string.arcade_snake
    override val description = R.string.arcade_snake_sub
    override val icon get() = Icons.Outlined.Snake
    override val order = 6

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::SnakeRun
        )
        val bodyPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onSwipe = run::steer,
                swipeOnDrag = true
            ) {
                drawSnakeScene(run, style, bodyPath)
            }
        }
    }
}

private fun DrawScope.drawSnakeScene(run: SnakeRun, style: ArcadeStyle, bodyPath: Path) {
    val side = minOf(run.width - 28f, run.height - run.topInset - 28f)
    val cell = side / SnakeCells
    val origin =
        Offset((run.width - side) / 2f, run.topInset + (run.height - run.topInset - side) / 2f)
    drawSnakeBackground(style, run.width, run.height)
    val shade = lerp(style.playerColor, style.playerDetailColor, 0.42f)
    val glint = lerp(style.playerColor, style.obstacleColor, 0.68f)
    drawRect(brush = style.background, topLeft = origin, size = Size(side, side))
    drawRect(style.overlayColor.copy(alpha = 0.18f), origin + Offset(0f, 3f), Size(side, side))
    drawRect(style.detailColor.copy(alpha = 0.05f), origin, Size(side, side))
    for (row in 0 until SnakeCells) for (column in 0 until SnakeCells) {
        pixelRect(
            style.obstacleColor.copy(alpha = 0.12f),
            origin.x + (column + 0.5f) * cell, origin.y + (row + 0.5f) * cell, 1f, 1f
        )
    }
    repeat(13) { index ->
        val point = origin + Offset(
            (index * 59 % 101 + 8) / 117f * side,
            (index * 73 % 107 + 8) / 123f * side
        )
        val ink = style.detailColor.copy(alpha = 0.08f)
        pixelRect(ink, point.x, point.y, 1f, 3f)
        pixelRect(ink, point.x - 2f, point.y + 1f, 1f, 2f)
        pixelRect(ink, point.x + 2f, point.y, 1f, 3f)
    }
    drawRect(style.obstacleColor.copy(alpha = 0.5f), origin, Size(side, side), style = Stroke(1f))
    for (x in listOf(origin.x - 2f, origin.x + side - 2f)) {
        for (y in listOf(origin.y - 2f, origin.y + side - 2f)) {
            pixelRect(style.obstacleColor, x, y, 4f, 4f)
            pixelRect(style.playerDetailColor, x + 1f, y + 1f, 2f, 2f)
        }
    }
    fun center(index: Int) =
        origin + Offset((index % SnakeCells + 0.5f) * cell, (index / SnakeCells + 0.5f) * cell)

    fun position(index: Int): Offset {
        val from = center(run.previous.getOrElse(index) { run.previous.last() })
        return from + (center(run.body[index]) - from) * run.progress
    }
    if (run.food >= 0) {
        val food = center(run.food)
        translate(left = food.x - cell * 0.5f, top = food.y - cell * 0.59f) {
            scale(scale = cell / 14f, pivot = Offset.Zero) {
                drawArcadeSprite(
                    SnakeFood,
                    lerp(style.accentColor, style.playerDetailColor, 0.4f),
                    1f,
                    1f
                )
                drawArcadeSprite(SnakeFood, style.accentColor, 0f, 0f)
                drawArcadeSprite(
                    SnakeFoodShade,
                    lerp(style.accentColor, style.playerDetailColor, 0.3f),
                    0f,
                    0f
                )
                drawArcadeSprite(SnakeLeaf, style.obstacleColor, 0f, 0f)
                pixelRect(lerp(style.accentColor, style.textColor, 0.55f), 3f, 6f, 2f, 3f)
            }
        }
    }
    val head = position(0)
    bodyPath.rewind()
    bodyPath.moveTo(head.x, head.y)
    for (index in 1 until run.body.lastIndex) {
        val point = position(index)
        bodyPath.lineTo(point.x, point.y)
    }
    translate(top = cell * 0.12f) {
        drawPath(
            bodyPath, style.playerDetailColor.copy(alpha = 0.45f),
            style = Stroke(cell * 0.96f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        )
    }
    drawPath(
        bodyPath,
        shade,
        style = Stroke(cell * 0.9f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    )
    drawPath(
        bodyPath,
        style.playerColor,
        style = Stroke(cell * 0.76f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    )
    translate(top = -cell * 0.17f) {
        drawPath(
            bodyPath,
            glint,
            style = Stroke(cell * 0.16f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
        )
    }
    for (index in 1 until run.body.lastIndex) {
        val point = position(index)
        pixelRect(shade, point.x - cell * 0.14f, point.y - cell * 0.05f, cell * 0.2f, cell * 0.2f)
        pixelRect(glint, point.x + cell * 0.08f, point.y - cell * 0.2f, cell * 0.14f, cell * 0.1f)
    }
    val tail = position(run.body.lastIndex)
    val tailBase = position(run.body.lastIndex - 1)
    for (step in 0..3) {
        val fraction = (step + 1) / 4f
        val point = tailBase + (tail - tailBase) * fraction
        val radius = cell * (0.42f - fraction * 0.31f)
        pixelCircle(shade, radius + cell * 0.04f, point)
        pixelCircle(style.playerColor, radius, point - Offset(0f, cell * 0.04f))
    }
    if (run.eatFlash > 0f) pixelCircle(
        style.accentColor.copy(alpha = run.eatFlash * 0.6f),
        cell * (0.6f + (1f - run.eatFlash) * 0.6f), head, style = Stroke(1f)
    )
    rotate(degrees = run.direction.angle, pivot = head) {
        translate(left = head.x - cell * 0.6f, top = head.y - cell * 0.56f) {
            scale(scale = cell / 16f, pivot = Offset.Zero) {
                drawArcadeSprite(SnakeHead, shade, 0f, 1f)
                drawArcadeSprite(SnakeHead, style.playerColor, 0f, 0f)
                drawArcadeSprite(SnakeHeadShade, shade, 0f, 0f)
                pixelRect(glint, 4f, 2f, 6f, 2f)
                drawArcadeSprite(SnakeEyes, style.textColor, 0f, 0f)
                pixelRect(style.playerDetailColor, 14f, 3f, 2f, 3f)
                pixelRect(style.playerDetailColor, 14f, 12f, 2f, 3f)
                pixelRect(shade, 18f, 8f, 2f, 2f)
                if (run.frame % 96 < 12 && run.phase == ArcadePhase.Running) {
                    pixelRect(style.hazardColor, 20f, 8f, 3f, 1f)
                    pixelRect(style.hazardColor, 23f, 7f, 2f, 1f)
                    pixelRect(style.hazardColor, 23f, 9f, 2f, 1f)
                }
            }
        }
    }
}

private const val SnakeCells = 20
private val SnakeHead = ArcadeSprite(
    width = 20, height = 18,
    data = "M3 1H12V0H16V2H18V4H20V14H18V16H15V18H3V16H1V13H0V5H1V3H3Z"
)
private val SnakeHeadShade = ArcadeSprite(
    width = 20, height = 18,
    data = "M18 5H20V14H18V16H15V18H3V16H15V14H18Z"
)
private val SnakeEyes = ArcadeSprite(
    width = 20, height = 18,
    data = "M11 2H16V7H11Z M11 11H16V16H11Z"
)
private val SnakeFood = ArcadeSprite(
    width = 14, height = 16,
    data = "M3 4H6V5H8V4H11V5H13V8H14V11H12V14H10V16H8V15H6V16H4V14H2V11H1V8H2V5H3Z"
)
private val SnakeFoodShade = ArcadeSprite(
    width = 14, height = 16,
    data = "M11 5H13V8H14V11H12V14H10V16H8V15H10V13H11V10H12V8H11Z"
)
private val SnakeLeaf = ArcadeSprite(
    width = 14,
    height = 16,
    data = "M7 2H9V5H7Z M9 0H13V2H11V3H9Z"
)

private fun DrawScope.drawSnakeBackground(style: ArcadeStyle, width: Float, height: Float) {
    val count = (width * height / 17000f).toInt().coerceAtLeast(6)
    repeat(count) { index ->
        val point =
            Offset((index + 0.25f) * width / count, height * (0.13f + (index * 43 % 71) / 100f))
        val ink = style.detailColor.copy(alpha = 0.12f)
        pixelRect(ink, point.x, point.y - 6f, 1f, 12f)
        pixelRect(ink, point.x - 4f, point.y - 4f, 4f, 2f)
        pixelRect(ink, point.x + 1f, point.y, 4f, 2f)
        pixelRect(ink.copy(alpha = 0.08f), point.x + 17f, point.y + 14f, 8f, 2f)
        pixelRect(ink.copy(alpha = 0.07f), point.x + 19f, point.y + 12f, 4f, 2f)
    }
}

@Serializable
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

@Serializable
private class SnakeRun : ArcadeRun() {
    val body = mutableListOf<Int>()
    val previous = mutableListOf<Int>()
    var food = 0
        private set
    var direction = SnakeDirection.Right
        private set
    var eatFlash = 0f
        private set
    val progress get() = (elapsed / interval).coerceIn(0f, 1f)
    private val interval get() = (0.16f / (1f + score * 0.035f)).coerceAtLeast(0.075f)
    private var elapsed = 0f
    private val turns: ArcadeDeque<SnakeDirection> = ArrayDeque()

    override fun isValid(): Boolean = super.isValid() &&
            body.isNotEmpty() && body.all { it in 0 until SnakeCells * SnakeCells } && previous.isNotEmpty()

    override fun reset() {
        body.clear()
        repeat(5) { body += SnakeCells * 10 + 8 - it }
        previous.clear()
        previous += body
        direction = SnakeDirection.Right
        food = SnakeCells * 10 + 14
        elapsed = 0f
        eatFlash = 0f
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
        eatFlash = (eatFlash - seconds * 4f).coerceAtLeast(0f)
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
            eatFlash = 1f
            score++
            food = (0 until SnakeCells * SnakeCells).filterNot { it in body }.randomOrNull() ?: -1
            if (food == -1) phase = ArcadePhase.Over
        } else {
            body.removeAt(body.lastIndex)
        }
    }
}