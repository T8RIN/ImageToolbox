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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeOffset
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.pixelPanel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Brick
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.random.Random

@RegisterArcadeGame
internal data object RicochetGame : ArcadeGameDefinition {
    override val title = R.string.arcade_ricochet
    override val description = R.string.arcade_ricochet_sub
    override val icon get() = Icons.Outlined.Brick
    override val order = 9

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::RicochetRun
        )
        val measurer = rememberTextMeasurer()

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
                onTap = { run.shoot() },
                onSwipe = { run.shoot() },
                onRelease = run::stopInput
            ) {
                drawRicochetBackground(style, run.width, run.height)
                val cell = run.cell
                val radius = run.ballRadius
                val rowHeight = run.rowHeight
                for (row in 0..6) for (column in 0..6) {
                    pixelCircle(
                        color = style.detailColor.copy(alpha = 0.2f),
                        radius = 1f,
                        center = run.origin + Offset(
                            x = (column + 0.5f) * cell,
                            y = (row + 0.5f) * rowHeight
                        )
                    )
                }
                for (beam in run.beams) {
                    val fade = (beam.time / 0.18f).coerceIn(0f, 1f)
                    val center = run.origin + Offset(
                        (beam.column + 0.5f) * cell,
                        (beam.row + 0.5f) * rowHeight
                    )
                    if (beam.vertical) {
                        pixelRect(
                            style.playerColor.copy(alpha = fade * 0.32f), center.x - 3f,
                            run.origin.y, 6f, run.launchY - run.origin.y
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = fade * 0.8f), center.x - 1f,
                            run.origin.y, 2f, run.launchY - run.origin.y
                        )
                    } else {
                        pixelRect(
                            style.playerColor.copy(alpha = fade * 0.32f), run.origin.x,
                            center.y - 3f, cell * 7f, 6f
                        )
                        pixelRect(
                            style.playerDetailColor.copy(alpha = fade * 0.8f), run.origin.x,
                            center.y - 1f, cell * 7f, 2f
                        )
                    }
                }
                for (brick in run.bricks) {
                    val (column, row, health) = brick
                    val point = run.origin + Offset(
                        x = column * cell + 2f,
                        y = (row - run.shift) * rowHeight + 2f
                    )
                    val center = point + Offset(x = cell / 2f - 2f, y = rowHeight / 2f - 2f)
                    if (health == 0) {
                        val color = if (brick.item == 0) style.accentColor else style.playerColor
                        pixelCircle(
                            color = color.copy(alpha = 0.14f),
                            radius = minOf(cell, rowHeight) * 0.28f,
                            center = center
                        )
                        pixelCircle(
                            color = color,
                            radius = minOf(cell, rowHeight) * 0.22f,
                            center = center,
                            style = Stroke(width = 1.5f)
                        )
                        val extent = minOf(cell, rowHeight) * 0.11f
                        if (brick.item != 2) drawLine(
                            color, center - Offset(extent, 0f), center + Offset(extent, 0f), 2f
                        )
                        if (brick.item != 1) drawLine(
                            color, center - Offset(0f, extent), center + Offset(0f, extent), 2f
                        )
                        if (brick.item > 0) {
                            for (side in listOf(-1f, 1f)) for (step in 0..1) {
                                val along = (extent - step * 2f) * side
                                val across = (step + 1f) * 2f
                                if (brick.item == 1) {
                                    pixelRect(
                                        color,
                                        center.x + along - 1f,
                                        center.y - across,
                                        2f,
                                        2f
                                    )
                                    pixelRect(
                                        color,
                                        center.x + along - 1f,
                                        center.y + across - 2f,
                                        2f,
                                        2f
                                    )
                                } else {
                                    pixelRect(
                                        color,
                                        center.x - across,
                                        center.y + along - 1f,
                                        2f,
                                        2f
                                    )
                                    pixelRect(
                                        color,
                                        center.x + across - 2f,
                                        center.y + along - 1f,
                                        2f,
                                        2f
                                    )
                                }
                            }
                        }
                    } else {
                        val color =
                            if (health > run.ballCount) style.accentColor else style.obstacleColor
                        drawRicochetBrick(
                            brick = brick,
                            point = point,
                            size = Size(cell - 4f, rowHeight - 4f),
                            color = color,
                            style = style,
                            measurer = measurer
                        )
                    }
                }
                val left = run.origin.x
                drawLine(
                    color = style.hazardColor.copy(alpha = 0.5f),
                    start = Offset(x = left, y = run.launchY - rowHeight * 0.35f),
                    end = Offset(x = left + cell * 7f, y = run.launchY - rowHeight * 0.35f),
                    strokeWidth = 1f
                )
                if (run.flying) {
                    val point = run.recallPoint
                    pixelCircle(style.detailColor.copy(alpha = 0.2f), 10f, point)
                    drawLine(style.accentColor, point + Offset(0f, -5f), point + Offset(0f, 4f), 2f)
                    for (side in listOf(-1f, 1f)) drawLine(
                        style.accentColor, point + Offset(side * 4f, 0f), point + Offset(0f, 4f), 2f
                    )
                }
                if (!run.flying) {
                    var point = Offset(x = run.launchX, y = run.launchY)
                    var direction = run.direction
                    val right = left + cell * 7f - radius
                    repeat(24) { index ->
                        point += direction * (rowHeight * 0.28f)
                        if (point.x < left + radius) {
                            point = point.copy(x = (left + radius) * 2f - point.x)
                            direction = direction.copy(x = -direction.x)
                        } else if (point.x > right) {
                            point = point.copy(x = right * 2f - point.x)
                            direction = direction.copy(x = -direction.x)
                        }
                        if (point.y < run.origin.y + radius) {
                            point = point.copy(y = (run.origin.y + radius) * 2f - point.y)
                            direction = direction.copy(y = -direction.y)
                        }
                        if (point.y <= run.launchY) pixelCircle(
                            color = style.playerColor.copy(alpha = 0.6f * (1f - index / 26f)),
                            radius = radius * 0.48f,
                            center = point
                        )
                    }
                }
                for ((x, y, vx, vy) in run.balls) {
                    drawLine(
                        color = style.playerColor.copy(alpha = 0.2f),
                        start = Offset(x = x - vx * 0.025f, y = y - vy * 0.025f),
                        end = Offset(x = x, y = y),
                        strokeWidth = radius * 1.5f
                    )
                    drawRicochetBall(
                        color = style.playerColor,
                        radius = radius,
                        center = Offset(x = x, y = y)
                    )
                }
                drawRicochetBall(
                    color = style.playerColor,
                    radius = radius * 1.3f,
                    center = Offset(x = run.launchX, y = run.launchY)
                )
                val count =
                    measurer.measure(
                        "×${run.ballCount}",
                        style.scoreTextStyle,
                        density = Density(1f)
                    )
                drawText(
                    count,
                    style.playerColor,
                    Offset(
                        x = (run.launchX - count.size.width / 2f)
                            .coerceIn(left, maxOf(left, left + cell * 7f - count.size.width)),
                        y = run.launchY + radius + 3f
                    )
                )
                run.effects.draw(
                    scope = this,
                    color = style.accentColor
                )
            }
        }
    }
}

private fun DrawScope.drawRicochetBall(color: Color, radius: Float, center: Offset) {
    scale(0.5f, center) {
        pixelCircle(color, radius * 2f, center)
    }
}

private fun DrawScope.drawRicochetBrick(
    brick: RicochetBrick,
    point: Offset,
    size: Size,
    color: Color,
    style: ArcadeStyle,
    measurer: TextMeasurer
) {
    if (brick.shape == 0) {
        pixelPanel(color, style.playerDetailColor, point, size)
    } else {
        val vertices = brick.vertices(point, size.width, size.height)
        val outline = Path().apply {
            vertices.forEachIndexed { index, vertex ->
                if (index == 0) moveTo(vertex.x, vertex.y) else lineTo(vertex.x, vertex.y)
            }
            close()
        }
        drawPath(outline, color)
        val sides = vertices.indices.map { index ->
            (vertices[(index + 1) % 3] - vertices[(index + 2) % 3]).getDistance()
        }
        val perimeter = sides.sum()
        val center =
            vertices.indices.map { vertices[it] * sides[it] }.reduce { a, b -> a + b } / perimeter
        val radius = size.width * size.height / perimeter
        val inset = (1f - minOf(1.5f, radius * 0.25f) / radius).coerceAtLeast(0f)
        val inner = vertices.map { center + (it - center) * inset }
        for (index in vertices.indices) {
            val next = (index + 1) % 3
            val edge = vertices[next] - vertices[index]
            drawLine(
                color = style.playerDetailColor.copy(alpha = if (edge.y - edge.x < 0f) 0.34f else 0.12f),
                start = inner[index],
                end = inner[next],
                strokeWidth = 1f
            )
        }
    }
    val padding = minOf(2.5f, size.minDimension * 0.14f)
    var fontSize = size.minDimension * 0.7f
    fun measure() = measurer.measure(
        text = brick.health.toString(),
        style = style.scoreTextStyle.copy(
            fontSize = fontSize.sp,
            lineHeight = fontSize.sp
        ),
        softWrap = false,
        maxLines = 1,
        density = Density(1f)
    )

    var label = measure()
    repeat(2) {
        val fit = if (brick.shape == 0) {
            minOf(
                (size.width - padding * 2f) / label.size.width,
                (size.height - padding * 2f) / label.size.height
            )
        } else {
            val margin = padding * (1f / size.width + 1f / size.height +
                    hypot(1f / size.width, 1f / size.height))
            (1f - margin) / (label.size.width / size.width + label.size.height / size.height)
        }
        if (fit < 1f) {
            fontSize *= (fit * 0.97f).coerceAtLeast(0.01f)
            label = measure()
        }
    }
    val position = if (brick.shape == 0) {
        point + Offset((size.width - label.size.width) / 2f, (size.height - label.size.height) / 2f)
    } else {
        point + Offset(
            x = if (brick.shape == 2 || brick.shape == 3) padding
            else size.width - padding - label.size.width,
            y = if (brick.shape >= 3) padding else size.height - padding - label.size.height
        )
    }
    drawText(label, style.playerDetailColor, position)
}

private fun DrawScope.drawRicochetBackground(style: ArcadeStyle, width: Float, height: Float) {
    val ink = style.detailColor.copy(alpha = 0.13f)
    var x = 0f
    while (x < width) {
        pixelRect(style.detailColor.copy(alpha = 0.018f), x, 0f, 43f, height)
        drawLine(ink.copy(alpha = 0.035f), Offset(x, 0f), Offset(x, height), 0.5f)
        x += 86f
    }
    for (side in 0..1) {
        val edge = if (side == 0) 6f else width - 6f
        val direction = if (side == 0) 1f else -1f
        drawLine(ink, Offset(edge, 8f), Offset(edge, height - 15f), 0.8f)
        var y = 26f
        while (y < height - 18f) {
            drawLine(
                ink,
                Offset(edge - direction * 2f, y - 3f),
                Offset(edge + direction * 2f, y),
                0.7f
            )
            drawLine(
                ink,
                Offset(edge + direction * 2f, y),
                Offset(edge - direction * 2f, y + 3f),
                0.7f
            )
            y += 39f
        }
    }
}

@Serializable
private data class RicochetBrick(
    val column: Int,
    var row: Int,
    var health: Int,
    val shape: Int = 0,
    val item: Int = 0
) {
    var used = false
    var cooldown = 0f

    fun vertices(point: Offset, width: Float, height: Float): List<Offset> {
        val corners = listOf(
            point,
            point + Offset(width, 0f),
            point + Offset(width, height),
            point + Offset(0f, height)
        )
        return if (shape == 0) corners else corners.filterIndexed { index, _ -> index != (shape - 1) }
    }
}

@Serializable
private data class RicochetBall(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val power: Int = 1
)

private class RicochetBeam(
    val column: Int,
    val row: Int,
    val vertical: Boolean,
    var time: Float = 0.18f
)

@Serializable
private class RicochetRun : ArcadeRun() {
    val cell get() = (width - 32f) / 7f
    val rowHeight get() = (height - topInset - 44f) / 8f
    val origin get() = Offset(x = (width - cell * 7f) / 2f, y = topInset + 6f)
    val launchY get() = origin.y + rowHeight * 7.7f
    val ballRadius get() = minOf(cell, rowHeight) * 0.12f
    val bricks = mutableListOf<RicochetBrick>()
    val balls = mutableListOf<RicochetBall>()

    @Transient
    val effects = ArcadeEffects()

    @Transient
    val beams = mutableListOf<RicochetBeam>()

    @Transient
    private var dragOrigin: Offset? = null
    var launchX = width / 2f
    var direction: ArcadeOffset = Offset(x = 0.25f, y = -0.97f)
    var ballCount = 1
    var flying = false
    var shift = 0f
    val recallPoint get() = Offset(origin.x + 12f, launchY)
    private var returning = false
    private var volleyBalls = 1
    private var volleySize = 1

    @Transient
    private var aiming = false
    private var pending = 0
    private var delay = 0f
    private var flightTime = 0f
    private var firstReturn = -1f
    private var round = 1

    override fun reset() {
        bricks.clear()
        balls.clear()
        effects.clear()
        beams.clear()
        dragOrigin = null
        launchX = width / 2f
        direction = Offset(x = 0.25f, y = -0.97f)
        ballCount = 1
        flying = false
        returning = false
        volleyBalls = 1
        volleySize = 1
        aiming = false
        shift = 0f
        pending = 0
        delay = 0f
        flightTime = 0f
        firstReturn = -1f
        round = 1
        addRow(0)
    }

    private fun addRow(row: Int) {
        val columns = (0..6).shuffled()
        val count = if (round == 1) 2 else Random.nextInt(2, minOf(6, round + 3))
        for (column in columns.take(count)) {
            bricks += RicochetBrick(
                column = column,
                row = row,
                health = round.coerceAtMost(ballCount + 8),
                shape = if (round >= 2 && Random.nextFloat() < 0.28f) Random.nextInt(1, 5) else 0
            )
        }
        bricks += RicochetBrick(column = columns[count], row = row, health = 0)
        if (round == 1 || round % 3 == 0) {
            bricks += RicochetBrick(
                column = columns[count + 1], row = row, health = 0,
                item = if (round == 1) 2 else Random.nextInt(1, 3)
            )
        }
    }

    fun begin(point: Offset) {
        if (flying) {
            aim(point)
            return
        }
        if (shift > 0f) return
        dragOrigin = point
        aiming = false
    }

    fun drag(point: Offset) {
        val origin = dragOrigin ?: return
        val pull = origin - point
        if (pull.y >= -3f || pull.getDistance() < 6f) {
            aiming = false
            return
        }
        aim(Offset(launchX, launchY) + pull)
    }

    fun aim(point: Offset) {
        if (flying) {
            if ((point - recallPoint).getDistance() < 23f) recall()
            return
        }
        if (shift > 0f) return
        val dx = point.x - launchX
        val dy = minOf(-rowHeight * 0.7f, point.y - launchY)
        val length = hypot(dx, dy).coerceAtLeast(1f)
        direction = Offset(x = dx / length, y = dy / length)
        aiming = true
    }

    fun shoot() {
        if (!aiming || flying || shift > 0f || phase != ArcadePhase.Running) return
        aiming = false
        flying = true
        returning = false
        volleyBalls = ballCount
        volleySize = minOf(ballCount, 72)
        pending = volleySize
        delay = 0f
        flightTime = 0f
        firstReturn = -1f
    }

    override fun stopInput() {
        aiming = false
        dragOrigin = null
    }

    fun recall() {
        if (!flying || returning) return
        returning = true
        pending = 0
        if (firstReturn < 0f) firstReturn = balls.firstOrNull()?.x ?: launchX
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        beams.forEach { it.time -= seconds }
        beams.removeAll { it.time <= 0f }
        bricks.forEach { it.cooldown = (it.cooldown - seconds).coerceAtLeast(0f) }
        shift = (shift - seconds * 4f).coerceAtLeast(0f)
        if (!flying) return
        flightTime += seconds
        delay -= seconds
        val speed = maxOf(width, height) * 1.3f
        if (pending > 0 && delay <= 0f) {
            balls += RicochetBall(
                x = launchX,
                y = launchY,
                vx = direction.x * speed,
                vy = direction.y * speed,
                power = volleyBalls / volleySize + if (volleySize - pending < volleyBalls % volleySize) 1 else 0
            )
            pending--
            delay += (1.1f / volleySize).coerceIn(0.018f, 0.1f)
        }
        val iterator = balls.iterator()
        val delta = seconds * (1f + (flightTime - 4f).coerceIn(0f, 8f) * 0.18f)
        while (iterator.hasNext()) {
            val ball = iterator.next()
            if (returning) {
                val destination = Offset(firstReturn, launchY)
                val offset = destination - Offset(ball.x, ball.y)
                val distance = offset.getDistance()
                if (distance <= speed * seconds * 2f) {
                    iterator.remove()
                    continue
                }
                val point = Offset(ball.x, ball.y) + offset * (speed * seconds * 2f / distance)
                ball.x = point.x
                ball.y = point.y
            } else {
                val steps = ceil(speed * delta / (ballRadius * 0.8f)).toInt().coerceIn(1, 16)
                repeat(steps) {
                    if (ball.y < launchY || ball.vy < 0f) moveBall(ball, delta / steps)
                }
                if (ball.y >= launchY && ball.vy > 0f) {
                    if (firstReturn < 0f) firstReturn =
                        ball.x.coerceIn(origin.x + ballRadius, origin.x + cell * 7f - ballRadius)
                    iterator.remove()
                }
            }
        }
        if (pending == 0 && balls.isEmpty()) {
            flying = false
            returning = false
            launchX = if (firstReturn >= 0f) firstReturn else width / 2f
            round++
            bricks.removeAll { it.health == 0 && (it.used || it.row >= 5) }
            for (brick in bricks) brick.row++
            if (bricks.any { it.row >= 7 && it.health > 0 }) {
                phase = ArcadePhase.Over
            } else {
                addRow(0)
                shift = 1f
            }
        }
    }

    private fun moveBall(ball: RicochetBall, seconds: Float) {
        ball.x += ball.vx * seconds
        ball.y += ball.vy * seconds
        val left = origin.x + ballRadius
        val right = origin.x + cell * 7f - ballRadius
        val wallHit = ball.x < left || ball.x > right
        if (ball.x < left) {
            ball.x = left * 2f - ball.x
            ball.vx = abs(ball.vx)
        } else if (ball.x > right) {
            ball.x = right * 2f - ball.x
            ball.vx = -abs(ball.vx)
        }
        if (wallHit) {
            val speed = hypot(ball.vx, ball.vy)
            val minimum = speed * 0.035f
            if (abs(ball.vy) < minimum) {
                ball.vy = if (ball.vy <= 0f) -minimum else minimum
                val horizontal = sqrt((speed * speed - minimum * minimum).coerceAtLeast(0f))
                ball.vx = if (ball.vx < 0f) -horizontal else horizontal
            }
        }
        val top = origin.y + ballRadius
        if (ball.y < top) {
            ball.y = top * 2f - ball.y
            ball.vy = abs(ball.vy)
        }
        val point = Offset(ball.x, ball.y)
        for (index in bricks.indices) {
            val brick = bricks[index]
            val corner = origin + Offset(brick.column * cell + 2f, brick.row * rowHeight + 2f)
            val size = Offset(cell - 4f, rowHeight - 4f)
            if (brick.health == 0) {
                if ((point - corner - size / 2f).getDistance() > minOf(
                        cell,
                        rowHeight
                    ) * 0.22f + ballRadius
                ) continue
                if (brick.item == 0) {
                    ballCount++
                    score += 5
                    bricks.removeAt(index)
                } else {
                    if (brick.cooldown > 0f) continue
                    brick.cooldown = 0.08f
                    brick.used = true
                    fireLine(brick, ball.power)
                }
            } else {
                if (ball.x + ballRadius < corner.x || ball.x - ballRadius > corner.x + size.x ||
                    ball.y + ballRadius < corner.y || ball.y - ballRadius > corner.y + size.y
                ) continue
                val vertices = brick.vertices(corner, size.x, size.y)
                var inside = true
                var nearest = Float.MAX_VALUE
                var normal = Offset.Zero
                var closest = Offset.Zero
                for (side in vertices.indices) {
                    val a = vertices[side]
                    val edge = vertices[(side + 1) % vertices.size] - a
                    val offset = point - a
                    if (edge.x * offset.y - edge.y * offset.x < 0f) inside = false
                    val projection =
                        (offset.x * edge.x + offset.y * edge.y) / edge.getDistanceSquared()
                    val contact = a + edge * projection.coerceIn(0f, 1f)
                    val distance = (point - contact).getDistanceSquared()
                    if (distance < nearest) {
                        nearest = distance
                        closest = contact
                        normal = Offset(edge.y, -edge.x) / edge.getDistance()
                    }
                }
                if (!inside && nearest >= ballRadius * ballRadius) continue
                val distance = sqrt(nearest)
                if (!inside && distance > 0.001f) normal = (point - closest) / distance
                val approach = ball.vx * normal.x + ball.vy * normal.y
                if (approach >= 0f && !inside) continue
                val position =
                    point + normal * (ballRadius + if (inside) distance + 0.01f else -distance + 0.01f)
                ball.x = position.x
                ball.y = position.y
                if (approach < 0f) {
                    ball.vx -= normal.x * approach * 2f
                    ball.vy -= normal.y * approach * 2f
                }
                val damage = minOf(brick.health, ball.power)
                brick.health -= damage
                score += damage
                if (brick.health == 0) bricks.removeAt(index)
            }
            effects.burst(Offset(ball.x, ball.y), 5)
            return
        }
    }

    private fun fireLine(item: RicochetBrick, power: Int) {
        beams += RicochetBeam(item.column, item.row, item.item == 2)
        val iterator = bricks.iterator()
        while (iterator.hasNext()) {
            val brick = iterator.next()
            if (brick.health <= 0 ||
                (if (item.item == 1) brick.row != item.row else brick.column != item.column)
            ) continue
            val damage = minOf(brick.health, power)
            brick.health -= damage
            score += damage
            effects.burst(
                origin + Offset(
                    (brick.column + 0.5f) * cell,
                    (brick.row + 0.5f) * rowHeight
                ), 4
            )
            if (brick.health == 0) iterator.remove()
        }
    }

    override fun onResize(previous: Size) {
        launchX *= width / previous.width
        for (ball in balls) {
            ball.x *= width / previous.width
            ball.y *= height / previous.height
        }
        effects.clear()
        beams.clear()
        stopInput()
    }
}