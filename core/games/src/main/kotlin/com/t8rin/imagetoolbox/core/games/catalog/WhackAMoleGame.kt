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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.random.Random

private val ArcadeMole = ArcadeSprite(
    "ArcadeMole", 40, 42,
    "M5 8V3H12V6H28V3H35V8H38V16H36V21H38V42H2V21H4V16H2V8Z"
)

private val MoleHole = ArcadeSprite(
    "MoleHole", 58, 18,
    "M10 0H46V3H53V6H58V12H51V15H44V18H13V15H5V12H0V6H5V3H10Z"
)
private val MoleMine = ArcadeSprite(
    "MoleMine", 40, 40,
    "M17 0H23V6H29V3H35V10H32V15H40V23H33V29H36V36H29V33H23V40H17V33H10V36H3V29H6V23H0V17H7V11H4V4H11V7H17Z"
)

@Composable
fun WhackAMoleGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::WhackAMoleRun
    )
    ArcadeBoard(
        game = ArcadeGame.WhackAMole,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::hit
        ) {
            repeat(28) { index ->
                val x = ((index * 73 + index * index * 17 + 29) % 347) / 347f * run.width
                val y =
                    run.topInset + ((index * 113 + index * index * 47 + 13) % 431) / 431f * (run.height - run.topInset)
                val color = style.obstacleColor.copy(alpha = 0.14f + index % 3 * 0.04f)
                pixelRect(
                    color = color,
                    x = x,
                    y = y,
                    width = 2f,
                    height = 5f
                )
                pixelRect(
                    color = color,
                    x = x - 3f,
                    y = y - 2f,
                    width = 2f,
                    height = 4f
                )
                pixelRect(
                    color = color,
                    x = x + 3f,
                    y = y - 3f,
                    width = 2f,
                    height = 5f
                )
            }
            for (index in 0..8) {
                val center = run.hole(index)
                val unit = run.cell / 76f
                translate(center.x, center.y) {
                    scale(unit, Offset.Zero) {
                        drawArcadeSprite(
                            sprite = MoleHole,
                            tint = style.obstacleColor.copy(alpha = 0.16f),
                            x = -29f,
                            y = 5f
                        )
                        pixelRect(
                            color = style.overlayColor.copy(alpha = 0.65f),
                            x = -21f,
                            y = 8f,
                            width = 42f,
                            height = 9f
                        )
                        pixelRect(
                            color = style.overlayColor.copy(alpha = 0.65f),
                            x = -25f,
                            y = 11f,
                            width = 50f,
                            height = 3f
                        )
                        val type = run.types[index]
                        if (type != 0) {
                            val rise = minOf(1f, run.ages[index] / 0.12f, run.timers[index] / 0.18f)
                            val y = 15f - rise * 42f
                            clipRect(left = -30f, top = -42f, right = 30f, bottom = 12f) {
                                if (type == 1) {
                                    drawArcadeSprite(
                                        sprite = ArcadeMole,
                                        tint = style.obstacleColor,
                                        x = -20f,
                                        y = y
                                    )
                                    pixelRect(
                                        color = style.accentColor,
                                        x = -14f,
                                        y = y + 7f,
                                        width = 5f,
                                        height = 5f
                                    )
                                    pixelRect(
                                        color = style.accentColor,
                                        x = 9f,
                                        y = y + 7f,
                                        width = 5f,
                                        height = 5f
                                    )
                                    pixelRect(
                                        color = style.playerDetailColor,
                                        x = -10f,
                                        y = y + 15f,
                                        width = 5f,
                                        height = 6f
                                    )
                                    pixelRect(
                                        color = style.playerDetailColor,
                                        x = 5f,
                                        y = y + 15f,
                                        width = 5f,
                                        height = 6f
                                    )
                                    pixelRect(
                                        color = style.textColor,
                                        x = -8f,
                                        y = y + 17f,
                                        width = 2f,
                                        height = 3f
                                    )
                                    pixelRect(
                                        color = style.textColor,
                                        x = 6f,
                                        y = y + 17f,
                                        width = 2f,
                                        height = 3f
                                    )
                                    pixelRect(
                                        color = style.accentColor,
                                        x = -9f,
                                        y = y + 24f,
                                        width = 18f,
                                        height = 8f
                                    )
                                    pixelRect(
                                        color = style.accentColor,
                                        x = -6f,
                                        y = y + 22f,
                                        width = 12f,
                                        height = 12f
                                    )
                                    pixelRect(
                                        color = style.playerDetailColor,
                                        x = -3f,
                                        y = y + 24f,
                                        width = 6f,
                                        height = 3f
                                    )
                                    pixelRect(
                                        color = style.playerDetailColor,
                                        x = -3f,
                                        y = y + 34f,
                                        width = 6f,
                                        height = 4f
                                    )
                                    pixelRect(
                                        color = style.obstacleColor,
                                        x = -1f,
                                        y = y + 34f,
                                        width = 2f,
                                        height = 4f
                                    )
                                } else {
                                    drawArcadeSprite(
                                        sprite = MoleMine,
                                        tint = style.hazardColor,
                                        x = -20f,
                                        y = y + 2f
                                    )
                                    repeat(4) { pixel ->
                                        pixelRect(
                                            color = style.playerDetailColor,
                                            x = -7f + pixel * 4f,
                                            y = y + 14f + pixel * 4f,
                                            width = 3f,
                                            height = 3f
                                        )
                                        pixelRect(
                                            color = style.playerDetailColor,
                                            x = 5f - pixel * 4f,
                                            y = y + 14f + pixel * 4f,
                                            width = 3f,
                                            height = 3f
                                        )
                                    }
                                }
                            }
                        }
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.32f),
                            x = -22f,
                            y = 16f,
                            width = 44f,
                            height = 5f
                        )
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.18f),
                            x = -17f,
                            y = 21f,
                            width = 34f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.7f),
                            x = -29f,
                            y = 10f,
                            width = 3f,
                            height = 8f
                        )
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.7f),
                            x = -32f,
                            y = 8f,
                            width = 3f,
                            height = 6f
                        )
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.7f),
                            x = 25f,
                            y = 8f,
                            width = 3f,
                            height = 10f
                        )
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.7f),
                            x = 29f,
                            y = 5f,
                            width = 3f,
                            height = 9f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.25f),
                            x = -15f,
                            y = 17f,
                            width = 5f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.25f),
                            x = 9f,
                            y = 19f,
                            width = 4f,
                            height = 2f
                        )
                        if (run.hits[index] > 0f) {
                            val length = 8f + (0.3f - run.hits[index]) * 20f
                            val color = style.accentColor.copy(
                                alpha = (run.hits[index] / 0.3f).coerceIn(
                                    0f,
                                    1f
                                )
                            )
                            pixelRect(
                                color = color,
                                x = -2f,
                                y = -19f - length,
                                width = 4f,
                                height = length * 2f
                            )
                            pixelRect(
                                color = color,
                                x = -length,
                                y = -21f,
                                width = length * 2f,
                                height = 4f
                            )
                            pixelRect(
                                color = color,
                                x = -8f,
                                y = -27f,
                                width = 16f,
                                height = 16f
                            )
                        }
                    }
                }
            }
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

internal class WhackAMoleRun : ArcadeRun() {
    val timers = FloatArray(9)
    val hits = FloatArray(9)
    val ages = FloatArray(9)
    val types = IntArray(9)
    val effects = ArcadeEffects()
    val side get() = minOf(width - 32f, height - topInset - 22f)
    val cell get() = side / 3f
    val origin
        get() = Offset(
            (width - side) / 2f,
            topInset + 14f + (height - topInset - 22f - side) / 2f
        )
    var misses = 0
    var combo = 0
    private var timer = 0.45f
    private var elapsed = 0f
    private var last = -1

    fun hole(index: Int) = origin + Offset((index % 3 + 0.5f) * cell, (index / 3 + 0.62f) * cell)

    override fun reset() {
        timers.fill(0f)
        hits.fill(0f)
        ages.fill(0f)
        types.fill(0)
        effects.clear()
        misses = 0
        combo = 0
        timer = 0.45f
        elapsed = 0f
        last = -1
    }

    fun hit(point: Offset) {
        val local = point - origin
        if (local.x !in 0f..<side || local.y !in 0f..<side) return
        val index = (local.y / cell).toInt() * 3 + (local.x / cell).toInt()
        when (types[index]) {
            1 -> {
                hits[index] = 0.3f
                combo++
                score += 1 + minOf(combo / 5, 4)
                effects.burst(point = hole(index) - Offset(0f, cell * 0.2f))
            }

            2 -> {
                misses++
                combo = 0
            }

            else -> return
        }
        types[index] = 0
        timers[index] = 0f
        if (misses >= 3) phase = ArcadePhase.Over
        redraw()
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        timer -= seconds
        effects.advance(seconds)
        for (index in hits.indices) hits[index] = (hits[index] - seconds).coerceAtLeast(0f)
        for (index in 0..8) if (types[index] != 0) {
            ages[index] += seconds
            timers[index] -= seconds
            if (timers[index] <= 0f) {
                if (types[index] == 1) {
                    misses++
                    combo = 0
                }
                types[index] = 0
                timers[index] = 0f
            }
        }
        if (misses >= 3) {
            phase = ArcadePhase.Over
            return
        }
        if (timer <= 0f) {
            val empty = types.indices.filter { types[it] == 0 && hits[it] == 0f && it != last }
            if (empty.isNotEmpty()) {
                val index = empty.random()
                last = index
                types[index] = if (elapsed > 7f && Random.nextFloat() < 0.22f) 2 else 1
                timers[index] = maxOf(0.85f, 1.65f - elapsed * 0.008f)
                ages[index] = 0f
            }
            timer += maxOf(0.4f, 0.95f - elapsed * 0.006f)
        }
    }

    override fun save(state: Bundle) {
        state.putIntArray("holes", types.copyOf())
        state.putFloatArray("hits", hits.copyOf())
        state.putFloatArray("timers", timers.copyOf())
        state.putFloatArray("ages", ages.copyOf())
        state.putFloatArray(
            "moles",
            floatArrayOf(misses.toFloat(), combo.toFloat(), timer, elapsed, last.toFloat())
        )
    }

    override fun restore(state: Bundle) {
        state.getIntArray("holes")?.copyInto(types)
        state.getFloatArray("hits")?.copyInto(hits)
        state.getFloatArray("timers")?.copyInto(timers)
        state.getFloatArray("ages")?.copyInto(ages)
        state.getFloatArray("moles")?.let {
            misses = it[0].toInt()
            combo = it[1].toInt()
            timer = it[2]
            elapsed = it[3]
            last = it[4].toInt()
        }
    }
}