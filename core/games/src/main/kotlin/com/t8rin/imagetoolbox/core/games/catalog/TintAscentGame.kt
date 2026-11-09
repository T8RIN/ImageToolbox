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

@file:UseSerializers(ArcadeDequeSerializer::class)

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
import androidx.compose.ui.graphics.luminance
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeDequeSerializer
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private const val SwitchBallRadius = 6.5f
private const val SwitchStroke = 10f
private const val SwitchStripeWidth = 44f
private const val SwitchRailWidth = 96f
private const val SwitchInnerRing = 31f
private const val SwitchDot = 4.4f
private const val SwitchRadians = 0.017453293f
private const val SwitchJump = 176f
private const val SwitchGravity = 430f
private const val SwitchRhythm = 0.18f
private const val SwitchRhythmRise = SwitchRhythm * (SwitchJump - SwitchGravity * SwitchRhythm / 2f)
private const val SwitchRailGap = 23f

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

@Composable
fun TintAscentGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(initialState, ::TintAscentRun)
    val dark = style.textColor.luminance() > 0.5f
    val colors = remember(dark) {
        if (dark) {
            listOf(Color(0xFFFF709B), Color(0xFFFFCC66), Color(0xFF60DFCE), Color(0xFFAA9BFF))
        } else {
            listOf(Color(0xFFC92963), Color(0xFFAA6600), Color(0xFF008575), Color(0xFF6751C8))
        }
    }
    ArcadeBoard(
        game = ArcadeGame.TintAscent,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver,
        isPaused = isPaused
    ) {
        ArcadeField(run = run, onPress = { run.jump() }) {
            translate(run.width / 2f, run.anchor + run.camera * run.unit) {
                scale(run.unit, Offset.Zero) {
                    val top = run.camera + run.anchor / run.unit
                    val bottom = run.camera - (run.height - run.anchor) / run.unit
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.25f),
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
                            drawCircle(
                                style.accentColor.copy(alpha = 0.12f),
                                15f * pulse,
                                point
                            )
                            translate(point.x, point.y) {
                                scale(9f * pulse, Offset.Zero) {
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
                            drawCircle(colors[gate.nextColor], 5f, center)
                        }
                    }
                    if (!run.launched) {
                        drawRoundRect(
                            color = style.detailColor,
                            topLeft = Offset(-22f, SwitchBallRadius + 6f),
                            size = Size(44f, 4f),
                            cornerRadius = CornerRadius(2f)
                        )
                    }
                    run.effects.draw(this, colors[run.color])
                    if (run.crash > 0f) return@scale
                    val center = Offset(0f, -run.ball)
                    if (run.launched) {
                        repeat(3) { index ->
                            drawCircle(
                                color = colors[run.color].copy(alpha = 0.16f - index * 0.04f),
                                radius = SwitchBallRadius - index,
                                center = center + Offset(0f, (index + 1) * run.velocity / 48f)
                            )
                        }
                    }
                    drawCircle(colors[run.color], SwitchBallRadius, center)
                    drawCircle(Color.White.copy(alpha = 0.8f), 2f, center - Offset(2f, 2f))
                }
            }
        }
    }
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
                    drawRect(
                        color = colors[color].copy(alpha = alpha),
                        topLeft = Offset(left, y - SwitchStroke / 2f),
                        size = Size(right - left, SwitchStroke)
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
                        style = Stroke(SwitchStroke)
                    )
                }
            }
            ring(gate.radius, gate.angle, reverse = false)
            if (gate.kind == SwitchGateKind.DoubleRing) ring(
                SwitchInnerRing,
                135f - gate.angle,
                reverse = true
            )
        }

        SwitchGateKind.Pulse -> gate.forEachDot { color, point ->
            drawCircle(colors[color].copy(alpha = alpha), SwitchDot, point)
        }

        else -> {
            gate.forEachEdge { color, start, end ->
                drawLine(
                    colors[color].copy(alpha = alpha),
                    start,
                    end,
                    SwitchStroke,
                    StrokeCap.Round
                )
            }
            if (gate.kind == SwitchGateKind.Spinner || gate.kind == SwitchGateKind.TwinSpinner) {
                drawCircle(colors[gate.colorOffset].copy(alpha = alpha), 6f, gate.hub)
                if (gate.kind == SwitchGateKind.TwinSpinner) {
                    drawCircle(colors[gate.colorOffset].copy(alpha = alpha), 6f, -gate.hub)
                }
            }
        }
    }
}

@Serializable
internal enum class SwitchGateKind {
    Ring, Rail, Square, Triangle, Spinner, Pulse, DoubleRing, Hexagon, TwinSpinner, DoubleRail
}

@Serializable
internal class SwitchGate(
    val y: Float,
    val radius: Float,
    val kind: SwitchGateKind,
    val colorOffset: Int,
    val nextColor: Int,
    val speed: Float,
    var angle: Float,
    var collected: Boolean = false,
    var switched: Boolean = false
) {
    val extent: Float
        get() = when (kind) {
            SwitchGateKind.Pulse -> radius + 9f
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

    fun colorAt(segment: Int): Int = (segment + colorOffset) and 3

    fun collides(ball: Float, color: Int): Boolean {
        val dy = y - ball
        val distance = abs(dy)
        val edge = SwitchStroke / 2f + SwitchBallRadius
        if (distance > extent + edge) return false
        when (kind) {
            SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> {
                if (abs(distance - extent) > edge) return false
                val stripe = if (kind == SwitchGateKind.DoubleRail && dy < 0f) {
                    -floor(railOffset / SwitchStripeWidth).toInt() - 1
                } else {
                    floor(-railOffset / SwitchStripeWidth).toInt()
                }
                return colorAt(stripe) != color
            }

            SwitchGateKind.Ring, SwitchGateKind.DoubleRing -> {
                val contact = if (dy > 0f) 90f else 270f
                if (abs(distance - radius) <= edge) {
                    return colorAt((arcadeAngle(contact - angle) / 45f).toInt()) != color
                }
                if (kind == SwitchGateKind.DoubleRing && abs(distance - SwitchInnerRing) <= edge) {
                    val segment = (arcadeAngle(contact - 135f + angle) / 45f).toInt()
                    return colorAt(-segment) != color
                }
            }

            SwitchGateKind.Pulse -> {
                val ballPoint = Offset(0f, dy)
                val contact = SwitchBallRadius + SwitchDot
                forEachDot { tint, point ->
                    if (tint != color && (point - ballPoint).getDistanceSquared() < contact * contact) return true
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
        forEachEdge { color, start, end ->
            repeat(4) { index ->
                draw(color, start + (end - start) * ((index + 0.5f) / 4f))
            }
        }
    }

    inline fun forEachEdge(draw: (Int, Offset, Offset) -> Unit) {
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
internal class TintAscentRun : ArcadeRun() {
    val gates = ArrayDeque<SwitchGate>()
    var ball = 0f
        private set
    var velocity = 0f
        private set
    var camera = 0f
        private set
    var color = 0
        private set
    var launched = false
        private set
    private var generated = 0
    private var elapsed = 0f
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
        ball = 0f
        velocity = 0f
        camera = 0f
        color = Random.nextInt(4)
        launched = false
        generated = 0
        elapsed = 0f
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
        elapsed += seconds
        val nextVelocity = (velocity - SwitchGravity * seconds).coerceAtLeast(-320f)
        ball += (velocity + nextVelocity) * seconds / 2f
        velocity = nextVelocity
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
                SwitchGateKind.DoubleRing -> 62f + variation * 6f
                SwitchGateKind.Spinner, SwitchGateKind.TwinSpinner -> 49f + variation * 8f
                SwitchGateKind.Square -> 53f + variation * 12f
                SwitchGateKind.Hexagon -> 46f + variation * 15f
                SwitchGateKind.Triangle -> 69f + variation * 20f
                SwitchGateKind.Pulse -> 56f + variation * 11f
                SwitchGateKind.Ring -> 44f + variation * 12f
            }
            val speed = when (kind) {
                SwitchGateKind.Rail, SwitchGateKind.DoubleRail -> 64f + 78f * difficulty
                SwitchGateKind.DoubleRing -> 23f + 16f * difficulty
                SwitchGateKind.Spinner, SwitchGateKind.TwinSpinner -> 35f + 57f * difficulty
                SwitchGateKind.Triangle -> 30f + 52f * difficulty
                SwitchGateKind.Pulse -> 31f + 40f * difficulty
                else -> 24f + 62f * difficulty
            } * if (Random.nextBoolean()) 1f else -1f

            val base = (previous?.switchY ?: 0f) + spacing
            val extent = when (kind) {
                SwitchGateKind.DoubleRail -> SwitchRailGap
                SwitchGateKind.Pulse -> radius + 9f
                else -> radius
            }
            var gate: SwitchGate? = null
            for (attempt in 0..2) {
                val candidate = SwitchGate(
                    y = base + extent,
                    radius = radius,
                    kind = kind,
                    colorOffset = entryColor,
                    nextColor = nextColor,
                    speed = speed * (1f - attempt * 0.24f),
                    angle = 0f
                )
                if (findPlayableAngle(candidate, entryColor, if (index == 0) 0.13f else 0.08f)) {
                    gate = candidate
                    break
                }
            }
            if (gate == null) {
                val fallbackRadius = 43f + variation * 7f
                val center = base + fallbackRadius
                val fallbackSpeed = if (speed > 0f) 23f else -23f
                gate = SwitchGate(
                    y = center,
                    radius = fallbackRadius,
                    kind = SwitchGateKind.Ring,
                    colorOffset = entryColor,
                    nextColor = nextColor,
                    speed = fallbackSpeed,
                    angle = arcadeAngle(
                        67.5f + fallbackSpeed * (elapsed - switchReferenceTime(
                            center
                        ))
                    )
                )
            }
            gates.addLast(gate)
        }
    }

    private fun findPlayableAngle(gate: SwitchGate, color: Int, margin: Float): Boolean {
        val edge = gate.extent + SwitchStroke / 2f + SwitchBallRadius
        val from = switchReferenceTime(gate.y - edge)
        val to = switchReferenceTime(gate.y + edge)
        val middle = (from + to) / 2f
        val firstAngle = Random.nextFloat() * 360f
        // Each gate must leave a window for the same repeating tap rhythm, so an
        // individually valid opening also lines up with the rest of the course.
        val samples = ((to - from) * 120f).toInt() + 1
        for (candidate in 0 until 36) {
            val phase = firstAngle + candidate * 10f
            var fits = true
            for (sample in 0..samples) {
                val time = from + (to - from) * sample / samples
                val ball = switchReferenceHeight(time)
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
                gate.angle = arcadeAngle(phase + gate.speed * (elapsed - middle))
                return true
            }
        }
        return false
    }

    private fun nextPattern(index: Int, previous: SwitchGateKind?): SwitchGateKind {
        if (index == 0) {
            return listOf(
                SwitchGateKind.Ring, SwitchGateKind.Rail, SwitchGateKind.Square,
                SwitchGateKind.Hexagon, SwitchGateKind.Spinner
            ).random()
        }
        if (patterns.isEmpty()) {
            patterns += SwitchGateKind.entries
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

private fun switchReferenceHeight(time: Float): Float {
    val cycle = (time / SwitchRhythm).toInt()
    val sinceTap = time - cycle * SwitchRhythm
    return cycle * SwitchRhythmRise + sinceTap * (SwitchJump - SwitchGravity * sinceTap / 2f)
}

private fun switchReferenceTime(height: Float): Float {
    val cycle = (height / SwitchRhythmRise).toInt()
    val remainder = height - cycle * SwitchRhythmRise
    val speed = sqrt((SwitchJump * SwitchJump - 2f * SwitchGravity * remainder).coerceAtLeast(0f))
    return cycle * SwitchRhythm + (SwitchJump - speed) / SwitchGravity
}
