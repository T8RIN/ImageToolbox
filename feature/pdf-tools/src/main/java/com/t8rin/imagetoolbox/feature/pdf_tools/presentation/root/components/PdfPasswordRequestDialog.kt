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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun PdfPasswordRequestDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onFillPassword: (String) -> Unit
) {
    var password by remember(isVisible) {
        mutableStateOf("")
    }
    var hidePassword by remember(isVisible) {
        mutableStateOf(true)
    }

    EnhancedAlertDialog(
        visible = isVisible,
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.password))
        },
        text = {
            RoundedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                ),
                label = null,
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    ),
                singleLine = true,
                visualTransformation = if (hidePassword) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                hint = {
                    Text(
                        text = stringResource(R.string.pdf_is_protected),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                startIcon = {
                    Icon(
                        imageVector = Icons.Filled.Password,
                        contentDescription = null
                    )
                },
                endIcon = {
                    EnhancedIconButton(
                        onClick = { hidePassword = !hidePassword }
                    ) {
                        Icon(
                            imageVector = if (hidePassword) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = null
                        )
                    }
                }
            )
        },
        confirmButton = {
            EnhancedButton(
                enabled = password.isNotEmpty(),
                onClick = { onFillPassword(password) }
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