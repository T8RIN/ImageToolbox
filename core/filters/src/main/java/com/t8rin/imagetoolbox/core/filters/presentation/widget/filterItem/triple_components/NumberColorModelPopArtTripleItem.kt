/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.triple_components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.translatedName
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun NumberColorModelPopArtTripleItem(
    value: Triple<Number, ColorModel, PopArtBlendingMode>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<Number, ColorModel, PopArtBlendingMode>) -> Unit,
    previewOnly: Boolean
) {
    var sliderState1 by remember { mutableFloatStateOf(value.first.toFloat()) }
    var color1 by remember(value) { mutableStateOf(value.second.toColor()) }
    var blendMode1 by remember(value) { mutableStateOf(value.third) }

    EnhancedSliderItem(
        modifier = Modifier
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
        enabled = !previewOnly,
        value = sliderState1,
        title = filter.paramsInfo[0].title?.let {
            stringResource(it)
        } ?: "",
        onValueChange = {
            sliderState1 = it
            onFilterChange(
                Triple(
                    sliderState1,
                    color1.toModel(),
                    blendMode1
                )
            )
        },
        internalStateTransformation = {
            it.roundTo(filter.paramsInfo[0].roundTo)
        },
        valueRange = filter.paramsInfo[0].valueRange,
        behaveAsContainer = false
    )
    Box(
        modifier = Modifier.padding(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp
        )
    ) {
        Column {
            ColorRowSelector(
                title = stringResource(filter.paramsInfo[1].title!!),
                value = color1,
                onValueChange = {
                    color1 = it
                    onFilterChange(
                        Triple(
                            sliderState1,
                            color1.toModel(),
                            blendMode1
                        )
                    )
                },
                allowScroll = !previewOnly,
                icon = null,
                defaultColors = ColorSelectionRowDefaults.colorList,
                contentHorizontalPadding = 0.dp
            )
            Text(
                text = stringResource(filter.paramsInfo[2].title!!),
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 12.dp,
                    end = 12.dp,
                )
            )
            val entries by remember(filter) {
                derivedStateOf {
                    PopArtBlendingMode.entries
                }
            }
            EnhancedButtonGroup(
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                items = entries.map { it.translatedName },
                selectedIndex = entries.indexOf(blendMode1),
                onIndexChange = {
                    blendMode1 = entries[it]
                    onFilterChange(
                        Triple(
                            sliderState1,
                            color1.toModel(),
                            blendMode1
                        )
                    )
                }
            )
        }
    }
}