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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberAppColorTuple
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.MiniEdit
import ru.tech.imageresizershrinker.core.ui.icons.material.Theme
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.AvailableColorTuplesSheet
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorTuplePicker
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSchemeSettingItem(
    toggleInvertColors: () -> Unit,
    setThemeStyle: (Int) -> Unit,
    updateThemeContrast: (Float) -> Unit,
    updateColorTuple: (ColorTuple) -> Unit,
    updateColorTuples: (List<ColorTuple>) -> Unit,
    onToggleUseEmojiAsPrimaryColor: () -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp),
) {
    val toastHostState = LocalToastHostState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    val enabled = !settingsState.isDynamicColors

    val showPickColorSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        modifier = modifier
            .alpha(
                animateFloatAsState(
                    if (enabled) 1f
                    else 0.5f
                ).value
            ),
        autoShadowElevation = if (enabled) 1.dp else 0.dp,
        shape = shape,
        title = stringResource(R.string.color_scheme),
        startIcon = Icons.Outlined.Theme,
        subtitle = stringResource(R.string.pick_accent_color),
        onClick = {
            if (enabled) showPickColorSheet.value = true
            else scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.Palette,
                    message = context.getString(R.string.cannot_change_palette_while_dynamic_colors_applied)
                )
            }
        },
        endContent = {
            ColorTupleItem(
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
                    ),
                colorTuple = remember(
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
                }.value,
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
                            shape = CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Rounded.MiniEdit,
                    contentDescription = null,
                    tint = settingsState.appColorTuple.primary
                )
            }
        }
    )
    val showColorPicker = rememberSaveable { mutableStateOf(false) }
    AvailableColorTuplesSheet(
        visible = showPickColorSheet,
        colorTupleList = settingsState.colorTupleList,
        currentColorTuple = rememberAppColorTuple(
            defaultColorTuple = settingsState.appColorTuple,
            dynamicColor = settingsState.isDynamicColors,
            darkTheme = settingsState.isNightMode
        ),
        onToggleInvertColors = toggleInvertColors,
        onThemeStyleSelected = { setThemeStyle(it.ordinal) },
        updateThemeContrast = updateThemeContrast,
        openColorPicker = {
            showColorPicker.value = true
        },
        colorPicker = { onUpdateColorTuples ->
            ColorTuplePicker(
                visible = showColorPicker,
                colorTuple = settingsState.appColorTuple,
                onColorChange = {
                    updateColorTuple(it)
                    onUpdateColorTuples(settingsState.colorTupleList + it)
                }
            )
        },
        onUpdateColorTuples = {
            updateColorTuples(it)
        },
        onToggleUseEmojiAsPrimaryColor = onToggleUseEmojiAsPrimaryColor,
        onPickTheme = { updateColorTuple(it) }
    )
}