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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

private val RiverFrog = ArcadeSprite(
    "RiverFrog", 24, 24,
    "M3 1H8V5H16V1H21V8H18V11H22V15H24V21H19V18H16V22H8V18H5V21H0V15H2V11H6V8H3Z"
)

@Composable
fun RiverHopGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::RiverHopRun)
    ArcadeBoard(
        ArcadeGame.RiverHop, run, style, modifier, initialState, onHighScoreChanged, onGameOver
    ) {
        ArcadeField(run, onTap = { run.hop(0, 1) }, onSwipe = run::swipe, swipeOnDrag = true) {
            val cell = run.cell
            val left = run.left
            for (lane in run.lanes) {
                val y = run.screenY(lane.index.toFloat()) - cell / 2f
                if (y < -cell || y > run.height) continue
                val tint = when (lane.kind) {
                    1 -> style.textColor
                    2 -> style.playerColor
                    else -> style.obstacleColor
                }
                pixelRect(
                    tint.copy(alpha = if (lane.kind == 2) 0.14f else 0.06f),
                    0f,
                    y,
                    run.width,
                    cell
                )
                if (lane.kind == 0) {
                    repeat(9) { column ->
                        val x = left + (column + 0.3f) * cell
                        pixelRect(
                            style.obstacleColor.copy(alpha = 0.35f),
                            x,
                            y + cell * 0.7f,
                            2f,
                            4f
                        )
                        pixelRect(
                            style.obstacleColor.copy(alpha = 0.25f),
                            x + 4f,
                            y + cell * 0.75f,
                            2f,
                            3f
                        )
                    }
                    continue
                }
                if (lane.kind == 1) {
                    repeat(9) { column ->
                        pixelRect(style.detailColor, left + column * cell, y + 1f, cell * 0.35f, 1f)
                    }
                } else {
                    repeat(9) { column ->
                        pixelRect(
                            style.playerColor.copy(alpha = 0.2f),
                            left + ((column + run.water * 0.2f) % 9f) * cell,
                            y + cell * 0.2f,
                            7f,
                            1f
                        )
                    }
                }
                var start = lane.offset % lane.period - lane.period
                while (start < 9f) {
                    val x = left + start * cell
                    val length = lane.length * cell
                    if (lane.kind == 2) {
                        pixelRect(
                            style.obstacleColor,
                            x + 2f,
                            y + cell * 0.22f,
                            length - 4f,
                            cell * 0.58f
                        )
                        pixelRect(
                            style.accentColor.copy(alpha = 0.35f),
                            x + 5f,
                            y + cell * 0.28f,
                            length - 10f,
                            2f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = 0.4f),
                            x + 5f,
                            y + cell * 0.45f,
                            length - 14f,
                            1f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = 0.3f),
                            x + 9f,
                            y + cell * 0.65f,
                            length - 18f,
                            1f
                        )
                        pixelRect(
                            style.textColor.copy(alpha = 0.2f),
                            x + 5f,
                            y + cell * 0.3f,
                            2f,
                            cell * 0.38f
                        )
                    } else {
                        val color =
                            if (lane.index % 2 == 0) style.accentColor else style.obstacleColor
                        pixelRect(
                            style.textColor,
                            x + 5f,
                            y + cell * 0.15f,
                            length - 10f,
                            cell * 0.7f
                        )
                        pixelRect(color, x + 1f, y + cell * 0.23f, length - 2f, cell * 0.54f)
                        val front = if (lane.speed > 0f) x + length * 0.65f else x + length * 0.18f
                        pixelRect(
                            style.playerDetailColor,
                            front,
                            y + cell * 0.29f,
                            length * 0.17f,
                            cell * 0.42f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = 0.6f),
                            x + length * 0.4f,
                            y + cell * 0.29f,
                            3f,
                            cell * 0.42f
                        )
                    }
                    start += lane.period
                }
            }
            val point = Offset(left + run.drawX * cell, run.screenY(run.drawRow))
            val lift = sin(run.hopProgress * kotlin.math.PI.toFloat()) * 7f
            drawOval(style.textColor.copy(alpha = 0.18f), point - Offset(9f, -5f), Size(18f, 6f))
            translate(point.x, point.y - lift) {
                rotate(run.facing, Offset.Zero) {
                    scale(cell * 0.68f / 24f, Offset.Zero) {
                        drawArcadeSprite(RiverFrog, style.playerColor, -12f, -12f)
                        pixelRect(style.playerDetailColor, -7f, -8f, 3f, 3f)
                        pixelRect(style.playerDetailColor, 4f, -8f, 3f, 3f)
                        pixelRect(style.playerDetailColor.copy(alpha = 0.4f), -4f, 1f, 8f, 3f)
                    }
                }
            }
            pixelRect(
                style.detailColor.copy(alpha = 0.25f),
                12f,
                run.height - 7f,
                run.width - 24f,
                2f
            )
            pixelRect(
                if (run.remaining < 2f) style.hazardColor else style.accentColor,
                12f, run.height - 7f, (run.width - 24f) * run.remaining / 8f, 2f
            )
        }
    }
}

internal data class RiverLane(
    val index: Int,
    val kind: Int,
    val speed: Float,
    val length: Float,
    val period: Float,
    var offset: Float
)

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
        lanes += RiverLane(0, 0, 0f, 0f, 1f, 0f)
        lanes += RiverLane(1, 0, 0f, 0f, 1f, 0f)
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
            lanes += RiverLane(index, kind, speed, length, period, Random.nextFloat() * period)
        }
        lanes.removeAll { it.index < camera.toInt() - 2 }
    }

    fun swipe(delta: Offset) {
        if (abs(delta.x) > abs(delta.y)) hop(if (delta.x > 0f) 1 else -1, 0)
        else hop(0, if (delta.y < 0f) 1 else -1)
    }

    fun hop(dx: Int, dy: Int) {
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

    override fun save(state: Bundle) {
        state.putFloatArray(
            "frog", floatArrayOf(
                frogX,
                row.toFloat(),
                highest.toFloat(),
                camera,
                remaining,
                facing,
                water,
                hopTime,
                fromX,
                fromRow.toFloat(),
                sectionLeft.toFloat(),
                sectionKind.toFloat()
            )
        )
        state.putFloatArray("lanes", lanes.flatMap {
            listOf(it.index.toFloat(), it.kind.toFloat(), it.speed, it.length, it.period, it.offset)
        }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("frog")?.let {
            frogX = it[0]
            row = it[1].toInt()
            highest = it[2].toInt()
            camera = it[3]
            remaining = it[4]
            facing = it[5]
            water = it[6]
            hopTime = it[7]
            fromX = it[8]
            fromRow = it[9].toInt()
            sectionLeft = it[10].toInt()
            sectionKind = it[11].toInt()
        }
        lanes.clear()
        state.getFloatArray("lanes")?.let {
            for (i in it.indices step 6) lanes += RiverLane(
                it[i].toInt(),
                it[i + 1].toInt(),
                it[i + 2],
                it[i + 3],
                it[i + 4],
                it[i + 5]
            )
        }
    }
}
