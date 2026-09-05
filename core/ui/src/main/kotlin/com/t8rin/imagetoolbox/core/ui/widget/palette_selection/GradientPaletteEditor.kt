/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.palette_selection

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Done
import com.t8rin.imagetoolbox.core.resources.icons.SwapHoriz
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelection
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun GradientPaletteEditor(
    value: GradientPalette,
    onValueChange: (GradientPalette) -> Unit
) {
    var stops by remember { mutableStateOf(value.colors.mapIndexed(::PaletteColor)) }
    var nextId by remember { mutableIntStateOf(stops.size) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val haptics = LocalHapticFeedback.current
    val state = rememberLazyListState()

    LaunchedEffect(value) {
        if (stops.map { it.color } != value.colors) {
            stops = value.colors.map { PaletteColor(nextId++, it) }
            selectedIndex = 0
        }
    }
    fun update(colors: List<PaletteColor>, selection: Int = selectedIndex) {
        stops = colors
        selectedIndex = selection.coerceIn(colors.indices)
        onValueChange(GradientPalette.fromColors(colors.map { it.color }))
    }

    val selected = stops[selectedIndex.coerceIn(stops.indices)]
    val reorderable = rememberReorderableLazyListState(state) { from, to ->
        val updated = stops.toMutableList().apply { add(to.index, removeAt(from.index)) }
        haptics.press()
        update(updated, updated.indexOfFirst { it.id == selected.id })
    }
    LaunchedEffect(selected.id, selectedIndex, reorderable.isAnyItemDragging) {
        if (!reorderable.isAnyItemDragging && state.layoutInfo.visibleItemsInfo.none { it.key == selected.id }) {
            state.animateScrollToItem(selectedIndex.coerceIn(stops.indices))
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier.container(
                shape = ShapeDefaults.extraLarge,
                resultPadding = 12.dp
            )
        ) {
            GradientPalettePreview(
                palette = value,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(ShapeDefaults.small)
            )
            TitleItem(
                text = stringResource(R.string.color_stops),
                modifier = Modifier.padding(top = 4.dp),
                endContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EnhancedIconButton(
                            onClick = { update(stops.reversed(), stops.lastIndex - selectedIndex) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SwapHoriz,
                                contentDescription = null
                            )
                        }
                        EnhancedIconButton(
                            enabled = stops.size > 2,
                            onClick = { update(stops.filterNot { it.id == selected.id }) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null
                            )
                        }
                        EnhancedIconButton(
                            onClick = {
                                val next =
                                    stops.getOrNull(selectedIndex + 1)?.color ?: selected.color
                                val color = ColorModel(
                                    lerp(selected.color.toColor(), next.toColor(), 0.5f).toArgb()
                                )
                                update(
                                    stops.toMutableList().apply {
                                        add(selectedIndex + 1, PaletteColor(nextId++, color))
                                    },
                                    selectedIndex + 1
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
            LazyRow(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .fadingEdges(state),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(stops, key = { _, stop -> stop.id }) { index, stop ->
                    ReorderableItem(reorderable, key = stop.id) { dragging ->
                        val isSelected = stop.id == selected.id
                        val color = stop.color.toColor()
                        val shape = if (isSelected) ShapeDefaults.small else ShapeDefaults.circle
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .aspectRatio(animateFloatAsState(if (isSelected) 1.5f else 1f).value)
                                .scale(
                                    animateFloatAsState(
                                        if (!reorderable.isAnyItemDragging || dragging) 1f else 0.8f
                                    ).value
                                )
                                .container(shape = shape, color = color, resultPadding = 0.dp)
                                .clip(shape)
                                .transparencyChecker()
                                .background(color)
                                .hapticsClickable { selectedIndex = index }
                                .longPressDraggableHandle(onDragStarted = { haptics.longPress() })
                                .animateItem(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = null,
                                    tint = color.inverse(
                                        fraction = { if (it) 0.8f else 0.5f },
                                        darkMode = color.luminance() < 0.3f
                                    ),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.gradient_colors_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        ColorSelection(
            value = selected.color.toColor(),
            onValueChange = { color ->
                update(stops.map {
                    if (it.id == selected.id) it.copy(color = ColorModel(color.toArgb())) else it
                })
            },
            withAlpha = true
        )
    }
}

private data class PaletteColor(
    val id: Int,
    val color: ColorModel
)