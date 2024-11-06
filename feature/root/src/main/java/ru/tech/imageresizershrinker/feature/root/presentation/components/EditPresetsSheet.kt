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

package ru.tech.imageresizershrinker.feature.root.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Icon
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
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.LabelPercent
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@OptIn(ExperimentalLayoutApi::class)
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
                        .verticalScroll(rememberScrollState())
                ) { list ->
                    FlowRow(
                        Modifier
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
                        var expanded by remember { mutableStateOf(false) }
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    OutlinedTextField(
                                        shape = RoundedCornerShape(16.dp),
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
                                                value = it
                                            }
                                        }
                                    )
                                    Text(
                                        text = "%",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                EnhancedButton(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = if (settingsState.isNightMode) 0.5f
                                        else 1f
                                    ),
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                                        onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    onClick = {
                                        onUpdatePresets(
                                            list + (value.toIntOrNull() ?: 0)
                                        )
                                        expanded = false
                                    },
                                ) {
                                    Text(stringResource(R.string.add))
                                }
                            }
                        )
                    }
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