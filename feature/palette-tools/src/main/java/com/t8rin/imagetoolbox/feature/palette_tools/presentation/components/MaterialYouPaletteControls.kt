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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Swatch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorInfo
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelection
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.IconShapeContainer
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.getTitle
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun MaterialYouPaletteControls(bitmap: Bitmap) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current

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
            color = keyColor,
            onColorChange = { keyColor = it },
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
                    shape = ShapeDefaults.mini,
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
        containerColor = Color.Unspecified,
        shape = ShapeDefaults.top
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
        containerColor = Color.Unspecified,
        shape = ShapeDefaults.center
    )
    Spacer(modifier = Modifier.height(4.dp))
    EnhancedSliderItem(
        containerColor = Color.Unspecified,
        value = contrast.roundToTwoDigits(),
        icon = Icons.Rounded.Contrast,
        title = stringResource(id = R.string.contrast),
        valueRange = -1f..1f,
        shape = ShapeDefaults.center,
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
            shape = ShapeDefaults.bottom
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
            contentPadding = PaddingValues(8.dp),
            flingBehavior = enhancedFlingBehavior()
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
                        .enhancedVerticalScroll(rememberScrollState())
                        .padding(
                            start = 36.dp,
                            top = 36.dp,
                            end = 36.dp,
                            bottom = 24.dp
                        )
                ) {
                    ColorSelection(
                        value = keyColor,
                        onValueChange = { keyColor = it }
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