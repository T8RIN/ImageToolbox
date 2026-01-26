/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.domain.model.SwitchType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.switches.CupertinoSwitch
import com.t8rin.imagetoolbox.core.ui.widget.switches.CupertinoSwitchDefaults
import com.t8rin.imagetoolbox.core.ui.widget.switches.FluentSwitch
import com.t8rin.imagetoolbox.core.ui.widget.switches.HyperOSSwitch
import com.t8rin.imagetoolbox.core.ui.widget.switches.LiquidGlassSwitch
import com.t8rin.imagetoolbox.core.ui.widget.switches.M3Switch
import com.t8rin.imagetoolbox.core.ui.widget.switches.PixelSwitch

@Composable
fun EnhancedSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbIcon: ImageVector? = null,
    enabled: Boolean = true,
    colors: SwitchColors? = null,
    interactionSource: MutableInteractionSource? = null,
    colorUnderSwitch: Color = Color.Unspecified
) {
    val switchColors = colors ?: SwitchDefaults.colors(
        disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = 0.12f)
            .compositeOver(MaterialTheme.colorScheme.surface)
    )
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current
    val focus = LocalFocusManager.current

    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        val switchModifier = modifier
            .minimumInteractiveComponentSize()
            .container(
                shape = ShapeDefaults.circle,
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
                haptics.longPress()
                focus.clearFocus()
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
            targetState = settingsState.switchType,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(false)
            }
        ) { switchType ->
            when (switchType) {
                is SwitchType.MaterialYou -> {
                    M3Switch(
                        modifier = modifier,
                        internalModifier = switchModifier,
                        colors = switchColors,
                        checked = checked,
                        enabled = enabled,
                        onCheckedChange = switchOnCheckedChange,
                        interactionSource = interactionSource
                    )
                }

                is SwitchType.Compose -> {
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

                is SwitchType.Pixel -> {
                    PixelSwitch(
                        modifier = switchModifier,
                        colors = switchColors,
                        checked = checked,
                        enabled = enabled,
                        onCheckedChange = switchOnCheckedChange,
                        interactionSource = interactionSource
                    )
                }

                is SwitchType.Fluent -> {
                    FluentSwitch(
                        modifier = switchModifier,
                        colors = switchColors,
                        checked = checked,
                        enabled = enabled,
                        onCheckedChange = switchOnCheckedChange,
                        interactionSource = interactionSource
                    )
                }

                is SwitchType.Cupertino -> {
                    CupertinoSwitch(
                        checked = checked,
                        onCheckedChange = switchOnCheckedChange,
                        modifier = switchModifier,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        colors = CupertinoSwitchDefaults.colors()
                    )
                }

                is SwitchType.LiquidGlass -> {
                    LiquidGlassSwitch(
                        checked = checked,
                        onCheckedChange = switchOnCheckedChange,
                        internalModifier = switchModifier,
                        modifier = modifier,
                        enabled = enabled,
                        interactionSource = interactionSource,
                        colors = CupertinoSwitchDefaults.colors(),
                        backgroundColor = colorUnderSwitch
                    )
                }

                is SwitchType.HyperOS -> {
                    HyperOSSwitch(
                        modifier = switchModifier,
                        colors = switchColors.copy(
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        checked = checked,
                        enabled = enabled,
                        onCheckedChange = switchOnCheckedChange,
                        interactionSource = interactionSource
                    )
                }
            }
        }
    }
}