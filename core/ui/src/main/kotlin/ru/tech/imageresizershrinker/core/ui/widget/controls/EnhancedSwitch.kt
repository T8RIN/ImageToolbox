package ru.tech.imageresizershrinker.core.ui.widget.controls


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PixelSwitch
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val switchColors = colors ?: SwitchDefaults.colors(
        disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 0.12f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    )
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current

    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        val switchModifier = modifier
            .minimumInteractiveComponentSize()
            .container(
                shape = CircleShape,
                resultPadding = 0.dp,
                autoShadowElevation = animateDpAsState(
                    if (settingsState.drawSwitchShadows) 1.dp
                    else 0.dp
                ).value,
                borderColor = Color.Transparent,
                isShadowClip = true,
                isStandaloneContainer = false,
                color = Color.Transparent
            )
        val switchOnCheckedChange: ((Boolean) -> Unit)? = onCheckedChange?.let {
            { boolean ->
                onCheckedChange(boolean)
                haptics.performHapticFeedback(
                    HapticFeedbackType.LongPress
                )
            }
        }
        val thumbContent: (@Composable () -> Unit)? = thumbIcon?.let {
            {
                AnimatedContent(thumbIcon) { thumbIcon ->
                    Icon(
                        imageVector = thumbIcon,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        }

        AnimatedContent(
            targetState = settingsState.usePixelSwitch,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { usePixelSwitch ->
            if (usePixelSwitch) {
                PixelSwitch(
                    modifier = switchModifier,
                    colors = switchColors,
                    checked = checked,
                    enabled = enabled,
                    onCheckedChange = switchOnCheckedChange,
                    interactionSource = interactionSource
                )
            } else {
                Switch(
                    modifier = switchModifier,
                    colors = switchColors,
                    checked = checked,
                    enabled = enabled,
                    onCheckedChange = switchOnCheckedChange,
                    interactionSource = interactionSource,
                    thumbContent = thumbContent
                )
            }
        }
    }
}