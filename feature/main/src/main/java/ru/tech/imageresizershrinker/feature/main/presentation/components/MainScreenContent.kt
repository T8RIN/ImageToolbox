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

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen

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
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val screenList by remember(settingsState.screenList) {
        derivedStateOf {
            settingsState.screenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }.takeIf { it.isNotEmpty() } ?: Screen.entries
        }
    }

    var selectedNavigationItem by rememberSaveable { mutableIntStateOf(0) }
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
                        Screen.typedEntries[selectedNavigationItem].first
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
                MainTopAppBar(
                    scrollBehavior = scrollBehavior,
                    showScreenSearch = showScreenSearch,
                    canSearchScreens = canSearchScreens,
                    onChangeShowScreenSearch = {
                        showScreenSearch = it
                    },
                    onShowSnowfall = onShowSnowfall,
                    sideSheetState = sideSheetState,
                    isSheetSlideable = isSheetSlideable
                )

                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    val showNavRail =
                        isGrid && settingsState.groupOptionsByTypes && screenSearchKeyword.isEmpty() && !sheetExpanded

                    AnimatedVisibility(
                        visible = showNavRail,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        MainNavigationRail(
                            selectedIndex = selectedNavigationItem,
                            onValueChange = {
                                selectedNavigationItem = it
                            }
                        )
                    }

                    ScreenPreferenceSelection(
                        currentScreenList = currentScreenList,
                        showScreenSearch = showScreenSearch,
                        screenSearchKeyword = screenSearchKeyword,
                        canSearchScreens = canSearchScreens,
                        isGrid = isGrid,
                        isSheetSlideable = isSheetSlideable,
                        showNavRail = showNavRail,
                        onGetClipList = onGetClipList
                    )
                }

                AnimatedVisibility(
                    visible = !isGrid || sheetExpanded || (showScreenSearch && canSearchScreens),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AnimatedContent(
                        targetState = settingsState.groupOptionsByTypes to (showScreenSearch && canSearchScreens),
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { (groupOptionsByTypes, searching) ->
                        if (groupOptionsByTypes && !searching) {
                            MainNavigationBar(
                                selectedIndex = selectedNavigationItem,
                                onValueChange = { selectedNavigationItem = it }
                            )
                        } else {
                            SearchableBottomBar(
                                searching = searching,
                                updateAvailable = updateAvailable,
                                onTryGetUpdate = onTryGetUpdate,
                                screenSearchKeyword = screenSearchKeyword,
                                onUpdateSearch = {
                                    screenSearchKeyword = it
                                },
                                onCloseSearch = {
                                    showScreenSearch = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}