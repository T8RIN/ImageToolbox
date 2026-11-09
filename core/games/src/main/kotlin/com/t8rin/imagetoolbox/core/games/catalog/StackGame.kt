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
import androidx.compose.ui.geometry.Size
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeCloud
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.arcadeGround
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlin.math.abs

private const val BlockHeight = 18f
private const val InitialBlockWidth = 156f

@Composable
fun StackGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::StackRun
    )
    ArcadeBoard(
        game = ArcadeGame.Stack,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        isPaused = isPaused,
        onGameOver = onGameOver
    ) {
        ArcadeField(
            run = run,
            onPress = { run.place() }
        ) {
            val ground = run.height - 24f
            var cloudY = run.camera * 0.12f % 180f + run.topInset + 20f
            while (cloudY < run.height) {
                drawArcadeSprite(
                    sprite = ArcadeCloud,
                    tint = style.detailColor,
                    x = run.width * 0.12f,
                    y = cloudY
                )
                drawArcadeSprite(
                    sprite = ArcadeCloud,
                    tint = style.detailColor,
                    x = run.width * 0.76f,
                    y = cloudY + 70f
                )
                cloudY += 180f
            }
            if (run.camera < 24f) arcadeGround(
                style = style,
                width = run.width,
                y = ground + run.camera
            )

            fun block(
                x: Float,
                y: Float,
                width: Float,
                active: Boolean,
                perfect: Boolean
            ) {
                val color = when {
                    active -> style.playerColor
                    perfect -> style.accentColor
                    else -> style.obstacleColor
                }
                pixelRect(
                    color = color,
                    x = x,
                    y = y,
                    width = width,
                    height = BlockHeight - 1
                )
                pixelRect(
                    color = style.buttonContentColor.copy(alpha = 0.25f),
                    x = x + 2,
                    y = y + 2,
                    width = width - 4,
                    height = 2f
                )
                pixelRect(
                    color = style.detailColor,
                    x = x + 2,
                    y = y + BlockHeight - 3,
                    width = width - 4,
                    height = 1f
                )
            }

            run.blocks.forEach { block ->
                block(
                    x = block.x,
                    y = ground - (block.level + 1) * BlockHeight + run.camera,
                    width = block.width,
                    active = false,
                    perfect = block.perfect
                )
            }
            for ((x, altitude, width) in run.pieces) {
                block(
                    x = x,
                    y = ground - altitude + run.camera,
                    width = width,
                    active = true,
                    perfect = false
                )
            }
            val top = run.blocks.last()
            if (run.phase != ArcadePhase.Over) {
                block(
                    x = run.x,
                    y = ground - (top.level + 2) * BlockHeight + run.camera,
                    width = top.width,
                    active = true,
                    perfect = false
                )
            }
            if (run.flash > 0f) {
                val y = ground - (top.level + 1) * BlockHeight + run.camera
                val color = style.accentColor.copy(alpha = run.flash * 0.5f)
                val spread = (1f - run.flash) * 12f
                pixelRect(
                    color = color,
                    x = top.x - spread,
                    y = y - 3 - spread,
                    width = top.width + spread * 2,
                    height = 1f
                )
                pixelRect(
                    color = color,
                    x = top.x - spread,
                    y = y + BlockHeight + 2 + spread,
                    width = top.width + spread * 2,
                    height = 1f
                )
            }
        }
    }
}

@Serializable
internal data class StackBlock(
    var x: Float,
    val width: Float,
    val level: Int,
    val perfect: Boolean
)

@Serializable
internal data class StackPiece(
    var x: Float,
    var altitude: Float,
    val width: Float,
    var velocity: Float,
    val direction: Int
)

@Serializable
internal class StackRun : ArcadeRun() {
    var x = 0f
        private set
    var camera = 0f
        private set
    var flash = 0f
        private set
    val blocks = mutableListOf<StackBlock>()
    val pieces = mutableListOf<StackPiece>()
    private var direction = 1
    private var perfectStreak = 0
    private var cooldown = 0f

    override fun isValid(): Boolean = super.isValid() &&
            blocks.isNotEmpty() && blocks.all { it.width > 0f }

    override fun reset() {
        x = 0f
        camera = 0f
        flash = 0f
        direction = 1
        perfectStreak = 0
        cooldown = 0f
        pieces.clear()
        blocks.clear()
        blocks += StackBlock(
            x = (width - InitialBlockWidth) / 2,
            width = InitialBlockWidth,
            level = 0,
            perfect = false
        )
    }

    fun place() {
        if (cooldown > 0f) return
        val last = blocks.last()
        val left = maxOf(x, last.x)
        val right = minOf(x + last.width, last.x + last.width)
        if (right - left < 3f) {
            phase = ArcadePhase.Over
            return
        }
        val perfect = abs(x - last.x) <= 4f
        perfectStreak = if (perfect) perfectStreak + 1 else 0
        val next = if (perfect) {
            val restored = if (perfectStreak >= 3) 8f else 0f
            val newWidth = minOf(last.width + restored, InitialBlockWidth)
            StackBlock(
                x = (last.x - (newWidth - last.width) / 2).coerceIn(0f, width - newWidth),
                width = newWidth, level = score + 1, perfect = true
            )
        } else {
            val cutX = if (x < last.x) x else right
            pieces += StackPiece(
                x = cutX,
                altitude = (score + 2) * BlockHeight,
                width = last.width - (right - left),
                velocity = 0f,
                direction = if (x < last.x) -1 else 1
            )
            StackBlock(x = left, width = right - left, level = score + 1, perfect = false)
        }
        if (perfect) flash = 1f
        blocks += next
        score++
        cooldown = 0.13f
        direction = if (score % 2 == 0) 1 else -1
        x = if (direction > 0) 0f else width - next.width
    }

    override fun advance(seconds: Float) {
        flash = (flash - seconds * 3f).coerceAtLeast(0f)
        cooldown = (cooldown - seconds).coerceAtLeast(0f)
        val target = maxOf(0f, (score + 2) * BlockHeight - (height - topInset) * 0.58f)
        camera += (target - camera) * minOf(1f, seconds * 8f)
        val edge = width - blocks.last().width
        if (cooldown == 0f) {
            x += direction * (145f + score.coerceAtMost(50) * 5f) * seconds
            if (x <= 0f || x >= edge) {
                x = x.coerceIn(0f, edge)
                direction = if (x == 0f) 1 else -1
            }
        }
        pieces.forEach {
            it.velocity += 760f * seconds
            it.altitude -= it.velocity * seconds
            it.x += it.direction * 45f * seconds
        }
        pieces.removeAll { it.altitude < camera - 40f }
        blocks.removeAll { (it.level + 1) * BlockHeight < camera - 40f }
    }

    override fun onResize(previous: Size) {
        val shift = (width - previous.width) / 2
        blocks.forEach { it.x = (it.x + shift).coerceIn(0f, width - it.width) }
        pieces.forEach { it.x += shift }
        x = (x + shift).coerceIn(0f, width - (blocks.lastOrNull()?.width ?: InitialBlockWidth))
    }
}