package ru.tech.imageresizershrinker.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import ru.tech.imageresizershrinker.main_screen.components.LocalAmoledMode
import ru.tech.imageresizershrinker.main_screen.components.LocalAppColorTuple
import ru.tech.imageresizershrinker.main_screen.components.LocalDynamicColors
import ru.tech.imageresizershrinker.main_screen.components.LocalNightMode
import ru.tech.imageresizershrinker.main_screen.components.isNightMode
import kotlin.math.min

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
        density = LocalDensity.current.run {
            Density(this.density, min(fontScale, 1f))
        },
        defaultColorTuple = LocalAppColorTuple.current,
        dynamicColor = dynamicColor,
        amoledMode = amoledMode,
        isDarkTheme = LocalNightMode.current.isNightMode(),
        content = content
    )
}