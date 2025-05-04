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

package ru.tech.imageresizershrinker.core.ui.widget.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedCircularProgressIndicator

@Composable
fun WantCancelLoadingDialog(
    visible: Boolean,
    onCancelLoading: () -> Unit,
    onDismissDialog: () -> Unit,
    isForSaving: Boolean = true
) {
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissDialog,
        confirmButton = {
            EnhancedButton(
                onClick = onDismissDialog
            ) {
                Text(stringResource(id = R.string.wait))
            }
        },
        title = {
            Text(stringResource(id = R.string.loading))
        },
        text = {
            Text(
                text = stringResource(
                    if (isForSaving) R.string.saving_almost_complete
                    else R.string.operation_almost_complete
                )
            )
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onCancelLoading
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        icon = {
            EnhancedCircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                strokeWidth = 3.dp
            )
        }
    )
}