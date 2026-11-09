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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.luminance
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.arcadeJoystick
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

internal const val TraceColumns = 78
private const val TraceRows = 64
private const val TraceCells = TraceColumns * TraceRows
private const val TraceRiders = 7

@Composable
fun TraceAndClaimGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(initialState, ::TraceAndClaimRun)
    val trail = remember { Path() }
    val dark = style.textColor.luminance() > 0.5f
    val colors = remember(style.playerColor, dark) {
        if (dark) {
            listOf(
                style.playerColor, Color(0xFFFF8980), Color(0xFFFFC168), Color(0xFF60D5A7),
                Color(0xFF6BCCFF), Color(0xFFF18FC6), Color(0xFFB2CE73)
            )
        } else {
            listOf(
                style.playerColor, Color(0xFFBA3C38), Color(0xFFAE6700), Color(0xFF008456),
                Color(0xFF087EAA), Color(0xFFAA3D84), Color(0xFF607A18)
            )
        }
    }
    ArcadeBoard(
        game = ArcadeGame.TraceAndClaim,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver,
        isPaused = isPaused
    ) {
        ArcadeField(
            run = run,
            onPress = run::grab,
            onDrag = run::steer,
            onRelease = run::release
        ) {
            val viewHeight = run.height - run.fieldTop
            val cell =
                maxOf(run.width / 50f, viewHeight / 35f, minOf(run.width / 29f, viewHeight / 18f))
            val player = run.riders.first()
            val camera = player.position(run.interpolation) * cell
            val view = Size(run.width, viewHeight)
            val world = Size(TraceColumns * cell, TraceRows * cell)
            val scroll = Offset(
                (camera.x - view.width / 2f).coerceIn(0f, world.width - view.width),
                (camera.y - view.height / 2f).coerceIn(0f, world.height - view.height)
            )
            val origin = Offset(0f, run.fieldTop) - scroll
            val left = floor(scroll.x / cell).toInt()
            val right = ceil((scroll.x + view.width) / cell).toInt().coerceAtMost(TraceColumns)
            val top = floor(scroll.y / cell).toInt()
            val bottom = ceil((scroll.y + view.height) / cell).toInt().coerceAtMost(TraceRows)
            fun center(index: Int): Offset = origin + traceCenter(index) * cell

            clipRect(left = 0f, top = run.fieldTop, right = run.width, bottom = run.height) {
                drawRect(style.detailColor.copy(alpha = 0.08f), origin, world)
                for (row in top until bottom) {
                    var col = left
                    while (col < right) {
                        val owner = run.territory[row * TraceColumns + col]
                        val start = col++
                        while (col < right && run.territory[row * TraceColumns + col] == owner) col++
                        if (owner == 0) continue
                        drawRect(
                            color = colors[owner - 1].copy(alpha = if (owner == 1) 0.47f else 0.34f),
                            topLeft = origin + Offset(start * cell, row * cell),
                            size = Size((col - start) * cell, cell)
                        )
                    }
                }
                for (col in left..right) {
                    val tint = style.detailColor.copy(alpha = if (col % 5 == 0) 0.30f else 0.15f)
                    val x = origin.x + col * cell
                    drawLine(tint, Offset(x, run.fieldTop), Offset(x, run.height), 0.7f)
                }
                for (row in top..bottom) {
                    val tint = style.detailColor.copy(alpha = if (row % 5 == 0) 0.30f else 0.15f)
                    val y = origin.y + row * cell
                    drawLine(tint, Offset(0f, y), Offset(run.width, y), 0.7f)
                }
                drawRect(style.hazardColor.copy(alpha = 0.8f), origin, world, style = Stroke(4f))

                for (rider in run.riders) {
                    if (rider.cell < 0) continue
                    val tint = colors[rider.id - 1]
                    val point = origin + rider.position(run.interpolation) * cell
                    if (rider.trail.isNotEmpty()) {
                        trail.rewind()
                        val start = center(rider.trailStart)
                        trail.moveTo(start.x, start.y)
                        for (index in 0 until rider.trail.lastIndex) {
                            val turn = center(rider.trail[index])
                            trail.lineTo(turn.x, turn.y)
                        }
                        trail.lineTo(point.x, point.y)
                        drawPath(
                            path = trail,
                            color = tint,
                            style = Stroke(
                                cell * 0.45f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    val radius = cell * if (rider === player) 0.59f else 0.50f
                    drawRoundRect(
                        color = tint,
                        topLeft = point - Offset(radius, radius),
                        size = Size(radius * 2f, radius * 2f),
                        cornerRadius = CornerRadius(radius * 0.46f)
                    )
                    val direction = rider.direction ?: TraceDirection.Up
                    drawCircle(
                        color = if (rider === player) style.playerDetailColor else Color.White,
                        radius = radius * 0.29f,
                        center = point + Offset(
                            direction.x * radius * 0.36f,
                            direction.y * radius * 0.36f
                        )
                    )
                }
                arcadeJoystick(style, run.touchOrigin, run.stick)
            }

            arcadeLives(style, run.lives, run.topInset / 2f)
            val progressStart = Offset(14f, run.fieldTop - 4f)
            val progressWidth = run.width - 28f
            drawLine(
                color = style.detailColor,
                start = progressStart,
                end = progressStart + Offset(progressWidth, 0f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = colors.first(),
                start = progressStart,
                end = progressStart + Offset(progressWidth * run.progress, 0f),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Serializable
internal enum class TraceDirection(val x: Int, val y: Int) {
    Up(0, -1), Right(1, 0), Down(0, 1), Left(-1, 0);

    val opposite: TraceDirection
        get() = entries[(ordinal + 2) % 4]

    fun move(cell: Int): Int {
        val col = cell % TraceColumns + x
        val row = cell / TraceColumns + y
        return if (col in 0 until TraceColumns && row in 0 until TraceRows) row * TraceColumns + col else -1
    }
}

@Serializable
internal class TraceRider(val id: Int) {
    var cell = -1
    var previous = -1
    var direction: TraceDirection? = null
    var trailStart = -1
    val trail = mutableListOf<Int>()
    var respawn = 0
}

@Serializable
internal class TraceAndClaimRun : ArcadeRun() {
    val territory = IntArray(TraceCells)
    val riders = List(TraceRiders) { TraceRider(it + 1) }
    private var arena = 0
    private var ticks = 0
    private var timer = 0f
    private var owned = 0
    var lives = 3
        private set

    @Transient
    var touchOrigin: Offset? = null
        private set

    @Transient
    var stick = Offset.Zero
        private set

    @Transient
    private val turns = ArrayDeque<TraceDirection>(2)

    @Transient
    private val trails = IntArray(TraceCells)

    @Transient
    private val targets = IntArray(TraceRiders)

    @Transient
    private val dead = BooleanArray(TraceRiders)

    @Transient
    private val queue = IntArray(TraceCells)

    @Transient
    private val visited = BooleanArray(TraceCells)

    @Transient
    private val firstTurns = IntArray(TraceCells)

    private val player: TraceRider
        get() = riders.first()
    private val claimTarget: Int
        get() = TraceCells * 17 / 100
    private val interval: Float
        get() = 0.169f - 0.052f * (arena + ticks / 220f).let { it / (it + 4f) }
    private val botCount: Int
        get() = 4 + (arena / 2 + ticks / 300).coerceAtMost(TraceRiders - 5)

    val fieldTop: Float
        get() = topInset + 8f
    val progress: Float
        get() = (owned.toFloat() / claimTarget).coerceIn(0f, 1f)
    val interpolation: Float
        get() = (timer / interval).coerceIn(0f, 1f)

    override fun reset() {
        lives = 3
        arena = 0
        resetArena()
    }

    private fun resetArena() {
        timer = 0f
        ticks = 0
        owned = 0
        turns.clear()
        territory.fill(0)
        trails.fill(0)
        for (rider in riders) {
            rider.cell = -1
            rider.previous = -1
            rider.direction = null
            rider.trail.clear()
            rider.trailStart = -1
            rider.respawn = 0
        }
        spawn(player, 39 + 32 * TraceColumns, null, radius = 2)
        spawn(riders[1], 29 + 25 * TraceColumns, TraceDirection.Right, radius = 2)
        spawn(riders[2], 48 + 24 * TraceColumns, TraceDirection.Down, radius = 2)
        spawn(riders[3], 30 + 40 * TraceColumns, TraceDirection.Up, radius = 2)
        spawn(riders[4], 49 + 42 * TraceColumns, TraceDirection.Left, radius = 2)
    }

    override fun stopInput() {
        release()
        turns.clear()
    }

    fun grab(point: Offset) {
        if (point.y >= fieldTop) touchOrigin = point
    }

    fun release() {
        touchOrigin = null
        stick = Offset.Zero
    }

    override fun isValid(): Boolean = super.isValid() &&
            territory.size == TraceCells &&
            riders.size == TraceRiders &&
            riders.all { rider ->
                rider.cell in -1 until TraceCells && rider.previous in -1 until TraceCells &&
                        rider.trailStart in -1 until TraceCells &&
                        rider.trail.all { it in territory.indices }
            }

    override fun onRestore() {
        for (rider in riders) for (cell in rider.trail) trails[cell] = rider.id
    }

    fun steer(point: Offset) {
        val origin = touchOrigin ?: return
        var delta = point - origin
        val distance = delta.getDistance()
        if (distance > 32f) {
            delta *= 32f / distance
            touchOrigin = point - delta
        }
        stick = delta
        if (distance < 8f || abs(abs(delta.x) - abs(delta.y)) < 3f) return

        val next = if (abs(delta.x) > abs(delta.y)) {
            if (delta.x > 0f) TraceDirection.Right else TraceDirection.Left
        } else {
            if (delta.y > 0f) TraceDirection.Down else TraceDirection.Up
        }
        val current = turns.lastOrNull() ?: player.direction
        if (player.direction == null) {
            player.direction = next
        } else if (turns.size < 2 && next != current && next != current?.opposite) {
            turns.addLast(next)
        }
    }

    override fun advance(seconds: Float) {
        if (player.direction == null) return
        timer += seconds
        val step = interval
        if (timer >= step) {
            timer -= step
            tick()
        }
    }

    private fun tick() {
        ticks++
        turns.removeFirstOrNull()?.let { player.direction = it }
        targets.fill(-1)
        dead.fill(false)
        for ((index, rider) in riders.withIndex()) {
            if (rider.cell < 0) continue
            if (rider !== player) rider.direction = chooseDirection(rider)
            targets[index] = rider.direction?.move(rider.cell) ?: -1
        }

        var cut = 0
        for ((index, rider) in riders.withIndex()) {
            if (rider.cell < 0) continue
            val target = targets[index]
            if (target < 0) {
                dead[index] = true
                continue
            }
            val trail = trails[target]
            if (trail != 0) {
                dead[trail - 1] = true
                if (rider === player && trail != 1) cut = trail
            }
            for (other in index + 1 until riders.size) {
                if (riders[other].cell < 0) continue
                if (target == targets[other]) {
                    if (territory[target] != rider.id) dead[index] = true
                    if (territory[target] != riders[other].id) dead[other] = true
                } else if (target == riders[other].cell && targets[other] == rider.cell) {
                    dead[index] = true
                    dead[other] = true
                }
            }
        }
        if (dead[0] && --lives == 0) {
            clearTrail(player)
            finish()
            return
        }

        for ((index, rider) in riders.withIndex()) {
            if (dead[index]) {
                clearTrail(rider)
                rider.cell = -1
                if (rider === player) continue
                for (cell in territory.indices) if (territory[cell] == rider.id) territory[cell] = 0
                rider.respawn = 12
                if (rider.id == cut) score += 30
            } else if (rider.cell >= 0) {
                rider.previous = rider.cell
                rider.cell = targets[index]
            }
        }
        for (rider in riders) {
            if (rider.cell < 0) continue
            if (territory[rider.cell] == rider.id) {
                if (rider.trail.isNotEmpty()) claim(rider)
            } else {
                if (rider.trail.isEmpty()) rider.trailStart = rider.previous
                rider.trail += rider.cell
                trails[rider.cell] = rider.id
            }
        }

        if (dead[0]) {
            respawnPlayer()
            animateLevel()
            return
        }
        if (owned >= claimTarget) {
            score += 100
            arena++
            resetArena()
            animateLevel()
            return
        }
        for (index in 1..botCount) {
            val rider = riders[index]
            if (rider.cell < 0 && --rider.respawn <= 0) respawn(rider)
        }
    }

    private fun claim(rider: TraceRider) {
        // The rider's land and closed trail act as walls for a flood from the perimeter.
        visited.fill(false)
        var end = 0
        fun visit(cell: Int) {
            if (cell >= 0 && !visited[cell] && territory[cell] != rider.id && trails[cell] != rider.id) {
                visited[cell] = true
                queue[end++] = cell
            }
        }
        for (col in 0 until TraceColumns) {
            visit(col)
            visit(TraceCells - TraceColumns + col)
        }
        for (row in 1 until TraceRows - 1) {
            visit(row * TraceColumns)
            visit(row * TraceColumns + TraceColumns - 1)
        }
        var start = 0
        while (start < end) {
            val cell = queue[start++]
            for (direction in TraceDirection.entries) visit(direction.move(cell))
        }
        for (cell in territory.indices) {
            if (visited[cell] || territory[cell] == rider.id) continue
            if (rider === player) {
                owned++
                score++
            } else if (territory[cell] == player.id) {
                owned--
            }
            territory[cell] = rider.id
        }
        clearTrail(rider)
    }

    private fun clearTrail(rider: TraceRider) {
        for (cell in rider.trail) {
            if (trails[cell] == rider.id) trails[cell] = 0
        }
        rider.trail.clear()
        rider.trailStart = -1
    }

    private fun chooseDirection(rider: TraceRider): TraceDirection? {
        val heading = rider.direction ?: return null
        val reach = 3 + (arena + ticks / 160).coerceAtMost(8)
        val length = rider.trail.size
        val targetLength = 8 + rider.id % 4 * 2 + arena.coerceAtMost(6)
        if (length >= targetLength || length >= 4 && heading.move(rider.cell) < 0) {
            return homeDirection(rider) ?: heading
        }
        var victim = -1
        var closest = reach + 1
        if (length < targetLength / 2) {
            for (cell in player.trail) {
                val distance = traceDistance(rider.cell, cell)
                if (distance < closest) {
                    victim = cell
                    closest = distance
                }
            }
        }
        var best = Float.NEGATIVE_INFINITY
        var chosen: TraceDirection? = null
        for (direction in TraceDirection.entries) {
            val next = direction.move(rider.cell)
            if (direction == heading.opposite || next < 0 || trails[next] == rider.id) continue
            var value = Random.nextFloat() * 3f
            if (direction == heading) value += 2f
            if (territory[next] != rider.id) value += if (length < 4) 3f else 0.5f
            if (victim >= 0) value -= traceDistance(next, victim) * 4f
            if (direction.move(next) < 0) value -= 1.5f
            if (value > best) {
                best = value
                chosen = direction
            }
        }
        return chosen ?: homeDirection(rider) ?: heading
    }

    private fun homeDirection(rider: TraceRider): TraceDirection? {
        visited.fill(false)
        visited[rider.cell] = true
        var start = 0
        var end = 0
        for (direction in TraceDirection.entries) {
            val next = direction.move(rider.cell)
            if (next < 0 || trails[next] == rider.id) continue
            queue[end++] = next
            visited[next] = true
            firstTurns[next] = direction.ordinal
        }
        while (start < end) {
            val cell = queue[start++]
            if (territory[cell] == rider.id) return TraceDirection.entries[firstTurns[cell]]
            for (direction in TraceDirection.entries) {
                val next = direction.move(cell)
                if (next < 0 || visited[next] || trails[next] == rider.id) continue
                queue[end++] = next
                visited[next] = true
                firstTurns[next] = firstTurns[cell]
            }
        }
        return null
    }

    private fun respawnPlayer() {
        var safest = -1
        var best = Int.MIN_VALUE
        for (cell in territory.indices) {
            if (territory[cell] != player.id || trails[cell] != 0) continue
            var clearance = 8
            for (index in 1 until riders.size) {
                val rival = riders[index].cell
                if (rival >= 0) clearance = minOf(clearance, traceDistance(cell, rival))
            }
            if (clearance == 0) continue
            val score = clearance * TraceColumns - traceDistance(cell, player.previous)
            if (score > best) {
                best = score
                safest = cell
            }
        }

        if (safest < 0) {
            resetArena()
        } else {
            spawn(player, safest, null, radius = 0)
            timer = 0f
        }
    }

    private fun respawn(rider: TraceRider) {
        repeat(24) {
            val x = Random.nextInt(2, TraceColumns - 2)
            val y = Random.nextInt(2, TraceRows - 2)
            val cell = y * TraceColumns + x
            if (riders.any { it.cell >= 0 && traceDistance(it.cell, cell) < 6 }) return@repeat
            for (row in y - 1..y + 1) for (col in x - 1..x + 1) {
                val index = row * TraceColumns + col
                if (territory[index] != 0 || trails[index] != 0) return@repeat
            }
            spawn(rider, cell, TraceDirection.entries.random())
            return
        }
        rider.respawn = 8
    }

    private fun spawn(rider: TraceRider, cell: Int, heading: TraceDirection?, radius: Int = 1) {
        rider.cell = cell
        rider.previous = cell
        rider.direction = heading
        val x = cell % TraceColumns
        val y = cell / TraceColumns
        for (row in y - radius..y + radius) for (col in x - radius..x + radius) {
            val index = row * TraceColumns + col
            if (rider === player && territory[index] != rider.id) owned++
            territory[index] = rider.id
        }
    }
}

private fun traceDistance(a: Int, b: Int): Int =
    abs(a % TraceColumns - b % TraceColumns) + abs(a / TraceColumns - b / TraceColumns)

private fun traceCenter(cell: Int) =
    Offset(cell % TraceColumns + 0.5f, cell / TraceColumns + 0.5f)

private fun TraceRider.position(interpolation: Float): Offset {
    val from = traceCenter(previous)
    return from + (traceCenter(cell) - from) * interpolation
}
