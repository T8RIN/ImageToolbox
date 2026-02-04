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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberColorScheme
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.nearestFor
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.Theme
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.model.defaultColorTuple
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedHorizontalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.PaletteStyleSelection
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay

@Composable
fun AvailableColorTuplesSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    colorTupleList: List<ColorTuple>,
    currentColorTuple: ColorTuple,
    onOpenColorPicker: () -> Unit,
    colorPicker: @Composable () -> Unit,
    onPickTheme: (ColorTuple) -> Unit,
    onUpdateThemeContrast: (Float) -> Unit,
    onThemeStyleSelected: (PaletteStyle) -> Unit,
    onToggleInvertColors: () -> Unit,
    onToggleUseEmojiAsPrimaryColor: () -> Unit,
    onUpdateColorTuples: (List<ColorTuple>) -> Unit,
) {
    var showEditColorPicker by rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        dragHandle = {
            EnhancedModalSheetDragHandle {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleItem(
                        text = stringResource(R.string.color_scheme),
                        icon = Icons.Outlined.Theme
                    )
                }
            }
        },
        title = {
            var showConfirmDeleteDialog by remember { mutableStateOf(false) }

            EnhancedAlertDialog(
                visible = showConfirmDeleteDialog,
                onDismissRequest = { showConfirmDeleteDialog = false },
                confirmButton = {
                    EnhancedButton(
                        onClick = { showConfirmDeleteDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                dismissButton = {
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {
                            showConfirmDeleteDialog = false
                            if ((colorTupleList - currentColorTuple).isEmpty()) {
                                onPickTheme(defaultColorTuple)
                            } else {
                                colorTupleList.nearestFor(currentColorTuple)
                                    ?.let { onPickTheme(it) }
                            }
                            onUpdateColorTuples(colorTupleList - currentColorTuple)
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                title = {
                    Text(stringResource(R.string.delete_color_scheme_title))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ColorTupleItem(
                            colorTuple = currentColorTuple,
                            modifier = Modifier
                                .padding(2.dp)
                                .size(64.dp)
                                .container(
                                    shape = MaterialStarShape,
                                    color = rememberColorScheme(
                                        isDarkTheme = settingsState.isNightMode,
                                        amoledMode = settingsState.isAmoledMode,
                                        colorTuple = currentColorTuple,
                                        contrastLevel = settingsState.themeContrastLevel,
                                        style = settingsState.themeStyle,
                                        dynamicColor = false,
                                        isInvertColors = settingsState.isInvertThemeColors
                                    ).surfaceVariant.copy(alpha = 0.8f),
                                    borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                    resultPadding = 0.dp
                                )
                                .padding(3.dp)
                                .clip(ShapeDefaults.circle),
                            backgroundColor = Color.Transparent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.delete_color_scheme_warn))
                    }
                }
            )
            Row {
                AnimatedVisibility(
                    visible = currentColorTuple !in ColorTupleDefaults.defaultColorTuples && !settingsState.useEmojiAsPrimaryColor
                ) {
                    Row {
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            onClick = {
                                showConfirmDeleteDialog = true
                            },
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                onTopOf = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showEditColorPicker = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EditAlt,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
        },
        sheetContent = {
            val isPortrait by isPortraitOrientationAsState()

            val isPickersEnabled = !settingsState.useEmojiAsPrimaryColor

            val palette = @Composable {
                PaletteStyleSelection(
                    onThemeStyleSelected = onThemeStyleSelected,
                    shape = ShapeDefaults.top,
                )
            }
            val invertColors = @Composable {
                PreferenceRowSwitch(
                    title = stringResource(R.string.invert_colors),
                    subtitle = stringResource(R.string.invert_colors_sub),
                    checked = settingsState.isInvertThemeColors,
                    modifier = Modifier,
                    startIcon = Icons.Rounded.InvertColors,
                    shape = ShapeDefaults.center,
                    onClick = { onToggleInvertColors() }
                )
            }
            val emojiAsPrimary = @Composable {
                PreferenceRowSwitch(
                    title = stringResource(R.string.emoji_as_color_scheme),
                    subtitle = stringResource(R.string.emoji_as_color_scheme_sub),
                    checked = settingsState.useEmojiAsPrimaryColor,
                    modifier = Modifier,
                    startIcon = Icons.Outlined.EmojiEmotions,
                    shape = ShapeDefaults.center,
                    onClick = { onToggleUseEmojiAsPrimaryColor() }
                )
            }
            val contrast = @Composable {
                EnhancedSliderItem(
                    value = settingsState.themeContrastLevel.toFloat().roundToTwoDigits(),
                    icon = Icons.Rounded.Contrast,
                    title = stringResource(id = R.string.contrast),
                    valueRange = -1f..1f,
                    shape = ShapeDefaults.bottom,
                    onValueChange = { },
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    steps = 198,
                    onValueChangeFinished = {
                        onUpdateThemeContrast(it)
                    },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            val defaultValues = @Composable {
                val listState = rememberScrollState()
                val defList = ColorTupleDefaults.defaultColorTuples
                val density = LocalDensity.current
                val cellSize = 60.dp
                LaunchedEffect(visible, isPickersEnabled) {
                    delay(100) // delay for sheet init
                    if (currentColorTuple in defList) {
                        listState.scrollTo(defList.indexOf(currentColorTuple) * with(density) { cellSize.roundToPx() })
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .container(ShapeDefaults.extraLarge)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            fontWeight = FontWeight.Medium,
                            text = stringResource(R.string.simple_variants),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp),
                            fontSize = 18.sp
                        )
                    }
                    Box {
                        Row(
                            modifier = Modifier
                                .enhancedHorizontalScroll(listState)
                                .padding(PaddingValues(16.dp)),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            defList.forEach { colorTuple ->
                                ColorTuplePreview(
                                    isDefaultItem = true,
                                    modifier = Modifier.size(cellSize),
                                    colorTuple = colorTuple,
                                    appColorTuple = currentColorTuple,
                                    onClick = { onPickTheme(colorTuple) }
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .width(8.dp)
                                .height(64.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        0f to MaterialTheme.colorScheme.surfaceContainer,
                                        1f to Color.Transparent
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .width(8.dp)
                                .height(64.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        0f to Color.Transparent,
                                        1f to MaterialTheme.colorScheme.surfaceContainer
                                    )
                                )
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isPortrait) {
                    Column(
                        modifier = Modifier
                            .enhancedVerticalScroll(rememberScrollState())
                            .weight(0.8f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        palette()
                        invertColors()
                        emojiAsPrimary()
                        contrast()
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier.weight(1f),
                    columns = GridCells.Adaptive(64.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    if (isPortrait) {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            palette()
                        }
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            invertColors()
                        }
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            emojiAsPrimary()
                        }
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            contrast()
                        }
                    }
                    item(
                        span = { GridItemSpan(maxLineSpan) }
                    ) {
                        DisableContainer(isPickersEnabled, defaultValues)
                    }
                    items(colorTupleList) { colorTuple ->
                        DisableContainer(isPickersEnabled) {
                            ColorTuplePreview(
                                colorTuple = colorTuple,
                                appColorTuple = currentColorTuple,
                                onClick = { onPickTheme(colorTuple) }
                            )
                        }
                    }
                    item {
                        DisableContainer(isPickersEnabled) {
                            ColorTupleItem(
                                colorTuple = ColorTuple(
                                    primary = MaterialTheme.colorScheme.secondary,
                                    secondary = MaterialTheme.colorScheme.secondary,
                                    tertiary = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .container(
                                        shape = MaterialStarShape,
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                        resultPadding = 0.dp
                                    )
                                    .hapticsClickable(onClick = onOpenColorPicker)
                                    .padding(3.dp)
                                    .clip(ShapeDefaults.circle),
                                backgroundColor = Color.Transparent
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircleOutline,
                                    contentDescription = stringResource(R.string.add),
                                    tint = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
    ColorTuplePicker(
        visible = showEditColorPicker,
        onDismiss = {
            showEditColorPicker = false
        },
        colorTuple = currentColorTuple,
        onColorChange = {
            onUpdateColorTuples(colorTupleList + it - currentColorTuple)
            onPickTheme(it)
        }
    )
    colorPicker()

    if (settingsState.isDynamicColors) onDismiss()
}

@Composable
private fun DisableContainer(
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.alpha(
            animateFloatAsState(
                if (enabled) 1f else 0.5f
            ).value
        )
    ) {
        content()
        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.matchParentSize()
            ) {}
        }
    }
}
