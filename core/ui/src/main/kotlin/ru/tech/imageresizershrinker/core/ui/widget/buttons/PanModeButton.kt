package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FrontHand
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant

@Composable
fun PanModeButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedIconButton(
        modifier = modifier,
        containerColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.primary
            else Color.Transparent
        ).value,
        contentColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ).value,
        borderColor = MaterialTheme.colorScheme.outlineVariant(
            luminance = 0.1f
        ),
        onClick = onClick
    ) {
        Icon(Icons.Rounded.FrontHand, null)
    }
}