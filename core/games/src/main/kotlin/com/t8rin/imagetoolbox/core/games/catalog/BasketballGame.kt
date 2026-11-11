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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SportsBasketball
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object BasketballGame : ArcadeGameDefinition {
    override val title = R.string.arcade_basketball
    override val description = R.string.arcade_basketball_sub
    override val icon get() = Icons.Outlined.SportsBasketball
    override val order = 31

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BasketballRun
        )

        val netPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onPress = run::begin,
                onDrag = run::aim,
                onRelease = run::shoot
            ) {
                drawBasketballBackground(style, run.width, run.height)
                val unit = run.unit
                val left = (run.width - 360f * unit) / 2f
                for (side in 0..1) {
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.3f),
                        start = Offset(x = left + side * 360f * unit, y = 0f),
                        end = Offset(x = left + side * 360f * unit, y = run.height),
                        strokeWidth = 1f
                    )
                }
                repeat(18) { index ->
                    val point = Offset(
                        x = left + (index * 79 % 347 + 6) * unit,
                        y = (index * 53f + run.camera * unit * 0.3f) % run.height
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
                    translate(left = point.x, top = point.y) {
                        scale(scale = unit, pivot = Offset.Zero) {
                            rotate(
                                degrees = -hoop.angle * 180f / kotlin.math.PI.toFloat(),
                                pivot = Offset.Zero
                            ) {
                                val half = hoop.halfWidth
                                val delta =
                                    if (index == 0 && !run.airborne) run.ball - hoop.center else -hoop.normal * 20f
                                val depth = maxOf(
                                    35f + (if (index == 0) run.net * 6f else 0f),
                                    -(delta.x * hoop.normal.x + delta.y * hoop.normal.y) + run.radius
                                )
                                val swing = if (index == 0 && !run.airborne) {
                                    delta.x * hoop.tangent.x + delta.y * hoop.tangent.y
                                } else 0f
                                val net = netPath.apply {
                                    rewind()
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
                                        val start = Offset(x = strand * half / 3f, y = 0f)
                                        drawLine(
                                            color = color.copy(alpha = 0.7f),
                                            start = start,
                                            end = start + Offset(
                                                x = side * half + swing,
                                                y = depth
                                            ),
                                            strokeWidth = 1.5f
                                        )
                                    }
                                }
                                drawPath(
                                    path = net,
                                    color = color.copy(alpha = 0.75f),
                                    style = Stroke(width = 1.5f)
                                )
                                drawOval(
                                    color = color,
                                    topLeft = Offset(x = -half, y = -6f),
                                    size = Size(width = half * 2f, height = 12f),
                                    style = Stroke(width = 4f)
                                )

                            }
                        }
                    }
                }
                run.trail.forEachIndexed { index, point ->
                    pixelCircle(
                        color = (if (run.combo >= 3) style.accentColor else style.playerColor).copy(
                            alpha = index / 8f * 0.16f
                        ),
                        radius = run.radius * unit * if (run.combo >= 3) 0.65f else 0.5f,
                        center = run.project(point)
                    )
                }
                if (run.aiming) {
                    run.preview.forEachIndexed { index, point ->
                        pixelCircle(
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
                translate(left = left, top = run.baseline + run.camera * unit) {
                    scale(scale = unit, pivot = Offset.Zero) {
                        scale(scaleX = 1f, scaleY = -1f, pivot = Offset.Zero) {
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
}

private fun DrawScope.drawBasketballBall(
    run: BasketballRun,
    style: ArcadeStyle
) {
    val unit = run.unit
    val center = run.project(run.ball)
    val radius = run.radius * unit
    val ink = style.overlayColor.copy(alpha = 1f)
    val silhouette = Path().apply {
        addOval(
            Rect(
                center - Offset(radius, radius),
                Size(radius * 2f, radius * 2f)
            )
        )
    }
    drawCircle(lerp(style.playerColor, ink, 0.25f), radius, center)
    clipPath(silhouette) {
        drawCircle(
            style.playerColor,
            radius * 0.96f,
            center - Offset(radius * 0.12f, radius * 0.14f)
        )
        rotate(degrees = run.spin, pivot = center) {
            val seam = lerp(style.playerColor, ink, 0.62f)
            for (vertical in listOf(false, true)) {
                val delta = if (vertical) Offset(0f, radius) else Offset(radius, 0f)
                drawLine(seam, center - delta, center + delta, strokeWidth = unit * 0.8f)
            }
            for (side in listOf(-1f, 1f)) {
                val curve = Path().apply {
                    moveTo(center.x - radius * 0.78f, center.y - radius * 0.63f * side)
                    cubicTo(
                        center.x - radius * 0.3f, center.y - radius * 0.15f * side,
                        center.x + radius * 0.3f, center.y - radius * 0.15f * side,
                        center.x + radius * 0.78f, center.y - radius * 0.63f * side
                    )
                }
                drawPath(curve, seam, style = Stroke(unit * 0.75f))
            }
        }
        drawOval(
            style.obstacleColor.copy(alpha = 0.28f),
            center - Offset(radius * 0.57f, radius * 0.67f),
            Size(radius * 0.35f, radius * 0.17f)
        )
    }
    drawCircle(ink.copy(alpha = 0.4f), radius, center, style = Stroke(unit * 0.7f))
}

private fun DrawScope.drawBasketballBackground(style: ArcadeStyle, width: Float, height: Float) {
    val color = style.detailColor.copy(alpha = 0.14f)
    val y = 8f
    drawLine(color, Offset(7f, y), Offset(width - 7f, y), 0.7f)
    repeat(9) { index ->
        val x = 17f + index * (width - 34f) / 8f
        val path = Path().apply {
            moveTo(x - 3f, y)
            lineTo(x + 3f, y)
            lineTo(x + 3f, y + 5f)
            lineTo(x, y + 8f)
            lineTo(x - 3f, y + 5f)
            close()
        }
        drawPath(path, color, style = Stroke(0.7f))
    }
    val ballInk = style.detailColor.copy(alpha = 0.2f)
    for (center in listOf(
        Offset(width * 0.14f, height * 0.28f),
        Offset(width * 0.86f, height * 0.76f)
    )) {
        val radius = minOf(width, height) * 0.052f
        pixelCircle(style.detailColor.copy(alpha = 0.04f), radius, center)
        pixelCircle(ballInk, radius, center, style = Stroke(0.9f))
        drawLine(ballInk, center - Offset(radius, 0f), center + Offset(radius, 0f), 0.8f)
        drawLine(ballInk, center - Offset(0f, radius), center + Offset(0f, radius), 0.8f)
        val seam = Path().apply {
            moveTo(center.x - radius * 0.65f, center.y - radius * 0.75f)
            cubicTo(
                center.x + radius * 0.2f, center.y - radius * 0.4f,
                center.x + radius * 0.2f, center.y + radius * 0.4f,
                center.x - radius * 0.65f, center.y + radius * 0.75f
            )
        }
        drawPath(seam, ballInk, style = Stroke(0.8f))
    }
    for (center in listOf(
        Offset(width * 0.83f, height * 0.24f),
        Offset(width * 0.17f, height * 0.7f)
    )) {
        val radius = minOf(width, height) * 0.039f
        for (side in -1..1 step 2) {
            val ribbon = Path().apply {
                moveTo(center.x + side * radius * 0.12f, center.y - radius * 0.55f)
                lineTo(center.x + side * radius * 0.44f, center.y - radius * 2f)
                lineTo(center.x + side * radius * 0.95f, center.y - radius * 2f)
                lineTo(center.x + side * radius * 0.7f, center.y)
                close()
            }
            drawPath(ribbon, style.detailColor.copy(alpha = 0.1f))
            drawPath(ribbon, ballInk, style = Stroke(0.7f))
        }
        pixelCircle(style.detailColor.copy(alpha = 0.07f), radius, center)
        pixelCircle(ballInk, radius, center, style = Stroke(0.9f))
        pixelCircle(ballInk, radius * 0.62f, center, style = Stroke(0.7f))
        pixelRect(ballInk, center.x - 0.6f, center.y - radius * 0.32f, 1.2f, radius * 0.64f)
    }

}

@Serializable
private data class BasketballHoop(
    var center: ArcadeOffset,
    val halfWidth: Float,
    var angle: Float,
    val anchorX: Float = center.x,
    val travel: Float = 0f,
    val phase: Float = 0f
) {
    val tangent get() = Offset(x = cos(angle), y = sin(angle))
    val normal get() = Offset(x = -sin(angle), y = cos(angle))

    fun positionAt(time: Float): Offset =
        center.copy(x = anchorX + sin(time * 0.9f + phase) * travel)

    fun speedAt(time: Float): Offset = Offset(x = cos(time * 0.9f + phase) * travel * 0.9f, y = 0f)
}

@Serializable
private class BasketballRun : ArcadeRun() {
    val hoops = mutableListOf<BasketballHoop>()

    @Transient
    val effects = ArcadeEffects()
    val trail: ArcadeDeque<ArcadeOffset> = ArrayDeque()

    @Transient
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
    var ball: ArcadeOffset = Offset.Zero
    var velocity: ArcadeOffset = Offset.Zero
    var camera = 0f
    var time = 0f
    var made = 0
    var combo = 0
    var spin = 0f
    var net = 0f
    var airborne = false

    @Transient
    var aiming = false
        private set

    @Transient
    var pull = Offset.Zero
        private set

    @Transient
    private var touch: Offset? = null
    private var settle = 0f
    private var clean = true
    private var banked = false
    private var trailTimer = 0f
    private var catchFrom: ArcadeOffset = Offset.Zero

    fun project(point: Offset): Offset = Offset(
        x = (width - 360f * unit) / 2f + point.x * unit,
        y = baseline - (point.y - camera) * unit
    )

    override fun isValid(): Boolean = super.isValid() &&
            hoops.size >= 2

    override fun reset() {
        hoops.clear()
        hoops += BasketballHoop(center = Offset(x = 85f, y = 0f), halfWidth = 37f, angle = 0f)
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
            center = Offset(x = x, y = previous.center.y + gap),
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
        ball = hoop.center - hoop.normal * 20f + Offset(x = pull.x, y = -pull.y) * 0.22f
        preview.clear()
        if (pull.getDistance() < 6f) return
        var point = ball
        var speed = launchVelocity()
        repeat(48) { index ->
            val step = 0.035f
            val next = point + speed * step - Offset(x = 0f, y = gravity * step * step / 2f)
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
            speed -= Offset(x = 0f, y = gravity * step)
            point = next
            if (point.x < radius || point.x > 360f - radius) {
                point = point.copy(x = point.x.coerceIn(radius, 360f - radius))
                speed = speed.copy(x = -speed.x * 0.86f)
            }
            if (project(point).y > height) return
            preview += point
        }
    }

    private fun launchVelocity() = Offset(x = -pull.x * power, y = pull.y * power)

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
        val normal = if (length > 0.001f) delta / length else Offset(x = 0f, y = 1f)
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
        ball += velocity * seconds - Offset(x = 0f, y = gravity * seconds * seconds / 2f)
        velocity -= Offset(x = 0f, y = gravity * seconds)
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
        stopInput()
    }
}