/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun QrEmailEditField(
    value: QrType.Email,
    onValueChange: (QrType.Email) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedTextField(
            value = value.address,
            onValueChange = { onValueChange(value.copy(address = it)) },
            label = { Text(stringResource(R.string.address)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.AlternateEmail,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        RoundedTextField(
            value = value.subject,
            onValueChange = { onValueChange(value.copy(subject = it)) },
            label = { Text(stringResource(R.string.subject)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Topic,
                    contentDescription = null
                )
            },
            singleLine = false
        )
        RoundedTextField(
            value = value.body,
            onValueChange = { onValueChange(value.copy(body = it)) },
            label = { Text(stringResource(R.string.body)) },
            startIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ShortText,
                    contentDescription = null
                )
            },
            singleLine = false
        )
    }
}