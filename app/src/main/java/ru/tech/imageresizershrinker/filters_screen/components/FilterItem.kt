package ru.tech.imageresizershrinker.filters_screen.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ViewArray
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.AlphaColorCustomComponent
import ru.tech.imageresizershrinker.main_screen.components.ColorCustomComponent
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.RGBFilter
import ru.tech.imageresizershrinker.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun <T> FilterItem(
    filter: FilterTransformation<T>,
    showDragHandle: Boolean,
    onRemove: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    previewOnly: Boolean = false,
    onFilterChange: (value: Any) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme
        .colorScheme
        .secondaryContainer
        .copy(0.2f)
) {
    val settingsState = LocalSettingsState.current
    Row(
        modifier = modifier
            .block(color = backgroundColor, shape = MaterialTheme.shapes.large)
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
        if (showDragHandle && filter.value !is Color) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Rounded.DragHandle, null)
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .height(64.dp)
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
                    if (filter.value.toString().contains("Color") && !previewOnly) {
                        IconButton(onClick = onRemove) {
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
                        onValueUpdate = { onFilterChange(it) }
                    )
                }
            }
            if (filter.value is Unit) {
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                when (filter.value) {
                    is Color -> {
                        Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                            if (filter is RGBFilter) {
                                ColorCustomComponent(
                                    color = filter.value.toArgb(),
                                    onColorChange = { c ->
                                        onFilterChange(Color(c))
                                    }
                                )
                            } else {
                                AlphaColorCustomComponent(
                                    color = (filter.value as Color).toArgb(),
                                    onColorChange = { c, alpha ->
                                        onFilterChange(Color(c).copy(alpha / 255f))
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
                                IconButton(
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
                        Slider(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                .offset(y = (-2).dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                                .height(40.dp)
                                .border(
                                    width = settingsState.borderWidth,
                                    color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                    shape = CircleShape
                                )
                                .padding(horizontal = 16.dp),
                            colors = SliderDefaults.colors(
                                inactiveTrackColor =
                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                            ),
                            enabled = !previewOnly,
                            value = animateFloatAsState(sliderValue).value,
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
                                            onValueUpdate = { onFilterChange(it to sliderState2) }
                                        )
                                    }
                                }
                            Slider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                enabled = !previewOnly,
                                value = animateFloatAsState(sliderState1).value,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(sliderState1 to sliderState2)
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            Spacer(Modifier.height(8.dp))
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
                                            onValueUpdate = { onFilterChange(sliderState1 to it) }
                                        )
                                    }
                                }
                            Slider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                enabled = !previewOnly,
                                value = animateFloatAsState(sliderState2).value,
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
                                    top = 8.dp,
                                    end = 16.dp
                                )
                            ) {
                                var color1 by remember(value) { mutableStateOf(value.first as Color) }
                                var color2 by remember(value) { mutableStateOf(value.second as Color) }

                                Column {
                                    Divider()
                                    Text(
                                        text = stringResource(R.string.first_color),
                                        modifier = Modifier
                                            .padding(
                                                bottom = 16.dp,
                                                top = 16.dp,
                                                end = 16.dp,
                                            )
                                    )
                                    ColorCustomComponent(
                                        color = color1.toArgb(),
                                        onColorChange = { c ->
                                            color1 = Color(c)
                                            onFilterChange(color1 to color2)
                                        }
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Divider()
                                    Text(
                                        text = stringResource(R.string.second_color),
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 16.dp,
                                                end = 16.dp
                                            )
                                    )
                                    ColorCustomComponent(
                                        color = color2.toArgb(),
                                        onColorChange = { c ->
                                            color2 = Color(c)
                                            onFilterChange(color1 to color2)
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
                            Slider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                enabled = !previewOnly,
                                value = animateFloatAsState(sliderState1).value,
                                onValueChange = {
                                    sliderState1 = it.roundTo(filter.paramsInfo[0].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[0].valueRange
                            )
                            Spacer(Modifier.height(8.dp))
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
                                            valueRange = valueRange,
                                            roundTo = roundTo,
                                            valueState = sliderState2.toString(),
                                            expanded = showValueDialog && !previewOnly,
                                            onDismiss = { showValueDialog = false },
                                            onValueUpdate = {
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
                            Slider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                enabled = !previewOnly,
                                value = animateFloatAsState(sliderState2).value,
                                onValueChange = {
                                    sliderState2 = it.roundTo(filter.paramsInfo[1].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[1].valueRange
                            )
                            Spacer(Modifier.height(8.dp))
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
                                                        top = 16.dp,
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
                            Slider(
                                modifier = Modifier
                                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                                    .offset(y = (-2).dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = CircleShape
                                    )
                                    .height(40.dp)
                                    .border(
                                        width = settingsState.borderWidth,
                                        color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                                        shape = CircleShape
                                    )
                                    .padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTrackColor =
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                enabled = !previewOnly,

                                value = animateFloatAsState(sliderState3).value,
                                onValueChange = {
                                    sliderState3 = it.roundTo(filter.paramsInfo[2].roundTo)
                                    onFilterChange(Triple(sliderState1, sliderState2, sliderState3))
                                },
                                valueRange = filter.paramsInfo[2].valueRange
                            )
                        }
                    }
                }
            }
        }
        if (!filter.value.toString().contains("Color") && !previewOnly) {
            Box(
                Modifier
                    .height(64.dp)
                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant())
                    .padding(start = 20.dp)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Rounded.RemoveCircleOutline, null)
            }
        }
    }
}

private fun Float.roundTo(digits: Int = 2) =
    (this * 10f.pow(digits)).roundToInt() / (10f.pow(digits))

@Composable
private fun ValueText(
    value: Float,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$value",
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.5f
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .clip(CircleShape)
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 6.dp),
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

@Composable
private fun ValueDialog(
    roundTo: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    valueState: String,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onValueUpdate: (Float) -> Unit
) {
    if (expanded) {
        var value by remember(valueState) { mutableStateOf(valueState) }
        AlertDialog(
            modifier = Modifier.alertDialog(),
            onDismissRequest = onDismiss,
            icon = {
                Icon(Icons.Outlined.ViewArray, null)
            },
            title = {
                Text(stringResource(R.string.color))
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        shape = RoundedCornerShape(16.dp),
                        value = value,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                        maxLines = 1,
                        onValueChange = { number ->
                            var tempS = number.trim {
                                it !in listOf(
                                    '1',
                                    '2',
                                    '3',
                                    '4',
                                    '5',
                                    '6',
                                    '7',
                                    '8',
                                    '9',
                                    '0',
                                    '.',
                                    '-'
                                )
                            }
                            tempS = (if (tempS.firstOrNull() == '-') "-" else "").plus(
                                tempS.replace("-", "")
                            )
                            val temp = tempS.split(".")
                            value = when (temp.size) {
                                1 -> temp[0]
                                2 -> temp[0] + "." + temp[1]
                                else -> {
                                    temp[0] + "." + temp[1] + temp.drop(2).joinToString("")
                                }
                            }
                        }
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onValueUpdate(
                            (value.toFloatOrNull() ?: 0f).roundTo(roundTo).coerceIn(valueRange)
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    border = BorderStroke(
                        LocalSettingsState.current.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(
                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ),
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
