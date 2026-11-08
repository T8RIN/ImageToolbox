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

import android.graphics.Bitmap
import android.os.SystemClock
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class ArcadeRenderingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun renderEveryNewGameInCompactLightDarkAndTallFields() {
        val games = ArcadeGame.playable.drop(8)
        val sample = mutableIntStateOf(0)
        compose.mainClock.autoAdvance = false
        compose.setContent {
            val index = sample.intValue
            val game = games[index % games.size]
            val variant = index / games.size
            ImageToolboxThemeForPreview(isDarkTheme = variant != 0) {
                CompositionLocalProvider(LocalInspectionMode provides true) {
                    key(index) {
                        Column(Modifier.width(360.dp).statusBarsPadding().padding(12.dp)) {
                            Text(
                                stringResource(game.title),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            PlayingGame(game, if (variant == 2) 620 else 280)
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
        for (index in 0 until games.size * 3) {
            compose.runOnIdle { sample.intValue = index }
            compose.mainClock.advanceTimeBy(1600L)
            compose.waitForIdle()
            if (capture) {
                instrumentation.waitForIdleSync()
                // Wait for the emulator compositor to present the new Canvas frame.
                SystemClock.sleep(200L)
                val bitmap = instrumentation.uiAutomation.takeScreenshot()
                assertTrue(bitmap != null && bitmap.width > 0 && bitmap.height > 0)
                File(
                    directory,
                    "${index / games.size}-${games[index % games.size].name}.png"
                ).outputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                bitmap.recycle()
            }
        }
    }
}

@Composable
private fun PlayingGame(game: ArcadeGame, height: Int) {
    val style = ArcadeDefaults.style.copy(gameHeight = height.dp)
    val initial = ArcadeGameState(isPlaying = true)
    val modifier = Modifier.fillMaxWidth()
    when (game) {
        ArcadeGame.Racer -> RacerGame(modifier, initial, style)
        ArcadeGame.Ricochet -> RicochetGame(modifier, initial, style)
        ArcadeGame.Asteroids -> AsteroidsGame(modifier, initial, style)
        ArcadeGame.FruitSlice -> FruitSliceGame(modifier, initial, style)
        ArcadeGame.KnifeHit -> KnifeHitGame(modifier, initial, style)
        ArcadeGame.MissileCommand -> MissileCommandGame(modifier, initial, style)
        ArcadeGame.RiverHop -> RiverHopGame(modifier, initial, style)
        ArcadeGame.WhackAMole -> WhackAMoleGame(modifier, initial, style)
        ArcadeGame.SimonSays -> SimonSaysGame(modifier, initial, style)
        ArcadeGame.BubblePop -> BubblePopGame(modifier, initial, style)
        ArcadeGame.Fishing -> FishingGame(modifier, initial, style)
        ArcadeGame.Hexagon -> HexagonGame(modifier, initial, style)
        else -> error("Expected a revised game")
    }
}