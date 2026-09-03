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

/**
 * Immutable renderer input. [viewportAspectRatio] can preserve the final output framing when a
 * smaller preview uses different pixel dimensions.
 */
data class FractalRenderRequest(
    val width: Int,
    val height: Int,
    val params: FractalParams,
    val viewportAspectRatio: Double = width.toDouble() / height
) {

    init {
        require(width in 1..MAX_OUTPUT_DIMENSION) {
            "Width must be in 1..$MAX_OUTPUT_DIMENSION, was $width"
        }
        require(height in 1..MAX_OUTPUT_DIMENSION) {
            "Height must be in 1..$MAX_OUTPUT_DIMENSION, was $height"
        }
        require(width.toLong() * height <= MAX_OUTPUT_PIXELS) {
            "Output must not exceed $MAX_OUTPUT_PIXELS pixels"
        }
        require(
            viewportAspectRatio.isFinite() &&
                    viewportAspectRatio in MIN_VIEWPORT_ASPECT_RATIO..MAX_VIEWPORT_ASPECT_RATIO
        ) {
            "Viewport aspect ratio must be in " +
                    "$MIN_VIEWPORT_ASPECT_RATIO..$MAX_VIEWPORT_ASPECT_RATIO, " +
                    "was $viewportAspectRatio"
        }
    }

    val aspectRatio: Double
        get() = viewportAspectRatio

    val effectiveIterations: Int
        get() = resolvePlan().iterations

    val effectiveSupersampling: Int
        get() = resolvePlan().supersampling

    fun resolvePlan(): FractalRenderPlan {
        val normalizedParams = params.normalized()
        val requestedSupersampling = normalizedParams.supersampling
        val requestedIterations = normalizedParams.effectiveIterations(
            width = if (normalizedParams.formula.isDensityVisualization) {
                width
            } else {
                width * requestedSupersampling
            },
            height = if (normalizedParams.formula.isDensityVisualization) {
                height
            } else {
                height * requestedSupersampling
            }
        )
        val pixelCount = width.toLong() * height
        if (normalizedParams.formula.isDensityVisualization) {
            return resolveDensityPlan(
                normalizedParams = normalizedParams,
                iterations = requestedIterations,
                supersampling = requestedSupersampling,
                pixelCount = pixelCount
            )
        }
        if (normalizedParams.formula.isThreeDimensional) {
            return resolveRayMarchPlan(
                normalizedParams = normalizedParams,
                requestedIterations = requestedIterations,
                requestedSupersampling = requestedSupersampling,
                pixelCount = pixelCount
            )
        }
        val preferredIterations = requestedIterations.coerceAtMost(
            MIN_PREFERRED_BUDGETED_ITERATIONS
        )
        var supersampling = requestedSupersampling

        while (
            supersampling > FractalParams.MIN_SUPERSAMPLING &&
            maxIterationsFor(pixelCount, supersampling) < preferredIterations
        ) {
            supersampling--
        }

        val iterations = requestedIterations.coerceAtMost(
            maxIterationsFor(pixelCount, supersampling)
        )
        val workUnits = pixelCount * supersampling * supersampling * iterations

        return FractalRenderPlan(
            params = normalizedParams.copy(
                iterations = iterations,
                iterationPolicy = FractalIterationPolicy.Fixed,
                supersampling = supersampling
            ),
            iterations = iterations,
            supersampling = supersampling,
            estimatedWorkUnits = workUnits
        )
    }

    private fun resolveRayMarchPlan(
        normalizedParams: FractalParams,
        requestedIterations: Int,
        requestedSupersampling: Int,
        pixelCount: Long
    ): FractalRenderPlan {
        val preferredIterations = requestedIterations.coerceAtMost(
            MIN_PREFERRED_BUDGETED_ITERATIONS
        )
        var supersampling = requestedSupersampling
        var iterations = maximumRayMarchIterationsWithinBudget(
            formula = normalizedParams.formula,
            requestedIterations = requestedIterations,
            sampledPixels = pixelCount * supersampling * supersampling
        )

        while (
            supersampling > FractalParams.MIN_SUPERSAMPLING &&
            (iterations == null || iterations < preferredIterations)
        ) {
            supersampling--
            iterations = maximumRayMarchIterationsWithinBudget(
                formula = normalizedParams.formula,
                requestedIterations = requestedIterations,
                sampledPixels = pixelCount * supersampling * supersampling
            )
        }

        val resolvedIterations = iterations ?: FractalIterationPolicy.MIN_ITERATIONS
        val sampledPixels = pixelCount * supersampling * supersampling
        val workUnits = if (iterations == null) {
            Long.MAX_VALUE
        } else {
            desiredRayMarchWork(
                formula = normalizedParams.formula,
                iterations = resolvedIterations,
                sampledPixels = sampledPixels
            )
        }

        return FractalRenderPlan(
            params = normalizedParams.copy(
                iterations = resolvedIterations,
                iterationPolicy = FractalIterationPolicy.Fixed,
                supersampling = supersampling
            ),
            iterations = resolvedIterations,
            supersampling = supersampling,
            estimatedWorkUnits = workUnits
        )
    }

    private fun maximumRayMarchIterationsWithinBudget(
        formula: FractalFormula,
        requestedIterations: Int,
        sampledPixels: Long
    ): Int? = (requestedIterations downTo FractalIterationPolicy.MIN_ITERATIONS).firstOrNull {
        desiredRayMarchWork(
            formula = formula,
            iterations = it,
            sampledPixels = sampledPixels
        ) <= MAX_RENDER_WORK_UNITS
    }

    private fun desiredRayMarchWork(
        formula: FractalFormula,
        iterations: Int,
        sampledPixels: Long
    ): Long {
        val maximumSteps = iterations.coerceIn(
            MIN_RAY_MARCH_STEPS,
            MAX_RAY_MARCH_STEPS
        ).toLong()
        val distanceIterations = formula.rayMarchDistanceIterations(iterations)
        return sampledPixels * (maximumSteps + RAY_MARCH_NORMAL_STEPS) * distanceIterations
    }

    private fun resolveDensityPlan(
        normalizedParams: FractalParams,
        iterations: Int,
        supersampling: Int,
        pixelCount: Long
    ): FractalRenderPlan {
        val samplingFactor = supersampling.toLong() * supersampling
        val availableWork = (MAX_RENDER_WORK_UNITS - pixelCount).coerceAtLeast(0L)
        val workUnits = when {
            normalizedParams.formula == FractalFormula.Buddhabrot -> {
                val orbitIterations = iterations.toLong().coerceAtLeast(
                    MIN_BUDDHABROT_ORBIT_ITERATIONS
                )
                val desiredSamples = (pixelCount * samplingFactor / 2L)
                    .coerceAtLeast(MIN_BUDDHABROT_SAMPLES)
                val samples = desiredSamples.coerceAtMost(availableWork / orbitIterations)
                pixelCount + samples * orbitIterations
            }

            normalizedParams.formula.isThreeDimensional -> {
                val baseSteps = (iterations.toLong() * ATTRACTOR_3D_ITERATION_MULTIPLIER)
                    .coerceIn(MIN_ATTRACTOR_3D_STEPS, MAX_ATTRACTOR_3D_BASE_STEPS)
                val steps = (baseSteps * densityLinearScale(pixelCount, samplingFactor))
                    .coerceAtMost(availableWork / ATTRACTOR_3D_STEP_WORK)
                pixelCount + steps * ATTRACTOR_3D_STEP_WORK
            }

            else -> {
                val baseSteps = (iterations.toLong() * ATTRACTOR_2D_ITERATION_MULTIPLIER)
                    .coerceIn(MIN_ATTRACTOR_2D_STEPS, MAX_ATTRACTOR_2D_BASE_STEPS)
                val steps = (baseSteps * densityLinearScale(pixelCount, samplingFactor))
                    .coerceAtMost(availableWork / ATTRACTOR_2D_STEP_WORK)
                pixelCount + steps * ATTRACTOR_2D_STEP_WORK
            }
        }

        return FractalRenderPlan(
            params = normalizedParams.copy(
                iterations = iterations,
                iterationPolicy = FractalIterationPolicy.Fixed
            ),
            iterations = iterations,
            supersampling = supersampling,
            estimatedWorkUnits = workUnits
        )
    }

    private fun densityLinearScale(
        pixelCount: Long,
        samplingFactor: Long
    ): Long = ceilSqrt(
        ceilDiv(pixelCount * samplingFactor, DENSITY_REFERENCE_PIXEL_COUNT)
    ).coerceAtLeast(1L)

    private fun ceilSqrt(value: Long): Long {
        var lower = 1L
        var upper = value.coerceAtLeast(1L)
        while (lower < upper) {
            val middle = lower + (upper - lower) / 2L
            if (middle >= ceilDiv(value, middle)) {
                upper = middle
            } else {
                lower = middle + 1L
            }
        }
        return lower
    }

    private fun ceilDiv(dividend: Long, divisor: Long): Long =
        dividend / divisor + if (dividend % divisor == 0L) 0L else 1L

    private fun maxIterationsFor(
        pixelCount: Long,
        supersampling: Int
    ): Int = (
            MAX_RENDER_WORK_UNITS / pixelCount / supersampling / supersampling
            ).toInt().coerceAtLeast(MIN_BUDGETED_ITERATIONS)

    companion object {
        const val MAX_OUTPUT_DIMENSION = 8_192
        const val MAX_OUTPUT_PIXELS = 16_777_216L
        const val MAX_RENDER_WORK_UNITS = 500_000_000L
        const val MIN_VIEWPORT_ASPECT_RATIO = 1.0 / 8_192.0
        const val MAX_VIEWPORT_ASPECT_RATIO = 8_192.0

        fun maxOutputPixelsFor(formula: FractalFormula): Long = if (
            formula.isThreeDimensional && !formula.isDensityVisualization
        ) {
            val minimumWorkPerPixel =
                (MIN_RAY_MARCH_STEPS + RAY_MARCH_NORMAL_STEPS) *
                        formula.minimumRayMarchDistanceIterations()
            (MAX_RENDER_WORK_UNITS / minimumWorkPerPixel).coerceAtMost(MAX_OUTPUT_PIXELS)
        } else {
            MAX_OUTPUT_PIXELS
        }

        private const val MIN_PREFERRED_BUDGETED_ITERATIONS = 64
        private const val MIN_BUDGETED_ITERATIONS = 1
        private const val MIN_RAY_MARCH_STEPS = 24
        private const val MAX_RAY_MARCH_STEPS = 192
        private const val RAY_MARCH_NORMAL_STEPS = 6L
        private const val DENSITY_REFERENCE_PIXEL_COUNT = 640L * 480L
        private const val MIN_BUDDHABROT_ORBIT_ITERATIONS = 32L
        private const val MIN_BUDDHABROT_SAMPLES = 8_192L
        private const val ATTRACTOR_2D_ITERATION_MULTIPLIER = 128L
        private const val MIN_ATTRACTOR_2D_STEPS = 24_000L
        private const val MAX_ATTRACTOR_2D_BASE_STEPS = 1_000_000L
        private const val ATTRACTOR_2D_STEP_WORK = 25L
        private const val ATTRACTOR_3D_ITERATION_MULTIPLIER = 32L
        private const val MIN_ATTRACTOR_3D_STEPS = 12_000L
        private const val MAX_ATTRACTOR_3D_BASE_STEPS = 240_000L
        private const val ATTRACTOR_3D_STEP_WORK = 9L
    }
}

/**
 * A normalized execution plan. [estimatedWorkUnits] is [Long.MAX_VALUE] when even the minimum
 * useful ray-march quality cannot fit the renderer budget.
 */
data class FractalRenderPlan(
    val params: FractalParams,
    val iterations: Int,
    val supersampling: Int,
    val estimatedWorkUnits: Long
)

private fun FractalFormula.minimumRayMarchDistanceIterations(): Long = when (this) {
    FractalFormula.MengerSponge -> 3L
    FractalFormula.OctahedralIFS,
    FractalFormula.IcosahedralIFS,
    FractalFormula.ApollonianGasket -> 4L

    FractalFormula.SierpinskiGasket -> 5L
    FractalFormula.SierpinskiTetrahedron,
    FractalFormula.HybridMandelbulbJulia,
    FractalFormula.QuaternionCubic -> 6L

    FractalFormula.Kleinian -> 10L

    FractalFormula.Mandelbulb,
    FractalFormula.Mandelbox,
    FractalFormula.QuaternionJulia -> 8L

    else -> error("$name is not ray-marched")
}

private fun FractalFormula.rayMarchDistanceIterations(iterations: Int): Long = when (this) {
    FractalFormula.MengerSponge -> (iterations / 48).coerceIn(3, 7).toLong()
    FractalFormula.SierpinskiTetrahedron -> (iterations / 32).coerceIn(6, 16).toLong()
    FractalFormula.OctahedralIFS,
    FractalFormula.IcosahedralIFS,
    FractalFormula.ApollonianGasket -> (iterations / 16).coerceIn(4, 18).toLong()

    FractalFormula.Kleinian -> (iterations / 16).coerceIn(10, 16).toLong()
    FractalFormula.HybridMandelbulbJulia,
    FractalFormula.QuaternionCubic -> (iterations / 16).coerceIn(6, 24).toLong()

    FractalFormula.SierpinskiGasket -> (iterations / 16).coerceIn(5, 16).toLong()
    FractalFormula.Mandelbulb,
    FractalFormula.Mandelbox,
    FractalFormula.QuaternionJulia -> (iterations / 16).coerceIn(8, 28).toLong()

    else -> error("$name is not ray-marched")
}
