package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRowSwitch

@Composable
fun DrawArrowsSelector(
    modifier: Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.draw_arrows),
        subtitle = stringResource(R.string.draw_arrows_sub),
        checked = checked,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = onCheckedChange,
        startIcon = Icons.Rounded.ArrowCircleUp
    )
}