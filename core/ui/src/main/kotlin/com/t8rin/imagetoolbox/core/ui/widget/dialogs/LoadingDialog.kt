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

package com.t8rin.imagetoolbox.core.ui.widget.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.StadiaController
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.BasicEnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.game.ArcadeGameContent
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable

@Composable
fun LoadingDialog(
    visible: Boolean,
    onCancelLoading: () -> Unit = {},
    canCancel: Boolean = true,
    isForSaving: Boolean = true
) {
    LoadingDialogContent(
        visible = visible,
        onCancelLoading = onCancelLoading,
        canCancel = canCancel,
        isForSaving = isForSaving
    ) {
        EnhancedLoadingIndicator(modifier = Modifier.size(108.dp))
    }
}

@Composable
fun LoadingDialog(
    visible: Boolean,
    done: Int,
    left: Int,
    onCancelLoading: () -> Unit,
    canCancel: Boolean = true,
) {
    LoadingDialogContent(
        visible = visible,
        onCancelLoading = onCancelLoading,
        canCancel = canCancel && visible
    ) {
        if (left < 0) {
            EnhancedLoadingIndicator(modifier = Modifier.size(108.dp))
        } else {
            EnhancedLoadingIndicator(done = done, left = left)
        }
    }
}

@Composable
fun LoadingDialog(
    visible: Boolean,
    progress: () -> Float,
    onCancelLoading: () -> Unit = {},
    canCancel: Boolean = true,
    loaderSize: Dp = 60.dp,
    switchToIndicator: Boolean = false,
    isLayoutSwappable: Boolean = true,
    isForSaving: Boolean = true,
    additionalContent: @Composable (Dp) -> Unit = {}
) {
    val progress = progress()

    LoadingDialogContent(
        visible = visible,
        onCancelLoading = onCancelLoading,
        canCancel = canCancel,
        isForSaving = isForSaving
    ) {
        AnimatedContent(
            targetState = switchToIndicator || progress == 1f && visible && isLayoutSwappable,
            modifier = Modifier.fillMaxSize()
        ) { isIndicator ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isIndicator) {
                    EnhancedLoadingIndicator(modifier = Modifier.size(108.dp))
                } else {
                    EnhancedLoadingIndicator(
                        progress = progress,
                        loaderSize = loaderSize,
                        additionalContent = additionalContent
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingDialogContent(
    visible: Boolean,
    onCancelLoading: () -> Unit,
    canCancel: Boolean,
    isForSaving: Boolean = true,
    indicator: @Composable BoxScope.() -> Unit
) {
    var showWantDismissDialog by remember(canCancel, visible) { mutableStateOf(false) }
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = { showWantDismissDialog = canCancel },
        modifier = Modifier.keepScreenOn()
    ) {
        val focus = LocalFocusManager.current
        LaunchedEffect(focus) {
            focus.clearFocus()
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .tappable(canCancel) {
                        showWantDismissDialog = canCancel
                    }
            )
            LoadingGameContent(indicator = indicator)
        }
    }
    WantCancelLoadingDialog(
        visible = showWantDismissDialog,
        onCancelLoading = onCancelLoading,
        onDismissDialog = {
            showWantDismissDialog = false
        },
        isForSaving = isForSaving,
        modifier = Modifier.keepScreenOn()
    )
}

@Composable
private fun LoadingGameContent(
    indicator: @Composable BoxScope.() -> Unit
) {
    val indicatorSize = 120.dp
    val settings = LocalSettingsState.current
    if (!settings.showLoadingGameButton) {
        Box(
            modifier = Modifier.size(indicatorSize),
            contentAlignment = Alignment.Center,
            content = indicator
        )
        return
    }

    var showGame by rememberSaveable { mutableStateOf(false) }
    val preference = settings.loadingGame
    var game by rememberSaveable(preference) {
        mutableStateOf(if (preference == ArcadeGame.Random) ArcadeGame.playable.random() else preference)
    }
    val isPortrait by isPortraitOrientationAsState()
    val buttonSize = 48.dp
    val buttonSpacing = 12.dp
    val gameSpacing = 16.dp
    val indicatorTopPadding by animateDpAsState(
        targetValue = if (showGame) 0.dp else buttonSize + buttonSpacing
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gameSpacing, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(gameSpacing),
        itemVerticalAlignment = Alignment.CenterVertically,
        maxItemsInEachRow = if (isPortrait) 1 else 2
    ) {
        Column(
            modifier = Modifier.padding(top = indicatorTopPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            Box(
                modifier = Modifier.size(indicatorSize),
                contentAlignment = Alignment.Center,
                content = indicator
            )
            EnhancedIconButton(
                onClick = {
                    if (!showGame && preference == ArcadeGame.Random) {
                        game = ArcadeGame.playable.filterNot { it == game }.random()
                    }
                    showGame = !showGame
                },
                modifier = Modifier.size(buttonSize),
                containerColor = if (showGame) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            ) {
                Icon(
                    imageVector = if (showGame) {
                        Icons.Rounded.StadiaController
                    } else {
                        Icons.Outlined.StadiaController
                    },
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(
            visible = showGame,
            modifier = if (isPortrait) Modifier else Modifier.weight(1f, fill = false),
            enter = fadeIn() + if (isPortrait) {
                expandVertically(expandFrom = Alignment.Top)
            } else {
                expandHorizontally(expandFrom = Alignment.Start)
            },
            exit = fadeOut() + if (isPortrait) {
                shrinkVertically(shrinkTowards = Alignment.Top)
            } else {
                shrinkHorizontally(shrinkTowards = Alignment.Start)
            }
        ) {
            ArcadeGameContent(
                game = game,
                modifier = Modifier.widthIn(max = 420.dp)
            )
        }
    }
}