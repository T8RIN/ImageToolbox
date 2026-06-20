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
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.history.AppHistoryRepository
import com.t8rin.imagetoolbox.core.domain.history.model.AppUsageStatistics
import com.t8rin.imagetoolbox.core.domain.history.model.LastUsedTool
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
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

    override fun successfulSavesCount(): Flow<Int> = dataStore.data.map { preferences ->
        preferences[SUCCESSFUL_SAVES_COUNT] ?: 0
    }

    override fun appUsageStatistics(): Flow<AppUsageStatistics> =
        dataStore.data.map { preferences ->
            AppUsageStatistics(
                successfulSavesCount = preferences[SUCCESSFUL_SAVES_COUNT] ?: 0,
                savedBytes = preferences[SAVED_BYTES] ?: 0,
                lastActivityDayEpoch = preferences[LAST_ACTIVITY_DAY_EPOCH] ?: 0,
                currentActivityStreak = preferences[CURRENT_ACTIVITY_STREAK] ?: 0,
                savedFormatCounts = preferences[SAVED_FORMAT_COUNTERS]?.let { counters ->
                    jsonParser.fromJson<SavedFormatCounters>(
                        json = counters,
                        type = SavedFormatCounters::class.java
                    )?.counters
                }.orEmpty()
            )
        }

    override suspend fun pushLastTool(screenId: Int) {
        dataStore.edit { preferences ->
            val todayEpochDay = LocalDate.now().toEpochDay()
            val lastActivityDayEpoch = preferences[LAST_ACTIVITY_DAY_EPOCH]

            if (lastActivityDayEpoch != todayEpochDay) {
                preferences[CURRENT_ACTIVITY_STREAK] =
                    if (lastActivityDayEpoch == todayEpochDay - 1) {
                        (preferences[CURRENT_ACTIVITY_STREAK] ?: 0) + 1
                    } else {
                        1
                    }
                preferences[LAST_ACTIVITY_DAY_EPOCH] = todayEpochDay
            }

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

    override suspend fun registerSuccessfulSave(
        savedBytes: Long,
        savedFormat: String
    ) {
        dataStore.edit { preferences ->
            preferences[SUCCESSFUL_SAVES_COUNT] = (preferences[SUCCESSFUL_SAVES_COUNT] ?: 0) + 1
            preferences[SAVED_BYTES] = (preferences[SAVED_BYTES] ?: 0) + savedBytes.coerceAtLeast(0)

            val format = savedFormat.lowercase().trim()
            if (format.isNotEmpty()) {
                val current = preferences[SAVED_FORMAT_COUNTERS]?.let {
                    jsonParser.fromJson(
                        json = it,
                        type = SavedFormatCounters::class.java
                    )
                } ?: SavedFormatCounters(emptyMap())

                preferences[SAVED_FORMAT_COUNTERS] = jsonParser.toJson(
                    obj = current.copy(
                        counters = current.counters + (format to ((current.counters[format]
                            ?: 0) + 1))
                    ),
                    type = SavedFormatCounters::class.java
                ) ?: preferences[SAVED_FORMAT_COUNTERS].orEmpty()
            }
        }
    }

    override suspend fun resetUsageStatistics() {
        dataStore.edit { preferences ->
            preferences.remove(LAST_USED_TOOLS)
            preferences.remove(SUCCESSFUL_SAVES_COUNT)
            preferences.remove(SAVED_BYTES)
            preferences.remove(LAST_ACTIVITY_DAY_EPOCH)
            preferences.remove(CURRENT_ACTIVITY_STREAK)
            preferences.remove(SAVED_FORMAT_COUNTERS)
        }
    }

}

private data class LastUsedTools(
    val tools: List<LastUsedTool>
)

private data class SavedFormatCounters(
    val counters: Map<String, Int>
)

private val LAST_USED_TOOLS = stringPreferencesKey("LAST_USED_TOOLS")
private val SUCCESSFUL_SAVES_COUNT = intPreferencesKey("SUCCESSFUL_SAVES_COUNT")
private val SAVED_BYTES = longPreferencesKey("SAVED_BYTES")
private val LAST_ACTIVITY_DAY_EPOCH = longPreferencesKey("LAST_ACTIVITY_DAY_EPOCH")
private val CURRENT_ACTIVITY_STREAK = intPreferencesKey("CURRENT_ACTIVITY_STREAK")
private val SAVED_FORMAT_COUNTERS = stringPreferencesKey("SAVED_FORMAT_COUNTERS")