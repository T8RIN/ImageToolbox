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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType

internal enum class TextureCategory(val titleRes: Int) {
    All(R.string.all),
    Favorites(R.string.favorite),
    Nature(R.string.texture_category_nature),
    Materials(R.string.texture_category_materials),
    Space(R.string.texture_category_space),
    Patterns(R.string.texture_category_patterns),
    Abstract(R.string.texture_category_abstract)
}

internal val TextureFilterType.category: TextureCategory
    get() = when (this) {
        TextureFilterType.Grass,
        TextureFilterType.Dirt,
        TextureFilterType.Foliage,
        TextureFilterType.Sand,
        TextureFilterType.WaterRipple,
        TextureFilterType.KelpForest,
        TextureFilterType.FrostFern,
        TextureFilterType.FireflySwarm,
        TextureFilterType.Mycelium,
        TextureFilterType.CoralGrowth,
        TextureFilterType.SlimeMold,
        TextureFilterType.VeinedLeaf,
        TextureFilterType.RiverDelta,
        TextureFilterType.LichenColony,
        TextureFilterType.BacterialCulture,
        TextureFilterType.RainOnGlass,
        TextureFilterType.OceanCurrents,
        TextureFilterType.InkWashMountains,
        TextureFilterType.PhyllotaxisBloom -> TextureCategory.Nature

        TextureFilterType.BrushedMetal,
        TextureFilterType.Brick,
        TextureFilterType.Fabric,
        TextureFilterType.Ice,
        TextureFilterType.Paper,
        TextureFilterType.Rust,
        TextureFilterType.Stone,
        TextureFilterType.AdvancedWood,
        TextureFilterType.Leather,
        TextureFilterType.Concrete,
        TextureFilterType.Asphalt,
        TextureFilterType.Opal,
        TextureFilterType.DamascusSteel,
        TextureFilterType.Velvet,
        TextureFilterType.HolographicFoil,
        TextureFilterType.Geode,
        TextureFilterType.StainedGlass,
        TextureFilterType.LiquidCrystal,
        TextureFilterType.DragonScales,
        TextureFilterType.Kintsugi,
        TextureFilterType.CarbonFiber,
        TextureFilterType.CircuitBoard,
        TextureFilterType.SoapFilm,
        TextureFilterType.SnakeSkin,
        TextureFilterType.Terrazzo,
        TextureFilterType.VolcanicObsidian -> TextureCategory.Materials

        TextureFilterType.Nebula,
        TextureFilterType.Aurora,
        TextureFilterType.CosmicVortex,
        TextureFilterType.EventHorizon,
        TextureFilterType.EclipseCorona,
        TextureFilterType.Supernova,
        TextureFilterType.RingedPlanet,
        TextureFilterType.GalaxyFilaments,
        TextureFilterType.GalacticWeb,
        TextureFilterType.QuantumFoam,
        TextureFilterType.SolarGranulation,
        TextureFilterType.LunarEjecta -> TextureCategory.Space

        TextureFilterType.Check,
        TextureFilterType.Quilt,
        TextureFilterType.Honeycomb,
        TextureFilterType.Topography,
        TextureFilterType.MoireGuilloche,
        TextureFilterType.MotherboardHeatmap,
        TextureFilterType.ChladniPlate,
        TextureFilterType.CymaticRosette,
        TextureFilterType.Quasicrystal,
        TextureFilterType.Mandelbrot,
        TextureFilterType.BurningShip,
        TextureFilterType.JuliaSet,
        TextureFilterType.KaleidoscopeCrystal,
        TextureFilterType.TopologicalKnot,
        TextureFilterType.AncientRunes,
        TextureFilterType.SierpinskiTriangle,
        TextureFilterType.ApollonianGasket,
        TextureFilterType.HyperbolicTiling,
        TextureFilterType.MoebiusWeave,
        TextureFilterType.RorschachInkblot,
        TextureFilterType.OrigamiFacets,
        TextureFilterType.Truchet -> TextureCategory.Patterns

        else -> TextureCategory.Abstract
    }