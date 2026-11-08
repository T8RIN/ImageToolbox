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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.Stroke
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun GravityFlipGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
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
                y = run.topInset,
                width = run.width,
                height = top - run.topInset
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
                    y = run.topInset + 10f,
                    width = 2f,
                    height = (top - run.topInset - 20f).coerceAtLeast(0f)
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
            val panelWidth = 94f
            var panel = -(run.distance * 0.12f % panelWidth)
            while (panel < run.width) {
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.12f),
                    x = panel,
                    y = top + 10f,
                    width = 1f,
                    height = span - 20f
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.1f),
                    x = panel + 12f,
                    y = top + span * 0.45f,
                    width = 34f,
                    height = 3f
                )
                panel += panelWidth
            }
            for (obstacle in run.obstacles) {
                if (obstacle.broken) continue
                val h = span * obstacle.depth
                val y = if (obstacle.ceiling) top else bottom - h
                val ink = if (obstacle.wide) style.accentColor else style.obstacleColor
                pixelRect(
                    color = ink.copy(alpha = 0.16f),
                    x = obstacle.x,
                    y = y,
                    width = obstacle.width,
                    height = h
                )
                pixelRect(
                    color = ink,
                    x = obstacle.x,
                    y = y,
                    width = 3f,
                    height = h
                )
                pixelRect(
                    color = ink,
                    x = obstacle.x + obstacle.width - 3f,
                    y = y,
                    width = 3f,
                    height = h
                )
                pixelRect(
                    color = ink,
                    x = obstacle.x,
                    y = if (obstacle.ceiling) y + h - 3f else y,
                    width = obstacle.width,
                    height = 3f
                )
                val inset = 5f
                pixelRect(
                    color = ink.copy(alpha = 0.4f),
                    x = obstacle.x + inset,
                    y = y + inset,
                    width = obstacle.width - inset * 2f,
                    height = 3f
                )
                pixelRect(
                    color = ink.copy(alpha = 0.35f),
                    x = obstacle.x + inset,
                    y = y + h - inset - 3f,
                    width = obstacle.width - inset * 2f,
                    height = 3f
                )
                val tip = if (obstacle.ceiling) y + h else y
                repeat(3) { tooth ->
                    val x = obstacle.x + 4f + tooth * (obstacle.width - 8f) / 3f
                    pixelRect(
                        color = style.accentColor,
                        x = x,
                        y = tip - 2f,
                        width = 4f,
                        height = 4f
                    )
                }
                val lightY = if (obstacle.ceiling) y + h - 9f else y + 6f
                pixelRect(
                    color = style.hazardColor,
                    x = obstacle.x + obstacle.width / 2f - 2f,
                    y = lightY,
                    width = 4f,
                    height = 3f
                )
            }
            for (beacon in run.beacons) {
                val center = Offset(beacon.x, top + beacon.y * span)
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
            val center = Offset(run.playerX, top + run.y * span)
            run.trail.forEachIndexed { index, point ->
                drawCircle(
                    color = style.playerColor.copy(alpha = (index + 1) * 0.025f),
                    radius = run.radius * 0.7f,
                    center = Offset(point.x, top + point.y * span)
                )
            }
            if (run.shield) drawCircle(
                color = style.accentColor,
                radius = run.radius + 5f,
                center = center,
                style = Stroke(1.5f)
            )
            val tilt = if (run.velocity == 0f) 0f else run.velocity * 4f
            rotate(tilt, center) {
                scale(1f, if (run.gravity == 1) 1f else -1f, center) {
                    val r = run.radius
                    val stride =
                        if (run.velocity == 0f) kotlin.math.sin(run.distance * 0.16f) * 2f else 0f
                    pixelRect(
                        color = style.playerColor,
                        x = center.x - r,
                        y = center.y - r,
                        width = r * 2,
                        height = r * 1.65f
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = center.x - r * 0.5f,
                        y = center.y - r * 0.6f,
                        width = r * 1.5f,
                        height = r * 0.45f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = center.x - r * 0.8f,
                        y = center.y + r * 0.65f + stride,
                        width = r * 0.55f,
                        height = r * 0.35f
                    )
                    pixelRect(
                        color = style.accentColor,
                        x = center.x + r * 0.3f,
                        y = center.y + r * 0.65f,
                        width = r * 0.55f,
                        height = r * 0.35f
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = center.x - r * 0.3f,
                        y = center.y - r * 1.35f,
                        width = r * 0.25f,
                        height = r * 0.35f
                    )
                }
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

internal data class GravityObstacle(
    var x: Float,
    val width: Float,
    val depth: Float,
    val ceiling: Boolean,
    val wide: Boolean,
    var passed: Boolean = false,
    var broken: Boolean = false
)

internal data class GravityBeacon(
    var x: Float,
    val y: Float
)

internal class GravityFlipRun : ArcadeRun() {
    val obstacles = mutableListOf<GravityObstacle>()
    val beacons = mutableListOf<GravityBeacon>()
    val trail = mutableListOf<Offset>()
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
    private var trailTime = 0f
    private var flipTime = 0f
    val speed get() = 135f + passed.coerceAtMost(60) * 2f

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
        trailTime = 0f
        flipTime = 0f
    }

    fun flip() {
        if (flipTime > 0f) return
        gravity = -gravity
        velocity = gravity * 1.1f
        flipTime = 0.1f
        effects.burst(
            point = Offset(playerX, top + y * span),
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
                point = Offset(playerX, if (gravity > 0) bottom else top),
                count = 5
            )
        }
        val step = speed * seconds
        distance += step
        timer -= seconds
        if (timer <= 0f) {
            val wide = passed >= 6 && Random.nextInt(3) == 0
            val w = if (wide) 44f else 25f + Random.nextFloat() * 10f
            val depth = 0.22f + Random.nextFloat() * 0.12f
            obstacles += GravityObstacle(width + 20f, w, depth, nextCeiling, wide)
            repeat(3) {
                val bank = radius / span + 0.025f
                beacons += GravityBeacon(
                    width + 20f + w / 2f + it * 20f,
                    if (nextCeiling) 1f - bank else bank
                )
            }
            if (Random.nextFloat() < 0.82f) nextCeiling = !nextCeiling
            timer += (w / speed + 1.0f + Random.nextFloat() * 0.32f)
        }
        for (obstacle in obstacles) {
            obstacle.x -= step
            if (!obstacle.passed && obstacle.x + obstacle.width < playerX - radius) {
                obstacle.passed = true
                passed++
                score += 5
            }
            if (!obstacle.broken && obstacle.x < playerX + radius * 0.8f && obstacle.x + obstacle.width > playerX - radius * 0.8f &&
                (if (obstacle.ceiling) y - margin * 0.8f < obstacle.depth else y + margin * 0.8f > 1f - obstacle.depth)
            ) {
                effects.burst(
                    point = Offset(playerX, top + y * span),
                    count = 18
                )
                if (shield) {
                    shield = false
                    obstacle.broken = true
                } else phase = ArcadePhase.Over
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
                        shield = true; charge = 0
                    }
                }
                effects.burst(
                    point = Offset(beacon.x, top + beacon.y * span),
                    count = 7
                )
                iterator.remove()
            } else if (beacon.x < -10f) iterator.remove()
        }
        trailTime += seconds
        for (index in trail.indices) trail[index] = trail[index] - Offset(step, 0f)
        if (trailTime >= 0.035f) {
            trailTime = 0f
            trail += Offset(playerX, y)
            if (trail.size > 7) trail.removeAt(0)
        }
    }

    override fun onResize(previous: Size) {
        obstacles.forEach { it.x *= width / previous.width }
        beacons.forEach { it.x *= width / previous.width }
        trail.clear()
        y = y.coerceIn(radius / span, 1f - radius / span)
    }

    override fun save(state: Bundle) {
        state.putBoolean("shield", shield)
        state.putInt("charge", charge)
        state.putFloatArray(
            "run",
            floatArrayOf(y, velocity, distance, timer, trailTime, flipTime)
        )
        state.putInt("gravity", gravity)
        state.putInt("passed", passed)
        state.putBoolean("nextCeiling", nextCeiling)
        state.putFloatArray(
            "obstacles",
            obstacles.flatMap {
                listOf(
                    it.x,
                    it.width,
                    it.depth,
                    if (it.ceiling) 1f else 0f,
                    if (it.wide) 1f else 0f,
                    if (it.passed) 1f else 0f,
                    if (it.broken) 1f else 0f
                )
            }.toFloatArray()
        )
        state.putFloatArray("beacons", beacons.flatMap { listOf(it.x, it.y) }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        shield = state.getBoolean("shield")
        charge = state.getInt("charge")
        state.getFloatArray("run")?.let {
            y = it[0]
            velocity = it[1]
            distance = it[2]
            timer = it[3]
            trailTime = it[4]
            flipTime = it[5]
        }
        gravity = state.getInt("gravity", 1)
        passed = state.getInt("passed")
        nextCeiling = state.getBoolean("nextCeiling")
        obstacles.clear()
        state.getFloatArray("obstacles")?.let {
            for (index in it.indices step 7) obstacles += GravityObstacle(
                it[index],
                it[index + 1],
                it[index + 2],
                it[index + 3] == 1f,
                it[index + 4] == 1f,
                it[index + 5] == 1f,
                it[index + 6] == 1f
            )
        }
        beacons.clear()
        state.getFloatArray("beacons")?.let {
            for (index in it.indices step 2) beacons += GravityBeacon(it[index], it[index + 1])
        }
    }
}