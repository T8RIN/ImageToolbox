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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SportsMotorsports
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.exp
import kotlin.random.Random

@RegisterArcadeGame
internal data object RacerGame : ArcadeGameDefinition {
    override val title = R.string.arcade_racer
    override val description = R.string.arcade_racer_sub
    override val icon get() = Icons.Outlined.SportsMotorsports
    override val order = 8

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::RacerRun
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
                onDrag = run::steer
            ) {
                drawRacingSurroundings(run, style)
                val left = run.roadLeft
                val roadWidth = run.laneWidth * 3f

                pixelRect(
                    color = style.detailColor.copy(alpha = 0.075f),
                    x = left,
                    y = 0f,
                    width = roadWidth,
                    height = run.height
                )
                for (side in 0..1) {
                    val edge = if (side == 0) left else left + roadWidth
                    val direction = if (side == 0) -1f else 1f
                    var y = run.distance % 144f - 144f
                    while (y < run.height) {
                        scale(
                            scaleX = direction,
                            scaleY = 1f,
                            pivot = Offset(x = edge, y = 0f)
                        ) {
                            drawArcadeSprite(
                                sprite = RacingShoulder,
                                tint = style.detailColor,
                                x = edge,
                                y = y
                            )
                        }
                        val x = if (side == 0) left / 2f else run.width - left / 2f
                        drawArcadeSprite(
                            sprite = RacingBush,
                            tint = style.detailColor.copy(alpha = 0.3f),
                            x = x - RacingBush.width / 2f,
                            y = y + if (side == 0) 60f else 116f
                        )
                        y += 144f
                    }
                }
                for (lane in 1..2) {
                    var y = run.distance % 42f - 42f
                    while (y < run.height) {
                        pixelRect(
                            color = style.detailColor,
                            x = left + lane * run.laneWidth - 1f,
                            y = y,
                            width = 2f,
                            height = 20f
                        )
                        y += 42f
                    }
                }
                for ((lane, y, kind) in run.traffic) {
                    racingCar(
                        style = style,
                        x = run.laneX(lane = lane.toFloat()),
                        y = y,
                        color = lerp(
                            style.obstacleColor,
                            style.accentColor,
                            if (kind == 1) 0.32f else if (kind == 2) 0.12f else 0f
                        ),
                        kind = kind
                    )
                }
                val x = run.laneX(run.position)
                if (run.phase == ArcadePhase.Running) {
                    pixelRect(
                        color = style.playerColor.copy(alpha = 0.15f),
                        x = x - 14f,
                        y = run.playerY + 18f,
                        width = 3f,
                        height = 18f
                    )
                    pixelRect(
                        color = style.playerColor.copy(alpha = 0.15f),
                        x = x + 11f,
                        y = run.playerY + 18f,
                        width = 3f,
                        height = 18f
                    )
                }
                rotate(
                    degrees = (run.targetLane - run.position) * 9f,
                    pivot = Offset(x = x, y = run.playerY)
                ) {
                    racingCar(
                        style = style,
                        x = x,
                        y = run.playerY,
                        color = style.playerColor
                    )
                }
                if (run.closeFlash > 0f) for (side in listOf(-1f, 1f)) {
                    pixelRect(
                        style.accentColor.copy(alpha = run.closeFlash * 0.6f),
                        x + side * 21f, run.playerY - 17f, 2f, 34f
                    )
                }
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private val RacingPlayer = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M6 0H24V2H26V28H28V40H30V44H26V46H4V44H0V40H2V28H4V2H6Z"
)

private val RacingPlayerDetails = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M5 4H10V7H5Z M20 4H25V7H20Z M9 15H21V17H23V22H7V17H9Z " +
            "M9 32H21V37H9Z M3 41H27V43H3Z"
)

private val RacingCar = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M6 0H24V2H26V18H29V21H26V44H24V46H6V44H4V21H1V18H4V2H6Z"
)

private val RacingCarDetails = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M5 3H10V5H5Z M20 3H25V5H20Z M8 11H22V17H8Z " +
            "M8 32H22V38H8Z M6 42H10V44H6Z M20 42H24V44H20Z"
)

private val RacingHatchback = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M4 0H26V2H28V16H30V19H28V44H26V46H4V44H2V19H0V16H2V2H4Z"
)

private val RacingHatchbackDetails = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M4 3H9V5H4Z M21 3H26V5H21Z M6 8H24V14H6Z " +
            "M6 28H24V39H6Z M3 41H6V44H3Z M24 41H27V44H24Z"
)

private val RacingTires = ArcadeSprite(
    width = 30,
    height = 46,
    data = "M0 8H5V17H0Z M25 8H30V17H25Z M0 31H5V40H0Z M25 31H30V40H25Z"
)

private val RacingTruck = ArcadeSprite(
    width = 38,
    height = 82,
    data = "M7 0H31V2H33V26H36V80H34V82H4V80H2V26H5V2H7Z"
)

private val RacingTruckDetails = ArcadeSprite(
    width = 38,
    height = 82,
    data = "M6 3H11V6H6Z M27 3H32V6H27Z M8 9H18V16H8Z M20 9H30V16H20Z " +
            "M4 27H34V30H4Z M4 77H9V80H4Z M29 77H34V80H29Z"
)

private val RacingTruckTires = ArcadeSprite(
    width = 38,
    height = 82,
    data = "M0 10H6V20H0Z M32 10H38V20H32Z M0 60H5V68H0Z M33 60H38V68H33Z " +
            "M0 72H5V80H0Z M33 72H38V80H33Z"
)

private val RacingBush = ArcadeSprite(
    width = 20,
    height = 14,
    data = "M0 6H2V4H6V2H8V0H12V2H14V4H18V6H20V10H18V12H14V14H6V12H2V10H0Z"
)

private val RacingShoulder = ArcadeSprite(
    width = 18,
    height = 144,
    data = "M0 0H1V58H3V85H1V144H0V84H2V59H0Z M8 32H11V33H8Z M14 110H16V112H14Z"
)

private fun DrawScope.racingCar(
    style: ArcadeStyle,
    x: Float,
    y: Float,
    color: Color,
    kind: Int = -1
) {
    val sprite = when (kind) {
        0 -> RacingCar
        1 -> RacingHatchback
        2 -> RacingTruck
        else -> RacingPlayer
    }
    val details = when (kind) {
        0 -> RacingCarDetails
        1 -> RacingHatchbackDetails
        2 -> RacingTruckDetails
        else -> RacingPlayerDetails
    }
    val left = x - sprite.width / 2f
    val top = y - sprite.height / 2f
    val ink = style.overlayColor.copy(alpha = 1f)
    drawArcadeSprite(sprite, ink.copy(alpha = 0.18f), left + 2f, top + 3f)
    drawArcadeSprite(
        sprite = if (kind == 2) RacingTruckTires else RacingTires,
        tint = ink.copy(alpha = 0.85f),
        x = left,
        y = top
    )
    drawArcadeSprite(
        sprite = sprite,
        tint = color,
        x = left,
        y = top
    )
    drawArcadeSprite(
        sprite = details,
        tint = style.playerDetailColor,
        x = left,
        y = top
    )
    pixelRect(style.hazardColor, left + 6f, top + sprite.height - 4f, 3f, 2f)
    pixelRect(style.hazardColor, left + sprite.width - 9f, top + sprite.height - 4f, 3f, 2f)
    val trim = style.playerDetailColor.copy(alpha = 0.25f)
    if (kind == 2) {
        pixelRect(color = trim, x = left + 8f, y = top + 21f, width = 22f, height = 2f)
        pixelRect(color = trim, x = left + 5f, y = top + 33f, width = 2f, height = 42f)
        pixelRect(color = trim, x = left + 31f, y = top + 33f, width = 2f, height = 42f)
        repeat(3) { index ->
            pixelRect(
                color = trim,
                x = left + 7f,
                y = top + 38f + index * 12f,
                width = 24f,
                height = 2f
            )
        }
    } else if (kind == -1) {
        pixelRect(color = trim, x = x - 3f, y = top + 3f, width = 2f, height = 10f)
        pixelRect(color = trim, x = x + 1f, y = top + 3f, width = 2f, height = 10f)
    } else {
        pixelRect(
            color = trim,
            x = left + 6f,
            y = top + if (kind == 0) 8f else 6f,
            width = 18f,
            height = 1f
        )
    }
}

private fun DrawScope.drawRacingSurroundings(run: RacerRun, style: ArcadeStyle) {
    val left = run.roadLeft
    val height = run.height
    val roadWidth = run.laneWidth * 3f
    for (lane in 0..2) for (track in listOf(0.25f, 0.72f)) {
        pixelRect(
            style.detailColor.copy(alpha = 0.025f),
            left + run.laneWidth * (lane + track),
            0f,
            2f,
            height
        )
    }
    var y = run.distance * 0.5f % 61f - 61f
    var row = 0
    while (y < height) {
        for (lane in 0..2) {
            val x = left + run.laneWidth * (lane + 0.24f) + row % 2 * 9f
            pixelRect(style.detailColor.copy(alpha = 0.055f), x, y, run.laneWidth * 0.31f, 1f)
            pixelRect(
                style.detailColor.copy(alpha = 0.035f),
                x + 11f,
                y + 19f,
                run.laneWidth * 0.16f,
                2f
            )
        }
        y += 61f
        row++
    }
    val columns = if (left >= 47f) ((left - 47f) / 47f).toInt() + 1 else 0
    for (side in 0..1) {
        val clipLeft = if (side == 0) 0f else left + roadWidth + 3f
        val clipRight = if (side == 0) left - 3f else run.width
        clipRect(left = clipLeft, top = 0f, right = clipRight, bottom = height) {
            for (column in 0 until columns) {
                val x = if (side == 0) column * 47f + 5f else run.width - 35f - column * 47f
                var top = run.distance * 0.12f % 104f - 104f
                while (top < height) {
                    val ink = style.detailColor.copy(alpha = 0.12f)
                    drawRect(ink.copy(alpha = 0.045f), Offset(x, top + 19f), Size(27f, 24f))
                    val roof = Path().apply {
                        moveTo(x - 3f, top + 19f)
                        lineTo(x + 13f, top + 7f)
                        lineTo(x + 30f, top + 19f)
                        close()
                    }
                    drawPath(roof, ink)
                    pixelRect(ink.copy(alpha = 0.15f), x + 5f, top + 25f, 5f, 5f)
                    pixelRect(ink.copy(alpha = 0.15f), x + 17f, top + 25f, 5f, 5f)
                    pixelRect(ink.copy(alpha = 0.1f), x + 11f, top + 35f, 6f, 8f)
                    top += 104f
                }
            }
        }
    }
}

@Serializable
private data class RacerCar(
    val lane: Int,
    var y: Float,
    val kind: Int,
    var passed: Boolean = false,
    var nearest: Float = Float.MAX_VALUE
)

@Serializable
private class RacerRun : ArcadeRun() {
    val roadLeft get() = (width * 0.14f).coerceAtLeast(24f)
    val laneWidth get() = (width - roadLeft * 2f) / 3f
    val playerY get() = height - 36f
    val traffic = mutableListOf<RacerCar>()

    @Transient
    val effects = ArcadeEffects()
    var closeFlash = 0f
        private set
    private var chain = 0
    private var chainTime = 0f
    var position = 1f
    var targetLane = 1
    var distance = 0f
    private var spawn = 0.8f
    private var elapsed = 0f
    private var waves = 0
    private val gaps: ArcadeDeque<Int> = ArrayDeque()

    fun laneX(lane: Float) = roadLeft + (lane + 0.5f) * laneWidth

    override fun reset() {
        traffic.clear()
        effects.clear()
        position = 1f
        closeFlash = 0f
        chain = 0
        chainTime = 0f
        targetLane = 1
        distance = 0f
        elapsed = 0f
        spawn = 0.8f
        waves = 0
        gaps.clear()
    }

    fun steer(point: Offset) {
        targetLane = ((point.x - roadLeft) / laneWidth).toInt().coerceIn(0, 2)
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        closeFlash = (closeFlash - seconds * 2.5f).coerceAtLeast(0f)
        chainTime = (chainTime - seconds).coerceAtLeast(0f)
        if (chainTime == 0f) chain = 0
        effects.advance(seconds)
        // Keep the same reaction window in a loading dialog and in the full screen.
        val speed = (height - topInset + 50f) / (2.5f - 1.1f * (1f - exp(-elapsed / 65f)))
        distance = (distance + speed * seconds) % 104580f
        position += (targetLane - position).coerceIn(-7f * seconds, 7f * seconds)
        spawn -= seconds
        if (spawn <= 0f) {
            if (gaps.isEmpty()) {
                val phrase = listOf(
                    listOf(0, 1, 2, 2, 1, 0),
                    listOf(1, 0, 2, 1, 2, 0),
                    listOf(0, 0, 1, 2, 1, 1),
                    listOf(2, 1, 0, 2, 0, 1)
                ).random()
                gaps.addAll(if (Random.nextBoolean()) phrase else phrase.map { 2 - it })
            }
            val gap = gaps.removeFirst()
            val options = (0..2).filter { it != gap }
            val blocked = if (waves % 5 == 2) listOf(options.random()) else options
            var longest = 46f
            for (lane in blocked) {
                val kind = if (waves >= 3 && Random.nextFloat() < 0.4f) 2 else Random.nextInt(2)
                traffic += RacerCar(lane = lane, y = -48f, kind = kind)
                if (kind == 2) longest = 82f
            }
            waves++
            // A truck must fully clear the player before the next gap changes lanes.
            val turnTime = (gaps.firstOrNull()?.let { abs(it - gap) } ?: 2) / 7f
            spawn += maxOf(
                0.85f,
                1.3f - elapsed * 0.003f,
                (longest + 46f) / speed + maxOf(0.38f, turnTime + 0.24f)
            )
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
            if (abs(car.y - playerY) < clearance + 7f) car.nearest = minOf(car.nearest, separation)
            if (!car.passed && car.y > playerY + clearance + 3f) {
                car.passed = true
                score += if (car.kind == 2) 20 else 10
                if (car.nearest < 45f) {
                    chain = (chain + 1).coerceAtMost(5)
                    chainTime = 4f
                    score += chain * 10
                    closeFlash = 1f
                    effects.burst(Offset(laneX(position), playerY), 10)
                }
                effects.burst(Offset(x = laneX(car.lane.toFloat()), y = playerY + 25f), 4)
            }
            if (car.y > height + 46f) iterator.remove()
        }
    }

    override fun onResize(previous: Size) {
        val ratio = height / previous.height
        for (car in traffic) car.y *= ratio
        effects.clear()
    }
}