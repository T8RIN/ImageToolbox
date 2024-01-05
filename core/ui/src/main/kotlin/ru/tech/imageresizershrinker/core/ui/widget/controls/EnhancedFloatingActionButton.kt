package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.theme.onMixedContainer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.containerFabBorder
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun EnhancedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: EnhancedFloatingActionButtonType = EnhancedFloatingActionButtonType.Primary,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = contentColor(containerColor),
    autoElevation: Dp = 1.5.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val settingsState = LocalSettingsState.current
    val size by animateDpAsState(type.size)
    val haptics = LocalHapticFeedback.current

    FloatingActionButton(
        onClick = {
            onClick()
            haptics.performHapticFeedback(
                HapticFeedbackType.LongPress
            )
        },
        modifier = modifier
            .sizeIn(minWidth = size, minHeight = size)
            .containerFabBorder(
                shape = type.shape,
                autoElevation = animateDpAsState(
                    if (settingsState.drawFabShadows) autoElevation
                    else 0.dp
                ).value
            ),
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        contentColor = contentColor,
        shape = type.shape,
        containerColor = containerColor,
        interactionSource = interactionSource,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    )
}

@Composable
private fun contentColor(
    backgroundColor: Color
) = MaterialTheme.colorScheme.contentColorFor(backgroundColor).takeOrElse {
    if (backgroundColor == MaterialTheme.colorScheme.mixedContainer) MaterialTheme.colorScheme.onMixedContainer
    else LocalContentColor.current
}