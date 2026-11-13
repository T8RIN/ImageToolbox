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
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SportsTennis
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object PongGame : ArcadeGameDefinition {
    override val title = R.string.arcade_pong
    override val description = R.string.arcade_pong_sub
    override val icon get() = Icons.Outlined.SportsTennis
    override val order = 4

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::PongRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.steer(it.y) },
                onDrag = { run.steer(it.y) }
            ) {
                drawPongCourt(style, run.width, run.height, run.top, run.bottom)
                val arena = style.detailColor.copy(alpha = 0.13f)
                for (side in 0..1) {
                    val edge = if (side == 0) 5f else run.width - 8f
                    var seat = run.top + 8f
                    while (seat < run.bottom - 6f) {
                        pixelRect(arena, edge, seat, 3f, 5f)
                        seat += 12f
                    }
                }
                for (y in listOf(run.top + 18f, run.bottom - 18f)) {
                    pixelRect(arena, 22f, y, run.width * 0.2f, 1f)
                    pixelRect(arena, run.width * 0.8f - 22f, y, run.width * 0.2f, 1f)
                }
                var y = run.top
                while (y < run.bottom) {
                    pixelRect(
                        color = style.detailColor,
                        x = run.width / 2 - 1,
                        y = y,
                        width = 2f,
                        height = minOf(8f, run.bottom - y)
                    )
                    y += 18f
                }
                pixelRect(
                    color = style.detailColor,
                    x = 0f,
                    y = run.top - 2,
                    width = run.width,
                    height = 1f
                )
                pixelRect(
                    color = style.detailColor,
                    x = 0f,
                    y = run.bottom + 1,
                    width = run.width,
                    height = 1f
                )
                pixelPanel(
                    style.playerColor,
                    style.playerDetailColor,
                    Offset(PaddleEdge - 7f, run.playerY - run.paddleHalf),
                    Size(7f, run.paddleHalf * 2f)
                )
                pixelPanel(
                    style.obstacleColor,
                    style.playerDetailColor,
                    Offset(run.width - PaddleEdge, run.rivalY - run.paddleHalf),
                    Size(7f, run.paddleHalf * 2f)
                )
                run.trail.forEachIndexed { index, point ->
                    val side = 3f + index * 0.8f
                    pixelRect(
                        color = style.accentColor.copy(alpha = (index + 1) * 0.05f),
                        x = point.x - side / 2,
                        y = point.y - side / 2,
                        width = side,
                        height = side
                    )
                }
                pixelCircle(style.accentColor, BallHalf, Offset(run.ballX, run.ballY))
                pixelRect(
                    style.playerDetailColor.copy(alpha = 0.45f),
                    run.ballX - 2f,
                    run.ballY - 3f,
                    2f,
                    2f
                )
            }
        }
    }
}

private const val BallHalf = 5f
private const val PaddleEdge = 22f

private fun DrawScope.drawPongCourt(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    top: Float,
    bottom: Float
) {
    val ink = style.detailColor.copy(alpha = 0.2f)
    var x = 0f
    while (x < width) {
        pixelRect(style.detailColor.copy(alpha = 0.025f), x, 0f, 17f, height)
        drawLine(ink.copy(alpha = 0.07f), Offset(x, 0f), Offset(x, height), 0.5f)
        x += 36f
    }
    val center = Offset(width / 2f, (top + bottom) / 2f)
    val radius = (bottom - top) * 0.23f
    drawCircle(ink, radius, center, style = Stroke(0.8f))
    drawCircle(ink.copy(alpha = 0.1f), radius * 0.12f, center)
    for (side in 0..1) {
        val edge = if (side == 0) 18f else width - 18f
        val direction = if (side == 0) 1f else -1f
        val area = Path().apply {
            moveTo(edge, center.y - radius * 1.1f)
            lineTo(edge + direction * radius, center.y - radius * 1.1f)
            lineTo(edge + direction * radius, center.y + radius * 1.1f)
            lineTo(edge, center.y + radius * 1.1f)
        }
        drawPath(area, ink, style = Stroke(0.8f))

    }
    for (row in 0..2) {
        var seat = 25f
        while (seat < width - 18f) {
            for (y in listOf(top - 8f - row * 5f, bottom + 7f + row * 5f)) {
                pixelRect(ink.copy(alpha = 0.14f), seat, y, 6f, 2f)
            }
            seat += 17f
        }
    }
}

@Serializable
private class PongRun : ArcadeRun() {
    val top get() = topInset + 12f
    val bottom get() = height - 18f
    val paddleHalf get() = ((bottom - top) * 0.12f).coerceIn(24f, 42f)
    var playerY = (top + bottom) / 2
        private set
    var rivalY = playerY
        private set
    var ballX = width / 2
        private set
    var ballY = playerY
        private set
    val trail: MutableList<ArcadeOffset> = mutableListOf()
    private var targetY = playerY
    private var rivalTarget = playerY
    private var velocityX = 0f
    private var velocityY = 0f
    private var serveDelay = 0f
    private var reaction = 0f
    private var trailTime = 0f
    private var rallies = 0

    override fun reset() {
        playerY = (top + bottom) / 2
        rivalY = playerY
        targetY = playerY
        rivalTarget = playerY
        reaction = 0f
        rallies = 0
        serve()
    }

    fun steer(y: Float) {
        targetY = y.coerceIn(top + paddleHalf, bottom - paddleHalf)
    }

    private fun serve() {
        ballX = width / 2
        ballY = (top + bottom) / 2
        velocityX = -(270f + rallies * 5f).coerceAtMost(620f)
        velocityY = Random.nextFloat() * 150f - 75f
        serveDelay = 0.8f
        trail.clear()
    }

    override fun advance(seconds: Float) {
        val motion = (targetY - playerY).coerceIn(-650f * seconds, 650f * seconds)
        playerY += motion
        reaction -= seconds
        val style = rallies / 8 % 3
        if (reaction <= 0f) {
            rivalTarget = if (velocityX > 0f) {
                val span = bottom - top - BallHalf * 2f
                val travel = (width - PaddleEdge - BallHalf - ballX) / velocityX
                val predicted = ballY + velocityY * travel - top - BallHalf
                val folded = ((predicted % (span * 2f)) + span * 2f) % (span * 2f)
                top + BallHalf + (if (folded < span) folded else span * 2f - folded) +
                        sin(rallies * 0.91f + ballX * 0.02f) * paddleHalf * if (style == 2) 0.38f else 0.18f
            } else (top + bottom) / 2f
            reaction =
                (0.2f - rallies * 0.0015f).coerceAtLeast(0.11f) + if (style == 2) 0.05f else 0f
        }
        val rivalSpeed = (205f + rallies * 2f).coerceAtMost(310f)
        rivalY =
            (rivalY + (rivalTarget - rivalY).coerceIn(-rivalSpeed * seconds, rivalSpeed * seconds))
            .coerceIn(top + paddleHalf, bottom - paddleHalf)
        if (serveDelay > 0f) {
            serveDelay -= seconds
            return
        }
        val oldX = ballX
        ballX += velocityX * seconds
        ballY += velocityY * seconds
        if (ballY < top + BallHalf) {
            ballY = 2 * (top + BallHalf) - ballY
            velocityY = abs(velocityY)
        } else if (ballY > bottom - BallHalf) {
            ballY = 2 * (bottom - BallHalf) - ballY
            velocityY = -abs(velocityY)
        }
        when {
            velocityX < 0 && oldX >= PaddleEdge + BallHalf && ballX <= PaddleEdge + BallHalf &&
                    abs(ballY - playerY) <= paddleHalf + BallHalf -> {
                ballX = PaddleEdge + BallHalf
                bounce(
                    paddleY = playerY,
                    direction = 1,
                    spin = motion / seconds.coerceAtLeast(0.0001f) * 0.16f
                )
                score++
                rallies++
            }

            velocityX > 0 && oldX <= width - PaddleEdge - BallHalf && ballX >= width - PaddleEdge - BallHalf &&
                    abs(ballY - rivalY) <= paddleHalf + BallHalf -> {
                ballX = width - PaddleEdge - BallHalf
                bounce(
                    paddleY = rivalY,
                    direction = -1,
                    spin = when (style) {
                        1 -> sin(rallies * 0.8f) * 95f
                        2 -> -velocityY * 0.3f
                        else -> 0f
                    }
                )
            }

            ballX < -BallHalf -> {
                phase = ArcadePhase.Over
            }

            ballX > width + BallHalf -> {
                score += 5
                serve()
            }
        }
        trailTime += seconds
        if (trailTime >= 0.025f) {
            trailTime = 0f
            trail += Offset(x = ballX, y = ballY)
            if (trail.size > 5) trail.removeAt(0)
        }
    }

    private fun bounce(
        paddleY: Float,
        direction: Int,
        spin: Float = 0f
    ) {
        velocityX = (abs(velocityX) + 18f).coerceAtMost(620f) * direction
        val offset = ((ballY - paddleY) / paddleHalf).coerceIn(-1f, 1f)
        velocityY = (offset * minOf(370f, abs(velocityX) * 0.85f) + spin).coerceIn(-420f, 420f)
        if (abs(velocityY) < 35f) velocityY = if (velocityY < 0) -35f else 35f
    }

    override fun onResize(previous: Size) {
        fun position(y: Float) =
            (y / previous.height * height).coerceIn(top + paddleHalf, bottom - paddleHalf)
        playerY = position(playerY)
        rivalY = position(rivalY)
        ballX = ballX / previous.width * width
        ballY = (ballY / previous.height * height).coerceIn(top + BallHalf, bottom - BallHalf)
        targetY = playerY
        rivalTarget = rivalY
        trail.clear()
    }
}