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

package com.t8rin.imagetoolbox.feature.root.presentation.components.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.LabelPercent
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun EditPresetsSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onUpdatePresets: (List<Int>) -> Unit
) {
    val settingsState = LocalSettingsState.current
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = stringResource(R.string.presets),
                icon = Icons.Rounded.LabelPercent
            )
        },
        sheetContent = {
            val data = settingsState.presets
            Box {
                AnimatedContent(
                    targetState = data,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .enhancedVerticalScroll(rememberScrollState())
                ) { list ->
                    var expanded by remember { mutableStateOf(false) }

                    FlowRow(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                    ) {
                        list.forEach {
                            EnhancedChip(
                                onClick = {
                                    onUpdatePresets(list - it)
                                },
                                selected = false,
                                selectedColor = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                AutoSizeText(it.toString())
                            }
                        }
                        EnhancedChip(
                            onClick = {
                                expanded = true
                            },
                            selected = false,
                            selectedColor = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium,
                            label = {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircle,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        )
                    }

                    var value by remember(expanded) { mutableStateOf("") }
                    EnhancedAlertDialog(
                        visible = expanded,
                        onDismissRequest = { expanded = false },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.PhotoSizeSelectSmall,
                                contentDescription = null
                            )
                        },
                        title = {
                            Text(stringResource(R.string.presets))
                        },
                        text = {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                shape = ShapeDefaults.default,
                                value = value,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                onValueChange = {
                                    if (it.isDigitsOnly()) {
                                        value = it.toIntOrNull()?.coerceAtMost(500)?.toString()
                                            ?: ""
                                    }
                                },
                                placeholder = {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(R.string.enter_percent),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            textAlign = TextAlign.Center
                                        ),
                                        color = LocalContentColor.current.copy(0.5f)
                                    )
                                }
                            )
                        },
                        confirmButton = {
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    onUpdatePresets(
                                        list + (value.toIntOrNull() ?: 0)
                                    )
                                    expanded = false
                                },
                                enabled = value.isNotEmpty()
                            ) {
                                Text(stringResource(R.string.add))
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                borderColor = MaterialTheme.colorScheme.outlineVariant(),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}