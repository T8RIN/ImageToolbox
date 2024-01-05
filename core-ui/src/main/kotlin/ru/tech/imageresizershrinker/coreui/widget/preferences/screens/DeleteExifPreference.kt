package ru.tech.imageresizershrinker.coreui.widget.preferences.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.icons.material.FingerprintOff
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem

@Composable
fun DeleteExifPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.FingerprintOff,
        title = stringResource(R.string.delete_exif),
        subtitle = stringResource(R.string.delete_exif_sub),
        color = color,
        modifier = modifier
    )
}