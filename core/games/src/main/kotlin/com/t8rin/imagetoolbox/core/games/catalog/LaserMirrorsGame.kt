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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Flare
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

@RegisterArcadeGame
internal data object LaserMirrorsGame : ArcadeGameDefinition {
    override val title = R.string.arcade_lasermirrors
    override val description = R.string.arcade_lasermirrors_sub
    override val icon get() = Icons.Outlined.Flare
    override val order = 32

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::LaserMirrorsRun
        )

        val beamPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier,
            turnBased = true
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onTap = run::tap
            ) {
                drawLaserBackground(style, run.width, run.height)
                val cell = run.cell
                val area = Size(width = cell * run.columns, height = cell * run.columns)
                drawRoundRect(
                    brush = style.background,
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(7f)
                )
                drawRoundRect(
                    color = style.detailColor.copy(alpha = 0.055f),
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 7f)
                )
                drawRoundRect(
                    color = style.detailColor.copy(alpha = 0.35f),
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 7f),
                    style = Stroke(width = 1f)
                )
                for (index in run.board.indices) {
                    val p = run.center(index)
                    if (run.board[index] == 3) {
                        pixelRect(
                            color = style.detailColor.copy(alpha = 0.65f),
                            x = p.x - cell * 0.25f,
                            y = p.y - cell * 0.25f,
                            width = cell * 0.5f,
                            height = cell * 0.5f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.3f),
                            x = p.x - cell * 0.12f,
                            y = p.y - 1f,
                            width = cell * 0.24f,
                            height = 2f
                        )
                    } else if (run.board[index] == 0 && index !in run.targets) pixelCircle(
                        color = style.detailColor.copy(alpha = 0.4f),
                        radius = 1f,
                        center = p
                    )
                }
                val source = run.center(run.source) - Offset(x = cell * 0.65f, y = 0f)
                val beam = beamPath.apply {
                    rewind()
                    for (ray in run.rays) {
                        if (ray.isEmpty()) continue
                        val start = run.center(ray.first()) - Offset(cell * 0.65f, 0f)
                        moveTo(start.x, start.y)
                        for (index in ray) lineTo(run.center(index).x, run.center(index).y)
                    }
                }
                drawPath(
                    path = beam,
                    color = style.playerColor.copy(alpha = 0.09f),
                    style = Stroke(
                        width = 8f,
                        cap = StrokeCap.Square
                    )
                )
                drawPath(
                    path = beam,
                    color = style.playerColor.copy(alpha = 0.3f),
                    style = Stroke(
                        width = 4f,
                        cap = StrokeCap.Square
                    )
                )
                drawPath(
                    path = beam,
                    color = style.playerColor,
                    style = Stroke(
                        width = 1.5f,
                        cap = StrokeCap.Square
                    )
                )
                pixelRect(
                    color = style.playerColor,
                    x = source.x - 4f,
                    y = source.y - 7f,
                    width = 8f,
                    height = 14f
                )
                pixelRect(
                    color = style.accentColor,
                    x = source.x + 3f,
                    y = source.y - 3f,
                    width = 5f,
                    height = 6f
                )
                pixelRect(
                    color = style.playerDetailColor.copy(alpha = 0.5f),
                    x = source.x - 2f,
                    y = source.y - 5f,
                    width = 2f,
                    height = 10f
                )
                pixelRect(
                    color = style.playerColor,
                    x = source.x + 6f,
                    y = source.y - 4f,
                    width = 2f,
                    height = 8f
                )
                for (origin in run.sources.drop(1)) {
                    val point = run.center(origin) - Offset(cell * 0.65f, 0f)
                    pixelRect(style.playerColor, point.x - 4f, point.y - 7f, 8f, 14f)
                    pixelRect(style.accentColor, point.x + 4f, point.y - 3f, 3f, 6f)
                }
                for (target in run.targets) {
                    val p = run.center(target)
                    val lit = target in run.lit
                    val color = if (lit) style.accentColor else style.obstacleColor
                    pixelCircle(
                        color = color.copy(alpha = if (lit) 0.18f else 0.06f),
                        radius = cell * 0.3f,
                        center = p
                    )
                    pixelCircle(
                        color = color,
                        radius = cell * 0.21f,
                        center = p,
                        style = Stroke(width = 1.5f)
                    )
                    pixelCircle(
                        color = color,
                        radius = cell * if (lit) 0.11f else 0.045f,
                        center = p
                    )
                    if (lit) repeat(4) { side ->
                        rotate(degrees = side * 90f, pivot = p) {
                            drawLine(
                                color = color,
                                start = p + Offset(x = 0f, y = -cell * 0.3f),
                                end = p + Offset(x = 0f, y = -cell * 0.36f),
                                strokeWidth = 1f
                            )
                        }
                    }
                }
                for (index in run.board.indices) if (run.board[index] in 1..2) {
                    val p = run.center(index)
                    pixelCircle(
                        color = style.detailColor.copy(alpha = 0.12f),
                        radius = cell * 0.32f,
                        center = p
                    )
                    val angle = run.angles[index]
                    rotate(degrees = angle, pivot = p) {
                        drawLine(
                            color = style.obstacleColor.copy(alpha = 0.2f),
                            start = p - Offset(x = cell * 0.34f, y = 0f),
                            end = p + Offset(x = cell * 0.34f, y = 0f),
                            strokeWidth = 6f,
                            cap = StrokeCap.Square
                        )
                        drawLine(
                            color = style.obstacleColor,
                            start = p - Offset(x = cell * 0.32f, y = 0f),
                            end = p + Offset(x = cell * 0.32f, y = 0f),
                            strokeWidth = 3f,
                            cap = StrokeCap.Square
                        )
                        for (end in listOf(-1f, 1f)) {
                            pixelRect(
                                color = style.obstacleColor,
                                x = p.x + end * cell * 0.32f - 1.5f,
                                y = p.y - 4f,
                                width = 3f,
                                height = 8f
                            )
                        }
                        drawLine(
                            color = style.accentColor,
                            start = p + Offset(x = -cell * 0.25f, y = -1f),
                            end = p + Offset(x = cell * 0.25f, y = -1f),
                            strokeWidth = 1f
                        )
                    }
                    pixelCircle(
                        color = style.playerDetailColor,
                        radius = 1.5f,
                        center = p
                    )
                }
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private fun DrawScope.drawLaserBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 176f).toInt()
    val rows = ceil(height / 138f).toInt()
    val ink = style.detailColor.copy(alpha = 0.13f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val x = column * 176f + 20f + row % 2 * 14f
        val y = row * 138f + 21f
        drawRoundRect(
            ink.copy(alpha = 0.055f),
            Offset(x + 24f, y + 13f),
            Size(27f, 31f),
            CornerRadius(1f)
        )
        drawRoundRect(
            ink,
            Offset(x + 24f, y + 13f),
            Size(27f, 31f),
            CornerRadius(1f),
            style = Stroke(0.9f)
        )
        for (pin in 0..3) {
            val py = y + 17f + pin * 7f
            drawLine(ink, Offset(x + 18f, py), Offset(x + 24f, py), 0.8f)
            drawLine(ink, Offset(x + 51f, py), Offset(x + 57f, py), 0.8f)
        }
        val route = Path().apply {
            moveTo(x, y)
            lineTo(x + 10f, y)
            lineTo(x + 10f, y + 55f)
            lineTo(x + 70f, y + 55f)
            lineTo(x + 70f, y + 28f)
            lineTo(x + 92f, y + 28f)
        }
        drawPath(route, ink.copy(alpha = 0.17f), style = Stroke(0.9f))
        pixelRect(ink, x - 2f, y - 2f, 4f, 4f)
        pixelCircle(ink, 2f, Offset(x + 92f, y + 28f), style = Stroke(0.8f))
    }
}

@Serializable
private class LaserMirrorsRun : ArcadeRun() {
    var columns = 5
    var board = IntArray(25)
    var angles = FloatArray(25)
    var targets = intArrayOf()
    var source = 0
    var sources = intArrayOf()
    val rays = mutableListOf<List<Int>>()
    var round = 1
    var turns = 0
    val beam = mutableListOf<Int>()
    val lit = mutableSetOf<Int>()

    @Transient
    val effects = ArcadeEffects()
    var solution = IntArray(25)
        private set
    private var initial = IntArray(25)
    var transition = 0f
        private set
    private var glow = 0f
    val cell get() = minOf((width - 62f) / columns, (height - topInset - 34f) / columns)
    val origin
        get() = Offset(
            x = (width - columns * cell) / 2f,
            y = topInset + (height - topInset - columns * cell) / 2f
        )

    fun center(index: Int) =
        origin + Offset(x = (index % columns + 0.5f) * cell, y = (index / columns + 0.5f) * cell)

    var exit: ArcadeOffset? = null
        private set

    fun neighbor(
        index: Int,
        direction: Int
    ): Int {
        val x = index % columns + when (direction) {
            0 -> 1
            2 -> -1
            else -> 0
        }
        val y = index / columns + when (direction) {
            1 -> 1
            3 -> -1
            else -> 0
        }
        return if (x in 0 until columns && y in 0 until columns) y * columns + x else -1
    }

    override fun isValid(): Boolean = super.isValid() &&
            columns in 5..7 &&
            board.size == columns * columns &&
            angles.size == board.size &&
            solution.size == board.size &&
            initial.size == board.size &&
            source in board.indices &&
            targets.all { it in board.indices }

    override fun reset() {
        round = 1
        newRound()
    }

    private fun newRound() {
        animateLevel()
        columns = (5 + (round - 1) / 5).coerceAtMost(7)
        val wanted = (3 + round / 2).coerceAtMost(9)
        var bestPath = emptyList<Int>()
        var bestTurns = -1
        repeat(100) {
            val path = mutableListOf(Random.nextInt(columns) * columns)
            var direction = 0
            var bends = 0
            repeat(columns * columns) {
                val choices = listOf(direction, (direction + 1) % 4, (direction + 3) % 4).filter {
                    val next = neighbor(path.last(), it)
                    next >= 0 && next !in path
                }
                if (choices.isNotEmpty() && bends < wanted + 2) {
                    val chosen = if (path.size == 1) 0 else choices.random()
                    val next = neighbor(path.last(), chosen)
                    if (next >= 0 && next !in path) {
                        if (chosen != direction) bends++
                        direction = chosen
                        path += next
                    }
                }
            }
            if (bends > bestTurns && path.size >= columns + wanted) {
                bestPath = path
                bestTurns = bends
            }
        }
        // A serpentine route is also a valid fallback on the smallest field.
        if (bestPath.isEmpty()) bestPath = (0 until columns).flatMap { row ->
            (0 until columns).map { col -> row * columns + if (row % 2 == 0) col else columns - 1 - col }
        }
        source = bestPath.first()
        sources = intArrayOf(source)
        solution = IntArray(columns * columns)
        var direction = 0
        for (i in 0 until bestPath.lastIndex) {
            val at = bestPath[i]
            val next = (0..3).first { neighbor(at, it) == bestPath[i + 1] }
            if (next != direction) solution[at] = if (3 - direction == next) 1 else 2
            direction = next
        }
        val straight = bestPath.drop(2).filter { solution[it] == 0 }
        val receivers = straight.shuffled().take((1 + round / 3).coerceAtMost(3)).toMutableSet()
        receivers += bestPath.last()
        targets = receivers.toIntArray()
        solution[bestPath.last()] = 0
        for (index in solution.indices) if (index !in bestPath) {
            solution[index] = when {
                Random.nextFloat() < 0.16f -> 3
                round > 2 && Random.nextFloat() < 0.12f -> Random.nextInt(1, 3)
                else -> 0
            }
        }
        if (round >= 8) {
            val preserved = bestPath.toSet()
            val entries = (0 until columns).map { it * columns }.filter {
                it !in preserved && solution[it] == 0
            }.shuffled()
            for (entry in entries) {
                var found = false
                repeat(24) {
                    if (found) return@repeat
                    val candidate = solution.copyOf()
                    val path = mutableListOf<Int>()
                    var at = entry
                    var heading = 0
                    var bends = 0
                    while (at >= 0 && at !in path && candidate[at] != 3) {
                        path += at
                        val incoming = heading
                        heading = when (candidate[at]) {
                            1 -> 3 - heading
                            2 -> heading xor 1
                            else -> {
                                val choices = if (at in preserved || path.size == 1) listOf(heading)
                                else listOf(heading, (heading + 1) % 4, (heading + 3) % 4)
                                choices.filter { direction ->
                                    val next = neighbor(at, direction)
                                    next >= 0 && next !in path && candidate[next] != 3
                                }.randomOrNull() ?: heading
                            }
                        }
                        if (heading != incoming) {
                            bends++
                            if (candidate[at] == 0) candidate[at] =
                                if (3 - incoming == heading) 1 else 2
                        }
                        at = neighbor(at, heading)
                    }
                    val primary = mutableSetOf<Int>()
                    val visited = mutableSetOf<Int>()
                    var cell = source
                    var direction = 0
                    while (cell >= 0 && visited.add(cell * 4 + direction)) {
                        primary += cell
                        if (candidate[cell] == 3) break
                        direction = when (candidate[cell]) {
                            1 -> 3 - direction
                            2 -> direction xor 1
                            else -> direction
                        }
                        cell = neighbor(cell, direction)
                    }
                    val extra = path.filter { it !in primary && candidate[it] == 0 }
                    if (bends >= 2 && path.size >= columns + 2 && extra.size >= 2) {
                        solution = candidate
                        sources = intArrayOf(source, entry)
                        targets =
                            (targets.toList() + extra.shuffled().take(2)).distinct().toIntArray()
                        found = true
                    }
                }
                if (found) break
            }
        }
        board = solution.copyOf()
        val mirrors = solution.indices.filter { solution[it] in 1..2 }.shuffled()
        mirrors.take((2 + round / 2).coerceAtMost(mirrors.size))
            .forEach { board[it] = 3 - board[it] }
        trace()
        if (lit.size == targets.size) {
            for (index in board.indices) if (board[index] in 1..2) board[index] = 1
            trace()
            if (lit.size == targets.size) {
                for (index in board.indices) if (board[index] in 1..2) board[index] = 2
            }
        }
        initial = board.copyOf()
        transition = 0f
        restartPuzzle()
    }

    override fun onRestore() {
        trace()
    }

    fun trace() {
        beam.clear()
        rays.clear()
        lit.clear()
        exit = null
        for (origin in if (sources.isEmpty()) intArrayOf(source) else sources) {
            val visited = BooleanArray(board.size * 4)
            val ray = mutableListOf<Int>()
            var at = origin
            var direction = 0
            while (at >= 0 && !visited[at * 4 + direction]) {
                visited[at * 4 + direction] = true
                ray += at
                if (board[at] == 3) break
                if (at in targets) lit += at
                direction = when (board[at]) {
                    1 -> 3 - direction
                    2 -> direction xor 1
                    else -> direction
                }
                val next = neighbor(at, direction)
                if (next < 0) {
                    val delta = when (direction) {
                        0 -> Offset(1f, 0f)
                        1 -> Offset(0f, 1f)
                        2 -> Offset(-1f, 0f)
                        else -> Offset(0f, -1f)
                    }
                    exit = center(at) + delta * cell * 0.5f
                }
                at = next
            }
            rays.add(ray.toList())
            beam.addAll(ray)
        }
    }

    fun restartPuzzle() {
        if (transition > 0f) return
        board = initial.copyOf()
        angles = FloatArray(board.size) { if (board[it] == 1) -45f else 45f }
        turns = 0
        glow = 0f
        effects.clear()
        trace()
        isAnimating = false
        redraw()
    }

    fun tap(point: Offset) {
        if (point.x < origin.x || point.y < origin.y) return
        val x = ((point.x - origin.x) / cell).toInt()
        val y = ((point.y - origin.y) / cell).toInt()
        if (x in 0 until columns && y in 0 until columns) turn(y * columns + x)
    }

    fun turn(index: Int) {
        if (transition > 0f || index !in board.indices || board[index] !in 1..2) return
        board[index] = 3 - board[index]
        turns++
        glow = 0.42f
        trace()
        effects.burst(
            point = center(index),
            count = 4
        )
        if (lit.size == targets.size) {
            score += 100 + round * 20 + (30 - turns).coerceAtLeast(0) * 2
            transition = 0.85f
            targets.forEach {
                effects.burst(
                    point = center(it),
                    count = 8
                )
            }
        }
        isAnimating = true
        redraw()
    }

    override fun advance(seconds: Float) {
        glow = (glow - seconds).coerceAtLeast(0f)
        effects.advance(seconds)
        for (index in board.indices) {
            val target = if (board[index] == 1) -45f else 45f
            angles[index] += (target - angles[index]).coerceIn(-seconds * 600f, seconds * 600f)
        }
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newRound()
            }
        }
        isAnimating = transition > 0f || glow > 0f || angles.indices.any {
            abs(angles[it] - if (board[it] == 1) -45f else 45f) > 0.1f
        }
    }

    override fun onResize(previous: Size) {
        trace()
    }
}