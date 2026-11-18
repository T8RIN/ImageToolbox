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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.StadiaController
import com.t8rin.imagetoolbox.core.settings.domain.model.LoadingGame
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.playable
import com.t8rin.imagetoolbox.core.ui.utils.helper.title
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
internal fun ShowLoadingGameButtonSettingItem(
    onClick: () -> Unit,
    onGameChange: (LoadingGame) -> Unit
) {
    val settings = LocalSettingsState.current
    val selection = settings.loadingGame
    PreferenceRowSwitch(
        shape = ShapeDefaults.center,
        modifier = Modifier.padding(horizontal = 8.dp),
        onClick = { onClick() },
        title = stringResource(R.string.show_loading_game_button),
        subtitle = stringResource(R.string.show_loading_game_button_sub),
        checked = settings.showLoadingGameButton,
        startIcon = Icons.Outlined.StadiaController,
        additionalContent = {
            AnimatedVisibility(visible = settings.showLoadingGameButton) {
                EnhancedButtonGroup(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    entries = listOf(
                        LoadingGame.Random,
                        LoadingGame.RandomFavorite
                    ) + LoadingGame.playable,
                    value = if (selection is LoadingGame.Specific && selection !in LoadingGame.playable) {
                        LoadingGame.Random
                    } else selection,
                    title = null,
                    itemContent = { Text(stringResource(it.title)) },
                    onValueChange = onGameChange,
                    activeButtonColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }
        }
    )
}
