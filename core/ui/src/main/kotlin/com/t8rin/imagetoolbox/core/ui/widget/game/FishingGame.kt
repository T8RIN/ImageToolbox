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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val ArcadeFish = ArcadeSprite(
    "ArcadeFish", 40, 22,
    "M0 4H4V6H8V9H11V6H15V3H20V0H26V3H31V5H35V8H38V10H40V14H37V17H32V19H26V22H20V19H15V16H11V13H8V16H4V18H0Z"
)
private val FishingCap = ArcadeSprite(
    "FishingCap", 18, 10,
    "M3 0H13V2H15V6H18V9H0V6H2V2H3Z"
)
private val FishingCoat = ArcadeSprite(
    "FishingCoat", 18, 17,
    "M4 0H11V3H14V6H18V10H12V8H10V13H16V17H3V14H0V5H2V2H4Z"
)

@Composable
fun FishingGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::FishingRun
    )
    ArcadeBoard(
        game = ArcadeGame.Fishing,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = { run.cast() }
        ) {
            val water = run.waterline
            pixelRect(
                color = style.playerColor.copy(alpha = 0.06f),
                x = 0f,
                y = water,
                width = run.width,
                height = run.height - water
            )
            var x = -20f
            while (x < run.width) {
                val y = water + sin(x * 0.06f + run.time * 2f) * 2f
                drawLine(
                    color = style.playerColor.copy(alpha = 0.4f),
                    start = Offset(x, y),
                    end = Offset(x + 12f, y + 1f),
                    strokeWidth = 1f
                )
                x += 20f
            }
            repeat(14) { index ->
                val x = (index * 83f + 17f) % run.width
                val depth = run.height - water - 12f
                val y = water + depth - (index * 41f + run.time * 9f) % depth
                drawCircle(
                    color = style.playerColor.copy(alpha = 0.15f),
                    radius = 1.5f + index % 2,
                    center = Offset(x, y),
                    style = Stroke(0.8f)
                )
            }
            val bottom = run.height - 8f
            pixelRect(
                color = style.detailColor.copy(alpha = 0.3f),
                x = 0f,
                y = bottom,
                width = run.width,
                height = 1f
            )
            repeat(7) { index ->
                val x = (index + 0.4f) * run.width / 7f
                val bend = sin(run.time * 1.4f + index) * 3f
                drawLine(
                    color = style.obstacleColor.copy(alpha = 0.4f),
                    start = Offset(x, bottom),
                    end = Offset(x + bend, bottom - 15f - index % 3 * 5f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = style.obstacleColor.copy(alpha = 0.4f),
                    start = Offset(x, bottom - 5f),
                    end = Offset(x - 4f + bend, bottom - 13f),
                    strokeWidth = 1.5f
                )
            }
            fun fish(
                point: Offset,
                kind: Int,
                direction: Int
            ) {
                val color = when (kind) {
                    1 -> style.obstacleColor
                    2 -> style.playerColor
                    else -> style.accentColor
                }
                translate(point.x, point.y) {
                    scale(
                        scaleX = direction * (1.1f - kind * 0.13f),
                        scaleY = 1.1f - kind * 0.13f,
                        pivot = Offset.Zero
                    ) {
                        drawArcadeSprite(
                            sprite = ArcadeFish,
                            tint = color,
                            x = -20f,
                            y = -11f
                        )
                        pixelRect(
                            color = style.playerDetailColor,
                            x = 10f,
                            y = -5f,
                            width = 3f,
                            height = 3f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.4f),
                            x = 5f,
                            y = -5f,
                            width = 2f,
                            height = 10f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.2f),
                            x = -6f,
                            y = 4f,
                            width = 15f,
                            height = 2f
                        )
                        pixelRect(
                            color = style.playerDetailColor.copy(alpha = 0.35f),
                            x = -4f,
                            y = -1f,
                            width = 5f,
                            height = 4f
                        )
                        pixelRect(
                            color = color,
                            x = -1f,
                            y = 0f,
                            width = 4f,
                            height = 3f
                        )
                        repeat(kind) { index ->
                            pixelRect(
                                color = style.playerDetailColor,
                                x = -8f + index * 4f,
                                y = -1f,
                                width = 2f,
                                height = 2f
                            )
                        }
                    }
                }
            }
            for (item in run.fish) fish(
                point = Offset(item.x, run.fishY(item.row)),
                kind = item.kind,
                direction = item.direction
            )
            val point = run.hook
            fishingLine(
                start = run.anchor,
                end = point,
                color = style.textColor.copy(alpha = 0.45f),
                width = 1f
            )
            pixelRect(
                color = style.textColor,
                x = point.x - 1f,
                y = point.y - 3f,
                width = 2f,
                height = 10f
            )
            pixelRect(
                color = style.textColor,
                x = point.x - 5f,
                y = point.y + 5f,
                width = 5f,
                height = 2f
            )
            pixelRect(
                color = style.textColor,
                x = point.x - 6f,
                y = point.y + 1f,
                width = 2f,
                height = 5f
            )
            if (run.caught > 0) fish(
                point = point + Offset(0f, 8f),
                kind = run.caught,
                direction = 1
            )
            val boatX = run.anchor.x - 18f
            val boatY = water - 9f + sin(run.time * 2f)
            pixelRect(
                color = style.obstacleColor,
                x = boatX - 22f,
                y = boatY,
                width = 44f,
                height = 5f
            )
            pixelRect(
                color = style.obstacleColor,
                x = boatX - 17f,
                y = boatY + 5f,
                width = 34f,
                height = 5f
            )
            pixelRect(
                color = style.playerDetailColor,
                x = boatX - 15f,
                y = boatY + 1f,
                width = 26f,
                height = 2f
            )
            pixelRect(
                color = style.accentColor,
                x = boatX - 4f,
                y = boatY - 26f,
                width = 9f,
                height = 9f
            )
            pixelRect(
                color = style.accentColor,
                x = boatX + 5f,
                y = boatY - 23f,
                width = 3f,
                height = 4f
            )
            pixelRect(
                color = style.playerDetailColor,
                x = boatX + 3f,
                y = boatY - 24f,
                width = 2f,
                height = 2f
            )
            drawArcadeSprite(
                sprite = FishingCap,
                tint = style.playerColor,
                x = boatX - 9f,
                y = boatY - 32f
            )
            pixelRect(
                color = style.playerDetailColor.copy(alpha = 0.4f),
                x = boatX - 6f,
                y = boatY - 27f,
                width = 15f,
                height = 2f
            )
            drawArcadeSprite(
                sprite = FishingCoat,
                tint = style.playerColor,
                x = boatX - 8f,
                y = boatY - 18f
            )
            pixelRect(
                color = style.playerDetailColor,
                x = boatX - 5f,
                y = boatY - 5f,
                width = 10f,
                height = 4f
            )
            pixelRect(
                color = style.accentColor,
                x = boatX + 7f,
                y = boatY - 12f,
                width = 4f,
                height = 3f
            )
            fishingLine(
                start = Offset(boatX + 9f, boatY - 10f),
                end = run.anchor,
                color = style.obstacleColor,
                width = 2f
            )
            pixelRect(
                color = style.accentColor,
                x = boatX + 6f,
                y = boatY - 11f,
                width = 3f,
                height = 7f
            )
            pixelRect(
                color = style.playerDetailColor,
                x = boatX + 4f,
                y = boatY - 8f,
                width = 5f,
                height = 4f
            )
            pixelRect(
                color = style.obstacleColor,
                x = boatX + 5f,
                y = boatY - 7f,
                width = 2f,
                height = 2f
            )
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
            arcadeLives(
                style = style,
                remaining = 3 - run.misses,
                y = run.topInset / 2f
            )
        }
    }
}

private fun DrawScope.fishingLine(
    start: Offset,
    end: Offset,
    color: Color,
    width: Float
) {
    val distance = end - start
    val steps = (maxOf(abs(distance.x), abs(distance.y)) / 3f).toInt().coerceAtLeast(1)
    val path = Path().apply {
        moveTo(start.x, start.y)
        var previous = start
        for (step in 1..steps) {
            val point = start + distance * (step.toFloat() / steps)
            lineTo(point.x, previous.y)
            lineTo(point.x, point.y)
            previous = point
        }
    }
    drawPath(path, color, style = Stroke(width))
}

internal data class SwimmingFish(
    var x: Float,
    val row: Int,
    val kind: Int,
    val direction: Int
)

internal class FishingRun : ArcadeRun() {
    val fish = mutableListOf<SwimmingFish>()
    val effects = ArcadeEffects()
    val waterline get() = topInset + 35f
    val anchor get() = Offset(width / 2f + 18f, waterline - 28f)
    var angle = 0f
    var length = 22f
    var caught = 0
    var misses = 0
    var time = 0f
    val hook get() = anchor + Offset(sin(angle), cos(angle)) * length
    private var casting = false
    private var returning = false

    fun fishY(row: Int) = waterline + (height - waterline - 18f) * (row + 0.7f) / 4.2f

    override fun reset() {
        fish.clear()
        effects.clear()
        angle = 0f
        length = 22f
        caught = 0
        misses = 0
        time = 0f
        casting = false
        returning = false
        repeat(4) { row ->
            fish += SwimmingFish(
                Random.nextFloat() * (width - 60f) + 30f, row,
                1 + row % 3, if (row % 2 == 0) 1 else -1
            )
        }
    }

    fun cast() {
        if (casting) return
        casting = true
        returning = false
        caught = 0
    }

    override fun advance(seconds: Float) {
        time += seconds
        effects.advance(seconds)
        for (item in fish) {
            item.x += item.direction * (22f + item.kind * 12f + minOf(score * 0.5f, 55f)) * seconds
            if (item.x > width + 30f) item.x = -30f
            if (item.x < -30f) item.x = width + 30f
        }
        if (!casting) {
            angle = sin(time * 1.25f) * 0.95f
            return
        }
        val speed = maxOf(240f, (height - waterline) * 1.7f)
        if (returning) {
            length -= speed * 1.2f * seconds
            if (length <= 22f) {
                length = 22f
                casting = false
                returning = false
                if (caught > 0) {
                    score += caught
                    effects.burst(point = Offset(width / 2f, waterline))
                } else misses++
                caught = 0
                if (misses >= 3) phase = ArcadePhase.Over
            }
        } else {
            length += speed * seconds
            val point = hook
            val hit = fish.indexOfFirst {
                val scale = 1.1f - it.kind * 0.13f
                val dx = (it.x - point.x) / (20f * scale)
                val dy = (fishY(it.row) - point.y) / (11f * scale)
                dx * dx + dy * dy <= 1f
            }
            if (hit >= 0) {
                val item = fish[hit]
                caught = item.kind
                returning = true
                effects.burst(
                    point = point,
                    count = 5
                )
                fish[hit] = SwimmingFish(
                    if (item.direction > 0) -32f else width + 32f,
                    item.row, Random.nextInt(1, 4), item.direction
                )
            } else if (point.x !in 10f..(width - 10f) || point.y >= height - 14f) returning = true
        }
    }

    override fun onResize(previous: Size) {
        fish.forEach { it.x *= width / previous.width }
        length *= height / previous.height
        effects.clear()
    }

    override fun save(state: Bundle) {
        state.putFloatArray(
            "hook",
            floatArrayOf(angle, length, caught.toFloat(), misses.toFloat(), time)
        )
        state.putBoolean("casting", casting)
        state.putBoolean("returning", returning)
        state.putFloatArray(
            "fish",
            fish.flatMap {
                listOf(
                    it.x,
                    it.row.toFloat(),
                    it.kind.toFloat(),
                    it.direction.toFloat()
                )
            }.toFloatArray()
        )
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("hook")?.let {
            angle = it[0]
            length = it[1]
            caught = it[2].toInt()
            misses = it[3].toInt()
            time = it[4]
        }
        casting = state.getBoolean("casting")
        returning = state.getBoolean("returning")
        fish.clear()
        state.getFloatArray("fish")?.let {
            for (i in it.indices step 4) fish += SwimmingFish(
                it[i],
                it[i + 1].toInt(),
                it[i + 2].toInt(),
                it[i + 3].toInt()
            )
        }
    }
}