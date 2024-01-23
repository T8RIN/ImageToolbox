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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiColorFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiRGBFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueDialog
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueText
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun <T> FilterItem(
    filter: UiFilter<T>,
    showDragHandle: Boolean,
    onRemove: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    previewOnly: Boolean = false,
    onFilterChange: (value: Any) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme
        .colorScheme
        .surfaceContainer,
    shape: Shape = MaterialTheme.shapes.extraLarge
) {
    val settingsState = LocalSettingsState.current
    Row(
        modifier = modifier
            .container(color = backgroundColor, shape = shape)
            .animateContentSize()
            .then(
                onLongPress?.let {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { it() }
                        )
                    }
                } ?: Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDragHandle) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.DragHandle, null)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .height(if (filter.value is Unit) 32.dp else 64.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant())
                    .padding(start = 20.dp)
            )
        }
        Column(
            Modifier
                .weight(1f)
                .alpha(if (previewOnly) 0.5f else 1f)
        ) {
            var sliderValue by remember(filter) {
                mutableFloatStateOf(
                    ((filter.value as? Number)?.toFloat()) ?: 0f
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(filter.title),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(
                                top = 8.dp,
                                end = 8.dp,
                                start = 16.dp
                            )
                            .weight(1f)
                    )
                    if (filter.value.toString().contains("Color") && !previewOnly) {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onRemove
                        ) {
                            Icon(Icons.Rounded.RemoveCircleOutline, null)
                        }
                    }
                }
                if (filter.value is Number) {
                    var showValueDialog by remember { mutableStateOf(false) }
                    ValueText(
                        value = sliderValue,
                        onClick = { showValueDialog = true }
                    )
                    ValueDialog(
                        roundTo = filter.paramsInfo[0].roundTo,
                        valueRange = filter.paramsInfo[0].valueRange,
                        valueState = sliderValue.toString(),
                        expanded = showValueDialog && !previewOnly,
                        onDismiss = { showValueDialog = false },
                        onValueUpdate = {
                            sliderValue = it
                            onFilterChange(it)
                        }
                    )
                }
            }
            if (filter.value is Unit) {
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                when (filter.value) {
                    is Color -> {
                        Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                            ColorSelectionRow(
                                value = filter.value as Color,
                                defaultColors = remember(filter) {
                                    derivedStateOf {
                                        ColorSelectionRowDefaults.colorList.map {
                                            if (filter is UiColorFilter) it.copy(0.5f)
                                            else it
                                        }
                                    }
                                }.value,
                                allowAlpha = filter !is UiRGBFilter,
                                allowScroll = !previewOnly,
                                onValueChange = { color ->
                                    onFilterChange(color)
                                }
                            )
                        }
                    }

                    is FloatArray -> {
                        val value = filter.value as FloatArray
                        val rows = filter.paramsInfo[0].valueRange.start.toInt().absoluteValue
                        var text by rememberSaveable(filter) {
                            mutableStateOf(
                                value.let {
                                    var string = ""
                                    it.forEachIndexed { index, float ->
                                        string += "$float, "
                                        if (index % rows == (rows - 1)) string += "\n"
                                    }
                                    string.dropLast(3)
                                }
                            )
                        }
                        RoundedTextField(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(16.dp),
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { text = it },
                            onLoseFocusTransformation = {
                                val matrix = filter.newInstance().value as FloatArray
                                split(", ").mapIndexed { index, num ->
                                    num.toFloatOrNull()?.let {
                                        matrix[index] = it
                                    }
                                }
                                onFilterChange(matrix)
                                this
                            },
                            startIcon = {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        val matrix = filter.newInstance().value as FloatArray
                                        text.split(", ").mapIndexed { index, num ->
                                            num.toFloatOrNull()?.let {
                                                matrix[index] = it
                                            }
                                        }
                                        onFilterChange(matrix)
                                    }
                                ) {
                                    Icon(Icons.Rounded.Done, null)
                                }
                            },
                            value = text,
                            label = { Text(stringResource(R.string.float_array_of)) }
                        )
                    }

                    is Float -> {
                        EnhancedSlider(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                .offset(y = (-2).dp),
                            enabled = !previewOnly,
                            value = sliderValue,
                            onValueChange = {
                                sliderValue = it.roundTo(filter.paramsInfo.first().roundTo)
                                onFilterChange(sliderValue)
                            },
                            valueRange = filter.paramsInfo.first().valueRange
                        )
                    }

                    is Pair<*, *> -> {
                        val value = filter.value as Pair<*, *>
                        if (value.first is Number && value.second is Number) {
                            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                            var sliderState2 by remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }

                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[0].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 16.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState1,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            roundTo = roundTo,
                                            valueRange = valueRange,
                                            valueState = sliderState1.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState1 = it
                                                onFilterChange(it to sliderState2)
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState1,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(sliderState1 to sliderState2)
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            filter.paramsInfo[1].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 16.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState2,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            roundTo = roundTo,
                                            valueRange = valueRange,
                                            valueState = sliderState2.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState2 = it
                                                onFilterChange(sliderState1 to it)
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState2,
                                onValueChange = {
                                    sliderState2 = it.roundTo(filter.paramsInfo[1].roundTo)
                                    onFilterChange(sliderState1 to sliderState2)
                                },
                                valueRange = filter.paramsInfo[1].valueRange
                            )
                        } else if (value.first is Color && value.second is Color) {
                            Box(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp
                                )
                            ) {
                                var color1 by remember(value) { mutableStateOf(value.first as Color) }
                                var color2 by remember(value) { mutableStateOf(value.second as Color) }

                                Column {
                                    HorizontalDivider()
                                    Text(
                                        text = stringResource(R.string.first_color),
                                        modifier = Modifier
                                            .padding(
                                                bottom = 16.dp,
                                                top = 16.dp,
                                                end = 16.dp,
                                            )
                                    )
                                    ColorSelectionRow(
                                        value = color1,
                                        allowScroll = !previewOnly,
                                        onValueChange = { color ->
                                            color1 = color
                                            onFilterChange(color1 to color2)
                                        }
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Text(
                                        text = stringResource(R.string.second_color),
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 16.dp,
                                                end = 16.dp
                                            )
                                    )
                                    ColorSelectionRow(
                                        value = color2,
                                        allowScroll = !previewOnly,
                                        onValueChange = { color ->
                                            color2 = color
                                            onFilterChange(color1 to color2)
                                        }
                                    )
                                }
                            }
                        } else if (value.first is Float && value.second is Color) {
                            var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                            var color1 by remember(value) { mutableStateOf(value.second as Color) }

                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[0].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 16.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState1,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            roundTo = roundTo,
                                            valueRange = valueRange,
                                            valueState = sliderState1.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState1 = it
                                                onFilterChange(sliderState1 to color1)
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState1,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(sliderState1 to color1)
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            Box(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp
                                )
                            ) {
                                Column {
                                    HorizontalDivider()
                                    Text(
                                        text = stringResource(filter.paramsInfo[1].title!!),
                                        modifier = Modifier
                                            .padding(
                                                bottom = 16.dp,
                                                top = 16.dp,
                                                end = 16.dp,
                                            )
                                    )
                                    ColorSelectionRow(
                                        value = color1,
                                        allowScroll = !previewOnly,
                                        allowAlpha = true,
                                        onValueChange = { color ->
                                            color1 = color
                                            onFilterChange(sliderState1 to color1)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is Triple<*, *, *> -> {
                        val value = filter.value as Triple<*, *, *>
                        if (value.first is Number && value.second is Number) {
                            var sliderState1 by remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                            var sliderState2 by remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
                            var sliderState3 by remember(value) { mutableFloatStateOf((value.third as Number).toFloat()) }

                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[0].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 16.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState1,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            valueRange = valueRange,
                                            roundTo = roundTo,
                                            valueState = sliderState1.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState1 = it
                                                onFilterChange(
                                                    Triple(
                                                        it,
                                                        sliderState2,
                                                        sliderState3
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState1,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            filter.paramsInfo[1].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState2,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            valueRange = valueRange,
                                            roundTo = roundTo,
                                            valueState = sliderState2.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState2 = it
                                                onFilterChange(
                                                    Triple(
                                                        sliderState1,
                                                        it,
                                                        sliderState3
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState2,
                                onValueChange = {
                                    sliderState2 = it.roundTo(filter.paramsInfo[1].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[1].valueRange
                            )
                            filter.paramsInfo[2].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 8.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState3,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            valueRange = valueRange,
                                            roundTo = roundTo,
                                            valueState = sliderState3.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState3 = it
                                                onFilterChange(
                                                    Triple(
                                                        sliderState1,
                                                        sliderState2,
                                                        it
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState3,
                                onValueChange = {
                                    sliderState3 = it.roundTo(filter.paramsInfo[2].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[2].valueRange
                            )
                        } else if (value.first is Number && value.second is Color && value.third is Color) {
                            var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                            var color1 by remember(value) { mutableStateOf(value.second as Color) }
                            var color2 by remember(value) { mutableStateOf(value.third as Color) }

                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[0].takeIf { it.title != null }
                                ?.let { (title, valueRange, roundTo) ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(Modifier.weight(1f)) {
                                            Text(
                                                text = stringResource(title!!),
                                                modifier = Modifier
                                                    .padding(
                                                        top = 16.dp,
                                                        end = 16.dp,
                                                        start = 16.dp
                                                    )
                                                    .weight(1f)
                                            )
                                        }
                                        var showValueDialog by remember { mutableStateOf(false) }
                                        ValueText(
                                            value = sliderState1,
                                            onClick = { showValueDialog = true }
                                        )
                                        ValueDialog(
                                            roundTo = roundTo,
                                            valueRange = valueRange,
                                            valueState = sliderState1.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
                                                sliderState1 = it
                                                onFilterChange(
                                                    Triple(
                                                        sliderState1,
                                                        color1,
                                                        color2
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            EnhancedSlider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp),
                                enabled = !previewOnly,
                                value = sliderState1,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(Triple(sliderState1, color1, color2))
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            Box(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp
                                )
                            ) {
                                Column {
                                    HorizontalDivider()
                                    Text(
                                        text = stringResource(filter.paramsInfo[1].title!!),
                                        modifier = Modifier
                                            .padding(
                                                bottom = 16.dp,
                                                top = 16.dp,
                                                end = 16.dp,
                                            )
                                    )
                                    ColorSelectionRow(
                                        value = color1,
                                        allowScroll = !previewOnly,
                                        allowAlpha = true,
                                        onValueChange = { color ->
                                            color1 = color
                                            onFilterChange(Triple(sliderState1, color1, color2))
                                        }
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Text(
                                        text = stringResource(filter.paramsInfo[2].title!!),
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 16.dp,
                                                end = 16.dp
                                            )
                                    )
                                    ColorSelectionRow(
                                        value = color2,
                                        allowScroll = !previewOnly,
                                        allowAlpha = true,
                                        onValueChange = { color ->
                                            color2 = color
                                            onFilterChange(Triple(sliderState1, color1, color2))
                                        }
                                    )
                                }
                                if (previewOnly) {
                                    Box(
                                        Modifier
                                            .matchParentSize()
                                            .pointerInput(Unit) {
                                                detectTapGestures { }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!filter.value.toString().contains("Color") && !previewOnly) {
            Box(
                Modifier
                    .height(if (filter.value is Unit) 32.dp else 64.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant())
                    .padding(start = 20.dp)
            )
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = onRemove
            ) {
                Icon(Icons.Rounded.RemoveCircleOutline, null)
            }
        }
    }
}

private fun Float.roundTo(digits: Int = 2) =
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))