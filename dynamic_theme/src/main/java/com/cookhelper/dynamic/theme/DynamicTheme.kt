package com.cookhelper.dynamic.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import material.util.color.scheme.Scheme
import kotlin.math.abs

/**
 * DynamicTheme allows you to dynamically change the color scheme of the content hierarchy.
 * To do this you just need to update [DynamicThemeState].
 * @param state - current instance of [DynamicThemeState]
 * */
@Composable
public fun DynamicTheme(
    state: DynamicThemeState,
    isDarkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val scheme = rememberColorScheme(
        isDarkTheme = isDarkTheme,
        color = state.primaryColor
    )
    MaterialTheme(
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
    val context = LocalContext.current
    return remember { DynamicThemeState(initialPrimaryColor, context) }
}

/**
 * Creates and remember [DynamicThemeState] instance
 * */
@Composable
public fun rememberDynamicThemeState(
    initialPrimaryColor: Int,
): DynamicThemeState {
    return rememberDynamicThemeState(Color(initialPrimaryColor))
}

@Stable
public class DynamicThemeState(
    initialPrimaryColor: Color,
    private val context: Context,
) {

    public var primaryColor: Color by mutableStateOf(initialPrimaryColor)

    public var primaryColorArgb: Int
        set(value) {
            primaryColor = Color(value)
        }
        get() = primaryColor.toArgb()

    public suspend fun updateColorByImage(@DrawableRes imageRes: Int) {
        withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeResource(context.resources, imageRes)
            updateColorByImage(bitmap)
        }
    }

    public fun updateColorByImage(bitmap: Bitmap) {
        val palette = Palette.from(bitmap).generate()
        palette.dominantSwatch?.rgb?.let { primaryColor = Color(it) }
    }
}

@Composable
public fun rememberColorScheme(isDarkTheme: Boolean, color: Color): ColorScheme {
    val colorArgb = color.toArgb()
    return remember(color, isDarkTheme) {
        if (isDarkTheme) {
            Scheme.darkContent(colorArgb).toDarkThemeColorScheme()
        } else {
            Scheme.lightContent(colorArgb).toLightThemeColorScheme()
        }
    }
}


@Composable
public fun rememberColorScheme(isDarkTheme: Boolean, bitmap: Bitmap): ColorScheme? {
    val palette = Palette.from(bitmap).generate()
    fun Int.blend(
        color: Int,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.5f
    ): Int = ColorUtils.blendARGB(this, color, fraction)

    return remember(bitmap, isDarkTheme) {
        palette.getDominantColor(Color.Transparent.toArgb())
            .blend(palette.getVibrantColor(Color.Transparent.toArgb()))
            .let { Color(it).toArgb() }
            .let { colorArgb ->
                if (isDarkTheme) {
                    Scheme.darkContent(colorArgb).toDarkThemeColorScheme()
                } else {
                    Scheme.lightContent(colorArgb).toLightThemeColorScheme()
                }
            }.takeIf {
                val (r, g, b) = it.primaryContainer.run { Triple(red, green, blue) }
                val (r1, g1, b1) = it.tertiaryContainer.run { Triple(red, green, blue) }
                abs(r - r1) >= 0.01 && abs(b - b1) >= 0.01 && abs(g - g1) >= 0.01
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