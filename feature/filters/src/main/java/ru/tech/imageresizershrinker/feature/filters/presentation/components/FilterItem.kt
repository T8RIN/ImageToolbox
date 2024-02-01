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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import ru.tech.imageresizershrinker.core.filters.domain.model.TiltShiftParams
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
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
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
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    previewOnly: Boolean = false,
    onFilterChange: (value: Any) -> Unit,
    backgroundColor: Color = MaterialTheme
        .colorScheme
        .surfaceContainerLow,
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
        val interactionSource = remember {
            MutableInteractionSource()
        }
        Column(
            Modifier
                .weight(1f)
                .alpha(if (previewOnly) 0.5f else 1f)
                .clickable(
                    enabled = previewOnly,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {}
                )
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
                    if (!filter.value.isSingle() && !previewOnly) {
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
                                onValueChange = onFilterChange
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

                            value.first is Color && value.second is Color -> {
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
                            }

                            value.first is Float && value.second is Color -> {
                                var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                                var color1 by remember(value) { mutableStateOf(value.second as Color) }

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
                                        onFilterChange(sliderState1 to color1)
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
                                            applyHorPadding = false,
                                            startContent = {},
                                            resultModifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                            }
                        }
                    }

                    is Triple<*, *, *> -> {
                        val value = filter.value as Triple<*, *, *>
                        when {
                            value.first is Number && value.second is Number && value.third is Number -> {
                                val sliderState1: MutableState<Float> =
                                    remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                                val sliderState2: MutableState<Float> =
                                    remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
                                val sliderState3: MutableState<Float> =
                                    remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }

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

                            value.first is Number && value.second is Number && value.third is Color -> {
                                val sliderState1: MutableState<Float> =
                                    remember(value) { mutableFloatStateOf((value.first as Number).toFloat()) }
                                val sliderState2: MutableState<Float> =
                                    remember(value) { mutableFloatStateOf((value.second as Number).toFloat()) }
                                var color3 by remember(value) { mutableStateOf(value.third as Color) }

                                LaunchedEffect(
                                    sliderState1.value,
                                    sliderState2.value,
                                    color3
                                ) {
                                    onFilterChange(
                                        Triple(sliderState1.value, sliderState2.value, color3)
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
                                Text(
                                    text = stringResource(filter.paramsInfo[2].title!!),
                                    modifier = Modifier.padding(16.dp)
                                )
                                ColorSelectionRow(
                                    value = color3,
                                    allowScroll = !previewOnly,
                                    allowAlpha = false,
                                    onValueChange = { color ->
                                        color3 = color
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                )
                            }

                            value.first is Number && value.second is Color && value.third is Color -> {
                                var sliderState1 by remember { mutableFloatStateOf((value.first as Number).toFloat()) }
                                var color1 by remember(value) { mutableStateOf(value.second as Color) }
                                var color2 by remember(value) { mutableStateOf(value.third as Color) }

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
                                        onFilterChange(Triple(sliderState1, color1, color2))
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
                                }
                            }
                        }
                    }

                    is TiltShiftParams -> {
                        val value = filter.value as TiltShiftParams

                        val blurRadius: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.blurRadius as Number).toFloat()) }
                        val sigma: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.sigma as Number).toFloat()) }
                        val anchorX: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.anchorX as Number).toFloat()) }
                        val anchorY: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.anchorY as Number).toFloat()) }
                        val holeRadius: MutableState<Float> =
                            remember(value) { mutableFloatStateOf((value.holeRadius as Number).toFloat()) }

                        LaunchedEffect(
                            blurRadius.value,
                            sigma.value,
                            anchorX.value,
                            anchorY.value,
                            holeRadius.value
                        ) {
                            onFilterChange(
                                TiltShiftParams(
                                    blurRadius = blurRadius.value,
                                    sigma = sigma.value,
                                    anchorX = anchorX.value,
                                    anchorY = anchorY.value,
                                    holeRadius = holeRadius.value
                                )
                            )
                        }

                        val paramsInfo by remember(filter) {
                            derivedStateOf {
                                filter.paramsInfo.mapIndexedNotNull { index, filterParam ->
                                    if (filterParam.title == null) return@mapIndexedNotNull null
                                    when (index) {
                                        0 -> blurRadius
                                        1 -> sigma
                                        2 -> anchorX
                                        3 -> anchorY
                                        else -> holeRadius
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
                }
            }
        }
        if (filter.value.isSingle() && !previewOnly) {
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

private fun Any?.isSingle(): Boolean {
    return when (this) {
        null -> return false
        is Pair<*, *> -> false
        is Triple<*, *, *> -> false
        is Color -> false
        is TiltShiftParams -> false
        else -> true
    }
}

private fun Float.roundTo(digits: Int = 2) =
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))