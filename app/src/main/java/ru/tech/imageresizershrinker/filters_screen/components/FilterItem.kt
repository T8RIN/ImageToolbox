@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.filters_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.AlphaColorCustomComponent
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun <T> FilterItem(
    filter: FilterTransformation<T>,
    showDragHandle: Boolean,
    onRemove: () -> Unit,
    previewOnly: Boolean = false,
    onFilterChange: (value: Any) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme
        .colorScheme
        .secondaryContainer
        .copy(0.2f)
) {
    val settingsState = LocalSettingsState.current
    Row(
        modifier = modifier
            .block(color = backgroundColor)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDragHandle && filter.value !is Color) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.DragHandle, null)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .height(48.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
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
                    (filter.value as? Float) ?: 0f
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(filter.title),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                end = 28.dp,
                                start = 16.dp
                            )
                            .weight(1f)
                    )
                    if (filter.value is Color && !previewOnly) {
                        IconButton(onClick = onRemove) {
                            Icon(Icons.Rounded.RemoveCircle, null)
                        }
                    }
                }
                if (filter.value is Number) {
                    Text(
                        text = "$sliderValue",
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.padding(top = 16.dp),
                        lineHeight = 18.sp
                    )
                    Spacer(
                        modifier = Modifier.padding(
                            start = 4.dp,
                            top = 16.dp,
                            end = 20.dp
                        )
                    )
                }
            }
            if (filter.value is Unit) {
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                when (filter.value) {
                    is Color -> {
                        Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                            AlphaColorCustomComponent(
                                color = (filter.value as Color).toArgb(),
                                onColorChange = { c, alpha ->
                                    onFilterChange(Color(c).copy(alpha / 255f))
                                }
                            )
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

                    is FloatArray -> {
                        val value = filter.value as FloatArray
                        var text by rememberSaveable(filter) {
                            mutableStateOf(
                                value.let {
                                    var string = ""
                                    it.forEachIndexed { index, float ->
                                        string += "$float, "
                                        if (index % 4 == 3) string += "\n"
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
                                val matrix = floatArrayOf(
                                    1.0f, 0.0f, 0.0f, 0.0f,
                                    0.0f, 1.0f, 0.0f, 0.0f,
                                    0.0f, 0.0f, 1.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f, 1.0f
                                )
                                split(", ").mapIndexed { index, num ->
                                    num.toFloatOrNull()?.let {
                                        matrix[index] = it
                                    }
                                }
                                onFilterChange(matrix)
                                this
                            },
                            startIcon = {
                                IconButton(
                                    onClick = {
                                        val matrix = floatArrayOf(
                                            1.0f, 0.0f, 0.0f, 0.0f,
                                            0.0f, 1.0f, 0.0f, 0.0f,
                                            0.0f, 0.0f, 1.0f, 0.0f,
                                            0.0f, 0.0f, 0.0f, 1.0f
                                        )
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(sliderValue).value,
                            onValueChange = {
                                sliderValue = it.roundTo(filter.paramsInfo.first().roundTo)
                                onFilterChange(sliderValue)
                            },
                            valueRange = filter.paramsInfo.first().valueRange
                        )
                    }

                    is Pair<*, *> -> {
                        val value = filter.value as? Pair<Number, Number>
                        if (value != null) {
                            var sliderState1 by remember(value) { mutableFloatStateOf(value.first.toFloat()) }
                            var sliderState2 by remember(value) { mutableFloatStateOf(value.second.toFloat()) }

                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[0].title?.let {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(it),
                                            modifier = Modifier
                                                .padding(
                                                    top = 16.dp,
                                                    end = 16.dp,
                                                    start = 16.dp
                                                )
                                                .weight(1f)
                                        )
                                    }
                                    Text(
                                        text = "$sliderState1",
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.padding(top = 16.dp),
                                        lineHeight = 18.sp
                                    )
                                    Spacer(
                                        modifier = Modifier.padding(
                                            start = 4.dp,
                                            top = 16.dp,
                                            end = 20.dp
                                        )
                                    )
                                }
                            }
                            Slider(
                                enabled = !previewOnly,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                value = animateFloatAsState(sliderState1).value,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(sliderState1 to sliderState2)
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            Spacer(Modifier.height(8.dp))
                            filter.paramsInfo[1].title?.let {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(Modifier.weight(1f)) {
                                        Text(
                                            text = stringResource(it),
                                            modifier = Modifier
                                                .padding(
                                                    top = 16.dp,
                                                    end = 16.dp,
                                                    start = 16.dp
                                                )
                                                .weight(1f)
                                        )
                                    }
                                    Text(
                                        text = "$sliderState2",
                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.5f
                                        ),
                                        modifier = Modifier.padding(top = 16.dp),
                                        lineHeight = 18.sp
                                    )
                                    Spacer(
                                        modifier = Modifier.padding(
                                            start = 4.dp,
                                            top = 16.dp,
                                            end = 20.dp
                                        )
                                    )
                                }
                            }
                            Slider(
                                enabled = !previewOnly,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                value = animateFloatAsState(sliderState2).value,
                                onValueChange = {
                                    sliderState2 = it.roundTo(filter.paramsInfo[1].roundTo)
                                    onFilterChange(sliderState1 to sliderState2)
                                },
                                valueRange = filter.paramsInfo[1].valueRange
                            )
                        }
                    }
                }
            }
        }
        if (filter.value !is Color && !previewOnly) {
            Box(
                Modifier
                    .height(48.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(start = 20.dp)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Rounded.RemoveCircle, null)
            }
        }
    }
}

private fun Float.roundTo(digits: Int = 2) =
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))
