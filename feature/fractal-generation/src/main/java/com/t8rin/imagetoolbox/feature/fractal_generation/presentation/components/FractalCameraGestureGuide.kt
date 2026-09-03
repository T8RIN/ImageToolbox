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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.RotateRight
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCamera
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun FractalCameraGestureGuide(
    startPosition: Offset,
    currentPosition: Offset,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var indicatorSize by remember {
        mutableStateOf(
            with(density) {
                IntSize(
                    width = 156.dp.roundToPx(),
                    height = 40.dp.roundToPx()
                )
            }
        )
    }
    val indicatorOffset by remember(
        density,
        viewportSize,
        indicatorSize,
        currentPosition
    ) {
        derivedStateOf {
            with(density) {
                val horizontalGap = -indicatorSize.width / 2
                val verticalGap = 56.dp.roundToPx()
                val positionAbove = currentPosition.y.roundToInt() -
                        verticalGap - indicatorSize.height
                val positionBelow = currentPosition.y.roundToInt() + verticalGap
                val maximumX = (viewportSize.width - indicatorSize.width).coerceAtLeast(0)
                val maximumY = (viewportSize.height - indicatorSize.height).coerceAtLeast(0)

                IntOffset(
                    x = (currentPosition.x.roundToInt() + horizontalGap).coerceIn(0, maximumX),
                    y = (if (positionAbove >= 0) positionAbove else positionBelow)
                        .coerceIn(0, maximumY)
                )
            }
        }
    }
    val yawDelta by remember(startPosition, currentPosition, viewportSize.width) {
        derivedStateOf {
            if (viewportSize.width == 0) 0 else {
                ((currentPosition.x - startPosition.x) / viewportSize.width *
                        FractalCamera.ORBIT_DEGREES_PER_VIEWPORT).roundToInt()
            }
        }
    }
    val pitchDelta by remember(startPosition, currentPosition, viewportSize.height) {
        derivedStateOf {
            if (viewportSize.height == 0) 0 else {
                ((currentPosition.y - startPosition.y) / viewportSize.height *
                        FractalCamera.ORBIT_DEGREES_PER_VIEWPORT).roundToInt()
            }
        }
    }

    val guideColor = MaterialTheme.colorScheme.primary
    val guideContainerColor = MaterialTheme.colorScheme.primaryContainer
    val guideContentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val guideShadowColor = Color.Black.copy(alpha = 0.55f)

    Box(
        modifier = modifier.onSizeChanged { viewportSize = it }
    ) {
        Canvas(Modifier.matchParentSize()) {
            val movement = currentPosition - startPosition
            val distance = movement.getDistance()
            if (distance <= 0f) return@Canvas
            val endInnerCircleRadius = 16.dp.toPx()

            val lineWidth = 3.dp.toPx()
            val unitVector = movement / distance
            val lineEnd = currentPosition - unitVector * endInnerCircleRadius

            if (distance > endInnerCircleRadius) {
                drawLine(
                    color = guideShadowColor,
                    start = startPosition,
                    end = lineEnd,
                    strokeWidth = lineWidth + 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = guideColor,
                    start = startPosition,
                    end = lineEnd,
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = guideShadowColor,
                radius = 10.dp.toPx(),
                center = startPosition
            )
            drawCircle(
                color = guideContainerColor,
                radius = 7.dp.toPx(),
                center = startPosition
            )
            drawCircle(
                color = guideColor,
                radius = 2.5.dp.toPx(),
                center = startPosition
            )

            drawCircle(
                color = guideColor.copy(alpha = 0.2f),
                radius = 25.dp.toPx(),
                center = currentPosition
            )
            drawCircle(
                color = guideShadowColor,
                radius = 16.dp.toPx(),
                center = currentPosition,
                style = Stroke(width = lineWidth + 4.dp.toPx())
            )
            drawCircle(
                color = guideColor,
                radius = 16.dp.toPx(),
                center = currentPosition,
                style = Stroke(width = lineWidth)
            )
        }

        Surface(
            modifier = Modifier
                .offset { indicatorOffset }
                .defaultMinSize(minHeight = 40.dp)
                .onSizeChanged { indicatorSize = it },
            shape = ShapeDefaults.extraLarge,
            color = guideContainerColor,
            contentColor = guideContentColor,
            tonalElevation = 6.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.RotateRight,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text(
                    text = "↔ ${yawDelta.signedDegrees()}  ↕ ${pitchDelta.signedDegrees()}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun Int.signedDegrees(): String = when {
    this > 0 -> "+$this°"
    this < 0 -> "−${abs(this)}°"
    else -> "0°"
}