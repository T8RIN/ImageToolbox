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

package com.t8rin.curves

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSlider
import kotlin.math.min
import kotlin.math.sqrt

@Composable
internal fun ColorWheelsEditor(
    value: ColorWheelsValue,
    onValueChange: (ColorWheelsValue) -> Unit,
    colors: ImageCurvesEditorColors,
    shape: Shape,
    modifier: Modifier = Modifier,
    imageContent: (@Composable () -> Unit)? = null
) {
    var currentValue by remember(value) {
        mutableStateOf(value.normalized())
    }
    val updateValue: (ColorWheelsValue) -> Unit = { updatedValue ->
        currentValue = updatedValue.normalized()
        onValueChange(currentValue)
    }

    Box(
        modifier = modifier.padding(8.dp)
    ) {
        imageContent?.invoke()
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (imageContent != null) {
                        colors.editorBackgroundColor
                    } else {
                        Color.Transparent
                    },
                    shape = shape
                )
                .padding(horizontal = 3.dp, vertical = 5.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                ColorWheel(
                    title = stringResource(R.string.shadows),
                    point = currentValue.shadows,
                    onPointChange = {
                        updateValue(currentValue.copy(shadows = it))
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                ColorWheel(
                    title = stringResource(R.string.midtones),
                    point = currentValue.midtones,
                    onPointChange = {
                        updateValue(currentValue.copy(midtones = it))
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
                ColorWheel(
                    title = stringResource(R.string.highlights),
                    point = currentValue.highlights,
                    onPointChange = {
                        updateValue(currentValue.copy(highlights = it))
                    },
                    colors = colors,
                    modifier = Modifier.weight(1f)
                )
            }
            EnhancedSlider(
                value = currentValue.edges,
                onValueChange = {
                    updateValue(currentValue.copy(edges = it))
                },
                valueRange = ColorWheelsValue.MinEdges..ColorWheelsValue.MaxEdges,
                steps = 14,
                modifier = Modifier.fillMaxWidth(1f)
            )
        }
    }
}

@Composable
private fun ColorWheel(
    title: String,
    point: ColorWheelPoint,
    onPointChange: (ColorWheelPoint) -> Unit,
    colors: ImageCurvesEditorColors,
    modifier: Modifier = Modifier
) {
    val currentOnPointChange by rememberUpdatedState(onPointChange)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    fun updatePoint(position: Offset) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = (min(size.width, size.height) / 2f)
                            .coerceAtLeast(1f)
                        currentOnPointChange(
                            ColorWheelPoint(
                                x = (position.x - center.x) / radius,
                                y = (position.y - center.y) / radius
                            ).normalized()
                        )
                    }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        updatePoint(down.position)
                        down.consume()
                        var pressed = true
                        while (pressed) {
                            val event = awaitPointerEvent()
                            val change = event.changes
                                .firstOrNull { it.id == down.id }
                                ?: break
                            pressed = change.pressed
                            if (pressed) {
                                updatePoint(change.position)
                            }
                            change.consume()
                        }
                    }
                }
        ) {
            val radius = min(size.width, size.height) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val configuredHueColors = colors.hueCurveColors.ifEmpty {
                FallbackHueColors
            }
            val baseHueColors = if (
                configuredHueColors.size > 1 &&
                configuredHueColors.first() == configuredHueColors.last()
            ) {
                configuredHueColors.dropLast(1)
            } else {
                configuredHueColors
            }
            val hueColors = if (baseHueColors.isEmpty()) {
                FallbackHueColors
            } else {
                listOf(baseHueColors.first()) +
                        baseHueColors.drop(1).asReversed() +
                        baseHueColors.first()
            }
            rotate(degrees = -90f, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = hueColors,
                        center = center
                    ),
                    radius = radius,
                    center = center
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0f to Color.White.copy(alpha = 0.98f),
                        0.36f to Color.White.copy(alpha = 0.62f),
                        0.68f to Color.White.copy(alpha = 0.12f),
                        1f to Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
            val guideColor = colors.guidelinesColor
                .blend(Color.Black)
                .copy(alpha = 0.65f)
            drawLine(
                color = guideColor,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 0.75.dp.toPx()
            )
            drawLine(
                color = guideColor,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 0.75.dp.toPx()
            )
            drawCircle(
                color = guideColor,
                radius = radius,
                center = center,
                style = Stroke(0.75.dp.toPx())
            )

            val normalizedPoint = point.normalized()
            val pointCenter = Offset(
                x = center.x + normalizedPoint.x * radius,
                y = center.y + normalizedPoint.y * radius
            )
            val pointRadius = min(5.dp.toPx(), radius * 0.085f)
            val distance = sqrt(
                normalizedPoint.x * normalizedPoint.x +
                        normalizedPoint.y * normalizedPoint.y
            )
            val pointColor = if (distance < 0.01f) {
                colors.lumaCurveColor
            } else {
                hueColors.colorAt(
                    (
                            kotlin.math.atan2(
                                normalizedPoint.y,
                                normalizedPoint.x
                            ) / (2f * Math.PI.toFloat()) + 0.25f + 1f
                            ) % 1f
                )
            }
            drawCircle(
                color = Color.Black.copy(alpha = 0.65f),
                radius = pointRadius + 2.dp.toPx(),
                center = pointCenter
            )
            drawCircle(
                color = pointColor,
                radius = pointRadius,
                center = pointCenter
            )
            drawCircle(
                color = Color.White,
                radius = pointRadius + 1.dp.toPx(),
                center = pointCenter,
                style = Stroke(1.5.dp.toPx())
            )
        }
    }
}

private fun List<Color>.colorAt(position: Float): Color {
    if (isEmpty()) return Color.White
    if (size == 1) return first()
    val scaledIndex = position.coerceIn(0f, 1f) * lastIndex
    val lowerIndex = scaledIndex.toInt().coerceAtMost(lastIndex - 1)
    return lerp(
        start = this[lowerIndex],
        stop = this[lowerIndex + 1],
        fraction = scaledIndex - lowerIndex
    )
}

private val FallbackHueColors = listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red
)
