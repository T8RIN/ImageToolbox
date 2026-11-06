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

package com.t8rin.imagetoolbox.collage_maker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.collage_maker.domain.SavedCollageStyle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun CollageStyleApplyDialog(
    preset: SavedCollageStyle?,
    imageCount: Int,
    visible: Boolean,
    onDismiss: () -> Unit,
    onApply: (Boolean) -> Unit
) {
    val compatible =
        preset?.style?.let { it.layoutId != null && it.imageCount == imageCount } == true
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        placeAboveAll = true,
        icon = { Icon(Icons.Rounded.Palette, contentDescription = null) },
        title = {
            Text(
                text = preset?.name.orEmpty(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            ProvideTextStyle(LocalTextStyle.current.copy(textAlign = TextAlign.Start)) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fadingEdges(scrollState, isVertical = true)
                        .enhancedVerticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PreferenceItem(
                        title = stringResource(R.string.collage_style_apply_full),
                        subtitle = stringResource(
                            if (compatible) R.string.collage_style_apply_full_sub
                            else R.string.collage_style_apply_layout_unavailable,
                            preset?.style?.imageCount ?: 0,
                            imageCount
                        ),
                        startIcon = Icons.Outlined.Collage,
                        enabled = compatible,
                        shape = ShapeDefaults.top,
                        onClick = {
                            onApply(true)
                            onDismiss()
                        }
                    )
                    PreferenceItem(
                        title = stringResource(R.string.collage_style_apply_appearance),
                        subtitle = stringResource(R.string.collage_style_apply_appearance_sub),
                        startIcon = Icons.Outlined.Palette,
                        shape = ShapeDefaults.bottom,
                        onClick = {
                            onApply(false)
                            onDismiss()
                        }
                    )
                }
            }
        }
    )
}