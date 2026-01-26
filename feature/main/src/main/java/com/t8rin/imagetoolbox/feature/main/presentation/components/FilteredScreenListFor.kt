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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.utils.appContext
import java.util.Locale

@Composable
internal fun filteredScreenListFor(
    screenSearchKeyword: String,
    selectedNavigationItem: Int,
    showScreenSearch: Boolean
): State<List<Screen>> {
    val settingsState = LocalSettingsState.current
    val canSearchScreens = settingsState.screensSearchEnabled

    val screenList by remember(settingsState.screenList) {
        derivedStateOf {
            settingsState.screenList.mapNotNull {
                Screen.entries.find { s -> s.id == it }
            }.takeIf { it.isNotEmpty() } ?: Screen.entries
        }
    }

    return remember(
        settingsState.groupOptionsByTypes,
        settingsState.favoriteScreenList,
        screenSearchKeyword,
        screenList,
        selectedNavigationItem,
        showScreenSearch
    ) {
        derivedStateOf {
            when {
                settingsState.groupOptionsByTypes && (screenSearchKeyword.isEmpty() && !showScreenSearch) -> {
                    Screen.typedEntries[selectedNavigationItem].entries
                }

                !settingsState.groupOptionsByTypes && (screenSearchKeyword.isEmpty() && !showScreenSearch) -> {
                    if (selectedNavigationItem == 0) {
                        screenList.filter {
                            it.id in settingsState.favoriteScreenList
                        }
                    } else screenList
                }

                else -> screenList
            }.let { screens ->
                if (screenSearchKeyword.isNotEmpty() && canSearchScreens) {
                    screens.filter {
                        val string =
                            appContext.getString(it.title) + " " + appContext.getString(it.subtitle)
                        val stringEn = appContext.getStringLocalized(it.title, Locale.ENGLISH)
                            .plus(" ")
                            .plus(appContext.getStringLocalized(it.subtitle, Locale.ENGLISH))
                        stringEn.contains(other = screenSearchKeyword, ignoreCase = true).or(
                            string.contains(other = screenSearchKeyword, ignoreCase = true)
                        )
                    }
                } else screens
            }
        }
    }
}