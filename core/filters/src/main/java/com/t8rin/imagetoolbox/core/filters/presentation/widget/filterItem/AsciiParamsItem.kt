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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.ascii.Gradient
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelectionRowDefaults
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun AsciiParamsItem(
    value: AsciiParams,
    filter: UiFilter<AsciiParams>,
    onFilterChange: (value: AsciiParams) -> Unit,
    previewOnly: Boolean
) {
    val gradient: MutableState<String> =
        remember(value) { mutableStateOf(value.gradient) }
    val fontSize: MutableState<Float> =
        remember(value) { mutableFloatStateOf(value.fontSize) }
    val backgroundColor: MutableState<ColorModel> =
        remember(value) { mutableStateOf(value.backgroundColor) }
    var isGrayscale by remember(value) { mutableStateOf(value.isGrayscale) }

    LaunchedEffect(
        gradient.value,
        fontSize.value,
        backgroundColor.value,
        isGrayscale,
    ) {
        onFilterChange(
            AsciiParams(
                gradient = gradient.value,
                fontSize = fontSize.value,
                backgroundColor = backgroundColor.value,
                isGrayscale = isGrayscale,
            )
        )
    }

    Column(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filter.paramsInfo.forEachIndexed { index, (title, valueRange, roundTo) ->
            when (index) {
                0 -> {
                    RoundedTextField(
                        value = gradient.value,
                        onValueChange = {
                            gradient.value = it.toList().distinct().joinToString("")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp
                            ),
                        label = stringResource(title!!)
                    )
                    val items = remember {
                        listOf(
                            Gradient.NORMAL,
                            Gradient.NORMAL2,
                            Gradient.ARROWS,
                            Gradient.OLD,
                            Gradient.EXTENDED_HIGH,
                            Gradient.MINIMAL,
                            Gradient.MATH,
                            Gradient.NUMERICAL
                        ).map { it.value }
                    }

                    EnhancedButtonGroup(
                        items = items,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp
                            ),
                        selectedIndex = items.indexOf(gradient.value),
                        onIndexChange = {
                            gradient.value = items[it]
                        },
                        inactiveButtonColor = Color.Unspecified
                    )
                }

                1 -> {
                    EnhancedSliderItem(
                        enabled = !previewOnly,
                        value = fontSize.value,
                        title = stringResource(title!!),
                        valueRange = valueRange,
                        steps = if (valueRange == 0f..3f) 2 else 0,
                        onValueChange = {
                            fontSize.value = it
                        },
                        internalStateTransformation = {
                            it.roundTo(roundTo)
                        },
                        behaveAsContainer = false
                    )
                }

                2 -> {
                    ColorRowSelector(
                        title = stringResource(title!!),
                        value = backgroundColor.value.toColor(),
                        onValueChange = {
                            backgroundColor.value = it.toModel()
                        },
                        allowScroll = !previewOnly,
                        icon = null,
                        defaultColors = ColorSelectionRowDefaults.colorList,
                        titleFontWeight = FontWeight.Companion.Normal,
                        contentHorizontalPadding = 16.dp,
                    )
                }

                3 -> {
                    PreferenceRowSwitch(
                        title = stringResource(id = title!!),
                        checked = isGrayscale,
                        onClick = {
                            isGrayscale = it
                        },
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 4.dp,
                            end = 4.dp
                        ),
                        applyHorizontalPadding = false,
                        startContent = {},
                        resultModifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        enabled = !previewOnly
                    )
                }
            }
        }
    }
}