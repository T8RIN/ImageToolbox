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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton

@Composable
internal fun DeleteStylePresetDialog(
    name: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    EnhancedAlertDialog(
        visible = visible,
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = {
            Text(
                text = stringResource(R.string.delete_style_preset),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_style_preset_sub, name),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onConfirm(name)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}