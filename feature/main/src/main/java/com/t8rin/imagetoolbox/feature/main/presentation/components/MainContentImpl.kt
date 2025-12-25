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

package com.t8rin.imagetoolbox.feature.main.presentation.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.t8rin.imagetoolbox.core.settings.domain.model.SnowfallMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberCurrentLifecycleEvent
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.realisticSnowfall
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
internal fun MainContentImpl(
    layoutDirection: LayoutDirection,
    isSheetSlideable: Boolean,
    sideSheetState: DrawerState,
    sheetExpanded: Boolean,
    isGrid: Boolean,
    onGetClipList: (List<Uri>) -> Unit,
    onNavigate: (Screen) -> Unit,
    onToggleFavorite: (Screen) -> Unit,
    onShowFeaturesFall: () -> Unit,
    onTryGetUpdate: () -> Unit,
    isUpdateAvailable: Boolean
) {
    val settingsState = LocalSettingsState.current

    var selectedNavigationItem by rememberSaveable { mutableIntStateOf(0) }
    val canSearchScreens = settingsState.screensSearchEnabled
    var showScreenSearch by rememberSaveable(canSearchScreens) { mutableStateOf(false) }
    var screenSearchKeyword by rememberSaveable(canSearchScreens) { mutableStateOf("") }
    val currentScreenList by filteredScreenListFor(
        screenSearchKeyword = screenSearchKeyword,
        selectedNavigationItem = selectedNavigationItem,
        showScreenSearch = showScreenSearch
    )

    LocalLayoutDirection.ProvidesValue(layoutDirection) {
        val snowfallMode = settingsState.snowfallMode

        val event = rememberCurrentLifecycleEvent()

        val showSnowfall by remember(snowfallMode, event) {
            derivedStateOf {
                when (snowfallMode) {
                    SnowfallMode.Auto -> {
                        LocalDate.now().run {
                            (monthValue == 12 && dayOfMonth >= 22) || (monthValue == 1 && dayOfMonth <= 11)
                        }
                    }

                    SnowfallMode.Enabled -> true
                    SnowfallMode.Disabled -> false
                }
            }
        }

        val scrollBehavior = if (showSnowfall) {
            TopAppBarDefaults.pinnedScrollBehavior()
        } else {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        }

        val topBar: @Composable () -> Unit = {
            MainTopAppBar(
                scrollBehavior = scrollBehavior,
                onShowFeaturesFall = onShowFeaturesFall,
                sideSheetState = sideSheetState,
                isSheetSlideable = isSheetSlideable,
                onNavigate = onNavigate,
                type = if (showSnowfall) {
                    EnhancedTopAppBarType.Medium
                } else {
                    EnhancedTopAppBarType.Large
                },
                modifier = Modifier.realisticSnowfall(
                    enabled = showSnowfall
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            val colorScheme = MaterialTheme.colorScheme

            var key by remember {
                mutableStateOf(colorScheme.primary)
            }

            LaunchedEffect(colorScheme) {
                delay(200)
                key = colorScheme.primary
            }

            if (showSnowfall) {
                key(key) {
                    topBar()
                }
            } else {
                topBar()
            }

            Row(
                modifier = Modifier.weight(1f)
            ) {
                val showNavRail =
                    isGrid && screenSearchKeyword.isEmpty() && !sheetExpanded

                AnimatedVisibility(
                    visible = showNavRail,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    if (settingsState.groupOptionsByTypes) {
                        MainNavigationRail(
                            selectedIndex = selectedNavigationItem,
                            onValueChange = {
                                selectedNavigationItem = it
                            }
                        )
                    } else {
                        MainNavigationRailForFavorites(
                            selectedIndex = selectedNavigationItem,
                            onValueChange = {
                                selectedNavigationItem = it
                            }
                        )
                    }
                }

                ScreenPreferenceSelection(
                    currentScreenList = currentScreenList,
                    showScreenSearch = showScreenSearch,
                    screenSearchKeyword = screenSearchKeyword,
                    isGrid = isGrid,
                    isSheetSlideable = isSheetSlideable,
                    showNavRail = showNavRail,
                    onChangeShowScreenSearch = {
                        showScreenSearch = it
                    },
                    onGetClipList = onGetClipList,
                    onNavigateToScreenWithPopUpTo = onNavigate,
                    onNavigationBarItemChange = { selectedNavigationItem = it },
                    onToggleFavorite = onToggleFavorite
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
                    } else if (!searching) {
                        MainNavigationBarForFavorites(
                            selectedIndex = selectedNavigationItem,
                            onValueChange = { selectedNavigationItem = it }
                        )
                    } else {
                        SearchableBottomBar(
                            searching = true,
                            updateAvailable = isUpdateAvailable,
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