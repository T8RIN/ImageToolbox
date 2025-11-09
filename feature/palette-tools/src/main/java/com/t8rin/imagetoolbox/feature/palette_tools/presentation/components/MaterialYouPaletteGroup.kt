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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MaterialYouPaletteGroup(
    colorScheme: ColorScheme,
    onCopy: (Color) -> Unit
) {
    Column(
        modifier = Modifier.clip(MaterialTheme.shapes.large),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.primary,
                colorScheme = colorScheme,
                name = "Primary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onPrimary,
                colorScheme = colorScheme,
                name = "On Primary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.primaryContainer,
                colorScheme = colorScheme,
                name = "Primary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onPrimaryContainer,
                colorScheme = colorScheme,
                name = "On Primary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.secondary,
                colorScheme = colorScheme,
                name = "Secondary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onSecondary,
                colorScheme = colorScheme,
                name = "On Secondary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.secondaryContainer,
                colorScheme = colorScheme,
                name = "Secondary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onSecondaryContainer,
                colorScheme = colorScheme,
                name = "On Secondary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.tertiary,
                colorScheme = colorScheme,
                name = "Tertiary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onTertiary,
                colorScheme = colorScheme,
                name = "On Tertiary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.tertiaryContainer,
                colorScheme = colorScheme,
                name = "Tertiary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onTertiaryContainer,
                colorScheme = colorScheme,
                name = "On Tertiary Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.error,
                colorScheme = colorScheme,
                name = "Error",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onError,
                colorScheme = colorScheme,
                name = "On Error",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.errorContainer,
                colorScheme = colorScheme,
                name = "Error Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onErrorContainer,
                colorScheme = colorScheme,
                name = "On Error Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.inversePrimary,
                colorScheme = colorScheme,
                name = "Inverse Primary",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceTint,
                colorScheme = colorScheme,
                name = "Surface Tint",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceDim,
                colorScheme = colorScheme,
                name = "Surface Dim",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceBright,
                colorScheme = colorScheme,
                name = "Surface Bright",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.surfaceVariant,
                colorScheme = colorScheme,
                name = "Surface Variant",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.onSurfaceVariant,
                colorScheme = colorScheme,
                name = "On Surface Variant",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.inverseSurface,
                colorScheme = colorScheme,
                name = "Inverse Surface",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.inverseOnSurface,
                colorScheme = colorScheme,
                name = "Inverse On Surface",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.onSurface,
                colorScheme = colorScheme,
                name = "On Surface",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surface,
                colorScheme = colorScheme,
                name = "Surface",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceContainerLowest,
                colorScheme = colorScheme,
                name = "Surface Lowest",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceContainerLow,
                colorScheme = colorScheme,
                name = "Surface Low",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.surfaceContainer,
                colorScheme = colorScheme,
                name = "Surface Container",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceContainerHigh,
                colorScheme = colorScheme,
                name = "Surface High",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.surfaceContainerHighest,
                colorScheme = colorScheme,
                name = "Surface Highest",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            MaterialYouPaletteItem(
                color = colorScheme.outline,
                colorScheme = colorScheme,
                name = "Outline",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
            MaterialYouPaletteItem(
                color = colorScheme.outlineVariant,
                colorScheme = colorScheme,
                name = "Outline Variant",
                onCopy = onCopy,
                modifier = Modifier.weight(1f)
            )
        }
    }
}