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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.arcadeJoystick
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val TraceSide = 96
private const val TraceCells = TraceSide * TraceSide
private const val TraceRiders = 7
private const val TraceRadius = TraceSide / 2f - 0.65f
private val TraceCenter = Offset(TraceSide / 2f, TraceSide / 2f)
private val TracePlayable = BooleanArray(TraceCells) {
    (traceCenter(it) - TraceCenter).getDistanceSquared() <= TraceRadius * TraceRadius
}
private val TracePerimeter = TracePlayable.indices.filter { cell ->
    TracePlayable[cell] && TraceDirection.entries.any { it.move(cell) < 0 }
}.toIntArray()
private val TraceClaimTarget = TracePlayable.count { it }
private const val TraceTravelRadius = TraceRadius - 0.72f
private const val TraceTrailWidth = 0.68f
private const val TraceTurn = 2f * PI.toFloat()
private val TraceDots = buildList {
    val random = Random(98)
    for (row in 0 until TraceSide step 3) for (col in 0 until TraceSide step 3) {
        val point =
            Offset(col + 0.7f + random.nextFloat() * 1.6f, row + 0.7f + random.nextFloat() * 1.6f)
        if ((point - TraceCenter).getDistanceSquared() < TraceRadius * TraceRadius) add(point)
    }
}

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
    val arena = remember { Path() }
    val land = remember { TraceLand() }
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
            val cell = maxOf(
                run.width / 58f,
                run.height / 52f,
                minOf(run.width / 41f, run.height / 24f)
            )
            val player = run.riders.first()
            val camera = player.point * cell
            val world = TraceSide * cell
            val scroll = Offset(
                (camera.x - run.width / 2f).coerceIn(0f, world - run.width),
                (camera.y - run.height / 2f).coerceIn(0f, world - run.height)
            )
            val origin = -scroll
            val center = origin + TraceCenter * cell
            val radius = TraceRadius * cell
            arena.rewind()
            arena.addOval(Rect(center, radius))
            land.update(run)
            clipPath(arena) {
                drawCircle(style.detailColor.copy(alpha = 0.12f), radius, center)
                translate(origin.x, origin.y) {
                    scale(cell, Offset.Zero) {
                        for (index in land.paths.indices) {
                            drawPath(
                                path = land.paths[index],
                                color = colors[index].copy(alpha = if (index == 0) 0.68f else 0.51f)
                            )
                        }
                        drawPoints(
                            points = TraceDots,
                            pointMode = PointMode.Points,
                            color = style.textColor.copy(alpha = 0.17f),
                            strokeWidth = 0.18f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                for (rider in run.riders) {
                    if (rider.cell < 0) continue
                    val tint = colors[rider.id - 1]
                    val point = origin + rider.point * cell
                    if (rider.route.isNotEmpty()) {
                        trail.rewind()
                        val first = origin + rider.route.first() * cell
                        trail.moveTo(first.x, first.y)
                        for (index in 1 until rider.route.size) {
                            val segment = origin + rider.route[index] * cell
                            trail.lineTo(segment.x, segment.y)
                        }
                        trail.lineTo(point.x, point.y)
                        drawPath(
                            path = trail,
                            color = tint,
                            style = Stroke(
                                cell * TraceTrailWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                    val radius = cell * if (rider === player) 0.97f else 0.88f
                    drawRoundRect(
                        color = tint,
                        topLeft = point - Offset(radius, radius),
                        size = Size(radius * 2f, radius * 2f),
                        cornerRadius = CornerRadius(radius * 0.46f)
                    )
                    val direction = if (rider === player) run.heading else rider.heading
                    drawCircle(
                        color = if (rider === player) style.playerDetailColor else Color.White,
                        radius = radius * 0.29f,
                        center = point + Offset(
                            direction.x * radius * 0.36f,
                            direction.y * radius * 0.36f
                        )
                    )
                }
            }
            drawCircle(style.borderColor, radius, center, style = Stroke(2.4f))
            arcadeJoystick(style, run.touchOrigin, run.stick)

            arcadeLives(style, run.lives, run.topInset / 2f)
            val progressStart = Offset(14f, run.topInset - 3f)
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

private enum class TraceDirection(val x: Int, val y: Int) {
    Up(0, -1), Right(1, 0), Down(0, 1), Left(-1, 0);

    fun move(cell: Int): Int {
        val col = cell % TraceSide + x
        val row = cell / TraceSide + y
        if (col !in 0 until TraceSide || row !in 0 until TraceSide) return -1
        val next = row * TraceSide + col
        return if (TracePlayable[next]) next else -1
    }
}

@Serializable
internal class TraceRider(val id: Int) {
    var cell = -1
    var point = Offset.Zero
    var circleCenter = -1
    var circleRadius = 0
    var angle = 0f
    var curve = 0f
    var home = -1
    var target = -1
    var reconsider = 0f

    val heading: Offset
        get() = Offset(cos(angle), sin(angle))
    val trail = mutableListOf<Int>()
    val route = mutableListOf<Offset>()
    var respawn = 0f
}

@Serializable
internal class TraceAndClaimRun : ArcadeRun() {
    val territory = IntArray(TraceCells)
    val riders = List(TraceRiders) { TraceRider(it + 1) }
    private var arena = 0
    private var elapsed = 0f
    var heading = Offset.Zero
        private set
    private var owned = 0
    var lives = 3
        private set

    @Transient
    var landRevision = 0
        private set

    @Transient
    var touchOrigin: Offset? = null
        private set

    @Transient
    var stick = Offset.Zero
        private set

    @Transient
    private val trails = IntArray(TraceCells)

    @Transient
    private val targets = IntArray(TraceRiders)

    @Transient
    private val corners = IntArray(TraceRiders)

    @Transient
    private val positions = Array(TraceRiders) { Offset.Zero }

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
    private val difficulty: Float
        get() = arena + elapsed / 34f
    private val speed: Float
        get() = 11.6f + 3.6f * difficulty / (difficulty + 4f)
    private val botCount: Int
        get() = 5 + (arena / 2 + (elapsed / 22f).toInt()).coerceAtMost(TraceRiders - 6)

    val progress: Float
        get() = (owned.toFloat() / TraceClaimTarget).coerceIn(0f, 1f)

    override fun reset() {
        lives = 3
        arena = 0
        resetArena()
    }

    private fun resetArena() {
        elapsed = 0f
        heading = Offset.Zero
        owned = 0
        territory.fill(0)
        trails.fill(0)
        for (rider in riders) {
            rider.cell = -1
            rider.circleRadius = 0
            rider.trail.clear()
            rider.route.clear()
            rider.respawn = 0f
        }
        spawn(player, 48 + 48 * TraceSide, radius = 4)
        spawn(riders[1], 38 + 33 * TraceSide, radius = 5)
        spawn(riders[2], 68 + 29 * TraceSide, radius = 5)
        spawn(riders[3], 26 + 66 * TraceSide, radius = 5)
        spawn(riders[4], 59 + 63 * TraceSide, radius = 5)
        spawn(riders[5], 47 + 77 * TraceSide, radius = 4)
    }

    override fun stopInput() = release()

    fun grab(point: Offset) {
        touchOrigin = point
    }

    fun release() {
        touchOrigin = null
        stick = Offset.Zero
    }

    override fun isValid(): Boolean = super.isValid() &&
            territory.size == TraceCells &&
            riders.size == TraceRiders &&
            riders.all { rider ->
                (rider.cell == -1 || TracePlayable.getOrElse(rider.cell) { false }) &&
                        rider.circleRadius in 0..5 &&
                        (rider.circleRadius == 0 || TracePlayable.getOrElse(rider.circleCenter) { false }) &&
                        rider.home in -1 until TraceCells &&
                        rider.target in -1 until TraceCells &&
                        rider.angle.isFinite() && rider.curve.isFinite() &&
                        rider.trail.all { it in territory.indices } &&
                        rider.trail.isEmpty() == rider.route.isEmpty() &&
                        rider.route.all { it.x.isFinite() && it.y.isFinite() }
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
        if (distance >= 8f) heading = delta / minOf(distance, 32f)
    }

    override fun advance(seconds: Float) {
        if (heading == Offset.Zero) return
        elapsed += seconds
        val step = speed * seconds
        dead.fill(false)
        for ((index, rider) in riders.withIndex()) {
            if (rider.cell < 0) continue
            val point = if (rider === player) {
                traceInside(rider.point + heading * step)
            } else {
                moveBot(rider, step, seconds)
            }
            val cell = traceCell(point)
            targets[index] = cell
            positions[index] = point
            corners[index] =
                if (cell >= 0) traceCorner(rider.point, point, rider.cell, cell) else -1
        }

        var cut = 0
        for ((index, rider) in riders.withIndex()) {
            if (rider.cell < 0) continue
            val target = targets[index]
            if (target < 0) {
                dead[index] = true
                continue
            }
            for ((other, opponent) in riders.withIndex()) {
                if (other == index || opponent.cell < 0 || opponent.route.isEmpty()) continue
                if (cutsRoute(positions[index], opponent)) {
                    dead[other] = true
                    if (rider === player) cut = opponent.id
                }
            }
            for (other in index + 1 until riders.size) {
                val opponent = riders[other]
                if (opponent.cell < 0 || targets[other] < 0) continue
                val difference = positions[index] - positions[other]
                if (difference.getDistanceSquared() >= 2.25f) continue
                if (territory[target] != rider.id) dead[index] = true
                if (territory[targets[other]] != opponent.id) dead[other] = true
            }
        }
        if (dead[0] && --lives == 0) {
            clearTrail(player)
            finish()
            return
        }
        for ((index, rider) in riders.withIndex()) {
            if (!dead[index]) continue
            clearTrail(rider)
            rider.cell = -1
            if (rider === player) continue
            rider.circleRadius = 0
            for (cell in territory.indices) if (territory[cell] == rider.id) territory[cell] = 0
            landRevision++
            rider.respawn = 1.6f + Random.nextFloat() * 1.8f
            if (rider.id == cut) score += 30
        }
        for ((index, rider) in riders.withIndex()) {
            if (rider.cell < 0) continue
            val corner = corners[index]
            if (corner >= 0) enter(rider, corner)
            val target = targets[index]
            if (target != rider.cell) enter(rider, target)
            rider.point = positions[index]
            if (rider.trail.isNotEmpty()) recordRoute(rider)
        }

        if (dead[0]) {
            respawnPlayer()
            heading = Offset.Zero
            animateLevel()
            return
        }
        val lastRivalEliminated = (1 until riders.size).any { dead[it] } &&
                (1 until riders.size).none { riders[it].cell >= 0 }
        if (owned >= TraceClaimTarget || lastRivalEliminated) {
            score += 100
            arena++
            resetArena()
            animateLevel()
            return
        }
        for (index in 1..botCount) {
            val rider = riders[index]
            if (rider.cell >= 0) continue
            rider.respawn -= seconds
            if (rider.respawn <= 0f) respawn(rider)
        }
    }

    private fun moveBot(rider: TraceRider, step: Float, seconds: Float): Offset {
        val radial = rider.point - TraceCenter
        val radius = radial.getDistance()
        val length = rider.trail.size
        val limit = 18 + rider.id % 4 * 4 + arena.coerceAtMost(8)
        rider.reconsider -= seconds
        if (rider.reconsider <= 0f) {
            rider.reconsider = 0.24f
            if (rider.home >= 0 && territory[rider.home] != rider.id) rider.home = -1
            if (rider.home < 0 && (length >= limit || length >= 6 && radius > TraceTravelRadius - 4f)) {
                rider.home = findHome(rider)
            }
            rider.target = if (rider.home < 0 && length < limit / 2) findTrail(rider) else -1
        }

        val target = if (rider.home >= 0) rider.home else rider.target
        var turn = rider.curve + sin(elapsed * 1.9f + rider.id) * 0.4f
        var travel = step
        if (target >= 0) {
            val delta = traceCenter(target) - rider.point
            turn = traceTurn(rider.angle, delta) * 5f
            if (rider.home >= 0) {
                travel *= (delta.getDistance() / 2.6f).coerceIn(0.55f, 1f)
            }
        }
        val forward = rider.heading
        if (radius > TraceTravelRadius - 5f && radial.x * forward.x + radial.y * forward.y > 0f) {
            val inward = -radial / radius
            val side = if (radial.x * forward.y - radial.y * forward.x > 0f) 1f else -1f
            val tangent = Offset(inward.y, -inward.x) * side
            val avoid = traceTurn(rider.angle, tangent + inward * 0.55f)
            val strength = ((radius - TraceTravelRadius + 5f) / 3f).coerceIn(0f, 1f)
            turn += (avoid * 5f - turn) * strength
        }
        rider.angle = (rider.angle + turn.coerceIn(-5.4f, 5.4f) * seconds) % TraceTurn
        return traceInside(rider.point + rider.heading * travel)
    }

    private fun enter(rider: TraceRider, cell: Int) {
        if (territory[cell] == rider.id) {
            if (rider.trail.isNotEmpty()) {
                claim(rider)
                rider.home = -1
                rider.target = -1
                rider.curve = randomCurve()
            }
        } else if (trails[cell] != rider.id) {
            if (rider.trail.isEmpty()) rider.route += rider.point
            rider.trail += cell
            trails[cell] = rider.id
        }
        rider.cell = cell
    }

    private fun cutsRoute(point: Offset, rider: TraceRider): Boolean {
        val reach = TraceTrailWidth / 2f + 0.78f
        fun hits(from: Offset, to: Offset): Boolean {
            if (point.x < minOf(from.x, to.x) - reach || point.x > maxOf(from.x, to.x) + reach ||
                point.y < minOf(from.y, to.y) - reach || point.y > maxOf(from.y, to.y) + reach
            ) return false
            val line = to - from
            val distance = line.getDistanceSquared()
            val delta = point - from
            if (distance <= 0.0001f) return delta.getDistanceSquared() < reach * reach
            val along = ((delta.x * line.x + delta.y * line.y) / distance).coerceIn(0f, 1f)
            return (delta - line * along).getDistanceSquared() < reach * reach
        }
        for (index in 1 until rider.route.size) {
            if (hits(rider.route[index - 1], rider.route[index])) return true
        }
        return hits(rider.route.last(), rider.point)
    }

    private fun recordRoute(rider: TraceRider) {
        val route = rider.route
        val last = route.last()
        val delta = rider.point - last
        if (delta.getDistanceSquared() < 0.12f) return
        if (route.size >= 2) {
            val direction = last - route[route.lastIndex - 1]
            val cross = direction.x * delta.y - direction.y * delta.x
            val dot = direction.x * delta.x + direction.y * delta.y
            if (dot > 0f && cross * cross < direction.getDistanceSquared() * 0.0025f) {
                route[route.lastIndex] = rider.point
                return
            }
        }
        route += rider.point
    }

    private fun claim(rider: TraceRider) {
        visited.fill(false)
        var end = 0
        var component = 0
        var exterior = 0
        var largest = 0
        val usesRim = rider.trail.any {
            (traceCenter(it) - TraceCenter).getDistanceSquared() >= (TraceRadius - 1.45f).let { r -> r * r }
        }
        fun visit(cell: Int) {
            if (cell >= 0 && !visited[cell] && territory[cell] != rider.id && trails[cell] != rider.id) {
                visited[cell] = true
                firstTurns[cell] = component
                queue[end++] = cell
            }
        }
        if (usesRim) {
            // A trace along the rim can cut off a pocket against the wall.
            for (cell in TracePerimeter) {
                if (visited[cell]) continue
                component++
                end = 0
                visit(cell)
                var start = 0
                while (start < end) {
                    val tile = queue[start++]
                    for (direction in TraceDirection.entries) visit(direction.move(tile))
                }
                if (end > largest) {
                    largest = end
                    exterior = component
                }
            }
        } else {
            for (cell in TracePerimeter) visit(cell)
            var start = 0
            while (start < end) {
                val cell = queue[start++]
                for (direction in TraceDirection.entries) visit(direction.move(cell))
            }
        }
        for (cell in territory.indices) {
            val previous = territory[cell]
            if (!TracePlayable[cell] || previous == rider.id) continue
            if (visited[cell] && (!usesRim || firstTurns[cell] == exterior)) continue
            if (rider === player) {
                owned++
                score++
            } else if (previous == player.id) {
                owned--
            }
            if (previous != 0) riders[previous - 1].circleRadius = 0
            rider.circleRadius = 0
            territory[cell] = rider.id
        }
        landRevision++
        clearTrail(rider)
    }

    private fun clearTrail(rider: TraceRider) {
        for (cell in rider.trail) {
            if (trails[cell] == rider.id) trails[cell] = 0
        }
        rider.trail.clear()
        rider.route.clear()
    }

    private fun findTrail(rider: TraceRider): Int {
        val reach = 8f + difficulty.coerceAtMost(7f)
        var nearest = reach * reach
        var target = -1
        for (opponent in riders) {
            if (opponent === rider || opponent.cell < 0) continue
            for (cell in opponent.trail) {
                val delta = traceCenter(cell) - rider.point
                val distance = delta.getDistanceSquared()
                if (distance < nearest && abs(traceTurn(rider.angle, delta)) < 1.5f) {
                    nearest = distance
                    target = cell
                }
            }
        }
        return target
    }

    private fun findHome(rider: TraceRider): Int {
        var nearest = Float.POSITIVE_INFINITY
        var target = -1
        for (cell in territory.indices) {
            if (territory[cell] != rider.id) continue
            val delta = traceCenter(cell) - rider.point
            val distance = delta.getDistanceSquared()
            if (distance >= nearest) continue
            val edge =
                TraceDirection.entries.count { territory.getOrNull(it.move(cell)) != rider.id }
            val cost = distance + edge * 18f
            if (cost < nearest) {
                nearest = cost
                target = cell
            }
        }
        return target
    }

    private fun respawnPlayer() {
        var safest = -1
        var best = Int.MIN_VALUE
        val last = (player.point.y.toInt() * TraceSide + player.point.x.toInt()).coerceIn(
            0,
            TraceCells - 1
        )
        for (cell in territory.indices) {
            if (territory[cell] != player.id || trails[cell] != 0) continue
            var clearance = 8
            for (index in 1 until riders.size) {
                val rival = riders[index].cell
                if (rival >= 0) clearance = minOf(clearance, traceDistance(cell, rival))
            }
            if (clearance == 0) continue
            val score = clearance * TraceSide - traceDistance(cell, last)
            if (score > best) {
                best = score
                safest = cell
            }
        }

        if (safest >= 0) {
            spawn(player, safest, radius = 0)
        } else if (!spawnInFreeSpace(player)) {
            resetArena()
        }
    }

    private fun respawn(rider: TraceRider) {
        if (!spawnInFreeSpace(rider)) rider.respawn = 1.2f
    }

    private fun spawnInFreeSpace(rider: TraceRider): Boolean {
        val start = Random.nextInt(TraceCells)
        for (offset in 0 until TraceCells) {
            val cell = (start + offset) % TraceCells
            val x = cell % TraceSide
            val y = cell / TraceSide
            if (!TracePlayable[cell] || x !in 3 until TraceSide - 3 || y !in 3 until TraceSide - 3) continue
            if (territory[cell] != 0 || trails[cell] != 0) continue
            if (riders.any { it.cell >= 0 && traceDistance(it.cell, cell) < 10 }) continue
            var clear = true
            search@ for (row in y - 2..y + 2) for (col in x - 2..x + 2) {
                val tile = row * TraceSide + col
                if (!TracePlayable[tile] || territory[tile] != 0 || trails[tile] != 0) {
                    clear = false
                    break@search
                }
            }
            if (!clear) continue
            spawn(rider, cell, radius = 2)
            return true
        }
        return false
    }

    private fun spawn(rider: TraceRider, cell: Int, radius: Int) {
        rider.cell = cell
        rider.point = traceCenter(cell)
        rider.angle = Random.nextFloat() * TraceTurn
        rider.curve = randomCurve()
        rider.home = -1
        rider.target = -1
        rider.reconsider = 0f
        if (radius > 0) {
            rider.circleCenter = cell
            rider.circleRadius = if (rider !== player || owned == 0) radius else 0
        }
        val x = cell % TraceSide
        val y = cell / TraceSide
        for (row in y - radius..y + radius) for (col in x - radius..x + radius) {
            val dx = col - x
            val dy = row - y
            if (dx * dx + dy * dy > radius * radius) continue
            val index = row * TraceSide + col
            if (rider === player && territory[index] != rider.id) owned++
            territory[index] = rider.id
        }
        landRevision++
    }
}

private fun randomCurve(): Float =
    (0.56f + Random.nextFloat() * 0.53f) * if (Random.nextBoolean()) 1f else -1f

private fun traceTurn(angle: Float, delta: Offset): Float {
    val difference = atan2(delta.y, delta.x) - angle
    return atan2(sin(difference), cos(difference))
}

private fun traceInside(point: Offset): Offset {
    val radial = point - TraceCenter
    val distance = radial.getDistance()
    return if (distance <= TraceTravelRadius) point else TraceCenter + radial * (TraceTravelRadius / distance)
}

private fun traceDistance(a: Int, b: Int): Int =
    abs(a % TraceSide - b % TraceSide) + abs(a / TraceSide - b / TraceSide)

private fun traceCenter(cell: Int) =
    Offset(cell % TraceSide + 0.5f, cell / TraceSide + 0.5f)

private fun traceCell(point: Offset): Int {
    if ((point - TraceCenter).getDistanceSquared() > TraceRadius * TraceRadius) return -1
    val cell = point.y.toInt() * TraceSide + point.x.toInt()
    return if (TracePlayable[cell]) cell else -1
}

private fun traceCorner(from: Offset, to: Offset, start: Int, end: Int): Int {
    val startCol = start % TraceSide
    val startRow = start / TraceSide
    val endCol = end % TraceSide
    val endRow = end / TraceSide
    if (startCol == endCol || startRow == endRow) return -1
    val vertical = maxOf(startCol, endCol).toFloat()
    val horizontal = maxOf(startRow, endRow).toFloat()
    val crossX = (vertical - from.x) / (to.x - from.x)
    val crossY = (horizontal - from.y) / (to.y - from.y)
    val cell =
        if (crossX <= crossY) startRow * TraceSide + endCol else endRow * TraceSide + startCol
    return if (TracePlayable[cell]) cell else -1
}

private class TraceLand {
    private val stride = TraceSide + 1
    private val edges = Array(TraceRiders) { IntArray(stride * stride) }
    private val step = intArrayOf(1, stride, -1, -stride)
    private val vertices = mutableListOf<Offset>()
    private val smooth = mutableListOf<Offset>()
    val paths = Array(TraceRiders) { Path().apply { fillType = PathFillType.EvenOdd } }
    private var revision = -1

    fun update(run: TraceAndClaimRun) {
        if (revision == run.landRevision) return
        revision = run.landRevision
        for (boundary in edges) boundary.fill(0)
        val territory = run.territory
        for (cell in territory.indices) {
            val owner = territory[cell]
            if (owner == 0 || run.riders[owner - 1].circleRadius > 0) continue
            val x = cell % TraceSide
            val y = cell / TraceSide
            val corner = y * stride + x
            val boundary = edges[owner - 1]
            if (y == 0 || territory[cell - TraceSide] != owner) {
                boundary[corner] = boundary[corner] or 1
            }
            if (x == TraceSide - 1 || territory[cell + 1] != owner) {
                boundary[corner + 1] = boundary[corner + 1] or 2
            }
            if (y == TraceSide - 1 || territory[cell + TraceSide] != owner) {
                boundary[corner + stride + 1] = boundary[corner + stride + 1] or 4
            }
            if (x == 0 || territory[cell - 1] != owner) {
                boundary[corner + stride] = boundary[corner + stride] or 8
            }
        }
        for ((index, boundary) in edges.withIndex()) {
            val path = paths[index]
            path.rewind()
            path.fillType = PathFillType.EvenOdd
            val rider = run.riders[index]
            if (rider.circleRadius > 0) {
                path.addOval(Rect(traceCenter(rider.circleCenter), rider.circleRadius.toFloat()))
                continue
            }
            for (vertex in boundary.indices) {
                while (boundary[vertex] != 0) trace(path, boundary, vertex)
            }
        }
    }

    private fun trace(path: Path, boundary: IntArray, start: Int) {
        vertices.clear()
        var vertex = start
        var direction = boundary[start].countTrailingZeroBits()
        do {
            vertices += point(vertex)
            boundary[vertex] = boundary[vertex] and (1 shl direction).inv()
            vertex += step[direction]
            if (vertex == start || boundary[vertex] == 0) break
            direction = (direction + 1) and 3
            while (boundary[vertex] and (1 shl direction) == 0) direction = (direction + 3) and 3
        } while (vertex != start)

        smooth.clear()
        val size = vertices.size
        val reach = minOf(4, (size - 1) / 4)
        // Average a short stretch of the closed contour to take out the raster steps.
        for (index in vertices.indices) {
            var sum = Offset.Zero
            for (offset in -reach..reach) {
                val weight = reach + 1 - abs(offset)
                sum += vertices[(index + offset + size) % size] * weight.toFloat()
            }
            smooth += sum / ((reach + 1) * (reach + 1)).toFloat()
        }
        val first = (smooth.last() + smooth.first()) / 2f
        path.moveTo(first.x, first.y)
        for (index in smooth.indices) {
            val point = smooth[index]
            val middle = (point + smooth[(index + 1) % size]) / 2f
            path.quadraticTo(point.x, point.y, middle.x, middle.y)
        }
        path.close()
    }

    private fun point(index: Int) = Offset((index % stride).toFloat(), (index / stride).toFloat())
}
