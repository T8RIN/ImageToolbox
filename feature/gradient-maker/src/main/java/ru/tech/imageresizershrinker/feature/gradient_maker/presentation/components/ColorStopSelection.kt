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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.MiniEdit
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.AlphaColorSelection
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorInfo
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.other.RevealDirection
import ru.tech.imageresizershrinker.core.ui.widget.other.RevealValue
import ru.tech.imageresizershrinker.core.ui.widget.other.SwipeToReveal
import ru.tech.imageresizershrinker.core.ui.widget.other.rememberRevealState
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueDialog
import ru.tech.imageresizershrinker.core.ui.widget.value.ValueText
import kotlin.math.roundToInt

@Composable
fun ColorStopSelection(
    colorStops: List<Pair<Float, Color>>,
    onRemoveClick: (Int) -> Unit,
    onValueChange: (Int, Pair<Float, Color>) -> Unit,
    onAddColorStop: (Pair<Float, Color>) -> Unit
) {
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressed by interactionSource.collectIsPressedAsState()

    val cornerSize by animateDpAsState(
        if (pressed) 8.dp
        else 24.dp
    )

    ExpandableItem(
        initialState = true,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(cornerSize),
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
    SimpleSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp, bottom = 24.dp)
                ) {
                    AlphaColorSelection(
                        color = color.toArgb(),
                        onColorChange = {
                            color = Color(it)
                        }
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
                icon = Icons.Rounded.Draw
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

@OptIn(ExperimentalMaterialApi::class)
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
                    .clickable {
                        scope.launch {
                            state.animateTo(RevealValue.Default)
                        }
                        onRemoveClick()
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = null,
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
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
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
                    color = color.toArgb(),
                    onColorChange = {
                        onValueChange(value to Color(it))
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

    SimpleSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp, bottom = 24.dp)
                ) {
                    AlphaColorSelection(
                        color = color.toArgb(),
                        onColorChange = {
                            onValueChange(value to Color(it))
                        }
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