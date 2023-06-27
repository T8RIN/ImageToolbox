package ru.tech.imageresizershrinker.presentation.widget.preferences

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    onClick: (() -> Unit)? = null,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    endIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
) {
    val _icon: (@Composable () -> Unit)? = if (icon == null) null else {
        { Icon(icon, null) }
    }
    val _icon2: (@Composable () -> Unit)? = if (endIcon == null) null else {
        { Icon(endIcon, null) }
    }
    PreferenceItemOverload(
        onClick = onClick,
        title = title,
        subtitle = subtitle,
        icon = _icon,
        endIcon = _icon2,
        shape = shape,
        color = color,
        modifier = modifier
    )
}