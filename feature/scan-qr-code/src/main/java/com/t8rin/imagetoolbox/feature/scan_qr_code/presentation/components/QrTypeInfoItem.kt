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

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.QrType.Wifi.EncryptionType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.getString

@Composable
internal fun QrTypeInfoItem(
    qrType: QrType,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = qrType,
        modifier = Modifier.fillMaxWidth()
    ) { type ->
        when (type) {
            is QrType.Complex -> ComplexQrTypeInfo(type, modifier)
            else -> Spacer(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ComplexQrTypeInfo(
    qrType: QrType.Complex,
    modifier: Modifier = Modifier
) {
    val qrInfo = when (qrType) {
        is QrType.Wifi -> wifiQrData(qrType)
        is QrType.Email -> emailQrData(qrType)

        is QrType.GeoPoint -> return //TODO: add other types preview and creation templates
        is QrType.Phone -> return
        is QrType.Sms -> return
        is QrType.ContactInfo -> return
        is QrType.CalendarEvent -> return
    }

    QrInfoItem(
        qrInfo = qrInfo,
        modifier = modifier
    )
}

@Composable
private fun emailQrData(
    qrType: QrType.Email
): QrInfo = qrInfoBuilder(qrType) {
    title(getString(R.string.qr_type_email))
    icon(Icons.Rounded.Email)

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
private fun wifiQrData(
    qrType: QrType.Wifi
): QrInfo = qrInfoBuilder(qrType) {
    title(getString(R.string.qr_type_wifi))
    icon(Icons.Rounded.Wifi)

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

@Composable
private fun QrInfoItem(
    qrInfo: QrInfo,
    modifier: Modifier,
) {
    if (qrInfo.data.isEmpty()) return

    val essentials = rememberLocalEssentials()

    Column(
        modifier = Modifier
            .then(modifier)
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 0.dp
            )
            .padding(8.dp)
    ) {
        TitleItem(
            text = qrInfo.title,
            icon = qrInfo.icon,
            modifier = Modifier.padding(8.dp),
            endContent = {
                qrInfo.intent?.let {
                    EnhancedIconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = { essentials.context.startActivity(it) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = "open",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            qrInfo.data.forEachIndexed { index, (icon, text, canCopy) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = qrInfo.data.size
                            ),
                            color = MaterialTheme.colorScheme.surface,
                            resultPadding = 0.dp
                        )
                        .clickable(
                            enabled = canCopy,
                            onClick = { essentials.copyToClipboard(text) }
                        )
                        .padding(
                            vertical = 10.dp,
                            horizontal = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = text,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (canCopy) {
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}