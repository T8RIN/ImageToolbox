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
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.resources.icons.Settings
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
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
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.settings.presentation.components.CategorizedSettings
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SearchableSettingItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SettingGroupItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.SettingItem
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@Composable
fun SettingsContent(
    component: SettingsComponent,
    disableBottomInsets: Boolean = false,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    appBarNavigationIcon: (@Composable (Boolean, () -> Unit) -> Unit)? = null,
    isActive: Boolean = true,
    category: SettingsGroup? = null
) {
    val isStandaloneScreen = appBarNavigationIcon == null
    val settingsState = LocalSettingsState.current
    val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val layoutDirection = LocalLayoutDirection.current
    val initialSettingGroups = SettingsGroup.entries

    val searchKeyword = component.searchKeyword
    var showSearch by rememberSaveable { mutableStateOf(false) }

    val settings = component.filteredSettings
    val loading = component.isFilteringSettings
    val targetSetting = component.targetSetting
    val useCategorizedSettings = settingsState.useCategorizedSettings || category != null
    var selectedGroupId by rememberSaveable(useCategorizedSettings, targetSetting) {
        mutableStateOf(
            initialSettingGroups.firstOrNull {
                !isStandaloneScreen && useCategorizedSettings && targetSetting in it.settingsList
            }?.id
        )
    }
    val selectedGroup = category ?: initialSettingGroups.firstOrNull {
        it.id == selectedGroupId
    }.takeIf { useCategorizedSettings }
    val categoriesGridState = rememberLazyStaggeredGridState()
    val categoryGridState = rememberSaveable(
        selectedGroup?.id,
        saver = LazyStaggeredGridState.Saver
    ) {
        LazyStaggeredGridState()
    }
    val searchGridState = rememberLazyStaggeredGridState()
    val targetRequester = remember(targetSetting) { BringIntoViewRequester() }
    var targetIsPlaced by remember(targetSetting, settings, selectedGroup, useCategorizedSettings) {
        mutableStateOf(false)
    }
    val targetModifier = Modifier
        .bringIntoViewRequester(targetRequester)
        .onPlaced { targetIsPlaced = true }

    val onNavigateBack: () -> Unit = {
        if (showSearch || searchKeyword.isNotEmpty()) {
            component.updateSearchKeyword("")
            showSearch = false
        } else if (category != null) {
            component.onGoBack?.invoke()
        } else {
            selectedGroupId = null
        }
    }

    if (isActive && (showSearch || searchKeyword.isNotEmpty() || (selectedGroup != null && category == null))) {
        BackHandler(onBack = onNavigateBack)
    }

    var showTargetHighlight by rememberSaveable {
        mutableStateOf(false)
    }
    var isHighlightShown by rememberSaveable(targetSetting) {
        mutableStateOf(false)
    }

    val highlightedContainerColor = if (showTargetHighlight) {
        val highlightTransition = rememberInfiniteTransition()
        val fraction by highlightTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0.75f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            )
        )

        MaterialTheme.colorScheme.surfaceContainerLow.blend(
            color = MaterialTheme.colorScheme.primaryContainer,
            fraction = fraction
        )
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    val contentSpacing = when {
        !isStandaloneScreen -> 12.dp
        useCategorizedSettings -> 16.dp
        else -> 8.dp
    }
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
                top = contentSpacing,
                bottom = calculateBottomPadding() + contentSpacing,
                end = calculateEndPadding(layoutDirection) + contentSpacing,
                start = if (isStandaloneScreen) {
                    calculateStartPadding(layoutDirection) + contentSpacing
                } else contentSpacing
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

    LaunchedEffect(targetSetting, settings, selectedGroup, useCategorizedSettings) {
        targetSetting ?: return@LaunchedEffect
        if (useCategorizedSettings && selectedGroup == null && settings == null) return@LaunchedEffect
        val index = settings?.indexOfFirst { (_, setting) ->
            setting == targetSetting
        } ?: selectedGroup?.settingsList?.indexOf(targetSetting)
        ?: initialSettingGroups.indexOfFirst { group ->
            group.settingsList.contains(targetSetting)
        }
        if (index < 0) return@LaunchedEffect

        val state = when {
            settings != null -> searchGridState
            selectedGroup != null -> categoryGridState
            else -> gridState
        }
        state.scrollToItem(index)
        snapshotFlow { targetIsPlaced }.first { it }
        targetRequester.bringIntoView()

        if (!isHighlightShown) {
            showTargetHighlight = true
            try {
                delay(3.seconds)
                isHighlightShown = true
            } finally {
                showTargetHighlight = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { showTargetHighlight = false }
    }

    Scaffold(
        modifier = if (isStandaloneScreen) {
            Modifier
                .fillMaxSize()
                .nestedScroll(
                    scrollBehavior.nestedScrollConnection
                )
        } else Modifier,
        topBar = {
            EnhancedTopAppBar(
                type = if (isStandaloneScreen) EnhancedTopAppBarType.Large
                else EnhancedTopAppBarType.Normal,
                title = {
                    AnimatedContent(
                        targetState = showSearch to selectedGroup?.titleId
                    ) { (searching, groupTitle) ->
                        if (!searching) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.marquee()
                            ) {
                                Text(
                                    text = stringResource(groupTitle ?: R.string.settings),
                                    style = if (!isStandaloneScreen) {
                                        MaterialTheme.typography.titleLarge
                                    } else LocalTextStyle.current
                                )
                                if (isStandaloneScreen && groupTitle == null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TopAppBarEmoji()
                                }
                            }
                        } else {
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
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = stringResource(R.string.search_here)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (appBarNavigationIcon != null) {
                        appBarNavigationIcon(
                            showSearch || selectedGroup != null || searchKeyword.isNotEmpty(),
                            onNavigateBack
                        )
                    } else if (component.onGoBack != null || showSearch || selectedGroup != null) {
                        EnhancedIconButton(
                            onClick = {
                                if (showSearch || selectedGroup != null || searchKeyword.isNotEmpty()) {
                                    onNavigateBack()
                                } else {
                                    component.onGoBack?.invoke()
                                }
                            },
                            containerColor = Color.Transparent
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
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
        },
        containerColor = if (isStandaloneScreen) MaterialTheme.colorScheme.surface else Color.Transparent,
        contentWindowInsets = WindowInsets()
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding)
        ) {
            AnimatedContent(
                targetState = settings?.isNotEmpty(),
                modifier = Modifier
                    .fillMaxSize()
                    .clearFocusOnTap(),
                transitionSpec = {
                    fadeIn() + scaleIn(initialScale = 0.95f) togetherWith fadeOut() + scaleOut(
                        targetScale = 0.8f
                    )
                }
            ) { haveSettings ->
                val oneColumn = LocalScreenSize.current.width < 600.dp
                val spacing = if (searchKeyword.isNotEmpty()) 4.dp
                else if (isStandaloneScreen) 8.dp
                else 2.dp

                if (haveSettings == null && useCategorizedSettings) {
                    CategorizedSettings(
                        groups = initialSettingGroups,
                        selectedGroup = selectedGroup,
                        onSelectGroup = {
                            if (isStandaloneScreen) {
                                component.onNavigate(Screen.SettingsCategory(it.id))
                            } else {
                                selectedGroupId = it.id
                            }
                        },
                        component = component,
                        gridState = categoriesGridState,
                        categoryGridState = categoryGridState,
                        contentPadding = padding,
                        targetSetting = targetSetting,
                        targetModifier = targetModifier,
                        highlightedContainerColor = highlightedContainerColor
                    )
                } else if (haveSettings == null) {
                    LazyVerticalStaggeredGrid(
                        state = gridState,
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
                                    val groupTitle = stringResource(group.titleId)
                                    val initialState = targetSetting in group.settingsList
                                            || (settingsState.settingGroupsInitialVisibility[group.id]
                                        ?: group.initialState)
                                    var expanded by rememberSaveable(group.id) {
                                        mutableStateOf(initialState)
                                    }
                                    val canCollapse = settingsState.allowCollapsingSettingsGroups
                                    val arrowRotation by animateFloatAsState(
                                        targetValue = if (expanded) 180f else 0f
                                    )

                                    Column(
                                        modifier = if (!oneColumn) {
                                            Modifier.container(
                                                shape = ShapeDefaults.large,
                                                resultPadding = 0.dp,
                                                color = MaterialTheme.colorScheme.surfaceContainerLowest
                                            )
                                        } else Modifier
                                    ) {

                                        TitleItem(
                                            modifier = Modifier.padding(8.dp),
                                            icon = group.icon,
                                            text = groupTitle,
                                            iconContainerColor = takeColorFromScheme {
                                                primary.blend(tertiary, 0.5f)
                                            },
                                            iconContentColor = takeColorFromScheme {
                                                onPrimary.blend(onTertiary, 0.5f)
                                            },
                                            endContent = if (canCollapse) {
                                                {
                                                    EnhancedIconButton(
                                                        containerColor = Color.Transparent,
                                                        onClick = { expanded = !expanded },
                                                        onLongClick = {
                                                            expanded = !expanded

                                                            scope.launch {
                                                                simpleSettingsInteractor
                                                                    .toggleSettingsGroupVisibility(
                                                                        key = group.id,
                                                                        value = !initialState
                                                                    )

                                                                AppToastHost.showToast(
                                                                    message = getString(
                                                                        if (initialState) {
                                                                            R.string.settings_group_visibility_hidden
                                                                        } else {
                                                                            R.string.settings_group_visibility_visible
                                                                        },
                                                                        groupTitle
                                                                    ),
                                                                    icon = Icons.Outlined.Settings
                                                                )
                                                            }
                                                        },
                                                        modifier = Modifier.offset(x = 4.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.KeyboardArrowDown,
                                                            contentDescription = stringResource(
                                                                if (expanded) R.string.hide
                                                                else R.string.show
                                                            ),
                                                            modifier = Modifier.rotate(arrowRotation)
                                                        )
                                                    }
                                                }
                                            } else null
                                        )

                                        if (!canCollapse) {
                                            Spacer(Modifier.height(8.dp))
                                        }

                                        BoxAnimatedVisibility(
                                            visible = !canCollapse || expanded,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                group.settingsList.forEach { setting ->
                                                    Column(if (setting == targetSetting) targetModifier else Modifier) {
                                                        SettingItem(
                                                            setting = setting,
                                                            component = component,
                                                            containerColor = if (showTargetHighlight && setting == targetSetting) {
                                                                highlightedContainerColor
                                                            } else {
                                                                MaterialTheme.colorScheme.surfaceContainerLow
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    SettingGroupItem(
                                        groupKey = group.id,
                                        icon = group.icon,
                                        text = stringResource(group.titleId),
                                        initialState = group.initialState,
                                        forceExpanded = targetSetting in group.settingsList
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            group.settingsList.forEach { setting ->
                                                Column(if (setting == targetSetting) targetModifier else Modifier) {
                                                    SettingItem(
                                                        setting = setting,
                                                        component = component,
                                                        containerColor = if (showTargetHighlight && setting == targetSetting) {
                                                            highlightedContainerColor
                                                        } else {
                                                            MaterialTheme.colorScheme.surface
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (haveSettings) {
                    val settingsAnimated = settings.orEmpty()
                    LazyVerticalStaggeredGrid(
                        state = searchGridState,
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
                                modifier = if (setting == targetSetting) targetModifier else Modifier,
                                shape = ShapeDefaults.byIndex(
                                    index = if (oneColumn) index else -1,
                                    size = settingsAnimated.size
                                ),
                                group = group,
                                setting = setting,
                                component = component
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
                            imageVector = Icons.Outlined.SearchOff,
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