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

package com.t8rin.imagetoolbox.feature.fractal_generation.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalColoring
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalIterationPolicy

@Composable
internal fun FractalFormula.label(): String = stringResource(
    when (this) {
        FractalFormula.Mandelbrot -> R.string.fractal_formula_mandelbrot
        FractalFormula.Julia -> R.string.fractal_formula_julia
        FractalFormula.BurningShip -> R.string.fractal_formula_burning_ship
        FractalFormula.Tricorn -> R.string.fractal_formula_tricorn
        FractalFormula.Multibrot -> R.string.fractal_formula_multibrot
        FractalFormula.Multicorn -> R.string.fractal_formula_multicorn
        FractalFormula.Celtic -> R.string.fractal_formula_celtic
        FractalFormula.Buffalo -> R.string.fractal_formula_buffalo
        FractalFormula.PerpendicularBurningShip -> R.string.fractal_formula_perpendicular_burning_ship
        FractalFormula.Phoenix -> R.string.fractal_formula_phoenix
        FractalFormula.Nova -> R.string.fractal_formula_nova
        FractalFormula.Newton -> R.string.fractal_formula_newton
        FractalFormula.MagnetI -> R.string.fractal_formula_magnet_i
        FractalFormula.MagnetII -> R.string.fractal_formula_magnet_ii
        FractalFormula.Lyapunov -> R.string.fractal_formula_lyapunov
        FractalFormula.SierpinskiCarpet -> R.string.fractal_formula_sierpinski_carpet
        FractalFormula.SierpinskiTriangle -> R.string.fractal_formula_sierpinski_triangle
        FractalFormula.BurningShipJulia -> R.string.fractal_formula_burning_ship_julia
        FractalFormula.CelticJulia -> R.string.fractal_formula_celtic_julia
        FractalFormula.Collatz -> R.string.fractal_formula_collatz
        FractalFormula.Buddhabrot -> R.string.fractal_formula_buddhabrot
        FractalFormula.Hopalong -> R.string.fractal_formula_hopalong
        FractalFormula.Martin -> R.string.fractal_formula_martin
        FractalFormula.Gingerbreadman -> R.string.fractal_formula_gingerbreadman
        FractalFormula.Chip -> R.string.fractal_formula_chip
        FractalFormula.Quadruptwo -> R.string.fractal_formula_quadruptwo
        FractalFormula.Threeply -> R.string.fractal_formula_threeply
        FractalFormula.Clifford -> R.string.fractal_formula_clifford
        FractalFormula.DeJong -> R.string.fractal_formula_de_jong
        FractalFormula.Ikeda -> R.string.fractal_formula_ikeda
        FractalFormula.Tinkerbell -> R.string.fractal_formula_tinkerbell
        FractalFormula.GumowskiMira -> R.string.fractal_formula_gumowski_mira
        FractalFormula.BarnsleyFern -> R.string.fractal_formula_barnsley_fern
        FractalFormula.IFSDragon -> R.string.fractal_formula_ifs_dragon
        FractalFormula.IFSTwig -> R.string.fractal_formula_ifs_twig
        FractalFormula.ChristmasTree -> R.string.fractal_formula_christmas_tree
        FractalFormula.VicsekCross -> R.string.fractal_formula_vicsek_cross
        FractalFormula.PythagorasTree -> R.string.fractal_formula_pythagoras_tree
        FractalFormula.HeighwayDragon -> R.string.fractal_formula_heighway_dragon
        FractalFormula.KochSnowflake -> R.string.fractal_formula_koch_snowflake
        FractalFormula.BarnsleyMandelbrot -> R.string.fractal_formula_barnsley_mandelbrot
        FractalFormula.BarnsleyJulia -> R.string.fractal_formula_barnsley_julia
        FractalFormula.AlphaMandelbrot -> R.string.fractal_formula_alpha_mandelbrot
        FractalFormula.AlphaMandelbrotJulia -> R.string.fractal_formula_alpha_mandelbrot_julia
        FractalFormula.MandelbrotSine -> R.string.fractal_formula_mandelbrot_sine
        FractalFormula.JuliaSine -> R.string.fractal_formula_julia_sine
        FractalFormula.Spider -> R.string.fractal_formula_spider
        FractalFormula.ManOWar -> R.string.fractal_formula_man_o_war
        FractalFormula.Lambda -> R.string.fractal_formula_lambda
        FractalFormula.Thorn -> R.string.fractal_formula_thorn
        FractalFormula.BarnsleyII -> R.string.fractal_formula_barnsley_ii
        FractalFormula.BarnsleyIII -> R.string.fractal_formula_barnsley_iii
        FractalFormula.MandelbrotCosine -> R.string.fractal_formula_mandelbrot_cosine
        FractalFormula.JuliaCosine -> R.string.fractal_formula_julia_cosine
        FractalFormula.MandelbrotSinh -> R.string.fractal_formula_mandelbrot_sinh
        FractalFormula.JuliaSinh -> R.string.fractal_formula_julia_sinh
        FractalFormula.Feather -> R.string.fractal_formula_feather
        FractalFormula.Cactus -> R.string.fractal_formula_cactus
        FractalFormula.Zubieta -> R.string.fractal_formula_zubieta
        FractalFormula.Tetration -> R.string.fractal_formula_tetration
        FractalFormula.Mandelbulb -> R.string.fractal_formula_mandelbulb
        FractalFormula.Mandelbox -> R.string.fractal_formula_mandelbox
        FractalFormula.MengerSponge -> R.string.fractal_formula_menger_sponge
        FractalFormula.SierpinskiTetrahedron -> R.string.fractal_formula_sierpinski_tetrahedron
        FractalFormula.QuaternionJulia -> R.string.fractal_formula_quaternion_julia
        FractalFormula.SierpinskiGasket -> R.string.fractal_formula_sierpinski_gasket
        FractalFormula.OctahedralIFS -> R.string.fractal_formula_octahedral_ifs
        FractalFormula.IcosahedralIFS -> R.string.fractal_formula_icosahedral_ifs
        FractalFormula.ApollonianGasket -> R.string.fractal_formula_apollonian_gasket
        FractalFormula.Kleinian -> R.string.fractal_formula_kleinian
        FractalFormula.HybridMandelbulbJulia -> R.string.fractal_formula_hybrid_mandelbulb_julia
        FractalFormula.QuaternionCubic -> R.string.fractal_formula_quaternion_cubic
        FractalFormula.Pickover -> R.string.fractal_formula_pickover
        FractalFormula.Lorenz -> R.string.fractal_formula_lorenz
        FractalFormula.Rossler -> R.string.fractal_formula_rossler
    }
)

@Composable
internal fun FractalColoring.label(): String = stringResource(
    when (this) {
        FractalColoring.Smooth -> R.string.fractal_coloring_smooth
        FractalColoring.Banded -> R.string.fractal_coloring_banded
        FractalColoring.OrbitTrap -> R.string.fractal_coloring_orbit_trap
        FractalColoring.Angle -> R.string.fractal_coloring_angle
    }
)

@Composable
internal fun FractalIterationPolicy.label(): String = stringResource(
    when (this) {
        FractalIterationPolicy.Fixed -> R.string.fractal_iteration_policy_fixed
        FractalIterationPolicy.ScaleWithZoom -> R.string.fractal_iteration_policy_scale_with_zoom
        FractalIterationPolicy.Adaptive -> R.string.fractal_iteration_policy_adaptive
    }
)
