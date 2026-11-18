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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.DragHandle
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.provider.SafeLocalContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
fun FontSelectionItem(
    font: UiFontFamily,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = ShapeDefaults.default,
    selected: Boolean = font == LocalSettingsState.current.font,
    coverage: Map<Script, Float> = emptyMap(),
    imported: Boolean = false,
    favorite: Boolean = false,
    source: String? = null,
    onToggleFavorite: (() -> Unit)? = null,
    previewText: String? = stringResource(R.string.alphabet_and_numbers),
    arranging: Boolean = false,
    reorderBadge: (@Composable () -> Unit)? = null
) {
    val settingsState = LocalSettingsState.current
    val (_, name, isVariable) = font
    ProvideTypography(font) {
        PreferenceItemOverload(
            onClick = onClick,
            onLongClick = onLongClick,
            title = name ?: stringResource(R.string.system),
            animateTitle = false,
            subtitle = previewText.takeUnless { arranging },
            containerColor = takeColorFromScheme {
                if (selected && !arranging) secondaryContainer else SafeLocalContainerColor
            },
            modifier = modifier.border(
                width = settingsState.borderWidth,
                color = animateColorAsState(
                    if (selected && !arranging) {
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
                    } else Color.Transparent
                ).value,
                shape = shape
            ),
            shape = shape,
            drawStartIconContainer = false,
            endIcon = when {
                !arranging && font == UiFontFamily.System -> {
                    {
                        Icon(
                            imageVector = if (selected) Icons.Rounded.RadioButtonChecked
                            else Icons.Rounded.RadioButtonUnchecked,
                            contentDescription = null
                        )
                    }
                }

                arranging && font != UiFontFamily.System -> {
                    {
                        Icon(
                            imageVector = Icons.Rounded.DragHandle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                !arranging && font != UiFontFamily.System && onToggleFavorite != null -> {
                    {
                        EnhancedIconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(40.dp),
                            contentColor = if (favorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Icon(
                                imageVector = if (favorite) Icons.Rounded.BookmarkRemove else Icons.Outlined.Bookmark,
                                contentDescription = null
                            )
                        }
                    }
                }

                else -> null
            },
            placeBottomContentInside = true,
            bottomContent = when {
                arranging -> reorderBadge
                (imported && source == null) || font == UiFontFamily.System -> null
                else -> ({
                    FontCoverageBadges(
                        coverage = coverage,
                        variable = (isVariable == true).takeUnless { imported },
                        selected = selected,
                        source = source
                    )
                })
            }
        )
    }
}
