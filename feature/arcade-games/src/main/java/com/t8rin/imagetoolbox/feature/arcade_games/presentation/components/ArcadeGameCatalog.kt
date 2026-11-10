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

package com.t8rin.imagetoolbox.feature.arcade_games.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.games.ArcadeGameRegistry
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.isKeyboardVisibleAsState
import com.t8rin.imagetoolbox.feature.arcade_games.presentation.screenLogic.ArcadeGamesComponent
import kotlinx.coroutines.CoroutineScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

@Composable
internal fun ArcadeGameCatalog(
    query: String,
    gridState: LazyStaggeredGridState,
    bottomPadding: Dp,
    component: ArcadeGamesComponent
) {
    val settings = LocalSettingsState.current
    val favorites = settings.favoriteArcadeGames
    val order = settings.arcadeGameOrder
    val orderedGames = remember(order, favorites) {
        val (favoriteGames, otherGames) = ArcadeGameRegistry.orderedGames(order)
            .partition { it.name in favorites }
        favoriteGames + otherGames
    }
    var games by remember(orderedGames) { mutableStateOf(orderedGames) }
    val haptics = LocalHapticFeedback.current
    val onMove: suspend CoroutineScope.(LazyStaggeredGridItemInfo, LazyStaggeredGridItemInfo) -> Unit =
        { from, to ->
        val fromIndex = games.indexOfFirst { it.name == from.key }
        val toIndex = games.indexOfFirst { it.name == to.key }
        if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
            games = games.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
            haptics.press()
        }
        }
    val (favoriteReorderState, otherReorderState) = key(favorites) {
        rememberReorderableLazyStaggeredGridState(gridState, onMove = onMove) to
                rememberReorderableLazyStaggeredGridState(gridState, onMove = onMove)
    }
    val isReordering = favoriteReorderState.isAnyItemDragging || otherReorderState.isAnyItemDragging
    val filteredGames = if (query.isEmpty()) games else games.filter {
        stringResource(it.title).contains(query, ignoreCase = true)
    }
    val cutoutInsets = WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
    val cutout = cutoutInsets.asPaddingValues()
    val direction = LocalLayoutDirection.current
    val focus = LocalFocusManager.current
    val canReorder = query.isEmpty()
    val isKeyboardVisible by isKeyboardVisibleAsState()
    val showResultsAboveSearch = !canReorder && isKeyboardVisible
    if (filteredGames.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.nothing_found_by_search),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Icon(
                Icons.Outlined.SearchOff,
                null,
                modifier = Modifier.size(140.dp)
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            columns = StaggeredGridCells.Adaptive(300.dp),
            reverseLayout = showResultsAboveSearch,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalItemSpacing = 12.dp,
            contentPadding = PaddingValues(
                top = 12.dp,
                bottom = bottomPadding + 12.dp,
                start = 12.dp + cutout.calculateStartPadding(direction),
                end = 12.dp + cutout.calculateEndPadding(direction)
            ),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(
                if (showResultsAboveSearch) filteredGames.asReversed() else filteredGames,
                key = { it.name }
            ) { game ->
                val isFavorite = game.name in favorites
                ReorderableItem(
                    state = if (isFavorite) favoriteReorderState else otherReorderState,
                    key = game.name,
                    enabled = canReorder
                ) { isDragging ->
                    PreferenceItemOverload(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        title = stringResource(game.title),
                        subtitle = stringResource(game.description),
                        startIcon = {
                            Icon(
                                imageVector = game.icon,
                                contentDescription = null
                            )
                        },
                        endIcon = {
                            EnhancedIconButton(
                                onClick = {
                                    if (!isReordering) component.toggleFavorite(game)
                                },
                                modifier = Modifier.offset(8.dp)
                            ) {
                                AnimatedContent(
                                    targetState = isFavorite,
                                    transitionSpec = {
                                        fadeIn() + scaleIn(initialScale = 0.85f) togetherWith
                                                fadeOut() + scaleOut(targetScale = 0.85f)
                                    }
                                ) { favorite ->
                                    Icon(
                                        imageVector = if (favorite) Icons.Rounded.BookmarkRemove else Icons.Outlined.Bookmark,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .longPressDraggableHandle(
                                enabled = canReorder && (!isReordering || isDragging),
                                onDragStarted = { haptics.longPress() },
                                onDragStopped = {
                                    if (games != orderedGames) {
                                        component.reorderGames(
                                            games.filter { (it.name in favorites) == isFavorite }
                                        )
                                    }
                                }
                            )
                            .scale(animateFloatAsState(if (isDragging) 1.05f else 1f).value),
                        onClick = {
                            focus.clearFocus()
                            component.selectGame(game)
                        }
                    )
                }
            }
        }
    }
}