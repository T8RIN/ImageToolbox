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
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coreui.icons.material.Interface
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceItem

@Composable
fun BytesResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Filled.Interface,
        title = stringResource(R.string.by_bytes_resize),
        subtitle = stringResource(R.string.by_bytes_resize_sub),
        color = color,
        modifier = modifier
    )
}