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

package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.triple_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.domain.model.PaletteTransferSpace
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.translatedName
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
internal fun FloatPaletteImageModelTripleItem(
    value: Triple<Float, PaletteTransferSpace, ImageModel>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<Float, PaletteTransferSpace, ImageModel>) -> Unit,
    previewOnly: Boolean
) {
    var sliderState1 by remember { mutableFloatStateOf(value.first.toFloat()) }
    var colorSpace1 by remember { mutableStateOf(value.second) }
    var uri1 by remember(value) { mutableStateOf(value.third.data) }

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
                    colorSpace1,
                    uri1.toImageModel()
                )
            )
        },
        internalStateTransformation = {
            it.roundTo(filter.paramsInfo[0].roundTo)
        },
        valueRange = filter.paramsInfo[0].valueRange,
        behaveAsContainer = false
    )
    Spacer(Modifier.height(8.dp))
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .container(
                shape = ContainerShapeDefaults.topShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            )
    ) {
        Text(
            text = stringResource(filter.paramsInfo[1].title!!),
            modifier = Modifier.padding(
                top = 8.dp,
                start = 12.dp,
                end = 12.dp,
            )
        )
        val entries by remember(filter) {
            derivedStateOf {
                PaletteTransferSpace.entries
            }
        }
        ToggleGroupButton(
            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            items = entries.map { it.translatedName },
            selectedIndex = entries.indexOf(colorSpace1),
            onIndexChange = {
                colorSpace1 = entries[it]
                onFilterChange(
                    Triple(
                        sliderState1,
                        colorSpace1,
                        uri1.toImageModel()
                    )
                )
            }
        )
    }
    Spacer(Modifier.height(4.dp))
    ImageSelector(
        modifier = Modifier.padding(
            horizontal = 16.dp
        ),
        value = uri1,
        title = filter.paramsInfo[2].title?.let {
            stringResource(it)
        } ?: stringResource(R.string.image),
        onValueChange = {
            uri1 = it.toString()
            onFilterChange(
                Triple(
                    sliderState1,
                    colorSpace1,
                    uri1.toImageModel()
                )
            )
        },
        subtitle = null,
        shape = ContainerShapeDefaults.bottomShape
    )
    Spacer(Modifier.height(16.dp))
}