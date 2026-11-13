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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.pixelGem
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Water
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object RiverHopGame : ArcadeGameDefinition {
    override val title = R.string.arcade_riverhop
    override val description = R.string.arcade_riverhop_sub
    override val icon get() = Icons.Rounded.Water
    override val order = 14

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::RiverHopRun
        )

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
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
                for ((index, kind, speed, length, period, offset, coin) in run.lanes) {
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
                    if (coin >= 0) pixelGem(
                        color = style.accentColor,
                        light = style.playerDetailColor,
                        center = Offset(left + (coin + 0.5f) * cell, y + cell * 0.45f),
                        radius = cell * 0.15f
                    )
                    if (kind == 3) {
                        val lane = run.lanes.first { it.index == index }
                        for (rail in listOf(0.28f, 0.72f)) pixelRect(
                            style.detailColor, left, y + cell * rail, cell * 9f, 2f
                        )
                        repeat(18) { tie ->
                            pixelRect(
                                style.obstacleColor.copy(alpha = 0.5f), left + tie * cell / 2f,
                                y + cell * 0.18f, 3f, cell * 0.64f
                            )
                        }
                        val warning = lane.trainTime in -1.5f..1.25f
                        for (post in listOf(left + 7f, left + cell * 9f - 7f)) {
                            pixelRect(
                                style.obstacleColor,
                                post - 2f,
                                y + cell * 0.05f,
                                4f,
                                cell * 0.9f
                            )
                            pixelRect(
                                if (warning && (run.water * 6f).toInt() % 2 == 0) style.hazardColor else style.detailColor,
                                post - 4f, y + cell * 0.12f, 8f, 5f
                            )
                        }
                        if (lane.trainTime <= 0f) repeat(5) { carriage ->
                            val x = left + (lane.trainX + carriage * 2.4f) * cell
                            pixelRect(
                                style.obstacleColor,
                                x,
                                y + cell * 0.13f,
                                cell * 2.3f,
                                cell * 0.74f
                            )
                            pixelRect(
                                style.accentColor,
                                x + 3f,
                                y + cell * 0.16f,
                                cell * 2.3f - 6f,
                                cell * 0.12f
                            )
                            repeat(4) { window ->
                                pixelRect(
                                    style.playerDetailColor,
                                    x + cell * (0.18f + window * 0.53f),
                                    y + cell * 0.38f,
                                    cell * 0.32f,
                                    cell * 0.23f
                                )
                            }
                        }
                        continue
                    }
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
                            val top = y + cell * 0.22f
                            val height = cell * 0.58f
                            pixelRect(
                                color = style.accentColor,
                                x = x + 5f,
                                y = top,
                                width = length - 10f,
                                height = height
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = x + 2f,
                                y = top + 3f,
                                width = length - 4f,
                                height = height - 6f
                            )
                            val grain = style.playerDetailColor.copy(alpha = 0.35f)
                            for (end in listOf(x + 5f, x + length - 9f)) {
                                pixelRect(
                                    color = grain,
                                    x = end,
                                    y = top + 3f,
                                    width = 2f,
                                    height = height - 6f
                                )
                                pixelRect(
                                    color = grain,
                                    x = end + 2f,
                                    y = top + 6f,
                                    width = 2f,
                                    height = height - 12f
                                )
                            }
                            pixelRect(
                                color = grain,
                                x = x + 12f,
                                y = top + 4f,
                                width = length * 0.35f,
                                height = 1f
                            )
                            pixelRect(
                                color = grain,
                                x = x + length * 0.45f,
                                y = top + height - 5f,
                                width = length * 0.3f,
                                height = 1f
                            )
                            val knot = x + length * 0.6f
                            pixelRect(
                                color = grain,
                                x = knot,
                                y = top + height / 2f - 2f,
                                width = 6f,
                                height = 1f
                            )
                            pixelRect(
                                color = grain,
                                x = knot - 2f,
                                y = top + height / 2f - 1f,
                                width = 2f,
                                height = 3f
                            )
                            pixelRect(
                                color = grain,
                                x = knot,
                                y = top + height / 2f + 2f,
                                width = 6f,
                                height = 1f
                            )
                        } else {
                            val color =
                                if (index % 2 == 0) style.accentColor else style.obstacleColor
                            val center = Offset(x = x + length / 2f, y = y + cell / 2f)
                            scale(
                                scaleX = if (speed > 0f) 1f else -1f,
                                scaleY = 1f,
                                pivot = center
                            ) {
                                val top = y + cell * 0.22f
                                val height = cell * 0.56f
                                val truck = length > cell * 1.8f
                                val detail = style.playerDetailColor
                                val trim = detail.copy(alpha = 0.25f)
                                val axles =
                                    if (truck) listOf(0.14f, 0.3f, 0.78f) else listOf(0.18f, 0.76f)
                                for (axle in axles) {
                                    pixelRect(
                                        color = style.overlayColor.copy(alpha = 0.65f),
                                        x = x + length * axle,
                                        y = top - 3f,
                                        width = length * 0.1f,
                                        height = height + 6f
                                    )
                                }
                                if (truck) {
                                    val cargo = length * 0.66f
                                    val cab = x + length * 0.72f
                                    pixelRect(
                                        color = color,
                                        x = x + 2f,
                                        y = top,
                                        width = cargo,
                                        height = height
                                    )
                                    pixelRect(
                                        color = color,
                                        x = x + cargo,
                                        y = top + 5f,
                                        width = length * 0.1f,
                                        height = height - 10f
                                    )
                                    pixelRect(
                                        color = color,
                                        x = cab,
                                        y = top + 1f,
                                        width = length * 0.28f - 4f,
                                        height = height - 2f
                                    )
                                    pixelRect(
                                        color = color,
                                        x = cab + 2f,
                                        y = top + 3f,
                                        width = length * 0.28f - 4f,
                                        height = height - 6f
                                    )
                                    for (pane in listOf(top + 3f, top + height / 2f + 1f)) {
                                        pixelRect(
                                            color = detail,
                                            x = x + length * 0.83f,
                                            y = pane,
                                            width = length * 0.09f,
                                            height = height / 2f - 4f
                                        )
                                    }
                                    for (edge in listOf(top + 3f, top + height - 4f)) {
                                        pixelRect(
                                            color = trim,
                                            x = x + 5f,
                                            y = edge,
                                            width = cargo - 6f,
                                            height = 1f
                                        )
                                    }
                                } else {
                                    pixelRect(
                                        color = color,
                                        x = x + 4f,
                                        y = top,
                                        width = length * 0.72f - 4f,
                                        height = height
                                    )
                                    pixelRect(
                                        color = color,
                                        x = x + length * 0.72f,
                                        y = top + 2f,
                                        width = length * 0.28f - 3f,
                                        height = height - 4f
                                    )
                                    pixelRect(
                                        color = color,
                                        x = x + 2f,
                                        y = top + 2f,
                                        width = 2f,
                                        height = height - 4f
                                    )
                                    pixelRect(
                                        color = color,
                                        x = x + length - 3f,
                                        y = top + 4f,
                                        width = 2f,
                                        height = height - 8f
                                    )
                                    pixelRect(
                                        color = detail,
                                        x = x + length * 0.61f,
                                        y = top + 3f,
                                        width = length * 0.14f,
                                        height = height - 6f
                                    )
                                    pixelRect(
                                        color = detail,
                                        x = x + length * 0.23f,
                                        y = top + 4f,
                                        width = length * 0.12f,
                                        height = height - 8f
                                    )
                                    for (mirror in listOf(top - 1f, top + height - 1f)) {
                                        pixelRect(
                                            color = color,
                                            x = x + length * 0.6f,
                                            y = mirror,
                                            width = 3f,
                                            height = 2f
                                        )
                                    }
                                    pixelRect(
                                        color = trim,
                                        x = x + length * 0.79f,
                                        y = top + 4f,
                                        width = 1f,
                                        height = height - 8f
                                    )
                                }
                                for (edge in listOf(top + 2f, top + height - 5f)) {
                                    pixelRect(
                                        color = style.playerDetailColor,
                                        x = x + length - if (truck) 6f else 5f,
                                        y = edge,
                                        width = 2f,
                                        height = 3f
                                    )
                                    pixelRect(
                                        color = style.hazardColor,
                                        x = x + 2f,
                                        y = edge,
                                        width = 2f,
                                        height = 3f
                                    )
                                }
                            }
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
                        scale(scale = cell * 0.68f / 24f, pivot = Offset.Zero) {
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
}

private val RiverFrog = ArcadeSprite(
    width = 24, height = 24,
    data = "M3 1H8V5H16V1H21V8H18V11H22V15H24V21H19V18H16V22H8V18H5V21H0V15H2V11H6V8H3Z"
)

@Serializable
private data class RiverLane(
    val index: Int,
    val kind: Int,
    val speed: Float,
    val length: Float,
    val period: Float,
    var offset: Float,
    var coin: Int = -1,
    var trainTime: Float = 4f
) {
    val trainX get() = if (speed > 0f) -13f - trainTime * 16f else 9f + trainTime * 16f
}

@Serializable
private class RiverHopRun : ArcadeRun() {
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
                sectionKind =
                    if (index >= 15 && Random.nextFloat() < 0.22f) 3 else if (Random.nextBoolean()) 1 else 2
                sectionLeft =
                    if (sectionKind == 3) 1 else if (index < 12) 2 else Random.nextInt(2, 4)
            } else {
                kind = sectionKind
                sectionLeft--
            }
            val speed = (0.65f + Random.nextFloat() * 0.35f + minOf(index * 0.005f, 0.7f)) *
                    if (index % 2 == 0) 1f else -1f
            val length = when {
                kind == 2 -> 2.8f
                kind == 1 && Random.nextInt(until = 4) == 0 -> 2.1f + Random.nextFloat() * 0.5f
                else -> 1.15f + Random.nextFloat() * 0.55f
            }
            val period = length + if (kind == 2) 1.3f else 2.4f + Random.nextFloat()
            lanes += RiverLane(
                index = index,
                kind = kind,
                speed = speed,
                length = length,
                period = period,
                offset = Random.nextFloat() * period,
                coin = if (kind == 0 && index > 1 && index % 2 == 1) Random.nextInt(1, 8) else -1,
                trainTime = 3f + Random.nextFloat() * 4f
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
        for (lane in lanes) if (lane.kind == 3) {
            lane.trainTime -= seconds
            if (lane.trainTime < -1.5f) lane.trainTime = 4f + Random.nextFloat() * 3f
        }
        hopTime = (hopTime - seconds).coerceAtLeast(0f)
        if (hopTime == 0f) {
            val lane = lanes.firstOrNull { it.index == row } ?: return
            if (lane.kind == 2) frogX += lane.speed * seconds
            val local = ((frogX - lane.offset) % lane.period + lane.period) % lane.period
            val collision = when (lane.kind) {
                1 -> local < lane.length + 0.22f || local > lane.period - 0.22f
                2 -> local !in 0.12f..(lane.length - 0.12f)
                3 -> lane.trainTime <= 0f && frogX > lane.trainX - 0.22f && frogX < lane.trainX + 12f + 0.22f
                else -> false
            }
            if (collision || frogX !in 0.15f..8.85f || remaining <= 0f) {
                phase = ArcadePhase.Over
                return
            }
            if (lane.coin >= 0 && abs(frogX - lane.coin - 0.5f) < 0.35f) {
                lane.coin = -1
                score += 5
                remaining = (remaining + 2f).coerceAtMost(8f)
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