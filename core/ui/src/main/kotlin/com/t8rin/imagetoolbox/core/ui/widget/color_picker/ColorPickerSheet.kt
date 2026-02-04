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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.toColorModel
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch

@Composable
fun ColorPickerSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    color: Color?,
    onColorSelected: (Color) -> Unit,
    allowAlpha: Boolean
) {
    val scope = rememberCoroutineScope()
    var tempColor by remember(visible) {
        mutableStateOf(color ?: Color.Black)
    }
    val settingsState = LocalSettingsState.current

    val simpleSettingsInteractor = LocalSimpleSettingsInteractor.current
    EnhancedModalBottomSheet(
        sheetContent = {
            Box {
                Column(
                    modifier = Modifier
                        .enhancedVerticalScroll(rememberScrollState(), reverseScrolling = true)
                        .padding(24.dp)
                ) {
                    RecentAndFavoriteColorsCard(
                        onRecentColorClick = { tempColor = it },
                        onFavoriteColorClick = { tempColor = it }
                    )

                    ColorSelection(
                        value = tempColor,
                        onValueChange = { tempColor = it },
                        withAlpha = allowAlpha
                    )
                }
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.ColorLens
            )
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val favoriteColors = settingsState.favoriteColors

                val inFavorite by remember(tempColor, favoriteColors) {
                    derivedStateOf {
                        tempColor in favoriteColors
                    }
                }

                val containerColor by animateColorAsState(
                    if (inFavorite) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.surfaceContainer
                )
                val contentColor by animateColorAsState(
                    if (inFavorite) MaterialTheme.colorScheme.onTertiaryContainer
                    else MaterialTheme.colorScheme.onBackground
                )

                EnhancedIconButton(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    onClick = {
                        scope.launch {
                            simpleSettingsInteractor.toggleFavoriteColor(
                                tempColor.toArgb().toColorModel()
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (inFavorite) {
                            Icons.Rounded.BookmarkRemove
                        } else {
                            Icons.Rounded.Bookmark
                        },
                        contentDescription = null
                    )
                }
                EnhancedButton(
                    onClick = {
                        scope.launch {
                            simpleSettingsInteractor.toggleRecentColor(
                                tempColor.toArgb().toColorModel()
                            )
                        }
                        onColorSelected(tempColor)
                        onDismiss()
                    }
                ) {
                    AutoSizeText(stringResource(R.string.ok))
                }
            }
        }
    )
}