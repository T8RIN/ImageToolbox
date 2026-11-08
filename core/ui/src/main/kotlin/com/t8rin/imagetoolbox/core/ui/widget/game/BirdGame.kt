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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import kotlin.random.Random

private const val PipeWidth = 52f
private const val BirdRadiusX = 16f
private const val BirdRadiusY = 12f

private const val BirdOutlinePath =
    "M13 1H27V3H33V6H36V12H38V16H44V19H46V23H44V27H35V29H30V32H14V30H8V27H4V23H1V16H3V11H7V6H13Z"
private const val BirdFillPath =
    "M14 3H27V5H32V8H34V14H36V18H42V21H44V23H42V25H34V27H29V30H14V28H9V25H6V22H3V16H5V12H9V8H14Z"
private const val BirdEyePath = "M25 4H32V6H35V17H32V19H25V17H23V7H25Z"
private const val BirdEyeFill = "M26 6H31V8H33V16H31V17H26V15H25V8H26Z"
private const val BirdPupilPath = "M30 9H33V15H30Z"
private const val BirdBeakPath = "M29 18H43V20H45V23H42V26H30V24H27V21H29Z"
private const val BirdBeakFill = "M30 20H42V22H29V21H30Z M31 24H41V25H31Z"
private const val WingUpPath = "M3 12H13V14H18V18H16V21H7V19H3V17H1V14H3Z"
private const val WingUpFill = "M4 14H12V16H16V18H14V19H8V17H4Z"
private const val WingDownPath = "M3 17H8V19H15V22H18V26H14V29H7V26H4V22H2V19H3Z"
private const val WingDownFill = "M4 19H7V21H13V23H16V25H13V27H8V24H6V21H4Z"
private val BirdBody = ArcadeSprite("BirdBody", 46, 32, BirdOutlinePath)
private val BirdFill = ArcadeSprite("BirdFill", 46, 32, BirdFillPath)
private val BirdEye = ArcadeSprite("BirdEye", 46, 32, BirdEyePath)
private val BirdEyeWhite = ArcadeSprite("BirdEyeWhite", 46, 32, BirdEyeFill)
private val BirdPupil = ArcadeSprite("BirdPupil", 46, 32, BirdPupilPath)
private val BirdBeak = ArcadeSprite("BirdBeak", 46, 32, BirdBeakPath)
private val BirdBeakColor = ArcadeSprite("BirdBeakColor", 46, 32, BirdBeakFill)
private val WingUp = ArcadeSprite("WingUp", 46, 32, WingUpPath)
private val WingUpColor = ArcadeSprite("WingUpColor", 46, 32, WingUpFill)
private val WingDown = ArcadeSprite("WingDown", 46, 32, WingDownPath)
private val WingDownColor = ArcadeSprite("WingDownColor", 46, 32, WingDownFill)

@Composable
fun BirdGame(
    modifier: Modifier = Modifier,
    initialState: ArcadeGameState = ArcadeGameState(),
    style: ArcadeStyle = ArcadeDefaults.style,
    onHighScoreChanged: ((Int) -> Unit)? = null,
    onGameOver: ((Int) -> Unit)? = null
) {
    val run = rememberArcadeRun(initialState, ::BirdRun)
    val ink = if (style.playerColor.luminance() < style.playerDetailColor.luminance()) {
        style.playerColor
    } else style.playerDetailColor
    val eye = if (style.playerColor.luminance() > style.playerDetailColor.luminance()) {
        style.playerColor
    } else style.playerDetailColor

    ArcadeBoard(
        game = ArcadeGame.Bird,
        run = run,
        style = style,
        modifier = modifier,
        initialState = initialState,
        onHighScoreChanged = onHighScoreChanged,
        onGameOver = onGameOver
    ) {
        ArcadeField(run, onPress = { run.flap() }) {
            var cloudX = 40f - run.distance * 0.18f % 690f
            var index = 0
            while (cloudX < run.width) {
                drawArcadeSprite(
                    ArcadeCloud, style.detailColor, cloudX,
                    run.topInset + 25f + index % 3 * (run.ground - run.topInset) * 0.15f
                )
                cloudX += 230f
                index++
            }
            for ((x, center1, gap) in run.pipes) {
                val top = center1 - gap / 2
                val bottom = center1 + gap / 2
                pixelRect(style.obstacleColor, x + 3, 0f, PipeWidth - 6, top - 9)
                pixelRect(style.obstacleColor, x, top - 9, PipeWidth, 9f)
                pixelRect(
                    style.obstacleColor,
                    x + 3,
                    bottom + 9,
                    PipeWidth - 6,
                    run.ground - bottom - 9
                )
                pixelRect(style.obstacleColor, x, bottom, PipeWidth, 9f)
                val highlight = style.buttonContentColor.copy(alpha = 0.3f)
                pixelRect(highlight, x + 7, 0f, 3f, top - 11)
                pixelRect(highlight, x + 4, top - 7, PipeWidth - 8, 2f)
                pixelRect(highlight, x + 7, bottom + 11, 3f, run.ground - bottom - 11)
                pixelRect(highlight, x + 4, bottom + 2, PipeWidth - 8, 2f)
            }
            arcadeGround(style, run.width, run.ground, run.distance)
            val wingUp = run.wingTime % 0.24f < 0.12f
            rotate(run.tilt, Offset(run.birdX, run.y)) {
                val x = run.birdX - 23
                val y = run.y - 16
                drawArcadeSprite(BirdBody, ink, x, y)
                drawArcadeSprite(BirdFill, style.playerColor, x, y)
                drawArcadeSprite(BirdEye, ink, x, y)
                drawArcadeSprite(BirdEyeWhite, eye, x, y)
                drawArcadeSprite(BirdPupil, ink, x, y)
                drawArcadeSprite(if (wingUp) WingUp else WingDown, ink, x, y)
                drawArcadeSprite(
                    if (wingUp) WingUpColor else WingDownColor,
                    style.obstacleColor,
                    x,
                    y
                )
                drawArcadeSprite(BirdBeak, ink, x, y)
                drawArcadeSprite(BirdBeakColor, style.accentColor, x, y)
            }
        }
    }
}

private data class BirdPipe(
    var x: Float,
    var center: Float,
    var gap: Float,
    var passed: Boolean = false
)

private class BirdRun : ArcadeRun() {
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

    override fun reset() {
        y = (topInset + ground) / 2
        distance = 0f
        wingTime = 0f
        tilt = 0f
        velocity = -110f
        pipes.clear()
        pipes += BirdPipe(birdX + 240f, y, gap())
        addPipes()
    }

    fun flap() {
        velocity = -315f
        wingTime = 0f
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

    private fun gap() = minOf(90f, (ground - topInset) * 0.58f)

    private fun addPipes() {
        while (pipes.last().x < width + PipeWidth) {
            val last = pipes.last()
            val gap = gap()
            pipes += BirdPipe(
                x = last.x + 190f,
                center = (last.center + Random.nextFloat() * 240f - 120f)
                    .coerceIn(topInset + gap / 2 + 12f, ground - gap / 2 - 16f),
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
        if (phase == ArcadePhase.Ready) reset()
    }

    override fun save(state: Bundle) {
        state.putFloatArray("bird", floatArrayOf(y, distance, velocity, wingTime, tilt))
        state.putFloatArray("pipes", pipes.flatMap {
            listOf(it.x, it.center, it.gap, if (it.passed) 1f else 0f)
        }.toFloatArray())
    }

    override fun restore(state: Bundle) {
        state.getFloatArray("bird")?.let {
            y = it[0]
            distance = it[1]
            velocity = it[2]
            wingTime = it[3]
            tilt = it[4]
        }
        state.getFloatArray("pipes")?.let { points ->
            pipes.clear()
            for (index in points.indices step 4) {
                pipes += BirdPipe(
                    points[index],
                    points[index + 1],
                    points[index + 2],
                    points[index + 3] == 1f
                )
            }
        }
    }
}