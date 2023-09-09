package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.draw_screen.components.materialShadow
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColor(containerColor),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant(onTopOf = containerColor),
    shape: Shape = ButtonDefaults.outlinedShape,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val settingsState = LocalSettingsState.current

    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)
    Box {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .alpha(alpha)
                    .materialShadow(
                        shape = shape,
                        elevation = if (settingsState.borderWidth > 0.dp) 0.dp else 0.5.dp
                    ),
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = contentColor,
                    containerColor = containerColor
                ),
                border = BorderStroke(
                    width = settingsState.borderWidth,
                    color = borderColor
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )
        }
        if (!enabled) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent
            ) {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColor(containerColor),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant(onTopOf = containerColor),
    shape: Shape = IconButtonDefaults.outlinedShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current

    val alpha by animateFloatAsState(if (enabled) 1f else 0.5f)
    Box {
        CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
            OutlinedIconButton(
                onClick = onClick,
                modifier = modifier
                    .minimumInteractiveComponentSize()
                    .alpha(alpha)
                    .materialShadow(
                        shape = shape,
                        elevation = if (settingsState.borderWidth > 0.dp) 0.dp else 0.5.dp
                    ),
                shape = shape,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = contentColor,
                    containerColor = containerColor
                ),
                border = BorderStroke(
                    width = settingsState.borderWidth,
                    color = borderColor
                ),
                interactionSource = interactionSource,
                content = content
            )
        }
        if (!enabled) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent
            ) {}
        }
    }
}

@Composable
private fun contentColor(
    backgroundColor: Color
) = MaterialTheme.colorScheme.contentColorFor(backgroundColor).takeOrElse {
    if (backgroundColor == MaterialTheme.colorScheme.mixedContainer) MaterialTheme.colorScheme.onMixedContainer
    else LocalContentColor.current
}