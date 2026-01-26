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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.containerFabBorder
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EnhancedFloatingActionButton(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    type: EnhancedFloatingActionButtonType = LocalFABType.current,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = contentColor(containerColor),
    autoElevation: Dp = 1.5.dp,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val settingsState = LocalSettingsState.current
    val width by animateDpAsState(type.width)
    val height by animateDpAsState(type.height)
    val haptics = LocalHapticFeedback.current
    val focus = LocalFocusManager.current

    val shape = shapeByInteraction(
        shape = type.shape(),
        pressedShape = AutoCornersShape(max(width, height) / 8),
        interactionSource = interactionSource
    )

    val realInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

    LocalMinimumInteractiveComponentSize.ProvidesValue(Dp.Unspecified) {
        if (onLongClick != null) {
            val viewConfiguration = LocalViewConfiguration.current

            LaunchedEffect(realInteractionSource) {
                var isLongClick = false

                realInteractionSource.interactions.collectLatest { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            isLongClick = false
                            delay(viewConfiguration.longPressTimeoutMillis)
                            isLongClick = true
                            onLongClick()
                            focus.clearFocus()
                            haptics.longPress()
                        }

                        is PressInteraction.Release -> {
                            if (!isLongClick && onClick != null) {
                                onClick()
                                focus.clearFocus()
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

        FloatingActionButton(
            onClick = {
                if (onLongClick == null && onClick != null) {
                    onClick()
                    focus.clearFocus()
                    haptics.longPress()
                }
            },
            modifier = modifier
                .sizeIn(minWidth = width, minHeight = height)
                .containerFabBorder(
                    shape = shape,
                    autoElevation = animateDpAsState(
                        if (settingsState.drawFabShadows) autoElevation
                        else 0.dp
                    ).value
                ),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            contentColor = animateColorAsState(contentColor).value,
            shape = shape,
            containerColor = animateColorAsState(containerColor).value,
            interactionSource = realInteractionSource,
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
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


sealed class EnhancedFloatingActionButtonType(
    val width: Dp,
    val height: Dp,
    val shape: @Composable () -> Shape
) {
    constructor(
        size: Dp,
        shape: @Composable () -> Shape
    ) : this(
        width = size,
        height = size,
        shape = shape
    )

    data object Small : EnhancedFloatingActionButtonType(
        size = 40.dp,
        shape = { ShapeDefaults.small }
    )

    data object Primary : EnhancedFloatingActionButtonType(
        size = 56.dp,
        shape = { ShapeDefaults.default }
    )

    data object SecondaryHorizontal : EnhancedFloatingActionButtonType(
        width = 42.dp,
        height = 56.dp,
        shape = { ShapeDefaults.circle }
    )

    data object SecondaryVertical : EnhancedFloatingActionButtonType(
        width = 56.dp,
        height = 42.dp,
        shape = { ShapeDefaults.circle }
    )

    data object Large : EnhancedFloatingActionButtonType(
        size = 96.dp,
        shape = { ShapeDefaults.extremeLarge }
    )

    class Custom(
        size: Dp,
        shape: Shape
    ) : EnhancedFloatingActionButtonType(
        size = size,
        shape = { shape }
    )
}

val LocalFABType = compositionLocalOf<EnhancedFloatingActionButtonType> {
    EnhancedFloatingActionButtonType.Primary
}

@Composable
fun ProvideFABType(
    type: EnhancedFloatingActionButtonType,
    content: @Composable () -> Unit
) {
    LocalFABType.ProvidesValue(
        value = type,
        content = content
    )
}