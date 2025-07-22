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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.translatedName
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun SideFadeRelativeItem(
    value: SideFadeParams.Relative,
    filter: UiFilter<SideFadeParams.Relative>,
    onFilterChange: (value: SideFadeParams.Relative) -> Unit,
    previewOnly: Boolean
) {
    var scale by remember(value) { mutableFloatStateOf(value.scale) }
    var sideFade by remember(value) { mutableStateOf(value.side) }

    LaunchedEffect(scale, sideFade) {
        onFilterChange(
            SideFadeParams.Relative(
                side = sideFade,
                scale = scale
            )
        )
    }

    EnhancedSliderItem(
        modifier = Modifier
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
        enabled = !previewOnly,
        value = scale,
        title = filter.paramsInfo[0].title?.let {
            stringResource(it)
        } ?: "",
        onValueChange = {
            scale = it
        },
        internalStateTransformation = {
            it.roundTo(filter.paramsInfo[0].roundTo)
        },
        valueRange = filter.paramsInfo[0].valueRange,
        behaveAsContainer = false
    )
    filter.paramsInfo[1].takeIf { it.title != null }
        ?.let { (title, _, _) ->
            Text(
                text = stringResource(title!!),
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 12.dp,
                    end = 12.dp,
                )
            )
            EnhancedButtonGroup(
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                items = FadeSide.entries.map { it.translatedName },
                selectedIndex = FadeSide.entries.indexOf(sideFade),
                onIndexChange = {
                    sideFade = FadeSide.entries[it]
                }
            )
        }
}

