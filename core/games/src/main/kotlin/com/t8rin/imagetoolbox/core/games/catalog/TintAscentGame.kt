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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DonutLarge
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object TintAscentGame : ArcadeGameDefinition {
    override val title = R.string.arcade_tintascent
    override val description = R.string.arcade_tintascent_sub
    override val icon get() = Icons.Outlined.DonutLarge
    override val order = 37

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::TintAscentRun
        )
        val colors = remember(style.playerColor) {
            listOf(
                Color(0xFFFF709B),
                Color(0xFFFFCC66),
                Color(0xFF60DFCE),
                Color(0xFFAA9BFF)
            ).map { lerp(it, style.playerColor, 0.2f) }
        }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.jump() }
            ) {
                val drift = run.camera * run.unit
                val backdropCount = (run.width * run.height / 21000f).toInt().coerceAtLeast(5)
                repeat(backdropCount) { index ->
                    val radius = 12f + index % 3 * 4f
                    val span = run.height + 48f
                    val x = (index + 0.35f) * run.width / backdropCount
                    val y =
                        (run.height * (0.1f + (index * 41 % 83) / 100f) + drift * 0.12f) % span - 17f
                    val tint = colors[index and 3]
                    pixelCircle(
                        tint.copy(alpha = 0.09f),
                        radius,
                        Offset(x, y),
                        style = Stroke(0.8f)
                    )
                    pixelCircle(tint.copy(alpha = 0.14f), 1.3f, Offset(x, y - radius))
                }
                translate(left = run.width / 2f, top = run.anchor + run.camera * run.unit) {
                    scale(scale = run.unit, pivot = Offset.Zero) {
                        val top = run.camera + run.anchor / run.unit
                        val bottom = run.camera - (run.height - run.anchor) / run.unit
                        drawLine(
                            color = style.detailColor.copy(alpha = 0.14f),
                            start = Offset(0f, -top),
                            end = Offset(0f, -bottom),
                            strokeWidth = 1f
                        )
                        for (gate in run.gates) {
                            if (gate.switchY + 20f < bottom || gate.y - gate.extent - 12f > top) continue
                            val alpha = if (gate.switched) 0.44f else 1f
                            translate(top = -gate.y) {
                                drawSwitchGate(gate, colors, alpha)
                            }
                            if (!gate.collected) {
                                val pulse = 1f + 0.13f * sin(gate.angle * 3f * SwitchRadians)
                                val point = Offset(0f, -gate.starY)
                                pixelCircle(
                                    color = style.accentColor.copy(alpha = 0.12f),
                                    radius = 15f * pulse,
                                    center = point
                                )
                                translate(left = point.x, top = point.y) {
                                    scale(scale = 9f * pulse, pivot = Offset.Zero) {
                                        drawPath(SwitchStar, style.accentColor)
                                    }
                                }
                            }
                            if (!gate.switched) {
                                val center = Offset(0f, -gate.switchY)
                                repeat(4) { index ->
                                    drawArc(
                                        color = colors[index],
                                        startAngle = gate.angle + index * 90f,
                                        sweepAngle = 90f,
                                        useCenter = false,
                                        topLeft = center - Offset(10f, 10f),
                                        size = Size(20f, 20f),
                                        style = Stroke(2.8f)
                                    )
                                }
                                pixelCircle(colors[gate.nextColor], 5f, center)
                            }
                        }
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.7f),
                            x = -18f,
                            y = -run.restHeight + SwitchBallRadius,
                            width = 36f,
                            height = 3f
                        )
                        run.effects.draw(
                            scope = this,
                            color = colors[run.color]
                        )
                        if (run.crash > 0f) return@scale
                        val center = Offset(0f, -run.ball)
                        if (run.launched) {
                            repeat(3) { index ->
                                drawSwitchBall(
                                    color = colors[run.color].copy(alpha = 0.16f - index * 0.04f),
                                    radius = SwitchBallRadius - index,
                                    center = center + Offset(0f, (index + 1) * run.velocity / 48f)
                                )
                            }
                        }
                        drawSwitchBall(colors[run.color], SwitchBallRadius, center)
                        drawSwitchBall(
                            style.playerDetailColor.copy(alpha = 0.8f),
                            2f,
                            center - Offset(2f, 2f)
                        )
                    }
                }
            }
        }
    }
}

private const val SwitchBallRadius = 6.5f

private fun DrawScope.drawSwitchBall(color: Color, radius: Float, center: Offset) {
    scale(0.5f, center) {
        pixelCircle(color, radius * 2f, center)
    }
}

private const val SwitchStroke = 10f
private const val SwitchStripeWidth = 44f
private const val SwitchRailWidth = 96f
private const val SwitchInnerRing = 40f
private const val SwitchDot = 4.4f
private const val SwitchRadians = 0.017453293f
private const val SwitchJump = 176f
private const val SwitchGravity = 430f
private const val SwitchRhythm = 0.18f
private const val SwitchRailGap = 23f
private const val SwitchOrbitSweep = 48f

private val SwitchStar = Path().apply {
    for (index in 0..9) {
        val radius = if (index % 2 == 0) 1f else 0.47f
        val angle = (index * 36f - 90f) * SwitchRadians
        val x = cos(angle) * radius
        val y = sin(angle) * radius
        if (index == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private fun DrawScope.drawSwitchGate(gate: SwitchGate, colors: List<Color>, alpha: Float) {
    when (gate.kind) {
        SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> {
            fun rail(y: Float, mirrored: Boolean) {
                val offset = if (mirrored) -gate.railOffset else gate.railOffset
                for (stripe in -7..7) {
                    val left = maxOf(-SwitchRailWidth, stripe * SwitchStripeWidth + offset)
                    val right = minOf(SwitchRailWidth, (stripe + 1) * SwitchStripeWidth + offset)
                    if (left >= right) continue
                    val color = gate.colorAt(if (mirrored) -stripe - 1 else stripe)
                    drawRoundRect(
                        color = colors[color].copy(alpha = alpha),
                        topLeft = Offset(left, y - SwitchStroke / 2f),
                        size = Size(right - left, SwitchStroke),
                        cornerRadius = CornerRadius(2.5f)
                    )
                }
            }
            if (gate.kind == SwitchGateKind.DoubleRail) {
                rail(-SwitchRailGap, mirrored = true)
                rail(SwitchRailGap, mirrored = false)
            } else {
                rail(0f, mirrored = false)
            }
        }

        SwitchGateKind.Ring, SwitchGateKind.DoubleRing -> {
            fun ring(radius: Float, angle: Float, reverse: Boolean) {
                repeat(8) { index ->
                    drawArc(
                        color = colors[gate.colorAt(if (reverse) -index else index)].copy(alpha = alpha),
                        startAngle = angle + index * 45f,
                        sweepAngle = 45f,
                        useCenter = false,
                        topLeft = Offset(-radius, -radius),
                        size = Size(radius * 2f, radius * 2f),
                        style = Stroke(SwitchStroke, cap = StrokeCap.Round)
                    )
                }
            }
            ring(gate.radius, gate.angle, reverse = false)
            if (gate.kind == SwitchGateKind.DoubleRing) ring(
                radius = SwitchInnerRing,
                angle = 135f - gate.angle,
                reverse = true
            )
        }

        SwitchGateKind.Orbit -> repeat(4) { index ->
            val radius = gate.radius
            drawArc(
                color = colors[gate.colorAt(index)].copy(alpha = alpha),
                startAngle = gate.angle + index * 90f,
                sweepAngle = SwitchOrbitSweep,
                useCenter = false,
                topLeft = Offset(-radius, -radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(SwitchStroke, cap = StrokeCap.Round)
            )
        }

        SwitchGateKind.Pulse, SwitchGateKind.Beads -> gate.forEachDot { color, point ->
            drawCircle(colors[color].copy(alpha = alpha), SwitchDot, point)
        }

        else -> {
            gate.forEachEdge { color, start, end ->
                drawLine(
                    color = colors[color].copy(alpha = alpha),
                    start = start,
                    end = end,
                    strokeWidth = SwitchStroke,
                    cap = StrokeCap.Round
                )
            }
            if (gate.kind == SwitchGateKind.Spinner || gate.kind == SwitchGateKind.TwinSpinner) {
                pixelCircle(colors[gate.colorOffset].copy(alpha = alpha), 6f, gate.hub)
                if (gate.kind == SwitchGateKind.TwinSpinner) {
                    pixelCircle(colors[gate.colorOffset].copy(alpha = alpha), 6f, -gate.hub)
                }
            }
        }
    }
}

@Serializable
private enum class SwitchGateKind {
    Ring, Rail, Square, Triangle, Spinner, Pulse, DoubleRing, Hexagon, TwinSpinner, DoubleRail,
    Orbit, Hourglass, Cross, Beads
}

@Serializable
private class SwitchGate(
    val y: Float,
    val radius: Float,
    val kind: SwitchGateKind,
    val colorOffset: Int,
    val nextColor: Int,
    val speed: Float,
    var angle: Float,
    var collected: Boolean = false,
    var switched: Boolean = false,
    val colorCount: Int = 4
) {
    val extent: Float
        get() = when (kind) {
            SwitchGateKind.Pulse -> radius + 9f
            SwitchGateKind.Beads -> radius + 3f
            SwitchGateKind.DoubleRail -> SwitchRailGap
            else -> radius
        }
    val switchY: Float
        get() = y + extent + 42f
    val starY: Float
        get() = if (kind == SwitchGateKind.Rail) y + 19f else y
    val railOffset: Float
        get() = angle / 360f * SwitchStripeWidth * 4f
    val hub: Offset
        get() = Offset(
            radius * (0.78f + sin(angle * SwitchRadians * 2f) * 0.12f) *
                    if (speed > 0f) 1f else -1f, 0f
        )

    fun colorAt(segment: Int): Int =
        (colorOffset + if (colorCount == 2) (segment / 2 and 1) * 2 else segment) and 3

    fun collides(ball: Float, color: Int): Boolean {
        val dy = y - ball
        val distance = abs(dy)
        val edge = SwitchStroke / 2f + SwitchBallRadius
        if (distance > extent + edge) return false
        when (kind) {
            SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> {
                fun hits(row: Float, mirrored: Boolean): Boolean {
                    if (abs(dy - row) > edge) return false
                    val offset = if (mirrored) -railOffset else railOffset
                    for (stripe in -7..7) {
                        if (colorAt(if (mirrored) -stripe - 1 else stripe) == color) continue
                        val left = maxOf(-SwitchRailWidth, stripe * SwitchStripeWidth + offset)
                        val right =
                            minOf(SwitchRailWidth, (stripe + 1) * SwitchStripeWidth + offset)
                        if (right <= left) continue
                        val corner = minOf(2.5f, (right - left) / 2f)
                        val dx = 0f.coerceIn(left + corner, right - corner)
                        val closest = dy.coerceIn(
                            row - SwitchStroke / 2f + corner,
                            row + SwitchStroke / 2f - corner
                        )
                        val contact = SwitchBallRadius + corner
                        if (dx * dx + (dy - closest) * (dy - closest) < contact * contact) return true
                    }
                    return false
                }
                if (kind == SwitchGateKind.DoubleRail) {
                    return hits(-SwitchRailGap, true) || hits(SwitchRailGap, false)
                }
                return hits(0f, false)
            }

            SwitchGateKind.Ring, SwitchGateKind.DoubleRing -> {
                fun hits(radius: Float, rotation: Float, reverse: Boolean): Boolean {
                    val gap = distance - radius
                    if (abs(gap) > edge) return false
                    val contact = if (dy > 0f) 90f else 270f
                    repeat(8) { index ->
                        if (colorAt(if (reverse) -index else index) == color) return@repeat
                        val offset = arcadeAngle(contact - rotation - index * 45f)
                        val excess = if (offset <= 45f) 0f else minOf(offset - 45f, 360f - offset)
                        val separation =
                            gap * gap + 2f * radius * distance * (1f - cos(excess * SwitchRadians))
                        if (separation < edge * edge) return true
                    }
                    return false
                }
                if (hits(radius, angle, false)) return true
                if (kind == SwitchGateKind.DoubleRing && hits(
                        SwitchInnerRing,
                        135f - angle,
                        true
                    )
                ) return true
            }

            SwitchGateKind.Orbit -> {
                val gap = distance - radius
                if (abs(gap) > edge) return false
                val contact = if (dy > 0f) 90f else 270f
                repeat(4) { index ->
                    if (colorAt(index) == color) return@repeat
                    val offset = arcadeAngle(contact - angle - index * 90f)
                    val excess = if (offset <= SwitchOrbitSweep) 0f else
                        minOf(offset - SwitchOrbitSweep, 360f - offset)
                    val distanceSquared =
                        gap * gap + 2f * radius * distance * (1f - cos(excess * SwitchRadians))
                    if (distanceSquared < edge * edge) return true
                }
            }

            SwitchGateKind.Pulse, SwitchGateKind.Beads -> {
                val ballPoint = Offset(0f, dy)
                val contact = SwitchBallRadius + SwitchDot
                forEachDot { tint, point ->
                    if (tint != color && (point - ballPoint).getDistanceSquared() < contact * contact) {
                        return true
                    }
                }
            }

            else -> {
                val ballPoint = Offset(0f, dy)
                forEachEdge { tint, start, end ->
                    if (tint != color) {
                        val line = end - start
                        val delta = ballPoint - start
                        val along =
                            ((delta.x * line.x + delta.y * line.y) / line.getDistanceSquared())
                                .coerceIn(0f, 1f)
                        if ((delta - line * along).getDistanceSquared() < edge * edge) return true
                    }
                }
            }
        }
        return false
    }

    inline fun forEachDot(draw: (Int, Offset) -> Unit) {
        if (kind == SwitchGateKind.Beads) {
            val extent = radius + sin(angle * 2f * SwitchRadians) * 3f
            repeat(16) { index ->
                draw(colorAt(index / 2), switchRay(angle + index * 22.5f) * extent)
            }
            return
        }
        forEachEdge { color, start, end ->
            repeat(4) { index ->
                draw(color, start + (end - start) * ((index + 0.5f) / 4f))
            }
        }
    }

    inline fun forEachEdge(draw: (Int, Offset, Offset) -> Unit) {
        if (kind == SwitchGateKind.Cross) {
            repeat(4) { index ->
                val ray = switchRay(angle + index * 90f)
                draw(colorAt(index and 1), ray * 18f, ray * radius)
            }
            return
        }
        if (kind == SwitchGateKind.Spinner || kind == SwitchGateKind.TwinSpinner) {
            val pivot = hub
            repeat(4) { index ->
                val ray = switchRay(angle + index * 90f)
                draw(colorAt(index), pivot + ray * 9f, pivot + ray * radius)
                if (kind == SwitchGateKind.TwinSpinner) {
                    val mirror = Offset(-ray.x, ray.y)
                    draw(colorAt(index), -pivot + mirror * 9f, -pivot + mirror * radius)
                }
            }
            return
        }
        if (kind == SwitchGateKind.Hourglass) {
            val axis = switchRay(angle)
            val cross = Offset(-axis.y, axis.x) * (radius * 0.58f)
            for (side in -1..1 step 2) {
                val tip = axis * (14f * side)
                val end = axis * (radius * 0.8f * side)
                val left = end - cross
                val right = end + cross
                val innerLeft = (tip + left) / 2f
                val innerRight = (tip + right) / 2f
                draw(colorAt(0), innerLeft, tip)
                draw(colorAt(0), tip, innerRight)
                draw(colorAt(1), left, innerLeft)
                draw(colorAt(2 + (side + 1) / 2), innerRight, right)
                draw(colorAt(0), right, left)
            }
            return
        }
        val sides = when (kind) {
            SwitchGateKind.Triangle -> 3
            SwitchGateKind.Hexagon -> 6
            else -> 4
        }
        val scale =
            if (kind == SwitchGateKind.Pulse) radius + sin(angle * 3f * SwitchRadians) * 9f else radius
        val step = 360f / sides
        var start = switchRay(angle) * scale
        repeat(sides) { index ->
            val end = switchRay(angle + (index + 1) * step) * scale
            if (sides == 6) {
                draw(colorAt(index % 3), start, end)
            } else {
                val mid = (start + end) / 2f
                val colors = if (sides == 3) 3 else 4
                draw(colorAt(index * 2 % colors), start, mid)
                draw(colorAt((index * 2 + 1) % colors), mid, end)
            }
            start = end
        }
    }
}

private fun switchRay(angle: Float): Offset {
    val radians = angle * SwitchRadians
    return Offset(cos(radians), sin(radians))
}

@Serializable
private class TintAscentRun : ArcadeRun() {
    val gates: ArcadeDeque<SwitchGate> = ArrayDeque()

    @SerialName("ball")
    private var altitude = 0.0
    val ball: Float
        get() = altitude.toFloat()
    var velocity = 0f
        private set
    var camera = 0f
        private set
    var color = 0
        private set
    var launched = false
        private set
    private var generated = 0
    var restHeight = 0f
        private set
    private val patterns = mutableListOf<SwitchGateKind>()
    var crash = 0f
        private set

    @Transient
    val effects = ArcadeEffects()

    val unit: Float
        get() = minOf(width / 254f, (height - topInset) / 232f)
    val anchor: Float
        get() = topInset + (height - topInset) * 0.65f

    override fun reset() {
        altitude = 0.0
        velocity = 0f
        camera = 0f
        color = Random.nextInt(4)
        launched = false
        generated = 0
        restHeight = 0f
        crash = 0f
        patterns.clear()
        effects.clear()
        gates.clear()
        fillGates()
    }

    override fun onResize(previous: Size) {
        fillGates()
    }

    fun jump() {
        if (crash > 0f) return
        launched = true
        velocity = SwitchJump
    }

    override fun advance(seconds: Float) {
        if (!launched) return
        effects.advance(seconds)
        if (crash > 0f) {
            crash -= seconds
            if (crash <= 0f) {
                crash = 0.001f
                finish()
            }
            return
        }
        val nextVelocity = (velocity - SwitchGravity * seconds).coerceAtLeast(-320f)
        altitude += (velocity + nextVelocity) * seconds / 2.0
        velocity = nextVelocity
        if (ball < restHeight) {
            altitude = restHeight.toDouble()
            velocity = 0f
        }
        camera += (maxOf(camera, ball - 26f) - camera) * minOf(1f, 13f * seconds)

        for (gate in gates) {
            gate.angle = arcadeAngle(gate.angle + gate.speed * seconds)
            if (gate.collides(ball, color)) {
                effects.burst(Offset(0f, -ball), 23)
                crash = 0.29f
                return
            }
            if (!gate.collected && abs(ball - gate.starY) <= SwitchBallRadius + 9f) {
                gate.collected = true
                score++
                effects.burst(Offset(0f, -gate.starY), 10)
            }
            if (!gate.switched && abs(ball - gate.switchY) < SwitchBallRadius + 10f) {
                gate.switched = true
                color = gate.nextColor
                restHeight = gate.switchY - 20f
                effects.burst(Offset(0f, -gate.switchY), 12)
            }
        }
        val bottom = camera - (height - anchor) / unit
        if (ball < bottom - SwitchBallRadius) {
            finish()
            return
        }
        while (gates.first().switchY < bottom - 24f) gates.removeFirst()
        fillGates()
    }

    private fun fillGates() {
        val top = camera + anchor / unit + 210f
        while (gates.isEmpty() || gates.last().y < top) {
            val index = generated++
            val previous = gates.lastOrNull()
            val kind = nextPattern(index, previous?.kind)
            val entryColor = previous?.nextColor ?: color
            val nextColor = (entryColor + Random.nextInt(1, 4)) and 3
            val spacing =
                if (index == 0) 52f + Random.nextFloat() * 11f else 71f + Random.nextFloat() * 32f
            val difficulty = index / (index + 35f)
            val variation = Random.nextFloat()
            val radius = when (kind) {
                SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> 0f
                SwitchGateKind.DoubleRing -> 74f + variation * 8f
                SwitchGateKind.Spinner, SwitchGateKind.TwinSpinner -> 49f + variation * 8f
                SwitchGateKind.Square -> 53f + variation * 12f
                SwitchGateKind.Hexagon -> 46f + variation * 15f
                SwitchGateKind.Triangle -> 69f + variation * 20f
                SwitchGateKind.Pulse -> 56f + variation * 11f
                SwitchGateKind.Orbit -> 55f + variation * 12f
                SwitchGateKind.Hourglass -> 65f + variation * 12f
                SwitchGateKind.Cross -> 51f + variation * 12f
                SwitchGateKind.Beads -> 48f + variation * 12f
                SwitchGateKind.Ring -> 44f + variation * 12f
            }
            val speed = when (kind) {
                SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> 64f + 78f * difficulty
                SwitchGateKind.DoubleRing -> 23f + 16f * difficulty
                SwitchGateKind.Spinner, SwitchGateKind.TwinSpinner -> 35f + 57f * difficulty
                SwitchGateKind.Triangle -> 30f + 52f * difficulty
                SwitchGateKind.Pulse -> 31f + 40f * difficulty
                SwitchGateKind.Orbit -> 37f + 42f * difficulty
                SwitchGateKind.Hourglass -> 22f + 33f * difficulty
                SwitchGateKind.Cross -> 29f + 44f * difficulty
                SwitchGateKind.Beads -> 28f + 40f * difficulty
                else -> 24f + 62f * difficulty
            } * if (Random.nextBoolean()) 1f else -1f

            val base = (previous?.switchY ?: 0f) + spacing
            val extent = when (kind) {
                SwitchGateKind.DoubleRail -> SwitchRailGap
                SwitchGateKind.Pulse -> radius + 9f
                SwitchGateKind.Beads -> radius + 3f
                else -> radius
            }
            val minimumSpeed = when {
                index == 0 -> 16f
                kind in listOf(
                    SwitchGateKind.Triangle, SwitchGateKind.Spinner,
                    SwitchGateKind.TwinSpinner, SwitchGateKind.Hourglass, SwitchGateKind.Orbit
                ) -> 48f

                else -> 32f
            }
            var gate: SwitchGate? = null
            for (attempt in 0..3) {
                val candidate = SwitchGate(
                    y = base + extent,
                    radius = radius,
                    kind = kind,
                    colorOffset = entryColor,
                    nextColor = nextColor,
                    speed = (abs(speed) * (1f - attempt * 0.24f)).coerceAtLeast(minimumSpeed) * if (speed > 0f) 1f else -1f,
                    angle = 0f,
                    colorCount = if (index == 0) 2 else 4
                )
                val rhythm = listOf(0.18f, 0.22f, 0.25f, 0.28f)[index % 4]
                if (findPlayableAngle(
                        gate = candidate,
                        color = entryColor,
                        margin = if (index == 0) 0.14f else 0.11f,
                        rhythm = rhythm,
                        floor = previous?.switchY?.minus(20f) ?: 0f
                    )
                ) {
                    gate = candidate
                    break
                }
            }
            if (gate == null) {
                val fallbackSpeed = if (speed > 0f) 104f else -104f
                gate = SwitchGate(
                    y = base,
                    radius = 0f,
                    kind = SwitchGateKind.Rail,
                    colorOffset = entryColor,
                    nextColor = nextColor,
                    speed = fallbackSpeed,
                    angle = Random.nextFloat() * 360f
                )
            }
            if (index == 0) findPlayableAngle(gate, entryColor, 0.14f, SwitchRhythm, 0f)
            gates.addLast(gate)
        }
    }

    private fun findPlayableAngle(
        gate: SwitchGate,
        color: Int,
        margin: Float,
        rhythm: Float,
        floor: Float
    ): Boolean {
        val edge = gate.extent + SwitchStroke / 2f + SwitchBallRadius
        val from = switchReferenceTime(gate.y - edge - floor, rhythm)
        val to = switchReferenceTime(gate.y + edge - floor, rhythm)
        val middle = (from + to) / 2f
        val firstAngle = Random.nextFloat() * 360f
        // Certify a complete crossing, then give it an independent phase. The
        // cleared gate leaves a ledge from which the player can choose a window.
        val samples = ((to - from) * 120f).toInt() + 1
        val angles = if (gate.kind == SwitchGateKind.DoubleRing) 180 else 72
        for (candidate in 0 until angles) {
            val phase = firstAngle + candidate * 360f / angles
            var fits = true
            for (sample in 0..samples) {
                val time = from + (to - from) * sample / samples
                val ball = floor + switchReferenceHeight(time, rhythm)
                for (offset in -1..1) {
                    gate.angle = arcadeAngle(phase + gate.speed * (time - middle + margin * offset))
                    if (gate.collides(ball, color)) {
                        fits = false
                        break
                    }
                }
                if (!fits) break
            }
            if (fits) {
                gate.angle = if (floor == 0f) arcadeAngle(phase - gate.speed * middle)
                else Random.nextFloat() * 360f
                return true
            }
        }
        return false
    }

    private fun nextPattern(index: Int, previous: SwitchGateKind?): SwitchGateKind {
        if (index == 0) {
            return SwitchGateKind.Ring
        }
        if (patterns.isEmpty()) {
            patterns += when {
                index < 4 -> listOf(
                    SwitchGateKind.Ring, SwitchGateKind.Rail, SwitchGateKind.Square
                )

                index < 10 -> listOf(
                    SwitchGateKind.Ring, SwitchGateKind.Rail, SwitchGateKind.Square,
                    SwitchGateKind.Hexagon, SwitchGateKind.Spinner,
                    SwitchGateKind.Cross, SwitchGateKind.Beads
                )

                else -> SwitchGateKind.entries
            }
            patterns.shuffle(Random)
        }
        if (patterns.size > 1 && patterns.last() == previous) {
            val last = patterns.lastIndex
            val next = patterns[0]
            patterns[0] = patterns[last]
            patterns[last] = next
        }
        return patterns.removeAt(patterns.lastIndex)
    }
}

private fun switchReferenceHeight(time: Float, rhythm: Float = SwitchRhythm): Float {
    val rise = rhythm * (SwitchJump - SwitchGravity * rhythm / 2f)
    val cycle = (time / rhythm).toInt()
    val sinceTap = time - cycle * rhythm
    return cycle * rise + sinceTap * (SwitchJump - SwitchGravity * sinceTap / 2f)
}

private fun switchReferenceTime(height: Float, rhythm: Float = SwitchRhythm): Float {
    val rise = rhythm * (SwitchJump - SwitchGravity * rhythm / 2f)
    val cycle = (height / rise).toInt()
    val remainder = height - cycle * rise
    val speed = sqrt((SwitchJump * SwitchJump - 2f * SwitchGravity * remainder).coerceAtLeast(0f))
    return cycle * rhythm + (SwitchJump - speed) / SwitchGravity
}