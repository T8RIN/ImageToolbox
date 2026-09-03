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

data class FractalCoefficients(
    val a: Double = 0.0,
    val b: Double = 0.0,
    val c: Double = 0.0,
    val d: Double = 0.0
) {

    fun normalized(specs: FractalCoefficientSpecs): FractalCoefficients = copy(
        a = a.normalized(specs.a),
        b = b.normalized(specs.b),
        c = c.normalized(specs.c),
        d = d.normalized(specs.d)
    )
}

data class FractalCoefficientSpecs(
    val a: FractalCoefficientSpec? = null,
    val b: FractalCoefficientSpec? = null,
    val c: FractalCoefficientSpec? = null,
    val d: FractalCoefficientSpec? = null
) {

    val isEmpty: Boolean
        get() = a == null && b == null && c == null && d == null

    val defaults: FractalCoefficients
        get() = FractalCoefficients(
            a = a?.defaultValue ?: 0.0,
            b = b?.defaultValue ?: 0.0,
            c = c?.defaultValue ?: 0.0,
            d = d?.defaultValue ?: 0.0
        )

    companion object {
        val None = FractalCoefficientSpecs()
    }
}

data class FractalCoefficientSpec(
    val label: FractalCoefficientLabel,
    val defaultValue: Double,
    val valueRange: ClosedFloatingPointRange<Double>
)

enum class FractalCoefficientLabel {
    A,
    B,
    C,
    D,
    Sigma,
    Rho,
    Beta,
    Scale,
    Fold,
    MinimumRadius
}

private fun Double.normalized(spec: FractalCoefficientSpec?): Double {
    if (spec == null) return 0.0

    return takeIf(Double::isFinite)
        ?.coerceIn(spec.valueRange)
        ?: spec.defaultValue
}
