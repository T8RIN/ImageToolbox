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

enum class FractalFormula(
    val defaultViewport: FractalViewport,
    val defaultPower: Double = 2.0,
    val defaultIterations: Int = FractalParams.DEFAULT_ITERATIONS,
    val defaultBailout: Double = FractalParams.DEFAULT_BAILOUT,
    val dimension: FractalDimension = FractalDimension.TwoDimensional,
    val defaultCamera: FractalCamera = FractalCamera.Default,
    val defaultQuaternionConstant: FractalQuaternion = FractalQuaternion.Zero,
    val coefficientSpecs: FractalCoefficientSpecs = FractalCoefficientSpecs.None,
    val capabilities: Set<FractalFormulaCapability> = emptySet()
) {
    Mandelbrot(
        defaultViewport = FractalViewport.of("-0.5", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.DeepZoom
        )
    ),
    Julia(
        defaultViewport = FractalViewport.of("0", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.JuliaConstant,
            FractalFormulaCapability.DeepZoom
        )
    ),
    BurningShip(
        defaultViewport = FractalViewport.of("-0.45", "-0.5", "2.4"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Tricorn(
        defaultViewport = FractalViewport.of("0", "0", "3.2"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Multibrot(
        defaultViewport = FractalViewport.of("0", "0", "3"),
        defaultPower = 3.0,
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Multicorn(
        defaultViewport = FractalViewport.of("0", "0", "3.2"),
        defaultPower = 3.0,
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Celtic(
        defaultViewport = FractalViewport.of("-0.4", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Buffalo(
        defaultViewport = FractalViewport.of("-0.5", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    PerpendicularBurningShip(
        defaultViewport = FractalViewport.of("-0.5", "-0.25", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Phoenix(
        defaultViewport = FractalViewport.of("0", "0", "3.2"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.PhoenixConstant
        )
    ),
    Nova(
        defaultViewport = FractalViewport.of("0", "0", "4"),
        defaultPower = 3.0,
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.NovaRelaxation
        )
    ),
    Newton(
        defaultViewport = FractalViewport.of("0", "0", "4"),
        defaultPower = 3.0,
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    MagnetI(
        defaultViewport = FractalViewport.of("1.5", "0", "4"),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    MagnetII(
        defaultViewport = FractalViewport.of("1.0", "0", "4"),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Lyapunov(
        defaultViewport = FractalViewport.of("3.0", "3.0", "2"),
        capabilities = setOf(FractalFormulaCapability.IterationControls)
    ),
    SierpinskiCarpet(
        defaultViewport = FractalViewport.of("0", "0", "2.2"),
        capabilities = setOf(FractalFormulaCapability.Geometric)
    ),
    SierpinskiTriangle(
        defaultViewport = FractalViewport.of("0", "0", "2.2"),
        capabilities = setOf(FractalFormulaCapability.Geometric)
    ),
    BurningShipJulia(
        defaultViewport = FractalViewport.of("0", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.JuliaConstant
        )
    ),
    CelticJulia(
        defaultViewport = FractalViewport.of("0", "0", "3"),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout,
            FractalFormulaCapability.JuliaConstant
        )
    ),
    Collatz(
        defaultViewport = FractalViewport.of("0", "0", "4"),
        defaultIterations = 160,
        defaultBailout = 100.0,
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.Bailout
        )
    ),
    Buddhabrot(
        defaultViewport = FractalViewport.of("0.4", "0", "4.444444444444444"),
        defaultIterations = 800,
        defaultBailout = 2.0,
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Hopalong(
        defaultViewport = FractalViewport.of("-0.1", "0.5", "4.2"),
        defaultIterations = 1_000,
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.A, 0.4, -10.0..10.0),
            b = coefficient(FractalCoefficientLabel.B, 1.0, -10.0..10.0),
            c = coefficient(FractalCoefficientLabel.C, 0.0, -10.0..10.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Martin(
        defaultViewport = FractalViewport.of(
            "1.5707963267948966",
            "1.5707963267948966",
            "145"
        ),
        defaultIterations = 1_000,
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.A, Math.PI, -10.0..10.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Gingerbreadman(
        defaultViewport = FractalViewport.of("2.5", "2.5", "12"),
        defaultIterations = 1_000,
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Chip(
        defaultViewport = FractalViewport.of("-7.2", "-7.9", "720"),
        defaultIterations = 1_000,
        coefficientSpecs = martinVariantCoefficients(
            a = -15.0,
            b = -19.0,
            c = 1.0
        ),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Quadruptwo(
        defaultViewport = FractalViewport.of("16.5", "17.5", "200"),
        defaultIterations = 1_000,
        coefficientSpecs = martinVariantCoefficients(
            a = 34.0,
            b = 1.0,
            c = 5.0
        ),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Threeply(
        defaultViewport = FractalViewport.of("-34", "-21", "5500"),
        defaultIterations = 1_000,
        coefficientSpecs = martinVariantCoefficients(
            a = -55.0,
            b = -1.0,
            c = -42.0
        ),
        capabilities = setOf(
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Mandelbulb(
        defaultViewport = FractalViewport.Default,
        defaultPower = 8.0,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 3.2),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.Camera3D
        )
    ),
    Mandelbox(
        defaultViewport = FractalViewport.Default,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 5.0),
        capabilities = setOf(FractalFormulaCapability.Camera3D)
    ),
    MengerSponge(
        defaultViewport = FractalViewport.Default,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(pitch = 25.0, distance = 4.0),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.Geometric
        )
    ),
    SierpinskiTetrahedron(
        defaultViewport = FractalViewport.Default,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(pitch = 25.0, distance = 4.0),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.Geometric
        )
    ),
    QuaternionJulia(
        defaultViewport = FractalViewport.Default,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 3.2),
        defaultQuaternionConstant = FractalQuaternion.JuliaDefault,
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.QuaternionConstant
        )
    ),
    SierpinskiGasket(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 128,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(pitch = 25.0, distance = 5.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Scale, 1.5, 0.5..5.0),
            b = coefficient(FractalCoefficientLabel.Fold, 1.0, 0.0..2.0),
            c = coefficient(FractalCoefficientLabel.MinimumRadius, 0.5, 0.1..2.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.Geometric
        )
    ),
    OctahedralIFS(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 160,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 9.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Scale, 2.0, 0.5..5.0),
            b = coefficient(FractalCoefficientLabel.Fold, 1.2, 0.5..3.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.Geometric
        )
    ),
    IcosahedralIFS(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 160,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 9.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Scale, 1.7, 0.5..5.0),
            b = coefficient(FractalCoefficientLabel.Fold, 1.5, 0.5..3.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.Geometric
        )
    ),
    ApollonianGasket(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 160,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 8.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Scale, 1.3, 0.5..3.0),
            b = coefficient(FractalCoefficientLabel.Fold, 1.35, 0.8..2.0),
            c = coefficient(FractalCoefficientLabel.MinimumRadius, 1.12, 0.1..2.0)
        ),
        capabilities = setOf(FractalFormulaCapability.Camera3D)
    ),
    Kleinian(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 192,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 5.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Scale, 1.5, 0.5..3.0),
            b = coefficient(FractalCoefficientLabel.Fold, 1.0, 0.5..2.0),
            c = coefficient(FractalCoefficientLabel.MinimumRadius, 0.5, 0.5..3.0)
        ),
        capabilities = setOf(FractalFormulaCapability.Camera3D)
    ),
    HybridMandelbulbJulia(
        defaultViewport = FractalViewport.Default,
        defaultPower = 8.0,
        defaultIterations = 192,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 4.0),
        capabilities = setOf(
            FractalFormulaCapability.Power,
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.JuliaConstant
        )
    ),
    QuaternionCubic(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 256,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 3.5),
        defaultQuaternionConstant = FractalQuaternion.CubicDefault,
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.QuaternionConstant
        )
    ),
    Pickover(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 1_000,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 3.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.A, 2.24, -10.0..10.0),
            b = coefficient(FractalCoefficientLabel.B, 0.43, -10.0..10.0),
            c = coefficient(FractalCoefficientLabel.C, -0.65, -10.0..10.0),
            d = coefficient(FractalCoefficientLabel.D, -2.43, -10.0..10.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Lorenz(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 1_000,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 5.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.Sigma, 10.0, 0.1..30.0),
            b = coefficient(FractalCoefficientLabel.Rho, 28.0, 0.1..100.0),
            c = coefficient(FractalCoefficientLabel.Beta, 8.0 / 3.0, 0.1..10.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    ),
    Rossler(
        defaultViewport = FractalViewport.Default,
        defaultIterations = 1_000,
        dimension = FractalDimension.ThreeDimensional,
        defaultCamera = FractalCamera(distance = 5.0),
        coefficientSpecs = FractalCoefficientSpecs(
            a = coefficient(FractalCoefficientLabel.A, 0.2, -1.0..1.0),
            b = coefficient(FractalCoefficientLabel.B, 0.2, 0.01..2.0),
            c = coefficient(FractalCoefficientLabel.C, 5.7, 0.1..20.0)
        ),
        capabilities = setOf(
            FractalFormulaCapability.Camera3D,
            FractalFormulaCapability.IterationControls,
            FractalFormulaCapability.DensityVisualization
        )
    );

    val isThreeDimensional: Boolean
        get() = dimension == FractalDimension.ThreeDimensional

    val usesPower: Boolean
        get() = FractalFormulaCapability.Power in capabilities

    val usesIterationControls: Boolean
        get() = FractalFormulaCapability.IterationControls in capabilities

    val usesBailout: Boolean
        get() = FractalFormulaCapability.Bailout in capabilities

    val usesJuliaConstant: Boolean
        get() = FractalFormulaCapability.JuliaConstant in capabilities

    val usesPhoenixConstant: Boolean
        get() = FractalFormulaCapability.PhoenixConstant in capabilities

    val usesNovaRelaxation: Boolean
        get() = FractalFormulaCapability.NovaRelaxation in capabilities

    val supportsDeepZoom: Boolean
        get() = FractalFormulaCapability.DeepZoom in capabilities

    val usesCamera: Boolean
        get() = FractalFormulaCapability.Camera3D in capabilities

    val usesQuaternionConstant: Boolean
        get() = FractalFormulaCapability.QuaternionConstant in capabilities

    val isGeometric: Boolean
        get() = FractalFormulaCapability.Geometric in capabilities

    val isDensityVisualization: Boolean
        get() = FractalFormulaCapability.DensityVisualization in capabilities

    val supportsFloor: Boolean
        get() = isThreeDimensional && !isDensityVisualization

    val usesCoefficients: Boolean
        get() = !coefficientSpecs.isEmpty

    val defaultJuliaConstant: FractalComplex
        get() = when (this) {
            Julia,
            BurningShipJulia,
            CelticJulia -> FractalComplex(real = -0.8, imaginary = 0.156)

            HybridMandelbulbJulia -> FractalComplex(real = -0.2, imaginary = 0.8)

            else -> FractalComplex.Zero
        }

    val defaultPhoenixConstant: FractalComplex
        get() = when (this) {
            Phoenix -> FractalComplex(real = -0.5, imaginary = 0.0)
            else -> FractalComplex.Zero
        }

    val defaultNovaRelaxation: Double
        get() = 1.0
}

enum class FractalFormulaCapability {
    Power,
    IterationControls,
    Bailout,
    JuliaConstant,
    PhoenixConstant,
    NovaRelaxation,
    DeepZoom,
    Camera3D,
    QuaternionConstant,
    Geometric,
    DensityVisualization
}

enum class FractalDimension {
    TwoDimensional,
    ThreeDimensional
}

private fun coefficient(
    label: FractalCoefficientLabel,
    defaultValue: Double,
    valueRange: ClosedFloatingPointRange<Double>
) = FractalCoefficientSpec(
    label = label,
    defaultValue = defaultValue,
    valueRange = valueRange
)

private fun martinVariantCoefficients(
    a: Double,
    b: Double,
    c: Double
) = FractalCoefficientSpecs(
    a = coefficient(FractalCoefficientLabel.A, a, -100.0..100.0),
    b = coefficient(FractalCoefficientLabel.B, b, -100.0..100.0),
    c = coefficient(FractalCoefficientLabel.C, c, -100.0..100.0)
)
