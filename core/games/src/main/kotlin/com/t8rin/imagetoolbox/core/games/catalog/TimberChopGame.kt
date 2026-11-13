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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Forest
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.ceil
import kotlin.random.Random

@RegisterArcadeGame
internal data object TimberChopGame : ArcadeGameDefinition {
    override val title = R.string.arcade_timberchop
    override val description = R.string.arcade_timberchop_sub
    override val icon get() = Icons.Outlined.Forest
    override val order = 25

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::TimberChopRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.chop(if (it.x < run.width / 2f) -1 else 1) }) {

                val center = run.width / 2f
                val base = run.base
                val segment = run.segment
                clipRect(top = 0f, bottom = run.height) {
                    drawTimberForest(style, run.width, base)
                    run.branches.forEachIndexed { index, side ->
                        val y = base - (index + 1f + run.cut) * segment
                        if (y + segment >= 0f && y < run.height) {
                            pixelRect(
                                color = style.obstacleColor,
                                x = center - 17f,
                                y = y,
                                width = 34f,
                                height = segment + 0.5f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.22f),
                                x = center - 13f,
                                y = y + 3f,
                                width = 6f,
                                height = segment - 7f
                            )
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.32f),
                                x = center + 10f,
                                y = y,
                                width = 5f,
                                height = segment
                            )
                            pixelRect(
                                color = style.detailColor,
                                x = center - 3f + index % 3 * 3f,
                                y = y + segment * 0.55f,
                                width = 3f,
                                height = 7f
                            )
                            if ((run.chops + index + 1) % 25 == 0) {
                                pixelRect(
                                    style.accentColor,
                                    run.width / 2f - 17f,
                                    y + segment * 0.18f,
                                    34f,
                                    3f
                                )
                                pixelRect(
                                    style.accentColor,
                                    run.width / 2f - 17f,
                                    y + segment * 0.72f,
                                    34f,
                                    3f
                                )
                            }
                            if (side != 0) {
                                val x = if (side < 0) center - 71f else center + 15f
                                pixelRect(
                                    color = style.obstacleColor,
                                    x = x,
                                    y = y + segment * 0.24f,
                                    width = 56f,
                                    height = 11f
                                )
                                pixelRect(
                                    color = style.detailColor,
                                    x = x,
                                    y = y + segment * 0.24f + 8f,
                                    width = 56f,
                                    height = 3f
                                )
                                val tip = if (side < 0) x + 8f else x + 42f
                                pixelRect(
                                    color = style.obstacleColor,
                                    x = tip,
                                    y = y + segment * 0.24f - 10f,
                                    width = 6f,
                                    height = 13f
                                )
                                pixelRect(
                                    color = style.playerColor.copy(alpha = 0.5f),
                                    x = tip - 5f,
                                    y = y + segment * 0.24f - 16f,
                                    width = 15f,
                                    height = 8f
                                )
                            }
                        }
                    }
                    for ((x, y, _, _, spin, age) in run.chips) {
                        rotate(degrees = spin * age, pivot = Offset(x = x, y = y)) {
                            pixelRect(
                                color = style.obstacleColor,
                                x = x - 14f,
                                y = y - 8f,
                                width = 28f,
                                height = 16f
                            )
                            pixelRect(
                                color = style.accentColor.copy(alpha = 0.7f),
                                x = x - 11f,
                                y = y - 6f,
                                width = 5f,
                                height = 12f
                            )
                        }
                    }
                }
                val px = center + run.side * 43f
                val foot = base - 1f
                val kick = run.cut * 4f
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.3f),
                    x = px - 15f,
                    y = foot,
                    width = 30f,
                    height = 4f
                )
                scale(scaleX = -run.side.toFloat(), scaleY = 1f, pivot = Offset(x = px, y = foot)) {
                    drawArcadeSprite(
                        sprite = TimberWorker,
                        tint = style.playerColor,
                        x = px - 14f,
                        y = foot - 44f + kick
                    )
                    drawArcadeSprite(
                        sprite = TimberFace,
                        tint = style.accentColor,
                        x = px - 14f,
                        y = foot - 44f + kick
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = px + 4f,
                        y = foot - 34f + kick,
                        width = 2f,
                        height = 3f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.5f),
                        x = px - 4f,
                        y = foot - 29f + kick,
                        width = 10f,
                        height = 3f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.4f),
                        x = px - 8f,
                        y = foot - 21f + kick,
                        width = 2f,
                        height = 11f
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = px - 11f,
                        y = foot - 4f,
                        width = 8f,
                        height = 4f
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = px + 2f,
                        y = foot - 4f,
                        width = 8f,
                        height = 4f
                    )
                }
                val hand = Offset(x = px - run.side * 9f, y = foot - 22f + kick)
                rotate(degrees = run.side * (65f - run.cut * 100f), pivot = hand) {
                    pixelRect(
                        color = style.accentColor,
                        x = hand.x - 2f,
                        y = hand.y - 20f,
                        width = 4f,
                        height = 24f
                    )
                    drawArcadeSprite(
                        sprite = TimberAxe,
                        tint = style.obstacleColor,
                        x = hand.x - 10f,
                        y = hand.y - 25f
                    )
                    pixelRect(
                        color = style.playerDetailColor,
                        x = hand.x - 10f,
                        y = hand.y - 25f,
                        width = 2f,
                        height = 14f
                    )
                }
                repeat(ceil(run.width / 37f).toInt()) {
                    val x = it * 37f + 9f
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.45f),
                        x = x,
                        y = base + 5f + it % 3 * 3f,
                        width = 3f,
                        height = 4f
                    )
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.3f),
                        x = x + 4f,
                        y = base + 7f + it % 3 * 3f,
                        width = 2f,
                        height = 3f
                    )
                }
                val color = if (run.energy < 0.25f) style.hazardColor else style.accentColor
                val clock = Offset(x = 22f, y = run.topInset / 2f)
                drawCircle(
                    color = color,
                    radius = 6f,
                    center = clock,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f)
                )
                drawLine(
                    color = color,
                    start = clock,
                    end = clock + Offset(x = 0f, y = -4f),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = color,
                    start = clock,
                    end = clock + Offset(x = 3f, y = 1f),
                    strokeWidth = 1.5f
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.3f),
                    x = 34f,
                    y = clock.y - 2f,
                    width = 42f,
                    height = 4f
                )
                pixelRect(
                    color = color,
                    x = 34f,
                    y = clock.y - 2f,
                    width = 42f * run.energy,
                    height = 4f
                )
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private val TimberWorker = ArcadeSprite(
    width = 28,
    height = 44,
    data = "M6 0H19V2H22V6H24V10H26V14H22V18H20V20H23V24H26V30H20V28H19V35H23V42H17V44H13V35H10V44H4V42H6V33H2V24H4V20H8V18H6V14H4V8H6Z"
)

private val TimberFace = ArcadeSprite(
    width = 28,
    height = 44,
    data = "M8 8H22V10H24V14H20V18H12V16H8Z M20 26H26V30H20Z"
)

private val TimberPine = ArcadeSprite(
    width = 40,
    height = 72,
    data = "M18 0H22V6H25V12H29V16H25V19H32V23H28V26H35V30H30V34H38V38H32V41H40V45H24V72H17V45H0V41H8V38H2V34H10V30H5V26H12V23H8V19H15V16H11V12H15V6H18Z"
)

private val TimberAxe = ArcadeSprite(
    width = 18,
    height = 14,
    data = "M0 0H5V2H11V4H18V10H11V12H5V14H0Z"
)

private fun DrawScope.drawTimberForest(style: ArcadeStyle, width: Float, base: Float) {
    for (layer in 0..1) {
        val spacing = if (layer == 0) 67f else 83f
        val count = ceil(width / spacing).toInt() + 2
        for (tree in 0 until count) {
            val x = tree * spacing - 19f + layer * 24f
            val height = (if (layer == 0) 65f else 91f) + (tree * 7 + layer) % 3 * 22f
            scale(height / 72f, pivot = Offset(x, base)) {
                drawArcadeSprite(
                    sprite = TimberPine,
                    tint = style.detailColor.copy(alpha = if (layer == 0) 0.085f else 0.17f),
                    x = x - 20f,
                    y = base - 72f
                )
            }
        }
    }
}

@Serializable
private data class TimberChip(
    var x: Float,
    var y: Float,
    val vx: Float,
    var vy: Float,
    val spin: Float,
    var age: Float = 0f
)

@Serializable
private class TimberChopRun : ArcadeRun() {
    val branches = mutableListOf<Int>()
    val chips = mutableListOf<TimberChip>()

    @Transient
    val effects = ArcadeEffects()
    val base get() = height - 23f
    val segment get() = minOf(43f, (height - topInset - 26f) / 5f)
    var side = -1
    var energy = 1f
    var cut = 0f
    var chops = 0
    var begun = false
    private var sinceChop = 0f
    private var chain = 0
    var rest = 0f
        private set

    override fun isValid(): Boolean = super.isValid() &&
            branches.isNotEmpty()

    override fun reset() {
        branches.clear()
        branches += listOf(0, 0, -1, 0, 1, 0)
        populate()
        chips.clear()
        effects.clear()
        side = -1
        energy = 1f
        cut = 0f
        chops = 0
        begun = false
        sinceChop = 0f
        chain = 0
        rest = 0f
    }

    private fun populate() {
        while (branches.size < maxOf(
                24,
                ceil(base / segment).toInt() + 3
            )
        ) branches += if (branches.last() != 0 || Random.nextFloat() < 0.22f) 0
        else if (Random.nextBoolean()) -1 else 1
    }

    fun chop(side: Int) {
        if (cut > 0.72f) return
        this.side = side
        begun = true
        if (branches.first() == side) {
            phase = ArcadePhase.Over
            return
        }
        branches.removeAt(0)
        populate()
        chips += TimberChip(
            x = width / 2f,
            y = base - segment / 2f,
            vx = -side * 170f,
            vy = -90f,
            spin = side * 300f
        )
        if (chips.size > 6) chips.removeAt(0)
        effects.burst(
            point = Offset(x = width / 2f - side * 18f, y = base - segment / 2f),
            count = 7
        )
        cut = 1f
        chops++
        chain = if (sinceChop < 0.45f) chain + 1 else 1
        score += 1 + if (chain % 10 == 0) 5 else 0
        sinceChop = 0f
        val bonus = chops % 25 == 0
        if (bonus) {
            rest = 1.2f
            score += 10
            effects.burst(Offset(width / 2f, base - segment / 2f), 14)
        }
        energy = (energy + if (bonus) 0.25f else 0.065f).coerceAtMost(1f)
        if (branches.first() == side) {
            cut = 0f
            phase = ArcadePhase.Over
        }
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        cut = (cut - seconds * 9f).coerceAtLeast(0f)
        sinceChop += seconds
        rest = (rest - seconds).coerceAtLeast(0f)
        if (begun && rest == 0f) {
            energy =
                (energy - seconds * (0.11f + 0.13f * chops / (chops + 120f))).coerceAtLeast(0f)
            if (energy == 0f) phase = ArcadePhase.Over
        }
        for (chip in chips) {
            chip.age += seconds
            chip.vy += 360f * seconds
            chip.x += chip.vx * seconds
            chip.y += chip.vy * seconds
        }
        chips.removeAll { it.age > 0.8f }
    }

    override fun onResize(previous: Size) {
        if (branches.isNotEmpty()) populate()
    }
}