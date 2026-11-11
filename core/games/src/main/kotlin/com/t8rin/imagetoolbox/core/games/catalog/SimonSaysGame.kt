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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import kotlinx.serialization.Serializable
import kotlin.random.Random

@RegisterArcadeGame
internal data object SimonSaysGame : ArcadeGameDefinition {
    override val title = R.string.arcade_simonsays
    override val description = R.string.arcade_simonsays_sub
    override val icon get() = Icons.Outlined.Neurology
    override val order = 16

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::SimonSaysRun
        )
        val measurer = rememberTextMeasurer()
        val watch = stringResource(R.string.arcade_watch)
        val repeatPrompt = stringResource(R.string.arcade_repeat)

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier,
            turnBased = true
        ) {
            ArcadeField(
                run = run,
                onPress = run::tap
            ) {
                val side = run.side
                val origin = run.origin
                val cell = side / 2f
                repeat((run.width * run.height / 4300f).toInt().coerceAtLeast(34)) { index ->
                    val x = ((index * 71 + index * index * 13 + 19) % 353) / 353f * run.width
                    val y = ((index * 97 + index * index * 23 + 41) % 439) / 439f * run.height
                    if (x < origin.x - 14f || x > origin.x + side + 14f ||
                        y < origin.y - 36f || y > origin.y + side + 24f
                    ) {
                        val color = if (index % 2 == 0) style.playerColor else style.accentColor
                        if (index % 3 == 0) {
                            drawSimonSymbol(
                                index = index % 4,
                                color = color.copy(alpha = 0.18f),
                                center = Offset(x = x, y = y),
                                radius = 4f + index % 4 * 2f,
                                stroke = 1f
                            )
                        } else {
                            pixelRect(
                                color = color.copy(alpha = 0.12f),
                                x = x,
                                y = y,
                                width = 2f,
                                height = 2f
                            )
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
                    Offset(
                        x = (run.width - caption.size.width) / 2f,
                        y = origin.y - caption.size.height - 8f
                    )
                )
                for (index in 0..3) {
                    val color = when (index) {
                        0 -> style.playerColor
                        1 -> style.accentColor
                        2 -> style.obstacleColor
                        else -> style.textColor
                    }
                    val active = run.lit == index
                    val point =
                        origin + Offset(x = index % 2 * cell + 4f, y = index / 2 * cell + 4f)
                    val pad = Size(width = cell - 8f, height = cell - 8f)
                    pixelPanel(
                        color = color.copy(alpha = if (active) 1f else if (run.accepting) 0.22f else 0.12f),
                        ink = color,
                        topLeft = point,
                        size = pad,
                        inset = active
                    )
                    val center = point + Offset(x = pad.width / 2f, y = pad.height / 2f)
                    val ink = if (active) style.playerDetailColor else color
                    val r = cell * 0.15f
                    drawSimonSymbol(
                        index = index,
                        color = ink,
                        center = center,
                        radius = r,
                        stroke = 2.5f
                    )
                }
                val count = minOf(run.sequence.size, 16)
                val progress = if (run.accepting) run.input else run.playback
                repeat(count) { index ->
                    val filled = index < (progress.toFloat() / run.sequence.size * count).toInt()
                    pixelCircle(
                        color = if (filled) style.accentColor else style.detailColor.copy(alpha = 0.4f),
                        radius = 2.5f,
                        center = Offset(
                            x = run.width / 2f + (index - (count - 1) / 2f) * 9f,
                            y = origin.y + side + 9f
                        )
                    )
                }
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
        0 -> drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = stroke)
        )

        1 -> drawRect(
            color = color,
            topLeft = center - Offset(x = radius, y = radius),
            size = Size(width = radius * 2f, height = radius * 2f),
            style = Stroke(width = stroke)
        )

        2 -> {
            drawLine(
                color = color,
                start = center - Offset(x = radius, y = 0f),
                end = center + Offset(x = radius, y = 0f),
                strokeWidth = stroke
            )
            drawLine(
                color = color,
                start = center - Offset(x = 0f, y = radius),
                end = center + Offset(x = 0f, y = radius),
                strokeWidth = stroke
            )
        }

        else -> {
            drawLine(
                color = color,
                start = center - Offset(x = radius, y = radius),
                end = center + Offset(x = radius, y = radius),
                strokeWidth = stroke
            )
            drawLine(
                color = color,
                start = center + Offset(x = radius, y = -radius),
                end = center + Offset(x = -radius, y = radius),
                strokeWidth = stroke
            )
        }
    }
}

@Serializable
private class SimonSaysRun : ArcadeRun() {
    val sequence = mutableListOf<Int>()
    val side get() = minOf(width - 40f, height - topInset - 58f)
    val origin
        get() = Offset(
            x = (width - side) / 2f,
            y = topInset + 30f + (height - topInset - 58f - side) / 2f
        )
    var lit = -1
    var accepting = false
    var input = 0
    var playback = 0
    private var timer = 0.75f
    private var gap = true
    private var completed = false

    override fun isValid(): Boolean = super.isValid() &&
            sequence.isNotEmpty() &&
            sequence.all { it in 0..3 } &&
            input in 0..sequence.size &&
            playback in 0..sequence.size

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
}