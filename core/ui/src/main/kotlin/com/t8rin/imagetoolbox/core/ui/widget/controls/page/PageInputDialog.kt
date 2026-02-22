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

package com.t8rin.imagetoolbox.core.ui.widget.controls.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pages
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton

@Composable
fun PageInputDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    value: List<Int>?,
    onValueChange: (List<Int>) -> Unit
) {
    var pages by rememberSaveable(visible) {
        mutableStateOf(value ?: emptyList())
    }
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.pages_selection))
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Pages,
                contentDescription = null
            )
        },
        text = {
            PageInputField(
                selectedPages = pages,
                onPagesChanged = { pages = it }
            )
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onValueChange(pages)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    )
}