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

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.provider.rememberAppColorTuple

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ImageToolboxTheme(
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    MaterialTheme(
        motionScheme = CustomMotionScheme
    ) {
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
            content = content
        )
    }
}