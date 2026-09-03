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

import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FractalModelsTest {

    @Test
    fun catalogHasUsefulUniqueOptions() {
        assertEquals(42, FractalFormula.entries.size)
        assertEquals(59, GradientPalette.entries.size)
        assertEquals(
            FractalFormula.entries.size,
            FractalFormula.entries.map(FractalFormula::name).distinct().size
        )
        assertEquals(
            GradientPalette.entries.size,
            GradientPalette.entries.map(GradientPalette::name).distinct().size
        )
        assertEquals(4, FractalColoring.entries.size)
    }

    @Test
    fun requestedCatalogIsFullyRepresented() {
        val requestedTwoDimensional = setOf(
            FractalFormula.Mandelbrot,
            FractalFormula.Julia,
            FractalFormula.SierpinskiCarpet,
            FractalFormula.SierpinskiTriangle,
            FractalFormula.BurningShip,
            FractalFormula.Tricorn,
            FractalFormula.Phoenix,
            FractalFormula.Celtic,
            FractalFormula.Newton,
            FractalFormula.Lyapunov,
            FractalFormula.Nova,
            FractalFormula.MagnetI,
            FractalFormula.Collatz,
            FractalFormula.Buddhabrot,
            FractalFormula.Hopalong,
            FractalFormula.Martin,
            FractalFormula.Gingerbreadman,
            FractalFormula.Chip,
            FractalFormula.Quadruptwo,
            FractalFormula.Threeply
        )
        val requestedThreeDimensional = setOf(
            FractalFormula.Mandelbulb,
            FractalFormula.MengerSponge,
            FractalFormula.SierpinskiTetrahedron,
            FractalFormula.QuaternionJulia,
            FractalFormula.Mandelbox,
            FractalFormula.OctahedralIFS,
            FractalFormula.IcosahedralIFS,
            FractalFormula.ApollonianGasket,
            FractalFormula.Kleinian,
            FractalFormula.HybridMandelbulbJulia,
            FractalFormula.QuaternionCubic,
            FractalFormula.SierpinskiGasket,
            FractalFormula.Pickover,
            FractalFormula.Lorenz,
            FractalFormula.Rossler
        )

        assertEquals(20, requestedTwoDimensional.size)
        assertEquals(15, requestedThreeDimensional.size)
        assertTrue(requestedTwoDimensional.all { !it.isThreeDimensional })
        assertTrue(requestedThreeDimensional.all(FractalFormula::isThreeDimensional))
        assertTrue(FractalFormula.entries.containsAll(requestedTwoDimensional))
        assertTrue(FractalFormula.entries.containsAll(requestedThreeDimensional))
    }

    @Test
    fun catalogDeclaresDimensionsAndCapabilities() {
        val threeDimensional = FractalFormula.entries.filter(
            FractalFormula::isThreeDimensional
        )

        assertEquals(15, threeDimensional.size)
        assertTrue(threeDimensional.all(FractalFormula::usesCamera))
        assertTrue(threeDimensional.none(FractalFormula::usesBailout))
        assertEquals(
            setOf(FractalFormula.Pickover, FractalFormula.Lorenz, FractalFormula.Rossler),
            threeDimensional.filter(FractalFormula::usesIterationControls).toSet()
        )
        assertTrue(FractalFormula.Mandelbulb.usesPower)
        assertFalse(FractalFormula.Mandelbox.usesPower)
        assertTrue(FractalFormula.QuaternionJulia.usesQuaternionConstant)
        assertTrue(FractalFormula.QuaternionCubic.usesQuaternionConstant)
        assertTrue(FractalFormula.HybridMandelbulbJulia.usesJuliaConstant)
        assertEquals(
            12,
            threeDimensional.count(FractalFormula::supportsFloor)
        )
        assertFalse(FractalFormula.Pickover.supportsFloor)
        listOf(
            FractalFormula.BurningShipJulia,
            FractalFormula.CelticJulia
        ).forEach { formula ->
            assertTrue(formula.usesPower)
            assertTrue(formula.usesIterationControls)
            assertTrue(formula.usesBailout)
            assertTrue(formula.usesJuliaConstant)
            assertEquals(FractalFormula.Julia.defaultJuliaConstant, formula.defaultJuliaConstant)
        }
        listOf(
            FractalFormula.SierpinskiCarpet,
            FractalFormula.SierpinskiTriangle
        ).forEach { formula ->
            assertTrue(formula.isGeometric)
            assertFalse(formula.usesPower)
            assertFalse(formula.usesIterationControls)
            assertFalse(formula.usesBailout)
        }
        listOf(
            FractalFormula.Buddhabrot,
            FractalFormula.Hopalong,
            FractalFormula.Martin,
            FractalFormula.Gingerbreadman,
            FractalFormula.Chip,
            FractalFormula.Quadruptwo,
            FractalFormula.Threeply,
            FractalFormula.Pickover,
            FractalFormula.Lorenz,
            FractalFormula.Rossler
        ).forEach { formula ->
            assertTrue(formula.isDensityVisualization)
        }
    }

    @Test
    fun formulaCoefficientsUseTheirOwnDefaultsAndRanges() {
        val lorenz = FractalParams.Default.withFormula(FractalFormula.Lorenz)
        val octahedral = FractalParams.Default.withFormula(FractalFormula.OctahedralIFS)
        val icosahedral = FractalParams.Default.withFormula(FractalFormula.IcosahedralIFS)
        val clampedThreeply = FractalParams.Default
            .withFormula(FractalFormula.Threeply)
            .copy(
                coefficients = FractalCoefficients(
                    a = Double.NaN,
                    b = -1_000.0,
                    c = 1_000.0
                )
            )
            .normalized()

        assertEquals(10.0, lorenz.coefficients.a, 0.0)
        assertEquals(28.0, lorenz.coefficients.b, 0.0)
        assertEquals(8.0 / 3.0, lorenz.coefficients.c, 0.0)
        assertEquals(2.0, octahedral.coefficients.a, 0.0)
        assertEquals(1.2, octahedral.coefficients.b, 0.0)
        assertEquals(1.7, icosahedral.coefficients.a, 0.0)
        assertEquals(1.5, icosahedral.coefficients.b, 0.0)
        assertEquals(-55.0, clampedThreeply.coefficients.a, 0.0)
        assertEquals(-100.0, clampedThreeply.coefficients.b, 0.0)
        assertEquals(100.0, clampedThreeply.coefficients.c, 0.0)
        assertFalse(FractalFormula.Gingerbreadman.usesCoefficients)
        assertTrue(FractalFormula.OctahedralIFS.usesCoefficients)
        assertTrue(FractalFormula.IcosahedralIFS.usesCoefficients)
        assertTrue(FractalFormula.ApollonianGasket.usesCoefficients)
        assertEquals(-0.65, FractalFormula.Pickover.coefficientSpecs.defaults.c, 0.0)
        assertEquals(-2.43, FractalFormula.Pickover.coefficientSpecs.defaults.d, 0.0)
    }

    @Test
    fun formulaChangeRestoresFormulaSpecificRenderDefaults() {
        val collatz = FractalParams.Default.withFormula(FractalFormula.Collatz)
        val buddhabrot = collatz.withFormula(FractalFormula.Buddhabrot)
        val lorenz = buddhabrot.withFormula(FractalFormula.Lorenz)
        val quaternionCubic = lorenz.withFormula(FractalFormula.QuaternionCubic)

        assertEquals(160, collatz.iterations)
        assertEquals(100.0, collatz.bailout, 0.0)
        assertEquals(800, buddhabrot.iterations)
        assertFalse(FractalFormula.Buddhabrot.usesBailout)
        assertEquals(1_000, lorenz.iterations)
        assertEquals(256, quaternionCubic.iterations)
        assertEquals(2.0, buddhabrot.bailout, 0.0)
    }

    @Test
    fun everyCoefficientDefaultIsFiniteAndInsideItsRange() {
        FractalFormula.entries.forEach { formula ->
            listOf(
                formula.coefficientSpecs.a,
                formula.coefficientSpecs.b,
                formula.coefficientSpecs.c,
                formula.coefficientSpecs.d
            ).filterNotNull().forEach { spec ->
                assertTrue("${formula.name}: ${spec.label}", spec.defaultValue.isFinite())
                assertTrue(
                    "${formula.name}: ${spec.label} default is outside its range",
                    spec.defaultValue in spec.valueRange
                )
            }
        }
    }

    @Test
    fun unsupportedDeepZoomIsClampedBeforeRendering() {
        val deepViewport = FractalViewport.of("-0.5", "0", "1E-100")
        val burningShip = FractalParams(
            formula = FractalFormula.BurningShip,
            viewport = deepViewport
        ).normalized()
        val nonQuadraticMandelbrot = FractalParams(
            formula = FractalFormula.Mandelbrot,
            viewport = deepViewport,
            power = 3.0
        ).normalized()
        val quadraticMandelbrot = FractalParams(
            formula = FractalFormula.Mandelbrot,
            viewport = deepViewport,
            power = 2.0
        ).normalized()

        assertEquals(
            0,
            FractalViewport.MIN_DIRECT_RENDER_SPAN.compareTo(burningShip.viewport.span)
        )
        assertEquals(
            0,
            FractalViewport.MIN_DIRECT_RENDER_SPAN.compareTo(nonQuadraticMandelbrot.viewport.span)
        )
        assertEquals(0, deepViewport.span.compareTo(quadraticMandelbrot.viewport.span))
        assertFalse(burningShip.isDeepZoomAvailable)
        assertTrue(quadraticMandelbrot.isDeepZoomAvailable)
    }

    @Test
    fun iterationPoliciesRespondToZoomAndResolution() {
        val base = 200
        val wide = FractalViewport.Default
        val deep = wide.copy(span = FractalViewport.MIN_SPAN)

        assertEquals(
            base,
            FractalIterationPolicy.Fixed.resolve(base, deep, width = 4000, height = 3000)
        )
        assertTrue(
            FractalIterationPolicy.ScaleWithZoom.resolve(base, deep, 800, 600) >
                    FractalIterationPolicy.ScaleWithZoom.resolve(base, wide, 800, 600)
        )
        assertTrue(
            FractalIterationPolicy.Adaptive.resolve(base, wide, 4000, 3000) >
                    FractalIterationPolicy.Adaptive.resolve(base, wide, 640, 480)
        )
    }

    @Test
    fun paramsNormalizationClampsAndWrapsUserValues() {
        val normalized = FractalParams(
            power = Double.NaN,
            iterations = Int.MAX_VALUE,
            bailout = Double.POSITIVE_INFINITY,
            paletteCycles = -100.0,
            paletteOffset = -0.25,
            supersampling = Int.MAX_VALUE
        ).normalized()

        assertEquals(FractalFormula.Mandelbrot.defaultPower, normalized.power, 0.0)
        assertEquals(FractalIterationPolicy.MAX_ITERATIONS, normalized.iterations)
        assertEquals(FractalParams.DEFAULT_BAILOUT, normalized.bailout, 0.0)
        assertEquals(FractalParams.MIN_PALETTE_CYCLES, normalized.paletteCycles, 0.0)
        assertEquals(0.75, normalized.paletteOffset, 0.0)
        assertEquals(FractalParams.MAX_SUPERSAMPLING, normalized.supersampling)
    }

    @Test
    fun cameraAndQuaternionNormalizeAgainstFormulaDefaults() {
        val normalized = FractalParams.Default
            .withFormula(FractalFormula.QuaternionJulia)
            .copy(
                camera = FractalCamera(
                    yaw = Double.NaN,
                    pitch = 200.0,
                    distance = -1.0
                ),
                quaternionConstant = FractalQuaternion(
                    x = Double.NaN,
                    y = 100.0,
                    z = -100.0,
                    w = Double.POSITIVE_INFINITY
                )
            )
            .normalized()

        assertEquals(FractalFormula.QuaternionJulia.defaultCamera.yaw, normalized.camera.yaw, 0.0)
        assertEquals(FractalCamera.MAX_PITCH, normalized.camera.pitch, 0.0)
        assertEquals(FractalCamera.MIN_DISTANCE, normalized.camera.distance, 0.0)
        assertEquals(
            FractalFormula.QuaternionJulia.defaultQuaternionConstant.x,
            normalized.quaternionConstant.x,
            0.0
        )
        assertEquals(FractalQuaternion.MAX_COMPONENT, normalized.quaternionConstant.y, 0.0)
        assertEquals(FractalQuaternion.MIN_COMPONENT, normalized.quaternionConstant.z, 0.0)
        assertEquals(
            FractalFormula.QuaternionJulia.defaultQuaternionConstant.w,
            normalized.quaternionConstant.w,
            0.0
        )
    }

    @Test
    fun formulaChangeRestoresItsCameraAndQuaternionDefaults() {
        val changed = FractalParams.Default
            .copy(
                camera = FractalCamera(yaw = 120.0, pitch = -40.0, distance = 10.0),
                quaternionConstant = FractalQuaternion(x = 1.0, y = 1.0, z = 1.0, w = 1.0)
            )
            .withFormula(FractalFormula.QuaternionJulia)

        assertEquals(FractalFormula.QuaternionJulia.defaultCamera, changed.camera)
        assertEquals(
            FractalFormula.QuaternionJulia.defaultQuaternionConstant,
            changed.quaternionConstant
        )
        assertTrue(changed.showFloor)
        assertEquals(
            FractalParams.DEFAULT_FLOOR_PRIMARY_COLOR,
            changed.floorPrimaryColor.colorInt
        )
        assertEquals(
            FractalParams.DEFAULT_FLOOR_SECONDARY_COLOR,
            changed.floorSecondaryColor.colorInt
        )
        assertFalse(changed.withFormula(FractalFormula.Lorenz).showFloor)
    }

    @Test
    fun cameraGestureMathOrbitsZoomsAndResetViewRestoresDefaults() {
        val movedCamera = FractalFormula.Mandelbulb.defaultCamera
            .orbit(yawDelta = 450.0, pitchDelta = 100.0)
            .zoomBy(2.0)
        val moved = FractalParams.Default
            .withFormula(FractalFormula.Mandelbulb)
            .copy(camera = movedCamera)

        assertEquals(55.0, moved.camera.yaw, 0.0)
        assertEquals(FractalCamera.MAX_PITCH, moved.camera.pitch, 0.0)
        assertEquals(1.6, moved.camera.distance, 0.0)
        assertEquals(FractalFormula.Mandelbulb.defaultCamera, moved.resetView().camera)
    }

    @Test
    fun currentZoomUsesEachFormulaDefaultView() {
        assertEquals("1", FractalParams.Default.formattedZoom())
        assertEquals(
            "1e100",
            FractalParams.Default.copy(
                viewport = FractalViewport.of("-0.5", "0", "3E-100")
            ).formattedZoom()
        )
        assertEquals(
            "2",
            FractalParams.Default
                .withFormula(FractalFormula.Mandelbulb)
                .copy(camera = FractalCamera(distance = 1.6))
                .formattedZoom()
        )
    }

    @Test
    fun palettesInterpolateAndWrapWithoutLosingAlpha() {
        GradientPalette.entries.forEach { palette ->
            val start = palette.colorIntAt(0.0)
            val middle = palette.colorIntAt(0.37)

            assertEquals(0xFF, start ushr 24)
            assertEquals(0xFF, middle ushr 24)
            assertEquals(start, palette.colorIntAt(1.0))
        }
        assertNotEquals(
            GradientPalette.Fire.colorIntAt(0.25),
            GradientPalette.Ocean.colorIntAt(0.25)
        )
        assertEquals(
            GradientPalette.Twilight.colors.first(),
            GradientPalette.Twilight.colors.last()
        )
    }

    @Test
    fun everyPaletteOffersUniqueGeneratedFillColors() {
        GradientPalette.entries.forEach { palette ->
            val suggestedColors = palette.suggestedColors

            assertTrue(
                "${palette.name} should offer at least ten fill colors",
                suggestedColors.size >= 10
            )
            assertEquals(
                "${palette.name} contains duplicate fill colors",
                suggestedColors.size,
                suggestedColors.map { it.colorInt }.distinct().size
            )
        }
    }

    @Test
    fun renderRequestAcceptsMaximumOutputSize() {
        val request = FractalRenderRequest(
            width = FractalRenderRequest.MAX_OUTPUT_DIMENSION,
            height = FractalRenderRequest.MAX_OUTPUT_DIMENSION,
            params = FractalParams.Default
        )

        assertEquals(8192, request.width)
        assertEquals(8192, request.height)
        assertTrue(
            request.resolvePlan().estimatedWorkUnits <=
                    FractalRenderRequest.MAX_ESCAPE_TIME_WORK_UNITS
        )
    }

    @Test
    fun escapeTimeRenderPlanCapsTotalWorkAndReducesSupersamplingFirst() {
        val request = FractalParams.Default.copy(
            iterations = FractalIterationPolicy.MAX_ITERATIONS,
            iterationPolicy = FractalIterationPolicy.Fixed,
            supersampling = FractalParams.MAX_SUPERSAMPLING
        ).toRenderRequest(width = 4096, height = 4096)

        val plan = request.resolvePlan()

        assertTrue(
            plan.estimatedWorkUnits <= FractalRenderRequest.MAX_ESCAPE_TIME_WORK_UNITS
        )
        assertTrue(plan.supersampling < FractalParams.MAX_SUPERSAMPLING)
        assertTrue(plan.iterations < FractalIterationPolicy.MAX_ITERATIONS)
        assertEquals(
            request.width.toLong() * request.height *
                    plan.supersampling * plan.supersampling * plan.iterations,
            plan.estimatedWorkUnits
        )
    }

    @Test
    fun renderPlanPreservesSettingsThatFitWithinBudget() {
        val request = FractalParams.Default.copy(
            iterations = 200,
            iterationPolicy = FractalIterationPolicy.Fixed,
            supersampling = 2
        ).toRenderRequest(width = 320, height = 240)

        val plan = request.resolvePlan()

        assertEquals(200, plan.iterations)
        assertEquals(2, plan.supersampling)
    }

    @Test
    fun densityRenderPlanKeepsIterationsAndUsesSupersamplingAsSampleQuality() {
        val baseParams = FractalParams.Default
            .withFormula(FractalFormula.Buddhabrot)
            .copy(
                iterations = 800,
                iterationPolicy = FractalIterationPolicy.Fixed,
                supersampling = FractalParams.MAX_SUPERSAMPLING
            )
        val highQuality = baseParams.toRenderRequest(width = 640, height = 480).resolvePlan()
        val lowQuality = baseParams.copy(
            supersampling = FractalParams.MIN_SUPERSAMPLING
        ).toRenderRequest(width = 640, height = 480).resolvePlan()

        assertEquals(800, highQuality.iterations)
        assertEquals(FractalParams.MAX_SUPERSAMPLING, highQuality.supersampling)
        assertTrue(highQuality.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
        assertTrue(lowQuality.estimatedWorkUnits < highQuality.estimatedWorkUnits)
    }

    @Test
    fun attractorRenderPlansScaleWithResolutionWithoutDroppingIterations() {
        listOf(FractalFormula.Hopalong, FractalFormula.Lorenz).forEach { formula ->
            val params = FractalParams.Default
                .withFormula(formula)
                .copy(iterationPolicy = FractalIterationPolicy.Fixed)
            val preview = params.toRenderRequest(width = 640, height = 480).resolvePlan()
            val output = params.toRenderRequest(width = 4096, height = 4096).resolvePlan()

            assertEquals(formula.defaultIterations, output.iterations)
            assertTrue(output.estimatedWorkUnits > preview.estimatedWorkUnits)
            assertTrue(output.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
        }
    }

    @Test
    fun rayMarchPlanEncodesOutputQualityForAnIdenticalPreviewPlan() {
        val output = FractalParams.Default
            .withFormula(FractalFormula.HybridMandelbulbJulia)
            .copy(iterationPolicy = FractalIterationPolicy.Fixed)
            .toRenderRequest(width = 2400, height = 1000)
            .resolvePlan()
        val preview = output.params
            .toRenderRequest(width = 2048, height = 853)
            .resolvePlan()

        assertEquals(28, output.iterations)
        assertEquals(output.iterations, preview.iterations)
        assertEquals(output.supersampling, preview.supersampling)
        assertTrue(output.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
        assertTrue(preview.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
    }

    @Test
    fun rayMarchPlanReducesSupersamplingBeforeGeometryQuality() {
        val plan = FractalParams.Default
            .withFormula(FractalFormula.Mandelbulb)
            .copy(
                iterationPolicy = FractalIterationPolicy.Fixed,
                supersampling = FractalParams.MAX_SUPERSAMPLING
            )
            .toRenderRequest(width = 640, height = 480)
            .resolvePlan()

        assertEquals(FractalParams.MIN_SUPERSAMPLING, plan.supersampling)
        assertTrue(plan.iterations >= 64)
        assertTrue(plan.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
    }

    @Test
    fun rayMarchOutputLimitMatchesTheMinimumNativeWorkPlan() {
        assertEquals(
            2_083_333L,
            FractalRenderRequest.maxOutputPixelsFor(FractalFormula.Mandelbulb)
        )
        assertEquals(
            1_666_666L,
            FractalRenderRequest.maxOutputPixelsFor(FractalFormula.Kleinian)
        )
        val params = FractalParams.Default
            .withFormula(FractalFormula.Mandelbulb)
            .copy(iterationPolicy = FractalIterationPolicy.Fixed)
        val withinLimit = params.toRenderRequest(width = 2048, height = 1017).resolvePlan()
        val overLimit = params.toRenderRequest(width = 2048, height = 1018).resolvePlan()

        assertTrue(withinLimit.estimatedWorkUnits <= FractalRenderRequest.MAX_RENDER_WORK_UNITS)
        assertEquals(Long.MAX_VALUE, overLimit.estimatedWorkUnits)
    }

    @Test
    fun explicit2048ExportPreservesItsAdaptiveIterationCount() {
        val request = FractalParams.Default.toRenderRequest(width = 2048, height = 2048)
        val unbudgetedIterations = request.params.effectiveIterations(2048, 2048)

        assertEquals(unbudgetedIterations, request.effectiveIterations)
        assertEquals(request.effectiveIterations, request.resolvePlan().iterations)
    }

    @Test
    fun fourKPreviewAndExportUseIdenticalEscapeTimeQuality() {
        val output = FractalParams.Default
            .toRenderRequest(width = 4096, height = 4096)
            .resolvePlan()
        val preview = output.params
            .toRenderRequest(width = 2048, height = 2048)
            .resolvePlan()

        assertEquals(
            FractalParams.Default.effectiveIterations(4096, 4096),
            output.iterations
        )
        assertEquals(output.iterations, preview.iterations)
        assertEquals(output.supersampling, preview.supersampling)
        assertEquals(output.params, preview.params)
    }

    @Test
    fun output1080PreservesTheDefaultAdaptiveQuality() {
        val request = FractalParams.Default.toRenderRequest(width = 1080, height = 1080)
        val unbudgetedIterations = request.params.effectiveIterations(1080, 1080)

        assertEquals(unbudgetedIterations, request.effectiveIterations)
        assertTrue(
            request.resolvePlan().estimatedWorkUnits <=
                    FractalRenderRequest.MAX_RENDER_WORK_UNITS
        )
    }

    @Test
    fun previewCanPreserveTheOutputViewportAspectRatio() {
        val request = FractalParams.Default.toRenderRequest(
            width = 512,
            height = 512,
            viewportAspectRatio = 16.0 / 9.0
        )

        assertEquals(16.0 / 9.0, request.aspectRatio, 0.0)
    }

    @Test
    fun previewBuiltFromOutputPlanKeepsThreeDimensionalRenderQuality() {
        val outputPlan = FractalParams.Default
            .withFormula(FractalFormula.Mandelbulb)
            .copy(
                iterations = 320,
                iterationPolicy = FractalIterationPolicy.Fixed,
                supersampling = 4
            )
            .toRenderRequest(width = 512, height = 768)
            .resolvePlan()
        val previewPlan = outputPlan.params
            .toRenderRequest(
                width = 320,
                height = 480,
                viewportAspectRatio = 2.0 / 3.0
            )
            .resolvePlan()

        assertEquals(outputPlan.iterations, previewPlan.iterations)
        assertEquals(outputPlan.supersampling, previewPlan.supersampling)
    }
}
