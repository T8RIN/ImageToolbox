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

import androidx.compose.runtime.Composable
import com.t8rin.dynamic.theme.ColorBlindType
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState

@Composable
fun ImageToolboxTheme(
    dynamicColor: Boolean = LocalSettingsState.current.isDynamicColors,
    amoledMode: Boolean = LocalSettingsState.current.isAmoledMode,
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    DynamicTheme(
        typography = Typography(settingsState.font),
        state = rememberDynamicThemeState(
            rememberAppColorTuple(
                defaultColorTuple = settingsState.appColorTuple,
                dynamicColor = dynamicColor,
                darkTheme = settingsState.isNightMode
            )
        ),
        colorBlindType = ColorBlindType.Tritanomaly,
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = dynamicColor,
        amoledMode = amoledMode,
        isDarkTheme = settingsState.isNightMode,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        isInvertColors = settingsState.isInvertThemeColors,
        content = content
    )
}