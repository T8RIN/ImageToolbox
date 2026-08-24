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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FormatLineSpacing
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.Stream
import com.t8rin.imagetoolbox.core.resources.icons.TextFields
import com.t8rin.imagetoolbox.core.resources.icons.ViewColumn
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.palette_tools.domain.model.PalettePdfParams
import kotlin.math.roundToInt

@Composable
internal fun PalettePdfControls(
    params: PalettePdfParams,
    onParamsChange: (PalettePdfParams) -> Unit,
    hasSourceImage: Boolean
) {
    Column {
        if (hasSourceImage) {
            EnhancedSliderItem(
                value = params.maximumColorCount,
                title = stringResource(R.string.max_colors_count),
                valueRange = 4f..64f,
                steps = 59,
                internalStateTransformation = Float::roundToInt,
                onValueChange = {
                    onParamsChange(params.copy(maximumColorCount = it.roundToInt()))
                },
                icon = Icons.Rounded.Palette,
                shape = ShapeDefaults.default,
                valueSuffix = ""
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        PalettePdfLayoutControls(
            params = params,
            onParamsChange = onParamsChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        PalettePdfContentControls(
            params = params,
            onParamsChange = onParamsChange,
            hasSourceImage = hasSourceImage
        )
    }
}

@Composable
private fun PalettePdfLayoutControls(
    params: PalettePdfParams,
    onParamsChange: (PalettePdfParams) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        EnhancedSliderItem(
            value = params.columns,
            title = stringResource(R.string.columns_count),
            icon = Icons.Rounded.ViewColumn,
            valueRange = 1f..6f,
            steps = 4,
            internalStateTransformation = Float::roundToInt,
            onValueChange = {
                onParamsChange(params.copy(columns = it.roundToInt()))
            },
            shape = ShapeDefaults.top,
            valueSuffix = ""
        )
        EnhancedSliderItem(
            value = params.margin,
            title = stringResource(R.string.palette_pdf_page_margin),
            icon = Icons.Rounded.Stream,
            valueRange = 0f..72f,
            internalStateTransformation = Float::roundToInt,
            onValueChange = {
                onParamsChange(params.copy(margin = it))
            },
            shape = ShapeDefaults.center,
            valueSuffix = " pt"
        )
        EnhancedSliderItem(
            value = params.spacing,
            title = stringResource(R.string.palette_pdf_color_spacing),
            icon = Icons.Rounded.FormatLineSpacing,
            valueRange = 0f..36f,
            internalStateTransformation = Float::roundToInt,
            onValueChange = {
                onParamsChange(params.copy(spacing = it))
            },
            shape = ShapeDefaults.bottom,
            valueSuffix = " pt"
        )
    }
}

@Composable
private fun PalettePdfContentControls(
    params: PalettePdfParams,
    onParamsChange: (PalettePdfParams) -> Unit,
    hasSourceImage: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (hasSourceImage) {
            PreferenceRowSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.palette_pdf_include_image),
                subtitle = stringResource(R.string.palette_pdf_include_image_sub),
                checked = params.includeSourceImage,
                startIcon = Icons.Rounded.Image,
                onClick = {
                    onParamsChange(params.copy(includeSourceImage = it))
                },
                shape = ShapeDefaults.top
            )
        }
        PreferenceRowSwitch(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.palette_pdf_show_names),
            checked = params.showColorNames,
            startIcon = Icons.Rounded.TextFields,
            onClick = {
                onParamsChange(params.copy(showColorNames = it))
            },
            shape = if (hasSourceImage) ShapeDefaults.center else ShapeDefaults.top
        )
        PreferenceRowSwitch(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.palette_pdf_show_hex),
            checked = params.showHexValues,
            startIcon = Icons.Rounded.HashTag,
            onClick = {
                onParamsChange(params.copy(showHexValues = it))
            },
            shape = ShapeDefaults.bottom
        )
    }
}
