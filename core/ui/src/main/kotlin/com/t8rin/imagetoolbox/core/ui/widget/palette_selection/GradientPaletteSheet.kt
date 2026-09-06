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

package com.t8rin.imagetoolbox.core.ui.widget.palette_selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.Gradient
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.saver.GradientPaletteSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
internal fun GradientPaletteSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    value: GradientPalette,
    onValueChange: (GradientPalette) -> Unit
) {
    var draft by rememberSaveable(visible, stateSaver = GradientPaletteSaver) {
        mutableStateOf(value)
    }
    val settings = LocalSettingsState.current
    val interactor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.gradient),
                icon = Icons.Rounded.Gradient
            )
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .enhancedVerticalScroll(rememberScrollState(), reverseScrolling = true)
                    .padding(24.dp),
            ) {
                GradientPaletteSelector(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.padding(bottom = 8.dp),
                    title = stringResource(R.string.basic),
                    shape = ShapeDefaults.extraLarge,
                    allowCustom = false
                )
                RecentAndFavoriteGradientsCard(
                    value = draft,
                    onValueChange = { draft = it }
                )
                GradientPaletteEditor(
                    value = draft,
                    onValueChange = { draft = it }
                )
            }
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isFavorite = draft in settings.favoriteGradients
                val containerColor by animateColorAsState(
                    if (isFavorite) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.surfaceContainer
                )
                val contentColor by animateColorAsState(
                    if (isFavorite) MaterialTheme.colorScheme.onTertiaryContainer
                    else MaterialTheme.colorScheme.onBackground
                )
                EnhancedIconButton(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    onClick = {
                        val selected = draft
                        scope.launch { interactor.toggleFavoriteGradient(selected) }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.BookmarkRemove
                        else Icons.Rounded.Bookmark,
                        contentDescription = null
                    )
                }
                EnhancedButton(
                    onClick = {
                        val selected = draft
                        scope.launch { interactor.addRecentGradient(selected) }
                        onValueChange(selected)
                        onDismiss()
                    }
                ) {
                    AutoSizeText(stringResource(R.string.ok))
                }
            }
        }
    )
}