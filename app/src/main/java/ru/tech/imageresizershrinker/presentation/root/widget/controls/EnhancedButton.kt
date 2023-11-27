package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    isShadowClip: Boolean = containerColor.alpha != 1f,
    shadowModifier: @Composable () -> Modifier = {
        val settingsState = LocalSettingsState.current
        Modifier.materialShadow(
            shape = shape,
            elevation = animateDpAsState(
                if (settingsState.borderWidth > 0.dp || !enabled) 0.dp else 0.5.dp
            ).value,
            enabled = LocalSettingsState.current.drawButtonShadows,
            isClipped = isShadowClip
        )
    },
    content: @Composable RowScope.() -> Unit
) {
    val settingsState = LocalSettingsState.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Box {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .then(shadowModifier()),
                shape = shape,
                colors = ButtonDefaults.buttonColors(
                    contentColor = animateColorAsState(
                        if (enabled) contentColor
                        else MaterialTheme.colorScheme.onSurface.copy(0.38f)
                    ).value,
                    containerColor = animateColorAsState(
                        if (enabled) containerColor
                        else MaterialTheme.colorScheme.onSurface.copy(0.12f)
                    ).value
                ),
                enabled = true,
                border = BorderStroke(
                    width = settingsState.borderWidth,
                    color = borderColor
                ),
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content
            )

            if (!enabled) {
                Surface(color = Color.Transparent, modifier = Modifier.matchParentSize()) {}
            }
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
    forceMinimumInteractiveComponentSize: Boolean = true,
    enableAutoShadowAndBorder: Boolean = true,
    isShadowClip: Boolean = containerColor.alpha != 1f || !enabled,
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        OutlinedIconButton(
            onClick = onClick,
            modifier = modifier
                .then(
                    if (forceMinimumInteractiveComponentSize) Modifier.minimumInteractiveComponentSize()
                    else Modifier
                )
                .then(
                    if (enableAutoShadowAndBorder) Modifier.materialShadow(
                        shape = shape,
                        isClipped = isShadowClip,
                        enabled = LocalSettingsState.current.drawButtonShadows,
                        elevation = if (settingsState.borderWidth > 0.dp) 0.dp else 0.7.dp
                    ) else Modifier
                ),
            shape = shape,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = contentColor,
                containerColor = containerColor
            ),
            enabled = enabled,
            border = if (enableAutoShadowAndBorder) BorderStroke(
                width = settingsState.borderWidth,
                color = borderColor
            ) else BorderStroke(0.dp, Color.Transparent),
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Composable
private fun contentColor(
    backgroundColor: Color
) = MaterialTheme.colorScheme.contentColorFor(backgroundColor).takeOrElse {
    if (backgroundColor == MaterialTheme.colorScheme.mixedContainer) MaterialTheme.colorScheme.onMixedContainer
    else LocalContentColor.current
}