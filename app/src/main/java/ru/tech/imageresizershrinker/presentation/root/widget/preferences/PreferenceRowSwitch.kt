package ru.tech.imageresizershrinker.presentation.root.widget.preferences

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSwitch

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
            EnhancedSwitch(
                thumbIcon = if (checked) Icons.Rounded.Check else null,
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline.blend(
                        color, 0.3f
                    ),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline.blend(
                        color, 0.3f
                    ),
                    uncheckedTrackColor = color,
                    checkedIconColor = MaterialTheme.colorScheme.primary
                ),
                enabled = enabled,
                checked = checked,
                onCheckedChange = onClick
            )
        }
    )
}