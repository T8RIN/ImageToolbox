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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

@Composable
fun FlowConnectGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::FlowConnectRun
    )

    ArcadeBoard(
        game = ArcadeGame.FlowConnect,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver,
        turnBased = true
    ) {
        ArcadeField(
            run = run,
            onPress = run::begin,
            onDrag = run::drag,
            onRelease = run::stopInput
        ) {
            val cell = run.cell
            val colors = listOf(
                style.playerColor,
                style.accentColor,
                style.obstacleColor,
                style.hazardColor,
                lerp(style.playerColor, style.accentColor, 0.5f),
                lerp(style.hazardColor, style.obstacleColor, 0.5f)
            )
            val area = Size(cell * run.columns, cell * run.columns)
            drawRoundRect(
                color = style.detailColor.copy(alpha = 0.055f),
                topLeft = run.origin,
                size = area,
                cornerRadius = CornerRadius(8f)
            )
            drawRoundRect(
                color = style.detailColor.copy(alpha = 0.4f),
                topLeft = run.origin,
                size = area,
                cornerRadius = CornerRadius(8f),
                style = Stroke(1f)
            )
            for (index in 1 until run.columns) {
                drawLine(
                    color = style.detailColor.copy(alpha = 0.16f),
                    start = run.origin + Offset(index * cell, 0f),
                    end = run.origin + Offset(index * cell, area.height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = style.detailColor.copy(alpha = 0.16f),
                    start = run.origin + Offset(0f, index * cell),
                    end = run.origin + Offset(area.width, index * cell),
                    strokeWidth = 1f
                )
            }
            run.paths.forEachIndexed { kind, points ->
                if (points.size > 1) {
                    val path = Path().apply {
                        moveTo(run.center(points.first()).x, run.center(points.first()).y)
                        points.drop(1).forEach { lineTo(run.center(it).x, run.center(it).y) }
                    }
                    val color = colors[kind]
                    drawPath(
                        path = path,
                        color = color.copy(alpha = 0.12f),
                        style = Stroke(
                            cell * 0.45f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    drawPath(
                        path = path,
                        color = color.copy(alpha = if (run.connected(kind)) 0.9f else 0.6f),
                        style = Stroke(
                            cell * 0.22f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                    if (kind == run.active) drawCircle(
                        color = color.copy(alpha = 0.25f),
                        radius = cell * 0.32f,
                        center = run.center(points.last())
                    )
                }
            }
            run.pairs.forEachIndexed { kind, pair ->
                for (index in pair) {
                    val p = run.center(index)
                    val color = colors[kind]
                    drawCircle(
                        color = color.copy(alpha = 0.16f),
                        radius = cell * 0.34f,
                        center = p
                    )
                    drawCircle(
                        color = color,
                        radius = cell * 0.27f,
                        center = p
                    )
                    val r = cell * 0.1f
                    val ink = style.playerDetailColor
                    when (kind) {
                        0 -> drawCircle(
                            color = ink,
                            radius = r,
                            center = p
                        )

                        1 -> drawRect(
                            color = ink,
                            topLeft = p - Offset(r, r),
                            size = Size(r * 2f, r * 2f)
                        )

                        2 -> rotate(45f, p) {
                            drawRect(
                                color = ink,
                                topLeft = p - Offset(r, r),
                                size = Size(r * 2f, r * 2f)
                            )
                        }

                        3, 4 -> rotate(if (kind == 3) 0f else 45f, p) {
                            drawLine(
                                color = ink,
                                start = p - Offset(r * 1.2f, 0f),
                                end = p + Offset(r * 1.2f, 0f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = ink,
                                start = p - Offset(0f, r * 1.2f),
                                end = p + Offset(0f, r * 1.2f),
                                strokeWidth = 2f
                            )
                        }

                        else -> drawCircle(
                            color = ink,
                            radius = r * 1.3f,
                            center = p,
                            style = Stroke(2f)
                        )
                    }
                }
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

internal class FlowConnectRun : ArcadeRun() {
    var columns = 4
    var round = 1
    val pairs = mutableListOf<IntArray>()
    val paths = mutableListOf<MutableList<Int>>()
    val solution = mutableListOf<IntArray>()
    val effects = ArcadeEffects()
    var active = -1
        private set
    private var lastTouch: Offset? = null
    private var transition = 0f
    private var glow = 0f
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 34f) / columns)
    val origin
        get() = Offset(
            (width - columns * cell) / 2f,
            topInset + (height - topInset - columns * cell) / 2f
        )

    fun center(index: Int) =
        origin + Offset((index % columns + 0.5f) * cell, (index / columns + 0.5f) * cell)

    fun adjacent(
        a: Int,
        b: Int
    ) = abs(a % columns - b % columns) + abs(a / columns - b / columns) == 1

    fun connected(kind: Int): Boolean =
        paths[kind].size > 1 && paths[kind].first() in pairs[kind] && paths[kind].last() in pairs[kind]

    override fun reset() {
        round = 1
        newRound()
    }

    private fun newRound() {
        columns = (4 + (round - 1) / 4).coerceAtMost(7)
        val count = (columns - 1).coerceIn(3, 6)
        var best = Int.MIN_VALUE
        var chosen = emptyList<IntArray>()
        repeat(24) {
            val route = (0 until columns).flatMap { row ->
                (0 until columns).map { col -> row * columns + if (row % 2 == 0) col else columns - 1 - col }
            }.toMutableList()
            repeat(columns * columns * 6) {
                if (Random.nextBoolean()) {
                    val neighbors =
                        route.indices.filter { it > 1 && adjacent(route.first(), route[it]) }
                    neighbors.randomOrNull()?.let { end -> route.subList(0, end).reverse() }
                } else {
                    val neighbors = route.indices.filter {
                        it < route.size - 2 && adjacent(
                            route.last(),
                            route[it]
                        )
                    }
                    neighbors.randomOrNull()
                        ?.let { start -> route.subList(start + 1, route.size).reverse() }
                }
            }
            val lengths = IntArray(count) { 3 }
            repeat(route.size - count * 3) { lengths[Random.nextInt(count)]++ }
            var start = 0
            val candidate = lengths.map { length ->
                route.subList(start, start + length).toIntArray().also { start += length }
            }
            val distances =
                candidate.map { abs(it.first() % columns - it.last() % columns) + abs(it.first() / columns - it.last() / columns) }
            val bends =
                candidate.sumOf { part -> (1 until part.lastIndex).count { part[it] - part[it - 1] != part[it + 1] - part[it] } }
            val quality =
                distances.sum() + bends * 2 - distances.count { it == 1 } * 30 - lengths.count { it < 4 } * 8
            if (quality > best) {
                best = quality
                chosen = candidate
            }
        }
        solution.clear()
        solution += chosen
        pairs.clear()
        chosen.forEach { pairs += intArrayOf(it.first(), it.last()) }
        transition = 0f
        restartPuzzle()
    }

    fun restartPuzzle() {
        if (transition > 0f) return
        paths.clear()
        repeat(pairs.size) { paths += mutableListOf<Int>() }
        active = -1
        lastTouch = null
        effects.clear()
        glow = 0f
        isAnimating = false
        redraw()
    }

    private fun indexAt(point: Offset): Int {
        if (point.x < origin.x || point.y < origin.y) return -1
        val x = ((point.x - origin.x) / cell).toInt()
        val y = ((point.y - origin.y) / cell).toInt()
        return if (x in 0 until columns && y in 0 until columns) y * columns + x else -1
    }

    fun begin(point: Offset) {
        if (transition > 0f) return
        val index = indexAt(point)
        if (index < 0) return
        val endpoint = pairs.indexOfFirst { index in it }
        val owner = paths.indexOfFirst { index in it }
        active = if (endpoint >= 0) endpoint else owner
        if (active < 0) return
        val path = paths[active]
        if (endpoint >= 0) {
            path.clear()
            path += index
        } else while (path.last() != index) path.removeAt(path.lastIndex)
        lastTouch = point
        glow = maxOf(glow, 0.2f)
        isAnimating = true
        redraw()
    }

    fun drag(point: Offset) {
        if (active < 0 || transition > 0f) return
        val from = lastTouch ?: point
        val steps = ceil((point - from).getDistance() / (cell * 0.2f)).toInt().coerceIn(1, 256)
        for (step in 1..steps) {
            if (active < 0) break
            val sample = from + (point - from) * (step.toFloat() / steps)
            val target = indexAt(sample)
            if (target < 0) continue
            for (distance in 0 until columns * 2) {
                if (active < 0) break
                val last = paths[active].last()
                if (last == target) break
                val dx = target % columns - last % columns
                val dy = target / columns - last / columns
                val horizontal = if (dx == 0) last else last + if (dx > 0) 1 else -1
                val vertical = if (dy == 0) last else last + if (dy > 0) columns else -columns
                val delta = sample - center(last)
                val across = abs(delta.x) >= abs(delta.y)
                extend(if (across) horizontal else vertical)
                if (active >= 0 && paths[active].last() == last) {
                    extend(if (across) vertical else horizontal)
                }
                if (active < 0 || paths[active].last() == last) break
            }
        }
        lastTouch = point
        redraw()
    }

    internal fun extend(index: Int) {
        if (active < 0 || index < 0 || transition > 0f) return
        val path = paths[active]
        if (index == path.last() || !adjacent(path.last(), index)) return
        val old = path.indexOf(index)
        if (old >= 0) {
            while (path.lastIndex > old) path.removeAt(path.lastIndex)
        } else {
            if (connected(active) || pairs.indices.any { it != active && index in pairs[it] } || paths.indices.any { it != active && index in paths[it] }) return
            path += index
            if (connected(active)) effects.burst(
                point = center(index),
                count = 6
            )
        }
        glow = 0.42f
        isAnimating = true
        if (pairs.indices.all(::connected) && paths.sumOf { it.size } == columns * columns) {
            score += 100 + round * 25
            transition = 0.9f
            pairs.forEach { pair ->
                pair.forEach {
                    effects.burst(
                        point = center(it),
                        count = 4
                    )
                }
            }
            active = -1
        }
    }

    override fun stopInput() {
        active = -1
        lastTouch = null
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        glow = (glow - seconds).coerceAtLeast(0f)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newRound()
            }
        }
        isAnimating = active >= 0 || transition > 0f || glow > 0f
    }

    override fun save(state: Bundle) {
        state.putIntArray("run", intArrayOf(columns, round))
        state.putFloatArray("time", floatArrayOf(transition, glow))
        state.putInt("count", pairs.size)
        pairs.indices.forEach {
            state.putIntArray("pair$it", pairs[it])
            state.putIntArray("path$it", paths[it].toIntArray())
            state.putIntArray("solution$it", solution[it])
        }
    }

    override fun restore(state: Bundle) {
        state.getIntArray("run")!!.let {
            columns = it[0]
            round = it[1]
        }
        state.getFloatArray("time")!!.let {
            transition = it[0]
            glow = it[1]
        }
        pairs.clear()
        paths.clear()
        solution.clear()
        repeat(state.getInt("count")) {
            pairs += state.getIntArray("pair$it")!!.copyOf()
            paths += state.getIntArray("path$it")!!.toMutableList()
            solution += state.getIntArray("solution$it")!!.copyOf()
        }
        stopInput()
        isAnimating = transition > 0f || glow > 0f
    }
}