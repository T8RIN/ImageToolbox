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

package com.t8rin.imagetoolbox.feature.fractal_generation.data

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCamera
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalCoefficients
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalColoring
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalComplex
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalIterationPolicy
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalPalette
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalParams
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalQuaternion
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalViewport
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import com.t8rin.fractal_engine.FractalColoring as NativeFractalColoring
import com.t8rin.fractal_engine.FractalType as NativeFractalType

class NativeFractalRendererBackendTest {

    @Test
    fun mapsEveryApplicationFormulaToOneNativeType() {
        val nativeTypes = FractalFormula.entries.map { formula ->
            FractalParams.Default
                .withFormula(formula)
                .toRenderRequest(width = 8, height = 8)
                .toNativeRequest()
                .type
        }

        assertEquals(FractalFormula.entries.size, nativeTypes.distinct().size)
        assertEquals(NativeFractalType.entries.toSet(), nativeTypes.toSet())
    }

    @Test
    fun mapsNormalizedPlanAndEveryRenderSetting() {
        val params = FractalParams(
            formula = FractalFormula.QuaternionJulia,
            viewport = FractalViewport.of("1.25", "-0.75", "2.5"),
            power = 7.0,
            iterations = 777,
            bailout = 128.0,
            iterationPolicy = FractalIterationPolicy.Fixed,
            palette = FractalPalette.Turbo,
            coloring = FractalColoring.Angle,
            paletteCycles = 2.5,
            paletteOffset = 0.25,
            insideColor = ColorModel(0xFF123456.toInt()),
            juliaConstant = FractalComplex(real = -0.4, imaginary = 0.6),
            phoenixConstant = FractalComplex(real = -0.3, imaginary = 0.2),
            novaRelaxation = 1.25,
            camera = FractalCamera(yaw = 90.0, pitch = -30.0, distance = 4.25),
            quaternionConstant = FractalQuaternion(x = -0.2, y = 0.8, z = 0.1, w = -0.1),
            showFloor = true,
            floorPrimaryColor = ColorModel(0xFF336699.toInt()),
            floorSecondaryColor = ColorModel(0xFF663399.toInt()),
            supersampling = 3
        )
        val native = params
            .toRenderRequest(width = 20, height = 10, viewportAspectRatio = 2.5)
            .toNativeRequest()

        assertEquals(NativeFractalType.QuaternionJulia, native.type)
        assertEquals(20, native.width)
        assertEquals(10, native.height)
        assertEquals(1.25, native.viewport.centerX, 0.0)
        assertEquals(-0.75, native.viewport.centerY, 0.0)
        assertEquals(2.5, native.viewport.span, 0.0)
        assertEquals("1.25", native.viewport.exact.centerX)
        assertEquals("-0.75", native.viewport.exact.centerY)
        assertEquals("2.5", native.viewport.exact.span)
        assertEquals(2.5, native.viewportAspectRatio, 0.0)
        assertEquals(777, native.maxIterations)
        assertEquals(7.0, native.power, 0.0)
        assertEquals(128.0, native.bailout, 0.0)
        assertEquals(3, native.supersampling)
        assertEquals(NativeFractalColoring.Angle, native.coloring)
        assertArrayEquals(
            FractalPalette.Turbo.colors.map { it.colorInt }.toIntArray(),
            native.palette
        )
        assertEquals(2.5, native.paletteCycles, 0.0)
        assertEquals(0.25, native.paletteOffset, 0.0)
        assertEquals(0xFF123456.toInt(), native.insideColor)
        assertEquals(-0.4, native.juliaReal, 0.0)
        assertEquals(0.6, native.juliaImaginary, 0.0)
        assertEquals(-0.3, native.phoenixReal, 0.0)
        assertEquals(0.2, native.phoenixImaginary, 0.0)
        assertEquals(1.25, native.novaRelaxation, 0.0)
        assertEquals(Math.PI / 2.0, native.camera.yaw, 1.0E-12)
        assertEquals(-Math.PI / 6.0, native.camera.pitch, 1.0E-12)
        assertEquals(4.25, native.camera.distance, 0.0)
        assertEquals(-0.2, native.quaternionConstant.x, 0.0)
        assertEquals(0.8, native.quaternionConstant.y, 0.0)
        assertEquals(0.1, native.quaternionConstant.z, 0.0)
        assertEquals(-0.1, native.quaternionConstant.w, 0.0)
        assertEquals("AB", native.lyapunovSequence)
        assertEquals(45.0, native.fieldOfViewDegrees, 0.0)
        assertTrue(native.showFloor)
        assertEquals(0xFF336699.toInt(), native.floorPrimaryColor)
        assertEquals(0xFF663399.toInt(), native.floorSecondaryColor)
    }

    @Test
    fun preservesExactDeepZoomViewportStrings() {
        val viewport = FractalViewport.of(
            centerReal = "-0.7436438870371510000000000000000000000001",
            centerImaginary = "0.1318259042053300000000000000000000000001",
            span = "1E-100"
        )
        val native = FractalParams.Default
            .copy(viewport = viewport)
            .toRenderRequest(width = 8, height = 8)
            .toNativeRequest()

        assertEquals(viewport.centerReal.toString(), native.viewport.exact.centerX)
        assertEquals(viewport.centerImaginary.toString(), native.viewport.exact.centerY)
        assertEquals(viewport.span.toString(), native.viewport.exact.span)
        assertEquals(viewport.centerReal.toDouble(), native.viewport.centerX, 0.0)
        assertEquals(viewport.centerImaginary.toDouble(), native.viewport.centerY, 0.0)
        assertEquals(viewport.span.toDouble(), native.viewport.span, 0.0)
        assertNotEquals(native.viewport.centerX.toString(), native.viewport.exact.centerX)
    }

    @Test
    fun mapsFormulaAwareCoefficientsToTheNativeWireFields() {
        val native = FractalParams.Default
            .withFormula(FractalFormula.Pickover)
            .copy(
                power = 9.0,
                juliaConstant = FractalComplex(real = -0.8, imaginary = 0.156),
                phoenixConstant = FractalComplex(real = -0.5, imaginary = 0.25),
                coefficients = FractalCoefficients(
                    a = 1.25,
                    b = -2.5,
                    c = 3.75,
                    d = -4.5
                )
            )
            .toRenderRequest(width = 8, height = 8)
            .toNativeRequest()

        assertEquals(NativeFractalType.Pickover, native.type)
        assertEquals(1.25, native.juliaReal, 0.0)
        assertEquals(-2.5, native.juliaImaginary, 0.0)
        assertEquals(3.75, native.power, 0.0)
        assertEquals(-4.5, native.phoenixReal, 0.0)
        assertEquals(0.25, native.phoenixImaginary, 0.0)
    }

    @Test
    fun exposesOnlyAvailableEngineTypes() {
        val backend = backend(
            available = true,
            types = setOf(
                NativeFractalType.Mandelbrot,
                NativeFractalType.BurningShipJulia,
                NativeFractalType.CelticJulia,
                NativeFractalType.Mandelbulb
            )
        )

        assertEquals(
            setOf(
                FractalFormula.Mandelbrot,
                FractalFormula.BurningShipJulia,
                FractalFormula.CelticJulia,
                FractalFormula.Mandelbulb
            ),
            backend.supportedFormulas
        )
        assertTrue(backend(available = false).supportedFormulas.isEmpty())
    }

    private fun backend(
        available: Boolean,
        types: Set<NativeFractalType> = NativeFractalType.entries.toSet()
    ) = NativeFractalRendererBackend(
        isEngineAvailable = { available },
        supportedNativeTypes = types,
        nativeRender = { error("Rendering is not expected in unit tests") }
    )
}
