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

package com.t8rin.imagetoolbox.feature.arcade_games.presentation.screenLogic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.serialization.builtins.serializer

class ArcadeGamesComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted val onGoBack: () -> Unit,
    @Assisted private val onNavigate: (Screen) -> Unit,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _selectedGame = mutableStateOf<ArcadeGame?>(null)
    val selectedGame by _selectedGame

    var isPlaying by mutableStateOf(false)
        private set

    private var activeSearchQuery by mutableStateOf(
        stateKeeper.consume("searchQuery", String.serializer())
    )
    val showSearch get() = activeSearchQuery != null
    val searchQuery get() = activeSearchQuery.orEmpty()

    init {
        stateKeeper.register("searchQuery", String.serializer()) { activeSearchQuery }
    }

    fun selectGame(game: ArcadeGame?) {
        if (game == selectedGame) return
        isPlaying = false
        _selectedGame.update { game }
    }

    fun onPlayingChanged(game: ArcadeGame, playing: Boolean) {
        if (game == selectedGame) isPlaying = playing
    }

    fun openSearch() {
        if (!showSearch) activeSearchQuery = ""
    }

    fun updateSearch(query: String): Boolean {
        if (!showSearch || searchQuery == query) return false
        activeSearchQuery = query
        return true
    }

    fun closeSearch() {
        activeSearchQuery = null
    }

    fun openStatistics() = onNavigate(Screen.ArcadeStatistics)

    fun toggleFavorite(game: ArcadeGame) {
        componentScope.launch { settingsManager.toggleFavoriteArcadeGame(game) }
    }

    fun reorderGames(games: List<ArcadeGame>) {
        componentScope.launch { settingsManager.reorderArcadeGames(games) }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ArcadeGamesComponent
    }
}