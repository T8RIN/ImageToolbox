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

package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.PaletteTransferSpace
import ru.tech.imageresizershrinker.core.filters.domain.model.PopArtBlendingMode
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.translatedName
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toColor
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.toModel
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
internal fun TripleItem(
    value: Triple<*, *, *>,
    filter: UiFilter<Triple<*, *, *>>,
    onFilterChange: (value: Triple<*, *, *>) -> Unit,
    previewOnly: Boolean
) {
    when {
        value.first is Float && value.second is PaletteTransferSpace && value.third is ImageModel -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Float).toFloat()) }
            var colorSpace1 by remember { mutableStateOf((value.second as PaletteTransferSpace)) }
            var uri1 by remember(value) { mutableStateOf((value.third as ImageModel).data) }

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

        value.first is Number && value.second is Number && value.third is Number -> {
            val sliderState1: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            val sliderState2: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
            val sliderState3: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.third as Number).toFloat()) }

            LaunchedEffect(
                sliderState1.value,
                sliderState2.value,
                sliderState3.value
            ) {
                onFilterChange(
                    Triple(
                        sliderState1.value,
                        sliderState2.value,
                        sliderState3.value
                    )
                )
            }

            val paramsInfo by remember(filter) {
                derivedStateOf {
                    filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                        if (filterParam.title == null) return@mapIndexedNotNull null
                        when (index) {
                            0 -> sliderState1
                            1 -> sliderState2
                            else -> sliderState3
                        } to filterParam
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                paramsInfo.forEach { (state, info) ->
                    val (title, valueRange, roundTo) = info
                    EnhancedSliderItem(
                        enabled = !previewOnly,
                        value = state.value,
                        title = stringResource(title!!),
                        valueRange = valueRange,
                        onValueChange = {
                            state.value = it
                        },
                        internalStateTransformation = {
                            it.roundTo(roundTo)
                        },
                        behaveAsContainer = false
                    )
                }
            }
        }

        value.first is Number && value.second is Number && value.third is ColorModel -> {
            val sliderState1: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            val sliderState2: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
            var color3 by remember(value) { mutableStateOf((value.third as ColorModel).toColor()) }

            LaunchedEffect(
                sliderState1.value,
                sliderState2.value,
                color3
            ) {
                onFilterChange(
                    Triple(sliderState1.value, sliderState2.value, color3.toModel())
                )
            }

            val paramsInfo by remember(filter) {
                derivedStateOf {
                    filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                        if (filterParam.title == null || index > 1) return@mapIndexedNotNull null
                        when (index) {
                            0 -> sliderState1
                            else -> sliderState2
                        } to filterParam
                    }
                }
            }

            Column(
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                paramsInfo.forEach { (state, info) ->
                    val (title, valueRange, roundTo) = info
                    EnhancedSliderItem(
                        enabled = !previewOnly,
                        value = state.value,
                        title = stringResource(title!!),
                        valueRange = valueRange,
                        onValueChange = {
                            state.value = it
                        },
                        internalStateTransformation = {
                            it.roundTo(roundTo)
                        },
                        behaveAsContainer = false
                    )
                }
            }

            ColorRowSelector(
                title = stringResource(filter.paramsInfo[2].title!!),
                value = color3,
                onValueChange = {
                    color3 = it
                },
                allowScroll = !previewOnly,
                icon = null,
                defaultColors = ColorSelectionRowDefaults.colorList,
                titleFontWeight = FontWeight.Normal,
                contentHorizontalPadding = 16.dp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        value.first is Number && value.second is ColorModel && value.third is ColorModel -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
            var color1 by remember(value) { mutableStateOf((value.second as ColorModel).toColor()) }
            var color2 by remember(value) { mutableStateOf((value.third as ColorModel).toColor()) }

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
                            color2.toModel()
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
                                    color2.toModel()
                                )
                            )
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Normal,
                        contentHorizontalPadding = 0.dp
                    )
                    Spacer(Modifier.height(8.dp))
                    ColorRowSelector(
                        title = stringResource(filter.paramsInfo[2].title!!),
                        value = color1,
                        onValueChange = {
                            color2 = it
                            onFilterChange(
                                Triple(
                                    sliderState1,
                                    color1.toModel(),
                                    color2.toModel()
                                )
                            )
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Normal,
                        contentHorizontalPadding = 0.dp
                    )
                }
            }
        }

        value.first is Number && value.second is Number && value.third is BlurEdgeMode -> {
            val sliderState1: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            val sliderState2: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
            var edgeMode by remember(value) { mutableStateOf(value.third as BlurEdgeMode) }

            LaunchedEffect(
                sliderState1.value,
                sliderState2.value,
                edgeMode
            ) {
                onFilterChange(
                    Triple(sliderState1.value, sliderState2.value, edgeMode)
                )
            }

            val paramsInfo by remember(filter) {
                derivedStateOf {
                    filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                        if (filterParam.title == null || index > 1) return@mapIndexedNotNull null
                        when (index) {
                            0 -> sliderState1
                            else -> sliderState2
                        } to filterParam
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                paramsInfo.forEach { (state, info) ->
                    val (title, valueRange, roundTo) = info
                    EnhancedSliderItem(
                        enabled = !previewOnly,
                        value = state.value,
                        title = stringResource(title!!),
                        valueRange = valueRange,
                        onValueChange = {
                            state.value = it
                        },
                        internalStateTransformation = {
                            it.roundTo(roundTo)
                        },
                        behaveAsContainer = false
                    )
                }
            }
            filter.paramsInfo[2].title?.let { title ->
                EdgeModeSelector(
                    title = title,
                    value = edgeMode,
                    onValueChange = { edgeMode = it }
                )
            }
        }

        value.first is Number && value.second is TransferFunc && value.third is BlurEdgeMode -> {
            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            var transferFunction by remember(value) { mutableStateOf(value.second as TransferFunc) }
            var edgeMode by remember(value) { mutableStateOf(value.third as BlurEdgeMode) }

            LaunchedEffect(
                sliderState1,
                transferFunction,
                edgeMode
            ) {
                onFilterChange(
                    Triple(sliderState1, transferFunction, edgeMode)
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
                value = sliderState1,
                title = filter.paramsInfo[0].title?.let {
                    stringResource(it)
                } ?: "",
                onValueChange = {
                    sliderState1 = it
                },
                internalStateTransformation = {
                    it.roundTo(filter.paramsInfo[0].roundTo)
                },
                valueRange = filter.paramsInfo[0].valueRange,
                behaveAsContainer = false
            )
            filter.paramsInfo[1].title?.let { title ->
                TransferFuncSelector(
                    title = title,
                    value = transferFunction,
                    onValueChange = { transferFunction = it }
                )
            }
            filter.paramsInfo[2].title?.let { title ->
                EdgeModeSelector(
                    title = title,
                    value = edgeMode,
                    onValueChange = { edgeMode = it }
                )
            }
        }

        value.first is ColorModel && value.second is ColorModel && value.third is ColorModel -> {
            Box(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp
                )
            ) {
                var color1 by remember(value) { mutableStateOf((value.first as ColorModel).toColor()) }
                var color2 by remember(value) { mutableStateOf((value.second as ColorModel).toColor()) }
                var color3 by remember(value) { mutableStateOf((value.third as ColorModel).toColor()) }

                Column {
                    ColorRowSelector(
                        title = stringResource(R.string.first_color),
                        value = color1,
                        onValueChange = {
                            color1 = it
                            onFilterChange(
                                Triple(
                                    color1.toModel(),
                                    color2.toModel(),
                                    color3.toModel()
                                )
                            )
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Normal,
                        contentHorizontalPadding = 0.dp
                    )
                    Spacer(Modifier.height(8.dp))
                    ColorRowSelector(
                        title = stringResource(R.string.second_color),
                        value = color2,
                        onValueChange = {
                            color2 = it
                            onFilterChange(
                                Triple(
                                    color1.toModel(),
                                    color2.toModel(),
                                    color3.toModel()
                                )
                            )
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Normal,
                        contentHorizontalPadding = 0.dp
                    )
                    Spacer(Modifier.height(8.dp))
                    ColorRowSelector(
                        title = stringResource(R.string.third_color),
                        value = color3,
                        onValueChange = {
                            color3 = it
                            onFilterChange(
                                Triple(
                                    color1.toModel(),
                                    color2.toModel(),
                                    color3.toModel()
                                )
                            )
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Normal,
                        contentHorizontalPadding = 0.dp
                    )
                }
            }
        }

        value.first is Number && value.second is ColorModel && value.third is PopArtBlendingMode -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
            var color1 by remember(value) { mutableStateOf((value.second as ColorModel).toColor()) }
            var blendMode1 by remember(value) { mutableStateOf((value.third as PopArtBlendingMode)) }

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
                        titleFontWeight = FontWeight.Normal,
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
                    ToggleGroupButton(
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
    }
}