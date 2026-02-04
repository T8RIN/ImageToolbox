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

package com.t8rin.imagetoolbox.feature.filters.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
fun MaskReorderSheet(
    maskList: List<UiFilterMask>,
    visible: Boolean,
    onDismiss: () -> Unit,
    onReorder: (List<UiFilterMask>) -> Unit
) {
    EnhancedModalBottomSheet(
        sheetContent = {
            if (maskList.size < 2) onDismiss()

            Box {
                val data = remember { mutableStateOf(maskList) }
                val listState = rememberLazyListState()
                val haptics = LocalHapticFeedback.current
                val state = rememberReorderableLazyListState(
                    lazyListState = listState,
                    onMove = { from, to ->
                        haptics.press()
                        data.value = data.value.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    }
                )
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = data.value,
                        key = { _, it -> it.hashCode() }
                    ) { index, mask ->
                        ReorderableItem(
                            state = state,
                            key = mask.hashCode()
                        ) { isDragging ->
                            MaskItem(
                                mask = mask,
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                onMaskChange = {},
                                previewOnly = true,
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = data.value.size
                                ),
                                titleText = stringResource(R.string.mask_indexed, index + 1),
                                showDragHandle = maskList.size >= 2,
                                onRemove = {},
                                addMaskSheetComponent = null,
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