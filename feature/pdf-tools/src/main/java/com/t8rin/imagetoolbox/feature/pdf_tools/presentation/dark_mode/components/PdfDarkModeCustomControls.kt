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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.BlendingMode
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.BlendingModeSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeParams
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfDarkModeTheme

@Composable
internal fun PdfDarkModeCustomControls(
    params: PdfDarkModeParams,
    onColorChange: (Color) -> Unit,
    onBlendModeChange: (BlendingMode) -> Unit
) {
    AnimatedVisibility(
        visible = params.theme == PdfDarkModeTheme.Custom,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            Spacer(Modifier.height(8.dp))
            val presetColors = remember {
                PdfDarkModeTheme.entries
                    .mapNotNull { it.backgroundColor }
                    .distinct()
                    .map(::Color)
            }

            ColorRowSelector(
                value = Color(params.customColor),
                onValueChange = onColorChange,
                title = stringResource(R.string.pdf_dark_mode_blend_color),
                allowAlpha = false,
                defaultColors = presetColors,
                modifier = Modifier.container(
                    shape = ShapeDefaults.large
                )
            )
            Spacer(Modifier.height(8.dp))
            BlendingModeSelector(
                value = params.customBlendMode,
                onValueChange = onBlendModeChange,
                entries = PdfDarkModeParams.SupportedBlendModes,
                shape = ShapeDefaults.large,
                color = Color.Unspecified
            )
        }
    }
}
