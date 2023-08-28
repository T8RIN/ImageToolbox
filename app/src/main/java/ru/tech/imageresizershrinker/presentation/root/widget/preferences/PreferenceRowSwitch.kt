package ru.tech.imageresizershrinker.presentation.root.widget.preferences

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.presentation.root.theme.blend

@Composable
fun PreferenceRowSwitch(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    applyHorPadding: Boolean = true,
    checked: Boolean,
    color: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.2f
    ),
    startContent: (@Composable () -> Unit)? = null,
    onClick: (Boolean) -> Unit
) {
    PreferenceRow(
        modifier = modifier,
        applyHorPadding = applyHorPadding,
        title = title,
        color = color,
        subtitle = subtitle,
        startContent = startContent,
        onClick = { onClick(!checked) },
        endContent = {
            val thumbIcon: (@Composable () -> Unit)? = if (checked) {
                {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            } else null
            Switch(
                thumbContent = thumbIcon,
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline.blend(
                        MaterialTheme.colorScheme.secondaryContainer, 0.3f
                    ),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline.blend(
                        MaterialTheme.colorScheme.secondaryContainer, 0.2f
                    ),
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                checked = checked,
                onCheckedChange = {
                    onClick(it)
                }
            )
        }
    )
}