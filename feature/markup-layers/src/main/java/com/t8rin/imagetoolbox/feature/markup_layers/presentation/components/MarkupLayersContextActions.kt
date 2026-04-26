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

@file:Suppress("UnusedReceiverParameter")

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowDropDown
import com.t8rin.imagetoolbox.core.resources.icons.ArrowDropUp
import com.t8rin.imagetoolbox.core.resources.icons.ArrowLeft
import com.t8rin.imagetoolbox.core.resources.icons.ArrowRight
import com.t8rin.imagetoolbox.core.resources.icons.CenterFocusStrong
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Deselect
import com.t8rin.imagetoolbox.core.resources.icons.FitScreen
import com.t8rin.imagetoolbox.core.resources.icons.Flip
import com.t8rin.imagetoolbox.core.resources.icons.FlipVertical
import com.t8rin.imagetoolbox.core.resources.icons.Lock
import com.t8rin.imagetoolbox.core.resources.icons.LockOpen
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.ScreenRotationAlt
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueDialog
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueText

@Composable
internal fun BoxScope.MarkupLayersContextActions(
    visible: Boolean,
    onDismiss: () -> Unit,
    onCopyLayer: () -> Unit,
    onToggleEditMode: () -> Unit,
    onRemoveLayer: () -> Unit,
    onActivateLayer: () -> Unit,
    isLayerLocked: Boolean,
    onToggleLayerLock: () -> Unit,
    isGroupingSelectionMode: Boolean,
    groupingSelectionCount: Int,
    onFlipLayerHorizontally: () -> Unit,
    onFlipLayerVertically: () -> Unit,
    onMoveLayerBy: (Float, Float) -> Unit,
    onResetLayerPosition: () -> Unit,
    onNormalizedPositionXChange: (Float) -> Unit,
    onNormalizedPositionYChange: (Float) -> Unit,
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    scale: Float?,
    onScaleChange: (Float) -> Unit,
    onScaleChangeFinished: () -> Unit,
    rotationDegrees: Float?,
    onRotationDegreesChange: (Float) -> Unit,
    onRotationDegreesChangeFinished: () -> Unit
) {
    var expandedAdjustableAction by rememberSaveable {
        mutableStateOf(AdjustableActionType.None)
    }
    var valueDialogType by rememberSaveable {
        mutableStateOf(ValueDialogType.None)
    }
    val transformActionsEnabled = !isLayerLocked && !isGroupingSelectionMode

    EnhancedDropdownMenu(
        expanded = visible,
        onDismissRequest = {
            if (expandedAdjustableAction != AdjustableActionType.None) {
                expandedAdjustableAction = AdjustableActionType.None
            } else {
                onDismiss()
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = {
                        onToggleEditMode()
                        onDismiss()
                    },
                    enabled = transformActionsEnabled,
                    icon = Icons.Rounded.MiniEdit,
                    text = stringResource(R.string.edit),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onCopyLayer,
                    enabled = !isGroupingSelectionMode,
                    icon = Icons.Rounded.ContentCopy,
                    text = stringResource(R.string.copy),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onRemoveLayer,
                    enabled = !isGroupingSelectionMode,
                    icon = Icons.Rounded.Delete,
                    text = stringResource(R.string.delete),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = onActivateLayer,
                    enabled = !isGroupingSelectionMode || groupingSelectionCount > 0,
                    icon = Icons.Outlined.Deselect,
                    text = stringResource(R.string.clear_selection),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onFlipLayerHorizontally,
                    enabled = transformActionsEnabled,
                    icon = Icons.Outlined.Flip,
                    text = stringResource(R.string.horizontal_flip),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onFlipLayerVertically,
                    enabled = transformActionsEnabled,
                    icon = Icons.Outlined.FlipVertical,
                    text = stringResource(R.string.vertical_flip),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
            }
            val activeActionContainerColor = takeColorFromScheme {
                surfaceContainerLow.blend(
                    color = tertiaryContainer,
                    fraction = 0.7f
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = {
                        onToggleLayerLock()
                        onDismiss()
                    },
                    enabled = !isGroupingSelectionMode,
                    icon = if (isLayerLocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                    text = stringResource(
                        if (isLayerLocked) R.string.unlock else R.string.lock
                    ),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = {
                        if (transformActionsEnabled) {
                            expandedAdjustableAction =
                                expandedAdjustableAction.toggle(AdjustableActionType.Rotation)
                        }
                    },
                    enabled = transformActionsEnabled,
                    icon = Icons.Rounded.ScreenRotationAlt,
                    text = stringResource(R.string.rotation),
                    containerColor = if (expandedAdjustableAction == AdjustableActionType.Rotation) {
                        activeActionContainerColor
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = {
                        if (transformActionsEnabled) {
                            expandedAdjustableAction =
                                expandedAdjustableAction.toggle(AdjustableActionType.Scale)
                        }
                    },
                    enabled = transformActionsEnabled,
                    icon = Icons.Outlined.FitScreen,
                    text = stringResource(R.string.scale),
                    containerColor = if (expandedAdjustableAction == AdjustableActionType.Scale) {
                        activeActionContainerColor
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
            }
            AnimatedContent(
                targetState = expandedAdjustableAction,
                modifier = Modifier.fillMaxWidth()
            ) { action ->
                when (action) {
                    AdjustableActionType.Rotation -> AdjustableActionCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        title = stringResource(R.string.rotation),
                        value = rotationDegrees ?: 0f,
                        valueRange = 0f..360f,
                        enabled = transformActionsEnabled,
                        sliderEnabled = rotationDegrees != null,
                        onValueClick = {
                            expandedAdjustableAction = AdjustableActionType.None
                            onDismiss()
                            valueDialogType = ValueDialogType.Rotation
                        },
                        onValueChange = onRotationDegreesChange,
                        onValueChangeFinished = onRotationDegreesChangeFinished
                    )

                    AdjustableActionType.Scale -> AdjustableActionCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        title = stringResource(R.string.scale),
                        value = scale ?: 1f,
                        valueRange = 0.1f..10f,
                        enabled = transformActionsEnabled,
                        sliderEnabled = scale != null,
                        onValueClick = {
                            expandedAdjustableAction = AdjustableActionType.None
                            onDismiss()
                            valueDialogType = ValueDialogType.Scale
                        },
                        onValueChange = onScaleChange,
                        onValueChangeFinished = onScaleChangeFinished
                    )

                    AdjustableActionType.None -> Unit
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    val buttonContainerColor = takeColorFromScheme {
                        surfaceContainerLow.blend(
                            color = primaryContainer,
                            fraction = 0.2f
                        )
                    }

                    ClickableTile(
                        onClick = { onMoveLayerBy(-1f, 0f) },
                        onHoldStep = { onMoveLayerBy(-1f, 0f) },
                        enabled = transformActionsEnabled,
                        icon = Icons.Rounded.ArrowLeft,
                        text = null,
                        containerColor = buttonContainerColor,
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                    ) {
                        ClickableTile(
                            onClick = { onMoveLayerBy(0f, -1f) },
                            onHoldStep = { onMoveLayerBy(0f, -1f) },
                            enabled = transformActionsEnabled,
                            icon = Icons.Rounded.ArrowDropUp,
                            text = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            containerColor = buttonContainerColor
                        )
                        ClickableTile(
                            onClick = { onMoveLayerBy(0f, 1f) },
                            onHoldStep = { onMoveLayerBy(0f, 1f) },
                            enabled = transformActionsEnabled,
                            icon = Icons.Rounded.ArrowDropDown,
                            text = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            containerColor = buttonContainerColor
                        )
                    }
                    ClickableTile(
                        onClick = { onMoveLayerBy(1f, 0f) },
                        onHoldStep = { onMoveLayerBy(1f, 0f) },
                        enabled = transformActionsEnabled,
                        icon = Icons.Rounded.ArrowRight,
                        text = null,
                        containerColor = buttonContainerColor,
                        modifier = Modifier
                            .width(66.dp)
                            .fillMaxHeight()
                    )
                }
            }
            if (normalizedPositionX != null && normalizedPositionY != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.container(
                        shape = ShapeDefaults.extraSmall,
                        color = takeColorFromScheme {
                            surfaceContainerLow.blend(tertiaryContainer, 0.3f)
                        },
                        resultPadding = 0.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val x = normalizedPositionX.roundTo(3)
                        val y = normalizedPositionY.roundTo(3)

                        AutoSizeText(
                            text = "X: $x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .then(
                                    if (transformActionsEnabled) {
                                        Modifier.hapticsClickable {
                                            onDismiss()
                                            valueDialogType = ValueDialogType.PositionX
                                        }
                                    } else Modifier
                                )
                                .alpha(if (transformActionsEnabled) 1f else 0.5f)
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 4.dp
                                )
                        )
                        AutoSizeText(
                            text = "Y: $y",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .then(
                                    if (transformActionsEnabled) {
                                        Modifier.hapticsClickable {
                                            onDismiss()
                                            valueDialogType = ValueDialogType.PositionY
                                        }
                                    } else Modifier
                                )
                                .alpha(if (transformActionsEnabled) 1f else 0.5f)
                                .padding(
                                    start = 4.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 8.dp
                                )
                        )
                    }
                    SupportingButton(
                        icon = Icons.Rounded.CenterFocusStrong,
                        onClick = {
                            if (transformActionsEnabled) {
                                onResetLayerPosition()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        shape = ShapeDefaults.extremeSmall,
                        modifier = Modifier
                            .padding(4.dp)
                            .alpha(if (transformActionsEnabled) 1f else 0.5f)
                    )
                }
            }
        }
    }

    val activeValueDialogType = valueDialogType

    ValueDialog(
        roundTo = when (activeValueDialogType) {
            ValueDialogType.Rotation -> null
            ValueDialogType.Scale -> 3
            ValueDialogType.PositionX,
            ValueDialogType.PositionY -> 3
            ValueDialogType.None -> null
        },
        valueRange = when (activeValueDialogType) {
            ValueDialogType.Rotation -> 0f..360f
            ValueDialogType.Scale -> 0.1f..10f
            ValueDialogType.PositionX,
            ValueDialogType.PositionY -> -2f..2f

            ValueDialogType.None -> 0f..1f
        },
        valueState = when (activeValueDialogType) {
            ValueDialogType.Rotation -> (rotationDegrees ?: 0f).toString()
            ValueDialogType.Scale -> (scale ?: 1f).toString()
            ValueDialogType.PositionX -> (normalizedPositionX?.roundTo(3) ?: 0f).toString()
            ValueDialogType.PositionY -> (normalizedPositionY?.roundTo(3) ?: 0f).toString()
            ValueDialogType.None -> "0"
        },
        expanded = activeValueDialogType != ValueDialogType.None,
        onDismiss = { valueDialogType = ValueDialogType.None },
        onValueUpdate = {
            when (activeValueDialogType) {
                ValueDialogType.Rotation -> {
                    onRotationDegreesChange(it)
                    onRotationDegreesChangeFinished()
                }

                ValueDialogType.Scale -> {
                    onScaleChange(it)
                    onScaleChangeFinished()
                }

                ValueDialogType.PositionX -> {
                    onNormalizedPositionXChange(it)
                }

                ValueDialogType.PositionY -> {
                    onNormalizedPositionYChange(it)
                }

                ValueDialogType.None -> Unit
            }
        }
    )
}

@Composable
private fun AdjustableActionCard(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    sliderEnabled: Boolean,
    onValueClick: () -> Unit,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .container(
                shape = ShapeDefaults.extraSmall,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                resultPadding = 0.dp
            )
            .alpha(if (enabled) 1f else 0.5f)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f)
            )
            ValueText(
                value = value,
                onClick = if (enabled) {
                    onValueClick
                } else null,
                modifier = Modifier
            )
        }
        EnhancedSlider(
            value = value,
            enabled = enabled && sliderEnabled,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            drawContainer = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }
}

private enum class AdjustableActionType {
    None, Rotation, Scale
}

private fun AdjustableActionType.toggle(target: AdjustableActionType): AdjustableActionType {
    return if (this == target) AdjustableActionType.None else target
}

private enum class ValueDialogType {
    None, Rotation, Scale, PositionX, PositionY
}
