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
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
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
import kotlin.math.ceil
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
                lightFeedback = true,
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
                            scale(
                                fruit.scale + fruit.flash,
                                Offset(x1, y1)
                            ) {
                                drawSliceFruit(style, kind, Offset(x1, y1))
                            }
                            if (fruit.explosive) {
                                val glow = Color(0xFFFFB657).sliceColor(style.playerColor)
                                val pulse = 1f + sin(fruit.angle * PI.toFloat() / 90f) * 0.08f
                                pixelCircle(
                                    glow.copy(alpha = 0.18f),
                                    fruit.radius * pulse + 5f,
                                    Offset(x1, y1)
                                )
                                pixelRect(glow, x1 + 12f, y1 - 23f, 3f, 9f)
                                pixelRect(glow, x1 + 9f, y1 - 20f, 9f, 3f)
                                pixelRect(style.playerDetailColor, x1 + 12f, y1 - 20f, 3f, 3f)
                            }
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
                for (piece in run.pieces) {
                    val center = Offset(piece.x, piece.y)
                    rotate(piece.angle + (0.8f - piece.life) * piece.spin, center) {
                        rotate(piece.cut, center) {
                            clipRect(
                                left = center.x - 80f,
                                top = center.y - if (piece.side < 0) 80f else 0f,
                                right = center.x + 80f,
                                bottom = center.y + if (piece.side > 0) 80f else 0f
                            ) {
                                rotate(-piece.cut, center) {
                                    scale(
                                        SliceFruitArts[piece.kind].scale * piece.size,
                                        center
                                    ) {
                                        drawSliceFruit(
                                            style = style,
                                            kind = piece.kind,
                                            center = center,
                                            cut = true,
                                            alpha = (piece.life / 0.35f).coerceIn(0f, 1f),
                                            cutAngle = piece.cut,
                                            cutSide = piece.side
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                for ((point, radius, life) in run.blasts) {
                    val progress = 1f - life / 0.35f
                    val color = Color(0xFFFFB657).sliceColor(style.playerColor)
                    repeat(12) { index ->
                        val angle = index * PI.toFloat() / 6f
                        val center = point + Offset(cos(angle), sin(angle)) * radius * progress
                        pixelCircle(
                            color.copy(alpha = 1f - progress),
                            4f * (1f - progress) + 1f,
                            center
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
                val powers = listOf(run.freeze, run.doublePoints, run.frenzy)
                for ((index, remaining) in powers.withIndex()) {
                    if (remaining <= 0f) continue
                    val kind = SliceFreeze + index
                    val color = slicePowerColor(style, kind)
                    val x = 12f + index * 56f
                    val y = run.topInset + 5f
                    pixelRect(color.copy(alpha = 0.16f), x, y, 50f, 18f)
                    pixelRect(color, x, y + 17f, remaining / 6f * 50f, 1f)
                    if (kind != SliceDouble) drawArcadeSprite(
                        if (kind == SliceFreeze) SliceFreezeMark else SliceFrenzyMark,
                        color, x + 4f, y + 3f
                    )
                    val text = textMeasurer.measure(
                        if (kind == SliceDouble) "×2 ${ceil(remaining).toInt()}" else "${
                            ceil(
                                remaining
                            ).toInt()
                        }",
                        style = rewardStyle
                    )
                    drawText(text, color, Offset(x + if (kind == SliceDouble) 4f else 23f, y + 1f))
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
    data = "M8 4H15V5H16V6H17V7H19V6H20V5H21V4H28V5H31V6H32V7H33V8H34V16H33V19H32V21H31V23H30V25H29V26H28V27H27V28H26V29H25V30H24V31H23V32H20V" +
            "31H19V30H17V31H16V32H13V31H12V30H11V29H10V28H9V27H8V26H7V25H6V23H5V21H4V19H3V16H2V8H3V7H4V6H5V5H8Z"
)
private val SliceOrange = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 2H22V3H25V4H27V5H28V6H29V7H30V8H31V9H32V11H33V14H34V22H33V25H32V27H31V28H30V29H29V30H28V31H27V32H25V33H22V34H14V33H11V32H9V31H" +
            "8V30H7V29H6V28H5V27H4V25H3V22H2V14H3V11H4V9H5V8H6V7H7V6H8V5H9V4H11V3H14Z"
)
private val SlicePear = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 3H20V4H21V5H22V10H23V13H24V14H25V15H26V16H28V17H29V18H30V19H31V20H32V22H33V26H32V28H31V29H30V30H28V31H26V32H23V33H13V32H10V31H" +
            "8V30H6V29H5V28H4V26H3V22H4V20H5V19H6V18H7V17H8V16H10V15H11V14H12V13H13V10H14V5H15V4H16Z"
)
private val SliceMelon = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 1H22V2H25V3H26V4H28V5H29V6H30V7H31V9H32V11H33V14H34V22H33V25H32V27H31V29H30V30H29V31H28V32H26V33H25V34H22V35H14V34H11V33H10V32" +
            "H8V31H7V30H6V29H5V27H4V25H3V22H2V14H3V11H4V9H5V7H6V6H7V5H8V4H10V3H11V2H14Z"
)
private val SliceBerry = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 5H24V6H28V7H30V8H32V9H33V15H32V18H31V20H30V21H29V23H28V24H27V25H26V26H25V27H24V28H23V29H22V30H21V31H20V32H19V33H17V32H16V31H15" +
            "V30H14V29H13V28H12V27H11V26H10V25H9V24H8V23H7V21H6V20H5V18H4V15H3V9H4V8H6V7H8V6H12Z"
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
    data = "M15 7H17V13H15Z M10 19H12V23H10Z M9 23H11V26H9Z"
)
private val SliceMelonDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 2H19V34H17Z M10 5H12V8H10V13H9V23H10V28H12V31H10V29H8V24H7V12H8V7H10Z M24 5H26V7H28V12H29V24H28V29H26V31H24V28H26V23H27V13H26V" +
            "8H24Z"
)
private val SliceBerryDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M8 12H10V15H8Z M17 10H19V13H17Z M26 12H28V15H26Z M12 19H14V22H12Z M22 19H24V22H22Z M17 26H19V29H17Z"
)
private val SliceLeaf = ArcadeSprite(
    width = 36, height = 13,
    data = "M18 5H20V2H23V0H28V1H30V2H28V4H25V6H21V7H18Z"
)
private val SliceBerryCrown = ArcadeSprite(
    width = 36, height = 12,
    data = "M16 0H20V5H25V3H30V6H25V9H22V11H18V8H14V11H11V9H6V6H11V3H16Z"
)
private const val SlicePomegranateScale = 1.8f
private const val SliceFreeze = 17
private const val SliceDouble = 18
private const val SliceFrenzy = 19
private val SliceBonusStripes = ArcadeSprite(
    width = 36, height = 36,
    data = "M8 0H11V36H8Z M15 0H18V36H15Z M22 0H25V36H22Z M29 0H32V36H29Z"
)
private val SliceFreezeMark = ArcadeSprite(
    width = 12, height = 12,
    data = "M5 0H7V12H5Z M0 5H12V7H0Z M1 1H3V3H5V5H3V3H1Z M9 1H11V3H9V5H7V3H9Z M1 9H3V7H5V9H3V11H1Z M9 9H7V7H9V9H11V11H9Z"
)
private val SliceFrenzyMark = ArcadeSprite(
    width = 12, height = 12,
    data = "M2 3H4V4H5V7H4V8H1V7H0V4H2Z M9 3H11V4H12V7H11V8H8V7H7V4H9Z M5 7H7V8H8V11H7V12H4V11H3V8H5Z M2 1H3V3H2Z M9 1H10V3H9Z M5 5H6V7H5Z"
)
private val SlicePomegranate = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 1H19V4H21V2H24V7H25V8H27V9H28V10H29V11H30V12H31V14H32V16H33V24H32V26H31V28H30V29H29V30H28V31H27V32H25V33H22V34H14V33H11V32H9V3" +
            "1H8V30H7V29H6V28H5V26H4V24H3V16H4V14H5V12H6V11H7V10H8V9H9V8H11V7H12V2H15V4H17Z"
)
private val SlicePomegranateDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M9 12H12V14H10V19H8V15H9Z M25 16H27V23H25V27H22V25H24V21H25Z M13 27H15V29H13Z M19 11H21V13H19Z"
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

private val SliceBanana = ArcadeSprite(
    width = 36, height = 36,
    data = "M30 8H31V9H32V10H33V12H34V14H35V20H34V22H33V24H32V25H31V26H30V27H28V28H26V29H23V30H13V29H10V28H8V27H6V26H5V25H4V24H3V22H2V20H1V18H" +
            "3V19H5V20H8V21H18V20H21V19H23V18H25V17H26V16H27V15H28V13H29V11H30Z"
)
private val SlicePineapple = ArcadeSprite(
    width = 36, height = 36,
    data = "M15 7H21V8H23V9H25V10H26V12H27V13H28V15H29V27H28V29H27V30H26V32H25V33H23V34H21V35H15V34H13V33H11V32H10V30H9V29H8V27H7V15H8V13H9V12" +
            "H10V10H11V9H13V8H15Z"
)
private val SliceKiwi = ArcadeSprite(
    width = 36, height = 36,
    data = "M15 2H21V3H24V4H26V5H27V6H28V7H29V9H30V11H31V14H32V22H31V25H30V27H29V29H28V30H27V31H26V32H24V33H21V34H15V33H12V32H10V31H9V30H8V29H" +
            "7V27H6V25H5V22H4V14H5V11H6V9H7V7H8V6H9V5H10V4H12V3H15Z"
)
private val SliceLemon = ArcadeSprite(
    width = 36, height = 36,
    data = "M13 6H23V7H26V8H28V9H29V10H30V11H31V12H32V13H33V15H34V16H35V20H34V21H33V23H32V24H31V25H30V26H29V27H28V28H26V29H23V30H13V29H10V28H8" +
            "V27H7V26H6V25H5V24H4V23H3V21H2V20H1V16H2V15H3V13H4V12H5V11H6V10H7V9H8V8H10V7H13Z"
)
private val SliceLime = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 4H22V5H25V6H27V7H28V8H29V9H30V10H31V12H32V14H33V22H32V24H31V26H30V27H29V28H28V29H27V30H25V31H22V32H14V31H11V30H9V29H8V28H7V27H" +
            "6V26H5V24H4V22H3V14H4V12H5V10H6V9H7V8H8V7H9V6H11V5H14Z"
)
private val SlicePeach = ArcadeSprite(
    width = 36, height = 36,
    data = "M8 4H15V5H16V6H17V7H19V6H20V5H21V4H28V5H31V6H32V7H33V8H34V18H33V20H32V22H31V23H30V25H29V26H28V27H27V28H26V29H25V30H24V31H22V32H20V" +
            "33H16V32H14V31H12V30H11V29H10V28H9V27H8V26H7V25H6V23H5V22H4V20H3V18H2V8H3V7H4V6H5V5H8Z"
)
private val SliceCoconut = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 3H22V4H25V5H27V6H28V7H29V8H30V9H31V11H32V13H33V23H32V25H31V27H30V28H29V29H28V30H27V31H25V32H22V33H14V32H11V31H9V30H8V29H7V28H6" +
            "V27H5V25H4V23H3V13H4V11H5V9H6V8H7V7H8V6H9V5H11V4H14Z"
)
private val SliceMango = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 2H22V3H25V4H26V5H27V6H28V7H29V9H30V11H31V16H32V18H31V23H30V25H29V27H28V29H27V30H26V31H24V32H23V33H20V34H14V33H11V32H10V31H9V30" +
            "H8V29H7V27H6V25H5V20H4V18H5V13H6V11H7V9H8V7H9V6H10V5H12V4H13V3H16Z"
)
private val SlicePlum = ArcadeSprite(
    width = 36, height = 36,
    data = "M15 2H21V3H24V4H25V5H26V6H27V7H28V8H29V10H30V12H31V24H30V26H29V28H28V29H27V30H26V31H25V32H24V33H21V34H15V33H12V32H11V31H10V30H9V29" +
            "H8V28H7V26H6V24H5V12H6V10H7V8H8V7H9V6H10V5H11V4H12V3H15Z"
)
private val SlicePassionFruit = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 3H22V4H25V5H26V6H28V7H29V8H30V10H31V11H32V14H33V22H32V25H31V26H30V28H29V29H28V30H26V31H25V32H22V33H14V32H11V31H10V30H8V29H7V28" +
            "H6V26H5V25H4V22H3V14H4V11H5V10H6V8H7V7H8V6H10V5H11V4H14Z"
)
private val SliceBananaDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M30 10H31V11H30Z M31 11H32V12H33V13H32V12H31Z M29 13H30V14H29Z M28 15H29V16H28Z M27 17H28V18H27V19H26V18H27Z M25 19H26V20H25V21H23" +
            "V20H25Z M21 21H23V22H21V23H18V24H8V23H18V22H21Z M5 22H8V23H5Z"
)
private val SlicePineappleDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M0 0H1V1H2V2H1V1H0Z M7 0H9V1H10V2H9V1H7V2H6V1H7Z M15 0H17V1H15Z M23 0H25V1H23V2H22V1H23Z M31 0H33V1H31V2H30V1H31Z M14 1H15V2H14V3H" +
            "13V5H14V6H13V5H11V6H10V5H11V3H13V2H14Z M17 1H18V2H17Z M25 1H26V2H25Z M33 1H34V2H35V3H36V5H35V3H34V2H33Z M2 2H3V3H5V5H3V6H2V7H1V6H2" +
            "V5H3V3H2Z M5 2H6V3H5Z M10 2H11V3H10Z M18 2H19V3H21V5H22V6H21V5H19V6H18V5H19V3H18Z M21 2H22V3H21Z M26 2H27V3H29V5H30V6H29V5H27V6H26" +
            "V5H27V3H26Z M29 2H30V3H29Z M5 5H6V6H7V7H6V6H5Z M34 5H35V6H34Z M9 6H10V7H9V9H7V7H9Z M14 6H15V7H14Z M17 6H18V7H17V9H18V10H19V11H21V1" +
            "3H19V14H18V13H19V11H18V10H17V9H15V7H17Z M22 6H23V7H25V9H23V7H22Z M25 6H26V7H25Z M30 6H31V7H33V9H31V7H30Z M33 6H34V7H33Z M0 7H1V9H0" +
            "Z M1 9H2V10H3V11H5V10H6V11H5V13H3V11H2V10H1Z M6 9H7V10H6Z M9 9H10V10H11V11H13V13H11V14H10V13H11V11H10V10H9Z M14 9H15V10H14V11H13V1" +
            "0H14Z M22 9H23V10H22V11H21V10H22Z M25 9H26V10H27V11H29V13H27V14H26V13H27V11H26V10H25Z M30 9H31V10H30V11H29V10H30Z M33 9H34V10H35V1" +
            "1H34V10H33Z M35 11H36V13H35V14H34V13H35Z M2 13H3V14H2V15H1V17H2V18H1V17H0V15H1V14H2Z M5 13H6V14H5Z M13 13H14V14H13Z M21 13H22V14H2" +
            "1Z M29 13H30V14H31V15H30V14H29Z M6 14H7V15H9V17H10V18H9V17H7V18H6V19H5V18H6V17H7V15H6Z M9 14H10V15H9Z M14 14H15V15H17V17H18V18H17V" +
            "17H15V18H14V19H13V18H14V17H15V15H14Z M17 14H18V15H17Z M22 14H23V15H25V17H26V18H25V17H23V15H22Z M25 14H26V15H25Z M33 14H34V15H33V17" +
            "H34V18H35V19H36V21H35V19H34V18H33V17H31V15H33Z M22 17H23V18H22Z M30 17H31V18H30V19H29V18H30Z M2 18H3V19H2Z M10 18H11V19H13V21H14V2" +
            "2H15V23H17V25H15V26H14V25H15V23H14V22H13V21H11V19H10Z M18 18H19V19H21V21H19V19H18Z M21 18H22V19H21Z M26 18H27V19H29V21H30V22H31V23" +
            "H33V25H34V26H35V27H34V26H33V25H31V26H30V25H31V23H30V22H29V21H27V19H26Z M3 19H5V21H3Z M2 21H3V22H2V23H1V22H2Z M5 21H6V22H7V23H6V22H" +
            "5Z M10 21H11V22H10V23H9V25H7V26H6V25H7V23H9V22H10Z M18 21H19V22H18V23H17V22H18Z M21 21H22V22H23V23H22V22H21Z M26 21H27V22H26V23H25" +
            "V25H23V26H22V25H23V23H25V22H26Z M34 21H35V22H34V23H33V22H34Z M0 23H1V25H0Z M1 25H2V26H1Z M9 25H10V26H9Z M17 25H18V26H17Z M25 25H26" +
            "V26H27V27H29V29H30V30H29V29H27V27H26V26H25Z M2 26H3V27H5V29H6V30H7V31H9V33H7V31H6V30H5V29H3V30H2V31H1V30H2V29H3V27H2Z M5 26H6V27H5" +
            "Z M10 26H11V27H13V29H14V30H13V29H11V27H10Z M13 26H14V27H13Z M18 26H19V27H21V29H22V30H21V29H19V27H18Z M21 26H22V27H21Z M29 26H30V27" +
            "H29Z M35 27H36V29H35Z M10 29H11V30H10Z M18 29H19V30H18V31H17V30H18Z M26 29H27V30H26V31H25V30H26Z M34 29H35V30H34V31H33V30H34Z M9 3" +
            "0H10V31H9Z M14 30H15V31H17V33H18V34H19V35H21V36H19V35H18V34H17V33H15V31H14Z M22 30H23V31H25V33H23V34H22V33H23V31H22Z M30 30H31V31H" +
            "33V33H31V34H30V33H31V31H30Z M0 31H1V33H0Z M1 33H2V34H3V35H5V36H3V35H2V34H1Z M6 33H7V34H6V35H5V34H6Z M9 33H10V34H11V35H10V34H9Z M14" +
            " 33H15V34H14V35H13V36H11V35H13V34H14Z M25 33H26V34H27V35H29V36H27V35H26V34H25Z M33 33H34V34H35V35H34V34H33Z M21 34H22V35H21Z M29 3" +
            "4H30V35H29Z M35 35H36V36H35Z"
)
private val SliceKiwiDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 6H14V9H12Z M23 8H25V11H23Z M7 14H9V17H7Z M17 13H19V16H17Z M27 16H29V19H27Z M11 22H13V25H11Z M21 23H23V26H21Z M17 29H19V31H17Z"
)
private val SlicePeachDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 5H20V10H22V16H21V24H19V29H17V25H19V17H20V11H17Z M8 12H10V17H8Z"
)
private val SliceCoconutDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M8 10H10V16H8Z M6 19H8V25H6Z M12 25H14V31H12Z M26 13H28V20H26Z M24 24H26V29H24Z M14 7H17V10H14Z M20 8H23V11H20Z M17 13H20V16H17Z"
)
private val SliceMangoDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M22 6H25V9H27V14H28V22H26V28H23V31H20V28H22V23H23V15H22Z"
)
private val SlicePlumDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 4H20V12H21V21H20V28H18V23H19V14H17Z M9 11H12V13H10V18H8V14H9Z"
)
private val SlicePassionDetail = ArcadeSprite(
    width = 36, height = 36,
    data = "M10 7H14V9H11V13H9V10H10Z M22 7H26V10H24V9H22Z M7 19H9V23H12V25H9V23H7Z M24 20H27V24H25V27H22V25H24Z M15 28H20V30H15Z"
)
private val SlicePineappleCrown = ArcadeSprite(
    width = 36, height = 20,
    data = "M17 0H19V4H20V12H22V18H14V15H16V8H17Z M25 3H28V6H27V10H25V14H23V19H18V15H21V11H23V7H25Z M8 3H11V7H13V11H15V15H18V19H13V14H11V10H9V" +
            "6H8Z M30 10H33V13H29V16H25V20H19V16H24V14H28V11H30Z M3 10H6V11H8V14H12V16H17V20H11V16H7V13H3Z"
)
private val SliceAppleCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 5H20V11H22V15H23V23H21V27H20V31H16V27H14V23H13V17H14V12H16Z"
)
private val SliceAppleSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 16H16V20H14Z M20 18H22V22H20Z"
)
private val SlicePearCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 7H19V17H22V20H24V25H22V29H15V27H12V22H14V18H16Z"
)
private val SlicePearSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M15 22H17V26H15Z M20 21H22V25H20Z"
)
private val SliceCitrusSegments = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 5H19V31H17Z M5 17H31V19H5Z M8 7H10V9H12V11H14V13H16V15H20V13H22V11H24V9H26V7H28V10H26V12H24V14H22V16H20V20H22V22H24V24H26V26H28V29H26V27H24V25H22V23H20V21H16V23H14V25H12V27H10V29H8V26H10V24H12V22H14V20H16V16H14V14H12V12H10V10H8Z"
)
private val SliceCitrusSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 11H16V14H14Z M21 21H23V24H21Z"
)
private val SliceLimeSegments = ArcadeSprite(
    width = 36, height = 36,
    data = "M17 6H19V30H17Z M5 17H30V19H5Z M15 14H21V22H15Z"
)
private val SliceMelonSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 9H14V13H12Z M22 10H24V14H22Z M8 18H10V22H8Z M19 18H21V22H19Z M13 26H15V29H13Z M25 24H27V27H25Z"
)
private val SliceBerryCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M13 8H22V11H24V15H22V18H20V23H18V28H16V23H14V18H12V14H10V11H13Z"
)
private val SlicePomegranateCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 6H20V17H24V19H29V22H26V21H22V19H20V29H16V21H12V23H7V20H11V18H16Z"
)
private val SlicePomegranateSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M10 11H13V14H10Z M12 16H15V19H12Z M7 17H10V20H7Z M10 24H13V27H10Z M13 28H16V31H13Z M21 10H24V13H21Z M25 14H28V17H25Z M21 15H24V18H21Z M23 23H26V26H23Z M20 28H23V31H20Z M27 26H30V29H27Z"
)
private val SliceBananaSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M26 22H28V23H26Z M10 24H12V25H10Z M18 24H20V25H18Z"
)
private val SlicePineappleCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 6H20V32H16Z M9 10H10V26H9Z M25 10H26V26H25Z M12 8H13V30H12Z M22 8H23V30H22Z"
)
private val SliceKiwiCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 10H21V13H23V22H21V26H16V23H14V14H16Z"
)
private val SliceKiwiSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M13 7H15V10H13Z M21 7H23V10H21Z M8 12H10V15H8Z M26 12H28V15H26Z M7 19H10V21H7Z M26 20H29V22H26Z M10 26H12V29H10Z M23 26H25V29H23Z M16 29H18V32H16Z"
)
private val SlicePeachStone = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 10H21V12H24V16H25V23H22V27H18V28H14V25H12V20H13V14H16Z"
)
private val SliceCoconutCore = ArcadeSprite(
    width = 36, height = 36,
    data = "M14 9H23V12H26V17H27V23H24V27H19V28H13V25H10V21H9V16H11V12H14Z"
)
private val SliceMangoStone = ArcadeSprite(
    width = 36, height = 36,
    data = "M23 8H26V13H24V18H21V22H17V25H12V27H8V24H12V21H16V17H20V12H23Z"
)
private val SlicePlumStone = ArcadeSprite(
    width = 36, height = 36,
    data = "M16 13H21V15H23V22H21V26H17V24H14V18H16Z"
)
private val SlicePassionSeeds = ArcadeSprite(
    width = 36, height = 36,
    data = "M12 9H14V12H12Z M18 8H20V11H18Z M24 10H26V13H24Z M8 15H10V18H8Z M15 14H17V17H15Z M21 15H23V18H21Z M27 16H29V19H27Z M11 21H13V24H11Z M17 20H19V23H17Z M24 22H26V25H24Z M14 27H16V30H14Z M20 27H22V30H20Z"
)

private data class SliceFruitArt(
    val body: ArcadeSprite,
    val skin: Color,
    val flesh: Color,
    val detail: ArcadeSprite,
    val section: ArcadeSprite? = null,
    val seeds: ArcadeSprite? = null,
    val leaf: ArcadeSprite? = SliceLeaf,
    val rind: Int = 3,
    val pivot: Offset = Offset(18f, 18f),
    val scale: Float = 1f
) {
    val surface by lazy { sliceInset(body.path, 1) }
    val rim by lazy { sliceInset(body.path, rind - 1) }
    val meat by lazy { sliceInset(body.path, rind) }
    val shade by lazy {
        Path.combine(
            PathOperation.Difference,
            surface,
            Path().apply { addPath(surface, Offset(-2f, -1f)) })
    }
    val shine by lazy {
        Path.combine(
            PathOperation.Difference,
            surface,
            Path().apply { addPath(surface, Offset(1f, 1f)) })
    }
}

private fun sliceInset(path: Path, amount: Int): Path {
    var result = path
    val inset = amount.toFloat()
    for (x in -1..1) for (y in -1..1) {
        if (x == 0 && y == 0) continue
        val shifted = Path().apply { addPath(path, Offset(x * inset, y * inset)) }
        result = Path.combine(PathOperation.Intersect, result, shifted)
    }
    return result
}

private val SliceFruitArts = listOf(
    SliceFruitArt(
        SliceApple, Color(0xFFDE5258), Color(0xFFFFEDC7), SliceAppleDetail,
        SliceAppleCore, SliceAppleSeeds
    ),
    SliceFruitArt(
        SliceOrange, Color(0xFFF3A044), Color(0xFFFFBE58), SliceOrangeDetail,
        SliceCitrusSegments
    ),
    SliceFruitArt(
        SlicePear, Color(0xFFB6C869), Color(0xFFFFEFCA), SlicePearDetail,
        SlicePearCore, SlicePearSeeds, scale = 1.05f
    ),
    SliceFruitArt(
        SliceMelon, Color(0xFF6EA55B), Color(0xFFF16C79), SliceMelonDetail,
        seeds = SliceMelonSeeds, leaf = null, rind = 4, scale = 1.35f
    ),
    SliceFruitArt(
        SliceBerry, Color(0xFFD95068), Color(0xFFF094A0), SliceBerryDetail,
        SliceBerryCore, leaf = SliceBerryCrown, scale = 0.88f
    ),
    SliceFruitArt(
        SlicePomegranate, Color(0xFFC8455F), Color(0xFFF39A9B), SlicePomegranateDetail,
        SlicePomegranateCore, SlicePomegranateSeeds, leaf = null, scale = SlicePomegranateScale
    ),
    SliceFruitArt(
        SliceBanana, Color(0xFFE9C95C), Color(0xFFFFEDB2), SliceBananaDetail,
        seeds = SliceBananaSeeds, leaf = null, rind = 2, pivot = Offset(20f, 22f), scale = 1.05f
    ),
    SliceFruitArt(
        SlicePineapple, Color(0xFFD4AB58), Color(0xFFFFDF85), SlicePineappleDetail,
        SlicePineappleCore, leaf = SlicePineappleCrown, rind = 4, scale = 1.18f
    ),
    SliceFruitArt(
        SliceKiwi, Color(0xFF9C835C), Color(0xFFB4D578), SliceKiwiDetail,
        SliceKiwiCore, SliceKiwiSeeds, leaf = null, scale = 0.93f
    ),
    SliceFruitArt(
        SliceLemon, Color(0xFFE5D45F), Color(0xFFFFEC94), SliceOrangeDetail,
        SliceCitrusSegments, SliceCitrusSeeds, scale = 0.95f
    ),
    SliceFruitArt(
        SlicePeach, Color(0xFFEA9A86), Color(0xFFFFD89C), SlicePeachDetail,
        seeds = SlicePeachStone
    ),
    SliceFruitArt(
        SliceCoconut, Color(0xFFA4805D), Color(0xFFFFF0DA), SliceCoconutDetail,
        SliceCoconutCore, leaf = null, rind = 4, scale = 1.18f
    ),
    SliceFruitArt(
        SliceMango, Color(0xFFE8AB58), Color(0xFFFFD578), SliceMangoDetail,
        seeds = SliceMangoStone, scale = 1.08f
    ),
    SliceFruitArt(
        SliceLime, Color(0xFF94BE64), Color(0xFFE0EB9F), SliceOrangeDetail,
        SliceLimeSegments, SliceCitrusSeeds, scale = 0.9f
    ),
    SliceFruitArt(
        SlicePlum, Color(0xFF926DA8), Color(0xFFF2C973), SlicePlumDetail,
        seeds = SlicePlumStone, scale = 0.88f
    ),
    SliceFruitArt(
        SlicePassionFruit, Color(0xFF97638D), Color(0xFFF6D574), SlicePassionDetail,
        seeds = SlicePassionSeeds, leaf = null, rind = 4, scale = 0.95f
    ),
    SliceFruitArt(
        SliceApple, Color(0xFF8BB967), Color(0xFFF2EFC7), SliceAppleDetail,
        SliceAppleCore, SliceAppleSeeds
    ),
    SliceFruitArt(
        SliceBanana, Color(0xFF97DDEB), Color(0xFFE8F7F5), SliceBananaDetail,
        seeds = SliceBananaSeeds, leaf = null, rind = 2, pivot = Offset(20f, 22f), scale = 1.05f
    ),
    SliceFruitArt(
        SliceBanana, Color(0xFFF1D166), Color(0xFFFFEFBA), SliceBananaDetail,
        seeds = SliceBananaSeeds, leaf = null, rind = 2, pivot = Offset(20f, 22f), scale = 1.05f
    ),
    SliceFruitArt(
        SliceBanana, Color(0xFFF1D166), Color(0xFFFFEFBA), SliceBananaDetail,
        seeds = SliceBananaSeeds, leaf = null, rind = 2, pivot = Offset(20f, 22f), scale = 1.05f
    )
)
private val SliceFruitKinds = (0 until SliceFreeze).filter { it != 5 }

private fun Color.sliceColor(primary: Color) =
    Color(ColorUtils.blendARGB(toArgb(), primary.toArgb(), 0.2f))

private fun fruitColor(style: ArcadeStyle, kind: Int) =
    SliceFruitArts[kind].skin.sliceColor(style.playerColor)

private fun slicePowerColor(style: ArcadeStyle, kind: Int) = when (kind) {
    SliceFreeze -> Color(0xFF78C8E8)
    SliceDouble -> Color(0xFF608BE0)
    else -> Color(0xFFEA794A)
}.sliceColor(style.playerColor)

private fun DrawScope.drawSliceFruit(
    style: ArcadeStyle,
    kind: Int,
    center: Offset,
    cut: Boolean = false,
    alpha: Float = 1f,
    cutAngle: Float = 0f,
    cutSide: Int = 1
) {
    val art = SliceFruitArts[kind]
    val primary = style.playerColor
    val skin = fruitColor(style, kind)
    val ink = Color(0xFF493131).sliceColor(primary)
    val white = Color.White.sliceColor(primary)
    fun tint(color: Color) = color.copy(alpha = color.alpha * alpha)
    val pivot = art.pivot
    translate(center.x - pivot.x, center.y - pivot.y) {
        drawPath(
            art.body.path,
            tint(if (kind >= SliceFreeze) slicePowerColor(style, kind) else ink)
        )
        drawPath(art.surface, tint(skin))
        drawPath(art.shade, tint(lerp(skin, ink, 0.28f)))
        drawPath(art.shine, tint(lerp(skin, white, 0.38f)))
        if (cut) {
            val flesh = art.flesh.sliceColor(primary)
            drawPath(art.rim, tint(lerp(flesh, white, 0.4f)))
            drawPath(art.meat, tint(flesh))
            clipPath(art.meat) {
                art.section?.let {
                    val color = when (kind) {
                        11 -> ink
                        0, 2, 16 -> lerp(flesh, skin, 0.22f)
                        else -> Color(0xFFFFF0C8).sliceColor(primary)
                    }
                    drawArcadeSprite(it, tint(color), 0f, 0f)
                }
                art.seeds?.let {
                    val color = when (kind) {
                        5 -> lerp(skin, ink, 0.15f)
                        10, 12, 14 -> Color(0xFF9D674C).sliceColor(primary)
                        9, 13 -> white
                        else -> ink
                    }
                    drawArcadeSprite(it, tint(color), 0f, 0f)
                }
            }
            clipPath(art.body.path) {
                rotate(cutAngle, pivot) {
                    drawLine(
                        tint(lerp(flesh, ink, 0.45f)),
                        pivot + Offset(-40f, cutSide * 0.5f),
                        pivot + Offset(40f, cutSide * 0.5f),
                        strokeWidth = 1f
                    )
                }
            }
        } else {
            val light = when (kind) {
                0, 1, 2, 4, 5, 6, 9, 13, 16 -> true
                else -> false
            }
            val color = if (light) lerp(skin, white, 0.5f) else lerp(skin, ink, 0.3f)
            clipPath(art.surface) {
                drawArcadeSprite(art.detail, tint(color), 0f, 0f)
                if (kind >= SliceFreeze) {
                    val stripe = if (kind == SliceFreeze) white else slicePowerColor(style, kind)
                    drawArcadeSprite(SliceBonusStripes, tint(stripe), 0f, 0f)
                }
            }
        }
        if (kind == 6 || kind >= SliceFreeze) {
            clipPath(art.body.path) {
                pixelRect(tint(ink), 1f, 18f, 3f, 3f)
                pixelRect(tint(ink), 29f, 8f, 4f, 3f)
            }
        }
        art.leaf?.let {
            val top = when (kind) {
                4 -> -2f
                7 -> -10f
                else -> -5f
            }
            val leaf = Color(0xFF40864F).sliceColor(primary)
            if (kind != 4 && kind != 7) pixelRect(tint(ink), 17f, 1f, 2f, 6f)
            drawArcadeSprite(it, tint(lerp(leaf, ink, 0.25f)), 0f, top + 1f)
            drawArcadeSprite(it, tint(leaf), 0f, top)
        }
    }
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
    var touching: Boolean = false, var flash: Float = 0f,
    var frenzy: Boolean = false,
    val size: Float = 1f,
    val explosive: Boolean = false
) {
    val scale get() = if (bomb) 1f else SliceFruitArts[kind].scale * size
    val radius get() = (if (!bomb && (kind == 6 || kind >= SliceFreeze)) 22f else 19f) * scale
}

@Serializable
private data class SliceBlast(
    val point: ArcadeOffset,
    val radius: Float,
    var life: Float = 0.35f
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
    val kind: Int,
    val cut: Float = 0f,
    val side: Int = 1,
    val spin: Float = 0f,
    val size: Float = 1f
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
    val blasts = mutableListOf<SliceBlast>()

    @Transient
    val effects = ArcadeEffects()
    var misses = 0
    var combo = 0
    var freeze = 0f
        private set
    var doublePoints = 0f
        private set
    var frenzy = 0f
        private set

    @Transient
    private var finger: Offset? = null
    private var comboTime = 0f
    private var timer = 0.6f
    private var elapsed = 0f
    private var waves = 0
    private var criticalIn = 30
    private var pomegranateIn = 30f
    private var stroke = 0
    private var bonusIn = 22f
    private var frenzyIn = 0f
    private val patterns: ArcadeDeque<Int> = ArrayDeque()
    private val bonuses: ArcadeDeque<Int> = ArrayDeque()
    private val gravity get() = (height - topInset) * 1.35f

    override fun reset() {
        fruits.clear()
        pieces.clear()
        trail.clear()
        stains.clear()
        rewards.clear()
        blasts.clear()
        patterns.clear()
        bonuses.clear()
        effects.clear()
        misses = 0
        timer = 0.6f
        elapsed = 0f
        waves = 0
        criticalIn = Random.nextInt(25, 45)
        pomegranateIn = 28f + Random.nextFloat() * 14f
        stroke = 0
        freeze = 0f
        doublePoints = 0f
        frenzy = 0f
        bonusIn = 18f + Random.nextFloat() * 8f
        frenzyIn = 0f
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
        val explosions = ArrayDeque<Offset>()
        val normal = Offset(-delta.y, delta.x) / sqrt(length)
        val angle = atan2(delta.y, delta.x) * 180f / PI.toFloat()
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            if (fruit.delay > 0f) continue
            if (fruit.bomb && frenzy > 0f) {
                iterator.remove()
                continue
            }
            val t =
                (((fruit.x - start.x) * delta.x + (fruit.y - start.y) * delta.y) / length).coerceIn(
                    0f,
                    1f
                )
            val radius = fruit.radius
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
                iterator.remove()
                cutFruit(fruit, normal, angle)
                if (fruit.explosive) explosions += Offset(fruit.x, fruit.y)
            }
        }
        while (explosions.isNotEmpty()) {
            val center = explosions.removeFirst()
            val radius = minOf(76f, width * 0.22f)
            blasts += SliceBlast(center, radius)
            effects.burst(center, 20)
            val nearby = fruits.filter {
                !it.bomb && it.kind != 5 && it.delay <= 0f &&
                        it.y in topInset..height &&
                        (Offset(it.x, it.y) - center).getDistance() <= radius
            }
            for (fruit in nearby) {
                fruits.remove(fruit)
                val direction = Offset(fruit.x, fruit.y) - center
                val distance = direction.getDistance()
                val outward = if (distance > 1f) direction / distance else normal
                cutFruit(fruit, outward, atan2(outward.y, outward.x) * 180f / PI.toFloat() + 90f)
                if (fruit.explosive) explosions += Offset(fruit.x, fruit.y)
            }
        }
        if (frenzy > 0f) fruits.removeAll { it.bomb }
    }

    private fun cutFruit(fruit: SliceFruit, normal: Offset, angle: Float) {
        when (fruit.kind) {
            SliceFreeze -> freeze = 6f
            SliceDouble -> doublePoints = 6f
            SliceFrenzy -> {
                frenzy = 6f
                frenzyIn = 0f
                fruits.forEach { it.frenzy = true }
            }
        }
        if (comboTime <= 0f) combo = 0
        combo++
        comboTime = 0.14f
        var points = 1
        var extra = if (fruit.explosive) 5 else 0
        if (combo >= 3) {
            points += if (combo == 3) 3 else 1
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
            extra += 10
        }
        if (extra > 0) rewards += SliceReward(
            Offset(fruit.x, fruit.y),
            extra * if (doublePoints > 0f) 2 else 1,
            false
        )
        addScore(points + extra)
        effects.burst(
            point = Offset(x = fruit.x, y = fruit.y),
            count = if (combo >= 3) 12 else 7
        )
        if (stains.size >= 18) stains.removeAt(0)
        stains += SliceStain(
            Offset(fruit.x, fruit.y),
            fruit.kind,
            radius = (7f + Random.nextFloat() * 6f) * fruit.scale
        )
        for (sign in -1..1 step 2) {
            pieces += SlicePiece(
                x = fruit.x, y = fruit.y,
                vx = fruit.vx * 0.25f + normal.x * sign * 65f,
                vy = fruit.vy * 0.2f + normal.y * sign * 65f - 40f,
                life = 0.8f,
                angle = fruit.angle,
                kind = fruit.kind,
                cut = angle - fruit.angle,
                side = sign,
                spin = sign * 95f + fruit.spin * 0.2f,
                size = fruit.size
            )
        }
    }

    override fun stopInput() {
        finger = null
        combo = 0
    }

    override fun advance(seconds: Float) {
        val motion = seconds * if (freeze > 0f) 0.45f else 1f
        freeze = (freeze - seconds).coerceAtLeast(0f)
        doublePoints = (doublePoints - seconds).coerceAtLeast(0f)
        val wasFrenzy = frenzy > 0f
        frenzy = (frenzy - seconds).coerceAtLeast(0f)
        if (wasFrenzy && frenzy == 0f) timer = maxOf(timer, 1.15f)
        elapsed += seconds
        pomegranateIn -= seconds
        bonusIn -= seconds
        timer -= motion
        comboTime -= motion
        if (comboTime <= 0f) combo = 0
        effects.advance(seconds)
        stains.forEach { it.life -= seconds }
        stains.removeAll { it.life <= 0f }
        rewards.forEach { it.life -= seconds }
        rewards.removeAll { it.life <= 0f }
        blasts.forEach { it.life -= seconds }
        blasts.removeAll { it.life <= 0f }
        for (trace in trail) trace.life -= seconds * 5f
        trail.removeAll { it.life <= 0f }
        if (pomegranateIn <= 0f && frenzy == 0f && fruits.isEmpty()) {
            fruits += thrownFruit(
                x = width * (0.35f + Random.nextFloat() * 0.3f),
                landing = width / 2f,
                rise = (height - topInset) * 0.72f,
                delay = 0f,
                bomb = false
            ).copy(kind = 5, spin = 15f, hang = 3.2f, explosive = false)
            pomegranateIn = 35f + Random.nextFloat() * 20f
        }
        val pomegranate = fruits.any { it.kind == 5 }
        if (frenzy > 0f && !pomegranate) {
            fruits.removeAll { it.bomb }
            frenzyIn -= motion
            timer = 0.9f
            if (frenzyIn <= 0f && fruits.size < 22) {
                for (side in 0..1) fruits += thrownFruit(
                    x = if (side == 0) -20f else width + 20f,
                    landing = width * if (side == 0) 0.8f else 0.2f,
                    rise = (height - topInset) * (0.62f + Random.nextFloat() * 0.1f),
                    delay = 0f,
                    bomb = false
                ).copy(frenzy = true)
                frenzyIn = 0.15f + Random.nextFloat() * 0.06f
            }
        } else if (timer <= 0f && pomegranateIn > 0f && !pomegranate) throwWave()
        if (bonusIn <= 0f && !pomegranate && fruits.size < 24) throwBonus()
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            fruit.flash = (fruit.flash - seconds * 0.8f).coerceAtLeast(0f)
            if (fruit.delay > 0f) {
                fruit.delay -= motion
                continue
            }
            if (fruit.kind == 5) {
                timer = 0.8f
                if (fruit.vy >= -35f && fruit.hang > 0f) {
                    fruit.hang -= motion
                    fruit.vy = 0f
                    fruit.x += (width / 2f - fruit.x) * minOf(1f, motion * 2f)
                    fruit.angle += 15f * motion
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
                            x = fruit.x, y = fruit.y, vx = -side * 80f, vy = -70f,
                            life = 0.8f, angle = fruit.angle, kind = 5,
                            cut = 90f - fruit.angle, side = side, spin = -side * 95f,
                            size = fruit.size
                        )
                        iterator.remove()
                    }
                    continue
                }
            }
            fruit.angle = arcadeAngle(fruit.angle + fruit.spin * motion)
            fruit.vy += gravity * motion
            fruit.y += fruit.vy * motion
            fruit.x += fruit.vx * motion
            if (fruit.y > height + 40f && fruit.vy > 0f) {
                if (!fruit.bomb && fruit.kind != 5 && fruit.kind < SliceFreeze && !fruit.frenzy) misses++
                iterator.remove()
            }
        }
        for (piece in pieces) {
            piece.life -= seconds
            piece.vy += gravity * motion
            piece.x += piece.vx * motion
            piece.y += piece.vy * motion
        }
        pieces.removeAll { it.life <= 0f }
        if (misses >= 3) phase = ArcadePhase.Over
    }

    private fun addScore(points: Int) {
        val before = score
        score += points * if (doublePoints > 0f) 2 else 1
        misses = (misses - (score / 100 - before / 100)).coerceAtLeast(0)
    }

    private fun throwBonus() {
        val roll = Random.nextFloat()
        val count = if (roll < 0.03f) 3 else if (roll < 0.25f) 2 else 1
        val spawned = mutableSetOf<Int>()
        repeat(minOf(count, 24 - fruits.size)) { index ->
            if (bonuses.isEmpty()) bonuses.addAll(
                listOf(SliceFreeze, SliceDouble, SliceFrenzy).shuffled()
            )
            val kind = bonuses.firstOrNull { it !in spawned } ?: return@repeat
            for (attempt in 0 until 24) {
                val fruit = thrownFruit(
                    x = width * (0.15f + Random.nextFloat() * 0.7f),
                    landing = width * (0.25f + Random.nextFloat() * 0.5f),
                    rise = (height - topInset) * 0.7f,
                    delay = index * 0.12f,
                    bomb = false
                ).copy(kind = kind, explosive = false)
                if (fruits.none { it.bomb && conflicts(fruit, it) }) {
                    fruits += fruit
                    bonuses.remove(kind)
                    spawned += kind
                    break
                }
            }
        }
        bonusIn = if (spawned.isEmpty()) 0.4f else 22f + Random.nextFloat() * 12f
    }

    private fun throwWave() {
        val pace = 1f - exp(-elapsed / 75f)
        if (patterns.isEmpty()) patterns.addAll((0..if (elapsed < 35f) 3 else 4).shuffled())
        val recovery = waves >= 8 && waves % 8 == 0
        val pattern = when {
            recovery || waves < 2 -> 0
            waves == 2 -> 1
            else -> patterns.removeFirst()
        }
        waves++
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
        val bombLimit = if (elapsed < 30f) 1 else if (elapsed < 75f) 2 else 3
        if (!recovery && elapsed > 10f && Random.nextFloat() < 0.18f + pace * 0.28f) {
            val roll = Random.nextFloat()
            val count = if (bombLimit == 3 && roll < 0.12f) 3
            else if (bombLimit >= 2 && roll < 0.4f) 2 else 1
            repeat(minOf(count, bombLimit - fruits.count { it.bomb }).coerceAtLeast(0)) { index ->
                for (attempt in 0 until 48) {
                    val bomb = thrownFruit(
                        x = width * (0.1f + Random.nextFloat() * 0.8f),
                        landing = width * (0.12f + Random.nextFloat() * 0.76f),
                        rise = rise * (0.65f + Random.nextFloat() * 0.3f),
                        delay = index * 0.18f + Random.nextFloat() * 0.3f,
                        bomb = true
                    )
                    if (fruits.none { conflicts(bomb, it) }) {
                        fruits += bomb
                        break
                    }
                }
            }
        }
        timer += 2.35f - pace * 0.95f + Random.nextFloat() * 0.5f +
                if (recovery) 0.65f else if (pattern == 4) 0.35f else 0f
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
        val kind = if (bomb) 0 else SliceFruitKinds.random()
        return SliceFruit(
            x = x, y = height + 20f, vx = (landing - x) / airtime, vy = -speed,
            bomb = bomb,
            kind = kind,
            angle = Random.nextFloat() * 360f,
            spin = (Random.nextFloat() - 0.5f) * 180f, delay = delay,
            size = if (bomb) 1f else 0.96f + Random.nextFloat() * 0.08f,
            explosive = !bomb && kind == 4 && elapsed > 15f && Random.nextInt(4) == 0
        )
    }

    private fun conflicts(first: SliceFruit, second: SliceFruit): Boolean {
        for (sample in 0..150) {
            val time = sample / 60f
            fun position(fruit: SliceFruit): Offset {
                val elapsed = (time - fruit.delay.coerceAtLeast(0f)).coerceAtLeast(0f)
                return Offset(
                    fruit.x + fruit.vx * elapsed,
                    fruit.y + fruit.vy * elapsed + gravity * elapsed * elapsed / 2f
                )
            }

            val a = position(first)
            val b = position(second)
            if (a.y in topInset - first.radius..height + first.radius &&
                b.y in topInset - second.radius..height + second.radius &&
                (a - b).getDistance() < first.radius + second.radius + 6f
            ) return true
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
        blasts.clear()
        effects.clear()
        stopInput()
    }
}