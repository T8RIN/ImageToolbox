package ru.tech.imageresizershrinker.presentation.main_screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.unit.max
import androidx.core.text.isDigitsOnly
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.utils.modifier.alertDialog
import ru.tech.imageresizershrinker.presentation.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.widget.utils.LocalSettingsState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditPresetsSheet(
    editPresetsState: MutableState<Boolean>,
    updatePresets: (List<Int>) -> Unit
) {
    val settingsState = LocalSettingsState.current
    SimpleSheet(
        visible = editPresetsState,
        title = {
            TitleItem(
                text = stringResource(R.string.presets),
                icon = Icons.Rounded.PhotoSizeSelectSmall
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
                            .padding(top = 8.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        list.forEach {
                            OutlinedIconButton(
                                onClick = {
                                    if (list.size > 7) {
                                        updatePresets(list - it)
                                    }
                                },
                                border = BorderStroke(
                                    settingsState.borderWidth.coerceAtLeast(1.dp),
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                colors = IconButtonDefaults.outlinedIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                        alpha = 0.3f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                AutoSizeText(it.toString())
                            }
                        }
                        var expanded by remember { mutableStateOf(false) }
                        OutlinedIconButton(
                            onClick = {
                                expanded = true
                            },
                            border = BorderStroke(
                                max(
                                    settingsState.borderWidth,
                                    1.dp
                                ),
                                MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = IconButtonDefaults.outlinedIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                    alpha = 0.3f
                                ),
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(Icons.Rounded.AddCircle, null)
                        }
                        if (expanded) {
                            var value by remember { mutableStateOf("") }
                            AlertDialog(
                                modifier = Modifier.alertDialog(),
                                onDismissRequest = { expanded = false },
                                icon = {
                                    Icon(
                                        Icons.Outlined.PhotoSizeSelectSmall,
                                        null
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
                                    OutlinedButton(
                                        onClick = {
                                            updatePresets(
                                                list + (value.toIntOrNull()
                                                    ?: 0).coerceIn(10..500)
                                            )
                                            expanded = false
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = if (settingsState.isNightMode) 0.5f
                                                else 1f
                                            ),
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                        border = BorderStroke(
                                            settingsState.borderWidth,
                                            MaterialTheme.colorScheme.outlineVariant(
                                                onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        ),
                                    ) {
                                        Text(stringResource(R.string.add))
                                    }
                                }
                            )
                        }
                    }
                }
                Divider(Modifier.align(Alignment.TopCenter))
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = { editPresetsState.value = false },
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant()
                )
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}