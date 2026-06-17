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

package com.t8rin.imagetoolbox.feature.main.presentation.screenLogic

import android.net.Uri
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.history.AppHistoryRepository
import com.t8rin.imagetoolbox.core.domain.remote.AnalyticsManager
import com.t8rin.imagetoolbox.core.domain.utils.onEachDebounced
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.main.presentation.components.UiLastUsedTool
import com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic.SettingsComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.minutes

class MainComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onTryGetUpdate: (Boolean) -> Unit,
    @Assisted private val onGetClipList: (List<Uri>) -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    @Assisted val isUpdateAvailable: Value<Boolean>,
    private val analyticsManager: AnalyticsManager,
    appHistoryRepository: AppHistoryRepository,
    dispatchersHolder: DispatchersHolder,
    private val settingsManager: SettingsManager,
    settingsComponentFactory: SettingsComponent.Factory
) : BaseComponent(dispatchersHolder, componentContext) {

    val lastUsedTools: StateFlow<List<UiLastUsedTool>> = appHistoryRepository
        .lastUsedTools()
        .map { lastUsedTools ->
            lastUsedTools.mapNotNull { tool ->
                Screen.entries.find { it.id == tool.screenId }?.let { screen ->
                    UiLastUsedTool(
                        screen = screen,
                        openCount = tool.openCount
                    )
                }
            }
        }
        .onEachDebounced(5.minutes) { mapped ->
            analyticsManager.pushMetric(
                tag = "AppHistory",
                metric = mapped.joinToString {
                    "[${it.screen.simpleName} - ${it.openCount}]"
                }
            )
        }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val settingsComponent = settingsComponentFactory(
        componentContext = childContext("mainSettings"),
        onTryGetUpdate = onTryGetUpdate,
        onNavigate = onNavigate,
        isUpdateAvailable = isUpdateAvailable,
        onGoBack = null,
        initialSearchQuery = ""
    )

    fun tryGetUpdate(isNewRequest: Boolean = false) = onTryGetUpdate(isNewRequest)

    fun toggleFavoriteScreen(screen: Screen) {
        componentScope.launch {
            settingsManager.toggleFavoriteScreen(screen.id)
        }
    }

    fun parseClipList(
        list: List<Uri>
    ) = onGetClipList(list)

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onTryGetUpdate: (Boolean) -> Unit,
            onGetClipList: (List<Uri>) -> Unit,
            onNavigate: (Screen) -> Unit,
            isUpdateAvailable: Value<Boolean>,
        ): MainComponent
    }

}