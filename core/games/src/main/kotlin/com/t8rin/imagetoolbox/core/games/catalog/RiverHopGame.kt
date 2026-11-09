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

@file:UseSerializers(ArcadeOffsetSerializer::class)

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

private val RiverFrog = ArcadeSprite(
    width = 24, height = 24,
    data = "M3 1H8V5H16V1H21V8H18V11H22V15H24V21H19V18H16V22H8V18H5V21H0V15H2V11H6V8H3Z"
)

@Composable
fun RiverHopGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::RiverHopRun
    )
    ArcadeBoard(
        game = ArcadeGame.RiverHop,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        isPaused = isPaused,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onTap = {
                run.hop(
                    dx = 0,
                    dy = 1
                )
            },
            onSwipe = run::swipe,
            swipeOnDrag = true
        ) {
            val cell = run.cell
            val left = run.left
            for ((index, kind, speed, length, period, offset) in run.lanes) {
                val y = run.screenY(index.toFloat()) - cell / 2f
                if (y < -cell || y > run.height) continue
                val tint = when (kind) {
                    1 -> style.textColor
                    2 -> style.playerColor
                    else -> style.obstacleColor
                }
                pixelRect(
                    color = tint.copy(
                        alpha = when (kind) {
                            0 -> 0.22f
                            2 -> 0.18f
                            else -> 0.035f
                        }
                    ),
                    x = 0f,
                    y = y,
                    width = run.width,
                    height = cell
                )
                if (kind == 0) {
                    pixelRect(
                        color = style.obstacleColor.copy(alpha = 0.45f),
                        x = 0f,
                        y = y,
                        width = run.width,
                        height = 2f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = 0.18f),
                        x = 0f,
                        y = y + cell - 3f,
                        width = run.width,
                        height = 3f
                    )
                    repeat(9) { column ->
                        val x = left + (column + 0.3f) * cell
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.7f),
                            x = x,
                            y = y + cell * 0.7f,
                            width = 2f,
                            height = 4f
                        )
                        pixelRect(
                            color = style.obstacleColor.copy(alpha = 0.5f),
                            x = x + 4f,
                            y = y + cell * 0.75f,
                            width = 2f,
                            height = 3f
                        )
                    }
                    continue
                }
                if (kind == 1) {
                    repeat(9) { column ->
                        pixelRect(
                            color = style.textColor.copy(alpha = 0.3f),
                            x = left + column * cell,
                            y = y + 1f,
                            width = cell * 0.4f,
                            height = 2f
                        )
                    }
                } else {
                    repeat(18) { index ->
                        val x =
                            left + ((index / 2f + run.water * speed * 0.15f + 90f) % 9f) * cell
                        val waveY = y + cell * if (index % 2 == 0) 0.12f else 0.86f
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.4f),
                            x = x,
                            y = waveY,
                            width = 8f,
                            height = 1f
                        )
                        pixelRect(
                            color = style.playerColor.copy(alpha = 0.25f),
                            x = x + 8f,
                            y = waveY - 1f,
                            width = 4f,
                            height = 1f
                        )
                    }
                }
                var start = offset % period - period
                while (start < 9f) {
                    val x = left + start * cell
                    val length = length * cell
                    if (kind == 2) {
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = x + 2f,
                            y = y + cell * 0.25f,
                            width = length - 4f,
                            height = cell * 0.62f
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = x + 2f,
                            y = y + cell * 0.22f,
                            width = length - 4f,
                            height = cell * 0.58f
                        )
                        pixelRect(
                            color = style.obstacleColor,
                            x = x + 5f,
                            y = y + cell * 0.28f,
                            width = length - 10f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = x + 5f,
                            y = y + cell * 0.45f,
                            width = length - 14f,
                            height = 1f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.3f),
                            x = x + 9f,
                            y = y + cell * 0.65f,
                            width = length - 18f,
                            height = 1f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.45f),
                            x = x + 5f,
                            y = y + cell * 0.3f,
                            width = 2f,
                            height = cell * 0.38f
                        )
                    } else {
                        val color =
                            if (index % 2 == 0) style.accentColor else style.obstacleColor
                        pixelRect(
                            color = style.textColor,
                            x = x + 5f,
                            y = y + cell * 0.15f,
                            width = length - 10f,
                            height = cell * 0.7f
                        )
                        pixelRect(
                            color = color,
                            x = x + 1f,
                            y = y + cell * 0.23f,
                            width = length - 2f,
                            height = cell * 0.54f
                        )
                        val headlightX = if (speed > 0f) x + length - 3f else x + 1f
                        val tailLightX = if (speed > 0f) x + 1f else x + length - 3f
                        repeat(2) { index ->
                            val edge = if (index == 0) 0.3f else 0.63f
                            pixelRect(
                                color = style.textColor,
                                x = headlightX,
                                y = y + cell * edge,
                                width = 2f,
                                height = 3f
                            )
                            pixelRect(
                                color = style.hazardColor,
                                x = tailLightX,
                                y = y + cell * edge,
                                width = 2f,
                                height = 3f
                            )
                        }
                        val front = if (speed > 0f) x + length * 0.65f else x + length * 0.18f
                        pixelRect(
                            color = style.playerDetailColor,
                            x = front,
                            y = y + cell * 0.29f,
                            width = length * 0.17f,
                            height = cell * 0.42f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.6f),
                            x = x + length * 0.4f,
                            y = y + cell * 0.29f,
                            width = 3f,
                            height = cell * 0.42f
                        )
                    }
                    start += period
                }
            }
            val point = Offset(x = left + run.drawX * cell, y = run.screenY(run.drawRow))
            val lift =
                sin(run.hopProgress * kotlin.math.PI.toFloat()) * 7f
            drawOval(
                color = style.textColor.copy(alpha = 0.18f),
                topLeft = point - Offset(x = 9f, y = -5f),
                size = Size(width = 18f, height = 6f)
            )
            translate(left = point.x, top = point.y - lift) {
                rotate(degrees = run.facing, pivot = Offset.Zero) {
                    scale(cell * 0.68f / 24f, Offset.Zero) {
                        repeat(4) { index ->
                            val offset = when (index) {
                                0 -> Offset(x = -1.5f, y = 0f)
                                1 -> Offset(x = 1.5f, y = 0f)
                                2 -> Offset(x = 0f, y = -1.5f)
                                else -> Offset(x = 0f, y = 1.5f)
                            }
                            drawArcadeSprite(
                                sprite = RiverFrog,
                                tint = style.playerDetailColor,
                                x = -12f + offset.x,
                                y = -12f + offset.y
                            )
                        }
                        drawArcadeSprite(
                            sprite = RiverFrog,
                            tint = style.playerColor,
                            x = -12f,
                            y = -12f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = -7f,
                            y = -8f,
                            width = 3f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = 4f,
                            y = -8f,
                            width = 3f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = -4f,
                            y = 1f,
                            width = 8f,
                            height = 3f
                        )
                    }
                }
            }
            pixelRect(
                color = style.detailColor.copy(alpha = 0.25f),
                x = 12f,
                y = run.height - 7f,
                width = run.width - 24f,
                height = 2f
            )
            pixelRect(
                color = if (run.remaining < 2f) style.hazardColor else style.accentColor,
                x = 12f,
                y = run.height - 7f,
                width = (run.width - 24f) * run.remaining / 8f,
                height = 2f
            )
        }
    }
}

@Serializable
internal data class RiverLane(
    val index: Int,
    val kind: Int,
    val speed: Float,
    val length: Float,
    val period: Float,
    var offset: Float
)

@Serializable
internal class RiverHopRun : ArcadeRun() {
    val lanes = mutableListOf<RiverLane>()
    val cell get() = minOf(width / 9f, (height - topInset - 12f) / 5f)
    val left get() = (width - cell * 9f) / 2f
    var frogX = 4.5f
    var row = 0
    var highest = 0
    var camera = 0f
    var remaining = 8f
    var facing = 0f
    var water = 0f
    private var hopTime = 0f
    private var fromX = frogX
    private var fromRow = 0
    private var sectionLeft = 0
    private var sectionKind = 1
    val hopProgress get() = if (hopTime <= 0f) 0f else 1f - hopTime / 0.12f
    val drawX get() = if (hopTime <= 0f) frogX else fromX + (frogX - fromX) * hopProgress
    val drawRow get() = if (hopTime <= 0f) row.toFloat() else fromRow + (row - fromRow) * hopProgress

    fun screenY(row: Float) = height - cell * 1.5f - (row - camera) * cell

    override fun isValid(): Boolean = super.isValid() &&
            lanes.isNotEmpty() && lanes.all { it.period > 0f }

    override fun reset() {
        lanes.clear()
        frogX = 4.5f
        row = 0
        highest = 0
        camera = 0f
        remaining = 8f
        facing = 0f
        water = 0f
        hopTime = 0f
        fromX = frogX
        fromRow = 0
        sectionLeft = 0
        sectionKind = 1
        lanes += RiverLane(index = 0, kind = 0, speed = 0f, length = 0f, period = 1f, offset = 0f)
        lanes += RiverLane(index = 1, kind = 0, speed = 0f, length = 0f, period = 1f, offset = 0f)
        populate()
    }

    private fun populate() {
        val end = highest + ceil(height / cell).toInt() + 2
        while (lanes.last().index < end) {
            val index = lanes.last().index + 1
            val kind: Int
            if (sectionLeft == 0) {
                kind = 0
                sectionKind = if (Random.nextBoolean()) 1 else 2
                sectionLeft = if (index < 12) 2 else Random.nextInt(2, 4)
            } else {
                kind = sectionKind
                sectionLeft--
            }
            val speed = (0.65f + Random.nextFloat() * 0.35f + minOf(index * 0.005f, 0.7f)) *
                    if (index % 2 == 0) 1f else -1f
            val length = if (kind == 2) 2.8f else 1.15f + Random.nextFloat() * 0.55f
            val period = length + if (kind == 2) 1.3f else 2.4f + Random.nextFloat()
            lanes += RiverLane(
                index = index,
                kind = kind,
                speed = speed,
                length = length,
                period = period,
                offset = Random.nextFloat() * period
            )
        }
        lanes.removeAll { it.index < camera.toInt() - 2 }
    }

    fun swipe(delta: Offset) {
        if (abs(delta.x) > abs(delta.y)) hop(
            dx = if (delta.x > 0f) 1 else -1,
            dy = 0
        )
        else hop(
            dx = 0,
            dy = if (delta.y < 0f) 1 else -1
        )
    }

    fun hop(
        dx: Int,
        dy: Int
    ) {
        if (hopTime > 0f || row + dy < maxOf(0, camera.toInt() - 1)) return
        val nextX = frogX + dx
        if (nextX !in 0.35f..8.65f) return
        fromX = frogX
        fromRow = row
        frogX = nextX
        row += dy
        facing = when {
            dx > 0 -> 90f
            dx < 0 -> -90f
            dy < 0 -> 180f
            else -> 0f
        }
        hopTime = 0.12f
    }

    override fun advance(seconds: Float) {
        water = (water + seconds) % 45f
        remaining -= seconds
        for (lane in lanes) lane.offset =
            (lane.offset + lane.speed * seconds + lane.period) % lane.period
        hopTime = (hopTime - seconds).coerceAtLeast(0f)
        if (hopTime == 0f) {
            val lane = lanes.firstOrNull { it.index == row } ?: return
            if (lane.kind == 2) frogX += lane.speed * seconds
            val local = ((frogX - lane.offset) % lane.period + lane.period) % lane.period
            val collision = when (lane.kind) {
                1 -> local < lane.length + 0.22f || local > lane.period - 0.22f
                2 -> local !in 0.12f..(lane.length - 0.12f)
                else -> false
            }
            if (collision || frogX !in 0.15f..8.85f || remaining <= 0f) {
                phase = ArcadePhase.Over
                return
            }
            if (row > highest) {
                score += row - highest
                highest = row
                remaining = 8f
            }
        }
        camera += (maxOf(0f, highest - 1f) - camera).coerceIn(0f, seconds * 7f)
        populate()
    }

    override fun onResize(previous: Size) {
        if (lanes.isNotEmpty()) populate()
    }
}