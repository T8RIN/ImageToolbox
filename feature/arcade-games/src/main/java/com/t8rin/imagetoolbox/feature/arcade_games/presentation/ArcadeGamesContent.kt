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

package com.t8rin.imagetoolbox.feature.arcade_games.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.games.ArcadeGameContent
import com.t8rin.imagetoolbox.core.games.ArcadeGameRegistry
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.StadiaController
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.arcade_games.presentation.components.ArcadeCatalogActions
import com.t8rin.imagetoolbox.feature.arcade_games.presentation.components.ArcadeGameCatalog
import com.t8rin.imagetoolbox.feature.arcade_games.presentation.screenLogic.ArcadeGamesComponent

@Composable
fun ArcadeGamesContent(component: ArcadeGamesComponent) {
    val gridState = rememberLazyStaggeredGridState()
    val focus = LocalFocusManager.current

    val showSearch = component.showSearch
    val searchQuery = component.searchQuery
    val game = component.selectedGame
    var showExitDialog by rememberSaveable(game) { mutableStateOf(false) }

    val closeSearch = {
        if (component.searchQuery.isNotBlank()) gridState.requestScrollToItem(0)
        component.closeSearch()
        focus.clearFocus()
    }
    val goBack = {
        when {
            game != null && component.isPlaying -> showExitDialog = true
            game != null -> component.closeGame()
            showSearch -> closeSearch()
            else -> component.onGoBack()
        }
    }

    AdaptiveLayoutScreen(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(game?.title ?: R.string.arcade_games)
                )
                AnimatedVisibility(
                    visible = game == null
                ) {
                    EnhancedBadge(
                        content = {
                            Text(
                                text = ArcadeGameRegistry.games.size.toString()
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .padding(bottom = 12.dp)
                            .scaleOnTap {
                                AppToastHost.showConfetti()
                            }
                    )
                }
            }
        },
        onGoBack = goBack,
        shouldDisableBackHandler = game == null && !showSearch,
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        actions = {},
        buttons = {},
        imagePreview = {},
        placeImagePreview = false,
        forceImagePreviewToMax = game != null,
        placeControlsSeparately = true,
        showImagePreviewAsStickyHeader = false,
        canShowScreenData = game != null,
        addHorizontalCutoutPaddingIfNoPreview = false,
        enableNoDataScroll = false,
        noDataControls = {
            var actionsHeight by remember { mutableIntStateOf(0) }
            val bottomPadding = with(LocalDensity.current) { actionsHeight.toDp() }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                ArcadeGameCatalog(
                    query = searchQuery.trim(),
                    gridState = gridState,
                    bottomPadding = bottomPadding,
                    component = component
                )
                ArcadeCatalogActions(
                    searching = showSearch,
                    query = searchQuery,
                    onQueryChange = {
                        if (component.updateSearch(it)) {
                            gridState.requestScrollToItem(0)
                        }
                    },
                    onOpenSearch = component::openSearch,
                    onCloseSearch = closeSearch,
                    onOpenStatistics = component::openStatistics,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { actionsHeight = it.height }
                )
            }
        },
        contentPadding = 12.dp,
        controls = {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(
                        WindowInsets.navigationBars.union(
                            WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                        )
                    )
                    .padding(12.dp)
            ) {
                ArcadeGameContent(
                    game = game ?: return@BoxWithConstraints,
                    modifier = Modifier.fillMaxSize(),
                    height = maxHeight,
                    isPaused = showExitDialog,
                    onPlayingChanged = { component.onPlayingChanged(game, it) }
                )
            }
        }
    )

    ExitWithoutSavingDialog(
        visible = showExitDialog,
        onExit = component::closeGame,
        onDismiss = { showExitDialog = false },
        respectSettings = false,
        title = stringResource(R.string.arcade_exit_title),
        text = stringResource(R.string.arcade_exit_sub),
        icon = Icons.Outlined.StadiaController
    )
}