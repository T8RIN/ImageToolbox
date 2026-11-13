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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Rocket
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object MoonLandingGame : ArcadeGameDefinition {
    override val title = R.string.arcade_moonlanding
    override val description = R.string.arcade_moonlanding_sub
    override val icon get() = Icons.Outlined.Rocket
    override val order = 27

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::MoonLandingRun
        )
        val horizonPath = remember { Path() }
        val groundPath = remember { Path() }
        val surfacePath = remember { Path() }
        val flamePath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onPress = run::beginThrust,
                onDrag = run::steer,
                onRelease = run::stopInput
            ) {
                drawLunarSky(style, run.width, run.height)
                val groundBottom =
                    maxOf(run.worldBottom, (run.height - run.camera.y) / run.zoom + 24f)
                translate(left = run.camera.x, top = run.camera.y) {
                    scale(scale = run.zoom, pivot = Offset.Zero) {
                        val horizon = horizonPath.apply {
                            rewind()
                            moveTo(-30f, groundBottom)
                            repeat(15) { ridge ->
                                lineTo(
                                    (ridge - 1) * run.worldWidth / 12f,
                                    run.top + run.span * (0.64f + ((ridge * 37 + run.round * 11) % 23) / 100f)
                                )
                            }
                            lineTo(run.worldWidth + 30f, groundBottom)
                            close()
                        }
                        drawPath(
                            path = horizon,
                            color = style.detailColor.copy(alpha = 0.12f)
                        )
                        val ground = groundPath.apply {
                            rewind()
                            moveTo(0f, groundBottom)
                            run.terrain.forEach {
                                lineTo(
                                    it.x * run.worldWidth,
                                    run.top + it.y * run.span
                                )
                            }
                            lineTo(run.worldWidth, groundBottom)
                            close()
                        }
                        drawPath(
                            path = ground,
                            color = style.obstacleColor.copy(alpha = 0.17f)
                        )
                        val surface = surfacePath.apply {
                            rewind()
                            run.terrain.forEachIndexed { index, point ->
                                val x = point.x * run.worldWidth
                                val y = run.top + point.y * run.span
                                if (index == 0) moveTo(x, y) else lineTo(x, y)
                            }
                        }
                        drawPath(
                            path = surface,
                            color = style.obstacleColor.copy(alpha = 0.6f),
                            style = Stroke(width = 1.5f)
                        )
                        repeat(48) {
                            val x = ((it * 79 + 23) % 359) / 359f
                            val surface = run.top + run.groundAt(x) * run.span
                            val y = surface + 8f + it % 4 * 5f
                            if (y < run.worldBottom - 6f) pixelRect(
                                color = style.detailColor.copy(alpha = 0.5f),
                                x = x * run.worldWidth,
                                y = y,
                                width = 3f + it % 5,
                                height = 2f
                            )
                        }
                        for ((left, width, y, multiplier) in run.pads) {
                            val padY = run.top + y * run.span
                            val padX = left * run.worldWidth
                            val padWidth = width * run.worldWidth
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
                            repeat(multiplier) { dot ->
                                pixelRect(
                                    color = padColor,
                                    x = padX + padWidth / 2f + (dot - (multiplier - 1) / 2f) * 7f - 1.5f,
                                    y = padY + 6f,
                                    width = 3f,
                                    height = 3f
                                )
                            }
                            if (abs(run.x - left - width / 2f) < width && y - run.y < 0.35f) {
                                for (side in 0..1) {
                                    val x = padX + side * padWidth
                                    var guideY = padY - 20f
                                    while (guideY > run.shipPoint.y - 12f) {
                                        pixelRect(
                                            color = padColor.copy(alpha = 0.3f),
                                            x = x,
                                            y = guideY,
                                            width = 1f,
                                            height = 3f
                                        )
                                        guideY -= 8f
                                    }
                                }
                            }
                        }
                        val center = run.shipPoint
                        if (!run.crashed) translate(left = center.x, top = center.y) {
                            rotate(degrees = run.angle * 180f / PI.toFloat(), pivot = Offset.Zero) {
                                scale(scale = run.shipScale, pivot = Offset.Zero) {
                                    if (run.burning) {
                                        val flame = flamePath.apply {
                                            rewind()
                                            moveTo(-3f, 6f)
                                            lineTo(
                                                0f,
                                                10f + run.throttle * 16f + sin(
                                                    run.time * 45f
                                                ) * 2f
                                            )
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
                                    reliefSprite(
                                        sprite = LanderCabin,
                                        color = style.playerColor,
                                        shade = lerp(
                                            style.playerColor,
                                            style.overlayColor.copy(alpha = 1f),
                                            0.25f
                                        ),
                                        light = lerp(style.playerColor, style.textColor, 0.2f),
                                        x = -8f,
                                        y = -10f
                                    )
                                    drawArcadeSprite(
                                        sprite = LanderWindow,
                                        tint = style.playerDetailColor,
                                        x = -8f,
                                        y = -10f
                                    )
                                    pixelRect(
                                        color = style.obstacleColor,
                                        x = -6f,
                                        y = 4f,
                                        width = 12f,
                                        height = 3f
                                    )
                                    pixelRect(
                                        color = style.playerColor,
                                        x = 4f,
                                        y = -14f,
                                        width = 1f,
                                        height = 6f
                                    )
                                    pixelRect(
                                        color = style.accentColor,
                                        x = 3f,
                                        y = -15f,
                                        width = 3f,
                                        height = 2f
                                    )
                                    for (side in listOf(-1f, 1f)) {
                                        drawLine(
                                            color = style.playerColor,
                                            start = Offset(x = side * 5f, y = 3f),
                                            end = Offset(x = side * 8f, y = 10f),
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
                            val drift =
                                Offset(x = run.vx * run.width, y = run.vy * run.span / 1.8f) * 0.35f
                            if (drift.getDistance() > 3f) drawLine(
                                color = if (run.safeSpeed) style.accentColor.copy(alpha = 0.5f) else style.hazardColor.copy(
                                    alpha = 0.65f
                                ),
                                start = run.shipPoint + Offset(x = 0f, y = 17f * run.shipScale),
                                end = run.shipPoint + Offset(
                                    x = 0f,
                                    y = 17f * run.shipScale
                                ) + drift,
                                strokeWidth = 1.5f
                            )
                        }
                        run.effects.draw(
                            scope = this,
                            color = if (run.crashed) style.hazardColor else style.accentColor
                        )
                    }
                }
                run.touchOrigin?.let { origin ->
                    val point = origin + Offset(
                        x = run.stick.x.coerceIn(-34f, 34f),
                        y = run.stick.y.coerceIn(-34f, 34f)
                    )
                    pixelCircle(
                        color = style.detailColor.copy(alpha = 0.4f),
                        radius = 27f,
                        center = origin,
                        style = Stroke(width = 1f)
                    )
                    drawLine(
                        color = style.accentColor.copy(alpha = 0.5f),
                        start = origin,
                        end = point,
                        strokeWidth = 2f
                    )
                    pixelCircle(
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
                    topLeft = Offset(x = 76f, y = fuelY),
                    size = Size(width = 34f, height = 10f),
                    style = Stroke(width = 1f)
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
}

private val LanderCabin = ArcadeSprite(
    width = 16,
    height = 16,
    data = "M5 0H11V2H14V5H16V12H13V14H3V12H0V5H2V2H5Z"
)

private val LanderWindow = ArcadeSprite(
    width = 16,
    height = 16,
    data = "M4 3H12V5H13V8H3V5H4Z M5 11H11V12H5Z"
)

private fun DrawScope.drawLunarSky(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 145f).toInt()
    for (column in 0 until columns) {
        val origin = Offset(column * 145f + 19f, height * (0.16f + column % 3 * 0.12f))
        val stars = listOf(
            origin,
            origin + Offset(29f, -12f),
            origin + Offset(57f, 9f),
            origin + Offset(79f, -6f)
        )
        for (index in 0..2) drawLine(
            style.detailColor.copy(alpha = 0.1f),
            stars[index],
            stars[index + 1],
            0.6f
        )
        for ((index, point) in stars.withIndex()) {
            val ink = style.detailColor.copy(alpha = if (index == 1) 0.36f else 0.22f)
            pixelRect(ink, point.x, point.y, 1f, if (index == 1) 3f else 1f)
            if (index == 1) pixelRect(ink, point.x - 1f, point.y + 1f, 3f, 1f)
        }
    }
    val count = (width * height / 2100f).toInt().coerceAtLeast(25)
    repeat(count) { index ->
        val x = ((index * 83 + index * index * 11 + 17) % 997) / 997f * width
        val y = ((index * 127 + 31) % 419) / 419f * height
        pixelRect(style.detailColor.copy(alpha = 0.16f + index % 3 * 0.04f), x, y, 1f, 1f)
    }
}

@Serializable
private data class LandingPad(
    val left: Float,
    val width: Float,
    val y: Float,
    val multiplier: Int = 1
)

@Serializable
private class MoonLandingRun : ArcadeRun() {
    val terrain: MutableList<ArcadeOffset> = mutableListOf()

    @Transient
    val effects = ArcadeEffects()
    val top get() = topInset + 12f
    val span get() = (height - top - 12f) * 1.8f
    val worldBottom get() = top + span + 12f
    val shipScale get() = minOf(width / 360f, (height - top - 12f) / 180f).coerceIn(0.85f, 1.25f)
    val worldWidth get() = width * 2.8f
    val shipPoint get() = Offset(x = x * worldWidth, y = top + y * span)
    val camera
        get() = Offset(
            x = (width / 2f - shipPoint.x * zoom).coerceIn(width - worldWidth * zoom, 0f),
            y = (topInset + (height - topInset) * 0.4f - shipPoint.y * zoom)
                .coerceAtMost(topInset + 8f - top * zoom)
        )
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
    val pads get() = listOfNotNull(LandingPad(left = padLeft, width = padWidth, y = padY), bonusPad)
    var throttle = 0.55f
    var zoom = 0.52f

    @Transient
    var touchOrigin: Offset? = null
        private set

    @Transient
    var stick = Offset.Zero
        private set
    var round = 1
    var lives = 3
    private var cleanLandings = 0
    var time = 0f
    var landed = false
    var crashed = false

    @Transient
    private var firing = false

    @Transient
    private var targetAngle = 0f

    @Transient
    private var touchAngle = 0f
    var transition = 0f
        private set
    private var grace = 0.7f
    val burning get() = firing && fuel > 0f && transition <= 0f
    val safeSpeed
        get() = abs(vx) <= 0.1f && abs(vy) <= 0.13f - round.coerceAtMost(16) * 0.002f && abs(
            angle
        ) <= 0.22f

    override fun isValid(): Boolean = super.isValid() &&
            terrain.size >= 2 && terrain.zipWithNext().all { (a, b) -> a.x < b.x }

    override fun reset() {
        round = 1
        lives = 3
        cleanLandings = 0
        time = 0f
        effects.clear()
        newRound()
    }

    private fun newRound() {
        padWidth = (0.225f - round * 0.005f).coerceAtLeast(0.13f) / 2.8f
        padLeft = 0.12f + Random.nextFloat() * (0.76f - padWidth)
        padY = 0.77f + Random.nextFloat() * 0.1f
        bonusPad = if (round > 1) {
            val w = (0.15f - round * 0.003f).coerceAtLeast(0.1f) / 2.8f
            val left = if (padLeft > 0.4f) 0.05f else 0.94f - w
            LandingPad(
                left = left,
                width = w,
                y = 0.7f + Random.nextFloat() * 0.15f,
                multiplier = 2
            )
        } else null
        terrain.clear()
        val pads = pads
        repeat(45) { index ->
            val x = index / 44f
            if (pads.none { x in it.left..(it.left + it.width) }) terrain += Offset(
                x = x, y = 0.66f + Random.nextFloat() * 0.29f
            )
        }
        for ((left, width, y) in pads) {
            terrain += Offset(x = left, y = y)
            terrain += Offset(x = left + width, y = y)
        }
        terrain.sortBy { it.x }
        launch()
    }

    private fun launch() {
        animateLevel()
        x =
            if (round == 1) padLeft + padWidth / 2f else if (padLeft + padWidth / 2f > 0.5f) 0.22f else 0.78f
        y = 0.1f
        vx = if (round == 1) 0f else if (x < 0.5f) 0.035f else -0.035f
        vy = 0f
        angle = 0f
        fuel = 1f
        zoom = 0.52f
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
        return Offset(x = x + px / worldWidth, y = y + py / span)
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        val clearance = padY - y
        val approach = ((0.5f - clearance) / 0.5f).coerceIn(0f, 1f)
        val targetZoom = 0.52f + approach * approach * (3f - 2f * approach) * 1.13f
        zoom += ((targetZoom - zoom) * minOf(1f, seconds * 2.5f))
            .coerceIn(-seconds * 0.6f, seconds * 0.6f)
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
        x += vx * seconds / 2.8f
        y += vy * seconds / 1.8f
        val side = 12f * shipScale / worldWidth
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
                cleanLandings++
                if (cleanLandings % 3 == 0) lives = (lives + 1).coerceAtMost(3)
                score += (100 + round * 20 + (fuel * 60f).toInt()) * pad.multiplier
                transition = 1f
                effects.burst(
                    point = shipPoint,
                    count = 18
                )
            } else {
                lives--
                cleanLandings = 0
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
}