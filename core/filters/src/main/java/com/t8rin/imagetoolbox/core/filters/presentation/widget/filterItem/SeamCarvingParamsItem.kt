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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SeamCarvingParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun SeamCarvingParamsItem(
    value: SeamCarvingParams,
    filter: UiFilter<SeamCarvingParams>,
    onFilterChange: (value: SeamCarvingParams) -> Unit,
    previewOnly: Boolean
) {
    IntegerSizeParamsItem(
        value = value.size,
        filter = filter,
        onFilterChange = { size ->
            onFilterChange(value.copy(size = size))
        },
        previewOnly = previewOnly
    )
    PreferenceRowSwitch(
        title = stringResource(R.string.seam_carving_backward_energy),
        subtitle = stringResource(R.string.seam_carving_backward_energy_sub),
        checked = value.useBackwardEnergy,
        onClick = {
            onFilterChange(value.copy(useBackwardEnergy = it))
        },
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        containerColor = Color.Unspecified,
        shape = ShapeDefaults.extraLarge,
        enabled = !previewOnly,
        applyHorizontalPadding = false,
        startContent = {},
        resultModifier = Modifier.padding(16.dp),
    )
}