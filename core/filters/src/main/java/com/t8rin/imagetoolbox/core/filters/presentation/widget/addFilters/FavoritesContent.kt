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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.addFilters

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiCubeLutFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.collectAsUiState
import com.t8rin.imagetoolbox.core.filters.presentation.widget.FilterSelectionItem
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkOff
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun FavoritesContent(
    component: AddFiltersSheetComponent,
    onVisibleChange: (Boolean) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
    onFilterPicked: (UiFilter<*>) -> Unit,
    previewBitmap: Bitmap?
) {
    val onRequestFilterMapping = component::filterToTransformation
    val favoriteFilters by component.favoritesFlow.collectAsUiState()
    val essentials = rememberLocalEssentials()

    AnimatedContent(
        targetState = favoriteFilters.isEmpty()
    ) { noFav ->
        if (noFav) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.no_favorite_filters),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.BookmarkOff,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2f)
                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                        .fillMaxSize()
                )
                Spacer(Modifier.weight(1f))
            }
        } else {
            val data = remember {
                mutableStateOf(favoriteFilters)
            }
            val haptics = LocalHapticFeedback.current
            val listState = rememberLazyListState()
            val state = rememberReorderableLazyListState(
                lazyListState = listState,
                onMove = { from, to ->
                    haptics.press()
                    data.value = data.value.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                }
            )
            LaunchedEffect(favoriteFilters) {
                if (data.value.size != favoriteFilters.size) {
                    data.value = favoriteFilters
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                ),
                contentPadding = PaddingValues(16.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = data.value,
                    key = { _, f -> f.hashCode() }
                ) { index, filter ->
                    ReorderableItem(
                        state = state,
                        key = filter.hashCode()
                    ) { isDragging ->
                        FilterSelectionItem(
                            filter = filter,
                            isFavoritePage = true,
                            canOpenPreview = previewBitmap != null,
                            favoriteFilters = favoriteFilters,
                            onLongClick = null,
                            onOpenPreview = {
                                component.setPreviewData(filter)
                            },
                            onClick = { custom ->
                                onVisibleChange(false)
                                if (custom != null) {
                                    onFilterPickedWithParams(custom)
                                } else {
                                    onFilterPicked(filter)
                                }
                            },
                            onRequestFilterMapping = onRequestFilterMapping,
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = favoriteFilters.size
                            ),
                            onToggleFavorite = {
                                component.toggleFavorite(filter)
                            },
                            modifier = Modifier
                                .longPressDraggableHandle(
                                    onDragStarted = {
                                        haptics.longPress()
                                    },
                                    onDragStopped = {
                                        component.reorderFavoriteFilters(data.value)
                                    }
                                )
                                .scale(
                                    animateFloatAsState(
                                        if (isDragging) 1.05f
                                        else 1f
                                    ).value
                                ),
                            cubeLutRemoteResources = if (filter is UiCubeLutFilter) {
                                component.cubeLutRemoteResources
                            } else null,
                            cubeLutDownloadProgress = if (filter is UiCubeLutFilter) {
                                component.cubeLutDownloadProgress
                            } else null,
                            onCubeLutDownloadRequest = { forceUpdate, downloadOnlyNewData ->
                                component.updateCubeLuts(
                                    startDownloadIfNeeded = true,
                                    forceUpdate = forceUpdate,
                                    onFailure = essentials::showFailureToast,
                                    downloadOnlyNewData = downloadOnlyNewData
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}