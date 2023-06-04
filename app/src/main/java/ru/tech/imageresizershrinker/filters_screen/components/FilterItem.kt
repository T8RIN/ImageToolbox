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
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.AlphaColorCustomComponent
import ru.tech.imageresizershrinker.utils.coil.filters.CrosshatchFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SlowBlurFilter
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.HalftoneFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HazeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.utils.coil.filters.KuwaharaFilter
import ru.tech.imageresizershrinker.utils.coil.filters.VignetteFilter
import ru.tech.imageresizershrinker.utils.coil.filters.WhiteBalanceFilter
import ru.tech.imageresizershrinker.utils.modifier.block
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

                !is Unit -> {
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
                                    highs = it.roundTo()
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
                                    shadows = it.roundTo()
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
                                    distance = it.roundTo()
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
                                    slope = it.roundTo()
                                    onFilterChange(distance to slope)
                                },
                                valueRange = filter.valueRange
                            )
                        }

                        is CrosshatchFilter -> {
                            var spacing by remember(filter) {
                                mutableFloatStateOf(filter.value.first)
                            }
                            var width by remember(filter) {
                                mutableFloatStateOf(filter.value.second)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.spacing),
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
                                    text = "$spacing",
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
                                value = animateFloatAsState(spacing).value,
                                onValueChange = {
                                    spacing = it.roundTo(3)
                                    onFilterChange(spacing to width)
                                },
                                valueRange = filter.valueRange
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.line_width),
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
                                    text = "$width",
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
                                value = animateFloatAsState(width).value,
                                onValueChange = {
                                    width = it.roundTo(3)
                                    onFilterChange(spacing to width)
                                },
                                valueRange = filter.valueRange
                            )
                        }

                        is VignetteFilter -> {
                            var start by remember(filter) {
                                mutableFloatStateOf(filter.value.first)
                            }
                            var end by remember(filter) {
                                mutableFloatStateOf(filter.value.second)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.start),
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
                                    text = "$start",
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
                                value = animateFloatAsState(start).value,
                                onValueChange = {
                                    start = it.roundTo()
                                    onFilterChange(start to end)
                                },
                                valueRange = filter.valueRange
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.end),
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
                                    text = "$end",
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
                                value = animateFloatAsState(end).value,
                                onValueChange = {
                                    end = it.roundTo()
                                    onFilterChange(start to end)
                                },
                                valueRange = filter.valueRange
                            )
                        }

                        is KuwaharaFilter -> {
                            Slider(
                                enabled = !previewOnly,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                value = animateFloatAsState(sliderValue).value,
                                onValueChange = {
                                    sliderValue = it.roundToInt().toFloat()
                                    onFilterChange(sliderValue)
                                },
                                valueRange = filter.valueRange
                            )
                        }

                        is SlowBlurFilter -> {
                            var scale by remember(filter) {
                                mutableFloatStateOf(filter.value.first)
                            }
                            var radius by remember(filter) {
                                mutableFloatStateOf(filter.value.second.toFloat())
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.scale),
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
                                    text = "$scale",
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
                                value = animateFloatAsState(scale).value,
                                onValueChange = {
                                    scale = it.roundTo()
                                    onFilterChange(scale to radius.toInt())
                                },
                                valueRange = 0f..1f
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(R.string.radius),
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
                                    text = "$radius",
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
                                value = animateFloatAsState(radius).value,
                                onValueChange = {
                                    radius = it.roundToInt().toFloat()
                                    onFilterChange(scale to radius.toInt())
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
                                    sliderValue = it.roundTo(if (filter is HalftoneFilter) 4 else 2)
                                    onFilterChange(sliderValue)
                                },
                                valueRange = filter.valueRange
                            )
                        }
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.height(16.dp))
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