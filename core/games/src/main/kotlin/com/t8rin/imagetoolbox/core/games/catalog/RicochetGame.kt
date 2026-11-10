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

@file:UseSerializers(ArcadeOffsetSerializer::class)

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.random.Random

@Composable
fun RicochetGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::RicochetRun
    )
    val measurer = rememberTextMeasurer()

    ArcadeBoard(
        game = ArcadeGame.Ricochet,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        isPaused = isPaused,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::aim,
            onDrag = run::aim,
            onTap = { run.shoot() },
            onSwipe = { run.shoot() },
            onRelease = run::stopInput
        ) {
            val cell = run.cell
            val radius = run.ballRadius
            val rowHeight = run.rowHeight
            for (row in 0..6) for (column in 0..6) {
                drawCircle(
                    color = style.detailColor.copy(alpha = 0.2f),
                    radius = 1f,
                    center = run.origin + Offset(
                        x = (column + 0.5f) * cell,
                        y = (row + 0.5f) * rowHeight
                    )
                )
            }
            for ((column, row, health) in run.bricks) {
                val point = run.origin + Offset(
                    x = column * cell + 2f,
                    y = (row - run.shift) * rowHeight + 2f
                )
                val center = point + Offset(x = cell / 2f - 2f, y = rowHeight / 2f - 2f)
                if (health == 0) {
                    drawCircle(
                        color = style.accentColor.copy(alpha = 0.14f),
                        radius = minOf(cell, rowHeight) * 0.28f,
                        center = center
                    )
                    drawCircle(
                        color = style.accentColor,
                        radius = minOf(cell, rowHeight) * 0.22f,
                        center = center,
                        style = Stroke(width = 1.5f)
                    )
                    drawLine(
                        color = style.accentColor,
                        start = center - Offset(x = minOf(cell, rowHeight) * 0.1f, y = 0f),
                        end = center + Offset(x = minOf(cell, rowHeight) * 0.1f, y = 0f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = style.accentColor,
                        start = center - Offset(x = 0f, y = minOf(cell, rowHeight) * 0.1f),
                        end = center + Offset(x = 0f, y = minOf(cell, rowHeight) * 0.1f),
                        strokeWidth = 2f
                    )
                } else {
                    val color =
                        if (health > run.ballCount) style.accentColor else style.obstacleColor
                    drawRoundRect(
                        color = color,
                        topLeft = point,
                        size = Size(width = cell - 4f, height = rowHeight - 4f),
                        cornerRadius = CornerRadius(x = 3f)
                    )
                    drawRoundRect(
                        color = color,
                        topLeft = point,
                        size = Size(width = cell - 4f, height = rowHeight - 4f),
                        cornerRadius = CornerRadius(x = 3f),
                        style = Stroke(width = 1.5f)
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.4f),
                        x = point.x + 2f,
                        y = point.y + 2f,
                        width = cell - 8f,
                        height = 1.5f
                    )
                    val label = measurer.measure(
                        health.toString(),
                        style.scoreTextStyle.copy(fontSize = (minOf(cell, rowHeight) * 0.46f).sp),
                        density = Density(1f)
                    )
                    drawText(
                        label,
                        style.playerDetailColor,
                        center - Offset(x = label.size.width / 2f, y = label.size.height / 2f)
                    )
                }
            }
            val left = run.origin.x
            drawLine(
                color = style.hazardColor.copy(alpha = 0.5f),
                start = Offset(x = left, y = run.launchY - rowHeight * 0.35f),
                end = Offset(x = left + cell * 7f, y = run.launchY - rowHeight * 0.35f),
                strokeWidth = 1f
            )
            if (!run.flying) {
                var point = Offset(x = run.launchX, y = run.launchY)
                repeat(18) { index ->
                    point += run.direction * (rowHeight * 0.32f)
                    if (point.x in (left + radius)..(left + cell * 7f - radius) && point.y > run.origin.y) {
                        drawCircle(
                            color = style.playerColor.copy(alpha = 0.55f * (1f - index / 20f)),
                            radius = radius * 0.48f,
                            center = point
                        )
                    }
                }
            }
            for ((x, y, vx, vy) in run.balls) {
                drawLine(
                    color = style.playerColor.copy(alpha = 0.2f),
                    start = Offset(x = x - vx * 0.025f, y = y - vy * 0.025f),
                    end = Offset(x = x, y = y),
                    strokeWidth = radius * 1.5f
                )
                drawCircle(
                    color = style.playerColor,
                    radius = radius,
                    center = Offset(x = x, y = y)
                )
            }
            drawCircle(
                color = style.playerColor,
                radius = radius * 1.3f,
                center = Offset(x = run.launchX, y = run.launchY)
            )
            val count =
                measurer.measure("×${run.ballCount}", style.scoreTextStyle, density = Density(1f))
            drawText(
                count,
                style.playerColor,
                Offset(x = run.launchX - count.size.width / 2f, y = run.launchY + radius + 3f)
            )
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

@Serializable
internal data class RicochetBrick(
    val column: Int,
    var row: Int,
    var health: Int
)

@Serializable
internal data class RicochetBall(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float
)

@Serializable
internal class RicochetRun : ArcadeRun() {
    val cell get() = (width - 32f) / 7f
    val rowHeight get() = (height - topInset - 44f) / 8f
    val origin get() = Offset(x = (width - cell * 7f) / 2f, y = topInset + 6f)
    val launchY get() = origin.y + rowHeight * 7.7f
    val ballRadius get() = minOf(cell, rowHeight) * 0.12f
    val bricks = mutableListOf<RicochetBrick>()
    val balls = mutableListOf<RicochetBall>()

    @Transient
    val effects = ArcadeEffects()
    var launchX = width / 2f
    var direction = Offset(x = 0.25f, y = -0.97f)
    var ballCount = 3
    var flying = false
    var shift = 0f

    @Transient
    private var aiming = false
    private var pending = 0
    private var delay = 0f
    private var flightTime = 0f
    private var firstReturn = -1f
    private var round = 1

    override fun reset() {
        bricks.clear()
        balls.clear()
        effects.clear()
        launchX = width / 2f
        direction = Offset(x = 0.25f, y = -0.97f)
        ballCount = 3
        flying = false
        aiming = false
        shift = 0f
        pending = 0
        delay = 0f
        flightTime = 0f
        firstReturn = -1f
        round = 1
        repeat(3) { addRow(it) }
    }

    private fun addRow(row: Int) {
        val columns = (0..6).shuffled()
        val count = minOf(5, 3 + round / 6)
        for (column in columns.take(count)) {
            bricks += RicochetBrick(
                column = column,
                row = row,
                health = (round.coerceAtMost(ballCount + 8)) + Random.nextInt(1, 4)
            )
        }
        bricks += RicochetBrick(column = columns[count], row = row, health = 0)
    }

    fun aim(point: Offset) {
        if (flying || shift > 0f) return
        val dx = point.x - launchX
        val dy = minOf(-rowHeight * 0.7f, point.y - launchY)
        val length = hypot(dx, dy).coerceAtLeast(1f)
        direction = Offset(x = dx / length, y = dy / length)
        aiming = true
    }

    fun shoot() {
        if (!aiming || flying || shift > 0f || phase != ArcadePhase.Running) return
        aiming = false
        flying = true
        pending = ballCount
        delay = 0f
        flightTime = 0f
        firstReturn = -1f
    }

    override fun stopInput() {
        aiming = false
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        shift = (shift - seconds * 4f).coerceAtLeast(0f)
        if (!flying) return
        flightTime += seconds
        delay -= seconds
        val speed = maxOf(width, height) * 1.3f
        if (pending > 0 && delay <= 0f) {
            balls += RicochetBall(
                x = launchX,
                y = launchY,
                vx = direction.x * speed,
                vy = direction.y * speed
            )
            pending--
            delay += 0.1f
        }
        val left = origin.x + ballRadius
        val right = origin.x + cell * 7f - ballRadius
        val iterator = balls.iterator()
        while (iterator.hasNext()) {
            val ball = iterator.next()
            val oldX = ball.x
            val oldY = ball.y
            ball.x += ball.vx * seconds
            ball.y += ball.vy * seconds
            if (ball.x !in left..right) {
                ball.x = ball.x.coerceIn(left, right)
                ball.vx = -ball.vx
            }
            if (ball.y < origin.y + ballRadius) {
                ball.y = origin.y + ballRadius
                ball.vy = abs(ball.vy)
            }
            val hit = bricks.firstOrNull {
                val x = origin.x + it.column * cell + 2f
                val y = origin.y + it.row * rowHeight + 2f
                ball.x + ballRadius > x && ball.x - ballRadius < x + cell - 4f &&
                        ball.y + ballRadius > y && ball.y - ballRadius < y + rowHeight - 4f
            }
            if (hit != null) {
                if (hit.health == 0) {
                    bricks.remove(hit)
                    ballCount = minOf(18, ballCount + 1)
                    score += 5
                } else {
                    val x = origin.x + hit.column * cell + 2f
                    if (oldX + ballRadius <= x || oldX - ballRadius >= x + cell - 4f) {
                        ball.vx = -ball.vx
                        ball.x = oldX
                    } else {
                        ball.vy = -ball.vy
                        ball.y = oldY
                    }
                    hit.health--
                    score++
                    if (hit.health == 0) bricks.remove(hit)
                }
                effects.burst(
                    point = Offset(x = ball.x, y = ball.y),
                    count = 5
                )
            }
            // Bring back shallow ricochets so a volley cannot stall the next throw.
            if (flightTime > 6f) ball.vy = maxOf(abs(ball.vy), speed * 0.7f)
            if (ball.y >= launchY && ball.vy > 0f) {
                if (firstReturn < 0f) firstReturn = ball.x.coerceIn(left, right)
                iterator.remove()
            }
        }
        if (pending == 0 && balls.isEmpty()) {
            flying = false
            launchX = if (firstReturn >= 0f) firstReturn else width / 2f
            round++
            bricks.removeAll { it.health == 0 && it.row >= 5 }
            for (brick in bricks) brick.row++
            if (bricks.any { it.row >= 7 && it.health > 0 }) {
                phase = ArcadePhase.Over
            } else {
                addRow(0)
                shift = 1f
            }
        }
    }

    override fun onResize(previous: Size) {
        launchX *= width / previous.width
        for (ball in balls) {
            ball.x *= width / previous.width
            ball.y *= height / previous.height
        }
        effects.clear()
        stopInput()
    }
}