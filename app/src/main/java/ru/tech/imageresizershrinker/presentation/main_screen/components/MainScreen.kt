package ru.tech.imageresizershrinker.presentation.main_screen.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.MenuOpen
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
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
import ru.tech.imageresizershrinker.core.AUTHOR_LINK
import ru.tech.imageresizershrinker.presentation.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.presentation.theme.Emoji
import ru.tech.imageresizershrinker.presentation.theme.allIcons
import ru.tech.imageresizershrinker.presentation.theme.icons.FileSettings
import ru.tech.imageresizershrinker.presentation.theme.icons.Github
import ru.tech.imageresizershrinker.presentation.theme.icons.GooglePlay
import ru.tech.imageresizershrinker.presentation.theme.icons.Telegram
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.theme.suggestContainerColorBy
import ru.tech.imageresizershrinker.utils.helper.ContextUtils.verifyInstallerId
import ru.tech.imageresizershrinker.presentation.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.utils.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.utils.modifier.scaleOnTap
import ru.tech.imageresizershrinker.presentation.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.utils.navigation.Screen
import ru.tech.imageresizershrinker.utils.storage.defaultPrefix
import ru.tech.imageresizershrinker.presentation.widget.other.AnimationBox
import ru.tech.imageresizershrinker.presentation.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.presentation.widget.other.RevealDirection
import ru.tech.imageresizershrinker.presentation.widget.other.RevealValue
import ru.tech.imageresizershrinker.presentation.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.widget.other.rememberRevealState
import ru.tech.imageresizershrinker.presentation.widget.other.revealSwipeable
import ru.tech.imageresizershrinker.presentation.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.presentation.widget.utils.isNightMode
import kotlin.math.roundToInt

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class, ExperimentalFoundationApi::class
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
    val showAuthorDialog = rememberSaveable { mutableStateOf(false) }
    val showEmojiDialog = rememberSaveable { mutableStateOf(false) }
    val showArrangementSheet = rememberSaveable { mutableStateOf(false) }

    var showChangeFilenameDialog by rememberSaveable { mutableStateOf(false) }

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val toastHost = LocalToastHost.current

    val compactHeight =
        LocalWindowSizeClass.current.heightSizeClass == WindowHeightSizeClass.Compact
    val isSheetSlideable = !isGrid
    val layoutDirection = LocalLayoutDirection.current
    val lazyListState = rememberLazyListState()

    val updateButtonOnClick = {
        if (viewModel.updateAvailable) {
            viewModel.tryGetUpdate(
                newRequest = true,
                onNoUpdates = {
                    scope.launch {
                        toastHost.showToast(
                            icon = Icons.Rounded.FileDownloadOff,
                            message = context.getString(R.string.no_updates)
                        )
                    }
                }
            )
        } else if (context.verifyInstallerId()) {
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

    val drawerContent = @Composable {
        if (sideSheetState.isOpen && isSheetSlideable) {
            BackHandler {
                scope.launch {
                    sideSheetState.close()
                }
            }
        }

        val configuration = LocalConfiguration.current
        val state = rememberRevealState()

        val widthState by remember(state.offset) {
            derivedStateOf {
                min(
                    configuration.screenWidthDp.dp * 0.85f,
                    if (!isSheetSlideable) {
                        270.dp
                    } else DrawerDefaults.MaximumDrawerWidth
                ) - (if (!isSheetSlideable) state.offset.value.roundToInt().dp else 0.dp)
            }
        }

        ModalDrawerSheet(
            modifier = Modifier
                .width(widthState)
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
                    } else Modifier.revealSwipeable(
                        maxRevealPx = with(LocalDensity.current) { 80.dp.toPx() },
                        directions = setOf(
                            RevealDirection.EndToStart,
                        ),
                        maxAmountOfOverflow = 1.dp,
                        state = state,
                        enabled = false
                    )
                ),
            windowInsets = WindowInsets(0)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            )
                        ) {
                            Text(stringResource(R.string.settings))
                        }
                    },
                    modifier = Modifier
                        .zIndex(6f)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
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
                        if (!isSheetSlideable && compactHeight) {
                            OutlinedIconButton(
                                onClick = updateButtonOnClick,
                                colors = IconButtonDefaults.filledTonalIconButtonColors(),
                                border = BorderStroke(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(
                                        luminance = 0.3f,
                                        onTopOf = MaterialTheme.colorScheme.suggestContainerColorBy(
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                ),
                                modifier = Modifier.pulsate(enabled = viewModel.updateAvailable),
                                content = {
                                    if (viewModel.updateAvailable) {
                                        Icon(Icons.Rounded.FileDownload, null)
                                    } else if (context.verifyInstallerId()) {
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
                                    scope.launch {
                                        if (state.currentValue == RevealValue.Default) {
                                            state.animateTo(RevealValue.FullyRevealedStart)
                                        } else {
                                            state.animateTo(RevealValue.Default)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.MenuOpen,
                                    null,
                                    modifier = Modifier.rotate(
                                        animateFloatAsState(if (state.currentValue == RevealValue.Default) 0f else 180f).value
                                    )
                                )
                            }
                        }
                    }
                )
                Divider()
                LazyColumn(
                    contentPadding = WindowInsets.navigationBars.asPaddingValues(),
                    state = lazyListState
                ) {
                    settingsBlock(
                        onEditPresets = { editPresetsState.value = true },
                        onEditArrangement = { showArrangementSheet.value = true },
                        onEditFilename = { showChangeFilenameDialog = true },
                        onEditEmoji = { showEmojiDialog.value = true },
                        onEditColorScheme = { showPickColorDialog.value = true },
                        onShowAuthor = { showAuthorDialog.value = true },
                        settingsState = settingsState,
                        toastHostState = toastHost,
                        scope = scope,
                        context = context,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    val content = @Composable {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box {
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(stringResource(R.string.app_name))
                                        Badge(
                                            content = { Text("${Screen.entries.size}") },
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
                            if (isSheetSlideable) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            sideSheetState.open()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.Settings, null)
                                }
                            }
                        },
                        modifier = Modifier.drawHorizontalStroke(),
                        scrollBehavior = scrollBehavior
                    )

                    Row(Modifier.weight(1f)) {
                        AnimatedVisibility(!isSheetSlideable && settingsState.groupOptionsByTypes) {
                            Row {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .widthIn(min = 80.dp)
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
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Spacer(Modifier.height(8.dp))
                                        Screen.typedEntries.forEachIndexed { index, (_, data) ->
                                            val selected = index == currentPage
                                            NavigationRailItem(
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

                        val cutout = if (!settingsState.groupOptionsByTypes) {
                            WindowInsets.displayCutout.asPaddingValues()
                        } else PaddingValues()

                        LazyVerticalStaggeredGrid(
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
                                end = 12.dp + cutout.calculateEndPadding(LocalLayoutDirection.current),
                                start = 12.dp + cutout.calculateStartPadding(LocalLayoutDirection.current)
                            ),
                            content = {
                                items(
                                    if (settingsState.groupOptionsByTypes) {
                                        Screen.typedEntries[currentPage].first
                                    } else screenList
                                ) { screen ->
                                    AnimationBox {
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
                                            },
                                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                                1.dp
                                            )
                                        )
                                    }
                                }
                            }
                        )
                    }

                    if (isSheetSlideable) {
                        AnimatedContent(
                            targetState = settingsState.groupOptionsByTypes,
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { groupOptionsByTypes ->
                            if (groupOptionsByTypes) {
                                NavigationBar(
                                    modifier = Modifier.drawHorizontalStroke(top = true),
                                ) {
                                    Screen.typedEntries.forEachIndexed { index, (_, data) ->
                                        val selected = index == currentPage
                                        NavigationBarItem(
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
                                        Button(
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            ),
                                            border = BorderStroke(
                                                settingsState.borderWidth,
                                                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                            ),
                                            elevation = if (settingsState.borderWidth > 0.dp) {
                                                null
                                            } else {
                                                ButtonDefaults.elevatedButtonElevation()
                                            },
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .pulsate(enabled = viewModel.updateAvailable),
                                            onClick = {
                                                viewModel.tryGetUpdate(
                                                    newRequest = true,
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
                                    },
                                    floatingActionButton = {
                                        FloatingActionButton(
                                            onClick = {
                                                if (context.verifyInstallerId()) {
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
                                                .fabBorder()
                                                .requiredSize(size = 56.dp),
                                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                            content = {
                                                if (context.verifyInstallerId()) {
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
                                )
                            }
                        }
                    }
                }
                if (!isSheetSlideable && !compactHeight) {
                    LargeFloatingActionButton(
                        onClick = updateButtonOnClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .systemBarsPadding()
                            .displayCutoutPadding()
                            .pulsate(enabled = viewModel.updateAvailable)
                            .fabBorder(shape = FloatingActionButtonDefaults.largeShape),
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        content = {
                            if (viewModel.updateAvailable) {
                                Icon(
                                    imageVector = Icons.Rounded.FileDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
                                )
                            } else if (context.verifyInstallerId()) {
                                Icon(
                                    imageVector = Icons.Rounded.GooglePlay,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(FloatingActionButtonDefaults.LargeIconSize)
                                        .offset(1.5.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Github,
                                    contentDescription = null,
                                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize)
                                )
                            }
                        }
                    )
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
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(settingsState.borderWidth.coerceAtLeast(1.dp))
                    .background(
                        MaterialTheme.colorScheme.outlineVariant(
                            0.3f,
                            DrawerDefaults.containerColor
                        )
                    )
            )
            drawerContent()
        }
    }

    val showColorPicker = rememberSaveable { mutableStateOf(false) }
    AvailableColorTuplesDialog(
        visible = showPickColorDialog,
        colorTupleList = viewModel.colorTupleList,
        currentColorTuple = getAppColorTuple(
            defaultColorTuple = viewModel.appColorTuple,
            dynamicColor = viewModel.dynamicColors,
            darkTheme = viewModel.nightMode.isNightMode()
        ),
        openColorPicker = {
            showColorPicker.value = true
        },
        colorPicker = { onUpdateColorTuples ->
            ColorPickerDialog(
                visible = showColorPicker,
                colorTuple = viewModel.appColorTuple,
                onColorChange = {
                    viewModel.updateColorTuple(it)
                    onUpdateColorTuples(viewModel.colorTupleList + it)
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
            mutableStateOf(viewModel.filenamePrefix.takeIf { it.isNotEmpty() } ?: defaultPrefix())
        }
        AlertDialog(
            modifier = Modifier
                .width(340.dp)
                .padding(16.dp)
                .alertDialog(),
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
                                text = defaultPrefix(),
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
                OutlinedButton(
                    onClick = {
                        viewModel.updateFilename(value.trim())
                        showChangeFilenameDialog = false
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = if (settingsState.isNightMode) 0.5f
                            else 1f
                        ),
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(
                            onTopOf = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ),
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    EmojiSheet(
        selectedEmojiIndex = viewModel.selectedEmoji,
        emojis = remember { Emoji.allIcons },
        onEmojiPicked = viewModel::updateEmoji,
        visible = showEmojiDialog
    )

    SimpleSheet(
        visible = showAuthorDialog,
        title = {
            TitleItem(
                text = stringResource(R.string.app_developer_nick),
                icon = Icons.Rounded.Person
            )
        },
        confirmButton = {
            OutlinedButton(
                onClick = { showAuthorDialog.value = false },
                border = BorderStroke(
                    settingsState.borderWidth, MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(16.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://t.me/${context.getString(R.string.app_developer_nick)}")
                                )
                            )
                        },
                        title = stringResource(R.string.telegram),
                        icon = Icons.Rounded.Telegram,
                        subtitle = stringResource(R.string.app_developer_nick)
                    )
                    Spacer(Modifier.height(8.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            Intent(Intent.ACTION_SENDTO).apply {
                                data =
                                    Uri.parse("mailto:${context.getString(R.string.developer_email)}")
                                context.startActivity(this)
                            }
                        },
                        title = stringResource(R.string.email),
                        icon = Icons.Rounded.AlternateEmail,
                        subtitle = stringResource(R.string.developer_email)
                    )
                    Spacer(Modifier.height(8.dp))
                    PreferenceItem(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(AUTHOR_LINK)
                                )
                            )
                        },
                        title = stringResource(R.string.github),
                        icon = Icons.Rounded.Github,
                        subtitle = stringResource(R.string.app_developer_nick)
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        }
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
            OutlinedButton(
                onClick = { showArrangementSheet.value = false },
                border = BorderStroke(
                    settingsState.borderWidth, MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(R.string.close))
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
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        }
    )
}