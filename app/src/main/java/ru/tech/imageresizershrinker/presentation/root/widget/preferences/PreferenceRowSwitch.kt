package ru.tech.imageresizershrinker.presentation.root.widget.preferences

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.theme.blend

@Composable
fun PreferenceRowSwitch(
    modifier: Modifier = Modifier,
    title: String,
    enabled: Boolean = true,
    subtitle: String? = null,
    autoShadowElevation: Dp = 1.dp,
    applyHorPadding: Boolean = true,
    checked: Boolean,
    color: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.2f
    ),
    contentColor: Color? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    startContent: (@Composable () -> Unit)? = null,
    resultModifier: Modifier = Modifier.padding(
        horizontal = if (startContent != null) 0.dp else 16.dp,
        vertical = 8.dp
    ),
    onClick: (Boolean) -> Unit
) {
    PreferenceRow(
        autoShadowElevation = autoShadowElevation,
        enabled = enabled,
        modifier = modifier,
        resultModifier = resultModifier,
        applyHorPadding = applyHorPadding,
        title = title,
        contentColor = contentColor,
        shape = shape,
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
                enabled = enabled,
                checked = checked,
                onCheckedChange = {
                    onClick(it)
                }
            )
        }
    )
}