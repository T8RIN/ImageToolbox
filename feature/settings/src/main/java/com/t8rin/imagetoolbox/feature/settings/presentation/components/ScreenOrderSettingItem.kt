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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatLineSpacing
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BatchPrediction
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Stacks
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ScreenOrderSettingItem(
    onValueChange: (List<Screen>) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val screenList by remember(settingsState.screenList, settingsState.favoriteScreenList) {
        derivedStateOf {
            val fav = settingsState.favoriteScreenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }

            val other = settingsState.screenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }.ifEmpty { Screen.entries }.filter {
                it !in fav
            }

            fav.plus(other).distinctBy { it.id }
        }
    }
    var showArrangementSheet by rememberSaveable { mutableStateOf(false) }

    val essentials = rememberLocalEssentials()

    PreferenceItem(
        shape = shape,
        modifier = modifier,
        onClick = {
            showArrangementSheet = true
        },
        onDisabledClick = {
            essentials.showToast(
                icon = Icons.Outlined.BatchPrediction,
                message = essentials.getString(R.string.cannot_change_arrangement_while_options_grouping_enabled)
            )
        },
        enabled = !settingsState.groupOptionsByTypes,
        startIcon = Icons.Outlined.FormatLineSpacing,
        title = stringResource(R.string.order),
        subtitle = stringResource(R.string.order_sub),
        endIcon = Icons.Rounded.MiniEdit,
    )

    EnhancedModalBottomSheet(
        visible = showArrangementSheet,
        onDismiss = {
            showArrangementSheet = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.order),
                icon = Icons.Rounded.Stacks
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showArrangementSheet = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                val data = remember(screenList) { mutableStateOf(screenList) }
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
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    itemsIndexed(
                        items = data.value,
                        key = { _, s -> s.id }
                    ) { index, screen ->
                        ReorderableItem(
                            state = state,
                            key = screen.id
                        ) { isDragging ->
                            PreferenceItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .longPressDraggableHandle(
                                        onDragStarted = {
                                            haptics.longPress()
                                        },
                                        onDragStopped = {
                                            onValueChange(data.value)
                                        }
                                    )
                                    .scale(
                                        animateFloatAsState(
                                            if (isDragging) 1.05f
                                            else 1f
                                        ).value
                                    ),
                                title = stringResource(screen.title),
                                subtitle = stringResource(screen.subtitle),
                                startIcon = screen.icon,
                                endIcon = Icons.Rounded.DragHandle,
                                containerColor = if (screen.id in settingsState.favoriteScreenList) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else Color.Unspecified,
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = data.value.size
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}