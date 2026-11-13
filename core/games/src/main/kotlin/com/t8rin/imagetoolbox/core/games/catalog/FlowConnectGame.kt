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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Route
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

@RegisterArcadeGame
internal data object FlowConnectGame : ArcadeGameDefinition {
    override val title = R.string.arcade_flowconnect
    override val description = R.string.arcade_flowconnect_sub
    override val icon get() = Icons.Outlined.Route
    override val order = 33

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::FlowConnectRun
        )

        val routePath = remember { Path() }

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
                val area = Size(width = cell * run.columns, height = cell * run.columns)
                drawFlowBackground(style, run.width, run.height)
                drawRoundRect(
                    brush = style.background,
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(8f)
                )
                drawRoundRect(
                    color = style.detailColor.copy(alpha = 0.055f),
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 8f)
                )
                drawRoundRect(
                    color = style.detailColor.copy(alpha = 0.4f),
                    topLeft = run.origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 8f),
                    style = Stroke(width = 1f)
                )
                for (index in 1 until run.columns) {
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.16f),
                        start = run.origin + Offset(x = index * cell, y = 0f),
                        end = run.origin + Offset(x = index * cell, y = area.height),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = style.detailColor.copy(alpha = 0.16f),
                        start = run.origin + Offset(x = 0f, y = index * cell),
                        end = run.origin + Offset(x = area.width, y = index * cell),
                        strokeWidth = 1f
                    )
                }
                run.paths.forEachIndexed { kind, points ->
                    if (points.size > 1) {
                        val path = routePath.apply {
                            rewind()
                            moveTo(run.center(points.first()).x, run.center(points.first()).y)
                            points.drop(1).forEach { lineTo(run.center(it).x, run.center(it).y) }
                        }
                        val color = colors[kind]
                        drawPath(
                            path = path,
                            color = color.copy(alpha = 0.12f),
                            style = Stroke(
                                width = cell * 0.45f,
                                cap = StrokeCap.Square,
                                join = StrokeJoin.Miter
                            )
                        )
                        drawPath(
                            path = path,
                            color = color.copy(alpha = if (run.connected(kind)) 0.9f else 0.6f),
                            style = Stroke(
                                width = cell * 0.22f,
                                cap = StrokeCap.Square,
                                join = StrokeJoin.Miter
                            )
                        )
                        if (kind == run.active) pixelCircle(
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
                        pixelCircle(
                            color = color.copy(alpha = 0.16f),
                            radius = cell * 0.34f,
                            center = p
                        )
                        pixelCircle(
                            color = color,
                            radius = cell * 0.27f,
                            center = p
                        )
                        val r = cell * 0.1f
                        val ink = style.playerDetailColor
                        when (kind) {
                            0 -> pixelCircle(
                                color = ink,
                                radius = r,
                                center = p
                            )

                            1 -> drawRect(
                                color = ink,
                                topLeft = p - Offset(x = r, y = r),
                                size = Size(width = r * 2f, height = r * 2f)
                            )

                            2 -> rotate(degrees = 45f, pivot = p) {
                                drawRect(
                                    color = ink,
                                    topLeft = p - Offset(x = r, y = r),
                                    size = Size(width = r * 2f, height = r * 2f)
                                )
                            }

                            3, 4 -> rotate(degrees = if (kind == 3) 0f else 45f, pivot = p) {
                                drawLine(
                                    color = ink,
                                    start = p - Offset(x = r * 1.2f, y = 0f),
                                    end = p + Offset(x = r * 1.2f, y = 0f),
                                    strokeWidth = 2f
                                )
                                drawLine(
                                    color = ink,
                                    start = p - Offset(x = 0f, y = r * 1.2f),
                                    end = p + Offset(x = 0f, y = r * 1.2f),
                                    strokeWidth = 2f
                                )
                            }

                            else -> pixelCircle(
                                color = ink,
                                radius = r * 1.3f,
                                center = p,
                                style = Stroke(width = 2f)
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
}

private fun DrawScope.drawFlowBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 156f).toInt()
    val rows = ceil(height / 128f).toInt()
    val ink = style.detailColor.copy(alpha = 0.12f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val x = column * 156f + 14f + row % 2 * 13f
        val y = row * 128f + 19f
        val path = Path().apply {
            moveTo(x, y)
            lineTo(x + 42f, y)
            lineTo(x + 42f, y + 22f)
            lineTo(x + 70f, y + 22f)
            lineTo(x + 70f, y + 51f)
            lineTo(x + 18f, y + 51f)
        }
        drawPath(path, ink.copy(alpha = 0.035f), style = Stroke(6f, join = StrokeJoin.Round))
        drawPath(path, ink, style = Stroke(0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        for (point in listOf(Offset(x, y), Offset(x + 18f, y + 51f))) {
            pixelCircle(ink.copy(alpha = 0.06f), 5f, point)
            pixelCircle(ink, 3.3f, point, style = Stroke(0.9f))
        }
        drawLine(ink.copy(alpha = 0.14f), Offset(x + 58f, y - 4f), Offset(x + 87f, y - 4f), 0.7f)
    }
}

@Serializable
private class FlowConnectRun : ArcadeRun() {
    var columns = 4
    var round = 1
    val pairs = mutableListOf<IntArray>()
    val paths = mutableListOf<MutableList<Int>>()
    val solution = mutableListOf<IntArray>()

    @Transient
    val effects = ArcadeEffects()

    @Transient
    var active = -1
        private set

    @Transient
    private var lastTouch: Offset? = null
    var transition = 0f
        private set
    private var glow = 0f
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 34f) / columns)
    val origin
        get() = Offset(
            x = (width - columns * cell) / 2f,
            y = topInset + (height - topInset - columns * cell) / 2f
        )

    fun center(index: Int) =
        origin + Offset(x = (index % columns + 0.5f) * cell, y = (index / columns + 0.5f) * cell)

    fun adjacent(
        a: Int,
        b: Int
    ) = abs(a % columns - b % columns) + abs(a / columns - b / columns) == 1

    fun connected(kind: Int): Boolean =
        paths[kind].size > 1 && paths[kind].first() in pairs[kind] && paths[kind].last() in pairs[kind]

    override fun isValid(): Boolean = super.isValid() &&
            columns in 4..7 &&
            pairs.size == paths.size &&
            pairs.size == solution.size &&
            pairs.all { it.size == 2 } &&
            (pairs.asSequence().flatMap { it.asSequence() } + paths.asSequence()
                .flatten()).all { it in 0 until columns * columns }

    override fun reset() {
        round = 1
        newRound()
    }

    private fun newRound() {
        animateLevel()
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
            val distances = candidate.map {
                abs(it.first() % columns - it.last() % columns) +
                        abs(it.first() / columns - it.last() / columns)
            }
            val bends = candidate.sumOf { part ->
                (1 until part.lastIndex).count { part[it] - part[it - 1] != part[it + 1] - part[it] }
            }
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
            var remaining = columns * 2
            while (remaining-- > 0) {
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

    fun extend(index: Int) {
        if (active < 0 || index < 0 || transition > 0f) return
        val path = paths[active]
        if (index == path.last() || !adjacent(path.last(), index)) return
        val old = path.indexOf(index)
        if (old >= 0) {
            while (path.lastIndex > old) path.removeAt(path.lastIndex)
        } else {
            if (connected(active) ||
                pairs.indices.any { it != active && index in pairs[it] }
            ) return
            paths.forEachIndexed { kind, other ->
                if (kind != active) {
                    val crossing = other.indexOf(index)
                    if (crossing >= 0) while (other.size > crossing) other.removeAt(other.lastIndex)
                }
            }
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
}