package ru.tech.imageresizershrinker.presentation.limits_resize_screen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MotionPhotosAuto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch

@Composable
fun AutoRotateLimitBoxToggle(
    value: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.auto_rotate_limits),
        subtitle = stringResource(R.string.auto_rotate_limits_sub),
        checked = value,
        shape = RoundedCornerShape(24.dp),
        onClick = {
            onClick()
        },
        startIcon = Icons.Rounded.MotionPhotosAuto
    )
}