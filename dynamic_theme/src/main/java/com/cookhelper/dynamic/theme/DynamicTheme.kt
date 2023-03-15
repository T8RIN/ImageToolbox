package com.cookhelper.dynamic.theme

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.cookhelper.dynamic.theme.scheme.Scheme

/**
 * DynamicTheme allows you to dynamically change the color scheme of the content hierarchy.
 * To do this you just need to update [DynamicThemeState].
 * @param state - current instance of [DynamicThemeState]
 * */
@Composable
public fun DynamicTheme(
    state: DynamicThemeState,
    typography: androidx.compose.material3.Typography,
    amoledMode: Boolean,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val scheme = rememberColorScheme(
        amoledMode = amoledMode,
        isDarkTheme = isDarkTheme,
        color = state.animatedPrimaryColor.value
    )
    MaterialTheme(
        typography = typography,
        colorScheme = scheme,
        content = content
    )
}

/**
 * Creates and remember [DynamicThemeState] instance
 * */
@Composable
public fun rememberDynamicThemeState(
    initialPrimaryColor: Color = MaterialTheme.colorScheme.primary,
): DynamicThemeState {
    return remember {
        DynamicThemeState(initialPrimaryColor)
    }
}

/**
 * Creates and remember [DynamicThemeState] instance
 * */
@Composable
public fun rememberDynamicThemeState(
    initialPrimaryColor: Int
): DynamicThemeState {
    return rememberDynamicThemeState(Color(initialPrimaryColor))
}

@Stable
public class DynamicThemeState(
    initialPrimaryColor: Color
) {
    public val primaryColor: MutableState<Color> = mutableStateOf(initialPrimaryColor)

    public inline val animatedPrimaryColor: State<Color>
        @Composable get() = animateColorAsState(primaryColor.value)

    public fun updateColor(color: Color) {
        primaryColor.value = color
    }

    public var primaryColorArgb: Int
        set(value) {
            primaryColor.value = Color(value)
        }
        get() = primaryColor.value.toArgb()

    public fun updateColorByImage(bitmap: Bitmap) {
        val palette = Palette.from(bitmap).generate()
        fun Int.blend(
            color: Int,
            @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f
        ): Int = ColorUtils.blendARGB(this, color, fraction)

        palette.getDominantColor(Color.Transparent.toArgb())
            .blend(palette.getVibrantColor(Color.Transparent.toArgb()))
            .let { Color(it).toArgb() }
            .let {
                primaryColor.value = Color(it)
            }
    }

}

@Composable
public fun rememberColorScheme(
    isDarkTheme: Boolean,
    amoledMode: Boolean,
    color: Color
): ColorScheme {
    val colorArgb = color.toArgb()
    return remember(color, isDarkTheme, amoledMode) {
        if (isDarkTheme) {
            Scheme.darkContent(colorArgb).toDarkThemeColorScheme()
        } else {
            Scheme.lightContent(colorArgb).toLightThemeColorScheme()
        }.let {
            if (amoledMode && isDarkTheme) {
                it.copy(background = Color.Black, surface = Color.Black)
            } else it
        }
    }
}

private fun Scheme.toDarkThemeColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        surfaceTint = Color(primary),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim),
    )
}

private fun Scheme.toLightThemeColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(secondary),
        onSecondary = Color(onSecondary),
        secondaryContainer = Color(secondaryContainer),
        onSecondaryContainer = Color(onSecondaryContainer),
        tertiary = Color(tertiary),
        onTertiary = Color(onTertiary),
        tertiaryContainer = Color(tertiaryContainer),
        onTertiaryContainer = Color(onTertiaryContainer),
        background = Color(background),
        onBackground = Color(onBackground),
        surface = Color(surface),
        onSurface = Color(onSurface),
        surfaceVariant = Color(surfaceVariant),
        onSurfaceVariant = Color(onSurfaceVariant),
        surfaceTint = Color(primary),
        inverseSurface = Color(inverseSurface),
        inverseOnSurface = Color(inverseOnSurface),
        error = Color(error),
        onError = Color(onError),
        errorContainer = Color(errorContainer),
        onErrorContainer = Color(onErrorContainer),
        outline = Color(outline),
        outlineVariant = Color(outlineVariant),
        scrim = Color(scrim),
    )
}

public val LocalDynamicThemeState: ProvidableCompositionLocal<DynamicThemeState> =
    compositionLocalOf { error("Not present") }