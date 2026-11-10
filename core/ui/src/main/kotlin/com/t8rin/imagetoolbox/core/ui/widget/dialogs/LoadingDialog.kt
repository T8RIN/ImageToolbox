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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.keepScreenOn
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.games.ArcadeGameContent
import com.t8rin.imagetoolbox.core.games.ArcadeGameRegistry
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.StadiaController
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.BasicEnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
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
    var showGame by rememberSaveable { mutableStateOf(false) }
    var keepGameOpen by rememberSaveable { mutableStateOf(false) }
    val gameVisible = showGame && LocalSettingsState.current.showLoadingGameButton
    val showGameAfterLoading = gameVisible && keepGameOpen
    val canDismissOnTap = visible && canCancel && !gameVisible

    fun closeGame() {
        showGame = false
        keepGameOpen = false
    }

    LaunchedEffect(visible, gameVisible) {
        if (!visible && (!gameVisible || !keepGameOpen)) closeGame()
    }

    BasicEnhancedAlertDialog(
        visible = visible || showGameAfterLoading,
        onDismissRequest = {
            if (visible) showWantDismissDialog = canCancel else closeGame()
        },
        dismissOnClickOutside = canDismissOnTap,
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
                    .tappable(canDismissOnTap) {
                        showWantDismissDialog = canDismissOnTap
                    }
            )
            LoadingGameContent(
                loading = visible,
                showGame = gameVisible,
                onShowGameChange = {
                    if (it) showGame = true else closeGame()
                },
                onPlayingChanged = { playing ->
                    if (showGame && (visible || playing)) keepGameOpen = playing
                },
                onReturn = ::closeGame,
                isPaused = visible && showWantDismissDialog,
                indicator = indicator
            )
        }
    }
    WantCancelLoadingDialog(
        visible = visible && showWantDismissDialog,
        onCancelLoading = {
            closeGame()
            onCancelLoading()
        },
        onDismissDialog = {
            showWantDismissDialog = false
        },
        isForSaving = isForSaving,
        modifier = Modifier.keepScreenOn()
    )
}

@Composable
private fun LoadingGameContent(
    loading: Boolean,
    showGame: Boolean,
    onShowGameChange: (Boolean) -> Unit,
    onPlayingChanged: (Boolean) -> Unit,
    onReturn: () -> Unit,
    isPaused: Boolean,
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

    val preference = settings.loadingGame
    var gameName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var gameSession by rememberSaveable { mutableIntStateOf(0) }
    val isPortrait by isPortraitOrientationAsState()
    val buttonSize = 48.dp
    val buttonSpacing = 12.dp
    val gameSpacing = 16.dp
    val indicatorTopPadding by animateDpAsState(
        targetValue = if (showGame) 0.dp else buttonSize + buttonSpacing
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val gameWidth = minOf(
            420.dp,
            if (isPortrait) maxWidth else (maxWidth - indicatorSize - gameSpacing).coerceAtLeast(0.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gameSpacing, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(gameSpacing),
            itemVerticalAlignment = Alignment.CenterVertically,
            maxItemsInEachRow = if (isPortrait) 1 else 2
        ) {
            AnimatedContent(
                targetState = loading,
                modifier = Modifier.width(indicatorSize),
                contentAlignment = Alignment.Center,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { isLoading ->
                if (isLoading) {
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
                                if (!showGame) {
                                    gameName = ArcadeGameRegistry.pickGame(
                                        selection = preference,
                                        favorites = settings.favoriteArcadeGames,
                                        previous = gameName
                                    )?.name
                                    gameSession++
                                }
                                onShowGameChange(!showGame)
                            },
                            modifier = Modifier.size(buttonSize),
                            enabled = loading,
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
                } else {
                    EnhancedButton(onClick = onReturn) {
                        Text(stringResource(R.string.arcade_return))
                    }
                }
            }

            AnimatedVisibility(
                visible = showGame,
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
                val selectedGame = gameName?.let(ArcadeGameRegistry::find)
                if (selectedGame != null) {
                    key(gameSession) {
                        ArcadeGameContent(
                            game = selectedGame,
                            modifier = Modifier.width(gameWidth),
                            isPaused = isPaused,
                            allowNewRound = loading,
                            onPlayingChanged = onPlayingChanged
                        )
                    }
                }
            }
        }
    }
}