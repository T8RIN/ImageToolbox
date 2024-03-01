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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.APP_LINK
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.isFirstLaunch
import ru.tech.imageresizershrinker.core.ui.icons.material.Github
import ru.tech.imageresizershrinker.core.ui.icons.material.GooglePlay
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberClipboardData
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
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass

@Composable
internal fun MainScreenContent(
    layoutDirection: LayoutDirection,
    isSheetSlideable: Boolean,
    sideSheetState: DrawerState,
    sheetExpanded: Boolean,
    isGrid: Boolean,
    onGetClipList: (List<Uri>) -> Unit,
    onShowSnowfall: () -> Unit,
    onTryGetUpdate: () -> Unit,
    updateAvailable: Boolean
) {
    val settingsState = LocalSettingsState.current
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val screenList by remember(settingsState.screenList) {
        derivedStateOf {
            settingsState.screenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }.takeIf { it.isNotEmpty() } ?: Screen.entries
        }
    }
    val scope = rememberCoroutineScope()
    val compactHeight =
        LocalWindowSizeClass.current.heightSizeClass == WindowHeightSizeClass.Compact
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
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
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                LargeTopAppBar(
                    title = {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme
                                    .surfaceColorAtElevation(3.dp)
                            ) {
                                val titleText = remember {
                                    "${Screen.featuresCount}".plus(
                                        if (BuildConfig.FLAVOR == "market") {
                                            versionPreRelease
                                        } else {
                                            " ${BuildConfig.FLAVOR.uppercase()} $versionPreRelease"
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
                                            .scaleOnTap {
                                                onShowSnowfall()
                                            }
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    TopAppBarEmoji()
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme
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
                                    imageVector = Icons.AutoMirrored.Rounded.ManageSearch,
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
                    val showNavRail =
                        !isSheetSlideable && settingsState.groupOptionsByTypes && screenSearchKeyword.isEmpty() && !sheetExpanded
                    AnimatedVisibility(
                        visible = showNavRail,
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
                                                AnimatedContent(
                                                    targetState = selected,
                                                    transitionSpec = {
                                                        fadeIn() togetherWith fadeOut()
                                                    }
                                                ) { selected ->
                                                    Icon(
                                                        imageVector = if (selected) data.second else data.third,
                                                        contentDescription = null
                                                    )
                                                }
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

                    val cutout = if (!showNavRail) {
                        WindowInsets.displayCutout.asPaddingValues()
                    } else PaddingValues()

                    AnimatedContent(
                        targetState = currentScreenList.isNotEmpty(),
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { hasScreens ->
                        if (hasScreens) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                val clipboardData by rememberClipboardData()
                                LazyVerticalStaggeredGrid(
                                    reverseLayout = showScreenSearch && screenSearchKeyword.isNotEmpty() && canSearchScreens,
                                    modifier = Modifier.fillMaxSize(),
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
                                        } else {
                                            0.dp
                                        } + if (clipboardData.isNotEmpty()) 76.dp
                                        else 0.dp,
                                        top = 12.dp,
                                        end = 12.dp,
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
                                                startIcon = {
                                                    AnimatedContent(
                                                        targetState = screen.icon,
                                                        transitionSpec = {
                                                            (slideInVertically() + fadeIn() + scaleIn())
                                                                .togetherWith(slideOutVertically { it / 2 } + fadeOut() + scaleOut())
                                                                .using(SizeTransform(false))
                                                        }
                                                    ) { icon ->
                                                        Icon(
                                                            imageVector = icon!!,
                                                            contentDescription = null
                                                        )
                                                    }
                                                },
                                                interactionSource = interactionSource
                                            )
                                        }
                                    }
                                )

                                BoxAnimatedVisibility(
                                    visible = clipboardData.isNotEmpty(),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp),
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ) {
                                                Text(clipboardData.size.toString())
                                            }
                                        }
                                    ) {
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                onGetClipList(clipboardData)
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
                                            AnimatedContent(
                                                targetState = selected,
                                                transitionSpec = {
                                                    fadeIn() togetherWith fadeOut()
                                                }
                                            ) { selected ->
                                                Icon(
                                                    imageVector = if (selected) data.second else data.third,
                                                    contentDescription = null
                                                )
                                            }
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
                                                .pulsate(enabled = updateAvailable),
                                            onClick = onTryGetUpdate
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

private val versionPreRelease: String by lazy {
    BuildConfig.VERSION_NAME
        .replace(BuildConfig.FLAVOR, "")
        .split("-")
        .takeIf { it.size > 1 }
        ?.drop(1)?.first()
        ?.takeWhile { it.isLetter() }
        ?.uppercase()?.takeIf { it.isNotEmpty() }?.let {
            " $it"
        } ?: ""
}