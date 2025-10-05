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

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun QrEditField(
    value: QrType.Complex,
    onValueChange: (QrType.Complex) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = value,
        contentKey = { it::class.simpleName },
        modifier = modifier
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 12.dp
            )
    ) { qrType ->
        when (qrType) {
            is QrType.Wifi -> QrWifiEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Phone -> QrPhoneEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Sms -> QrSmsEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Email -> QrEmailEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Geo -> QrGeoEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Calendar -> QrCalendarEditField(
                value = qrType,
                onValueChange = onValueChange
            )

            is QrType.Contact -> QrContactEditField(
                value = qrType,
                onValueChange = onValueChange
            )
        }
    }
}