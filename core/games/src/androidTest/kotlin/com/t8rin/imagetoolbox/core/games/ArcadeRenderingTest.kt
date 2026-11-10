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

package com.t8rin.imagetoolbox.core.games

import android.graphics.Bitmap
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ArcadeRenderingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun renderEveryGameInCompactLightDarkAndTallFields() {
        val wide = InstrumentationRegistry.getArguments().getString("arcadeWide") == "true"
        val variants = if (wide) 1 else 3
        val requested = InstrumentationRegistry.getArguments().getString("arcadeGames")?.split(',')
        val games = ArcadeGameRegistry.games.filter { requested == null || it.name in requested }
        assertTrue(games.isNotEmpty())
        var fieldBounds = Rect.Zero
        val sample = mutableIntStateOf(0)
        compose.mainClock.autoAdvance = false
        compose.setContent {
            val index = sample.intValue
            val game = games[index % games.size]
            val variant = index / games.size
            ImageToolboxThemeForPreview(isDarkTheme = wide || variant != 0) {
                CompositionLocalProvider(LocalInspectionMode provides true) {
                    key(index) {
                        Column(
                            Modifier
                                .width(if (wide) 680.dp else 360.dp)
                                .statusBarsPadding()
                                .padding(12.dp)
                        ) {
                            Text(
                                stringResource(game.title),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            PlayingGame(
                                game,
                                if (wide) 220 else if (variant == 2) 620 else 280
                            ) { fieldBounds = it }
                        }
                    }
                }
            }
        }
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val capture =
            InstrumentationRegistry.getArguments().getString("arcadeScreenshots") == "true"
        val directory =
            File(instrumentation.targetContext.getExternalFilesDir(null), "arcade-previews")
        if (capture) directory.mkdirs()
        for (index in 0 until games.size * variants) {
            compose.runOnIdle { sample.intValue = index }
            compose.mainClock.advanceTimeBy(
                if (games[index % games.size] in ArcadeGameRegistry.games.take(
                        8
                    )
                ) 300L else 1600L
            )
            compose.waitForIdle()
            var downTime = 0L
            var heldPoint: Offset? = null
            fun send(action: Int, point: Offset) {
                val event = MotionEvent.obtain(
                    downTime,
                    SystemClock.uptimeMillis(),
                    action,
                    point.x,
                    point.y,
                    0
                )
                event.source = InputDevice.SOURCE_TOUCHSCREEN
                instrumentation.uiAutomation.injectInputEvent(event, true)
                event.recycle()
            }

            fun touch(point: Offset, duration: Long) {
                downTime = SystemClock.uptimeMillis()
                send(MotionEvent.ACTION_DOWN, point)
                compose.mainClock.advanceTimeBy(duration)
                send(MotionEvent.ACTION_UP, point)
            }
            when (games[index % games.size].name) {
                "Minesweeper" -> {
                    touch(
                        fieldBounds.topLeft + Offset(
                            fieldBounds.width * 0.16f,
                            fieldBounds.height * 0.27f
                        ), 650L
                    )
                    touch(fieldBounds.center + Offset(0f, fieldBounds.height * 0.05f), 32L)
                    compose.mainClock.advanceTimeBy(500L)
                }

                "Bomber" -> {
                    touch(fieldBounds.center, 32L)
                    val from = fieldBounds.center
                    val to = from + Offset(fieldBounds.width * 0.2f, 0f)
                    downTime = SystemClock.uptimeMillis()
                    send(MotionEvent.ACTION_DOWN, from)
                    send(MotionEvent.ACTION_MOVE, to)
                    compose.mainClock.advanceTimeBy(180L)
                    send(MotionEvent.ACTION_UP, to)
                    compose.mainClock.advanceTimeBy(80L)
                }

                "TimberChop" -> {
                    val point = fieldBounds.center + Offset(fieldBounds.width * 0.25f, 0f)
                    touch(point, 32L)
                    compose.mainClock.advanceTimeBy(160L)
                    touch(point, 32L)
                    compose.mainClock.advanceTimeBy(48L)
                }

                "GravityFlip" -> {
                    touch(fieldBounds.center, 32L)
                    compose.mainClock.advanceTimeBy(450L)
                }

                "MoonLanding" -> {
                    val from = fieldBounds.topLeft + Offset(
                        fieldBounds.width * 0.5f,
                        fieldBounds.height * 0.7f
                    )
                    val to = from + Offset(0f, -fieldBounds.height * 0.025f)
                    downTime = SystemClock.uptimeMillis()
                    send(MotionEvent.ACTION_DOWN, from)
                    send(MotionEvent.ACTION_MOVE, to)
                    compose.mainClock.advanceTimeBy(400L)
                    heldPoint = to
                }

                "BalanceTower" -> {
                    var elapsed = 1.6
                    repeat(3) { count ->
                        val speed = 0.7 + count.toDouble() / (count + 35)
                        val crossing = kotlin.math.ceil(elapsed * speed / Math.PI) * Math.PI / speed
                        compose.mainClock.advanceTimeBy(((crossing - elapsed) * 1000).toLong())
                        touch(fieldBounds.center, 32L)
                        compose.mainClock.advanceTimeBy(1200L)
                        elapsed = crossing + 1.232
                    }
                }

                "Basketball" -> {
                    val scale = minOf(fieldBounds.width / 360f, fieldBounds.height / 220f)
                    val from = fieldBounds.center
                    val to = from + Offset(-38f, 78f) * scale
                    downTime = SystemClock.uptimeMillis()
                    send(MotionEvent.ACTION_DOWN, from)
                    send(MotionEvent.ACTION_MOVE, to)
                    compose.mainClock.advanceTimeBy(120L)
                    heldPoint = to
                }

                "AirTraffic" -> {
                    compose.mainClock.advanceTimeBy(7200L)
                }

                else -> Unit
            }
            compose.waitForIdle()
            if (capture) {
                instrumentation.waitForIdleSync()
                // Wait for the emulator compositor to present the new Canvas frame.
                SystemClock.sleep(200L)
                val bitmap = instrumentation.uiAutomation.takeScreenshot()
                assertTrue(bitmap != null && bitmap.width > 0 && bitmap.height > 0)
                File(
                    directory,
                    "${if (wide) "wide" else index / games.size}-${games[index % games.size].name}.png"
                ).outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmap.recycle()
            }
            heldPoint?.let { send(MotionEvent.ACTION_UP, it) }
            if (capture && games[index % games.size].name == "MoonLanding") {
                compose.mainClock.advanceTimeBy(3600L)
                compose.waitForIdle()
                instrumentation.waitForIdleSync()
                SystemClock.sleep(200L)
                val bitmap = instrumentation.uiAutomation.takeScreenshot()
                File(
                    directory,
                    "approach-${if (wide) "wide" else index / games.size}-MoonLanding.png"
                )
                    .outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
                bitmap.recycle()
            }
        }
    }
}

@Composable
private fun PlayingGame(game: ArcadeGame, height: Int, onBounds: (Rect) -> Unit) {
    ArcadeGameContent(
        game = game,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { onBounds(it.boundsInWindow()) },
        height = height.dp,
        initialState = ArcadeGameState(isPlaying = true)
    )
}