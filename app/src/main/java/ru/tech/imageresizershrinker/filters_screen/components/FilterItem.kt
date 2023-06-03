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
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.AlphaColorCustomComponent
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.HazeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.utils.coil.filters.WhiteBalanceFilter
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
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
                                end = 16.dp,
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
                if (filter.value is Float) {
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
            if (filter.value is Color) {
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
            } else if (filter.value !is Unit) {
                when (filter) {
                    is WhiteBalanceFilter -> {
                        var temp by remember(filter) {
                            mutableFloatStateOf(filter.value.first)
                        }
                        var tint by remember(filter) {
                            mutableFloatStateOf(filter.value.second)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.temperature),
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
                                text = "$temp",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(temp).value,
                            onValueChange = {
                                temp = it.roundToInt().toFloat()
                                onFilterChange(temp to tint)
                            },
                            valueRange = filter.valueRange
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.tint),
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
                                text = "$tint",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(tint).value,
                            onValueChange = {
                                tint = it.roundToInt().toFloat()
                                onFilterChange(temp to tint)
                            },
                            valueRange = -100f..100f
                        )
                    }

                    is HighlightsAndShadowsFilter -> {
                        var highs by remember(filter) {
                            mutableFloatStateOf(filter.value.first)
                        }
                        var shadows by remember(filter) {
                            mutableFloatStateOf(filter.value.second)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.highlights),
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
                                text = "$highs",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(highs).value,
                            onValueChange = {
                                highs = it.roundToTwoDigits()
                                onFilterChange(highs to shadows)
                            },
                            valueRange = filter.valueRange
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.shadows),
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
                                text = "$shadows",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(shadows).value,
                            onValueChange = {
                                shadows = it.roundToTwoDigits()
                                onFilterChange(highs to shadows)
                            },
                            valueRange = filter.valueRange
                        )
                    }

                    is HazeFilter -> {
                        var distance by remember(filter) {
                            mutableFloatStateOf(filter.value.first)
                        }
                        var slope by remember(filter) {
                            mutableFloatStateOf(filter.value.second)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.distance),
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
                                text = "$distance",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(distance).value,
                            onValueChange = {
                                distance = it.roundToTwoDigits()
                                onFilterChange(distance to slope)
                            },
                            valueRange = filter.valueRange
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.slope),
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
                                text = "$slope",
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
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(slope).value,
                            onValueChange = {
                                slope = it.roundToTwoDigits()
                                onFilterChange(distance to slope)
                            },
                            valueRange = filter.valueRange
                        )
                    }

                    else -> {
                        Slider(
                            enabled = !previewOnly,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            value = animateFloatAsState(sliderValue).value,
                            onValueChange = {
                                sliderValue = it.roundToTwoDigits()
                                onFilterChange(sliderValue)
                            },
                            valueRange = filter.valueRange
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
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

private fun Float.roundToTwoDigits() = (this * 100.0f).roundToInt() / 100.0f