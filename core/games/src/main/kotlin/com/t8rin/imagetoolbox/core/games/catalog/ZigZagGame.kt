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

@file:UseSerializers(ArcadeOffsetSerializer::class)

package com.t8rin.imagetoolbox.core.games.catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.ArcadeOffsetSerializer
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeStars
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun ZigZagGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null,
    isPaused: Boolean = false
) {
    val run = rememberArcadeRun(
        initialState = initialState,
        create = ::ZigZagRun
    )
    val tilePath = remember { Path() }
    val edgePath = remember { Path() }
    val gemPath = remember { Path() }
    ArcadeBoard(
        game = ArcadeGame.ZigZag,
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
            onPress = { run.turn() }
        ) {
            arcadeStars(
                style = style,
                width = run.width,
                height = run.height,
                drift = run.progress * 2f
            )
            val tint = lerp(style.playerColor, style.accentColor, run.tint)

            for ((x, y, hasGem, span) in run.tiles.asReversed()) {
                val half = run.tileWidth * span / 2f
                val depth = half / 2f
                val point = run.project(x.toFloat(), y.toFloat())
                if (point.y < -depth || point.y > run.height + depth) continue
                val path = tilePath.apply {
                    rewind()
                    moveTo(point.x, point.y - depth)
                    lineTo(point.x + half, point.y)
                    lineTo(point.x, point.y + depth)
                    lineTo(point.x - half, point.y)
                    close()
                }
                val edge = edgePath.apply {
                    rewind()
                    moveTo(point.x - half, point.y)
                    lineTo(point.x, point.y + depth)
                    lineTo(point.x + half, point.y)
                    lineTo(point.x + half, point.y + 5f)
                    lineTo(point.x, point.y + depth + 5f)
                    lineTo(point.x - half, point.y + 5f)
                    close()
                }
                drawPath(
                    path = edge,
                    color = tint.copy(alpha = 0.25f)
                )
                drawPath(
                    path = path,
                    color = style.obstacleColor.copy(alpha = 0.3f)
                )
                drawPath(
                    path = path,
                    color = tint.copy(alpha = 0.65f),
                    style = Stroke(width = 1f)
                )
                if (hasGem) {
                    val gem = gemPath.apply {
                        rewind()
                        moveTo(point.x, point.y - 12f)
                        lineTo(point.x + 5f, point.y - 5f)
                        lineTo(point.x, point.y + 2f)
                        lineTo(point.x - 5f, point.y - 5f)
                        close()
                    }
                    drawCircle(
                        color = style.accentColor.copy(alpha = 0.12f),
                        radius = 10f,
                        center = point - Offset(x = 0f, y = 5f)
                    )
                    drawPath(
                        path = gem,
                        color = style.accentColor
                    )
                }
            }
            val point = run.project(run.x, run.y)
            val falling = run.fall.coerceAtLeast(0f)
            val radius = run.tileWidth * 0.12f * (1f - falling * 0.5f)
            drawOval(
                color = style.playerDetailColor.copy(alpha = 0.2f),
                topLeft = point - Offset(x = radius, y = 1f),
                size = Size(width = radius * 2f, height = radius)
            )
            val ball = point + Offset(x = 0f, y = -radius + falling * falling * 100f)
            drawCircle(
                color = style.playerColor.copy(alpha = 1f - falling),
                radius = radius,
                center = ball
            )
            drawCircle(
                color = style.playerDetailColor.copy(alpha = (1f - falling) * 0.7f),
                radius = radius * 0.28f,
                center = ball - Offset(x = radius * 0.28f, y = radius * 0.3f)
            )
            run.effects.draw(
                scope = this,
                color = style.accentColor
            )
        }
    }
}

@Serializable
internal data class ZigZagTile(
    val x: Int,
    val y: Int,
    var gem: Boolean,
    val span: Float = 1f
)

@Serializable
internal class ZigZagRun : ArcadeRun() {
    val tiles = mutableListOf<ZigZagTile>()

    @Transient
    val effects = ArcadeEffects()
    var x = 0f
    var y = 0f
    var direction = 0
    var fall = -1f
    var progress = 0f
    private var camera = 0f
    private var edgeTime = 0f
    private var generatedDirection = 0
    private var remaining = 4
    private var gems = 0
    private var grace = 0.7f
    val tileWidth get() = minOf(58f, width * 0.17f, (height - topInset) * 0.32f)
    val speed get() = 2.1f + 2f * progress / (progress + 240f)
    val tint: Float
        get() {
            val phase = progress / 80f % 2f
            val amount = if (phase <= 1f) phase else 2f - phase
            return amount * amount * (3f - 2f * amount)
        }

    fun project(
        x: Float,
        y: Float
    ) = Offset(
        x = width / 2f + (x - y - camera) * tileWidth / 2f,
        y = topInset + (height - topInset) * 0.76f - (x + y - progress) * tileWidth / 4f
    )

    override fun isValid(): Boolean = super.isValid() &&
            tiles.isNotEmpty()

    override fun reset() {
        tiles.clear()
        effects.clear()
        x = 0f
        y = 0f
        direction = 0
        fall = -1f
        progress = 0f
        camera = 0f
        edgeTime = 0f
        generatedDirection = 0
        remaining = 4
        gems = 0
        grace = 0.7f
        tiles += ZigZagTile(x = -1, y = 0, gem = false)
        tiles += ZigZagTile(x = 0, y = 0, gem = false)
        populate()
    }

    private fun populate() {
        val ahead = ((height - topInset) / (tileWidth / 4f)).toInt().coerceIn(14, 50)
        while (tiles.last().x + tiles.last().y < progress + ahead) {
            val last = tiles.last()
            if (remaining == 0) {
                generatedDirection = 1 - generatedDirection
                remaining =
                    if ((last.x + last.y) % 36 < 6) Random.nextInt(5, 7) else Random.nextInt(2, 5)
            }
            val nextX = last.x + if (generatedDirection == 0) 1 else 0
            val nextY = last.y + if (generatedDirection == 1) 1 else 0
            val index = nextX + nextY
            tiles += ZigZagTile(
                x = nextX, y = nextY, gem = index % 5 == 0 || index % 36 == 16,
                span = if (remaining == 1 && index % 24 < 6 && index > 10) 1.35f else 1f
            )
            remaining--
        }
        tiles.removeAll { it.x + it.y < progress - 5f }
    }

    fun turn() {
        if (fall >= 0f) return
        direction = 1 - direction
    }

    internal fun supported(
        x: Float,
        y: Float
    ): Boolean = tiles.any {
        // The contact point supports the ball even when part of it overhangs an edge.
        abs(x - it.x) <= it.span * 0.5f && abs(y - it.y) <= it.span * 0.5f
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        if (fall >= 0f) {
            fall += seconds * 1.6f
            if (fall >= 0.9f) phase = ArcadePhase.Over
            return
        }
        if (grace > 0f) {
            grace -= seconds
            return
        }
        if (direction == 0) x += speed * seconds else y += speed * seconds
        progress = x + y
        camera += (x - y - camera) * minOf(1f, seconds * 8f)
        edgeTime = if (supported(x, y)) 0f else edgeTime + seconds
        if (edgeTime > 0.075f) {
            fall = 0f
            return
        }
        for (tile in tiles) {
            if (tile.gem && abs(x - tile.x) < 0.3f && abs(y - tile.y) < 0.3f) {
                tile.gem = false
                gems++
                effects.burst(
                    point = project(tile.x.toFloat(), tile.y.toFloat()) - Offset(x = 0f, y = 5f),
                    count = 8
                )
            }
        }
        score = progress.toInt() + gems * 5
        populate()
    }

    override fun onResize(previous: Size) {
        if (tiles.isNotEmpty()) populate()
    }
}