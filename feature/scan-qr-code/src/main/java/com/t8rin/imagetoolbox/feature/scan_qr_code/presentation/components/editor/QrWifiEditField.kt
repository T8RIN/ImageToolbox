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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.QrType.Wifi.EncryptionType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun QrWifiEditField(
    value: QrType.Wifi,
    onValueChange: (QrType.Wifi) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.wifi_configuration),
            icon = Icons.Outlined.Build,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RoundedTextField(
            value = value.ssid,
            onValueChange = { onValueChange(value.copy(ssid = it)) },
            label = { Text(stringResource(R.string.ssid)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.TextFields,
                    contentDescription = null
                )
            },
            singleLine = false
        )
        AnimatedVisibility(
            visible = value.encryptionType != EncryptionType.OPEN,
            modifier = Modifier.fillMaxWidth()
        ) {
            RoundedTextField(
                value = value.password,
                onValueChange = { onValueChange(value.copy(password = it)) },
                label = { Text(stringResource(R.string.password)) },
                startIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
        }

        DataSelector(
            value = value.encryptionType,
            onValueChange = { onValueChange(value.copy(encryptionType = it)) },
            entries = EncryptionType.entries,
            title = stringResource(R.string.security),
            titleIcon = Icons.Rounded.Security,
            itemContentText = { it.name },
            itemContentIcon = { _, selected -> if (selected) Icons.Rounded.CheckCircleOutline else null },
            spanCount = 1,
            behaveAsContainer = false,
            titlePadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            contentPadding = PaddingValues(),
            canExpand = false,
            selectedItemColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}