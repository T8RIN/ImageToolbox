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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DropShadowParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun DropShadowParamsItem(
    value: DropShadowParams,
    filter: UiFilter<DropShadowParams>,
    onFilterChange: (value: DropShadowParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        ColorRowSelector(
            value = value.color.toColor(),
            onValueChange = {
                if (!previewOnly) {
                    onFilterChange(value.copy(color = it.toArgb().toColorModel()))
                }
            },
            title = stringResource(R.string.shadow_color),
            allowCustom = !previewOnly
        )
        ShadowSlider(
            value = value.blurRadius,
            title = filter.paramsInfo[0].title!!,
            valueRange = filter.paramsInfo[0].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(blurRadius = it.roundTo(2)))
            }
        )
        ShadowSlider(
            value = value.offsetX,
            title = filter.paramsInfo[1].title!!,
            valueRange = filter.paramsInfo[1].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(offsetX = it.roundTo(2)))
            }
        )
        ShadowSlider(
            value = value.offsetY,
            title = filter.paramsInfo[2].title!!,
            valueRange = filter.paramsInfo[2].valueRange,
            enabled = !previewOnly,
            onValueChange = {
                onFilterChange(value.copy(offsetY = it.roundTo(2)))
            }
        )
    }
}

@Composable
private fun ShadowSlider(
    value: Float,
    title: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        enabled = enabled,
        value = value,
        title = stringResource(title),
        valueRange = valueRange,
        onValueChange = onValueChange,
        internalStateTransformation = { it.roundTo(2) },
        behaveAsContainer = false
    )
}
