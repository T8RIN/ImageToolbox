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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MazeChaserGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::MazeChaserRun
    )
    ArcadeBoard(
        game = ArcadeGame.MazeChaser,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onTap = { run.steer(it - run.playerPoint) },
            onSwipe = run::steer,
            swipeOnDrag = true
        ) {
            val cell = run.cell
            val origin = run.origin
            val area = Size(cell * run.columns, cell * run.rows)
            val outline = Path().apply {
                addRoundRect(RoundRect(Rect(origin, area), CornerRadius(10f)))
            }
            clipPath(outline) {
                for (index in run.walls.indices) {
                    val point =
                        origin + Offset(index % run.columns * cell, index / run.columns * cell)
                    if (run.walls[index]) {
                        drawRect(
                            color = style.playerColor.copy(alpha = 0.1f),
                            topLeft = point,
                            size = Size(cell, cell)
                        )
                        val ink = style.playerColor.copy(alpha = 0.65f)
                        if (index % run.columns > 0 && !run.walls[index - 1]) drawLine(
                            color = ink,
                            start = point,
                            end = point + Offset(0f, cell),
                            strokeWidth = 1.5f
                        )
                        if (index % run.columns < run.columns - 1 && !run.walls[index + 1]) drawLine(
                            color = ink,
                            start = point + Offset(cell, 0f),
                            end = point + Offset(cell, cell),
                            strokeWidth = 1.5f
                        )
                        if (index >= run.columns && !run.walls[index - run.columns]) drawLine(
                            color = ink,
                            start = point,
                            end = point + Offset(cell, 0f),
                            strokeWidth = 1.5f
                        )
                        if (index + run.columns < run.walls.size && !run.walls[index + run.columns]) drawLine(
                            color = ink,
                            start = point + Offset(0f, cell),
                            end = point + Offset(cell, cell),
                            strokeWidth = 1.5f
                        )
                    } else if (run.dots[index] > 0) {
                        val center = point + Offset(cell / 2f, cell / 2f)
                        if (run.dots[index] == 2) {
                            val radius = cell * (0.19f + sin(run.time * 5f) * 0.025f)
                            drawCircle(
                                color = style.accentColor.copy(alpha = 0.18f),
                                radius = radius * 1.6f,
                                center = center
                            )
                            drawCircle(
                                color = style.accentColor,
                                radius = radius,
                                center = center
                            )
                        } else drawCircle(
                            color = style.obstacleColor.copy(alpha = 0.8f),
                            radius = cell * 0.065f,
                            center = center
                        )
                    }
                }
                for ((index, enemy) in run.enemies.withIndex()) {
                    if (enemy.cooldown > 0f) continue
                    val point = run.enemyPoint(enemy)
                    val r = cell * 0.35f
                    val frightened =
                        run.power > 0f && (run.power > 1.5f || run.time % 0.25f < 0.15f)
                    val ink = if (frightened) style.detailColor else when (index % 3) {
                        0 -> style.accentColor
                        1 -> style.hazardColor
                        else -> style.obstacleColor
                    }
                    val body = Path().apply {
                        moveTo(point.x - r, point.y + r)
                        lineTo(point.x - r, point.y - r * 0.5f)
                        lineTo(point.x - r * 0.5f, point.y - r)
                        lineTo(point.x + r * 0.5f, point.y - r)
                        lineTo(point.x + r, point.y - r * 0.5f)
                        lineTo(point.x + r, point.y + r)
                        repeat(5) {
                            lineTo(
                                point.x + r - it * r * 0.5f,
                                point.y + r - if (it % 2 == 1) r * 0.3f else 0f
                            )
                        }
                        close()
                    }
                    drawPath(
                        path = body,
                        color = ink
                    )
                    for (side in listOf(-1, 1)) {
                        drawRect(
                            color = style.playerDetailColor,
                            topLeft = point + Offset(side * r * 0.45f - r * 0.18f, -r * 0.4f),
                            size = Size(r * 0.36f, r * 0.5f)
                        )
                        if (!frightened) drawRect(
                            color = style.textColor,
                            topLeft = point + Offset(side * r * 0.45f - r * 0.07f, -r * 0.25f),
                            size = Size(r * 0.17f, r * 0.25f)
                        )
                    }
                }
                if (run.invulnerable <= 0f || run.time % 0.2f < 0.13f) {
                    val radius = cell * 0.36f
                    val mouth = 12f + abs(sin(run.time * 13f)) * 28f
                    val point = run.playerPoint
                    drawArc(
                        color = style.playerColor,
                        startAngle = run.direction * 90f + mouth,
                        sweepAngle = 360f - mouth * 2f,
                        useCenter = true,
                        topLeft = point - Offset(radius, radius),
                        size = Size(radius * 2f, radius * 2f)
                    )
                }
                if (run.transition > 0f) drawRect(
                    color = style.accentColor.copy(alpha = run.transition * 0.12f),
                    topLeft = origin,
                    size = Size(cell * run.columns, cell * run.rows)
                )
            }
            drawRoundRect(
                color = style.playerColor.copy(alpha = 0.35f),
                topLeft = origin,
                size = area,
                cornerRadius = CornerRadius(10f),
                style = Stroke(1f)
            )
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
            arcadeLives(
                style = style,
                remaining = run.lives,
                y = run.topInset / 2f
            )
        }
    }
}

internal data class MazeEnemy(
    var position: Int,
    var previous: Int,
    var elapsed: Float = 0f,
    var cooldown: Float = 0f
)

internal class MazeChaserRun : ArcadeRun() {
    val columns = 13
    var rows = 9
    var walls = BooleanArray(columns * rows) { true }
    var dots = IntArray(walls.size)
    val enemies = mutableListOf<MazeEnemy>()
    val effects = ArcadeEffects()
    var player = columns + 1
    var previous = player
    var direction = 0
    var queued = 0
    var lives = 3
    var round = 1
    var time = 0f
    var power = 0f
    var invulnerable = 0f
    var transition = 0f
    private var elapsed = 0f
    private val playerInterval get() = (0.145f - round.coerceAtMost(12) * 0.002f)
    private val enemyInterval get() = (0.25f - round.coerceAtMost(15) * 0.005f) * if (power > 0f) 1.55f else 1f
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 48f) / rows)
    val origin
        get() = Offset(
            (width - columns * cell) / 2f,
            topInset + (height - topInset - rows * cell) / 2f
        )
    val playerPoint
        get() = center(previous) + (center(player) - center(previous)) * (elapsed / playerInterval).coerceIn(
            0f,
            1f
        )

    fun center(index: Int) =
        origin + Offset((index % columns + 0.5f) * cell, (index / columns + 0.5f) * cell)

    fun enemyPoint(enemy: MazeEnemy) =
        center(enemy.previous) + (center(enemy.position) - center(enemy.previous)) * enemy.elapsed.coerceIn(
            0f,
            1f
        )

    override fun reset() {
        round = 1
        lives = 3
        time = 0f
        effects.clear()
        newRound()
    }

    private fun newRound() {
        rows = (((height - topInset - 48f) / 24f).toInt().coerceIn(7, 19) / 2) * 2 + 1
        walls = BooleanArray(columns * rows) { true }
        val stack = ArrayDeque<Int>()
        walls[columns + 1] = false
        stack.addLast(columns + 1)
        while (stack.isNotEmpty()) {
            val current = stack.last()
            val x = current % columns
            val y = current / columns
            val options =
                listOf(Offset(2f, 0f), Offset(-2f, 0f), Offset(0f, 2f), Offset(0f, -2f)).filter {
                    val nx = x + it.x.toInt()
                    val ny = y + it.y.toInt()
                    nx in 1 until columns - 1 && ny in 1 until rows - 1 && walls[ny * columns + nx]
                }
            val delta = options.randomOrNull()
            if (delta == null) stack.removeLast()
            else {
                val next = (y + delta.y.toInt()) * columns + x + delta.x.toInt()
                walls[(current + next) / 2] = false
                walls[next] = false
                stack.addLast(next)
            }
        }
        // Braid every dead end back into the maze so there is always a way around a pursuer.
        for (y in 1 until rows - 1 step 2) for (x in 1 until columns - 1 step 2) {
            val index = y * columns + x
            if ((0..3).count { next(index, it) >= 0 } != 1) continue
            val options = listOf(-2, 2, -columns * 2, columns * 2).filter { offset ->
                val other = index + offset
                other in walls.indices && other % columns in 1 until columns - 1 &&
                        other / columns in 1 until rows - 1 &&
                        abs(other % columns - x) + abs(other / columns - y) == 2 &&
                        walls[(index + other) / 2]
            }
            options.randomOrNull()?.let { walls[index + it / 2] = false }
        }
        dots = IntArray(walls.size) { if (walls[it]) 0 else 1 }
        listOf(
            columns + 1,
            columns * 2 - 2,
            (rows - 2) * columns + 1,
            (rows - 1) * columns - 2
        ).forEach { dots[it] = 2 }
        dots[columns + 1] = 0
        transition = 0f
        respawn(protect = false)
    }

    private fun respawn(protect: Boolean = true) {
        player = columns + 1
        previous = player
        direction = 0
        queued = 0
        elapsed = 0f
        power = 0f
        invulnerable = if (protect) 1.8f else 0f
        enemies.clear()
        val choices =
            walls.indices.filter { !walls[it] && abs(it % columns - 1) + abs(it / columns - 1) > (columns + rows) / 3 }
        val spawns = choices.shuffled().take((1 + (round - 1) / 3).coerceAtMost(3))
        spawns.forEach { enemies += MazeEnemy(it, it, cooldown = 1.2f) }
    }

    private fun next(
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
        if (x !in 0 until columns || y !in 0 until rows) return -1
        return (y * columns + x).takeUnless { walls[it] } ?: -1
    }

    fun steer(delta: Offset) {
        queued = if (abs(delta.x) > abs(delta.y)) {
            if (delta.x > 0f) 0 else 2
        } else if (delta.y > 0f) 1 else 3
        if (queued == (direction + 2) % 4 && player != previous) {
            val destination = player
            player = previous
            previous = destination
            elapsed = playerInterval - elapsed
            direction = queued
        }
    }

    internal fun distances(target: Int): IntArray {
        val distances = IntArray(walls.size) { Int.MAX_VALUE }
        val queue = IntArray(walls.size)
        var head = 0
        var tail = 1
        queue[0] = target
        distances[target] = 0
        while (head < tail) {
            val index = queue[head++]
            repeat(4) {
                val other = next(index, it)
                if (other >= 0 && distances[other] == Int.MAX_VALUE) {
                    distances[other] = distances[index] + 1
                    queue[tail++] = other
                }
            }
        }
        return distances
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
        power = (power - seconds).coerceAtLeast(0f)
        invulnerable = (invulnerable - seconds).coerceAtLeast(0f)
        elapsed += seconds
        if (elapsed >= playerInterval) {
            elapsed -= playerInterval
            if (dots[player] > 0) {
                if (dots[player] == 2) {
                    power = 6f
                    score += 25
                    effects.burst(point = center(player))
                } else score += 5
                dots[player] = 0
                if (dots.none { it > 0 }) {
                    score += 100 * round
                    transition = 1f
                    return
                }
            }
            if (next(player, queued) >= 0) direction = queued
            val destination = next(player, direction)
            previous = player
            if (destination >= 0) player = destination
        }
        for ((index, enemy) in enemies.withIndex()) {
            if (enemy.cooldown > 0f) {
                enemy.cooldown -= seconds
                continue
            }
            enemy.elapsed += seconds / enemyInterval
            if (enemy.elapsed >= 1f) {
                enemy.elapsed -= 1f
                val options =
                    (0..3).map { next(enemy.position, it) }.filter { it >= 0 }.toMutableList()
                if (options.size > 1) options.remove(enemy.previous)
                val scatter = time % 12f < 3f && power <= 0f
                val target =
                    if (scatter) (rows - 2) * columns + if (index % 2 == 0) columns - 2 else 1 else player
                val distances = distances(target)
                val destination = when {
                    power > 0f -> options.maxByOrNull { distances[it] }
                    Random.nextFloat() < 0.12f -> options.randomOrNull()
                    else -> options.minByOrNull { distances[it] }
                } ?: enemy.position
                enemy.previous = enemy.position
                enemy.position = destination
            }
            if ((playerPoint - enemyPoint(enemy)).getDistance() < cell * 0.6f) {
                if (power > 0f) {
                    score += 100
                    effects.burst(
                        point = enemyPoint(enemy),
                        count = 12
                    )
                    enemy.position = (rows - 1) * columns - 2
                    enemy.previous = enemy.position
                    enemy.cooldown = 2f
                    enemy.elapsed = 0f
                } else if (invulnerable <= 0f) {
                    effects.burst(
                        point = playerPoint,
                        count = 18
                    )
                    lives--
                    if (lives == 0) phase = ArcadePhase.Over else respawn()
                    return
                }
            }
        }
    }

    override fun onResize(previous: Size) {
        if (time == 0f) newRound()
    }

    override fun save(state: Bundle) {
        state.putBooleanArray("walls", walls.copyOf())
        state.putIntArray("dots", dots.copyOf())
        state.putIntArray(
            "run",
            intArrayOf(rows, player, previous, direction, queued, lives, round)
        )
        state.putFloatArray("time", floatArrayOf(time, power, invulnerable, transition, elapsed))
        state.putFloatArray(
            "enemies",
            enemies.flatMap {
                listOf(
                    it.position.toFloat(),
                    it.previous.toFloat(),
                    it.elapsed,
                    it.cooldown
                )
            }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getBooleanArray("walls")?.let { walls = it.copyOf() }
        state.getIntArray("dots")?.let { dots = it.copyOf() }
        state.getIntArray("run")?.let {
            rows = it[0]
            player = it[1]
            previous = it[2]
            direction = it[3]
            queued = it[4]
            lives = it[5]
            round = it[6]
        }
        state.getFloatArray("time")?.let {
            time = it[0]
            power = it[1]
            invulnerable = it[2]
            transition = it[3]
            elapsed = it[4]
        }
        enemies.clear()
        state.getFloatArray("enemies")?.let {
            for (index in it.indices step 4) enemies += MazeEnemy(
                it[index].toInt(),
                it[index + 1].toInt(),
                it[index + 2],
                it[index + 3]
            )
        }
    }
}