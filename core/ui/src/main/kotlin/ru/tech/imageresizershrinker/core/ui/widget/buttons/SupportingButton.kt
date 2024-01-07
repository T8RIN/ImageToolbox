package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun SupportingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Info,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor)
) {
    val haptics = LocalHapticFeedback.current
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = modifier
            .background(
                color = containerColor,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable {
                haptics.performHapticFeedback(
                    HapticFeedbackType.TextHandleMove
                )
                onClick()
            }
            .padding(1.dp)
            .size(
                with(LocalDensity.current) {
                    LocalTextStyle.current.fontSize.toDp()
                }
            )
    )
}