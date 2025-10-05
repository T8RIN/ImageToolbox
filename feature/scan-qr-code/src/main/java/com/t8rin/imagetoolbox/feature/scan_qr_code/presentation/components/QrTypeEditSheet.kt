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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
import com.t8rin.imagetoolbox.core.resources.icons.TimerEdit
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Contact
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberContactPicker
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDateRangePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTimePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.value.filterDecimal
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

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
                onValueChange = {
                    edited = if (qrType != null && it::class.isInstance(qrType)) qrType else it
                },
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

@Composable
private fun QrContactEditField(
    value: QrType.Contact,
    onValueChange: (QrType.Contact) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ContactPickerButton(
            onPicked = { contact ->
                onValueChange(
                    contact.toQrType()
                )
            }
        )

        TitleItem(
            text = stringResource(R.string.contact_info),
            icon = Icons.Outlined.Person,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // --- PERSON NAME FIELDS ---
        RoundedTextField(
            value = value.name.formattedName,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(formattedName = it))) },
            label = { Text(stringResource(R.string.formatted_name)) },
        )

        RoundedTextField(
            value = value.name.first,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(first = it))) },
            label = { Text(stringResource(R.string.first_name)) },
        )

        RoundedTextField(
            value = value.name.middle,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(middle = it))) },
            label = { Text(stringResource(R.string.middle_name)) },
        )

        RoundedTextField(
            value = value.name.last,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(last = it))) },
            label = { Text(stringResource(R.string.last_name)) },
        )

        RoundedTextField(
            value = value.name.prefix,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(prefix = it))) },
            label = { Text(stringResource(R.string.prefix)) }
        )


        RoundedTextField(
            value = value.name.suffix,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(suffix = it))) },
            label = { Text(stringResource(R.string.suffix)) },
        )

        RoundedTextField(
            value = value.name.pronunciation,
            onValueChange = { onValueChange(value.copy(name = value.name.copy(pronunciation = it))) },
            label = { Text(stringResource(R.string.pronunciation)) },
            startIcon = { Icon(Icons.Outlined.RecordVoiceOver, null) }
        )

        // --- ORGANIZATION & TITLE ---
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

        // --- PHONES ---
        TitleItem(
            text = stringResource(R.string.phones),
            icon = Icons.Outlined.Call,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        value.phones.forEachIndexed { index, phone ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedTextField(
                    value = phone.number,
                    onValueChange = {
                        val updated = value.phones.toMutableList()
                        updated[index] = phone.copy(number = it)
                        onValueChange(value.copy(phones = updated))
                    },
                    label = { Text("${stringResource(R.string.phone)} ${index + 1}") },
                    startIcon = { Icon(Icons.Rounded.Numbers, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    onClick = {
                        val updated = value.phones.toMutableList()
                        updated.removeAt(index)
                        onValueChange(value.copy(phones = updated))
                    }
                ) {
                    Icon(Icons.Outlined.Delete, null)
                }
            }
        }

        EnhancedButton(
            onClick = {
                onValueChange(value.copy(phones = value.phones + QrType.Phone("", "", 0)))
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Outlined.Add, null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.add_phone))
        }

        // --- EMAILS ---
        TitleItem(
            text = stringResource(R.string.emails),
            icon = Icons.Outlined.Email,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        value.emails.forEachIndexed { index, email ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedTextField(
                    value = email.address,
                    onValueChange = {
                        val updated = value.emails.toMutableList()
                        updated[index] = email.copy(address = it)
                        onValueChange(value.copy(emails = updated))
                    },
                    label = { Text("${stringResource(R.string.email)} ${index + 1}") },
                    startIcon = { Icon(Icons.Outlined.Email, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    onClick = {
                        val updated = value.emails.toMutableList()
                        updated.removeAt(index)
                        onValueChange(value.copy(emails = updated))
                    }
                ) {
                    Icon(Icons.Outlined.Delete, null)
                }
            }
        }

        EnhancedButton(
            onClick = {
                onValueChange(value.copy(emails = value.emails + QrType.Email("", "", "", "", 0)))
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Outlined.Add, null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.add_email))
        }

        // --- ADDRESSES ---
        TitleItem(
            text = stringResource(R.string.addresses),
            icon = Icons.Outlined.Place,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        value.addresses.forEachIndexed { index, address ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundedTextField(
                    value = address.addressLines.joinToString(", "),
                    onValueChange = {
                        val updated = value.addresses.toMutableList()
                        updated[index] =
                            address.copy(addressLines = it.split(",").map(String::trim))
                        onValueChange(value.copy(addresses = updated))
                    },
                    label = { Text("${stringResource(R.string.address)} ${index + 1}") },
                    startIcon = { Icon(Icons.Outlined.Place, null) },
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    onClick = {
                        val updated = value.addresses.toMutableList()
                        updated.removeAt(index)
                        onValueChange(value.copy(addresses = updated))
                    }
                ) {
                    Icon(Icons.Outlined.Delete, null)
                }
            }
        }

        EnhancedButton(
            onClick = {
                onValueChange(
                    value.copy(
                        addresses = value.addresses + QrType.Contact.Address(
                            emptyList(),
                            0
                        )
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Outlined.Add, null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.add_address))
        }

        // --- URLS ---
        TitleItem(
            text = stringResource(R.string.urls),
            icon = Icons.Outlined.Public,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        value.urls.forEachIndexed { index, url ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundedTextField(
                    value = url,
                    onValueChange = {
                        val updated = value.urls.toMutableList()
                        updated[index] = it
                        onValueChange(value.copy(urls = updated))
                    },
                    label = { Text("${stringResource(R.string.website)} ${index + 1}") },
                    startIcon = { Icon(Icons.Outlined.Link, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.weight(1f)
                )
                EnhancedIconButton(
                    onClick = {
                        val updated = value.urls.toMutableList()
                        updated.removeAt(index)
                        onValueChange(value.copy(urls = updated))
                    }
                ) {
                    Icon(Icons.Outlined.Delete, null)
                }
            }
        }

        EnhancedButton(
            onClick = {
                onValueChange(value.copy(urls = value.urls + ""))
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Outlined.Add, null)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.add_website))
        }
    }
}

@Composable
private fun QrCalendarEditField(
    value: QrType.Calendar,
    onValueChange: (QrType.Calendar) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedTextField(
            value = value.summary,
            onValueChange = { onValueChange(value.copy(summary = it)) },
            label = { Text(stringResource(R.string.summary)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null
                )
            }
        )

        RoundedTextField(
            value = value.description,
            onValueChange = { onValueChange(value.copy(description = it)) },
            label = { Text(stringResource(R.string.description)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null
                )
            },
            singleLine = false
        )

        RoundedTextField(
            value = value.location,
            onValueChange = { onValueChange(value.copy(location = it)) },
            label = { Text(stringResource(R.string.location)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null
                )
            }
        )

        RoundedTextField(
            value = value.organizer,
            onValueChange = { onValueChange(value.copy(organizer = it)) },
            label = { Text(stringResource(R.string.organizer)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null
                )
            }
        )

        var isDateDialogVisible by rememberSaveable { mutableStateOf(false) }
        var showStartTimePicker by rememberSaveable { mutableStateOf(false) }
        var showEndTimePicker by rememberSaveable { mutableStateOf(false) }

        val startDate = remember(value.start) {
            value.start ?: Date()
        }
        val endDate = remember(value.end) {
            value.end ?: Calendar.getInstance()
                .apply { add(Calendar.DAY_OF_YEAR, 1) }.time
        }

        val startCalendar = remember(startDate) {
            Calendar.getInstance().apply { time = startDate }
        }
        val endCalendar = remember(endDate) {
            Calendar.getInstance().apply { time = endDate }
        }

        val startText = remember(startDate) {
            runCatching {
                DateFormat.getDateTimeInstance().format(startDate).removeSuffix(":00")
            }.getOrDefault("")
        }

        val endText = remember(endDate) {
            runCatching {
                DateFormat.getDateTimeInstance().format(endDate).removeSuffix(":00")
            }.getOrDefault("")
        }

        Box {
            RoundedTextField(
                value = startText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.start_date)) },
                startIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Start,
                        contentDescription = null
                    )
                },
                endIcon = {
                    Icon(
                        imageVector = Icons.Outlined.TimerEdit,
                        contentDescription = null
                    )
                }
            )
            Spacer(
                Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (offset.x > size.width - size.height) {
                                showStartTimePicker = true
                            } else {
                                isDateDialogVisible = true
                            }
                        }
                    }
            )
        }

        Box {
            RoundedTextField(
                value = endText,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.end_date)) },
                startIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = null
                    )
                },
                endIcon = {
                    Icon(
                        imageVector = Icons.Outlined.TimerEdit,
                        contentDescription = null
                    )
                }
            )
            Spacer(
                Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (offset.x > size.width - size.height) {
                                showEndTimePicker = true
                            } else {
                                isDateDialogVisible = true
                            }
                        }
                    }
            )
        }

        RoundedTextField(
            value = value.status,
            onValueChange = { onValueChange(value.copy(status = it)) },
            label = { Text(stringResource(R.string.status)) },
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            }
        )
        val state = rememberDateRangePickerState(
            initialSelectedStartDateMillis = startDate.time,
            initialSelectedEndDateMillis = endDate.time
        )
        val startTimeState = rememberTimePickerState(
            initialHour = startCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = startCalendar.get(Calendar.MINUTE)
        )
        val endTimeState = rememberTimePickerState(
            initialHour = endCalendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = endCalendar.get(Calendar.MINUTE)
        )

        LaunchedEffect(startCalendar, endCalendar) {
            startTimeState.hour = startCalendar.get(Calendar.HOUR_OF_DAY)
            startTimeState.minute = startCalendar.get(Calendar.MINUTE)

            endTimeState.hour = endCalendar.get(Calendar.HOUR_OF_DAY)
            endTimeState.minute = endCalendar.get(Calendar.MINUTE)
        }

        EnhancedDateRangePickerDialog(
            visible = isDateDialogVisible,
            onDismissRequest = { isDateDialogVisible = false },
            state = state,
            onDatePicked = { start, end ->
                onValueChange(
                    value.copy(
                        start = Date(start),
                        end = Date(end)
                    )
                )
            }
        )

        EnhancedTimePickerDialog(
            visible = showStartTimePicker,
            onDismissRequest = { showStartTimePicker = false },
            state = startTimeState,
            onTimePicked = { hour, minute ->
                onValueChange(
                    value.copy(
                        start = startCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }.time
                    )
                )
            }
        )

        EnhancedTimePickerDialog(
            visible = showEndTimePicker,
            onDismissRequest = { showEndTimePicker = false },
            state = endTimeState,
            onTimePicked = { hour, minute ->
                onValueChange(
                    value.copy(
                        end = endCalendar.apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }.time
                    )
                )
            }
        )

        LaunchedEffect(Unit) {
            val start = state.selectedStartDateMillis
            val end = state.selectedEndDateMillis

            if (start != null && end != null) {
                onValueChange(
                    value.copy(
                        start = Date(start),
                        end = Date(end)
                    )
                )
            }
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
        containerColor = MaterialTheme.colorScheme.mixedContainer,
        contentColor = MaterialTheme.colorScheme.onMixedContainer
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
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