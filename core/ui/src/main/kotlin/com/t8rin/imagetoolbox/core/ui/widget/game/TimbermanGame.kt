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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.ceil
import kotlin.random.Random

@Composable
fun TimbermanGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState = initialState, create = ::TimbermanRun)
    ArcadeBoard(
        game = ArcadeGame.Timberman,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(run = run, onPress = { run.chop(if (it.x < run.width / 2f) -1 else 1) }) {
            val center = run.width / 2f
            val base = run.base
            val segment = run.segment
            clipRect(top = 0f, bottom = run.height) {
                repeat(9) { tree ->
                    val x = (tree * 71f + 17f) % run.width
                    val height = 65f + tree % 4 * 23f
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.13f),
                        x = x,
                        y = base - height,
                        width = 7f,
                        height = height
                    )
                    repeat(4) { tier ->
                        val w = 42f - tier * 8f
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.1f),
                            x = x + 3f - w / 2f,
                            y = base - height - tier * 9f,
                            width = w,
                            height = 12f
                        )
                    }
                }
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
                for (chip in run.chips) {
                    rotate(chip.spin * chip.age, Offset(chip.x, chip.y)) {
                        pixelRect(
                            color = style.obstacleColor,
                            x = chip.x - 14f,
                            y = chip.y - 8f,
                            width = 28f,
                            height = 16f
                        )
                        pixelRect(
                            color = style.accentColor.copy(alpha = 0.7f),
                            x = chip.x - 11f,
                            y = chip.y - 6f,
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
            pixelRect(
                color = style.obstacleColor,
                x = px - 9f,
                y = foot - 9f,
                width = 6f,
                height = 10f
            )
            pixelRect(
                color = style.obstacleColor,
                x = px + 3f,
                y = foot - 9f,
                width = 6f,
                height = 10f
            )
            pixelRect(
                color = style.playerColor,
                x = px - 10f,
                y = foot - 27f + kick,
                width = 20f,
                height = 19f - kick
            )
            pixelRect(
                color = style.playerDetailColor.copy(alpha = 0.4f),
                x = px - 1f,
                y = foot - 25f + kick,
                width = 3f,
                height = 15f
            )
            pixelRect(
                color = style.accentColor,
                x = px - 8f,
                y = foot - 41f + kick,
                width = 16f,
                height = 14f
            )
            pixelRect(
                color = style.playerColor,
                x = px - 10f,
                y = foot - 44f + kick,
                width = 20f,
                height = 6f
            )
            pixelRect(
                color = style.playerDetailColor,
                x = px - run.side * 4f - 1f,
                y = foot - 36f + kick,
                width = 3f,
                height = 3f
            )
            val hand = Offset(px - run.side * 9f, foot - 22f + kick)
            rotate(run.side * (65f - run.cut * 100f), hand) {
                pixelRect(
                    color = style.accentColor,
                    x = hand.x - 2f,
                    y = hand.y - 20f,
                    width = 4f,
                    height = 24f
                )
                pixelRect(
                    color = style.obstacleColor,
                    x = hand.x - 8f,
                    y = hand.y - 23f,
                    width = 15f,
                    height = 10f
                )
                pixelRect(
                    color = style.playerDetailColor,
                    x = hand.x - 8f,
                    y = hand.y - 23f,
                    width = 3f,
                    height = 10f
                )
            }
            repeat(17) {
                val x = (it * 37f + 9f) % run.width
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
            val clock = Offset(22f, run.topInset / 2f)
            drawCircle(
                color = color,
                radius = 6f,
                center = clock,
                style = androidx.compose.ui.graphics.drawscope.Stroke(1.5f)
            )
            drawLine(
                color = color,
                start = clock,
                end = clock + Offset(0f, -4f),
                strokeWidth = 1.5f
            )
            drawLine(
                color = color,
                start = clock,
                end = clock + Offset(3f, 1f),
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

internal data class TimberChip(
    var x: Float,
    var y: Float,
    val vx: Float,
    var vy: Float,
    val spin: Float,
    var age: Float = 0f
)

internal class TimbermanRun : ArcadeRun() {
    val branches = mutableListOf<Int>()
    val chips = mutableListOf<TimberChip>()
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
        chips += TimberChip(width / 2f, base - segment / 2f, -side * 170f, -90f, side * 300f)
        if (chips.size > 6) chips.removeAt(0)
        effects.burst(
            point = Offset(width / 2f - side * 18f, base - segment / 2f),
            count = 7
        )
        cut = 1f
        chops++
        chain = if (sinceChop < 0.45f) chain + 1 else 1
        score += 1 + if (chain % 10 == 0) 5 else 0
        sinceChop = 0f
        energy = (energy + 0.065f).coerceAtMost(1f)
        if (branches.first() == side) {
            cut = 0f
            phase = ArcadePhase.Over
        }
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        cut = (cut - seconds * 9f).coerceAtLeast(0f)
        sinceChop += seconds
        if (begun) {
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

    override fun save(state: Bundle) {
        state.putIntArray("branches", branches.toIntArray())
        state.putIntArray("run", intArrayOf(side, chops, chain))
        state.putFloatArray("time", floatArrayOf(energy, cut, sinceChop))
        state.putBoolean("begun", begun)
    }

    override fun restore(state: Bundle) {
        branches.clear()
        state.getIntArray("branches")?.forEach(branches::add)
        state.getIntArray("run")?.let { side = it[0]; chops = it[1]; chain = it[2] }
        state.getFloatArray("time")?.let { energy = it[0]; cut = it[1]; sinceChop = it[2] }
        begun = state.getBoolean("begun")
        chips.clear()
    }
}