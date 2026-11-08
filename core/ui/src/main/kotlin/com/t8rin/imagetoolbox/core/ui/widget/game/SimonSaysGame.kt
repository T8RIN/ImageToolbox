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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.random.Random

@Composable
fun SimonSaysGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::SimonSaysRun)
    val measurer = rememberTextMeasurer()
    val watch = stringResource(R.string.arcade_watch)
    val repeatPrompt = stringResource(R.string.arcade_repeat)
    ArcadeBoard(
        ArcadeGame.SimonSays, run, style, modifier, initialState, onHighScoreChanged, onGameOver,
        turnBased = true
    ) {
        ArcadeField(run, onPress = run::tap) {
            val side = run.side
            val origin = run.origin
            val cell = side / 2f
            repeat(30) { index ->
                val x = ((index * 71 + index * index * 13 + 19) % 353) / 353f * run.width
                val y = ((index * 97 + index * index * 23 + 41) % 439) / 439f * run.height
                if (x < origin.x - 14f || x > origin.x + side + 14f ||
                    y < origin.y - 36f || y > origin.y + side + 24f
                ) {
                    val color = if (index % 2 == 0) style.playerColor else style.accentColor
                    if (index % 3 == 0) {
                        drawSimonSymbol(
                            index % 4, color.copy(alpha = 0.18f),
                            Offset(x, y), 4f + index % 4 * 2f, 1f
                        )
                    } else {
                        pixelRect(color.copy(alpha = 0.12f), x, y, 2f, 2f)
                    }
                }
            }
            val caption = measurer.measure(
                text = if (run.accepting) repeatPrompt else watch,
                style = style.scoreTextStyle,
                density = Density(1f)
            )
            drawText(
                caption,
                style.textColor,
                Offset((run.width - caption.size.width) / 2f, origin.y - caption.size.height - 8f)
            )
            for (index in 0..3) {
                val color = when (index) {
                    0 -> style.playerColor
                    1 -> style.accentColor
                    2 -> style.obstacleColor
                    else -> style.textColor
                }
                val active = run.lit == index
                val point = origin + Offset(index % 2 * cell + 4f, index / 2 * cell + 4f)
                val pad = Size(cell - 8f, cell - 8f)
                drawRoundRect(
                    color.copy(alpha = if (active) 1f else 0.1f),
                    point,
                    pad,
                    CornerRadius(12f)
                )
                drawRoundRect(
                    color.copy(alpha = if (active || run.accepting) 0.8f else 0.3f),
                    point,
                    pad,
                    CornerRadius(12f),
                    style = Stroke(if (active) 2.5f else 1f)
                )
                val center = point + Offset(pad.width / 2f, pad.height / 2f)
                val ink = if (active) style.playerDetailColor else color
                val r = cell * 0.15f
                drawSimonSymbol(index, ink, center, r, 2.5f)
            }
            val count = minOf(run.sequence.size, 16)
            val progress = if (run.accepting) run.input else run.playback
            repeat(count) { index ->
                val filled = index < (progress.toFloat() / run.sequence.size * count).toInt()
                drawCircle(
                    if (filled) style.accentColor else style.detailColor.copy(alpha = 0.4f), 2.5f,
                    Offset(run.width / 2f + (index - (count - 1) / 2f) * 9f, origin.y + side + 9f)
                )
            }
        }
    }
}

private fun DrawScope.drawSimonSymbol(
    index: Int,
    color: Color,
    center: Offset,
    radius: Float,
    stroke: Float
) {
    when (index) {
        0 -> drawCircle(color, radius, center, style = Stroke(stroke))
        1 -> drawRect(
            color,
            center - Offset(radius, radius),
            Size(radius * 2f, radius * 2f),
            style = Stroke(stroke)
        )

        2 -> {
            drawLine(color, center - Offset(radius, 0f), center + Offset(radius, 0f), stroke)
            drawLine(color, center - Offset(0f, radius), center + Offset(0f, radius), stroke)
        }

        else -> {
            drawLine(
                color,
                center - Offset(radius, radius),
                center + Offset(radius, radius),
                stroke
            )
            drawLine(
                color,
                center + Offset(radius, -radius),
                center + Offset(-radius, radius),
                stroke
            )
        }
    }
}

internal class SimonSaysRun : ArcadeRun() {
    val sequence = mutableListOf<Int>()
    val side get() = minOf(width - 40f, height - topInset - 58f)
    val origin
        get() = Offset(
            (width - side) / 2f,
            topInset + 30f + (height - topInset - 58f - side) / 2f
        )
    var lit = -1
    var accepting = false
    var input = 0
    var playback = 0
    private var timer = 0.75f
    private var gap = true
    private var completed = false

    override fun reset() {
        sequence.clear()
        sequence += Random.nextInt(4)
        lit = -1
        accepting = false
        input = 0
        playback = 0
        timer = 0.75f
        gap = true
        completed = false
        isAnimating = true
    }

    fun tap(point: Offset) {
        if (!accepting) return
        val local = point - origin
        if (local.x !in 0f..<side || local.y !in 0f..<side) return
        val cell = side / 2f
        if (local.x % cell !in 4f..(cell - 4f) || local.y % cell !in 4f..(cell - 4f)) return
        val pad = (local.y / cell).toInt() * 2 + (local.x / cell).toInt()
        lit = pad
        timer = 0.22f
        isAnimating = true
        if (pad != sequence[input]) {
            phase = ArcadePhase.Over
            redraw()
            return
        }
        input++
        if (input == sequence.size) {
            score++
            accepting = false
            playback = 0
            gap = false
            timer = 0.4f
            sequence += Random.nextInt(4)
            completed = true
        }
        redraw()
    }

    override fun advance(seconds: Float) {
        timer -= seconds
        if (timer > 0f) return
        if (accepting) {
            lit = -1
            isAnimating = false
            return
        }
        if (!gap) {
            lit = -1
            // The last input flash belongs to the preceding round, not playback.
            if (completed) {
                completed = false
                input = 0
                timer = 0.6f
            } else {
                playback++
                timer = 0.18f
            }
            gap = true
        } else if (playback == sequence.size) {
            accepting = true
            input = 0
            lit = -1
            isAnimating = false
        } else {
            lit = sequence[playback]
            timer = maxOf(0.28f, 0.55f - score * 0.012f)
            gap = false
        }
    }

    override fun save(state: Bundle) {
        state.putIntArray("sequence", sequence.toIntArray())
        state.putFloatArray(
            "memory",
            floatArrayOf(lit.toFloat(), input.toFloat(), playback.toFloat(), timer)
        )
        state.putBoolean("accepting", accepting)
        state.putBoolean("gap", gap)
        state.putBoolean("completed", completed)
        state.putBoolean("animating", isAnimating)
    }

    override fun restore(state: Bundle) {
        sequence.clear()
        state.getIntArray("sequence")?.let { sequence.addAll(it.toList()) }
        state.getFloatArray("memory")?.let {
            lit = it[0].toInt()
            input = it[1].toInt()
            playback = it[2].toInt()
            timer = it[3]
        }
        accepting = state.getBoolean("accepting")
        gap = state.getBoolean("gap")
        completed = state.getBoolean("completed")
        isAnimating = state.getBoolean("animating")
    }
}