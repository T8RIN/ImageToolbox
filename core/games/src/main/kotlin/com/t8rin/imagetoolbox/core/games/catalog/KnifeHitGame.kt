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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngleDistance
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val KnifeApple = ArcadeSprite(
    "KnifeApple", 18, 18,
    "M3 3H7V5H11V3H15V5H18V13H15V17H11V16H7V17H3V13H0V5H3Z"
)

@Composable
fun KnifeHitGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::KnifeHitRun
    )
    ArcadeBoard(
        game = ArcadeGame.KnifeHit,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = { run.throwKnife() }
        ) {
            val center = run.center + Offset(
                0f,
                sin(run.impact * PI.toFloat()) * 3f
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
                    val offset = Offset(cos(angle), sin(angle)) * progress * 42f
                    drawArc(
                        color = style.obstacleColor.copy(alpha = 1f - progress),
                        startAngle = index * 90f + 3f,
                        sweepAngle = 84f,
                        useCenter = true,
                        topLeft = center + offset - Offset(radius, radius),
                        size = Size(radius * 2f, radius * 2f)
                    )
                }
            } else {
                drawCircle(
                    color = style.detailColor.copy(alpha = 0.2f),
                    radius = radius + 3f,
                    center = center + Offset(0f, 3f)
                )
                drawCircle(
                    color = style.obstacleColor,
                    radius = radius,
                    center = center
                )
                rotate(run.angle, center) {
                    drawCircle(
                        color = style.playerDetailColor.copy(alpha = 0.35f),
                        radius = radius - 5f,
                        center = center,
                        style = Stroke(1.5f)
                    )
                    drawCircle(
                        color = style.playerDetailColor.copy(alpha = 0.25f),
                        radius = radius * 0.62f,
                        center = center + Offset(3f, -2f),
                        style = Stroke(2f)
                    )
                    drawCircle(
                        color = style.playerDetailColor.copy(alpha = 0.25f),
                        radius = radius * 0.31f,
                        center = center + Offset(4f, -3f),
                        style = Stroke(1f)
                    )
                    drawCircle(
                        color = style.playerDetailColor.copy(alpha = 0.3f),
                        radius = 3f,
                        center = center + Offset(5f, -4f)
                    )
                    repeat(12) { index ->
                        rotate(index * 30f, center) {
                            drawLine(
                                color = style.playerDetailColor.copy(alpha = 0.5f),
                                start = center + Offset(0f, radius - 8f),
                                end = center + Offset(0f, radius - 2f),
                                strokeWidth = 2f
                            )
                        }
                    }
                }
                for (bonus in run.bonuses) {
                    val angle = (bonus + run.angle) * PI.toFloat() / 180f
                    val point = center + Offset(
                        cos(angle),
                        sin(angle)
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
                    rotate(knife + run.angle - 90f, center) {
                        drawKnife(
                            style = style,
                            x = center.x,
                            y = center.y + radius - 5f
                        )
                    }
                }
                val y = run.launchY + (run.center.y + radius - 5f - run.launchY) * run.flight
                if (run.flight > 0f) {
                    pixelRect(
                        color = style.accentColor.copy(alpha = 0.2f),
                        x = center.x - 1f,
                        y = y + 29f,
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
                val color =
                    if (index < run.remaining) style.playerColor else style.detailColor.copy(alpha = 0.3f)
                pixelRect(
                    color = color,
                    x = x - 1f,
                    y = run.height - 17f,
                    width = 2f,
                    height = 7f
                )
                pixelRect(
                    color = color,
                    x = x - 3f,
                    y = run.height - 10f,
                    width = 6f,
                    height = 2f
                )
                pixelRect(
                    color = color,
                    x = x - 1f,
                    y = run.height - 8f,
                    width = 2f,
                    height = 4f
                )
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

private fun DrawScope.drawKnife(
    style: ArcadeStyle,
    x: Float,
    y: Float
) {
    pixelRect(
        color = style.textColor,
        x = x - 2f,
        y = y + 3f,
        width = 4f,
        height = 17f
    )
    pixelRect(
        color = style.playerDetailColor,
        x = x - 1f,
        y = y,
        width = 2f,
        height = 16f
    )
    pixelRect(
        color = style.accentColor,
        x = x - 5f,
        y = y + 18f,
        width = 10f,
        height = 3f
    )
    pixelRect(
        color = style.playerColor,
        x = x - 3f,
        y = y + 21f,
        width = 6f,
        height = 10f
    )
    pixelRect(
        color = style.playerDetailColor.copy(alpha = 0.5f),
        x = x - 2f,
        y = y + 25f,
        width = 4f,
        height = 1f
    )
}

internal class KnifeHitRun : ArcadeRun() {
    val knives = mutableListOf<Float>()
    val bonuses = mutableListOf<Float>()
    val effects = ArcadeEffects()
    val radius get() = minOf(64f, (height - topInset) * 0.22f, width * 0.2f)
    val center get() = Offset(width / 2f, topInset + (height - topInset) * 0.34f)
    val launchY get() = height - 68f
    var angle = 0f
    var flight = 0f
    var remaining = 6
    var total = 6
    var transition = 0f
    var impact = 0f
    private var flying = false
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
        knives.clear()
        bonuses.clear()
        total = minOf(10, 6 + targets / 3)
        remaining = total
        repeat(minOf(3, targets / 2)) { knives += it * 90f + 30f }
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
        val rhythm = when (targets % 3) {
            0 -> 1f + sin(time * 2f) * 0.25f
            1 -> -0.9f + sin(time * 1.7f) * 0.4f
            else -> cos(time * (0.6f + minOf(targets, 10) * 0.05f)) * 1.3f
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
                point = center + Offset(0f, radius + 12f),
                count = 12
            )
        }
        score++
        remaining--
        impact = 1f
        effects.burst(
            point = center + Offset(0f, radius),
            count = 5
        )
        if (remaining == 0) {
            targets++
            transition = 0.6f
            score += 5
            effects.burst(
                point = center,
                count = 16
            )
        }
        flight = 0f
        flying = false
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "target",
            floatArrayOf(
                angle,
                flight,
                remaining.toFloat(),
                total.toFloat(),
                transition,
                targets.toFloat(),
                time,
                impact
            )
        )
        state.putBoolean("flying", flying)
        state.putFloatArray("knives", knives.toFloatArray())
        state.putFloatArray("bonuses", bonuses.toFloatArray())
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("target")?.let {
            angle = it[0]
            flight = it[1]
            remaining = it[2].toInt()
            total = it[3].toInt()
            transition = it[4]
            targets = it[5].toInt()
            time = it[6]
            impact = it[7]
        }
        flying = state.getBoolean("flying")
        knives.clear()
        state.getFloatArray("knives")?.let { knives.addAll(it.toList()) }
        bonuses.clear()
        state.getFloatArray("bonuses")?.let { bonuses.addAll(it.toList()) }
    }
}