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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.pixelRect
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.pixelGem
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.VectorPolyline
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

@RegisterArcadeGame
internal data object SharpTurnsGame : ArcadeGameDefinition {
    override val title = R.string.arcade_sharpturns
    override val description = R.string.arcade_sharpturns_sub
    override val icon get() = Icons.Outlined.VectorPolyline
    override val order = 24

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::SharpTurnsRun
        )
        val tilePath = remember { Path() }
        val edgePath = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.turn() }
            ) {
                drawZigzagBackground(style, run.width, run.height)
                val tint = lerp(style.playerColor, style.accentColor, run.tint)

                for (index in run.visibleTiles.reversed()) {
                    val (x, y, hasGem, span) = run.tiles[index]
                    val half = run.tileWidth * span / 2f
                    val depth = half / 2f
                    val point = run.project(x.toFloat(), y.toFloat())
                    if (point.y + depth + 5f < 0f || point.y - maxOf(
                            depth,
                            if (hasGem) 12f else 0f
                        ) > run.height
                    ) continue
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
                        color = lerp(style.playerDetailColor, tint, 0.6f)
                    )
                    drawPath(
                        path = path,
                        color = lerp(style.playerDetailColor, style.obstacleColor, 0.22f)
                    )
                    drawPath(
                        path = path,
                        color = tint.copy(alpha = 0.65f),
                        style = Stroke(width = 1f)
                    )
                    val face = edgePath.apply {
                        rewind()
                        moveTo(point.x, point.y + depth)
                        lineTo(point.x + half, point.y)
                        lineTo(point.x + half, point.y + 5f)
                        lineTo(point.x, point.y + depth + 5f)
                        close()
                    }
                    drawPath(path = face, color = lerp(style.playerDetailColor, tint, 0.4f))
                    if (hasGem) {
                        pixelGem(
                            color = style.accentColor,
                            light = style.playerDetailColor,
                            center = point - Offset(x = 0f, y = 5f),
                            radius = 6.5f
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
}

private fun DrawScope.drawZigzagBackground(style: ArcadeStyle, width: Float, height: Float) {
    val count = (width * height / 20000f).toInt().coerceAtLeast(5)
    repeat(count) { index ->
        val x = (index + 0.2f) * width / count
        val y = height * (0.12f + (index * 37 % 79) / 100f)
        val half = 13f + index % 3 * 4f
        val top = Path().apply {
            moveTo(x, y - half * 0.5f)
            lineTo(x + half, y)
            lineTo(x, y + half * 0.5f)
            lineTo(x - half, y)
            close()
        }
        drawPath(top, style.detailColor.copy(alpha = 0.035f))
        drawPath(top, style.detailColor.copy(alpha = 0.14f), style = Stroke(0.8f))
        val side = Path().apply {
            moveTo(x - half, y)
            lineTo(x, y + half * 0.5f)
            lineTo(x + half, y)
            lineTo(x + half, y + 6f)
            lineTo(x, y + half * 0.5f + 6f)
            lineTo(x - half, y + 6f)
            close()
        }
        drawPath(side, style.detailColor.copy(alpha = 0.065f))
        pixelRect(style.detailColor.copy(alpha = 0.1f), x - 8f, y + half * 0.5f + 13f, 4f, 2f)
    }
}

@Serializable
private data class SharpTurnsTile(
    val x: Int,
    val y: Int,
    var gem: Boolean,
    val span: Float = 1f
)

@Serializable
private class SharpTurnsRun : ArcadeRun() {
    val tiles = mutableListOf<SharpTurnsTile>()

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
    private val lengths: ArcadeDeque<Int> = ArrayDeque()
    private var gems = 0
    val tileWidth get() = minOf(58f, width * 0.17f, (height - topInset) * 0.32f)
    val visibleTiles: IntRange
        get() {
            if (tiles.isEmpty()) return IntRange.EMPTY
            val step = tileWidth / 4f
            val anchor = topInset + (height - topInset) * 0.76f
            val margin = tileWidth / 2f + 8f
            val origin = tiles.first().x + tiles.first().y
            val first = floor(progress + (anchor - height - margin) / step).toInt() - origin
            val last = ceil(progress + (anchor + margin) / step).toInt() - origin
            return first.coerceAtLeast(0)..last.coerceAtMost(tiles.lastIndex)
        }
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
        lengths.clear()
        gems = 0
        tiles += SharpTurnsTile(x = -1, y = 0, gem = false)
        tiles += SharpTurnsTile(x = 0, y = 0, gem = false)
        populate()
    }

    private fun populate() {
        val ahead = ceil((height - topInset) / (tileWidth / 4f)).toInt().coerceAtLeast(14) + 3
        // A taller layout can bring old tiles back into view.
        while (tiles.last().x + tiles.last().y < progress + ahead) {
            val last = tiles.last()
            if (remaining == 0) {
                generatedDirection = 1 - generatedDirection
                if (lengths.isEmpty()) lengths.addAll(
                    listOf(
                        listOf(2, 2, 4, 3, 5, 3, 2, 4),
                        listOf(3, 5, 2, 2, 4, 2, 3, 4),
                        listOf(4, 2, 3, 2, 5, 4, 2, 3)
                    ).random()
                )
                remaining =
                    if ((last.x + last.y) % 36 < 6) Random.nextInt(5, 7) else lengths.removeFirst()
            }
            val nextX = last.x + if (generatedDirection == 0) 1 else 0
            val nextY = last.y + if (generatedDirection == 1) 1 else 0
            val index = nextX + nextY
            tiles += SharpTurnsTile(
                x = nextX, y = nextY, gem = index % 5 == 0 || index % 36 == 16,
                span = if (remaining == 1 && index % 24 < 6 && index > 10) 1.35f else 1f
            )
            remaining--
        }
        while (tiles.size > 2048 && tiles.first().x + tiles.first().y < progress - ahead * 2) {
            tiles.removeAt(0)
        }
    }

    fun turn() {
        if (fall >= 0f) return
        direction = 1 - direction
    }

    fun supported(
        x: Float,
        y: Float
    ): Boolean = visibleTiles.any { index ->
        val tile = tiles[index]
        // The contact point supports the ball even when part of it overhangs an edge.
        abs(x - tile.x) <= tile.span * 0.5f && abs(y - tile.y) <= tile.span * 0.5f
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        if (fall >= 0f) {
            fall += seconds * 1.6f
            if (fall >= 0.9f) phase = ArcadePhase.Over
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
        for (index in visibleTiles) {
            val tile = tiles[index]
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