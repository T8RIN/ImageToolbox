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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LunarLanderGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState = initialState, create = ::LunarLanderRun)
    ArcadeBoard(
        game = ArcadeGame.LunarLander,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::beginThrust,
            onDrag = run::steer,
            onRelease = run::stopInput
        ) {
            arcadeStars(
                style = style,
                width = run.width,
                height = run.height,
                drift = 0f
            )
            scale(run.zoom, run.shipPoint) {
                val horizon = Path().apply {
                    moveTo(-30f, run.height)
                    repeat(15) { ridge ->
                        lineTo(
                            (ridge - 1) * run.width / 12f,
                            run.top + run.span * (0.64f + ((ridge * 37 + run.round * 11) % 23) / 100f)
                        )
                    }
                    lineTo(run.width + 30f, run.height)
                    close()
                }
                drawPath(
                    path = horizon,
                    color = style.detailColor.copy(alpha = 0.12f)
                )
                val ground = Path().apply {
                    moveTo(0f, run.height)
                    run.terrain.forEach { lineTo(it.x * run.width, run.top + it.y * run.span) }
                    lineTo(run.width, run.height)
                    close()
                }
                drawPath(
                    path = ground,
                    color = style.obstacleColor.copy(alpha = 0.17f)
                )
                drawPath(
                    path = ground,
                    color = style.obstacleColor.copy(alpha = 0.6f),
                    style = Stroke(1.5f)
                )
                repeat(24) {
                    val x = ((it * 79 + 23) % 359) / 359f
                    val surface = run.top + run.groundAt(x) * run.span
                    val y = surface + 8f + it % 4 * 5f
                    if (y < run.height - 6f) pixelRect(
                        color = style.detailColor.copy(alpha = 0.5f),
                        x = x * run.width,
                        y = y,
                        width = 3f + it % 5,
                        height = 2f
                    )
                }
                for (pad in run.pads) {
                    val padY = run.top + pad.y * run.span
                    val padX = pad.left * run.width
                    val padWidth = pad.width * run.width
                    val padColor =
                        if (run.safeSpeed || run.landed) style.accentColor else style.hazardColor
                    pixelRect(
                        color = padColor.copy(alpha = 0.12f),
                        x = padX,
                        y = padY - 6f,
                        width = padWidth,
                        height = 8f
                    )
                    pixelRect(
                        color = padColor,
                        x = padX,
                        y = padY - 1f,
                        width = padWidth,
                        height = 3f
                    )
                    for (side in 0..1) {
                        val x = padX + side * padWidth
                        pixelRect(
                            color = style.obstacleColor,
                            x = x - 1f,
                            y = padY - 12f,
                            width = 2f,
                            height = 12f
                        )
                        pixelRect(
                            color = padColor,
                            x = x - 3f,
                            y = padY - 14f,
                            width = 6f,
                            height = 3f
                        )
                    }
                    repeat(pad.multiplier) { dot ->
                        pixelRect(
                            color = padColor,
                            x = padX + padWidth / 2f + (dot - (pad.multiplier - 1) / 2f) * 7f - 1.5f,
                            y = padY + 6f,
                            width = 3f,
                            height = 3f
                        )
                    }
                    if (abs(run.x - pad.left - pad.width / 2f) < pad.width && pad.y - run.y < 0.35f) {
                        for (side in 0..1) {
                            val x = padX + side * padWidth
                            var y = padY - 20f
                            while (y > run.shipPoint.y - 12f) {
                                pixelRect(
                                    color = padColor.copy(alpha = 0.3f),
                                    x = x,
                                    y = y,
                                    width = 1f,
                                    height = 3f
                                )
                                y -= 8f
                            }
                        }
                    }
                }
                val center = run.shipPoint
                if (!run.crashed) translate(center.x, center.y) {
                    rotate(run.angle * 180f / PI.toFloat(), Offset.Zero) {
                        scale(run.shipScale, Offset.Zero) {
                            if (run.burning) {
                                val flame = Path().apply {
                                    moveTo(-3f, 6f)
                                    lineTo(0f, 10f + run.throttle * 16f + sin(run.time * 45f) * 2f)
                                    lineTo(3f, 6f)
                                    close()
                                }
                                drawPath(
                                    path = flame,
                                    color = style.accentColor
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = -1f,
                                    y = 7f,
                                    width = 2f,
                                    height = 4f
                                )
                            }
                            val cabin = Path().apply {
                                moveTo(-6f, -3f)
                                lineTo(-3f, -8f)
                                lineTo(3f, -8f)
                                lineTo(6f, -3f)
                                lineTo(6f, 4f)
                                lineTo(-6f, 4f)
                                close()
                            }
                            drawPath(
                                path = cabin,
                                color = style.playerColor
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = -3f,
                                y = -5f,
                                width = 6f,
                                height = 4f
                            )
                            pixelRect(
                                color = style.obstacleColor,
                                x = -5f,
                                y = 4f,
                                width = 10f,
                                height = 3f
                            )
                            for (side in listOf(-1f, 1f)) {
                                drawLine(
                                    color = style.playerColor,
                                    start = Offset(side * 5f, 3f),
                                    end = Offset(side * 8f, 10f),
                                    strokeWidth = 2f
                                )
                                pixelRect(
                                    color = style.playerColor,
                                    x = side * 8f - 3f,
                                    y = 10f,
                                    width = 6f,
                                    height = 2f
                                )
                            }
                        }
                    }
                }
                if (!run.crashed && !run.landed) {
                    val drift = Offset(run.vx * run.width, run.vy * run.span) * 0.35f
                    if (drift.getDistance() > 3f) drawLine(
                        color = if (run.safeSpeed) style.accentColor.copy(alpha = 0.5f) else style.hazardColor.copy(
                            alpha = 0.65f
                        ),
                        start = run.shipPoint + Offset(0f, 17f * run.shipScale),
                        end = run.shipPoint + Offset(0f, 17f * run.shipScale) + drift,
                        strokeWidth = 1.5f
                    )
                }
                run.effects.draw(
                    scope = this,
                    color = if (run.crashed) style.hazardColor else style.accentColor
                )
            }
            run.touchOrigin?.let { origin ->
                val point = origin + Offset(
                    run.stick.x.coerceIn(-34f, 34f),
                    run.stick.y.coerceIn(-34f, 34f)
                )
                drawCircle(
                    color = style.detailColor.copy(alpha = 0.4f),
                    radius = 27f,
                    center = origin,
                    style = Stroke(1f)
                )
                drawLine(
                    color = style.accentColor.copy(alpha = 0.5f),
                    start = origin,
                    end = point,
                    strokeWidth = 2f
                )
                drawCircle(
                    color = style.accentColor.copy(alpha = 0.5f),
                    radius = 5f,
                    center = point
                )
            }
            arcadeLives(
                style = style,
                remaining = run.lives,
                y = run.topInset / 2f
            )
            val fuelY = run.topInset / 2f - 5f
            drawRect(
                color = style.detailColor,
                topLeft = Offset(76f, fuelY),
                size = Size(34f, 10f),
                style = Stroke(1f)
            )
            pixelRect(
                color = if (run.fuel > 0.2f) style.playerColor else style.hazardColor,
                x = 78f,
                y = fuelY + 2f,
                width = 30f * run.fuel,
                height = 6f
            )
            pixelRect(
                color = style.detailColor,
                x = 110f,
                y = fuelY + 3f,
                width = 2f,
                height = 4f
            )
        }
    }
}

internal data class LandingPad(
    val left: Float,
    val width: Float,
    val y: Float,
    val multiplier: Int = 1
)

internal class LunarLanderRun : ArcadeRun() {
    val terrain = mutableListOf<Offset>()
    val effects = ArcadeEffects()
    val top get() = topInset + 12f
    val span get() = height - top - 12f
    val shipScale get() = minOf(width / 360f, span / 180f).coerceIn(0.85f, 1.25f)
    val shipPoint get() = Offset(x * width, top + y * span)
    var x = 0.2f
    var y = 0.1f
    var vx = 0f
    var vy = 0f
    var angle = 0f
    var fuel = 1f
    var padLeft = 0.5f
    var padWidth = 0.22f
    var padY = 0.85f
    var bonusPad: LandingPad? = null
    val pads get() = listOfNotNull(LandingPad(padLeft, padWidth, padY), bonusPad)
    var throttle = 0.55f
    var zoom = 1f
    var touchOrigin: Offset? = null
        private set
    var stick = Offset.Zero
        private set
    var round = 1
    var lives = 3
    var time = 0f
    var landed = false
    var crashed = false
    private var firing = false
    private var targetAngle = 0f
    private var touchAngle = 0f
    private var transition = 0f
    private var grace = 0.7f
    val burning get() = firing && fuel > 0f && transition <= 0f
    val safeSpeed
        get() = abs(vx) <= 0.1f && abs(vy) <= 0.13f - round.coerceAtMost(16) * 0.002f && abs(
            angle
        ) <= 0.22f

    override fun reset() {
        round = 1
        lives = 3
        time = 0f
        effects.clear()
        newRound()
    }

    private fun newRound() {
        padWidth = (0.225f - round * 0.005f).coerceAtLeast(0.13f)
        padLeft = 0.12f + Random.nextFloat() * (0.76f - padWidth)
        padY = 0.77f + Random.nextFloat() * 0.1f
        bonusPad = if (round > 1) {
            val w = (0.15f - round * 0.003f).coerceAtLeast(0.1f)
            val left = if (padLeft > 0.4f) 0.05f else 0.94f - w
            LandingPad(left, w, 0.7f + Random.nextFloat() * 0.15f, 2)
        } else null
        terrain.clear()
        val pads = pads
        repeat(17) {
            val x = it / 16f
            if (pads.none { x in it.left..(it.left + it.width) }) terrain += Offset(
                x, 0.66f + Random.nextFloat() * 0.29f
            )
        }
        for (pad in pads) {
            terrain += Offset(pad.left, pad.y)
            terrain += Offset(pad.left + pad.width, pad.y)
        }
        terrain.sortBy { it.x }
        launch()
    }

    private fun launch() {
        x =
            if (round == 1) padLeft + padWidth / 2f else if (padLeft + padWidth / 2f > 0.5f) 0.22f else 0.78f
        y = 0.1f
        vx = if (round == 1) 0f else if (x < 0.5f) 0.035f else -0.035f
        vy = 0f
        angle = 0f
        fuel = 1f
        zoom = 1f
        throttle = 0.55f
        landed = false
        crashed = false
        transition = 0f
        grace = 0.7f
        stopInput()
    }

    fun groundAt(x: Float): Float {
        val right = terrain.indexOfFirst { it.x >= x.coerceIn(0f, 1f) }.coerceAtLeast(0)
        if (right == 0) return terrain.firstOrNull()?.y ?: 1f
        val a = terrain[right - 1]
        val b = terrain[right]
        return a.y + (b.y - a.y) * ((x - a.x) / (b.x - a.x)).coerceIn(0f, 1f)
    }

    fun beginThrust(point: Offset) {
        if (transition > 0f) return
        firing = true
        touchOrigin = point
        stick = Offset.Zero
        throttle = 0.55f
        touchAngle = angle
        targetAngle = angle
        grace = 0f
    }

    fun steer(point: Offset) {
        val origin = touchOrigin ?: return
        stick = point - origin
        targetAngle = (touchAngle + stick.x / 70f).coerceIn(-0.85f, 0.85f)
        throttle = (0.55f - stick.y / 90f).coerceIn(0.1f, 1f)
    }

    override fun stopInput() {
        firing = false
        touchOrigin = null
        stick = Offset.Zero
        targetAngle = 0f
    }

    private fun vertex(
        dx: Float,
        dy: Float
    ): Offset {
        val px = (dx * cos(angle) - dy * sin(angle)) * shipScale
        val py = (dx * sin(angle) + dy * cos(angle)) * shipScale
        return Offset(x + px / width, y + py / span)
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        val clearance = groundAt(x) - y
        val targetZoom = 1f + ((0.3f - clearance) / 0.3f).coerceIn(0f, 1f) * 0.65f
        zoom += (targetZoom - zoom) * minOf(1f, seconds * 4f)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                if (landed) {
                    round++
                    newRound()
                } else launch()
            }
            return
        }
        if (grace > 0f) {
            grace -= seconds
            return
        }
        angle += (targetAngle - angle).coerceIn(-2.8f * seconds, 2.8f * seconds)
        val thrust = if (burning) 0.36f * throttle else 0f
        fuel = (fuel - if (burning) seconds * 0.075f * throttle else 0f).coerceAtLeast(0f)
        vx += sin(angle) * thrust * seconds
        vy += (0.115f + round.coerceAtMost(15) * 0.002f - cos(angle) * thrust) * seconds
        x += vx * seconds
        y += vy * seconds
        val side = 12f * shipScale / width
        if (x < side || x > 1f - side) {
            x = x.coerceIn(side, 1f - side)
            vx *= -0.25f
        }
        if (y < 10f * shipScale / span) {
            y = 10f * shipScale / span
            vy = maxOf(0f, vy)
        }
        val feet = listOf(vertex(-11f, 12f), vertex(11f, 12f))
        val hull = feet + listOf(vertex(-6f, -3f), vertex(6f, -3f), vertex(0f, -8f))
        if (hull.any { it.y >= groundAt(it.x) }) {
            val pad =
                pads.firstOrNull { pad -> feet.all { it.x in pad.left..(pad.left + pad.width) } && y < pad.y }
            if (pad != null && safeSpeed && vy >= 0f) {
                y = pad.y - 12f * shipScale / span
                angle = 0f
                vx = 0f
                vy = 0f
                landed = true
                score += (100 + round * 20 + (fuel * 60f).toInt()) * pad.multiplier
                transition = 1f
                effects.burst(
                    point = shipPoint,
                    count = 18
                )
            } else {
                lives--
                crashed = true
                effects.burst(
                    point = shipPoint,
                    count = 24
                )
                if (lives <= 0) phase = ArcadePhase.Over else transition = 1.1f
            }
            stopInput()
        }
    }

    override fun save(state: Bundle) {
        state.putFloat("throttle", throttle)
        state.putFloat("zoom", zoom)
        bonusPad?.let { state.putFloatArray("bonus", floatArrayOf(it.left, it.width, it.y)) }
        state.putFloatArray(
            "ship",
            floatArrayOf(
                x,
                y,
                vx,
                vy,
                angle,
                fuel,
                padLeft,
                padWidth,
                padY,
                time,
                transition,
                grace
            )
        )
        state.putInt("round", round)
        state.putInt("lives", lives)
        state.putBoolean("landed", landed)
        state.putBoolean("crashed", crashed)
        state.putFloatArray("terrain", terrain.flatMap { listOf(it.x, it.y) }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        throttle = state.getFloat("throttle", 0.55f)
        zoom = state.getFloat("zoom", 1f)
        bonusPad = state.getFloatArray("bonus")?.let { LandingPad(it[0], it[1], it[2], 2) }
        state.getFloatArray("ship")?.let {
            x = it[0]
            y = it[1]
            vx = it[2]
            vy = it[3]
            angle = it[4]
            fuel = it[5]
            padLeft = it[6]
            padWidth = it[7]
            padY = it[8]
            time = it[9]
            transition = it[10]
            grace = it[11]
        }
        round = state.getInt("round", 1)
        lives = state.getInt("lives", 3)
        landed = state.getBoolean("landed")
        crashed = state.getBoolean("crashed")
        terrain.clear()
        state.getFloatArray("terrain")?.let {
            for (index in it.indices step 2) terrain += Offset(it[index], it[index + 1])
        }
        stopInput()
    }
}