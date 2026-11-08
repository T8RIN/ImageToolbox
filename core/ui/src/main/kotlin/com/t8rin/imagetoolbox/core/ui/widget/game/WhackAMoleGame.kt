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

package com.t8rin.imagetoolbox.core.ui.widget.game

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
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
    val run = rememberArcadeRun(initialState, ::WhackAMoleRun)
    ArcadeBoard(
        ArcadeGame.WhackAMole, run, style, modifier, initialState, onHighScoreChanged, onGameOver
    ) {
        ArcadeField(run, onPress = run::hit) {
            repeat(28) { index ->
                val x = ((index * 73 + index * index * 17 + 29) % 347) / 347f * run.width
                val y =
                    run.topInset + ((index * 113 + index * index * 47 + 13) % 431) / 431f * (run.height - run.topInset)
                val color = style.obstacleColor.copy(alpha = 0.14f + index % 3 * 0.04f)
                pixelRect(color, x, y, 2f, 5f)
                pixelRect(color, x - 3f, y - 2f, 2f, 4f)
                pixelRect(color, x + 3f, y - 3f, 2f, 5f)
            }
            for (index in 0..8) {
                val center = run.hole(index)
                val unit = run.cell / 76f
                translate(center.x, center.y) {
                    scale(unit, Offset.Zero) {
                        drawArcadeSprite(
                            MoleHole,
                            style.obstacleColor.copy(alpha = 0.16f),
                            -29f,
                            5f
                        )
                        pixelRect(style.overlayColor.copy(alpha = 0.65f), -21f, 8f, 42f, 9f)
                        pixelRect(style.overlayColor.copy(alpha = 0.65f), -25f, 11f, 50f, 3f)
                        val type = run.types[index]
                        if (type != 0) {
                            val rise = minOf(1f, run.ages[index] / 0.12f, run.timers[index] / 0.18f)
                            val y = 15f - rise * 42f
                            clipRect(left = -30f, top = -42f, right = 30f, bottom = 12f) {
                                if (type == 1) {
                                    drawArcadeSprite(ArcadeMole, style.obstacleColor, -20f, y)
                                    pixelRect(style.accentColor, -14f, y + 7f, 5f, 5f)
                                    pixelRect(style.accentColor, 9f, y + 7f, 5f, 5f)
                                    pixelRect(style.playerDetailColor, -10f, y + 15f, 5f, 6f)
                                    pixelRect(style.playerDetailColor, 5f, y + 15f, 5f, 6f)
                                    pixelRect(style.textColor, -8f, y + 17f, 2f, 3f)
                                    pixelRect(style.textColor, 6f, y + 17f, 2f, 3f)
                                    pixelRect(style.accentColor, -9f, y + 24f, 18f, 8f)
                                    pixelRect(style.accentColor, -6f, y + 22f, 12f, 12f)
                                    pixelRect(style.playerDetailColor, -3f, y + 24f, 6f, 3f)
                                    pixelRect(style.playerDetailColor, -3f, y + 34f, 6f, 4f)
                                    pixelRect(style.obstacleColor, -1f, y + 34f, 2f, 4f)
                                } else {
                                    drawArcadeSprite(MoleMine, style.hazardColor, -20f, y + 2f)
                                    repeat(4) { pixel ->
                                        pixelRect(
                                            style.playerDetailColor,
                                            -7f + pixel * 4f,
                                            y + 14f + pixel * 4f,
                                            3f,
                                            3f
                                        )
                                        pixelRect(
                                            style.playerDetailColor,
                                            5f - pixel * 4f,
                                            y + 14f + pixel * 4f,
                                            3f,
                                            3f
                                        )
                                    }
                                }
                            }
                        }
                        pixelRect(style.obstacleColor.copy(alpha = 0.32f), -22f, 16f, 44f, 5f)
                        pixelRect(style.obstacleColor.copy(alpha = 0.18f), -17f, 21f, 34f, 3f)
                        pixelRect(style.playerColor.copy(alpha = 0.7f), -29f, 10f, 3f, 8f)
                        pixelRect(style.playerColor.copy(alpha = 0.7f), -32f, 8f, 3f, 6f)
                        pixelRect(style.playerColor.copy(alpha = 0.7f), 25f, 8f, 3f, 10f)
                        pixelRect(style.playerColor.copy(alpha = 0.7f), 29f, 5f, 3f, 9f)
                        pixelRect(style.playerDetailColor.copy(alpha = 0.25f), -15f, 17f, 5f, 2f)
                        pixelRect(style.playerDetailColor.copy(alpha = 0.25f), 9f, 19f, 4f, 2f)
                        if (run.hits[index] > 0f) {
                            val length = 8f + (0.3f - run.hits[index]) * 20f
                            val color = style.accentColor.copy(
                                alpha = (run.hits[index] / 0.3f).coerceIn(
                                    0f,
                                    1f
                                )
                            )
                            pixelRect(color, -2f, -19f - length, 4f, length * 2f)
                            pixelRect(color, -length, -21f, length * 2f, 4f)
                            pixelRect(color, -8f, -27f, 16f, 16f)
                        }
                    }
                }
            }
            repeat(minOf(run.combo, 10)) { index ->
                pixelRect(
                    style.accentColor,
                    run.width / 2f + (index - 4.5f) * 9f,
                    run.height - 9f,
                    5f,
                    2f
                )
            }
            run.effects.draw(this, style.accentColor)
            arcadeLives(style, 3 - run.misses, run.topInset / 2f)
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
                effects.burst(hole(index) - Offset(0f, cell * 0.2f))
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