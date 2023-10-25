package ru.tech.imageresizershrinker.presentation.root.theme

import androidx.compose.runtime.Composable
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

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
            getAppColorTuple(
                defaultColorTuple = settingsState.appColorTuple,
                dynamicColor = dynamicColor,
                darkTheme = settingsState.isNightMode
            )
        ),
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = dynamicColor,
        amoledMode = amoledMode,
        isDarkTheme = settingsState.isNightMode,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        content = content
    )
}