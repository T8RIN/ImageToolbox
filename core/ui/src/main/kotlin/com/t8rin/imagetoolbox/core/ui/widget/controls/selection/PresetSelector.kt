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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitScreen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.Telegram
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalEditPresetsController
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.OOMWarning
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.AspectRatioSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextFieldColors
import kotlinx.coroutines.launch


@Composable
fun PresetSelector(
    value: Preset,
    includeTelegramOption: Boolean = false,
    includeAspectRatioOption: Boolean = false,
    isBytesResize: Boolean = false,
    showWarning: Boolean = false,
    onValueChange: (Preset) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val editPresetsController = LocalEditPresetsController.current
    val data by remember(settingsState.presets, value) {
        derivedStateOf {
            settingsState.presets.let {
                val currentValue = value.value()
                if (currentValue !in it && !value.isTelegram() && currentValue != null) {
                    listOf(currentValue) + it
                } else it
            }
        }
    }

    val state = rememberRevealState()
    val scope = rememberCoroutineScope()

    var showPresetInfoDialog by remember { mutableStateOf(false) }

    val canEnterPresetsByTextField = settingsState.canEnterPresetsByTextField

    SwipeToReveal(
        directions = setOf(
            RevealDirection.EndToStart
        ),
        maxRevealDp = 88.dp,
        state = state,
        swipeableContent = {
            Column(
                modifier = Modifier
                    .container(shape = ShapeDefaults.extraLarge)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                scope.launch {
                                    state.animateTo(RevealValue.FullyRevealedStart)
                                }
                            },
                            onDoubleTap = {
                                scope.launch {
                                    state.animateTo(RevealValue.FullyRevealedStart)
                                }
                            }
                        )
                    }
                    .animateContentSizeNoClip(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.presets),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SupportingButton(
                        onClick = {
                            showPresetInfoDialog = true
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(visible = value is Preset.AspectRatio && includeAspectRatioOption) {
                    val aspectRatios = remember {
                        DomainAspectRatio.defaultList.drop(3)
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        AspectRatioSelector(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(8.dp),
                            selectedAspectRatio = remember(value, aspectRatios) {
                                derivedStateOf {
                                    aspectRatios.firstOrNull {
                                        it.value == (value as? Preset.AspectRatio)?.ratio
                                    }
                                }
                            }.value,
                            onAspectRatioChange = { domainAspectRatio, _ ->
                                if (value is Preset.AspectRatio) {
                                    onValueChange(
                                        value.copy(ratio = domainAspectRatio.value)
                                    )
                                } else {
                                    onValueChange(
                                        Preset.AspectRatio(
                                            ratio = domainAspectRatio.value,
                                            isFit = false
                                        )
                                    )
                                }
                            },
                            title = {},
                            aspectRatios = aspectRatios,
                            shape = ShapeDefaults.top,
                            color = MaterialTheme.colorScheme.surface,
                            unselectedCardColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PreferenceRowSwitch(
                            modifier = Modifier.fillMaxWidth(),
                            title = stringResource(R.string.fit_to_bounds),
                            subtitle = stringResource(R.string.fit_to_bounds_sub),
                            checked = (value as? Preset.AspectRatio)?.isFit == true,
                            onClick = {
                                if (value is Preset.AspectRatio) {
                                    onValueChange(
                                        value.copy(isFit = it)
                                    )
                                } else {
                                    onValueChange(
                                        Preset.AspectRatio(
                                            ratio = 1f,
                                            isFit = it
                                        )
                                    )
                                }
                            },
                            startIcon = Icons.Outlined.FitScreen,
                            shape = ShapeDefaults.bottom,
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val listState = rememberLazyListState()
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fadingEdges(listState)
                            .padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp, Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        if (includeTelegramOption) {
                            item(key = "tg") {
                                val selected = value.isTelegram()
                                EnhancedChip(
                                    selected = selected,
                                    onClick = { onValueChange(Preset.Telegram) },
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Telegram,
                                        contentDescription = stringResource(R.string.telegram)
                                    )
                                }
                            }
                        }
                        if (includeAspectRatioOption) {
                            item(key = "aspect") {
                                val selected = value.isAspectRatio()
                                EnhancedChip(
                                    selected = selected,
                                    onClick = {
                                        onValueChange(
                                            Preset.AspectRatio(
                                                ratio = 1f,
                                                isFit = false
                                            )
                                        )
                                    },
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.AspectRatio,
                                        contentDescription = stringResource(R.string.aspect_ratio)
                                    )
                                }
                            }
                        }
                        items(
                            items = data,
                            key = { it }
                        ) {
                            val selected = value.value() == it
                            EnhancedChip(
                                selected = selected,
                                onClick = { onValueChange(Preset.Percentage(it)) },
                                selectedColor = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                AutoSizeText(it.toString())
                            }
                        }
                    }
                }
                AnimatedVisibility(canEnterPresetsByTextField) {
                    var textValue by remember(value) {
                        mutableStateOf(
                            value.value()?.toString() ?: ""
                        )
                    }
                    RoundedTextField(
                        onValueChange = { targetText ->
                            if (targetText.isEmpty()) {
                                textValue = ""
                                onValueChange(Preset.None)
                            } else {
                                val newValue = targetText.filter {
                                    it.isDigit()
                                }.toIntOrNull()?.coerceIn(0, 500)
                                textValue = newValue?.toString() ?: ""

                                newValue?.let {
                                    onValueChange(
                                        Preset.Percentage(it)
                                    )
                                } ?: onValueChange(Preset.None)
                            }
                        },
                        value = textValue,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        label = stringResource(R.string.enter_percentage),
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                        colors = RoundedTextFieldColors(
                            isError = false,
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary
                        ).let {
                            it.copy(
                                unfocusedIndicatorColor = it.unfocusedIndicatorColor.copy(0.5f)
                                    .compositeOver(
                                        it.unfocusedContainerColor
                                    )
                            )
                        }
                    )
                }

                OOMWarning(visible = showWarning)
            }
        },
        revealedContentEnd = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = ShapeDefaults.extraLarge,
                        autoShadowElevation = 0.5.dp
                    )
            ) {
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = editPresetsController::open,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EditAlt,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
        }
    )

    EnhancedAlertDialog(
        visible = showPresetInfoDialog,
        onDismissRequest = { showPresetInfoDialog = false },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showPresetInfoDialog = false }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        title = {
            Text(stringResource(R.string.presets))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.about_app)
            )
        },
        text = {
            if (isBytesResize) Text(stringResource(R.string.presets_sub_bytes))
            else Text(stringResource(R.string.presets_sub))
        }
    )
}