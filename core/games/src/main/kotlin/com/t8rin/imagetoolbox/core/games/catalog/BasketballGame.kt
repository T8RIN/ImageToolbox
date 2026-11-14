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
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.SportsBasketball
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.pow
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
        val measurer = rememberTextMeasurer()

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::begin,
                onDrag = run::aim,
                onRelease = run::shoot
            ) {
                drawBasketballBackground(style, run.width, run.height)
                if (run.combo >= 3) drawRect(style.accentColor.copy(alpha = 0.06f))
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
                for (star in run.stars) {
                    val point = run.project(star)
                    val radius = (5f + sin(run.time * 4f) * 0.7f) * unit
                    pixelCircle(style.accentColor.copy(alpha = 0.12f), radius * 2f, point)
                    pixelRect(
                        style.accentColor,
                        point.x - radius,
                        point.y - unit,
                        radius * 2f,
                        unit * 2f
                    )
                    pixelRect(
                        style.accentColor,
                        point.x - unit,
                        point.y - radius,
                        unit * 2f,
                        radius * 2f
                    )
                    pixelCircle(style.playerDetailColor, unit, point)
                }
                for (prop in run.props) {
                    if (prop.hits == 0) continue
                    val point = run.project(prop.positionAt(run.time))
                    val color = if (prop.kind == 2) style.accentColor else style.obstacleColor
                    if (prop.kind == 0) {
                        val axis = Offset(cos(prop.angle), sin(prop.angle)) * prop.size
                        drawLine(
                            color,
                            run.project(prop.center - axis),
                            run.project(prop.center + axis),
                            6f * unit
                        )
                        drawLine(
                            style.playerDetailColor.copy(alpha = 0.5f),
                            run.project(prop.center - axis),
                            run.project(prop.center + axis),
                            1.5f * unit
                        )
                    } else {
                        val radius = prop.size * unit
                        pixelCircle(
                            color.copy(alpha = 0.1f + prop.flash * 0.15f),
                            radius + 8f * unit,
                            point
                        )
                        pixelCircle(color, radius, point)
                        pixelCircle(style.playerDetailColor, radius * 0.75f, point)
                        pixelCircle(color, radius * 0.58f, point)
                        if (prop.kind == 2) {
                            for (mark in 0 until prop.hits) {
                                pixelCircle(
                                    style.playerDetailColor,
                                    1.4f * unit,
                                    point + Offset((mark - (prop.hits - 1) / 2f) * 6f * unit, 0f)
                                )
                            }
                            drawLine(
                                style.detailColor,
                                point - Offset(0f, radius + 13f * unit),
                                point - Offset(0f, radius),
                                2f * unit
                            )
                        }
                    }
                }
                if (!run.airborne) drawBasketballBall(
                    run = run,
                    style = style
                )
                run.hoops.forEachIndexed { index, hoop ->
                    val point = run.project(hoop.center)
                    val color = if (index == 1) style.accentColor else style.obstacleColor
                    if (index > 0 && (hoop.travel > 0f || hoop.vertical > 0f)) {
                        val span = maxOf(hoop.travel, hoop.vertical)
                        val dots = ceil(span * 2f / 8f).toInt().coerceAtLeast(2)
                        for (dot in 0..dots) {
                            val progress = dot * 2f / dots - 1f
                            val point = if (hoop.travel > 0f) {
                                Offset(hoop.anchorX + hoop.travel * progress, hoop.anchorY)
                            } else {
                                Offset(hoop.anchorX, hoop.anchorY + hoop.vertical * progress)
                            }
                            pixelCircle(
                                color = style.detailColor.copy(alpha = 0.75f),
                                radius = 1.5f * unit,
                                center = run.project(point)
                            )
                        }
                    }
                    for ((from, to) in hoop.walls()) {
                        drawLine(color, run.project(from), run.project(to), 5f * unit)
                        drawLine(
                            style.playerDetailColor.copy(alpha = 0.45f), run.project(from),
                            run.project(to), 1.3f * unit
                        )
                    }
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
                    if (run.combo >= 3) {
                        val fade =
                            if (run.airborne) 1f else (run.celebration / 1.2f).coerceIn(0f, 1f)
                        val center = run.project(point)
                        pixelCircle(
                            style.obstacleColor.copy(alpha = (1f - index / 16f) * 0.08f * fade),
                            run.radius * unit * (1.5f - index / 24f), center
                        )
                        pixelRect(
                            style.accentColor.copy(alpha = index / 16f * 0.5f * fade),
                            center.x - 2f * unit, center.y - 2f * unit, 4f * unit, 4f * unit
                        )
                    }
                    pixelCircle(
                        color = (if (run.combo >= 3) style.accentColor else style.playerColor).copy(
                            alpha = index / 16f * 0.24f * if (run.airborne) 1f else (run.celebration / 1.2f).coerceIn(
                                0f,
                                1f
                            )
                        ),
                        radius = run.radius * unit * if (run.combo >= 3) 0.65f else 0.5f,
                        center = run.project(point)
                    )
                }
                if (run.celebration > 0f) {
                    val hoop = run.hoops.first()
                    val center = run.project(hoop.center)
                    pixelCircle(
                        style.accentColor.copy(alpha = run.celebration * 0.35f),
                        (40f + (1.2f - run.celebration) * 35f) * unit,
                        center,
                        style = Stroke(2f * unit)
                    )
                    val message = if (run.lastBonus > 1) "PERFECT ×${run.lastBonus}"
                    else if (run.lastBanks > 0) "BOUNCE ×${run.lastBanks}" else "+2"
                    val label = measurer.measure(
                        message,
                        style.scoreTextStyle.copy(fontSize = (13f * unit).sp),
                        density = Density(1f)
                    )
                    drawText(
                        label, style.accentColor.copy(alpha = run.celebration.coerceAtMost(1f)),
                        Offset(
                            (center.x - label.size.width / 2f).coerceIn(
                                8f,
                                (run.width - label.size.width - 8f).coerceAtLeast(8f)
                            ),
                            (center.y - 64f * unit).coerceAtLeast(run.topInset + 4f)
                        )
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

private val BasketballFlame = listOf(
    ArcadeSprite(
        width = 20, height = 13,
        data = "M0 6H1V5H3V4H4V5H5V4H6V3H7V2H8V3H10V2H11V1H13V0H15V1H16V2H17V3H18V4H19V6H20V7H19V8H18V9H17V10H16V12" +
                "H15V13H13V12H11V11H9V10H8V11H6V10H7V9H4V8H2V7H0Z M6 6V7H8V8H9V9H10V10H14V9H15V8H16V7H17V5H16V4H15V3H12V4H9V5H8V6Z"
    ),
    ArcadeSprite(
        width = 20, height = 13,
        data = "M6 6H8V5H9V4H12V3H15V4H16V5H17V7H16V8H15V9H14V10H10V9H9V8H8V7H6Z M9 6V7H10V8H13V7H14V5H11V6Z"
    ),
    ArcadeSprite(
        width = 20, height = 13,
        data = "M9 6H11V5H14V7H13V8H10V7H9Z"
    )
)

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
    if (run.combo >= 3) {
        pixelCircle(style.accentColor.copy(alpha = 0.1f), radius * 1.3f, center)
        if (run.airborne && run.velocity.getDistance() > 1f) {
            val motion = Offset(run.velocity.x, -run.velocity.y)
            val rotation = atan2(motion.y, motion.x) * 180f / kotlin.math.PI.toFloat()
            rotate(rotation, center) {
                translate(center.x - radius * 3.1f, center.y - radius * 1.3f) {
                    scale(
                        scaleX = radius / 5f * (1f + sin(run.time * 23f) * 0.06f),
                        scaleY = radius / 5f,
                        pivot = Offset.Zero
                    ) {
                        BasketballFlame.forEachIndexed { index, sprite ->
                            drawArcadeSprite(
                                sprite = sprite,
                                tint = when (index) {
                                    0 -> style.accentColor.copy(alpha = 0.9f)
                                    1 -> lerp(style.accentColor, style.playerDetailColor, 0.4f)
                                    else -> style.playerDetailColor
                                },
                                x = 0f,
                                y = 0f
                            )
                        }
                    }
                }
            }
        }
    }
    pixelCircle(lerp(style.playerColor, ink, 0.25f), radius, center)
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
    val color = style.detailColor.copy(alpha = 0.17f)
    drawLine(color, Offset(7f, 8f), Offset(width - 7f, 8f), 0.7f)
    val flags = ceil(width / 37f).toInt()
    repeat(flags) { index ->
        val x = 17f + index * 37f
        val path = Path().apply {
            moveTo(x - 3f, 8f)
            lineTo(x + 3f, 8f)
            lineTo(x + 3f, 13f)
            lineTo(x, 16f)
            lineTo(x - 3f, 13f)
            close()
        }
        drawPath(path, color, style = Stroke(0.7f))
    }
    val ballInk = style.detailColor.copy(alpha = 0.17f)
    fun ball(center: Offset) {
        val radius = 15f
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

    fun medal(center: Offset) {
        val radius = 11f
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

    val columns = ceil(width / 163f).toInt()
    val rows = ceil(height / 143f).toInt()
    for (row in 0 until rows) for (column in 0 until columns) {
        val center = Offset(column * 163f + 41f + row % 2 * 31f, row * 143f + 62f)
        if ((column + row) % 2 == 0) ball(center) else medal(center)
    }
}

@Serializable
private data class BasketballHoop(
    var center: ArcadeOffset,
    val halfWidth: Float,
    var angle: Float,
    var anchorX: Float = center.x,
    var travel: Float = 0f,
    val phase: Float = 0f,
    var vertical: Float = 0f,
    var anchorY: Float = center.y,
    var board: Int = 0,
    var guard: Int = 0,
    var swing: Float = 0f,
    var restingAngle: Float = angle
) {
    val tangent get() = Offset(cos(angle), sin(angle))
    val normal get() = Offset(-sin(angle), cos(angle))
    fun positionAt(time: Float) = Offset(
        anchorX + sin(time * 0.9f + phase) * travel,
        anchorY + sin(time * 0.75f + phase) * vertical
    )

    fun angleAt(time: Float) = restingAngle + sin(time * 0.8f + phase) * swing
    fun speedAt(time: Float) = Offset(
        cos(time * 0.9f + phase) * travel * 0.9f,
        cos(time * 0.75f + phase) * vertical * 0.75f
    )

    fun walls(center: Offset = this.center, rotation: Float = angle): List<Pair<Offset, Offset>> {
        val result = mutableListOf<Pair<Offset, Offset>>()
        if (board != 0) {
            val point = center + Offset(board * (halfWidth + 17f), 22f)
            result += point - Offset(0f, 43f) to point + Offset(0f, 43f)
        }
        if (guard > 0) {
            val entry = if (center.x > 180f) 2.3561945f else 0.7853982f
            val ranges = when (guard) {
                1 -> listOf(entry + 1.05f to entry + 5.23f)
                2 -> listOf(entry + 1.57f to entry + 4.71f)
                3 -> listOf(entry + 1.05f to entry + 2.09f, entry + 4.19f to entry + 5.23f)
                else -> listOf(entry + 2.36f to entry + 3.93f)
            }
            for ((from, to) in ranges) {
                val steps = ceil((to - from) / 0.26f).toInt()
                val radius = halfWidth + 34f
                for (step in 0 until steps) {
                    val a = from + (to - from) * step / steps + rotation * 0.25f
                    val b = from + (to - from) * (step + 1) / steps + rotation * 0.25f
                    result += center + Offset(cos(a), sin(a)) * radius to
                            center + Offset(cos(b), sin(b)) * radius
                }
            }
        }
        return result
    }
}

@Serializable
private data class BasketballProp(
    val center: ArcadeOffset,
    val kind: Int,
    val size: Float,
    val angle: Float = 0f,
    val travel: Float = 0f,
    val phase: Float = 0f,
    var hits: Int = 3,
    var cooldown: Float = 0f,
    var flash: Float = 0f
) {
    fun positionAt(time: Float) = center + Offset(sin(time + phase) * travel, 0f)
    fun speedAt(time: Float) = Offset(cos(time + phase) * travel, 0f)
}

private class BasketballFlight(
    var point: Offset,
    var velocity: Offset,
    var clean: Boolean = true,
    var banks: Int = 0
) {
    var caught = -1
    val contacts = mutableSetOf<Int>()
}

@Serializable
private class BasketballRun : ArcadeRun() {
    val hoops = mutableListOf<BasketballHoop>()
    val stars = mutableListOf<ArcadeOffset>()
    val props = mutableListOf<BasketballProp>()
    private val patterns: ArcadeDeque<Int> = ArrayDeque()
    @Transient
    val effects = ArcadeEffects()
    val trail: ArcadeDeque<ArcadeOffset> = ArrayDeque()
    @Transient
    val preview = mutableListOf<Offset>()
    val radius = 15f

    @Transient
    val gravity = 640f * 0.065f * 32f * 0.36f

    @Transient
    val maximumSpeed = 360f * 0.09f * 32f * 0.6f
    val unit
        get() = minOf(
            1f, width / 360f,
            (height - topInset - 12f) / ((hoops.getOrNull(1)?.center?.y ?: 80f) -
                    (hoops.firstOrNull()?.center?.y ?: 0f) + 150f)
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
    var celebration = 0f
        private set
    var lastBonus = 0
        private set
    var lastBanks = 0
        private set
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
    private var banks = 0
    private var trailTimer = 0f
    private var catchFrom: ArcadeOffset = Offset.Zero

    fun project(point: Offset) = Offset(
        (width - 360f * unit) / 2f + point.x * unit,
        baseline - (point.y - camera) * unit
    )

    override fun isValid() = super.isValid() && hoops.size >= 2 &&
            props.all { it.size > 0f && it.hits >= 0 }

    override fun reset() {
        hoops.clear()
        stars.clear()
        props.clear()
        patterns.clear()
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
        banks = 0
        airborne = false
        celebration = 0f
        lastBonus = 0
        lastBanks = 0
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
        if (patterns.isEmpty()) patterns.addAll((0..11).shuffled())
        val pattern = if (number < 5) 0 else patterns.removeFirst()
        val close = pattern == 1 || pattern == 11
        val gap = (height - topInset).coerceIn(170f, 660f) *
                (0.27f + Random.nextFloat() * 0.045f) * if (close) 0.8f else 1f
        val x = if (pattern == 4) {
            if (previous.center.x < 180f) 225f else 135f
        } else if (close) (previous.center.x + Random.nextFloat() * 36f - 18f).coerceIn(75f, 285f)
        else if (previous.center.x < 180f) 253f + Random.nextFloat() * 24f
        else 83f + Random.nextFloat() * 24f
        val side = if (x > 180f) 1 else -1
        val tilt = if (pattern in listOf(2, 8)) side * (0.48f + Random.nextFloat() * 0.38f) else 0f
        val hoop = BasketballHoop(
            center = Offset(x, previous.center.y + gap),
            halfWidth = if (pattern == 11) 43f else 36f,
            angle = tilt,
            travel = if (pattern == 4) 64f else 0f,
            phase = Random.nextFloat() * 6.2831855f,
            vertical = if (pattern == 5) 44f else 0f,
            board = if (pattern in listOf(3, 8)) side else 0,
            guard = if (number > 12 && pattern == 9) Random.nextInt(1, 5) else 0,
            swing = if (number > 10 && pattern == 6) 0.48f else 0f
        )
        hoop.center = hoop.positionAt(time)
        hoop.angle = hoop.angleAt(time)
        hoops += hoop
        val oldProps = props.size
        if (number >= 8 && pattern in listOf(7, 10)) {
            props += BasketballProp(
                center = Offset(180f, hoop.center.y + 83f),
                kind = if (pattern == 10) 2 else 1,
                size = if (pattern == 10) 19f else 21f,
                travel = if (number >= 18 && pattern == 7) 34f else 0f,
                phase = Random.nextFloat() * 6.2831855f
            )
        }
        if (number > 14 && pattern == 8) props += BasketballProp(
            Offset(180f, previous.center.y + gap * 0.5f), 0, 29f,
            angle = side * 0.35f
        )
        // Check the actual collision model, including rims, shields and moving objects.
        if (!hasShot()) {
            while (props.size > oldProps) props.removeAt(props.lastIndex)
            hoop.guard = 0
            if (!hasShot()) {
                hoop.swing = 0f
                hoop.travel = 0f
                hoop.vertical = 0f
                hoop.restingAngle *= 0.5f
                hoop.angle = hoop.angleAt(time)
                hoop.center = hoop.positionAt(time)
            }
            if (!hasShot()) {
                props.clear()
                hoop.board = 0
                hoop.restingAngle = 0f
                hoop.angle = 0f
                hoops.first().guard = 0
                hoop.anchorX = if (previous.center.x < 180f) 265f else 95f
                hoop.anchorY = previous.center.y + minOf(150f, gap)
                hoop.center = hoop.positionAt(time)
            }
        }
        if (number >= 5 && number % 3 == 2) stars += hoop.center +
                hoop.normal * 48f - hoop.tangent * side.toFloat() * 12f
    }

    private fun shotPose(pull: Offset): Pair<Offset, Float> {
        val angle = atan2(pull.x, pull.y)
        return hoops.first().center - Offset(-sin(angle), cos(angle)) * 20f +
                Offset(pull.x, -pull.y) * 0.22f to angle
    }

    private fun hasShot(): Boolean = hasShotAt(time) && hasShotAt(time + 0.35f)

    private fun hasShotAt(startTime: Float): Boolean {
        val hoop = hoops[1]
        for (sample in 0..48) {
            val duration = 0.75f + sample / 24f
            var pull = Offset.Zero
            var start = hoops.first().center - hoops.first().normal * 20f
            repeat(5) {
                val speed = (hoop.positionAt(startTime + duration) - start) / duration + Offset(
                    0f,
                    gravity * duration / 2f
                )
                pull = pullForVelocity(speed)
                start = shotPose(pull).first
            }
            if (pull.getDistance() > hoops.first().halfWidth * 3.65f || pull.y < 0f) continue
            var fits = true
            for (error in listOf(-0.75f, 0.75f)) {
                val attempt = pull + Offset(error, 0f)
                val pose = shotPose(attempt)
                val flight = BasketballFlight(pose.first, velocityForPull(attempt))
                var elapsed = 0f
                while (elapsed < 4.8f && flight.caught < 0) {
                    moveFlight(flight, 1f / 60f, startTime + elapsed, pose.second)
                    elapsed += 1f / 60f
                    if (flight.point.y < hoops.first().center.y - 100f) break
                }
                if (flight.caught != 1) {
                    fits = false
                    break
                }
            }
            if (fits) return true
        }
        return false
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
        val maximum = hoops.first().halfWidth * 3.7f
        pull = delta * minOf(1f, maximum / delta.getDistance().coerceAtLeast(0.001f))
        val pose = shotPose(pull)
        hoops.first().angle = pose.second
        ball = pose.first
        preview.clear()
        if (velocityForPull(pull).getDistance() < 1f) return
        val flight = BasketballFlight(ball, launchVelocity())
        repeat(10) { index ->
            moveFlight(flight, 1f / 12f, time + index / 12f)
            if (flight.caught >= 0 || project(flight.point).y > height) return
            preview += flight.point
        }
    }

    private fun launchVelocity() = velocityForPull(pull)

    private fun velocityForPull(pull: Offset): Offset {
        val distance = pull.getDistance()
        if (distance < 0.001f) return Offset.Zero
        val diameter = hoops.first().halfWidth * 2f
        val stretch = ((distance / diameter - 0.35f) / 1.5f).coerceIn(0f, 1f)
        val strength = 1f - (1f - stretch).pow(2.5f)
        return Offset(-pull.x, pull.y) / distance * (maximumSpeed * strength)
    }

    private fun pullForVelocity(velocity: Offset): Offset {
        val speed = velocity.getDistance()
        if (speed < 0.001f) return Offset.Zero
        val fraction = speed / maximumSpeed
        if (fraction >= 1f) return Offset(-velocity.x, velocity.y) / speed * 1000f
        val stretch = 1f - (1f - fraction).pow(0.4f)
        val distance = hoops.first().halfWidth * 2f * (0.35f + 1.5f * stretch)
        return Offset(-velocity.x, velocity.y) / speed * distance
    }

    fun shoot() {
        if (!aiming || airborne) return
        if (launchVelocity().getDistance() >= 1f) {
            velocity = launchVelocity()
            airborne = true
            clean = true
            banked = false
            banks = 0
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

    private fun dot(a: Offset, b: Offset) = a.x * b.x + a.y * b.y
    private fun closest(point: Offset, from: Offset, to: Offset): Offset {
        val segment = to - from
        val amount = (dot(point - from, segment) / segment.getDistanceSquared()
            .coerceAtLeast(0.001f)).coerceIn(0f, 1f)
        return from + segment * amount
    }

    private fun distanceToSegment(point: Offset, from: Offset, to: Offset) =
        (point - closest(point, from, to)).getDistance()

    private fun moveFlight(
        flight: BasketballFlight,
        seconds: Float,
        startTime: Float,
        launchAngle: Float = hoops.first().angle
    ) {
        val steps = ceil(seconds * 120f).toInt().coerceAtLeast(1)
        val step = seconds / steps
        repeat(steps) { frame ->
            if (flight.caught >= 0) return
            val now = startTime + (frame + 1) * step
            val before = flight.point
            flight.point += flight.velocity * step - Offset(0f, gravity * step * step / 2f)
            flight.velocity -= Offset(0f, gravity * step)
            if (flight.point.x < radius || flight.point.x > 360f - radius) {
                flight.point = flight.point.copy(x = flight.point.x.coerceIn(radius, 360f - radius))
                flight.velocity = flight.velocity.copy(x = -flight.velocity.x * 0.86f)
                flight.banks++
            }
            fun contact(
                point: Offset,
                size: Float,
                motion: Offset,
                bounce: Float,
                rim: Boolean = false
            ): Boolean {
                val delta = flight.point - point
                val distance = delta.getDistance()
                val limit = radius + size
                if (distance >= limit) return false
                val normal = if (distance > 0.001f) delta / distance else Offset(0f, 1f)
                flight.point = point + normal * limit
                val speed = dot(flight.velocity - motion, normal)
                if (speed >= 0f) return false
                flight.velocity -= normal * (speed * (1f + bounce))
                val magnitude = flight.velocity.getDistance()
                if (magnitude > 720f) flight.velocity *= 720f / magnitude
                if (rim) flight.clean = false else flight.banks++
                return true
            }
            for (index in 0..1) {
                val hoop = hoops[index]
                val center = if (index == 0) hoop.center else hoop.positionAt(now)
                val angle = if (index == 0) launchAngle else hoop.angleAt(now)
                val tangent = Offset(cos(angle), sin(angle))
                val motion = if (index == 0) Offset.Zero else hoop.speedAt(now)
                contact(center - tangent * hoop.halfWidth, 1.5f, motion, 0.68f, true)
                contact(center + tangent * hoop.halfWidth, 1.5f, motion, 0.68f, true)
                for ((from, to) in hoop.walls(center, angle)) contact(
                    closest(
                        flight.point,
                        from,
                        to
                    ), 2.5f, motion, 0.86f
                )
            }
            props.forEachIndexed { index, prop ->
                if (prop.hits == 0) return@forEachIndexed
                val center = prop.positionAt(now)
                val point = if (prop.kind == 0) {
                    val axis = Offset(cos(prop.angle), sin(prop.angle)) * prop.size
                    closest(flight.point, center - axis, center + axis)
                } else center
                if (contact(
                        point, if (prop.kind == 0) 3f else prop.size,
                        prop.speedAt(now), if (prop.kind == 1) 1.08f else 0.86f
                    )
                ) flight.contacts += index
            }
            for (index in 1 downTo 0) {
                val hoop = hoops[index]
                val center = if (index == 0) hoop.center else hoop.positionAt(now)
                val angle = if (index == 0) launchAngle else hoop.angleAt(now)
                val normal = Offset(-sin(angle), cos(angle))
                val tangent = Offset(cos(angle), sin(angle))
                val previous = if (index == 0) center else hoop.positionAt(now - step)
                val from = before - previous
                val to = flight.point - center
                val above = dot(from, normal)
                val below = dot(to, normal)
                if (above > 0f && below <= 0f) {
                    val crossing = from + (to - from) * (above / (above - below))
                    if (abs(dot(crossing, tangent)) < hoop.halfWidth - radius - 1.5f) {
                        flight.caught = index
                        return
                    }
                }
            }
        }
    }

    override fun advance(seconds: Float) {
        val beforeTime = time
        time += seconds
        for (index in 1..hoops.lastIndex) {
            hoops[index].center = hoops[index].positionAt(time)
            hoops[index].angle = hoops[index].angleAt(time)
        }
        celebration = (celebration - seconds).coerceAtLeast(0f)
        for (prop in props) {
            prop.cooldown = (prop.cooldown - seconds).coerceAtLeast(0f)
            prop.flash = (prop.flash - seconds * 3f).coerceAtLeast(0f)
        }
        if (aiming) touch?.let { aim(it + pull * unit) }
        effects.advance(seconds)
        net = (net - seconds * 2.8f).coerceAtLeast(0f)
        settle = (settle - seconds).coerceAtLeast(0f)
        val target = maxOf(hoops.first().center.y, ball.y - (height - topInset) / unit * 0.48f)
        camera += (target - camera) * minOf(1f, seconds * 5f)
        stars.removeAll { it.y < camera - (height - baseline) / unit - 24f }
        props.removeAll { it.center.y < hoops.first().center.y - 90f || it.hits == 0 }
        if (!airborne) {
            if (!aiming) {
                val rest = hoops.first().center - hoops.first().normal * 20f
                val amount = (settle / 0.22f).coerceIn(0f, 1f)
                ball = rest + (catchFrom - rest) * amount * amount
            }
            return
        }
        val previous = ball
        val flight = BasketballFlight(ball, velocity, clean, banks)
        moveFlight(flight, seconds, beforeTime)
        ball = flight.point
        velocity = flight.velocity
        clean = flight.clean
        banks = flight.banks
        banked = banks > 0
        spin += velocity.x * seconds * 1.8f
        for (index in flight.contacts) props.getOrNull(index)?.let { prop ->
            prop.flash = 1f
            if (prop.kind == 2 && prop.cooldown == 0f) {
                prop.hits--
                prop.cooldown = 0.2f
                effects.burst(prop.positionAt(time), 8)
                if (prop.hits == 0) {
                    score += 15
                    repeat(5) { star ->
                        stars += prop.positionAt(time) + Offset(
                            (star - 2) * 14f,
                            (star % 2) * 18f
                        )
                    }
                    effects.burst(prop.positionAt(time), 24)
                }
            }
        }
        val collected = stars.filter { distanceToSegment(it, previous, ball) < radius + 6f }
        for (star in collected) {
            score += if (combo >= 3) 10 else 5
            effects.burst(star, 12)
        }
        stars.removeAll(collected.toSet())
        if (flight.caught >= 0) {
            if (flight.caught == 1) {
                made++
                combo = if (clean) combo + 1 else 0
                lastBonus = if (clean) minOf(combo + 1, 6) else 1
                lastBanks = banks
                score += lastBonus * 2 + banks.coerceAtMost(4) * 2
                celebration = 1.2f
                effects.burst(hoops[1].center, if (clean) 18 else 8)
                hoops.removeAt(0)
                addHoop()
            }
            catchFrom = ball
            velocity = Offset.Zero
            airborne = false
            settle = 0.22f
            net = 1f
            return
        }
        trailTimer += seconds
        if (trailTimer >= 0.04f) {
            trailTimer = 0f
            trail.addLast(ball)
            if (trail.size > 16) trail.removeFirst()
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