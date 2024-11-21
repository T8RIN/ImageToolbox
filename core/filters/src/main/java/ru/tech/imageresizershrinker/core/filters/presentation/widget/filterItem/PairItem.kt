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
import ru.tech.imageresizershrinker.core.domain.model.FileModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.domain.model.BlurEdgeMode
import ru.tech.imageresizershrinker.core.filters.domain.model.TransferFunc
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toColor
import ru.tech.imageresizershrinker.core.ui.utils.helper.toFileModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.core.ui.utils.helper.toModel
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ColorRowSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FileSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun PairItem(
    value: Pair<*, *>,
    filter: UiFilter<Pair<*, *>>,
    onFilterChange: (value: Pair<*, *>) -> Unit,
    previewOnly: Boolean
) {
    when {
        value.first is Number && value.second is Number -> {
            val sliderState1: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            val sliderState2: MutableState<Float> =
                remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }

            LaunchedEffect(
                sliderState1.value,
                sliderState2.value
            ) {
                onFilterChange(
                    sliderState1.value to sliderState2.value
                )
            }

            val paramsInfo by remember(filter) {
                derivedStateOf {
                    filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                        if (filterParam.title == null) return@mapIndexedNotNull null
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
        }

        value.first is ColorModel && value.second is ColorModel -> {
            Box(
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp
                )
            ) {
                var color1 by remember(value) { mutableStateOf((value.first as ColorModel).toColor()) }
                var color2 by remember(value) { mutableStateOf((value.second as ColorModel).toColor()) }

                Column {
                    ColorRowSelector(
                        title = stringResource(R.string.first_color),
                        value = color1,
                        onValueChange = {
                            color1 = it
                            onFilterChange(color1.toModel() to color2.toModel())
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
                            onFilterChange(color1.toModel() to color2.toModel())
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

        value.first is Float && value.second is ColorModel -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
            var color1 by remember(value) { mutableStateOf((value.second as ColorModel).toColor()) }

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
                    onFilterChange(sliderState1 to color1.toModel())
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
                    end = 16.dp
                )
            ) {
                ColorRowSelector(
                    title = stringResource(filter.paramsInfo[1].title!!),
                    value = color1,
                    onValueChange = {
                        color1 = it
                        onFilterChange(sliderState1 to color1.toModel())
                    },
                    allowScroll = !previewOnly,
                    icon = null,
                    defaultColors = ColorSelectionRowDefaults.colorList,
                    titleFontWeight = FontWeight.Normal,
                    contentHorizontalPadding = 0.dp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        value.first is Float && value.second is ImageModel -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Float).toFloat()) }
            var uri1 by remember(value) { mutableStateOf((value.second as ImageModel).data) }

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
                    onFilterChange(sliderState1 to uri1.toImageModel())
                },
                internalStateTransformation = {
                    it.roundTo(filter.paramsInfo[0].roundTo)
                },
                valueRange = filter.paramsInfo[0].valueRange,
                behaveAsContainer = false
            )
            ImageSelector(
                modifier = Modifier.padding(16.dp),
                value = uri1,
                title = filter.paramsInfo[1].title?.let {
                    stringResource(it)
                } ?: stringResource(R.string.image),
                onValueChange = {
                    uri1 = it.toString()
                    onFilterChange(sliderState1 to uri1.toImageModel())
                },
                subtitle = null
            )
        }

        value.first is Float && value.second is FileModel -> {
            var sliderState1 by remember { mutableFloatStateOf((value.first as Float).toFloat()) }
            var uri1 by remember(value) { mutableStateOf((value.second as FileModel).uri) }

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
                    onFilterChange(sliderState1 to uri1.toFileModel())
                },
                internalStateTransformation = {
                    it.roundTo(filter.paramsInfo[0].roundTo)
                },
                valueRange = filter.paramsInfo[0].valueRange,
                behaveAsContainer = false
            )
            FileSelector(
                modifier = Modifier.padding(16.dp),
                value = uri1,
                title = filter.paramsInfo[1].title?.let {
                    stringResource(it)
                } ?: stringResource(R.string.pick_file),
                onValueChange = {
                    uri1 = it.toString()
                    onFilterChange(sliderState1 to uri1.toFileModel())
                },
                subtitle = null
            )
        }

        value.first is Number && value.second is Boolean -> {
            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            var booleanState2 by remember(value) { mutableStateOf(value.second as Boolean) }

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
                    onFilterChange(sliderState1 to booleanState2)
                },
                internalStateTransformation = {
                    it.roundTo(filter.paramsInfo[0].roundTo)
                },
                valueRange = filter.paramsInfo[0].valueRange,
                behaveAsContainer = false
            )
            filter.paramsInfo[1].takeIf { it.title != null }
                ?.let { (title, _, _) ->
                    PreferenceRowSwitch(
                        title = stringResource(id = title!!),
                        checked = booleanState2,
                        onClick = {
                            booleanState2 = it
                            onFilterChange(sliderState1 to it)
                        },
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        ),
                        applyHorizontalPadding = false,
                        startContent = {},
                        resultModifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )
                }
        }

        value.first is Number && value.second is BlurEdgeMode -> {
            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            var edgeMode by remember(value) { mutableStateOf(value.second as BlurEdgeMode) }

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
                    onFilterChange(sliderState1 to edgeMode)
                },
                internalStateTransformation = {
                    it.roundTo(filter.paramsInfo[0].roundTo)
                },
                valueRange = filter.paramsInfo[0].valueRange,
                behaveAsContainer = false
            )
            filter.paramsInfo[1].title?.let { title ->
                EdgeModeSelector(
                    title = title,
                    value = edgeMode,
                    onValueChange = {
                        edgeMode = it
                        onFilterChange(sliderState1 to edgeMode)
                    }
                )
            }
        }

        value.first is Number && value.second is TransferFunc -> {
            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
            var transferFunction by remember(value) { mutableStateOf(value.second as TransferFunc) }

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
                    onFilterChange(sliderState1 to transferFunction)
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
                    onValueChange = {
                        transferFunction = it
                        onFilterChange(sliderState1 to transferFunction)
                    }
                )
            }
        }
    }
}