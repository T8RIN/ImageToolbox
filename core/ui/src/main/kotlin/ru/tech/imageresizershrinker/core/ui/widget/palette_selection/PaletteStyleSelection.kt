package ru.tech.imageresizershrinker.core.ui.widget.palette_selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.icons.material.Swatch
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

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
        icon = Icons.Rounded.Swatch,
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
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                items(PaletteStyle.entries) { style ->
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