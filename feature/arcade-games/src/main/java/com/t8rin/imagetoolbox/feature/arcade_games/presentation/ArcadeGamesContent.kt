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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.game.ArcadeGameContent
import com.t8rin.imagetoolbox.core.ui.widget.game.description
import com.t8rin.imagetoolbox.core.ui.widget.game.icon
import com.t8rin.imagetoolbox.core.ui.widget.game.title
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.arcade_games.presentation.screenLogic.ArcadeGamesComponent

@Composable
fun ArcadeGamesContent(component: ArcadeGamesComponent) {
    val gridState = rememberLazyStaggeredGridState()

    val game = component.selectedGame

    val goBack = {
        if (game == null) {
            component.onGoBack()
        } else {
            component.selectGame(null)
        }
    }

    AdaptiveLayoutScreen(
        title = {
            AnimatedContent(targetState = game?.title ?: R.string.arcade_games) {
                Text(stringResource(it))
            }
        },
        onGoBack = goBack,
        shouldDisableBackHandler = game == null,
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
        noDataControls = {
            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = LocalScreenSize.current.height),
                state = gridState,
                columns = StaggeredGridCells.Adaptive(300.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                flingBehavior = enhancedFlingBehavior()
            ) {
                items(
                    items = ArcadeGame.playable,
                    key = { it.name }
                ) { option ->
                    PreferenceItem(
                        title = stringResource(option.title),
                        subtitle = stringResource(option.description),
                        startIcon = option.icon,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        onClick = {
                            component.selectGame(option)
                        }
                    )
                }
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
                    height = maxHeight
                )
            }
        }
    )
}