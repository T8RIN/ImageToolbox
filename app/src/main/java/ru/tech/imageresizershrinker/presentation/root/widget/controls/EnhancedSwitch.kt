package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun EnhancedSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val switchColors = colors ?: SwitchDefaults.colors(
        uncheckedBorderColor = MaterialTheme.colorScheme.primary,
        uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
        uncheckedTrackColor = MaterialTheme.colorScheme.primary,
        uncheckedIconColor = MaterialTheme.colorScheme.primary,
        checkedIconColor = MaterialTheme.colorScheme.primary
    )
    Switch(
        modifier = modifier,
        colors = switchColors,
        checked = checked,
        onCheckedChange = onCheckedChange,
        interactionSource = interactionSource,
        thumbContent = thumbIcon?.let {
            {
                AnimatedContent(thumbIcon) { thumbIcon ->
                    Icon(
                        imageVector = thumbIcon,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        }
    )
}