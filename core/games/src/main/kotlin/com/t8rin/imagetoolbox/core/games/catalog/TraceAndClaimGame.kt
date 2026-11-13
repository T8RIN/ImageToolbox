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

import android.graphics.Paint
import android.graphics.PathMeasure
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathIterator
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.addSvg
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toSvg
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.arcadeJoystick
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.LassoSelect
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object TraceAndClaimGame : ArcadeGameDefinition {
    override val title = R.string.arcade_traceandclaim
    override val description = R.string.arcade_traceandclaim_sub
    override val icon get() = Icons.Outlined.LassoSelect
    override val order = 38

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::TraceAndClaimRun
        )
        val trail = remember { Path() }
        val arena = remember { Path() }
        val colors = remember(style.playerColor) {
            listOf(
                style.playerColor, Color(0xFFFF8980), Color(0xFFFFC168), Color(0xFF60D5A7),
                Color(0xFF6BCCFF), Color(0xFFF18FC6), Color(0xFFB2CE73)
            ).map { lerp(it, style.playerColor, 0.2f) }
        }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::grab,
                onDrag = run::steer,
                onRelease = run::release
            ) {
                val unit = maxOf(
                    run.width / 58f,
                    run.height / 52f,
                    minOf(run.width / 41f, run.height / 24f)
                )
                val player = run.riders.first()
                val camera = player.point * unit
                val world = TraceSize * unit
                val scroll = Offset(
                    (camera.x - run.width / 2f).coerceIn(0f, world - run.width),
                    (camera.y - run.height / 2f).coerceIn(0f, world - run.height)
                )
                val origin = -scroll
                val center = origin + TraceCenter * unit
                val radius = TraceRadius * unit
                arena.rewind()
                arena.addOval(Rect(center, radius))
                clipPath(path = arena) {
                    drawCircle(
                        color = style.detailColor.copy(alpha = 0.12f),
                        radius = radius,
                        center = center
                    )
                    translate(left = origin.x, top = origin.y) {
                        scale(scale = unit, pivot = Offset.Zero) {
                            drawPath(
                                TraceTiles,
                                style.detailColor.copy(alpha = 0.14f),
                                style = Stroke(0.07f)
                            )
                            for (rider in run.riders) {
                                drawPath(
                                    path = rider.land.path,
                                    color = colors[rider.id - 1].copy(
                                        alpha = if (rider === player) 0.68f else 0.51f
                                    )
                                )
                                drawPath(
                                    path = rider.land.path,
                                    color = colors[rider.id - 1].copy(alpha = 0.8f),
                                    style = Stroke(0.12f)
                                )
                            }
                            drawPoints(
                                points = TraceDots,
                                pointMode = PointMode.Points,
                                color = style.textColor.copy(alpha = 0.17f),
                                strokeWidth = 0.18f,
                                cap = StrokeCap.Round
                            )
                            for (rider in run.riders) {
                                if (!rider.active) continue
                                val tint = colors[rider.id - 1]
                                val point = rider.point
                                if (rider.route.isNotEmpty()) {
                                    trail.followTrace(rider.route, point)
                                    drawPath(
                                        path = trail,
                                        color = tint,
                                        style = Stroke(
                                            TraceTrailWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                                val direction = if (rider === player) run.heading else rider.heading
                                drawTraceScout(
                                    style = style,
                                    color = tint,
                                    point = point,
                                    direction = direction,
                                    radius = if (rider === player) 1.05f else 0.95f,
                                    kind = rider.id,
                                    step = if (direction == Offset.Zero || (run.frame / 12 + rider.id) % 2 == 0) 0f else 1f
                                )
                            }
                        }
                    }
                }
                drawCircle(
                    color = style.borderColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.4f)
                )
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
}

private fun DrawScope.drawTraceScout(
    style: ArcadeStyle,
    color: Color,
    point: Offset,
    direction: Offset,
    radius: Float,
    kind: Int,
    step: Float
) {
    rotate(degrees = atan2(direction.y, direction.x) * 180f / PI.toFloat(), pivot = point) {
        translate(left = point.x - radius, top = point.y - radius) {
            scale(scale = radius / 8f, pivot = Offset.Zero) {
                val ink = style.playerDetailColor
                for (x in listOf(3f, 7f, 11f)) {
                    val offset = if (x == 7f) 1f - step else step
                    pixelRect(ink, x, 1f - offset, 2f, 4f)
                    pixelRect(ink, x + 1f, 12f + offset, 2f, 4f)
                }
                drawArcadeSprite(TraceScout, ink.copy(alpha = 0.4f), 0f, 1f)
                drawArcadeSprite(TraceScout, color, 0f, 0f)
                drawArcadeSprite(TraceScoutShade, lerp(color, ink, 0.35f), 0f, 0f)
                pixelRect(lerp(color, style.obstacleColor, 0.6f), 4f, 4f, 5f, 2f)
                repeat((kind - 1) % 3 + 1) { index ->
                    pixelRect(
                        ink.copy(alpha = 0.5f),
                        3f + index * 2f,
                        8f,
                        1f,
                        3f
                    )
                }
                drawArcadeSprite(TraceScoutEyes, style.textColor, 0f, 0f)
                pixelRect(ink, 13f, 4f, 1f, 2f)
                pixelRect(ink, 13f, 10f, 1f, 2f)
                pixelRect(color, 15f, 2f, 2f, 1f)
                pixelRect(color, 15f, 13f, 2f, 1f)
            }
        }
    }
}

private val TraceScout = ArcadeSprite(
    width = 16, height = 16,
    data = "M4 2H9V3H12V4H14V6H16V10H14V12H12V13H9V14H4V13H2V11H1V5H2V3H4Z"
)
private val TraceScoutShade = ArcadeSprite(
    width = 16, height = 16,
    data = "M14 7H16V10H14V12H12V13H9V14H4V13H2V11H9V12H12V10H14Z"
)
private val TraceScoutEyes = ArcadeSprite(
    width = 16, height = 16,
    data = "M11 3H14V7H11Z M11 9H14V13H11Z"
)

private const val TraceSize = 96f
private const val TraceRiders = 7
private const val TraceRadius = TraceSize / 2f - 0.65f
private const val TraceTrailWidth = 0.68f
private const val TraceTravelRadius = TraceRadius - TraceTrailWidth * 0.35f
private const val TraceTurn = 2f * PI.toFloat()
private const val TraceTolerance = 0.008f
private val TraceCenter = Offset(TraceSize / 2f, TraceSize / 2f)
private val TraceArena = TraceLand(traceCircle(TraceCenter, TraceRadius))
private val TraceStroke = Paint().apply {
    style = Paint.Style.STROKE
    strokeWidth = TraceTrailWidth
    strokeCap = Paint.Cap.ROUND
    strokeJoin = Paint.Join.ROUND
}
private val TraceTiles = Path().apply {
    val radius = 3.5f
    var column = 0
    var x = -radius
    while (x < TraceSize + radius) {
        var y = if (column++ % 2 == 0) 0f else radius * 0.8660254f
        while (y < TraceSize + radius * 2f) {
            for (side in 0..5) {
                val angle = side * PI.toFloat() / 3f
                val px = x + cos(angle) * radius
                val py = y + sin(angle) * radius
                if (side == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
            y += radius * 1.7320508f
        }
        x += radius * 1.5f
    }
}

private val TraceDots = buildList {
    val random = Random(98)
    for (row in 0 until TraceSize.toInt() step 3) for (col in 0 until TraceSize.toInt() step 3) {
        val point =
            Offset(col + 0.7f + random.nextFloat() * 1.6f, row + 0.7f + random.nextFloat() * 1.6f)
        if ((point - TraceCenter).getDistanceSquared() < TraceRadius * TraceRadius) add(point)
    }
}

@Serializable
private class TraceRider(val id: Int) {
    var active = false
    var point: ArcadeOffset = Offset.Zero
    var land = TraceLand()
    var angle = 0f
    var curve = 0f
    var home: ArcadeOffset? = null
    var target: ArcadeOffset? = null
    var reconsider = 0f
    val route: MutableList<ArcadeOffset> = mutableListOf()
    var excursion = 0f
    var respawn = 0f

    val heading: Offset
        get() = Offset(cos(angle), sin(angle))

    fun clearTrail() {
        route.clear()
        excursion = 0f
    }

    inline fun forEachTrail(segment: (Offset, Offset) -> Unit) {
        for (index in route.indices) {
            segment(route[index], route.getOrElse(index + 1) { point })
        }
    }
}

@Serializable
private class TraceAndClaimRun : ArcadeRun() {
    val riders = List(TraceRiders) { TraceRider(it + 1) }
    private var arena = 0
    private var elapsed = 0f
    private var scoreRemainder = 0f
    private var milestone = 0
    var heading: ArcadeOffset = Offset.Zero
        private set
    var lives = 3
        private set

    @Transient
    var touchOrigin: Offset? = null
        private set

    @Transient
    var stick = Offset.Zero
        private set

    @Transient
    private val positions = Array(TraceRiders) { Offset.Zero }

    @Transient
    private val dead = BooleanArray(TraceRiders)

    private val player: TraceRider
        get() = riders.first()
    private val difficulty: Float
        get() = arena + elapsed / 34f
    private val speed: Float
        get() = 11.6f + 3.6f * difficulty / (difficulty + 4f)
    private val botCount: Int
        get() = 5 + (arena / 2 + (elapsed / 22f).toInt()).coerceAtMost(TraceRiders - 6)

    val progress: Float
        get() = (player.land.area / TraceArena.area).coerceIn(0f, 1f)

    override fun reset() {
        lives = 3
        arena = 0
        scoreRemainder = 0f
        resetArena()
    }

    private fun resetArena() {
        milestone = 0
        elapsed = 0f
        heading = Offset.Zero
        for (rider in riders) {
            rider.active = false
            rider.land = TraceLand()
            rider.clearTrail()
            rider.respawn = 0f
        }
        spawn(player, Offset(48.5f, 48.5f), 4f)
        spawn(riders[1], Offset(38.5f, 33.5f), 5f)
        spawn(riders[2], Offset(68.5f, 29.5f), 5f)
        spawn(riders[3], Offset(26.5f, 66.5f), 5f)
        spawn(riders[4], Offset(59.5f, 63.5f), 5f)
        spawn(riders[5], Offset(47.5f, 77.5f), 4f)
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
            riders.size == TraceRiders && player.active &&
            riders.withIndex().all { (index, rider) ->
                rider.id == index + 1 && rider.point.isFinite &&
                        rider.angle.isFinite() && rider.curve.isFinite() &&
                        rider.home?.isFinite != false && rider.target?.isFinite != false &&
                        rider.route.all { it.isFinite }
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
            if (!rider.active) continue
            positions[index] = if (rider === player) {
                traceInside(rider.point + heading * step)
            } else {
                moveBot(rider, step, seconds)
            }
        }

        var cut = 0
        for ((index, rider) in riders.withIndex()) {
            if (!rider.active) continue
            val point = positions[index]
            for ((other, opponent) in riders.withIndex()) {
                if (other == index || !opponent.active) continue
                if (opponent.trailNear(point, TraceTrailWidth / 2f + 0.78f)) {
                    dead[other] = true
                    if (rider === player) cut = opponent.id
                }
            }
            for (other in index + 1 until riders.size) {
                val opponent = riders[other]
                if (!opponent.active) continue
                val rival = positions[other]
                if ((point - rival).getDistanceSquared() >= 2.25f) continue
                if (point !in rider.land) dead[index] = true
                if (rival !in opponent.land) dead[other] = true
            }
        }
        if (dead[0] && --lives == 0) {
            player.clearTrail()
            finish()
            return
        }
        for ((index, rider) in riders.withIndex()) {
            if (!dead[index]) continue
            rider.clearTrail()
            rider.active = false
            if (rider === player) continue
            rider.land = TraceLand()
            rider.respawn = 1.6f + Random.nextFloat() * 1.8f
            if (rider.id == cut) score += 30
        }
        for ((index, rider) in riders.withIndex()) {
            if (rider.active) move(rider, positions[index])
        }

        if (dead[0]) {
            respawnPlayer()
            heading = Offset.Zero
            animateLevel()
            return
        }
        val lastRivalEliminated = (1 until riders.size).any { dead[it] } &&
                riders.drop(1).none { it.active }
        if (progress >= 0.9999f || lastRivalEliminated) {
            score += 100
            arena++
            resetArena()
            animateLevel()
            return
        }
        for (index in 1..botCount) {
            val rider = riders[index]
            if (rider.active) continue
            rider.respawn -= seconds
            if (rider.respawn <= 0f && !spawnInFreeSpace(rider)) rider.respawn = 1.2f
        }
    }

    private fun moveBot(rider: TraceRider, step: Float, seconds: Float): Offset {
        val radial = rider.point - TraceCenter
        val radius = radial.getDistance()
        val limit = when (rider.id % 3) {
            0 -> 12f; 1 -> 24f; else -> 17f
        } + arena.coerceAtMost(8)
        rider.reconsider -= seconds
        if (rider.reconsider <= 0f) {
            rider.reconsider = if (rider.id % 3 == 2) 0.18f else 0.3f
            if (rider.home?.let { it !in rider.land } == true) rider.home = null
            if (rider.home == null &&
                (rider.excursion >= limit || rider.excursion >= 5f && radius > TraceTravelRadius - 4f)
            ) {
                rider.home = rider.land.nearestInside(rider.point)
            }
            rider.target = if (rider.home == null && rider.excursion < limit / 2f) {
                findTrail(rider)
            } else null
        }

        var turn = rider.curve + sin(elapsed * 1.9f + rider.id) * 0.4f
        var travel = step
        val target = rider.home ?: rider.target
        if (target != null) {
            val delta = target - rider.point
            turn = traceTurn(rider.angle, delta) * 5f
            if (rider.home != null) {
                travel *= (delta.getDistance() / 2.6f).coerceIn(0.55f, 1f)
            }
        }
        val forward = rider.heading
        if (radius > TraceTravelRadius - 5f && radial.traceDot(forward) > 0f) {
            val inward = -radial / radius
            val side = if (radial.traceCross(forward) > 0f) 1f else -1f
            val tangent = Offset(inward.y, -inward.x) * side
            val avoid = traceTurn(rider.angle, tangent + inward * 0.55f)
            val strength = ((radius - TraceTravelRadius + 5f) / 3f).coerceIn(0f, 1f)
            turn += (avoid * 5f - turn) * strength
        }
        rider.angle = (rider.angle + turn.coerceIn(-5.4f, 5.4f) * seconds) % TraceTurn
        return traceInside(rider.point + rider.heading * travel)
    }

    private fun move(rider: TraceRider, point: Offset) {
        val previous = rider.point
        rider.point = point
        if (point in rider.land) {
            if (rider.route.isNotEmpty()) {
                val end =
                    if (previous in rider.land) previous else rider.land.crossing(previous, point)
                claim(rider, end)
                rider.clearTrail()
                rider.home = null
                rider.target = null
                rider.curve = randomCurve()
            }
            return
        }

        if (rider.route.isEmpty()) {
            val start =
                if (previous in rider.land) rider.land.crossing(previous, point) else previous
            rider.route += start
            rider.excursion += (point - start).getDistance()
        } else {
            rider.excursion += (point - previous).getDistance()
        }
        recordRoute(rider)
    }

    private fun recordRoute(rider: TraceRider) {
        val route = rider.route
        val last = route.last()
        val delta = rider.point - last
        if (delta.getDistanceSquared() < 0.12f) return
        if (route.size >= 2) {
            val direction = last - route[route.lastIndex - 1]
            val cross = direction.traceCross(delta)
            if (direction.traceDot(delta) > 0f && cross * cross < direction.getDistanceSquared() * 0.0025f) {
                route[route.lastIndex] = rider.point
                return
            }
        }
        route += rider.point
    }

    private fun claim(rider: TraceRider, end: Offset) {
        val trace = Path().apply { followTrace(rider.route, end) }
        val ribbon = Path()
        TraceStroke.getFillPath(trace.asAndroidPath(), ribbon.asAndroidPath())
        val next = rider.land.claim(ribbon) ?: return
        val otherLands = riders.map { opponent ->
            if (rider === opponent || !next.bounds.overlaps(opponent.land.bounds)) {
                opponent.land
            } else {
                val remaining =
                    opponent.land.path.clipWith(next.path, PathOperation.Difference) ?: return
                TraceLand(remaining)
            }
        }
        if (rider === player) {
            val gain = (next.area - player.land.area).coerceAtLeast(0f) + scoreRemainder
            val points = gain.toInt()
            score += points
            scoreRemainder = gain - points
        }
        for ((index, opponent) in riders.withIndex()) opponent.land = otherLands[index]
        rider.land = next
        if (rider === player) {
            val reached = (progress * 4f).toInt()
            if (reached > milestone) {
                score += (reached - milestone) * 25
                milestone = reached
            }
        }
    }

    private fun findTrail(rider: TraceRider): Offset? {
        val reach = (8f + difficulty.coerceAtMost(7f)) * if (rider.id % 3 == 2) 1.3f else 0.8f
        var nearest = reach * reach
        var target: Offset? = null
        for (opponent in riders) {
            if (opponent === rider || !opponent.active) continue
            opponent.forEachTrail { start, end ->
                val candidate = traceClosestPoint(rider.point, start, end)
                val delta = candidate - rider.point
                val distance = delta.getDistanceSquared()
                if (distance < nearest && abs(traceTurn(rider.angle, delta)) < 1.5f) {
                    nearest = distance
                    target = candidate
                }
            }
        }
        return target
    }

    private fun respawnPlayer() {
        var safest: Offset? = null
        var best = Float.NEGATIVE_INFINITY
        fun consider(point: Offset) {
            if (point !in player.land ||
                (point - TraceCenter).getDistanceSquared() > TraceTravelRadius * TraceTravelRadius
            ) return
            var clearance = 8f
            for (rider in riders) {
                if (rider === player || !rider.active) continue
                clearance = minOf(clearance, (point - rider.point).getDistance())
                if (clearance < 1.5f || rider.trailNear(point, 1.5f)) return
            }
            val value = clearance * TraceSize - (player.point - point).getDistance()
            if (value > best) {
                best = value
                safest = point
            }
        }

        player.land.nearestInside(player.point)?.let(::consider)
        if (!player.land.path.isEmpty) {
            repeat(96) { consider(player.land.bounds.randomPoint()) }
        }
        val point = safest
        if (point != null) {
            spawn(player, point)
        } else if (!spawnInFreeSpace(player)) {
            resetArena()
        }
    }

    private fun spawnInFreeSpace(rider: TraceRider): Boolean {
        val radius = 2f
        val clearance = radius + 0.65f
        val extent = TraceRadius - clearance
        val searchArea = Rect(TraceCenter, extent)
        // Allow time to steer even if the new bot moves straight toward the player.
        val reactionDistance = speed * 0.85f
        val playerAhead = traceInside(player.point + heading * reactionDistance)
        val playerClearance = reactionDistance * 2f + radius

        repeat(160) {
            val point = searchArea.randomPoint()
            if ((point - TraceCenter).getDistanceSquared() > extent * extent) return@repeat
            if (rider !== player) {
                val nearest = traceClosestPoint(
                    point = point,
                    start = player.point,
                    end = playerAhead
                )
                if ((point - nearest).getDistanceSquared() < playerClearance * playerClearance) {
                    return@repeat
                }
            }
            if (riders.any { it.active && (it.point - point).getDistanceSquared() < 100f }) return@repeat
            val occupied =
                riders.any { it.land.near(point, clearance) || it.trailNear(point, clearance) }
            if (occupied) return@repeat
            if (spawn(rider, point, radius)) return true
        }
        return false
    }

    private fun spawn(rider: TraceRider, point: Offset, radius: Float = 0f): Boolean {
        if (radius > 0f) {
            val circle = traceCircle(point, radius)
            val land = if (rider.land.path.isEmpty) circle else {
                rider.land.path.clipWith(circle, PathOperation.Union) ?: return false
            }
            rider.land = TraceLand(land)
        }
        rider.active = true
        rider.point = point
        rider.angle = Random.nextFloat() * TraceTurn
        rider.curve = randomCurve()
        rider.home = null
        rider.target = null
        rider.reconsider = 0f
        return true
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

private fun Rect.randomPoint() =
    Offset(left + Random.nextFloat() * width, top + Random.nextFloat() * height)

private fun traceCircle(center: Offset, radius: Float): Path =
    Path().apply { addOval(Rect(center, radius)) }

private fun Path.clipWith(other: Path, operation: PathOperation): Path? =
    Path().takeIf { it.op(this, other, operation) }

private fun Path.followTrace(route: List<Offset>, end: Offset) {
    rewind()
    val first = route.first()
    moveTo(first.x, first.y)
    for (index in 1 until route.size) {
        val point = route[index]
        lineTo(point.x, point.y)
    }
    lineTo(end.x, end.y)
}

private fun Offset.traceDot(other: Offset): Float = x * other.x + y * other.y

private fun Offset.traceCross(other: Offset): Float = x * other.y - y * other.x

private fun traceClosestPoint(point: Offset, start: Offset, end: Offset): Offset {
    val line = end - start
    val length = line.getDistanceSquared()
    if (length <= 0f) return start
    return start + line * (((point - start).traceDot(line) / length).coerceIn(0f, 1f))
}

private fun TraceRider.trailNear(point: Offset, reach: Float): Boolean {
    forEachTrail { start, end ->
        if (point.x >= minOf(start.x, end.x) - reach && point.x <= maxOf(start.x, end.x) + reach &&
            point.y >= minOf(start.y, end.y) - reach && point.y <= maxOf(start.y, end.y) + reach &&
            (point - traceClosestPoint(point, start, end)).getDistanceSquared() < reach * reach
        ) return true
    }
    return false
}

@Serializable(with = TraceLandSerializer::class)
private class TraceLand(val path: Path = Path(), snapshot: String? = null) {
    private val contours = path.traceContours()
    val bounds = path.getBounds()
    val area = contours.sumOf { if (it.depth % 2 == 0) it.area else -it.area }.toFloat()
    val savedPath: String by lazy {
        snapshot ?: Path().apply {
            addPath(path)
            transform(Matrix().apply { scale(32f, 32f) })
        }.toSvg()
    }

    operator fun contains(point: Offset): Boolean =
        point in bounds && contours.count { point in it } % 2 != 0

    fun crossing(from: Offset, to: Offset): Offset {
        val startsInside = from in this
        var first = from
        var last = to
        repeat(8) {
            val middle = (first + last) / 2f
            if ((middle in this) == startsInside) first = middle else last = middle
        }
        return (first + last) / 2f
    }

    fun near(point: Offset, radius: Float): Boolean {
        if (point !in bounds.inflate(radius)) return false
        if (point in this) return true
        for (contour in contours) {
            if (point !in contour.bounds.inflate(radius)) continue
            contour.forEachEdge { start, end ->
                if ((traceClosestPoint(
                        point,
                        start,
                        end
                    ) - point).getDistanceSquared() < radius * radius
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun nearestInside(point: Offset): Offset? {
        if (point in this) return point
        var nearest = Offset.Zero
        var normal = Offset.Zero
        var distance = Float.POSITIVE_INFINITY
        for (contour in contours) {
            contour.forEachEdge { start, end ->
                val candidate = traceClosestPoint(point, start, end)
                val squared = (candidate - point).getDistanceSquared()
                if (squared < distance && start != end) {
                    distance = squared
                    nearest = candidate
                    val line = end - start
                    normal = Offset(-line.y, line.x) / line.getDistance()
                }
            }
        }
        if (!distance.isFinite()) return null
        for (depth in floatArrayOf(0.9f, 0.16f, 0.035f)) {
            val left = nearest + normal * depth
            if (left in this) return left
            val right = nearest - normal * depth
            if (right in this) return right
        }
        repeat(32) {
            val candidate = bounds.randomPoint()
            if (candidate in this) return candidate
        }
        return null
    }

    fun claim(ribbon: Path): TraceLand? {
        val blocked = path.clipWith(ribbon, PathOperation.Union) ?: return null
        val open = TraceArena.path.clipWith(blocked, PathOperation.Difference) ?: return null
        val contours = open.traceContours()
        // Only the largest pocket connected to the arena rim remains outside the claim.
        val exterior = contours.filter { it.parent == null && it.touchesRim }.maxByOrNull { outer ->
            outer.area - contours.sumOf { if (it.parent === outer) it.area else 0.0 }
        }
        if (exterior == null) return TraceArena

        val outside = Path().apply {
            fillType = PathFillType.EvenOdd
            addPath(exterior.path)
            for (hole in contours) if (hole.parent === exterior) addPath(hole.path)
        }
        return TraceArena.path.clipWith(outside, PathOperation.Difference)?.let { TraceLand(it) }
    }
}

private object TraceLandSerializer : KSerializer<TraceLand> {
    override val descriptor = PrimitiveSerialDescriptor("TraceLand", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TraceLand) {
        encoder.encodeString(value.savedPath)
    }

    override fun deserialize(decoder: Decoder): TraceLand {
        val saved = decoder.decodeString()
        val path = Path().apply {
            if (saved.isNotEmpty()) addSvg(saved)
            transform(Matrix().apply { scale(1f / 32f, 1f / 32f) })
            fillType = PathFillType.EvenOdd
        }
        return TraceLand(path, saved)
    }
}

private fun Path.traceContours(): List<TraceContour> {
    if (isEmpty) return emptyList()
    val measure = PathMeasure(asAndroidPath(), true)
    val contours = mutableListOf<TraceContour>()
    do {
        val part = Path()
        if (measure.getSegment(0f, measure.length, part.asAndroidPath(), true)) {
            part.close()
            val contour = TraceContour(part)
            if (contour.area > 0.000001) contours += contour
        }
    } while (measure.nextContour())

    contours.sortByDescending { it.area }
    for ((index, contour) in contours.withIndex()) {
        for (outer in index - 1 downTo 0) {
            val candidate = contours[outer]
            if (contour.inside(candidate)) {
                contour.parent = candidate
                contour.depth = candidate.depth + 1
                break
            }
        }
    }
    return contours
}

private class TraceContour(val path: Path) {
    val bounds = path.getBounds()
    private val points = path.traceVertices()
    var parent: TraceContour? = null
    var depth = 0
    val area: Double = run {
        var sum = 0.0
        forEachEdge { from, to ->
            sum += from.x.toDouble() * to.y - from.y.toDouble() * to.x
        }
        abs(sum) / 2.0
    }

    val touchesRim: Boolean
        get() = points.any {
            val edge = TraceRadius - TraceTolerance * 2f
            (it - TraceCenter).getDistanceSquared() >= edge * edge
        }

    fun inside(other: TraceContour): Boolean =
        points.isNotEmpty() && (points.first() in other ||
                points[points.size / 3] in other || points[points.size * 2 / 3] in other)

    operator fun contains(point: Offset): Boolean {
        if (point !in bounds) return false
        var inside = false
        forEachEdge { from, to ->
            if ((from.y > point.y) != (to.y > point.y) &&
                point.x < from.x + (point.y - from.y) * (to.x - from.x) / (to.y - from.y)
            ) inside = !inside
        }
        return inside
    }

    inline fun forEachEdge(edge: (Offset, Offset) -> Unit) {
        for (index in points.indices) {
            edge(points[index], points.getOrElse(index + 1) { points.first() })
        }
    }
}

private fun Path.traceVertices(): List<Offset> = buildList {
    val iterator = iterator(PathIterator.ConicEvaluation.AsQuadratics, TraceTolerance / 2f)
    val coordinates = FloatArray(8)
    while (iterator.hasNext()) {
        val segment = iterator.next(coordinates)
        val a = Offset(coordinates[0], coordinates[1])
        val b = Offset(coordinates[2], coordinates[3])
        when (segment) {
            PathSegment.Type.Move -> add(a)
            PathSegment.Type.Line -> add(b)
            PathSegment.Type.Quadratic -> {
                val c = Offset(coordinates[4], coordinates[5])
                val bend = (a - b * 2f + c).getDistance()
                val steps = ceil(sqrt(bend / (4f * TraceTolerance))).toInt().coerceAtLeast(1)
                for (index in 1..steps) {
                    val t = index.toFloat() / steps
                    val u = 1f - t
                    add(a * (u * u) + b * (2f * u * t) + c * (t * t))
                }
            }

            PathSegment.Type.Cubic -> {
                val c = Offset(coordinates[4], coordinates[5])
                val d = Offset(coordinates[6], coordinates[7])
                val bend = maxOf((a - b * 2f + c).getDistance(), (b - c * 2f + d).getDistance())
                val steps = ceil(sqrt(bend * 0.75f / TraceTolerance)).toInt().coerceAtLeast(1)
                for (index in 1..steps) {
                    val t = index.toFloat() / steps
                    val u = 1f - t
                    add(a * (u * u * u) + b * (3f * u * u * t) + c * (3f * u * t * t) + d * (t * t * t))
                }
            }

            else -> Unit
        }
    }
}