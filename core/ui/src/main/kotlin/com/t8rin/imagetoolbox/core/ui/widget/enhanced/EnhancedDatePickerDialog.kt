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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.alertDialogBorder
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges

@Composable
fun EnhancedDatePickerDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placeAboveAll: Boolean = false,
    state: DatePickerState,
    onDatePicked: (Long) -> Unit,
    colors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = AlertDialogDefaults.containerColor
    ),
    dateFormatter: DatePickerFormatter = remember { DatePickerDefaults.dateFormatter() },
    title: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding),
            contentColor = colors.titleContentColor,
        )
    },
    headline: (@Composable () -> Unit)? = {
        DatePickerDefaults.DatePickerHeadline(
            selectedDateMillis = state.selectedDateMillis,
            displayMode = state.displayMode,
            dateFormatter = dateFormatter,
            modifier = Modifier.padding(DatePickerHeadlinePadding),
            contentColor = colors.headlineContentColor,
        )
    },
    showModeToggle: Boolean = true,
    focusRequester: FocusRequester? = remember { FocusRequester() },
    shape: Shape = AlertDialogDefaults.shape,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    EnhancedDatePickerDialogContainer(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placeAboveAll = placeAboveAll,
        shape = shape,
        tonalElevation = tonalElevation,
        colors = colors,
        onConfirmClick = {
            state.selectedDateMillis?.let {
                onDatePicked(it)
                onDismissRequest()
            }
        },
        isConfirmEnabled = state.selectedDateMillis != null,
        height = ContainerHeight
    ) {
        val scrollState = rememberScrollState()

        DatePicker(
            modifier = Modifier
                .fadingEdges(
                    scrollableState = scrollState,
                    isVertical = true
                )
                .verticalScroll(scrollState),
            state = state,
            dateFormatter = dateFormatter,
            colors = colors,
            title = title,
            headline = headline,
            showModeToggle = showModeToggle,
            focusRequester = focusRequester
        )
    }
}

@Composable
fun EnhancedDateRangePickerDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placeAboveAll: Boolean = false,
    state: DateRangePickerState,
    onDatePicked: (start: Long, end: Long) -> Unit,
    colors: DatePickerColors = DatePickerDefaults.colors(
        containerColor = AlertDialogDefaults.containerColor
    ),
    dateFormatter: DatePickerFormatter = remember {
        DatePickerDefaults.dateFormatter(selectedDateSkeleton = "dd.MM.yy")
    },
    title: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding),
            contentColor = colors.titleContentColor,
        )
    },
    headline: (@Composable () -> Unit)? = {
        DateRangePickerDefaults.DateRangePickerHeadline(
            selectedStartDateMillis = state.selectedStartDateMillis,
            selectedEndDateMillis = state.selectedEndDateMillis,
            displayMode = state.displayMode,
            dateFormatter = dateFormatter,
            modifier = Modifier.padding(DatePickerHeadlinePadding),
            contentColor = colors.headlineContentColor,
        )
    },
    showModeToggle: Boolean = true,
    focusRequester: FocusRequester? = remember { FocusRequester() },
    shape: Shape = AlertDialogDefaults.shape,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    EnhancedDatePickerDialogContainer(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placeAboveAll = placeAboveAll,
        shape = shape,
        tonalElevation = tonalElevation,
        colors = colors,
        onConfirmClick = {
            val start = state.selectedStartDateMillis
            val end = state.selectedEndDateMillis

            if (start != null && end != null) {
                onDatePicked(start, end)
                onDismissRequest()
            }
        },
        isConfirmEnabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
        height = ContainerHeightRange
    ) {
        DateRangePicker(
            modifier = Modifier
                .fadingEdges(
                    scrollableState = null,
                    isVertical = true,
                    length = 8.dp
                ),
            state = state,
            dateFormatter = dateFormatter,
            colors = colors,
            title = title,
            headline = headline,
            showModeToggle = showModeToggle,
            focusRequester = focusRequester
        )
    }
}

@Composable
private fun EnhancedDatePickerDialogContainer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placeAboveAll: Boolean = false,
    shape: Shape,
    tonalElevation: Dp,
    colors: DatePickerColors,
    onConfirmClick: () -> Unit,
    isConfirmEnabled: Boolean,
    height: Dp,
    content: @Composable ColumnScope.() -> Unit
) {
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placeAboveAll = placeAboveAll,
    ) {
        val isCenterAlignButtons = LocalSettingsState.current.isCenterAlignDialogButtons

        Surface(
            modifier = Modifier
                .alertDialogBorder()
                .requiredWidth(ContainerWidth)
                .heightIn(max = height),
            shape = shape,
            color = colors.containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                DatePickerContainer(content = content)

                FlowRow(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(DialogButtonsPadding),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = ButtonsHorizontalSpacing,
                        alignment = if (isCenterAlignButtons) {
                            Alignment.CenterHorizontally
                        } else Alignment.End
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        space = ButtonsVerticalSpacing,
                        alignment = if (isCenterAlignButtons) {
                            Alignment.CenterVertically
                        } else Alignment.Bottom
                    ),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    EnhancedButton(
                        onClick = onDismissRequest,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(stringResource(R.string.close))
                    }
                    EnhancedButton(
                        onClick = onConfirmClick,
                        enabled = isConfirmEnabled
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.DatePickerContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    // Wrap the content with a Box and Modifier.weight(1f) to ensure that any "confirm"
    // and "dismiss" buttons are not pushed out of view when running on small screens,
    // or when nesting a DateRangePicker.
    // Fill is false to support collapsing the dialog's height when switching to input
    // mode.
    Box(
        modifier = Modifier.weight(1f, fill = false)
    ) {
        this@DatePickerContainer.content()
    }
}

private val DialogButtonsPadding = PaddingValues(24.dp)
private val ButtonsHorizontalSpacing = 8.dp
private val ButtonsVerticalSpacing = 12.dp
private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)
private val ContainerWidth = 360.dp
private val ContainerHeight = 568.dp
private val ContainerHeightRange = 568.dp