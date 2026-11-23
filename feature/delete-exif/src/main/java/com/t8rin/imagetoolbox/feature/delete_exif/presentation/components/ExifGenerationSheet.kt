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

package com.t8rin.imagetoolbox.feature.delete_exif.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.toCoordinateOrNull
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AccessTime
import com.t8rin.imagetoolbox.core.resources.icons.CalendarMonth
import com.t8rin.imagetoolbox.core.resources.icons.CenterFocusStrong
import com.t8rin.imagetoolbox.core.resources.icons.LightMode
import com.t8rin.imagetoolbox.core.resources.icons.LocationOn
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.resources.icons.WandStars
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDatePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTimePickerDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifLighting
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifLocation
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifMetering
import com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic.DeleteExifComponent
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Composable
internal fun ExifGenerationSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    component: DeleteExifComponent
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.exif_generation_settings),
                icon = Icons.Rounded.Tune
            )
        },
        confirmButton = {
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        val settings = component.replacementSettings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DataSelector(
                value = settings.lighting,
                onValueChange = { component.updateReplacementSettings { copy(lighting = it) } },
                entries = ExifLighting.entries,
                title = stringResource(R.string.texture_lighting),
                titleIcon = Icons.Rounded.WandStars,
                itemContentText = { stringResource(it.titleRes()) },
                spanCount = 1,
                shape = ShapeDefaults.extraLarge,
                modifier = Modifier.fillMaxWidth()
            )
            DataSelector(
                value = settings.manualWhiteBalance,
                onValueChange = { component.updateReplacementSettings { copy(manualWhiteBalance = it) } },
                entries = listOf(false, true),
                title = stringResource(R.string.tag_white_balance),
                titleIcon = Icons.Outlined.LightMode,
                itemContentText = { stringResource(if (it) R.string.manually else R.string.auto) },
                spanCount = 1,
                shape = ShapeDefaults.extraLarge,
                modifier = Modifier.fillMaxWidth()
            )
            DataSelector(
                value = settings.metering,
                onValueChange = { component.updateReplacementSettings { copy(metering = it) } },
                entries = ExifMetering.entries,
                title = stringResource(R.string.tag_metering_mode),
                titleIcon = Icons.Rounded.CenterFocusStrong,
                itemContentText = {
                    stringResource(
                        if (it == ExifMetering.CenterWeighted) R.string.center_position
                        else it.titleRes()
                    )
                },
                spanCount = 1,
                shape = ShapeDefaults.extraLarge,
                modifier = Modifier.fillMaxWidth()
            )
            DataSelector(
                value = settings.captureTime != null,
                onValueChange = { manual ->
                    component.updateReplacementSettings {
                        val date = captureTime ?: LocalDateTime.now().withSecond(0).withNano(0)
                        copy(captureTime = date.takeIf { manual })
                    }
                },
                entries = listOf(false, true),
                title = stringResource(R.string.caption_date_taken),
                titleIcon = Icons.Rounded.CalendarMonth,
                itemContentText = {
                    stringResource(if (it) R.string.manual_date else R.string.exif_random_date)
                },
                spanCount = 1,
                shape = ShapeDefaults.extraLarge,
                modifier = Modifier.fillMaxWidth()
            )
            settings.captureTime?.let { date ->
                CaptureTimeControls(
                    value = date,
                    onValueChange = { component.updateReplacementSettings { copy(captureTime = it) } }
                )
            }
            DataSelector(
                value = settings.location,
                onValueChange = { component.updateReplacementSettings { copy(location = it) } },
                entries = ExifLocation.entries,
                title = stringResource(R.string.location),
                titleIcon = Icons.Rounded.LocationOn,
                itemContentText = { stringResource(it.titleRes()) },
                spanCount = 1,
                shape = ShapeDefaults.extraLarge,
                modifier = Modifier.fillMaxWidth()
            )
            if (settings.location == ExifLocation.Manual) {
                RoundedTextField(
                    value = settings.latitude,
                    onValueChange = { component.updateReplacementSettings { copy(latitude = it) } },
                    label = stringResource(R.string.latitude),
                    hint = "-90 … 90",
                    isError = settings.latitude.isNotEmpty() && settings.latitude.toCoordinateOrNull(
                        90
                    ) == null,
                    modifier = Modifier.fillMaxWidth()
                )
                RoundedTextField(
                    value = settings.longitude,
                    onValueChange = { component.updateReplacementSettings { copy(longitude = it) } },
                    label = stringResource(R.string.longitude),
                    hint = "-180 … 180",
                    isError = settings.longitude.isNotEmpty() && settings.longitude.toCoordinateOrNull(
                        180
                    ) == null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PreferenceRowSwitch(
                    title = stringResource(R.string.exif_include_software),
                    checked = settings.includeSoftware,
                    onClick = { component.updateReplacementSettings { copy(includeSoftware = it) } },
                    applyHorizontalPadding = false,
                    shape = ShapeDefaults.top,
                    modifier = Modifier.fillMaxWidth()
                )
                PreferenceRowSwitch(
                    title = stringResource(R.string.exif_same_for_batch),
                    checked = settings.sameForBatch,
                    onClick = { component.updateReplacementSettings { copy(sameForBatch = it) } },
                    applyHorizontalPadding = false,
                    shape = ShapeDefaults.bottom,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

internal fun ExifLighting.titleRes(): Int = when (this) {
    ExifLighting.Auto -> R.string.auto
    ExifLighting.Daylight -> R.string.exif_lighting_daylight
    ExifLighting.Indoors -> R.string.exif_lighting_indoors
    ExifLighting.Night -> R.string.gallery_category_night
}

internal fun ExifLocation.titleRes(): Int = when (this) {
    ExifLocation.None -> R.string.exif_no_gps
    ExifLocation.Random -> R.string.exif_random_gps
    ExifLocation.Manual -> R.string.exif_manual_gps
}

internal fun ExifMetering.titleRes(): Int = when (this) {
    ExifMetering.MultiSegment -> R.string.exif_metering_multi_segment
    ExifMetering.CenterWeighted -> R.string.exif_metering_center_weighted
    ExifMetering.Spot -> R.string.exif_metering_spot
}

@Composable
private fun CaptureTimeControls(
    value: LocalDateTime,
    onValueChange: (LocalDateTime) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = value.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = value.hour,
        initialMinute = value.minute
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PreferenceItem(
            title = stringResource(R.string.manual_date),
            subtitle = value.toLocalDate().toString(),
            startIcon = Icons.Rounded.CalendarMonth,
            shape = ShapeDefaults.top,
            modifier = Modifier.fillMaxWidth(),
            onClick = { showDatePicker = true }
        )
        PreferenceItem(
            title = stringResource(R.string.time),
            subtitle = value.toLocalTime().withSecond(0).withNano(0).toString(),
            startIcon = Icons.Outlined.AccessTime,
            shape = ShapeDefaults.bottom,
            modifier = Modifier.fillMaxWidth(),
            onClick = { showTimePicker = true }
        )
    }
    EnhancedDatePickerDialog(
        visible = showDatePicker,
        onDismissRequest = { showDatePicker = false },
        state = datePickerState,
        onDatePicked = {
            val date = Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC).toLocalDate()
            onValueChange(date.atTime(value.toLocalTime()))
        }
    )
    EnhancedTimePickerDialog(
        visible = showTimePicker,
        onDismissRequest = { showTimePicker = false },
        state = timePickerState,
        onTimePicked = { hour, minute ->
            onValueChange(value.toLocalDate().atTime(LocalTime.of(hour, minute)))
        }
    )
}