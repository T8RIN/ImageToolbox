package ru.tech.imageresizershrinker.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.utils.LocalSettingsState
import kotlin.math.min

@Composable
fun ImageResizerTheme(
    dynamicColor: Boolean = LocalSettingsState.current.isDynamicColors,
    amoledMode: Boolean = LocalSettingsState.current.isAmoledMode,
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    DynamicTheme(
        typography = Typography,
        state = rememberDynamicThemeState(
            getAppColorTuple(
                defaultColorTuple = settingsState.appColorTuple,
                dynamicColor = dynamicColor,
                darkTheme = settingsState.isNightMode
            )
        ),
        density = LocalDensity.current.run {
            Density(this.density, min(fontScale, 1f))
        },
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = dynamicColor,
        amoledMode = amoledMode,
        isDarkTheme = settingsState.isNightMode,
        content = content
    )
}