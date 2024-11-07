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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState

@Composable
fun ResetDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onReset: () -> Unit,
    title: String = stringResource(R.string.reset_image),
    text: String = stringResource(R.string.reset_image_sub),
    icon: ImageVector = Icons.Rounded.ImageReset
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current

    EnhancedAlertDialog(
        visible = visible,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
        },
        title = { Text(title) },
        text = {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth()
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onReset()
                    onDismiss()
                    scope.launch {
                        toastHostState.showToast(
                            context.getString(R.string.values_reset),
                            Icons.Rounded.DoneOutline
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.reset))
            }
        }
    )
}