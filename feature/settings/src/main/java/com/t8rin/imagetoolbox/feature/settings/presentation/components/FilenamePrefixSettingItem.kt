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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Prefix
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun FilenamePrefixSettingItem(
    onValueChange: (String) -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    var showChangeFilenameDialog by rememberSaveable { mutableStateOf(false) }

    PreferenceItem(
        shape = shape,
        onClick = {
            showChangeFilenameDialog = true
        },
        enabled = settingsState.filenameBehavior is FilenameBehavior.None,
        title = stringResource(R.string.prefix),
        subtitle = (settingsState.filenamePrefix.takeIf { it.isNotEmpty() }
            ?: stringResource(R.string.empty)),
        endIcon = Icons.Rounded.MiniEdit,
        startIcon = Icons.Filled.Prefix,
        modifier = modifier
    )

    var value by remember(showChangeFilenameDialog) {
        mutableStateOf(
            settingsState.filenamePrefix
        )
    }
    EnhancedAlertDialog(
        visible = showChangeFilenameDialog,
        onDismissRequest = { showChangeFilenameDialog = false },
        icon = {
            Icon(
                imageVector = Icons.Filled.Prefix,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.prefix))
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    placeholder = {
                        Text(
                            text = stringResource(R.string.default_prefix),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    shape = ShapeDefaults.default,
                    value = value,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    onValueChange = {
                        value = it
                    }
                )
            }
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = if (settingsState.isNightMode) 0.5f
                    else 1f
                ),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = {
                    onValueChange(value.trim())
                    showChangeFilenameDialog = false
                },
                borderColor = MaterialTheme.colorScheme.outlineVariant(
                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                ),
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}