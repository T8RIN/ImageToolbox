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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.History
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
internal fun RecentAndFavoriteGradientsCard(
    value: GradientPalette,
    onValueChange: (GradientPalette) -> Unit
) {
    val settings = LocalSettingsState.current
    val interactor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val recent = settings.recentGradients
    val favorites = settings.favoriteGradients
    BoxAnimatedVisibility(recent.isNotEmpty() || favorites.isNotEmpty()) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .container(shape = ShapeDefaults.extraLarge, resultPadding = 0.dp)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BoxAnimatedVisibility(recent.isNotEmpty()) {
                Column {
                    TitleItem(
                        text = stringResource(R.string.recently_used),
                        icon = Icons.Rounded.History,
                        modifier = Modifier,
                        endContent = {
                            EnhancedIconButton(
                                modifier = Modifier.offset(x = 8.dp),
                                onClick = { scope.launch { interactor.clearRecentGradients() } }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = null,
                                    tint = takeColorFromScheme { primary.blend(error, 0.8f) }
                                )
                            }
                        }
                    )
                    GradientPaletteRow(
                        value = value,
                        onValueChange = onValueChange,
                        palettes = recent,
                        contentPadding = PaddingValues(),
                        showLabels = false
                    )
                }
            }
            BoxAnimatedVisibility(favorites.isNotEmpty()) {
                Column {
                    TitleItem(
                        text = stringResource(R.string.favorite),
                        icon = Icons.Outlined.Bookmark,
                        modifier = Modifier
                    )
                    val state = rememberLazyListState()
                    var items by remember(favorites) { mutableStateOf(favorites) }
                    val haptics = LocalHapticFeedback.current
                    val reorderable = rememberReorderableLazyListState(state) { from, to ->
                        haptics.press()
                        items = items.toMutableList().apply { add(to.index, removeAt(from.index)) }
                    }
                    LazyRow(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 11.dp)
                            .fadingEdges(state),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        items(items, key = GradientPalette::toSerializedString) { palette ->
                            ReorderableItem(
                                reorderable,
                                key = palette.toSerializedString()
                            ) { dragging ->
                                GradientPaletteItem(
                                    palette = palette,
                                    selected = palette == value,
                                    onClick = { onValueChange(palette) },
                                    modifier = Modifier
                                        .scale(
                                            animateFloatAsState(
                                                if (!reorderable.isAnyItemDragging || dragging) 1f else 0.8f
                                            ).value
                                        )
                                        .longPressDraggableHandle(
                                            onDragStarted = { haptics.longPress() },
                                            onDragStopped = {
                                                val ordered = items
                                                scope.launch {
                                                    interactor.updateFavoriteGradients(
                                                        ordered
                                                    )
                                                }
                                            }
                                        )
                                        .animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}