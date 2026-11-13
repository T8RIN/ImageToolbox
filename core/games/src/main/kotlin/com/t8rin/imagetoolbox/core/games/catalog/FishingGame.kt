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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Phishing
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object FishingGame : ArcadeGameDefinition {
    override val title = R.string.arcade_fishing
    override val description = R.string.arcade_fishing_sub
    override val icon get() = Icons.Outlined.Phishing
    override val order = 18

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::FishingRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.cast() }
            ) {
                drawFishingDepths(style, run.width, run.height, run.waterline)
                val water = run.waterline
                pixelRect(
                    color = style.playerColor.copy(alpha = 0.06f),
                    x = 0f,
                    y = water,
                    width = run.width,
                    height = run.height - water
                )
                var x = -20f
                while (x < run.width) {
                    val y = water + sin(x * 0.06f + run.time * 2f) * 2f
                    drawLine(
                        color = style.playerColor.copy(alpha = 0.4f),
                        start = Offset(x = x, y = y),
                        end = Offset(x = x + 12f, y = y + 1f),
                        strokeWidth = 1f
                    )
                    x += 20f
                }
                repeat(14) { index ->
                    val x = (index * 83f + 17f) % run.width
                    val depth = run.height - water - 12f
                    val y = water + depth - (index * 41f + run.time * 9f) % depth
                    pixelCircle(
                        color = style.playerColor.copy(alpha = 0.15f),
                        radius = 1.5f + index % 2,
                        center = Offset(x = x, y = y),
                        style = Stroke(width = 0.8f)
                    )
                }
                val bottom = run.height - 8f
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.3f),
                    x = 0f,
                    y = bottom,
                    width = run.width,
                    height = 1f
                )
                repeat(7) { index ->
                    val x = (index + 0.4f) * run.width / 7f
                    val bend = sin(run.time * 1.4f + index) * 3f
                    drawLine(
                        color = style.obstacleColor.copy(alpha = 0.4f),
                        start = Offset(x = x, y = bottom),
                        end = Offset(x = x + bend, y = bottom - 15f - index % 3 * 5f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = style.obstacleColor.copy(alpha = 0.4f),
                        start = Offset(x = x, y = bottom - 5f),
                        end = Offset(x = x - 4f + bend, y = bottom - 13f),
                        strokeWidth = 1.5f
                    )
                }
                fun fish(
                    point: Offset,
                    kind: Int,
                    direction: Int
                ) {
                    val color = when (kind) {
                        1 -> style.obstacleColor
                        2 -> style.playerColor
                        else -> style.accentColor
                    }
                    translate(left = point.x, top = point.y) {
                        scale(
                            scaleX = direction * (1.1f - kind * 0.13f),
                            scaleY = 1.1f - kind * 0.13f,
                            pivot = Offset.Zero
                        ) {
                            reliefSprite(
                                sprite = when (kind) {
                                    1 -> FishingPerch
                                    2 -> FishingMinnow
                                    else -> ArcadeFish
                                },
                                color = color,
                                shade = lerp(color, style.overlayColor.copy(alpha = 1f), 0.25f),
                                light = lerp(color, style.textColor, 0.2f),
                                x = -20f,
                                y = -11f
                            )
                            if (kind == 4) {
                                for (spot in -1..1) pixelRect(
                                    style.playerDetailColor,
                                    spot * 9f - 2f, -7f, 4f, 11f
                                )
                                pixelRect(style.accentColor, -7f, -18f, 14f, 3f)
                            }
                            pixelRect(
                                color = style.playerDetailColor,
                                x = 10f,
                                y = -5f,
                                width = 3f,
                                height = 3f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.4f),
                                x = 5f,
                                y = -5f,
                                width = 2f,
                                height = 10f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.2f),
                                x = -6f,
                                y = 4f,
                                width = 15f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.35f),
                                x = -4f,
                                y = -1f,
                                width = 5f,
                                height = 4f
                            )
                            pixelRect(
                                color = color,
                                x = -1f,
                                y = 0f,
                                width = 4f,
                                height = 3f
                            )
                            repeat(kind) { index ->
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = -8f + index * 4f,
                                    y = -1f,
                                    width = 2f,
                                    height = 2f
                                )
                            }
                        }
                    }
                }
                for ((x, row, kind, direction) in run.fish) fish(
                    point = Offset(x = x, y = run.fishY(row)),
                    kind = kind,
                    direction = direction
                )
                val point = run.hook
                fishingLine(
                    start = run.anchor,
                    end = point,
                    color = style.textColor.copy(alpha = 0.45f),
                    width = 1f
                )
                pixelRect(
                    color = style.textColor,
                    x = point.x - 1f,
                    y = point.y - 3f,
                    width = 2f,
                    height = 10f
                )
                pixelRect(
                    color = style.textColor,
                    x = point.x - 5f,
                    y = point.y + 5f,
                    width = 5f,
                    height = 2f
                )
                pixelRect(
                    color = style.textColor,
                    x = point.x - 6f,
                    y = point.y + 1f,
                    width = 2f,
                    height = 5f
                )
                if (run.caught > 0) fish(
                    point = point + Offset(x = 0f, y = 8f),
                    kind = run.caught,
                    direction = 1
                )
                repeat(5) { index ->
                    pixelRect(
                        color = if (index < run.streak % 5) style.accentColor else style.detailColor,
                        x = run.anchor.x - 20f + index * 5f,
                        y = water + 8f,
                        width = 3f,
                        height = 3f
                    )
                }
                val boatX = run.anchor.x - 18f
                val boatY = water - 9f + sin(run.time * 2f)
                pixelRect(
                    color = style.obstacleColor,
                    x = boatX - 22f,
                    y = boatY,
                    width = 44f,
                    height = 5f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = boatX - 17f,
                    y = boatY + 5f,
                    width = 34f,
                    height = 5f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = boatX - 15f,
                    y = boatY + 1f,
                    width = 26f,
                    height = 2f
                )
                pixelRect(
                    color = style.accentColor,
                    x = boatX - 4f,
                    y = boatY - 26f,
                    width = 9f,
                    height = 9f
                )
                pixelRect(
                    color = style.accentColor,
                    x = boatX + 5f,
                    y = boatY - 23f,
                    width = 3f,
                    height = 4f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = boatX + 3f,
                    y = boatY - 24f,
                    width = 2f,
                    height = 2f
                )
                drawArcadeSprite(
                    sprite = FishingCap,
                    tint = style.playerColor,
                    x = boatX - 9f,
                    y = boatY - 32f
                )
                pixelRect(
                    color = style.playerDetailColor.copy(alpha = 0.4f),
                    x = boatX - 6f,
                    y = boatY - 27f,
                    width = 15f,
                    height = 2f
                )
                drawArcadeSprite(
                    sprite = FishingCoat,
                    tint = style.playerColor,
                    x = boatX - 8f,
                    y = boatY - 18f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = boatX - 5f,
                    y = boatY - 5f,
                    width = 10f,
                    height = 4f
                )
                pixelRect(
                    color = style.accentColor,
                    x = boatX + 7f,
                    y = boatY - 12f,
                    width = 4f,
                    height = 3f
                )
                fishingLine(
                    start = Offset(x = boatX + 9f, y = boatY - 10f),
                    end = run.anchor,
                    color = style.obstacleColor,
                    width = 2f
                )
                pixelRect(
                    color = style.accentColor,
                    x = boatX + 6f,
                    y = boatY - 11f,
                    width = 3f,
                    height = 7f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = boatX + 4f,
                    y = boatY - 8f,
                    width = 5f,
                    height = 4f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = boatX + 5f,
                    y = boatY - 7f,
                    width = 2f,
                    height = 2f
                )
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
                arcadeLives(
                    style = style,
                    remaining = 3 - run.misses,
                    y = run.topInset / 2f
                )
            }
        }
    }
}

private val ArcadeFish = ArcadeSprite(
    width = 40, height = 22,
    data = "M0 4H4V6H8V9H11V6H15V3H20V0H26V3H31V5H35V8H38V10H40V14H37V17H32V19H26V22H20V19H15V16H11V13H8V16H4V18H0Z"
)
private val FishingCap = ArcadeSprite(
    width = 18, height = 10,
    data = "M3 0H13V2H15V6H18V9H0V6H2V2H3Z"
)
private val FishingCoat = ArcadeSprite(
    width = 18, height = 17,
    data = "M4 0H11V3H14V6H18V10H12V8H10V13H16V17H3V14H0V5H2V2H4Z"
)

private val FishingPerch = ArcadeSprite(
    width = 40,
    height = 22,
    data = "M0 3H3V6H7V9H10V7H13V4H17V0H19V3H22V1H24V4H28V6H33V8H37V10H40V14H36V17H29V19H23V22H19V19H14V16H10V13H7V16H3V19H0Z"
)

private val FishingMinnow = ArcadeSprite(
    width = 40,
    height = 22,
    data = "M0 5H3V8H8V10H13V8H21V5H25V8H31V9H36V11H40V13H35V15H29V17H23V20H19V17H13V15H8V13H5V16H1V18H0Z"
)

private fun DrawScope.fishingLine(
    start: Offset,
    end: Offset,
    color: Color,
    width: Float
) {
    val distance = end - start
    val steps = (maxOf(abs(distance.x), abs(distance.y)) / 3f).toInt().coerceAtLeast(1)
    val path = Path().apply {
        moveTo(start.x, start.y)
        var previous = start
        for (step in 1..steps) {
            val point = start + distance * (step.toFloat() / steps)
            lineTo(point.x, previous.y)
            lineTo(point.x, point.y)
            previous = point
        }
    }
    drawPath(path = path, color = color, style = Stroke(width = width))
}

private fun DrawScope.drawFishingDepths(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    water: Float
) {
    val ink = style.detailColor.copy(alpha = 0.17f)
    val columns = ceil(width / 113f).toInt()
    for (column in 0 until columns) {
        val x = column * 113f - 13f
        val depth = 22f + column % 3 * 11f
        val ridge = Path().apply {
            moveTo(x - 9f, height)
            lineTo(x + 2f, height - depth * 0.6f)
            lineTo(x + 15f, height - depth)
            lineTo(x + 31f, height - depth * 0.9f)
            lineTo(x + 47f, height - depth * 0.35f)
            lineTo(x + 76f, height - depth * 0.6f)
            lineTo(x + 103f, height)
            close()
        }
        drawPath(ridge, ink.copy(alpha = 0.08f))
        drawPath(ridge, ink, style = Stroke(0.7f))
        for (weed in 0..2) {
            val base = Offset(x + 55f + weed * 11f, height - 3f)
            val stem = Path().apply {
                moveTo(base.x, base.y)
                cubicTo(
                    base.x - 11f,
                    base.y - 13f,
                    base.x + 9f,
                    base.y - 27f,
                    base.x - 3f,
                    base.y - 39f - weed * 5f
                )
            }
            drawPath(stem, ink.copy(alpha = 0.15f), style = Stroke(1.3f))
        }
        val far = Path().apply {
            moveTo(x, water - 3f)
            lineTo(x + 18f, water - 12f)
            lineTo(x + 39f, water - 7f)
            lineTo(x + 55f, water - 17f)
            lineTo(x + 96f, water - 3f)
            close()
        }
        drawPath(far, ink.copy(alpha = 0.1f))
        drawLine(
            ink.copy(alpha = 0.07f),
            Offset(x + 11f, water + 34f),
            Offset(x + 65f, water + 34f),
            0.7f
        )
    }
}

@Serializable
private data class SwimmingFish(
    var x: Float,
    val row: Int,
    val kind: Int,
    val direction: Int
)

@Serializable
private class FishingRun : ArcadeRun() {
    val fish = mutableListOf<SwimmingFish>()

    @Transient
    val effects = ArcadeEffects()
    val waterline get() = topInset + 35f
    val anchor get() = Offset(x = width / 2f + 18f, y = waterline - 28f)
    var angle = 0f
    var length = 22f
    var caught = 0
    var misses = 0
    var time = 0f
    var streak = 0
        private set
    private var catches = 0
    val hook get() = anchor + Offset(x = sin(angle), y = cos(angle)) * length
    private var casting = false
    private var returning = false

    fun fishY(row: Int) = waterline + (height - waterline - 18f) * (row + 0.7f) / 4.2f

    override fun reset() {
        fish.clear()
        effects.clear()
        angle = 0f
        length = 22f
        caught = 0
        misses = 0
        time = 0f
        streak = 0
        catches = 0
        casting = false
        returning = false
        repeat(4) { row ->
            fish += SwimmingFish(
                x = Random.nextFloat() * (width - 60f) + 30f, row = row,
                kind = 1 + row % 3, direction = if (row % 2 == 0) 1 else -1
            )
        }
    }

    fun cast() {
        if (casting) return
        casting = true
        returning = false
        caught = 0
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        for (item in fish) {
            item.x += item.direction * (22f + item.kind * 12f + minOf(
                catches * 0.75f,
                55f
            )) * seconds
            if (item.x > width + 30f) item.x = -30f
            if (item.x < -30f) item.x = width + 30f
        }
        if (!casting) {
            angle = sin(time * 1.25f) * 0.95f
            return
        }
        val speed = maxOf(240f, (height - waterline) * 1.7f)
        if (returning) {
            length -= speed * 1.2f * seconds
            if (length <= 22f) {
                length = 22f
                casting = false
                returning = false
                if (caught > 0) {
                    catches++
                    streak++
                    score += (if (caught == 4) 12 else caught) * (1 + minOf(streak / 3, 2))
                    if (streak % 5 == 0) misses = (misses - 1).coerceAtLeast(0)
                    effects.burst(
                        point = Offset(x = width / 2f, y = waterline),
                        count = if (streak % 5 == 0) 18 else 9
                    )
                } else {
                    misses++
                    streak = 0
                }
                caught = 0
                if (misses >= 3) phase = ArcadePhase.Over
            }
        } else {
            length += speed * seconds
            val point = hook
            val hit = fish.indexOfFirst {
                val scale = 1.1f - it.kind * 0.13f
                val dx = (it.x - point.x) / (20f * scale)
                val dy = (fishY(it.row) - point.y) / (11f * scale)
                dx * dx + dy * dy <= 1f
            }
            if (hit >= 0) {
                val item = fish[hit]
                caught = item.kind
                returning = true
                effects.burst(
                    point = point,
                    count = 5
                )
                fish[hit] = SwimmingFish(
                    x = if (item.direction > 0) -32f else width + 32f,
                    row = item.row,
                    kind = if (item.row >= 2 && catches >= 5 && Random.nextFloat() < 0.12f) 4 else Random.nextInt(
                        1,
                        4
                    ),
                    direction = item.direction
                )
            } else if (point.x !in 10f..(width - 10f) || point.y >= height - 14f) returning = true
        }
    }

    override fun onResize(previous: Size) {
        fish.forEach { it.x *= width / previous.width }
        length *= height / previous.height
        effects.clear()
    }
}