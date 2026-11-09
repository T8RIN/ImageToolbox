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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val FlightPlaneSprite = ArcadeSprite(
    "FlightPlane",
    22,
    24,
    "M10 0H12V2H13V8L22 14V17L13 13V20L17 22V24L11 22L5 24V22L9 20V13L0 17V14L9 8V2H10Z"
)
private val FlightHelicopterSprite = ArcadeSprite(
    "FlightHelicopter",
    18,
    24,
    "M7 2H11V4H13V12H11V20H13V22H5V20H7V12H5V4H7Z M2 5H4V14H2Z M14 5H16V14H14Z"
)

@Composable
fun FlightControlGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::FlightControlRun
    )

    ArcadeBoard(
        game = ArcadeGame.FlightControl,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = run::begin,
            onDrag = run::drag,
            onRelease = run::commit
        ) {
            val colors = listOf(style.playerColor, style.hazardColor, style.accentColor)
            repeat(22) { index ->
                val p = Offset(
                    ((index * 71 + 37) % 353) / 353f * run.width,
                    ((index * 101 + 19) % 419) / 419f * run.height
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.23f),
                    x = p.x,
                    y = p.y,
                    width = 6f,
                    height = 2f
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.16f),
                    x = p.x + 2f,
                    y = p.y - 3f,
                    width = 2f,
                    height = 8f
                )
            }
            repeat(3) { kind ->
                val p = run.zone(kind)
                val color = colors[kind]
                val active = run.planes.any { it.kind == kind && it.cleared }
                if (kind == 2) {
                    drawCircle(
                        color = color.copy(alpha = if (active) 0.28f else 0.12f),
                        radius = 21f,
                        center = p
                    )
                    drawCircle(
                        color = color.copy(alpha = 0.65f),
                        radius = 17f,
                        center = p,
                        style = Stroke(1.5f)
                    )
                    pixelRect(
                        color = color,
                        x = p.x - 7f,
                        y = p.y - 8f,
                        width = 3f,
                        height = 16f
                    )
                    pixelRect(
                        color = color,
                        x = p.x + 4f,
                        y = p.y - 8f,
                        width = 3f,
                        height = 16f
                    )
                    pixelRect(
                        color = color,
                        x = p.x - 5f,
                        y = p.y - 1.5f,
                        width = 10f,
                        height = 3f
                    )
                } else rotate(run.runways[kind].heading * 180f / PI.toFloat(), p) {
                    drawRoundRect(
                        color = color.copy(alpha = 0.12f),
                        topLeft = p - Offset(35f, 12f),
                        size = Size(70f, 24f),
                        cornerRadius = CornerRadius(3f)
                    )
                    drawRoundRect(
                        color = color.copy(alpha = if (active) 1f else 0.65f),
                        topLeft = p - Offset(35f, 12f),
                        size = Size(70f, 24f),
                        cornerRadius = CornerRadius(3f),
                        style = Stroke(1f)
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
                    drawLine(
                        color = color,
                        start = p + Offset(-47f, -5f),
                        end = p + Offset(-42f, 0f),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = color,
                        start = p + Offset(-47f, 5f),
                        end = p + Offset(-42f, 0f),
                        strokeWidth = 1.5f
                    )
                }
            }
            for (plane in run.planes) {
                val color = colors[plane.kind]
                if (plane.route.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(plane.point.x, plane.point.y)
                        plane.route.forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(
                        path = path,
                        color = color.copy(alpha = if (plane.id == run.selected) 0.15f else 0.4f),
                        style = Stroke(
                            1f,
                            cap = StrokeCap.Round
                        )
                    )
                }
                if (plane.warning || plane.id == run.selected) drawCircle(
                    color = if (plane.warning) style.hazardColor.copy(alpha = 0.7f) else color.copy(
                        alpha = 0.5f
                    ),
                    radius = 17f + if (plane.warning) sin(run.time * 10f) * 2f else 0f,
                    center = plane.point,
                    style = Stroke(1f)
                )
                rotate(plane.heading * 180f / PI.toFloat() + 90f, plane.point) {
                    scale(
                        if (plane.landing > 0f) (plane.landing / 0.6f).coerceAtLeast(0.1f) else 1f,
                        plane.point
                    ) {
                        if (plane.kind == 2) {
                            drawArcadeSprite(
                                sprite = FlightHelicopterSprite,
                                tint = color,
                                x = plane.point.x - 9f,
                                y = plane.point.y - 12f
                            )
                            rotate(run.time * 950f, plane.point - Offset(0f, 4f)) {
                                pixelRect(
                                    color = color.copy(alpha = 0.8f),
                                    x = plane.point.x - 12f,
                                    y = plane.point.y - 5f,
                                    width = 24f,
                                    height = 2f
                                )
                            }
                        } else {
                            drawArcadeSprite(
                                sprite = FlightPlaneSprite,
                                tint = color,
                                x = plane.point.x - 11f,
                                y = plane.point.y - 12f
                            )
                            pixelRect(
                                color = style.playerDetailColor,
                                x = plane.point.x - 1f,
                                y = plane.point.y - 8f,
                                width = 2f,
                                height = 4f
                            )
                            if (plane.kind == 1) {
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = plane.point.x - 5f,
                                    y = plane.point.y + 1f,
                                    width = 3f,
                                    height = 3f
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = plane.point.x + 2f,
                                    y = plane.point.y + 1f,
                                    width = 3f,
                                    height = 3f
                                )
                            }
                        }
                    }
                }
            }
            if (run.drawing.size > 1) {
                val color = colors[run.planes.firstOrNull { it.id == run.selected }?.kind ?: 0]
                val path = Path().apply {
                    moveTo(run.drawing.first().x, run.drawing.first().y)
                    run.drawing.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = path,
                    color = color.copy(alpha = 0.18f),
                    style = Stroke(
                        7f,
                        cap = StrokeCap.Round
                    )
                )
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        2f,
                        cap = StrokeCap.Round
                    )
                )
                drawCircle(
                    color = color,
                    radius = 4f,
                    center = run.drawing.last(),
                    style = Stroke(1.5f)
                )
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

internal data class FlightRunway(
    val position: Offset,
    val heading: Float
)

internal class ControlledFlight(
    val id: Int,
    val kind: Int,
    var point: Offset,
    var heading: Float,
    var entered: Boolean = false,
    var landing: Float = 0f,
    var cleared: Boolean = false
) {
    val route = ArrayDeque<Offset>()
    var warning = false
}

internal class FlightControlRun : ArcadeRun() {
    val planes = mutableListOf<ControlledFlight>()
    val runways = mutableListOf<FlightRunway>()
    val drawing = mutableListOf<Offset>()
    val effects = ArcadeEffects()
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
            52f + position.x * (width - 104f),
            topInset + 44f + position.y * (height - topInset - 88f)
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

    override fun reset() {
        runways.clear()
        val positions = listOf(
            Offset(Random.nextFloat() * 0.16f, Random.nextFloat()),
            Offset(0.84f + Random.nextFloat() * 0.16f, Random.nextFloat())
        ).shuffled()
        positions.forEach {
            runways += FlightRunway(it, Random.nextInt(8) * PI.toFloat() / 4f)
        }
        runways += FlightRunway(
            Offset(0.47f + Random.nextFloat() * 0.06f, Random.nextFloat()),
            0f
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
            nextId++,
            0,
            Offset(-18f, topInset + (height - topInset) * 0.36f),
            0f
        )
    }

    fun begin(point: Offset) {
        val plane =
            planes.filter { it.landing <= 0f }.minByOrNull { (it.point - point).getDistance() }
        selected = plane?.takeIf { (it.point - point).getDistance() <= 32f }?.id ?: -1
        drawing.clear()
        if (selected >= 0) drawing += plane!!.point
    }

    fun drag(point: Offset) {
        if (selected < 0) return
        val point = Offset(
            point.x.coerceIn(12f, width - 12f),
            point.y.coerceIn(topInset + 12f, height - 12f)
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
        drawing.drop(nearest + 1).forEach(plane.route::addLast)
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
            plane.id, plane.kind, plane.point, plane.heading,
            plane.entered, plane.landing, plane.cleared
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
                0 -> Offset(-18f, topInset + 20f + Random.nextFloat() * (height - topInset - 50f))
                1 -> Offset(
                    width + 18f,
                    topInset + 20f + Random.nextFloat() * (height - topInset - 50f)
                )

                2 -> Offset(24f + Random.nextFloat() * (width - 48f), -18f)
                else -> Offset(24f + Random.nextFloat() * (width - 48f), height + 18f)
            }
            val delta = Offset(
                width * (0.25f + Random.nextFloat() * 0.5f),
                topInset + (height - topInset) * (0.25f + Random.nextFloat() * 0.5f)
            ) - p
            val plane = ControlledFlight(nextId, kind, p, atan2(delta.y, delta.x))
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
        var direction = Offset(cos(plane.heading), sin(plane.heading))
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
                    width * (0.25f + Random.nextFloat() * 0.5f),
                    topInset + (height - topInset) * (0.25f + Random.nextFloat() * 0.5f)
                ) - plane.point
                val desired = atan2(delta.y, delta.x)
                val error = atan2(sin(desired - plane.heading), cos(desired - plane.heading))
                plane.heading += error.coerceIn(-seconds * 1.8f, seconds * 1.8f)
                direction = Offset(cos(plane.heading), sin(plane.heading))
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
            if (distance < 38f) {
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
            plane.point = Offset(plane.point.x * x, plane.point.y * y)
            val route = plane.route.map { Offset(it.x * x, it.y * y) }
            plane.route.clear()
            plane.route.addAll(route)
            if (plane.cleared && plane.route.isNotEmpty()) {
                plane.route.removeLast()
                plane.route.addLast(zone(plane.kind))
            }
        }
        stopInput()
    }

    override fun save(state: Bundle) {
        state.putFloatArray("runways", runways.flatMap {
            listOf(it.position.x, it.position.y, it.heading)
        }.toFloatArray())
        state.putIntArray("run", intArrayOf(landed, nextId, planes.size))
        state.putFloatArray("time", floatArrayOf(time, timer))
        planes.forEachIndexed { index, plane ->
            state.putIntArray(
                "plane$index",
                intArrayOf(
                    plane.id,
                    plane.kind,
                    if (plane.entered) 1 else 0,
                    if (plane.cleared) 1 else 0
                )
            )
            state.putFloatArray(
                "position$index",
                floatArrayOf(
                    plane.point.x,
                    plane.point.y,
                    plane.heading,
                    plane.landing
                )
            )
            state.putFloatArray(
                "route$index",
                plane.route.flatMap { listOf(it.x, it.y) }.toFloatArray()
            )
        }
    }

    override fun restore(state: Bundle) {
        runways.clear()
        state.getFloatArray("runways")!!.let {
            for (index in it.indices step 3) {
                runways += FlightRunway(Offset(it[index], it[index + 1]), it[index + 2])
            }
        }
        val run = state.getIntArray("run")!!
        landed = run[0]
        nextId = run[1]
        state.getFloatArray("time")!!.let {
            time = it[0]
            timer = it[1]
        }
        planes.clear()
        repeat(run[2]) { index ->
            val data = state.getIntArray("plane$index")!!
            val point = state.getFloatArray("position$index")!!
            val plane = ControlledFlight(
                data[0],
                data[1],
                Offset(point[0], point[1]),
                point[2],
                data[2] == 1,
                point[3],
                data[3] == 1
            )
            state.getFloatArray("route$index")!!
                .let { for (i in it.indices step 2) plane.route += Offset(it[i], it[i + 1]) }
            planes += plane
        }
        stopInput()
    }
}