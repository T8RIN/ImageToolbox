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

package ru.tech.imageresizershrinker.feature.generate_palette.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.ImageColorPalette
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.extractPrimaryColor
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.resources.icons.PaletteSwatch
import ru.tech.imageresizershrinker.core.resources.icons.Swatch
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.copyToClipboard
import ru.tech.imageresizershrinker.core.ui.utils.helper.toHex
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorInfo
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelection
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.icon_shape.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.palette_selection.getTitle
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
internal fun GeneratePaletteScreenControls(
    bitmap: Bitmap,
    useMaterialYouPalette: Boolean?,
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    AnimatedContent(
        targetState = useMaterialYouPalette == true
    ) { isMaterialYou ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isMaterialYou) {
                var showColorPicker by rememberSaveable { mutableStateOf(false) }

                var paletteStyle by rememberSaveable {
                    mutableStateOf(PaletteStyle.TonalSpot)
                }
                var isDarkTheme by rememberSaveable {
                    mutableStateOf(settingsState.isNightMode)
                }
                var invertColors by rememberSaveable {
                    mutableStateOf(false)
                }
                var contrast by rememberSaveable {
                    mutableFloatStateOf(0f)
                }
                var keyColor by rememberSaveable(bitmap, stateSaver = ColorSaver) {
                    mutableStateOf(bitmap.extractPrimaryColor())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColorInfo(
                        color = keyColor.toArgb(),
                        onColorChange = {
                            keyColor = it.toColor()
                        },
                        supportButtonIcon = Icons.Rounded.MiniEdit,
                        onSupportButtonClick = {
                            showColorPicker = true
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Picture(
                        model = bitmap,
                        shape = RectangleShape,
                        modifier = Modifier
                            .size(56.dp)
                            .container(
                                shape = RoundedCornerShape(8.dp),
                                resultPadding = 0.dp
                            )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                MaterialYouPalette(
                    keyColor = keyColor,
                    paletteStyle = paletteStyle,
                    isDarkTheme = isDarkTheme,
                    isInvertColors = invertColors,
                    contrastLevel = contrast
                )
                Spacer(modifier = Modifier.height(16.dp))
                PreferenceRowSwitch(
                    title = stringResource(R.string.dark_colors),
                    subtitle = stringResource(R.string.dark_colors_sub),
                    checked = isDarkTheme,
                    startIcon = Icons.Rounded.DarkMode,
                    onClick = {
                        isDarkTheme = it
                    },
                    color = Color.Unspecified,
                    shape = ContainerShapeDefaults.topShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                PreferenceRowSwitch(
                    title = stringResource(R.string.invert_colors),
                    subtitle = stringResource(R.string.invert_colors_sub),
                    checked = invertColors,
                    startIcon = Icons.Rounded.InvertColors,
                    onClick = {
                        invertColors = it
                    },
                    color = Color.Unspecified,
                    shape = ContainerShapeDefaults.centerShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                EnhancedSliderItem(
                    color = Color.Unspecified,
                    value = contrast.roundToTwoDigits(),
                    icon = Icons.Rounded.Contrast,
                    title = stringResource(id = R.string.contrast),
                    valueRange = -1f..1f,
                    shape = ContainerShapeDefaults.centerShape,
                    onValueChange = { },
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    steps = 198,
                    onValueChangeFinished = {
                        contrast = it
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.container(
                        shape = ContainerShapeDefaults.bottomShape
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconShapeContainer(
                            enabled = true,
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Swatch,
                                contentDescription = null
                            )
                        }
                        Text(
                            fontWeight = FontWeight.Medium,
                            text = stringResource(R.string.palette_style),
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val listState = rememberLazyListState()
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fadingEdges(listState),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(PaletteStyle.entries) {
                            EnhancedChip(
                                selected = it == paletteStyle,
                                onClick = { paletteStyle = it },
                                selectedColor = MaterialTheme.colorScheme.secondary,
                                contentPadding = PaddingValues(
                                    horizontal = 12.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Text(it.getTitle(context))
                            }
                        }
                    }
                }

                EnhancedModalBottomSheet(
                    sheetContent = {
                        Box {
                            Column(
                                Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(
                                        start = 36.dp,
                                        top = 36.dp,
                                        end = 36.dp,
                                        bottom = 24.dp
                                    )
                            ) {
                                ColorSelection(
                                    color = keyColor.toArgb(),
                                    onColorChange = {
                                        keyColor = it.toColor()
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
            } else {
                var count by rememberSaveable { mutableIntStateOf(32) }
                PaletteColorsCountSelector(
                    value = count,
                    onValueChange = { count = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ImageColorPalette(
                    borderWidth = settingsState.borderWidth,
                    imageBitmap = bitmap.asImageBitmap(),
                    modifier = Modifier
                        .fillMaxSize()
                        .container(RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    style = LocalTextStyle.current,
                    onEmpty = {
                        Column(
                            Modifier.container(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(16.dp))
                            FilledIconButton(
                                enabled = false,
                                onClick = {},
                                modifier = Modifier.size(100.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.7f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.7f
                                    ),
                                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PaletteSwatch,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                stringResource(R.string.no_palette),
                                Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    maximumColorCount = count,
                    onColorChange = {
                        context.copyToClipboard(it.color.toHex())
                        scope.launch {
                            toastHostState.showToast(
                                icon = Icons.Rounded.ContentPaste,
                                message = context.getString(R.string.color_copied)
                            )
                        }
                    }
                )
            }
        }
    }
}