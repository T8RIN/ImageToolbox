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
import androidx.compose.ui.graphics.drawscope.clipRect
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
import kotlin.random.Random

private const val SwitchBallRadius = 6.5f
private const val SwitchStroke = 10f
private const val SwitchStripeWidth = 44f
private const val SwitchRailWidth = 96f
private const val SwitchInnerRing = 31f
private const val SwitchDot = 4.4f
private const val SwitchRadians = 0.017453293f

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
            clipRect(left = 0f, top = run.topInset, right = run.width, bottom = run.height) {
                translate(run.width / 2f, run.anchor + run.camera * run.unit) {
                    scale(run.unit, Offset.Zero) {
                        val top = run.camera + (run.anchor - run.topInset) / run.unit
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
}

private fun DrawScope.drawSwitchGate(gate: SwitchGate, colors: List<Color>, alpha: Float) {
    when (gate.kind) {
        SwitchGateKind.Rail -> {
            val offset = gate.railOffset
            for (stripe in -7..2) {
                val left = maxOf(-SwitchRailWidth, stripe * SwitchStripeWidth + offset)
                val right = minOf(SwitchRailWidth, (stripe + 1) * SwitchStripeWidth + offset)
                if (left >= right) continue
                drawRect(
                    color = colors[gate.colorAt(stripe)].copy(alpha = alpha),
                    topLeft = Offset(left, -SwitchStroke / 2f),
                    size = Size(right - left, SwitchStroke)
                )
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
            if (gate.kind == SwitchGateKind.Spinner) {
                drawCircle(colors[gate.colorOffset].copy(alpha = alpha), 6f, gate.hub)
            }
        }
    }
}

@Serializable
internal enum class SwitchGateKind {
    Ring, Rail, Square, Triangle, Spinner, Pulse, DoubleRing
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
        get() = if (kind == SwitchGateKind.Pulse) radius + 9f else radius
    val switchY: Float
        get() = y + extent + 42f
    val starY: Float
        get() = if (kind == SwitchGateKind.Rail) y + 19f else y
    val railOffset: Float
        get() = angle / 360f * SwitchStripeWidth * 4f
    val hub: Offset
        get() = Offset(radius * if (speed > 0f) 0.76f else -0.76f, 0f)

    fun colorAt(segment: Int): Int = (segment + colorOffset) and 3

    fun collides(ball: Float, color: Int): Boolean {
        val dy = y - ball
        val distance = abs(dy)
        val edge = SwitchStroke / 2f + SwitchBallRadius
        if (distance > extent + edge) return false
        when (kind) {
            SwitchGateKind.Rail -> {
                return colorAt(floor(-railOffset / SwitchStripeWidth).toInt()) != color
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
        if (kind == SwitchGateKind.Spinner) {
            val pivot = hub
            repeat(4) { index ->
                val ray = switchRay(angle + index * 90f)
                draw(colorAt(index), pivot + ray * 9f, pivot + ray * radius)
            }
            return
        }
        val sides = if (kind == SwitchGateKind.Triangle) 3 else 4
        val scale =
            if (kind == SwitchGateKind.Pulse) radius + sin(angle * 3f * SwitchRadians) * 9f else radius
        val step = 360f / sides
        var start = switchRay(angle) * scale
        repeat(sides) { index ->
            val end = switchRay(angle + (index + 1) * step) * scale
            if (sides == 3) {
                draw(colorAt(index), start, end)
            } else {
                val mid = (start + end) / 2f
                draw(colorAt(index * 2), start, mid)
                draw(colorAt(index * 2 + 1), mid, end)
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
        velocity = 176f
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
        velocity = (velocity - 430f * seconds).coerceAtLeast(-320f)
        ball += velocity * seconds
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
        val top = camera + (anchor - topInset) / unit + 210f
        while (gates.isEmpty() || gates.last().y < top) {
            val index = generated++
            val difficulty = index.toFloat() / (index + 35f)
            val previous = gates.lastOrNull()
            val entryColor = previous?.nextColor ?: color
            val kind = nextPattern(index, previous?.kind)
            val radius = when (kind) {
                SwitchGateKind.Rail -> 0f
                SwitchGateKind.DoubleRing -> 65f
                SwitchGateKind.Spinner -> 55f
                SwitchGateKind.Square -> 59f
                SwitchGateKind.Triangle -> 88f
                SwitchGateKind.Pulse -> 62f
                else -> 49f + Random.nextFloat() * 10f * difficulty
            }
            val speed = when (kind) {
                SwitchGateKind.Rail -> 82f + 73f * difficulty
                SwitchGateKind.DoubleRing -> 24f + 10f * difficulty
                SwitchGateKind.Spinner -> 40f + 50f * difficulty
                SwitchGateKind.Triangle -> 42f + 50f * difficulty
                SwitchGateKind.Pulse -> 36f + 34f * difficulty
                else -> if (index == 0) 25f else 31f + 60f * difficulty
            }
            val direction = if (index == 0 || Random.nextBoolean()) 1f else -1f
            gates.addLast(
                SwitchGate(
                    y = previous?.let { it.switchY + 88f + radius } ?: 101f,
                    radius = radius,
                    kind = kind,
                    colorOffset = if (index == 0 || kind == SwitchGateKind.Triangle) entryColor else Random.nextInt(
                        4
                    ),
                    nextColor = (entryColor + Random.nextInt(1, 4)) and 3,
                    speed = speed * direction,
                    angle = if (index == 0) 52f else Random.nextFloat() * 360f
                )
            )
        }
    }

    private fun nextPattern(index: Int, previous: SwitchGateKind?): SwitchGateKind {
        when (index) {
            0 -> return SwitchGateKind.Ring
            1 -> return SwitchGateKind.Square
            2 -> return SwitchGateKind.Rail
            3 -> return SwitchGateKind.Spinner
            4 -> return SwitchGateKind.Triangle
            5 -> return SwitchGateKind.Pulse
            6 -> return SwitchGateKind.DoubleRing
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
