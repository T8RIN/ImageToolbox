package ru.tech.imageresizershrinker.presentation.image_stitching_screen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LinearScale
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch

@Composable
fun ScaleSmallImagesToLargeToggle(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.scale_small_images_to_large),
        subtitle = stringResource(R.string.scale_small_images_to_large_sub),
        checked = checked,
        shape = RoundedCornerShape(24.dp),
        onClick = onCheckedChange,
        startIcon = Icons.Rounded.LinearScale
    )
}