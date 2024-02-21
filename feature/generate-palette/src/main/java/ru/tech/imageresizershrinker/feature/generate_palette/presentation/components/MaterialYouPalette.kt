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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.extractPrimaryColor
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
            context.copyToClipboard(
                context.getString(R.string.color_scheme),
                colorScheme.asCodeString()
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

private fun ColorScheme.asCodeString(): String = """
val colorScheme = ColorScheme(
    background = Color(0xff${
    background.toHex()
        .drop(1)
}),
    error = Color(0xff${
    error.toHex()
        .drop(1)
}),
    errorContainer = Color(0xff${
    errorContainer.toHex()
        .drop(1)
}),
    inverseOnSurface = Color(0xff${
    inverseOnSurface.toHex()
        .drop(1)
}),
    inversePrimary = Color(0xff${
    inversePrimary.toHex()
        .drop(1)
}),
    inverseSurface = Color(0xff${
    inverseSurface.toHex()
        .drop(1)
}),
    onBackground = Color(0xff${
    onBackground.toHex()
        .drop(1)
}),
    onError = Color(0xff${
    onError.toHex()
        .drop(1)
}),
    onErrorContainer = Color(0xff${
    onErrorContainer.toHex()
        .drop(1)
}),
    onPrimary = Color(0xff${
    onPrimary.toHex()
        .drop(1)
}),
    onPrimaryContainer = Color(0xff${
    onPrimaryContainer.toHex()
        .drop(1)
}),
    onSecondary = Color(0xff${
    onSecondary.toHex()
        .drop(1)
}),
    onSecondaryContainer = Color(0xff${
    onSecondaryContainer.toHex()
        .drop(1)
}),
    onSurface = Color(0xff${
    onSurface.toHex()
        .drop(1)
}),
    onSurfaceVariant = Color(0xff${
    onSurfaceVariant.toHex()
        .drop(1)
}),
    onTertiary = Color(0xff${
    onTertiary.toHex()
        .drop(1)
}),
    onTertiaryContainer = Color(0xff${
    onTertiaryContainer.toHex()
        .drop(1)
}),
    outline = Color(0xff${
    outline.toHex()
        .drop(1)
}),
    outlineVariant = Color(0xff${
    outlineVariant.toHex()
        .drop(1)
}),
    primary = Color(0xff${
    primary.toHex()
        .drop(1)
}),
    primaryContainer = Color(0xff${
    primaryContainer.toHex()
        .drop(1)
}),
    scrim = Color(0xff${
    scrim.toHex()
        .drop(1)
}),
    secondary = Color(0xff${
    secondary.toHex()
        .drop(1)
}),
    secondaryContainer = Color(0xff${
    secondaryContainer.toHex()
        .drop(1)
}),
    surface = Color(0xff${
    surface.toHex()
        .drop(1)
}),
    surfaceTint = Color(0xff${
    surfaceTint.toHex()
        .drop(1)
}),
    surfaceVariant = Color(0xff${
    surfaceVariant.toHex()
        .drop(1)
}),
    tertiary = Color(0xff${
    tertiary.toHex()
        .drop(1)
}),
    tertiaryContainer = Color(0xff${
    tertiaryContainer.toHex()
        .drop(1)
}),
    surfaceBright = Color(0xff${
    surfaceBright.toHex()
        .drop(1)
}),
    surfaceDim = Color(0xff${
    surfaceDim.toHex()
        .drop(1)
}),
    surfaceContainer = Color(0xff${
    surfaceContainer.toHex()
        .drop(1)
}),
    surfaceContainerHigh = Color(0xff${
    surfaceContainerHigh.toHex()
        .drop(1)
}),
    surfaceContainerHighest = Color(0xff${
    surfaceContainerHighest.toHex()
        .drop(1)
}),
    surfaceContainerLow = Color(0xff${
    surfaceContainerLow.toHex()
        .drop(1)
}),
    surfaceContainerLowest = Color(0xff${
    surfaceContainerLowest.toHex()
        .drop(1)
}),
)
""".trim()

