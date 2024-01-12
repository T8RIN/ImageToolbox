package ru.tech.imageresizershrinker.core.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.t8rin.dynamic.theme.ColorTuple
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

val md_theme_dark_primary = Color(0xFF8FDB3A)

fun ColorScheme.outlineVariant(
    luminance: Float = 0.3f,
    onTopOf: Color = surfaceColorAtElevation(3.dp)
) = onSecondaryContainer
    .copy(alpha = luminance)
    .compositeOver(onTopOf)


fun ColorScheme.suggestContainerColorBy(color: Color) = when (color) {
    onPrimary -> primary
    onSecondary -> secondary
    onTertiary -> tertiary
    onPrimaryContainer -> primaryContainer
    onSecondaryContainer -> secondaryContainer
    onTertiaryContainer -> tertiaryContainer
    onError -> error
    onErrorContainer -> errorContainer
    onSurfaceVariant -> surfaceVariant
    inverseOnSurface -> inverseSurface
    else -> surface
}

inline val ColorScheme.mixedContainer: Color
    @Composable get() = run {
        tertiaryContainer.blend(
            primaryContainer,
            0.4f
        )
    }

inline val ColorScheme.onMixedContainer: Color
    @Composable get() = run {
        onTertiaryContainer.blend(
            onPrimaryContainer,
            0.4f
        )
    }

fun Color.blend(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
): Color = Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))

@Composable
fun Color.inverse(
    fraction: (Boolean) -> Float = { 0.5f },
    darkMode: Boolean = LocalSettingsState.current.isNightMode,
): Color = if (darkMode) blend(Color.White, fraction(true))
else blend(Color.Black, fraction(false))


fun Int.blend(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
): Int = ColorUtils.blendARGB(this, color.toArgb(), fraction)

@Composable
fun Color.harmonizeWithPrimary(
    @FloatRange(
        from = 0.0,
        to = 1.0
    ) fraction: Float = 0.2f
): Color = blend(MaterialTheme.colorScheme.primary, fraction)


fun Int.toColor() = Color(this)

val defaultColorTuple = ColorTuple(md_theme_dark_primary)

inline val Green: Color
    @Composable get() = Color(0xFFBADB94).harmonizeWithPrimary(0.2f)

inline val GreenContrast: Color
    @Composable get() = Color(0xFF7FC232).harmonizeWithPrimary(0.2f)

inline val RedContrast: Color
    @Composable get() = Color(0xFFCF4B4B).harmonizeWithPrimary(0.2f)


inline val Blue: Color
    @Composable get() = Color(0xFF0088CC).harmonizeWithPrimary(0.2f)

inline val Black: Color
    @Composable get() = Color(0xFF142329).harmonizeWithPrimary(0.2f)

inline val White: Color
    @Composable get() = Color(0xFFFFFFFF).harmonizeWithPrimary(0.05f)

inline val BitcoinColor: Color
    @Composable get() = Color(0xFFF7931A).harmonizeWithPrimary(0.2f)

inline val USDTColor: Color
    @Composable get() = Color(0xFF50AF95).harmonizeWithPrimary(0.2f)

inline val TONSpaceColor: Color
    @Composable get() = Color(0xFF232328).harmonizeWithPrimary(0.2f)

inline val TONColor: Color
    @Composable get() = Color(0xFF0098EA).harmonizeWithPrimary(0.2f)
