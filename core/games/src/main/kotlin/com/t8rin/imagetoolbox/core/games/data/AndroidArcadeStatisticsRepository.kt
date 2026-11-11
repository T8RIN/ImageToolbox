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

package com.t8rin.imagetoolbox.core.games.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.t8rin.imagetoolbox.core.games.ArcadeGameRegistry
import com.t8rin.imagetoolbox.core.games.ArcadeGameStatistics
import com.t8rin.imagetoolbox.core.games.ArcadeStatisticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AndroidArcadeStatisticsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ArcadeStatisticsRepository {

    private val keys = ArcadeGameRegistry.games.associate { it.name to StatisticsKeys(it.name) }

    override val statistics: Flow<List<ArcadeGameStatistics>> = dataStore.data.map { preferences ->
        ArcadeGameRegistry.games.mapNotNull { game ->
            val keys = keys.getValue(game.name)
            val startedRounds = preferences[keys.startedRounds] ?: 0
            val finishedRounds = preferences[keys.finishedRounds] ?: 0
            if (startedRounds == 0 && finishedRounds == 0) {
                return@mapNotNull null
            }

            ArcadeGameStatistics(
                game = game,
                highScore = preferences[keys.highScore],
                startedRounds = startedRounds,
                finishedRounds = finishedRounds,
                lastPlayed = preferences[keys.lastPlayed]
            )
        }.sortedWith(
            compareByDescending<ArcadeGameStatistics> { it.startedRounds }
                .thenByDescending { it.lastPlayed }
        )
    }

    override fun highScore(game: String): Flow<Int> = dataStore.data.map {
        it[keys.getValue(game).highScore] ?: 0
    }

    override suspend fun recordStart(game: String, startedAt: Long) {
        val keys = keys.getValue(game)
        dataStore.edit {
            it[keys.startedRounds] = (it[keys.startedRounds] ?: 0) + 1
            it[keys.lastPlayed] = maxOf(it[keys.lastPlayed] ?: 0L, startedAt)
        }
    }

    override suspend fun recordFinish(game: String, score: Int, trackRound: Boolean) {
        val keys = keys.getValue(game)
        dataStore.edit {
            val highScore = it[keys.highScore]
            if (score > (highScore ?: 0) || highScore == null && trackRound) {
                it[keys.highScore] = score.coerceAtLeast(0)
            }
            if (trackRound) {
                it[keys.finishedRounds] = (it[keys.finishedRounds] ?: 0) + 1
            }
        }
    }

    override suspend fun resetStatistics() {
        dataStore.edit { preferences ->
            keys.values.forEach { keys ->
                preferences.remove(keys.highScore)
                preferences.remove(keys.startedRounds)
                preferences.remove(keys.finishedRounds)
                preferences.remove(keys.lastPlayed)
            }
        }
    }

    private class StatisticsKeys(game: String) {
        private val prefix = "ARCADE_${game.uppercase()}"
        val highScore = intPreferencesKey("${prefix}_HIGH_SCORE")
        val startedRounds = intPreferencesKey("${prefix}_STARTED_ROUNDS")
        val finishedRounds = intPreferencesKey("${prefix}_FINISHED_ROUNDS")
        val lastPlayed = longPreferencesKey("${prefix}_LAST_PLAYED")
    }
}
