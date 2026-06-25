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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SimpleWavyShape(
    private val size: Dp,
    private val waveLength: Dp = WavyShape.DefaultWaveLength,
    private val waveHeight: Dp = WavyShape.DefaultWaveHeight,
) : Shape {
    @Suppress("UnnecessaryVariable")
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = with(density) { this@SimpleWavyShape.size.toPx() }
            .coerceAtMost(min(size.width, size.height) / 2f)
        val wavelength = with(density) { waveLength.toPx() }.coerceAtLeast(1f)
        val amplitude = with(density) { waveHeight.toPx() }
            .coerceAtMost(min(size.width, size.height) / 4f)
            .coerceAtMost(radius / 2f)
        val centerRadius = (radius - amplitude).coerceAtLeast(0f)
        val left = amplitude
        val top = amplitude
        val right = size.width - amplitude
        val bottom = size.height - amplitude
        val horizontalLength = (right - left - centerRadius * 2f).coerceAtLeast(0f)
        val verticalLength = (bottom - top - centerRadius * 2f).coerceAtLeast(0f)
        val arcLength = (PI.toFloat() * centerRadius) / 2f
        val perimeter = horizontalLength * 2f + verticalLength * 2f + arcLength * 4f

        if (perimeter <= 0f) return Outline.Generic(Path())

        val waves = ceil(perimeter / wavelength).toInt().coerceAtLeast(1)
        val actualWavelength = perimeter / waves
        val sampleStep = (actualWavelength / 8f).coerceAtLeast(1f)

        val path = Path().apply {
            var distance = 0f
            var isFirstPoint = true

            fun addPoint(
                x: Float,
                y: Float,
                normalX: Float,
                normalY: Float,
                pointDistance: Float
            ) {
                val wave = amplitude * sin(2f * PI.toFloat() * pointDistance / actualWavelength)
                val pointX = x + normalX * wave
                val pointY = y + normalY * wave

                if (isFirstPoint) {
                    moveTo(pointX, pointY)
                    isFirstPoint = false
                } else {
                    lineTo(pointX, pointY)
                }
            }

            fun addLine(
                fromX: Float,
                fromY: Float,
                toX: Float,
                toY: Float,
                normalX: Float,
                normalY: Float,
                length: Float
            ) {
                val steps = ceil(length / sampleStep).toInt().coerceAtLeast(1)

                repeat(steps + 1) { index ->
                    val progress = index / steps.toFloat()
                    addPoint(
                        x = fromX + (toX - fromX) * progress,
                        y = fromY + (toY - fromY) * progress,
                        normalX = normalX,
                        normalY = normalY,
                        pointDistance = distance + length * progress
                    )
                }
                distance += length
            }

            fun addArc(
                centerX: Float,
                centerY: Float,
                startAngle: Float,
                sweepAngle: Float
            ) {
                val length = abs(sweepAngle) * centerRadius
                val steps = ceil(length / sampleStep).toInt().coerceAtLeast(1)

                repeat(steps + 1) { index ->
                    val progress = index / steps.toFloat()
                    val angle = startAngle + sweepAngle * progress
                    val normalX = cos(angle)
                    val normalY = sin(angle)
                    addPoint(
                        x = centerX + centerRadius * normalX,
                        y = centerY + centerRadius * normalY,
                        normalX = normalX,
                        normalY = normalY,
                        pointDistance = distance + length * progress
                    )
                }
                distance += length
            }

            addLine(
                fromX = left + centerRadius,
                fromY = top,
                toX = right - centerRadius,
                toY = top,
                normalX = 0f,
                normalY = -1f,
                length = horizontalLength
            )
            addArc(
                centerX = right - centerRadius,
                centerY = top + centerRadius,
                startAngle = -PI.toFloat() / 2f,
                sweepAngle = PI.toFloat() / 2f
            )
            addLine(
                fromX = right,
                fromY = top + centerRadius,
                toX = right,
                toY = bottom - centerRadius,
                normalX = 1f,
                normalY = 0f,
                length = verticalLength
            )
            addArc(
                centerX = right - centerRadius,
                centerY = bottom - centerRadius,
                startAngle = 0f,
                sweepAngle = PI.toFloat() / 2f
            )
            addLine(
                fromX = right - centerRadius,
                fromY = bottom,
                toX = left + centerRadius,
                toY = bottom,
                normalX = 0f,
                normalY = 1f,
                length = horizontalLength
            )
            addArc(
                centerX = left + centerRadius,
                centerY = bottom - centerRadius,
                startAngle = PI.toFloat() / 2f,
                sweepAngle = PI.toFloat() / 2f
            )
            addLine(
                fromX = left,
                fromY = bottom - centerRadius,
                toX = left,
                toY = top + centerRadius,
                normalX = -1f,
                normalY = 0f,
                length = verticalLength
            )
            addArc(
                centerX = left + centerRadius,
                centerY = top + centerRadius,
                startAngle = PI.toFloat(),
                sweepAngle = PI.toFloat() / 2f
            )
            close()
        }
        return Outline.Generic(path)
    }
}