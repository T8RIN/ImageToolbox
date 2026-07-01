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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShearParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun ShearParamsItem(
    value: ShearParams,
    filter: UiFilter<ShearParams>,
    onFilterChange: (value: ShearParams) -> Unit,
    previewOnly: Boolean
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        EnhancedSliderItem(
            enabled = !previewOnly,
            value = value.xAngle,
            title = stringResource(filter.paramsInfo[0].title!!),
            valueRange = filter.paramsInfo[0].valueRange,
            onValueChange = { onFilterChange(value.copy(xAngle = it)) },
            internalStateTransformation = {
                it.roundTo(filter.paramsInfo[0].roundTo)
            },
            behaveAsContainer = false
        )
        EnhancedSliderItem(
            enabled = !previewOnly,
            value = value.yAngle,
            title = stringResource(filter.paramsInfo[1].title!!),
            valueRange = filter.paramsInfo[1].valueRange,
            onValueChange = { onFilterChange(value.copy(yAngle = it)) },
            internalStateTransformation = {
                it.roundTo(filter.paramsInfo[1].roundTo)
            },
            behaveAsContainer = false
        )
        PreferenceRowSwitch(
            title = stringResource(filter.paramsInfo[2].title!!),
            checked = value.resize,
            onClick = { onFilterChange(value.copy(resize = it)) },
            modifier = Modifier.padding(
                top = 8.dp,
                start = 4.dp,
                end = 4.dp
            ),
            applyHorizontalPadding = false,
            startContent = {},
            resultModifier = Modifier.padding(16.dp),
            enabled = !previewOnly
        )
    }
}
