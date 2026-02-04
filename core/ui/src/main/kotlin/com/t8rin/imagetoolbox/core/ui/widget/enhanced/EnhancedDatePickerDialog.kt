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

@file:Suppress("unused")

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.alertDialogBorder
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import kotlin.math.truncate

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
        containerColor = colors.containerColor,
        onConfirmClick = {
            state.selectedDateMillis?.let {
                onDatePicked(it)
                onDismissRequest()
            }
        },
        isConfirmEnabled = state.selectedDateMillis != null
    ) {
        val scrollState = rememberScrollState()

        DatePicker(
            modifier = Modifier
                .fadingEdges(
                    scrollableState = scrollState,
                    isVertical = true
                )
                .enhancedVerticalScroll(scrollState),
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
        containerColor = colors.containerColor,
        onConfirmClick = {
            val start = state.selectedStartDateMillis
            val end = state.selectedEndDateMillis

            if (start != null && end != null) {
                onDatePicked(start, end)
                onDismissRequest()
            }
        },
        isConfirmEnabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null
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
fun EnhancedTimePickerDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placeAboveAll: Boolean = false,
    state: TimePickerState,
    onTimePicked: (hour: Int, minute: Int) -> Unit,
    colors: TimePickerColors = TimePickerDefaults.colors(
        containerColor = AlertDialogDefaults.containerColor
    ),
    layoutType: TimePickerLayoutType = TimePickerDefaults.layoutType(),
    shape: Shape = AlertDialogDefaults.shape,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    var displayMode by remember {
        mutableStateOf(TimePickerDisplayMode.Picker)
    }

    EnhancedDatePickerDialogContainer(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placeAboveAll = placeAboveAll,
        shape = shape,
        tonalElevation = tonalElevation,
        containerColor = colors.containerColor,
        onConfirmClick = {},
        showButtons = false,
        isConfirmEnabled = false
    ) {
        TimePickerCustomLayout(
            title = { TimePickerDialogDefaults.Title(displayMode) },
            actions = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnhancedIconButton(
                        modifier = modifier,
                        onClick = {
                            displayMode = if (displayMode == TimePickerDisplayMode.Picker) {
                                TimePickerDisplayMode.Input
                            } else {
                                TimePickerDisplayMode.Picker
                            }
                        }
                    ) {
                        val icon = if (displayMode == TimePickerDisplayMode.Picker) {
                            Icons.Outlined.Keyboard
                        } else {
                            Icons.Outlined.Schedule
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = icon.name
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    EnhancedButton(
                        onClick = {
                            onTimePicked(state.hour, state.minute)
                            onDismissRequest()
                        }
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            },
            content = {
                AnimatedContent(displayMode) { mode ->
                    if (mode == TimePickerDisplayMode.Picker) {
                        TimePicker(
                            state = state,
                            colors = colors,
                            layoutType = layoutType
                        )
                    } else {
                        TimeInput(
                            state = state,
                            colors = colors
                        )
                    }
                }
            }
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
    containerColor: Color,
    onConfirmClick: () -> Unit,
    isConfirmEnabled: Boolean,
    showButtons: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    BasicEnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placeAboveAll = placeAboveAll,
    ) {
        Surface(
            modifier = Modifier
                .alertDialogBorder()
                .then(
                    if (showButtons) Modifier.requiredWidth(ContainerWidth)
                    else Modifier
                )
                .heightIn(max = ContainerHeight),
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                DatePickerContainer(content = content)

                if (showButtons) {
                    val isCenterAlignButtons = LocalSettingsState.current.isCenterAlignDialogButtons

                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(DialogButtonsPadding)
                    ) {
                        FlowRow(
                            modifier = Modifier.weight(1f),
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
    }
}

@Composable
private fun TimePickerCustomLayout(
    title: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val content =
        @Composable {
            Box(modifier = Modifier.layoutId("title")) { title() }
            Box(modifier = Modifier.layoutId("actions")) { actions() }
            Column(modifier = Modifier.layoutId("timePickerContent"), content = content)
        }

    val measurePolicy = MeasurePolicy { measurables, constraints ->
        val titleMeasurable = measurables.fastFirst { it.layoutId == "title" }
        val contentMeasurable = measurables.fastFirst { it.layoutId == "timePickerContent" }
        val actionsMeasurable = measurables.fastFirst { it.layoutId == "actions" }

        val contentPadding = 24.dp.roundToPx()
        val landMaxDialogHeight = 384.dp.roundToPx()
        val landTitleTopPadding = 24.dp.roundToPx()
        val landContentTopPadding = 16.dp.roundToPx()
        val landContentActionsPadding = 4.dp.roundToPx()
        val landActionsBottomPadding = 8.dp.roundToPx()

        val portTitleTopPadding = 24.dp.roundToPx()
        val portActionsBottomPadding = 24.dp.roundToPx()

        val contentPlaceable = contentMeasurable.measure(constraints.copy(minHeight = 0))

        // Input mode will be smaller than the smallest TimePickerContent (currently 200.dp)
        // But will always use portrait layout for correctness.
        val isLandscape =
            contentPlaceable.width > contentPlaceable.height &&
                    contentPlaceable.height >= truncate(ClockDialMinContainerSize.toPx())

        val dialogWidth =
            if (isLandscape) {
                contentPlaceable.width + contentPadding * 2
            } else {
                contentPlaceable.width + contentPadding * 2
            }

        val actionsPlaceable =
            actionsMeasurable.measure(
                constraints.copy(minWidth = 0, minHeight = 0, maxWidth = contentPlaceable.width)
            )

        val titlePlaceable =
            titleMeasurable.measure(
                constraints.copy(minWidth = 0, minHeight = 0, maxWidth = contentPlaceable.width)
            )

        val layoutHeight =
            if (isLandscape) {
                val contentTotalHeight =
                    contentPlaceable.height +
                            actionsPlaceable.height +
                            landActionsBottomPadding +
                            landContentTopPadding +
                            landContentActionsPadding
                if (constraints.hasBoundedHeight) constraints.maxHeight else contentTotalHeight
            } else {
                portTitleTopPadding +
                        titlePlaceable.height +
                        contentPlaceable.height +
                        actionsPlaceable.height +
                        portActionsBottomPadding
            }

        layout(width = dialogWidth, height = layoutHeight) {
            if (isLandscape) {
                val contentHeight =
                    landContentTopPadding +
                            contentPlaceable.height +
                            landContentActionsPadding +
                            actionsPlaceable.height +
                            landActionsBottomPadding
                val remainingSpace = layoutHeight - contentHeight
                val adjustedActionsBottomPadding =
                    if (layoutHeight >= landMaxDialogHeight) {
                        16.dp.roundToPx()
                    } else {
                        0
                    }

                titlePlaceable.place(x = landTitleTopPadding, y = landTitleTopPadding)
                val timePickerContentX = contentPadding
                val timePickerContentY = landContentTopPadding + remainingSpace / 2
                contentPlaceable.place(x = timePickerContentX, y = timePickerContentY)
                val actionsY =
                    timePickerContentY + contentPlaceable.height + landContentActionsPadding -
                            adjustedActionsBottomPadding + remainingSpace / 2
                actionsPlaceable.place(x = timePickerContentX, y = actionsY)
            } else {
                val titleX = landTitleTopPadding
                titlePlaceable.place(x = titleX, y = portTitleTopPadding)

                val contentX = (dialogWidth - contentPlaceable.width) / 2
                val contentY = portTitleTopPadding + titlePlaceable.height
                contentPlaceable.place(x = contentX, y = contentY)

                val actionsX = (dialogWidth - actionsPlaceable.width) / 2
                val actionsY = contentY + contentPlaceable.height
                actionsPlaceable.place(x = actionsX, y = actionsY)
            }
        }
    }

    Layout(content = content, measurePolicy = measurePolicy)
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

private val TimePickerMaxHeight = 384.dp
private val TimePickerMidHeight = 330.dp
private val ClockDialMidContainerSize = 238.dp
internal val ClockDialMinContainerSize = 200.dp