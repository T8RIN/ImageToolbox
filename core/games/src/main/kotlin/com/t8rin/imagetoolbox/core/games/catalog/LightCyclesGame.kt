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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
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
import com.t8rin.imagetoolbox.core.resources.icons.TwoWheeler
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object LightCyclesGame : ArcadeGameDefinition {
    override val title = R.string.arcade_lightcycles
    override val description = R.string.arcade_lightcycles_sub
    override val icon get() = Icons.Outlined.TwoWheeler
    override val order = 29

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::LightCyclesRun
        )
        val boltPath = remember { Path() }
        val beamPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onTap = { run.turn(if (it.x < run.width / 2f) -1 else 1) },
                onSwipe = run::steer,
                swipeOnDrag = true
            ) {
                drawLightCyclesBackground(style, run.width, run.height)
                val cell = run.cell
                val origin = run.origin
                val area = Size(width = run.columns * cell, height = run.rows * cell)
                val outline =
                    Path().apply {
                        addRoundRect(
                            RoundRect(
                                Rect(origin, area),
                                CornerRadius(x = 10f)
                            )
                        )
                    }
                clipPath(outline) {
                    drawRect(
                        color = style.detailColor.copy(alpha = 0.035f),
                        topLeft = origin,
                        size = area
                    )
                    for (index in run.board.indices) {
                        val point =
                            origin + Offset(
                                x = index % run.columns * cell,
                                y = index / run.columns * cell
                            )
                        val x = index % run.columns
                        val y = index / run.columns
                        if (run.board[index] < 0 && x in 1 until run.columns - 1 && y in 1 until run.rows - 1) {
                            pixelRect(
                                color = style.obstacleColor.copy(alpha = 0.12f),
                                x = point.x + 1f,
                                y = point.y + 1f,
                                width = cell - 2f,
                                height = cell - 2f
                            )
                            val edge = cell * 0.3f
                            for (dx in listOf(1f, cell - edge - 1f)) {
                                pixelRect(
                                    color = style.obstacleColor.copy(alpha = 0.7f),
                                    x = point.x + dx,
                                    y = point.y + 1f,
                                    width = edge,
                                    height = 1f
                                )
                                pixelRect(
                                    color = style.obstacleColor.copy(alpha = 0.7f),
                                    x = point.x + dx,
                                    y = point.y + cell - 2f,
                                    width = edge,
                                    height = 1f
                                )
                            }
                            pixelRect(
                                color = style.obstacleColor.copy(alpha = 0.7f),
                                x = point.x + 1f,
                                y = point.y + 1f,
                                width = 1f,
                                height = cell - 2f
                            )
                            pixelRect(
                                color = style.obstacleColor.copy(alpha = 0.7f),
                                x = point.x + cell - 2f,
                                y = point.y + 1f,
                                width = 1f,
                                height = cell - 2f
                            )
                        } else if (index % run.columns % 2 == 0 && index / run.columns % 2 == 0) {
                            pixelRect(
                                color = style.detailColor.copy(alpha = 0.25f),
                                x = point.x,
                                y = point.y,
                                width = 1f,
                                height = 1f
                            )
                        }
                    }
                    for (pickup in run.pickups) {
                        val point = run.center(pickup)
                        val r =
                            cell * (0.48f + sin(run.time * 5f) * 0.06f)
                        drawCircle(
                            color = style.accentColor.copy(alpha = 0.15f),
                            radius = r,
                            center = point
                        )
                        val bolt = boltPath.apply {
                            rewind()
                            moveTo(point.x + cell * 0.1f, point.y - cell * 0.38f)
                            lineTo(point.x - cell * 0.25f, point.y + cell * 0.06f)
                            lineTo(point.x, point.y + cell * 0.06f)
                            lineTo(point.x - cell * 0.1f, point.y + cell * 0.38f)
                            lineTo(point.x + cell * 0.25f, point.y - cell * 0.06f)
                            lineTo(point.x, point.y - cell * 0.06f)
                            close()
                        }
                        drawPath(
                            path = bolt,
                            color = style.accentColor
                        )
                    }
                    run.cycles.forEachIndexed { index, cycle ->
                        val color = when (index) {
                            0 -> style.playerColor
                            1 -> style.hazardColor
                            2 -> style.accentColor
                            else -> style.obstacleColor
                        }
                        val ink = color.copy(alpha = if (cycle.alive) 1f else 0.45f)
                        translate(left = origin.x, top = origin.y) {
                            scale(scale = cell, pivot = Offset.Zero) {
                                drawPath(
                                    path = cycle.path,
                                    color = ink.copy(alpha = 0.16f),
                                    style = Stroke(width = 0.8f)
                                )
                                drawPath(
                                    path = cycle.path,
                                    color = ink,
                                    style = Stroke(width = 0.28f)
                                )
                            }
                        }
                        val point = run.center(cycle.previous) +
                                (run.center(cycle.position) - run.center(cycle.previous)) * run.progress
                        drawLine(
                            color = ink,
                            start = run.center(cycle.previous),
                            end = point,
                            strokeWidth = cell * 0.28f
                        )
                        if (cycle.alive) rotate(degrees = cycle.direction * 90f, pivot = point) {
                            val beam = beamPath.apply {
                                rewind()
                                moveTo(point.x + cell * 0.3f, point.y)
                                lineTo(point.x + cell * 1.8f, point.y - cell * 0.6f)
                                lineTo(point.x + cell * 1.8f, point.y + cell * 0.6f)
                                close()
                            }
                            drawPath(
                                path = beam,
                                color = ink.copy(alpha = 0.1f)
                            )
                            if (cycle.charged) drawCircle(
                                color = style.accentColor,
                                radius = cell * 0.6f,
                                center = point,
                                style = Stroke(width = 1f)
                            )
                            if (cycle.phaseTime > 0f) drawArc(
                                color = style.accentColor,
                                startAngle = -90f - cycle.direction * 90f,
                                sweepAngle = 360f * cycle.phaseTime / 1.6f,
                                useCenter = false,
                                topLeft = point - Offset(x = cell * 0.7f, y = cell * 0.7f),
                                size = Size(width = cell * 1.4f, height = cell * 1.4f),
                                style = Stroke(width = 2f)
                            )
                            scale(scale = cell * 0.95f / 24f, pivot = point) {
                                drawArcadeSprite(
                                    sprite = LightCycleBody,
                                    tint = ink,
                                    x = point.x - 12f,
                                    y = point.y - 8f
                                )
                                drawArcadeSprite(
                                    sprite = LightCycleRider,
                                    tint = style.playerDetailColor,
                                    x = point.x - 12f,
                                    y = point.y - 8f
                                )
                            }
                        }
                    }
                }
                val inner = Rect(
                    origin + Offset(x = cell, y = cell),
                    Size(width = area.width - cell * 2f, height = area.height - cell * 2f)
                )
                drawRoundRect(
                    color = style.playerColor.copy(alpha = 0.35f),
                    topLeft = inner.topLeft,
                    size = inner.size,
                    cornerRadius = CornerRadius(x = 5f),
                    style = Stroke(width = 1f)
                )
                for (corner in listOf(
                    inner.topLeft,
                    inner.topRight,
                    inner.bottomLeft,
                    inner.bottomRight
                )) {
                    val dx = if (corner.x == inner.left) 1f else -1f
                    val dy = if (corner.y == inner.top) 1f else -1f
                    drawLine(
                        color = style.accentColor.copy(alpha = 0.8f),
                        start = corner,
                        end = corner + Offset(x = dx * 12f, y = 0f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = style.accentColor.copy(alpha = 0.8f),
                        start = corner,
                        end = corner + Offset(x = 0f, y = dy * 12f),
                        strokeWidth = 2f
                    )
                }
                drawRoundRect(
                    color = style.detailColor.copy(alpha = 0.2f),
                    topLeft = origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 10f),
                    style = Stroke(width = 1f)
                )
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private val LightCycleBody = ArcadeSprite(
    width = 24,
    height = 16,
    data = "M2 4H7V2H15V4H19V5H22V6H24V10H22V11H19V12H15V14H7V12H2V10H0V6H2Z M2 6H6V10H2Z M19 7H22V9H19Z"
)

private val LightCycleRider = ArcadeSprite(
    width = 24,
    height = 16,
    data = "M9 5H14V6H17V10H14V11H9Z"
)

private fun DrawScope.drawLightCyclesBackground(style: ArcadeStyle, width: Float, height: Float) {
    val ink = style.detailColor.copy(alpha = 0.065f)
    var x = 0f
    while (x < width) {
        drawLine(ink, Offset(x, 0f), Offset(x, height), 0.6f)
        x += 32f
    }
    var y = 12f
    while (y < height) {
        drawLine(ink, Offset(0f, y), Offset(width, y), 0.6f)
        y += 32f
    }
    val count = ceil(width / 129f).toInt()
    for (index in 0 until count) {
        val x = index * 129f + 31f
        val color = style.detailColor.copy(alpha = 0.12f)
        for (y in listOf(18f, height - 21f)) {
            pixelRect(color, x, y, 19f, 1f)
            pixelRect(color, x, y - 3f, 1f, 7f)
            pixelRect(color, x + 18f, y - 3f, 1f, 7f)
        }
    }
}

@Serializable
private class LightCycle(
    var position: Int,
    var previous: Int,
    var direction: Int,
    var alive: Boolean = true,
    var charged: Boolean = false,
    var phaseTime: Float = 0f
) {
    val trail = mutableListOf<Int>()

    @Transient
    val path = Path()
}

@Serializable
private class LightCyclesRun : ArcadeRun() {
    val columns = 25
    var rows = 15
    var board = IntArray(columns * rows)
    val cycles = mutableListOf<LightCycle>()
    val pickups = mutableSetOf<Int>()

    @Transient
    val effects = ArcadeEffects()
    var round = 1
    var time = 0f
    private var elapsed = 0f
    var transition = 0f
        private set
    private var steps = 0
    private val turns: ArcadeDeque<Int> = ArrayDeque()

    @Transient
    private var visited = IntArray(board.size)

    @Transient
    private val queue = IntArray(100)

    @Transient
    private var visitId = 0
    private val interval get() = 0.145f - 0.055f * (round - 1) / (round + 7f)
    val progress get() = (elapsed / interval).coerceIn(0f, 1f)
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 48f) / rows)
    val origin
        get() = Offset(
            x = (width - columns * cell) / 2f,
            y = topInset + (height - topInset - rows * cell) / 2f
        )

    fun center(index: Int) =
        origin + Offset(x = (index % columns + 0.5f) * cell, y = (index / columns + 0.5f) * cell)

    override fun isValid(): Boolean = super.isValid() &&
            rows in 11..35 &&
            board.size == columns * rows &&
            cycles.isNotEmpty() &&
            cycles.all {
                it.position in board.indices &&
                        it.previous in board.indices &&
                        it.trail.all { point -> point in board.indices }
            }

    override fun reset() {
        round = 1
        time = 0f
        steps = 0
        effects.clear()
        newRound()
    }

    private fun newRound() {
        animateLevel()
        rows = ((height - topInset - 48f) / ((width - 48f) / columns)).toInt().coerceIn(11, 35)
        board = IntArray(columns * rows) {
            if (it % columns == 0 || it % columns == columns - 1 ||
                it / columns == 0 || it / columns == rows - 1
            ) -1 else 0
        }
        if (round > 1) repeat((1 + round / 3).coerceAtMost(4)) { pillar ->
            val x = 5 + (pillar * 5 + round * 3) % (columns - 10)
            val y = 4 + (pillar * 7 + round) % (rows - 8).coerceAtLeast(1)
            val shape = when (round % 4) {
                1 -> listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
                2 -> listOf(0 to 0, 0 to 1, 0 to 2)
                3 -> listOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2)
                else -> listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1)
            }
            val starts = listOf(
                (rows - 3) * columns + columns / 4,
                2 * columns + columns * 3 / 4,
                rows / 2 * columns + columns - 3,
                rows / 2 * columns + 2
            )
            for ((dx, dy) in shape) {
                val px = x + dx
                val py = y + dy
                if (px in 1 until columns - 1 && py in 1 until rows - 1 && starts.none {
                        abs(it % columns - px) + abs(it / columns - py) <= 3
                    }) board[py * columns + px] = -1
            }
        }
        cycles.clear()
        val starts = listOf(
            Triple((rows - 3) * columns + columns / 4, 0, true),
            Triple(2 * columns + columns * 3 / 4, 2, true),
            Triple(rows / 2 * columns + columns - 3, 3, round >= 5),
            Triple(rows / 2 * columns + 2, 1, round >= 10)
        )
        for ((position, direction, enabled) in starts) if (enabled) {
            val cycle = LightCycle(position = position, previous = position, direction = direction)
            cycle.trail += position
            cycle.path.moveTo(position % columns + 0.5f, position / columns + 0.5f)
            cycles += cycle
            board[position] = cycles.size
        }
        pickups.clear()
        val first = cycles.first().position + 4
        if (board[first] == 0) pickups += first
        spawnPickup()
        elapsed = 0f
        transition = 0f
        turns.clear()
        visited = IntArray(board.size)
        visitId = 0
    }

    private fun spawnPickup() {
        if (pickups.size >= 2) return
        board.indices.filter { index ->
            board[index] == 0 && index !in pickups && cycles.none {
                abs(it.position % columns - index % columns) + abs(it.position / columns - index / columns) < 3
            }
        }.randomOrNull()?.let(pickups::add)
    }

    fun steer(delta: Offset) {
        val direction = if (abs(delta.x) > abs(delta.y)) {
            if (delta.x > 0f) 0 else 2
        } else if (delta.y > 0f) 1 else 3
        queueTurn(direction)
    }

    fun turn(amount: Int) {
        val current = turns.lastOrNull() ?: cycles.first().direction
        queueTurn((current + amount + 4) % 4)
    }

    private fun queueTurn(direction: Int) {
        if (transition > 0f || turns.size >= 2) return
        val current = turns.lastOrNull() ?: cycles.first().direction
        if (direction != current && direction != (current + 2) % 4) turns.addLast(direction)
    }

    private fun next(
        position: Int,
        direction: Int
    ): Int {
        val x = position % columns + when (direction) {
            0 -> 1
            2 -> -1
            else -> 0
        }
        val y = position / columns + when (direction) {
            1 -> 1
            3 -> -1
            else -> 0
        }
        return if (x in 0 until columns && y in 0 until rows) y * columns + x else -1
    }

    private fun space(start: Int): Int {
        visitId++
        var head = 0
        var tail = 1
        queue[0] = start
        visited[start] = visitId
        while (head < tail && tail < queue.size) {
            val position = queue[head++]
            for (direction in 0..3) {
                val other = next(position, direction)
                if (other >= 0 && board[other] == 0 && visited[other] != visitId) {
                    visited[other] = visitId
                    queue[tail++] = other
                    if (tail == queue.size) break
                }
            }
        }
        return tail
    }

    private fun choose(cycle: LightCycle): Int {
        val options = (0..3).filter { direction ->
            val position = next(cycle.position, direction)
            direction != (cycle.direction + 2) % 4 && position >= 0 &&
                    (board[position] == 0 || (cycle.charged || cycle.phaseTime > 0f) &&
                            board[position] > 0 && cycles.none { it.alive && it.position == position })
        }
        if (options.isEmpty()) return cycle.direction
        if (Random.nextFloat() < (0.18f - round * 0.01f).coerceAtLeast(0.04f)) return options.random()
        return options.maxBy { direction ->
            val destination = next(cycle.position, direction)
            var distance = 0
            var position = destination
            while (position >= 0 && board[position] == 0 && distance < 12) {
                distance++
                position = next(position, direction)
            }
            space(destination) * 2f + distance +
                    (if (direction == cycle.direction) 1.5f else 0f) +
                    (if (!cycle.charged && destination in pickups) 18f else 0f)
        }
    }

    fun step() {
        if (turns.isNotEmpty()) cycles.first().direction = turns.removeFirst()
        cycles.drop(1).filter { it.alive }.forEach { it.direction = choose(it) }
        val destinations = cycles.map { if (it.alive) next(it.position, it.direction) else -1 }
        val heads = cycles.filter { it.alive }.map { it.position }.toSet()
        for ((index, cycle) in cycles.withIndex()) {
            if (!cycle.alive) continue
            val destination = destinations[index]
            cycle.path.lineTo(cycle.position % columns + 0.5f, cycle.position / columns + 0.5f)
            cycle.previous = cycle.position
            val headOn =
                destinations.count { it == destination } > 1 || (destination in heads && destination != cycle.position)
            val trail = destination >= 0 && board[destination] > 0
            val absorb = (cycle.charged || cycle.phaseTime > 0f) && trail && !headOn
            if (absorb && cycle.phaseTime <= 0f) {
                cycle.charged = false
                cycle.phaseTime = 1.6f
                effects.burst(
                    point = center(destination),
                    count = 14
                )
            }
            if (destination < 0 || board[destination] != 0 && !absorb || headOn) {
                cycle.alive = false
                effects.burst(
                    point = center(cycle.position),
                    count = 16
                )
                if (index > 0) score += 25
            }
        }
        for ((index, cycle) in cycles.withIndex()) if (cycle.alive) {
            cycle.position = destinations[index]
            board[cycle.position] = index + 1
            cycle.trail += cycle.position
            if (pickups.remove(cycle.position)) {
                cycle.charged = true
                effects.burst(
                    point = center(cycle.position),
                    count = 8
                )
                if (index == 0) score += 10
            }
        }
        if (!cycles.first().alive) phase = ArcadePhase.Over
        else if (cycles.drop(1).none { it.alive }) {
            score += round * 100
            transition = 1f
        }
        if (phase == ArcadePhase.Over || transition > 0f) elapsed = interval
        steps++
        if (steps % 18 == 0) spawnPickup()
        if (cycles.first().alive && steps % 3 == 0) score++
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newRound()
            }
            return
        }
        cycles.forEach { it.phaseTime = (it.phaseTime - seconds).coerceAtLeast(0f) }
        elapsed += seconds
        if (elapsed >= interval) {
            elapsed -= interval
            step()
        }
    }

    override fun onRestore() {
        visited = IntArray(board.size)
        visitId = 0
        cycles.forEach { cycle ->
            val points = if (cycle.alive) cycle.trail.dropLast(1) else cycle.trail
            points.forEachIndexed { index, point ->
                val x = point % columns + 0.5f
                val y = point / columns + 0.5f
                if (index == 0) cycle.path.moveTo(x, y) else cycle.path.lineTo(x, y)
            }
            if (points.isEmpty()) cycle.path.moveTo(
                cycle.position % columns + 0.5f,
                cycle.position / columns + 0.5f
            )
        }
    }
}