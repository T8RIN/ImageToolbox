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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.FontSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import kotlinx.coroutines.launch

@Composable
internal fun FontItem(
    font: UiFontFamily,
    selected: Boolean,
    onFontSelected: (UiFontFamily) -> Unit,
    onRemoveFont: ((UiFontFamily.Custom) -> Unit)?,
    modifier: Modifier = Modifier,
    coverage: Map<Script, Float> = emptyMap(),
    favorite: Boolean = false,
    source: String? = null,
    onToggleFavorite: (() -> Unit)? = null,
    previewText: String? = stringResource(R.string.alphabet_and_numbers),
    arranging: Boolean = false,
    reorderBadge: (@Composable () -> Unit)? = null
) {
    val content: @Composable (Modifier, Shape, (() -> Unit)?) -> Unit =
        { itemModifier, shape, onLongClick ->
            FontSelectionItem(
                font = font,
                selected = selected,
                onClick = if (arranging) null else {
                    { onFontSelected(font) }
                },
                onLongClick = onLongClick,
                modifier = itemModifier,
                shape = shape,
                coverage = coverage,
                imported = onRemoveFont != null,
                favorite = favorite,
                source = source,
                onToggleFavorite = onToggleFavorite,
                previewText = previewText,
                arranging = arranging,
                reorderBadge = reorderBadge
            )
        }
    if (font is UiFontFamily.Custom && onRemoveFont != null && !arranging) {
        val scope = rememberCoroutineScope()
        val state = rememberRevealState()
        val interactionSource = remember { MutableInteractionSource() }
        val isDragged by interactionSource.collectIsDraggedAsState()
        val shape = animateShape(if (isDragged) ShapeDefaults.extraSmall else ShapeDefaults.default)
        SwipeToReveal(
            state = state,
            modifier = modifier,
            revealedContentEnd = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .container(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = shape,
                            autoShadowElevation = 0.dp,
                            resultPadding = 0.dp
                        )
                        .hapticsClickable {
                            scope.launch { state.animateTo(RevealValue.Default) }
                            onRemoveFont(font)
                        }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(end = 8.dp)
                            .align(Alignment.CenterEnd),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            },
            directions = setOf(RevealDirection.EndToStart),
            interactionSource = interactionSource,
            swipeableContent = {
                content(Modifier.fillMaxWidth(), shape) {
                    scope.launch { state.animateTo(RevealValue.FullyRevealedStart) }
                }
            }
        )
    } else {
        content(modifier, ShapeDefaults.default, null)
    }
}
