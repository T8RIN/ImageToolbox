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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentPasteGo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@Composable
fun RecentAndFavoriteColorsCard(
    onFavoriteColorClick: (Color) -> Unit,
    onRecentColorClick: (Color) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val recentColors = settingsState.recentColors
    val favoriteColors = settingsState.favoriteColors
    val interactor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()

    BoxAnimatedVisibility(
        visible = recentColors.isNotEmpty() || favoriteColors.isNotEmpty()
    ) {
        val itemWidth = with(LocalDensity.current) { 48.dp.toPx() }

        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .container(
                    shape = ShapeDefaults.extraLarge,
                    resultPadding = 0.dp
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BoxAnimatedVisibility(recentColors.isNotEmpty()) {
                Column {
                    TitleItem(
                        text = stringResource(R.string.recently_used),
                        icon = Icons.Outlined.History,
                        modifier = Modifier,
                        endContent = {
                            EnhancedIconButton(
                                onClick = {
                                    scope.launch {
                                        interactor.clearRecentColors()
                                    }
                                },
                                modifier = Modifier.offset(x = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = null,
                                    tint = takeColorFromScheme {
                                        primary.blend(error, 0.8f)
                                    }
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    val recentState = rememberLazyListState()
                    val possibleCount by remember(recentState, itemWidth) {
                        derivedStateOf {
                            (recentState.layoutInfo.viewportSize.width / itemWidth).roundToInt()
                        }
                    }
                    LazyRow(
                        state = recentState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fadingEdges(recentState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(
                            items = recentColors,
                            key = { it.toArgb() }
                        ) { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .aspectRatio(1f)
                                    .container(
                                        shape = ShapeDefaults.circle,
                                        color = color,
                                        resultPadding = 0.dp
                                    )
                                    .transparencyChecker()
                                    .background(color, ShapeDefaults.circle)
                                    .hapticsClickable {
                                        onRecentColorClick(color)
                                    }
                                    .animateItem(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentPasteGo,
                                    contentDescription = null,
                                    tint = color.inverse(
                                        fraction = {
                                            if (it) 0.8f
                                            else 0.5f
                                        },
                                        darkMode = color.luminance() < 0.3f
                                    ),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = color.copy(alpha = 1f),
                                            shape = ShapeDefaults.circle
                                        )
                                        .padding(3.dp)
                                )
                            }
                        }
                        if (recentColors.size < possibleCount) {
                            items(possibleCount - recentColors.size) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(ShapeDefaults.circle)
                                        .alpha(0.4f)
                                        .transparencyChecker()
                                        .animateItem()
                                )
                            }
                        }
                    }
                }
            }
            BoxAnimatedVisibility(favoriteColors.isNotEmpty()) {
                Column {
                    TitleItem(
                        text = stringResource(R.string.favorite),
                        icon = Icons.Rounded.BookmarkBorder,
                        modifier = Modifier
                    )
                    Spacer(Modifier.height(12.dp))
                    val favoriteState = rememberLazyListState()
                    val possibleCount by remember(favoriteState, itemWidth) {
                        derivedStateOf {
                            (favoriteState.layoutInfo.viewportSize.width / itemWidth).roundToInt()
                        }
                    }
                    val data = remember(favoriteColors) { mutableStateOf(favoriteColors) }
                    val haptics = LocalHapticFeedback.current
                    val reorderableState = rememberReorderableLazyListState(
                        lazyListState = favoriteState,
                        onMove = { from, to ->
                            haptics.press()
                            data.value = data.value.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                        }
                    )
                    LazyRow(
                        state = favoriteState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fadingEdges(favoriteState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(
                            items = data.value,
                            key = { it.toArgb() }
                        ) { color ->
                            ReorderableItem(
                                state = reorderableState,
                                key = color.toArgb()
                            ) { isDragging ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .aspectRatio(1f)
                                        .scale(
                                            animateFloatAsState(
                                                if (!reorderableState.isAnyItemDragging || isDragging) 1f
                                                else 0.8f
                                            ).value
                                        )
                                        .container(
                                            shape = ShapeDefaults.circle,
                                            color = color,
                                            resultPadding = 0.dp
                                        )
                                        .transparencyChecker()
                                        .background(color, ShapeDefaults.circle)
                                        .hapticsClickable {
                                            onFavoriteColorClick(color)
                                        }
                                        .longPressDraggableHandle(
                                            onDragStarted = {
                                                haptics.longPress()
                                            },
                                            onDragStopped = {
                                                scope.launch {
                                                    interactor.updateFavoriteColors(data.value.map { it.toModel() })
                                                }
                                            }
                                        )
                                        .animateItem(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ContentPasteGo,
                                        contentDescription = null,
                                        tint = color.inverse(
                                            fraction = {
                                                if (it) 0.8f
                                                else 0.5f
                                            },
                                            darkMode = color.luminance() < 0.3f
                                        ),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                color = color.copy(alpha = 1f),
                                                shape = ShapeDefaults.circle
                                            )
                                            .padding(3.dp)
                                    )
                                }
                            }
                        }
                        if (favoriteColors.size < possibleCount) {
                            items(possibleCount - favoriteColors.size) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(ShapeDefaults.circle)
                                        .alpha(0.4f)
                                        .transparencyChecker()
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