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

package ru.tech.imageresizershrinker.core.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberAppColorTuple

@Composable
fun ImageToolboxTheme(
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    DynamicTheme(
        typography = Typography(settingsState.font),
        state = rememberDynamicThemeState(rememberAppColorTuple()),
        colorBlindType = settingsState.colorBlindType,
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        amoledMode = settingsState.isAmoledMode,
        isDarkTheme = settingsState.isNightMode,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        isInvertColors = settingsState.isInvertThemeColors,
        content = {
            MaterialTheme(
                motionScheme = CustomMotionScheme,
                colorScheme = MaterialTheme.colorScheme.copy(
                    errorContainer = MaterialTheme.colorScheme.errorContainer.blend(
                        color = MaterialTheme.colorScheme.primary,
                        fraction = 0.15f
                    )
                ),
                content = content
            )
        }
    )
}

@Composable
fun ImageToolboxThemeSurface(
    content: @Composable BoxScope.() -> Unit
) {
    ImageToolboxTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            content = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    content = content
                )
            }
        )
    }
}