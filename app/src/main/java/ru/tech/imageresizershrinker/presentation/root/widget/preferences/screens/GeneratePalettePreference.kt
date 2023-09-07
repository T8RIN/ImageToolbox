package ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens

import android.annotation.SuppressLint
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
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.PaletteSwatch
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem

@Composable
fun GeneratePalettePreference(
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.PaletteSwatch,
        title = stringResource(R.string.generate_palette),
        subtitle = stringResource(R.string.palette_sub),
        modifier = modifier,
        color = color
    )
}