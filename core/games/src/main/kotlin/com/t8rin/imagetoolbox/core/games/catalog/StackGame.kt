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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadePhase
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Stacks
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.exp

@RegisterArcadeGame
internal data object StackGame : ArcadeGameDefinition {
    override val title = R.string.arcade_stack
    override val description = R.string.arcade_stack_sub
    override val icon get() = Icons.Outlined.Stacks
    override val order = 3

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::StackRun
        )
        val style = context.style
        val palette = remember(style) { StackPalette(style) }
        val path = remember { Path() }

        ArcadeBoard(context = context, run = run, modifier = modifier) {
            ArcadeField(
                run = run,
                lightFeedback = true, onPress = { run.place() }) {
                drawStackScene(run, palette, path)
            }
        }
    }
}

private class StackPalette(style: ArcadeStyle) {
    val light = style.obstacleColor
    val shade = style.overlayColor.copy(alpha = 1f)
    val edge = style.borderColor
    val accent = style.accentColor
    val detail = style.detailColor
    private val layers = listOf(
        style.playerColor, style.obstacleColor, style.accentColor,
        lerp(style.accentColor, style.playerColor, 0.35f),
        lerp(style.playerColor, style.obstacleColor, 0.5f)
    )

    fun color(level: Int) = layers[(level / 3) % layers.size]
}

private fun DrawScope.drawStackScene(run: StackRun, palette: StackPalette, path: Path) {
    fun face(color: Color, a: Offset, b: Offset, c: Offset, d: Offset) {
        path.rewind()
        path.moveTo(a.x, a.y)
        path.lineTo(b.x, b.y)
        path.lineTo(c.x, c.y)
        path.lineTo(d.x, d.y)
        path.close()
        drawPath(path, color)
    }

    fun block(
        x: Float,
        z: Float,
        width: Float,
        depth: Float,
        altitude: Float,
        color: Color,
        thickness: Float = BlockHeight,
        alpha: Float = 1f
    ) {
        val back = run.project(x, z, altitude)
        val right = run.project(x + width, z, altitude)
        val front = run.project(x + width, z + depth, altitude)
        val left = run.project(x, z + depth, altitude)
        val lower = Offset(0f, thickness * run.unit)
        face(
            lerp(color, palette.shade, 0.36f).copy(alpha = alpha),
            left,
            front,
            front + lower,
            left + lower
        )
        face(
            lerp(color, palette.shade, 0.2f).copy(alpha = alpha),
            right,
            front,
            front + lower,
            right + lower
        )
        face(color.copy(alpha = alpha), back, right, front, left)
        drawPath(path, palette.edge.copy(alpha = alpha * 0.28f), style = Stroke(run.unit * 0.65f))
    }

    val backdropCount = (run.width * run.height / 18000f).toInt().coerceAtLeast(6)
    repeat(backdropCount) { index ->
        val span = run.height + 70f
        val x = (index + 0.4f) * run.width / backdropCount
        val y =
            (run.height * (0.14f + (index * 43 % 79) / 100f) + run.camera * 0.06f * run.unit) % span - 25f
        val half = 13f + index % 3 * 5f
        val lower = 6f + index % 2 * 3f
        face(
            palette.detail.copy(alpha = 0.045f), Offset(x - half, y), Offset(x, y + half * 0.5f),
            Offset(x, y + half * 0.5f + lower), Offset(x - half, y + lower)
        )
        face(
            palette.detail.copy(alpha = 0.075f), Offset(x, y + half * 0.5f), Offset(x + half, y),
            Offset(x + half, y + lower), Offset(x, y + half * 0.5f + lower)
        )
        face(
            palette.detail.copy(alpha = 0.06f), Offset(x, y - half * 0.5f),
            Offset(x + half, y), Offset(x, y + half * 0.5f), Offset(x - half, y)
        )
        drawPath(path, palette.detail.copy(alpha = 0.17f), style = Stroke(0.7f))
    }

    block(0f, 0f, InitialBlockWidth, InitialBlockWidth, 0f, palette.color(0), 80f)
    for (slab in run.blocks) {
        val altitude = (slab.level + 1) * BlockHeight
        val upper = run.project(slab.x, slab.z, altitude).y
        val lower = run.project(
            slab.x + slab.width,
            slab.z + slab.depth,
            altitude
        ).y + BlockHeight * run.unit
        if (lower < -2f || upper > run.height + 2f) continue
        block(slab.x, slab.z, slab.width, slab.depth, altitude, palette.color(slab.level))
    }
    for (piece in run.pieces) {
        val center =
            run.project(piece.x + piece.width / 2f, piece.z + piece.depth / 2f, piece.altitude)
        val alpha = ((run.height + 50f - center.y) / 70f).coerceIn(0f, 1f)
        rotate(piece.tilt, center) {
            block(
                piece.x,
                piece.z,
                piece.width,
                piece.depth,
                piece.altitude,
                palette.color(piece.level),
                alpha = alpha
            )
        }
    }
    val top = run.blocks.last()
    if (run.phase != ArcadePhase.Over && run.missTime == 0f) {
        block(
            x = if (run.axis == 0) run.x else top.x,
            z = if (run.axis == 1) run.x else top.z,
            width = top.width,
            depth = top.depth,
            altitude = (top.level + 2) * BlockHeight,
            color = palette.color(top.level + 1)
        )
    }
    if (run.flash > 0f) {
        val altitude = (top.level + 1) * BlockHeight
        val spread = (1f - run.flash) * 9f
        face(
            palette.accent.copy(alpha = run.flash * 0.2f),
            run.project(top.x - spread, top.z - spread, altitude + 1f),
            run.project(top.x + top.width + spread, top.z - spread, altitude + 1f),
            run.project(top.x + top.width + spread, top.z + top.depth + spread, altitude + 1f),
            run.project(top.x - spread, top.z + top.depth + spread, altitude + 1f)
        )
        drawPath(
            path,
            palette.light.copy(alpha = run.flash * 0.9f),
            style = Stroke(run.unit * 1.5f)
        )
    }
}

private const val BlockHeight = 14f
private const val InitialBlockWidth = 120f
private const val StackTravel = 140f

@Serializable
private data class StackBlock(
    var x: Float,
    val width: Float,
    val level: Int,
    val perfect: Boolean,
    var z: Float = 0f,
    val depth: Float = InitialBlockWidth
)

@Serializable
private data class StackPiece(
    var x: Float,
    var z: Float,
    var altitude: Float,
    val width: Float,
    val depth: Float,
    val level: Int,
    val axis: Int,
    val direction: Int,
    var velocity: Float = 0f,
    var tilt: Float = 0f
)

@Serializable
private class StackRun : ArcadeRun() {
    var x = -StackTravel
        private set
    var axis = 0
        private set
    var camera = 0f
        private set
    private var cameraX = InitialBlockWidth / 2f
    private var cameraZ = InitialBlockWidth / 2f
    var flash = 0f
        private set
    var missTime = 0f
        private set
    val blocks = mutableListOf<StackBlock>()
    val pieces = mutableListOf<StackPiece>()
    private var direction = 1
    private var perfectStreak = 0
    private var cooldown = 0f
    val unit get() = minOf(width / 200f, (height - topInset) / 170f)

    fun project(x: Float, z: Float, altitude: Float): Offset = Offset(
        width / 2f + (x - cameraX - z + cameraZ) * 0.70710677f * unit,
        height - 22f - InitialBlockWidth * 0.5f * unit +
                (x + z - cameraX - cameraZ) * 0.5f * unit - (altitude - camera) * unit
    )

    override fun isValid(): Boolean = super.isValid() && blocks.isNotEmpty() &&
            blocks.all {
                it.width in 3f..InitialBlockWidth && it.depth in 3f..InitialBlockWidth &&
                        it.x >= -0.01f && it.z >= -0.01f &&
                        it.x + it.width <= InitialBlockWidth + 0.01f &&
                        it.z + it.depth <= InitialBlockWidth + 0.01f
            }

    override fun reset() {
        x = -StackTravel
        axis = 0
        camera = 0f
        cameraX = InitialBlockWidth / 2f
        cameraZ = InitialBlockWidth / 2f
        flash = 0f
        missTime = 0f
        direction = 1
        perfectStreak = 0
        cooldown = 0f
        pieces.clear()
        blocks.clear()
        blocks += StackBlock(x = 0f, width = InitialBlockWidth, level = 0, perfect = false)
    }

    fun place() {
        if (cooldown > 0f || missTime > 0f) return
        val last = blocks.last()
        val start = if (axis == 0) last.x else last.z
        val span = if (axis == 0) last.width else last.depth
        val left = maxOf(x, start)
        val right = minOf(x + span, start + span)
        val overlap = right - left
        val perfect = abs(x - start) <= minOf(2.8f, span * 0.18f)
        if (!perfect) {
            val cutStart = if (overlap < 3f) x else if (x < start) x else right
            val cutSize = if (overlap < 3f) span else span - overlap
            pieces += StackPiece(
                x = if (axis == 0) cutStart else last.x,
                z = if (axis == 1) cutStart else last.z,
                altitude = (last.level + 2) * BlockHeight,
                width = if (axis == 0) cutSize else last.width,
                depth = if (axis == 1) cutSize else last.depth,
                level = last.level + 1,
                axis = axis,
                direction = if (x < start) -1 else 1
            )
        }
        if (!perfect && overlap < 3f) {
            missTime = 0.65f
            return
        }
        perfectStreak = if (perfect) perfectStreak + 1 else 0
        val checkpoint = (score + 1) % 25 == 0
        val restored =
            (if (perfect && perfectStreak % 3 == 0) 4f else 0f) + if (checkpoint) 2f else 0f
        val nextWidth = if (perfect) minOf(last.width + restored, InitialBlockWidth)
        else minOf((if (axis == 0) overlap else last.width) + restored, InitialBlockWidth)
        val nextDepth = if (perfect) minOf(last.depth + restored, InitialBlockWidth)
        else minOf((if (axis == 1) overlap else last.depth) + restored, InitialBlockWidth)
        val next = StackBlock(
            x = if (perfect) (last.x - restored / 2f).coerceIn(0f, InitialBlockWidth - nextWidth)
            else ((if (axis == 0) left else last.x) - restored / 2f).coerceIn(
                0f,
                InitialBlockWidth - nextWidth
            ),
            z = if (perfect) (last.z - restored / 2f).coerceIn(0f, InitialBlockWidth - nextDepth)
            else ((if (axis == 1) left else last.z) - restored / 2f).coerceIn(
                0f,
                InitialBlockWidth - nextDepth
            ),
            width = nextWidth,
            depth = nextDepth,
            level = last.level + 1,
            perfect = perfect
        )
        if (perfect || checkpoint) flash = 1f
        blocks += next
        // Keep enough history for taller layouts while bounding the saved run.
        if (blocks.size > 2048) blocks.subList(0, blocks.size - 2048).clear()
        score++
        cooldown = 0.16f
        axis = 1 - axis
        direction = if (score % 4 < 2) 1 else -1
        x = (if (axis == 0) next.x else next.z) - direction * StackTravel
    }

    override fun advance(seconds: Float) {
        flash = (flash - seconds * 3f).coerceAtLeast(0f)
        cooldown = (cooldown - seconds).coerceAtLeast(0f)
        val targetY = topInset + (height - topInset) * 0.44f
        val target = maxOf(
            0f, (score + 2) * BlockHeight -
                    (height - 22f - InitialBlockWidth * 0.5f * unit - targetY) / unit
        )
        val follow = 1f - exp(-seconds * 8f)
        camera += (target - camera) * follow
        val top = blocks.last()
        cameraX += (top.x + top.width / 2f - cameraX) * follow
        cameraZ += (top.z + top.depth / 2f - cameraZ) * follow
        for (piece in pieces) {
            piece.velocity += 680f * seconds
            piece.altitude -= piece.velocity * seconds
            if (piece.axis == 0) piece.x += piece.direction * 42f * seconds
            else piece.z += piece.direction * 42f * seconds
            piece.tilt += piece.direction * seconds * 45f
        }
        pieces.removeAll { it.altitude < camera - 160f }
        if (missTime > 0f) {
            missTime = (missTime - seconds).coerceAtLeast(0f)
            if (missTime == 0f) finish()
            return
        }
        val start = if (axis == 0) top.x else top.z
        x += direction * (125f + 85f * (1f - exp(-score / 25f))) * seconds
        if (x < start - StackTravel || x > start + StackTravel) {
            x = x.coerceIn(start - StackTravel, start + StackTravel)
            direction = -direction
        }
    }
}