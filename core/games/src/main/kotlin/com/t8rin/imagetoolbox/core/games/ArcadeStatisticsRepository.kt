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

package com.t8rin.imagetoolbox.core.games
import kotlinx.coroutines.flow.Flow

interface ArcadeStatisticsRepository {
    val statistics: Flow<List<ArcadeGameStatistics>>

    fun highScore(game: String): Flow<Int>

    suspend fun recordStart(game: String, startedAt: Long)

    suspend fun recordFinish(game: String, score: Int, trackRound: Boolean)

    suspend fun resetStatistics()
}

data class ArcadeGameStatistics(
    val game: ArcadeGame,
    val highScore: Int?,
    val startedRounds: Int,
    val finishedRounds: Int,
    val lastPlayed: Long?
)
