package com.cookhelper.dynamic.theme

import android.graphics.Bitmap
import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.cookhelper.dynamic.theme.hct.Hct
import com.cookhelper.dynamic.theme.palettes.TonalPalette
import com.cookhelper.dynamic.theme.scheme.Scheme

/**
 * DynamicTheme allows you to dynamically change the color scheme of the content hierarchy.
 * To do this you just need to update [DynamicThemeState].
 * @param state - current instance of [DynamicThemeState]
 * */
@Composable
public fun DynamicTheme(
    state: DynamicThemeState,
    typography: Typography,
    amoledMode: Boolean,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val scheme = rememberColorScheme(
        amoledMode = amoledMode,
        isDarkTheme = isDarkTheme,
        colorTuple = state.colorTuple.value
    ).animateAllColors(tween(150))
    MaterialTheme(
        typography = typography,
        colorScheme = scheme,
        content = {
            CompositionLocalProvider(
                LocalDynamicThemeState provides state,
                content = content
            )
        }
    )
}

/**
 * This function animates colors when current color scheme changes.
 *
 * @param animationSpec Animation that will be applied when theming option changes.
 * @return [ColorScheme] with animated colors.
 */
@Composable
private fun ColorScheme.animateAllColors(animationSpec: AnimationSpec<Color>): ColorScheme {

    /**
     * Wraps color into [animateColorAsState].
     *
     * @return Animated [Color].
     */
    @Composable
    fun Color.animateColor() = animateColorAsState(this, animationSpec).value

    return this.copy(
        primary = primary.animateColor(),
        onPrimary = onPrimary.animateColor(),
        primaryContainer = primaryContainer.animateColor(),
        onPrimaryContainer = onPrimaryContainer.animateColor(),
        inversePrimary = inversePrimary.animateColor(),
        secondary = secondary.animateColor(),
        onSecondary = onSecondary.animateColor(),
        secondaryContainer = secondaryContainer.animateColor(),
        onSecondaryContainer = onSecondaryContainer.animateColor(),
        tertiary = tertiary.animateColor(),
        onTertiary = onTertiary.animateColor(),
        tertiaryContainer = tertiaryContainer.animateColor(),
        onTertiaryContainer = onTertiaryContainer.animateColor(),
        background = background.animateColor(),
        onBackground = onBackground.animateColor(),
        surface = surface.animateColor(),
        onSurface = onSurface.animateColor(),
        surfaceVariant = surfaceVariant.animateColor(),
        onSurfaceVariant = onSurfaceVariant.animateColor(),
        surfaceTint = surfaceTint.animateColor(),
        inverseSurface = inverseSurface.animateColor(),
        inverseOnSurface = inverseOnSurface.animateColor(),
        error = error.animateColor(),
        onError = onError.animateColor(),
        errorContainer = errorContainer.animateColor(),
        onErrorContainer = onErrorContainer.animateColor(),
        outline = outline.animateColor(),
    )
}


public fun Bitmap.extractPrimaryColor(default: Int = 0, blendWithVibrant: Boolean = true): Color {
    fun Int.blend(
        color: Int,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f
    ): Int = ColorUtils.blendARGB(this, color, fraction)

    val palette = Palette
        .from(this)
        .generate()

    return Color(
        palette.getDominantColor(default).run {
            if (blendWithVibrant) blend(palette.getVibrantColor(default))
            else this
        }
    )
}

public data class ColorTuple(
    val primary: Color,
    val secondary: Color?,
    val tertiary: Color?
)

/**
 * Creates and remember [DynamicThemeState] instance
 * */
@Composable
public fun rememberDynamicThemeState(
    initialColorTuple: ColorTuple = ColorTuple(
        primary = MaterialTheme.colorScheme.primary,
        secondary = MaterialTheme.colorScheme.secondary,
        tertiary = MaterialTheme.colorScheme.tertiary,
    )
): DynamicThemeState {
    return remember {
        DynamicThemeState(initialColorTuple)
    }
}

@Stable
public class DynamicThemeState(
    initialColorTuple: ColorTuple
) {
    public val colorTuple: MutableState<ColorTuple> = mutableStateOf(initialColorTuple)

    public fun updateColor(color: Color) {
        colorTuple.value = ColorTuple(primary = color, secondary = null, tertiary = null)
    }

    public fun updateColorTuple(newColorTuple: ColorTuple) {
        colorTuple.value = newColorTuple
    }

    public fun updateColorByImage(bitmap: Bitmap) {
        updateColor(bitmap.extractPrimaryColor())
    }
}

@Composable
public fun rememberColorScheme(
    isDarkTheme: Boolean,
    amoledMode: Boolean,
    colorTuple: ColorTuple
): ColorScheme {
    return remember(colorTuple, isDarkTheme, amoledMode) {
        if (isDarkTheme) {
            Scheme.darkContent(colorTuple.primary.toArgb()).toDarkThemeColorScheme(colorTuple)
        } else {
            Scheme.lightContent(colorTuple.primary.toArgb()).toLightThemeColorScheme(colorTuple)
        }.let {
            if (amoledMode && isDarkTheme) {
                it.copy(background = Color.Black, surface = Color.Black)
            } else it
        }.run {
            copy(
                outlineVariant = onSecondaryContainer
                    .copy(alpha = 0.2f)
                    .compositeOver(surfaceColorAtElevation(6.dp))
            )
        }
    }
}

private fun Scheme.toDarkThemeColorScheme(
    colorTuple: ColorTuple
): ColorScheme {
    val hct = Hct.fromInt(colorTuple.primary.toArgb())
    val hue = hct.hue
    val chroma = hct.chroma

    val a2 = colorTuple.secondary?.toArgb().let {
        if (it != null) {
            TonalPalette.fromInt(it)
        } else {
            TonalPalette.fromHueAndChroma(hue, chroma / 3.0)
        }
    }
    val a3 = colorTuple.tertiary?.toArgb().let {
        if (it != null) {
            TonalPalette.fromInt(it)
        } else {
            TonalPalette.fromHueAndChroma(hue + 60.0, chroma / 2.0)
        }
    }

    return darkColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(a2?.tone(80) ?: secondary),
        onSecondary = Color(a2?.tone(20) ?: onSecondary),
        secondaryContainer = Color(a2?.tone(30) ?: secondaryContainer),
        onSecondaryContainer = Color(a2?.tone(90) ?: onSecondaryContainer),
        tertiary = Color(a3?.tone(80) ?: tertiary),
        onTertiary = Color(a3?.tone(20) ?: onTertiary),
        tertiaryContainer = Color(a3?.tone(30) ?: tertiaryContainer),
        onTertiaryContainer = Color(a3?.tone(90) ?: onTertiaryContainer),
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

private fun Scheme.toLightThemeColorScheme(
    colorTuple: ColorTuple
): ColorScheme {
    val hct = Hct.fromInt(colorTuple.primary.toArgb())
    val hue = hct.hue
    val chroma = hct.chroma

    val a2 = colorTuple.secondary?.toArgb().let {
        if (it != null) {
            TonalPalette.fromInt(it)
        } else {
            TonalPalette.fromHueAndChroma(hue, chroma / 3.0)
        }
    }
    val a3 = colorTuple.tertiary?.toArgb().let {
        if (it != null) {
            TonalPalette.fromInt(it)
        } else {
            TonalPalette.fromHueAndChroma(hue + 60.0, chroma / 2.0)
        }
    }

    return lightColorScheme(
        primary = Color(primary),
        onPrimary = Color(onPrimary),
        primaryContainer = Color(primaryContainer),
        onPrimaryContainer = Color(onPrimaryContainer),
        inversePrimary = Color(inversePrimary),
        secondary = Color(a2?.tone(40) ?: secondary),
        onSecondary = Color(a2?.tone(100) ?: onSecondary),
        secondaryContainer = Color(a2?.tone(90) ?: secondaryContainer),
        onSecondaryContainer = Color(a2?.tone(10) ?: onSecondaryContainer),
        tertiary = Color(a3?.tone(40) ?: tertiary),
        onTertiary = Color(a3?.tone(100) ?: onTertiary),
        tertiaryContainer = Color(a3?.tone(90) ?: tertiaryContainer),
        onTertiaryContainer = Color(a3?.tone(10) ?: onTertiaryContainer),
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