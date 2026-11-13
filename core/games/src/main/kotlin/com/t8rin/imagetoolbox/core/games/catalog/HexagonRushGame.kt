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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Hexagon
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object HexagonRushGame : ArcadeGameDefinition {
    override val title = R.string.arcade_hexagonrush
    override val description = R.string.arcade_hexagonrush_sub
    override val icon get() = Icons.Outlined.Hexagon
    override val order = 19

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::HexagonRushRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::steer,
                onDrag = run::steer,
                onRelease = run::stopInput
            ) {
                val center = run.center
                val extent = run.extent
                val path = run.outline
                fun point(
                    angle: Float,
                    radius: Float
                ) =
                    center + Offset(x = cos(angle), y = sin(angle)) * radius * extent

                val backdropRadius = maxOf(run.width, run.height) * 2f / extent
                for (sector in 0..5) {
                    val a = sector * PI.toFloat() / 3f + run.rotation * 0.45f
                    val b = a + PI.toFloat() / 3f
                    path.reset()
                    path.moveTo(center.x, center.y)
                    val first = point(
                        angle = a,
                        radius = backdropRadius
                    )
                    val second = point(
                        angle = b,
                        radius = backdropRadius
                    )
                    path.lineTo(first.x, first.y)
                    path.lineTo(second.x, second.y)
                    path.close()
                    drawPath(
                        path = path,
                        color = if (sector % 2 == 0) style.playerColor.copy(alpha = 0.065f)
                        else style.obstacleColor.copy(alpha = 0.08f)
                    )
                }
                for (sector in 0..5) {
                    val angle = sector * PI.toFloat() / 3f + run.rotation * 0.45f
                    val edge = point(angle, 1.65f)
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.14f),
                        start = point(angle - 0.035f, 1.65f),
                        end = edge,
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.14f),
                        start = edge,
                        end = point(angle, 1.8f),
                        strokeWidth = 1f
                    )
                }
                repeat(24) { index ->
                    val x = ((index * 71 + index * index * 19 + 23) % 359) / 359f * run.width
                    val y = ((index * 97 + index * index * 31 + 11) % 443) / 443f * run.height
                    if ((Offset(x = x, y = y) - center).getDistance() > extent * 0.6f) {
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.18f),
                            x = x,
                            y = y,
                            width = 2f,
                            height = 2f
                        )
                    }
                }
                pixelCircle(
                    color = style.detailColor.copy(alpha = 0.35f),
                    radius = extent * 0.46f,
                    center = center,
                    style = Stroke(width = 1f)
                )
                for (wall in run.walls) {
                    val (radius, gap, passed) = wall
                    for (side in 0..5) {
                        if ((side - gap + 6) % 6 < wall.opening) continue
                        val a = side * PI.toFloat() / 3f + run.rotation
                        val b = a + PI.toFloat() / 3f
                        val first = point(
                            angle = a,
                            radius = radius
                        )
                        val second = point(
                            angle = b,
                            radius = radius
                        )
                        val third = point(
                            angle = b,
                            radius = radius + 0.1f
                        )
                        val fourth = point(
                            angle = a,
                            radius = radius + 0.1f
                        )
                        path.reset()
                        path.moveTo(first.x, first.y)
                        path.lineTo(second.x, second.y)
                        path.lineTo(third.x, third.y)
                        path.lineTo(fourth.x, fourth.y)
                        path.close()
                        val color = if (passed) style.detailColor else style.obstacleColor
                        drawPath(path = path, color = color)
                        drawLine(
                            color = style.playerDetailColor.copy(alpha = 0.35f),
                            start = third,
                            end = fourth,
                            strokeWidth = 1f
                        )
                    }
                }
                path.reset()
                for (index in 0..5) {
                    val point =
                        point(
                            angle = index * PI.toFloat() / 3f + run.rotation,
                            radius = 0.18f + run.pulse * 0.025f
                        )
                    if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                }
                path.close()
                drawPath(path = path, color = style.accentColor)
                drawPath(
                    path = path,
                    color = style.playerDetailColor.copy(alpha = 0.35f),
                    style = Stroke(width = 1.5f)
                )
                val player = point(
                    angle = run.angle,
                    radius = 0.46f
                )
                rotate(degrees = run.angle * 180f / PI.toFloat() + 90f, pivot = player) {
                    drawArcadeSprite(HexagonShip, style.playerColor, player.x - 5f, player.y - 7f)
                    pixelRect(style.playerDetailColor, player.x - 1f, player.y - 1f, 2f, 3f)
                }
            }
        }
    }
}

private val HexagonShip = ArcadeSprite(
    width = 10, height = 12,
    data = "M4 0H6V3H8V6H10V12H7V10H3V12H0V6H2V3H4Z"
)

@Serializable
private data class RimWall(
    var radius: Float,
    val gap: Int,
    var passed: Boolean = false,
    val opening: Int = 1
)

@Serializable
private class HexagonRushRun : ArcadeRun() {
    val walls = mutableListOf<RimWall>()

    @Transient
    val outline = Path()
    val center get() = Offset(x = width / 2f, y = topInset + (height - topInset - 18f) / 2f)
    val extent get() = minOf(width - 28f, height - topInset - 30f) / 2f
    var angle = 0f
    var rotation = 0f

    @Transient
    var direction = 0
    var pulse = 0f
    private var timer = 0.7f
    private var elapsed = 0f
    private var previousGap = 0
    private var spawned = 0
    private var rotationSpeed = 0.12f

    override fun reset() {
        walls.clear()
        angle = PI.toFloat() / 6f
        rotation = 0f
        direction = 0
        pulse = 0f
        timer = 0.7f
        elapsed = 0f
        previousGap = 0
        spawned = 0
        rotationSpeed = 0.12f
    }

    fun steer(point: Offset) {
        direction = if (point.x < width / 2f) -1 else 1
    }

    override fun stopInput() {
        direction = 0
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        timer -= seconds
        pulse = (pulse - seconds * 3f).coerceAtLeast(0f)
        val full = 2f * PI.toFloat()
        val targetSpin = if (elapsed < 20f) 0.12f else when (spawned / 12 % 4) {
            0 -> 0.18f
            1 -> -0.24f
            2 -> -0.14f
            else -> 0.28f
        }
        rotationSpeed += (targetSpin - rotationSpeed) * (1f - kotlin.math.exp(-seconds * 0.9f))
        rotation = (rotation + rotationSpeed * seconds + full) % full
        angle = (angle + direction * 3.6f * seconds + full) % full
        val speed = 0.26f + minOf(elapsed * 0.0016f, 0.2f)
        if (timer <= 0f) {
            val phrase = listOf(
                listOf(1, 1, 1, 1, 0, -1), listOf(2, -2, 2, -2, 1, -1),
                listOf(-1, -1, 0, -1, -1, 1), listOf(0, 2, 0, -2, 1, 1),
                listOf(1, 2, 1, -1, -2, -1), listOf(-2, 0, 2, 1, 0, -1),
                listOf(0, 1, -1, 0, 2, -2), listOf(-1, 1, -2, 2, -1, 1)
            )[spawned / 6 % 8]
            val shift = if (spawned < 8) Random.nextInt(-1, 2) else phrase[spawned % 6]
            spawned++
            previousGap = (previousGap + shift + 6) % 6
            walls += RimWall(
                radius = 1.12f, gap = previousGap,
                opening = if (spawned > 8 && spawned % 24 in 1..2) 2 else 1
            )
            timer += maxOf(1.4f, 1.85f - elapsed * 0.004f) + if (spawned % 24 == 0) 0.4f else 0f
        }
        val relative = ((angle - rotation) % full + full) % full
        val sector = (relative / (PI.toFloat() / 3f)).toInt().coerceIn(0, 5)
        val local = relative - sector * PI.toFloat() / 3f - PI.toFloat() / 6f
        val playerRadius = 6f / extent
        for (wall in walls) {
            wall.radius -= speed * seconds
            val inner = wall.radius * cos(PI.toFloat() / 6f) / cos(local)
            val outer = (wall.radius + 0.1f) * cos(PI.toFloat() / 6f) / cos(local)
            if (inner < 0.46f + playerRadius && outer > 0.46f - playerRadius) {
                val gapCenter = (wall.gap + wall.opening / 2f) * PI.toFloat() / 3f
                val delta = abs(((relative - gapCenter + 3f * PI.toFloat()) % full) - PI.toFloat())
                if (delta > wall.opening * PI.toFloat() / 6f - playerRadius / 0.46f) {
                    phase = ArcadePhase.Over
                    return
                }
            }
            if (!wall.passed && wall.radius + 0.1f < 0.46f - playerRadius) {
                wall.passed = true
                score++
                pulse = 1f
            }
        }
        walls.removeAll { it.radius < 0.15f }
    }
}