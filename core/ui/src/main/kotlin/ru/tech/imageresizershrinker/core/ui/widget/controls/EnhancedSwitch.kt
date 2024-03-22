/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PixelSwitch
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource? = null,
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
//                M3Switch(
//                    modifier = modifier,
//                    internalModifier = switchModifier,
//                    colors = switchColors,
//                    checked = checked,
//                    enabled = enabled,
//                    onCheckedChange = switchOnCheckedChange,
//                    interactionSource = interactionSource
//                )
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