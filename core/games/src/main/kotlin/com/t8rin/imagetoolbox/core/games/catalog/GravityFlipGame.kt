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

@file:UseSerializers(ArcadeOffsetSerializer::class, ArcadeDequeSerializer::class)

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeDequeSerializer
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GravityFlipGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::GravityFlipRun
    )
    ArcadeBoard(
        game = ArcadeGame.GravityFlip,
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
            onPress = { run.flip() }
        ) {
            val top = run.top
            val bottom = run.bottom
            val span = run.span
            pixelRect(
                color = style.obstacleColor.copy(alpha = 0.055f),
                x = 0f,
                y = 0f,
                width = run.width,
                height = top
            )
            pixelRect(
                color = style.obstacleColor.copy(alpha = 0.055f),
                x = 0f,
                y = bottom,
                width = run.width,
                height = run.height - bottom
            )
            repeat(7) { column ->
                val x =
                    ((column * 83f - run.distance * 0.12f) % (run.width + 83f) + run.width + 83f) % (run.width + 83f) - 40f
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.12f),
                    x = x,
                    y = 0f,
                    width = 2f,
                    height = (top - 10f).coerceAtLeast(0f)
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.12f),
                    x = x + 20f,
                    y = bottom + 10f,
                    width = 2f,
                    height = (run.height - bottom - 20f).coerceAtLeast(0f)
                )
            }
            repeat(18) {
                val x =
                    ((it * 67f - run.distance * 0.22f) % (run.width + 40f) + run.width + 40f) % (run.width + 40f) - 20f
                val y = top + ((it * 37 % 89) / 89f) * span
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.23f),
                    x = x,
                    y = y,
                    width = 13f,
                    height = 1f
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.3f),
                    x = x + 13f,
                    y = y - 2f,
                    width = 3f,
                    height = 3f
                )
            }
            for (edge in listOf(top, bottom)) {
                val floor = edge == bottom
                pixelRect(
                    color = style.playerColor.copy(alpha = 0.12f),
                    x = 0f,
                    y = if (floor) edge else edge - 10f,
                    width = run.width,
                    height = 10f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = 0f,
                    y = edge,
                    width = run.width,
                    height = 2f
                )
                var x = -(run.distance % 32f)
                while (x < run.width) {
                    pixelRect(
                        color = style.detailColor,
                        x = x,
                        y = edge + if (floor) 5f else -6f,
                        width = 12f,
                        height = 2f
                    )
                    x += 32f
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
                        color = ink.copy(alpha = 0.18f),
                        x = x,
                        y = y,
                        width = width,
                        height = h
                    )
                    pixelRect(
                        color = ink,
                        x = x,
                        y = y + 3f,
                        width = width,
                        height = h - 6f
                    )
                    pixelRect(
                        color = ink,
                        x = x + 3f,
                        y = y,
                        width = (width - 6f).coerceAtLeast(1f),
                        height = h
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.35f),
                        x = x + 4f,
                        y = y + 5f,
                        width = 2f,
                        height = h - 10f
                    )
                    val tip = if (start == 0f) y + h - 4f else y + 2f
                    pixelRect(
                        color = style.accentColor,
                        x = x + 2f,
                        y = tip,
                        width = width - 4f,
                        height = 2f
                    )
                    if (kind == 1) {
                        pixelRect(
                            color = style.hazardColor,
                            x = x + width / 2f - 2f,
                            y = y + h / 2f - 3f,
                            width = 4f,
                            height = 6f
                        )
                    } else if (width > 20f) {
                        var mark = y + 12f
                        while (mark < y + h - 10f) {
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.25f),
                                x = x + 8f,
                                y = mark,
                                width = width - 12f,
                                height = 2f
                            )
                            mark += 12f
                        }
                    }
                }
            }
            for ((x, y) in run.beacons) {
                val center = Offset(x = x, y = top + y * span)
                val path = Path().apply {
                    moveTo(center.x, center.y - 6f)
                    lineTo(center.x + 5f, center.y)
                    lineTo(center.x, center.y + 6f)
                    lineTo(center.x - 5f, center.y)
                    close()
                }
                drawCircle(
                    color = style.accentColor.copy(alpha = 0.13f),
                    radius = 10f,
                    center = center
                )
                drawPath(
                    path = path,
                    color = style.accentColor
                )
            }
            val center = Offset(x = run.playerX, y = top + run.y * span)
            run.trail.forEachIndexed { index, point ->
                drawCircle(
                    color = style.playerColor.copy(alpha = (index + 1) * 0.025f),
                    radius = run.radius * 0.7f,
                    center = Offset(x = point.x, y = top + point.y * span)
                )
            }
            if (run.shield) drawCircle(
                color = style.accentColor,
                radius = run.radius + 5f,
                center = center,
                style = Stroke(width = 1.5f)
            )
            val tilt = if (run.velocity == 0f) 0f else run.velocity * 4f
            rotate(degrees = tilt, pivot = center) {
                scale(1f, if (run.gravity == 1) 1f else -1f, center) {
                    val unit = run.radius / 8f
                    val stride =
                        if (run.velocity == 0f) sin(run.distance * 0.17f) * 1.4f else 0f
                    translate(left = center.x - 8f * unit, top = center.y - 8f * unit) {
                        scale(unit, Offset.Zero) {
                            drawArcadeSprite(
                                sprite = GravityRunner,
                                tint = style.playerColor,
                                x = 0f,
                                y = 0f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = 9f,
                                y = 3f,
                                width = 5f,
                                height = 4f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = 13f,
                                y = 4f,
                                width = 1f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.obstacleColor,
                                x = 1f,
                                y = 7f,
                                width = 3f,
                                height = 6f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = 4f,
                                y = 14f + stride,
                                width = 4f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = 10f,
                                y = 14f - stride,
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

private val GravityRunner = ArcadeSprite(
    width = 16,
    height = 16,
    data = "M6 0H13V1H15V7H13V9H14V12H12V14H14V16H10V13H8V16H4V14H6V12H4V8H5V7H4V2H6Z"
)

@Serializable
internal data class GravityObstacle(
    var x: Float,
    val width: Float,
    val depth: Float,
    val ceiling: Boolean,
    var passed: Boolean = false,
    var broken: Boolean = false,
    val kind: Int = 0
)

@Serializable
internal data class GravityBeacon(
    var x: Float,
    val y: Float
)

@Serializable
internal class GravityFlipRun : ArcadeRun() {
    val obstacles = mutableListOf<GravityObstacle>()
    val beacons = mutableListOf<GravityBeacon>()
    val trail = mutableListOf<Offset>()

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
    private var charge = 0
    private var passed = 0
    private var timer = 0.7f
    private var nextCeiling = false
    private val patterns = ArrayDeque<Int>()
    private var previousPattern = -1
    private var trailTime = 0f
    private var flipTime = 0f
    val speed get() = 135f + 90f * passed / (passed + 50f)

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
            if (!obstacle.broken && obstacle.x < playerX + radius * 0.8f && obstacle.x + obstacle.width > playerX - radius * 0.8f &&
                when (obstacle.kind) {
                    1 -> y + margin * 0.8f > 0.38f && y - margin * 0.8f < 0.62f
                    2 -> y - margin * 0.8f < obstacle.depth || y + margin * 0.8f > 1f - obstacle.depth
                    else -> if (obstacle.ceiling) y - margin * 0.8f < obstacle.depth else y + margin * 0.8f > 1f - obstacle.depth
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
                score += 3
                if (!shield) {
                    charge++
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
        // A middle gate asks for a timed flip. The next obstacle leaves a full crossing to settle.
        if (kind == 0 || kind == 2) nextCeiling = !nextCeiling
        timer += w / speed + 1.05f + Random.nextFloat() * 0.35f + if (kind == 2) 0.2f else 0f
    }

    override fun onResize(previous: Size) {
        obstacles.forEach { it.x *= width / previous.width }
        beacons.forEach { it.x *= width / previous.width }
        trail.clear()
        y = y.coerceIn(radius / span, 1f - radius / span)
    }
}