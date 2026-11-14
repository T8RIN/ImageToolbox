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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import com.t8rin.imagetoolbox.core.games.ArcadeBoard
import com.t8rin.imagetoolbox.core.games.ArcadeDeque
import com.t8rin.imagetoolbox.core.games.ArcadeField
import com.t8rin.imagetoolbox.core.games.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.games.ArcadeRun
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.RegisterArcadeGame
import com.t8rin.imagetoolbox.core.games.rememberArcadeRun
import com.t8rin.imagetoolbox.core.games.utils.ArcadeEffects
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngle
import com.t8rin.imagetoolbox.core.games.utils.arcadeAngleDistance
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Laps
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@RegisterArcadeGame
internal data object SpiralShaftGame : ArcadeGameDefinition {
    override val title = R.string.arcade_spiralshaft
    override val description = R.string.arcade_spiralshaft_sub
    override val icon get() = Icons.Outlined.Laps
    override val order = 36

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::SpiralShaftRun
        )
        val path = remember { Path() }

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                lightFeedback = true,
                onPress = { run.grab(it.x) },
                onDrag = { run.spin(it.x) },
                onRelease = run::stopInput
            ) {
                drawShaftBackground(style, run.width, run.height, run.camera * run.unit * 0.12f)
                translate(left = run.width / 2f, top = run.anchor) {
                    scale(scale = run.unit, pivot = Offset.Zero) {
                        val top = -run.anchor / run.unit
                        val bottom = (run.height - run.anchor) / run.unit
                        val platforms = run.platforms.asReversed()
                        for (platform in platforms) {
                            val y = run.project(0f, platform.y, 0f).y
                            if (y in top - 52f..bottom + 52f) {
                                shaftPlatform(
                                    run = run,
                                    style = style,
                                    path = path,
                                    platform = platform,
                                    front = false
                                )
                            }
                        }
                        drawShaft(
                            run = run,
                            style = style,
                            path = path,
                            top = top,
                            bottom = bottom
                        )

                        var ballDrawn = false
                        for (platform in platforms) {
                            val y = run.project(0f, platform.y, 0f).y
                            if (y !in top - 52f..bottom + 52f) continue
                            if (!ballDrawn && run.ball > platform.y) {
                                shaftBall(run, style)
                                ballDrawn = true
                            }
                            shaftPlatform(
                                run = run,
                                style = style,
                                path = path,
                                platform = platform,
                                front = true
                            )
                        }
                        if (!ballDrawn) shaftBall(run, style)
                        translate(
                            top = run.project(
                                0f,
                                run.camera,
                                ShaftTrackRadius
                            ).y - run.camera
                        ) {
                            run.effects.draw(
                                scope = this,
                                color = style.accentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val ShaftOuterRadius = 83f
private const val ShaftInnerRadius = 17f
private const val ShaftTrackRadius = 69f
private const val ShaftThickness = 9f
private const val ShaftRadians = 0.017453293f
private const val ShaftBallRadius = 8f
private const val ShaftSpacing = 84f
private const val ShaftCombo = 3
private const val ShaftStageSize = 11
private const val ShaftShatterTime = 0.46f

private fun DrawScope.drawShaft(
    run: SpiralShaftRun,
    style: ArcadeStyle,
    path: Path,
    top: Float,
    bottom: Float
) {
    val radius = ShaftInnerRadius - 1f
    // At a fixed depth the projected cylinder sides are straight lines.
    val upper = run.camera + top * 1.7f
    val lower = run.camera + bottom * 2.4f
    val leftTop = run.project(-radius, upper, 0f)
    val rightTop = run.project(radius, upper, 0f)
    val rightBottom = run.project(radius, lower, 0f)
    val leftBottom = run.project(-radius, lower, 0f)
    path.rewind()
    path.moveTo(leftTop.x, leftTop.y)
    path.lineTo(rightTop.x, rightTop.y)
    path.lineTo(rightBottom.x, rightBottom.y)
    path.lineTo(leftBottom.x, leftBottom.y)
    path.close()
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            0f to lerp(style.obstacleColor, style.detailColor, 0.32f),
            0.36f to lerp(style.obstacleColor, Color.White, 0.27f),
            0.60f to style.obstacleColor,
            1f to lerp(style.obstacleColor, Color.Black, 0.48f),
            startX = -radius,
            endX = radius
        )
    )
}

private fun DrawScope.shaftPlatform(
    run: SpiralShaftRun,
    style: ArcadeStyle,
    path: Path,
    platform: ShaftPlatform,
    front: Boolean
) {
    val start = arcadeAngle(platform.gap + run.rotation + platform.turn + platform.opening / 2f)
    val end = start + 360f - platform.opening
    val danger = start + platform.dangerOffset
    val color = when {
        platform.checkpoint -> style.accentColor
        platform.passed -> style.obstacleColor
        platform.stage % 3 == 1 -> lerp(style.playerColor, style.accentColor, 0.55f)
        platform.stage % 3 == 2 -> lerp(style.playerColor, style.obstacleColor, 0.58f)
        else -> style.playerColor
    }
    if (platform.shatter >= 0f) {
        if (front && platform.shatter < ShaftShatterTime) {
            shaftShards(run, path, color, platform, start, end)
        }
        return
    }
    val y = platform.y
    for (half in 0..3) {
        if ((half % 2 == 0) != front) continue
        val first = maxOf(start, half * 180f)
        val last = minOf(end, (half + 1) * 180f)
        if (first >= last) continue
        shaftWedge(run, path, color, y, first, last, front)
        shaftWedge(
            run = run,
            path = path,
            color = style.hazardColor,
            y = y,
            first = maxOf(first, danger),
            last = minOf(last, danger + platform.dangerWidth),
            front = front
        )
    }
    if (platform.opening > 0f) for (edgeIndex in 0..1) {
        val isStart = edgeIndex == 0
        val edge = if (isStart) start else end
        val angle = edge * ShaftRadians
        if ((sin(angle) >= 0f) != front || (cos(angle) < 0f) != isStart) continue
        val inner = run.projectRim(ShaftInnerRadius, y, edge)
        val outer = run.projectRim(ShaftOuterRadius, y, edge)
        val lowerOuter = run.projectRim(ShaftOuterRadius, y + ShaftThickness, edge)
        val lowerInner = run.projectRim(ShaftInnerRadius, y + ShaftThickness, edge)
        path.rewind()
        path.moveTo(inner.x, inner.y)
        path.lineTo(outer.x, outer.y)
        path.lineTo(lowerOuter.x, lowerOuter.y)
        path.lineTo(lowerInner.x, lowerInner.y)
        path.close()
        drawPath(path, lerp(color, Color.Black, 0.41f))
        drawLine(lerp(color, Color.White, 0.32f), inner, outer, 0.8f)
    }

    val distance = y - run.ball - ShaftBallRadius
    val relative = arcadeAngle(90f - start)
    if (front && distance in 0f..46f && relative in 9f..end - start - 9f) {
        val shadow = run.project(0f, y + 0.1f, ShaftTrackRadius)
        val radius = 12f + distance * 0.09f
        drawOval(
            brush = Brush.radialGradient(
                listOf(Color.Black.copy(alpha = 0.30f * (1f - distance / 46f)), Color.Transparent),
                center = shadow,
                radius = radius
            ),
            topLeft = shadow - Offset(radius, radius * 0.4f),
            size = Size(radius * 2f, radius * 0.8f)
        )
    }
}

private fun DrawScope.shaftShards(
    run: SpiralShaftRun,
    path: Path,
    color: Color,
    platform: ShaftPlatform,
    start: Float,
    end: Float
) {
    val time = platform.shatter
    val opacity = 1f - time / ShaftShatterTime
    repeat(8) { index ->
        val angle = start + (end - start) * (index + 0.5f) / 8f
        val spread = time * (82f + index % 3 * 33f)
        val y = platform.y - time * (54f + index % 4 * 17f) + time * time * 470f
        val swing = time * (if (index % 2 == 0) 78f else -78f)
        val a = run.projectRim(37f + spread, y, angle + swing - 6f)
        val b = run.projectRim(ShaftOuterRadius + spread, y, angle + swing - 9f)
        val c = run.projectRim(ShaftOuterRadius + spread, y + time * 22f, angle + swing + 8f)
        val d = run.projectRim(55f + spread, y + time * 16f, angle + swing + 4f)
        path.rewind()
        path.moveTo(a.x, a.y)
        path.lineTo(b.x, b.y)
        path.lineTo(c.x, c.y)
        path.lineTo(d.x, d.y)
        path.close()
        drawPath(path, lerp(color, Color.White, index % 3 * 0.12f).copy(alpha = opacity))
    }
}

private fun DrawScope.shaftWedge(
    run: SpiralShaftRun,
    path: Path,
    color: Color,
    y: Float,
    first: Float,
    last: Float,
    front: Boolean
) {
    if (first >= last) return
    val center = run.project(0f, y, 0f)
    val steps = ceil((last - first) / 7f).toInt()
    val rim = if (front) ShaftOuterRadius else ShaftInnerRadius
    path.rewind()
    run.traceRim(path, rim, y, first, last, steps, move = true)
    run.traceRim(path, rim, y + ShaftThickness, last, first, steps, move = false)
    path.close()
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            0f to lerp(color, Color.Black, if (front) 0.30f else 0.51f),
            1f to lerp(color, Color.Black, 0.57f),
            startX = -ShaftOuterRadius,
            endX = ShaftOuterRadius
        )
    )
    path.rewind()
    run.traceRim(path, ShaftOuterRadius, y, first, last, steps, move = true)
    run.traceRim(path, ShaftInnerRadius, y, last, first, steps, move = false)
    path.close()
    val surface = Brush.linearGradient(
        listOf(
            lerp(color, Color.White, 0.28f),
            lerp(color, Color.Black, 0.16f)
        ),
        start = center - Offset(ShaftOuterRadius, 23f),
        end = center + Offset(ShaftOuterRadius, 32f)
    )
    drawPath(path, surface)
    drawPath(path, surface, style = Stroke(width = 0.45f))
}

private fun SpiralShaftRun.traceRim(
    path: Path,
    radius: Float,
    y: Float,
    first: Float,
    last: Float,
    steps: Int,
    move: Boolean
) {
    for (index in 0..steps) {
        val point = projectRim(radius, y, first + (last - first) * index / steps)
        if (index == 0 && move) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
}

private fun DrawScope.shaftBall(run: SpiralShaftRun, style: ArcadeStyle) {
    val center = run.project(0f, run.ball, ShaftTrackRadius)
    val radius = run.project(ShaftBallRadius, run.ball, ShaftTrackRadius).x
    val boosted = run.smashes > 0
    val color =
        if (boosted) lerp(style.accentColor, Color(0xFFFF9D43), 0.55f) else style.playerColor
    if (run.velocity > 210f) {
        val count = if (boosted) 6 else 3
        for (index in count downTo 1) {
            drawCircle(
                color = color.copy(alpha = (if (boosted) 0.46f else 0.27f) * (1f - index / (count + 1f))),
                radius = radius * (1f - index * 0.11f),
                center = run.project(
                    0f,
                    run.ball - index * run.velocity / (if (boosted) 65f else 43f),
                    ShaftTrackRadius
                )
            )
        }
    }
    val squash = radius * run.impact * 0.25f
    drawOval(
        brush = Brush.radialGradient(
            0f to lerp(color, Color.White, 0.80f),
            0.40f to lerp(color, Color.White, 0.23f),
            0.76f to color,
            1f to lerp(color, Color.Black, 0.42f),
            center = center - Offset(radius * 0.28f, radius * 0.32f),
            radius = radius * 1.34f
        ),
        topLeft = center - Offset(radius + squash, radius - squash),
        size = Size((radius + squash) * 2f, (radius - squash) * 2f)
    )
}

private fun DrawScope.drawShaftBackground(
    style: ArcadeStyle,
    width: Float,
    height: Float,
    drift: Float
) {
    val columns = ceil(width / 179f).toInt() + 1
    val rows = ceil(height / 149f).toInt() + 1
    for (row in 0 until rows) for (column in 0 until columns) {
        val span = height + 149f
        val x = column * 179f + row % 2 * 61f - 7f
        val y = (row * 149f + 36f - drift % span + span) % span - 38f
        val radius = 13f + (row + column) % 3 * 6f
        val depth = radius * 0.5f
        val upper = Path().apply {
            moveTo(x - radius, y)
            lineTo(x - radius * 0.25f, y - radius * 0.62f)
            lineTo(x + radius, y - radius * 0.17f)
            lineTo(x + radius * 0.35f, y + radius * 0.5f)
            close()
        }
        drawPath(upper, style.detailColor.copy(alpha = 0.12f))
        val near = Path().apply {
            moveTo(x - radius, y)
            lineTo(x + radius * 0.35f, y + radius * 0.5f)
            lineTo(x + radius * 0.35f, y + radius * 0.5f + depth)
            lineTo(x - radius, y + depth)
            close()
        }
        drawPath(near, style.detailColor.copy(alpha = 0.055f))
        val far = Path().apply {
            moveTo(x + radius * 0.35f, y + radius * 0.5f)
            lineTo(x + radius, y - radius * 0.17f)
            lineTo(x + radius, y - radius * 0.17f + depth)
            lineTo(x + radius * 0.35f, y + radius * 0.5f + depth)
            close()
        }
        drawPath(far, style.detailColor.copy(alpha = 0.085f))
    }
}

@Serializable
private class ShaftPlatform(
    val y: Float,
    val gap: Float,
    val opening: Float,
    val dangerOffset: Float,
    val dangerWidth: Float,
    var passed: Boolean = false,
    val stage: Int = 0,
    val checkpoint: Boolean = false,
    val spinSpeed: Float = 0f,
    var turn: Float = 0f,
    var shatter: Float = -1f
)

@Serializable
private class SpiralShaftRun : ArcadeRun() {
    val platforms: ArcadeDeque<ShaftPlatform> = ArrayDeque()
    var ball = -46f
        private set
    var velocity = 0f
        private set
    var rotation = 0f
        private set
    var camera = 0f
        private set
    private var combo = 0
    var smashes = 0
        private set
    var impact = 0f
        private set
    private var checkpointPause = 0f
    private var generated = 0

    @Transient
    private var dragX: Float? = null

    @Transient
    val effects = ArcadeEffects()

    val unit: Float
        get() = minOf(width / 262f, (height - topInset) / 180f)
    val anchor: Float
        get() = topInset + (height - topInset) * 0.30f

    fun project(x: Float, y: Float, z: Float): Offset {
        val relativeY = y - camera
        val depth = z * 0.937f - relativeY * 0.35f
        val perspective = 490f / (490f - depth).coerceAtLeast(120f)
        return Offset(x * perspective, (relativeY * 0.937f + z * 0.35f) * perspective)
    }

    fun projectRim(radius: Float, y: Float, degrees: Float): Offset {
        val angle = degrees * ShaftRadians
        return project(radius * cos(angle), y, radius * sin(angle))
    }

    override fun reset() {
        ball = -46f
        velocity = 0f
        rotation = 0f
        camera = 0f
        combo = 0
        smashes = 0
        impact = 0f
        checkpointPause = 0f
        generated = 0
        effects.clear()
        platforms.clear()
        fillPlatforms()
    }

    override fun stopInput() {
        dragX = null
    }

    fun grab(x: Float) {
        dragX = x
    }

    fun spin(x: Float) {
        val previous = dragX ?: return
        rotation = arcadeAngle(rotation + (x - previous) * 1.55f / unit)
        dragX = x
    }

    override fun advance(seconds: Float) {
        effects.advance(seconds)
        impact = (impact - seconds * 6.5f).coerceAtLeast(0f)
        for (platform in platforms) {
            if (platform.shatter in 0f..ShaftShatterTime) platform.shatter += seconds
            if (!platform.passed && platform.spinSpeed != 0f) {
                platform.turn = arcadeAngle(platform.turn + platform.spinSpeed * seconds)
            }
        }
        val previous = ball
        if (checkpointPause > 0f) {
            checkpointPause -= seconds
        } else {
            velocity = (velocity + 890f * seconds).coerceAtMost(if (smashes > 0) 720f else 600f)
        }
        ball += velocity * seconds
        if (velocity > 0f) {
            val platform =
                platforms.firstOrNull { !it.passed && it.y - ShaftBallRadius >= previous }
            if (platform != null && ball + ShaftBallRadius >= platform.y) {
                land(platform)
            }
        }
        camera += (maxOf(camera, ball - 16f) - camera) * minOf(1f, 13f * seconds)
        while (platforms.first().y < camera - anchor / unit - 38f) {
            platforms.removeFirst()
        }
        fillPlatforms()
    }

    private fun land(platform: ShaftPlatform) {
        if (platform.checkpoint) {
            platform.passed = true
            platform.shatter = 0f
            ball = platform.y - ShaftBallRadius
            velocity = 0f
            checkpointPause = 0.26f
            impact = 1f
            combo = 0
            smashes = 0
            score += 20
            effects.burst(Offset(0f, platform.y), 24)
            return
        }
        val contact = arcadeAngle(90f - rotation - platform.turn)
        val inGap = arcadeAngleDistance(contact, platform.gap) < platform.opening / 2f - 6f
        if (inGap) {
            platform.passed = true
            if (combo < ShaftCombo) {
                combo++
                if (combo == ShaftCombo) smashes = 2
            }
            score += combo
            effects.burst(Offset(0f, platform.y), if (smashes > 0) 9 else 5)
            return
        }
        if (smashes > 0) {
            platform.passed = true
            platform.shatter = 0f
            smashes--
            if (smashes == 0) combo = 0
            score += 6
            effects.burst(Offset(0f, platform.y), 18)
            return
        }

        ball = platform.y - ShaftBallRadius
        val start = platform.gap + platform.opening / 2f + platform.dangerOffset
        val hazard = platform.dangerWidth > 0f &&
                arcadeAngleDistance(contact, start + platform.dangerWidth / 2f) <
                platform.dangerWidth / 2f + 5f
        if (hazard) {
            effects.burst(Offset(0f, ball), 18)
            finish()
        } else {
            combo = 0
            impact = 1f
            velocity = -236f
        }
    }

    private fun fillPlatforms() {
        val bottom = camera + maxOf(6f * ShaftSpacing, (height - anchor) / unit + ShaftSpacing)
        while (platforms.isEmpty() || platforms.last().y < bottom) {
            val index = generated++
            val stage = index / ShaftStageSize
            val section = index % ShaftStageSize
            val checkpoint = section == ShaftStageSize - 1
            val difficulty = index.toFloat() / (index + 46f)
            val previous = platforms.lastOrNull()
            val gap = when {
                previous == null -> 36f
                section in 1..2 -> previous.gap + Random.nextFloat() * 26f - 13f
                stage % 4 == 1 -> previous.gap + (28f + Random.nextFloat() * 22f) * if (stage % 2 == 0) 1f else -1f
                stage % 4 == 2 -> previous.gap + (if (section % 2 == 0) 54f else -54f)
                stage % 4 == 3 && section % 3 != 0 -> previous.gap + Random.nextFloat() * 20f - 10f
                section in 3..4 -> previous.gap + Random.nextFloat() * 114f - 57f
                else -> previous.gap + Random.nextFloat() * 196f - 98f
            }
            val opening =
                if (checkpoint) 0f else 102f - difficulty * 48f + Random.nextFloat() * 10f +
                        if (stage % 4 == 3 && section % 3 == 0) 14f else 0f
            val danger = if (checkpoint || index < 3 || section == 5) {
                0f
            } else {
                31f + 86f * difficulty
            }
            val safe = 360f - opening - danger
            val spacing = when {
                checkpoint -> 105f
                section in 1..3 -> 72f
                else -> ShaftSpacing + Random.nextFloat() * 14f
            }
            val spin = if (stage > 0 && section in 4..8 && section % 2 == stage % 2) {
                (19f + difficulty * 37f) * if (section % 3 == 0) -1f else 1f
            } else {
                0f
            }
            platforms.addLast(
                ShaftPlatform(
                    y = previous?.let { it.y + spacing } ?: 0f,
                    gap = arcadeAngle(gap),
                    opening = opening,
                    dangerOffset = 18f + Random.nextFloat() * (safe - 36f),
                    dangerWidth = danger,
                    stage = stage,
                    checkpoint = checkpoint,
                    spinSpeed = spin
                )
            )
        }
    }
}