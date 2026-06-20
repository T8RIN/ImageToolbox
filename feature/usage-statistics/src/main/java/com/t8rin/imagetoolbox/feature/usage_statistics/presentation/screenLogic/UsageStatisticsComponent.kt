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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation.screenLogic

import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.history.AppHistoryRepository
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UiToolUsageStatistic
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticsState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class UsageStatisticsComponent @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    private val appHistoryRepository: AppHistoryRepository,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    val state: StateFlow<UsageStatisticsState> = combine(
        settingsProvider.settingsState,
        appHistoryRepository.toolUsageStatistics(),
        appHistoryRepository.appUsageStatistics()
    ) { settings, history, usageStatistics ->
        UsageStatisticsState(
            appOpenCount = settings.appOpenCount,
            successfulSavesCount = usageStatistics.successfulSavesCount,
            activityStreak = usageStatistics.currentActivityStreak,
            savedBytes = usageStatistics.savedBytes,
            topSavedFormat = usageStatistics.savedFormatCounts
                .maxWithOrNull(compareBy<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                ?.key
                ?.asSavedFormatLabel().orEmpty(),
            tools = history.mapNotNull { item ->
                Screen.entries.find { it.id == item.screenId }?.let { screen ->
                    UiToolUsageStatistic(
                        screen = screen,
                        openCount = item.openCount,
                        lastOpenedTimestamp = item.lastOpenedTimestamp
                    )
                }
            }
        )
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.Eagerly,
        initialValue = UsageStatisticsState()
    )

    private fun String.asSavedFormatLabel(): String {
        val format = ImageFormat.entries.find {
            it.title.equals(this, ignoreCase = true) ||
                    it.extension.equals(this, ignoreCase = true) ||
                    it.mimeType.entry.equals(this, ignoreCase = true)
        }

        return format?.title ?: uppercase()
    }

    fun resetUsageStatistics() {
        componentScope.launch {
            appHistoryRepository.resetUsageStatistics()
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): UsageStatisticsComponent
    }
}