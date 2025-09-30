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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.QrType.Wifi.EncryptionType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalEssentials
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun QrTypeInfo(
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
    val essentials = rememberLocalEssentials()

    Box(modifier) {
        when (qrType) {
            is QrType.Wifi -> WifiQrTypeInfo(
                qrType = qrType,
                essentials = essentials,
                modifier = modifier
            )

            is QrType.Email -> Unit //TODO: add other types preview and creation templates
            is QrType.GeoPoint -> Unit
            is QrType.Phone -> Unit
            is QrType.Sms -> Unit
            is QrType.ContactInfo -> Unit
            is QrType.CalendarEvent -> Unit
        }
    }
}

@Composable
private fun WifiQrTypeInfo(
    qrType: QrType.Wifi,
    essentials: LocalEssentials,
    modifier: Modifier,
) {
    val data by remember(qrType) {
        derivedStateOf {
            buildList {
                val ssid = Triple(
                    first = Icons.Rounded.TextFields,
                    second = qrType.ssid.ifBlank {
                        essentials.context.getString(R.string.not_specified)
                    },
                    third = qrType.ssid.isNotBlank()
                )
                when (qrType.encryptionType) {
                    EncryptionType.OPEN -> {
                        add(
                            Triple(
                                first = Icons.Rounded.Public,
                                second = essentials.context.getString(R.string.open_network),
                                third = false
                            )
                        )
                        add(ssid)
                    }

                    else -> {
                        add(
                            Triple(
                                first = Icons.Rounded.Security,
                                second = qrType.encryptionType.toString(),
                                third = false
                            )
                        )
                        add(ssid)
                        add(
                            Triple(
                                first = Icons.Rounded.Password,
                                second = qrType.password.ifBlank {
                                    essentials.context.getString(R.string.not_specified)
                                },
                                third = qrType.password.isNotBlank()
                            )
                        )
                    }
                }
            }
        }
    }

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
            text = stringResource(R.string.qr_type_wifi),
            icon = Icons.Rounded.Wifi,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEachIndexed { index, (icon, text, canCopy) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = data.size
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