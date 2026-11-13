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
import androidx.compose.ui.geometry.CornerRadius
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
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
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
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SwapVerticalCircle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object GravityFlipGame : ArcadeGameDefinition {
    override val title = R.string.arcade_gravityflip
    override val description = R.string.arcade_gravityflip_sub
    override val icon get() = Icons.Outlined.SwapVerticalCircle
    override val order = 22

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::GravityFlipRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.flip() }
            ) {
                drawGravityBackground(style, run.width, run.height)
                val top = run.top
                val bottom = run.bottom
                val span = run.span
                val pace = run.pace
                pixelRect(
                    color = style.obstacleColor.copy(alpha = 0.07f),
                    x = 0f,
                    y = 0f,
                    width = run.width,
                    height = top
                )
                pixelRect(
                    color = style.obstacleColor.copy(alpha = 0.07f),
                    x = 0f,
                    y = bottom,
                    width = run.width,
                    height = run.height - bottom
                )
                repeat(16) { index ->
                    val near = index % 4 == 0
                    val drift = if (near) 0.65f else 0.2f
                    val x = ((index * 67f - run.distance * drift) % (run.width + 40f) +
                            run.width + 40f) % (run.width + 40f) - 20f
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.15f + pace * 0.1f),
                        x = x,
                        y = top + (index * 37 % 89) / 89f * span,
                        width = if (near) 3f + pace * 9f else 1f,
                        height = 1f
                    )
                }
                for (edge in listOf(top, bottom)) {
                    val floor = edge == bottom
                    pixelRect(
                        color = style.obstacleColor,
                        x = 0f,
                        y = edge,
                        width = run.width,
                        height = 2f
                    )
                    var x = -(run.distance % 432f) - 72f
                    var index = 0
                    while (x < run.width) {
                        val depth = 4f + index % 3 * 2f
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.25f),
                            x = x,
                            y = if (floor) edge + 2f else edge - depth,
                            width = 28f + index % 2 * 12f,
                            height = depth
                        )
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.4f),
                            x = x + 13f,
                            y = edge + if (floor) 12f else -12f,
                            width = 19f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.25f),
                            x = x + 48f,
                            y = edge + if (floor) 20f else -20f,
                            width = 5f,
                            height = 2f
                        )
                        x += 72f
                        index++
                    }
                }
                for ((x, width, depth, ceiling, _, broken, kind) in run.obstacles) {
                    if (broken) continue
                    val ranges = when (kind) {
                        1 -> listOf(0.38f to 0.62f)
                        2 -> listOf(0f to depth, 1f - depth to 1f)
                        else -> if (ceiling) listOf(0f to depth) else listOf(1f - depth to 1f)
                    }
                    for ((start, end) in ranges) {
                        val y = top + span * start
                        val h = span * (end - start)
                        val ink = if (kind == 2) style.hazardColor else style.obstacleColor
                        pixelRect(
                            color = ink,
                            x = x,
                            y = y + 2f,
                            width = width,
                            height = h - 4f
                        )
                        pixelRect(
                            color = ink,
                            x = x + 2f,
                            y = y,
                            width = width - 4f,
                            height = h
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.3f),
                            x = x + 4f,
                            y = y + 4f,
                            width = 7f,
                            height = 2f
                        )
                        val crack = y + h * 0.38f
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = x,
                            y = crack,
                            width = width * 0.42f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = x + width * 0.42f - 2f,
                            y = crack + 2f,
                            width = 2f,
                            height = 5f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.3f),
                            x = x + width * 0.65f,
                            y = y + h * 0.74f,
                            width = width * 0.35f,
                            height = 2f
                        )
                        if (kind == 1) {
                            for (index in 0..2) {
                                pixelRect(
                                    color = style.accentColor,
                                    x = x + width / 2f - 3f + index * 2f,
                                    y = y + h / 2f - 4f + index * 3f,
                                    width = 3f,
                                    height = 4f
                                )
                            }
                        }
                    }
                }
                for ((x, y, value) in run.beacons) {
                    val center = Offset(x = x, y = top + y * span)
                    val path = Path().apply {
                        moveTo(center.x, center.y - 6f)
                        lineTo(center.x + 5f, center.y)
                        lineTo(center.x, center.y + 6f)
                        lineTo(center.x - 5f, center.y)
                        close()
                    }
                    pixelCircle(
                        color = style.accentColor.copy(alpha = 0.13f),
                        radius = 10f,
                        center = center
                    )
                    drawPath(
                        path = path,
                        color = style.accentColor
                    )
                    if (value > 1) {
                        pixelRect(style.playerDetailColor, center.x - 2f, center.y - 3f, 1f, 6f)
                        pixelRect(style.playerDetailColor, center.x + 1f, center.y - 3f, 1f, 6f)
                    }
                }
                val center = Offset(x = run.playerX, y = top + run.y * span)
                run.trail.forEachIndexed { index, point ->
                    if (index == 0) return@forEachIndexed
                    val previous = run.trail[index - 1]
                    drawLine(
                        color = style.playerColor.copy(alpha = (index + 1) * 0.035f),
                        start = Offset(x = previous.x, y = top + previous.y * span),
                        end = Offset(x = point.x, y = top + point.y * span),
                        strokeWidth = run.radius * (0.15f + index * 0.035f)
                    )
                }
                if (run.shield) pixelCircle(
                    color = style.accentColor,
                    radius = run.radius + 5f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )
                val tilt = if (run.velocity == 0f) 0f else run.velocity * 4f
                rotate(degrees = tilt, pivot = center) {
                    scale(
                        scaleX = 1f,
                        scaleY = if (run.gravity == 1) 1f else -1f,
                        pivot = center
                    ) {
                        val unit = run.radius / 14f
                        val stride = run.velocity == 0f && (run.distance / 18f).toInt() % 2 == 0
                        translate(left = center.x - 12f * unit, top = center.y - 14f * unit) {
                            scale(scale = unit, pivot = Offset.Zero) {
                                drawArcadeSprite(
                                    sprite = if (stride) GravityStride else GravityLeap,
                                    tint = style.playerColor,
                                    x = 0f,
                                    y = 0f
                                )
                                reliefSprite(
                                    sprite = GravityRunner,
                                    color = style.playerColor,
                                    shade = lerp(
                                        style.playerColor,
                                        style.overlayColor.copy(alpha = 1f),
                                        0.25f
                                    ),
                                    light = lerp(style.playerColor, style.textColor, 0.2f),
                                    x = 0f,
                                    y = 0f
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = 17f,
                                    y = 3f,
                                    width = 6f,
                                    height = 4f
                                )
                                pixelRect(
                                    color = style.accentColor,
                                    x = 11f,
                                    y = 11f,
                                    width = 4f,
                                    height = 2f
                                )
                            }
                        }
                    }
                }
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private val GravityRunner = ArcadeSprite(
    width = 24,
    height = 28,
    data = "M14 0H21V2H23V8H21V10H17V12H19V14H23V17H19V16H16V15H15V18H11V20H7V17H9V14" +
            "H7V16H4V15H2V12H5V11H7V13H10V10H12V8H11V3H12V1H14Z"
)
private val GravityStride = ArcadeSprite(
    width = 24,
    height = 28,
    data = "M11 17H14V19H18V21H20V25H24V28H17V23H14V22H10Z " +
            "M8 17H11V20H8V23H3V22H0V19H3V18H5V20H7V19H8Z"
)
private val GravityLeap = ArcadeSprite(
    width = 24,
    height = 28,
    data = "M10 17H14V21H11V24H9V26H12V28H5V25H7V22H9V20H10Z " +
            "M13 17H16V19H21V21H22V24H19V25H17V23H18V22H14V21H12V19Z"
)

private fun DrawScope.drawGravityBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 107f).toInt()
    val rows = ceil(height / 91f).toInt()
    val ink = style.detailColor.copy(alpha = 0.11f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val point = Offset(column * 107f + 6f, row * 91f + 5f)
        drawRoundRect(ink.copy(alpha = 0.045f), point, Size(99f, 82f), CornerRadius(4f))
        drawRoundRect(ink, point, Size(99f, 82f), CornerRadius(4f), style = Stroke(0.8f))
        val center = point + Offset(48f, 39f)
        drawCircle(ink.copy(alpha = 0.15f), 22f, center, style = Stroke(0.8f))
        drawCircle(ink, 17f, center, style = Stroke(0.8f))
        for (blade in 0..3) {
            val angle = blade * PI.toFloat() / 2f + 0.45f
            val a = center + Offset(cos(angle), sin(angle)) * 6f
            val b = center + Offset(cos(angle + 0.5f), sin(angle + 0.5f)) * 16f
            drawLine(ink, a, b, 2f)
        }
        for (screw in listOf(point + Offset(6f, 6f), point + Offset(92f, 75f))) pixelRect(
            ink,
            screw.x,
            screw.y,
            2f,
            2f
        )
    }
}

@Serializable
private data class GravityObstacle(
    var x: Float,
    val width: Float,
    val depth: Float,
    val ceiling: Boolean,
    var passed: Boolean = false,
    var broken: Boolean = false,
    val kind: Int = 0
)

@Serializable
private data class GravityBeacon(
    var x: Float,
    val y: Float,
    val value: Int = 1
)

@Serializable
private class GravityFlipRun : ArcadeRun() {
    val obstacles = mutableListOf<GravityObstacle>()
    val beacons = mutableListOf<GravityBeacon>()
    val trail: MutableList<ArcadeOffset> = mutableListOf()

    @Transient
    val effects = ArcadeEffects()
    val span get() = minOf(230f, height - topInset - 40f)
    val top get() = topInset + (height - topInset - span) / 2f
    val bottom get() = top + span
    val radius get() = minOf(10f, span * 0.055f)
    val playerX get() = (width * 0.22f).coerceIn(55f, 120f)
    var y = 0.94f
    var velocity = 0f
    var gravity = 1
    var distance = 0f
    var shield = false
    private var elapsed = 0f
    private var charge = 0
    private var passed = 0
    private var timer = 0.7f
    private var nextCeiling = false
    private val patterns: ArcadeDeque<Int> = ArrayDeque()
    private var previousPattern = -1
    private var trailTime = 0f
    private var flipTime = 0f
    val pace get() = 1f - exp(-elapsed / 45f)
    val speed get() = 150f + 150f * pace

    override fun reset() {
        obstacles.clear()
        beacons.clear()
        trail.clear()
        effects.clear()
        y = 1f - radius / span
        velocity = 0f
        gravity = 1
        distance = 0f
        shield = false
        elapsed = 0f
        charge = 0
        passed = 0
        timer = 0.65f
        nextCeiling = false
        patterns.clear()
        previousPattern = -1
        trailTime = 0f
        flipTime = 0f
    }

    fun flip() {
        if (flipTime > 0f) return
        gravity = -gravity
        velocity = gravity * 1.1f
        flipTime = 0.1f
        effects.burst(
            point = Offset(x = playerX, y = top + y * span),
            count = 5
        )
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        effects.advance(seconds)
        flipTime = (flipTime - seconds).coerceAtLeast(0f)
        velocity = (velocity + gravity * 7.2f * seconds).coerceIn(-3.8f, 3.8f)
        y += velocity * seconds
        val airborne = abs(velocity) > 1f
        val margin = radius / span
        if (y < margin || y > 1f - margin) {
            y = y.coerceIn(margin, 1f - margin)
            velocity = 0f
            if (airborne) effects.burst(
                point = Offset(x = playerX, y = if (gravity > 0) bottom else top),
                count = 5
            )
        }
        val step = speed * seconds
        distance += step
        timer -= seconds
        if (timer <= 0f) spawnPattern()
        for (obstacle in obstacles) {
            obstacle.x -= step
            if (!obstacle.passed && obstacle.x + obstacle.width < playerX - radius) {
                obstacle.passed = true
                passed++
                score += 5
            }
            if (!obstacle.broken &&
                obstacle.x < playerX + radius * 0.8f &&
                obstacle.x + obstacle.width > playerX - radius * 0.8f &&
                when (obstacle.kind) {
                    1 -> y + margin * 0.8f > 0.38f && y - margin * 0.8f < 0.62f
                    2 -> y - margin * 0.8f < obstacle.depth || y + margin * 0.8f > 1f - obstacle.depth
                    else -> if (obstacle.ceiling) {
                        y - margin * 0.8f < obstacle.depth
                    } else {
                        y + margin * 0.8f > 1f - obstacle.depth
                    }
                }
            ) {
                effects.burst(
                    point = Offset(x = playerX, y = top + y * span),
                    count = 18
                )
                if (shield) {
                    shield = false
                    obstacle.broken = true
                } else {
                    phase = ArcadePhase.Over
                    return
                }
            }
        }
        obstacles.removeAll { it.x + it.width < -8f }
        val iterator = beacons.iterator()
        while (iterator.hasNext()) {
            val beacon = iterator.next()
            beacon.x -= step
            if (abs(beacon.x - playerX) < radius + 5f && abs(beacon.y - y) * span < radius + 6f) {
                score += 3 * beacon.value
                if (!shield) {
                    charge += beacon.value
                    if (charge >= 12) {
                        shield = true
                        charge = 0
                    }
                }
                effects.burst(
                    point = Offset(x = beacon.x, y = top + beacon.y * span),
                    count = 7
                )
                iterator.remove()
            } else if (beacon.x < -10f) iterator.remove()
        }
        trailTime += seconds
        for (index in trail.indices) trail[index] = trail[index] - Offset(x = step, y = 0f)
        if (trailTime >= 0.035f) {
            trailTime = 0f
            trail += Offset(x = playerX, y = y)
            if (trail.size > 7) trail.removeAt(0)
        }
    }

    private fun spawnPattern() {
        if (patterns.isEmpty()) {
            val choices = when {
                passed < 4 -> listOf(0, 1, 0)
                passed < 12 -> listOf(0, 1, 2, 3)
                else -> listOf(0, 1, 2, 3, 4)
            }
            val shuffled = choices.shuffled().toMutableList()
            if (shuffled.first() == previousPattern) shuffled.reverse()
            patterns.addAll(shuffled)
        }
        val pattern = patterns.removeFirst()
        previousPattern = pattern
        val kind = when (pattern) {
            3 -> 1
            4 -> 2
            else -> 0
        }
        val w = when (pattern) {
            1 -> 66f
            2 -> 20f
            3 -> 48f
            4 -> 8f
            else -> 28f
        }
        val depth = when (pattern) {
            2 -> 0.62f
            4 -> 0.16f
            else -> 0.28f + Random.nextFloat() * 0.1f
        }
        val x = maxOf(width + 20f, playerX + speed * 1.2f)
        obstacles += GravityObstacle(
            x = x,
            width = w,
            depth = depth,
            ceiling = nextCeiling,
            kind = kind
        )
        val bank = radius / span + 0.025f
        repeat(3) { index ->
            beacons += GravityBeacon(
                x = x + w / 2f + index * 19f,
                y = if (kind == 2) 0.5f else if (nextCeiling) 1f - bank else bank
            )
        }
        if (passed >= 12 && pattern % 2 == 0) beacons += GravityBeacon(
            x = x + w + speed * 0.42f,
            y = if (nextCeiling) 0.68f else 0.32f,
            value = 2
        )
        // A middle gate asks for a timed flip. The next obstacle leaves a full crossing to settle.
        if (kind == 0 || kind == 2) nextCeiling = !nextCeiling
        timer += w / speed + 1.05f - pace * 0.3f + Random.nextFloat() * 0.25f +
                if (kind == 2) 0.2f else 0f
    }

    override fun onResize(previous: Size) {
        obstacles.forEach { it.x *= width / previous.width }
        beacons.forEach { it.x *= width / previous.width }
        trail.clear()
        y = y.coerceIn(radius / span, 1f - radius / span)
    }
}