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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Theme
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.AvailableColorTuplesSheet
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorTuplePicker
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow

@Composable
fun ColorSchemeSettingItem(
    onToggleInvertColors: () -> Unit,
    onSetThemeStyle: (Int) -> Unit,
    onUpdateThemeContrast: (Float) -> Unit,
    onUpdateColorTuple: (ColorTuple) -> Unit,
    onUpdateColorTuples: (List<ColorTuple>) -> Unit,
    onToggleUseEmojiAsPrimaryColor: () -> Unit,
    shape: Shape = ShapeDefaults.top,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    val essentials = rememberLocalEssentials()
    val settingsState = LocalSettingsState.current
    val enabled = !settingsState.isDynamicColors

    var showPickColorSheet by rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        title = stringResource(R.string.color_scheme),
        startIcon = Icons.Outlined.Theme,
        subtitle = stringResource(R.string.pick_accent_color),
        onClick = {
            showPickColorSheet = true
        },
        onDisabledClick = {
            essentials.showToast(
                icon = Icons.Rounded.Palette,
                message = essentials.getString(R.string.cannot_change_palette_while_dynamic_colors_applied)
            )
        },
        endContent = {
            val colorTuple by remember(
                settingsState.themeStyle,
                settingsState.appColorTuple
            ) {
                derivedStateOf {
                    if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                        settingsState.appColorTuple
                    } else settingsState.appColorTuple.run {
                        copy(secondary = primary, tertiary = primary)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(72.dp)
                    .container(
                        shape = MaterialStarShape,
                        color = MaterialTheme.colorScheme
                            .surfaceVariant
                            .copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.outlineVariant(
                            0.2f
                        ),
                        resultPadding = 5.dp
                    )
            ) {
                ColorTupleItem(
                    modifier = Modifier
                        .clip(ShapeDefaults.circle),
                    colorTuple = colorTuple,
                    backgroundColor = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = animateColorAsState(
                                    settingsState.appColorTuple.primary.inverse(
                                        fraction = {
                                            if (it) 0.8f
                                            else 0.5f
                                        },
                                        darkMode = settingsState.appColorTuple.primary.luminance() < 0.3f
                                    )
                                ).value,
                                shape = ShapeDefaults.circle
                            )
                    )
                    Icon(
                        imageVector = Icons.Rounded.MiniEdit,
                        contentDescription = stringResource(R.string.edit),
                        tint = settingsState.appColorTuple.primary
                    )
                }
            }
        }
    )
    var showColorPicker by rememberSaveable { mutableStateOf(false) }
    AvailableColorTuplesSheet(
        visible = showPickColorSheet,
        colorTupleList = settingsState.colorTupleList,
        currentColorTuple = rememberAppColorTuple(),
        onToggleInvertColors = onToggleInvertColors,
        onThemeStyleSelected = { onSetThemeStyle(it.ordinal) },
        onUpdateThemeContrast = onUpdateThemeContrast,
        onOpenColorPicker = {
            showColorPicker = true
        },
        colorPicker = {
            ColorTuplePicker(
                visible = showColorPicker,
                colorTuple = settingsState.appColorTuple,
                onDismiss = {
                    showColorPicker = false
                },
                onColorChange = {
                    onUpdateColorTuple(it)
                    onUpdateColorTuples(settingsState.colorTupleList + it)
                }
            )
        },
        onUpdateColorTuples = onUpdateColorTuples,
        onToggleUseEmojiAsPrimaryColor = onToggleUseEmojiAsPrimaryColor,
        onDismiss = {
            showPickColorSheet = false
        },
        onPickTheme = onUpdateColorTuple
    )
}