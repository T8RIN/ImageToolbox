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

internal fun FractalParams.formattedZoom(): String {
    val factor = if (formula.isThreeDimensional) {
        BigDecimal.valueOf(formula.defaultCamera.distance).divide(
            BigDecimal.valueOf(camera.distance),
            ZOOM_CONTEXT
        )
    } else {
        formula.defaultViewport.span.divide(viewport.normalized().span, ZOOM_CONTEXT)
    }
    val rounded = factor.round(DISPLAY_CONTEXT).stripTrailingZeros()
    val exponent = rounded.precision() - rounded.scale() - 1

    return if (exponent in PLAIN_MIN_EXPONENT..PLAIN_MAX_EXPONENT) {
        rounded.toPlainString()
    } else {
        val mantissa = rounded.movePointLeft(exponent).stripTrailingZeros().toPlainString()
        "${mantissa}e$exponent"
    }
}

private val ZOOM_CONTEXT = MathContext(340, RoundingMode.HALF_EVEN)
private val DISPLAY_CONTEXT = MathContext(3, RoundingMode.HALF_UP)
private const val PLAIN_MIN_EXPONENT = -2
private const val PLAIN_MAX_EXPONENT = 5
