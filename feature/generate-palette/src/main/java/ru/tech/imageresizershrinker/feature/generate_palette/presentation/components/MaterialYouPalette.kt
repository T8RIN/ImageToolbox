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

package ru.tech.imageresizershrinker.feature.generate_palette.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.dynamic.theme.getColorScheme
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Cube
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.plus
import ru.tech.imageresizershrinker.core.ui.utils.helper.toHex
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState

@Composable
internal fun MaterialYouPalette(
    image: Bitmap,
    paletteStyle: PaletteStyle,
    isDarkTheme: Boolean,
    isInvertColors: Boolean,
    contrastLevel: Float
) {
    val primaryColor by remember(image) {
        derivedStateOf {
            image.extractPrimaryColor()
        }
    }

    val colorScheme = rememberColorScheme(
        isDarkTheme = isDarkTheme,
        amoledMode = false,
        colorTuple = ColorTuple(primaryColor),
        style = paletteStyle,
        contrastLevel = contrastLevel.toDouble(),
        dynamicColor = false,
        isInvertColors = isInvertColors
    )
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    MaterialYouPaletteGroup(
        colorScheme = colorScheme,
        onCopy = { color ->
            context.copyToClipboard(
                context.getString(R.string.color),
                color.toHex()
            )
            scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.ContentPaste,
                    message = context.getString(R.string.color_copied)
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    EnhancedButton(
        onClick = {
            val light = getColorScheme(
                isDarkTheme = false,
                amoledMode = false,
                colorTuple = ColorTuple(primaryColor),
                style = paletteStyle,
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = false,
                isInvertColors = isInvertColors
            ).asCodeString(false)

            val dark = getColorScheme(
                isDarkTheme = true,
                amoledMode = false,
                colorTuple = ColorTuple(primaryColor),
                style = paletteStyle,
                contrastLevel = contrastLevel.toDouble(),
                dynamicColor = false,
                isInvertColors = isInvertColors
            ).asCodeString(true)

            context.copyToClipboard(
                context.getString(R.string.color_scheme),
                light + "\n\n" + dark
            )
            scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.ContentPaste,
                    message = context.getString(R.string.copied)
                )
            }
        },
        contentPadding = ButtonDefaults.ContentPadding + PaddingValues(
            start = (-4).dp
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Cube,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.copy_as_compose_code))
    }

}

private fun ColorScheme.asCodeString(
    isDarkTheme: Boolean
): String = """
val ${if (isDarkTheme) "dark" else "light"}ColorScheme = ColorScheme(
    background = Color(0xff${background.hex}),
    error = Color(0xff${error.hex}),
    errorContainer = Color(0xff${errorContainer.hex}),
    inverseOnSurface = Color(0xff${inverseOnSurface.hex}),
    inversePrimary = Color(0xff${inversePrimary.hex}),
    inverseSurface = Color(0xff${inverseSurface.hex}),
    onBackground = Color(0xff${onBackground.hex}),
    onError = Color(0xff${onError.hex}),
    onErrorContainer = Color(0xff${onErrorContainer.hex}),
    onPrimary = Color(0xff${onPrimary.hex}),
    onPrimaryContainer = Color(0xff${onPrimaryContainer.hex}),
    onSecondary = Color(0xff${onSecondary.hex}),
    onSecondaryContainer = Color(0xff${onSecondaryContainer.hex}),
    onSurface = Color(0xff${onSurface.hex}),
    onSurfaceVariant = Color(0xff${onSurfaceVariant.hex}),
    onTertiary = Color(0xff${onTertiary.hex}),
    onTertiaryContainer = Color(0xff${onTertiaryContainer.hex}),
    outline = Color(0xff${outline.hex}),
    outlineVariant = Color(0xff${outlineVariant.hex}),
    primary = Color(0xff${primary.hex}),
    primaryContainer = Color(0xff${primaryContainer.hex}),
    scrim = Color(0xff${scrim.hex}),
    secondary = Color(0xff${secondary.hex}),
    secondaryContainer = Color(0xff${secondaryContainer.hex}),
    surface = Color(0xff${surface.hex}),
    surfaceTint = Color(0xff${surfaceTint.hex}),
    surfaceVariant = Color(0xff${surfaceVariant.hex}),
    tertiary = Color(0xff${tertiary.hex}),
    tertiaryContainer = Color(0xff${tertiaryContainer.hex}),
    surfaceBright = Color(0xff${surfaceBright.hex}),
    surfaceDim = Color(0xff${surfaceDim.hex}),
    surfaceContainer = Color(0xff${surfaceContainer.hex}),
    surfaceContainerHigh = Color(0xff${surfaceContainerHigh.hex}),
    surfaceContainerHighest = Color(0xff${surfaceContainerHighest.hex}),
    surfaceContainerLow = Color(0xff${surfaceContainerLow.hex}),
    surfaceContainerLowest = Color(0xff${surfaceContainerLowest.hex}),
)
""".trim()

private val Color.hex
    get() = this.toHex().drop(1).lowercase()