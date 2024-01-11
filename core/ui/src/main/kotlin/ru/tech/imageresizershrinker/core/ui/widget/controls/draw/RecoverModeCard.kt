package ru.tech.imageresizershrinker.core.ui.widget.controls.draw

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun RecoverModeCard(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    PreferenceRowSwitch(
        title = stringResource(R.string.restore_background),
        subtitle = stringResource(R.string.restore_background_sub),
        startIcon = Icons.Rounded.Brush,
        checked = selected,
        onClick = {
            onClick()
        }
    )
}

@Composable
fun RecoverModeButton(
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedIconButton(
        modifier = modifier,
        enabled = enabled,
        containerColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.mixedContainer
            else Color.Transparent
        ).value,
        contentColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onMixedContainer
            else MaterialTheme.colorScheme.onSurface
        ).value,
        borderColor = MaterialTheme.colorScheme.outlineVariant(
            luminance = 0.1f
        ),
        onClick = onClick
    ) {
        Icon(Icons.Rounded.Brush, null)
    }
}