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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Nutrition
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.hypot
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
                for ((x1, y1, _, _, bomb, kind, angle) in run.fruits) {
                    rotate(degrees = angle, pivot = Offset(x = x1, y = y1)) {
                        val x = x1 - 18f
                        val y = y1 - 18f
                        if (bomb) {
                            drawArcadeSprite(
                                sprite = SliceBomb,
                                tint = style.hazardColor,
                                x = x,
                                y = y
                            )
                            drawArcadeSprite(
                                sprite = SliceBombFuse,
                                tint = style.obstacleColor,
                                x = x,
                                y = y - 7f
                            )
                            drawArcadeSprite(
                                sprite = SliceBombShine,
                                tint = style.playerDetailColor.copy(alpha = 0.75f),
                                x = x,
                                y = y
                            )
                            drawLine(
                                color = style.playerDetailColor,
                                start = Offset(x = x + 13f, y = y + 16f),
                                end = Offset(x = x + 23f, y = y + 26f),
                                strokeWidth = 3f,
                                cap = StrokeCap.Square
                            )
                            drawLine(
                                color = style.playerDetailColor,
                                start = Offset(x = x + 23f, y = y + 16f),
                                end = Offset(x = x + 13f, y = y + 26f),
                                strokeWidth = 3f,
                                cap = StrokeCap.Square
                            )
                            pixelRect(
                                color = style.hazardColor,
                                x = x + 29f,
                                y = y - 8f,
                                width = 2f,
                                height = 6f
                            )
                            pixelRect(
                                color = style.hazardColor,
                                x = x + 27f,
                                y = y - 6f,
                                width = 6f,
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
                                else -> SliceOrange
                            }
                            drawArcadeSprite(
                                sprite = sprite,
                                tint = color,
                                x = x,
                                y = y
                            )
                            drawArcadeSprite(
                                sprite = when (kind) {
                                    0 -> SliceAppleDetail
                                    2 -> SlicePearDetail
                                    3 -> SliceMelonDetail
                                    4 -> SliceBerryDetail
                                    else -> SliceOrangeDetail
                                },
                                tint = style.playerDetailColor.copy(alpha = 0.65f),
                                x = x,
                                y = y
                            )
                            drawArcadeSprite(
                                sprite = if (kind == 4) SliceBerryCrown else SliceLeaf,
                                tint = style.obstacleColor,
                                x = x,
                                y = y - if (kind == 4) 2f else 5f
                            )
                        }
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
private val SliceBomb = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 4H24V6H28V8H30V12H32V16H34V24H32V28H28V32H24V34H12V32H8V28H4V24H2V16H4V12H6V8H8V6H12Z"
)
private val SliceBombFuse = ArcadeSprite(
    width = 36, height = 12,
    data = "M16 7H24V12H16Z M21 7V3H23V1H29V3H24V5H23V7H21Z"
)
private val SliceBombShine = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 11H14V13H11V16H9V20H7V15H9Z"
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
    else -> lerp(style.accentColor, style.textColor, 0.35f)
}

private fun DrawScope.drawFruitSliceBackground(style: ArcadeStyle, width: Float, height: Float) {
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
    val kind: Int, var angle: Float = 0f, val spin: Float = 0f
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

    @Transient
    val effects = ArcadeEffects()
    var misses = 0
    var combo = 0

    @Transient
    private var finger: Offset? = null
    private var comboTime = 0f
    private var timer = 0.6f
    private var elapsed = 0f
    private val gravity get() = (height - topInset) * 1.65f

    override fun reset() {
        fruits.clear()
        pieces.clear()
        trail.clear()
        effects.clear()
        misses = 0
        timer = 0.6f
        elapsed = 0f
        comboTime = 0f
        stopInput()
    }

    fun begin(point: Offset) {
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
            val t =
                (((fruit.x - start.x) * delta.x + (fruit.y - start.y) * delta.y) / length).coerceIn(
                    0f,
                    1f
                )
            if (hypot(fruit.x - start.x - t * delta.x, fruit.y - start.y - t * delta.y) < 19f) {
                if (fruit.bomb) {
                    phase = ArcadePhase.Over
                    return
                }
                if (comboTime <= 0f) combo = 0
                combo++
                comboTime = 0.3f
                score += minOf(combo, 5)
                effects.burst(
                    point = Offset(x = fruit.x, y = fruit.y),
                    count = 6
                )
                for (sign in -1..1 step 2) {
                    pieces += SlicePiece(
                        x = fruit.x, y = fruit.y, vx = sign * 65f, vy = -65f, life = 0.8f,
                        angle = if (sign < 0) 90f else 270f, kind = fruit.kind
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
        timer -= seconds
        comboTime -= seconds
        if (comboTime <= 0f) combo = 0
        effects.advance(seconds)
        for (trace in trail) trace.life -= seconds * 5f
        trail.removeAll { it.life <= 0f }
        if (timer <= 0f) {
            val count = Random.nextInt(1, if (elapsed < 12f) 4 else 5)
            val bomb = if (elapsed > 5f && Random.nextFloat() < 0.4f) Random.nextInt(count) else -1
            repeat(count) { index ->
                val x = 24f + Random.nextFloat() * (width - 48f)
                val landing = 24f + Random.nextFloat() * (width - 48f)
                val rise = (height - topInset) * (0.55f + Random.nextFloat() * 0.4f)
                val velocity = sqrt(2f * gravity * rise)
                val airtime = 2f * velocity / gravity
                fruits += SliceFruit(
                    x = x, y = height + 20f, vx = (landing - x) / airtime,
                    vy = -velocity, bomb = index == bomb, kind = Random.nextInt(5),
                    angle = Random.nextFloat() * 360f, spin = (Random.nextFloat() - 0.5f) * 180f
                )
            }
            timer += maxOf(0.9f, 1.7f - elapsed * 0.005f) * (0.8f + Random.nextFloat() * 0.4f)
        }
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            fruit.angle = arcadeAngle(fruit.angle + fruit.spin * seconds)
            fruit.vy += gravity * seconds
            fruit.y += fruit.vy * seconds
            fruit.x += fruit.vx * seconds
            if (fruit.y > height + 40f && fruit.vy > 0f) {
                if (!fruit.bomb) misses++
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

    override fun onResize(previous: Size) {
        for (fruit in fruits) {
            fruit.x *= width / previous.width
            fruit.vx *= width / previous.width
            fruit.y *= height / previous.height
            fruit.vy *= sqrt(height / previous.height)
        }
        pieces.clear()
        trail.clear()
        effects.clear()
        stopInput()
    }
}