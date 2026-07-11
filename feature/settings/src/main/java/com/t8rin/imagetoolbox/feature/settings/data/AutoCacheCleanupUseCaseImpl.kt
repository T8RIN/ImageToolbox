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

package com.t8rin.imagetoolbox.feature.settings.data

import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.settings.domain.AutoCacheCleanupUseCase
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.CacheAutoClearInterval
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AutoCacheCleanupUseCaseImpl @Inject constructor(
    private val fileController: FileController,
    private val settingsManager: SettingsManager,
    private val appScope: AppScope
) : AutoCacheCleanupUseCase {
    override fun clearCacheIfNeeded(settings: SettingsState) {
        if (!settings.clearCacheOnLaunch) return
        if (fileController.getCacheSize() <= settings.cacheAutoClearLimitBytes) return

        val now = System.currentTimeMillis()
        val intervalElapsed = settings.cacheAutoClearInterval ==
                CacheAutoClearInterval.OnAppLaunch ||
                settings.lastCacheAutoClearTimestampMillis <= 0L ||
                now - settings.lastCacheAutoClearTimestampMillis >=
                settings.cacheAutoClearInterval.duration.inWholeMilliseconds

        if (!intervalElapsed) return

        fileController.clearCache {
            appScope.launch {
                settingsManager.setLastCacheAutoClearTimestampMillis(now)
            }
        }
    }
}