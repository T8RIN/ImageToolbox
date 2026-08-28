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

package com.t8rin.imagetoolbox.feature.archive_tools.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Lock
import com.t8rin.imagetoolbox.core.resources.icons.Password
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.rememberFilename
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.archive_tools.presentation.screenLogic.ArchivePassphraseStatus

@Composable
internal fun ArchivePasswordDialog(
    uri: Uri?,
    currentPassword: String,
    status: ArchivePassphraseStatus?,
    onPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var hidePassword by remember(uri) { mutableStateOf(true) }
    val isChecking = status == ArchivePassphraseStatus.Checking
    val isInvalid = status == ArchivePassphraseStatus.Invalid
    val filename = uri?.let { rememberFilename(it) }

    EnhancedAlertDialog(
        visible = uri != null,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null
            )
        },
        title = {
            Text(filename ?: stringResource(R.string.encrypted_file_detected))
        },
        text = {
            RoundedTextField(
                value = currentPassword,
                onValueChange = onPasswordChange,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                label = null,
                hint = {
                    Text(
                        text = stringResource(R.string.password),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                startIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = null
                    )
                },
                endIcon = {
                    EnhancedIconButton(onClick = { hidePassword = !hidePassword }) {
                        Icon(
                            imageVector = if (hidePassword) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (hidePassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                isError = isInvalid,
                loading = isChecking,
                supportingText = if (isInvalid) {
                    {
                        Text(stringResource(R.string.invalid_password_or_not_encrypted))
                    }
                } else null,
                enabled = !isChecking,
                singleLine = true,
                modifier = Modifier.container(
                    shape = MaterialTheme.shapes.large,
                    resultPadding = 8.dp
                )
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = currentPassword.isNotEmpty() && !isChecking,
                onClick = { onConfirm(currentPassword) }
            ) {
                Text(stringResource(R.string.unlock))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}
