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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.exp
import kotlin.random.Random

private val RacingBody = ArcadeSprite(
    "RacingBody", 30, 46,
    "M8 0H22V3H25V10H27V36H25V43H22V46H8V43H5V36H3V10H5V3H8Z"
)
private val RacingTires = ArcadeSprite(
    "RacingTires", 30, 46,
    "M0 9H5V19H0Z M25 9H30V19H25Z M0 30H5V40H0Z M25 30H30V40H25Z"
)

@Composable
fun RacerGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::RacerRun
    )

    ArcadeBoard(
        game = ArcadeGame.Racer,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::steer,
            onDrag = run::steer
        ) {
            val left = run.roadLeft
            val roadWidth = run.laneWidth * 3f

            pixelRect(
                color = style.detailColor.copy(alpha = 0.1f),
                x = left,
                y = 0f,
                width = roadWidth,
                height = run.height
            )

            for (side in 0..1) {
                val edge = if (side == 0) left else left + roadWidth
                pixelRect(
                    color = style.detailColor,
                    x = edge - 1f,
                    y = 0f,
                    width = 2f,
                    height = run.height
                )
                var y = run.distance % 36f - 36f
                while (y < run.height) {
                    pixelRect(
                        color = style.obstacleColor,
                        x = edge + if (side == 0) -6f else 2f,
                        y = y,
                        width = 4f,
                        height = 18f
                    )
                    y += 36f
                }
                repeat(9) { index ->
                    val y = (index * 83f + run.distance * 0.75f) % (run.height + 40f) - 20f
                    val x = if (side == 0) left * 0.44f else run.width - left * 0.44f
                    pixelRect(
                        color = style.detailColor,
                        x = x - 1f,
                        y = y,
                        width = 2f,
                        height = 15f
                    )
                    pixelRect(
                        color = style.obstacleColor.copy(alpha = 0.4f),
                        x = x - 5f,
                        y = y - 4f,
                        width = 10f,
                        height = 10f
                    )
                    pixelRect(
                        color = style.obstacleColor.copy(alpha = 0.4f),
                        x = x - 8f,
                        y = y,
                        width = 16f,
                        height = 5f
                    )
                }
            }
            for (lane in 1..2) {
                var y = run.distance % 42f - 42f
                while (y < run.height) {
                    pixelRect(style.detailColor, left + lane * run.laneWidth - 1f, y, 2f, 20f)
                    y += 42f
                }
            }
            for ((lane, y, kind) in run.traffic) {
                racingCar(
                    style, run.laneX(lane.toFloat()), y,
                    if (kind == 0) style.obstacleColor else style.accentColor,
                    kind
                )
            }
            val x = run.laneX(run.position)
            if (run.phase == ArcadePhase.Running) {
                pixelRect(
                    color = style.playerColor.copy(alpha = 0.15f),
                    x = x - 9f,
                    y = run.playerY + 22f,
                    width = 3f,
                    height = 15f
                )
                pixelRect(
                    color = style.playerColor.copy(alpha = 0.15f),
                    x = x + 6f,
                    y = run.playerY + 22f,
                    width = 3f,
                    height = 15f
                )
            }
            rotate((run.targetLane - run.position) * 9f, Offset(x, run.playerY)) {
                racingCar(style, x, run.playerY, style.playerColor)
            }
            run.effects.draw(this, style.accentColor)
        }
    }
}

private fun DrawScope.racingCar(
    style: ArcadeStyle,
    x: Float,
    y: Float,
    color: Color,
    kind: Int = 0
) {
    if (kind == 2) {
        pixelRect(
            color = style.textColor,
            x = x - 19f,
            y = y - 32f,
            width = 38f,
            height = 12f
        )
        pixelRect(
            color = style.textColor,
            x = x - 19f,
            y = y + 22f,
            width = 38f,
            height = 13f
        )
        pixelRect(
            color = color,
            x = x - 16f,
            y = y - 41f,
            width = 32f,
            height = 82f
        )
        pixelRect(
            color = style.playerDetailColor,
            x = x - 12f,
            y = y - 32f,
            width = 24f,
            height = 9f
        )
        pixelRect(
            color = style.detailColor,
            x = x - 17f,
            y = y - 15f,
            width = 34f,
            height = 54f
        )
        pixelRect(
            color = color,
            x = x - 14f,
            y = y - 12f,
            width = 28f,
            height = 48f
        )
        repeat(5) { index ->
            pixelRect(
                color = style.playerDetailColor.copy(alpha = 0.22f),
                x = x - 12f,
                y = y - 7f + index * 8f,
                width = 24f,
                height = 2f
            )
        }
        pixelRect(
            color = style.playerDetailColor,
            x = x - 13f,
            y = y - 39f,
            width = 5f,
            height = 3f
        )
        pixelRect(
            color = style.playerDetailColor,
            x = x + 8f,
            y = y - 39f,
            width = 5f,
            height = 3f
        )
        pixelRect(
            color = style.hazardColor,
            x = x - 13f,
            y = y + 38f,
            width = 5f,
            height = 3f
        )
        pixelRect(
            color = style.hazardColor,
            x = x + 8f,
            y = y + 38f,
            width = 5f,
            height = 3f
        )
        return
    }
    drawArcadeSprite(
        sprite = RacingBody,
        tint = style.textColor.copy(alpha = 0.12f),
        x = x - 13f,
        y = y - 20f
    )
    drawArcadeSprite(
        sprite = RacingTires,
        tint = style.textColor,
        x = x - 15f,
        y = y - 23f
    )
    drawArcadeSprite(
        sprite = RacingBody,
        tint = color,
        x = x - 15f,
        y = y - 23f
    )
    pixelRect(
        color = style.playerDetailColor,
        x = x - 8f,
        y = y - 10f,
        width = 16f,
        height = 8f
    )
    pixelRect(
        color = style.playerDetailColor.copy(alpha = 0.65f),
        x = x - 7f,
        y = y + 10f,
        width = 14f,
        height = 5f
    )
    pixelRect(
        color = color,
        x = x - 2f,
        y = y - 10f,
        width = 4f,
        height = 8f
    )
    pixelRect(
        color = style.playerDetailColor,
        x = x - 9f,
        y = y - 20f,
        width = 4f,
        height = 3f
    )
    pixelRect(
        color = style.playerDetailColor,
        x = x + 5f,
        y = y - 20f,
        width = 4f,
        height = 3f
    )
    pixelRect(
        color = style.hazardColor,
        x = x - 9f,
        y = y + 18f,
        width = 4f,
        height = 2f
    )
    pixelRect(
        color = style.hazardColor,
        x = x + 5f,
        y = y + 18f,
        width = 4f,
        height = 2f
    )
}

internal data class RacerCar(
    val lane: Int,
    var y: Float,
    val kind: Int,
    var passed: Boolean = false
)

internal class RacerRun : ArcadeRun() {
    val roadLeft get() = (width * 0.14f).coerceAtLeast(24f)
    val laneWidth get() = (width - roadLeft * 2f) / 3f
    val playerY get() = height - 36f
    val traffic = mutableListOf<RacerCar>()
    val effects = ArcadeEffects()
    var position = 1f
    var targetLane = 1
    var distance = 0f
    private var spawn = 0.8f
    private var elapsed = 0f
    private var lastGap = 1
    private var waves = 0

    fun laneX(lane: Float) = roadLeft + (lane + 0.5f) * laneWidth

    override fun reset() {
        traffic.clear()
        effects.clear()
        position = 1f
        targetLane = 1
        distance = 0f
        elapsed = 0f
        spawn = 0.8f
        lastGap = 1
        waves = 0
    }

    fun steer(point: Offset) {
        targetLane = ((point.x - roadLeft) / laneWidth).toInt().coerceIn(0, 2)
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        effects.advance(seconds)
        // Keep the same reaction window in a loading dialog and in the full screen.
        val speed = (height - topInset + 50f) / (2.5f - 1.1f * (1f - exp(-elapsed / 65f)))
        distance = (distance + speed * seconds) % 104580f
        position += (targetLane - position).coerceIn(-7f * seconds, 7f * seconds)
        spawn -= seconds
        if (spawn <= 0f) {
            val gap = if (lastGap == 1) {
                if (Random.nextBoolean()) 0 else 2
            } else 1
            val blocked = (0..2).filter { it != gap }
            var longest = 46f
            for (lane in blocked) {
                val kind = if (waves >= 3 && Random.nextFloat() < 0.4f) 2 else Random.nextInt(2)
                traffic += RacerCar(lane, -48f, kind)
                if (kind == 2) longest = 82f
            }
            lastGap = gap
            waves++
            // A truck must fully clear the player before the next gap changes lanes.
            spawn += maxOf(0.85f, 1.3f - elapsed * 0.003f, (longest + 46f) / speed + 0.38f)
        }
        val iterator = traffic.iterator()
        while (iterator.hasNext()) {
            val car = iterator.next()
            car.y += speed * seconds
            val separation = abs(car.lane - position) * laneWidth
            val clearance = if (car.kind == 2) 60f else 39f
            if (abs(car.y - playerY) < clearance && separation < if (car.kind == 2) 28f else 24f) {
                phase = ArcadePhase.Over
                return
            }
            if (!car.passed && car.y > playerY + clearance + 3f) {
                car.passed = true
                score += if (car.kind == 2) 20 else 10
                effects.burst(Offset(laneX(car.lane.toFloat()), playerY + 25f), 4)
            }
            if (car.y > height + 46f) iterator.remove()
        }
    }

    override fun onResize(previous: Size) {
        val ratio = height / previous.height
        for (car in traffic) car.y *= ratio
        effects.clear()
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "road",
            floatArrayOf(
                position,
                distance,
                targetLane.toFloat(),
                spawn,
                elapsed,
                lastGap.toFloat(),
                waves.toFloat()
            )
        )
        state.putFloatArray("traffic", traffic.flatMap {
            listOf(it.lane.toFloat(), it.y, it.kind.toFloat(), if (it.passed) 1f else 0f)
        }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("road")?.let {
            position = it[0]
            distance = it[1]
            targetLane = it[2].toInt()
            spawn = it[3]
            elapsed = it[4]
            lastGap = it[5].toInt()
            waves = it[6].toInt()
        }
        traffic.clear()
        state.getFloatArray("traffic")?.let {
            for (i in it.indices step 4) traffic += RacerCar(
                it[i].toInt(),
                it[i + 1],
                it[i + 2].toInt(),
                it[i + 3] == 1f
            )
        }
    }
}
