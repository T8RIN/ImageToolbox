package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun EnhancedChip(
    selected: Boolean,
    onClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(6.dp),
    selectedColor: Color,
    selectedContentColor: Color = MaterialTheme.colorScheme.contentColorFor(selectedColor),
    unselectedColor: Color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    shape: Shape = MaterialTheme.shapes.small,
    label: @Composable () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val color by animateColorAsState(
        if (selected) selectedColor
        else unselectedColor
    )
    val contentColor by animateColorAsState(
        if (selected) selectedContentColor
        else unselectedContentColor
    )

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        ),
        LocalContentColor provides contentColor
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(36.dp, 36.dp)
                .container(
                    color = color,
                    resultPadding = 0.dp,
                    borderColor = if (!selected) MaterialTheme.colorScheme.outlineVariant()
                    else selectedColor
                        .copy(alpha = 0.9f)
                        .compositeOver(Color.Black),
                    shape = shape,
                    autoShadowElevation = 0.5.dp
                )
                .clickable {
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                label()
            }
        }
    }
}