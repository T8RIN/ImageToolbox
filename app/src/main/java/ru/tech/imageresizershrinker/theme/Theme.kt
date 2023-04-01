package ru.tech.imageresizershrinker.theme

import androidx.compose.runtime.Composable
import com.cookhelper.dynamic.theme.DynamicTheme
import com.cookhelper.dynamic.theme.getAppColorTuple
import com.cookhelper.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.main_screen.components.*

@Composable
fun ImageResizerTheme(
    dynamicColor: Boolean = LocalDynamicColors.current,
    amoledMode: Boolean = LocalAmoledMode.current,
    content: @Composable () -> Unit
) {
    DynamicTheme(
        typography = Typography,
        state = rememberDynamicThemeState(
            getAppColorTuple(
                defaultColorTuple = LocalAppColorTuple.current,
                dynamicColor = dynamicColor,
                darkTheme = LocalNightMode.current.isNightMode()
            )
        ),
        defaultColorTuple = LocalAppColorTuple.current,
        dynamicColor = dynamicColor,
        amoledMode = amoledMode,
        isDarkTheme = LocalNightMode.current.isNightMode(),
        content = content
    )
}