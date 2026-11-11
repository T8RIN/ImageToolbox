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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeStars
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Orbit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@RegisterArcadeGame
internal data object OrbitSurvivalGame : ArcadeGameDefinition {
    override val title = R.string.arcade_orbitsurvival
    override val description = R.string.arcade_orbitsurvival_sub
    override val icon get() = Icons.Outlined.Orbit
    override val order = 26

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::OrbitSurvivalRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onPress = run::steer,
                onDrag = run::steer,
                onRelease = run::stopInput
            ) {
                arcadeStars(
                    style = style,
                    width = run.width,
                    height = run.height,
                    drift = run.time * 9f
                )
                val center = run.center
                val radius = run.radius
                pixelCircle(
                    color = style.detailColor.copy(alpha = 0.25f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1f)
                )
                pixelCircle(
                    color = style.playerColor.copy(alpha = run.pulse * 0.16f),
                    radius = radius * (1f + run.pulse * 0.15f),
                    center = center,
                    style = Stroke(width = 2f)
                )
                clipRect(top = 0f, bottom = run.height) {
                    for (gate in run.gates) {
                        rotate(degrees = gate.angle * 180f / PI.toFloat(), pivot = center) {
                            if (gate.kind < 3) {
                                for ((left, right) in run.segments(gate.kind)) {
                                    val point =
                                        Offset(
                                            x = center.x + left * radius,
                                            y = center.y + gate.y * radius
                                        )
                                    val size =
                                        Size(
                                            width = (right - left) * radius,
                                            height = radius * 0.22f
                                        )
                                    val corner = CornerRadius(2f)
                                    drawRoundRect(
                                        color = lerp(
                                            style.obstacleColor,
                                            style.playerDetailColor,
                                            0.28f
                                        ),
                                        topLeft = point + Offset(0f, 2f),
                                        size = size,
                                        cornerRadius = corner
                                    )
                                    drawRoundRect(
                                        color = style.obstacleColor,
                                        topLeft = point,
                                        size = size,
                                        cornerRadius = corner
                                    )
                                    drawRoundRect(
                                        color = style.playerDetailColor.copy(alpha = 0.3f),
                                        topLeft = point,
                                        size = size,
                                        cornerRadius = corner,
                                        style = Stroke(1f)
                                    )
                                    pixelRect(
                                        style.playerDetailColor.copy(alpha = 0.22f),
                                        point.x + 3f,
                                        point.y + size.height - 3f,
                                        size.width - 6f,
                                        2f
                                    )
                                    pixelRect(
                                        style.textColor.copy(alpha = 0.24f),
                                        point.x + 3f, point.y + 2f, size.width - 6f, 1f
                                    )
                                    val panels = (size.width / 27f).toInt().coerceAtLeast(1)
                                    for (panel in 1 until panels) {
                                        val seam = point.x + size.width * panel / panels
                                        pixelRect(
                                            style.playerDetailColor.copy(alpha = 0.22f),
                                            seam,
                                            point.y + 4f,
                                            1f,
                                            size.height - 8f
                                        )
                                    }
                                    for (end in listOf(5f, size.width - 7f)) {
                                        pixelRect(
                                            style.playerDetailColor.copy(alpha = 0.45f),
                                            point.x + end,
                                            point.y + size.height / 2f - 1f,
                                            2f,
                                            2f
                                        )
                                    }
                                }
                            } else {
                                for (meteor in run.meteors(gate)) {
                                    val point = center + meteor * radius
                                    pixelCircle(
                                        color = style.hazardColor.copy(alpha = 0.13f),
                                        radius = radius * 0.32f,
                                        center = point
                                    )
                                    scale(scale = radius * 0.46f / 32f, pivot = point) {
                                        drawArcadeSprite(
                                            sprite = OrbitMeteor,
                                            tint = style.obstacleColor,
                                            x = point.x - 16f,
                                            y = point.y - 16f
                                        )
                                    }
                                    pixelCircle(
                                        color = style.playerDetailColor.copy(alpha = 0.35f),
                                        radius = radius * 0.07f,
                                        center = point + Offset(
                                            x = -radius * 0.06f,
                                            y = -radius * 0.07f
                                        )
                                    )
                                    pixelCircle(
                                        color = style.detailColor,
                                        radius = radius * 0.045f,
                                        center = point + Offset(
                                            x = radius * 0.08f,
                                            y = radius * 0.06f
                                        )
                                    )
                                    drawLine(
                                        color = style.hazardColor.copy(alpha = 0.3f),
                                        start = point - Offset(x = 0f, y = radius * 0.28f),
                                        end = point - Offset(x = 0f, y = radius * 0.6f),
                                        strokeWidth = 2f
                                    )
                                }
                            }
                        }
                    }
                }
                for (side in listOf(-1f, 1f)) {
                    val color = if (side > 0f) style.playerColor else style.accentColor
                    run.trail.forEachIndexed { index, angle ->
                        val point = center + Offset(
                            x = cos(angle),
                            y = sin(angle)
                        ) * radius * side
                        pixelCircle(
                            color = color.copy(alpha = (index + 1) * 0.025f),
                            radius = run.dotRadius * 0.75f,
                            center = point
                        )
                    }
                    val point = run.dot(side)
                    pixelCircle(
                        color = color.copy(alpha = 0.14f),
                        radius = run.dotRadius * 1.8f,
                        center = point
                    )
                    pixelCircle(
                        color = color,
                        radius = run.dotRadius,
                        center = point
                    )
                    pixelCircle(
                        color = style.playerDetailColor.copy(alpha = 0.8f),
                        radius = run.dotRadius * 0.3f,
                        center = point - Offset(x = 1f, y = 1.5f)
                    )
                }
                pixelCircle(
                    color = style.detailColor,
                    radius = 2f,
                    center = center
                )
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private val OrbitSegments = listOf(
    listOf(-0.62f to 0.62f),
    listOf(-1.5f to -0.27f, 0.27f to 1.5f),
    listOf(-1.5f to -1.02f, -0.4f to 0.4f, 1.02f to 1.5f)
)

private val OrbitMeteor = ArcadeSprite(
    width = 32,
    height = 32,
    data = "M9 1H19V3H25V7H29V12H32V20H28V24H25V28H18V31H11V28H5V24H2V17H0V10H4V5H9Z"
)

@Serializable
private data class OrbitGate(
    var y: Float,
    val kind: Int,
    var passed: Boolean = false,
    val angle: Float = 0f,
    val offset: Float = 0.6f
)

@Serializable
private class OrbitSurvivalRun : ArcadeRun() {
    val gates = mutableListOf<OrbitGate>()
    val trail = mutableListOf<Float>()

    @Transient
    val effects = ArcadeEffects()
    val center get() = Offset(x = width / 2f, y = topInset + (height - topInset) * 0.64f)
    val radius get() = minOf((width - 60f) * 0.3f, (height - topInset - 32f) * 0.28f)
    val dotRadius get() = radius * 0.115f
    var angle = 0f
    var time = 0f
    var pulse = 0f

    @Transient
    private var direction = 0
    private var timer = 0f
    private var previousKind = -1
    private var previousAngle = 0f
    private val patterns: ArcadeDeque<Int> = ArrayDeque()
    private var trailTime = 0f
    val speed get() = 1.45f + (time * 0.004f).coerceAtMost(1f)

    fun dot(side: Float) = center + Offset(x = cos(angle), y = sin(angle)) * radius * side

    fun segments(kind: Int) = OrbitSegments[kind]

    fun meteors(gate: OrbitGate): List<Offset> =
        if (gate.kind == 3) listOf(Offset(x = gate.offset, y = gate.y))
        else listOf(
            Offset(x = gate.offset, y = gate.y),
            Offset(x = -gate.offset, y = gate.y - 0.85f)
        )

    override fun reset() {
        gates.clear()
        trail.clear()
        effects.clear()
        angle = 0f
        time = 0f
        pulse = 0f
        direction = 0
        previousKind = -1
        previousAngle = 0f
        patterns.clear()
        timer = 0f
        trailTime = 0f
    }

    fun steer(point: Offset) {
        direction = when {
            point.x < center.x - 12f -> -1
            point.x > center.x + 12f -> 1
            else -> 0
        }
    }

    override fun stopInput() {
        direction = 0
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        pulse = (pulse - seconds * 2.5f).coerceAtLeast(0f)
        val full = PI.toFloat() * 2f
        angle = (angle + direction * 3.5f * seconds + full) % full
        timer -= seconds
        if (timer <= 0f) {
            if (patterns.isEmpty()) patterns.addAll((0..4).shuffled())
            if (patterns.first() == previousKind) patterns.addLast(patterns.removeFirst())
            val kind = if (previousKind < 0) 1 else patterns.removeFirst()
            val angles =
                listOf(0f, PI.toFloat() / 4f, -PI.toFloat() / 4f, PI.toFloat() / 2f, PI.toFloat())
            val angle = if (previousKind < 0) 0f else angles.filter { it != previousAngle }.random()
            previousKind = kind
            previousAngle = angle
            val dx = maxOf(center.x, width - center.x)
            val dy = maxOf(center.y, height - center.y)
            gates += OrbitGate(
                y = -sqrt(dx * dx + dy * dy) / radius - 0.4f, kind = kind, angle = angle,
                offset = if (kotlin.random.Random.nextBoolean()) 0.65f else -0.65f
            )
            timer += (if (kind == 4) 3.5f else 2.6f) / speed + 0.95f
        }
        val dotSize = dotRadius / radius
        for (gate in gates) {
            gate.y += speed * seconds
            for (side in listOf(-1f, 1f)) {
                val relative = angle - gate.angle
                val x = cos(relative) * side
                val y = sin(relative) * side
                val hit = if (gate.kind < 3) segments(gate.kind).any { (left, right) ->
                    val dx = x - x.coerceIn(left, right)
                    val dy = y - y.coerceIn(gate.y, gate.y + 0.22f)
                    dx * dx + dy * dy < dotSize * dotSize
                } else meteors(gate).any {
                    val dx = x - it.x
                    val dy = y - it.y
                    dx * dx + dy * dy < (dotSize + 0.23f) * (dotSize + 0.23f)
                }
                if (hit) {
                    effects.burst(
                        point = dot(side),
                        count = 18
                    )
                    phase = ArcadePhase.Over
                    return
                }
            }
            if (!gate.passed && gate.y > 1f + dotSize +
                (if (gate.kind >= 3) 0.23f else 0f) + (if (gate.kind == 4) 0.85f else 0f)
            ) {
                gate.passed = true
                score++
                pulse = 1f
            }
        }
        gates.removeAll { it.y > maxOf(width, height) / radius + 2f }
        trailTime += seconds
        if (trailTime > 0.035f) {
            trailTime = 0f
            trail += angle
            if (trail.size > 7) trail.removeAt(0)
        }
    }
}