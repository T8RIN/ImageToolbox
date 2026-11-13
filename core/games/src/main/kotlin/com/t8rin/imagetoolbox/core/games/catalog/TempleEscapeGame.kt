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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ShineDiamond
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object TempleEscapeGame : ArcadeGameDefinition {
    override val title = R.string.arcade_templeescape
    override val description = R.string.arcade_templeescape_sub
    override val icon get() = Icons.Outlined.ShineDiamond
    override val order = 35

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::TempleEscapeRun
        )
        val fieldPaint = remember { Paint() }
        val wallPath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                transition = { run.transition },
                onSwipe = run::swipe,
                swipeOnDrag = true
            ) {
                drawTempleBackground(style, run.width, run.height)
                if (run.freeze > 0f) drawRect(style.accentColor.copy(alpha = 0.07f), size = size)
                val cell = run.cell
                val unit = cell / 30f
                val left = run.left
                val top = run.topInset + 6f
                val bottom = run.height
                val ink = style.obstacleColor
                val fieldSize = Size(width = run.width, height = run.height)
                drawContext.canvas.saveLayer(
                    bounds = Rect(offset = Offset.Zero, size = fieldSize),
                    paint = fieldPaint
                )
                try {
                    val first = floor(run.camera - top / cell).toInt().coerceAtLeast(0)
                    val last =
                        (run.camera + run.visibleRows + 1f).toInt().coerceAtMost(run.rows - 1)
                    wallPath.rewind()
                    for (row in first..last) {
                        var column = 0
                        while (column < run.columns) {
                            if (!run.walls[row * run.columns + column]) {
                                column++
                                continue
                            }
                            val start = column
                            while (column < run.columns && run.walls[row * run.columns + column]) column++
                            wallPath.addRect(
                                rect = Rect(
                                    left = left + start * cell,
                                    top = top + (row - run.camera) * cell,
                                    right = left + column * cell,
                                    bottom = top + (row + 1f - run.camera) * cell
                                )
                            )
                        }
                    }
                    drawPath(path = wallPath, color = ink.copy(alpha = 0.5f))
                    for (row in first..last) for (column in 0 until run.columns) {
                        val index = row * run.columns + column
                        val x = left + column * cell
                        val y = top + (row - run.camera) * cell
                        if (run.walls[index]) {
                            val openLeft = column > 0 && !run.walls[index - 1]
                            val openRight = column < run.columns - 1 && !run.walls[index + 1]
                            val openTop = row > 0 && !run.walls[index - run.columns]
                            val openBottom = row < run.rows - 1 && !run.walls[index + run.columns]
                            val exposed = openLeft || openRight || openTop || openBottom
                            if (openTop) {
                                pixelRect(
                                    color = ink,
                                    x = x + if (openLeft) unit * 2f else 0f,
                                    y = y,
                                    width = cell - if (openLeft || openRight) unit * 2f else 0f,
                                    height = unit * 2f
                                )
                                pixelRect(
                                    color = ink.copy(alpha = 0.15f),
                                    x = x + unit * 3f, y = y + unit * 3f,
                                    width = cell * 0.55f, height = unit
                                )
                            }
                            if (openBottom) pixelRect(
                                color = ink.copy(alpha = 0.7f),
                                x = x, y = y + cell - unit * 2f,
                                width = cell, height = unit * 2f
                            )
                            if (openLeft) pixelRect(
                                color = ink.copy(alpha = 0.8f),
                                x = x, y = y + if (openTop) unit * 2f else 0f,
                                width = unit * 2f, height = cell - if (openTop) unit * 2f else 0f
                            )
                            if (openRight) pixelRect(
                                color = ink.copy(alpha = 0.8f),
                                x = x + cell - unit * 2f, y = y + if (openTop) unit * 2f else 0f,
                                width = unit * 2f, height = cell - if (openTop) unit * 2f else 0f
                            )
                            if (exposed && (column * 7 + row * 3) % 5 == 0) {
                                pixelRect(
                                    color = ink.copy(alpha = 0.45f),
                                    x = x + cell * 0.28f, y = y + cell * 0.5f,
                                    width = cell * 0.2f, height = unit
                                )
                                pixelRect(
                                    color = ink.copy(alpha = 0.45f),
                                    x = x + cell * 0.45f, y = y + cell * 0.5f,
                                    width = unit, height = unit * 3f
                                )
                            }
                        } else when (run.pickups[index]) {
                            1 -> pixelRect(
                                color = style.accentColor.copy(alpha = 0.7f),
                                x = x + cell * 0.45f,
                                y = y + cell * 0.45f,
                                width = unit * 2f,
                                height = unit * 2f
                            )

                            2 -> {
                                val point = Offset(x = x + cell / 2f, y = y + cell / 2f)
                                scale(scale = unit, pivot = point) {
                                    drawArcadeSprite(
                                        sprite = TempleRelic,
                                        tint = style.accentColor,
                                        x = point.x - 6f,
                                        y = point.y - 7f + sin(run.time * 4f) * 0.6f
                                    )
                                }
                            }

                            4 -> {
                                val point = Offset(x + cell / 2f, y + cell / 2f)
                                for (axis in listOf(
                                    Offset(1f, 0f),
                                    Offset(0f, 1f),
                                    Offset(0.7f, 0.7f),
                                    Offset(0.7f, -0.7f)
                                )) {
                                    drawLine(
                                        style.accentColor, point - axis * cell * 0.28f,
                                        point + axis * cell * 0.28f, unit * 2f
                                    )
                                }
                            }

                            3 -> {
                                val point = Offset(x = x + cell / 2f, y = y + cell / 2f)
                                scale(scale = unit, pivot = point) {
                                    drawArcadeSprite(
                                        sprite = TempleShield,
                                        tint = style.playerColor,
                                        x = point.x - 7f,
                                        y = point.y - 8f
                                    )
                                }
                            }
                        }
                    }
                    for ((index, side) in run.traps) {
                        val point = run.point(index = index)
                        val active = run.trapActive(index = index)
                        val warning = run.trapWarning(index = index)
                        rotate(degrees = side * 90f, pivot = point) {
                            val reach = if (active) 14f else 2f
                            pixelRect(
                                color = style.hazardColor.copy(alpha = if (active || warning) 0.18f else 0.06f),
                                x = point.x - cell * 0.44f,
                                y = point.y - cell * 0.44f,
                                width = cell * 0.88f,
                                height = cell * 0.88f
                            )
                            repeat(3) { tooth ->
                                val x = point.x + (tooth * 5f - 6f) * unit
                                val y = point.y - cell / 2f
                                val color =
                                    style.hazardColor.copy(alpha = if (active || warning) 1f else 0.4f)
                                pixelRect(
                                    color = color,
                                    x = x - unit,
                                    y = y,
                                    width = unit * 3f,
                                    height = unit * reach
                                )
                                pixelRect(
                                    color = color,
                                    x = x,
                                    y = y + unit * reach,
                                    width = unit,
                                    height = unit * 2f
                                )
                            }
                        }
                    }
                    val door = run.point(index = run.exit)
                    scale(scale = unit, pivot = door) {
                        drawArcadeSprite(
                            sprite = TempleDoor,
                            tint = style.accentColor,
                            x = door.x - 12f,
                            y = door.y - 14f
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = door.x - 1f,
                            y = door.y - 4f,
                            width = 2f,
                            height = 9f
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = door.x - 3f,
                            y = door.y - 2f,
                            width = 6f,
                            height = 2f
                        )
                    }
                    for (guard in run.guards) {
                        val point = run.guardPoint(guard = guard)
                        scale(scale = unit, pivot = point) {
                            drawArcadeSprite(
                                sprite = if ((run.time * 7f + guard.phase).toInt() % 2 == 0) TempleBatUp else TempleBatDown,
                                tint = style.accentColor,
                                x = point.x - 16f,
                                y = point.y - 12f
                            )
                        }
                    }
                    run.trail.forEachIndexed { index, tile ->
                        val from = run.point(index = tile)
                        val to =
                            run.trail.getOrNull(index = index + 1)?.let { run.point(index = it) }
                                ?: run.playerPoint
                        drawLine(
                            color = style.playerColor.copy(alpha = run.trailFade * (index + 1f) * 0.035f),
                            start = from,
                            end = to,
                            strokeWidth = cell * 0.55f
                        )
                    }
                    val motion = (abs(run.motionX) + abs(run.motionY)).coerceAtMost(1f)
                    val spiritUnit = cell / 36f
                    val player = run.playerPoint + Offset(
                        x = 0f,
                        y = sin(run.time * 3f) * spiritUnit * (1f - motion)
                    )
                    val squash = run.impact * cos((1f - run.impact) * 2f * PI.toFloat()) * 0.18f
                    scale(
                        scaleX = spiritUnit * (1f + abs(run.motionX) * 0.12f - abs(run.motionY) * 0.06f) *
                                (1f + if (run.impactHorizontal) -squash else squash * 0.5f),
                        scaleY = spiritUnit * (1f + abs(run.motionY) * 0.12f - abs(run.motionX) * 0.06f) *
                                (1f + if (run.impactHorizontal) squash * 0.5f else -squash),
                        pivot = player
                    ) {
                        val tint =
                            if (run.phase == ArcadePhase.Over) style.hazardColor else style.playerColor
                        rotate(degrees = run.motionX * 10f, pivot = player) {
                            drawArcadeSprite(
                                sprite = TempleSpirit[run.flutter.toInt()],
                                tint = tint,
                                x = player.x - 17f,
                                y = player.y - 17f
                            )
                            drawArcadeSprite(
                                sprite = TempleFace,
                                tint = style.playerDetailColor,
                                x = player.x - 17f,
                                y = player.y - 17f
                            )
                            val blink = motion < 0.1f && run.time % 5.6f > 5.48f
                            repeat(2) { eye ->
                                pixelRect(
                                    color = tint,
                                    x = player.x - 5f + eye * 8f + run.motionX,
                                    y = player.y - 3f + run.motionY + if (blink) 2f else 0f,
                                    width = 3f,
                                    height = if (blink) 1f else 5f
                                )
                            }
                        }
                    }
                    if (run.shield || run.protection > 0f) drawCircle(
                        color = style.playerColor.copy(alpha = 0.7f),
                        radius = cell * 0.62f,
                        center = player,
                        style = Stroke(width = unit)
                    )
                    val lavaUnit = cell / 20f
                    val floodY = top + (run.flood - run.camera) * cell
                    if (floodY < bottom) {
                        pixelRect(
                            color = style.hazardColor.copy(alpha = 0.85f),
                            x = left,
                            y = floodY,
                            width = cell * run.columns,
                            height = bottom - floodY
                        )
                        for (column in 0 until run.columns) {
                            val ripple = sin(run.time * 4f + column * 1.3f) * lavaUnit * 1.5f
                            pixelRect(
                                color = style.hazardColor,
                                x = left + column * cell,
                                y = floodY + ripple - lavaUnit * 2f,
                                width = cell,
                                height = lavaUnit * 4f
                            )
                            if (column % 3 == 1) pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.5f),
                                x = left + (column + 0.2f) * cell,
                                y = floodY + ripple + lavaUnit * 3f,
                                width = cell * 0.35f,
                                height = lavaUnit
                            )
                        }
                    }
                    run.effects.draw(
                        scope = this,
                        color = style.accentColor
                    )
                    repeat(8) { band ->
                        drawRect(
                            color = style.textColor.copy(alpha = band / 8f),
                            topLeft = Offset(0f, run.topInset * band / 8f),
                            size = Size(run.width, run.topInset / 8f),
                            blendMode = BlendMode.DstIn
                        )
                    }
                } finally {
                    drawContext.canvas.restore()
                }
                scale(
                    scale = minOf(run.topInset / 44f, run.width / 320f),
                    pivot = Offset.Zero
                ) {
                    repeat(3) { index ->
                        drawArcadeSprite(
                            sprite = TempleRelic,
                            tint = if (index < run.relics) style.accentColor else style.detailColor.copy(
                                alpha = 0.4f
                            ),
                            x = 14f + index * 18f,
                            y = 15f
                        )
                    }
                }
            }
        }
    }
}

private const val TempleHood =
    "M3 23H4V16H5V15H6V13H7V9H8V8H9V7H10V6H11V5H12V4H13V3H15V2H16V1H17V2H19V3H22V5H25V9H27V14H30V21H31V22H32V23H33V24H29V26H3Z"

private val TempleSpirit = listOf(
    ArcadeSprite(
        width = 34, height = 34,
        data = "$TempleHood M2 26H29V30H28V29H26V28H24V30H23V32H22V33H21V32H20V31H18V30H17V31H16V32H15V33H14V32H13V31H12V30H11V29H9V30H7V31H6V27H3V28H2Z"
    ),
    ArcadeSprite(
        width = 34, height = 34,
        data = "$TempleHood M2 26H29V29H26V28H24V29H25V30H24V32H23V33H22V32H21V31H19V30H18V31H17V32H16V33H15V32H14V31H13V30H12V29H10V30H8V31H7V29H6V27H3V28H2Z"
    ),
    ArcadeSprite(
        width = 34, height = 34,
        data = "$TempleHood M2 26H29V28H28V30H27V29H25V28H23V33H22V32H20V31H18V30H17V31H16V33H15V32H13V31H12V30H10V29H8V30H7V31H6V30H5V28H6V27H3V28H2Z"
    ),
    ArcadeSprite(
        width = 34, height = 34,
        data = "$TempleHood M2 26H29V29H28V30H27V29H26V28H24V29H23V30H22V32H21V33H20V32H19V31H17V30H16V31H15V32H14V33H13V32H12V31H11V30H10V29H8V30H6V31H5V29H6V27H3V28H2Z"
    )
)

private val TempleFace = ArcadeSprite(
    width = 34, height = 34,
    data = "M8 14H9V13H10V12H11V11H12V10H23V12H26V21H25V22H24V23H10V22H9V20H8Z M9 25H12V26H9Z"
)

private val TempleRelic = ArcadeSprite(
    width = 12, height = 14,
    data = "M4 0H8V2H10V4H12V8H10V10H8V12H7V14H5V12H4V10H2V8H0V4H2V2H4ZM4 4V8H8V4Z"
)

private val TempleDoor = ArcadeSprite(
    width = 24, height = 28,
    data = "M6 0H18V2H22V5H24V28H18V7H16V5H8V7H6V28H0V5H2V2H6Z " +
            "M8 1V3H16V1Z M1 10V12H5V10Z M19 19V21H23V19Z"
)

private val TempleBatUp = ArcadeSprite(
    width = 32, height = 24,
    data = "M0 0H2V3H5V6H8V8H11V11H13V9H14V6H16V9H18V6H20V11H22V9H25V6H28V3H30V0H32V11H30V15H27V14H25V17H22V16H21V20H18V24H15V21H12V17H10V18H7V15H5V16H2V12H0Z " +
            "M14 12V14H16V12Z M18 12V14H20V12Z"
)

private val TempleBatDown = ArcadeSprite(
    width = 32, height = 24,
    data = "M14 6H16V9H18V6H20V11H22V13H25V15H28V18H30V22H27V21H24V22H22V19H20V21H18V24H15V21H12V18H10V20H7V22H4V20H1V22H2V17H5V14H8V12H11V11H13V9H14Z " +
            "M14 12V14H16V12Z M18 12V14H20V12Z"
)

private val TempleShield = ArcadeSprite(
    width = 14, height = 16,
    data = "M5 0H9V1H14V9H12V12H10V14H8V16H6V14H4V12H2V9H0V1H5ZM3 4V8H4V10H6V12H8V10H10V8H11V4Z"
)

private fun DrawScope.drawTempleBackground(style: ArcadeStyle, width: Float, height: Float) {
    val columns = ceil(width / 141f).toInt()
    val rows = ceil(height / 119f).toInt()
    val ink = style.detailColor.copy(alpha = 0.1f)
    for (row in 0 until rows) for (column in 0 until columns) {
        val point = Offset(column * 141f + 9f + row % 2 * 19f, row * 119f + 8f)
        drawRoundRect(ink.copy(alpha = 0.025f), point, Size(123f, 103f), CornerRadius(3f))
        drawRoundRect(
            ink.copy(alpha = 0.12f),
            point,
            Size(123f, 103f),
            CornerRadius(3f),
            style = Stroke(0.8f)
        )
        for (x in listOf(point.x + 10f, point.x + 108f)) {
            pixelRect(ink.copy(alpha = 0.15f), x, point.y + 11f, 5f, 76f)
            pixelRect(ink, x - 2f, point.y + 9f, 9f, 3f)
            pixelRect(ink, x - 2f, point.y + 87f, 9f, 3f)
        }
        val center = point + Offset(62f, 49f)
        val glyph = Path().apply {
            moveTo(center.x - 19f, center.y + 15f)
            lineTo(center.x, center.y - 19f)
            lineTo(center.x + 19f, center.y + 15f)
            close()
            moveTo(center.x - 10f, center.y + 1f)
            lineTo(center.x + 10f, center.y + 1f)
            moveTo(center.x, center.y - 7f)
            lineTo(center.x, center.y + 21f)
        }
        drawPath(glyph, ink, style = Stroke(1f))
        for (dot in -1..1) pixelRect(
            ink.copy(alpha = 0.14f),
            center.x + dot * 10f - 1f,
            center.y + 31f,
            2f,
            2f
        )
    }
}

@Serializable
private data class TempleTrap(val position: Int, val side: Int)

@Serializable
private data class TempleGuard(val from: Int, val to: Int, val phase: Float)

@Serializable
private class TempleEscapeRun : ArcadeRun() {
    var columns = 13
        private set
    var rows = 33
        private set
    var walls = BooleanArray(columns * rows)
        private set
    var pickups = IntArray(columns * rows)
        private set
    val traps = mutableListOf<TempleTrap>()
    val guards = mutableListOf<TempleGuard>()
    var flood = rows + 0.5f
        private set
    var shield = false
        private set
    var protection = 0f
        private set
    var freeze = 0f
        private set
    private var begun = false
    private var floodTime = 0f
    private var floodSpeed = 0f
    var position = 0
        private set
    var exit = 0
        private set
    var direction = -1
        private set
    var progress = 0f
        private set
    var relics = 0
        private set
    var round = 1
        private set
    var camera = 0f
        private set
    var time = 0f
        private set
    var transition = 0f
        private set
    var impact = 0f
        private set
    var motionX = 0f
        private set
    var motionY = 0f
        private set
    var flutter = 0f
        private set
    var impactHorizontal = true
        private set
    val trail = mutableListOf<Int>()
    var trailFade = 0f
        private set
    private var queued = -1

    @Transient
    val effects = ArcadeEffects()

    val cell get() = width / columns
    val left get() = 0f
    val visibleRows get() = (height - topInset - 12f) / cell
    val playerX
        get() = position % columns + 0.5f + progress * when (direction) {
            0 -> 1f
            2 -> -1f
            else -> 0f
        }
    val playerY
        get() = position / columns + 0.5f + progress * when (direction) {
            1 -> 1f
            3 -> -1f
            else -> 0f
        }
    val playerPoint
        get() = Offset(
            x = left + playerX * cell,
            y = topInset + 6f + (playerY - camera) * cell
        )

    fun point(index: Int) = Offset(
        x = left + (index % columns + 0.5f) * cell,
        y = topInset + 6f + (index / columns + 0.5f - camera) * cell
    )

    fun guardPosition(guard: TempleGuard): Offset {
        val start = Offset(x = guard.from % columns + 0.5f, y = guard.from / columns + 0.5f)
        val end = Offset(x = guard.to % columns + 0.5f, y = guard.to / columns + 0.5f)
        val span = (end - start).getDistance()
        val travel = (time * (2f + minOf(round * 0.12f, 2f)) + guard.phase) % (span * 2f)
        return start + (end - start) * (if (travel < span) travel else span * 2f - travel) / span
    }

    fun guardPoint(guard: TempleGuard): Offset {
        val position = guardPosition(guard = guard)
        return Offset(
            x = left + position.x * cell,
            y = topInset + 6f + (position.y - camera) * cell
        )
    }

    fun next(
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
        return if (x in 0 until columns && y in 0 until rows) y * columns + x else -1
    }

    fun trapActive(index: Int): Boolean =
        freeze <= 0f && time > 2f && (time + index / columns * 0.17f) % 3.6f < 1.2f

    fun trapWarning(index: Int): Boolean = (time + index / columns * 0.17f) % 3.6f > 3.1f

    override fun isValid(): Boolean = super.isValid() && columns in 13..39 && rows in 30..87 &&
            walls.size == columns * rows && pickups.size == walls.size &&
            position in walls.indices && !walls[position] && exit in walls.indices && !walls[exit] &&
            relics in 0..3 && direction in -1..3 && progress in 0f..1f && camera.isFinite() &&
            flood.isFinite() && floodTime.isFinite() && floodTime >= 0f &&
            floodSpeed.isFinite() && floodSpeed >= 0f && protection >= 0f &&
            motionX in -1f..1f && motionY in -1f..1f &&
            flutter >= 0f && flutter < 4f &&
            traps.all { it.position in walls.indices && !walls[it.position] && it.side in 0..3 } &&
            guards.all {
                it.from in walls.indices && it.to in walls.indices && !walls[it.from] && !walls[it.to] &&
                        it.from != it.to &&
                        (it.from / columns == it.to / columns || it.from % columns == it.to % columns)
            }

    override fun reset() {
        round = 1
        columns = ((width / (height - topInset) * 9f).toInt() or 1).coerceIn(13, 39)
        newLevel()
    }

    private fun newLevel() {
        animateLevel()
        generate()
        flood = rows + 0.5f
        begun = false
        floodTime = 0f
        floodSpeed = 0f
        shield = false
        protection = 0f
        freeze = 0f
        camera = (rows - visibleRows).coerceAtLeast(0f)
        direction = -1
        queued = -1
        progress = 0f
        relics = 0
        time = 0f
        transition = 0f
        impact = 0f
        motionX = 0f
        motionY = 0f
        flutter = 0f
        impactHorizontal = true
        trail.clear()
        trailFade = 0f
        effects.clear()
    }

    private fun generate() {
        val count = maxOf(3 + (round - 1) / 3, (visibleRows / 9f).toInt() + 2).coerceAtMost(7)
        val heights = List(count) { Random.nextInt(from = 9, until = 13) }
        rows = heights.sum() + 3
        walls = BooleanArray(columns * rows) { true }
        pickups = IntArray(walls.size)
        traps.clear()
        guards.clear()
        var entry = columns / 2
        var top = rows - 2
        position = top * columns + entry
        val chambers = mutableListOf<Pair<Int, Int>>()
        for (height in heights) {
            val (board, opening) = generateRoom(height = height + 1, entry = entry)
            top -= height
            board.copyInto(destination = walls, destinationOffset = top * columns)
            chambers += top to top + height
            entry = opening
        }
        exit = columns + entry
        val parents = findRoutes(board = walls, origin = position)
        val stops = parents.indices.filter { parents[it] >= 0 }
        val route = BooleanArray(walls.size)
        var at = exit
        while (at != position) {
            val previous = parents[at]
            val step = if (at / columns == previous / columns) {
                if (previous > at) 1 else -1
            } else if (previous > at) columns else -columns
            while (at != previous) {
                route[at] = true
                if (at % columns % 2 == 0 || at / columns % 2 == 0) pickups[at] = 1
                at += step
            }
        }
        val bonuses = stops.filter { !route[it] && it != position }.shuffled().toMutableList()
        if (bonuses.size < 3) bonuses += stops.filter { it != position && it != exit && it !in bonuses }
            .shuffled()
        for (index in bonuses.take(3)) pickups[index] = 2
        val shield = stops.filter { route[it] && it != exit }.maxBy { it / columns }
        pickups[shield] = 3
        if (round >= 3) bonuses.drop(3).firstOrNull { pickups[it] == 0 }?.let { pickups[it] = 4 }
        for ((top, bottom) in chambers.drop(1)) {
            val hazards = stops.filter {
                it / columns in top + 2 until bottom - 1 && it != shield && it != exit && !route[it]
            }
            hazards.randomOrNull()?.let { index ->
                val side = (0..3).firstOrNull { heading ->
                    val adjacent = next(index = index, direction = heading)
                    adjacent >= 0 && walls[adjacent]
                }
                if (side != null) traps += TempleTrap(position = index, side = (side + 1) % 4)
            }
            val patrols = mutableListOf<TempleGuard>()
            for (y in top + 2 until bottom - 1) for (x in 1 until columns - 1) {
                val from = y * columns + x
                if (walls[from] || route[from]) continue
                for (heading in 0..1) {
                    var to = from
                    var crossings = 0
                    for (length in 1..5) {
                        to = next(index = to, direction = heading)
                        if (to < 0 || walls[to] || to / columns >= bottom - 1) break
                        if (route[to]) crossings++
                        if (length >= 3 && crossings in 1..2 && !route[to]) patrols += TempleGuard(
                            from = from, to = to, phase = Random.nextFloat() * length * 2f
                        )
                    }
                }
            }
            patrols.randomOrNull()?.let { guards += it }
        }
        pickups[position] = 0
        pickups[exit] = 0
    }

    private fun generateRoom(height: Int, entry: Int): Pair<BooleanArray, Int> {
        val origin = (height - 1) * columns + entry
        repeat(64) {
            val board = BooleanArray(columns * height) { index ->
                val x = index % columns
                val y = index / columns
                x == 0 || x == columns - 1 || y == 0 || y == height - 1
            }
            board[origin] = false
            repeat(Random.nextInt(from = 3, until = 7)) {
                val kind = Random.nextInt(until = 4)
                val width = Random.nextInt(from = 2, until = maxOf(3, columns / 3) + 1)
                val depth = Random.nextInt(from = 1, until = 4)
                val left = if (kind == 1) {
                    if (Random.nextBoolean()) 1 else columns - width - 1
                } else Random.nextInt(from = 1, until = columns - width)
                val top = Random.nextInt(from = 2, until = height - 3)
                for (y in top until minOf(height - 2, top + depth)) {
                    for (x in left until left + width) {
                        if (kind < 2 || y == top || x == left || kind == 3 && x == left + width - 1) {
                            board[y * columns + x] = true
                        }
                    }
                }
            }
            if (board.count { !it } < (columns - 2) * (height - 2) * 0.55f) return@repeat
            val parents = findRoutes(board = board, origin = origin)
            val distances = IntArray(columns) { Int.MAX_VALUE }
            for (index in parents.indices) {
                val parent = parents[index]
                val x = index % columns
                val y = index / columns
                if (parent < 0 || x !in 2 until columns - 2 || (1 until y).any { board[it * columns + x] }) continue
                var turns = 0
                var at = index
                while (parents[at] != at) {
                    turns++
                    at = parents[at]
                }
                if (parent <= index || parent % columns != x) turns++
                distances[x] = minOf(distances[x], turns)
            }
            val exit = distances.indices.filter { distances[it] in 4..10 }.randomOrNull()
            if (exit != null) {
                board[exit] = false
                return board to exit
            }
        }
        val board = BooleanArray(columns * height) { index ->
            val x = index % columns
            val y = index / columns
            x == 0 || x == columns - 1 || y == 0 || y == height - 1
        }
        val exit = columns / 2
        val right = entry <= exit
        board[origin] = false
        board[exit] = false
        board[(height - 3) * columns + entry] = true
        board[columns + if (right) columns - 2 else 1] = true
        val ledge = if (right) 1 until exit else exit + 1 until columns - 1
        for (x in ledge) board[2 * columns + x] = true
        return board to exit
    }

    private fun findRoutes(board: BooleanArray, origin: Int): IntArray {
        val parents = IntArray(board.size) { -1 }
        val queue = ArrayDeque<Int>()
        parents[origin] = origin
        queue += origin
        while (queue.isNotEmpty()) {
            val start = queue.removeFirst()
            for (heading in 0..3) {
                var at = start
                while (true) {
                    val next = next(index = at, direction = heading)
                    if (next !in board.indices || board[next]) break
                    at = next
                }
                // Keep the guaranteed route above the lava; deeper detours remain optional.
                if (heading == 1 && at / columns - start / columns > 3) continue
                if (parents[at] < 0) {
                    parents[at] = start
                    queue += at
                }
            }
        }
        return parents
    }

    fun swipe(delta: Offset) {
        if (transition > 0f) return
        val heading = if (abs(delta.x) > abs(delta.y)) {
            if (delta.x > 0f) 0 else 2
        } else if (delta.y > 0f) 1 else 3
        if (direction >= 0) queued = heading
        else startSlide(heading = heading)
    }

    private fun startSlide(heading: Int) {
        val next = next(index = position, direction = heading)
        if (next >= 0 && !walls[next]) {
            direction = heading
            begun = true
        }
    }

    override fun stopInput() {
        queued = -1
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds = seconds)
        if (transition > 0f) {
            transition = (transition - seconds).coerceAtLeast(0f)
            if (transition == 0f) {
                round++
                newLevel()
            }
            return
        }
        freeze = (freeze - seconds).coerceAtLeast(0f)
        if (freeze == 0f) time += seconds
        val blend = 1f - exp(-seconds * 24f)
        val targetX = when (direction) {
            0 -> 1f
            2 -> -1f
            else -> 0f
        }
        val targetY = when (direction) {
            1 -> 1f
            3 -> -1f
            else -> 0f
        }
        motionX += (targetX - motionX) * blend
        motionY += (targetY - motionY) * blend
        val motion = (abs(motionX) + abs(motionY)).coerceAtMost(1f)
        flutter = (flutter + seconds * (4f + motion * 8f)) % 4f
        protection = (protection - seconds).coerceAtLeast(0f)
        if (begun && freeze == 0f) {
            floodTime += seconds
            val elapsed = (floodTime - 1f).coerceIn(0f, seconds)
            if (elapsed > 0f) {
                val pace = 1.1f + minOf((round - 1) * 0.025f, 0.35f) +
                        minOf((floodTime - 1f) * 0.01f, 0.3f)
                val chase = ((flood - playerY - 7f) * 0.45f).coerceIn(0f, 2.5f)
                val speed = floodSpeed + (pace + chase - floodSpeed).coerceIn(
                    minimumValue = -elapsed * 1.2f,
                    maximumValue = elapsed * 0.8f
                )
                flood -= (floodSpeed + speed) * elapsed / 2f
                floodSpeed = speed
            }
        }
        impact = (impact - seconds * 5f).coerceAtLeast(0f)
        trailFade = (trailFade - seconds * 3f).coerceAtLeast(0f)
        if (direction >= 0) {
            progress += seconds * 32f
            while (progress >= 1f && direction >= 0) {
                progress -= 1f
                trail += position
                if (trail.size > 6) trail.removeAt(0)
                trailFade = 1f
                position = next(index = position, direction = direction)
                when (pickups[position]) {
                    1 -> score++
                    2 -> {
                        relics++
                        score += 15 + relics * 5
                        if (relics == 3) freeze = maxOf(freeze, 2f)
                        effects.burst(point = playerPoint, count = 10)
                    }

                    4 -> {
                        freeze = 4.5f
                        effects.burst(point = playerPoint, count = 16)
                    }
                    3 -> {
                        shield = true
                        effects.burst(point = playerPoint, count = 8)
                    }
                }
                pickups[position] = 0
                if (position == exit) {
                    score += 50 + round * 10
                    transition = 0.24f
                    progress = 0f
                    direction = -1
                    queued = -1
                    return
                }
                val next = next(index = position, direction = direction)
                if (next < 0 || walls[next]) {
                    impactHorizontal = direction % 2 == 0
                    direction = -1
                    progress = 0f
                    impact = 1f
                    if (queued >= 0) {
                        startSlide(heading = queued)
                        queued = -1
                    }
                }
            }
        }
        if (playerY + 0.35f >= flood) {
            phase = ArcadePhase.Over
            return
        }
        val guard = guards.indexOfFirst {
            val point = guardPosition(guard = it)
            abs(playerX - point.x) < 0.65f && abs(playerY - point.y) < 0.6f
        }
        val hit = guard >= 0 || traps.any { (index) ->
            trapActive(index = index) && abs(playerX - index % columns - 0.5f) < 0.6f &&
                    abs(playerY - index / columns - 0.5f) < 0.6f
        }
        if (hit && protection == 0f) {
            if (shield) {
                shield = false
                protection = 1.2f
                if (guard >= 0) {
                    guards.removeAt(guard)
                    score += 10
                }
                effects.burst(point = playerPoint, count = 12)
            } else {
                phase = ArcadePhase.Over
                return
            }
        }
        val target =
            (playerY - visibleRows * 0.65f).coerceIn(0f, (rows - visibleRows).coerceAtLeast(0f))
        camera += (target - camera) * minOf(1f, seconds * 12f)
    }

    override fun onResize(previous: Size) {
        camera =
            (playerY - visibleRows * 0.65f).coerceIn(0f, (rows - visibleRows).coerceAtLeast(0f))
        effects.clear()
    }
}