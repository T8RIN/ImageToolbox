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

package com.t8rin.imagetoolbox.texture_generation.domain.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.resources.R
import kotlin.math.roundToInt

private val patternSeed = PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)

enum class PatternTextureType(
    val parameters: List<PatternParameter>,
    val colorCount: Int,
    val defaultFrequency: Float,
    private val palette: List<Int>? = null,
    val placement: PatternPlacement = PatternPlacement.Canvas,
    val hasRotation: Boolean = true,
    val hasScale: Boolean = true
) {
    HatchPattern(
        listOf(
            PatternParameter("thickness", 0.5f, 0.02f..0.98f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    SmoothHatch(
        listOf(
            PatternParameter("hardness", 1.0f, 0.1f..8.0f),
            PatternParameter("balance", 0.5f, 0.01f..0.99f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    XorPattern(
        listOf(
            PatternParameter("intensity", 4.0f, 1.0f..16.0f, integer = true)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    SquareSpiralPattern(
        listOf(
            PatternParameter("count", 4.0f, 1.0f..12.0f, integer = true),
            PatternParameter("thickness", 0.4f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 5f
    ),
    CrossStitchPattern(
        listOf(
            PatternParameter("thickness", 0.65f, 0.1f..0.95f),
            PatternParameter("shadows", 0.6f, 0.0f..1.0f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    ChromaticPlaidPattern(
        listOf(
            patternSeed,
            PatternParameter("thickness", 0.45f, 0.05f..0.95f)
        ),
        colorCount = 4,
        defaultFrequency = 12f
    ),
    PlaidPattern(
        listOf(
            patternSeed,
            PatternParameter("thickness", 0.45f, 0.05f..0.95f)
        ),
        colorCount = 4,
        defaultFrequency = 12f
    ),
    GeneratedWaves(
        listOf(
            PatternParameter("variability", 0.5f, 0.0f..1.0f),
            patternSeed
        ),
        colorCount = 4,
        defaultFrequency = 12f
    ),
    VoronoiHatch(
        listOf(
            patternSeed,
            PatternParameter("thickness", 0.45f, 0.05f..0.95f),
            PatternParameter("variability", 1.0f, 0.0f..1.0f)
        ),
        colorCount = 2,
        defaultFrequency = 12f
    ),
    LoopPattern(
        listOf(
            PatternParameter("count", 8.0f, 1.0f..64.0f, integer = true),
            PatternParameter("radius", 0.15f, 0.01f..0.5f),
            PatternParameter("thickness", 0.1f, 0.01f..0.5f),
            patternSeed
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    ScintillatingGrid(
        listOf(
            PatternParameter("thickness", 0.08f, 0.01f..0.4f),
            PatternParameter("radius", 0.09f, 0.01f..0.4f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    CircleSpiralPattern(
        listOf(
            PatternParameter("count", 5.0f, 1.0f..24.0f, integer = true)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    TestChart(emptyList(), colorCount = 2, defaultFrequency = 1f),
    BentRows(
        listOf(
            PatternParameter("offset", 0.2f, 0.0f..0.5f),
            PatternParameter("thickness", 0.04f, 0.005f..0.15f)
        ),
        colorCount = 3,
        defaultFrequency = 12f
    ),
    DiamondsIllusion(
        listOf(
            PatternParameter("count", 5.0f, 1.0f..16.0f, integer = true),
            PatternParameter("thickness", 0.5f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 4f
    ),
    RandomTriangles(
        listOf(
            PatternParameter("count", 40.0f, 1.0f..100.0f, integer = true),
            patternSeed
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    HexCubePattern(
        listOf(
            PatternParameter("thickness", 0.03f, 0.0f..0.3f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    SquareLooper(
        listOf(
            PatternParameter("time", 0.0f, 0.0f..10.0f),
            PatternParameter("thickness", 0.15f, 0.01f..0.8f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    MoireInterference(
        listOf(
            PatternParameter("intensity", 30.0f, 1.0f..100.0f),
            PatternParameter("thickness", 0.5f, 0.05f..0.95f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    TruchetArcs(
        listOf(
            patternSeed,
            PatternParameter("thickness", 0.15f, 0.02f..0.45f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    CircuitPattern(
        listOf(
            patternSeed,
            PatternParameter("thickness", 0.05f, 0.01f..0.25f),
            PatternParameter("radius", 0.13f, 0.03f..0.3f)
        ),
        colorCount = 2,
        defaultFrequency = 8f
    ),
    RosacePattern(
        listOf(
            PatternParameter("count", 12.0f, 3.0f..64.0f, integer = true),
            PatternParameter("radius", 0.24f, 0.05f..0.8f),
            PatternParameter("thickness", 0.005f, 0.005f..0.15f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    RosettePattern(
        listOf(
            PatternParameter("count", 12.0f, 3.0f..64.0f, integer = true),
            PatternParameter("radius", 0.4f, 0.05f..0.8f),
            PatternParameter("thickness", 0.006f, 0.005f..0.15f)
        ),
        colorCount = 2,
        defaultFrequency = 1f
    ),
    LightArray(
        listOf(
            PatternParameter("intensity", 0.7f, 0.0f..2.0f),
            PatternParameter("layers", 3.0f, 1.0f..6.0f, integer = true),
            patternSeed
        ),
        colorCount = 4,
        defaultFrequency = 4f
    ),
    VoronoiGems(
        listOf(
            patternSeed,
            PatternParameter("shadows", 0.5f, 0.0f..1.0f),
            PatternParameter("variability", 1.0f, 0.0f..1.0f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    CitySkyline(
        listOf(
            PatternParameter("layers", 4.0f, 1.0f..8.0f, integer = true),
            patternSeed
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    WinterLandscape(
        listOf(
            PatternParameter("time", 0.0f, 0.0f..10.0f),
            patternSeed
        ),
        colorCount = 4,
        defaultFrequency = 1f
    ),
    CircleRipplePattern(
        listOf(
            PatternParameter("radius", 0.4f, 0.1f..0.49f),
            PatternParameter("shapeAspect", 0.7f, 0.25f..2.0f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    SquareRipplePattern(
        listOf(
            PatternParameter("thickness", 0.08f, 0.01f..0.3f),
            PatternParameter("offset", 0.08f, 0.0f..0.3f)
        ),
        colorCount = 4,
        defaultFrequency = 8f
    ),
    LunariaPods(
        1f, 0xFF213D38, 0xFF64916C, 0xFFB8B684, 0xFFE8DDBA,
        PatternParameter("sizeVariation", 0.55f, 0f..1f, title = R.string.size_variation),
        PatternParameter("overlap", 0.55f, 0.1f..1f, title = R.string.texture_param_overlap),
        PatternParameter("membraneTransparency", 0.56f, 0.12f..0.94f, title = R.string.texture_param_membrane_transparency),
        patternSeed,
        hasRotation = true
    ),
    HorsetailStems(
        1f, 0xFF172F2B, 0xFF456A41, 0xFF86A45D, 0xFFC3BC82,
        PatternParameter("internodeLength", 0.19f, 0.08f..0.32f, title = R.string.texture_param_internode_length),
        PatternParameter("branchAngle", 35f, 12f..64f, title = R.string.texture_param_branch_angle),
        PatternParameter("stemDensity", 8f, 4f..14f, title = R.string.texture_param_stem_density),
        patternSeed,
        hasRotation = true
    ),
    DandelionClocks(
        1f, 0xFF123F43, 0xFF477368, 0xFFB8AD85, 0xFFEEE8C9,
        PatternParameter("crownRadius", 0.34f, 0.17f..0.52f, title = R.string.texture_param_crown_radius),
        PatternParameter("rayCount", 36f, 12f..52f, integer = true, title = R.string.texture_param_ray_count),
        PatternParameter("downDensity", 7f, 3f..10f, title = R.string.texture_param_down_density),
        patternSeed
    ),
    CalicoKoi(
        1f, 0xFFEEDCBA, 0xFFB44730, 0xFF29353A, 0xFFC6A077,
        PatternParameter("patchSize", 0.22f, 0.08f..0.42f, title = R.string.texture_param_patch_size),
        PatternParameter("scarletCoverage", 0.5f, 0.13f..0.85f, title = R.string.texture_param_scarlet_coverage),
        PatternParameter("inkFlecks", 0.38f, 0f..1f, title = R.string.texture_param_ink_flecks),
        patternSeed
    ),
    ValleyBells(
        1f, 0xFF213E3B, 0xFF4F7757, 0xFF9BAF79, 0xFFF1E4BB,
        PatternParameter("shootLean", 0.23f, -0.35f..0.5f, title = R.string.texture_param_shoot_lean),
        PatternParameter("bellCount", 5f, 3f..8f, integer = true, title = R.string.texture_param_bell_count),
        PatternParameter("leafWidth", 0.19f, 0.08f..0.34f, title = R.string.texture_param_leaf_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    SilkPoppies(
        1f, 0xFF243D35, 0xFF758362, 0xFFC75943, 0xFFEB9871,
        PatternParameter("repeatPitch", 0.25f, 0.15f..0.44f, title = R.string.texture_param_repeat_pitch),
        PatternParameter("petalOpening", 0.76f, 0.32f..1f, title = R.string.texture_param_petal_opening),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    FlyAgaricGrove(
        1f, 0xFF8B2A20, 0xFFCF492D, 0xFFDBBD8F, 0xFFF6E8CB,
        PatternParameter("flakeSize", 0.23f, 0.11f..0.42f, title = R.string.texture_param_flake_size),
        PatternParameter("fleckDensity", 0.58f, 0.15f..0.93f, title = R.string.texture_param_fleck_density),
        PatternParameter("flakeRelief", 0.58f, 0f..1f, title = R.string.texture_param_flake_relief),
        patternSeed
    ),
    BeachPebbles(
        1f, 0xFF2C4245, 0xFF917A66, 0xFFA9AAA0, 0xFFD2BEA3,
        PatternParameter("sizeVariation", 0.6f, 0f..1f, title = R.string.size_variation),
        PatternParameter("finePebbles", 0.65f, 0.1f..1f, title = R.string.texture_param_fine_pebbles),
        PatternParameter("veinFrequency", 0.25f, 0f..0.72f, title = R.string.texture_param_vein_frequency),
        patternSeed
    ),
    TravertinePools(
        1f, 0xFF286A75, 0xFF5DA299, 0xFFC6C59A, 0xFFF0D9A4,
        PatternParameter("terraceSpacing", 0.15f, 0.065f..0.28f, title = R.string.texture_param_terrace_spacing),
        PatternParameter("rimWidth", 0.11f, 0.03f..0.26f, title = R.string.texture_param_rim_width),
        PatternParameter("meander", 0.7f, 0f..1f, title = R.string.texture_meander),
        patternSeed,
        hasRotation = true
    ),
    TidepoolStars(
        1f, 0xFF994931, 0xFFCA794C, 0xFFE4B282, 0xFFF5D6A2,
        PatternParameter("rowSpacing", 0.095f, 0.045f..0.18f, title = R.string.texture_param_row_spacing),
        PatternParameter("spineSize", 0.3f, 0.12f..0.44f, title = R.string.texture_param_spine_size),
        PatternParameter("skinRoughness", 0.58f, 0f..1f, title = R.string.texture_param_skin_roughness),
        patternSeed
    ),
    WaveCutCliffs(
        1f, 0xFF536668, 0xFF95846E, 0xFFC7A88B, 0xFFE0C2A8,
        PatternParameter("grooveSpacing", 0.13f, 0.065f..0.29f, title = R.string.texture_param_groove_spacing),
        PatternParameter("channelDepth", 0.72f, 0.05f..1f, title = R.string.texture_param_channel_depth),
        PatternParameter("wetPatches", 0.35f, 0f..1f, title = R.string.texture_param_wet_patches),
        patternSeed,
        hasRotation = true
    ),
    SailingRegatta(
        1f, 0xFF8E4A35, 0xFFC4A477, 0xFF347C79, 0xFFD6C69D,
        PatternParameter("paintWear", 0.45f, 0.08f..0.9f, title = R.string.texture_param_paint_wear),
        PatternParameter("scratchDensity", 0.4f, 0.02f..0.95f, title = R.string.texture_param_scratch_density),
        patternSeed,
        hasRotation = true
    ),
    BarnacleColony(
        1f, 0xFF6D6556, 0xFF9D947B, 0xFFC2B7A2, 0xFFE2D4B9,
        PatternParameter("crowding", 0.68f, 0.25f..1f, title = R.string.texture_param_crowding),
        PatternParameter("diameterVariation", 0.57f, 0.05f..0.95f, title = R.string.texture_param_diameter_variation),
        PatternParameter("shellRibs", 0.65f, 0.05f..1f, title = R.string.texture_param_shell_ribs),
        patternSeed
    ),
    FlyingPetrels(
        1f, 0xFF455A64, 0xFF829497, 0xFFB1B9A2, 0xFFD8CBB0,
        PatternParameter("barbSpacing", 0.02f, 0.008f..0.04f, title = R.string.texture_param_barb_spacing),
        PatternParameter("barbAngle", 39f, 18f..64f, title = R.string.texture_param_barb_angle),
        PatternParameter("splitFrequency", 0.35f, 0.02f..0.92f, title = R.string.texture_param_split_frequency),
        patternSeed,
        hasRotation = true
    ),
    MoonPhases(
        1f, 0xFF34343B, 0xFF6C6260, 0xFFA09687, 0xFFD2C2AB,
        PatternParameter("clastSize", 0.14f, 0.055f..0.26f, title = R.string.texture_param_clast_size),
        PatternParameter("dustFraction", 0.39f, 0f..0.85f, title = R.string.texture_param_dust_fraction),
        PatternParameter("matrixWidth", 0.08f, 0.02f..0.22f, title = R.string.texture_param_matrix_width),
        patternSeed
    ),
    SiderealTrails(
        1f, 0xFF152638, 0xFF416079, 0xFFAA8E6D, 0xFFE7D9AD,
        PatternParameter("exposureLength", 0.58f, 0.08f..1f, title = R.string.texture_param_exposure_length),
        PatternParameter("starDensity", 68f, 24f..104f, integer = true, title = R.string.texture_param_star_density),
        PatternParameter("brightnessSpread", 0.57f, 0.05f..1f, title = R.string.texture_param_brightness_spread),
        patternSeed,
        hasRotation = true
    ),
    OrionChart(
        1f, 0xFF152F40, 0xFF436373, 0xFFBCA879, 0xFFEDE3B4,
        PatternParameter("starDensity", 17f, 8f..27f, integer = true, title = R.string.texture_param_star_density),
        PatternParameter("groupRadius", 0.17f, 0.06f..0.32f, title = R.string.texture_param_group_radius),
        PatternParameter("linkLength", 0.77f, 0.36f..1.24f, title = R.string.texture_param_link_length),
        patternSeed
    ),
    MeteorShower(
        1f, 0xFF162735, 0xFF314B65, 0xFF98ACAD, 0xFFF3DBAC,
        PatternParameter("trailLength", 0.45f, 0.12f..0.86f, title = R.string.texture_param_trail_length),
        PatternParameter("trailDensity", 12f, 5f..19f, integer = true, title = R.string.texture_param_trail_density),
        PatternParameter("angularSpread", 17f, 0f..45f, title = R.string.texture_param_angular_spread),
        patternSeed,
        hasRotation = true
    ),
    ClockworkOrrery(
        1f, 0xFF574B39, 0xFF9B794C, 0xFFC2A370, 0xFFE6C792,
        PatternParameter("toothPitch", 0.074f, 0.035f..0.14f, title = R.string.texture_param_tooth_pitch),
        PatternParameter("toothHeight", 0.68f, 0.1f..1f, title = R.string.texture_param_tooth_height),
        PatternParameter("crestWidth", 0.25f, 0.05f..0.44f, title = R.string.texture_param_crest_width),
        hasRotation = true
    ),
    CometPlume(
        1f, 0xFF132C3D, 0xFF385B74, 0xFF80A9AF, 0xFFE5DDB0,
        PatternParameter("streamFrequency", 7f, 3f..13f, integer = true, title = R.string.texture_param_stream_frequency),
        PatternParameter("fadeLength", 0.72f, 0.18f..1f, title = R.string.texture_param_fade_length),
        PatternParameter("feathering", 0.68f, 0.05f..1f, title = R.string.texture_param_feathering),
        patternSeed,
        hasRotation = true
    ),
    BinarySunset(
        1f, 0xFF263649, 0xFF86526A, 0xFFD89071, 0xFFF5D49D,
        PatternParameter("sunDiameter", 21.6f, 12f..30f, title = R.string.texture_param_sun_diameter),
        PatternParameter("sunDistance", 20.2071769f, 12f..36f, title = R.string.texture_param_distance_between_suns),
        PatternParameter("ridges", 4f, 1f..6f, integer = true, title = R.string.texture_param_ridge_count),
        PatternParameter("relief", 100f, 25f..180f, integer = true, title = R.string.texture_param_ridge_height),
        patternSeed,
        hasScale = false
    ),
    PivotFields(
        1f, 0xFF647855, 0xFFA5A176, 0xFFBFB891, 0xFFE0D8AB,
        PatternParameter("fieldSize", 0.26f, 0.15f..0.43f, title = R.string.texture_param_field_size),
        PatternParameter("furrowPitch", 0.013f, 0.006f..0.032f, title = R.string.texture_param_furrow_pitch),
        PatternParameter("sectorAngle", 82f, 17f..155f, title = R.string.texture_param_sector_angle),
        patternSeed
    ),
    AlluvialBends(
        1f, 0xFF315B69, 0xFF698E99, 0xFFB3A377, 0xFFD8C69D,
        PatternParameter("riverWidth", 0.1f, 0.035f..0.23f, title = R.string.texture_param_river_width),
        PatternParameter("bendRadius", 0.44f, 0.1f..0.82f, title = R.string.texture_param_bend_radius),
        PatternParameter("depositSpacing", 0.055f, 0.023f..0.1f, title = R.string.texture_param_deposit_spacing),
        patternSeed,
        hasRotation = true
    ),
    RakedSandGarden(
        1f, 0xFF53605C, 0xFFA29978, 0xFFC4BC95, 0xFFE5DCB4,
        PatternParameter("stoneFrequency", 5f, 2.5f..8f, title = R.string.texture_param_stone_frequency),
        PatternParameter("rakePitch", 0.017f, 0.009f..0.036f, title = R.string.texture_param_rake_pitch),
        PatternParameter("deflection", 0.67f, 0.1f..1f, title = R.string.texture_param_deflection),
        patternSeed
    ),
    BalloonFestival(
        1f, 0xFF926347, 0xFFC07861, 0xFFD7B58D, 0xFF598A91,
        PatternParameter("goreWidth", 0.13f, 0.07f..0.26f, title = R.string.texture_param_gore_width),
        PatternParameter("goreFullness", 0.65f, 0f..1f, title = R.string.texture_param_gore_fullness),
        PatternParameter("stitchPitch", 0.016f, 0.008f..0.034f, title = R.string.texture_param_stitch_pitch),
        hasRotation = true
    ),
    HarborLight(
        1f, 0xFF17484E, 0xFF407D83, 0xFF9DB3AB, 0xFFE5BC78,
        PatternParameter("flutePitch", 0.066f, 0.032f..0.14f, title = R.string.texture_param_flute_pitch),
        PatternParameter("refraction", 0.64f, 0.08f..1f, title = R.string.texture_refraction),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    PaperLanterns(
        1f, 0xFF7C5840, 0xFFC08D62, 0xFFD9B68A, 0xFFF0DCB0,
        PatternParameter("ribPitch", 0.12f, 0.055f..0.23f, title = R.string.texture_param_rib_pitch),
        PatternParameter("paperDensity", 0.55f, 0.1f..0.95f, title = R.string.texture_param_paper_density),
        PatternParameter("translucency", 0.64f, 0.1f..1f, title = R.string.texture_param_translucency),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    ParasolMarket(
        1f, 0xFF354F53, 0xFFAD6455, 0xFFD3A378, 0xFFE1C69A,
        PatternParameter("panelCount", 10f, 5f..16f, integer = true, title = R.string.texture_param_panel_count),
        PatternParameter("overlap", 0.6f, 0.2f..0.92f, title = R.string.texture_param_overlap),
        PatternParameter("pleatDepth", 0.58f, 0.08f..1f, title = R.string.texture_param_pleat_depth),
        patternSeed
    ),
    BackgammonPoints(
        1f, 0xFF685141, 0xFFB08764, 0xFFCCC0A0, 0xFFE0C7A3,
        PatternParameter("wedgeLength", 0.81f, 0.35f..0.99f, title = R.string.texture_param_wedge_length),
        PatternParameter("insetWidth", 0.73f, 0.28f..0.98f, title = R.string.texture_param_inset_width),
        PatternParameter("grainContrast", 0.62f, 0.05f..1f, title = R.string.texture_param_grain_contrast),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    DominoTable(
        1f, 0xFF3E3029, 0xFF856647, 0xFFD2B385, 0xFFF1E6C6,
        PatternParameter("inlayAspect", 1.7f, 0.85f..2.9f, title = R.string.texture_param_inlay_aspect),
        PatternParameter("jointWidth", 0.022f, 0.006f..0.08f, title = R.string.texture_param_joint_width),
        PatternParameter("pegSize", 0.065f, 0.025f..0.14f, title = R.string.texture_param_peg_size),
        patternSeed,
        hasRotation = true
    ),
    PlayingCardSuits(
        1f, 0xFF325B5D, 0xFFB85F4F, 0xFFC9AD80, 0xFFE5CFAA,
        PatternParameter("punchShape", 2f, 1f..3f, integer = true, title = R.string.texture_param_punch_shape),
        PatternParameter("bridgeWidth", 0.22f, 0.06f..0.49f, title = R.string.texture_param_bridge_width),
        PatternParameter("shadowDepth", 0.49f, 0.03f..1f, title = R.string.texture_param_shadow_depth),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    VinylCrate(
        1f, 0xFF10202B, 0xFF344553, 0xFF64818A, 0xFFCFCAB3,
        PatternParameter("groovePitch", 0.012f, 0.006f..0.025f, title = R.string.texture_param_groove_pitch),
        PatternParameter("grooveModulation", 0.5f, 0.02f..1f, title = R.string.texture_param_groove_modulation),
        PatternParameter("dustSpecks", 0.25f, 0f..0.95f, title = R.string.texture_param_dust_specks),
        hasRotation = true
    ),
    MusicBoxRoll(
        1f, 0xFF3C403A, 0xFF8B856B, 0xFFC7B98B, 0xFFE3D5AC,
        PatternParameter("trackCount", 16f, 6f..28f, integer = true, title = R.string.texture_param_track_count),
        PatternParameter("punchDensity", 0.49f, 0.12f..0.91f, title = R.string.texture_param_punch_density),
        PatternParameter("slotLength", 0.49f, 0.14f..0.88f, title = R.string.texture_param_slot_length),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    ZipperTape(
        1f, 0xFF304845, 0xFF667568, 0xFFA98C60, 0xFFDDC18A,
        PatternParameter("toothPitch", 0.04f, 0.022f..0.077f, title = R.string.texture_param_tooth_pitch),
        PatternParameter("tapeWidth", 0.73f, 0.4f..0.93f, title = R.string.texture_param_tape_width),
        PatternParameter("toothHeight", 0.58f, 0.2f..0.87f, title = R.string.texture_param_tooth_height),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    BobbinLace(
        1f, 0xFF264546, 0xFF698075, 0xFFBAAD89, 0xFFEFDFC0,
        PatternParameter("meshPitch", 0.2f, 0.12f..0.36f, title = R.string.texture_param_mesh_pitch),
        PatternParameter("threadThickness", 0.005f, 0.002f..0.012f, title = R.string.texture_param_thread_thickness),
        PatternParameter("scallopDepth", 0.2f, 0.04f..0.41f, title = R.string.texture_param_scallop_depth),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    MarquetryCompass(
        1f, 0xFF42342F, 0xFF815C42, 0xFFB89166, 0xFFE2CFA6,
        PatternParameter("pointCount", 8f, 4f..14f, integer = true, title = R.string.texture_param_point_count),
        PatternParameter("inlayWidth", 0.014f, 0.004f..0.045f, title = R.string.texture_param_inlay_width),
        PatternParameter("grainContrast", 0.62f, 0.05f..1f, title = R.string.texture_param_grain_contrast),
        placement = PatternPlacement.PhaseXY
    ),
    WaffleIron(
        1f, 0xFF825238, 0xFFA87046, 0xFFC69560, 0xFFE4BC82,
        PatternParameter("cellDepth", 0.73f, 0.08f..1f, title = R.string.texture_param_cell_depth),
        PatternParameter("rimWidth", 0.18f, 0.06f..0.35f, title = R.string.texture_param_rim_width),
        PatternParameter("browning", 0.59f, 0.04f..1f, title = R.string.texture_param_browning),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    CitrusWheels(
        1f, 0xFFB9653A, 0xFFE99853, 0xFFF4B875, 0xFFF9D499,
        PatternParameter("vesicleSize", 0.056f, 0.027f..0.095f, title = R.string.texture_param_vesicle_size),
        PatternParameter("membraneWidth", 0.018f, 0.004f..0.045f, title = R.string.texture_param_membrane_width),
        PatternParameter("translucency", 0.64f, 0.08f..0.98f, title = R.string.texture_translucency),
        patternSeed
    ),
    SaltedPretzels(
        1f, 0xFF6E4631, 0xFFB87948, 0xFFD19D65, 0xFFF2DFBA,
        PatternParameter("saltSize", 0.012f, 0.005f..0.028f, title = R.string.texture_param_salt_size),
        PatternParameter("blisterDensity", 0.48f, 0.08f..0.93f, title = R.string.texture_param_blister_density),
        PatternParameter("toasting", 0.61f, 0.05f..1f, title = R.string.texture_param_toasting),
        patternSeed
    ),
    LiquoriceAllsorts(
        1f, 0xFF283D44, 0xFFCA8F98, 0xFFE8BD7E, 0xFF74ACAA,
        PatternParameter("sliceSize", 0.22f, 0.13f..0.37f, title = R.string.texture_param_slice_size),
        PatternParameter("layerCount", 5f, 2f..9f, integer = true, title = R.string.texture_param_layer_count),
        PatternParameter("ringFraction", 0.28f, 0.02f..0.85f, title = R.string.texture_param_ring_fraction),
        patternSeed
    ),
    RavioliStamp(
        1f, 0xFF85572F, 0xFFBF8B52, 0xFFE5BE7B, 0xFFF8E4B6,
        PatternParameter("crimpPitch", 0.019f, 0.01f..0.033f, title = R.string.texture_param_crimp_pitch),
        PatternParameter("pressDepth", 0.68f, 0.04f..1f, title = R.string.texture_param_press_depth),
        PatternParameter("flourDust", 0.39f, 0f..1f, title = R.string.texture_param_flour_dust),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    CameoBrocade(
        1f, 0xFF2F5659, 0xFF807969, 0xFFC68678, 0xFFE4CF9F,
        PatternParameter("medallionSize", 0.81f, 0.5f..0.96f, title = R.string.texture_param_medallion_size),
        PatternParameter("ribPitch", 0.011f, 0.005f..0.025f, title = R.string.texture_param_rib_pitch),
        PatternParameter("reliefHeight", 0.66f, 0.1f..1f, title = R.string.texture_param_relief_height),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    BowTiePatchwork(
        1f, 0xFF476068, 0xFFA3765B, 0xFFBAA681, 0xFFD9C8A7,
        PatternParameter("knotWidth", 0.2f, 0.09f..0.34f, title = R.string.texture_param_knot_width),
        PatternParameter("stitchLength", 0.028f, 0.012f..0.055f, title = R.string.stitch_length),
        PatternParameter("fabricVariation", 0.65f, 0f..1f, title = R.string.texture_param_fabric_variation),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TasselTrim(
        1f, 0xFF305052, 0xFF997044, 0xFFCD8A5F, 0xFFE2BA8C,
        PatternParameter("tasselLength", 0.68f, 0.34f..0.86f, title = R.string.texture_param_tassel_length),
        PatternParameter("flare", 0.64f, 0.1f..1f, title = R.string.texture_param_flare),
        PatternParameter("rowDensity", 5f, 3f..8f, integer = true, title = R.string.texture_param_row_density),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    MillefioriCanes(
        1f, 0xFF36565B, 0xFFA96B62, 0xFFEAC789, 0xFF86A69F,
        PatternParameter("petalCount", 7f, 4f..12f, integer = true, title = R.string.texture_param_petal_count),
        PatternParameter("diameterVariation", 0.52f, 0.05f..0.89f, title = R.string.texture_param_diameter_variation),
        PatternParameter("glassCompression", 0.3f, 0.01f..0.78f, title = R.string.texture_param_glass_compression),
        patternSeed
    ),
    GreekMeander(
        1f, 0xFF354E53, 0xFF688182, 0xFFB88C58, 0xFFE6CFA2,
        PatternParameter("meanderPitch", 0.16f, 0.09f..0.3f, title = R.string.texture_param_meander_pitch),
        PatternParameter("borderHeight", 0.22f, 0.12f..0.39f, title = R.string.texture_param_border_height),
        PatternParameter("lineWidth", 0.051f, 0.023f..0.09f, title = R.string.texture_param_line_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PaperChainGarland(
        1f, 0xFF2F4D50, 0xFFA8926F, 0xFFD6BC91, 0xFFF2DEB4,
        PatternParameter("cutLength", 0.8f, 0.48f..0.94f, title = R.string.texture_param_cut_length),
        PatternParameter("slitOpening", 0.34f, 0.09f..0.59f, title = R.string.texture_param_slit_opening),
        PatternParameter("bridgeWidth", 0.14f, 0.04f..0.28f, title = R.string.texture_param_bridge_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    RibbonCockade(
        1f, 0xFF854A50, 0xFFAC6971, 0xFFD49A94, 0xFFF0C4AA,
        PatternParameter("pleatCount", 29f, 12f..53f, integer = true, title = R.string.texture_param_pleat_count),
        PatternParameter("foldDepth", 0.7f, 0.08f..1f, title = R.string.texture_param_fold_depth),
        PatternParameter("highlightWidth", 0.18f, 0.04f..0.39f, title = R.string.texture_highlight_width),
        patternSeed,
        hasRotation = true
    ),
    VenetianShutters(
        1f, 0xFF233D41, 0xFF38645D, 0xFF689187, 0xFFA5A996,
        PatternParameter("slatTilt", 29f, 6f..68f, title = R.string.texture_param_slat_tilt),
        PatternParameter("slatWidth", 0.077f, 0.04f..0.16f, title = R.string.texture_param_slat_width),
        PatternParameter("lightGap", 0.105f, 0.025f..0.31f, title = R.string.texture_param_light_gap),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    SandstoneArcade(
        1f, 0xFF8E7661, 0xFFB69877, 0xFFD8BE9A, 0xFFE8CEAB,
        PatternParameter("archRadius", 0.44f, 0.25f..0.49f, title = R.string.texture_param_arch_radius),
        PatternParameter("voussoirCount", 11f, 5f..18f, integer = true, title = R.string.texture_param_voussoir_count),
        PatternParameter("jointWidth", 0.018f, 0.005f..0.041f, title = R.string.texture_param_joint_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    GearBlueprint(
        1f, 0xFF53666A, 0xFF889797, 0xFFBABCAB, 0xFFE6D7B2,
        PatternParameter("threadPitch", 0.082f, 0.04f..0.16f, title = R.string.texture_param_thread_pitch),
        PatternParameter("helixAngle", 24f, 7f..52f, title = R.string.texture_param_helix_angle),
        PatternParameter("chamferWidth", 0.2f, 0.05f..0.36f, title = R.string.texture_param_chamfer_width),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    AdmissionTickets(
        1f, 0xFF665D4F, 0xFFA98965, 0xFFD0B28D, 0xFFE0C7A2,
        PatternParameter("holePitch", 0.046f, 0.021f..0.08f, title = R.string.texture_param_hole_pitch),
        PatternParameter("bridgeWidth", 0.37f, 0.09f..0.72f, title = R.string.texture_param_bridge_width),
        PatternParameter("tornFraction", 0.3f, 0.01f..0.92f, title = R.string.texture_param_torn_fraction),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    SawtoothRoofscape(
        1f, 0xFF304E58, 0xFF698A8E, 0xFFA4B5A8, 0xFFC8C5A1,
        PatternParameter("roofAngle", 28f, 8f..62f, title = R.string.texture_param_roof_angle),
        PatternParameter("mullionWidth", 0.023f, 0.007f..0.053f, title = R.string.texture_param_mullion_width),
        PatternParameter("glassFrost", 0.45f, 0.02f..0.93f, title = R.string.texture_param_glass_frost),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    FanVault(
        1f, 0xFF727361, 0xFFA5A387, 0xFFD0C3A1, 0xFFE8D7B0,
        PatternParameter("ribCount", 9f, 4f..15f, integer = true, title = R.string.texture_param_rib_count),
        PatternParameter("fanSpread", 0.8f, 0.42f..1.04f, title = R.string.texture_param_fan_spread),
        PatternParameter("ribHeight", 0.63f, 0.04f..1f, title = R.string.texture_param_rib_height),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    WoodcutSwells(
        1f, 0xFF203C3C, 0xFF4B6660, 0xFFC6B987, 0xFFE8D6AC,
        PatternParameter("hatchPitch", 0.024f, 0.012f..0.05f, title = R.string.texture_param_hatch_pitch),
        PatternParameter("cutWidth", 0.5f, 0.17f..0.83f, title = R.string.texture_param_cut_width),
        patternSeed,
        hasRotation = true
    ),
    ApartmentPlan(
        1f, 0xFF535954, 0xFF8B8B77, 0xFFB7AF95, 0xFFD2C2A1,
        PatternParameter("slabAspect", 1.85f, 0.94f..2.8f, title = R.string.texture_param_slab_aspect),
        PatternParameter("crampFrequency", 0.63f, 0.08f..0.97f, title = R.string.texture_param_cramp_frequency),
        PatternParameter("edgeChips", 0.54f, 0.02f..1f, title = R.string.texture_param_edge_chips),
        patternSeed,
        hasRotation = true
    ),
    RooftopTanks(
        1f, 0xFF734D3F, 0xFFAE6C49, 0xFF578779, 0xFF9EB19A,
        PatternParameter("sheetWidth", 0.27f, 0.13f..0.43f, title = R.string.texture_param_sheet_width),
        PatternParameter("patinaCoverage", 0.55f, 0.06f..0.94f, title = R.string.texture_param_patina_coverage),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    BookclothSpines(
        1f, 0xFF3E5E56, 0xFF66827A, 0xFFA89C74, 0xFFC3AC83,
        PatternParameter("weavePitch", 0.011f, 0.006f..0.024f, title = R.string.texture_param_weave_pitch),
        PatternParameter("coatingThickness", 0.52f, 0.04f..0.94f, title = R.string.texture_param_coating_thickness),
        PatternParameter("foldWear", 0.45f, 0.01f..1f, title = R.string.texture_param_fold_wear),
        hasRotation = true
    ),
    HarbourBunting(
        1f, 0xFF5B7774, 0xFFAF846A, 0xFFD5C3A1, 0xFFECE0BA,
        PatternParameter("panelWidth", 0.2f, 0.11f..0.35f, title = R.string.texture_param_panel_width),
        PatternParameter("stitchPitch", 0.016f, 0.007f..0.031f, title = R.string.texture_param_stitch_pitch),
        PatternParameter("clothTranslucency", 0.4f, 0.03f..0.88f, title = R.string.texture_param_cloth_translucency),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    PantographLinkage(
        1f, 0xFF385353, 0xFF786C52, 0xFFB49562, 0xFFD7BD8E,
        PatternParameter("openingAngle", 37f, 18f..62f, title = R.string.texture_param_opening_angle),
        PatternParameter("barWidth", 0.011f, 0.005f..0.023f, title = R.string.texture_param_bar_width),
        PatternParameter("rivetRadius", 0.015f, 0.006f..0.027f, title = R.string.texture_param_rivet_radius),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    DualTrace(
        1f, 0xFF17373B, 0xFF31706E, 0xFF77A89B, 0xFFD3C99A,
        PatternParameter("tracePhase", 0.24f, 0f..0.88f, title = R.string.texture_param_trace_phase),
        PatternParameter("signalPeriod", 0.3f, 0.14f..0.57f, title = R.string.texture_param_signal_period),
        PatternParameter("afterglow", 0.49f, 0.03f..1f, title = R.string.texture_param_afterglow),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    StressFringes(
        1.75f, 0xFF18132E, 0xFF235CA3, 0xFFE98B55, 0xFFF5E6A7,
        PatternParameter("intensity", 4.9f, 1f..9f, title = R.string.texture_param_load),
        hasRotation = true
    ),
    WindBarbs(
        1f, 0xFF40534F, 0xFF778970, 0xFFC8C8A7, 0xFFE8E0C5,
        PatternParameter("hatchLength", 0.61f, 0.18f..0.95f, title = R.string.texture_param_hatch_length),
        PatternParameter("strokeDensity", 37f, 18f..55f, integer = true, title = R.string.texture_param_stroke_density),
        PatternParameter("vortexRadius", 0.28f, 0.09f..0.52f, title = R.string.texture_param_vortex_radius),
        patternSeed
    ),
    AlignmentNomogram(
        1f, 0xFF636963, 0xFF9A9B83, 0xFFD7CFB2, 0xFFE9DEBC,
        PatternParameter("scaleCurvature", 0.66f, 0.04f..1f, title = R.string.texture_param_scale_curvature),
        PatternParameter("tickPitch", 0.027f, 0.012f..0.056f, title = R.string.texture_param_tick_pitch),
        PatternParameter("tickLength", 0.03f, 0.009f..0.066f, title = R.string.texture_param_tick_length),
        hasRotation = true
    ),
    VacuumTriodes(
        1f, 0xFF29363B, 0xFF566368, 0xFFB57956, 0xFFE9B379,
        PatternParameter("helixPitch", 0.083f, 0.039f..0.16f, title = R.string.texture_param_helix_pitch),
        PatternParameter("layerGap", 0.24f, 0.04f..0.49f, title = R.string.texture_param_layer_gap),
        PatternParameter("filamentHeat", 0.68f, 0.02f..1f, title = R.string.texture_param_filament_heat),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    BasePairs(
        1f, 0xFF2A4247, 0xFF907561, 0xFFCBA171, 0xFF75A79A,
        PatternParameter("helixPitch", 0.32f, 0.16f..0.52f, title = R.string.texture_param_helix_pitch),
        PatternParameter("neighbourPhase", 0.36f, 0f..0.88f, title = R.string.texture_param_neighbour_phase),
        PatternParameter("rungFrequency", 10f, 4f..19f, integer = true, title = R.string.texture_param_rung_frequency),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    HarmonicSpectrogram(
        1.45f, 0xFF14162F, 0xFF314A78, 0xFFC5666D, 0xFFF9CB76,
        PatternParameter("intensity", 1.2f, 0.5f..2f, title = R.string.texture_param_harmonic_gain),
        PatternParameter("seed", 3f, 0f..10000f, integer = true),
        hasRotation = true
    ),
    GelBands(
        1f, 0xFF152C37, 0xFF2B5260, 0xFF68A2A4, 0xFFC0D0AD,
        PatternParameter("laneCount", 14f, 5f..23f, integer = true, title = R.string.texture_param_lane_count),
        PatternParameter("bandWidth", 0.55f, 0.18f..0.91f, title = R.string.texture_param_band_width),
        PatternParameter("bandBlur", 0.36f, 0.03f..0.96f, title = R.string.texture_param_band_blur),
        patternSeed,
        hasRotation = true
    ),
    Stereonet(
        1f, 0xFF32565A, 0xFF76918A, 0xFFC4B28D, 0xFFE2CD9D,
        PatternParameter("arcCount", 10f, 5f..19f, integer = true, title = R.string.texture_param_arc_count),
        PatternParameter("curvature", 0.62f, 0.04f..1f, title = R.string.texture_param_curvature),
        PatternParameter("threadWidth", 0.0045f, 0.002f..0.011f, title = R.string.thread_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PressureDials(
        1f, 0xFF565C5A, 0xFF848983, 0xFFB2B1A3, 0xFFE5DDC5,
        PatternParameter("spotOverlap", 0.44f, 0.15f..0.72f, title = R.string.texture_param_spot_overlap),
        PatternParameter("grindingLines", 23f, 9f..36f, integer = true, title = R.string.texture_param_grinding_lines),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    AirfoilStreamlines(
        1f, 0xFF26505B, 0xFF44727B, 0xFF88ABA3, 0xFFC3CAAC,
        PatternParameter("linePitch", 0.031f, 0.014f..0.061f, title = R.string.texture_param_line_pitch),
        PatternParameter("deflection", 0.58f, 0.05f..1f, title = R.string.texture_param_deflection),
        PatternParameter("wakeLength", 0.38f, 0.12f..0.69f, title = R.string.texture_param_wake_length),
        patternSeed,
        hasRotation = true
    ),
    MolecularModel(
        1f, 0xFF53675B, 0xFF8F917B, 0xFFCABF9C, 0xFFE8DCC1,
        PatternParameter("latticePitch", 0.104f, 0.056f..0.18f, title = R.string.texture_param_lattice_pitch),
        PatternParameter("defectFrequency", 0.43f, 0.01f..0.94f, title = R.string.texture_param_defect_frequency),
        PatternParameter("nodeSize", 0.013f, 0.004f..0.027f, title = R.string.texture_param_node_size),
        patternSeed
    ),
    MarshHeron(
        1f, 0xFF1C3737, 0xFF4D6554, 0xFFB69D60, 0xFFDFC98B,
        PatternParameter("reedCount", 12f, 5f..22f, title = R.string.texture_param_stem_density),
        PatternParameter("reedTilt", 0.38f, 0.05f..0.85f, title = R.string.texture_param_stem_tilt),
        PatternParameter("internode", 0.26f, 0.11f..0.45f, title = R.string.texture_param_internode_length),
        patternSeed,
        hasRotation = true
    ),
    SwitchbackPass(
        1f, 0xFF443D3C, 0xFF726153, 0xFF9E8D76, 0xFFD0B89B,
        PatternParameter("rubbleSize", 1f, 0.52f..1.7f, title = R.string.texture_param_fragment_size),
        PatternParameter("slopeGrade", 0.45f, 0f..1f, title = R.string.texture_param_scree_slope),
        PatternParameter("gravelShare", 0.58f, 0.09f..0.96f, title = R.string.texture_param_fine_gravel),
        patternSeed
    ),
    CicadaAmber(
        1f, 0xFF213C37, 0xFF506C55, 0xFF92B59D, 0xFFD4B399,
        PatternParameter("crossVeins", 12f, 6f..24f, title = R.string.texture_param_cross_vein_density),
        PatternParameter("membraneSheen", 0.58f, 0f..1f, title = R.string.texture_param_membrane_iridescence),
        PatternParameter("veinWidth", 0.068f, 0.031f..0.13f, title = R.string.texture_vein_width),
        patternSeed,
        hasRotation = true
    ),
    Passionflower(
        1f, 0xFF314D4C, 0xFF819B86, 0xFFAB94AD, 0xFFE7DFCB,
        PatternParameter("flowerPitch", 0.32f, 0.2f..0.49f, title = R.string.texture_param_flower_spacing),
        PatternParameter("filaments", 51f, 22f..80f, integer = true, title = R.string.texture_param_corona_filaments),
        PatternParameter("petalLength", 0.48f, 0.32f..0.6f, title = R.string.texture_param_petal_length),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    IcebergKeel(
        1f, 0xFF164759, 0xFF367488, 0xFF86B2B7, 0xFFDCE8DC,
        PatternParameter("bubbleBands", 0.18f, 0.08f..0.36f, title = R.string.texture_param_band_spacing),
        PatternParameter("bubbleSize", 0.11f, 0.05f..0.22f, title = R.string.texture_param_bubble_diameter),
        PatternParameter("iceCloud", 0.46f, 0f..1f, title = R.string.texture_param_ice_clouding),
        patternSeed
    ),
    SoundingFlukes(
        1f, 0xFF163E47, 0xFF397774, 0xFF89B0A2, 0xFFE1DFBC,
        PatternParameter("wakeAngle", 0.78f, 0.35f..1.4f, title = R.string.texture_param_wake_angle),
        PatternParameter("wakePitch", 0.078f, 0.043f..0.145f, title = R.string.texture_param_crest_spacing),
        PatternParameter("foamCover", 0.44f, 0.05f..0.94f, title = R.string.texture_param_foam_cover),
        patternSeed,
        hasRotation = true
    ),
    SamaraFlight(
        1f, 0xFF627260, 0xFF9E9E77, 0xFFAC8E54, 0xFFE1C58C,
        PatternParameter("seedDensity", 4.5f, 2.4f..7f, title = R.string.texture_param_samara_density),
        PatternParameter("wingSpan", 0.47f, 0.3f..0.62f, title = R.string.texture_param_wingspan),
        PatternParameter("angleSpread", 0.79f, 0.1f..1f, title = R.string.texture_param_angular_spread),
        patternSeed,
        hasRotation = true
    ),
    OctopusArm(
        1f, 0xFF603442, 0xFF9C5C59, 0xFFCE8D73, 0xFFF0BE98,
        PatternParameter("cupPitch", 1f, 0.65f..1.55f, title = R.string.texture_param_sucker_spacing),
        PatternParameter("rimHeight", 0.7f, 0.16f..1f, title = R.string.texture_param_rim_height),
        PatternParameter("skinFolds", 0.5f, 0f..1f, title = R.string.texture_param_skin_folds),
        patternSeed
    ),
    WardianCase(
        1f, 0xFF4C6B65, 0xFF85998C, 0xFFB4C2B2, 0xFFE0E6D6,
        PatternParameter("fogAmount", 0.57f, 0.04f..0.95f, title = R.string.texture_param_condensation),
        PatternParameter("dropSize", 0.13f, 0.055f..0.26f, title = R.string.texture_param_droplet_size),
        PatternParameter("runWidth", 0.035f, 0.006f..0.085f, title = R.string.texture_param_run_width),
        patternSeed,
        hasRotation = true
    ),
    AntGalleries(
        1f, 0xFF2C211E, 0xFF6B422C, 0xFFA67851, 0xFFD2A16E,
        PatternParameter("roomSize", 0.115f, 0.05f..0.24f, title = R.string.texture_param_chamber_size),
        PatternParameter("junctions", 0.68f, 0.03f..0.98f, title = R.string.texture_param_branch_probability),
        PatternParameter("tunnelWidth", 0.064f, 0.03f..0.13f, title = R.string.texture_param_tunnel_width),
        patternSeed
    ),
    RainShafts(
        1f, 0xFF263F4E, 0xFF607782, 0xFF9BAAAD, 0xFFD4D6C9,
        PatternParameter("rainTilt", 0.35f, -0.65f..0.85f, title = R.string.texture_param_rain_angle),
        PatternParameter("rainLength", 0.52f, 0.12f..0.93f, title = R.string.texture_param_streak_length),
        PatternParameter("veilCover", 0.61f, 0.05f..1f, title = R.string.texture_param_rain_density),
        patternSeed,
        hasRotation = true
    ),
    PitcherPlants(
        1f, 0xFF27413F, 0xFF536B56, 0xFF975E59, 0xFFE0AD7C,
        PatternParameter("pitcherDensity", 3.6f, 2.2f..5.4f, title = R.string.texture_param_pitcher_density),
        PatternParameter("cupDepth", 0.7f, 0.46f..0.88f, title = R.string.texture_param_cup_depth),
        PatternParameter("tendrilReach", 0.53f, 0.15f..0.9f, title = R.string.texture_param_tendril_length),
        patternSeed,
        hasRotation = true
    ),
    PoleFoldShibori(
        1f, 0xFF162D57, 0xFF315C89, 0xFF85A6BA, 0xFFEFE6CF,
        PatternParameter("tiePitch", 0.072f, 0.036f..0.13f, title = R.string.texture_param_binding_pitch),
        PatternParameter("foldBunch", 0.6f, 0.1f..1f, title = R.string.texture_param_fold_compression),
        PatternParameter("dyeBleed", 0.53f, 0.05f..1f, title = R.string.texture_param_dye_bleed),
        patternSeed,
        hasRotation = true
    ),
    PeacockMarbling(
        1f, 0xFF234955, 0xFF349A90, 0xFFE3B775, 0xFFC27369,
        PatternParameter("combPitch", 0.17f, 0.09f..0.29f, title = R.string.texture_param_comb_tooth_pitch),
        PatternParameter("combTravel", 0.72f, 0.15f..1f, title = R.string.texture_param_comb_travel),
        PatternParameter("groundShare", 0.29f, 0.09f..0.56f, title = R.string.texture_param_ground_fraction),
        patternSeed,
        hasRotation = true
    ),
    LogCabinQuilt(
        1f, 0xFF36545B, 0xFF9A6458, 0xFFDAB799, 0xFF8B9C91,
        PatternParameter("stripCount", 5f, 3f..9f, integer = true, title = R.string.texture_param_strip_count),
        PatternParameter("seamWidth", 0.045f, 0.012f..0.13f, title = R.string.texture_param_seam_width),
        PatternParameter("blockTurn", 1f, 0f..1f, integer = true, title = R.string.texture_param_block_rotation),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    CoiledRagRug(
        1f, 0xFF4C6069, 0xFFA56057, 0xFFD6A360, 0xFFE1CDB0,
        PatternParameter("cordWidth", 0.056f, 0.036f..0.11f, title = R.string.texture_param_cord_thickness),
        PatternParameter("braidRate", 32f, 13f..62f, title = R.string.texture_param_braid_frequency),
        PatternParameter("channelDepth", 0.58f, 0.06f..1f, title = R.string.texture_param_groove_depth),
        patternSeed,
        hasRotation = true
    ),
    ButtonTufting(
        1f, 0xFF372E48, 0xFF67576E, 0xFF998292, 0xFFC39C73,
        PatternParameter("diamondAspect", 1.4f, 0.75f..2.2f, title = R.string.texture_param_diamond_aspect),
        PatternParameter("tuftDepth", 0.74f, 0.08f..1f, title = R.string.texture_param_tuft_depth),
        PatternParameter("buttonSize", 0.068f, 0.032f..0.12f, title = R.string.texture_param_button_diameter),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TornPosterWall(
        1f, 0xFF354E55, 0xFFC07461, 0xFFB9A969, 0xFFE3BE98,
        PatternParameter("posterLayers", 5f, 2f..7f, integer = true, title = R.string.texture_param_paper_layers),
        PatternParameter("tearSize", 1f, 0.52f..1.7f, title = R.string.texture_param_tear_size),
        PatternParameter("edgeFiber", 0.68f, 0.05f..1f, title = R.string.texture_param_edge_fibers),
        patternSeed
    ),
    LatteRosetta(
        1f, 0xFF543228, 0xFF84513C, 0xFFC68E65, 0xFFF1D9AC,
        PatternParameter("frothSize", 0.15f, 0.065f..0.28f, title = R.string.texture_param_bubble_size),
        PatternParameter("milkShare", 0.43f, 0.1f..0.85f, title = R.string.texture_param_milk_fraction),
        PatternParameter("milkCurl", 0.65f, 0.1f..1f, title = R.string.texture_param_swirl),
        patternSeed,
        hasRotation = true
    ),
    SalmonMarbling(
        1f, 0xFFAE4135, 0xFFF99873, 0xFFFFBD9F, 0xFFFFE4C7,
        PatternParameter("fatWidth", 0.055f, 0.02f..0.15f, title = R.string.texture_param_fat_vein_thickness),
        PatternParameter("fatForks", 0.34f, 0.03f..1f, title = R.string.texture_branching),
        PatternParameter("layerPitch", 1f, 0.55f..1.6f, title = R.string.texture_param_vein_spacing),
        patternSeed,
        hasRotation = true
    ),
    PomegranateCut(
        1f, 0xFF681D37, 0xFFB43F50, 0xFFD2776E, 0xFFF2C094,
        PatternParameter("arilSize", 1f, 0.55f..1.7f, title = R.string.texture_param_aril_size),
        PatternParameter("septaWidth", 0.052f, 0.015f..0.14f, title = R.string.texture_param_pith_width),
        PatternParameter("arilGloss", 0.74f, 0.05f..1f, title = R.string.texture_param_aril_gloss),
        patternSeed
    ),
    LetterpressOverprint(
        1.7f, 0xFFEBDAB3, 0xFFEF7660, 0xFF107D83, 0xFF283F51,
        PatternParameter("offset", 0.018f, -0.12f..0.12f, title = R.string.texture_param_ink_misregistration),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PaperQuilling(
        1f, 0xFF4D4D46, 0xFF83988B, 0xFFC28272, 0xFFE1BA8E,
        PatternParameter("coilSize", 1f, 0.55f..1.7f, title = R.string.texture_param_roll_size),
        PatternParameter("windPitch", 0.085f, 0.045f..0.15f, title = R.string.texture_param_winding_pitch),
        PatternParameter("paperGauge", 0.19f, 0.07f..0.38f, title = R.string.texture_param_paper_thickness),
        patternSeed
    ),
    GoldLeafTransfer(
        1f, 0xFF71271E, 0xFFB06B37, 0xFFC99940, 0xFFFFE4A2,
        PatternParameter("gildingWear", 0.37f, 0.24f..0.6f, title = R.string.texture_param_leaf_wear),
        PatternParameter("foilCreases", 0.85f, 0.05f..1f, title = R.string.texture_param_crease_relief),
        PatternParameter("leafSize", 1f, 0.52f..1.8f, title = R.string.texture_param_leaf_size),
        patternSeed
    ),
    CaneWebbing(
        1f, 0xFF282320, 0xFF634A34, 0xFFAC7C48, 0xFFE5C58A,
        PatternParameter("reedWidth", 0.105f, 0.06f..0.18f, title = R.string.texture_param_cane_width),
        PatternParameter("weaveDepth", 0.9f, 0.05f..1f, title = R.string.texture_param_weave_depth),
        PatternParameter("fiberGrain", 0.48f, 0f..1f, title = R.string.texture_param_fiber_definition),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PleatedHandFan(
        1f, 0xFF736653, 0xFFA79272, 0xFFD0BB94, 0xFFF1E2BC,
        PatternParameter("pleatPitch", 0.077f, 0.035f..0.14f, title = R.string.texture_param_pleat_spacing),
        PatternParameter("pleatDepth", 0.65f, 0.05f..1f, title = R.string.texture_param_fold_depth),
        PatternParameter("facetBias", 0.64f, 0.25f..0.76f, title = R.string.texture_param_fold_asymmetry),
        patternSeed,
        hasRotation = true
    ),
    TailorTape(
        1f, 0xFF394E51, 0xFF747767, 0xFFC8A06B, 0xFFE9CF9C,
        PatternParameter("tapeWidth", 0.22f, 0.13f..0.34f, title = R.string.texture_param_tape_width),
        PatternParameter("tapeBend", 0.22f, 0.04f..0.45f, title = R.string.texture_param_ribbon_curvature),
        PatternParameter("tickPitch", 0.016f, 0.009f..0.032f, title = R.string.texture_param_graduation_pitch),
        patternSeed,
        hasRotation = true
    ),
    PalmetteFrieze(
        2.1f, 0xFF283D50, 0xFF6B97A1, 0xFFF1BD68, 0xFFDF7D68,
        PatternParameter("thickness", 0.046f, 0.018f..0.08f, title = R.string.texture_param_leaf_thickness),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    Switchyard(
        1f, 0xFF333C39, 0xFF6C6961, 0xFF967252, 0xFFB3B8AE,
        PatternParameter("tiePitch", 0.16f, 0.095f..0.25f, title = R.string.texture_param_sleeper_spacing),
        PatternParameter("railGauge", 0.41f, 0.24f..0.59f, title = R.string.texture_param_rail_gauge),
        PatternParameter("ballastSize", 1f, 0.55f..1.9f, title = R.string.texture_param_ballast_size),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    SuspensionSpan(
        1f, 0xFF303B3E, 0xFF66777B, 0xFFA0ACA6, 0xFFE4DFC9,
        PatternParameter("strandCount", 5f, 3f..9f, integer = true, title = R.string.texture_param_strand_count),
        PatternParameter("layPitch", 1f, 0.5f..1.8f, title = R.string.texture_param_rope_lay),
        PatternParameter("wireShine", 0.77f, 0.08f..1f, title = R.string.texture_param_wire_shine),
        patternSeed,
        hasRotation = true
    ),
    RainCrosswalk(
        1f, 0xFF17232B, 0xFF656B68, 0xFFB38C4A, 0xFFF2D98B,
        PatternParameter("markWidth", 0.52f, 0.16f..0.85f, title = R.string.texture_param_stripe_width),
        PatternParameter("paintWear", 0.46f, 0.27f..0.68f, title = R.string.texture_param_paint_wear),
        PatternParameter("roadWetness", 0.29f, 0f..1f, title = R.string.texture_param_asphalt_wetness),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    CofferedRotunda(
        1f, 0xFF584C46, 0xFF887866, 0xFFB7A28B, 0xFFE0D2B8,
        PatternParameter("cofferAspect", 1.3f, 0.72f..1.85f, title = R.string.texture_param_coffer_aspect),
        PatternParameter("cofferDepth", 0.66f, 0.08f..1f, title = R.string.texture_param_recess_depth),
        PatternParameter("rimGauge", 0.04f, 0.02f..0.075f, title = R.string.texture_param_moulding_width),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    StationCanopy(
        1f, 0xFF2E4748, 0xFF617D78, 0xFF9AAFA4, 0xFFD1D9C6,
        PatternParameter("meshPitch", 0.093f, 0.047f..0.18f, title = R.string.texture_param_mesh_pitch),
        PatternParameter("wireWidth", 0.035f, 0.015f..0.08f, title = R.string.texture_param_wire_thickness),
        PatternParameter("glassHaze", 0.57f, 0.04f..1f, title = R.string.texture_param_glass_haze),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PendulumCase(
        1f, 0xFF2C3739, 0xFF716044, 0xFFB09359, 0xFFE1CC91,
        PatternParameter("linkLength", 0.115f, 0.075f..0.19f, title = R.string.texture_param_link_length),
        PatternParameter("linkGauge", 0.27f, 0.13f..0.41f, title = R.string.texture_param_wire_gauge),
        PatternParameter("brassShine", 0.77f, 0.04f..1f, title = R.string.texture_param_brass_shine),
        patternSeed,
        hasRotation = true
    ),
    ScrewBookpress(
        1f, 0xFF472E2D, 0xFF7A4845, 0xFFB16F61, 0xFFE7AD8A,
        PatternParameter("toolPitch", 0.16f, 0.09f..0.26f, title = R.string.texture_param_stamp_spacing),
        PatternParameter("stampDepth", 0.72f, 0.08f..1f, title = R.string.texture_param_impression_depth),
        PatternParameter("hideGrain", 0.52f, 0.03f..1f, title = R.string.texture_param_hide_grain),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TheatreWings(
        1f, 0xFF4B3541, 0xFF864D52, 0xFFBC7563, 0xFFE8AC89,
        PatternParameter("swagPitch", 0.34f, 0.19f..0.55f, title = R.string.texture_param_gather_spacing),
        PatternParameter("swagDrop", 0.27f, 0.09f..0.47f, title = R.string.texture_param_swag_depth),
        PatternParameter("drapeFolds", 7f, 4f..13f, integer = true, title = R.string.texture_fold_density),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    HornGramophone(
        1f, 0xFF67533B, 0xFF9B804E, 0xFFC5A56E, 0xFFE8CD91,
        PatternParameter("rivetPitch", 0.12f, 0.07f..0.21f, title = R.string.texture_param_rivet_pitch),
        PatternParameter("hammerMarks", 0.58f, 0.08f..1f, title = R.string.texture_param_hammer_marks),
        PatternParameter("brassPatina", 0.38f, 0f..1f, title = R.string.texture_patina),
        patternSeed,
        hasRotation = true
    ),
    CanalLock(
        1f, 0xFF344748, 0xFF5C7472, 0xFF85948A, 0xFFA07655,
        PatternParameter("pilePitch", 0.17f, 0.085f..0.3f, title = R.string.texture_param_profile_pitch),
        PatternParameter("fluteDepth", 0.71f, 0.1f..1f, title = R.string.texture_param_rib_depth),
        PatternParameter("runLength", 0.68f, 0.15f..1f, title = R.string.texture_param_rust_streak_length),
        patternSeed,
        hasRotation = true
    ),
    FunicularIncline(
        1f, 0xFF253438, 0xFF586567, 0xFF929791, 0xFFC6C5B9,
        PatternParameter("gearPitch", 0.095f, 0.051f..0.16f, title = R.string.texture_param_tooth_pitch),
        PatternParameter("toothForm", 0.53f, 0.15f..0.85f, title = R.string.texture_param_tooth_profile),
        PatternParameter("greaseDepth", 0.44f, 0f..1f, title = R.string.texture_param_grease_depth),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    CinemaMarquee(
        1f, 0xFF303F45, 0xFF875A48, 0xFFD59459, 0xFFFFE1A3,
        PatternParameter("lampPitch", 0.145f, 0.09f..0.22f, title = R.string.texture_param_lamp_pitch),
        PatternParameter("haloRadius", 0.31f, 0.15f..0.55f, title = R.string.texture_param_halo_radius),
        PatternParameter("lampVariance", 0.58f, 0f..1f, title = R.string.texture_param_brightness_variation),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    SolarProminences(
        1f, 0xFF281E33, 0xFF733335, 0xFFBC503B, 0xFFFBD18A,
        PatternParameter("loopPitch", 3.6f, 2f..5.8f, title = R.string.texture_param_loop_density),
        PatternParameter("plumeHeight", 0.79f, 0.35f..1.3f, title = R.string.texture_param_arc_height),
        PatternParameter("strandWidth", 0.081f, 0.035f..0.16f, title = R.string.texture_param_strand_thickness),
        patternSeed,
        hasRotation = true
    ),
    StellarBowShock(
        1f, 0xFF172B42, 0xFF675572, 0xFFB86F77, 0xFFEAC195,
        PatternParameter("frontPitch", 0.18f, 0.1f..0.31f, title = R.string.texture_param_front_spacing),
        PatternParameter("shockBend", 1.1f, 0.2f..1.8f, title = R.string.texture_param_front_curvature),
        PatternParameter("dustDensity", 0.67f, 0.12f..1f, title = R.string.texture_param_dust_density),
        patternSeed,
        hasRotation = true
    ),
    HourglassNebula(
        1f, 0xFF172837, 0xFF48777B, 0xFFB88273, 0xFFE8B997,
        PatternParameter("cavitySize", 1f, 0.55f..1.85f, title = R.string.texture_param_cavity_size),
        PatternParameter("shellWall", 0.09f, 0.035f..0.2f, title = R.string.texture_param_shell_thickness),
        PatternParameter("gasFilaments", 0.7f, 0.1f..1f, title = R.string.texture_param_filaments),
        patternSeed,
        hasRotation = true
    ),
    RocheLobes(
        1f, 0xFF203347, 0xFF536477, 0xFFBE91A9, 0xFFEDC687,
        PatternParameter("fociGap", 0.34f, 0.17f..0.52f, title = R.string.texture_param_pair_separation),
        PatternParameter("massRatio", 0.85f, 0.42f..1.38f, title = R.string.texture_param_mass_ratio),
        PatternParameter("contourPitch", 0.092f, 0.05f..0.16f, title = R.string.texture_param_contour_spacing),
        patternSeed,
        hasRotation = true
    ),
    BasaltColonnade(
        1f, 0xFF303E46, 0xFF5E6B6F, 0xFF92978C, 0xFFC4BBAA,
        PatternParameter("columnSize", 1f, 0.55f..1.65f, title = R.string.texture_param_column_size),
        PatternParameter("heightSpread", 0.66f, 0.06f..1f, title = R.string.texture_param_height_variation),
        PatternParameter("columnGap", 0.035f, 0.012f..0.1f, title = R.string.texture_param_crack_width),
        patternSeed
    ),
    FaultedStrata(
        1f, 0xFF6E514A, 0xFFAD8070, 0xFFC2A58D, 0xFFD8BE99,
        PatternParameter("faultSlip", 0.14f, 0f..0.28f, title = R.string.texture_param_block_displacement),
        PatternParameter("bedCount", 11f, 5f..25f, integer = true, title = R.string.texture_param_bed_count),
        PatternParameter("faultPitch", 1f, 0.5f..2f, title = R.string.texture_param_fault_spacing),
        patternSeed,
        hasRotation = true
    ),
    GarnetSchist(
        1f, 0xFF283842, 0xFF82918B, 0xFF993D48, 0xFFE4CCAE,
        PatternParameter("radius", 1f, 0.5f..1.35f, title = R.string.texture_param_garnet_size),
        PatternParameter("seed", 56f, 0f..10000f, integer = true)
    ),
    OoliticLimestone(
        1f, 0xFF584E45, 0xFFBA9F79, 0xFFE8D5A8, 0xFFA26950,
        PatternParameter("count", 4f, 2f..7f, integer = true, title = R.string.texture_param_shell_count),
        PatternParameter("seed", 43f, 0f..10000f, integer = true)
    ),
    TrilobiteImpression(
        1f, 0xFF445258, 0xFF707E7B, 0xFF959E92, 0xFFC3BFA8,
        PatternParameter("fossilCount", 3.4f, 2f..5.5f, title = R.string.texture_param_fossil_density),
        PatternParameter("preservation", 0.68f, 0.08f..1f, title = R.string.texture_param_preservation),
        PatternParameter("fossilRelief", 0.66f, 0.1f..1f, title = R.string.texture_param_imprint_relief),
        patternSeed,
        hasRotation = true
    ),
    DoubleSlit(
        1f, 0xFF273951, 0xFF516D82, 0xFF8DB7B7, 0xFFD9E0BB,
        PatternParameter("waveLength", 0.114f, 0.067f..0.22f, title = R.string.wavelength),
        PatternParameter("sourceGap", 0.35f, 0.12f..0.63f, title = R.string.texture_param_source_separation),
        PatternParameter("attenuation", 0.58f, 0.09f..1f, title = R.string.gmic_param_attenuation),
        hasRotation = true
    ),
    LensCrossSection(
        1f, 0xFF273257, 0xFF4AA2AC, 0xFFC26E91, 0xFFE8C77D,
        PatternParameter("fringeRate", 9f, 4f..16f, title = R.string.texture_param_fringe_density),
        PatternParameter("gapSlope", 0.55f, 0.03f..1f, title = R.string.texture_param_gap_variation),
        PatternParameter("spectrumWidth", 0.62f, 0.15f..1f, title = R.string.texture_param_spectrum_width),
        patternSeed,
        hasRotation = true
    ),
    IrisDiaphragm(
        1f, 0xFF293C44, 0xFF506973, 0xFF879E99, 0xFFC0C6B9,
        PatternParameter("bladeCount", 8f, 5f..12f, integer = true, title = R.string.texture_param_blade_count),
        PatternParameter("aperture", 0.1f, 0.04f..0.23f, title = R.string.texture_param_aperture),
        PatternParameter("bladeDepth", 0.64f, 0.05f..1f, title = R.string.texture_param_blade_overlap),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    EmissionSpectrum(
        1f, 0xFF16242F, 0xFF638FA8, 0xFFABC7A3, 0xFFDFA683,
        PatternParameter("lineCount", 30f, 12f..60f, integer = true, title = R.string.texture_line_density),
        PatternParameter("lineWidth", 0.065f, 0.022f..0.16f, title = R.string.line_width),
        PatternParameter("doublets", 0.36f, 0f..1f, title = R.string.texture_param_doublet_fraction),
        patternSeed,
        hasRotation = true
    ),
    SchlierenPlume(
        1f, 0xFF45596E, 0xFF7D8997, 0xFFBAC0C3, 0xFFEFDBCB,
        PatternParameter("flowWidth", 1f, 0.54f..1.75f, title = R.string.texture_param_plume_width),
        PatternParameter("flowCurl", 0.61f, 0.09f..1f, title = R.string.texture_param_curl_strength),
        PatternParameter("edgeContrast", 0.85f, 0.19f..1f, title = R.string.texture_param_boundary_contrast),
        patternSeed,
        hasRotation = true
    ),
    HandRadiograph(
        1f, 0xFF261F23, 0xFF855E46, 0xFFBD946F, 0xFFF5D7B1,
        PatternParameter("strutWidth", 0.12f, 0.05f..0.25f, title = R.string.texture_param_strut_thickness),
        PatternParameter("poreAspect", 1f, 0.7f..2f, title = R.string.texture_param_pore_elongation),
        PatternParameter("poreSize", 1f, 0.55f..1.8f, title = R.string.texture_param_pore_size),
        patternSeed
    ),
    LogisticBifurcation(
        1f, 0xFF16293E, 0xFF426377, 0xFF9DAF89, 0xFFE7C27E,
        PatternParameter("mapIterations", 16f, 8f..28f, integer = true, title = R.string.texture_param_iteration_depth),
        PatternParameter("sampleDensity", 9f, 4f..14f, integer = true, title = R.string.texture_param_sample_density),
        PatternParameter("branchContrast", 0.72f, 0.16f..1f, title = R.string.texture_param_branch_contrast),
        patternSeed,
        hasRotation = true
    ),
    SunstoneFeldspar(
        1f, 0xFF744837, 0xFFB87753, 0xFFD69A71, 0xFFF2CA9B,
        PatternParameter("plateletDensity", 0.48f, 0.08f..0.93f, title = R.string.texture_param_platelet_density),
        PatternParameter("plateletAlignment", 0.75f, 0.08f..1f, title = R.string.texture_param_platelet_alignment),
        patternSeed
    ),
    RingPunchedSuede(
        1f, 0xFF49312A, 0xFF92664B, 0xFFBE9777, 0xFFD7B593,
        PatternParameter("broguePitch", 0.21f, 0.13f..0.36f, title = R.string.texture_param_brogue_pitch),
        PatternParameter("holeDiameter", 0.02f, 0.009f..0.033f, title = R.string.texture_param_hole_diameter),
        PatternParameter("nap", 0.55f, 0.07f..1f, title = R.string.texture_param_nap),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    SpindleMoulding(
        1f, 0xFF674A3F, 0xFF9F7252, 0xFFBF966A, 0xFFE3C49A,
        PatternParameter("beadPitch", 0.18f, 0.1f..0.31f, title = R.string.texture_param_bead_pitch),
        PatternParameter("beadSwell", 0.69f, 0.15f..1f, title = R.string.texture_param_bead_swell),
        PatternParameter("collarWidth", 0.13f, 0.06f..0.22f, title = R.string.texture_param_collar_width),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    LenoGauze(
        1f, 0xFF2A4447, 0xFF6A807B, 0xFFB9BDA1, 0xFFE3DABD,
        PatternParameter("weftPitch", 0.083f, 0.05f..0.14f, title = R.string.texture_param_weft_pitch),
        PatternParameter("warpTwist", 0.64f, 0.15f..0.94f, title = R.string.texture_param_warp_twist),
        PatternParameter("yarnWidth", 0.0045f, 0.002f..0.01f, title = R.string.texture_param_yarn_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    SoumakBraid(
        1f, 0xFF592F31, 0xFFA14F46, 0xFFC98B60, 0xFFDEBC88,
        PatternParameter("wrapPitch", 0.078f, 0.045f..0.14f, title = R.string.texture_param_wrap_pitch),
        PatternParameter("wrapLean", 0.63f, 0.25f..0.92f, title = R.string.texture_param_wrap_lean),
        PatternParameter("woolHair", 0.51f, 0.03f..0.93f, title = R.string.texture_param_wool_hair),
        hasRotation = true
    ),
    CouchingLoops(
        1f, 0xFF294B4D, 0xFF637168, 0xFFB28C50, 0xFFE6C88B,
        PatternParameter("loopPitch", 0.24f, 0.14f..0.34f, title = R.string.texture_param_loop_pitch),
        PatternParameter("cordGauge", 0.009f, 0.004f..0.018f, title = R.string.texture_param_cord_gauge),
        PatternParameter("tieSpacing", 0.031f, 0.014f..0.062f, title = R.string.texture_param_tie_spacing),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    CrazyQuiltSeams(
        1f, 0xFF3C5960, 0xFFA25759, 0xFFD0996E, 0xFFE2C399,
        PatternParameter("patchSize", 0.22f, 0.13f..0.34f, title = R.string.texture_param_patch_size),
        PatternParameter("seamFeather", 0.25f, 0.08f..0.43f, title = R.string.texture_param_seam_feather),
        PatternParameter("stitchDensity", 26f, 13f..42f, integer = true, title = R.string.texture_stitch_density),
        patternSeed,
        hasRotation = true
    ),
    PassementerieFrogs(
        1f, 0xFF4B363F, 0xFF795D64, 0xFFAC926B, 0xFFE3C492,
        PatternParameter("knotPitch", 0.32f, 0.19f..0.48f, title = R.string.texture_param_knot_pitch),
        PatternParameter("loopSpan", 0.33f, 0.18f..0.42f, title = R.string.texture_param_loop_span),
        PatternParameter("braidGauge", 0.01f, 0.004f..0.021f, title = R.string.texture_param_braid_gauge),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    RyaPile(
        1f, 0xFF3E504D, 0xFF758779, 0xFFB48870, 0xFFD7C2A0,
        PatternParameter("pileLength", 0.13f, 0.06f..0.23f, title = R.string.texture_param_pile_length),
        PatternParameter("tuftScatter", 0.6f, 0.05f..0.96f, title = R.string.texture_param_tuft_scatter),
        PatternParameter("fibreCurl", 0.44f, 0.02f..0.9f, title = R.string.texture_param_fibre_curl),
        patternSeed,
        hasRotation = true
    ),
    OttomanCord(
        1f, 0xFF433D51, 0xFF71566E, 0xFFAD8592, 0xFFD2B4AB,
        PatternParameter("cordGauge", 0.054f, 0.027f..0.095f, title = R.string.texture_param_cord_gauge),
        PatternParameter("ribLoft", 0.71f, 0.12f..1f, title = R.string.texture_param_rib_loft),
        PatternParameter("bindingCount", 10f, 4f..19f, integer = true, title = R.string.texture_param_binding_count),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    BattenbergLace(
        1f, 0xFF454B4C, 0xFF7C857B, 0xFFC2C3AC, 0xFFEEE6CC,
        PatternParameter("medallionPitch", 0.32f, 0.22f..0.48f, title = R.string.texture_param_medallion_pitch),
        PatternParameter("tapeWidth", 0.023f, 0.01f..0.038f, title = R.string.texture_param_tape_width),
        PatternParameter("brideCount", 7f, 4f..13f, integer = true, title = R.string.texture_param_bride_count),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TatamiInlay(
        1f, 0xFF454D48, 0xFF838767, 0xFFB0AB83, 0xFFD0C59F,
        PatternParameter("matWidth", 0.27f, 0.18f..0.44f, title = R.string.texture_param_mat_width),
        PatternParameter("rushPitch", 0.01f, 0.005f..0.023f, title = R.string.texture_param_rush_pitch),
        PatternParameter("borderWidth", 0.06f, 0.023f..0.11f, title = R.string.texture_param_border_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    DarningSampler(
        1f, 0xFF495961, 0xFF8F7E69, 0xFFBD8E76, 0xFFE7BC8D,
        PatternParameter("patchWidth", 0.59f, 0.3f..0.87f, title = R.string.texture_param_patch_width),
        PatternParameter("yarnDensity", 15f, 8f..24f, integer = true, title = R.string.texture_param_yarn_density),
        PatternParameter("frayedEnds", 0.5f, 0.05f..0.92f, title = R.string.texture_param_frayed_ends),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    ChenilleRows(
        1f, 0xFF445151, 0xFF6A8279, 0xFFBA9983, 0xFFE2C5A7,
        PatternParameter("walePitch", 0.102f, 0.06f..0.16f, title = R.string.texture_param_wale_pitch),
        PatternParameter("pileWidth", 0.62f, 0.28f..0.89f, title = R.string.texture_param_pile_width),
        PatternParameter("pileFray", 0.67f, 0.05f..0.97f, title = R.string.texture_param_pile_fray),
        patternSeed,
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    SpanishBlackwork(
        1f, 0xFF303638, 0xFF75756C, 0xFFC7B898, 0xFFE3D6B9,
        PatternParameter("patternPitch", 0.26f, 0.16f..0.4f, title = R.string.texture_param_pattern_pitch),
        PatternParameter("stitchGauge", 0.003f, 0.0013f..0.007f, title = R.string.texture_param_stitch_gauge),
        PatternParameter("fillDensity", 0.48f, 0.05f..0.94f, title = R.string.texture_param_fill_density),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    ShingleFishscale(
        1f, 0xFF514F4B, 0xFF817469, 0xFFA99B83, 0xFFC8B49B,
        PatternParameter("shingleWidth", 0.16f, 0.1f..0.25f, title = R.string.texture_param_shingle_width),
        PatternParameter("scallopDepth", 0.42f, 0.18f..0.62f, title = R.string.texture_param_scallop_depth),
        PatternParameter("grainWeathering", 0.53f, 0.04f..0.96f, title = R.string.texture_param_grain_weathering),
        patternSeed,
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TwistedPaperCord(
        1f, 0xFF65544A, 0xFF9F8469, 0xFFC8B397, 0xFFE4D4B5,
        PatternParameter("cordWidth", 0.095f, 0.053f..0.16f, title = R.string.texture_param_cord_width),
        PatternParameter("twistPitch", 0.083f, 0.037f..0.15f, title = R.string.texture_param_twist_pitch),
        PatternParameter("creaseDepth", 0.63f, 0.05f..0.96f, title = R.string.texture_param_crease_depth),
        placement = PatternPlacement.Phase,
        hasRotation = true
    ),
    DrypointBurr(
        1f, 0xFF2C4147, 0xFF707C74, 0xFFBBAE8D, 0xFFE8D8B7,
        PatternParameter("strokePitch", 0.043f, 0.024f..0.085f, title = R.string.texture_param_stroke_pitch),
        PatternParameter("burrWidth", 0.18f, 0.04f..0.33f, title = R.string.texture_param_burr_width),
        PatternParameter("plateTone", 0.38f, 0.02f..0.91f, title = R.string.texture_param_plate_tone),
        patternSeed,
        hasRotation = true
    ),
    MezzotintRocker(
        1f, 0xFF253338, 0xFF52605C, 0xFFA3A38F, 0xFFDED4B4,
        PatternParameter("rockerTeeth", 65f, 32f..96f, integer = true, title = R.string.texture_param_rocker_teeth),
        PatternParameter("scrapedLight", 0.52f, 0.12f..0.86f, title = R.string.texture_param_scraped_light),
        PatternParameter("passAngle", 49f, 21f..78f, title = R.string.texture_param_pass_angle),
        patternSeed,
        hasRotation = true
    ),
    BenDayDots(
        1f, 0xFFE8DCC2, 0xFFC7665E, 0xFF548D91, 0xFF47434E,
        PatternParameter("screenPitch", 0.042f, 0.022f..0.079f, title = R.string.texture_param_screen_pitch),
        PatternParameter("inkCoverage", 0.64f, 0.2f..0.9f, title = R.string.texture_param_ink_coverage),
        PatternParameter("screenAngle", 36f, 14f..62f, title = R.string.texture_param_screen_angle),
        hasRotation = true
    ),
    SplitFountainInk(
        1f, 0xFF303F59, 0xFF5B8E96, 0xFFB96A68, 0xFFE1C9A4,
        PatternParameter("blendWidth", 0.32f, 0.06f..0.63f, title = R.string.texture_param_blend_width),
        PatternParameter("rollerPitch", 0.34f, 0.19f..0.55f, title = R.string.texture_param_roller_pitch),
        PatternParameter("inkSkip", 0.32f, 0.02f..0.9f, title = R.string.texture_param_ink_skip),
        patternSeed,
        hasRotation = true
    ),
    StippleAquatint(
        1f, 0xFF36484A, 0xFF737C68, 0xFFB3AB84, 0xFFDED0AC,
        PatternParameter("rosinGrain", 0.03f, 0.015f..0.054f, title = R.string.texture_param_rosin_grain),
        PatternParameter("biteDepth", 0.56f, 0.12f..0.88f, title = R.string.texture_param_bite_depth),
        PatternParameter("stopoutArea", 0.34f, 0.04f..0.74f, title = R.string.texture_param_stopout_area),
        patternSeed,
        hasRotation = true
    ),
    ChineColle(
        1f, 0xFF655C59, 0xFF9E736D, 0xFFB6B6A2, 0xFFE6D9BB,
        PatternParameter("paperFragmentSize", 0.28f, 0.15f..0.44f, title = R.string.texture_param_paper_fragment_size),
        PatternParameter("deckleDepth", 0.52f, 0.06f..0.95f, title = R.string.texture_param_deckle_depth),
        PatternParameter("inkVeil", 0.46f, 0.05f..0.92f, title = R.string.texture_param_ink_veil),
        patternSeed,
        hasRotation = true
    ),
    DeckledPulp(
        1f, 0xFF817D63, 0xFFB6AA83, 0xFFCFC9A7, 0xFFE9DEC0,
        PatternParameter("fibreLength", 0.061f, 0.02f..0.11f, title = R.string.texture_param_fibre_length),
        PatternParameter("pulpClumps", 0.59f, 0.06f..0.95f, title = R.string.texture_param_pulp_clumps),
        PatternParameter("colouredFibres", 0.36f, 0.03f..0.88f, title = R.string.texture_param_coloured_fibres),
        patternSeed,
        hasRotation = true
    ),
    LeatherBasketStamp(
        1f, 0xFF51382D, 0xFF91603F, 0xFFBF8455, 0xFFD9A875,
        PatternParameter("stampPitch", 0.118f, 0.075f..0.19f, title = R.string.texture_param_stamp_pitch),
        PatternParameter("toolDepth", 0.69f, 0.08f..0.97f, title = R.string.texture_param_tool_depth),
        PatternParameter("ribCount", 4f, 2f..8f, integer = true, title = R.string.texture_param_rib_count),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    AdzeHewnOak(
        1f, 0xFF584A3A, 0xFF93734E, 0xFFBC9464, 0xFFD4B484,
        PatternParameter("adzeWidth", 0.16f, 0.085f..0.25f, title = R.string.texture_param_adze_width),
        PatternParameter("cutDepth", 0.64f, 0.1f..0.97f, title = R.string.texture_param_cut_depth),
        PatternParameter("tornGrain", 0.54f, 0.05f..0.91f, title = R.string.texture_param_torn_grain),
        patternSeed,
        hasRotation = true
    ),
    CloisonneCells(
        1f, 0xFF254F57, 0xFF428D92, 0xFFB75F57, 0xFFD8B373,
        PatternParameter("compartmentSize", 0.18f, 0.1f..0.3f, title = R.string.texture_param_compartment_size),
        PatternParameter("wireGauge", 0.0047f, 0.002f..0.012f, title = R.string.texture_param_wire_gauge),
        PatternParameter("enamelCurvature", 0.66f, 0.06f..1f, title = R.string.texture_param_enamel_curvature),
        patternSeed,
        hasRotation = true
    ),
    LaidPaperWatermark(
        1f, 0xFF7B826F, 0xFFA7A894, 0xFFC9C6A6, 0xFFEBE3C3,
        PatternParameter("laidPitch", 0.012f, 0.006f..0.025f, title = R.string.texture_param_laid_pitch),
        PatternParameter("chainInterval", 0.18f, 0.1f..0.31f, title = R.string.texture_param_chain_interval),
        PatternParameter("markOpacity", 0.63f, 0.06f..0.95f, title = R.string.texture_param_mark_opacity),
        hasRotation = true
    ),
    ShellacCheckering(
        1f, 0xFF472E29, 0xFF83503B, 0xFFAE7951, 0xFFD5A976,
        PatternParameter("checkSpacing", 0.097f, 0.05f..0.18f, title = R.string.texture_param_check_spacing),
        PatternParameter("glazeTint", 0.64f, 0.1f..0.96f, title = R.string.texture_param_glaze_tint),
        PatternParameter("openCracks", 0.55f, 0.05f..0.96f, title = R.string.texture_param_open_cracks),
        patternSeed,
        hasRotation = true
    ),
    CombedSlip(
        1f, 0xFF694631, 0xFFAA6C4C, 0xFFD3AC7F, 0xFFE8D4A7,
        PatternParameter("combPitch", 0.062f, 0.036f..0.11f, title = R.string.texture_param_comb_pitch),
        PatternParameter("drawDepth", 0.7f, 0.12f..0.96f, title = R.string.texture_param_draw_depth),
        PatternParameter("slipRatio", 0.46f, 0.19f..0.77f, title = R.string.texture_param_slip_ratio),
        hasRotation = true
    ),
    ParquetDeVersailles(
        1f, 0xFF614C3B, 0xFF997A56, 0xFFBFA079, 0xFFD8BD91,
        PatternParameter("panelPitch", 0.4f, 0.24f..0.67f, title = R.string.texture_param_panel_pitch),
        PatternParameter("frameWidth", 0.092f, 0.045f..0.15f, title = R.string.texture_param_frame_width),
        PatternParameter("slatWidth", 0.107f, 0.064f..0.16f, title = R.string.texture_param_slat_width),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    ScallopValves(
        1f, 0xFF3D5960, 0xFF9F6C60, 0xFFDBA68A, 0xFFF1D4A8,
        PatternParameter("ribs", 17f, 8f..29f, integer = true, title = R.string.texture_param_fan_ribs),
        PatternParameter("valveFan", 0.95f, 0.65f..1.23f, title = R.string.texture_param_fan_opening),
        PatternParameter("lipHeight", 0.65f, 0.12f..1f, title = R.string.texture_param_lip_relief),
        patternSeed,
        hasRotation = true
    ),
    UrchinTest(
        1f, 0xFF335553, 0xFF83A797, 0xFFC9C8A1, 0xFFEBDCC0,
        PatternParameter("platePitch", 0.144f, 0.088f..0.23f, title = R.string.texture_param_plate_spacing),
        PatternParameter("tubercles", 0.16f, 0.085f..0.24f, title = R.string.texture_param_tubercle_size),
        PatternParameter("porePairs", 0.2f, 0.12f..0.3f, title = R.string.texture_param_pore_spacing),
        patternSeed
    ),
    NummuliteBank(
        1f, 0xFF645444, 0xFF9E8564, 0xFFC2A982, 0xFFE3D0A5,
        PatternParameter("whorls", 4.4f, 2.2f..7f, title = R.string.texture_param_whorls),
        PatternParameter("septalCount", 18f, 8f..31f, integer = true, title = R.string.texture_param_chamber_divisions),
        PatternParameter("fossilSize", 0.45f, 0.26f..0.53f, title = R.string.texture_param_test_size),
        patternSeed
    ),
    CrinoidColumnals(
        1f, 0xFF433F3A, 0xFF8B8070, 0xFFBEAF94, 0xFFDFCFB5,
        PatternParameter("facetCount", 22f, 12f..38f, integer = true, title = R.string.texture_param_articular_ridges),
        PatternParameter("lumenSize", 0.095f, 0.045f..0.16f, title = R.string.texture_param_central_opening),
        PatternParameter("jointSize", 0.37f, 0.24f..0.48f, title = R.string.texture_param_ossicle_size),
        patternSeed
    ),
    GraptoliteShale(
        1f, 0xFF30373A, 0xFF575E60, 0xFF938F85, 0xFFD3BB87,
        PatternParameter("stipeLength", 0.75f, 0.4f..0.94f, title = R.string.texture_param_stipe_length),
        PatternParameter("thecae", 11f, 5f..19f, integer = true, title = R.string.texture_param_cup_count),
        PatternParameter("fossilMetal", 0.65f, 0.04f..1f, title = R.string.texture_param_pyrite_sheen),
        patternSeed,
        hasRotation = true
    ),
    StromatoliteDomes(
        1f, 0xFF444E49, 0xFF726D59, 0xFFB29B71, 0xFFE1C99D,
        PatternParameter("domeHeight", 0.42f, 0.13f..0.76f, title = R.string.texture_param_dome_height),
        PatternParameter("laminae", 0.06f, 0.03f..0.11f, title = R.string.texture_param_lamina_spacing),
        PatternParameter("matWobble", 0.48f, 0.05f..0.93f, title = R.string.texture_param_mat_irregularity),
        patternSeed,
        hasRotation = true
    ),
    SeptarianNodules(
        1f, 0xFF454941, 0xFF807961, 0xFFB9A46B, 0xFFE8CD88,
        PatternParameter("noduleSize", 0.47f, 0.3f..0.61f, title = R.string.texture_param_nodule_size),
        PatternParameter("veinGauge", 0.07f, 0.023f..0.15f, title = R.string.texture_param_calcite_vein_width),
        PatternParameter("oxideRim", 0.45f, 0.05f..1f, title = R.string.texture_param_oxidized_margin),
        patternSeed
    ),
    TourmalinePrisms(
        1f, 0xFF394B49, 0xFF527B69, 0xFFA97074, 0xFFD0AA92,
        PatternParameter("prismRadius", 0.36f, 0.23f..0.5f, title = R.string.texture_param_prism_radius),
        PatternParameter("coreFraction", 0.48f, 0.2f..0.7f, title = R.string.texture_param_core_fraction),
        PatternParameter("zoning", 0.72f, 0.12f..1f, title = R.string.texture_param_color_zoning),
        patternSeed,
        hasRotation = true
    ),
    SeleniteBlades(
        1f, 0xFF46525C, 0xFF8C9DA0, 0xFFC1C4B0, 0xFFF1E2BF,
        PatternParameter("bladeLength", 0.64f, 0.31f..0.91f, title = R.string.texture_param_crystal_length),
        PatternParameter("bladeWidth", 0.12f, 0.06f..0.22f, title = R.string.texture_param_crystal_width),
        PatternParameter("silkFlash", 0.67f, 0.1f..1f, title = R.string.texture_param_silky_reflection),
        patternSeed,
        hasRotation = true
    ),
    GraphicGranite(
        1f, 0xFF434C4D, 0xFF807069, 0xFFC49982, 0xFFE0BEA4,
        PatternParameter("glyphScale", 1f, 0.57f..1.75f, title = R.string.texture_param_quartz_scale),
        PatternParameter("intergrowth", 0.56f, 0.18f..0.9f, title = R.string.texture_param_intergrowth),
        PatternParameter("feldsparGrain", 0.55f, 0.05f..1f, title = R.string.texture_param_feldspar_grain),
        patternSeed
    ),
    PillowBasalt(
        1f, 0xFF273E41, 0xFF58655B, 0xFF897D65, 0xFFD0B899,
        PatternParameter("lobeAspect", 1.45f, 0.85f..2.2f, title = R.string.texture_param_pillow_length),
        PatternParameter("rindWidth", 0.095f, 0.035f..0.17f, title = R.string.texture_param_chilled_rind),
        PatternParameter("radialCracks", 7f, 3f..12f, integer = true, title = R.string.texture_param_radial_cracks),
        patternSeed
    ),
    ChevronFolds(
        1f, 0xFF4E5151, 0xFF8B776E, 0xFFB89E83, 0xFFDCCCAC,
        PatternParameter("foldAngle", 0.64f, 0.21f..1.1f, title = R.string.texture_param_limb_slope),
        PatternParameter("hingeRound", 0.052f, 0.008f..0.13f, title = R.string.texture_param_hinge_rounding),
        PatternParameter("bedWidth", 0.043f, 0.02f..0.079f, title = R.string.texture_param_bed_spacing),
        patternSeed,
        hasRotation = true
    ),
    OrbicularDiorite(
        1f, 0xFF34403F, 0xFF697168, 0xFFA5A58C, 0xFFD3CBB3,
        PatternParameter("mantleCount", 4f, 2f..8f, integer = true, title = R.string.texture_param_mantle_count),
        PatternParameter("orbRadius", 0.39f, 0.25f..0.52f, title = R.string.texture_param_orbicule_radius),
        PatternParameter("crystalFleck", 0.71f, 0.11f..1f, title = R.string.texture_param_crystal_flecks),
        patternSeed
    ),
    DesertRoses(
        1f, 0xFF67564E, 0xFFA0785F, 0xFFC8A185, 0xFFEDCFAA,
        PatternParameter("bladeFans", 9f, 5f..14f, integer = true, title = R.string.texture_param_blade_count),
        PatternParameter("crystalSpan", 0.44f, 0.28f..0.56f, title = R.string.texture_param_petal_length),
        PatternParameter("sandCoat", 0.43f, 0.02f..0.9f, title = R.string.texture_param_sand_coating),
        patternSeed
    ),
    RippleCrossbeds(
        1f, 0xFF604E42, 0xFF9B7F5F, 0xFFBDA17B, 0xFFE1CAA4,
        PatternParameter("setDepth", 0.2f, 0.1f..0.36f, title = R.string.texture_param_set_thickness),
        PatternParameter("dipAngle", 0.61f, 0.18f..0.98f, title = R.string.texture_param_lamina_dip),
        PatternParameter("laminaPitch", 0.022f, 0.011f..0.046f, title = R.string.texture_param_lamina_spacing),
        patternSeed,
        hasRotation = true
    ),
    MangroveKnees(
        1f, 0xFF293F3E, 0xFF53635A, 0xFF9C8063, 0xFFC3AF83,
        PatternParameter("rootHeight", 0.61f, 0.25f..0.85f, title = R.string.texture_param_root_height),
        PatternParameter("rippleReach", 0.37f, 0.16f..0.55f, title = R.string.texture_param_ripple_reach),
        PatternParameter("mudCover", 0.41f, 0.06f..0.92f, title = R.string.texture_param_exposed_mud),
        patternSeed,
        hasRotation = true
    ),
    CottonBolls(
        1f, 0xFF445348, 0xFF92765A, 0xFFD2C7AA, 0xFFF1E8CF,
        PatternParameter("lockCount", 4f, 3f..6f, integer = true, title = R.string.texture_param_fiber_locks),
        PatternParameter("fluffSize", 0.22f, 0.12f..0.29f, title = R.string.texture_param_tuft_diameter),
        PatternParameter("bractLength", 0.42f, 0.29f..0.54f, title = R.string.texture_param_bract_length),
        patternSeed
    ),
    BirchLenticels(
        1f, 0xFF4D4740, 0xFF9B8F7A, 0xFFD8CFB4, 0xFFECE3C9,
        PatternParameter("lenticelSpan", 0.32f, 0.12f..0.48f, title = R.string.texture_param_lenticel_length),
        PatternParameter("peelWidth", 0.048f, 0.015f..0.11f, title = R.string.texture_param_peeling_strip_width),
        PatternParameter("barkTone", 0.46f, 0.05f..0.93f, title = R.string.texture_param_bark_mottling),
        patternSeed,
        hasRotation = true
    ),
    PlaneBark(
        1f, 0xFF596557, 0xFF879377, 0xFFB3AC89, 0xFFD6C7A4,
        PatternParameter("patchSize", 1f, 0.55f..1.85f, title = R.string.texture_param_bark_scale),
        PatternParameter("peeling", 0.54f, 0.13f..0.88f, title = R.string.texture_param_peeling_extent),
        PatternParameter("lipWidth", 0.029f, 0.007f..0.078f, title = R.string.texture_param_peeling_lip),
        patternSeed
    ),
    CattailFloss(
        1f, 0xFF594C40, 0xFF90745B, 0xFFC9AC88, 0xFFEBD9B7,
        PatternParameter("tuftDensity", 3.8f, 2.4f..6.5f, title = R.string.texture_param_tuft_density),
        PatternParameter("fiberLength", 0.66f, 0.37f..0.9f, title = R.string.texture_param_fiber_length),
        PatternParameter("fanSpread", 0.83f, 0.37f..1.23f, title = R.string.texture_param_tuft_spread),
        patternSeed,
        hasRotation = true
    ),
    FernSori(
        1f, 0xFF28443B, 0xFF637C57, 0xFFA0884F, 0xFFD7AD66,
        PatternParameter("frondPitch", 0.17f, 0.1f..0.26f, title = R.string.texture_param_pinna_spacing),
        PatternParameter("sorusCount", 6f, 3f..10f, integer = true, title = R.string.texture_param_sorus_pairs),
        PatternParameter("sorusSize", 0.063f, 0.034f..0.098f, title = R.string.texture_param_sorus_diameter),
        patternSeed,
        hasRotation = true
    ),
    EquisetumCrosscuts(
        1f, 0xFF2A4B46, 0xFF73937A, 0xFFBCB895, 0xFFE8D9AD,
        PatternParameter("canalCount", 10f, 6f..16f, integer = true, title = R.string.texture_param_canal_count),
        PatternParameter("pithRadius", 0.13f, 0.075f..0.2f, title = R.string.texture_param_pith_opening),
        PatternParameter("stalkRadius", 0.39f, 0.29f..0.48f, title = R.string.texture_param_stem_radius),
        patternSeed,
        hasRotation = true
    ),
    LotusSeedheads(
        1f, 0xFF435247, 0xFF85764E, 0xFFB59A66, 0xFFE1BD82,
        PatternParameter("headSize", 0.43f, 0.31f..0.54f, title = R.string.texture_param_seedhead_diameter),
        PatternParameter("socketSize", 0.16f, 0.09f..0.23f, title = R.string.texture_param_socket_diameter),
        PatternParameter("seedFill", 0.62f, 0.12f..0.96f, title = R.string.texture_param_filled_sockets),
        patternSeed
    ),
    DuckweedRafts(
        1f, 0xFF204E4E, 0xFF528166, 0xFF8DB276, 0xFFCCCA83,
        PatternParameter("frondSize", 0.21f, 0.12f..0.29f, title = R.string.texture_param_frond_size),
        PatternParameter("budding", 0.73f, 0.05f..1f, title = R.string.texture_param_daughter_fronds),
        PatternParameter("raftGap", 0.32f, 0.01f..0.8f, title = R.string.texture_param_raft_openings),
        patternSeed
    ),
    MapleBurl(
        1f, 0xFF785031, 0xFFAB7752, 0xFFD7AE79, 0xFFEBD0A0,
        PatternParameter("eyeSize", 0.17f, 0.09f..0.28f, title = R.string.texture_param_eye_size),
        PatternParameter("grainPitch", 0.045f, 0.024f..0.074f, title = R.string.texture_param_grain_spacing),
        PatternParameter("grainCurl", 0.72f, 0.08f..1f, title = R.string.texture_param_grain_deflection),
        patternSeed,
        hasRotation = true
    ),
    MusselByssus(
        1f, 0xFF254952, 0xFF526B5D, 0xFFB28E61, 0xFFDFBC86,
        PatternParameter("anchorSpread", 0.79f, 0.31f..1f, title = R.string.texture_param_attachment_spread),
        PatternParameter("threadCount", 8f, 4f..13f, integer = true, title = R.string.texture_param_thread_count),
        PatternParameter("plaqueSize", 0.034f, 0.015f..0.066f, title = R.string.texture_param_plaque_size),
        patternSeed,
        hasRotation = true
    ),
    SandDollarBed(
        1f, 0xFF55666A, 0xFF9F9C86, 0xFFD2C9A8, 0xFFEEE1BD,
        PatternParameter("testRadius", 0.45f, 0.29f..0.54f, title = R.string.texture_param_test_radius),
        PatternParameter("petalReach", 0.3f, 0.18f..0.39f, title = R.string.texture_param_petal_reach),
        PatternParameter("poreRows", 9f, 5f..15f, integer = true, title = R.string.texture_pore_density),
        patternSeed
    ),
    ScuteGrowthRings(
        1f, 0xFF3E4735, 0xFF6F6D49, 0xFFA7945E, 0xFFD3B97E,
        PatternParameter("growthBands", 5f, 3f..9f, integer = true, title = R.string.texture_param_growth_bands),
        PatternParameter("scuteDome", 0.69f, 0.08f..1f, title = R.string.texture_param_scute_convexity),
        PatternParameter("sutureWidth", 0.042f, 0.014f..0.105f, title = R.string.texture_param_suture_width),
        patternSeed
    ),
    CuttlefishMantle(
        1f, 0xFF334B50, 0xFF796259, 0xFFB19478, 0xFFDCC39D,
        PatternParameter("barPitch", 0.12f, 0.075f..0.2f, title = R.string.texture_param_mantle_bars),
        PatternParameter("ruffle", 0.65f, 0.07f..1f, title = R.string.texture_param_band_ruffling),
        PatternParameter("speckles", 0.6f, 0.05f..1f, title = R.string.texture_param_pigment_speckles),
        patternSeed
    ),
    LimpetRadula(
        1f, 0xFF3B4E55, 0xFF748B87, 0xFFAB8066, 0xFFDCC49A,
        PatternParameter("toothSpacing", 0.14f, 0.08f..0.23f, title = R.string.texture_param_tooth_spacing),
        PatternParameter("hookReach", 0.51f, 0.1f..0.88f, title = R.string.texture_param_hook_curvature),
        PatternParameter("tipMineral", 0.68f, 0.1f..1f, title = R.string.texture_param_mineral_tips),
        patternSeed,
        hasRotation = true
    ),
    CornerCubeReflector(
        8f, 0xFF40232E, 0xFFA84B42, 0xFFF8A36B, 0xFFFFE6AE,
        PatternParameter("facetRelief", 0.78f, 0.15f..1f, title = R.string.texture_param_facet_relief),
        PatternParameter("reflectorSeam", 0.036f, 0.009f..0.105f, title = R.string.texture_param_cell_border),
        PatternParameter("lightAzimuth", 32f, 0f..360f, title = R.string.texture_param_light_angle),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    MicroficheFrames(
        3.4f, 0xFF172D37, 0xFF2C5663, 0xFF81A7A4, 0xFFE4D4A8,
        PatternParameter("filmMargin", 0.052f, 0.02f..0.13f, title = R.string.texture_param_frame_margin),
        PatternParameter("lineFill", 0.74f, 0.25f..1f, title = R.string.texture_param_line_density),
        PatternParameter("microFog", 0.21f, 0f..0.75f, title = R.string.texture_param_film_fog),
        patternSeed,
        hasRotation = true
    ),
    AcousticSlotPanel(
        6.6f, 0xFF333331, 0xFF79624F, 0xFFB59A79, 0xFFDBC7A3,
        PatternParameter("slotWidth", 0.083f, 0.028f..0.145f, title = R.string.texture_param_slot_width),
        PatternParameter("slotLength", 0.65f, 0.31f..0.88f, title = R.string.texture_param_slot_length),
        PatternParameter("panelGrain", 0.52f, 0f..1f, title = R.string.texture_param_panel_grain),
        hasRotation = true
    ),
    BearingRaceway(
        4.2f, 0xFF202B34, 0xFF557181, 0xFFB48369, 0xFFF1E8CE,
        PatternParameter("ballRadius", 0.245f, 0.13f..0.29f, title = R.string.texture_param_ball_radius),
        PatternParameter("cageWidth", 0.082f, 0.025f..0.14f, title = R.string.texture_param_cage_width),
        PatternParameter("steelPolish", 0.75f, 0.1f..1f, title = R.string.texture_param_steel_polish),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PinFinHeatsink(
        7f, 0xFF252F37, 0xFF435F6B, 0xFF94A7A7, 0xFFE0C7A1,
        PatternParameter("pinRadius", 0.33f, 0.2f..0.42f, title = R.string.texture_param_fin_width),
        PatternParameter("pinHeight", 0.25f, 0.05f..0.35f, title = R.string.texture_param_fin_height),
        PatternParameter("topBevel", 0.036f, 0.009f..0.09f, title = R.string.texture_param_top_bevel),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TreadPlate(
        5.7f, 0xFF243743, 0xFF60767E, 0xFFABB3A8, 0xFFE0D0B2,
        PatternParameter("lugLength", 0.75f, 0.45f..0.9f, title = R.string.texture_param_lug_length),
        PatternParameter("lugBreadth", 0.065f, 0.035f..0.091f, title = R.string.texture_param_lug_width),
        PatternParameter("edgePolish", 0.64f, 0.02f..1f, title = R.string.texture_param_edge_polish),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    SplitFlapMatrix(
        4.6f, 0xFF242B32, 0xFF53646C, 0xFFA9AB91, 0xFFF6D9A3,
        PatternParameter("flapGap", 0.027f, 0.009f..0.07f, title = R.string.texture_param_blade_gap),
        PatternParameter("segmentStroke", 0.039f, 0.018f..0.073f, title = R.string.texture_param_segment_stroke),
        PatternParameter("blankFraction", 0.16f, 0f..0.62f, title = R.string.texture_param_blank_cells),
        patternSeed,
        hasRotation = true
    ),
    WireRopeSection(
        2.45f, 0xFF26313B, 0xFF61717A, 0xFFA7A79A, 0xFFEAC9A2,
        PatternParameter("strandPacking", 0.88f, 0.65f..0.97f, title = R.string.texture_param_strand_packing),
        PatternParameter("wireCore", 0.114f, 0.055f..0.14f, title = R.string.texture_param_core_diameter),
        PatternParameter("strandTwist", 0.62f, 0f..1f, title = R.string.texture_param_strand_rotation),
        patternSeed,
        hasRotation = true
    ),
    KnurledInsert(
        9.5f, 0xFF303C40, 0xFF77553E, 0xFFC49862, 0xFFF1D9A3,
        PatternParameter("knurlSlant", 0.68f, 0.38f..0.95f, title = R.string.texture_param_tooth_angle),
        PatternParameter("knurlFlat", 0.32f, 0.09f..0.62f, title = R.string.texture_param_flat_crest),
        PatternParameter("knurlOxide", 0.56f, 0.1f..0.96f, title = R.string.texture_param_darkening),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    BallGridArray(
        6.1f, 0xFF183B3D, 0xFF36605A, 0xFFB69664, 0xFFE0E2CF,
        PatternParameter("solderDiameter", 0.45f, 0.25f..0.61f, title = R.string.texture_param_solder_diameter),
        PatternParameter("occupiedPads", 0.77f, 0.25f..1f, title = R.string.texture_param_pad_population),
        PatternParameter("padCollar", 0.075f, 0.02f..0.125f, title = R.string.texture_param_copper_collar),
        patternSeed,
        hasRotation = true
    ),
    PiezoComb(
        2.7f, 0xFF283747, 0xFF567384, 0xFFBE9B63, 0xFFFAE1A6,
        PatternParameter("fingerPairs", 7f, 3f..12f, integer = true, title = R.string.texture_param_electrode_pairs),
        PatternParameter("fingerWidth", 0.34f, 0.16f..0.64f, title = R.string.texture_param_finger_width),
        PatternParameter("fingerReach", 0.77f, 0.55f..0.9f, title = R.string.texture_param_finger_overlap),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PolarizerMosaic(
        3.4f, 0xFF242E46, 0xFF42687A, 0xFFB29998, 0xFFECD4AE,
        PatternParameter("analyzerAngle", 27f, 0f..180f, title = R.string.texture_param_analyzer_angle),
        PatternParameter("lamellaCount", 6f, 3f..12f, integer = true, title = R.string.texture_param_stripes_per_cell),
        PatternParameter("polarizerLeak", 0.24f, 0.04f..0.65f, title = R.string.texture_param_dark_transmission),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    PhosphorTriads(
        8.1f, 0xFF1B1B26, 0xFFD95059, 0xFF77BF9A, 0xFF75A1DE,
        PatternParameter("phosphorHeight", 0.67f, 0.36f..0.85f, title = R.string.texture_param_phosphor_height),
        PatternParameter("maskRatio", 0.22f, 0.07f..0.55f, title = R.string.texture_param_shadow_mask),
        PatternParameter("crtBloom", 0.57f, 0.03f..1f, title = R.string.texture_param_phosphor_bloom),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    AnamorphicStreaks(
        4.6f, 0xFF161F36, 0xFF526786, 0xFFBC80AE, 0xFFF6CF9C,
        PatternParameter("streakReach", 0.71f, 0.28f..1.25f, title = R.string.texture_param_streak_length),
        PatternParameter("ghostOffset", 0.34f, 0.09f..0.58f, title = R.string.texture_param_ghost_separation),
        PatternParameter("glintCount", 0.75f, 0.2f..0.96f, title = R.string.texture_param_light_density),
        patternSeed,
        hasRotation = true
    ),
    DichroicSplitter(
        3.3f, 0xFF172E43, 0xFF429C96, 0xFFCA758D, 0xFFE9CA87,
        PatternParameter("paneTilt", 48f, 24f..79f, title = R.string.texture_param_crossing_angle),
        PatternParameter("glassFraction", 0.64f, 0.34f..0.88f, title = R.string.texture_param_glass_coverage),
        PatternParameter("coatingMix", 0.75f, 0.18f..1f, title = R.string.texture_param_coating_strength),
        patternSeed
    ),
    TalbotCarpet(
        1.65f, 0xFF1B3045, 0xFF3F7283, 0xFFC18980, 0xFFF4DA9A,
        PatternParameter("slitFraction", 0.28f, 0.1f..0.55f, title = R.string.texture_param_slit_fraction),
        PatternParameter("modeCount", 7f, 3f..11f, integer = true, title = R.string.texture_param_diffraction_orders),
        PatternParameter("carpetContrast", 0.6f, 0.24f..0.96f, title = R.string.texture_param_fringe_contrast),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    ConoscopicCrosses(
        3.8f, 0xFF222638, 0xFF487382, 0xFFBB7281, 0xFFE1BF84,
        PatternParameter("isochromes", 5f, 2f..11f, integer = true, title = R.string.texture_param_interference_rings),
        PatternParameter("opticEccentricity", 0.49f, 0.08f..0.78f, title = R.string.texture_param_optic_axis_spread),
        PatternParameter("extinctionWidth", 0.2f, 0.06f..0.43f, title = R.string.texture_param_dark_cross_width),
        patternSeed,
        hasRotation = true
    ),
    SpeckleShear(
        3.8f, 0xFF193543, 0xFF5E8583, 0xFFCAB39A, 0xFFE6D7B1,
        PatternParameter("grainRate", 0.58f, 0.23f..1f, title = R.string.texture_param_speckle_size),
        PatternParameter("shearRate", 0.54f, 0.06f..1f, title = R.string.texture_param_wavefront_shear),
        PatternParameter("fringeGain", 0.72f, 0.22f..0.98f, title = R.string.texture_param_fringe_modulation),
        patternSeed
    ),
    PythagoreanTiling(
        4.7f, 0xFF333E40, 0xFF568782, 0xFFC8AA82, 0xFFE9D4A5,
        PatternParameter("insertRatio", 0.48f, 0.19f..0.78f, title = R.string.texture_param_inset_size),
        PatternParameter("stoneJoint", 0.023f, 0.006f..0.063f, title = R.string.texture_param_joint_width),
        PatternParameter("mineralGrain", 0.43f, 0.04f..0.88f, title = R.string.texture_param_stone_grain),
        patternSeed,
        hasRotation = true
    ),
    MiuraFolds(
        4.9f, 0xFF293B54, 0xFF537F91, 0xFFB8796D, 0xFFE2C69C,
        PatternParameter("foldSkew", 0.39f, 0.12f..0.72f, title = R.string.texture_param_fold_lean),
        PatternParameter("creaseWidth", 0.022f, 0.005f..0.067f, title = R.string.texture_param_crease_width),
        PatternParameter("foldLight", 0.7f, 0.17f..0.96f, title = R.string.texture_param_face_contrast),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    DovetailMesh(
        4.1f, 0xFF283A3E, 0xFF986D4A, 0xFFD2B787, 0xFFF3D9AC,
        PatternParameter("tailFlare", 0.12f, 0.035f..0.18f, title = R.string.texture_param_flare_angle),
        PatternParameter("tailDepth", 0.26f, 0.12f..0.37f, title = R.string.texture_param_tail_depth),
        PatternParameter("joinClearance", 0.019f, 0.005f..0.047f, title = R.string.texture_param_joint_clearance),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    ReuleauxTripods(
        5.8f, 0xFF282E46, 0xFF615272, 0xFFBC7F87, 0xFFF2D5A1,
        PatternParameter("reuleauxSpan", 0.7f, 0.44f..0.83f, title = R.string.texture_param_triangle_width),
        PatternParameter("reuleauxInset", 0.056f, 0.019f..0.11f, title = R.string.texture_param_inset_distance),
        PatternParameter("reuleauxSpin", 0.23f, -0.52f..0.52f, title = R.string.texture_param_turning_angle),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    HingedSquares(
        5.1f, 0xFF233B47, 0xFF42777A, 0xFFB87960, 0xFFF1C98F,
        PatternParameter("hingeOpening", 0.31f, 0.09f..0.65f, title = R.string.texture_param_hinge_opening),
        PatternParameter("hingeRadius", 0.045f, 0.012f..0.078f, title = R.string.texture_param_rivet_radius),
        PatternParameter("plateLip", 0.035f, 0.009f..0.07f, title = R.string.texture_param_plate_bevel),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    OctagonCabochons(
        4f, 0xFF343D4A, 0xFF8D6357, 0xFFE1CDB0, 0xFFB29869,
        PatternParameter("cabochonSpan", 0.29f, 0.14f..0.42f, title = R.string.texture_param_cabochon_size),
        PatternParameter("borderGauge", 0.032f, 0.012f..0.073f, title = R.string.texture_inlay_width),
        PatternParameter("marbleVeins", 0.54f, 0.03f..1f, title = R.string.texture_param_vein_strength),
        patternSeed,
        hasRotation = true
    ),
    PentominoMosaic(
        5.2f, 0xFF293F49, 0xFF498F88, 0xFFC78465, 0xFFE8C995,
        PatternParameter("jointWidth", 0.044f, 0.015f..0.095f, title = R.string.texture_grout_width),
        PatternParameter("bevelDepth", 0.65f, 0.12f..1f, title = R.string.texture_param_tile_bevel),
        PatternParameter("glazeMottle", 0.36f, 0.04f..0.9f, title = R.string.texture_param_glaze_mottling),
        patternSeed,
        hasRotation = true
    ),
    VacuumChuck(
        2.4f, 0xFF29343C, 0xFF667B80, 0xFFAAB2A7, 0xFFD9C3A0,
        PatternParameter("grooveCount", 5f, 2f..8f, integer = true, title = R.string.texture_param_vacuum_channels),
        PatternParameter("cornerFillet", 0.16f, 0.05f..0.31f, title = R.string.texture_param_corner_radius),
        PatternParameter("portRadius", 0.029f, 0.012f..0.058f, title = R.string.texture_param_port_radius),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    TorsionSprings(
        5.4f, 0xFF293D46, 0xFF617984, 0xFFB5A68A, 0xFFE7CF9A,
        PatternParameter("coilSpan", 0.67f, 0.36f..0.79f, title = R.string.texture_param_coil_diameter),
        PatternParameter("wireGauge", 0.053f, 0.024f..0.087f, title = R.string.texture_param_wire_gauge),
        PatternParameter("helixProjection", 0.44f, 0.16f..0.67f, title = R.string.texture_param_turn_overlap),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    ),
    CuneiformImpressions(
        5.1f, 0xFF51433D, 0xFFA66E4C, 0xFFCB9D73, 0xFFE7C29B,
        PatternParameter("markSize", 0.65f, 0.4f..0.83f, title = R.string.texture_param_impression_length),
        PatternParameter("stylusDepth", 0.84f, 0.19f..1f, title = R.string.texture_param_stylus_depth),
        PatternParameter("markDensity", 0.73f, 0.29f..0.95f, title = R.string.texture_param_mark_density),
        patternSeed,
        hasRotation = true
    ),
    AnechoicWedges(
        3.8f, 0xFF25333B, 0xFF485D65, 0xFF88A39B, 0xFFCABDA2,
        PatternParameter("wedgePitch", 4f, 2f..7f, integer = true, title = R.string.texture_param_wedges_per_panel),
        PatternParameter("wedgeDepth", 0.74f, 0.18f..0.98f, title = R.string.texture_param_wedge_depth),
        PatternParameter("foamPores", 0.52f, 0.05f..1f, title = R.string.texture_param_foam_pores),
        placement = PatternPlacement.PhaseXY,
        hasRotation = true
    );

    private constructor(
        frequency: Float,
        background: Long,
        primary: Long,
        secondary: Long,
        accent: Long,
        vararg parameters: PatternParameter,
        placement: PatternPlacement = PatternPlacement.None,
        hasRotation: Boolean = false,
        hasScale: Boolean = true
    ) : this(
        parameters.toList(), 4, frequency,
        listOf(background.toInt(), primary.toInt(), secondary.toInt(), accent.toInt()),
        placement, hasRotation, hasScale
    )

    val defaultColors: List<Int>
        get() = palette ?: when (this) {
            ChromaticPlaidPattern -> listOf(-256, -65536, -65281, -16711681)
            PlaidPattern -> listOf(
                0xFF102B33.toInt(), 0xFF8C3444.toInt(), 0xFF356B65.toInt(), 0xFFE2C58B.toInt()
            )

            GeneratedWaves -> listOf(
                0xFF173A56.toInt(), 0xFF3E8B95.toInt(), 0xFFF0C88C.toInt(), 0xFFE58D7A.toInt()
            )
            CrossStitchPattern -> listOf(-16777216, -2767701, -13338717, -1)
            ScintillatingGrid -> listOf(-16777216, -8355712, -1, -1)
            RandomTriangles -> listOf(
                0xFF16334B.toInt(), 0xFF408C9A.toInt(), 0xFFE8B66C.toInt(), 0xFFDA7770.toInt()
            )

            VoronoiGems -> listOf(
                0xFF174B69.toInt(), 0xFF43A7A0.toInt(), 0xFF9D71BD.toInt(), 0xFFF0C67A.toInt()
            )

            HexCubePattern -> listOf(
                0xFFBCDCD7.toInt(), 0xFF5F9B9E.toInt(), 0xFF284E66.toInt(), 0xFF132E40.toInt()
            )
            LightArray -> listOf(-16777216, -256, -16711681, -65281)
            CitySkyline -> listOf(-16117736, -9424331, -32768, -256)
            WinterLandscape -> listOf(-16117736, -10053172, -14587077, -1)
            CircleRipplePattern -> listOf(
                0xFF101E38.toInt(), 0xFF315988.toInt(), 0xFFA7C9DC.toInt(), 0xFFF6DCA1.toInt()
            )

            SquareRipplePattern -> listOf(
                0xFF142C3C.toInt(), 0xFF439A9B.toInt(), 0xFFD99472.toInt(), 0xFFF4E3B5.toInt()
            )
            else -> listOf(-16777216, -1, -8355712, -16711681)
        }

    fun resolveFrequency(value: Float): Float {
        if (!hasScale || !value.isFinite()) return defaultFrequency
        val range = if (placement == PatternPlacement.Canvas) 0.1f..64f
        else (defaultFrequency / 4f)..(defaultFrequency * 4f)
        return value.coerceIn(range)
    }

    fun resolve(values: Map<String, Float>): Map<String, Float> =
        parameters.associate { parameter ->
            val value = values[parameter.name]?.takeIf { it.isFinite() } ?: parameter.defaultValue
            parameter.name to value.coerceIn(parameter.range)
                .let { if (parameter.integer) it.roundToInt().toFloat() else it }
        }
}

enum class PatternPlacement { Canvas, None, Phase, PhaseXY }

data class PatternParameter(
    val name: String,
    val defaultValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val integer: Boolean = false,
    @StringRes val title: Int = patternParameterTitle(name)
)

@StringRes
private fun patternParameterTitle(name: String): Int = when (name) {
    "hardness" -> R.string.procedural_hardness
    "shapeAspect" -> R.string.procedural_shape_aspect
    "seed" -> R.string.procedural_seed
    "variability" -> R.string.procedural_variability
    "thickness" -> R.string.texture_thickness
    "balance" -> R.string.balance
    "intensity" -> R.string.intensity
    "count" -> R.string.procedural_count
    "shadows" -> R.string.shadows
    "radius" -> R.string.radius
    "offset" -> R.string.offset
    "layers" -> R.string.texture_pattern_layers
    "time" -> R.string.time
    else -> error("Unknown pattern parameter: $name")
}

data class PatternTextureParams(
    val values: Map<String, Float> = emptyMap(),
    val colors: List<Int> = listOf(-16777216, -1, -8355712, -16711681),
    val frequency: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val phaseX: Float = 0f,
    val phaseY: Float = 0f
)