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

package com.t8rin.imagetoolbox.feature.root.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.animation.springySpec
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberRipple
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import kotlinx.coroutines.launch

@Composable
internal fun BoxScope.SettingsOpenButton(
    isWantOpenSettings: Boolean,
    onStateChange: (Boolean) -> Unit,
    scaffoldState: BackdropScaffoldState,
    canExpandSettings: Boolean
) {
    val scope = rememberCoroutineScope()
    val fastSettingsSide = LocalSettingsState.current.fastSettingsSide
    val alignment = if (fastSettingsSide == FastSettingsSide.CenterStart) {
        Alignment.CenterStart
    } else {
        Alignment.CenterEnd
    }

    val direction = LocalLayoutDirection.current
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()
    val (startPadding, endPadding) = remember(paddingValues, direction) {
        derivedStateOf {
            paddingValues.calculateStartPadding(direction) to paddingValues.calculateEndPadding(
                direction
            )
        }
    }.value

    val expandedPart = if (isWantOpenSettings) 12.dp else 42.dp
    val cornerRadius = expandedPart.coerceAtLeast(4.dp)

    val shape = animateShape(
        targetValue = key(cornerRadius) {
            if (fastSettingsSide == FastSettingsSide.CenterStart) {
                if (startPadding == 0.dp) {
                    AutoCornersShape(
                        topEnd = cornerRadius,
                        bottomEnd = cornerRadius
                    )
                } else {
                    AutoCornersShape(cornerRadius)
                }
            } else {
                if (endPadding == 0.dp) {
                    AutoCornersShape(
                        topStart = cornerRadius,
                        bottomStart = cornerRadius
                    )
                } else {
                    AutoCornersShape(cornerRadius)
                }
            }
        },
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessLow
        )
    )

    val height by animateDpAsState(
        targetValue = if (isWantOpenSettings) 64.dp else 104.dp
    )
    val width by animateDpAsState(
        targetValue = if (isWantOpenSettings) 48.dp else 24.dp,
        animationSpec = springySpec()
    )
    val xOffset by animateDpAsState(
        targetValue = if (!canExpandSettings) {
            if (fastSettingsSide == FastSettingsSide.CenterStart) {
                -width
            } else {
                width
            }
        } else {
            0.dp
        },
        animationSpec = spring(
            visibilityThreshold = Dp.VisibilityThreshold,
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    val alpha by animateFloatAsState(
        targetValue = if (canExpandSettings) 1f else 0f,
        animationSpec = tween(650)
    )
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .align(alignment)
            .padding(
                start = startPadding,
                end = endPadding
            )
            .size(
                height = height,
                width = width
            )
            .hapticsClickable(
                enabled = canExpandSettings,
                indication = null,
                interactionSource = interactionSource
            ) {
                if (isWantOpenSettings) {
                    scope.launch {
                        scaffoldState.reveal()
                        onStateChange(false)
                    }
                } else {
                    onStateChange(true)
                }
            }
            .alpha(alpha)
            .offset {
                IntOffset(
                    x = xOffset.roundToPx(),
                    y = 0
                )
            }
    ) {
        Box {
            val width by animateDpAsState(
                targetValue = if (isWantOpenSettings) 48.dp else 4.dp,
                animationSpec = spring(
                    dampingRatio = 0.35f,
                    stiffness = Spring.StiffnessLow
                )
            )

            val containerColor = takeColorFromScheme {
                tertiary.blend(primary, 0.65f)
            }

            val contentColor = takeColorFromScheme {
                onTertiary.blend(onPrimary, 0.8f)
            }

            Box(
                modifier = Modifier
                    .align(alignment)
                    .width(width)
                    .height(64.dp)
                    .container(
                        shape = shape,
                        resultPadding = 0.dp,
                        color = containerColor
                    )
                    .indication(
                        interactionSource = interactionSource,
                        indication = rememberRipple(contentColor = contentColor)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = isWantOpenSettings,
                    enter = fadeIn() + scaleIn(
                        animationSpec = spring(
                            dampingRatio = 0.35f,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = contentColor
                    )
                }
            }
        }
    }
}