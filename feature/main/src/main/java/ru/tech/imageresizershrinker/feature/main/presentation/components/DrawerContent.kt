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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.SearchBar
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee

@Composable
internal fun DrawerContent(
    sideSheetState: DrawerState,
    isSheetSlideable: Boolean,
    sheetExpanded: Boolean,
    onToggleSheetExpanded: (Boolean) -> Unit,
    layoutDirection: LayoutDirection,
    settingsBlockContent: @Composable (keyword: String) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val scope = rememberCoroutineScope()
    var settingsSearchKeyword by rememberSaveable {
        mutableStateOf("")
    }
    var showSettingsSearch by rememberSaveable { mutableStateOf(false) }
    val settingsBlock = remember {
        movableContentOf {
            settingsBlockContent(settingsSearchKeyword)
        }
    }
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
                colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
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
                    .drawHorizontalStroke()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
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
                                    onToggleSheetExpanded(!sheetExpanded)
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