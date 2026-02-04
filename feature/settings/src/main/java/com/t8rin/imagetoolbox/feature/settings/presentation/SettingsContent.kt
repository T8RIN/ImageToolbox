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

@file:Suppress("KotlinConstantConditions")

package com.t8rin.imagetoolbox.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.SearchBar
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.isKeyboardVisibleAsState
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SearchableSettingItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SettingGroupItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SettingItem
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent


@Composable
fun SettingsContent(
    component: SettingsComponent,
    disableBottomInsets: Boolean = false,
    appBarNavigationIcon: (@Composable (Boolean, () -> Unit) -> Unit)? = null
) {
    val isStandaloneScreen = appBarNavigationIcon == null

    val isUpdateAvailable by component.isUpdateAvailable.subscribeAsState()

    val settingsState = LocalSettingsState.current
    val layoutDirection = LocalLayoutDirection.current
    val initialSettingGroups = SettingsGroup.entries

    val searchKeyword = component.searchKeyword
    var showSearch by rememberSaveable { mutableStateOf(false) }

    val settings = component.filteredSettings
    val loading = component.isFilteringSettings

    val padding = WindowInsets.navigationBars
        .union(WindowInsets.displayCutout)
        .let { insets ->
            if (disableBottomInsets) {
                insets.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Top
                )
            } else {
                insets
            }
        }
        .asPaddingValues()
        .run {
            PaddingValues(
                top = 8.dp,
                bottom = calculateBottomPadding() + 8.dp,
                end = calculateEndPadding(layoutDirection) + 8.dp,
                start = if (isStandaloneScreen) calculateStartPadding(layoutDirection) + 8.dp
                else 8.dp
            )
        }

    val focus = LocalFocusManager.current
    val isKeyboardVisible by isKeyboardVisibleAsState()

    DisposableEffect(Unit) {
        onDispose {
            if (!isKeyboardVisible) {
                focus.clearFocus()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Column(
        modifier = if (isStandaloneScreen) {
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .nestedScroll(
                    scrollBehavior.nestedScrollConnection
                )
        } else Modifier
    ) {
        EnhancedTopAppBar(
            type = if (isStandaloneScreen) EnhancedTopAppBarType.Large
            else EnhancedTopAppBarType.Normal,
            title = {
                AnimatedContent(
                    targetState = showSearch
                ) { searching ->
                    if (!searching) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.marquee()
                        ) {
                            Text(
                                text = stringResource(R.string.settings),
                                style = if (!isStandaloneScreen) {
                                    MaterialTheme.typography.titleLarge
                                } else LocalTextStyle.current
                            )
                            if (isStandaloneScreen) {
                                Spacer(modifier = Modifier.width(8.dp))
                                TopAppBarEmoji()
                            }
                        }
                    } else {
                        BackHandler {
                            component.updateSearchKeyword("")
                            showSearch = false
                        }
                        SearchBar(
                            searchString = searchKeyword,
                            onValueChange = component::updateSearchKeyword
                        )
                    }
                }
            },
            actions = {
                AnimatedContent(
                    targetState = showSearch to searchKeyword.isNotEmpty(),
                    transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                ) { (searching, hasSearchKey) ->
                    EnhancedIconButton(
                        onClick = {
                            if (!showSearch) {
                                showSearch = true
                            } else {
                                component.updateSearchKeyword("")
                            }
                        }
                    ) {
                        if (searching && hasSearchKey) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        } else if (!searching) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (appBarNavigationIcon != null) {
                    appBarNavigationIcon(showSearch) {
                        showSearch = false
                        component.updateSearchKeyword("")
                    }
                } else if (component.onGoBack != null || showSearch) {
                    EnhancedIconButton(
                        onClick = {
                            if (showSearch) {
                                showSearch = false
                                component.updateSearchKeyword("")
                            } else {
                                component.onGoBack?.invoke()
                            }
                        },
                        containerColor = Color.Transparent
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                }
            },
            windowInsets = if (isStandaloneScreen) {
                EnhancedTopAppBarDefaults.windowInsets
            } else {
                EnhancedTopAppBarDefaults.windowInsets.only(
                    WindowInsetsSides.End + WindowInsetsSides.Top
                )
            },
            colors = if (isStandaloneScreen) {
                EnhancedTopAppBarDefaults.colors()
            } else {
                EnhancedTopAppBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.blend(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        fraction = 0.5f
                    )
                )
            },
            scrollBehavior = if (isStandaloneScreen) {
                scrollBehavior
            } else null
        )
        Box {
            AnimatedContent(
                targetState = settings,
                modifier = Modifier
                    .fillMaxSize()
                    .clearFocusOnTap(),
                transitionSpec = {
                    fadeIn() + scaleIn(initialScale = 0.95f) togetherWith fadeOut() + scaleOut(
                        targetScale = 0.8f
                    )
                }
            ) { settingsAnimated ->
                val oneColumn = LocalScreenSize.current.width < 600.dp
                val spacing = if (searchKeyword.isNotEmpty()) 4.dp
                else if (isStandaloneScreen) 8.dp
                else 2.dp

                if (settingsAnimated == null) {
                    LazyVerticalStaggeredGrid(
                        contentPadding = padding,
                        columns = StaggeredGridCells.Adaptive(300.dp),
                        verticalItemSpacing = spacing,
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        items(
                            items = initialSettingGroups,
                            key = { it.id }
                        ) { group ->
                            BoxAnimatedVisibility(
                                visible = if (group is SettingsGroup.Shadows) {
                                    settingsState.borderWidth <= 0.dp
                                } else true
                            ) {
                                if (isStandaloneScreen) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = if (!oneColumn) {
                                            Modifier.container(
                                                shape = ShapeDefaults.large,
                                                resultPadding = 0.dp,
                                                color = MaterialTheme.colorScheme.surfaceContainerLowest
                                            )
                                        } else Modifier
                                    ) {
                                        TitleItem(
                                            modifier = Modifier.padding(
                                                start = 8.dp,
                                                end = 8.dp,
                                                top = 12.dp,
                                                bottom = 12.dp
                                            ),
                                            icon = group.icon,
                                            text = stringResource(group.titleId),
                                            iconContainerColor = takeColorFromScheme {
                                                primary.blend(tertiary, 0.5f)
                                            },
                                            iconContentColor = takeColorFromScheme {
                                                onPrimary.blend(onTertiary, 0.5f)
                                            }
                                        )
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            group.settingsList.forEach { setting ->
                                                SettingItem(
                                                    setting = setting,
                                                    component = component,
                                                    isUpdateAvailable = isUpdateAvailable,
                                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                                    onNavigateToEasterEgg = {
                                                        component.onNavigate(Screen.EasterEgg)
                                                    },
                                                    onNavigateToSettings = {
                                                        component.onNavigate(Screen.Settings())
                                                    },
                                                    onNavigateToLibrariesInfo = {
                                                        component.onNavigate(Screen.LibrariesInfo)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    SettingGroupItem(
                                        groupKey = group.id,
                                        icon = group.icon,
                                        text = stringResource(group.titleId),
                                        initialState = group.initialState
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            group.settingsList.forEach { setting ->
                                                SettingItem(
                                                    setting = setting,
                                                    component = component,
                                                    isUpdateAvailable = isUpdateAvailable,
                                                    onNavigateToEasterEgg = {
                                                        component.onNavigate(Screen.EasterEgg)
                                                    },
                                                    onNavigateToSettings = {
                                                        component.onNavigate(Screen.Settings())
                                                    },
                                                    onNavigateToLibrariesInfo = {
                                                        component.onNavigate(Screen.LibrariesInfo)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (settingsAnimated.isNotEmpty()) {
                    LazyVerticalStaggeredGrid(
                        contentPadding = padding,
                        columns = StaggeredGridCells.Adaptive(300.dp),
                        verticalItemSpacing = spacing,
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        itemsIndexed(
                            items = settingsAnimated,
                            key = { _, v -> v.hashCode() }
                        ) { index, (group, setting) ->
                            SearchableSettingItem(
                                shape = ShapeDefaults.byIndex(
                                    index = if (oneColumn) index else -1,
                                    size = settingsAnimated.size
                                ),
                                group = group,
                                setting = setting,
                                component = component,
                                isUpdateAvailable = isUpdateAvailable,
                                onNavigateToEasterEgg = {
                                    component.onNavigate(Screen.EasterEgg)
                                },
                                onNavigateToSettings = {
                                    component.onNavigate(Screen.Settings())
                                },
                                onNavigateToLibrariesInfo = {
                                    component.onNavigate(Screen.LibrariesInfo)
                                }
                            )
                        }
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
            BoxAnimatedVisibility(
                visible = loading,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surfaceDim.copy(0.7f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    EnhancedLoadingIndicator()
                }
            }
        }
    }
}