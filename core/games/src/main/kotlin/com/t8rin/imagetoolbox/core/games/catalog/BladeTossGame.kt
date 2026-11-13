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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngleDistance
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.TrackChanges
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object BladeTossGame : ArcadeGameDefinition {
    override val title = R.string.arcade_bladetoss
    override val description = R.string.arcade_bladetoss_sub
    override val icon get() = Icons.Outlined.TrackChanges
    override val order = 12

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BladeTossRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onPress = { run.throwKnife() }
            ) {
                drawBladeBackground(style, run.width, run.height)
                val center = run.center + Offset(
                    x = 0f,
                    y = sin(run.impact * PI.toFloat()) * 3f
                )
                val radius = run.radius
                repeat(20) { index ->
                    val x = (index * 97f + 19f) % run.width
                    val y = (index * 61f + 37f) % run.height
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.18f),
                        x = x,
                        y = y,
                        width = 5f,
                        height = 2f
                    )
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.1f),
                        x = x + 3f,
                        y = y + 3f,
                        width = 9f,
                        height = 1f
                    )
                }
                if (run.transition > 0f) {
                    val progress = 1f - run.transition / 0.6f
                    repeat(4) { index ->
                        val angle = (index * 90f + 45f) * PI.toFloat() / 180f
                        val offset = Offset(x = cos(angle), y = sin(angle)) * progress * 42f
                        drawArc(
                            color = style.obstacleColor.copy(alpha = 1f - progress),
                            startAngle = index * 90f + 3f,
                            sweepAngle = 84f,
                            useCenter = true,
                            topLeft = center + offset - Offset(x = radius, y = radius),
                            size = Size(width = radius * 2f, height = radius * 2f)
                        )
                    }
                } else {
                    pixelCircle(
                        color = style.detailColor.copy(alpha = 0.2f),
                        radius = radius + 3f,
                        center = center + Offset(x = 0f, y = 3f)
                    )
                    pixelCircle(
                        color = style.obstacleColor,
                        radius = radius,
                        center = center
                    )
                    if (run.boss) {
                        pixelCircle(style.accentColor, radius, center, style = Stroke(5f))
                        repeat(8) { index ->
                            val theta = (run.angle + index * 45f) * PI.toFloat() / 180f
                            val point = center + Offset(cos(theta), sin(theta)) * (radius - 2f)
                            pixelRect(style.playerDetailColor, point.x - 1f, point.y - 1f, 2f, 2f)
                        }
                    }
                    rotate(degrees = run.angle, pivot = center) {
                        pixelCircle(
                            color = style.playerDetailColor.copy(alpha = 0.35f),
                            radius = radius - 5f,
                            center = center,
                            style = Stroke(width = 1.5f)
                        )
                        pixelCircle(
                            color = style.playerDetailColor.copy(alpha = 0.25f),
                            radius = radius * 0.62f,
                            center = center + Offset(x = 3f, y = -2f),
                            style = Stroke(width = 2f)
                        )
                        pixelCircle(
                            color = style.playerDetailColor.copy(alpha = 0.25f),
                            radius = radius * 0.31f,
                            center = center + Offset(x = 4f, y = -3f),
                            style = Stroke(width = 1f)
                        )
                        pixelCircle(
                            color = style.playerDetailColor.copy(alpha = 0.3f),
                            radius = 3f,
                            center = center + Offset(x = 5f, y = -4f)
                        )
                        repeat(12) { index ->
                            rotate(degrees = index * 30f, pivot = center) {
                                drawLine(
                                    color = style.playerDetailColor.copy(alpha = 0.5f),
                                    start = center + Offset(x = 0f, y = radius - 8f),
                                    end = center + Offset(x = 0f, y = radius - 2f),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                    for (bonus in run.bonuses) {
                        val angle = (bonus + run.angle) * PI.toFloat() / 180f
                        val point = center + Offset(
                            x = cos(angle),
                            y = sin(angle)
                        ) * (radius + 12f)
                        drawArcadeSprite(
                            sprite = KnifeApple,
                            tint = style.accentColor,
                            x = point.x - 9f,
                            y = point.y - 9f
                        )
                        pixelRect(
                            color = style.playerColor,
                            x = point.x,
                            y = point.y - 12f,
                            width = 2f,
                            height = 7f
                        )
                        pixelRect(
                            color = style.playerColor,
                            x = point.x + 2f,
                            y = point.y - 12f,
                            width = 5f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = point.x - 5f,
                            y = point.y - 3f,
                            width = 2f,
                            height = 4f
                        )
                    }
                    for (knife in run.knives) {
                        rotate(degrees = knife + run.angle - 90f, pivot = center) {
                            drawKnife(
                                style = style,
                                x = center.x,
                                y = center.y + radius - 5f
                            )
                        }
                    }
                    val launchY = run.launchY - 10f
                    val y = launchY + (run.center.y + radius - 5f - launchY) * run.flight
                    if (run.flight > 0f) {
                        pixelRect(
                            color = style.accentColor.copy(alpha = 0.2f),
                            x = center.x - 1f,
                            y = y + 43f,
                            width = 2f,
                            height = 18f
                        )
                    }
                    drawKnife(
                        style = style,
                        x = center.x,
                        y = y
                    )
                }
                repeat(run.total) { index ->
                    val x = run.width / 2f + (index - (run.total - 1) / 2f) * 12f
                    val y = run.height - 25f
                    scale(scale = 0.45f, pivot = Offset(x = x, y = y)) {
                        drawKnife(
                            style = style,
                            x = x,
                            y = y,
                            alpha = if (index < run.remaining) 1f else 0.25f
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
}

private val KnifeApple = ArcadeSprite(
    width = 18, height = 18,
    data = "M3 3H7V5H11V3H15V5H18V13H15V17H11V16H7V17H3V13H0V5H3Z"
)

private val KnifeBlade = ArcadeSprite(
    width = 10,
    height = 20,
    data = "M8 0H9V18H3V15H1V9H3V6H5V3H7V1H8Z"
)

private fun DrawScope.drawKnife(
    style: ArcadeStyle,
    x: Float,
    y: Float,
    alpha: Float = 1f
) {
    scale(scale = 1.4f, pivot = Offset(x = x, y = y)) {
        drawArcadeSprite(
            sprite = KnifeBlade,
            tint = style.obstacleColor.copy(alpha = alpha),
            x = x - 5f,
            y = y
        )
        pixelRect(
            color = style.playerDetailColor.copy(alpha = alpha),
            x = x + 2f,
            y = y + 5f,
            width = 1f,
            height = 12f
        )
        pixelRect(
            color = style.accentColor.copy(alpha = alpha),
            x = x - 3f,
            y = y + 18f,
            width = 8f,
            height = 2f
        )
        pixelRect(
            color = style.playerColor.copy(alpha = alpha),
            x = x - 1f,
            y = y + 20f,
            width = 5f,
            height = 10f
        )
        pixelRect(
            color = style.obstacleColor.copy(alpha = alpha),
            x = x - 1f,
            y = y + 30f,
            width = 5f,
            height = 2f
        )
        for (rivet in listOf(23f, 27f)) {
            pixelRect(
                color = style.playerDetailColor.copy(alpha = alpha),
                x = x + 1f,
                y = y + rivet,
                width = 1f,
                height = 1f
            )
        }
    }
}

private fun DrawScope.drawBladeBackground(style: ArcadeStyle, width: Float, height: Float) {
    val rows = ceil(height / 73f).toInt()
    val columns = ceil(width / 124f).toInt()
    for (row in 0 until rows) {
        val top = row * 73f
        pixelRect(
            style.detailColor.copy(alpha = if (row % 2 == 0) 0.035f else 0.02f),
            0f,
            top,
            width,
            71f
        )
        drawLine(style.detailColor.copy(alpha = 0.17f), Offset(0f, top), Offset(width, top), 0.8f)
        for (column in 0 until columns) {
            val x = column * 124f + 16f + row % 2 * 43f
            val ink = style.detailColor.copy(alpha = 0.18f)
            for (grain in 0..2) {
                val y = top + 16f + grain * 14f
                val path = Path().apply {
                    moveTo(x - 13f, y)
                    cubicTo(x + 4f, y - 3f, x + 20f, y + 3f, x + 39f, y)
                    cubicTo(x + 54f, y - 2f, x + 68f, y + 3f, x + 84f, y)
                }
                drawPath(path, ink.copy(alpha = 0.1f + grain * 0.025f), style = Stroke(0.7f))
            }
            val center = Offset(x + 40f, top + 36f)
            for (radius in listOf(5f, 11f, 18f)) drawOval(
                ink,
                center - Offset(radius, radius * 0.3f),
                Size(radius * 2f, radius * 0.6f),
                style = Stroke(0.7f)
            )
            pixelRect(ink.copy(alpha = 0.23f), x, top + 7f, 2f, 2f)
            pixelRect(ink.copy(alpha = 0.23f), x + 91f, top + 64f, 2f, 2f)
        }
    }
}

@Serializable
private class BladeTossRun : ArcadeRun() {
    val knives = mutableListOf<Float>()
    val bonuses = mutableListOf<Float>()

    @Transient
    val effects = ArcadeEffects()
    val radius get() = minOf(64f, (height - topInset) * 0.22f, width * 0.2f)
    val center get() = Offset(x = width / 2f, y = topInset + (height - topInset) * 0.34f)
    val launchY get() = height - 68f
    var angle = 0f
    var flight = 0f
    var remaining = 6
    var total = 6
    var transition = 0f
    var impact = 0f
    private var flying = false
    val boss get() = targets % 5 == 4
    private var targets = 0
    private var time = 0f

    override fun reset() {
        effects.clear()
        targets = 0
        time = 0f
        angle = 0f
        transition = 0f
        impact = 0f
        nextTarget()
    }

    private fun nextTarget() {
        animateLevel()
        knives.clear()
        bonuses.clear()
        total = when {
            boss && targets < 5 -> 8
            boss && targets < 10 -> 10
            boss -> 12 - targets / 5 % 3
            targets < 4 -> 5 + targets
            targets < 15 -> minOf(10, 6 + targets / 3)
            else -> 8 + (targets / 5 + targets) % 4
        }
        remaining = total
        repeat(
            if (boss) 2 + targets / 5 % 3 else minOf(
                3,
                (targets + 1) / 2
            )
        ) { knives += it * 90f + 30f }
        for (index in 0..minOf(2, targets / 2)) {
            val bonus = arcadeAngle(210f + index * 115f + Random.nextFloat() * 25f)
            if (knives.none {
                    arcadeAngleDistance(
                        first = it,
                        second = bonus
                    ) < 25f
                }) bonuses += bonus
        }
        flight = 0f
        flying = false
    }

    fun throwKnife() {
        if (!flying && transition <= 0f) flying = true
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        impact = (impact - seconds * 7f).coerceAtLeast(0f)
        val speed = 65f + minOf(targets * 6f, 70f)
        val rhythm = if (boss) {
            when (targets / 5 % 4) {
                0 -> 0.72f + sin(time * 1.15f) * 0.92f + sin(time * 2.3f) * 0.28f
                1 -> 0.85f + sin(time * 1.3f) * 1.2f
                2 -> -0.75f + cos(time * 1.8f) * 0.72f
                else -> 0.8f + sin(time * 0.7f) * 0.55f + cos(time * 2.1f) * 0.22f
            }
        } else when (targets % 3) {
            0 -> 1f + sin(time * 2f) * 0.25f
            1 -> -0.9f + sin(time * 1.7f) * 0.4f
            else -> 0.65f + cos(time * (0.6f + minOf(targets, 10) * 0.05f)) * 1.05f
        }
        angle = arcadeAngle(angle + speed * rhythm * seconds)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) nextTarget()
            return
        }
        if (!flying) return
        flight += seconds / 0.16f
        if (flight < 1f) return
        val local = arcadeAngle(90f - angle)
        if (knives.any {
                arcadeAngleDistance(
                    first = it,
                    second = local
                ) < 13f
            }) {
            flight = 1f
            phase = ArcadePhase.Over
            return
        }
        knives += local
        val bonus = bonuses.indexOfFirst {
            arcadeAngleDistance(
                first = it,
                second = local
            ) < 16f
        }
        if (bonus >= 0) {
            bonuses.removeAt(bonus)
            score += 5
            effects.burst(
                point = center + Offset(x = 0f, y = radius + 12f),
                count = 12
            )
        }
        score++
        remaining--
        impact = 1f
        effects.burst(
            point = center + Offset(x = 0f, y = radius),
            count = 5
        )
        if (remaining == 0) {
            score += if (boss) 30 else 5
            targets++
            transition = 0.6f
            effects.burst(
                point = center,
                count = 16
            )
        }
        flight = 0f
        flying = false
    }
}