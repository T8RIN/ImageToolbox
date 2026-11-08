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
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.random.Random

private const val BallHalf = 5f
private const val PaddleEdge = 22f

@Composable
fun PongGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::PongRun)
    ArcadeBoard(
        ArcadeGame.Pong, run, style, modifier, initialState, onHighScoreChanged, onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = { run.steer(it.y) },
            onDrag = { run.steer(it.y) }
        ) {
            var y = run.top
            while (y < run.bottom) {
                pixelRect(style.detailColor, run.width / 2 - 1, y, 2f, minOf(8f, run.bottom - y))
                y += 18f
            }
            pixelRect(style.detailColor, 0f, run.top - 2, run.width, 1f)
            pixelRect(style.detailColor, 0f, run.bottom + 1, run.width, 1f)
            pixelRect(
                style.playerColor,
                PaddleEdge - 7,
                run.playerY - run.paddleHalf,
                7f,
                run.paddleHalf * 2
            )
            pixelRect(
                style.obstacleColor,
                run.width - PaddleEdge,
                run.rivalY - run.paddleHalf,
                7f,
                run.paddleHalf * 2
            )
            run.trail.forEachIndexed { index, point ->
                val side = 3f + index * 0.8f
                pixelRect(
                    style.accentColor.copy(alpha = (index + 1) * 0.05f),
                    point.x - side / 2,
                    point.y - side / 2,
                    side,
                    side
                )
            }
            pixelRect(
                style.accentColor,
                run.ballX - BallHalf,
                run.ballY - BallHalf,
                BallHalf * 2,
                BallHalf * 2
            )
        }
    }
}

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
    val trail = mutableListOf<Offset>()
    private var targetY = playerY
    private var rivalTarget = playerY
    private var velocityX = 0f
    private var velocityY = 0f
    private var serveDelay = 0f
    private var reaction = 0f
    private var trailTime = 0f

    override fun reset() {
        playerY = (top + bottom) / 2
        rivalY = playerY
        targetY = playerY
        rivalTarget = playerY
        reaction = 0f
        serve(-1)
    }

    fun steer(y: Float) {
        targetY = y.coerceIn(top + paddleHalf, bottom - paddleHalf)
    }

    private fun serve(direction: Int) {
        ballX = width / 2
        ballY = (top + bottom) / 2
        velocityX = (270f + score * 5f).coerceAtMost(620f) * direction
        velocityY = Random.nextFloat() * 150f - 75f
        serveDelay = 0.8f
        trail.clear()
    }

    override fun advance(seconds: Float) {
        playerY += (targetY - playerY).coerceIn(-650f * seconds, 650f * seconds)
        reaction -= seconds
        if (reaction <= 0f) {
            rivalTarget = if (velocityX > 0) ballY else (top + bottom) / 2
            reaction = 0.16f
        }
        rivalY = (rivalY + (rivalTarget - rivalY).coerceIn(-205f * seconds, 205f * seconds))
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
                bounce(playerY, 1)
                score++
            }

            velocityX > 0 && oldX <= width - PaddleEdge - BallHalf && ballX >= width - PaddleEdge - BallHalf &&
                    abs(ballY - rivalY) <= paddleHalf + BallHalf -> {
                ballX = width - PaddleEdge - BallHalf
                bounce(rivalY, -1)
            }

            ballX < -BallHalf -> {
                phase = ArcadePhase.Over
            }

            ballX > width + BallHalf -> {
                serve(-1)
            }
        }
        trailTime += seconds
        if (trailTime >= 0.025f) {
            trailTime = 0f
            trail += Offset(ballX, ballY)
            if (trail.size > 5) trail.removeAt(0)
        }
    }

    private fun bounce(paddleY: Float, direction: Int) {
        velocityX = (abs(velocityX) + 18f).coerceAtMost(620f) * direction
        val offset = ((ballY - paddleY) / paddleHalf).coerceIn(-1f, 1f)
        velocityY = offset * minOf(370f, abs(velocityX) * 0.85f)
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
        if (phase == ArcadePhase.Ready) reset()
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "court", floatArrayOf(
                playerY,
                rivalY,
                ballX,
                ballY,
                targetY,
                velocityX,
                velocityY,
                serveDelay,
                rivalTarget,
                reaction
            )
        )
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("court")?.let {
            playerY = it[0]
            rivalY = it[1]
            ballX = it[2]
            ballY = it[3]
            targetY = it[4]
            velocityX = it[5]
            velocityY = it[6]
            serveDelay = it[7]
            rivalTarget = it[8]
            reaction = it[9]
        }
    }
}