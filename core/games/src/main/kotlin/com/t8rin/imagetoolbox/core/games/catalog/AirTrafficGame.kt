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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Flight
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object AirTrafficGame : ArcadeGameDefinition {
    override val title = R.string.arcade_airtraffic
    override val description = R.string.arcade_airtraffic_sub
    override val icon get() = Icons.Outlined.Flight
    override val order = 30

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::AirTrafficRun
        )

        val routePath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = run::begin,
                onDrag = run::drag,
                onRelease = run::commit
            ) {
                drawAirTrafficBackground(style, run.width, run.height)
                val colors = listOf(style.playerColor, style.hazardColor, style.accentColor)
                repeat(18) { index ->
                    val x = ((index * 71 + 37) % 353) / 353f * run.width
                    val y =
                        run.topInset + ((index * 101 + 19) % 419) / 419f * (run.height - run.topInset)
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.22f),
                        x = x,
                        y = y,
                        width = if (index % 3 == 0) 6f else 3f,
                        height = 1f
                    )
                    if (index % 3 == 0) pixelRect(
                        color = style.detailColor.copy(alpha = 0.22f),
                        x = x + 9f,
                        y = y - 3f,
                        width = 2f,
                        height = 1f
                    )
                }
                repeat(3) { kind ->
                    val p = run.zone(kind)
                    val color = colors[kind]
                    val active = run.planes.any { it.kind == kind && it.cleared }
                    if (kind == 2) {
                        drawArcadeSprite(
                            sprite = FlightHelipad,
                            tint = color.copy(alpha = if (active) 1f else 0.65f),
                            x = p.x - FlightHelipad.width / 2f,
                            y = p.y - FlightHelipad.height / 2f
                        )
                    } else rotate(
                        degrees = run.runways[kind].heading * 180f / PI.toFloat(),
                        pivot = p
                    ) {
                        drawArcadeSprite(
                            sprite = FlightAirstrip,
                            tint = color.copy(alpha = if (active) 1f else 0.65f),
                            x = p.x - FlightAirstrip.width / 2f,
                            y = p.y - FlightAirstrip.height / 2f
                        )
                        repeat(5) {
                            pixelRect(
                                color = color.copy(alpha = 0.5f),
                                x = p.x - 27f + it * 12f,
                                y = p.y - 1f,
                                width = 6f,
                                height = 2f
                            )
                        }
                        for (side in listOf(-1f, 1f)) repeat(3) {
                            pixelRect(
                                color = color,
                                x = p.x - 31f + it * 4f,
                                y = p.y + side * 7f - 1f,
                                width = 2f,
                                height = 3f
                            )
                        }
                        drawArcadeSprite(
                            sprite = FlightApproach,
                            tint = color,
                            x = p.x - 47f,
                            y = p.y - FlightApproach.height / 2f
                        )
                    }
                }
                for (plane in run.planes) {
                    val color = colors[plane.kind]
                    if (plane.route.isNotEmpty()) {
                        val path = routePath.apply {
                            rewind()
                            moveTo(plane.point.x, plane.point.y)
                            plane.route.forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(
                            path = path,
                            color = color.copy(alpha = if (plane.id == run.selected) 0.15f else 0.4f),
                            style = Stroke(
                                width = 1f,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                    if (plane.warning || plane.id == run.selected) {
                        val ink = if (plane.warning) style.hazardColor else color
                        val radius = 18f + if (plane.warning) sin(run.time * 10f) * 2f else 0f
                        for (side in 0..1) for (end in 0..1) {
                            val x = plane.point.x + if (side == 0) -radius else radius - 6f
                            val y = plane.point.y + if (end == 0) -radius else radius - 2f
                            pixelRect(color = ink, x = x, y = y, width = 6f, height = 2f)
                            pixelRect(
                                color = ink,
                                x = x + if (side == 0) 0f else 4f,
                                y = y + if (end == 0) 0f else -4f,
                                width = 2f,
                                height = 6f
                            )
                        }
                    }
                    rotate(
                        degrees = plane.heading * 180f / PI.toFloat() + 90f,
                        pivot = plane.point
                    ) {
                        scale(
                            scale = if (plane.landing > 0f) (plane.landing / 0.6f).coerceAtLeast(
                                0.1f
                            ) else 1f,
                            pivot = plane.point
                        ) {
                            val sprite = when (plane.kind) {
                                1 -> FlightJetSprite
                                2 -> FlightHelicopterSprite
                                else -> FlightPlaneSprite
                            }
                            drawArcadeSprite(
                                sprite = sprite,
                                tint = color,
                                x = plane.point.x - sprite.width / 2f,
                                y = plane.point.y - sprite.height / 2f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = plane.point.x - 1f,
                                y = plane.point.y - if (plane.kind == 2) 7f else 9f,
                                width = 2f,
                                height = 4f
                            )
                            if (plane.kind == 2) {
                                val hub = plane.point - Offset(x = 0f, y = 3f)
                                rotate(degrees = run.time * 900f, pivot = hub) {
                                    for (side in 0..1) {
                                        pixelRect(
                                            color = color.copy(alpha = 0.7f),
                                            x = hub.x + if (side == 0) -15f else 3f,
                                            y = hub.y - 1f,
                                            width = 12f,
                                            height = 2f
                                        )
                                    }
                                }
                                pixelRect(
                                    color = color,
                                    x = hub.x - 2f,
                                    y = hub.y - 2f,
                                    width = 4f,
                                    height = 4f
                                )
                            }
                        }
                    }
                }
                if (run.drawing.size > 1) {
                    val color = colors[run.planes.firstOrNull { it.id == run.selected }?.kind ?: 0]
                    val path = routePath.apply {
                        rewind()
                        moveTo(run.drawing.first().x, run.drawing.first().y)
                        run.drawing.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path = path,
                        color = color.copy(alpha = 0.18f),
                        style = Stroke(
                            width = 7f,
                            cap = StrokeCap.Round
                        )
                    )
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = 2f,
                            cap = StrokeCap.Round
                        )
                    )
                    pixelCircle(
                        color = color,
                        radius = 4f,
                        center = run.drawing.last(),
                        style = Stroke(width = 1.5f)
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

private val FlightPlaneSprite = ArcadeSprite(
    width = 26,
    height = 30,
    data = "M0 14H4V12H8V10H10V2H12V0H14V2H16V10H18V12H22V14H26V18H20V16H16V24H20V26H22V28H16V30H10V28H4V26H6V24H10V16H6V18H0Z M12 4V8H14V4Z"
)

private val FlightJetSprite = ArcadeSprite(
    width = 26,
    height = 30,
    data = "M0 18H2V16H4V14H6V12H8V10H10V2H12V0H14V2H16V10H18V12H20V14H22V16H24V18H26V22H20V20H16V24H20V26H22V30H18V28H14V30H12V28H8V30H4V26H6V24H10V20H6V22H0Z M12 4V8H14V4Z"
)

private val FlightHelicopterSprite = ArcadeSprite(
    width = 26,
    height = 30,
    data = "M10 0H16V2H18V4H20V12H18V14H16V16H10V14H8V12H6V4H8V2H10Z " +
            "M10 4H16V6H18V10H8V6H10Z M12 16H14V24H18V26H14V30H12V26H8V24H12Z"
)

private val FlightHelipad = ArcadeSprite(
    width = 42,
    height = 42,
    data = "M8 0H34V2H38V4H40V8H42V34H40V38H38V40H34V42H8V40H4V38H2V34H0V8H2V4H4V2H8Z " +
            "M8 2H34V4H38V8H40V34H38V38H34V40H8V38H4V34H2V8H4V4H8Z " +
            "M13 11H17V19H25V11H29V31H25V23H17V31H13Z"
)

private val FlightAirstrip = ArcadeSprite(
    width = 70,
    height = 24,
    data = "M4 0H66V2H70V22H66V24H4V22H0V2H4Z M4 2H66V4H68V20H66V22H4V20H2V4H4Z"
)

private val FlightApproach = ArcadeSprite(
    width = 6,
    height = 10,
    data = "M0 0H2V2H0Z M2 2H4V4H2Z M4 4H6V6H4Z M2 6H4V8H2Z M0 8H2V10H0Z"
)

private fun DrawScope.drawAirTrafficBackground(style: ArcadeStyle, width: Float, height: Float) {
    val ink = style.detailColor.copy(alpha = 0.105f)
    val count = if (width > height * 2f) 2 else 1
    repeat(count) { index ->
        val center = Offset(
            width * (if (index == 0) 0.16f else 0.83f),
            height * (if (index == 0) 0.27f else 0.7f)
        )
        val radius = minOf(width, height) * 0.3f
        for (ring in 1..3) drawCircle(ink, radius * ring / 3f, center, style = Stroke(0.7f))
        drawLine(
            ink.copy(alpha = 0.06f),
            center - Offset(radius + 13f, 0f),
            center + Offset(radius + 13f, 0f),
            0.6f
        )
        drawLine(
            ink.copy(alpha = 0.06f),
            center - Offset(0f, radius + 13f),
            center + Offset(0f, radius + 13f),
            0.6f
        )
    }
    val marks = ceil(width / 79f).toInt()
    repeat(marks) { index ->
        val point = Offset(index * 79f + 19f, height * (0.12f + (index * 29 % 73) / 100f))
        drawLine(ink, point - Offset(2f, 0f), point + Offset(2f, 0f), 0.6f)
        drawLine(ink, point - Offset(0f, 2f), point + Offset(0f, 2f), 0.6f)
    }
    val coast = Path().apply {
        moveTo(0f, height)
        lineTo(0f, height * 0.79f)
        var x = 0f
        while (x < width + 30f) {
            lineTo(x, height * 0.8f + sin(x * 0.018f) * height * 0.07f)
            x += 20f
        }
        lineTo(width, height)
        close()
    }
    drawPath(coast, ink.copy(alpha = 0.03f))
    drawPath(coast, ink, style = Stroke(0.7f))
}

@Serializable
private data class FlightRunway(
    val position: ArcadeOffset,
    val heading: Float
)

@Serializable
private class ControlledFlight(
    val id: Int,
    val kind: Int,
    var point: ArcadeOffset,
    var heading: Float,
    var entered: Boolean = false,
    var landing: Float = 0f,
    var cleared: Boolean = false
) {
    val route: ArcadeDeque<ArcadeOffset> = ArrayDeque()
    var warning = false
}

@Serializable
private class AirTrafficRun : ArcadeRun() {
    val planes = mutableListOf<ControlledFlight>()
    val runways = mutableListOf<FlightRunway>()

    @Transient
    val drawing = mutableListOf<Offset>()

    @Transient
    val effects = ArcadeEffects()

    @Transient
    var selected = -1
        private set
    var time = 0f
    var landed = 0
    private var nextId = 0
    private var timer = 2.5f
    val pace get() = landed / (landed + 18f)
    fun zone(kind: Int): Offset {
        val position = runways[kind].position
        return Offset(
            x = 52f + position.x * (width - 104f),
            y = topInset + 44f + position.y * (height - topInset - 88f)
        )
    }

    fun onRunway(
        point: Offset,
        kind: Int,
        padding: Float = 0f
    ): Boolean {
        val delta = point - zone(kind)
        if (kind == 2) return delta.getDistance() <= 23f + padding
        val angle = runways[kind].heading
        val along = delta.x * cos(angle) + delta.y * sin(angle)
        val across = -delta.x * sin(angle) + delta.y * cos(angle)
        return abs(along) <= 35f + padding && abs(across) <= 12f + padding
    }

    override fun isValid(): Boolean = super.isValid() &&
            runways.size == 3 && planes.all { it.kind in 0..2 }

    override fun reset() {
        runways.clear()
        val positions = listOf(
            Offset(x = Random.nextFloat() * 0.16f, y = Random.nextFloat()),
            Offset(x = 0.84f + Random.nextFloat() * 0.16f, y = Random.nextFloat())
        ).shuffled()
        positions.forEach {
            runways += FlightRunway(position = it, heading = Random.nextInt(8) * PI.toFloat() / 4f)
        }
        runways += FlightRunway(
            position = Offset(x = 0.47f + Random.nextFloat() * 0.06f, y = Random.nextFloat()),
            heading = 0f
        )
        planes.clear()
        drawing.clear()
        effects.clear()
        selected = -1
        time = 0f
        landed = 0
        nextId = 0
        timer = 2.4f
        planes += ControlledFlight(
            id = nextId++,
            kind = 0,
            point = Offset(x = -18f, y = topInset + (height - topInset) * 0.36f),
            heading = 0f
        )
    }

    fun begin(point: Offset) {
        val plane =
            planes.filter { it.landing <= 0f }.minByOrNull { (it.point - point).getDistance() }
        val selectedPlane = plane?.takeIf { (it.point - point).getDistance() <= 32f }
        selected = selectedPlane?.id ?: -1
        drawing.clear()
        selectedPlane?.let { drawing += it.point }
    }

    fun drag(point: Offset) {
        if (selected < 0) return
        val point = Offset(
            x = point.x.coerceIn(12f, width - 12f),
            y = point.y.coerceIn(topInset + 12f, height - 12f)
        )
        if (drawing.size > 1 && (point - drawing[drawing.lastIndex - 1]).getDistance() < 5f) {
            drawing[drawing.lastIndex] = point
        } else {
            if (drawing.size >= 160) {
                for (index in drawing.lastIndex - 1 downTo 1 step 2) drawing.removeAt(index)
            }
            drawing += point
        }
        updateRoute()
    }

    private fun updateRoute() {
        val plane = planes.firstOrNull { it.id == selected } ?: return
        if (drawing.size < 2) return
        var nearest = 0
        var distance = Float.MAX_VALUE
        for (index in 0 until drawing.lastIndex) {
            val start = drawing[index]
            val segment = drawing[index + 1] - start
            val delta = plane.point - start
            val progress = ((delta.x * segment.x + delta.y * segment.y) /
                    segment.getDistanceSquared().coerceAtLeast(0.001f)).coerceIn(0f, 1f)
            val separation = (start + segment * progress - plane.point).getDistanceSquared()
            if (separation <= distance) {
                distance = separation
                nearest = index
            }
        }
        plane.route.clear()
        val points = drawing.drop(nearest + 1)
        var start = plane.point
        for (index in points.indices) {
            val control = points[index]
            val end = points.getOrNull(index + 1)?.let { (control + it) / 2f } ?: control
            val steps =
                ceil(((control - start).getDistance() + (end - control).getDistance()) / 6f).toInt()
                    .coerceIn(1, 12)
            repeat(steps) { step ->
                val t = (step + 1f) / steps
                plane.route += start * ((1f - t) * (1f - t)) + control * (2f * t * (1f - t)) + end * (t * t)
            }
            start = end
        }
        if (plane.route.size > 256) {
            val route = plane.route.toList()
            plane.route.clear()
            repeat(256) { index ->
                plane.route += route[index * route.lastIndex / 255]
            }
        }
        while (plane.route.isNotEmpty() && (plane.route.first() - plane.point).getDistance() < 3f) {
            plane.route.removeFirst()
        }
        plane.cleared = onRunway(drawing.last(), plane.kind, 16f)
        if (plane.cleared) {
            while (plane.route.isNotEmpty() && onRunway(plane.route.last(), plane.kind, 12f)) {
                plane.route.removeLast()
            }
            plane.route += zone(plane.kind)
        }
    }

    fun commit() {
        updateRoute()
        stopInput()
    }

    override fun stopInput() {
        selected = -1
        drawing.clear()
    }

    private fun forecast(plane: ControlledFlight): List<Offset> {
        val copy = ControlledFlight(
            id = plane.id, kind = plane.kind, point = plane.point, heading = plane.heading,
            entered = plane.entered, landing = plane.landing, cleared = plane.cleared
        ).apply {
            route.addAll(plane.route)
        }
        return List(21) {
            if (copy.landing <= 0f && move(copy, 0.1f)) copy.landing = 0.6f
            copy.point
        }
    }

    fun canSpawn(plane: ControlledFlight): Boolean {
        val traffic = planes.filter { it.landing <= 0f }
        if (traffic.any { (it.point - plane.point).getDistance() < 60f }) return false
        val route = forecast(plane)
        return traffic.all { other ->
            forecast(other).zip(route).all { (a, b) -> (a - b).getDistance() >= 34f }
        }
    }

    private fun spawn(): Boolean {
        if (planes.size >= 12) return false
        repeat(24) {
            val kind = if (nextId < 3) nextId else Random.nextInt(3)
            val p = when (Random.nextInt(4)) {
                0 -> Offset(
                    x = -18f,
                    y = topInset + 20f + Random.nextFloat() * (height - topInset - 50f)
                )

                1 -> Offset(
                    x = width + 18f,
                    y = topInset + 20f + Random.nextFloat() * (height - topInset - 50f)
                )

                2 -> Offset(x = 24f + Random.nextFloat() * (width - 48f), y = -18f)
                else -> Offset(x = 24f + Random.nextFloat() * (width - 48f), y = height + 18f)
            }
            val delta = Offset(
                x = width * (0.25f + Random.nextFloat() * 0.5f),
                y = topInset + (height - topInset) * (0.25f + Random.nextFloat() * 0.5f)
            ) - p
            val plane = ControlledFlight(
                id = nextId,
                kind = kind,
                point = p,
                heading = atan2(delta.y, delta.x)
            )
            if (canSpawn(plane)) {
                nextId++
                planes += plane
                return true
            }
        }
        return false
    }

    private fun move(
        plane: ControlledFlight,
        seconds: Float
    ): Boolean {
        var speed = (30f + pace * 12f) * when (plane.kind) {
            1 -> 1.12f
            2 -> 0.75f
            else -> 1f
        }
        if (plane.cleared && plane.route.size <= 1) speed *= 0.8f
        var remaining = speed * seconds
        var direction = Offset(x = cos(plane.heading), y = sin(plane.heading))
        while (remaining > 0f && plane.route.isNotEmpty()) {
            val delta = plane.route.first() - plane.point
            val distance = delta.getDistance()
            if (distance < 0.01f) {
                plane.route.removeFirst()
                continue
            }
            direction = delta / distance
            val step = minOf(remaining, distance)
            plane.point += direction * step
            remaining -= step
            if (step >= distance) plane.route.removeFirst()
        }
        if (remaining > 0f) {
            val outside = plane.point.x !in 24f..width - 24f ||
                    plane.point.y !in topInset + 24f..height - 24f
            if (outside && plane.entered) {
                val delta = Offset(
                    x = width * (0.25f + Random.nextFloat() * 0.5f),
                    y = topInset + (height - topInset) * (0.25f + Random.nextFloat() * 0.5f)
                ) - plane.point
                val desired = atan2(delta.y, delta.x)
                val error = atan2(sin(desired - plane.heading), cos(desired - plane.heading))
                plane.heading += error.coerceIn(-seconds * 1.8f, seconds * 1.8f)
                direction = Offset(x = cos(plane.heading), y = sin(plane.heading))
            }
            plane.point += direction * remaining
        }
        val heading = atan2(direction.y, direction.x)
        val error = atan2(sin(heading - plane.heading), cos(heading - plane.heading))
        plane.heading += error.coerceIn(-seconds * 7f, seconds * 7f)
        if (plane.point.x in 0f..width && plane.point.y in 0f..height) plane.entered = true
        return plane.cleared && onRunway(plane.point, plane.kind, 5f)
    }

    override fun advance(seconds: Float) {
        time = (time + seconds) % 600f
        effects.advance(seconds)
        timer -= seconds
        if (timer <= 0f) {
            timer = if (spawn()) 2.8f - pace * 1.4f else 0.35f
        }
        val iterator = planes.iterator()
        while (iterator.hasNext()) {
            val plane = iterator.next()
            plane.warning = false
            if (plane.landing > 0f) {
                val target = zone(plane.kind)
                plane.point += (target - plane.point) * minOf(1f, seconds * 6f)
                val heading = runways[plane.kind].heading
                val error = atan2(sin(heading - plane.heading), cos(heading - plane.heading))
                plane.heading += error * minOf(1f, seconds * 8f)
                plane.landing -= seconds
                if (plane.landing <= 0f) iterator.remove()
                continue
            }
            if (move(plane, seconds)) {
                plane.landing = 0.6f
                plane.route.clear()
                landed++
                if (landed % 8 == 0) timer = maxOf(timer, 3.8f)
                score += if (plane.kind == 1) 20 else 10
                effects.burst(
                    point = zone(plane.kind),
                    count = 10
                )
                if (selected == plane.id) stopInput()
            }
        }
        for (i in planes.indices) for (j in i + 1 until planes.size) {
            val a = planes[i]
            val b = planes[j]
            if (a.landing > 0f || b.landing > 0f || !a.entered || !b.entered) continue
            val distance = (a.point - b.point).getDistance()
            fun velocity(plane: ControlledFlight): Offset {
                val delta = plane.route.firstOrNull()?.minus(plane.point)
                val direction =
                    if (delta != null && delta.getDistance() > 0.01f) delta / delta.getDistance()
                    else Offset(cos(plane.heading), sin(plane.heading))
                val speed = (30f + pace * 12f) * when (plane.kind) {
                    1 -> 1.12f; 2 -> 0.75f; else -> 1f
                }
                return direction * speed
            }

            val relative = a.point - b.point
            val motion = velocity(a) - velocity(b)
            val encounter = (-(relative.x * motion.x + relative.y * motion.y) /
                    motion.getDistanceSquared().coerceAtLeast(0.001f)).coerceIn(0f, 1.6f)
            if (distance < 38f || (relative + motion * encounter).getDistance() < 24f) {
                a.warning = true
                b.warning = true
            }
            if (distance < 14f) {
                effects.burst(
                    point = (a.point + b.point) / 2f,
                    count = 18
                )
                phase = ArcadePhase.Over
                stopInput()
            }
        }
    }

    override fun onResize(previous: Size) {
        val x = width / previous.width
        val y = height / previous.height
        planes.forEach { plane ->
            plane.point = Offset(x = plane.point.x * x, y = plane.point.y * y)
            val route = plane.route.map { Offset(x = it.x * x, y = it.y * y) }
            plane.route.clear()
            plane.route.addAll(route)
            if (plane.cleared && plane.route.isNotEmpty()) {
                plane.route.removeLast()
                plane.route.addLast(zone(plane.kind))
            }
        }
        stopInput()
    }
}