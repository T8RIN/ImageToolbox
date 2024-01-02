package ru.tech.imageresizershrinker.coreui.widget.controls.resize_group.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurLinear
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch

@Composable
fun UseBlurredBackgroundToggle(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.blur_edges),
        subtitle = stringResource(R.string.blur_edges_sub),
        checked = checked,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = onCheckedChange,
        startIcon = Icons.Rounded.BlurLinear
    )
}