@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.presentation.main_screen.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.t8rin.dynamic.theme.getAppColorTuple
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.APP_LINK
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.Emoji
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.allIcons
import ru.tech.imageresizershrinker.presentation.root.icons.emoji.allIconsCategorized
import ru.tech.imageresizershrinker.presentation.root.icons.material.FileSettings
import ru.tech.imageresizershrinker.presentation.root.icons.material.Github
import ru.tech.imageresizershrinker.presentation.root.icons.material.GooglePlay
import ru.tech.imageresizershrinker.presentation.root.model.isFirstLaunch
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.AvailableColorTuplesSheet
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.ColorTuplePicker
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.rotateAnimation
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalWindowSizeClass

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun MainScreen(
    screenList: List<Screen>,
    viewModel: MainViewModel
) {
    val navController = LocalNavController.current
    val settingsState = LocalSettingsState.current
    val editPresetsState = LocalEditPresetsState.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

    val scope = rememberCoroutineScope()

    val showPickColorDialog = rememberSaveable { mutableStateOf(false) }
    val showEmojiDialog = rememberSaveable { mutableStateOf(false) }
    val showArrangementSheet = rememberSaveable { mutableStateOf(false) }

    var showChangeFilenameDialog by rememberSaveable { mutableStateOf(false) }

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val toastHost = LocalToastHost.current

    val compactHeight =
        LocalWindowSizeClass.current.heightSizeClass == WindowHeightSizeClass.Compact
    val isSheetSlideable = !isGrid
    val layoutDirection = LocalLayoutDirection.current

    val updateButtonOnClick = {
        if (viewModel.updateAvailable) {
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
        } else if (context.isInstalledFromPlayStore()) {
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
    }

    var sheetExpanded by rememberSaveable { mutableStateOf(false) }
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
                }
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
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            6.dp
                        )
                    ),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                6.dp
                            )
                        ) {
                            Text(
                                stringResource(R.string.settings),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    modifier = Modifier
                        .zIndex(6f)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                        .drawHorizontalStroke(),
                    actions = {
                        if (isSheetSlideable) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        sideSheetState.close()
                                    }
                                }
                            ) {
                                Icon(Icons.Rounded.Close, null)
                            }
                        }
                        if (!isSheetSlideable) {
                            EnhancedIconButton(
                                onClick = updateButtonOnClick,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.pulsate(enabled = viewModel.updateAvailable),
                                content = {
                                    if (viewModel.updateAvailable) {
                                        Icon(Icons.Rounded.FileDownload, null)
                                    } else if (context.isInstalledFromPlayStore()) {
                                        Icon(
                                            Icons.Rounded.GooglePlay,
                                            null,
                                            modifier = Modifier.offset(1.5.dp)
                                        )
                                    } else {
                                        Icon(Icons.Rounded.Github, null)
                                    }
                                }
                            )
                        }
                    },
                    navigationIcon = {
                        if (!isSheetSlideable) {
                            IconButton(
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
                )
                SettingsBlock(
                    onEditPresets = { editPresetsState.value = true },
                    onEditArrangement = { showArrangementSheet.value = true },
                    onEditFilename = { showChangeFilenameDialog = true },
                    onEditEmoji = { showEmojiDialog.value = true },
                    onEditColorScheme = { showPickColorDialog.value = true },
                    viewModel = viewModel
                )
            }
        }
    }

    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    val content = @Composable {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box {
                val canSearch = settingsState.screensSearchEnabled
                var showSearch by rememberSaveable(canSearch) { mutableStateOf(false) }
                var searchValue by rememberSaveable(canSearch) { mutableStateOf("") }
                val currentList by remember(
                    settingsState.groupOptionsByTypes,
                    searchValue,
                    screenList
                ) {
                    derivedStateOf {
                        if (settingsState.groupOptionsByTypes && (searchValue.isEmpty() && !showSearch)) {
                            Screen.typedEntries[currentPage].first
                        } else {
                            screenList
                        }.let { screens ->
                            if (searchValue.isNotEmpty() && canSearch) {
                                screens.filter {
                                    val string =
                                        context.getString(it.title) + " " + context.getString(it.subtitle)
                                    string.contains(other = searchValue, ignoreCase = true)
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
                                        "${Screen.entries.size}".plus(
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
                                visible = !showSearch && canSearch,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut()
                            ) {
                                IconButton(
                                    onClick = { showSearch = true && canSearch }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Search,
                                        contentDescription = null
                                    )
                                }
                            }
                            if (isSheetSlideable) {
                                IconButton(
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
                            visible = !isSheetSlideable && settingsState.groupOptionsByTypes && searchValue.isEmpty() && !sheetExpanded,
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
                            if (!settingsState.groupOptionsByTypes || searchValue.isNotEmpty()) {
                                WindowInsets.displayCutout.asPaddingValues()
                            } else PaddingValues()

                        AnimatedContent(
                            targetState = currentList.isNotEmpty(),
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { hasScreens ->
                            if (hasScreens) {
                                LazyVerticalStaggeredGrid(
                                    reverseLayout = showSearch && searchValue.isNotEmpty() && canSearch,
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
                                        items(currentList) { screen ->
                                            PreferenceItemOverload(
                                                onClick = {
                                                    navController.popUpTo { it == Screen.Main }
                                                    navController.navigate(screen)
                                                },
                                                onLongClick = {
                                                    showArrangementSheet.value =
                                                        !settingsState.groupOptionsByTypes
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .animateItemPlacement(),
                                                title = stringResource(screen.title),
                                                subtitle = stringResource(screen.subtitle),
                                                icon = {
                                                    Icon(screen.icon!!, null)
                                                }
                                            )
                                        }
                                    }
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
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
                        visible = isSheetSlideable || sheetExpanded || (showSearch && canSearch),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AnimatedContent(
                            targetState = settingsState.groupOptionsByTypes to (showSearch && canSearch),
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
                                                    searchValue = ""
                                                    showSearch = false
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
                                                        value = searchValue,
                                                        onValueChange = { searchValue = it },
                                                        startIcon = {
                                                            IconButton(
                                                                onClick = {
                                                                    searchValue = ""
                                                                    showSearch = false
                                                                },
                                                                modifier = Modifier.padding(start = 4.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.AutoMirrored.Rounded.ArrowBack,
                                                                    null
                                                                )
                                                            }
                                                        },
                                                        endIcon = {
                                                            AnimatedVisibility(
                                                                visible = searchValue.isNotEmpty(),
                                                                enter = fadeIn() + scaleIn(),
                                                                exit = fadeOut() + scaleOut()
                                                            ) {
                                                                IconButton(
                                                                    onClick = {
                                                                        searchValue = ""
                                                                    },
                                                                    modifier = Modifier.padding(end = 4.dp)
                                                                ) {
                                                                    Icon(Icons.Rounded.Close, null)
                                                                }
                                                            }
                                                        },
                                                        shape = CircleShape
                                                    )
                                                }
                                            } else {
                                                searchValue = ""
                                                showSearch = false
                                            }
                                        }
                                    },
                                    floatingActionButton = {
                                        if (!searching) {
                                            FloatingActionButton(
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
                                                    .autoElevatedBorder()
                                                    .requiredSize(size = 56.dp),
                                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                                content = {
                                                    if (context.isInstalledFromPlayStore()) {
                                                        Icon(
                                                            Icons.Rounded.GooglePlay,
                                                            null,
                                                            modifier = Modifier.offset(1.5.dp)
                                                        )
                                                    } else {
                                                        Icon(Icons.Rounded.Github, null)
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
            Box(Modifier.weight(1f)) {
                content()
            }
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
            Box(
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
            ) {
                drawerContent()
            }
        }
    }

    val showColorPicker = rememberSaveable { mutableStateOf(false) }
    AvailableColorTuplesSheet(
        visible = showPickColorDialog,
        colorTupleList = settingsState.colorTupleList,
        currentColorTuple = getAppColorTuple(
            defaultColorTuple = settingsState.appColorTuple,
            dynamicColor = settingsState.isDynamicColors,
            darkTheme = settingsState.isNightMode
        ),
        onToggleInvertColors = viewModel::toggleInvertColors,
        onThemeStyleSelected = { viewModel.setThemeStyle(it.ordinal) },
        updateThemeContrast = viewModel::updateThemeContrast,
        openColorPicker = {
            showColorPicker.value = true
        },
        colorPicker = { onUpdateColorTuples ->
            ColorTuplePicker(
                visible = showColorPicker,
                colorTuple = settingsState.appColorTuple,
                onColorChange = {
                    viewModel.updateColorTuple(it)
                    onUpdateColorTuples(settingsState.colorTupleList + it)
                }
            )
        },
        onUpdateColorTuples = {
            viewModel.updateColorTuples(it)
        },
        onPickTheme = { viewModel.updateColorTuple(it) }
    )

    if (showChangeFilenameDialog) {
        var value by remember {
            mutableStateOf(
                settingsState
                    .filenamePrefix
                    .takeIf {
                        it.isNotEmpty()
                    } ?: context.getString(R.string.default_prefix)
            )
        }
        AlertDialog(
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp)
                .alertDialogBorder(),
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showChangeFilenameDialog = false },
            icon = {
                Icon(Icons.Rounded.FileSettings, null)
            },
            title = {
                Text(stringResource(R.string.prefix))
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        placeholder = {
                            Text(
                                text = stringResource(R.string.default_prefix),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        value = value,
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                        onValueChange = {
                            value = it
                        }
                    )
                }
            },
            confirmButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = if (settingsState.isNightMode) 0.5f
                        else 1f
                    ),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = {
                        viewModel.updateFilename(value.trim())
                        showChangeFilenameDialog = false
                    },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                    ),
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    EmojiSheet(
        selectedEmojiIndex = viewModel.settingsState.selectedEmoji ?: 0,
        emojiWithCategories = Emoji.allIconsCategorized(),
        allEmojis = Emoji.allIcons(),
        onEmojiPicked = viewModel::updateEmoji,
        visible = showEmojiDialog
    )

    SimpleSheet(
        visible = showArrangementSheet,
        title = {
            TitleItem(
                text = stringResource(R.string.order),
                icon = Icons.Rounded.TableRows
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = { showArrangementSheet.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                val data = remember { mutableStateOf(screenList) }
                val state = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        data.value = data.value.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    },
                    onDragEnd = { _, _ ->
                        viewModel.updateOrder(data.value)
                    }
                )
                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .reorderable(state)
                        .detectReorderAfterLongPress(state),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.value, key = { it }) { screen ->
                        ReorderableItem(state, key = screen) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 1.dp)
                            PreferenceItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation, RoundedCornerShape(16.dp)),
                                title = stringResource(screen.title),
                                subtitle = stringResource(screen.subtitle),
                                icon = screen.icon,
                                endIcon = Icons.Rounded.DragHandle,
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    tonalElevation
                                )
                            )
                        }
                    }
                }
            }
        }
    )
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
