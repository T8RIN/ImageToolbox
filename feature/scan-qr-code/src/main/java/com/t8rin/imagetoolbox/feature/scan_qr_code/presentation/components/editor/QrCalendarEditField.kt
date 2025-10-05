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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.TimerEdit
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDateRangePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTimePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

@Composable
internal fun QrCalendarEditField(
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