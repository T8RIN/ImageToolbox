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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
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
import com.t8rin.imagetoolbox.core.games.utils.arcadeJoystick
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.games.utils.pixelCircle
import com.t8rin.imagetoolbox.core.games.utils.reliefSprite
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bomb
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object BomberGame : ArcadeGameDefinition {
    override val title = R.string.arcade_bomber
    override val description = R.string.arcade_bomber_sub
    override val icon get() = Icons.Outlined.Bomb
    override val order = 20

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::BomberRun
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
                onPress = run::begin,
                onDrag = run::drag,
                onSwipe = run::swipe,
                onTap = { run.placeBomb() },
                onSecondaryTap = { run.placeBomb() },
                onRelease = run::release
            ) {
                drawBomberBackground(style, run.width, run.height)
                val cell = run.cell
                val origin = run.origin
                val bounds =
                    Rect(origin, Size(width = run.columns * cell, height = run.rows * cell))
                val outline = outlinePath.apply {
                    rewind()
                    addRoundRect(RoundRect(bounds, CornerRadius(x = 10f)))
                }
                clipPath(outline) {
                    run.board.forEachIndexed { index, tile ->
                        val point = run.center(index) - Offset(x = cell / 2f, y = cell / 2f)
                        when (tile) {
                            1 -> {
                                pixelRect(
                                    color = style.obstacleColor.copy(alpha = 0.3f),
                                    x = point.x + 1f,
                                    y = point.y + 3f,
                                    width = cell - 2f,
                                    height = cell - 6f
                                )
                                pixelRect(
                                    color = style.obstacleColor.copy(alpha = 0.3f),
                                    x = point.x + 3f,
                                    y = point.y + 1f,
                                    width = cell - 6f,
                                    height = cell - 2f
                                )
                                pixelRect(
                                    color = style.detailColor.copy(alpha = 0.65f),
                                    x = point.x + 5f,
                                    y = point.y + 5f,
                                    width = 4f,
                                    height = 2f
                                )
                                pixelRect(
                                    color = style.detailColor.copy(alpha = 0.4f),
                                    x = point.x + cell - 8f,
                                    y = point.y + cell - 6f,
                                    width = 3f,
                                    height = 2f
                                )
                            }

                            2 -> {
                                pixelRect(
                                    color = style.accentColor.copy(alpha = 0.7f),
                                    x = point.x + 3f,
                                    y = point.y + 2f,
                                    width = cell - 6f,
                                    height = cell - 4f
                                )
                                pixelRect(
                                    color = style.playerDetailColor.copy(alpha = 0.45f),
                                    x = point.x + cell * 0.35f,
                                    y = point.y + 4f,
                                    width = 2f,
                                    height = cell - 8f
                                )
                                pixelRect(
                                    color = style.playerDetailColor.copy(alpha = 0.45f),
                                    x = point.x + cell * 0.65f,
                                    y = point.y + 4f,
                                    width = 2f,
                                    height = cell - 8f
                                )
                                pixelRect(
                                    color = style.obstacleColor,
                                    x = point.x + 2f,
                                    y = point.y + 5f,
                                    width = cell - 4f,
                                    height = 3f
                                )
                                pixelRect(
                                    color = style.obstacleColor,
                                    x = point.x + 2f,
                                    y = point.y + cell - 8f,
                                    width = cell - 4f,
                                    height = 3f
                                )
                            }

                            else -> if (index % 3 == 0) {
                                pixelRect(
                                    color = style.detailColor.copy(alpha = 0.35f),
                                    x = point.x + cell * 0.3f,
                                    y = point.y + cell * 0.7f,
                                    width = 2f,
                                    height = 1f
                                )
                                pixelRect(
                                    color = style.detailColor.copy(alpha = 0.2f),
                                    x = point.x + cell * 0.3f + 3f,
                                    y = point.y + cell * 0.7f + 2f,
                                    width = 1f,
                                    height = 1f
                                )
                            }
                        }
                    }
                    for ((index, kind) in run.items) {
                        val p = run.center(index)
                        pixelCircle(
                            color = style.accentColor.copy(alpha = 0.18f),
                            radius = cell * 0.32f,
                            center = p
                        )
                        if (kind == 2) {
                            val unit = cell * 0.6f / 18f
                            translate(left = p.x - unit * 8f, top = p.y - unit * 9f) {
                                scale(scale = unit, pivot = Offset.Zero) {
                                    drawArcadeSprite(BomberShield, style.accentColor, 0f, 0f)
                                }
                            }
                            continue
                        }
                        pixelRect(
                            color = style.accentColor,
                            x = p.x - cell * 0.2f,
                            y = p.y - 1.5f,
                            width = cell * 0.4f,
                            height = 3f
                        )
                        if (kind == 0) {
                            pixelRect(
                                color = style.accentColor,
                                x = p.x - 1.5f,
                                y = p.y - cell * 0.2f,
                                width = 3f,
                                height = cell * 0.4f
                            )
                        } else {
                            pixelCircle(
                                color = style.accentColor,
                                radius = cell * 0.18f,
                                center = p,
                                style = Stroke(width = 2f)
                            )
                            pixelRect(
                                color = style.accentColor,
                                x = p.x + cell * 0.1f,
                                y = p.y - cell * 0.28f,
                                width = 3f,
                                height = 4f
                            )
                        }
                    }
                    for ((position, fuse) in run.bombs) {
                        val p = run.center(position)
                        val pulse =
                            if (fuse < 0.8f) 1f + sin(fuse * 32f) * 0.08f else 1f
                        val unit = cell * 0.62f * pulse / 24f
                        translate(left = p.x - 12f * unit, top = p.y - 15f * unit) {
                            scale(scale = unit, pivot = Offset.Zero) {
                                drawArcadeSprite(
                                    sprite = BomberBombBody,
                                    tint = style.obstacleColor,
                                    x = 0f,
                                    y = 0f
                                )
                                drawArcadeSprite(
                                    sprite = BomberBombShine,
                                    tint = style.playerDetailColor,
                                    x = 0f,
                                    y = 0f
                                )
                                pixelRect(
                                    color = style.accentColor,
                                    x = 12f,
                                    y = 2f,
                                    width = 2f,
                                    height = 4f
                                )
                                pixelRect(
                                    color = style.accentColor,
                                    x = 14f,
                                    y = 1f,
                                    width = 4f,
                                    height = 2f
                                )
                                pixelRect(
                                    color = style.hazardColor,
                                    x = 18f,
                                    y = -2f,
                                    width = 2f,
                                    height = 6f
                                )
                                pixelRect(
                                    color = style.hazardColor,
                                    x = 16f,
                                    y = 0f,
                                    width = 6f,
                                    height = 2f
                                )
                            }
                        }
                        drawArc(
                            color = style.accentColor,
                            startAngle = -90f,
                            sweepAngle = fuse / 2.2f * 360f,
                            useCenter = false,
                            topLeft = p - Offset(x = cell * 0.38f, y = cell * 0.38f),
                            size = Size(width = cell * 0.76f, height = cell * 0.76f),
                            style = Stroke(width = 1f)
                        )
                    }
                    for ((index, life) in run.blasts) {
                        val p = run.center(index)
                        val r = cell * (0.3f + life * 0.5f)
                        pixelRect(
                            color = style.accentColor.copy(alpha = 0.35f),
                            x = p.x - cell / 2f,
                            y = p.y - cell / 2f,
                            width = cell,
                            height = cell
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = p.x - r,
                            y = p.y - r * 0.45f,
                            width = r * 2f,
                            height = r * 0.9f
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = p.x - r * 0.45f,
                            y = p.y - r,
                            width = r * 0.9f,
                            height = r * 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = p.x - 2f,
                            y = p.y - 2f,
                            width = 4f,
                            height = 4f
                        )
                    }
                    for (enemy in run.enemies) {
                        val point = run.enemyPoint(enemy)
                        val unit = cell / 17f
                        val enemyColor = when (enemy.kind) {
                            1 -> style.hazardColor
                            2 -> style.accentColor
                            else -> style.obstacleColor
                        }
                        translate(left = point.x - unit * 5f, top = point.y - unit * 5.5f) {
                            scale(scale = unit, pivot = Offset.Zero) {
                                reliefSprite(
                                    sprite = BomberBug,
                                    color = enemyColor,
                                    shade = lerp(
                                        enemyColor,
                                        style.overlayColor.copy(alpha = 1f),
                                        0.25f
                                    ),
                                    light = lerp(enemyColor, style.textColor, 0.2f),
                                    x = 0f,
                                    y = 0f
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = 2f,
                                    y = 4f,
                                    width = 2f,
                                    height = 3f
                                )
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = 6f,
                                    y = 4f,
                                    width = 2f,
                                    height = 3f
                                )
                                val step = if ((run.time * 7f).toInt() % 2 == 0) 0f else 1f
                                pixelRect(
                                    color = enemyColor,
                                    x = step,
                                    y = 9f,
                                    width = 2f,
                                    height = 2f
                                )
                                pixelRect(
                                    color = enemyColor,
                                    x = 8f - step,
                                    y = 9f,
                                    width = 2f,
                                    height = 2f
                                )
                            }
                        }
                    }
                    if (run.fireproof > 0f && (run.fireproof > 3f || (run.fireproof * 6f).toInt() % 2 == 0)) {
                        pixelCircle(
                            color = style.accentColor.copy(alpha = 0.14f),
                            radius = cell * 0.48f,
                            center = run.playerPoint
                        )
                        pixelCircle(
                            color = style.accentColor.copy(alpha = 0.75f),
                            radius = cell * 0.48f,
                            center = run.playerPoint,
                            style = Stroke(width = 1.5f)
                        )
                    }
                    if (run.invulnerable <= 0f || (run.invulnerable * 9f).toInt() % 2 == 0) {
                        val point = run.playerPoint
                        val unit = cell / 31f
                        translate(left = point.x - unit * 10f, top = point.y - unit * 13f) {
                            scale(scale = unit, pivot = Offset.Zero) {
                                drawArcadeSprite(
                                    sprite = BomberBody,
                                    tint = style.playerColor,
                                    x = 0f,
                                    y = 0f
                                )
                                drawArcadeSprite(
                                    sprite = BomberVisor,
                                    tint = style.playerDetailColor,
                                    x = 0f,
                                    y = 0f
                                )
                                pixelRect(
                                    color = style.accentColor,
                                    x = 8f,
                                    y = 0f,
                                    width = 4f,
                                    height = 3f
                                )
                                for (eye in listOf(6f, 12f)) {
                                    pixelRect(
                                        color = style.playerColor,
                                        x = eye,
                                        y = 7f,
                                        width = 2f,
                                        height = 4f
                                    )
                                }
                                pixelRect(
                                    color = style.obstacleColor,
                                    x = 6f,
                                    y = 19f,
                                    width = 8f,
                                    height = 2f
                                )
                                for (hand in listOf(1f, 15f)) {
                                    pixelRect(
                                        color = style.accentColor,
                                        x = hand,
                                        y = 16f,
                                        width = 4f,
                                        height = 4f
                                    )
                                }
                                val step =
                                    sin(run.progress * Math.PI.toFloat()) * (if (run.player % 2 == 0) 1f else -1f)
                                pixelRect(
                                    color = style.accentColor,
                                    x = 3f,
                                    y = 23f + step,
                                    width = 6f,
                                    height = 3f
                                )
                                pixelRect(
                                    color = style.accentColor,
                                    x = 11f,
                                    y = 23f - step,
                                    width = 6f,
                                    height = 3f
                                )
                            }
                        }
                    }
                }
                arcadeJoystick(style, run.touchOrigin, run.stick)
                drawRoundRect(
                    color = style.detailColor,
                    topLeft = bounds.topLeft,
                    size = bounds.size,
                    cornerRadius = CornerRadius(x = 10f),
                    style = Stroke(width = 1f)
                )
                arcadeLives(
                    style = style,
                    remaining = run.lives,
                    y = run.topInset / 2f
                )
                repeat(run.capacity) { index ->
                    val p = Offset(x = 83f + index * 13f, y = run.topInset / 2f)
                    val color =
                        style.accentColor.copy(alpha = if (index < run.capacity - run.bombs.size) 1f else 0.2f)
                    pixelCircle(
                        color = color,
                        radius = 4f,
                        center = p
                    )
                    pixelRect(
                        color = color,
                        x = p.x + 1f,
                        y = p.y - 7f,
                        width = 2f,
                        height = 3f
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

private val BomberBody = ArcadeSprite(
    width = 20,
    height = 26,
    data = "M5 2H15V3H18V5H20V12H18V14H14V16H17V19H15V22H13V24H11V22H9V24H7V22H5V19H3V16H6V14H2V12H0V5H2V3H5Z"
)
private val BomberVisor = ArcadeSprite(
    width = 20,
    height = 26,
    data = "M5 5H15V6H17V11H15V12H5V11H3V6H5Z"
)
private val BomberBombBody = ArcadeSprite(
    width = 24,
    height = 28,
    data = "M9 4H15V6H19V8H22V11H24V20H22V23H19V25H15V26H8V25H4V23H2V20H0V12H2V9H5V7H9Z"
)
private val BomberBombShine = ArcadeSprite(
    width = 24,
    height = 28,
    data = "M6 10H11V12H8V14H6V17H4V13H6Z"
)

private val BomberBug = ArcadeSprite(
    width = 10,
    height = 11,
    data = "M1 0H3V2H7V0H9V3H10V8H8V10H6V9H4V10H2V8H0V3H1Z"
)

private fun DrawScope.drawBomberBackground(style: ArcadeStyle, width: Float, height: Float) {
    val color = style.detailColor.copy(alpha = 0.07f)
    var y = 0f
    var row = 0
    while (y < height) {
        drawLine(color, Offset(0f, y), Offset(width, y), 0.7f)
        var x = if (row++ % 2 == 0) 0f else -18f
        while (x < width) {
            drawLine(color, Offset(x, y), Offset(x, y + 16f), 0.7f)
            x += 36f
        }
        y += 16f
    }
}

@Serializable
private data class BomberBomb(
    val position: Int,
    var fuse: Float,
    val range: Int
)

@Serializable
private data class BomberEnemy(
    var position: Int,
    var previous: Int,
    var progress: Float = 1f,
    val kind: Int = 0
)

private val BomberShield = ArcadeSprite(
    width = 16,
    height = 18,
    data = "M1 1H15V9H13V13H11V15H9V17H7V15H5V13H3V9H1Z M3 3V8H5V12H7V14H9V12H11V8H13V3Z"
)

@Serializable
private class BomberRun : ArcadeRun() {
    val columns = 11
    var rows = 9
    var board = IntArray(columns * rows)
    val bombs = mutableListOf<BomberBomb>()
    val blasts = mutableMapOf<Int, Float>()
    val items = mutableMapOf<Int, Int>()
    val enemies = mutableListOf<BomberEnemy>()

    @Transient
    val effects = ArcadeEffects()
    val cell get() = minOf((width - 48f) / columns, (height - topInset - 32f) / rows)
    val origin
        get() = Offset(
            x = (width - columns * cell) / 2f,
            y = topInset + (height - topInset - rows * cell) / 2f
        )
    var player = columns + 1
    var previous = player
    var progress = 1f
    var lives = 3
    var round = 1
    var range = 2
    var capacity = 1
    var invulnerable = 0f
    var fireproof = 0f
    var time = 0f
    var transition = 0f
    private var broken = 0

    @Transient
    private var direction = -1

    @Transient
    private var queuedDirection = -1

    @Transient
    private var gestureStepped = false

    @Transient
    private var moving = false

    @Transient
    var touchOrigin: Offset? = null
        private set

    @Transient
    var stick = Offset.Zero
        private set
    val playerPoint get() = center(previous) + (center(player) - center(previous)) * progress
    fun enemyPoint(enemy: BomberEnemy) =
        center(enemy.previous) + (center(enemy.position) - center(enemy.previous)) * enemy.progress

    fun center(index: Int) =
        origin + Offset(x = (index % columns + 0.5f) * cell, y = (index / columns + 0.5f) * cell)

    override fun isValid(): Boolean = super.isValid() &&
            rows in 7..15 &&
            board.size == columns * rows &&
            player in board.indices &&
            previous in board.indices &&
            enemies.all { it.position in board.indices && it.previous in board.indices }

    override fun reset() {
        round = 1
        lives = 3
        range = 2
        capacity = 1
        fireproof = 0f
        time = 0f
        broken = 0
        effects.clear()
        newRound()
    }

    private fun newRound() {
        animateLevel()
        rows = (((height - topInset - 32f) / ((width - 48f) / columns)).toInt()
            .coerceIn(7, 15) / 2) * 2 + 1
        val starts = listOf(columns * 2 - 2, (rows - 2) * columns + 1, (rows - 1) * columns - 2)
        board = IntArray(columns * rows) {
            val x = it % columns
            val y = it / columns
            when {
                x == 0 || y == 0 || x == columns - 1 || y == rows - 1 || x % 2 == 0 && y % 2 == 0 -> 1
                abs(x - 1) + abs(y - 1) <= 2 || x == 3 && y == 2 || x == 2 && y == 3 -> 0
                starts.any { start -> abs(start % columns - x) + abs(start / columns - y) <= 1 } -> 0
                Random.nextFloat() < (0.38f + round * 0.012f).coerceAtMost(0.55f) -> 2
                else -> 0
            }
        }
        bombs.clear()
        blasts.clear()
        items.clear()
        enemies.clear()
        starts.shuffled().take((1 + round / 2).coerceAtMost(3))
            .forEachIndexed { index, position ->
                enemies += BomberEnemy(
                    position = position,
                    previous = position,
                    kind = if (round < 3) 0 else (round + index) % 3
                )
            }
        player = columns + 1
        previous = player
        progress = 1f
        invulnerable = 1.5f
        transition = 0f
        stopInput()
    }

    fun next(
        index: Int,
        direction: Int
    ): Int {
        val x = index % columns + if (direction == 0) 1 else if (direction == 2) -1 else 0
        val y = index / columns + if (direction == 1) 1 else if (direction == 3) -1 else 0
        return if (x in 0 until columns && y in 0 until rows) y * columns + x else -1
    }

    private fun open(
        index: Int,
        allowBomb: Boolean = false
    ) = index in board.indices && board[index] == 0 &&
            (allowBomb || bombs.none { it.position == index })

    fun begin(point: Offset) {
        touchOrigin = point
        stick = Offset.Zero
        gestureStepped = false
        moving = false
    }

    private fun heading(delta: Offset): Int = if (abs(delta.x) > abs(delta.y)) {
        if (delta.x > 0f) 0 else 2
    } else if (delta.y > 0f) 1 else 3

    fun drag(point: Offset) {
        var origin = touchOrigin ?: return
        var delta = point - origin
        if (delta.getDistance() > 32f) {
            delta *= 32f / delta.getDistance()
            origin = point - delta
            touchOrigin = origin
        }
        stick = delta
        moving = delta.getDistance() >= 8f
        if (moving && (direction < 0 || abs(abs(delta.x) - abs(delta.y)) > 3f)) direction =
            heading(delta)
    }

    fun steer(delta: Offset) {
        if (transition <= 0f) queuedDirection = heading(delta)
    }

    fun swipe(delta: Offset) {
        if (!gestureStepped) steer(delta)
    }

    fun release() {
        moving = false
        direction = -1
        touchOrigin = null
        stick = Offset.Zero
    }

    override fun stopInput() {
        release()
        queuedDirection = -1
        gestureStepped = false
    }

    fun placeBomb() {
        if (transition > 0f || bombs.size >= capacity) return
        val index = if (progress < 0.5f) previous else player
        if (bombs.none { it.position == index }) bombs += BomberBomb(
            position = index,
            fuse = 2.2f,
            range = range
        )
    }

    fun blastCells(
        position: Int,
        range: Int
    ): List<Int> {
        val result = mutableListOf(position)
        repeat(4) { direction ->
            var cell = position
            for (step in 1..range) {
                cell = next(cell, direction)
                if (cell < 0 || board[cell] == 1) break
                result += cell
                if (board[cell] == 2) break
            }
        }
        return result
    }

    fun explode(bomb: BomberBomb) {
        val queue = ArrayDeque<BomberBomb>()
        queue += bomb
        while (queue.isNotEmpty()) {
            val nextBomb = queue.removeFirst()
            if (!bombs.remove(nextBomb)) continue
            val cells = blastCells(nextBomb.position, nextBomb.range)
            for (index in cells) {
                blasts[index] = 0.38f
                items.remove(index)
                if (board[index] == 2) {
                    board[index] = 0
                    broken++
                    score += 10
                    if (broken % 4 == 0) items[index] = (broken / 4 - 1) % 3
                }
                bombs.firstOrNull { it.position == index }?.let(queue::addLast)
            }
            effects.burst(
                point = center(nextBomb.position),
                count = 8
            )
        }
    }

    private fun hurt() {
        lives--
        effects.burst(
            point = playerPoint,
            count = 18
        )
        if (lives == 0) phase = ArcadePhase.Over
        else {
            player = columns + 1
            previous = player
            progress = 1f
            invulnerable = 2f
            stopInput()
        }
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        if (transition > 0f) {
            transition -= seconds
            if (transition <= 0f) {
                round++
                newRound()
            }
            return
        }
        invulnerable = (invulnerable - seconds).coerceAtLeast(0f)
        fireproof = (fireproof - seconds).coerceAtLeast(0f)
        val iterator = blasts.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            entry.setValue(entry.value - seconds)
            if (entry.value <= 0f) iterator.remove()
        }
        bombs.forEach { it.fuse -= seconds }
        bombs.filter { it.fuse <= 0f }.forEach(::explode)
        progress = (progress + seconds / 0.14f).coerceAtMost(1f)
        val heading = if (moving) direction else queuedDirection
        if (progress >= 1f && heading >= 0) {
            val destination = next(player, heading)
            queuedDirection = -1
            if (open(index = destination, allowBomb = true)) {
                previous = player
                player = destination
                progress = 0f
                gestureStepped = true
            }
        }
        val index = if (progress < 0.5f) previous else player
        if (index !in blasts) items.remove(index)?.let {
            when (it) {
                0 -> if (range < 4) range++ else score += 25
                1 -> if (capacity < 3) capacity++ else score += 25
                2 -> fireproof = 20f
            }
            effects.burst(
                point = playerPoint,
                count = 10
            )
        }
        val danger =
            bombs.filter { it.fuse < 1f }.flatMap { blastCells(it.position, it.range) }.toSet()
        val enemyIterator = enemies.iterator()
        while (enemyIterator.hasNext()) {
            val enemy = enemyIterator.next()
            val pace = when (enemy.kind) {
                1 -> 0.95f
                2 -> 1.08f
                else -> 1f
            }
            val interval = (0.39f - 0.15f * (round - 1) / (round + 9f)) * pace
            enemy.progress = (enemy.progress + seconds / interval).coerceAtMost(1f)
            val enemyIndex = if (enemy.progress < 0.5f) enemy.previous else enemy.position
            if (enemyIndex in blasts) {
                score += 50
                effects.burst(
                    point = enemyPoint(enemy),
                    count = 12
                )
                enemyIterator.remove()
                continue
            }
            if (enemy.progress >= 1f && time > 1.5f) {
                val options =
                    (0..3).map { next(enemy.position, it) }.filter { open(it) && it !in blasts }
                val safe = options.filter { it !in danger }.ifEmpty { options }
                val onward = safe.filter { it != enemy.previous }.ifEmpty { safe }
                val ahead = (player + (player - previous) * 2).coerceIn(board.indices)
                val target = if (enemy.kind == 1) ahead else player
                val destination = when {
                    enemy.kind == 2 -> onward.firstOrNull { it - enemy.position == enemy.position - enemy.previous }
                        ?: onward.randomOrNull()

                    Random.nextFloat() < if (enemy.kind == 1) 0.85f else 0.35f -> onward.minByOrNull {
                        abs(it % columns - target % columns) + abs(it / columns - target / columns)
                    }

                    else -> onward.randomOrNull()
                }
                enemy.previous = enemy.position
                enemy.position = destination ?: enemy.position
                enemy.progress = 0f
            }
        }
        if (invulnerable <= 0f &&
            (fireproof <= 0f && index in blasts || enemies.any { (enemyPoint(it) - playerPoint).getDistance() < cell * 0.55f })
        ) hurt()
        if (phase == ArcadePhase.Running && enemies.isEmpty()) {
            score += round * 100
            transition = 1f
            stopInput()
        }
    }

}