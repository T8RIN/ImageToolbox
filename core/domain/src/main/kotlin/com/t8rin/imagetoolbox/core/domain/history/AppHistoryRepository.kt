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

package com.t8rin.imagetoolbox.core.domain.history

import com.t8rin.imagetoolbox.core.domain.history.model.AppUsageStatistics
import com.t8rin.imagetoolbox.core.domain.history.model.LastUsedTool
import kotlinx.coroutines.flow.Flow

interface AppHistoryRepository {

    fun lastUsedTools(maxCount: Int = 5): Flow<List<LastUsedTool>>

    fun toolUsageStatistics(): Flow<List<LastUsedTool>>

    fun successfulSavesCount(): Flow<Int>

    fun appUsageStatistics(): Flow<AppUsageStatistics>

    suspend fun pushLastTool(screenId: Int)

    suspend fun registerSuccessfulSave(
        savedBytes: Long,
        savedFormat: String
    )

    suspend fun resetUsageStatistics()

}