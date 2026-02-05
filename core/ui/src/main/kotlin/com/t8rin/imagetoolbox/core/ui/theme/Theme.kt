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
import androidx.compose.material3.Shapes
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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.model.defaultColorTuple
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.presentation.provider.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.ui.utils.animation.FancyTransitionEasing
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import com.t8rin.imagetoolbox.core.ui.utils.helper.DeviceInfo
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.initAppContext
import java.util.Locale

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
                shapes = modifiedShapes(),
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
    shapesType: ShapeType = ShapeType.Rounded(),
    content: @Composable () -> Unit
) {
    LocalContext.current.applicationContext.initAppContext()

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
                LocalSettingsState provides SettingsState.Default.toUiState().copy(
                    shapesType = shapesType
                ),
                LocalSimpleSettingsInteractor provides object : SimpleSettingsInteractor {
                    override suspend fun toggleMagnifierEnabled() = Unit
                    override suspend fun setOneTimeSaveLocations(value: List<OneTimeSaveLocation>) =
                        Unit

                    override suspend fun toggleRecentColor(
                        color: ColorModel,
                        forceExclude: Boolean
                    ) = Unit

                    override suspend fun toggleFavoriteColor(
                        color: ColorModel,
                        forceExclude: Boolean
                    ) = Unit

                    override fun isInstalledFromPlayStore(): Boolean = false

                    override suspend fun toggleSettingsGroupVisibility(
                        key: Int,
                        value: Boolean
                    ) = Unit

                    override suspend fun clearRecentColors() = Unit

                    override suspend fun updateFavoriteColors(colors: List<ColorModel>) = Unit

                    override suspend fun setBackgroundColorForNoAlphaFormats(color: ColorModel) =
                        Unit

                    override suspend fun toggleCustomAsciiGradient(gradient: String) = Unit

                    override suspend fun toggleOverwriteFiles() = Unit

                    override suspend fun setSpotHealMode(mode: Int) = Unit
                    override suspend fun setBorderWidth(width: Float) = Unit
                },
                LocalResourceManager provides object : ResourceManager {
                    override fun getString(resId: Int): String = appContext.getString(resId)

                    override fun getString(
                        resId: Int,
                        vararg formatArgs: Any
                    ): String = appContext.getString(resId, formatArgs)

                    override fun getStringLocalized(
                        resId: Int,
                        language: String
                    ): String =
                        appContext.getStringLocalized(resId, Locale.forLanguageTag(language))

                    override fun getStringLocalized(
                        resId: Int,
                        language: String,
                        vararg formatArgs: Any
                    ): String =
                        appContext.getStringLocalized(resId, Locale.forLanguageTag(language))

                }
            ) {
                MaterialTheme(
                    motionScheme = CustomMotionScheme,
                    colorScheme = modifiedColorScheme(),
                    shapes = modifiedShapes(),
                    content = {
                        Surface {
                            content()
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun modifiedShapes(): Shapes {
    val shapes = MaterialTheme.shapes
    val shapesType = LocalSettingsState.current.shapesType

    return remember(shapes, shapesType) {
        derivedStateOf {
            shapes.copy(
                extraSmall = AutoCornersShape(
                    topStart = shapes.extraSmall.topStart,
                    topEnd = shapes.extraSmall.topEnd,
                    bottomEnd = shapes.extraSmall.bottomEnd,
                    bottomStart = shapes.extraSmall.bottomStart,
                    shapesType = shapesType
                ),
                small = AutoCornersShape(
                    topStart = shapes.small.topStart,
                    topEnd = shapes.small.topEnd,
                    bottomEnd = shapes.small.bottomEnd,
                    bottomStart = shapes.small.bottomStart,
                    shapesType = shapesType
                ),
                medium = AutoCornersShape(
                    topStart = shapes.medium.topStart,
                    topEnd = shapes.medium.topEnd,
                    bottomEnd = shapes.medium.bottomEnd,
                    bottomStart = shapes.medium.bottomStart,
                    shapesType = shapesType
                ),
                large = AutoCornersShape(
                    topStart = shapes.large.topStart,
                    topEnd = shapes.large.topEnd,
                    bottomEnd = shapes.large.bottomEnd,
                    bottomStart = shapes.large.bottomStart,
                    shapesType = shapesType
                ),
                extraLarge = AutoCornersShape(
                    topStart = shapes.extraLarge.topStart,
                    topEnd = shapes.extraLarge.topEnd,
                    bottomEnd = shapes.extraLarge.bottomEnd,
                    bottomStart = shapes.extraLarge.bottomStart,
                    shapesType = shapesType
                ),
                largeIncreased = AutoCornersShape(
                    topStart = shapes.largeIncreased.topStart,
                    topEnd = shapes.largeIncreased.topEnd,
                    bottomEnd = shapes.largeIncreased.bottomEnd,
                    bottomStart = shapes.largeIncreased.bottomStart,
                    shapesType = shapesType
                ),
                extraLargeIncreased = AutoCornersShape(
                    topStart = shapes.extraLargeIncreased.topStart,
                    topEnd = shapes.extraLargeIncreased.topEnd,
                    bottomEnd = shapes.extraLargeIncreased.bottomEnd,
                    bottomStart = shapes.extraLargeIncreased.bottomStart,
                    shapesType = shapesType
                ),
                extraExtraLarge = AutoCornersShape(
                    topStart = shapes.extraExtraLarge.topStart,
                    topEnd = shapes.extraExtraLarge.topEnd,
                    bottomEnd = shapes.extraExtraLarge.bottomEnd,
                    bottomStart = shapes.extraExtraLarge.bottomStart,
                    shapesType = shapesType
                )
            )
        }
    }.value
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

const val DisabledAlpha = 0.38f