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

package com.t8rin.imagetoolbox.feature.batchrename.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AccessTime
import com.t8rin.imagetoolbox.core.resources.icons.CalendarMonth
import com.t8rin.imagetoolbox.core.resources.icons.Description
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDatePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTimePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.FilenamePatternEditSheet
import com.t8rin.imagetoolbox.feature.batchrename.domain.helper.RenamePatterns
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.DateSource
import com.t8rin.imagetoolbox.feature.batchrename.domain.model.RenameValidationError
import com.t8rin.imagetoolbox.feature.batchrename.presentation.screenLogic.BatchRenameComponent
import java.util.Calendar

@Composable
internal fun BatchRenameControls(
    component: BatchRenameComponent,
    validationError: RenameValidationError?
) {
    val previews by component.previews.collectAsStateWithLifecycle()
    val exampleFilename = previews.firstOrNull()?.newName.orEmpty()
    val isPortrait by isPortraitOrientationAsState()
    val usedFallbackDate = remember(previews) {
        previews.any { it.usedFallbackDate }
    }
    val hasDate = remember(component.pattern) {
        component.pattern.contains(
            other = FilenamePattern.Date.value,
            ignoreCase = true
        ) || component.pattern.contains(
            other = FilenamePattern.DateUpper.value,
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isPortrait) 20.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var showEditDialog by rememberSaveable { mutableStateOf(false) }

        PreferenceItem(
            onClick = { showEditDialog = true },
            title = stringResource(R.string.filename_pattern),
            subtitle = exampleFilename.ifBlank { component.pattern }.takeIf { it.isNotBlank() },
            endIcon = Icons.Rounded.MiniEdit,
            startIcon = Icons.Outlined.Description,
            modifier = Modifier.fillMaxWidth()
        )

        FilenamePatternEditSheet(
            visible = showEditDialog,
            onDismiss = { showEditDialog = false },
            value = component.pattern,
            onValueChange = component::updatePattern,
            onValueChangeFinished = component::updatePattern,
            allowedPatterns = RenamePatterns.entries,
            exampleFilename = {
                AnimatedVisibility(exampleFilename.isNotBlank()) {
                    PreferenceItem(
                        title = stringResource(R.string.filename),
                        subtitle = exampleFilename,
                        modifier = Modifier
                    )
                }
            }
        )

        validationError?.let { error ->
            InfoContainer(
                text = stringResource(error.messageRes()),
                containerColor = if (error == RenameValidationError.NoChanges) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer
                },
                contentColor = if (error == RenameValidationError.NoChanges) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }

        AnimatedVisibility(hasDate) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DataSelector(
                    value = component.dateSource,
                    onValueChange = component::updateDateSource,
                    entries = DateSource.entries,
                    title = stringResource(R.string.date_source),
                    titleIcon = Icons.Rounded.CalendarMonth,
                    itemContentText = { stringResource(it.titleRes()) },
                    spanCount = 2,
                    selectedItemColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(component.dateSource == DateSource.Manual) {
                    ManualDateControls(
                        value = component.manualDate,
                        onValueChange = component::updateManualDate
                    )
                }

                AnimatedVisibility(usedFallbackDate) {
                    InfoContainer(
                        text = stringResource(R.string.date_source_fallback)
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            previews.forEachIndexed { index, preview ->
                RenamePreviewItem(
                    preview = preview,
                    index = index,
                    shape = ShapeDefaults.byIndex(index, previews.size),
                    onRemove = { component.removeFile(preview.uri) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ManualDateControls(
    value: Long,
    onValueChange: (Long) -> Unit
) {
    val calendar = remember(value) {
        Calendar.getInstance().apply { timeInMillis = value }
    }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = value)
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PreferenceItem(
            title = stringResource(R.string.manual_date),
            subtitle = remember(value) {
                timestamp("yyyy-MM-dd", value)
            },
            startIcon = Icons.Rounded.CalendarMonth,
            onClick = { showDatePicker = true },
            shape = ShapeDefaults.top,
            modifier = Modifier.fillMaxWidth()
        )
        PreferenceItem(
            title = stringResource(R.string.manual_time),
            subtitle = remember(value) {
                timestamp("HH:mm", value)
            },
            startIcon = Icons.Outlined.AccessTime,
            onClick = { showTimePicker = true },
            shape = ShapeDefaults.bottom,
            modifier = Modifier.fillMaxWidth()
        )
    }

    EnhancedDatePickerDialog(
        visible = showDatePicker,
        onDismissRequest = { showDatePicker = false },
        state = datePickerState,
        onDatePicked = { selected ->
            val selectedCalendar = Calendar.getInstance().apply { timeInMillis = selected }
            val result = Calendar.getInstance().apply {
                timeInMillis = value
                set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
                set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, selectedCalendar.get(Calendar.DAY_OF_MONTH))
            }
            onValueChange(result.timeInMillis)
        }
    )
    EnhancedTimePickerDialog(
        visible = showTimePicker,
        onDismissRequest = { showTimePicker = false },
        state = timePickerState,
        onTimePicked = { hour, minute ->
            onValueChange(
                Calendar.getInstance().apply {
                    timeInMillis = value
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            )
        }
    )
}

private fun DateSource.titleRes(): Int = when (this) {
    DateSource.ExifDateTaken -> R.string.date_source_exif
    DateSource.FileModified -> R.string.date_source_modified
    DateSource.FileCreated -> R.string.date_source_created
    DateSource.Current -> R.string.date_source_current
    DateSource.Manual -> R.string.date_source_manual
}

private fun RenameValidationError.messageRes(): Int = when (this) {
    RenameValidationError.EmptyPattern -> R.string.rename_error_empty_pattern
    RenameValidationError.InvalidName -> R.string.rename_error_invalid_name
    RenameValidationError.DuplicateName -> R.string.rename_error_duplicate_name
    RenameValidationError.NoChanges -> R.string.rename_error_no_changes
    RenameValidationError.UnsupportedPattern -> R.string.rename_error_unsupported_pattern
}
