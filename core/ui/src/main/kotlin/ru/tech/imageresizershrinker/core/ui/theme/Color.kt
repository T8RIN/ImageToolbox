/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState

fun ColorScheme.outlineVariant(
    luminance: Float = 0.3f,
    onTopOf: Color = surfaceColorAtElevation(3.dp)
) = onSecondaryContainer
    .copy(alpha = luminance)
    .compositeOver(onTopOf)

@Composable
fun takeColorFromScheme(
    action: ColorScheme.() -> Color
) = animateColorAsState(
    MaterialTheme.colorScheme.run(action)
).value


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

@Composable
fun Color.inverse(
    fraction: (Boolean) -> Float = { 0.5f },
    color: (Boolean) -> Color,
    darkMode: Boolean = LocalSettingsState.current.isNightMode,
): Color = if (darkMode) blend(color(true), fraction(true))
else blend(color(true), fraction(false))


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

inline val Green: Color
    @Composable get() = Color(0xFFBADB94).harmonizeWithPrimary(0.2f)

inline val Red: Color
    @Composable get() = Color(0xFFDB9494).harmonizeWithPrimary(0.2f)

inline val Blue: Color
    @Composable get() = Color(0xFF0088CC).harmonizeWithPrimary(0.2f)

inline val Black: Color
    @Composable get() = Color(0xFF142329).harmonizeWithPrimary(0.2f)

inline val StrongBlack: Color
    @Composable get() = Color(0xFF141414).harmonizeWithPrimary(0.07f)

inline val White: Color
    @Composable get() = Color(0xFFFFFFFF).harmonizeWithPrimary(0.07f)

inline val BitcoinColor: Color
    @Composable get() = Color(0xFFF7931A).harmonizeWithPrimary(0.2f)

inline val USDTColor: Color
    @Composable get() = Color(0xFF50AF95).harmonizeWithPrimary(0.2f)

inline val TONSpaceColor: Color
    @Composable get() = Color(0xFF232328).harmonizeWithPrimary(0.2f)

inline val TONColor: Color
    @Composable get() = Color(0xFF0098EA).harmonizeWithPrimary(0.2f)
