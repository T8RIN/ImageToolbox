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

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.settings.domain.model.LoadingGame

/**
 * The annotated object's name is the key stored in settings and statistics.
 * SnakeGame is stored as Snake, and Game2048 is stored as 2048.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
internal annotation class RegisterArcadeGame

internal interface ArcadeGameDefinition {
    @get:StringRes
    val title: Int

    @get:StringRes
    val description: Int

    val icon: ImageVector

    val order: Int get() = Int.MAX_VALUE

    @Composable
    fun Content(context: ArcadeGameContext, modifier: Modifier)
}

@Immutable
class ArcadeGame internal constructor(
    val name: String,
    internal val definition: ArcadeGameDefinition
) {
    @get:StringRes
    val title: Int get() = definition.title

    @get:StringRes
    val description: Int get() = definition.description

    val icon: ImageVector get() = definition.icon
}

object ArcadeGameRegistry {
    val games: List<ArcadeGame> = registeredArcadeGames.sortedWith(
        compareBy(
            { it.definition.order },
            { it.name }
        )
    )

    private val byName = games.associateBy(ArcadeGame::name)

    fun find(name: String): ArcadeGame? = byName[name]

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
