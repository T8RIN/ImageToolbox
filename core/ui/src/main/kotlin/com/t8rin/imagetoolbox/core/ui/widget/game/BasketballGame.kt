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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
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
            arcadeLives(
                style = style,
                remaining = run.lives,
                y = run.topInset * 0.5f
            )
            val floor = run.floor
            val left = run.courtLeft
            val right = run.width - left
            repeat(6) { row ->
                val y = run.topInset + 24f + row * (floor - run.topInset - 24f) / 6f
                repeat(5) { column ->
                    val x =
                        left + ((column + if (row % 2 == 0) 0.25f else 0.75f) / 5f) * (right - left)
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.17f),
                        x = x,
                        y = y,
                        width = 7f,
                        height = 2f
                    )
                }
            }
            drawLine(
                color = style.detailColor.copy(alpha = 0.5f),
                start = Offset(left, floor),
                end = Offset(right, floor),
                strokeWidth = 1f
            )
            drawArc(
                color = style.detailColor.copy(alpha = 0.25f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(run.width / 2f - 52f, floor - 12f),
                size = Size(104f, 24f),
                style = Stroke(1f)
            )
            val hoop = run.hoop
            val board = run.boardX
            val half = run.rimWidth
            val netDepth = 24f + run.net * 7f
            val swing = sin(run.net * 9f) * run.net * 4f
            drawLine(
                color = style.detailColor.copy(alpha = 0.45f),
                start = Offset(board, hoop.y - 22f),
                end = Offset(if (run.side > 0) right else left, hoop.y - 22f),
                strokeWidth = 3f
            )
            drawRoundRect(
                color = style.obstacleColor.copy(alpha = 0.14f),
                topLeft = Offset(board - 3f, hoop.y - 49f),
                size = Size(6f, 59f),
                cornerRadius = CornerRadius(2f)
            )
            drawLine(
                color = style.obstacleColor,
                start = Offset(board, hoop.y - 49f),
                end = Offset(board, hoop.y + 10f),
                strokeWidth = 3f
            )
            drawLine(
                color = style.accentColor.copy(alpha = 0.7f),
                start = Offset(board - run.side * 3f, hoop.y - 30f),
                end = Offset(board - run.side * 3f, hoop.y - 12f),
                strokeWidth = 2f
            )
            for (index in 0..4) {
                val x = (index / 4f - 0.5f) * 2f
                val top = hoop + Offset(x * half, 3f)
                val bottom = hoop + Offset(x * half * 0.6f + swing, netDepth)
                drawLine(
                    color = style.obstacleColor.copy(alpha = 0.65f),
                    start = top,
                    end = bottom,
                    strokeWidth = 1f
                )
                if (index < 4) drawLine(
                    color = style.obstacleColor.copy(alpha = 0.35f),
                    start = top,
                    end = hoop + Offset((x + 0.5f) * half * 0.6f + swing, netDepth),
                    strokeWidth = 1f
                )
            }
            drawLine(
                color = style.obstacleColor.copy(alpha = 0.5f),
                start = hoop + Offset(-half * 0.6f + swing, netDepth),
                end = hoop + Offset(half * 0.6f + swing, netDepth),
                strokeWidth = 1f
            )
            drawLine(
                color = style.accentColor,
                start = hoop - Offset(half, 0f),
                end = hoop + Offset(half, 0f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            if (run.net > 0f) drawCircle(
                color = style.accentColor.copy(alpha = run.net * 0.12f),
                radius = half + (1f - run.net) * 12f,
                center = hoop
            )
            val shadow = (1f - (floor - run.ball.y) / (floor - run.topInset)).coerceIn(0f, 1f)
            drawOval(
                color = style.detailColor.copy(alpha = 0.12f + shadow * 0.15f),
                topLeft = Offset(run.ball.x - 11f, floor - 3f),
                size = Size(22f, 5f)
            )
            run.trail.forEachIndexed { index, point ->
                drawCircle(
                    color = style.playerColor.copy(alpha = index / 8f * 0.12f),
                    radius = run.radius * 0.45f,
                    center = point
                )
            }
            if (run.aiming) {
                run.preview.forEachIndexed { index, point ->
                    drawCircle(
                        color = style.accentColor.copy(alpha = 0.55f - index * 0.01f),
                        radius = 1.8f,
                        center = point
                    )
                }
            }
            rotate(run.spin, run.ball) {
                val r = run.radius
                drawCircle(
                    color = style.playerColor,
                    radius = r,
                    center = run.ball
                )
                drawCircle(
                    color = style.playerDetailColor.copy(alpha = 0.5f),
                    radius = r,
                    center = run.ball,
                    style = Stroke(1f)
                )
                drawLine(
                    color = style.playerDetailColor.copy(alpha = 0.65f),
                    start = run.ball - Offset(r, 0f),
                    end = run.ball + Offset(r, 0f),
                    strokeWidth = 1f
                )
                drawLine(
                    color = style.playerDetailColor.copy(alpha = 0.65f),
                    start = run.ball - Offset(0f, r),
                    end = run.ball + Offset(0f, r),
                    strokeWidth = 1f
                )
                for (side in listOf(-1f, 1f)) {
                    val seam = Path().apply {
                        moveTo(run.ball.x, run.ball.y - r)
                        cubicTo(
                            run.ball.x + side * r * 0.9f, run.ball.y - r * 0.5f,
                            run.ball.x + side * r * 0.9f, run.ball.y + r * 0.5f,
                            run.ball.x, run.ball.y + r
                        )
                    }
                    drawPath(
                        path = seam,
                        color = style.playerDetailColor.copy(alpha = 0.65f),
                        style = Stroke(1f)
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

internal class BasketballRun : ArcadeRun() {
    var ball = Offset.Zero
    var velocity = Offset.Zero
    var launch = Offset.Zero
    var lives = 3
    var made = 0
    var combo = 0
    var spin = 0f
    var net = 0f
    var airborne = false
    var aiming = false
        private set
    var side = 1f
        private set
    val effects = ArcadeEffects()
    val trail = ArrayDeque<Offset>()
    val preview = mutableListOf<Offset>()
    private var start = Offset.Zero
    private var aim = Offset.Zero
    private var hoopX = 260f
    private var hoopY = 110f
    private var time = 0f
    private var shotTime = 0f
    private var settle = 0f
    private var scored = false
    private var bounced = false
    private var trailTime = 0f
    val radius = 8f
    val floor get() = height - 20f
    val courtLeft get() = maxOf(18f, (width - 420f) / 2f)
    val pace get() = made / (made + 18f)
    val rimWidth get() = 27f - pace * 5f
    val gravity get() = 340f * ((height - topInset) / 240f).coerceIn(0.75f, 2.6f)
    val maxSwipe get() = ((height - topInset) * 0.5f).coerceIn(70f, 160f)
    val aimVelocity: Offset
        get() {
            if (aim.y >= -4f) return Offset.Zero
            val targetX = (ball.x + aim.x / -aim.y * (ball.y - hoopY))
                .coerceIn(courtLeft - 24f, width - courtLeft + 24f)
            val clearance = 32f + (aim.getDistance() / maxSwipe).coerceAtMost(1f) * 36f
            val apex = maxOf(topInset + radius + 10f, hoopY - clearance)
            val vy = -sqrt(2f * gravity * (ball.y - apex).coerceAtLeast(1f))
            val duration =
                (-vy + sqrt((vy * vy + 2f * gravity * (hoopY - ball.y)).coerceAtLeast(0f))) / gravity
            return Offset((targetX - ball.x) / duration, vy)
        }
    val hoop get() = hoopAt(0f)
    val boardX get() = hoop.x + side * (rimWidth + 7f)

    private fun hoopAt(seconds: Float) = Offset(
        hoopX + sin((time + seconds) * (0.55f + pace * 0.3f)) * 20f * (made - 7).coerceAtLeast(0) / (made + 18f),
        hoopY
    )

    override fun reset() {
        lives = 3
        made = 0
        combo = 0
        time = 0f
        net = 0f
        effects.clear()
        nextShot(true)
    }

    private fun nextShot(newTarget: Boolean) {
        if (newTarget) {
            side = if (made % 4 < 2) 1f else -1f
            val span = width - courtLeft * 2f
            launch = Offset(width / 2f - side * span * 0.31f, floor - radius - 1f)
            hoopX = width / 2f + side * span * (0.25f + Random.nextFloat() * 0.06f)
            hoopY = topInset + (floor - topInset) * (0.32f + Random.nextFloat() * 0.12f)
        }
        ball = launch
        velocity = Offset.Zero
        spin = 0f
        airborne = false
        scored = false
        bounced = false
        shotTime = 0f
        settle = 0f
        trailTime = 0f
        trail.clear()
        stopInput()
    }

    fun begin(point: Offset) {
        if (airborne || settle > 0f || (point - ball).getDistance() > 38f) return
        start = point
        aim = Offset.Zero
        aiming = true
    }

    fun aim(point: Offset) {
        if (!aiming) return
        val delta = point - start
        aim = Offset(delta.x, minOf(delta.y, 0f)) * minOf(
            1f,
            maxSwipe / delta.getDistance().coerceAtLeast(1f)
        )
        updatePreview()
    }

    fun shoot() {
        if (!aiming) return
        if (aim.y < -4f && aim.getDistance() > 14f) {
            velocity = aimVelocity
            airborne = true
            shotTime = 0f
        }
        stopInput()
    }

    override fun stopInput() {
        aiming = false
        aim = Offset.Zero
        preview.clear()
    }

    private fun updatePreview() {
        preview.clear()
        val velocity = aimVelocity
        if (velocity.y >= 0f) return
        val duration = (-velocity.y + sqrt(
            (velocity.y * velocity.y + 2f * gravity * (hoopY - ball.y)).coerceAtLeast(0f)
        )) / gravity
        var previous = ball
        for (index in 1..32) {
            val t = duration * index / 32f
            val point = ball + velocity * t + Offset(0f, gravity * t * t / 2f)
            val hoop = hoopAt(t)
            val segment = point - previous
            val board = hoop.x + side * (rimWidth + 7f)
            if (abs(segment.x) > 0.001f) {
                val surface = board + if (segment.x > 0f) -radius - 3f else radius + 3f
                val crossing = (surface - previous.x) / segment.x
                val y = previous.y + segment.y * crossing
                if (crossing in 0f..1f && y in hoop.y - 49f - radius..hoop.y + 10f + radius) break
            }
            val blocked = listOf(-rimWidth, rimWidth).any { x ->
                val peg = hoop + Offset(x, 0f)
                val delta = peg - previous
                val projection =
                    ((delta.x * segment.x + delta.y * segment.y) / segment.getDistanceSquared()
                        .coerceAtLeast(0.001f)).coerceIn(0f, 1f)
                (previous + segment * projection - peg).getDistance() <= radius + 2f
            }
            if (blocked) break
            preview += point
            previous = point
        }
    }

    private fun bounceRim(
        previous: Offset,
        center: Offset
    ) {
        val travel = ball - previous
        val relative = previous - center
        val reach = radius + 2f
        val a = travel.getDistanceSquared()
        if (a < 0.0001f) return
        val b = 2f * (relative.x * travel.x + relative.y * travel.y)
        val c = relative.getDistanceSquared() - reach * reach
        val discriminant = b * b - 4f * a * c
        if (discriminant < 0f) return
        val hit = if (c < 0f) 0f else (-b - sqrt(discriminant)) / (2f * a)
        if (hit !in 0f..1f) return
        val contact = previous + travel * hit
        val normal = (contact - center) / (contact - center).getDistance().coerceAtLeast(0.01f)
        val impact = velocity.x * normal.x + velocity.y * normal.y
        if (impact >= 0f) return
        ball = center + normal * (reach + 0.2f)
        velocity -= normal * (impact * 1.65f)
        bounced = true
    }

    override fun advance(seconds: Float) {
        time += seconds
        net = (net - seconds * 2f).coerceAtLeast(0f)
        effects.advance(seconds)
        if (aiming && made >= 8) updatePreview()
        if (settle > 0f) {
            settle -= seconds
            if (settle <= 0f) {
                if (lives <= 0) phase = ArcadePhase.Over else nextShot(scored)
                return
            }
        }
        if (!airborne) return
        shotTime += seconds
        val previous = ball
        ball += velocity * seconds + Offset(0f, gravity * seconds * seconds / 2f)
        velocity += Offset(0f, gravity * seconds)
        spin = (spin + velocity.x * seconds * 5f) % 360f
        trailTime += seconds
        if (trailTime >= 0.035f) {
            trailTime = 0f
            trail += ball
            if (trail.size > 8) trail.removeFirst()
        }
        val target = hoop
        val board = boardX
        val surface = board + if (velocity.x > 0f) -radius - 3f else radius + 3f
        if ((previous.x - surface) * (ball.x - surface) <= 0f && abs(ball.x - previous.x) > 0.001f) {
            val t = (surface - previous.x) / (ball.x - previous.x)
            val y = previous.y + (ball.y - previous.y) * t
            if (y in target.y - 49f - radius..target.y + 10f + radius) {
                ball = Offset(surface, ball.y)
                velocity = Offset(-velocity.x * 0.72f, velocity.y * 0.95f)
                bounced = true
            }
        }
        if (!scored) {
            bounceRim(previous, target - Offset(rimWidth, 0f))
            bounceRim(previous, target + Offset(rimWidth, 0f))
            if (settle <= 0f && velocity.y > 0f && previous.y < target.y && ball.y >= target.y) {
                val t = (target.y - previous.y) / (ball.y - previous.y)
                val x = previous.x + (ball.x - previous.x) * t
                if (abs(x - target.x) < rimWidth - radius) {
                    scored = true
                    made++
                    combo++
                    score += 10 + minOf(combo - 1, 5) * 2 + if (bounced) 0 else 5
                    net = 1f
                    settle = 0.7f
                    effects.burst(
                        point = target,
                        count = 12
                    )
                }
            }
        }
        val missed =
            ball.y + radius >= floor || ball.x < courtLeft - 40f || ball.x > width - courtLeft + 40f || shotTime > 6f
        if (missed) {
            if (settle <= 0f) {
                lives--
                combo = 0
                settle = 0.6f
            }
            ball =
                Offset(ball.x.coerceIn(courtLeft, width - courtLeft), minOf(ball.y, floor - radius))
            velocity = Offset.Zero
            airborne = false
            trail.clear()
        }
    }

    override fun onResize(previous: Size) {
        val previousLeft = maxOf(18f, (previous.width - 420f) / 2f)
        val x = (width - courtLeft * 2f) / (previous.width - previousLeft * 2f)
        val y = height / previous.height
        ball = Offset(courtLeft + (ball.x - previousLeft) * x, ball.y * y)
        launch = Offset(courtLeft + (launch.x - previousLeft) * x, floor - radius - 1f)
        if (!airborne && settle <= 0f) ball = launch
        velocity = Offset(velocity.x * x, velocity.y * y)
        hoopX = courtLeft + (hoopX - previousLeft) * x
        hoopY = (hoopY * y).coerceIn(topInset + 24f, floor - 65f)
        trail.clear()
        stopInput()
    }

    override fun save(state: Bundle) {
        state.putIntArray("run", intArrayOf(lives, made, combo))
        state.putFloatArray(
            "motion",
            floatArrayOf(
                ball.x,
                ball.y,
                velocity.x,
                velocity.y,
                launch.x,
                launch.y,
                hoopX,
                hoopY,
                side,
                spin
            )
        )
        state.putFloatArray("time", floatArrayOf(time, shotTime, settle, net, trailTime))
        state.putBoolean("airborne", airborne)
        state.putBoolean("scored", scored)
        state.putBoolean("bounced", bounced)
        state.putFloatArray("trail", trail.flatMap { listOf(it.x, it.y) }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        state.getIntArray("run")!!.let {
            lives = it[0]
            made = it[1]
            combo = it[2]
        }
        state.getFloatArray("motion")!!.let {
            ball = Offset(it[0], it[1])
            velocity = Offset(it[2], it[3])
            launch = Offset(it[4], it[5])
            hoopX = it[6]
            hoopY = it[7]
            side = it[8]
            spin = it[9]
        }
        state.getFloatArray("time")!!.let {
            time = it[0]
            shotTime = it[1]
            settle = it[2]
            net = it[3]
            trailTime = it[4]
        }
        airborne = state.getBoolean("airborne")
        scored = state.getBoolean("scored")
        bounced = state.getBoolean("bounced")
        trail.clear()
        state.getFloatArray("trail")!!
            .let { for (index in it.indices step 2) trail += Offset(it[index], it[index + 1]) }
        stopInput()
    }
}