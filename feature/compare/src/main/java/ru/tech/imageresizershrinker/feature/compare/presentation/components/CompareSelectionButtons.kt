package ru.tech.imageresizershrinker.feature.compare.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun CompareSelectionButtons(
    value: CompareType,
    onValueChange: (CompareType) -> Unit,
    isPortrait: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonsContent = @Composable {
        CompareType.entries.forEach { compareType ->
            val selected by remember(compareType, value) {
                derivedStateOf {
                    compareType == value
                }
            }
            val containerColor by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else Color.Transparent
            )
            EnhancedIconButton(
                containerColor = containerColor,
                onClick = { onValueChange(compareType) }
            ) {
                Icon(
                    imageVector = compareType.icon,
                    contentDescription = null
                )
            }
        }
    }
    val internalModifier = modifier.container(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = CircleShape
    )
    if (isPortrait) {
        Row(
            modifier = internalModifier
        ) {
            buttonsContent()
        }
    } else {
        Column(
            modifier = internalModifier
        ) {
            buttonsContent()
        }
    }
}