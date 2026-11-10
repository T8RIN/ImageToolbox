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
import com.arkivanov.decompose.ComponentContext
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.games.ArcadeGame
import com.t8rin.imagetoolbox.core.games.ArcadeGameRegistry
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ArcadeGamesComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted config: Screen.ArcadeGames,
    @Assisted val onGoBack: () -> Unit,
    @Assisted private val onNavigate: (Screen) -> Unit,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val initialGame = config.game?.let(ArcadeGameRegistry::find)
    private val _selectedGame = mutableStateOf(initialGame)
    val selectedGame by _selectedGame

    private val _isPlaying = mutableStateOf(false)
    val isPlaying by _isPlaying

    private val _showSearch = mutableStateOf(false)
    val showSearch by _showSearch

    private val _searchQuery = mutableStateOf("")
    val searchQuery by _searchQuery

    fun selectGame(game: ArcadeGame?) {
        if (game == selectedGame) return
        _isPlaying.update { false }
        _selectedGame.update { game }
    }

    fun closeGame() {
        if (initialGame == null) {
            selectGame(null)
        } else {
            onGoBack()
        }
    }

    fun onPlayingChanged(game: ArcadeGame, playing: Boolean) {
        if (game == selectedGame) _isPlaying.update { playing }
    }

    fun openSearch() {
        _showSearch.update { true }
    }

    fun updateSearch(query: String): Boolean {
        if (!showSearch || searchQuery == query) return false
        _searchQuery.update { query }
        return true
    }

    fun closeSearch() {
        _showSearch.update { false }
        _searchQuery.update { "" }
    }

    fun openStatistics() = onNavigate(Screen.ArcadeStatistics)

    fun toggleFavorite(game: ArcadeGame) {
        componentScope.launch { settingsManager.toggleFavoriteArcadeGame(game.name) }
    }

    fun reorderGames(games: List<ArcadeGame>) {
        componentScope.launch {
            settingsManager.reorderArcadeGames(
                games = games.map(ArcadeGame::name),
                defaultOrder = ArcadeGameRegistry.games.map(ArcadeGame::name)
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            config: Screen.ArcadeGames,
            onGoBack: () -> Unit,
            onNavigate: (Screen) -> Unit,
        ): ArcadeGamesComponent
    }
}