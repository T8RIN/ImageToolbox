package ru.tech.imageresizershrinker.coreui.widget.controls

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable

object EnhancedSwitchDefaults {

    @Composable
    fun uncheckableColors(): SwitchColors = SwitchDefaults.colors(
        uncheckedBorderColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
        uncheckedTrackColor = MaterialTheme.colorScheme.primary,
        uncheckedIconColor = MaterialTheme.colorScheme.primary,
        checkedIconColor = MaterialTheme.colorScheme.primary
    )

}