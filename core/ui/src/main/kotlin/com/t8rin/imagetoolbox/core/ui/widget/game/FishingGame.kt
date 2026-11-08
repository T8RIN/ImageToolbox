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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val ArcadeFish = ArcadeSprite(
    "ArcadeFish", 40, 22,
    "M0 3L10 8L15 3H22V0H27V3H31L40 11L31 19H26V22H21V19H15L10 14L0 19Z"
)

@Composable
fun FishingGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::FishingRun)
    ArcadeBoard(
        ArcadeGame.Fishing, run, style, modifier, initialState, onHighScoreChanged, onGameOver
    ) {
        ArcadeField(run, onPress = { run.cast() }) {
            val water = run.waterline
            pixelRect(
                style.playerColor.copy(alpha = 0.06f),
                0f,
                water,
                run.width,
                run.height - water
            )
            var x = -20f
            while (x < run.width) {
                val y = water + sin(x * 0.06f + run.time * 2f) * 2f
                drawLine(
                    style.playerColor.copy(alpha = 0.4f),
                    Offset(x, y),
                    Offset(x + 12f, y + 1f),
                    1f
                )
                x += 20f
            }
            repeat(14) { index ->
                val x = (index * 83f + 17f) % run.width
                val depth = run.height - water - 12f
                val y = water + depth - (index * 41f + run.time * 9f) % depth
                drawCircle(
                    style.playerColor.copy(alpha = 0.15f),
                    1.5f + index % 2,
                    Offset(x, y),
                    style = Stroke(0.8f)
                )
            }
            val bottom = run.height - 8f
            pixelRect(style.detailColor.copy(alpha = 0.3f), 0f, bottom, run.width, 1f)
            repeat(7) { index ->
                val x = (index + 0.4f) * run.width / 7f
                val bend = sin(run.time * 1.4f + index) * 3f
                drawLine(
                    style.obstacleColor.copy(alpha = 0.4f),
                    Offset(x, bottom),
                    Offset(x + bend, bottom - 15f - index % 3 * 5f),
                    2f
                )
                drawLine(
                    style.obstacleColor.copy(alpha = 0.4f),
                    Offset(x, bottom - 5f),
                    Offset(x - 4f + bend, bottom - 13f),
                    1.5f
                )
            }
            fun fish(point: Offset, kind: Int, direction: Int) {
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
                        drawArcadeSprite(ArcadeFish, color, -20f, -11f)
                        pixelRect(style.playerDetailColor, 10f, -5f, 3f, 3f)
                        drawLine(
                            style.playerDetailColor.copy(alpha = 0.5f),
                            Offset(5f, -5f),
                            Offset(5f, 5f),
                            1.5f
                        )
                        repeat(kind) { index ->
                            pixelRect(style.playerDetailColor, -8f + index * 4f, -1f, 2f, 2f)
                        }
                    }
                }
            }
            for (item in run.fish) fish(
                Offset(item.x, run.fishY(item.row)),
                item.kind,
                item.direction
            )
            val point = run.hook
            drawLine(style.textColor.copy(alpha = 0.65f), run.anchor, point, 1f)
            drawArc(
                style.textColor,
                0f,
                210f,
                false,
                point - Offset(4f, 0f),
                Size(8f, 10f),
                style = Stroke(2f)
            )
            drawLine(style.textColor, point - Offset(0f, 4f), point + Offset(0f, 2f), 1.5f)
            if (run.caught > 0) fish(point + Offset(0f, 8f), run.caught, 1)
            val boatX = run.anchor.x - 18f
            val boatY = water - 9f + sin(run.time * 2f)
            pixelRect(style.obstacleColor, boatX - 22f, boatY, 44f, 5f)
            pixelRect(style.obstacleColor, boatX - 17f, boatY + 5f, 34f, 5f)
            pixelRect(style.playerDetailColor, boatX - 15f, boatY + 1f, 26f, 2f)
            pixelRect(style.playerColor, boatX - 4f, boatY - 11f, 8f, 11f)
            drawCircle(style.playerColor, 4.5f, Offset(boatX, boatY - 15f))
            drawLine(style.textColor, Offset(boatX + 3f, boatY - 8f), run.anchor, 2f)
            run.effects.draw(this, style.accentColor)
            arcadeLives(style, 3 - run.misses, run.topInset / 2f)
        }
    }
}

internal data class SwimmingFish(var x: Float, val row: Int, val kind: Int, val direction: Int)

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
                    effects.burst(Offset(width / 2f, waterline))
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
                effects.burst(point, 5)
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