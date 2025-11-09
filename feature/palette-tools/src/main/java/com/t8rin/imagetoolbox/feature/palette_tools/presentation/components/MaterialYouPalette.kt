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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.getColorScheme
import com.t8rin.dynamic.theme.rememberColorScheme
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Cube
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import kotlinx.coroutines.delay

@Composable
internal fun MaterialYouPalette(
    keyColor: Color,
    paletteStyle: PaletteStyle,
    isDarkTheme: Boolean,
    isInvertColors: Boolean,
    contrastLevel: Float
) {
    val colorScheme = rememberColorScheme(
        isDarkTheme = isDarkTheme,
        amoledMode = false,
        colorTuple = ColorTuple(keyColor),
        style = paletteStyle,
        contrastLevel = contrastLevel.toDouble(),
        dynamicColor = false,
        isInvertColors = isInvertColors
    )
    val context = LocalContext.current
    val essentials = rememberLocalEssentials()

    val themeState = LocalDynamicThemeState.current
    LaunchedEffect(colorScheme.primary) {
        delay(200L)
        themeState.updateColorTuple(
            ColorTuple(
                primary = colorScheme.primary,
                secondary = colorScheme.secondary,
                tertiary = colorScheme.tertiary,
                surface = colorScheme.surface
            )
        )
    }

    MaterialYouPaletteGroup(
        colorScheme = colorScheme,
        onCopy = { color ->
            essentials.copyToClipboard(
                text = color.toHex(),
                message = R.string.color_copied
            )
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    EnhancedButton(
        onClick = {
            val light = context.getColorScheme(
                isDarkTheme = false,
                amoledMode = false,
                colorTuple = ColorTuple(keyColor),
                style = paletteStyle,
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = false,
                isInvertColors = isInvertColors
            ).asCodeString(false)

            val dark = context.getColorScheme(
                isDarkTheme = true,
                amoledMode = false,
                colorTuple = ColorTuple(keyColor),
                style = paletteStyle,
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = false,
                isInvertColors = isInvertColors
            ).asCodeString(true)

            essentials.copyToClipboard(light + "\n\n" + dark)
        },
        containerColor = MaterialTheme.colorScheme.tertiary
    ) {
        Icon(
            imageVector = Icons.Outlined.Cube,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.copy_as_compose_code))
    }

}

private fun ColorScheme.asCodeString(
    isDarkTheme: Boolean
): String = """
val ${isDarkTheme.schemeName} = ColorScheme(
    background = ${background.colorCode},
    error = ${error.colorCode},
    errorContainer = ${errorContainer.colorCode},
    inverseOnSurface = ${inverseOnSurface.colorCode},
    inversePrimary = ${inversePrimary.colorCode},
    inverseSurface = ${inverseSurface.colorCode},
    onBackground = ${onBackground.colorCode},
    onError = ${onError.colorCode},
    onErrorContainer = ${onErrorContainer.colorCode},
    onPrimary = ${onPrimary.colorCode},
    onPrimaryContainer = ${onPrimaryContainer.colorCode},
    onSecondary = ${onSecondary.colorCode},
    onSecondaryContainer = ${onSecondaryContainer.colorCode},
    onSurface = ${onSurface.colorCode},
    onSurfaceVariant = ${onSurfaceVariant.colorCode},
    onTertiary = ${onTertiary.colorCode},
    onTertiaryContainer = ${onTertiaryContainer.colorCode},
    outline = ${outline.colorCode},
    outlineVariant = ${outlineVariant.colorCode},
    primary = ${primary.colorCode},
    primaryContainer = ${primaryContainer.colorCode},
    scrim = ${scrim.colorCode},
    secondary = ${secondary.colorCode},
    secondaryContainer = ${secondaryContainer.colorCode},
    surface = ${surface.colorCode},
    surfaceTint = ${surfaceTint.colorCode},
    surfaceVariant = ${surfaceVariant.colorCode},
    tertiary = ${tertiary.colorCode},
    tertiaryContainer = ${tertiaryContainer.colorCode},
    surfaceBright = ${surfaceBright.colorCode},
    surfaceDim = ${surfaceDim.colorCode},
    surfaceContainer = ${surfaceContainer.colorCode},
    surfaceContainerHigh = ${surfaceContainerHigh.colorCode},
    surfaceContainerHighest = ${surfaceContainerHighest.colorCode},
    surfaceContainerLow = ${surfaceContainerLow.colorCode},
    surfaceContainerLowest = ${surfaceContainerLowest.colorCode},
)
""".trim()

private val Boolean.schemeName: String
    get() = "${if (this) "dark" else "light"}ColorScheme"

private val Color.colorCode
    get() = "Color(0xff${this.toHex().drop(1).lowercase()})"