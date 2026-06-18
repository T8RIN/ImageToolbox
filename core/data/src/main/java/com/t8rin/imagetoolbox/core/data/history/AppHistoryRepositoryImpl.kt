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

package com.t8rin.imagetoolbox.core.data.history

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.history.AppHistoryRepository
import com.t8rin.imagetoolbox.core.domain.history.model.LastUsedTool
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AppHistoryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val jsonParser: JsonParser,
    private val settingsProvider: SettingsProvider
) : AppHistoryRepository {

    private val rawLastUsedTools: Flow<List<LastUsedTool>>
        get() = dataStore.data.map { preferences ->
            preferences[LAST_USED_TOOLS]?.let { lastTools ->
                jsonParser.fromJson<LastUsedTools>(
                    json = lastTools,
                    type = LastUsedTools::class.java
                )?.tools
            }.orEmpty()
        }

    override fun lastUsedTools(
        maxCount: Int
    ): Flow<List<LastUsedTool>> = settingsProvider.settingsState.flatMapLatest { settings ->
        if (settings.showToolsHistory) {
            rawLastUsedTools.map { rawList ->
                rawList.sortedByDescending { it.openCount }.let {
                    if (maxCount < Int.MAX_VALUE) {
                        val last = rawList.lastOrNull()
                        val limited = it.take(maxCount)

                        if (last != null) {
                            listOf(last) + limited.minus(last)
                        } else {
                            limited
                        }
                    } else {
                        it
                    }
                }
            }
        } else {
            flowOf(emptyList())
        }
    }

    override fun toolUsageStatistics(): Flow<List<LastUsedTool>> = rawLastUsedTools.map { rawList ->
        rawList.sortedWith(
            compareByDescending<LastUsedTool> { it.openCount }
                .thenByDescending { it.lastOpenedTimestamp }
        )
    }

    override suspend fun pushLastTool(screenId: Int) {
        dataStore.edit { preferences ->
            val current = preferences[LAST_USED_TOOLS]?.let {
                jsonParser.fromJson(
                    json = it,
                    type = LastUsedTools::class.java
                )
            } ?: LastUsedTools(emptyList())

            val screenEntry = current.tools.find {
                it.screenId == screenId
            } ?: LastUsedTool(
                screenId = screenId,
                openCount = 0,
                lastOpenedTimestamp = System.currentTimeMillis()
            )

            val newValue = current.copy(
                tools = current.tools
                    .minus(screenEntry)
                    .plus(
                        screenEntry.copy(
                            openCount = screenEntry.openCount + 1,
                            lastOpenedTimestamp = System.currentTimeMillis()
                        )
                    )
            )

            preferences[LAST_USED_TOOLS] = jsonParser.toJson(
                obj = newValue,
                type = LastUsedTools::class.java
            ) ?: preferences[LAST_USED_TOOLS].orEmpty()
        }
    }

}

private data class LastUsedTools(
    val tools: List<LastUsedTool>
)

private val LAST_USED_TOOLS = stringPreferencesKey("LAST_USED_TOOLS")