package ru.tech.imageresizershrinker.filters_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.main_screen.components.AlphaColorCustomComponent
import ru.tech.imageresizershrinker.utils.coil.filters.ColorFilter
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

@Composable
fun FilterItem(
    filter: FilterTransformation,
    showDragHandle: Boolean,
    onRemove: () -> Unit,
    onFilterChange: (value: Float) -> Unit
) {
    val settingsState = LocalSettingsState.current
    Row(
        modifier = Modifier
            .block(
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f)
            )
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDragHandle) {
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
        Column(Modifier.weight(1f)) {
            var sliderValue by remember(filter) {
                mutableStateOf(
                    filter.value
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(filter.title),
                        modifier = Modifier
                            .padding(
                                top = 16.dp,
                                end = 16.dp,
                                start = 16.dp
                            )
                            .weight(1f)
                    )
                    if (filter is ColorFilter) {
                        IconButton(onClick = onRemove) {
                            Icon(Icons.Rounded.RemoveCircle, null)
                        }
                    }
                }
                if (filter !is ColorFilter) {
                    Text(
                        text = "$sliderValue",
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        ),
                        modifier = Modifier.padding(top = 16.dp),
                        lineHeight = 18.sp
                    )
                }
                Spacer(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        top = 16.dp,
                        end = 20.dp
                    )
                )
            }
            if (filter is ColorFilter) {
                Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                    AlphaColorCustomComponent(
                        color = filter.value.toInt(),
                        onColorChange = { color ->
                            onFilterChange(color.toFloat())
                        }
                    )
                }
            } else {
                Slider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = animateFloatAsState(sliderValue).value,
                    onValueChange = {
                        fun Float.roundToTwoDigits() = (this * 100.0f).roundToInt() / 100.0f
                        sliderValue = it.roundToTwoDigits()
                        onFilterChange(sliderValue)
                    },
                    valueRange = filter.valueRange
                )
            }
        }
        if (filter !is ColorFilter) {
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