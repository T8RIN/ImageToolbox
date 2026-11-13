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
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Explore
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object MazeChaserGame : ArcadeGameDefinition {
    override val title = R.string.arcade_mazechaser
    override val description = R.string.arcade_mazechaser_sub
    override val icon get() = Icons.Outlined.Explore
    override val order = 23

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::MazeChaserRun
        )
        val outlinePath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onTap = { run.steer(it - run.playerPoint) },
                onSwipe = run::steer,
                swipeOnDrag = true
            ) {
                drawMazeBackground(style, run.width, run.height)
                val cell = run.cell
                val origin = run.origin
                val area = Size(width = cell * run.columns, height = cell * run.rows)
                val outline = outlinePath.apply {
                    rewind()
                    addRoundRect(RoundRect(Rect(origin, area), CornerRadius(x = 10f)))
                }
                clipPath(outline) {
                    drawRect(brush = style.background, topLeft = origin, size = area)
                    for (index in run.walls.indices) {
                        val point =
                            origin + Offset(
                                x = index % run.columns * cell,
                                y = index / run.columns * cell
                            )
                        if (run.walls[index]) {
                            drawRect(
                                color = style.playerColor.copy(alpha = 0.1f),
                                topLeft = point,
                                size = Size(width = cell, height = cell)
                            )
                            val ink = style.playerColor.copy(alpha = 0.65f)
                            if (index % run.columns > 0 && !run.walls[index - 1]) drawLine(
                                color = ink,
                                start = point,
                                end = point + Offset(x = 0f, y = cell),
                                strokeWidth = 1.5f
                            )
                            if (index % run.columns < run.columns - 1 && !run.walls[index + 1]) drawLine(
                                color = ink,
                                start = point + Offset(x = cell, y = 0f),
                                end = point + Offset(x = cell, y = cell),
                                strokeWidth = 1.5f
                            )
                            if (index >= run.columns && !run.walls[index - run.columns]) drawLine(
                                color = ink,
                                start = point,
                                end = point + Offset(x = cell, y = 0f),
                                strokeWidth = 1.5f
                            )
                            if (index + run.columns < run.walls.size && !run.walls[index + run.columns]) drawLine(
                                color = ink,
                                start = point + Offset(x = 0f, y = cell),
                                end = point + Offset(x = cell, y = cell),
                                strokeWidth = 1.5f
                            )
                        } else if (run.dots[index] > 0) {
                            val center = point + Offset(x = cell / 2f, y = cell / 2f)
                            if (run.dots[index] == 2) {
                                val radius =
                                    cell * (0.19f + sin(run.time * 5f) * 0.025f)
                                pixelCircle(
                                    color = style.accentColor.copy(alpha = 0.18f),
                                    radius = radius * 1.6f,
                                    center = center
                                )
                                pixelCircle(
                                    color = style.accentColor,
                                    radius = radius,
                                    center = center
                                )
                            } else pixelCircle(
                                color = style.obstacleColor.copy(alpha = 0.8f),
                                radius = cell * 0.065f,
                                center = center
                            )
                        }
                    }
                    if (run.bonus >= 0) {
                        val center = run.center(run.bonus)
                        val radius = cell * 0.15f
                        for (side in listOf(-1f, 1f)) {
                            val fruit = center + Offset(side * radius, radius * 0.55f)
                            drawLine(
                                style.obstacleColor, center - Offset(0f, radius * 1.5f), fruit,
                                strokeWidth = maxOf(1f, cell * 0.05f)
                            )
                            pixelCircle(style.accentColor, radius, fruit)
                            pixelRect(
                                style.playerDetailColor,
                                fruit.x - radius / 2f,
                                fruit.y - radius / 2f,
                                1f,
                                1f
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
                        scale(scale = r / 8f, pivot = point) {
                            drawArcadeSprite(
                                sprite = if ((run.time * 6f).toInt() % 2 == 0) MazeGhost else MazeGhostStep,
                                tint = ink,
                                x = point.x - 8f,
                                y = point.y - 8f
                            )
                        }
                        val looking = Offset(
                            x = (enemy.position % run.columns - enemy.previous % run.columns).coerceIn(
                                -1,
                                1
                            ) * r * 0.12f,
                            y = (enemy.position / run.columns - enemy.previous / run.columns).coerceIn(
                                -1,
                                1
                            ) * r * 0.12f
                        )
                        for (side in listOf(-1, 1)) {
                            drawRect(
                                color = style.playerDetailColor,
                                topLeft = point + Offset(
                                    x = side * r * 0.45f - r * 0.18f,
                                    y = -r * 0.4f
                                ),
                                size = Size(width = r * 0.36f, height = r * 0.5f)
                            )
                            if (!frightened) drawRect(
                                color = style.textColor,
                                topLeft = point + Offset(
                                    x = side * r * 0.45f - r * 0.07f + looking.x,
                                    y = -r * 0.25f + looking.y
                                ),
                                size = Size(width = r * 0.17f, height = r * 0.25f)
                            )
                        }
                    }
                    if (run.invulnerable <= 0f || run.time % 0.2f < 0.13f) {
                        val radius = cell * 0.36f
                        val point = run.playerPoint
                        rotate(degrees = run.direction * 90f, pivot = point) {
                            scale(scale = radius / 8f, pivot = point) {
                                drawArcadeSprite(
                                    sprite = if (abs(sin(run.time * 13f)) > 0.45f) MazePlayerOpen else MazePlayerClosed,
                                    tint = style.playerColor,
                                    x = point.x - 8f,
                                    y = point.y - 8f
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = point.x + 1f,
                                    y = point.y - 5f,
                                    width = 2f,
                                    height = 2f
                                )
                            }
                        }
                    }
                    if (run.transition > 0f) drawRect(
                        color = style.accentColor.copy(alpha = run.transition * 0.12f),
                        topLeft = origin,
                        size = Size(width = cell * run.columns, height = cell * run.rows)
                    )
                }
                drawRoundRect(
                    color = style.playerColor.copy(alpha = 0.35f),
                    topLeft = origin,
                    size = area,
                    cornerRadius = CornerRadius(x = 10f),
                    style = Stroke(width = 1f)
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
}

private val MazePlayerOpen = ArcadeSprite(
    width = 16, height = 16,
    data = "M5 0H11V1H14V3H16V5H13V6H10V7H8V9H10V10H13V11H16V13H14V15H11V16H5V15H2V13H0V3H2V1H5Z"
)
private val MazePlayerClosed = ArcadeSprite(
    width = 16, height = 16,
    data = "M5 0H11V1H14V3H16V7H11V9H16V13H14V15H11V16H5V15H2V13H0V3H2V1H5Z"
)

private val MazeGhost = ArcadeSprite(
    width = 16,
    height = 16,
    data = "M5 0H11V1H13V3H15V6H16V16H13V14H11V16H8V14H6V16H3V14H0V6H1V3H3V1H5Z"
)

private val MazeGhostStep = ArcadeSprite(
    width = 16,
    height = 16,
    data = "M5 0H11V1H13V3H15V6H16V14H14V16H11V14H8V16H6V14H3V16H0V6H1V3H3V1H5Z"
)

private fun DrawScope.drawMazeBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 151f).toInt()
    val rows = ceil(height / 122f).toInt()
    val ink = style.detailColor.copy(alpha = 0.13f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val x = column * 151f + 12f + row % 2 * 18f
        val y = row * 122f + 16f
        val path = Path().apply {
            moveTo(x, y + 53f)
            lineTo(x, y)
            lineTo(x + 74f, y)
            lineTo(x + 74f, y + 18f)
            lineTo(x + 23f, y + 18f)
            lineTo(x + 23f, y + 39f)
            lineTo(x + 93f, y + 39f)
            lineTo(x + 93f, y + 62f)
            lineTo(x + 43f, y + 62f)
            lineTo(x + 43f, y + 51f)
        }
        drawPath(path, ink.copy(alpha = 0.04f), style = Stroke(5f))
        drawPath(path, ink, style = Stroke(0.9f))
    }
}

@Serializable
private data class MazeEnemy(
    var position: Int,
    var previous: Int,
    var elapsed: Float = 0f,
    var cooldown: Float = 0f
)

@Serializable
private class MazeChaserRun : ArcadeRun() {
    val columns = 13
    var rows = 9
    var walls = BooleanArray(columns * rows) { true }
    var dots = IntArray(walls.size)
    val enemies = mutableListOf<MazeEnemy>()

    @Transient
    val effects = ArcadeEffects()
    var player = columns + 1
    var previous = player
    var direction = 0
    var queued = 0
    var lives = 3
    var round = 1
    var time = 0f
    var power = 0f
    var bonus = -1
        private set
    private var bonusTime = 0f
    private var bonusStage = 0
    private var lastBonus = -1
    private var eaten = 0
    private var totalDots = 0
    var invulnerable = 0f
    var transition = 0f
    private var elapsed = 0f
    private var ghostChain = 0
    private val playerInterval get() = (0.145f - round.coerceAtMost(12) * 0.002f)
    private val enemyInterval get() = (0.25f - round.coerceAtMost(15) * 0.005f) * if (power > 0f) 1.55f else 1f
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 48f) / rows)
    val origin
        get() = Offset(
            x = (width - columns * cell) / 2f,
            y = topInset + (height - topInset - rows * cell) / 2f
        )
    private val tunnelRow get() = (rows / 2) or 1
    val playerPoint get() = movingPoint(previous, player, elapsed / playerInterval)

    fun center(index: Int) =
        origin + Offset(x = (index % columns + 0.5f) * cell, y = (index / columns + 0.5f) * cell)

    fun enemyPoint(enemy: MazeEnemy) =
        movingPoint(enemy.previous, enemy.position, enemy.elapsed)

    private fun movingPoint(from: Int, to: Int, progress: Float): Offset {
        val amount = progress.coerceIn(0f, 1f)
        if (from / columns != tunnelRow || to / columns != tunnelRow || abs(to - from) <= 1) {
            return center(from) + (center(to) - center(from)) * amount
        }
        val dx = if (to > from) -1f else 1f
        val x = ((from % columns + dx * amount + 0.5f + columns) % columns) - 0.5f
        return origin + Offset((x + 0.5f) * cell, (tunnelRow + 0.5f) * cell)
    }

    override fun isValid(): Boolean = super.isValid() &&
            rows in 7..19 &&
            walls.size == columns * rows &&
            dots.size == walls.size &&
            player in walls.indices &&
            previous in walls.indices &&
            enemies.all { it.position in walls.indices && it.previous in walls.indices }

    override fun reset() {
        round = 1
        lives = 3
        time = 0f
        lastBonus = -1
        effects.clear()
        newRound()
    }

    private fun newRound() {
        animateLevel()
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
                listOf(
                    Offset(x = 2f, y = 0f),
                    Offset(x = -2f, y = 0f),
                    Offset(x = 0f, y = 2f),
                    Offset(x = 0f, y = -2f)
                ).filter {
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
        walls[tunnelRow * columns] = false
        walls[(tunnelRow + 1) * columns - 1] = false
        dots = IntArray(walls.size) { if (walls[it]) 0 else 1 }
        listOf(
            columns + 1,
            columns * 2 - 2,
            (rows - 2) * columns + 1,
            (rows - 1) * columns - 2
        ).forEach { dots[it] = 2 }
        dots[columns + 1] = 0
        totalDots = dots.count { it > 0 }
        eaten = 0
        bonusStage = 0
        bonus = -1
        bonusTime = 0f
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
        ghostChain = 0
        invulnerable = if (protect) 1.8f else 0f
        enemies.clear()
        val choices =
            walls.indices.filter { !walls[it] && abs(it % columns - 1) + abs(it / columns - 1) > (columns + rows) / 3 }
        val spawns = choices.shuffled().take((1 + (round - 1) / 3).coerceAtMost(3))
        spawns.forEach { enemies += MazeEnemy(position = it, previous = it, cooldown = 1.2f) }
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
        if (y !in 0 until rows) return -1
        val column = if (y == tunnelRow) (x + columns) % columns else x
        if (column !in 0 until columns) return -1
        return (y * columns + column).takeUnless { walls[it] } ?: -1
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

    fun distances(target: Int): IntArray {
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
        bonusTime = (bonusTime - seconds).coerceAtLeast(0f)
        if (bonusTime == 0f) bonus = -1
        invulnerable = (invulnerable - seconds).coerceAtLeast(0f)
        elapsed += seconds
        if (elapsed >= playerInterval) {
            elapsed -= playerInterval
            if (player == bonus) {
                score += 100 + round.coerceAtMost(16) * 25
                bonus = -1
                bonusTime = 0f
                effects.burst(point = center(player), count = 14)
            }
            if (dots[player] > 0) {
                if (dots[player] == 2) {
                    power = (6f - (round - 1) * 0.2f).coerceAtLeast(3f)
                    ghostChain = 0
                    score += 25
                    effects.burst(point = center(player))
                } else score += 5
                dots[player] = 0
                eaten++
                if (bonusStage < 2 && eaten >= totalDots * (bonusStage + 1) / 3) {
                    bonusStage++
                    val distance = distances(player)
                    val limit = (7f / playerInterval).toInt()
                    bonus = walls.indices.filter {
                        !walls[it] && it != lastBonus && distance[it] in 4..limit
                    }.randomOrNull() ?: -1
                    lastBonus = bonus
                    bonusTime = 9f
                }
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
                val corner = when (index) {
                    0 -> columns * 2 - 2
                    1 -> (rows - 2) * columns + 1
                    else -> (rows - 1) * columns - 2
                }
                var ahead = player
                repeat(if (index == 1) 4 else 2) {
                    ahead = next(ahead, direction).takeIf { it >= 0 } ?: ahead
                }
                val target = when {
                    scatter -> corner
                    index == 0 -> player
                    index == 1 -> ahead
                    distances(player)[enemy.position] < 6 -> corner
                    else -> ahead
                }
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
                    score += 100 * (1 shl ghostChain.coerceAtMost(3))
                    ghostChain++
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

}