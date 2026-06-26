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

@file:Suppress("unused")

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.content.res.Resources
import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Immutable
class WavyShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    private val waveLength: Dp = DefaultWaveLength,
    private val waveHeight: Dp = DefaultWaveHeight,
    private val maxPathPoints: Float = MAX_PATH_POINTS,
    private val sampleStepsPerWave: Float = SAMPLE_STEPS_PER_WAVE
) : CornerBasedShape(
    topStart = topStart,
    topEnd = topEnd,
    bottomEnd = bottomEnd,
    bottomStart = bottomStart
) {
    private var outlineCache: WavyShapeOutlineCache? = null

    constructor(
        size: CornerSize,
        waveLength: Dp = DefaultWaveLength,
        waveHeight: Dp = DefaultWaveHeight
    ) : this(
        topStart = size,
        topEnd = size,
        bottomEnd = size,
        bottomStart = size,
        waveLength = waveLength,
        waveHeight = waveHeight
    )

    constructor(
        size: Dp,
        waveLength: Dp = DefaultWaveLength,
        waveHeight: Dp = DefaultWaveHeight
    ) : this(
        size = CornerSize(size),
        waveLength = waveLength,
        waveHeight = waveHeight
    )

    constructor(
        @IntRange(from = 0, to = 100) percent: Int,
        waveLength: Dp = DefaultWaveLength,
        waveHeight: Dp = DefaultWaveHeight
    ) : this(
        size = CornerSize(percent),
        waveLength = waveLength,
        waveHeight = waveHeight
    )

    constructor(
        topStart: Dp = 0.dp,
        topEnd: Dp = 0.dp,
        bottomEnd: Dp = 0.dp,
        bottomStart: Dp = 0.dp,
        waveLength: Dp = DefaultWaveLength,
        waveHeight: Dp = DefaultWaveHeight
    ) : this(
        topStart = CornerSize(topStart),
        topEnd = CornerSize(topEnd),
        bottomEnd = CornerSize(bottomEnd),
        bottomStart = CornerSize(bottomStart),
        waveLength = waveLength,
        waveHeight = waveHeight
    )

    @Suppress("UnnecessaryVariable")
    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline {
        val maxRadius = min(size.width, size.height) / 2f
        val topLeftRadius = (if (layoutDirection == Ltr) topStart else topEnd)
            .coerceIn(0f, maxRadius)
        val topRightRadius = (if (layoutDirection == Ltr) topEnd else topStart)
            .coerceIn(0f, maxRadius)
        val bottomRightRadius = (if (layoutDirection == Ltr) bottomEnd else bottomStart)
            .coerceIn(0f, maxRadius)
        val bottomLeftRadius = (if (layoutDirection == Ltr) bottomStart else bottomEnd)
            .coerceIn(0f, maxRadius)

        val density = Resources.getSystem().displayMetrics.density
        outlineCache?.takeIf {
            it.size == size &&
                    it.topLeftRadius == topLeftRadius &&
                    it.topRightRadius == topRightRadius &&
                    it.bottomRightRadius == bottomRightRadius &&
                    it.bottomLeftRadius == bottomLeftRadius &&
                    it.density == density
        }?.let { return it.outline }

        val minDimension = min(size.width, size.height)
        val waveScale = (minDimension / (MIN_ADAPTIVE_SIZE.value * density))
            .coerceIn(MIN_WAVE_SCALE, 1f)
        val wavelength = (waveLength.value * density * waveScale).coerceAtLeast(1f)
        val amplitude = (waveHeight.value * density * waveScale)
            .coerceAtMost(minDimension / 4f)
            .coerceAtLeast(0f)
        val topWaveAmplitude = sideAmplitude(
            firstRadius = topLeftRadius,
            secondRadius = topRightRadius,
            amplitude = amplitude
        )
        val rightWaveAmplitude = sideAmplitude(
            firstRadius = topRightRadius,
            secondRadius = bottomRightRadius,
            amplitude = amplitude
        )
        val bottomWaveAmplitude = sideAmplitude(
            firstRadius = bottomRightRadius,
            secondRadius = bottomLeftRadius,
            amplitude = amplitude
        )
        val leftWaveAmplitude = sideAmplitude(
            firstRadius = bottomLeftRadius,
            secondRadius = topLeftRadius,
            amplitude = amplitude
        )

        if (amplitude <= 0f || size.width <= 0f || size.height <= 0f) {
            return roundedOutline(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius
            ).cache(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius,
                density = density
            )
        }

        if (
            topWaveAmplitude <= 0f &&
            rightWaveAmplitude <= 0f &&
            bottomWaveAmplitude <= 0f &&
            leftWaveAmplitude <= 0f
        ) {
            return roundedOutline(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius
            ).cache(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius,
                density = density
            )
        }

        val topLeftCenterRadius = (topLeftRadius - amplitude).coerceAtLeast(0f)
        val topRightCenterRadius = (topRightRadius - amplitude).coerceAtLeast(0f)
        val bottomRightCenterRadius = (bottomRightRadius - amplitude).coerceAtLeast(0f)
        val bottomLeftCenterRadius = (bottomLeftRadius - amplitude).coerceAtLeast(0f)
        val left = leftWaveAmplitude
        val top = topWaveAmplitude
        val right = size.width - rightWaveAmplitude
        val bottom = size.height - bottomWaveAmplitude
        val topLineStartX = if (topLeftRadius > 0f) {
            left + topLeftCenterRadius
        } else 0f
        val topLineEndX = if (topRightRadius > 0f) {
            right - topRightCenterRadius
        } else size.width
        val rightLineStartY = if (topRightRadius > 0f) {
            top + topRightCenterRadius
        } else 0f
        val rightLineEndY = if (bottomRightRadius > 0f) {
            bottom - bottomRightCenterRadius
        } else size.height
        val bottomLineStartX = if (bottomRightRadius > 0f) {
            right - bottomRightCenterRadius
        } else size.width
        val bottomLineEndX = if (bottomLeftRadius > 0f) {
            left + bottomLeftCenterRadius
        } else 0f
        val leftLineStartY = if (bottomLeftRadius > 0f) {
            bottom - bottomLeftCenterRadius
        } else size.height
        val leftLineEndY = if (topLeftRadius > 0f) {
            top + topLeftCenterRadius
        } else 0f
        val topLength = (topLineEndX - topLineStartX)
            .coerceAtLeast(0f)
        val rightLength = (rightLineEndY - rightLineStartY)
            .coerceAtLeast(0f)
        val bottomLength = (bottomLineStartX - bottomLineEndX)
            .coerceAtLeast(0f)
        val leftLength = (leftLineStartY - leftLineEndY)
            .coerceAtLeast(0f)
        val topLeftArcLength = (PI.toFloat() * topLeftCenterRadius) / 2f
        val topRightArcLength = (PI.toFloat() * topRightCenterRadius) / 2f
        val bottomRightArcLength = (PI.toFloat() * bottomRightCenterRadius) / 2f
        val bottomLeftArcLength = (PI.toFloat() * bottomLeftCenterRadius) / 2f
        val perimeter = topLength + rightLength + bottomLength + leftLength +
                topLeftArcLength + topRightArcLength + bottomRightArcLength + bottomLeftArcLength

        if (perimeter <= 0f) {
            return roundedOutline(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius
            ).cache(
                size = size,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomRightRadius = bottomRightRadius,
                bottomLeftRadius = bottomLeftRadius,
                density = density
            )
        }

        val waves = ceil(perimeter / wavelength).toInt().coerceAtLeast(1)
        val actualWavelength = perimeter / waves
        val sampleStep = maxOf(
            actualWavelength / sampleStepsPerWave,
            perimeter / maxPathPoints,
            1f
        )

        val path = Path().apply {
            var distance = 0f
            var hasPreviousPoint = false
            var firstPointX = 0f
            var firstPointY = 0f
            var previousPointX = 0f
            var previousPointY = 0f

            fun addPoint(
                x: Float,
                y: Float,
                normalX: Float,
                normalY: Float,
                pointDistance: Float,
                waveAmplitude: Float
            ) {
                val wave = waveAmplitude * sin(
                    2f * PI.toFloat() * pointDistance / actualWavelength
                )
                val pointX = x + normalX * wave
                val pointY = y + normalY * wave

                if (!hasPreviousPoint) {
                    moveTo(pointX, pointY)
                    hasPreviousPoint = true
                    firstPointX = pointX
                    firstPointY = pointY
                } else {
                    quadraticTo(
                        x1 = previousPointX,
                        y1 = previousPointY,
                        x2 = (previousPointX + pointX) / 2f,
                        y2 = (previousPointY + pointY) / 2f
                    )
                }

                previousPointX = pointX
                previousPointY = pointY
            }

            fun addLine(
                fromX: Float,
                fromY: Float,
                toX: Float,
                toY: Float,
                normalX: Float,
                normalY: Float,
                length: Float,
                waveAmplitude: Float
            ) {
                val steps = ceil(length / sampleStep).toInt().coerceAtLeast(1)

                val firstIndex = if (!hasPreviousPoint) 0 else 1
                for (index in firstIndex..steps) {
                    val progress = index / steps.toFloat()
                    addPoint(
                        x = fromX + (toX - fromX) * progress,
                        y = fromY + (toY - fromY) * progress,
                        normalX = normalX,
                        normalY = normalY,
                        pointDistance = distance + length * progress,
                        waveAmplitude = waveAmplitude
                    )
                }
                distance += length
            }

            fun addArc(
                centerX: Float,
                centerY: Float,
                radius: Float,
                startAngle: Float,
                sweepAngle: Float,
                waveAmplitude: Float
            ) {
                val length = abs(sweepAngle) * radius
                val steps = ceil(length / sampleStep).toInt().coerceAtLeast(1)

                val firstIndex = if (!hasPreviousPoint) 0 else 1
                for (index in firstIndex..steps) {
                    val progress = index / steps.toFloat()
                    val angle = startAngle + sweepAngle * progress
                    val normalX = cos(angle)
                    val normalY = sin(angle)
                    addPoint(
                        x = centerX + radius * normalX,
                        y = centerY + radius * normalY,
                        normalX = normalX,
                        normalY = normalY,
                        pointDistance = distance + length * progress,
                        waveAmplitude = waveAmplitude
                    )
                }
                distance += length
            }

            addLine(
                fromX = topLineStartX,
                fromY = top,
                toX = topLineEndX,
                toY = top,
                normalX = 0f,
                normalY = -1f,
                length = topLength,
                waveAmplitude = topWaveAmplitude
            )
            addArc(
                centerX = right - topRightCenterRadius,
                centerY = top + topRightCenterRadius,
                radius = topRightCenterRadius,
                startAngle = -PI.toFloat() / 2f,
                sweepAngle = PI.toFloat() / 2f,
                waveAmplitude = amplitude
            )
            addLine(
                fromX = right,
                fromY = rightLineStartY,
                toX = right,
                toY = rightLineEndY,
                normalX = 1f,
                normalY = 0f,
                length = rightLength,
                waveAmplitude = rightWaveAmplitude
            )
            addArc(
                centerX = right - bottomRightCenterRadius,
                centerY = bottom - bottomRightCenterRadius,
                radius = bottomRightCenterRadius,
                startAngle = 0f,
                sweepAngle = PI.toFloat() / 2f,
                waveAmplitude = amplitude
            )
            addLine(
                fromX = bottomLineStartX,
                fromY = bottom,
                toX = bottomLineEndX,
                toY = bottom,
                normalX = 0f,
                normalY = 1f,
                length = bottomLength,
                waveAmplitude = bottomWaveAmplitude
            )
            addArc(
                centerX = left + bottomLeftCenterRadius,
                centerY = bottom - bottomLeftCenterRadius,
                radius = bottomLeftCenterRadius,
                startAngle = PI.toFloat() / 2f,
                sweepAngle = PI.toFloat() / 2f,
                waveAmplitude = amplitude
            )
            addLine(
                fromX = left,
                fromY = leftLineStartY,
                toX = left,
                toY = leftLineEndY,
                normalX = -1f,
                normalY = 0f,
                length = leftLength,
                waveAmplitude = leftWaveAmplitude
            )
            addArc(
                centerX = left + topLeftCenterRadius,
                centerY = top + topLeftCenterRadius,
                radius = topLeftCenterRadius,
                startAngle = PI.toFloat(),
                sweepAngle = PI.toFloat() / 2f,
                waveAmplitude = amplitude
            )
            quadraticTo(
                x1 = previousPointX,
                y1 = previousPointY,
                x2 = firstPointX,
                y2 = firstPointY
            )
            close()
        }
        return Outline.Generic(path).cache(
            size = size,
            topLeftRadius = topLeftRadius,
            topRightRadius = topRightRadius,
            bottomRightRadius = bottomRightRadius,
            bottomLeftRadius = bottomLeftRadius,
            density = density
        )
    }

    private fun sideAmplitude(
        firstRadius: Float,
        secondRadius: Float,
        amplitude: Float
    ): Float {
        return if (firstRadius > 0f || secondRadius > 0f) amplitude else 0f
    }

    private fun roundedOutline(
        size: Size,
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float
    ): Outline {
        return Outline.Rounded(
            RoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                topLeftCornerRadius = CornerRadius(topLeftRadius),
                topRightCornerRadius = CornerRadius(topRightRadius),
                bottomRightCornerRadius = CornerRadius(bottomRightRadius),
                bottomLeftCornerRadius = CornerRadius(bottomLeftRadius)
            )
        )
    }

    private fun Outline.cache(
        size: Size,
        topLeftRadius: Float,
        topRightRadius: Float,
        bottomRightRadius: Float,
        bottomLeftRadius: Float,
        density: Float
    ): Outline {
        outlineCache = WavyShapeOutlineCache(
            size = size,
            topLeftRadius = topLeftRadius,
            topRightRadius = topRightRadius,
            bottomRightRadius = bottomRightRadius,
            bottomLeftRadius = bottomLeftRadius,
            density = density,
            outline = this
        )
        return this
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): WavyShape {
        return WavyShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            waveLength = waveLength,
            waveHeight = waveHeight,
            maxPathPoints = maxPathPoints,
            sampleStepsPerWave = sampleStepsPerWave
        )
    }

    fun copy(
        topStart: CornerSize = this.topStart,
        topEnd: CornerSize = this.topEnd,
        bottomEnd: CornerSize = this.bottomEnd,
        bottomStart: CornerSize = this.bottomStart,
        waveLength: Dp = this.waveLength,
        waveHeight: Dp = this.waveHeight
    ): WavyShape {
        return WavyShape(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
            waveLength = waveLength,
            waveHeight = waveHeight,
            maxPathPoints = maxPathPoints,
            sampleStepsPerWave = sampleStepsPerWave
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WavyShape) return false

        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomEnd != other.bottomEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (waveLength != other.waveLength) return false
        if (waveHeight != other.waveHeight) return false
        if (maxPathPoints != other.maxPathPoints) return false
        if (sampleStepsPerWave != other.sampleStepsPerWave) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + waveLength.hashCode()
        result = 31 * result + waveHeight.hashCode()
        result = 31 * result + maxPathPoints.hashCode()
        result = 31 * result + sampleStepsPerWave.hashCode()
        return result
    }

    override fun toString(): String {
        return "WavyShape(topStart=$topStart, topEnd=$topEnd, bottomEnd=$bottomEnd, " +
                "bottomStart=$bottomStart, waveLength=$waveLength, waveHeight=$waveHeight, " +
                "maxPathPoints=$maxPathPoints, sampleStepsPerWave=$sampleStepsPerWave)"
    }

    companion object {
        val DefaultWaveLength = 12.dp
        val DefaultWaveHeight = 0.9.dp
        private const val MAX_PATH_POINTS = 360f
        private const val SAMPLE_STEPS_PER_WAVE = 8f
        private const val MIN_WAVE_SCALE = 0.42f
        private val MIN_ADAPTIVE_SIZE = 48.dp
    }
}

private data class WavyShapeOutlineCache(
    val size: Size,
    val topLeftRadius: Float,
    val topRightRadius: Float,
    val bottomRightRadius: Float,
    val bottomLeftRadius: Float,
    val density: Float,
    val outline: Outline
)