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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Nutrition
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object FruitSliceGame : ArcadeGameDefinition {
    override val title = R.string.arcade_fruitslice
    override val description = R.string.arcade_fruitslice_sub
    override val icon get() = Icons.Outlined.Nutrition
    override val order = 11

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::FruitSliceRun
        )
        val textMeasurer = rememberTextMeasurer()
        val rewardStyle = with(LocalDensity.current) {
            style.scoreTextStyle.copy(
                fontSize = (13f / density / fontScale).sp,
                lineHeight = (16f / density / fontScale).sp
            )
        }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onPress = run::begin,
                onDrag = run::slice,
                onRelease = run::stopInput
            ) {
                drawFruitSliceBackground(style, run.width, run.height)
                val plank = 58f
                repeat((run.width / plank).toInt() + 1) { index ->
                    val x = index * plank
                    pixelRect(
                        color = style.obstacleColor.copy(alpha = if (index % 2 == 0) 0.05f else 0.02f),
                        x = x,
                        y = 0f,
                        width = plank - 2f,
                        height = run.height
                    )
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.18f),
                        x = x,
                        y = 0f,
                        width = 1f,
                        height = run.height
                    )
                    repeat(5) { grain ->
                        val y = (index * 71f + grain * 103f + 39f) % run.height
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.22f),
                            x = x + 10f + grain % 3 * 12f,
                            y = y,
                            width = 1f,
                            height = 17f + grain * 3f
                        )
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.14f),
                            x = x + 13f + grain % 3 * 12f,
                            y = y + 8f,
                            width = 1f,
                            height = 12f
                        )
                    }
                }
                for ((point, kind, life, radius) in run.stains) {
                    val color = fruitColor(style, kind).copy(alpha = minOf(life, 1f) * 0.14f)
                    pixelCircle(color, radius, point)
                    repeat(5) { index ->
                        val angle = (index * 73f + kind * 29f) * PI.toFloat() / 180f
                        pixelCircle(
                            color, 1.5f + index % 3,
                            point + Offset(cos(angle), sin(angle)) * (radius + 4f + index)
                        )
                    }
                }
                for (fruit in run.fruits) {
                    val (x1, y1, _, _, bomb, kind, angle, _, delay) = fruit
                    if (delay > 0f) continue
                    rotate(degrees = angle, pivot = Offset(x = x1, y = y1)) {
                        val x = x1 - 18f
                        val y = y1 - 18f
                        if (bomb) {
                            val dark = style.overlayColor.copy(alpha = 1f)
                            val casing = lerp(dark, style.obstacleColor, 0.22f)
                            drawArcadeSprite(SliceBomb, style.hazardColor, x, y)
                            scale(0.86f, Offset(x1, y1)) {
                                reliefSprite(
                                    sprite = SliceBomb,
                                    color = casing,
                                    shade = dark,
                                    light = lerp(casing, style.obstacleColor, 0.45f),
                                    x = x,
                                    y = y
                                )
                            }
                            drawArcadeSprite(
                                sprite = SliceBombCap,
                                tint = lerp(casing, style.obstacleColor, 0.65f),
                                x = x,
                                y = y - 9f
                            )
                            drawArcadeSprite(
                                sprite = SliceBombFuse,
                                tint = lerp(style.accentColor, style.playerDetailColor, 0.4f),
                                x = x,
                                y = y - 14f
                            )
                            drawArcadeSprite(
                                sprite = SliceBombShine,
                                tint = lerp(casing, style.obstacleColor, 0.65f),
                                x = x,
                                y = y
                            )
                            drawArcadeSprite(SliceBombMark, style.hazardColor, x, y)
                            pixelRect(
                                color = style.hazardColor,
                                x = x + 28f,
                                y = y - 17f,
                                width = 2f,
                                height = 7f
                            )
                            pixelRect(
                                color = style.hazardColor,
                                x = x + 25f,
                                y = y - 14f,
                                width = 8f,
                                height = 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = x + 28f,
                                y = y - 14f,
                                width = 2f,
                                height = 2f
                            )
                        } else {
                            val color = fruitColor(
                                style = style,
                                kind = kind
                            )
                            val sprite = when (kind) {
                                0 -> SliceApple
                                2 -> SlicePear
                                3 -> SliceMelon
                                4 -> SliceBerry
                                5 -> SlicePomegranate
                                else -> SliceOrange
                            }
                            scale(
                                if (kind == 5) SlicePomegranateScale + fruit.flash else 1f,
                                Offset(x1, y1)
                            ) {
                                reliefSprite(
                                    sprite = sprite,
                                    color = color,
                                    shade = lerp(color, style.textColor, 0.4f),
                                    light = style.playerDetailColor,
                                    x = x,
                                    y = y
                                )
                                pixelRect(
                                    style.playerDetailColor.copy(alpha = 0.55f),
                                    x + 11f,
                                    y + 7f,
                                    5f,
                                    2f
                                )
                                drawArcadeSprite(
                                    sprite = when (kind) {
                                        0 -> SliceAppleDetail
                                        2 -> SlicePearDetail
                                        3 -> SliceMelonDetail
                                        4 -> SliceBerryDetail
                                        5 -> SlicePomegranateDetail
                                        else -> SliceOrangeDetail
                                    },
                                    tint = style.playerDetailColor.copy(alpha = 0.65f),
                                    x = x,
                                    y = y
                                )
                            }
                            if (kind != 5) drawArcadeSprite(
                                sprite = if (kind == 4) SliceBerryCrown else SliceLeaf,
                                tint = style.obstacleColor,
                                x = x,
                                y = y - if (kind == 4) 2f else 5f
                            )
                        }
                    }
                    if (kind == 5 && fruit.hits > 0) {
                        val text = textMeasurer.measure("×${fruit.hits}", style = rewardStyle)
                        drawText(
                            textLayoutResult = text,
                            color = style.accentColor,
                            topLeft = Offset(x1 - text.size.width / 2f, y1 - 60f)
                        )
                    }
                }
                for ((x, y, vx, _, life, angle, kind) in run.pieces) {
                    val color =
                        fruitColor(
                            style = style,
                            kind = kind
                        ).copy(alpha = (life / 0.8f).coerceIn(0f, 1f))
                    rotate(degrees = angle + (0.8f - life) * vx, pivot = Offset(x = x, y = y)) {
                        drawArcadeSprite(
                            sprite = SlicedFruit,
                            tint = color,
                            x = x - 17f,
                            y = y - 9f
                        )
                        drawArcadeSprite(
                            sprite = SliceFlesh,
                            tint = style.playerDetailColor.copy(alpha = life * 0.65f),
                            x = x - 17f,
                            y = y - 9f
                        )
                        if (kind == 3 || kind == 4) repeat(4) { index ->
                            pixelRect(
                                color = style.textColor.copy(alpha = life * 0.65f),
                                x = x - 10f + index * 6f,
                                y = y - 5f + index % 2 * 3f,
                                width = 2f,
                                height = 3f
                            )
                        }
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = life),
                            x = x - 6f,
                            y = y - 4f,
                            width = 3f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = life),
                            x = x + 4f,
                            y = y - 4f,
                            width = 3f,
                            height = 3f
                        )
                    }
                }
                for ((point, amount, combo, life) in run.rewards) {
                    val text = textMeasurer.measure(
                        text = if (combo) "×$amount" else "+$amount",
                        style = rewardStyle
                    )
                    drawText(
                        textLayoutResult = text,
                        color = style.accentColor.copy(alpha = minOf(1f, life * 3f)),
                        topLeft = point - Offset(text.size.width / 2f, (0.75f - life) * 28f + 22f)
                    )
                }
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
                for ((start, end, life) in run.trail) {
                    drawLine(
                        color = style.playerColor.copy(alpha = life * 0.15f),
                        start = start,
                        end = end,
                        strokeWidth = 9f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = style.playerColor.copy(alpha = life),
                        start = start,
                        end = end,
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }
                arcadeLives(
                    style = style,
                    remaining = 3 - run.misses,
                    y = run.topInset / 2f
                )
            }
        }
    }
}

private val SliceApple = ArcadeSprite(
    width = 36, height = 36,
    data = "M6 4H12V5H15V8H21V5H24V3H29V5H32V8H34V12H35V21H33V27H30V31H26V34H22V33H18V31H15V34H10V33H6" +
            "V29H3V24H1V15H2V9H4V6H6Z"
)
private val SliceOrange = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 2H24V3H28V5H31V8H33V12H35V24H33V28H30V31H26V33H22V35H13V34H8V32H5V29H3V25H1V15H2V10H5V6H8V4H12Z"
)
private val SlicePear = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 1H21V3H23V9H24V13H27V16H30V20H32V25H31V29H28V32H24V34H13V35H9V33H5V30H3V26H4V21H6V17H10" +
            "V14H12V10H13V5H15V3H16Z"
)
private val SliceMelon = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 1H24V3H29V6H32V10H34V14H35V23H33V28H30V31H25V34H12V33H7V30H4V26H2V22H1V14H3V9H6V5H10V3H12Z"
)
private val SliceBerry = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 4H26V5H30V7H33V11H34V16H32V21H29V25H25V29H21V33H18V35H15V32H11V29H7V25H4V20H2V14H3V9H5V6H9Z"
)
private val SliceAppleDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M6 11H8V9H12V11H10V15H8V19H6Z M28 22H30V26H28V29H25V31H22V29H26V26H28Z"
)
private val SliceOrangeDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M7 10H10V8H13V10H10V14H8V18H6V13H7Z M23 13H25V15H23Z M27 19H29V21H27Z M20 23H22V25H20Z " +
            "M12 26H14V28H12Z M25 27H27V29H25Z"
)
private val SlicePearDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M15 6H17V12H15V16H12V19H10V23H8V20H9V17H12V13H14V9H15Z M25 26H27V29H24V31H20V29H24V28H25Z"
)
private val SliceMelonDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M11 4H14V6H12V10H10V15H9V22H10V27H12V31H9V28H7V23H6V15H8V9H10V6H11Z M19 3H21V9H20V17H19V25" +
            "H20V32H18V25H17V18H18V10H19Z M26 5H28V8H30V13H31V22H29V27H27V30H24V28H26V24H28V20H28V14H27V9" +
            "H26Z"
)
private val SliceBerryDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M8 12H10V15H8Z M17 10H19V13H17Z M26 12H28V15H26Z M12 19H14V22H12Z M22 19H24V22H22Z M17 26H19V29H17Z"
)
private val SliceLeaf = ArcadeSprite(
    width = 36, height = 13,
    data = "M17 4H19V13H17Z M19 5V2H22V0H29V3H26V5H19Z"
)
private val SliceBerryCrown = ArcadeSprite(
    width = 36, height = 12,
    data = "M17 0H20V4H26V3H31V5H27V7H23V10H20V7H17V12H14V8H10V9H6V6H12V4H17Z"
)
private const val SlicePomegranateScale = 1.8f
private val SlicePomegranate = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 1H15V4H17V0H20V4H22V1H25V7H28V9H31V13H33V20H34V26H31V31H27V34H10V32H6V28H3V21H4V14H6V10H10V7H12Z"
)
private val SlicePomegranateDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 14H12V17H9Z M16 11H19V14H16Z M23 14H26V17H23Z M12 21H15V24H12Z M20 19H23V22H20Z M24 26H27V29H24Z M15 28H18V31H15Z"
)
private val SliceBomb = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 1H24V3H28V5H31V8H33V12H35V24H33V28H31V31H28V33H24V35H12V33H8V31H5V28H3V24H1V12H3V8H5V5H8V3H12Z"
)
private val SliceBombCap = ArcadeSprite(
    width = 36, height = 12,
    data = "M13 4H25V12H13Z M16 1H22V4H16Z"
)
private val SliceBombFuse = ArcadeSprite(
    width = 36, height = 12,
    data = "M19 6H22V10H19Z M20 3H24V7H20Z M23 1H29V4H23Z"
)
private val SliceBombShine = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 11H14V13H11V16H9V20H7V15H9Z"
)
private val SliceBombMark = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 9H13V11H15V13H18V15H20V13H22V11H25V10H28V13H26V15H24V17H22V19H24V21H27V23H29V27H25V26H22V24H20V22H18V24H16V26H13V28H9V25H11V23H13V21H15V19H13V17H11V15H9Z"
)
private val SlicedFruit = ArcadeSprite(
    width = 34, height = 18,
    data = "M0 0H34V6H32V10H29V13H25V16H20V18H13V17H8V14H4V10H1V6H0Z"
)
private val SliceFlesh = ArcadeSprite(
    width = 34, height = 18,
    data = "M3 2H31V6H29V9H26V12H22V14H14V14H9V11H6V8H4V5H3Z"
)

private fun fruitColor(
    style: ArcadeStyle,
    kind: Int
) = when (kind) {
    0 -> style.playerColor
    1 -> style.accentColor
    2 -> lerp(style.playerColor, style.accentColor, 0.5f)
    3 -> style.obstacleColor
    5 -> lerp(style.hazardColor, style.accentColor, 0.35f)
    else -> lerp(style.accentColor, style.textColor, 0.35f)
}

private fun DrawScope.drawFruitSliceBackground(style: ArcadeStyle, width: Float, height: Float) {
    val grain = style.obstacleColor.copy(alpha = 0.1f)
    repeat((width / 58f).toInt() + 1) { plank ->
        val x = plank * 58f
        pixelRect(style.playerDetailColor.copy(alpha = 0.1f), x + 1f, 0f, 1f, height)
        repeat((height / 120f).toInt() + 1) { index ->
            val y = index * 120f + (plank * 37 % 83)
            val center = Offset(x + 18f + (plank + index) % 3 * 7f, y + 35f)
            scale(scaleX = 0.65f, scaleY = 1.8f, pivot = center) {
                for (radius in listOf(3f, 7f, 11f)) {
                    pixelCircle(grain, radius, center, style = Stroke(1f))
                }
            }
            pixelRect(grain, x + 6f, y + 5f, 2f, 2f)
            pixelRect(grain, x + 49f, y + 5f, 2f, 2f)
        }
    }
    val color = style.detailColor.copy(alpha = 0.12f)
    for (side in 0..1) {
        val x = if (side == 0) 10f else width - 11f
        val direction = if (side == 0) 1f else -1f
        val bottom = height - 5f
        val stemTop = height * if (side == 0) 0.22f else 0.4f
        pixelRect(color, x, stemTop, 2f, bottom - stemTop)
        var y = bottom - 14f
        while (y > stemTop + 8f) {
            pixelRect(color, x - 1f, y, 4f, 1f)
            drawLine(color, Offset(x, y), Offset(x + 10f * direction, y - 7f), 2f)
            drawLine(
                color,
                Offset(x + 5f * direction, y - 3f),
                Offset(x + 14f * direction, y - 3f),
                2f
            )
            y -= 27f
        }
    }
}

@Serializable
private data class SliceFruit(
    var x: Float, var y: Float, var vx: Float, var vy: Float, val bomb: Boolean,
    val kind: Int, var angle: Float = 0f, val spin: Float = 0f, var delay: Float = 0f,
    var hang: Float = 0f, var hits: Int = 0, var stroke: Int = -1,
    var touching: Boolean = false, var flash: Float = 0f
)

@Serializable
private data class SliceStain(
    val point: ArcadeOffset,
    val kind: Int,
    var life: Float = 7f,
    val radius: Float = 9f
)

@Serializable
private data class SliceReward(
    var point: ArcadeOffset,
    var amount: Int,
    val combo: Boolean,
    var life: Float = 0.75f
)

@Serializable
private data class SlicePiece(
    var x: Float,
    var y: Float,
    val vx: Float,
    var vy: Float,
    var life: Float,
    val angle: Float,
    val kind: Int
)

@Serializable
private data class SliceTrace(
    val start: ArcadeOffset,
    val end: ArcadeOffset,
    var life: Float = 1f
)

@Serializable
private class FruitSliceRun : ArcadeRun() {
    val fruits = mutableListOf<SliceFruit>()
    val pieces = mutableListOf<SlicePiece>()
    val trail = mutableListOf<SliceTrace>()
    val stains = mutableListOf<SliceStain>()
    val rewards = mutableListOf<SliceReward>()

    @Transient
    val effects = ArcadeEffects()
    var misses = 0
    var combo = 0

    @Transient
    private var finger: Offset? = null
    private var comboTime = 0f
    private var timer = 0.6f
    private var elapsed = 0f
    private var waves = 0
    private var criticalIn = 30
    private var pomegranateIn = 30f
    private var stroke = 0
    private val patterns: ArcadeDeque<Int> = ArrayDeque()
    private val gravity get() = (height - topInset) * 1.35f

    override fun reset() {
        fruits.clear()
        pieces.clear()
        trail.clear()
        stains.clear()
        rewards.clear()
        patterns.clear()
        effects.clear()
        misses = 0
        timer = 0.6f
        elapsed = 0f
        waves = 0
        criticalIn = Random.nextInt(25, 45)
        pomegranateIn = 28f + Random.nextFloat() * 14f
        stroke = 0
        comboTime = 0f
        stopInput()
    }

    fun begin(point: Offset) {
        stroke++
        finger = point
        combo = 0
    }

    fun slice(point: Offset) {
        val start = finger ?: return
        val delta = point - start
        val length = delta.x * delta.x + delta.y * delta.y
        if (length < 1f) return
        if (trail.size >= 18) trail.removeAt(0)
        trail += SliceTrace(start = start, end = point)
        finger = point
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            if (fruit.delay > 0f) continue
            val t =
                (((fruit.x - start.x) * delta.x + (fruit.y - start.y) * delta.y) / length).coerceIn(
                    0f,
                    1f
                )
            val radius = 19f * if (fruit.kind == 5) SlicePomegranateScale else 1f
            val crossed =
                hypot(fruit.x - start.x - t * delta.x, fruit.y - start.y - t * delta.y) < radius
            if (fruit.kind == 5) {
                val held = fruit.stroke == stroke && fruit.touching
                fruit.stroke = stroke
                fruit.touching = hypot(fruit.x - point.x, fruit.y - point.y) < radius
                if (crossed && !held) {
                    fruit.hits++
                    fruit.flash = 0.1f
                    addScore(1)
                    effects.burst(Offset(fruit.x, fruit.y), 5)
                    if (stains.size >= 18) stains.removeAt(0)
                    stains += SliceStain(Offset(fruit.x, fruit.y), fruit.kind, radius = 6f)
                }
                continue
            }
            if (crossed) {
                if (fruit.bomb) {
                    phase = ArcadePhase.Over
                    return
                }
                if (comboTime <= 0f) combo = 0
                combo++
                comboTime = 0.14f
                val before = score
                score++
                if (combo >= 3) {
                    score += if (combo == 3) 3 else 1
                    val reward = rewards.lastOrNull { it.combo && it.life > 0.5f }
                    if (reward == null) rewards += SliceReward(
                        Offset(fruit.x, fruit.y),
                        combo,
                        true
                    )
                    else {
                        reward.point = Offset(fruit.x, fruit.y)
                        reward.amount = combo
                        reward.life = 0.75f
                    }
                }
                criticalIn--
                if (criticalIn <= 0) {
                    criticalIn = Random.nextInt(25, 55)
                    score += 10
                    rewards += SliceReward(Offset(fruit.x, fruit.y), 10, false)
                }
                if (score / 100 > before / 100) misses = (misses - 1).coerceAtLeast(0)
                effects.burst(
                    point = Offset(x = fruit.x, y = fruit.y),
                    count = if (combo >= 3) 12 else 7
                )
                if (stains.size >= 18) stains.removeAt(0)
                stains += SliceStain(
                    Offset(fruit.x, fruit.y),
                    fruit.kind,
                    radius = 7f + Random.nextFloat() * 6f
                )
                val normal = Offset(-delta.y, delta.x) / sqrt(length)
                val angle = atan2(delta.y, delta.x) * 180f / PI.toFloat()
                for (sign in -1..1 step 2) {
                    pieces += SlicePiece(
                        x = fruit.x, y = fruit.y,
                        vx = fruit.vx * 0.25f + normal.x * sign * 65f,
                        vy = fruit.vy * 0.2f + normal.y * sign * 65f - 40f,
                        life = 0.8f, angle = angle + if (sign < 0) 0f else 180f, kind = fruit.kind
                    )
                }
                iterator.remove()
            }
        }
    }

    override fun stopInput() {
        finger = null
        combo = 0
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        pomegranateIn -= seconds
        timer -= seconds
        comboTime -= seconds
        if (comboTime <= 0f) combo = 0
        effects.advance(seconds)
        stains.forEach { it.life -= seconds }
        stains.removeAll { it.life <= 0f }
        rewards.forEach { it.life -= seconds }
        rewards.removeAll { it.life <= 0f }
        for (trace in trail) trace.life -= seconds * 5f
        trail.removeAll { it.life <= 0f }
        if (pomegranateIn <= 0f && fruits.isEmpty()) {
            fruits += thrownFruit(
                x = width * (0.35f + Random.nextFloat() * 0.3f),
                landing = width / 2f,
                rise = (height - topInset) * 0.72f,
                delay = 0f,
                bomb = false
            ).copy(kind = 5, spin = 15f, hang = 3.2f)
            pomegranateIn = 35f + Random.nextFloat() * 20f
        }
        if (timer <= 0f && pomegranateIn > 0f && fruits.none { it.kind == 5 }) throwWave()
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            fruit.flash = (fruit.flash - seconds * 0.8f).coerceAtLeast(0f)
            if (fruit.delay > 0f) {
                fruit.delay -= seconds
                continue
            }
            if (fruit.kind == 5) {
                timer = 0.8f
                if (fruit.vy >= -35f && fruit.hang > 0f) {
                    fruit.hang -= seconds
                    fruit.vy = 0f
                    fruit.x += (width / 2f - fruit.x) * minOf(1f, seconds * 2f)
                    fruit.angle += 15f * seconds
                    if (fruit.hang <= 0f) {
                        val bonus = fruit.hits / 5 * 5
                        addScore(bonus)
                        if (fruit.hits > 0) rewards += SliceReward(
                            Offset(fruit.x, fruit.y),
                            fruit.hits,
                            true
                        )
                        effects.burst(Offset(fruit.x, fruit.y), 22)
                        for (side in -1..1 step 2) pieces += SlicePiece(
                            x = fruit.x, y = fruit.y, vx = side * 80f, vy = -70f,
                            life = 0.8f, angle = if (side < 0) 0f else 180f, kind = 5
                        )
                        iterator.remove()
                    }
                    continue
                }
            }
            fruit.angle = arcadeAngle(fruit.angle + fruit.spin * seconds)
            fruit.vy += gravity * seconds
            fruit.y += fruit.vy * seconds
            fruit.x += fruit.vx * seconds
            if (fruit.y > height + 40f && fruit.vy > 0f) {
                if (!fruit.bomb && fruit.kind != 5) misses++
                iterator.remove()
            }
        }
        for (piece in pieces) {
            piece.life -= seconds
            piece.vy += gravity * seconds
            piece.x += piece.vx * seconds
            piece.y += piece.vy * seconds
        }
        pieces.removeAll { it.life <= 0f }
        if (misses >= 3) phase = ArcadePhase.Over
    }

    private fun addScore(points: Int) {
        val before = score
        score += points
        if (score / 100 > before / 100) misses = (misses - 1).coerceAtLeast(0)
    }

    private fun throwWave() {
        val pace = 1f - exp(-elapsed / 75f)
        if (patterns.isEmpty()) patterns.addAll((0..if (elapsed < 35f) 3 else 4).shuffled())
        val pattern = when (waves++) {
            0, 1 -> 0
            2 -> 1
            else -> patterns.removeFirst()
        }
        val count = when (pattern) {
            0 -> if (pace < 0.4f) 1 else Random.nextInt(1, 3)
            1 -> Random.nextInt(3, if (pace < 0.5f) 5 else 6)
            2 -> Random.nextInt(2, 5)
            3 -> Random.nextInt(3, 6)
            else -> Random.nextInt(4, 7)
        }
        val center = width * (0.24f + Random.nextFloat() * 0.52f)
        val rise = (height - topInset) * (0.58f + Random.nextFloat() * 0.2f)
        for (index in 0 until count) {
            for (attempt in 0 until 16) {
                val x = when (pattern) {
                    1 -> center + (index - (count - 1) / 2f) * minOf(29f, width / 14f)
                    2 -> width * if (index % 2 == 0) 0.15f else 0.85f
                    3 -> center + Random.nextFloat() * 16f - 8f
                    else -> width * (0.12f + Random.nextFloat() * 0.76f)
                }.coerceIn(22f, width - 22f)
                val landing = when (pattern) {
                    1 -> x + (width / 2f - center) * 0.65f
                    2 -> width - x
                    3 -> width * (0.18f + index.toFloat() / maxOf(1, count - 1) * 0.64f)
                    else -> width * (0.15f + Random.nextFloat() * 0.7f)
                }.coerceIn(22f, width - 22f)
                val lift = rise * if (pattern == 1) 0.97f + Random.nextFloat() * 0.06f
                else 0.88f + Random.nextFloat() * 0.22f
                val delay = index * if (pattern == 4) 0.14f else if (pattern == 1) 0.025f else 0.07f
                val fruit = thrownFruit(x, landing, lift, delay, false)
                if (fruits.none { it.bomb && conflicts(fruit, it) }) {
                    fruits += fruit
                    break
                }
            }
        }
        if (elapsed > 10f && Random.nextFloat() < 0.18f + pace * 0.28f && fruits.count { it.bomb } < 2) {
            for (attempt in 0 until 24) {
                val x = width * (0.12f + Random.nextFloat() * 0.76f)
                val bomb = thrownFruit(
                    x = x,
                    landing = width * (0.18f + Random.nextFloat() * 0.64f),
                    rise = rise * (0.8f + Random.nextFloat() * 0.15f),
                    delay = Random.nextFloat() * 0.25f,
                    bomb = true
                )
                if (fruits.none { !it.bomb && conflicts(bomb, it) }) {
                    fruits += bomb
                    break
                }
            }
        }
        timer += 2.35f - pace * 0.95f + Random.nextFloat() * 0.5f + if (pattern == 4) 0.35f else 0f
    }

    private fun thrownFruit(
        x: Float,
        landing: Float,
        rise: Float,
        delay: Float,
        bomb: Boolean
    ): SliceFruit {
        val speed = sqrt(2f * gravity * rise)
        val airtime = 2f * speed / gravity
        return SliceFruit(
            x = x, y = height + 20f, vx = (landing - x) / airtime, vy = -speed,
            bomb = bomb, kind = Random.nextInt(5), angle = Random.nextFloat() * 360f,
            spin = (Random.nextFloat() - 0.5f) * 180f, delay = delay
        )
    }

    private fun conflicts(first: SliceFruit, second: SliceFruit): Boolean {
        for (sample in 0..75) {
            val time = sample / 30f
            fun position(fruit: SliceFruit): Offset {
                val elapsed = (time - fruit.delay.coerceAtLeast(0f)).coerceAtLeast(0f)
                return Offset(
                    fruit.x + fruit.vx * elapsed,
                    fruit.y + fruit.vy * elapsed + gravity * elapsed * elapsed / 2f
                )
            }

            val a = position(first)
            val b = position(second)
            if (a.y in topInset..height && b.y in topInset..height && (a - b).getDistance() < 45f) return true
        }
        return false
    }

    override fun onResize(previous: Size) {
        for (fruit in fruits) {
            fruit.x *= width / previous.width
            fruit.vx *= width / previous.width
            fruit.y *= height / previous.height
            fruit.vy *= sqrt(height / previous.height)
        }
        pieces.clear()
        trail.clear()
        stains.clear()
        rewards.clear()
        effects.clear()
        stopInput()
    }
}