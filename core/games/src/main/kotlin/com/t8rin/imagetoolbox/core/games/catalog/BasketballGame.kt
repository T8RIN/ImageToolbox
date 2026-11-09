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

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun BasketballGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::BasketballRun
    )

    ArcadeBoard(
        game = ArcadeGame.Basketball,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::begin,
            onDrag = run::aim,
            onRelease = run::shoot
        ) {
            val unit = run.unit
            val left = (run.width - 360f * unit) / 2f
            for (side in 0..1) {
                drawLine(
                    color = style.detailColor.copy(alpha = 0.3f),
                    start = Offset(left + side * 360f * unit, 0f),
                    end = Offset(left + side * 360f * unit, run.height),
                    strokeWidth = 1f
                )
            }
            repeat(36) { index ->
                val point = Offset(
                    left + (index * 79 % 347 + 6) * unit,
                    (index * 53f + run.camera * unit * 0.3f) % run.height
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.2f),
                    x = point.x,
                    y = point.y,
                    width = 2f,
                    height = 2f
                )
            }
            if (!run.airborne) drawBasketballBall(
                run = run,
                style = style
            )
            run.hoops.forEachIndexed { index, hoop ->
                val point = run.project(hoop.center)
                val color = if (index == 1) style.accentColor else style.obstacleColor
                translate(point.x, point.y) {
                    scale(unit, Offset.Zero) {
                        rotate(-hoop.angle * 180f / kotlin.math.PI.toFloat(), Offset.Zero) {
                            val half = hoop.halfWidth
                            val delta =
                                if (index == 0 && !run.airborne) run.ball - hoop.center else -hoop.normal * 20f
                            val depth = maxOf(
                                35f + (if (index == 0) run.net * 6f else 0f),
                                -(delta.x * hoop.normal.x + delta.y * hoop.normal.y) + run.radius
                            )
                            val swing =
                                if (index == 0 && !run.airborne) delta.x * hoop.tangent.x + delta.y * hoop.tangent.y else 0f
                            val net = Path().apply {
                                moveTo(-half, 0f)
                                quadraticTo(-half * 0.55f + swing, depth, swing, depth)
                                quadraticTo(half * 0.55f + swing, depth, half, 0f)
                                close()
                            }
                            drawPath(
                                path = net,
                                color = color.copy(alpha = 0.06f)
                            )
                            clipPath(net) {
                                for (strand in -5..5) for (side in listOf(-1f, 1f)) {
                                    val start = Offset(strand * half / 3f, 0f)
                                    drawLine(
                                        color = color.copy(alpha = 0.7f),
                                        start = start,
                                        end = start + Offset(side * half + swing, depth),
                                        strokeWidth = 1.5f
                                    )
                                }
                            }
                            drawPath(
                                path = net,
                                color = color.copy(alpha = 0.75f),
                                style = Stroke(1.5f)
                            )
                            drawOval(
                                color = color,
                                topLeft = Offset(-half, -6f),
                                size = Size(half * 2f, 12f),
                                style = Stroke(4f)
                            )

                        }
                    }
                }
            }
            run.trail.forEachIndexed { index, point ->
                drawCircle(
                    color = (if (run.combo >= 3) style.accentColor else style.playerColor).copy(
                        alpha = index / 8f * 0.16f
                    ),
                    radius = run.radius * unit * if (run.combo >= 3) 0.65f else 0.5f,
                    center = run.project(point)
                )
            }
            if (run.aiming) {
                run.preview.forEachIndexed { index, point ->
                    drawCircle(
                        color = style.accentColor.copy(alpha = 0.7f - index * 0.012f),
                        radius = 1.7f * unit,
                        center = run.project(point)
                    )
                }

            }
            if (run.airborne) drawBasketballBall(
                run = run,
                style = style
            )
            translate(left, run.baseline + run.camera * unit) {
                scale(unit, Offset.Zero) {
                    scale(1f, -1f, Offset.Zero) {
                        run.effects.draw(
                            scope = this,
                            color = style.accentColor
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawBasketballBall(
    run: BasketballRun,
    style: ArcadeStyle
) {
    val unit = run.unit
    val center = run.project(run.ball)
    val radius = run.radius * unit
    rotate(run.spin, center) {
        drawCircle(
            color = style.playerColor,
            radius = radius,
            center = center
        )
        drawCircle(
            color = style.playerDetailColor.copy(alpha = 0.5f),
            radius = radius,
            center = center,
            style = Stroke(unit)
        )
        drawLine(
            color = style.playerDetailColor.copy(alpha = 0.65f),
            start = center - Offset(radius, 0f),
            end = center + Offset(radius, 0f),
            strokeWidth = unit
        )
        for (side in listOf(-1f, 1f)) {
            val seam = Path().apply {
                moveTo(center.x, center.y - radius)
                cubicTo(
                    center.x + side * radius * 0.9f, center.y - radius * 0.3f,
                    center.x + side * radius * 0.9f, center.y + radius * 0.3f,
                    center.x, center.y + radius
                )
            }
            drawPath(
                path = seam,
                color = style.playerDetailColor.copy(alpha = 0.65f),
                style = Stroke(unit)
            )
        }
    }
}

internal data class BasketballHoop(
    var center: Offset,
    val halfWidth: Float,
    var angle: Float,
    val anchorX: Float = center.x,
    val travel: Float = 0f,
    val phase: Float = 0f
) {
    val tangent get() = Offset(cos(angle), sin(angle))
    val normal get() = Offset(-sin(angle), cos(angle))

    fun positionAt(time: Float): Offset =
        center.copy(x = anchorX + sin(time * 0.9f + phase) * travel)

    fun speedAt(time: Float): Offset = Offset(cos(time * 0.9f + phase) * travel * 0.9f, 0f)
}

internal class BasketballRun : ArcadeRun() {
    val hoops = mutableListOf<BasketballHoop>()
    val effects = ArcadeEffects()
    val trail = ArrayDeque<Offset>()
    val preview = mutableListOf<Offset>()
    val radius = 15f
    val gravity = 360f
    val power = 4.8f
    val unit
        get() = minOf(
            1f, width / 360f,
            (height - topInset - 12f) / ((hoops.getOrNull(1)?.center?.y ?: 80f) -
                    (hoops.firstOrNull()?.center?.y ?: 0f) + 128f)
        )
    val baseline get() = minOf(height - 70f * unit, topInset + (height - topInset) * 0.78f)
    var ball = Offset.Zero
    var velocity = Offset.Zero
    var camera = 0f
    var time = 0f
    var made = 0
    var combo = 0
    var spin = 0f
    var net = 0f
    var airborne = false
    var aiming = false
        private set
    var pull = Offset.Zero
        private set
    private var touch: Offset? = null
    private var settle = 0f
    private var clean = true
    private var banked = false
    private var trailTimer = 0f
    private var catchFrom = Offset.Zero

    fun project(point: Offset): Offset = Offset(
        (width - 360f * unit) / 2f + point.x * unit,
        baseline - (point.y - camera) * unit
    )

    override fun reset() {
        hoops.clear()
        hoops += BasketballHoop(Offset(85f, 0f), 37f, 0f)
        made = 0
        combo = 0
        camera = 0f
        time = 0f
        spin = 0f
        net = 0f
        settle = 0f
        clean = true
        banked = false
        airborne = false
        velocity = Offset.Zero
        trailTimer = 0f
        trail.clear()
        effects.clear()
        stopInput()
        addHoop()
        ball = hoops.first().center - hoops.first().normal * 20f
        catchFrom = ball
    }

    private fun addHoop() {
        val previous = hoops.last()
        val number = made + hoops.size
        val pace = number / (number + 35f)
        val close = number % 4 == 0
        val gap = (height - topInset).coerceIn(170f, 660f) *
                (0.27f + Random.nextFloat() * 0.045f) * if (close) 0.85f else 1f
        val x = if (close) (previous.center.x + Random.nextFloat() * 16f - 8f).coerceIn(65f, 295f)
        else if (previous.center.x < 180f) 260f + Random.nextFloat() * 24f
        else 76f + Random.nextFloat() * 24f
        val hoop = BasketballHoop(
            center = Offset(x, previous.center.y + gap),
            halfWidth = 37f - pace * 5f,
            angle = if (number < 5) 0f else (Random.nextFloat() - 0.5f) * 0.35f * pace,
            travel = if (number >= 6 && number % 3 == 0 && !close) 10f + pace * 8f else 0f,
            phase = Random.nextFloat() * kotlin.math.PI.toFloat() * 2f
        )
        hoop.center = hoop.positionAt(time)
        hoops += hoop
    }

    fun begin(point: Offset) {
        if (airborne || settle > 0f || point.y < topInset) return
        touch = point
        aiming = true
        pull = Offset.Zero
        preview.clear()
    }

    fun aim(point: Offset) {
        val start = touch ?: return
        val delta = (point - start) / unit
        pull = delta * minOf(1f, 105f / delta.getDistance().coerceAtLeast(0.001f))
        val hoop = hoops.first()
        if (pull.getDistance() >= 6f) hoop.angle =
            atan2(pull.x, maxOf(12f, pull.y)).coerceIn(-0.65f, 0.65f)
        ball = hoop.center - hoop.normal * 20f + Offset(pull.x, -pull.y) * 0.22f
        preview.clear()
        if (pull.getDistance() < 6f) return
        var point = ball
        var speed = launchVelocity()
        repeat(48) { index ->
            val step = 0.035f
            val next = point + speed * step - Offset(0f, gravity * step * step / 2f)
            for (basket in 0..1) {
                val hoop = hoops[basket]
                val center =
                    if (basket == 0) hoop.center else hoop.positionAt(time + (index + 1) * step)
                val before = if (basket == 0) hoop.center else hoop.positionAt(time + index * step)
                for (side in -1..1 step 2) {
                    val peg = center + hoop.tangent * hoop.halfWidth * side.toFloat()
                    if (distanceToSegment(peg, point, next) < radius + 1.5f) return
                }
                if (crossesHoop(hoop, point, next, center, before)) return
            }
            speed -= Offset(0f, gravity * step)
            point = next
            if (point.x < radius || point.x > 360f - radius) {
                point = point.copy(x = point.x.coerceIn(radius, 360f - radius))
                speed = speed.copy(x = -speed.x * 0.86f)
            }
            if (project(point).y > height) return
            preview += point
        }
    }

    private fun launchVelocity() = Offset(-pull.x * power, pull.y * power)

    fun shoot() {
        if (!aiming || airborne) return
        if (pull.getDistance() >= 6f) {
            velocity = launchVelocity()
            airborne = true
            clean = true
            banked = false
            trail.clear()
        }
        stopInput()
    }

    override fun stopInput() {
        aiming = false
        touch = null
        pull = Offset.Zero
        preview.clear()
        if (!airborne && hoops.isNotEmpty()) ball =
            hoops.first().center - hoops.first().normal * 20f
    }

    private fun dot(
        a: Offset,
        b: Offset
    ): Float = a.x * b.x + a.y * b.y

    private fun distanceToSegment(
        point: Offset,
        from: Offset,
        to: Offset
    ): Float {
        val segment = to - from
        val t = (dot(point - from, segment) / segment.getDistanceSquared().coerceAtLeast(0.001f))
            .coerceIn(0f, 1f)
        return (point - from - segment * t).getDistance()
    }

    private fun crossesHoop(
        hoop: BasketballHoop,
        from: Offset,
        to: Offset,
        center: Offset = hoop.center,
        previousCenter: Offset = center
    ): Boolean {
        val start = from - previousCenter
        val end = to - center
        val above = dot(start, hoop.normal)
        val below = dot(end, hoop.normal)
        if (above <= 0f || below > 0f) return false
        val crossing = start + (end - start) * (above / (above - below))
        return abs(dot(crossing, hoop.tangent)) < hoop.halfWidth - radius - 1.5f
    }

    private fun rebound(
        peg: Offset,
        motion: Offset
    ) {
        val delta = ball - peg
        val length = delta.getDistance()
        val contact = radius + 1.5f
        if (length >= contact) return
        val normal = if (length > 0.001f) delta / length else Offset(0f, 1f)
        ball = peg + normal * contact
        val speed = dot(velocity - motion, normal)
        if (speed < 0f) velocity -= normal * (speed * 1.68f)
        clean = false
    }

    override fun advance(seconds: Float) {
        time += seconds
        for (index in 1..hoops.lastIndex) hoops[index].center = hoops[index].positionAt(time)
        if (aiming && hoops[1].travel > 0f) touch?.let { aim(it + pull * unit) }
        effects.advance(seconds)
        net = (net - seconds * 2.8f).coerceAtLeast(0f)
        settle = (settle - seconds).coerceAtLeast(0f)
        val target = maxOf(hoops.first().center.y, ball.y - (height - topInset) / unit * 0.48f)
        camera += (target - camera) * minOf(1f, seconds * 5f)
        if (!airborne) {
            if (!aiming) {
                val rest = hoops.first().center - hoops.first().normal * 20f
                val amount = (settle / 0.22f).coerceIn(0f, 1f)
                ball = rest + (catchFrom - rest) * amount * amount
            }
            return
        }
        val previous = ball
        ball += velocity * seconds - Offset(0f, gravity * seconds * seconds / 2f)
        velocity -= Offset(0f, gravity * seconds)
        spin += velocity.x * seconds * 1.8f
        if (ball.x < radius || ball.x > 360f - radius) {
            ball = ball.copy(x = ball.x.coerceIn(radius, 360f - radius))
            velocity = velocity.copy(x = -velocity.x * 0.86f)
            banked = true
        }
        for (index in 0..1) {
            val hoop = hoops[index]
            val motion = if (index == 0) Offset.Zero else hoop.speedAt(time)
            rebound(hoop.center - hoop.tangent * hoop.halfWidth, motion)
            rebound(hoop.center + hoop.tangent * hoop.halfWidth, motion)
        }
        for (index in 1 downTo 0) {
            val hoop = hoops[index]
            val previousCenter = if (index == 0) hoop.center else hoop.positionAt(time - seconds)
            if (!crossesHoop(hoop, previous, ball, hoop.center, previousCenter)) continue
            if (index == 1) {
                made++
                combo = if (clean) combo + 1 else 0
                score += 1 + (if (clean) minOf(combo, 5) else 0) + if (banked) 1 else 0
                effects.burst(
                    point = hoop.center,
                    count = if (clean) 16 else 8
                )
                hoops.removeAt(0)
                addHoop()
            }
            catchFrom = ball
            velocity = Offset.Zero
            airborne = false
            settle = 0.22f
            net = 1f
            trail.clear()
            return
        }
        trailTimer += seconds
        if (trailTimer >= 0.035f) {
            trailTimer = 0f
            trail.addLast(ball)
            if (trail.size > 8) trail.removeFirst()
        }
        if (project(ball).y > height + radius * unit) {
            phase = ArcadePhase.Over
            stopInput()
        }
    }

    override fun onResize(previous: Size) {
        if (hoops.isNotEmpty() && time == 0f && !airborne) reset()
        stopInput()
    }

    override fun save(state: Bundle) {
        state.putFloatArray("hoops", hoops.flatMap {
            listOf(
                it.center.x,
                it.center.y,
                it.halfWidth,
                it.angle,
                it.anchorX,
                it.travel,
                it.phase
            )
        }.toFloatArray())
        state.putFloatArray("ball", floatArrayOf(ball.x, ball.y, velocity.x, velocity.y))
        state.putFloatArray(
            "motion",
            floatArrayOf(camera, spin, net, settle, trailTimer, time, catchFrom.x, catchFrom.y)
        )
        state.putIntArray("score", intArrayOf(made, combo))
        state.putBoolean("airborne", airborne)
        state.putBoolean("clean", clean)
        state.putBoolean("banked", banked)
    }

    override fun restore(state: Bundle) {
        hoops.clear()
        state.getFloatArray("hoops")!!.let {
            for (index in it.indices step 7) {
                hoops += BasketballHoop(
                    Offset(it[index], it[index + 1]),
                    it[index + 2],
                    it[index + 3],
                    it[index + 4],
                    it[index + 5],
                    it[index + 6]
                )
            }
        }
        state.getFloatArray("ball")!!.let {
            ball = Offset(it[0], it[1])
            velocity = Offset(it[2], it[3])
        }
        state.getFloatArray("motion")!!.let {
            camera = it[0]
            spin = it[1]
            net = it[2]
            settle = it[3]
            trailTimer = it[4]
            time = it[5]
            catchFrom = Offset(it[6], it[7])
        }
        state.getIntArray("score")!!.let {
            made = it[0]
            combo = it[1]
        }
        airborne = state.getBoolean("airborne")
        clean = state.getBoolean("clean")
        banked = state.getBoolean("banked")
        trail.clear()
        aiming = false
        touch = null
        pull = Offset.Zero
        preview.clear()
    }
}