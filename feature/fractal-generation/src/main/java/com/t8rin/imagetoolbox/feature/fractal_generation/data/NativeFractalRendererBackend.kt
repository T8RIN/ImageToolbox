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

import android.graphics.Bitmap
import com.t8rin.fractal_engine.FractalEngine
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.FractalRenderer
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalColoring
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalRenderRequest
import javax.inject.Inject
import com.t8rin.fractal_engine.FractalCamera as NativeFractalCamera
import com.t8rin.fractal_engine.FractalColoring as NativeFractalColoring
import com.t8rin.fractal_engine.FractalExactViewport as NativeFractalExactViewport
import com.t8rin.fractal_engine.FractalRenderRequest as NativeFractalRenderRequest
import com.t8rin.fractal_engine.FractalType as NativeFractalType
import com.t8rin.fractal_engine.FractalViewport as NativeFractalViewport
import com.t8rin.fractal_engine.QuaternionConstant as NativeQuaternionConstant

internal class NativeFractalRendererBackend internal constructor(
    supportedNativeTypes: Set<NativeFractalType>,
    private val nativeRender: suspend (NativeFractalRenderRequest) -> Bitmap
) : FractalRenderer<Bitmap> {

    @Inject
    constructor() : this(
        supportedNativeTypes = FractalEngine.supportedTypes,
        nativeRender = { request -> FractalEngine.render(request) }
    )

    override val supportedFormulas: Set<FractalFormula> = NATIVE_TYPE_BY_FORMULA
        .filterValues { type -> type in supportedNativeTypes }
        .keys

    override suspend fun render(request: FractalRenderRequest): Bitmap {
        require(request.params.formula in supportedFormulas) {
            "Native fractal engine does not support ${request.params.formula.name}"
        }

        return try {
            nativeRender(request.toNativeRequest())
        } catch (error: LinkageError) {
            throw IllegalStateException(
                "Native fractal engine could not be loaded",
                error
            )
        }
    }
}

internal fun FractalRenderRequest.toNativeRequest(): NativeFractalRenderRequest {
    val plan = resolvePlan()
    val params = plan.params
    val camera = params.camera
    val quaternion = params.quaternionConstant
    val coefficientSpecs = params.formula.coefficientSpecs
    val coefficients = params.coefficients

    return NativeFractalRenderRequest(
        type = requireNotNull(NATIVE_TYPE_BY_FORMULA[params.formula]),
        width = width,
        height = height,
        viewport = NativeFractalViewport(
            centerX = params.viewport.centerReal.toDouble(),
            centerY = params.viewport.centerImaginary.toDouble(),
            span = params.viewport.span.toDouble(),
            exact = NativeFractalExactViewport(
                centerX = params.viewport.centerReal.toString(),
                centerY = params.viewport.centerImaginary.toString(),
                span = params.viewport.span.toString()
            )
        ),
        viewportAspectRatio = aspectRatio,
        maxIterations = plan.iterations,
        power = if (coefficientSpecs.c != null) coefficients.c else params.power,
        bailout = params.bailout,
        supersampling = plan.supersampling,
        coloring = params.coloring.toNativeColoring(),
        palette = params.palette.colors.map { it.colorInt }.toIntArray(),
        paletteCycles = params.paletteCycles,
        paletteOffset = params.paletteOffset,
        insideColor = params.insideColor.colorInt,
        juliaReal = if (coefficientSpecs.a != null) coefficients.a else params.juliaConstant.real,
        juliaImaginary = if (coefficientSpecs.b != null) {
            coefficients.b
        } else {
            params.juliaConstant.imaginary
        },
        phoenixReal = if (coefficientSpecs.d != null) {
            coefficients.d
        } else {
            params.phoenixConstant.real
        },
        phoenixImaginary = params.phoenixConstant.imaginary,
        novaRelaxation = params.novaRelaxation,
        lyapunovSequence = DEFAULT_LYAPUNOV_SEQUENCE,
        camera = NativeFractalCamera(
            yaw = Math.toRadians(camera.yaw),
            pitch = Math.toRadians(camera.pitch),
            distance = camera.distance
        ),
        quaternionConstant = NativeQuaternionConstant(
            x = quaternion.x,
            y = quaternion.y,
            z = quaternion.z,
            w = quaternion.w
        ),
        fieldOfViewDegrees = DEFAULT_FIELD_OF_VIEW_DEGREES,
        showFloor = params.showFloor && params.formula.supportsFloor,
        floorPrimaryColor = params.floorPrimaryColor.colorInt,
        floorSecondaryColor = params.floorSecondaryColor.colorInt
    )
}

private fun FractalColoring.toNativeColoring(): NativeFractalColoring = when (this) {
    FractalColoring.Smooth -> NativeFractalColoring.Smooth
    FractalColoring.Banded -> NativeFractalColoring.Banded
    FractalColoring.OrbitTrap -> NativeFractalColoring.OrbitTrap
    FractalColoring.Angle -> NativeFractalColoring.Angle
}

private val NATIVE_TYPE_BY_FORMULA: Map<FractalFormula, NativeFractalType> = mapOf(
    FractalFormula.Mandelbrot to NativeFractalType.Mandelbrot,
    FractalFormula.Julia to NativeFractalType.Julia,
    FractalFormula.BurningShip to NativeFractalType.BurningShip,
    FractalFormula.Tricorn to NativeFractalType.Tricorn,
    FractalFormula.Multibrot to NativeFractalType.Multibrot,
    FractalFormula.Multicorn to NativeFractalType.Multicorn,
    FractalFormula.Celtic to NativeFractalType.Celtic,
    FractalFormula.Buffalo to NativeFractalType.Buffalo,
    FractalFormula.PerpendicularBurningShip to NativeFractalType.PerpendicularBurningShip,
    FractalFormula.Phoenix to NativeFractalType.Phoenix,
    FractalFormula.Nova to NativeFractalType.Nova,
    FractalFormula.Newton to NativeFractalType.Newton,
    FractalFormula.MagnetI to NativeFractalType.MagnetI,
    FractalFormula.MagnetII to NativeFractalType.MagnetII,
    FractalFormula.Lyapunov to NativeFractalType.Lyapunov,
    FractalFormula.SierpinskiCarpet to NativeFractalType.SierpinskiCarpet,
    FractalFormula.SierpinskiTriangle to NativeFractalType.SierpinskiTriangle,
    FractalFormula.BurningShipJulia to NativeFractalType.BurningShipJulia,
    FractalFormula.CelticJulia to NativeFractalType.CelticJulia,
    FractalFormula.Collatz to NativeFractalType.Collatz,
    FractalFormula.Buddhabrot to NativeFractalType.Buddhabrot,
    FractalFormula.Hopalong to NativeFractalType.Hopalong,
    FractalFormula.Martin to NativeFractalType.Martin,
    FractalFormula.Gingerbreadman to NativeFractalType.Gingerbreadman,
    FractalFormula.Chip to NativeFractalType.Chip,
    FractalFormula.Quadruptwo to NativeFractalType.Quadruptwo,
    FractalFormula.Threeply to NativeFractalType.Threeply,
    FractalFormula.Clifford to NativeFractalType.Clifford,
    FractalFormula.DeJong to NativeFractalType.DeJong,
    FractalFormula.Ikeda to NativeFractalType.Ikeda,
    FractalFormula.Tinkerbell to NativeFractalType.Tinkerbell,
    FractalFormula.GumowskiMira to NativeFractalType.GumowskiMira,
    FractalFormula.BarnsleyFern to NativeFractalType.BarnsleyFern,
    FractalFormula.IFSDragon to NativeFractalType.IFSDragon,
    FractalFormula.IFSTwig to NativeFractalType.IFSTwig,
    FractalFormula.ChristmasTree to NativeFractalType.ChristmasTree,
    FractalFormula.VicsekCross to NativeFractalType.VicsekCross,
    FractalFormula.PythagorasTree to NativeFractalType.PythagorasTree,
    FractalFormula.HeighwayDragon to NativeFractalType.HeighwayDragon,
    FractalFormula.KochSnowflake to NativeFractalType.KochSnowflake,
    FractalFormula.BarnsleyMandelbrot to NativeFractalType.BarnsleyMandelbrot,
    FractalFormula.BarnsleyJulia to NativeFractalType.BarnsleyJulia,
    FractalFormula.AlphaMandelbrot to NativeFractalType.AlphaMandelbrot,
    FractalFormula.AlphaMandelbrotJulia to NativeFractalType.AlphaMandelbrotJulia,
    FractalFormula.MandelbrotSine to NativeFractalType.MandelbrotSine,
    FractalFormula.JuliaSine to NativeFractalType.JuliaSine,
    FractalFormula.Spider to NativeFractalType.Spider,
    FractalFormula.ManOWar to NativeFractalType.ManOWar,
    FractalFormula.Lambda to NativeFractalType.Lambda,
    FractalFormula.Thorn to NativeFractalType.Thorn,
    FractalFormula.BarnsleyII to NativeFractalType.BarnsleyII,
    FractalFormula.BarnsleyIII to NativeFractalType.BarnsleyIII,
    FractalFormula.MandelbrotCosine to NativeFractalType.MandelbrotCosine,
    FractalFormula.JuliaCosine to NativeFractalType.JuliaCosine,
    FractalFormula.MandelbrotSinh to NativeFractalType.MandelbrotSinh,
    FractalFormula.JuliaSinh to NativeFractalType.JuliaSinh,
    FractalFormula.Feather to NativeFractalType.Feather,
    FractalFormula.Cactus to NativeFractalType.Cactus,
    FractalFormula.Zubieta to NativeFractalType.Zubieta,
    FractalFormula.Tetration to NativeFractalType.Tetration,
    FractalFormula.Mandelbulb to NativeFractalType.Mandelbulb,
    FractalFormula.Mandelbox to NativeFractalType.Mandelbox,
    FractalFormula.MengerSponge to NativeFractalType.MengerSponge,
    FractalFormula.SierpinskiTetrahedron to NativeFractalType.SierpinskiTetrahedron,
    FractalFormula.QuaternionJulia to NativeFractalType.QuaternionJulia,
    FractalFormula.SierpinskiGasket to NativeFractalType.SierpinskiGasket,
    FractalFormula.OctahedralIFS to NativeFractalType.OctahedralIFS,
    FractalFormula.IcosahedralIFS to NativeFractalType.IcosahedralIFS,
    FractalFormula.ApollonianGasket to NativeFractalType.ApollonianGasket,
    FractalFormula.Kleinian to NativeFractalType.Kleinian,
    FractalFormula.HybridMandelbulbJulia to NativeFractalType.HybridMandelbulbJulia,
    FractalFormula.QuaternionCubic to NativeFractalType.QuaternionCubic,
    FractalFormula.Pickover to NativeFractalType.Pickover,
    FractalFormula.Lorenz to NativeFractalType.Lorenz,
    FractalFormula.Rossler to NativeFractalType.Rossler
)

private const val DEFAULT_LYAPUNOV_SEQUENCE = "AB"
private const val DEFAULT_FIELD_OF_VIEW_DEGREES = 45.0
