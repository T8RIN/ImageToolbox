package ru.tech.imageresizershrinker.presentation.root.widget.palette_selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun PaletteStyleSelection(
    onThemeStyleSelected: (PaletteStyle) -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val showPaletteStyleSelectionSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceItem(
        title = stringResource(R.string.palette_style),
        subtitle = remember(settingsState.themeStyle) {
            derivedStateOf {
                settingsState.themeStyle.getTitle(context)
            }
        }.value,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier,
        icon = Icons.Rounded.InvertColors,
        endIcon = Icons.Rounded.CreateAlt,
        onClick = {
            showPaletteStyleSelectionSheet.value = true
        }
    )

    SimpleSheet(
        visible = showPaletteStyleSelectionSheet,
        title = {
            TitleItem(
                text = stringResource(R.string.palette_style),
                icon = Icons.Rounded.InvertColors
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = { showPaletteStyleSelectionSheet.value = false }
            ) {
                Text(text = stringResource(R.string.close))
            }
        },
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                PaletteStyle.entries.forEach { style ->
                    PaletteStyleSelectionItem(
                        style = style,
                        onClick = {
                            onThemeStyleSelected(style)
                        }
                    )
                }
            }
        }
    )
}