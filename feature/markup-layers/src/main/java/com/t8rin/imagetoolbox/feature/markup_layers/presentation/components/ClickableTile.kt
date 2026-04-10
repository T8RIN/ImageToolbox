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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container

@Composable
internal fun ClickableTile(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String?,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onHoldStep: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (text != null) {
        val tooltipState = rememberTooltipState()
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                TooltipAnchorPosition.Above
            ),
            tooltip = {
                PlainTooltip {
                    Text(text)
                }
            },
            state = tooltipState
        ) {
            ClickableTile(
                containerColor = containerColor,
                onClick = onClick,
                onHoldStep = onHoldStep,
                modifier = modifier
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }
    } else {
        ClickableTile(
            containerColor = containerColor,
            onClick = onClick,
            onHoldStep = onHoldStep,
            modifier = modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        }
    }
}


@Composable
internal fun ClickableTile(
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onHoldStep: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val haptics = LocalHapticFeedback.current

    val interactionModifier = if (onHoldStep != null) {
        Modifier.pointerInput(onClick, onHoldStep) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                down.consume()

                haptics.longPress()
                onClick()

                var repeatDelayMs = 140L
                val minRepeatDelayMs = 45L

                var up = withTimeoutOrNull(240L) {
                    waitForUpOrCancellation()
                }
                if (up != null) {
                    up.consume()
                    return@awaitEachGesture
                }

                val holdStartNanos = System.nanoTime()
                var stepsAccumulator = 0f
                while (up == null) {
                    val holdDurationMs = ((System.nanoTime() - holdStartNanos) / 1_000_000L)

                    // Smoothly ramps average speed from 1 to 10 steps per tick.
                    val progress = (holdDurationMs / 3000f).coerceIn(0f, 1f)
                    val smoothStepsPerTick = 1f + 9f * progress
                    stepsAccumulator += smoothStepsPerTick

                    val stepsPerTick = stepsAccumulator.toInt().coerceIn(1, 20).also {
                        stepsAccumulator -= it
                    }
                    repeat(stepsPerTick) {
                        onHoldStep()
                    }

                    repeatDelayMs = (repeatDelayMs * 0.94f).toLong()
                        .coerceAtLeast(minRepeatDelayMs)
                    up = withTimeoutOrNull(repeatDelayMs) {
                        waitForUpOrCancellation()
                    }
                }
                up.consume()
            }
        }
    } else {
        Modifier.hapticsClickable(onClick = onClick)
    }

    Column(
        modifier = Modifier
            .then(
                if (modifier == Modifier) {
                    Modifier.size(
                        width = 100.dp,
                        height = 72.dp
                    )
                } else {
                    modifier
                }
            )
            .container(
                shape = ShapeDefaults.extraSmall,
                color = containerColor,
                resultPadding = 0.dp
            )
            .then(interactionModifier)
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}
