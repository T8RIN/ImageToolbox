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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ViewComfy
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object BrickBreakerGame : ArcadeGameDefinition {
    override val title = R.string.arcade_brickbreaker
    override val description = R.string.arcade_brickbreaker_sub
    override val icon get() = Icons.Outlined.ViewComfy
    override val order = 28

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BrickBreakerRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onPress = run::steer,
                onDrag = run::steer
            ) {
                drawBrickBreakerBackground(style, run.width, run.height)
                for (brick in run.bricks) {
                    val rect = run.bounds(brick)
                    val color = when (brick.row % 3) {
                        0 -> style.accentColor
                        1 -> style.obstacleColor
                        else -> style.playerColor
                    }
                    pixelPanel(
                        color = color.copy(alpha = 0.85f + brick.flash.coerceAtMost(0.3f) * 0.5f),
                        ink = style.playerDetailColor,
                        topLeft = rect.topLeft,
                        size = rect.size
                    )
                    repeat(brick.health - 1) { notch ->
                        pixelRect(
                            color = style.playerDetailColor,
                            x = rect.left + 7f + notch * 5f,
                            y = rect.top + 4f,
                            width = 2f,
                            height = rect.height - 8f
                        )
                    }
                }
                val paddle = Rect(
                    run.paddleX - run.paddleHalf,
                    run.paddleY,
                    run.paddleX + run.paddleHalf,
                    run.paddleY + 7f
                )
                pixelPanel(
                    color = style.playerColor,
                    ink = style.playerDetailColor,
                    topLeft = paddle.topLeft,
                    size = paddle.size
                )
                pixelRect(
                    color = style.playerDetailColor.copy(alpha = 0.45f),
                    x = paddle.left + 7f,
                    y = paddle.top + 2f,
                    width = paddle.width - 14f,
                    height = 1f
                )
                if (run.extension > 0f && (run.wideTime > 2f || (run.wideTime * 6f).toInt() % 2 == 0)) {
                    pixelRect(
                        color = style.accentColor,
                        x = paddle.left + 2f,
                        y = paddle.top + 1f,
                        width = 4f,
                        height = 5f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = paddle.right - 6f,
                        y = paddle.top + 1f,
                        width = 4f,
                        height = 5f
                    )
                }
                if (run.laserTime > 0f) {
                    for (side in listOf(-1f, 1f)) {
                        val x = run.paddleX + side * (run.paddleHalf - 8f)
                        pixelRect(style.accentColor, x - 3f, paddle.top - 5f, 6f, 6f)
                        pixelRect(style.playerDetailColor, x - 1f, paddle.top - 7f, 2f, 4f)
                    }
                }
                for ((x, y) in run.lasers) {
                    pixelRect(style.accentColor.copy(alpha = 0.2f), x - 3f, y - 5f, 6f, 14f)
                    pixelRect(style.accentColor, x - 1f, y - 5f, 2f, 10f)
                    pixelRect(style.playerDetailColor, x - 1f, y - 5f, 2f, 3f)
                }
                run.trail.forEachIndexed { index, point ->
                    pixelCircle(
                        color = style.playerColor.copy(alpha = (index + 1) * 0.035f),
                        radius = run.ballRadius * 0.7f,
                        center = point
                    )
                }
                pixelCircle(
                    color = style.playerColor,
                    radius = run.ballRadius,
                    center = Offset(x = run.ballX, y = run.ballY)
                )
                pixelCircle(
                    color = style.playerDetailColor.copy(alpha = 0.8f),
                    radius = 1.5f,
                    center = Offset(x = run.ballX - 1f, y = run.ballY - 1.5f)
                )
                for (ball in run.extraBalls) {
                    pixelCircle(style.accentColor, run.ballRadius, Offset(ball.x, ball.y))
                    pixelCircle(style.playerDetailColor, 1.5f, Offset(ball.x - 1f, ball.y - 1.5f))
                }
                for ((x, y, kind) in run.gifts) {
                    pixelPanel(
                        color = style.accentColor,
                        ink = style.playerDetailColor,
                        topLeft = Offset(x = x - 9f, y = y - 6f),
                        size = Size(width = 18f, height = 12f)
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = x - 4f,
                        y = y - 1f,
                        width = 8f,
                        height = 2f
                    )
                    if (kind == 1) pixelRect(
                        color = style.playerDetailColor,
                        x = x - 1f,
                        y = y - 4f,
                        width = 2f,
                        height = 8f
                    )
                    else if (kind == 3) for (side in -1..1) pixelCircle(
                        style.playerDetailColor, 1.7f, Offset(x + side * 5f, y)
                    )
                    else if (kind == 2) for (side in listOf(-1f, 1f)) {
                        pixelRect(style.playerDetailColor, x + side * 4f - 1f, y - 4f, 2f, 8f)
                        pixelRect(style.playerDetailColor, x + side * 4f - 2f, y - 4f, 4f, 2f)
                    }
                    else for (side in listOf(-1f, 1f)) pixelRect(
                        color = style.playerDetailColor,
                        x = x + side * 4f - 1f,
                        y = y - 3f,
                        width = 2f,
                        height = 6f
                    )
                }
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
                arcadeLives(
                    style = style,
                    remaining = run.lives,
                    y = run.topInset / 2f
                )
            }
        }
    }
}

private fun DrawScope.drawBrickBreakerBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 183f).toInt()
    val rows = ceil(height / 139f).toInt()
    val ink = style.detailColor.copy(alpha = 0.11f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val point = Offset(column * 183f + 12f, row * 139f + 9f)
        val panel = Path().apply {
            moveTo(point.x + 12f, point.y)
            lineTo(point.x + 145f, point.y)
            lineTo(point.x + 157f, point.y + 12f)
            lineTo(point.x + 157f, point.y + 106f)
            lineTo(point.x + 145f, point.y + 118f)
            lineTo(point.x + 12f, point.y + 118f)
            lineTo(point.x, point.y + 106f)
            lineTo(point.x, point.y + 12f)
            close()
        }
        drawPath(panel, ink.copy(alpha = 0.018f))
        drawPath(panel, ink, style = Stroke(0.7f))
        for (slot in 0..2) pixelRect(ink, point.x + 17f + slot * 7f, point.y + 105f, 3f, 1f)
        pixelRect(ink.copy(alpha = 0.14f), point.x + 145f, point.y + 12f, 2f, 2f)
    }
}

@Serializable
private data class BreakerBrick(
    val column: Int,
    val row: Int,
    var health: Int,
    var flash: Float = 0f
)

@Serializable
private data class BreakerGift(
    var x: Float,
    var y: Float,
    val kind: Int
)

@Serializable
private data class BreakerLaser(val x: Float, var y: Float)

@Serializable
private data class BreakerBall(var x: Float, var y: Float, var vx: Float, var vy: Float)

@Serializable
private class BrickBreakerRun : ArcadeRun() {
    val bricks = mutableListOf<BreakerBrick>()
    val gifts = mutableListOf<BreakerGift>()
    val extraBalls = mutableListOf<BreakerBall>()
    val lasers = mutableListOf<BreakerLaser>()
    val trail: MutableList<ArcadeOffset> = mutableListOf()

    @Transient
    val effects = ArcadeEffects()
    val ballRadius = 5f
    val paddleY get() = height - 26f
    val paddleHalf get() = ((39f - round * 0.6f).coerceAtLeast(26f)) * (1f + extension * 0.4f)
    val brickWidth get() = (width - 48f) / 8f
    val brickHeight get() = minOf(25f, (height - topInset - 80f) / 5.5f)
    val speed
        get() = ((height - topInset - 40f) * 0.65f + 90f).coerceIn(
            165f,
            400f
        ) + round.coerceAtMost(15) * 7f
    var paddleX = width / 2f
    var ballX = paddleX
    var ballY = paddleY - 10f
    var vx = 0f
    var vy = 0f
    var lives = 3
    var round = 1
    var laserTime = 0f
        private set
    private var laserIn = 0f
    var wideTime = 0f
    var extension = 0f
    private var targetX = paddleX
    private var serve = 0.8f
    var transition = 0f
        private set
    private var broken = 0
    private var trailTime = 0f

    fun bounds(brick: BreakerBrick) = Rect(
        24f + brick.column * brickWidth + 2f,
        topInset + 14f + brick.row * brickHeight,
        24f + (brick.column + 1) * brickWidth - 2f,
        topInset + 14f + (brick.row + 1) * brickHeight - 4f
    )

    override fun reset() {
        round = 1
        lives = 3
        wideTime = 0f
        laserTime = 0f
        laserIn = 0f
        lasers.clear()
        extension = 0f
        gifts.clear()
        broken = 0
        effects.clear()
        paddleX = width / 2f
        targetX = paddleX
        newWave()
    }

    private fun newWave() {
        animateLevel()
        bricks.clear()
        lasers.clear()
        extraBalls.clear()
        val rows = (4 + round / 4).coerceAtMost(5)
        val pattern = if (round <= 2) 0 else 1 + (round - 3) % 6
        for (row in 0 until rows) for (column in 0..7) {
            val filled = when (pattern) {
                1 -> row in abs(column - 3)..abs(column - 3) + 1
                2 -> abs(column - 3.5f) + abs(row - 2f) * 1.4f < 4f
                3 -> column in 1..2 || column in 5..6 || row == 0
                4 -> (column + row) % 2 == 0 || row == 0
                5 -> row >= 2 || column % 3 == 0
                6 -> (column + round / 7) % 4 != row % 4
                else -> round == 1 || (column + row * 2 + round) % 7 != 0
            }
            if (!filled) continue
            val health = if (row < 2) (1 + round / 3).coerceAtMost(3) else 1
            bricks += BreakerBrick(column = column, row = row, health = health)
        }
        transition = 0f
        serveBall()
    }

    private fun serveBall() {
        ballX = paddleX
        ballY = paddleY - ballRadius - 2f
        vx = speed * if (Random.nextBoolean()) 0.35f else -0.35f
        vy = -sqrt(speed * speed - vx * vx)
        serve = 0.8f
        trail.clear()
    }

    fun steer(point: Offset) {
        targetX = point.x.coerceIn(16f + paddleHalf, width - 16f - paddleHalf)
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        wideTime = (wideTime - seconds).coerceAtLeast(0f)
        laserTime = (laserTime - seconds).coerceAtLeast(0f)
        extension += ((if (wideTime > 0f) 1f else 0f) - extension).coerceIn(
            -seconds * 5f,
            seconds * 5f
        )
        targetX = targetX.coerceIn(16f + paddleHalf, width - 16f - paddleHalf)
        val oldPaddleX = paddleX
        paddleX = (paddleX + (targetX - paddleX).coerceIn(-1000f * seconds, 1000f * seconds))
            .coerceIn(16f + paddleHalf, width - 16f - paddleHalf)
        bricks.forEach { it.flash = (it.flash - seconds * 5f).coerceAtLeast(0f) }
        val drops = gifts.iterator()
        while (drops.hasNext()) {
            val gift = drops.next()
            gift.y += 70f * seconds
            if (gift.y + 6f >= paddleY && gift.y - 6f <= paddleY + 7f && abs(gift.x - paddleX) < paddleHalf + 9f) {
                when (gift.kind) {
                    0 -> wideTime = 10f
                    3 -> splitBall()
                    2 -> {
                        laserTime = 8f
                        laserIn = 0f
                    }

                    else -> if (lives < 3) lives++ else score += 25
                }
                effects.burst(
                    point = Offset(x = gift.x, y = paddleY),
                    count = 12
                )
                drops.remove()
            } else if (gift.y > height + 10f) drops.remove()
        }
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newWave()
            }
            return
        }
        if (serve > 0f) {
            serve -= seconds
            ballX = paddleX
            ballY = paddleY - ballRadius - 2f
            return
        }
        laserIn -= seconds
        if (laserTime > 0f && laserIn <= 0f) {
            for (side in listOf(-1f, 1f)) lasers += BreakerLaser(
                paddleX + side * (paddleHalf - 8f), paddleY - 8f
            )
            laserIn = 0.42f
        }
        val beams = lasers.iterator()
        while (beams.hasNext()) {
            val laser = beams.next()
            val old = laser.y
            laser.y -= maxOf(360f, (height - topInset) * 1.5f) * seconds
            val brick = bricks.filter {
                val rect = bounds(it)
                laser.x in rect.left - 1f..rect.right + 1f &&
                        laser.y - 5f <= rect.bottom && old + 5f >= rect.top
            }.maxByOrNull { bounds(it).bottom }
            if (brick != null) {
                if (hitBrick(brick)) bricks.remove(brick)
                beams.remove()
            } else if (laser.y < topInset - 10f) beams.remove()
        }
        if (bricks.isEmpty()) {
            score += round * 50
            transition = 0.9f
            return
        }
        val primary = BreakerBall(ballX, ballY, vx, vy)
        val moving = mutableListOf(primary).apply { addAll(extraBalls) }
        moving.removeAll { ball ->
            advanceBall(ball, seconds, oldPaddleX)
            ball.y > height + ballRadius
        }
        val survived = moving.isNotEmpty()
        extraBalls.clear()
        if (survived) {
            val ball = moving.removeAt(0)
            ballX = ball.x
            ballY = ball.y
            vx = ball.vx
            vy = ball.vy
            extraBalls.addAll(moving)
        }
        if (bricks.isEmpty()) {
            score += round * 50
            transition = 0.9f
        } else if (!survived) {
            laserTime = 0f
            lasers.clear()
            lives--
            if (lives <= 0) phase = ArcadePhase.Over else serveBall()
        }
        trailTime += seconds
        if (trailTime > 0.025f) {
            trailTime = 0f
            trail += Offset(x = ballX, y = ballY)
            if (trail.size > 6) trail.removeAt(0)
        }
    }

    private fun splitBall() {
        if (extraBalls.isNotEmpty()) return
        for (side in listOf(-1f, 1f)) {
            val angle = side * 0.32f
            extraBalls += BreakerBall(
                ballX, ballY,
                vx * cos(angle) - vy * sin(angle), vx * sin(angle) + vy * cos(angle)
            )
        }
    }

    private fun advanceBall(ball: BreakerBall, seconds: Float, oldPaddleX: Float) {
        val oldX = ball.x
        val oldY = ball.y
        ball.x += ball.vx * seconds
        ball.y += ball.vy * seconds
        if (ball.x < 16f + ballRadius || ball.x > width - 16f - ballRadius) {
            ball.x = ball.x.coerceIn(16f + ballRadius, width - 16f - ballRadius)
            ball.vx = if (ball.x < width / 2f) abs(ball.vx) else -abs(ball.vx)
        }
        if (ball.y < topInset + ballRadius) {
            ball.y = topInset + ballRadius
            ball.vy = abs(ball.vy)
        }
        if (ball.vy > 0f && oldY + ballRadius <= paddleY && ball.y + ballRadius >= paddleY) {
            val fraction = (paddleY - ballRadius - oldY) / (ball.y - oldY)
            val impactX = oldX + (ball.x - oldX) * fraction
            val impactPaddleX = oldPaddleX + (paddleX - oldPaddleX) * fraction
            if (abs(impactX - impactPaddleX) <= paddleHalf + ballRadius) {
                ball.y = paddleY - ballRadius
                ball.vx = ((impactX - impactPaddleX) / paddleHalf).coerceIn(-1f, 1f) * speed * 0.85f
                if (abs(ball.vx) < speed * 0.14f) ball.vx =
                    speed * 0.14f * if (ball.vx < 0f) -1f else 1f
                ball.vy = -sqrt(speed * speed - ball.vx * ball.vx)
            }
        }
        val iterator = bricks.iterator()
        while (iterator.hasNext()) {
            val brick = iterator.next()
            val rect = bounds(brick)
            val dx = ball.x - ball.x.coerceIn(rect.left, rect.right)
            val dy = ball.y - ball.y.coerceIn(rect.top, rect.bottom)
            val distance = sqrt(dx * dx + dy * dy)
            if (distance >= ballRadius) continue
            val nx: Float
            val ny: Float
            if (distance > 0.001f) {
                nx = dx / distance
                ny = dy / distance
            } else {
                nx = if (oldX < rect.left) -1f else if (oldX > rect.right) 1f else 0f
                ny = if (nx == 0f) if (oldY < rect.center.y) -1f else 1f else 0f
            }
            ball.x += nx * (ballRadius - distance + 0.1f)
            ball.y += ny * (ballRadius - distance + 0.1f)
            val dot = ball.vx * nx + ball.vy * ny
            if (dot < 0f) {
                ball.vx -= 2f * dot * nx
                ball.vy -= 2f * dot * ny
            }
            if (abs(ball.vy) < speed * 0.25f) {
                ball.vy = speed * 0.25f * if (ball.vy < 0f) -1f else 1f
                ball.vx = sqrt(speed * speed - ball.vy * ball.vy) * if (ball.vx < 0f) -1f else 1f
            }
            if (hitBrick(brick)) iterator.remove()
            break
        }
    }

    private fun hitBrick(brick: BreakerBrick): Boolean {
        brick.health--
        brick.flash = 1f
        score += 5
        if (brick.health > 0) return false
        score += 10
        broken++
        val center = bounds(brick).center
        effects.burst(point = center, count = 8)
        if (broken % 9 == 0 && gifts.size < 3) gifts += BreakerGift(
            x = center.x,
            y = center.y,
            kind = when {
                broken % 36 == 0 -> 3
                broken % 27 == 0 -> 2
                broken % 18 == 0 -> 1
                else -> 0
            }
        )
        return true
    }

    override fun onResize(previous: Size) {
        paddleX =
            (paddleX / previous.width * width).coerceIn(16f + paddleHalf, width - 16f - paddleHalf)
        targetX = paddleX
        ballX =
            (ballX / previous.width * width).coerceIn(16f + ballRadius, width - 16f - ballRadius)
        ballY =
            (ballY / previous.height * height).coerceIn(topInset + ballRadius, paddleY - ballRadius)
        if (bricks.any { bounds(it).inflate(ballRadius).contains(Offset(x = ballX, y = ballY)) }) {
            ballY =
                (bricks.maxOf { bounds(it).bottom } + ballRadius + 1f).coerceAtMost(paddleY - ballRadius)
            vy = abs(vy)
        }
        val length = sqrt(vx * vx + vy * vy)
        if (length > 0f) {
            vx = vx / length * speed
            vy = vy / length * speed
        }
        extraBalls.forEach {
            it.x *= width / previous.width
            it.y *= height / previous.height
            val length = sqrt(it.vx * it.vx + it.vy * it.vy).coerceAtLeast(1f)
            it.vx = it.vx / length * speed
            it.vy = it.vy / length * speed
        }
        gifts.forEach {
            it.x *= width / previous.width
            it.y *= height / previous.height
        }
        trail.clear()
        lasers.clear()
    }
}