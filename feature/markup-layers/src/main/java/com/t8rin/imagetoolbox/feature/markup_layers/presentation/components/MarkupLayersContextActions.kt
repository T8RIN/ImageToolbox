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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowLeft
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.outlined.FitScreen
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Deselect
import androidx.compose.material.icons.rounded.ScreenRotationAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Flip
import com.t8rin.imagetoolbox.core.resources.icons.FlipVertical
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
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
    onFlipLayerHorizontally: () -> Unit,
    onFlipLayerVertically: () -> Unit,
    onMoveLayerBy: (Float, Float) -> Unit,
    onResetLayerPosition: () -> Unit,
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    scale: Float?,
    onScaleChange: (Float) -> Unit,
    onScaleChangeFinished: () -> Unit,
    rotationDegrees: Float?,
    onRotationDegreesChange: (Float) -> Unit,
    onRotationDegreesChangeFinished: () -> Unit
) {
    var isRotationAdjusting by rememberSaveable {
        mutableStateOf(false)
    }
    var isScaleAdjusting by rememberSaveable {
        mutableStateOf(false)
    }
    var valueDialogType by rememberSaveable {
        mutableStateOf(ValueDialogType.None)
    }

    EnhancedDropdownMenu(
        expanded = visible,
        onDismissRequest = {
            if (isRotationAdjusting || isScaleAdjusting) {
                isScaleAdjusting = false
                isRotationAdjusting = false
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
                    icon = Icons.Rounded.MiniEdit,
                    text = stringResource(R.string.edit),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onCopyLayer,
                    icon = Icons.Rounded.ContentCopy,
                    text = stringResource(R.string.copy),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onRemoveLayer,
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
                    icon = Icons.Rounded.Deselect,
                    text = stringResource(R.string.clear_selection),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onFlipLayerHorizontally,
                    icon = Icons.Outlined.Flip,
                    text = stringResource(R.string.horizontal_flip),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
                ClickableTile(
                    onClick = onFlipLayerVertically,
                    icon = Icons.Outlined.FlipVertical,
                    text = stringResource(R.string.vertical_flip),
                    modifier = Modifier.size(
                        width = 66.dp,
                        height = 50.dp
                    )
                )
            }

            ClickableTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp),
                onClick = {
                    isRotationAdjusting = !isRotationAdjusting
                },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = BiasAlignment.Horizontal(
                                animateFloatAsState(
                                    if (isRotationAdjusting) -0.5f
                                    else 0f
                                ).value
                            )
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ScreenRotationAlt,
                                    contentDescription = null
                                )
                                AutoSizeText(
                                    text = stringResource(R.string.rotation),
                                    textAlign = TextAlign.Center,
                                    style = LocalTextStyle.current.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 13.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                        AnimatedVisibility(isRotationAdjusting) {
                            ValueText(
                                value = rotationDegrees ?: 0f,
                                onClick = {
                                    isRotationAdjusting = false
                                    isScaleAdjusting = false
                                    onDismiss()
                                    valueDialogType = ValueDialogType.Rotation
                                }
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = isRotationAdjusting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedSlider(
                            value = rotationDegrees ?: 0f,
                            enabled = rotationDegrees != null,
                            onValueChange = onRotationDegreesChange,
                            onValueChangeFinished = onRotationDegreesChangeFinished,
                            valueRange = 0f..360f,
                            drawContainer = false,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            )
            ClickableTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp),
                onClick = {
                    isScaleAdjusting = !isScaleAdjusting
                },
                content = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = BiasAlignment.Horizontal(
                                animateFloatAsState(
                                    if (isScaleAdjusting) -0.5f
                                    else 0f
                                ).value
                            )
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.FitScreen,
                                    contentDescription = null
                                )
                                AutoSizeText(
                                    text = stringResource(R.string.scale),
                                    textAlign = TextAlign.Center,
                                    style = LocalTextStyle.current.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 13.sp
                                    ),
                                    maxLines = 2
                                )
                            }
                        }
                        AnimatedVisibility(isScaleAdjusting) {
                            ValueText(
                                value = scale ?: 1f,
                                onClick = {
                                    isRotationAdjusting = false
                                    isScaleAdjusting = false
                                    onDismiss()
                                    valueDialogType = ValueDialogType.Scale
                                }
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = isScaleAdjusting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedSlider(
                            value = scale ?: 1f,
                            enabled = scale != null,
                            onValueChange = onScaleChange,
                            onValueChangeFinished = onScaleChangeFinished,
                            valueRange = 0.3f..10f,
                            drawContainer = false,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            )

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
                    icon = Icons.AutoMirrored.Rounded.ArrowLeft,
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
                    icon = Icons.AutoMirrored.Rounded.ArrowRight,
                    text = null,
                    containerColor = buttonContainerColor,
                    modifier = Modifier
                        .width(66.dp)
                        .fillMaxHeight()
                )
            }
            if (normalizedPositionX != null && normalizedPositionY != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.container(
                        shape = ShapeDefaults.extraSmall,
                        color = takeColorFromScheme {
                            surfaceContainerLow.blend(tertiaryContainer, 0.3f)
                        }
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        AutoSizeText(
                            text = "X: ${normalizedPositionX.roundTo(3)}   Y: ${
                                normalizedPositionY.roundTo(
                                    3
                                )
                            }",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SupportingButton(
                        icon = Icons.Rounded.CenterFocusStrong,
                        onClick = onResetLayerPosition,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        shape = ShapeDefaults.extremeSmall
                    )
                }
            }
        }
    }

    ValueDialog(
        roundTo = when (valueDialogType) {
            ValueDialogType.Rotation -> null
            ValueDialogType.Scale -> 3
            ValueDialogType.None -> null
        },
        valueRange = when (valueDialogType) {
            ValueDialogType.Rotation -> 0f..360f
            ValueDialogType.Scale -> 0.3f..10f
            ValueDialogType.None -> 0f..1f
        },
        valueState = when (valueDialogType) {
            ValueDialogType.Rotation -> (rotationDegrees ?: 0f).toString()
            ValueDialogType.Scale -> (scale ?: 1f).toString()
            ValueDialogType.None -> "0"
        },
        expanded = valueDialogType != ValueDialogType.None,
        onDismiss = { valueDialogType = ValueDialogType.None },
        onValueUpdate = {
            when (valueDialogType) {
                ValueDialogType.Rotation -> {
                    onRotationDegreesChange(it)
                    onRotationDegreesChangeFinished()
                }

                ValueDialogType.Scale -> {
                    onScaleChange(it)
                    onScaleChangeFinished()
                }

                ValueDialogType.None -> Unit
            }
        }
    )
}

private enum class ValueDialogType {
    None, Rotation, Scale
}