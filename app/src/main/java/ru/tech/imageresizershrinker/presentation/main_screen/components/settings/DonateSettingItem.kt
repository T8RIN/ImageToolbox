package ru.tech.imageresizershrinker.presentation.main_screen.components.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.main_screen.components.DonateSheet
import ru.tech.imageresizershrinker.presentation.root.model.isFirstLaunch
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.pulsate
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun DonateSettingItem(
    shape: Shape = SettingsShapeDefaults.bottomShape
) {
    val settingsState = LocalSettingsState.current
    val showDonateSheet = rememberSaveable { mutableStateOf(false) }
    PreferenceRow(
        modifier = Modifier
            .pulsate(
                range = 0.98f..1.02f,
                enabled = settingsState.isFirstLaunch()
            )
            .padding(horizontal = 8.dp),
        shape = shape,
        applyHorPadding = false,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        title = stringResource(R.string.donation),
        subtitle = stringResource(R.string.donation_sub),
        endContent = {
            Icon(Icons.Rounded.Payments, null)
        },
        onClick = {
            showDonateSheet.value = true
        }
    )
    DonateSheet(showDonateSheet)
}