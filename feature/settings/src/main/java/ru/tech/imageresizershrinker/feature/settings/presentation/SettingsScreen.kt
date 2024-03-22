/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.domain.utils.Lambda
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.Setting
import ru.tech.imageresizershrinker.core.settings.presentation.SettingsGroup
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getStringLocalized
import ru.tech.imageresizershrinker.core.ui.utils.helper.plus
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.SearchBar
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.settings.presentation.components.SearchableSettingItem
import ru.tech.imageresizershrinker.feature.settings.presentation.components.SettingGroupItem
import ru.tech.imageresizershrinker.feature.settings.presentation.components.SettingItem
import ru.tech.imageresizershrinker.feature.settings.presentation.viewModel.SettingsViewModel
import java.util.Locale


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onTryGetUpdate: (
        newRequest: Boolean,
        installedFromMarket: Boolean,
        onNoUpdates: Lambda
    ) -> Unit,
    updateAvailable: Boolean,
    onGoBack: Lambda? = null,
    isStandaloneScreen: Boolean = true,
    appBarNavigationIcon: (@Composable (Boolean, Lambda) -> Unit)? = null
) {
    val layoutDirection = LocalLayoutDirection.current
    val initialSettingGroups = remember {
        SettingsGroup.entries.filter {
            !(it is SettingsGroup.Firebase && BuildConfig.FLAVOR == "foss")
        }
    }

    var searchKeyword by rememberSaveable {
        mutableStateOf("")
    }
    var showSearch by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    var settings: List<Pair<SettingsGroup, Setting>>? by remember { mutableStateOf(null) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(searchKeyword) {
        delay(150)
        loading = searchKeyword.isNotEmpty()
        settings = searchKeyword.takeIf { it.trim().isNotEmpty() }?.let {
            val newList = mutableListOf<Pair<Pair<SettingsGroup, Setting>, Int>>()
            initialSettingGroups.forEach { group ->
                group.settingsList.forEach { setting ->
                    val keywords = mutableListOf<String>().apply {
                        add(context.getString(group.titleId))
                        add(context.getString(setting.title))
                        add(context.getStringLocalized(group.titleId, Locale.ENGLISH))
                        add(context.getStringLocalized(setting.title, Locale.ENGLISH))
                        setting.subtitle?.let {
                            add(context.getString(it))
                            add(context.getStringLocalized(it, Locale.ENGLISH))
                        }
                    }

                    val substringStart = keywords
                        .joinToString()
                        .indexOf(
                            string = searchKeyword,
                            ignoreCase = true
                        ).takeIf { it != -1 }

                    substringStart?.plus(searchKeyword.length)?.let { substringEnd ->
                        newList.add(group to setting to (substringEnd - substringStart))
                    }
                }
            }
            newList.sortedBy { it.second }.map { it.first }
        }
        loading = false
    }

    val padding = WindowInsets.navigationBars
        .asPaddingValues()
        .plus(
            paddingValues = WindowInsets.displayCutout
                .asPaddingValues()
                .run {
                    PaddingValues(
                        top = 8.dp,
                        bottom = calculateBottomPadding() + 8.dp,
                        end = calculateEndPadding(layoutDirection),
                        start = if (isStandaloneScreen) calculateStartPadding(layoutDirection)
                        else 0.dp
                    )
                }
        )

    val focus = LocalFocusManager.current
    val settingsState = LocalSettingsState.current

    DisposableEffect(Unit) {
        onDispose {
            focus.clearFocus()
            showSearch = false
            searchKeyword = ""
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Column(
        modifier = if (isStandaloneScreen) {
            Modifier.nestedScroll(
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
                        Marquee {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
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
                        }
                    } else {
                        BackHandler {
                            searchKeyword = ""
                            showSearch = false
                        }
                        SearchBar(
                            searchString = searchKeyword,
                            onValueChange = {
                                searchKeyword = it
                            }
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
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current,
                        enableAutoShadowAndBorder = false,
                        onClick = {
                            if (!showSearch) {
                                showSearch = true
                            } else {
                                searchKeyword = ""
                            }
                        }
                    ) {
                        if (searching && hasSearchKey) {
                            Icon(Icons.Rounded.Close, null)
                        } else if (!searching) {
                            Icon(Icons.Rounded.Search, null)
                        }
                    }
                }
            },
            navigationIcon = {
                if (appBarNavigationIcon != null) {
                    appBarNavigationIcon(
                        showSearch,
                        Lambda {
                            showSearch = false
                            searchKeyword = ""
                        }
                    )
                } else if (onGoBack != null) {
                    EnhancedIconButton(
                        onClick = onGoBack,
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
            } else EnhancedTopAppBarDefaults.windowInsets.only(
                WindowInsetsSides.End + WindowInsetsSides.Top
            ),
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
                    .pointerInput(Unit) {
                        detectTapGestures { focus.clearFocus() }
                    },
                transitionSpec = {
                    fadeIn() + scaleIn(initialScale = 0.95f) togetherWith fadeOut() + scaleOut(
                        targetScale = 0.8f
                    )
                }
            ) { settingsAnimated ->
                if (settingsAnimated == null) {
                    LazyColumn(
                        contentPadding = padding
                    ) {
                        initialSettingGroups.forEachIndexed { index, group ->
                            item {
                                BoxAnimatedVisibility(
                                    visible = if (group is SettingsGroup.Shadows) {
                                        settingsState.borderWidth <= 0.dp
                                    } else true
                                ) {
                                    if (isStandaloneScreen) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        ) {
                                            if (index != 0) {
                                                Spacer(modifier = Modifier.height(12.dp))
                                            }
                                            TitleItem(
                                                modifier = Modifier.padding(
                                                    start = 8.dp,
                                                    end = 8.dp,
                                                    top = 12.dp,
                                                    bottom = 20.dp
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
                                            group.settingsList.forEach { setting ->
                                                SettingItem(
                                                    setting = setting,
                                                    viewModel = viewModel,
                                                    onTryGetUpdate = onTryGetUpdate,
                                                    updateAvailable = updateAvailable,
                                                    color = MaterialTheme.colorScheme.surfaceContainerLow
                                                )
                                            }
                                            if (index == initialSettingGroups.lastIndex) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    } else {
                                        SettingGroupItem(
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
                                                        viewModel = viewModel,
                                                        onTryGetUpdate = onTryGetUpdate,
                                                        updateAvailable = updateAvailable
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (settingsAnimated.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = padding
                    ) {
                        settingsAnimated.forEachIndexed { index, (group, setting) ->
                            item {
                                SearchableSettingItem(
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = settingsAnimated.size
                                    ),
                                    modifier = Modifier
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        ),
                                    group = group,
                                    setting = setting,
                                    viewModel = viewModel,
                                    onTryGetUpdate = onTryGetUpdate,
                                    updateAvailable = updateAvailable
                                )
                            }
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
                    Loading()
                }
            }
        }
    }

    onGoBack?.let {
        BackHandler(onBack = it)
    }
}