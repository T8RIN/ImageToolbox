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

@file:Suppress("UnusedReceiverParameter")

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.editor

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.SupervisedUserCircle
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Prefix
import com.t8rin.imagetoolbox.core.resources.icons.Suffix
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ContactPickerButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.toQrType
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.updateFormattedName

@Composable
internal fun QrContactEditField(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ContactPickerButton(
            onPicked = { contact ->
                onValueChange(contact.toQrType())
            }
        )

        ContactInfoEnterBlock(
            value = value,
            onValueChange = onValueChange
        )

        PhonesEnterBlock(
            value = value,
            onValueChange = onValueChange
        )

        EmailsEnterBlock(
            value = value,
            onValueChange = onValueChange
        )

        AddressesEnterBlock(
            value = value,
            onValueChange = onValueChange
        )

        UrlsEnterBlock(
            value = value,
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun ColumnScope.ContactInfoEnterBlock(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    TitleItem(
        text = stringResource(R.string.contact_info),
        icon = Icons.Outlined.Person,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    NameEditField(
        value = value,
        onValueChange = { onValueChange(it.updateFormattedName()) }
    )

    RoundedTextField(
        value = value.organization,
        onValueChange = { onValueChange(value.copy(organization = it)) },
        label = { Text(stringResource(R.string.organization)) },
        startIcon = { Icon(Icons.Outlined.Business, null) }
    )

    RoundedTextField(
        value = value.title,
        onValueChange = { onValueChange(value.copy(title = it)) },
        label = { Text(stringResource(R.string.title)) },
        startIcon = { Icon(Icons.Outlined.Badge, null) }
    )
}

@Composable
private fun ColumnScope.NameEditField(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    RoundedTextField(
        value = value.name.first,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(first = it))) },
        label = { Text(stringResource(R.string.first_name)) },
        startIcon = { Icon(Icons.Outlined.SupervisedUserCircle, null) }
    )

    RoundedTextField(
        value = value.name.middle,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(middle = it))) },
        label = { Text(stringResource(R.string.middle_name)) },
        startIcon = { Icon(Icons.Outlined.SupervisedUserCircle, null) }
    )

    RoundedTextField(
        value = value.name.last,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(last = it))) },
        label = { Text(stringResource(R.string.last_name)) },
        startIcon = { Icon(Icons.Outlined.SupervisedUserCircle, null) }
    )
    RoundedTextField(
        value = value.name.prefix,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(prefix = it))) },
        label = { Text(stringResource(R.string.prefix)) },
        startIcon = { Icon(Icons.Filled.Prefix, null) }
    )

    RoundedTextField(
        value = value.name.suffix,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(suffix = it))) },
        label = { Text(stringResource(R.string.suffix)) },
        startIcon = { Icon(Icons.Filled.Suffix, null) }
    )


    RoundedTextField(
        value = value.name.pronunciation,
        onValueChange = { onValueChange(value.copy(name = value.name.copy(pronunciation = it))) },
        label = { Text(stringResource(R.string.pronunciation)) },
        startIcon = { Icon(Icons.Outlined.RecordVoiceOver, null) }
    )
}

@Composable
private fun ColumnScope.UrlsEnterBlock(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    TitleItem(
        text = stringResource(R.string.urls),
        icon = Icons.Outlined.Public,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    value.urls.forEachIndexed { index, url ->
        RemovableTextField(
            value = url,
            onValueChange = {
                val updated = value.urls.toMutableList()
                updated[index] = it
                onValueChange(value.copy(urls = updated))
            },
            startIcon = Icons.Outlined.Link,
            label = "${stringResource(R.string.website)} ${index + 1}",
            onRemove = {
                val updated = value.urls.toMutableList()
                updated.removeAt(index)
                onValueChange(value.copy(urls = updated))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        )
    }

    AddButton(
        onClick = {
            onValueChange(value.copy(urls = value.urls + ""))
        },
        title = R.string.add_website
    )
}

@Composable
private fun ColumnScope.AddressesEnterBlock(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    TitleItem(
        text = stringResource(R.string.addresses),
        icon = Icons.Outlined.Home,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    value.addresses.forEachIndexed { index, address ->
        RemovableTextField(
            value = address.addressLines.joinToString(", "),
            onValueChange = {
                val updated = value.addresses.toMutableList()
                updated[index] =
                    address.copy(addressLines = it.split(",").map(String::trim))
                onValueChange(value.copy(addresses = updated))
            },
            startIcon = Icons.Outlined.Place,
            label = "${stringResource(R.string.address)} ${index + 1}",
            onRemove = {
                val updated = value.addresses.toMutableList()
                updated.removeAt(index)
                onValueChange(value.copy(addresses = updated))
            },
            keyboardOptions = KeyboardOptions()
        )
    }

    AddButton(
        onClick = {
            onValueChange(
                value.copy(
                    addresses = value.addresses + QrType.Contact.Address()
                )
            )
        },
        title = R.string.add_address
    )
}

@Composable
private fun ColumnScope.EmailsEnterBlock(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    TitleItem(
        text = stringResource(R.string.emails),
        icon = Icons.Outlined.Email,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    value.emails.forEachIndexed { index, email ->
        RemovableTextField(
            value = email.address,
            onValueChange = {
                val updated = value.emails.toMutableList()
                updated[index] = email.copy(address = it)
                onValueChange(value.copy(emails = updated))
            },
            startIcon = Icons.Outlined.AlternateEmail,
            label = "${stringResource(R.string.email)} ${index + 1}",
            onRemove = {
                val updated = value.emails.toMutableList()
                updated.removeAt(index)
                onValueChange(value.copy(emails = updated))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }

    AddButton(
        onClick = {
            onValueChange(value.copy(emails = value.emails + QrType.Email()))
        },
        title = R.string.add_email
    )
}

@Composable
private fun ColumnScope.PhonesEnterBlock(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    TitleItem(
        text = stringResource(R.string.phones),
        icon = Icons.Outlined.Call,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    value.phones.forEachIndexed { index, phone ->
        RemovableTextField(
            value = phone.number,
            onValueChange = {
                val updated = value.phones.toMutableList()
                updated[index] = phone.copy(number = it)
                onValueChange(value.copy(phones = updated))
            },
            startIcon = Icons.Rounded.Numbers,
            label = "${stringResource(R.string.phone)} ${index + 1}",
            onRemove = {
                val updated = value.phones.toMutableList()
                updated.removeAt(index)
                onValueChange(value.copy(phones = updated))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
    }

    AddButton(
        onClick = {
            onValueChange(value.copy(phones = value.phones + QrType.Phone()))
        },
        title = R.string.add_phone
    )
}

@Composable
private fun RemovableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
    startIcon: ImageVector,
    label: String,
    keyboardOptions: KeyboardOptions
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(text = label)
            },
            startIcon = {
                Icon(
                    imageVector = startIcon,
                    contentDescription = null
                )
            },
            keyboardOptions = keyboardOptions,
            modifier = Modifier.weight(1f)
        )
        EnhancedIconButton(
            onClick = onRemove
        ) {
            Icon(
                imageVector = Icons.Outlined.RemoveCircleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun AddButton(
    onClick: () -> Unit,
    @StringRes title: Int
) {
    EnhancedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Icon(Icons.Outlined.Add, null)
        Spacer(Modifier.width(4.dp))
        Text(stringResource(title))
    }
}