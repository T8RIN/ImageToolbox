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

package com.t8rin.imagetoolbox.core.games.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.t8rin.imagetoolbox.core.games.domain.model.ArcadeGame
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadeGameContext
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadeGameState
import com.t8rin.imagetoolbox.core.games.presentation.components.utils.ArcadeDefaults
import com.t8rin.imagetoolbox.core.games.presentation.components.utils.ArcadeGameRegistry

@Composable
fun ArcadeGameContent(
    game: ArcadeGame,
    modifier: Modifier = Modifier,
    height: Dp = Dp.Unspecified,
    isPaused: Boolean = false,
    allowNewRound: Boolean = true,
    onPlayingChanged: ((Boolean) -> Unit)? = null,
    initialState: ArcadeGameState = ArcadeGameState()
) {
    val style = ArcadeDefaults.style.let {
        if (height == Dp.Unspecified) it else it.copy(gameHeight = height)
    }
    val context = ArcadeGameContext(
        name = game.name,
        initialState = initialState,
        style = style,
        isPaused = isPaused,
        allowNewRound = allowNewRound,
        onPlayingChanged = onPlayingChanged
    )
    key(game.name) {
        ArcadeGameRegistry.definition(game).Content(context = context, modifier = modifier)
    }
}