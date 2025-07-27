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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.modifier.materialShadow
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EnhancedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    containerColor: Color = Color.Transparent,
    contentColor: Color = contentColor(containerColor),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant(onTopOf = containerColor),
    shape: Shape = IconButtonDefaults.smallRoundShape,
    pressedShape: Shape = IconButtonDefaults.smallPressedShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    forceMinimumInteractiveComponentSize: Boolean = true,
    enableAutoShadowAndBorder: Boolean = containerColor != Color.Transparent,
    isShadowClip: Boolean = containerColor.alpha != 1f || !enabled,
    content: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current

    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        if (onLongClick != null) {
            val viewConfiguration = LocalViewConfiguration.current


            LaunchedEffect(interactionSource) {
                var isLongClick = false

                interactionSource.interactions.collectLatest { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            isLongClick = false
                            delay(viewConfiguration.longPressTimeoutMillis)
                            isLongClick = true
                            onLongClick()
                            haptics.longPress()
                        }

                        is PressInteraction.Release -> {
                            if (!isLongClick) {
                                onClick()
                                haptics.press()
                            }
                        }

                        is PressInteraction.Cancel -> {
                            isLongClick = false
                        }
                    }
                }
            }
        }
        val animatedShape = shapeByInteraction(
            shape = shape,
            pressedShape = pressedShape,
            interactionSource = interactionSource
        )

        OutlinedIconButton(
            onClick = {
                if (onLongClick == null) {
                    onClick()
                    haptics.longPress()
                }
            },
            modifier = modifier
                .then(
                    if (forceMinimumInteractiveComponentSize) Modifier.minimumInteractiveComponentSize()
                    else Modifier
                )
                .materialShadow(
                    shape = animatedShape,
                    isClipped = isShadowClip,
                    enabled = LocalSettingsState.current.drawButtonShadows,
                    elevation = if (settingsState.borderWidth > 0.dp || !enableAutoShadowAndBorder) 0.dp else 0.7.dp
                ),
            shape = animatedShape,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = contentColor,
                containerColor = containerColor
            ),
            enabled = enabled,
            border = BorderStroke(
                width = animateDpAsState(
                    if (enableAutoShadowAndBorder) settingsState.borderWidth
                    else 0.dp
                ).value,
                color = animateColorAsState(
                    if (enableAutoShadowAndBorder) borderColor
                    else Color.Transparent
                ).value
            ),
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