/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.dark_mode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeTheme

@Composable
internal fun PdfDarkModeThemeSelector(
    value: PdfDarkModeTheme,
    onValueChange: (PdfDarkModeTheme) -> Unit
) {
    EnhancedButtonGroup(
        modifier = Modifier
            .container(ShapeDefaults.large),
        entries = PdfDarkModeTheme.entries,
        value = value,
        title = stringResource(R.string.color_scheme),
        onValueChange = onValueChange,
        itemContent = { theme ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            color = Color(theme.backgroundColor ?: 0xFFFFFFFF.toInt()),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(
                        when (theme) {
                            PdfDarkModeTheme.Warm -> R.string.warm
                            PdfDarkModeTheme.Classic -> R.string.classic
                            PdfDarkModeTheme.Blue -> R.string.color_blue
                            PdfDarkModeTheme.Green -> R.string.color_green
                            PdfDarkModeTheme.Negative -> R.string.negative
                        }
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    )
}
