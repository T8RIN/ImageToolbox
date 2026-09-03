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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import kotlin.math.abs
import kotlin.math.floor

data class FractalParams(
    val formula: FractalFormula = FractalFormula.Mandelbrot,
    val viewport: FractalViewport = formula.defaultViewport,
    val power: Double = formula.defaultPower,
    val iterations: Int = formula.defaultIterations,
    val bailout: Double = formula.defaultBailout,
    val iterationPolicy: FractalIterationPolicy = FractalIterationPolicy.Adaptive,
    val palette: GradientPalette = GradientPalette.Classic,
    val coloring: FractalColoring = FractalColoring.Smooth,
    val paletteCycles: Double = DEFAULT_PALETTE_CYCLES,
    val paletteOffset: Double = 0.0,
    val insideColor: ColorModel = ColorModel(DEFAULT_INSIDE_COLOR),
    val juliaConstant: FractalComplex = formula.defaultJuliaConstant,
    val phoenixConstant: FractalComplex = formula.defaultPhoenixConstant,
    val novaRelaxation: Double = formula.defaultNovaRelaxation,
    val camera: FractalCamera = formula.defaultCamera,
    val quaternionConstant: FractalQuaternion = formula.defaultQuaternionConstant,
    val coefficients: FractalCoefficients = formula.coefficientSpecs.defaults,
    val showFloor: Boolean = formula.supportsFloor,
    val floorPrimaryColor: ColorModel = ColorModel(DEFAULT_FLOOR_PRIMARY_COLOR),
    val floorSecondaryColor: ColorModel = ColorModel(DEFAULT_FLOOR_SECONDARY_COLOR),
    val supersampling: Int = 1
) {

    val isDeepZoomAvailable: Boolean
        get() = formula.supportsDeepZoom && abs(power - DEEP_ZOOM_POWER) < POWER_EPSILON

    fun normalized(): FractalParams {
        val normalizedPower = power
            .takeIf(Double::isFinite)
            ?.coerceIn(MIN_POWER, MAX_POWER)
            ?.let { value ->
                if (abs(value - DEEP_ZOOM_POWER) < POWER_EPSILON) {
                    DEEP_ZOOM_POWER
                } else {
                    value
                }
            }
            ?: formula.defaultPower
        val deepZoomAvailable = formula.supportsDeepZoom &&
                abs(normalizedPower - DEEP_ZOOM_POWER) < POWER_EPSILON
        val normalizedViewport = viewport.normalized().let { normalizedViewport ->
            if (deepZoomAvailable || normalizedViewport.span >= FractalViewport.MIN_DIRECT_RENDER_SPAN) {
                normalizedViewport
            } else {
                normalizedViewport.copy(span = FractalViewport.MIN_DIRECT_RENDER_SPAN)
            }
        }

        return copy(
            viewport = normalizedViewport,
            power = normalizedPower,
            iterations = iterations.coerceIn(
                FractalIterationPolicy.MIN_ITERATIONS,
                FractalIterationPolicy.MAX_ITERATIONS
            ),
            bailout = bailout.takeIf(Double::isFinite)?.coerceIn(MIN_BAILOUT, MAX_BAILOUT)
                ?: formula.defaultBailout,
            paletteCycles = paletteCycles
                .takeIf(Double::isFinite)
                ?.coerceIn(MIN_PALETTE_CYCLES, MAX_PALETTE_CYCLES)
                ?: DEFAULT_PALETTE_CYCLES,
            paletteOffset = paletteOffset
                .takeIf(Double::isFinite)
                ?.let { it - floor(it) }
                ?: 0.0,
            juliaConstant = juliaConstant.normalized(formula.defaultJuliaConstant),
            phoenixConstant = phoenixConstant.normalized(formula.defaultPhoenixConstant),
            novaRelaxation = novaRelaxation
                .takeIf(Double::isFinite)
                ?.coerceIn(MIN_NOVA_RELAXATION, MAX_NOVA_RELAXATION)
                ?: formula.defaultNovaRelaxation,
            camera = camera.normalized(formula.defaultCamera),
            quaternionConstant = quaternionConstant.normalized(
                formula.defaultQuaternionConstant
            ),
            coefficients = coefficients.normalized(formula.coefficientSpecs),
            showFloor = showFloor && formula.supportsFloor,
            supersampling = supersampling.coerceIn(MIN_SUPERSAMPLING, MAX_SUPERSAMPLING)
        )
    }

    fun effectiveIterations(
        width: Int = REFERENCE_WIDTH,
        height: Int = REFERENCE_HEIGHT
    ): Int = effectiveIterations(
        viewport = viewport,
        width = width,
        height = height
    )

    fun effectiveIterations(
        viewport: FractalViewport,
        width: Int = REFERENCE_WIDTH,
        height: Int = REFERENCE_HEIGHT
    ): Int = iterationPolicy.resolve(
        baseIterations = iterations,
        viewport = viewport,
        width = width,
        height = height
    )

    fun withFormula(
        formula: FractalFormula,
        resetViewport: Boolean = true
    ): FractalParams = copy(
        formula = formula,
        viewport = if (resetViewport) formula.defaultViewport else viewport,
        power = formula.defaultPower,
        iterations = formula.defaultIterations,
        bailout = formula.defaultBailout,
        juliaConstant = formula.defaultJuliaConstant,
        phoenixConstant = formula.defaultPhoenixConstant,
        novaRelaxation = formula.defaultNovaRelaxation,
        camera = formula.defaultCamera,
        quaternionConstant = formula.defaultQuaternionConstant,
        coefficients = formula.coefficientSpecs.defaults,
        showFloor = formula.supportsFloor
    )

    fun resetView(): FractalParams = copy(
        viewport = formula.defaultViewport,
        camera = formula.defaultCamera
    )

    fun toRenderRequest(
        width: Int,
        height: Int,
        viewportAspectRatio: Double = width.toDouble() / height
    ): FractalRenderRequest = FractalRenderRequest(
        width = width,
        height = height,
        params = normalized(),
        viewportAspectRatio = viewportAspectRatio
    )

    companion object {
        val Default by lazy { FractalParams() }

        const val DEFAULT_ITERATIONS = 320
        const val DEFAULT_BAILOUT = 4.0
        const val DEFAULT_PALETTE_CYCLES = 1.0
        const val DEFAULT_INSIDE_COLOR = -0x1000000
        const val DEFAULT_FLOOR_PRIMARY_COLOR = -0x1
        const val DEFAULT_FLOOR_SECONDARY_COLOR = -0x1000000
        const val MIN_POWER = 2.0
        const val MAX_POWER = 12.0
        const val MIN_BAILOUT = 2.0
        const val MAX_BAILOUT = 1.0E12
        const val MIN_PALETTE_CYCLES = 0.05
        const val MAX_PALETTE_CYCLES = 64.0
        const val MIN_NOVA_RELAXATION = 0.05
        const val MAX_NOVA_RELAXATION = 2.0
        const val MIN_SUPERSAMPLING = 1
        const val MAX_SUPERSAMPLING = 4

        private const val REFERENCE_WIDTH = 640
        private const val REFERENCE_HEIGHT = 480
        private const val DEEP_ZOOM_POWER = 2.0
        private const val POWER_EPSILON = 1.0E-10
    }
}
