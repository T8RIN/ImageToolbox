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

package ru.tech.imageresizershrinker.core.ui.widget.color_picker

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.defaultColorTuple
import ru.tech.imageresizershrinker.core.ui.icons.material.EditAlt
import ru.tech.imageresizershrinker.core.ui.icons.material.Theme
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ListUtils.nearestFor
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.palette_selection.PaletteStyleSelection
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun AvailableColorTuplesSheet(
    visible: MutableState<Boolean>,
    colorTupleList: List<ColorTuple>,
    currentColorTuple: ColorTuple,
    openColorPicker: () -> Unit,
    colorPicker: @Composable (onUpdateColorTuples: (List<ColorTuple>) -> Unit) -> Unit,
    onPickTheme: (ColorTuple) -> Unit,
    updateThemeContrast: (Float) -> Unit,
    onThemeStyleSelected: (PaletteStyle) -> Unit,
    onToggleInvertColors: () -> Unit,
    onToggleUseEmojiAsPrimaryColor: () -> Unit,
    onUpdateColorTuples: (List<ColorTuple>) -> Unit,
) {
    val showEditColorPicker = rememberSaveable { mutableStateOf(false) }

    val settingsState = LocalSettingsState.current
    SimpleSheet(
        visible = visible,
        endConfirmButtonPadding = 0.dp,
        dragHandle = {
            SimpleDragHandle {
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

            if (showConfirmDeleteDialog) {
                AlertDialog(
                    modifier = Modifier.alertDialogBorder(),
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
                            contentDescription = null
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
                                            amoledMode = settingsState.isDynamicColors,
                                            colorTuple = currentColorTuple,
                                            contrastLevel = settingsState.themeContrastLevel,
                                            style = settingsState.themeStyle,
                                            dynamicColor = settingsState.isDynamicColors,
                                            isInvertColors = settingsState.isInvertThemeColors
                                        ).surfaceVariant.copy(alpha = 0.8f),
                                        borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                                        resultPadding = 0.dp
                                    )
                                    .padding(3.dp),
                                backgroundColor = Color.Transparent
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(R.string.delete_color_scheme_warn))
                        }
                    }
                )
            }
            Row {
                AnimatedVisibility(visible = currentColorTuple !in ColorTupleDefaults.defaultColorTuples) {
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
                            Icon(Icons.Rounded.Delete, null)
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showEditColorPicker.value = true
                    }
                ) {
                    Icon(Icons.Rounded.EditAlt, null)
                }
            }
        },
        sheetContent = {
            val portrait =
                LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

            val isPickersEnabled = !settingsState.useEmojiAsPrimaryColor

            val palette = @Composable {
                PaletteStyleSelection(
                    onThemeStyleSelected = onThemeStyleSelected,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
            }
            val invertColors = @Composable {
                PreferenceRowSwitch(
                    title = stringResource(R.string.invert_colors),
                    subtitle = stringResource(R.string.invert_colors_sub),
                    checked = settingsState.isInvertThemeColors,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier,
                    startIcon = Icons.Rounded.InvertColors,
                    shape = RoundedCornerShape(4.dp),
                    onClick = { onToggleInvertColors() }
                )
            }
            val emojiAsPrimary = @Composable {
                PreferenceRowSwitch(
                    title = stringResource(R.string.emoji_as_color_scheme),
                    subtitle = stringResource(R.string.emoji_as_color_scheme_sub),
                    checked = settingsState.useEmojiAsPrimaryColor,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier,
                    startIcon = Icons.Outlined.EmojiEmotions,
                    shape = RoundedCornerShape(4.dp),
                    onClick = { onToggleUseEmojiAsPrimaryColor() }
                )
            }
            val contrast = @Composable {
                EnhancedSliderItem(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    value = settingsState.themeContrastLevel.toFloat().roundToTwoDigits(),
                    icon = Icons.Rounded.Contrast,
                    title = stringResource(id = R.string.contrast),
                    valueRange = -1f..1f,
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp,
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    ),
                    onValueChange = { },
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    steps = 198,
                    onValueChangeFinished = {
                        updateThemeContrast(it)
                    },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            val defaultValues = @Composable {
                val listState = rememberLazyListState()
                val defList = ColorTupleDefaults.defaultColorTuples
                LaunchedEffect(visible.value, isPickersEnabled) {
                    delay(100) // delay for sheet init
                    if (currentColorTuple in defList) {
                        listState.animateScrollToItem(defList.indexOf(currentColorTuple))
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .container(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
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
                        LazyRow(
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(defList) { colorTuple ->
                                ColorTuplePreview(
                                    isDefaultItem = true,
                                    modifier = Modifier.size(60.dp),
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
                                        0f to MaterialTheme.colorScheme.surfaceContainerHigh,
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
                                        1f to MaterialTheme.colorScheme.surfaceContainerHigh
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
                if (!portrait) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
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
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                ) {
                    if (portrait) {
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
                                    .clickable { openColorPicker() }
                                    .padding(3.dp),
                                backgroundColor = Color.Transparent
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircleOutline,
                                    contentDescription = null,
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
                onClick = {
                    visible.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
    )
    ColorTuplePicker(
        visible = showEditColorPicker,
        colorTuple = currentColorTuple,
        onColorChange = {
            onUpdateColorTuples(colorTupleList + it - currentColorTuple)
            onPickTheme(it)
        }
    )
    colorPicker(onUpdateColorTuples)

    if (settingsState.isDynamicColors) {
        visible.value = false
    }
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
