package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

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
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.coreui.icons.material.Theme
import ru.tech.imageresizershrinker.coreui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.coreui.theme.inverse
import ru.tech.imageresizershrinker.coreui.theme.outlineVariant
import ru.tech.imageresizershrinker.coreui.widget.color_picker.AvailableColorTuplesSheet
import ru.tech.imageresizershrinker.coreui.widget.color_picker.ColorTuplePicker
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSchemeSettingItem(
    toggleInvertColors: () -> Unit,
    setThemeStyle: (Int) -> Unit,
    updateThemeContrast: (Float) -> Unit,
    updateColorTuple: (ColorTuple) -> Unit,
    updateColorTuples: (List<ColorTuple>) -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp),
) {
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    val enabled = !settingsState.isDynamicColors

    val showPickColorSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        applyHorPadding = false,
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
        startContent = {
            Icon(
                imageVector = Icons.Outlined.Theme,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
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
                            animateColorAsState(
                                settingsState.appColorTuple.primary.inverse(
                                    fraction = {
                                        if (it) 0.8f
                                        else 0.5f
                                    },
                                    darkMode = settingsState.appColorTuple.primary.luminance() < 0.3f
                                )
                            ).value,
                            CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Rounded.CreateAlt,
                    contentDescription = null,
                    tint = settingsState.appColorTuple.primary,
                    modifier = Modifier.size(14.dp)
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
        onPickTheme = { updateColorTuple(it) }
    )
}