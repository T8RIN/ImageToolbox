/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
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
    rotationDegrees: Float?,
    onRotationDegreesChange: (Float) -> Unit
) {
    var isRotationAdjusting by rememberSaveable {
        mutableStateOf(false)
    }
    var showValueDialog by rememberSaveable { mutableStateOf(false) }

    EnhancedDropdownMenu(
        expanded = visible,
        onDismissRequest = {
            if (isRotationAdjusting) isRotationAdjusting = false
            else onDismiss()
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
                    text = stringResource(R.string.edit)
                )
                ClickableTile(
                    onClick = onCopyLayer,
                    icon = Icons.Rounded.ContentCopy,
                    text = stringResource(R.string.copy)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ClickableTile(
                    onClick = onRemoveLayer,
                    icon = Icons.Rounded.Delete,
                    text = stringResource(R.string.delete)
                )
                ClickableTile(
                    onClick = onActivateLayer,
                    icon = Icons.Rounded.Deselect,
                    text = stringResource(R.string.clear_selection)
                )
            }

            ClickableTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 84.dp),
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
                                    onDismiss()
                                    showValueDialog = true
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
                            valueRange = 0f..360f,
                            drawContainer = false,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            )
        }
    }

    ValueDialog(
        roundTo = null,
        valueRange = 0f..360f,
        valueState = (rotationDegrees ?: 0f).toString(),
        expanded = showValueDialog,
        onDismiss = { showValueDialog = false },
        onValueUpdate = {
            onRotationDegreesChange(it)
        }
    )
}