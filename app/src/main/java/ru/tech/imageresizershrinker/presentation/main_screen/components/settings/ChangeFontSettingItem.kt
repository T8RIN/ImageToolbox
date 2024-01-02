package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.coreui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.coreui.icons.material.FontFamily
import ru.tech.imageresizershrinker.coreui.model.UiFontFam
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.coreui.widget.sheets.PickFontFamilySheet
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun ChangeFontSettingItem(
    onFontSelected: (UiFontFam) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val showFontSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceItem(
        shape = shape,
        onClick = { showFontSheet.value = true },
        title = stringResource(R.string.font),
        subtitle = settingsState.font.name ?: stringResource(R.string.system),
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        icon = Icons.Rounded.FontFamily,
        endIcon = Icons.Rounded.CreateAlt,
        modifier = modifier
    )
    PickFontFamilySheet(
        visible = showFontSheet,
        onFontSelected = onFontSelected
    )
}