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

package com.t8rin.imagetoolbox.core.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.defaultColorTuple
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.ui.utils.animation.FancyTransitionEasing
import com.t8rin.imagetoolbox.core.ui.utils.helper.DeviceInfo

@SuppressLint("NewApi")
@Composable
fun ImageToolboxTheme(
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current

    DynamicTheme(
        typography = rememberTypography(settingsState.font),
        state = rememberDynamicThemeState(rememberAppColorTuple()),
        colorBlindType = settingsState.colorBlindType,
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        dynamicColorsOverride = { isNightMode ->
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.BAKLAVA && DeviceInfo.isPixel()) {
                val colors = if (isNightMode) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }

                ColorTuple(
                    primary = colors.primary,
                    secondary = colors.secondary,
                    tertiary = colors.tertiary,
                    surface = colors.surface
                )
            } else null
        },
        amoledMode = settingsState.isAmoledMode,
        isDarkTheme = settingsState.isNightMode,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        isInvertColors = settingsState.isInvertThemeColors,
        colorAnimationSpec = tween(
            durationMillis = 400,
            easing = FancyTransitionEasing
        ),
        content = {
            MaterialTheme(
                motionScheme = CustomMotionScheme,
                colorScheme = modifiedColorScheme(),
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

@Composable
fun ImageToolboxThemeForPreview(
    isDarkTheme: Boolean,
    keyColor: Color? = defaultColorTuple.primary,
    content: @Composable () -> Unit
) {
    DynamicTheme(
        state = rememberDynamicThemeState(
            initialColorTuple = ColorTuple(keyColor ?: Color.Transparent)
        ),
        dynamicColor = keyColor == null,
        isDarkTheme = isDarkTheme,
        defaultColorTuple = ColorTuple(keyColor ?: Color.Transparent),
        colorAnimationSpec = snap(),
        content = {
            CompositionLocalProvider(
                LocalSettingsState provides SettingsState.Default.toUiState()
            ) {
                MaterialTheme(
                    motionScheme = CustomMotionScheme,
                    colorScheme = modifiedColorScheme(),
                    content = content
                )
            }
        }
    )
}

@Composable
private fun modifiedColorScheme(): ColorScheme {
    val scheme = MaterialTheme.colorScheme

    return remember(scheme) {
        derivedStateOf {
            scheme.copy(
                errorContainer = scheme.errorContainer.blend(
                    color = scheme.primary,
                    fraction = 0.15f
                )
            )
        }
    }.value
}