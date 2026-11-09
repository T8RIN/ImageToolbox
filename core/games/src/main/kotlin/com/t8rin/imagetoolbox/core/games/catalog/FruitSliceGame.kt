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

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.arcadeLives
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.random.Random

private val SliceApple = ArcadeSprite(
    "SliceApple", 36, 36,
    "M6 3H13V5H23V3H30V6H34V12H36V24H33V31H28V35H21V33H15V35H8V31H3V24H0V12H2V6H6Z"
)
private val SliceOrange = ArcadeSprite(
    "SliceOrange", 36, 36,
    "M11 0H25V3H31V7H34V12H36V24H33V30H29V34H23V36H12V34H6V30H2V24H0V12H3V7H7V3H11Z"
)
private val SlicePear = ArcadeSprite(
    "SlicePear", 36, 36,
    "M14 0H22V4H24V11H28V16H32V22H35V30H31V34H25V36H11V34H5V30H1V22H4V16H8V11H12V4H14Z"
)
private val SliceMelon = ArcadeSprite(
    "SliceMelon", 36, 36,
    "M10 2H26V5H31V9H34V15H36V24H33V30H27V34H9V30H3V24H0V15H2V9H5V5H10Z"
)
private val SliceBerry = ArcadeSprite(
    "SliceBerry", 36, 36,
    "M6 3H30V6H34V14H31V21H27V27H22V32H18V36H14V32H9V26H5V20H2V13H2V7H6Z"
)
private val SliceBomb = ArcadeSprite(
    "SliceBomb", 36, 36,
    "M15 0H24V5H29V8H33V13H36V26H32V32H26V36H11V33H5V28H1V15H4V9H9V6H15Z"
)
private val SlicedFruit = ArcadeSprite(
    "SlicedFruit", 34, 18,
    "M0 0H34V7H31V12H26V16H21V18H13V16H7V12H3V7H0Z"
)

@Composable
fun FruitSliceGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::FruitSliceRun
    )
    ArcadeBoard(
        game = ArcadeGame.FruitSlice,
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
            onDrag = run::slice,
            onRelease = run::stopInput
        ) {
            val plank = 58f
            repeat((run.width / plank).toInt() + 1) { index ->
                val x = index * plank
                pixelRect(
                    color = style.obstacleColor.copy(alpha = if (index % 2 == 0) 0.05f else 0.02f),
                    x = x,
                    y = 0f,
                    width = plank - 2f,
                    height = run.height
                )
                pixelRect(
                    color = style.detailColor.copy(alpha = 0.18f),
                    x = x,
                    y = 0f,
                    width = 1f,
                    height = run.height
                )
                repeat(5) { grain ->
                    val y = (index * 71f + grain * 103f + 39f) % run.height
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.22f),
                        x = x + 10f + grain % 3 * 12f,
                        y = y,
                        width = 1f,
                        height = 17f + grain * 3f
                    )
                    pixelRect(
                        color = style.detailColor.copy(alpha = 0.14f),
                        x = x + 13f + grain % 3 * 12f,
                        y = y + 8f,
                        width = 1f,
                        height = 12f
                    )
                }
            }
            for ((x1, y1, _, _, bomb, kind, angle) in run.fruits) {
                rotate(angle, Offset(x1, y1)) {
                    val x = x1 - 18f
                    val y = y1 - 18f
                    if (bomb) {
                        drawArcadeSprite(
                            sprite = SliceBomb,
                            tint = style.textColor,
                            x = x,
                            y = y
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = x + 23f,
                            y = y - 4f,
                            width = 3f,
                            height = 5f
                        )
                        pixelRect(
                            color = style.accentColor,
                            x = x + 28f,
                            y = y - 7f,
                            width = 3f,
                            height = 3f
                        )
                        drawLine(
                            color = style.playerDetailColor,
                            start = Offset(x + 13f, y + 13f),
                            end = Offset(x + 23f, y + 23f),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = style.playerDetailColor,
                            start = Offset(x + 23f, y + 13f),
                            end = Offset(x + 13f, y + 23f),
                            strokeWidth = 3f
                        )
                    } else {
                        val color = fruitColor(
                            style = style,
                            kind = kind
                        )
                        val sprite = when (kind) {
                            0 -> SliceApple
                            2 -> SlicePear
                            3 -> SliceMelon
                            4 -> SliceBerry
                            else -> SliceOrange
                        }
                        drawArcadeSprite(
                            sprite = sprite,
                            tint = color,
                            x = x,
                            y = y
                        )
                        if (kind == 4) {
                            repeat(6) { seed ->
                                pixelRect(
                                    color = style.playerDetailColor,
                                    x = x + 9f + seed % 3 * 7f,
                                    y = y + 11f + seed / 3 * 8f,
                                    width = 2f,
                                    height = 3f
                                )
                            }
                            pixelRect(
                                color = style.obstacleColor,
                                x = x + 6f,
                                y = y + 1f,
                                width = 24f,
                                height = 3f
                            )
                            pixelRect(
                                color = style.obstacleColor,
                                x = x + 13f,
                                y = y,
                                width = 4f,
                                height = 7f
                            )
                        } else if (kind == 3) {
                            repeat(3) { stripe ->
                                pixelRect(
                                    color = style.playerDetailColor.copy(alpha = 0.45f),
                                    x = x + 8f + stripe * 8f,
                                    y = y + 7f,
                                    width = 3f,
                                    height = 22f
                                )
                            }
                        } else {
                            pixelRect(
                                color = style.playerDetailColor.copy(alpha = 0.7f),
                                x = x + 6f,
                                y = y + 10f,
                                width = 3f,
                                height = 8f
                            )
                            if (kind == 1) repeat(5) { dot ->
                                pixelRect(
                                    color = style.playerDetailColor.copy(alpha = 0.35f),
                                    x = x + 14f + dot % 3 * 5f,
                                    y = y + 17f + dot / 3 * 7f,
                                    width = 2f,
                                    height = 2f
                                )
                            }
                        }
                        pixelRect(
                            color = style.obstacleColor,
                            x = x + 17f,
                            y = y - 3f,
                            width = 3f,
                            height = 7f
                        )
                        pixelRect(
                            color = style.obstacleColor,
                            x = x + 20f,
                            y = y - 4f,
                            width = 8f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.25f),
                            x = x + 21f,
                            y = y - 3f,
                            width = 5f,
                            height = 1f
                        )
                    }
                }
            }
            for ((x, y, vx, _, life, angle, kind) in run.pieces) {
                val color =
                    fruitColor(
                        style = style,
                        kind = kind
                    ).copy(alpha = (life / 0.8f).coerceIn(0f, 1f))
                rotate(angle + (0.8f - life) * vx, Offset(x, y)) {
                    drawArcadeSprite(
                        sprite = SlicedFruit,
                        tint = color,
                        x = x - 17f,
                        y = y - 9f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = life),
                        x = x - 6f,
                        y = y - 4f,
                        width = 3f,
                        height = 3f
                    )
                    pixelRect(
                        color = style.playerDetailColor.copy(alpha = life),
                        x = x + 4f,
                        y = y - 4f,
                        width = 3f,
                        height = 3f
                    )
                }
            }
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
            for ((start, end, life) in run.trail) {
                drawLine(
                    color = style.playerColor.copy(alpha = life * 0.15f),
                    start = start,
                    end = end,
                    strokeWidth = 9f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = style.playerColor.copy(alpha = life),
                    start = start,
                    end = end,
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
            }
            arcadeLives(
                style = style,
                remaining = 3 - run.misses,
                y = run.topInset / 2f
            )
        }
    }
}


private fun fruitColor(
    style: ArcadeStyle,
    kind: Int
) = when (kind) {
    0 -> style.playerColor
    1 -> style.accentColor
    2 -> lerp(style.playerColor, style.accentColor, 0.5f)
    3 -> style.obstacleColor
    else -> lerp(style.accentColor, style.textColor, 0.35f)
}

internal data class SliceFruit(
    var x: Float, var y: Float, var vx: Float, var vy: Float, val bomb: Boolean,
    val kind: Int, var angle: Float = 0f, val spin: Float = 0f
)

internal data class SlicePiece(
    var x: Float,
    var y: Float,
    val vx: Float,
    var vy: Float,
    var life: Float,
    val angle: Float,
    val kind: Int
)

internal data class SliceTrace(
    val start: Offset,
    val end: Offset,
    var life: Float = 1f
)

internal class FruitSliceRun : ArcadeRun() {
    val fruits = mutableListOf<SliceFruit>()
    val pieces = mutableListOf<SlicePiece>()
    val trail = mutableListOf<SliceTrace>()
    val effects = ArcadeEffects()
    var misses = 0
    var combo = 0
    private var finger: Offset? = null
    private var comboTime = 0f
    private var timer = 0.6f
    private var elapsed = 0f
    private val gravity get() = (height - topInset) * 1.65f

    override fun reset() {
        fruits.clear()
        pieces.clear()
        trail.clear()
        effects.clear()
        misses = 0
        timer = 0.6f
        elapsed = 0f
        comboTime = 0f
        stopInput()
    }

    fun begin(point: Offset) {
        finger = point
        combo = 0
    }

    fun slice(point: Offset) {
        val start = finger ?: return
        val delta = point - start
        val length = delta.x * delta.x + delta.y * delta.y
        if (length < 1f) return
        if (trail.size >= 18) trail.removeAt(0)
        trail += SliceTrace(start, point)
        finger = point
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            val t =
                (((fruit.x - start.x) * delta.x + (fruit.y - start.y) * delta.y) / length).coerceIn(
                    0f,
                    1f
                )
            if (hypot(fruit.x - start.x - t * delta.x, fruit.y - start.y - t * delta.y) < 19f) {
                if (fruit.bomb) {
                    phase = ArcadePhase.Over
                    return
                }
                if (comboTime <= 0f) combo = 0
                combo++
                comboTime = 0.3f
                score += minOf(combo, 5)
                effects.burst(
                    point = Offset(fruit.x, fruit.y),
                    count = 6
                )
                for (sign in -1..1 step 2) {
                    pieces += SlicePiece(
                        fruit.x, fruit.y, sign * 65f, -65f, 0.8f,
                        if (sign < 0) 90f else 270f, fruit.kind
                    )
                }
                iterator.remove()
            }
        }
    }

    override fun stopInput() {
        finger = null
        combo = 0
    }

    override fun advance(seconds: Float) {
        elapsed += seconds
        timer -= seconds
        comboTime -= seconds
        if (comboTime <= 0f) combo = 0
        effects.advance(seconds)
        for (trace in trail) trace.life -= seconds * 5f
        trail.removeAll { it.life <= 0f }
        if (timer <= 0f) {
            val count = Random.nextInt(1, if (elapsed < 12f) 4 else 5)
            val bomb = if (elapsed > 5f && Random.nextFloat() < 0.4f) Random.nextInt(count) else -1
            repeat(count) { index ->
                val x = 24f + Random.nextFloat() * (width - 48f)
                val landing = 24f + Random.nextFloat() * (width - 48f)
                val rise = (height - topInset) * (0.55f + Random.nextFloat() * 0.4f)
                val velocity = sqrt(2f * gravity * rise)
                val airtime = 2f * velocity / gravity
                fruits += SliceFruit(
                    x, height + 20f, (landing - x) / airtime,
                    -velocity, index == bomb, Random.nextInt(5),
                    Random.nextFloat() * 360f, (Random.nextFloat() - 0.5f) * 180f
                )
            }
            timer += maxOf(0.9f, 1.7f - elapsed * 0.005f) * (0.8f + Random.nextFloat() * 0.4f)
        }
        val iterator = fruits.iterator()
        while (iterator.hasNext()) {
            val fruit = iterator.next()
            fruit.angle = arcadeAngle(fruit.angle + fruit.spin * seconds)
            fruit.vy += gravity * seconds
            fruit.y += fruit.vy * seconds
            fruit.x += fruit.vx * seconds
            if (fruit.y > height + 40f && fruit.vy > 0f) {
                if (!fruit.bomb) misses++
                iterator.remove()
            }
        }
        for (piece in pieces) {
            piece.life -= seconds
            piece.vy += gravity * seconds
            piece.x += piece.vx * seconds
            piece.y += piece.vy * seconds
        }
        pieces.removeAll { it.life <= 0f }
        if (misses >= 3) phase = ArcadePhase.Over
    }

    override fun onResize(previous: Size) {
        for (fruit in fruits) {
            fruit.x *= width / previous.width
            fruit.vx *= width / previous.width
            fruit.y *= height / previous.height
            fruit.vy *= sqrt(height / previous.height)
        }
        pieces.clear()
        trail.clear()
        effects.clear()
        stopInput()
    }

    override fun save(state: Bundle) {
        state.putFloatArray("timing", floatArrayOf(timer, elapsed, misses.toFloat()))
        state.putFloatArray(
            "fruit",
            fruits.flatMap {
                listOf(
                    it.x,
                    it.y,
                    it.vx,
                    it.vy,
                    if (it.bomb) 1f else 0f,
                    it.kind.toFloat(),
                    it.angle,
                    it.spin
                )
            }.toFloatArray()
        )
        state.putFloatArray(
            "pieces",
            pieces.flatMap {
                listOf(
                    it.x,
                    it.y,
                    it.vx,
                    it.vy,
                    it.life,
                    it.angle,
                    it.kind.toFloat()
                )
            }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("timing")?.let {
            timer = it[0]
            elapsed = it[1]
            misses = it[2].toInt()
        }
        fruits.clear()
        pieces.clear()
        state.getFloatArray("fruit")?.let {
            for (i in it.indices step 8) fruits += SliceFruit(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4] == 1f,
                it[i + 5].toInt(),
                it[i + 6],
                it[i + 7]
            )
        }
        state.getFloatArray("pieces")?.let {
            for (i in it.indices step 7) pieces += SlicePiece(
                it[i],
                it[i + 1],
                it[i + 2],
                it[i + 3],
                it[i + 4],
                it[i + 5],
                it[i + 6].toInt()
            )
        }
        stopInput()
    }
}