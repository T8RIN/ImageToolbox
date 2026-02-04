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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorInfo
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelection
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueDialog
import com.t8rin.imagetoolbox.core.ui.widget.value.ValueText
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ColorStopSelection(
    colorStops: List<Pair<Float, Color>>,
    onRemoveClick: (Int) -> Unit,
    onValueChange: (Int, Pair<Float, Color>) -> Unit,
    onAddColorStop: (Pair<Float, Color>) -> Unit
) {
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    ExpandableItem(
        initialState = true,
        modifier = Modifier.padding(1.dp),
        shape = ShapeDefaults.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        visibleContent = {
            TitleItem(text = stringResource(R.string.color_stops))
        },
        expandableContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                colorStops.forEachIndexed { index, (value, color) ->
                    ColorStopSelectionItem(
                        value = value,
                        color = color,
                        onRemoveClick = {
                            onRemoveClick(index)
                        },
                        onValueChange = {
                            onValueChange(index, it)
                        },
                        canDelete = colorStops.size > 2
                    )
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.mixedContainer,
                    onClick = {
                        showColorPicker = true
                    },
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.add_color))
                }
            }
        }
    )

    var color by rememberSaveable(stateSaver = ColorSaver) {
        mutableStateOf(Color.Red)
    }
    EnhancedModalBottomSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .enhancedVerticalScroll(rememberScrollState())
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp, bottom = 24.dp)
                ) {
                    ColorSelection(
                        value = color,
                        onValueChange = { color = it },
                        withAlpha = true
                    )
                }
            }
        },
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.Palette
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onAddColorStop(1f to color)
                    showColorPicker = false
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
private fun ColorStopSelectionItem(
    value: Float,
    color: Color,
    onRemoveClick: () -> Unit,
    onValueChange: (Pair<Float, Color>) -> Unit,
    canDelete: Boolean
) {
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val state = rememberRevealState()
    SwipeToReveal(
        state = state,
        enableSwipe = canDelete,
        revealedContentEnd = {
            Box(
                Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.large,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch {
                            state.animateTo(RevealValue.Default)
                        }
                        onRemoveClick()
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        directions = setOf(RevealDirection.EndToStart),
        swipeableContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 8.dp
                    )
                    .then(
                        if (canDelete) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        val time = System.currentTimeMillis()
                                        awaitRelease()
                                        if (System.currentTimeMillis() - time >= 200) {
                                            scope.launch {
                                                state.animateTo(RevealValue.FullyRevealedStart)
                                            }
                                        }
                                    }
                                )
                            }
                        } else Modifier
                    )
            ) {
                ColorInfo(
                    color = color,
                    onColorChange = {
                        onValueChange(value to it)
                    },
                    supportButtonIcon = Icons.Rounded.MiniEdit,
                    onSupportButtonClick = {
                        showColorPicker = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnhancedSlider(
                        value = value,
                        onValueChange = { onValueChange(it to color) },
                        valueRange = 0f..1f,
                        modifier = Modifier.weight(1f)
                    )
                    var showValueDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    ValueText(
                        modifier = Modifier,
                        value = (value * 100).toInt(),
                        onClick = {
                            showValueDialog = true
                        }
                    )
                    ValueDialog(
                        roundTo = 0,
                        valueRange = 0f..100f,
                        valueState = (value * 100).toInt().toString(),
                        expanded = showValueDialog,
                        onDismiss = {
                            showValueDialog = false
                        },
                        onValueUpdate = {
                            onValueChange(it.roundToInt() / 100f to color)
                            showValueDialog = false
                        }
                    )
                }
            }
        }
    )

    EnhancedModalBottomSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .enhancedVerticalScroll(rememberScrollState())
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp, bottom = 24.dp)
                ) {
                    ColorSelection(
                        value = color,
                        onValueChange = {
                            onValueChange(value to it)
                        },
                        withAlpha = true
                    )
                }
            }
        },
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.Palette
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showColorPicker = false
                }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}