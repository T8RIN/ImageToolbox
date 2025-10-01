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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.QrType.Wifi.EncryptionType
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Latitude
import com.t8rin.imagetoolbox.core.resources.icons.Longitude
import com.t8rin.imagetoolbox.core.utils.getString

internal data class InfoEntry(
    val icon: ImageVector,
    val text: String,
    val canCopy: Boolean,
)

internal data class QrInfo(
    val title: String,
    val icon: ImageVector,
    val intent: Intent?,
    val data: List<InfoEntry>,
) {
    companion object
}

@Composable
internal fun rememberQrInfo(qrType: QrType.Complex): QrInfo? {
    return when (qrType) {
        is QrType.Wifi -> wifiQrInfo(qrType)
        is QrType.Email -> emailQrInfo(qrType)
        is QrType.GeoPoint -> geoPointQrInfo(qrType)

        is QrType.Phone -> null //TODO: add other types preview and creation templates
        is QrType.Sms -> null
        is QrType.ContactInfo -> null
        is QrType.CalendarEvent -> null
    }
}

@Composable
private fun geoPointQrInfo(
    qrType: QrType.GeoPoint
): QrInfo = qrInfoBuilder(qrType) {
    entry(
        InfoEntry(
            icon = Icons.Outlined.Latitude,
            text = qrType.latitude.toString().trimTrailingZero(),
            canCopy = true
        )
    )

    entry(
        InfoEntry(
            icon = Icons.Outlined.Longitude,
            text = qrType.longitude.toString().trimTrailingZero(),
            canCopy = true
        )
    )
}

@Composable
private fun emailQrInfo(
    qrType: QrType.Email
): QrInfo = qrInfoBuilder(qrType) {
    entry(
        InfoEntry(
            icon = Icons.Rounded.AlternateEmail,
            text = qrType.address.ifBlank { getString(R.string.not_specified) },
            canCopy = qrType.address.isNotBlank()
        )
    )

    entry(
        InfoEntry(
            icon = Icons.Rounded.Topic,
            text = qrType.subject.ifBlank { getString(R.string.not_specified) },
            canCopy = qrType.subject.isNotBlank()
        )
    )

    entry(
        InfoEntry(
            icon = Icons.AutoMirrored.Rounded.ShortText,
            text = qrType.body.ifBlank { getString(R.string.not_specified) },
            canCopy = qrType.body.isNotBlank()
        )
    )
}

@Composable
private fun wifiQrInfo(
    qrType: QrType.Wifi
): QrInfo = qrInfoBuilder(qrType) {
    val ssid = InfoEntry(
        icon = Icons.Rounded.TextFields,
        text = qrType.ssid.ifBlank { getString(R.string.not_specified) },
        canCopy = qrType.ssid.isNotBlank()
    )
    when (qrType.encryptionType) {
        EncryptionType.OPEN -> {
            entry(
                InfoEntry(
                    icon = Icons.Rounded.Public,
                    text = getString(R.string.open_network),
                    canCopy = false
                )
            )
            entry(ssid)
        }

        else -> {
            entry(
                InfoEntry(
                    icon = Icons.Rounded.Security,
                    text = qrType.encryptionType.toString(),
                    canCopy = false
                )
            )
            entry(ssid)
            entry(
                InfoEntry(
                    icon = Icons.Rounded.Password,
                    text = qrType.password.ifBlank { getString(R.string.not_specified) },
                    canCopy = qrType.password.isNotBlank()
                )
            )
        }
    }
}