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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Reorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun FilterReorderSheet(
    filterList: List<UiFilter<*>>,
    visible: Boolean,
    onDismiss: () -> Unit,
    onReorder: (List<UiFilter<*>>) -> Unit
) {
    EnhancedModalBottomSheet(
        sheetContent = {
            if (filterList.size < 2) onDismiss()

            Box {
                val data = remember { mutableStateOf(filterList) }
                val listState = rememberLazyListState()
                val haptics = LocalHapticFeedback.current
                val state = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        haptics.press()
                        data.value = data.value.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    },
                    lazyListState = listState
                )
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = data.value,
                        key = { _, v -> v.hashCode() }
                    ) { index, filter ->
                        ReorderableItem(
                            state = state,
                            key = filter.hashCode()
                        ) { isDragging ->
                            FilterItem(
                                filter = filter,
                                onFilterChange = {},
                                modifier = Modifier
                                    .longPressDraggableHandle(
                                        onDragStarted = {
                                            haptics.longPress()
                                        },
                                        onDragStopped = {
                                            onReorder(data.value)
                                        }
                                    )
                                    .scale(
                                        animateFloatAsState(
                                            if (isDragging) 1.05f
                                            else 1f
                                        ).value
                                    ),
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = data.value.size
                                ),
                                previewOnly = true,
                                showDragHandle = filterList.size >= 2,
                                onRemove = {},
                                onCreateTemplate = null
                            )
                        }
                    }
                }
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = stringResource(R.string.reorder),
                icon = Icons.Rounded.Reorder
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
}