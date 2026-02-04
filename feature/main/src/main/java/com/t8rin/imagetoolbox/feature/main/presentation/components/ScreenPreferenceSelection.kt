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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import android.content.ClipboardManager
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteOff
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkOff
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.LayersSearchOutline
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.clipList
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberClipboardData
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
internal fun RowScope.ScreenPreferenceSelection(
    currentScreenList: List<Screen>,
    showScreenSearch: Boolean,
    screenSearchKeyword: String,
    isGrid: Boolean,
    isSheetSlideable: Boolean,
    onGetClipList: (List<Uri>) -> Unit,
    onNavigationBarItemChange: (Int) -> Unit,
    onNavigateToScreenWithPopUpTo: (Screen) -> Unit,
    onChangeShowScreenSearch: (Boolean) -> Unit,
    onToggleFavorite: (Screen) -> Unit,
    showNavRail: Boolean,
) {
    val essentials = rememberLocalEssentials()
    val settingsState = LocalSettingsState.current
    val cutout = WindowInsets.displayCutout.asPaddingValues()
    val canSearchScreens = settingsState.screensSearchEnabled
    val isSearching =
        showScreenSearch && screenSearchKeyword.isNotEmpty() && canSearchScreens
    val isScreenSelectionLauncherMode = settingsState.isScreenSelectionLauncherMode

    AnimatedContent(
        modifier = Modifier
            .weight(1f)
            .widthIn(min = 1.dp),
        targetState = remember(currentScreenList, isSearching, settingsState.favoriteScreenList) {
            Triple(
                currentScreenList.isNotEmpty(),
                isSearching,
                settingsState.favoriteScreenList.isEmpty()
            )
        },
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { (hasScreens, isSearching, noFavorites) ->
        if (hasScreens) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val clipboardData by rememberClipboardData()
                val allowAutoPaste = settingsState.allowAutoClipboardPaste
                val showClipButton =
                    (clipboardData.isNotEmpty() && allowAutoPaste) || !allowAutoPaste
                val showSearchButton = !showScreenSearch && canSearchScreens

                val layoutDirection = LocalLayoutDirection.current
                val navBarsPadding = WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()

                val contentPadding by remember(
                    isGrid, navBarsPadding,
                    showClipButton, showSearchButton,
                    isSheetSlideable, layoutDirection,
                    cutout, showNavRail, isScreenSelectionLauncherMode
                ) {
                    derivedStateOf {
                        val vertical = if (isScreenSelectionLauncherMode) 12.dp else 0.dp
                        val firstBottomPart = if (isGrid) {
                            navBarsPadding
                        } else {
                            0.dp
                        }

                        val secondBottomPart = if (showClipButton && showSearchButton) {
                            76.dp + 48.dp
                        } else if (showClipButton || showSearchButton) {
                            76.dp
                        } else {
                            0.dp
                        }

                        PaddingValues(
                            bottom = 12.dp + firstBottomPart + secondBottomPart + vertical,
                            top = 12.dp + vertical,
                            end = 12.dp + if (isSheetSlideable) {
                                cutout.calculateEndPadding(layoutDirection)
                            } else 0.dp,
                            start = 12.dp + if (!showNavRail) {
                                cutout.calculateStartPadding(layoutDirection)
                            } else 0.dp
                        )
                    }
                }

                AnimatedContent(
                    targetState = isScreenSelectionLauncherMode,
                    modifier = Modifier.fillMaxSize()
                ) { isLauncherMode ->
                    if (isLauncherMode) {
                        LauncherScreenSelector(
                            screenList = currentScreenList,
                            onNavigateToScreenWithPopUpTo = onNavigateToScreenWithPopUpTo,
                            contentPadding = contentPadding,
                            onToggleFavorite = onToggleFavorite
                        )
                    } else {
                        LazyVerticalStaggeredGrid(
                            reverseLayout = showScreenSearch && screenSearchKeyword.isNotEmpty() && canSearchScreens,
                            modifier = Modifier.fillMaxSize(),
                            columns = StaggeredGridCells.Adaptive(220.dp),
                            verticalItemSpacing = 12.dp,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 12.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                            contentPadding = contentPadding,
                            flingBehavior = enhancedFlingBehavior(),
                            content = {
                                items(currentScreenList) { screen ->
                                    PreferenceItemOverload(
                                        onClick = {
                                            onNavigateToScreenWithPopUpTo(screen)
                                        },
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        modifier = Modifier
                                            .widthIn(min = 1.dp)
                                            .fillMaxWidth()
                                            .animateItem(),
                                        shape = ShapeDefaults.default,
                                        title = stringResource(screen.title),
                                        subtitle = stringResource(screen.subtitle),
                                        badge = {
                                            AnimatedVisibility(
                                                visible = screen.isBetaFeature,
                                                modifier = Modifier
                                                    .align(Alignment.CenterVertically)
                                                    .padding(
                                                        start = 4.dp,
                                                        bottom = 2.dp,
                                                        top = 2.dp
                                                    ),
                                                enter = fadeIn(),
                                                exit = fadeOut()
                                            ) {
                                                EnhancedBadge(
                                                    content = {
                                                        Text(stringResource(R.string.beta))
                                                    },
                                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                                    contentColor = MaterialTheme.colorScheme.onTertiary
                                                )
                                            }
                                        },
                                        endIcon = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                if (!settingsState.groupOptionsByTypes) {
                                                    EnhancedIconButton(
                                                        onClick = {
                                                            onToggleFavorite(screen)
                                                        },
                                                        modifier = Modifier.offset(8.dp)
                                                    ) {
                                                        val inFavorite by remember(
                                                            settingsState.favoriteScreenList,
                                                            screen
                                                        ) {
                                                            derivedStateOf {
                                                                settingsState.favoriteScreenList.find { it == screen.id } != null
                                                            }
                                                        }
                                                        AnimatedContent(
                                                            targetState = inFavorite,
                                                            transitionSpec = {
                                                                (fadeIn() + scaleIn(initialScale = 0.85f))
                                                                    .togetherWith(
                                                                        fadeOut() + scaleOut(
                                                                            targetScale = 0.85f
                                                                        )
                                                                    )
                                                            }
                                                        ) { isInFavorite ->
                                                            val icon by remember(isInFavorite) {
                                                                derivedStateOf {
                                                                    if (isInFavorite) Icons.Rounded.BookmarkRemove
                                                                    else Icons.Rounded.BookmarkBorder
                                                                }
                                                            }
                                                            Icon(
                                                                imageVector = icon,
                                                                contentDescription = null
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        startIcon = {
                                            AnimatedContent(
                                                targetState = screen.icon,
                                                transitionSpec = {
                                                    (slideInVertically() + fadeIn() + scaleIn())
                                                        .togetherWith(slideOutVertically { it / 2 } + fadeOut() + scaleOut())
                                                        .using(SizeTransform(false))
                                                }
                                            ) { icon ->
                                                icon?.let {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }

                val context = LocalContext.current
                val clipboardManager = remember(context) {
                    context.getSystemService<ClipboardManager>()
                }
                BoxAnimatedVisibility(
                    visible = showClipButton,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .then(
                            if (showNavRail) {
                                Modifier.navigationBarsPadding()
                            } else Modifier
                        ),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    BadgedBox(
                        badge = {
                            if (clipboardData.isNotEmpty()) {
                                EnhancedBadge(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(clipboardData.size.toString())
                                }
                            }
                        }
                    ) {
                        EnhancedFloatingActionButton(
                            onClick = {
                                if (!allowAutoPaste) {
                                    val list = clipboardManager.clipList()
                                    if (list.isEmpty()) {
                                        essentials.showToast(
                                            message = essentials.getString(R.string.clipboard_paste_invalid_empty),
                                            icon = Icons.Outlined.ContentPasteOff
                                        )
                                    } else onGetClipList(list)
                                } else onGetClipList(clipboardData)
                            },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentPaste,
                                contentDescription = stringResource(R.string.copy)
                            )
                        }
                    }
                }
                BoxAnimatedVisibility(
                    visible = showSearchButton,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .then(
                            if (showClipButton) {
                                Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                            } else Modifier.padding(16.dp)
                        )
                        .then(
                            if (showNavRail) {
                                Modifier.navigationBarsPadding()
                            } else Modifier
                        )
                        .then(
                            if (showClipButton) {
                                Modifier.padding(bottom = 76.dp)
                            } else Modifier
                        )
                ) {
                    EnhancedFloatingActionButton(
                        containerColor = if (showClipButton) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else MaterialTheme.colorScheme.tertiaryContainer,
                        type = if (showClipButton) {
                            EnhancedFloatingActionButtonType.Small
                        } else EnhancedFloatingActionButtonType.Primary,
                        onClick = { onChangeShowScreenSearch(canSearchScreens) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LayersSearchOutline,
                            contentDescription = stringResource(R.string.search_here)
                        )
                    }
                }
            }
        } else {
            if (!isSearching && noFavorites) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.no_favorite_options_selected),
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
                    Spacer(Modifier.height(16.dp))
                    EnhancedButton(
                        onClick = {
                            onNavigationBarItemChange(1)
                        }
                    ) {
                        Text(stringResource(R.string.add_favorites))
                    }
                    Spacer(Modifier.weight(1f))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(R.string.nothing_found_by_search),
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
                        imageVector = Icons.Rounded.SearchOff,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(2f)
                            .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                            .fillMaxSize()
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}