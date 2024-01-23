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

package ru.tech.imageresizershrinker.feature.main.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.APP_LINK
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Github
import ru.tech.imageresizershrinker.core.ui.icons.material.GooglePlay
import ru.tech.imageresizershrinker.core.ui.model.isFirstLaunch
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.modifier.pulsate
import ru.tech.imageresizershrinker.core.ui.widget.modifier.rotateAnimation
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.SearchBar
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.main.presentation.components.NavigationItem
import ru.tech.imageresizershrinker.feature.main.presentation.components.settings.SettingsBlock
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val navController = LocalNavController.current
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact
    val screenList = settingsState.screenList

    val scope = rememberCoroutineScope()

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val toastHost = LocalToastHost.current

    val compactHeight =
        LocalWindowSizeClass.current.heightSizeClass == WindowHeightSizeClass.Compact
    val isSheetSlideable = !isGrid
    val layoutDirection = LocalLayoutDirection.current

    var sheetExpanded by rememberSaveable { mutableStateOf(false) }

    var settingsSearchKeyword by rememberSaveable {
        mutableStateOf("")
    }
    var showSettingsSearch by rememberSaveable { mutableStateOf(false) }
    val settingsBlock = remember {
        movableContentOf {
            SettingsBlock(
                searchKeyword = settingsSearchKeyword,
                viewModel = viewModel
            )
        }
    }
    val drawerContent = @Composable {
        if (sideSheetState.isOpen && isSheetSlideable) {
            BackHandler {
                scope.launch {
                    sideSheetState.close()
                }
            }
        }

        val configuration = LocalConfiguration.current
        val widthState by remember(sheetExpanded) {
            derivedStateOf {
                if (isSheetSlideable) {
                    min(
                        configuration.screenWidthDp.dp * 0.85f,
                        DrawerDefaults.MaximumDrawerWidth
                    )
                } else {
                    if (sheetExpanded) configuration.screenWidthDp.dp * 0.55f
                    else min(
                        configuration.screenWidthDp.dp * 0.4f,
                        270.dp
                    )
                }.coerceAtLeast(0.dp)
            }
        }

        ModalDrawerSheet(
            modifier = Modifier
                .width(animateDpAsState(targetValue = widthState).value)
                .then(
                    if (isSheetSlideable) {
                        Modifier
                            .offset(-((settingsState.borderWidth + 1.dp)))
                            .border(
                                settingsState.borderWidth,
                                MaterialTheme.colorScheme.outlineVariant(
                                    0.3f,
                                    DrawerDefaults.containerColor
                                ),
                                DrawerDefaults.shape
                            )
                    } else Modifier
                ),
            drawerShape = if (isSheetSlideable) DrawerDefaults.shape else RectangleShape,
            windowInsets = WindowInsets(0)
        ) {
            val focus = LocalFocusManager.current
            LaunchedEffect(sideSheetState.isClosed) {
                if (sideSheetState.isClosed) {
                    focus.clearFocus()
                    showSettingsSearch = false
                    settingsSearchKeyword = ""
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            6.dp
                        )
                    ),
                    title = {
                        AnimatedContent(
                            targetState = showSettingsSearch
                        ) { searching ->
                            if (!searching) {
                                Marquee(
                                    edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        6.dp
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            } else {
                                BackHandler {
                                    settingsSearchKeyword = ""
                                    showSettingsSearch = false
                                }
                                SearchBar(
                                    searchString = settingsSearchKeyword,
                                    onValueChange = {
                                        settingsSearchKeyword = it
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .zIndex(6f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .drawHorizontalStroke(),
                    actions = {
                        AnimatedContent(
                            targetState = showSettingsSearch to settingsSearchKeyword.isNotEmpty(),
                            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                        ) { (searching, hasSearchKey) ->
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    if (!showSettingsSearch) {
                                        showSettingsSearch = true
                                    } else {
                                        settingsSearchKeyword = ""
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
                        AnimatedContent(
                            targetState = !isSheetSlideable to showSettingsSearch,
                            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                        ) { (expanded, searching) ->
                            if (searching) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        showSettingsSearch = false
                                        settingsSearchKeyword = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            } else if (expanded) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        sheetExpanded = !sheetExpanded
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.MenuOpen,
                                        null,
                                        modifier = Modifier.rotate(
                                            animateFloatAsState(if (!sheetExpanded) 0f else 180f).value
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
                settingsBlock()
            }
        }
    }

    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    val content = @Composable {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box {
                val canSearchScreens = settingsState.screensSearchEnabled
                var showScreenSearch by rememberSaveable(canSearchScreens) { mutableStateOf(false) }
                var screenSearchKeyword by rememberSaveable(canSearchScreens) { mutableStateOf("") }
                val currentScreenList by remember(
                    settingsState.groupOptionsByTypes,
                    screenSearchKeyword,
                    screenList
                ) {
                    derivedStateOf {
                        if (settingsState.groupOptionsByTypes && (screenSearchKeyword.isEmpty() && !showScreenSearch)) {
                            Screen.typedEntries[currentPage].first
                        } else {
                            screenList
                        }.let { screens ->
                            if (screenSearchKeyword.isNotEmpty() && canSearchScreens) {
                                screens.filter {
                                    val string =
                                        context.getString(it.title) + " " + context.getString(it.subtitle)
                                    string.contains(other = screenSearchKeyword, ignoreCase = true)
                                }
                            } else screens
                        }
                    }
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    LargeTopAppBar(
                        title = {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Marquee(
                                    edgeColor = MaterialTheme
                                        .colorScheme
                                        .surfaceColorAtElevation(3.dp)
                                ) {
                                    val titleText = remember {
                                        "${Screen.featuresCount}".plus(
                                            if (BuildConfig.FLAVOR == "market") {
                                                getVersionPreRelease()
                                            } else {
                                                " ${BuildConfig.FLAVOR.uppercase()} ${getVersionPreRelease()}"
                                            }
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stringResource(R.string.app_name),
                                            modifier = Modifier.padding(
                                                start = WindowInsets
                                                    .displayCutout
                                                    .asPaddingValues()
                                                    .calculateStartPadding(layoutDirection)
                                            )
                                        )
                                        Badge(
                                            content = {
                                                Text(
                                                    text = titleText
                                                )
                                            },
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.onTertiary,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .padding(bottom = 12.dp)
                                                .scaleOnTap {}
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        TopAppBarEmoji()
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme
                                .colorScheme
                                .surfaceColorAtElevation(3.dp)
                        ),
                        actions = {
                            AnimatedVisibility(
                                visible = !showScreenSearch && canSearchScreens,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = { showScreenSearch = true && canSearchScreens }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                }
                            }
                            if (isSheetSlideable) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        scope.launch {
                                            sideSheetState.open()
                                        }
                                    },
                                    modifier = Modifier
                                        .pulsate(
                                            range = 0.95f..1.2f,
                                            enabled = settingsState.isFirstLaunch()
                                        )
                                        .rotateAnimation(enabled = settingsState.isFirstLaunch())
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        modifier = Modifier.drawHorizontalStroke(),
                        scrollBehavior = scrollBehavior
                    )

                    Row(Modifier.weight(1f)) {
                        AnimatedVisibility(
                            visible = !isSheetSlideable && settingsState.groupOptionsByTypes && screenSearchKeyword.isEmpty() && !sheetExpanded,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .widthIn(min = 80.dp)
                                        .container(
                                            shape = RectangleShape,
                                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                1.dp
                                            ),
                                            autoShadowElevation = 10.dp,
                                            resultPadding = 0.dp
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .padding(horizontal = 8.dp)
                                            .verticalScroll(rememberScrollState())
                                            .navigationBarsPadding()
                                            .padding(
                                                start = WindowInsets
                                                    .statusBars
                                                    .asPaddingValues()
                                                    .calculateStartPadding(LocalLayoutDirection.current)
                                            )
                                            .padding(
                                                start = WindowInsets
                                                    .displayCutout
                                                    .asPaddingValues()
                                                    .calculateStartPadding(LocalLayoutDirection.current)
                                            ),
                                        verticalArrangement = Arrangement.spacedBy(
                                            4.dp,
                                            Alignment.CenterVertically
                                        ),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(Modifier.height(8.dp))
                                        Screen.typedEntries.forEachIndexed { index, (_, data) ->
                                            val selected = index == currentPage
                                            NavigationItem(
                                                modifier = Modifier
                                                    .height(height = 56.dp)
                                                    .width(100.dp),
                                                selected = selected,
                                                onClick = { currentPage = index },
                                                icon = {
                                                    Icon(
                                                        if (selected) data.second else data.third,
                                                        null
                                                    )
                                                },
                                                label = {
                                                    Text(stringResource(data.first))
                                                }
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(settingsState.borderWidth)
                                        .background(
                                            MaterialTheme.colorScheme.outlineVariant(
                                                0.3f,
                                                DrawerDefaults.containerColor
                                            )
                                        )
                                )
                            }
                        }

                        val cutout =
                            if (!settingsState.groupOptionsByTypes || screenSearchKeyword.isNotEmpty()) {
                                WindowInsets.displayCutout.asPaddingValues()
                            } else PaddingValues()

                        AnimatedContent(
                            targetState = currentScreenList.isNotEmpty(),
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { hasScreens ->
                            if (hasScreens) {
                                LazyVerticalStaggeredGrid(
                                    reverseLayout = showScreenSearch && screenSearchKeyword.isNotEmpty() && canSearchScreens,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
                                    columns = StaggeredGridCells.Adaptive(220.dp),
                                    verticalItemSpacing = 12.dp,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        12.dp,
                                        Alignment.CenterHorizontally
                                    ),
                                    contentPadding = PaddingValues(
                                        bottom = 12.dp + if (isGrid) {
                                            WindowInsets
                                                .navigationBars
                                                .asPaddingValues()
                                                .calculateBottomPadding() + if (!compactHeight) {
                                                128.dp
                                            } else 0.dp
                                        } else 0.dp,
                                        top = 12.dp,
                                        end = 12.dp + cutout.calculateEndPadding(
                                            LocalLayoutDirection.current
                                        ),
                                        start = 12.dp + cutout.calculateStartPadding(
                                            LocalLayoutDirection.current
                                        )
                                    ),
                                    content = {
                                        items(currentScreenList) { screen ->
                                            val interactionSource = remember {
                                                MutableInteractionSource()
                                            }
                                            val pressed by interactionSource.collectIsPressedAsState()

                                            val cornerSize by animateDpAsState(
                                                if (pressed) 6.dp
                                                else 18.dp
                                            )
                                            PreferenceItemOverload(
                                                onClick = {
                                                    navController.popUpTo { it == Screen.Main }
                                                    navController.navigate(screen)
                                                },
                                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                    1.dp
                                                ),
                                                modifier = Modifier
                                                    .widthIn(min = 1.dp)
                                                    .fillMaxWidth()
                                                    .animateItemPlacement(),
                                                shape = RoundedCornerShape(cornerSize),
                                                title = stringResource(screen.title),
                                                subtitle = stringResource(screen.subtitle),
                                                icon = {
                                                    Icon(screen.icon!!, null)
                                                },
                                                interactionSource = interactionSource
                                            )
                                        }
                                    }
                                )
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

                    AnimatedVisibility(
                        visible = isSheetSlideable || sheetExpanded || (showScreenSearch && canSearchScreens),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AnimatedContent(
                            targetState = settingsState.groupOptionsByTypes to (showScreenSearch && canSearchScreens),
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { (groupOptionsByTypes, searching) ->
                            if (groupOptionsByTypes && !searching) {
                                NavigationBar(
                                    modifier = Modifier
                                        .drawHorizontalStroke(top = true)
                                        .height(
                                            80.dp + WindowInsets.systemBars
                                                .asPaddingValues()
                                                .calculateBottomPadding()
                                        ),
                                ) {
                                    Screen.typedEntries.forEachIndexed { index, (_, data) ->
                                        val selected = index == currentPage
                                        NavigationItem(
                                            modifier = Modifier.weight(1f),
                                            selected = selected,
                                            onClick = { currentPage = index },
                                            icon = {
                                                Icon(
                                                    if (selected) data.second else data.third,
                                                    null
                                                )
                                            },
                                            label = {
                                                Text(stringResource(data.first))
                                            }
                                        )
                                    }
                                }
                            } else {
                                BottomAppBar(
                                    modifier = Modifier.drawHorizontalStroke(top = true),
                                    actions = {
                                        if (!searching) {
                                            EnhancedButton(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                    alpha = 0.5f
                                                ),
                                                borderColor = MaterialTheme.colorScheme.outlineVariant(
                                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                                ),
                                                modifier = Modifier
                                                    .padding(horizontal = 16.dp)
                                                    .pulsate(enabled = viewModel.updateAvailable),
                                                onClick = {
                                                    viewModel.tryGetUpdate(
                                                        newRequest = true,
                                                        installedFromMarket = context.isInstalledFromPlayStore(),
                                                        onNoUpdates = {
                                                            scope.launch {
                                                                toastHost.showToast(
                                                                    icon = Icons.Rounded.FileDownloadOff,
                                                                    message = context.getString(R.string.no_updates)
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            ) {
                                                Text(
                                                    stringResource(R.string.version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                                                )
                                            }
                                        } else {
                                            if (searching) {
                                                BackHandler {
                                                    screenSearchKeyword = ""
                                                    showScreenSearch = false
                                                }
                                                ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                                                    RoundedTextField(
                                                        maxLines = 1,
                                                        hint = { Text(stringResource(id = R.string.search_here)) },
                                                        modifier = Modifier
                                                            .padding(start = 6.dp)
                                                            .offset(2.dp, (-2).dp),
                                                        keyboardOptions = KeyboardOptions.Default.copy(
                                                            imeAction = ImeAction.Search
                                                        ),
                                                        value = screenSearchKeyword,
                                                        onValueChange = {
                                                            screenSearchKeyword = it
                                                        },
                                                        startIcon = {
                                                            EnhancedIconButton(
                                                                containerColor = Color.Transparent,
                                                                contentColor = LocalContentColor.current,
                                                                enableAutoShadowAndBorder = false,
                                                                onClick = {
                                                                    screenSearchKeyword = ""
                                                                    showScreenSearch = false
                                                                },
                                                                modifier = Modifier.padding(start = 4.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.onSurface
                                                                )
                                                            }
                                                        },
                                                        endIcon = {
                                                            AnimatedVisibility(
                                                                visible = screenSearchKeyword.isNotEmpty(),
                                                                enter = fadeIn() + scaleIn(),
                                                                exit = fadeOut() + scaleOut()
                                                            ) {
                                                                EnhancedIconButton(
                                                                    containerColor = Color.Transparent,
                                                                    contentColor = LocalContentColor.current,
                                                                    enableAutoShadowAndBorder = false,
                                                                    onClick = {
                                                                        screenSearchKeyword = ""
                                                                    },
                                                                    modifier = Modifier.padding(end = 4.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = Icons.Rounded.Close,
                                                                        contentDescription = null,
                                                                        tint = MaterialTheme.colorScheme.onSurface
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        shape = CircleShape
                                                    )
                                                }
                                            } else {
                                                screenSearchKeyword = ""
                                                showScreenSearch = false
                                            }
                                        }
                                    },
                                    floatingActionButton = {
                                        if (!searching) {
                                            EnhancedFloatingActionButton(
                                                onClick = {
                                                    if (context.isInstalledFromPlayStore()) {
                                                        try {
                                                            context.startActivity(
                                                                Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    Uri.parse("market://details?id=${context.packageName}")
                                                                )
                                                            )
                                                        } catch (e: ActivityNotFoundException) {
                                                            context.startActivity(
                                                                Intent(
                                                                    Intent.ACTION_VIEW,
                                                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse(APP_LINK)
                                                            )
                                                        )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .requiredSize(size = 56.dp),
                                                content = {
                                                    if (context.isInstalledFromPlayStore()) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.GooglePlay,
                                                            contentDescription = null,
                                                            modifier = Modifier.offset(1.5.dp)
                                                        )
                                                    } else {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Github,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isSheetSlideable) {
        CompositionLocalProvider(
            LocalLayoutDirection provides if (layoutDirection == LayoutDirection.Ltr) LayoutDirection.Rtl
            else LayoutDirection.Ltr
        ) {
            ModalNavigationDrawer(
                drawerState = sideSheetState,
                drawerContent = drawerContent,
                content = content
            )
        }
    } else {
        Row {
            content.withModifier(
                Modifier.weight(1f)
            )
            if (settingsState.borderWidth > 0.dp) {
                Spacer(
                    Modifier
                        .fillMaxHeight()
                        .width(settingsState.borderWidth)
                        .background(
                            MaterialTheme.colorScheme.outlineVariant(
                                0.3f,
                                DrawerDefaults.containerColor
                            )
                        )
                )
            }
            drawerContent.withModifier(
                Modifier.container(
                    shape = RectangleShape,
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        1.dp
                    ),
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        0.3f,
                        DrawerDefaults.containerColor
                    ),
                    autoShadowElevation = 2.dp,
                    resultPadding = 0.dp
                )
            )
        }
    }
}

private fun getVersionPreRelease(): String {
    return BuildConfig.VERSION_NAME
        .replace(BuildConfig.FLAVOR, "")
        .split("-")
        .takeIf { it.size > 1 }
        ?.drop(1)?.first()
        ?.takeWhile { it.isLetter() }
        ?.uppercase()?.takeIf { it.isNotEmpty() }?.let {
            " $it"
        } ?: ""
}