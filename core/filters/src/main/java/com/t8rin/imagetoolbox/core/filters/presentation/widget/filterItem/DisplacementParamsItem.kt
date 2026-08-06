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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementBoundary
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.DisplacementInterpolation
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DisplacementParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun DisplacementParamsItem(
    value: DisplacementParams,
    filter: UiFilter<DisplacementParams>,
    onFilterChange: (value: DisplacementParams) -> Unit,
    previewOnly: Boolean
) {
    var useSeparateVerticalMap by remember(value.verticalMap) {
        mutableStateOf(value.verticalMap != null)
    }

    Column(modifier = Modifier.padding(8.dp)) {
        ImageSelector(
            value = value.horizontalMap.data,
            title = stringResource(R.string.horizontal_displacement_map),
            subtitle = null,
            onValueChange = {
                onFilterChange(value.copy(horizontalMap = ImageModel(it.toString())))
            },
            modifier = Modifier.padding(8.dp)
        )
        PreferenceRowSwitch(
            title = stringResource(R.string.use_separate_vertical_map),
            checked = useSeparateVerticalMap,
            onClick = {
                useSeparateVerticalMap = it
                if (!it) onFilterChange(value.copy(verticalMap = null))
            },
            modifier = Modifier.padding(8.dp),
            applyHorizontalPadding = false,
            startContent = {},
            resultModifier = Modifier.padding(16.dp),
            enabled = !previewOnly
        )
        if (useSeparateVerticalMap) {
            ImageSelector(
                value = value.verticalMap?.data,
                title = stringResource(R.string.vertical_displacement_map),
                subtitle = null,
                onValueChange = {
                    onFilterChange(value.copy(verticalMap = ImageModel(it.toString())))
                },
                modifier = Modifier.padding(8.dp)
            )
        }
        DisplacementSlider(
            value = value.strengthX,
            title = stringResource(filter.paramsInfo[0].title!!),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(strengthX = it)) }
        )
        DisplacementSlider(
            value = value.strengthY,
            title = stringResource(filter.paramsInfo[1].title!!),
            enabled = !previewOnly,
            onValueChange = { onFilterChange(value.copy(strengthY = it)) }
        )
        DisplacementSelection(
            title = stringResource(R.string.gmic_param_interpolation),
            items = listOf(
                stringResource(R.string.nearest),
                stringResource(R.string.linear),
                stringResource(R.string.cubic)
            ),
            selectedIndex = DisplacementInterpolation.entries.indexOf(value.interpolation),
            enabled = !previewOnly,
            onIndexChange = {
                onFilterChange(value.copy(interpolation = DisplacementInterpolation.entries[it]))
            }
        )
        DisplacementSelection(
            title = stringResource(R.string.gmic_param_boundary),
            items = listOf(
                stringResource(R.string.displacement_boundary_transparent),
                stringResource(R.string.tile_mode_clamp),
                stringResource(R.string.wrap),
                stringResource(R.string.tile_mode_mirror)
            ),
            selectedIndex = DisplacementBoundary.entries.indexOf(value.boundary),
            enabled = !previewOnly,
            onIndexChange = {
                onFilterChange(value.copy(boundary = DisplacementBoundary.entries[it]))
            }
        )
    }
}

@Composable
private fun DisplacementSlider(
    value: Float,
    title: String,
    enabled: Boolean,
    onValueChange: (Float) -> Unit
) {
    EnhancedSliderItem(
        value = value,
        title = title,
        valueRange = -1f..1f,
        enabled = enabled,
        onValueChange = onValueChange,
        internalStateTransformation = { it.roundTo(3) },
        behaveAsContainer = false,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun DisplacementSelection(
    title: String,
    items: List<String>,
    selectedIndex: Int,
    enabled: Boolean,
    onIndexChange: (Int) -> Unit
) {
    EnhancedButtonGroup(
        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        items = items,
        selectedIndex = selectedIndex,
        title = title,
        onIndexChange = onIndexChange,
        enabled = enabled
    )
}
