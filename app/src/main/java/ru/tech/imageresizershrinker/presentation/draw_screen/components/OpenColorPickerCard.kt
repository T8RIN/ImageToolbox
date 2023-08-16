package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block

@Composable
fun OpenColorPickerCard(
    onOpen: () -> Unit
) {
    Row(
        Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .block(applyResultPadding = false)
            .clickable { onOpen() }
            .padding(16.dp)
    ) {
        Text(stringResource(id = R.string.pick_color), modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.Colorize, null)
    }
}