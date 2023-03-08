package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Telegram

@Composable
fun TelegramButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isTelegramSpecs: Boolean
) {
    OutlinedIconButton(
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = animateColorAsState(
                if (isTelegramSpecs) MaterialTheme.colorScheme.mixedColor
                else Color.Transparent
            ).value,
            contentColor = animateColorAsState(
                if (isTelegramSpecs) MaterialTheme.colorScheme.onMixedColor
                else MaterialTheme.colorScheme.onSurface
            ).value,
            disabledContainerColor = Color.Transparent
        ),
        border = BorderStroke(
            1.dp, animateColorAsState(
                if (isTelegramSpecs) MaterialTheme.colorScheme.outlineVariant
                else Color.Transparent
            ).value
        ),
    ) {
        Icon(Icons.Rounded.Telegram, null)
    }
}