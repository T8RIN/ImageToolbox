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
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SportsTennis
import kotlinx.serialization.Serializable
import kotlin.math.abs
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
                onPress = { run.steer(it.y) },
                onDrag = { run.steer(it.y) }
            ) {
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
                pixelRect(
                    color = style.playerColor,
                    x = PaddleEdge - 7,
                    y = run.playerY - run.paddleHalf,
                    width = 7f,
                    height = run.paddleHalf * 2
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = run.width - PaddleEdge,
                    y = run.rivalY - run.paddleHalf,
                    width = 7f,
                    height = run.paddleHalf * 2
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
                pixelRect(
                    color = style.accentColor,
                    x = run.ballX - BallHalf,
                    y = run.ballY - BallHalf,
                    width = BallHalf * 2,
                    height = BallHalf * 2
                )
            }
        }
    }
}

private const val BallHalf = 5f
private const val PaddleEdge = 22f

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

    override fun reset() {
        playerY = (top + bottom) / 2
        rivalY = playerY
        targetY = playerY
        rivalTarget = playerY
        reaction = 0f
        serve()
    }

    fun steer(y: Float) {
        targetY = y.coerceIn(top + paddleHalf, bottom - paddleHalf)
    }

    private fun serve() {
        ballX = width / 2
        ballY = (top + bottom) / 2
        velocityX = -(270f + score * 5f).coerceAtMost(620f)
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
                bounce(
                    paddleY = playerY,
                    direction = 1
                )
                score++
            }

            velocityX > 0 && oldX <= width - PaddleEdge - BallHalf && ballX >= width - PaddleEdge - BallHalf &&
                    abs(ballY - rivalY) <= paddleHalf + BallHalf -> {
                ballX = width - PaddleEdge - BallHalf
                bounce(
                    paddleY = rivalY,
                    direction = -1
                )
            }

            ballX < -BallHalf -> {
                phase = ArcadePhase.Over
            }

            ballX > width + BallHalf -> {
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
        direction: Int
    ) {
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
    }
}