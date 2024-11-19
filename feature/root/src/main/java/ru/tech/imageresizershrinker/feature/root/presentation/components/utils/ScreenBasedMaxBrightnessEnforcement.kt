/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.root.presentation.components.utils

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.util.fastAny
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity

@Composable
internal fun ScreenBasedMaxBrightnessEnforcement(
    currentScreen: Screen?
) {
    val context = LocalComponentActivity.current

    val listToForceBrightness = LocalSettingsState.current.screenListWithMaxBrightnessEnforcement

    DisposableEffect(currentScreen) {
        if (listToForceBrightness.fastAny { it == currentScreen?.id }) {
            context.window.apply {
                attributes.apply {
                    screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                }
                addFlags(WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED)
            }
        }
        onDispose {
            context.window.apply {
                attributes.apply {
                    screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                }
                addFlags(WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED)
            }
        }
    }
}