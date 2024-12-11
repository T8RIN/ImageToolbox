/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedDropdownMenu

@Composable
internal fun BoxScope.MarkupLayersContextActions(
    visible: Boolean,
    onDismiss: () -> Unit,
    onCopyLayer: () -> Unit,
    onToggleEditMode: () -> Unit,
    onRemoveLayer: () -> Unit,
    onActivateLayer: () -> Unit
) {
    EnhancedDropdownMenu(
        expanded = visible,
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = {
                        onToggleEditMode()
                        onDismiss()
                    },
                    icon = Icons.Rounded.MiniEdit,
                    text = stringResource(R.string.edit)
                )
                ClickableTile(
                    onClick = onCopyLayer,
                    icon = Icons.Rounded.ContentCopy,
                    text = stringResource(R.string.copy)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = onRemoveLayer,
                    icon = Icons.Rounded.Delete,
                    text = stringResource(R.string.delete)
                )
                ClickableTile(
                    onClick = onActivateLayer,
                    icon = Icons.Rounded.Deselect,
                    text = stringResource(R.string.clear_selection)
                )
            }
        }
    }
}