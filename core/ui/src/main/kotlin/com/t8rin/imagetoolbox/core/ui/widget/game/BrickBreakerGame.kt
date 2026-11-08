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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun BrickBreakerGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState = initialState, create = ::BrickBreakerRun)
    ArcadeBoard(
        game = ArcadeGame.BrickBreaker,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(run = run, onPress = run::steer, onDrag = run::steer) {
            arcadeStars(
                style = style,
                width = run.width,
                height = run.height,
                drift = 0f
            )
            for (brick in run.bricks) {
                val rect = run.bounds(brick)
                val color = when (brick.row % 3) {
                    0 -> style.accentColor
                    1 -> style.obstacleColor
                    else -> style.playerColor
                }
                drawRoundRect(
                    color = color.copy(alpha = 0.25f + brick.flash * 0.5f),
                    topLeft = rect.topLeft,
                    size = rect.size,
                    cornerRadius = CornerRadius(2f)
                )
                drawRoundRect(
                    color = color.copy(alpha = 0.8f),
                    topLeft = rect.topLeft,
                    size = rect.size,
                    cornerRadius = CornerRadius(2f),
                    style = Stroke(1f)
                )
                repeat(brick.health - 1) { notch ->
                    pixelRect(
                        color = color,
                        x = rect.left + 5f + notch * 5f,
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
            drawRoundRect(
                color = style.playerColor,
                topLeft = paddle.topLeft,
                size = paddle.size,
                cornerRadius = CornerRadius(3f)
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
            run.trail.forEachIndexed { index, point ->
                drawCircle(
                    color = style.playerColor.copy(alpha = (index + 1) * 0.035f),
                    radius = run.ballRadius * 0.7f,
                    center = point
                )
            }
            drawCircle(
                color = style.playerColor,
                radius = run.ballRadius,
                center = Offset(run.ballX, run.ballY)
            )
            drawCircle(
                color = style.playerDetailColor.copy(alpha = 0.8f),
                radius = 1.5f,
                center = Offset(run.ballX - 1f, run.ballY - 1.5f)
            )
            for (gift in run.gifts) {
                drawRoundRect(
                    color = style.accentColor,
                    topLeft = Offset(gift.x - 9f, gift.y - 6f),
                    size = Size(18f, 12f),
                    cornerRadius = CornerRadius(3f)
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = gift.x - 4f,
                    y = gift.y - 1f,
                    width = 8f,
                    height = 2f
                )
                if (gift.kind == 1) pixelRect(
                    color = style.playerDetailColor,
                    x = gift.x - 1f,
                    y = gift.y - 4f,
                    width = 2f,
                    height = 8f
                )
                else for (side in listOf(-1f, 1f)) pixelRect(
                    color = style.playerDetailColor,
                    x = gift.x + side * 4f - 1f,
                    y = gift.y - 3f,
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

internal data class BreakerBrick(
    val column: Int,
    val row: Int,
    var health: Int,
    var flash: Float = 0f
)

internal data class BreakerGift(
    var x: Float,
    var y: Float,
    val kind: Int
)

internal class BrickBreakerRun : ArcadeRun() {
    val bricks = mutableListOf<BreakerBrick>()
    val gifts = mutableListOf<BreakerGift>()
    val trail = mutableListOf<Offset>()
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
    var wideTime = 0f
    var extension = 0f
    private var targetX = paddleX
    private var serve = 0.8f
    private var transition = 0f
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
        extension = 0f
        gifts.clear()
        broken = 0
        effects.clear()
        paddleX = width / 2f
        targetX = paddleX
        newWave()
    }

    private fun newWave() {
        bricks.clear()
        val rows = (4 + round / 4).coerceAtMost(5)
        for (row in 0 until rows) for (column in 0..7) {
            if (round > 1 && (column + row * 2 + round) % 7 == 0) continue
            val health = if (row < 2) (1 + round / 3).coerceAtMost(3) else 1
            bricks += BreakerBrick(column, row, health)
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
                if (gift.kind == 0) wideTime = 10f
                else if (lives < 3) lives++ else score += 25
                effects.burst(
                    point = Offset(gift.x, paddleY),
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
        val oldX = ballX
        val oldY = ballY
        ballX += vx * seconds
        ballY += vy * seconds
        if (ballX < 16f + ballRadius || ballX > width - 16f - ballRadius) {
            ballX = ballX.coerceIn(16f + ballRadius, width - 16f - ballRadius)
            vx = if (ballX < width / 2f) abs(vx) else -abs(vx)
        }
        if (ballY < topInset + ballRadius) {
            ballY = topInset + ballRadius
            vy = abs(vy)
        }
        if (vy > 0f && oldY + ballRadius <= paddleY && ballY + ballRadius >= paddleY) {
            val fraction = (paddleY - ballRadius - oldY) / (ballY - oldY)
            val impactX = oldX + (ballX - oldX) * fraction
            val impactPaddleX = oldPaddleX + (paddleX - oldPaddleX) * fraction
            if (abs(impactX - impactPaddleX) <= paddleHalf + ballRadius) {
                ballY = paddleY - ballRadius
                vx = ((impactX - impactPaddleX) / paddleHalf).coerceIn(-1f, 1f) * speed * 0.85f
                if (abs(vx) < speed * 0.14f) vx = speed * 0.14f * if (vx < 0f) -1f else 1f
                vy = -sqrt(speed * speed - vx * vx)
            }
        }
        val iterator = bricks.iterator()
        while (iterator.hasNext()) {
            val brick = iterator.next()
            val rect = bounds(brick)
            val dx = ballX - ballX.coerceIn(rect.left, rect.right)
            val dy = ballY - ballY.coerceIn(rect.top, rect.bottom)
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
            ballX += nx * (ballRadius - distance + 0.1f)
            ballY += ny * (ballRadius - distance + 0.1f)
            val dot = vx * nx + vy * ny
            if (dot < 0f) {
                vx -= 2f * dot * nx
                vy -= 2f * dot * ny
            }
            if (abs(vy) < speed * 0.25f) {
                vy = speed * 0.25f * if (vy < 0f) -1f else 1f
                vx = sqrt(speed * speed - vy * vy) * if (vx < 0f) -1f else 1f
            }
            brick.health--
            brick.flash = 1f
            score += 5
            if (brick.health == 0) {
                score += 10
                broken++
                effects.burst(
                    point = rect.center,
                    count = 8
                )
                if (broken % 9 == 0 && gifts.size < 3) gifts += BreakerGift(
                    rect.center.x,
                    rect.center.y,
                    if (broken % 18 == 0) 1 else 0
                )
                iterator.remove()
            }
            break
        }
        if (bricks.isEmpty()) {
            score += round * 50
            transition = 0.9f
        } else if (ballY > height + ballRadius) {
            lives--
            if (lives <= 0) phase = ArcadePhase.Over else serveBall()
        }
        trailTime += seconds
        if (trailTime > 0.025f) {
            trailTime = 0f
            trail += Offset(ballX, ballY)
            if (trail.size > 6) trail.removeAt(0)
        }
    }

    override fun onResize(previous: Size) {
        paddleX =
            (paddleX / previous.width * width).coerceIn(16f + paddleHalf, width - 16f - paddleHalf)
        targetX = paddleX
        ballX =
            (ballX / previous.width * width).coerceIn(16f + ballRadius, width - 16f - ballRadius)
        ballY =
            (ballY / previous.height * height).coerceIn(topInset + ballRadius, paddleY - ballRadius)
        if (bricks.any { bounds(it).inflate(ballRadius).contains(Offset(ballX, ballY)) }) {
            ballY =
                (bricks.maxOf { bounds(it).bottom } + ballRadius + 1f).coerceAtMost(paddleY - ballRadius)
            vy = abs(vy)
        }
        val length = sqrt(vx * vx + vy * vy)
        if (length > 0f) {
            vx = vx / length * speed
            vy = vy / length * speed
        }
        gifts.forEach {
            it.x *= width / previous.width
            it.y *= height / previous.height
        }
        trail.clear()
    }

    override fun save(state: Bundle) {
        state.putFloat("extension", extension)
        state.putFloatArray(
            "run",
            floatArrayOf(
                paddleX,
                ballX,
                ballY,
                vx,
                vy,
                wideTime,
                targetX,
                serve,
                transition,
                trailTime
            )
        )
        state.putIntArray("round", intArrayOf(round, lives, broken))
        state.putFloatArray(
            "bricks",
            bricks.flatMap {
                listOf(
                    it.column.toFloat(),
                    it.row.toFloat(),
                    it.health.toFloat(),
                    it.flash
                )
            }.toFloatArray()
        )
        state.putFloatArray(
            "gifts",
            gifts.flatMap { listOf(it.x, it.y, it.kind.toFloat()) }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        extension = state.getFloat("extension")
        state.getFloatArray("run")?.let {
            paddleX = it[0]
            ballX = it[1]
            ballY = it[2]
            vx = it[3]
            vy = it[4]
            wideTime = it[5]
            targetX = it[6]
            serve = it[7]
            transition = it[8]
            trailTime = it[9]
        }
        state.getIntArray("round")?.let {
            round = it[0]
            lives = it[1]
            broken = it[2]
        }
        bricks.clear()
        state.getFloatArray("bricks")?.let {
            for (index in it.indices step 4) bricks += BreakerBrick(
                it[index].toInt(),
                it[index + 1].toInt(),
                it[index + 2].toInt(),
                it[index + 3]
            )
        }
        gifts.clear()
        state.getFloatArray("gifts")?.let {
            for (index in it.indices step 3) gifts += BreakerGift(
                it[index],
                it[index + 1],
                it[index + 2].toInt()
            )
        }
    }
}