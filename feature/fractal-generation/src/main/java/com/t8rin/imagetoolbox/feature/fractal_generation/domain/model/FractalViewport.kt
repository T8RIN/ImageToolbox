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

package com.t8rin.imagetoolbox.feature.fractal_generation.domain.model

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.log10

/**
 * A complex-plane viewport whose [span] is its vertical extent.
 *
 * Screen coordinates passed to [pointAt], [zoomBy], and [panBy] are normalized to `0..1`.
 * The aspect ratio is `width / height`. Positive pan X moves the viewport towards larger real
 * values; positive pan Y moves it down the screen, towards smaller imaginary values.
 */
data class FractalViewport(
    val centerReal: BigDecimal,
    val centerImaginary: BigDecimal,
    val span: BigDecimal
) {

    val decimalZoomDepth: Double
        get() = (DEFAULT_SPAN.log10Magnitude() - normalized().span.log10Magnitude())
            .coerceAtLeast(0.0)

    fun normalized(): FractalViewport = copy(
        centerReal = centerReal.takeUnless { it.isUnreasonablyLarge() } ?: BigDecimal.ZERO,
        centerImaginary = centerImaginary.takeUnless { it.isUnreasonablyLarge() }
            ?: BigDecimal.ZERO,
        span = when {
            span.signum() <= 0 -> DEFAULT_SPAN
            span < MIN_SPAN -> MIN_SPAN
            span > MAX_SPAN -> MAX_SPAN
            else -> span
        }
    )

    fun horizontalSpan(aspectRatio: Double): BigDecimal {
        val viewport = normalized()
        val context = viewport.mathContext()
        return viewport.span.multiply(aspectRatio.normalizedAspect().toBigDecimal(), context)
    }

    fun pointAt(
        normalizedX: Double,
        normalizedY: Double,
        aspectRatio: Double
    ): FractalCoordinate {
        val viewport = normalized()
        val context = viewport.mathContext()
        val x = normalizedX.normalizedAnchor().toBigDecimal()
        val y = normalizedY.normalizedAnchor().toBigDecimal()
        val half = HALF
        val horizontalSpan = viewport.horizontalSpan(aspectRatio)

        return FractalCoordinate(
            real = viewport.centerReal.add(
                x.subtract(half, context).multiply(horizontalSpan, context),
                context
            ),
            imaginary = viewport.centerImaginary.add(
                half.subtract(y, context).multiply(viewport.span, context),
                context
            )
        )
    }

    /**
     * Returns a viewport zoomed around the supplied screen anchor. Values above one zoom in.
     */
    fun zoomBy(
        factor: Double,
        anchorX: Double = 0.5,
        anchorY: Double = 0.5,
        aspectRatio: Double = 1.0
    ): FractalViewport {
        val viewport = normalized()
        val safeFactor = factor.takeIf { it.isFinite() && it > 0.0 } ?: 1.0
        val context = viewport.mathContext(extraDigits = 12)
        val anchor = viewport.pointAt(anchorX, anchorY, aspectRatio)
        val newSpan = viewport.span
            .divide(safeFactor.toBigDecimal(), context)
            .coerceIn(MIN_SPAN, MAX_SPAN)
        val newHorizontalSpan = newSpan.multiply(
            aspectRatio.normalizedAspect().toBigDecimal(),
            context
        )
        val xOffset = anchorX.normalizedAnchor().toBigDecimal().subtract(HALF, context)
        val yOffset = HALF.subtract(anchorY.normalizedAnchor().toBigDecimal(), context)

        return FractalViewport(
            centerReal = anchor.real.subtract(
                xOffset.multiply(newHorizontalSpan, context),
                context
            ),
            centerImaginary = anchor.imaginary.subtract(
                yOffset.multiply(newSpan, context),
                context
            ),
            span = newSpan
        ).normalized()
    }

    fun panBy(
        normalizedDeltaX: Double,
        normalizedDeltaY: Double,
        aspectRatio: Double = 1.0
    ): FractalViewport {
        val viewport = normalized()
        val context = viewport.mathContext(extraDigits = 12)
        val dx = normalizedDeltaX.takeIf(Double::isFinite) ?: 0.0
        val dy = normalizedDeltaY.takeIf(Double::isFinite) ?: 0.0

        return viewport.copy(
            centerReal = viewport.centerReal.add(
                viewport.horizontalSpan(aspectRatio).multiply(dx.toBigDecimal(), context),
                context
            ),
            centerImaginary = viewport.centerImaginary.subtract(
                viewport.span.multiply(dy.toBigDecimal(), context),
                context
            )
        )
    }

    internal fun mathContext(extraDigits: Int = 0): MathContext {
        val safeSpan = normalized().span.stripTrailingZeros()
        val digitsAfterDecimal = (safeSpan.scale() - safeSpan.precision() + 1).coerceAtLeast(0)
        val precision = (BASE_PRECISION + digitsAfterDecimal + extraDigits)
            .coerceAtMost(MAX_PRECISION)
        return MathContext(precision, RoundingMode.HALF_EVEN)
    }

    companion object {
        val MIN_SPAN: BigDecimal = BigDecimal("1E-300")
        val MIN_DIRECT_RENDER_SPAN: BigDecimal = BigDecimal("1E-12")
        val MAX_SPAN: BigDecimal = BigDecimal("1E6")
        val DEFAULT_SPAN: BigDecimal = BigDecimal("3")

        val Default = FractalViewport(
            centerReal = BigDecimal("-0.5"),
            centerImaginary = BigDecimal.ZERO,
            span = DEFAULT_SPAN
        )

        fun of(
            centerReal: String,
            centerImaginary: String,
            span: String
        ): FractalViewport = FractalViewport(
            centerReal = centerReal.toBigDecimal(),
            centerImaginary = centerImaginary.toBigDecimal(),
            span = span.toBigDecimal()
        ).normalized()

        private val HALF = BigDecimal("0.5")
        private const val BASE_PRECISION = 40
        private const val MAX_PRECISION = 352
    }
}

data class FractalCoordinate(
    val real: BigDecimal,
    val imaginary: BigDecimal
)

private fun Double.normalizedAspect(): Double = takeIf { it.isFinite() && it > 0.0 } ?: 1.0

private fun Double.normalizedAnchor(): Double = takeIf(Double::isFinite)?.coerceIn(0.0, 1.0) ?: 0.5

private fun BigDecimal.isUnreasonablyLarge(): Boolean = precision() - scale() > 1_000_000

private fun BigDecimal.log10Magnitude(): Double {
    val value = abs().stripTrailingZeros()
    if (value.signum() == 0) return Double.NEGATIVE_INFINITY

    val exponent = value.precision() - value.scale() - 1
    val mantissa = value.movePointLeft(exponent).toDouble()
    return exponent + log10(mantissa)
}
