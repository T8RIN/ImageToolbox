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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Raven
import kotlinx.serialization.Serializable
import kotlin.random.Random

@RegisterArcadeGame
internal data object WingDashGame : ArcadeGameDefinition {
    override val title = R.string.arcade_wingdash
    override val description = R.string.arcade_wingdash_sub
    override val icon get() = Icons.Outlined.Raven
    override val order = 2

    @Composable
    override fun Content(context: ArcadeGameContext, modifier: Modifier) {
        val style = context.style
        val run = rememberArcadeRun(
            startPlaying = context.initialState.isPlaying,
            create = ::WingDashRun
        )
        val ink = style.playerDetailColor
        val eye = style.textColor

        ArcadeBoard(
            context = context,
            run = run,
            modifier = modifier
        ) {
            ArcadeField(
                run = run,
                onPress = { run.flap() }
            ) {
                drawFlappyBackground(run, style)
                for ((x, center1, gap) in run.pipes) {
                    val top = center1 - gap / 2
                    val bottom = center1 + gap / 2
                    pixelRect(
                        color = style.obstacleColor,
                        x = x + 3,
                        y = 0f,
                        width = PipeWidth - 6,
                        height = top - 9
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = x,
                        y = top - 9,
                        width = PipeWidth,
                        height = 9f
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = x + 3,
                        y = bottom + 9,
                        width = PipeWidth - 6,
                        height = run.ground - bottom - 9
                    )
                    pixelRect(
                        color = style.obstacleColor,
                        x = x,
                        y = bottom,
                        width = PipeWidth,
                        height = 9f
                    )
                    val highlight = style.buttonContentColor.copy(alpha = 0.3f)
                    pixelRect(
                        color = highlight,
                        x = x + 7,
                        y = 0f,
                        width = 3f,
                        height = top - 11
                    )
                    pixelRect(
                        color = highlight,
                        x = x + 4,
                        y = top - 7,
                        width = PipeWidth - 8,
                        height = 2f
                    )
                    val shade = lerp(style.obstacleColor, style.playerDetailColor, 0.28f)
                    pixelRect(shade, x + PipeWidth - 9f, 0f, 3f, top - 9f)
                    pixelRect(shade, x + PipeWidth - 9f, bottom + 9f, 3f, run.ground - bottom - 9f)
                    pixelRect(shade, x + 2f, top - 3f, PipeWidth - 4f, 2f)
                    pixelRect(shade, x + 2f, bottom + 6f, PipeWidth - 4f, 2f)
                    pixelRect(
                        color = highlight,
                        x = x + 7,
                        y = bottom + 11,
                        width = 3f,
                        height = run.ground - bottom - 11
                    )
                    pixelRect(
                        color = highlight,
                        x = x + 4,
                        y = bottom + 2,
                        width = PipeWidth - 8,
                        height = 2f
                    )
                }
                drawFlappyGround(run, style)
                val wingUp = run.wingTime % 0.24f < 0.12f
                rotate(degrees = run.tilt, pivot = Offset(x = run.birdX, y = run.y)) {
                    val x = run.birdX - 23
                    val y = run.y - 16
                    drawArcadeSprite(
                        sprite = BirdBody,
                        tint = ink,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = BirdFill,
                        tint = style.playerColor,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = BirdEye,
                        tint = ink,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = BirdEyeGlint,
                        tint = eye,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = if (wingUp) WingUp else WingDown,
                        tint = ink,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = if (wingUp) WingUpColor else WingDownColor,
                        tint = style.obstacleColor,
                        x = x,
                        y = y
                    )
                    drawArcadeSprite(
                        sprite = BirdBeak,
                        tint = style.accentColor,
                        x = x,
                        y = y
                    )
                }
            }
        }
    }
}

private const val PipeWidth = 52f
private const val BirdRadiusX = 16f
private const val BirdRadiusY = 12f

private val BirdBody = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M28 4H32V6H36V8H38V12H40V14H44V18H40V22H36V26H30V28H18V26H14V24H8V26H2V22H6V18H4V14H10V16H16V14H20V12H24V8H28Z"
)
private val BirdFill = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M28 8H34V10H36V14H38V20H34V24H28V26H20V24H16V22H10V20H8V18H12V20H18V16H22V14H26V10H28Z"
)
private val BirdEye = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M32 10H36V14H32Z"
)
private val BirdEyeGlint = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M34 10H36V12H34Z"
)
private val BirdBeak = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M38 14H42V15H44V17H40V18H38Z"
)
private val WingUp = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M10 2H16V4H20V8H24V12H28V18H26V22H20V20H16V16H12V10H10Z"
)
private val WingUpColor = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M12 4H15V6H18V10H22V14H26V18H24V20H21V18H18V14H14V8H12Z"
)
private val WingDown = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M16 16H26V18H28V24H24V28H20V30H14V26H12V20H16Z"
)
private val WingDownColor = ArcadeSprite(
    width = 46,
    height = 32,
    data = "M17 18H24V20H26V22H22V26H18V28H16V24H14V22H17Z"
)

private val FlappyCloud = ArcadeSprite(
    width = 42, height = 19,
    data = "M0 12H3V8H8V4H14V1H24V4H30V8H35V11H39V14H42V19H0Z"
)

private fun DrawScope.drawFlappyBackground(run: WingDashRun, style: ArcadeStyle) {
    var cloudX = -(run.distance * 0.08f % 173f) - 42f
    var index = 0
    while (cloudX < run.width) {
        val y = run.height * (0.12f + index % 3 * 0.1f)
        drawArcadeSprite(FlappyCloud, style.detailColor.copy(alpha = 0.15f), cloudX, y)
        pixelRect(style.detailColor.copy(alpha = 0.11f), cloudX + 2f, y + 16f, 38f, 3f)
        cloudX += 173f
        index++
    }
    var x = -(run.distance * 0.035f % 106f) - 20f
    index = 0
    while (x < run.width) {
        val height = 17f + index % 4 * 7f
        val y = run.ground - height
        pixelRect(style.detailColor.copy(alpha = 0.07f), x, y, 23f, height)
        pixelRect(style.detailColor.copy(alpha = 0.13f), x + 2f, y - 2f, 19f, 2f)
        for (row in 0..2) for (column in 0..2) {
            pixelRect(
                style.detailColor.copy(alpha = 0.12f), x + 4f + column * 6f,
                y + 5f + row * 7f, 2f, 3f
            )
        }
        x += 35f
        index++
    }

}

private fun DrawScope.drawFlappyGround(run: WingDashRun, style: ArcadeStyle) {
    val y = run.ground
    pixelRect(style.detailColor.copy(alpha = 0.12f), 0f, y, run.width, run.height - y)
    pixelRect(style.obstacleColor.copy(alpha = 0.85f), 0f, y, run.width, 2f)
    pixelRect(style.playerDetailColor.copy(alpha = 0.25f), 0f, y + 5f, run.width, 1f)
    var x = -(run.distance % 12f).toInt().toFloat()
    while (x < run.width) {
        pixelRect(style.obstacleColor.copy(alpha = 0.45f), x, y + 2f, 5f, 2f)
        pixelRect(style.obstacleColor.copy(alpha = 0.35f), x + 3f, y + 4f, 5f, 1f)
        pixelRect(style.detailColor.copy(alpha = 0.14f), x + 7f, y + 10f, 1f, 5f)
        x += 12f
    }
}

@Serializable
private data class BirdPipe(
    var x: Float,
    var center: Float,
    var gap: Float,
    var passed: Boolean = false
)

@Serializable
private class WingDashRun : ArcadeRun() {
    val birdX get() = (width * 0.25f).coerceIn(72f, 150f)
    val ground get() = height - 20f
    var y = height / 2
        private set
    var distance = 0f
        private set
    var wingTime = 0f
        private set
    var tilt = 0f
        private set
    val pipes = mutableListOf<BirdPipe>()
    private var velocity = 0f

    override fun isValid(): Boolean = super.isValid() &&
            pipes.isNotEmpty() && pipes.all { it.gap > BirdRadiusY * 2 }

    override fun reset() {
        y = (topInset + ground) / 2
        distance = 0f
        wingTime = 0f
        tilt = 0f
        velocity = -110f
        pipes.clear()
        pipes += BirdPipe(x = birdX + 240f, center = y, gap = gap())
        addPipes()
    }

    fun flap() {
        velocity = -315f
        wingTime = 0f
        tilt = -18f
    }

    override fun advance(seconds: Float) {
        velocity = (velocity + 1080f * seconds).coerceAtMost(460f)
        y += velocity * seconds
        wingTime += seconds
        tilt += ((velocity / 7f).coerceIn(-18f, 65f) - tilt) * minOf(1f, seconds * 10f)
        val step = 185f * seconds
        distance += step
        pipes.forEach { pipe ->
            pipe.x -= step
            if (!pipe.passed && pipe.x + PipeWidth < birdX - BirdRadiusX) {
                pipe.passed = true
                score++
            }
        }
        addPipes()
        pipes.removeAll { it.x + PipeWidth < 0f }
        if (y < BirdRadiusY || y + BirdRadiusY > ground || pipes.any {
                birdX + BirdRadiusX > it.x && birdX - BirdRadiusX < it.x + PipeWidth &&
                        (y - BirdRadiusY < it.center - it.gap / 2 || y + BirdRadiusY > it.center + it.gap / 2)
            }
        ) phase = ArcadePhase.Over
    }

    private fun gap() = minOf(92f, (ground - topInset) * 0.58f)

    private fun addPipes() {
        while (pipes.last().x < width + PipeWidth) {
            val last = pipes.last()
            val gap = gap()
            val low = maxOf(last.center - 120f, topInset + gap / 2 + 12f)
            val high = minOf(last.center + 120f, ground - gap / 2 - 16f)
            pipes += BirdPipe(
                x = last.x + 190f,
                center = low + Random.nextFloat() * (high - low),
                gap = gap
            )
        }
    }

    override fun onResize(previous: Size) {
        val oldX = (previous.width * 0.25f).coerceIn(72f, 150f)
        val activePipe = pipes.firstOrNull {
            oldX + BirdRadiusX > it.x && oldX - BirdRadiusX < it.x + PipeWidth
        }
        val offset = activePipe?.let { (y - it.center) / it.gap }
        y = (y / previous.height * height).coerceIn(BirdRadiusY, ground - BirdRadiusY)
        pipes.forEach {
            it.x += birdX - oldX
            it.gap = minOf(it.gap, gap())
            it.center = (it.center / previous.height * height)
                .coerceIn(topInset + it.gap / 2 + 4f, ground - it.gap / 2 - 4f)
        }
        if (activePipe != null && offset != null) {
            val clearance = activePipe.gap / 2 - BirdRadiusY - 1f
            y = activePipe.center + (offset * activePipe.gap).coerceIn(-clearance, clearance)
        }
    }
}