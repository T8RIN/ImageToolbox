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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.domain.model.QrType.Wifi.EncryptionType
import com.t8rin.imagetoolbox.core.domain.model.copy
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Latitude
import com.t8rin.imagetoolbox.core.resources.icons.Longitude
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Contact
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberContactPicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.value.filterDecimal

@Composable
internal fun QrTypeEditSheet(
    qrType: QrType.Complex?,
    onSave: (QrType.Complex) -> Unit,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    var edited by retain(visible, qrType) {
        mutableStateOf(qrType ?: QrType.Wifi())
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { onDismiss() },
        title = {
            TitleItem(
                text = stringResource(
                    if (qrType == null) R.string.create_barcode
                    else R.string.edit_barcode
                ),
                icon = Icons.Rounded.QrCode
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onSave(
                        edited.copy(raw = edited.asRaw())
                    )
                    onDismiss()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                enabled = !edited.isEmpty()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .clearFocusOnTap(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DataSelector(
                value = edited,
                onValueChange = { edited = it },
                itemEqualityDelegate = { t, o -> t::class.isInstance(o) },
                entries = QrType.complexEntries,
                title = null,
                titleIcon = null,
                itemContentText = { stringResource(it.name) },
                itemContentIcon = { item, _ -> item.icon },
                canExpand = false
            )
            QrEditField(
                value = edited,
                onValueChange = { edited = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun QrEditField(
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

            is QrType.Calendar -> Text("TODO")

            //TODO: Add all left types
            is QrType.Contact -> Text("TODO")
        }
    }
}

@Composable
private fun QrGeoEditField(
    value: QrType.Geo,
    onValueChange: (QrType.Geo) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var latitude by remember(value.latitude) {
            mutableStateOf(value.latitude?.toString().orEmpty().trimTrailingZero())
        }
        var longitude by remember(value.longitude) {
            mutableStateOf(value.longitude?.toString().orEmpty().trimTrailingZero())
        }

        RoundedTextField(
            value = latitude,
            onValueChange = {
                latitude = it.filterDecimal()

                latitude.toDoubleOrNull()?.coerceIn(LatitudeRange)?.let { new ->
                    onValueChange(value.copy(latitude = new))
                    latitude = new.toString().trimTrailingZero()
                }
            },
            label = { Text(stringResource(R.string.latitude)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Latitude,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
        RoundedTextField(
            value = longitude,
            onValueChange = {
                longitude = it.filterDecimal()

                longitude.toDoubleOrNull()?.coerceIn(LongitudeRange)?.let { new ->
                    onValueChange(value.copy(longitude = new))
                    longitude = new.toString().trimTrailingZero()
                }
            },
            label = { Text(stringResource(R.string.longitude)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Longitude,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }
}

@Composable
private fun QrEmailEditField(
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

@Composable
private fun QrSmsEditField(
    value: QrType.Sms,
    onValueChange: (QrType.Sms) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedTextField(
            value = value.phoneNumber,
            onValueChange = { onValueChange(value.copy(phoneNumber = it)) },
            label = { Text(stringResource(R.string.phone)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.Numbers,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )
        RoundedTextField(
            value = value.message,
            onValueChange = { onValueChange(value.copy(message = it)) },
            label = { Text(stringResource(R.string.message)) },
            startIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.StickyNote2,
                    contentDescription = null
                )
            },
            singleLine = false
        )

        ContactPickerButton(
            onPicked = { onValueChange(value.copy(phoneNumber = it.phones.firstOrNull()?.number.orEmpty())) }
        )
    }
}

@Composable
private fun QrPhoneEditField(
    value: QrType.Phone,
    onValueChange: (QrType.Phone) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedTextField(
            value = value.number,
            onValueChange = { onValueChange(value.copy(number = it)) },
            label = { Text(stringResource(R.string.phone)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Rounded.Numbers,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )

        ContactPickerButton(
            onPicked = { onValueChange(value.copy(number = it.phones.firstOrNull()?.number.orEmpty())) }
        )
    }
}

@Composable
private fun QrWifiEditField(
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

@Composable
private fun ContactPickerButton(onPicked: (Contact) -> Unit) {
    val contactPicker = rememberContactPicker(onSuccess = onPicked)

    EnhancedButton(
        onClick = contactPicker::pickContact,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.pick_contact)
            )
        }
    }
}

private val LatitudeRange = -90.0..90.0
private val LongitudeRange = -180.0..180.0