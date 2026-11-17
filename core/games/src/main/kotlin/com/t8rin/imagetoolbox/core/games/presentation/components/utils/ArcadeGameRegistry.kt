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

package com.t8rin.imagetoolbox.core.games.presentation.components.utils

import com.t8rin.imagetoolbox.core.games.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadeGameDefinition
import com.t8rin.imagetoolbox.core.settings.domain.model.LoadingGame

object ArcadeGameRegistry {
    val games: List<ArcadeGame> = registeredArcadeGames.entries.sortedWith(
        compareBy(
            { it.value.order },
            { it.key }
        )
    ).map { (name, definition) ->
        ArcadeGame(
            name = name,
            title = definition.title,
            description = definition.description,
            icon = definition.icon
        )
    }

    private val byName = games.associateBy(ArcadeGame::name)

    fun find(name: String): ArcadeGame? = byName[name]

    internal fun definition(game: ArcadeGame): ArcadeGameDefinition =
        registeredArcadeGames.getValue(game.name)

    fun orderedGames(savedOrder: List<String>): List<ArcadeGame> =
        (savedOrder.mapNotNull(::find) + games).distinct()

    fun pickGame(
        selection: LoadingGame,
        favorites: Set<String>,
        previous: String? = null
    ): ArcadeGame? {
        val choices = when (selection) {
            LoadingGame.Random -> games
            LoadingGame.RandomFavorite -> games.filter { it.name in favorites }.ifEmpty { games }
            is LoadingGame.Specific -> listOfNotNull(find(selection.game)).ifEmpty { games }
        }
        return choices.filterNot { it.name == previous }.ifEmpty { choices }.randomOrNull()
    }
}
