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

import kotlin.math.roundToInt

private val patternSeed = PatternParameter("seed", 0.0f, 0.0f..10000.0f, integer = true)

enum class PatternTextureType(
    val parameters: List<PatternParameter>,
    val colorCount: Int,
    val defaultFrequency: Float,
    private val palette: List<Int>? = null
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
    LunariaPods(3.6f, 0xFF213E3B, 0xFF608073, 0xFFD2C6A3, 0xFFF5EBD3),
    HorsetailStems(
        4.6f, 0xFF132C30, 0xFF326355, 0xFF8EB578, 0xFFDDD499,
        patternSeed
    ),
    DandelionClocks(
        2.8f, 0xFF193E46, 0xFF5F8780, 0xFFD8C895, 0xFFF6F1DC,
        patternSeed
    ),
    CalicoKoi(
        3.3f, 0xFF174A50, 0xFF65A9A0, 0xFFF2E5C8, 0xFFCA573C,
        patternSeed
    ),
    ValleyBells(3.4f, 0xFF193E36, 0xFF48765C, 0xFF90AD78, 0xFFF4F0DC),
    SilkPoppies(
        3.2f, 0xFF233D39, 0xFF8F9B6D, 0xFFD85C45, 0xFFF3AD7C,
        patternSeed
    ),
    FlyAgaricGrove(
        3.7f, 0xFF263F42, 0xFF6D8D78, 0xFFC7523F, 0xFFF2DAB2,
        patternSeed
    ),
    BeachPebbles(
        4f, 0xFF294950, 0xFF789A94, 0xFFBEAE94, 0xFFEFE3C7,
        patternSeed
    ),
    TravertinePools(
        2.2f, 0xFF366F72, 0xFF82B2A8, 0xFFD1CEAB, 0xFFF4ECD8,
        patternSeed
    ),
    TidepoolStars(
        3.6f, 0xFF234D54, 0xFF62877C, 0xFFCF7655, 0xFFF2D49D,
        patternSeed
    ),
    WaveCutCliffs(
        1f, 0xFF243F51, 0xFF497E83, 0xFFC58467, 0xFFE6C89C,
        patternSeed
    ),
    SailingRegatta(
        3.2f, 0xFF203F58, 0xFF4B95A0, 0xFFE77E57, 0xFFF9E6B7,
        patternSeed
    ),
    BarnacleColony(
        6.3f, 0xFF2D4546, 0xFF6B8278, 0xFFBCE0D2, 0xFFD8B999,
        patternSeed
    ),
    FlyingPetrels(
        3.6f, 0xFF273D4D, 0xFF60919A, 0xFFC4CCC0, 0xFFF2E1BC,
        patternSeed
    ),
    MoonPhases(
        3.2f, 0xFF1B2C42, 0xFF506279, 0xFFC3A684, 0xFFF2E5C6,
        patternSeed
    ),
    SiderealTrails(
        1f, 0xFF142A42, 0xFF376381, 0xFFD9A36D, 0xFFE9EBDB,
        patternSeed
    ),
    OrionChart(
        1f, 0xFF172D43, 0xFF456E79, 0xFFC0996B, 0xFFF8E9B9,
        patternSeed
    ),
    MeteorShower(
        1.6f, 0xFF172D41, 0xFF456F79, 0xFFD08B67, 0xFFF8D59E,
        patternSeed
    ),
    ClockworkOrrery(
        1f, 0xFF132F37, 0xFF54736A, 0xFFC59155, 0xFFF4DC9D,
        PatternParameter("time", 0f, 0f..10f)
    ),
    CometPlume(
        1f, 0xFF17233A, 0xFF425583, 0xFF70BFC0, 0xFFF2D593,
        patternSeed
    ),
    BinarySunset(
        1f, 0xFF263649, 0xFF86526A, 0xFFD89071, 0xFFF5D49D,
        patternSeed
    ),
    PivotFields(
        4.8f, 0xFF7B7458, 0xFFA1A57A, 0xFFC1B587, 0xFF546E5A,
        patternSeed
    ),
    AlluvialBends(
        1.6f, 0xFF244E59, 0xFF639292, 0xFFB1AE83, 0xFFDFCCA0,
        patternSeed
    ),
    RakedSandGarden(
        1f, 0xFF374748, 0xFF8C8D7B, 0xFFC9BDA2, 0xFFEAE1C8,
        patternSeed
    ),
    BalloonFestival(
        3f, 0xFF4C8790, 0xFFF0C99A, 0xFFCE6656, 0xFF384958,
        patternSeed
    ),
    HarborLight(
        1f, 0xFF193544, 0xFF48737C, 0xFFD08563, 0xFFF3DCAD,
        patternSeed
    ),
    PaperLanterns(3.5f, 0xFF263348, 0xFFAF5448, 0xFFED9869, 0xFFF4D899),
    ParasolMarket(
        4f, 0xFF304B50, 0xFFD07055, 0xFFEBB477, 0xFFF5DEAA,
        patternSeed
    ),
    BackgammonPoints(
        2f, 0xFF184544, 0xFFEAD3A5, 0xFFBE563B, 0xFF332427,
        patternSeed
    ),
    DominoTable(
        3.2f, 0xFF224A43, 0xFFE7D7BA, 0xFF322D2B, 0xFFC69A55,
        patternSeed
    ),
    PlayingCardSuits(4f, 0xFFEFDFC8, 0xFF8D3138, 0xFF21383A, 0xFFC4955B),
    VinylCrate(2.5f, 0xFFD8694E, 0xFF203039, 0xFFE4B66E, 0xFFEBE1C5),
    MusicBoxRoll(2.5f, 0xFFDDC391, 0xFF352E39, 0xFFA24F3C, 0xFFF2DFB5),
    ZipperTape(3f, 0xFF4D6C66, 0xFF1D3336, 0xFFB88850, 0xFFE4C991),
    BobbinLace(3.2f, 0xFF35525D, 0xFFA2C1B7, 0xFFE2D3AD, 0xFFFBF0D2),
    MarquetryCompass(2.05f, 0xFF352D2C, 0xFF9B5839, 0xFFD4A667, 0xFFE9D3A1),
    WaffleIron(7f, 0xFF93602E, 0xFFCC8942, 0xFFEDBB73, 0xFF895227),
    CitrusWheels(
        3.2f, 0xFF276967, 0xFFE9D5A0, 0xFFE98447, 0xFFB94745,
        patternSeed
    ),
    SaltedPretzels(
        3.15f, 0xFF315B59, 0xFF7A392A, 0xFFD7924D, 0xFFF1D5A4,
        patternSeed
    ),
    LiquoriceAllsorts(
        4f, 0xFFF1DAC1, 0xFF302E37, 0xFFC8667C, 0xFF79A9A0,
        patternSeed
    ),
    RavioliStamp(
        3.5f, 0xFF355854, 0xFFF3D59D, 0xFFD69B54, 0xFF92572E,
        patternSeed
    ),
    CameoBrocade(2.9f, 0xFF3B4F52, 0xFF995D5E, 0xFFD19B6E, 0xFFEBD6B7),
    BowTiePatchwork(3.3f, 0xFFE9CEAC, 0xFFA35448, 0xFF3E6865, 0xFFE3A34E),
    TasselTrim(3.2f, 0xFF25434A, 0xFFCE744E, 0xFFE9B768, 0xFFE3CFAB),
    MillefioriCanes(
        4.4f, 0xFF223B43, 0xFFC76565, 0xFFE7B565, 0xFFEAD7AE,
        patternSeed
    ),
    GreekMeander(
        3.3f, 0xFF29485A, 0xFFE9D2A2, 0xFFAF604B, 0xFFDCA15B,
        PatternParameter("thickness", 0.038f, 0.015f..0.072f)
    ),
    PaperChainGarland(2.8f, 0xFF263C46, 0xFFE59B6B, 0xFF85BBB0, 0xFFD86F71),
    RibbonCockade(2.4f, 0xFF364858, 0xFFB8544F, 0xFFE6AD65, 0xFFECDCC2),
    VenetianShutters(
        3.5f, 0xFF153E3C, 0xFF358071, 0xFF8AB59A, 0xFFF4DAA3,
        PatternParameter("shadows", 0.68f, 0.05f..1f)
    ),
    SandstoneArcade(
        4f, 0xFF2C3538, 0xFFAE6C50, 0xFFE1BB88, 0xFFF5DEB0,
        PatternParameter("radius", 0.325f, 0.23f..0.37f)
    ),
    GearBlueprint(
        2.4f, 0xFF143752, 0xFF57819A, 0xFFEBE6CE, 0xFFE79459,
        PatternParameter("thickness", 0.008f, 0.003f..0.023f)
    ),
    AdmissionTickets(
        3.2f, 0xFF303C39, 0xFFD98958, 0xFFE6C991, 0xFF417A74,
        PatternParameter("radius", 0.07f, 0.03f..0.14f)
    ),
    SawtoothRoofscape(
        3.3f, 0xFFDFBD82, 0xFF617B80, 0xFFB65C46, 0xFF283F50,
        PatternParameter("shadows", 0.7f, 0f..1f)
    ),
    FanVault(
        2.6f, 0xFF343E49, 0xFF768780, 0xFFCFB688, 0xFFF1E2C5,
        PatternParameter("thickness", 0.013f, 0.005f..0.035f)
    ),
    WoodcutSwells(
        2.8f, 0xFFECDCC0, 0xFF76A39A, 0xFF365F69, 0xFF1F3541,
        PatternParameter("thickness", 0.034f, 0.012f..0.075f)
    ),
    ApartmentPlan(
        2.25f, 0xFFE5E0C7, 0xFF394D4E, 0xFFB69C73, 0xFF6D9B95,
        PatternParameter("thickness", 0.016f, 0.007f..0.03f)
    ),
    RooftopTanks(
        3.3f, 0xFFDAAC86, 0xFFE7D8B0, 0xFFA35646, 0xFF3A5360,
        PatternParameter("shadows", 0.8f, 0.1f..1f)
    ),
    BookclothSpines(
        2.6f, 0xFF384E55, 0xFF975344, 0xFFC4A16A, 0xFFE4CD96,
        PatternParameter("shadows", 0.65f, 0.05f..1f)
    ),
    HarbourBunting(
        2.5f, 0xFFE3CFA9, 0xFFC05B4D, 0xFF335971, 0xFFEDDFAF,
        PatternParameter("intensity", 0.75f, 0f..1f)
    ),
    PantographLinkage(
        2.6f, 0xFF234C4E, 0xFFBE9C55, 0xFFE7D1A0, 0xFFB55E44,
        PatternParameter("thickness", 0.025f, 0.012f..0.045f)
    ),
    DualTrace(
        1.45f, 0xFF09292A, 0xFF20524E, 0xFF8DF4B4, 0xFFEFAC61,
        PatternParameter("intensity", 0.8f, 0f..1f)
    ),
    StressFringes(
        1.75f, 0xFF18132E, 0xFF235CA3, 0xFFE98B55, 0xFFF5E6A7,
        PatternParameter("intensity", 4.9f, 1f..9f)
    ),
    WindBarbs(
        5.7f, 0xFFE7DDC1, 0xFFC0D1B8, 0xFF2C5956, 0xFFC0643D,
        PatternParameter("seed", 17f, 0f..10000f, integer = true),
        PatternParameter("variability", 0.65f, 0f..2f)
    ),
    AlignmentNomogram(
        2.5f, 0xFFF1E4CD, 0xFF527783, 0xFF282F3A, 0xFFC36645,
        PatternParameter("seed", 4f, 0f..10000f, integer = true)
    ),
    VacuumTriodes(
        3.15f, 0xFF172B38, 0xFF607F87, 0xFFEC9D5A, 0xFFF8D094,
        PatternParameter("intensity", 0.78f, 0f..1f),
        PatternParameter("seed", 9f, 0f..10000f, integer = true)
    ),
    BasePairs(
        2.6f, 0xFF192E43, 0xFF648AA1, 0xFFE98A74, 0xFFEBC971,
        PatternParameter("thickness", 0.028f, 0.012f..0.06f)
    ),
    HarmonicSpectrogram(
        1.45f, 0xFF14162F, 0xFF314A78, 0xFFC5666D, 0xFFF9CB76,
        PatternParameter("intensity", 1.2f, 0.5f..2f),
        PatternParameter("seed", 3f, 0f..10000f, integer = true)
    ),
    GelBands(
        1.2f, 0xFF101B31, 0xFF173E59, 0xFF43A9A6, 0xFFA9F0C7,
        PatternParameter("seed", 11f, 0f..10000f, integer = true)
    ),
    Stereonet(
        1.85f, 0xFF183441, 0xFF87AAAE, 0xFFF4E5C8, 0xFFD4855F,
        PatternParameter("count", 24f, 12f..40f, integer = true)
    ),
    PressureDials(
        2.35f, 0xFFE9DEBF, 0xFF31565E, 0xFFB86649, 0xFFF5EBCF,
        PatternParameter("seed", 18f, 0f..10000f, integer = true)
    ),
    AirfoilStreamlines(
        1.54f, 0xFF1E4653, 0xFF81ACAA, 0xFFEEDDB3, 0xFFD78A62,
        PatternParameter("count", 15f, 7f..25f, integer = true)
    ),
    MolecularModel(
        2.3f, 0xFFF1E4C8, 0xFF35676C, 0xFFCB7354, 0xFF263C49,
        PatternParameter("seed", 13f, 0f..10000f, integer = true),
        PatternParameter("variability", 0.65f, 0f..2f)
    ),
    MarshHeron(
        1f, 0xFFCED7C6, 0xFF6B918B, 0xFF203E4C, 0xFFD89D5C,
        PatternParameter("variability", 0.55f, 0f..1f)
    ),
    SwitchbackPass(
        1f, 0xFFD9C4A5, 0xFFA47665, 0xFF5E7271, 0xFF293D4D,
        PatternParameter("variability", 0.62f, 0.15f..1f)
    ),
    CicadaAmber(
        1f, 0xFF302B38, 0xFF8C6A56, 0xFFE0A858, 0xFFF1DFAD,
        PatternParameter("shapeAspect", 1f, 0.7f..1.25f)
    ),
    Passionflower(
        1f, 0xFF244C47, 0xFFD7E3BA, 0xFFA88BC4, 0xFFF0BD61,
        PatternParameter("count", 60f, 30f..80f, integer = true)
    ),
    IcebergKeel(
        1f, 0xFFDDE9DC, 0xFF65BBC1, 0xFF2C758E, 0xFF173951,
        PatternParameter("balance", 0.43f, 0.25f..0.68f)
    ),
    SoundingFlukes(
        1f, 0xFFC7CBB4, 0xFFD79A70, 0xFF467C80, 0xFF1B3A4D,
        PatternParameter("time", 0.2f, 0f..4f)
    ),
    SamaraFlight(
        3.2f, 0xFF313F48, 0xFFC48556, 0xFFCDAC70, 0xFFE3D5AC,
        PatternParameter("variability", 0.7f, 0f..1f),
        patternSeed
    ),
    OctopusArm(
        1f, 0xFF142F49, 0xFF467F87, 0xFFCB735F, 0xFFF4C59B,
        PatternParameter("variability", 0.72f, 0.15f..1f)
    ),
    WardianCase(
        1f, 0xFF293B42, 0xFF5F887A, 0xFFBFAB71, 0xFFE3DDC3,
        PatternParameter("layers", 4f, 2f..6f, integer = true)
    ),
    AntGalleries(
        1f, 0xFF432F39, 0xFFA76550, 0xFFDCAB78, 0xFF392B2C,
        PatternParameter("variability", 0.65f, 0f..1f)
    ),
    RainShafts(
        1f, 0xFF343B57, 0xFF6C7895, 0xFFC4AFB1, 0xFFF0CA8C,
        PatternParameter("intensity", 0.7f, 0.2f..1f)
    ),
    PitcherPlants(
        2f, 0xFF263D43, 0xFF69866F, 0xFFC57067, 0xFFEBC095,
        PatternParameter("shapeAspect", 1f, 0.72f..1.22f)
    ),
    PoleFoldShibori(
        1.6f, 0xFF152C59, 0xFF245A89, 0xFF81A8BC, 0xFFF2E9D3,
        PatternParameter("variability", 0.65f, 0f..1f)
    ),
    PeacockMarbling(
        2.45f, 0xFF203F55, 0xFF17928B, 0xFFEAB967, 0xFFDE7663,
        PatternParameter("radius", 0.255f, 0.12f..0.48f)
    ),
    LogCabinQuilt(
        2f, 0xFF244B55, 0xFF549692, 0xFFDA784D, 0xFFEBCB8E,
        PatternParameter("thickness", 0.0027f, 0.0005f..0.009f)
    ),
    CoiledRagRug(
        1.05f, 0xFF243E4A, 0xFFB94653, 0xFFE79845, 0xFFD7C78F,
        PatternParameter("shadows", 0.85f, 0.05f..1f)
    ),
    ButtonTufting(
        3.5f, 0xFF212B55, 0xFF424F9C, 0xFF9B91BE, 0xFFEBC47E,
        PatternParameter("shadows", 0.7f, 0f..1f)
    ),
    TornPosterWall(
        1.25f, 0xFF234750, 0xFFC65B55, 0xFFE3AD4C, 0xFFF1D8AA,
        PatternParameter("variability", 0.8f, 0f..1f)
    ),
    LatteRosetta(
        1f, 0xFF55342B, 0xFFAF6843, 0xFFDAA66F, 0xFFF6EBD0,
        PatternParameter("thickness", 0.021f, 0.009f..0.034f)
    ),
    SalmonMarbling(
        1.5f, 0xFF963F3C, 0xFFF47759, 0xFFF1AF81, 0xFFF8DEC0,
        PatternParameter("thickness", 0.011f, 0.0035f..0.027f)
    ),
    PomegranateCut(
        1f, 0xFF294E52, 0xFF8B2A41, 0xFFE3565A, 0xFFEEC391,
        patternSeed
    ),
    LetterpressOverprint(
        1.7f, 0xFFEBDAB3, 0xFFEF7660, 0xFF107D83, 0xFF283F51,
        PatternParameter("offset", 0.018f, -0.12f..0.12f)
    ),
    PaperQuilling(
        1.1f, 0xFF2E394B, 0xFFE8B865, 0xFFE47278, 0xFF88BEB2,
        PatternParameter("thickness", 0.0064f, 0.0025f..0.0135f)
    ),
    GoldLeafTransfer(
        2f, 0xFF633831, 0xFF9F5540, 0xFFCC943D, 0xFFF4D989,
        PatternParameter("variability", 0.55f, 0f..0.95f)
    ),
    CaneWebbing(
        3.1f, 0xFF313F3C, 0xFFAA7542, 0xFFD9AD6B, 0xFFF2D198,
        PatternParameter("thickness", 0.073f, 0.035f..0.105f)
    ),
    PleatedHandFan(
        1f, 0xFF283956, 0xFFC35651, 0xFFF3B76D, 0xFFF4D9B3,
        PatternParameter("count", 19f, 9f..35f, integer = true)
    ),
    TailorTape(
        1.1f, 0xFF424564, 0xFFF1C45B, 0xFFE9BEB0, 0xFF343336,
        PatternParameter("variability", 0.56f, 0f..1f)
    ),
    PalmetteFrieze(
        2.1f, 0xFF283D50, 0xFF6B97A1, 0xFFF1BD68, 0xFFDF7D68,
        PatternParameter("thickness", 0.046f, 0.018f..0.08f)
    ),
    Switchyard(
        1f, 0xFF263F44, 0xFF8E6950, 0xFFD7BC84, 0xFFC76745,
        PatternParameter("offset", 0.19f, -0.12f..0.35f)
    ),
    SuspensionSpan(
        1f, 0xFFD8C6A4, 0xFF81A49C, 0xFF4A6471, 0xFF854D43,
        PatternParameter("intensity", 0.31f, 0.12f..0.48f)
    ),
    RainCrosswalk(
        1f, 0xFF203943, 0xFF668780, 0xFFDCCDA7, 0xFFD25E45,
        PatternParameter("radius", 0.117f, 0.07f..0.17f)
    ),
    CofferedRotunda(
        1f, 0xFF363E4A, 0xFF78634E, 0xFFBD986F, 0xFFEBD6B0,
        PatternParameter("count", 17f, 8f..28f, integer = true)
    ),
    StationCanopy(
        1f, 0xFFDFD8B2, 0xFF92B8B2, 0xFF456071, 0xFFBE775B,
        PatternParameter("offset", 0.2f, -0.25f..0.33f)
    ),
    PendulumCase(
        1f, 0xFF293E42, 0xFFB18657, 0xFFE9CE8F, 0xFFA45547,
        PatternParameter("time", 0.34f, -1.1f..1.1f)
    ),
    ScrewBookpress(
        1f, 0xFFE2C89B, 0xFF946B53, 0xFF344C50, 0xFFC2684D,
        PatternParameter("offset", 0.075f, 0f..0.17f)
    ),
    TheatreWings(
        1f, 0xFF222C3B, 0xFF71464B, 0xFFC76B52, 0xFFF2CD94,
        PatternParameter("offset", 0.18f, 0.045f..0.29f)
    ),
    HornGramophone(
        1f, 0xFF253E44, 0xFF9F6752, 0xFFD3A265, 0xFFF0D7A5,
        PatternParameter("radius", 0.273f, 0.18f..0.32f)
    ),
    CanalLock(
        1f, 0xFF274C50, 0xFF6E9690, 0xFFE1C291, 0xFFA9684E,
        PatternParameter("offset", 0.106f, -0.03f..0.205f)
    ),
    FunicularIncline(
        1f, 0xFFDCCCA8, 0xFF8BA5A0, 0xFF364F56, 0xFFB96248,
        PatternParameter("offset", 0.08f, -0.24f..0.24f)
    ),
    CinemaMarquee(
        1f, 0xFF2A384A, 0xFF673F49, 0xFFC57659, 0xFFF2CA82,
        PatternParameter("intensity", 0.82f, 0.2f..1.2f)
    ),
    SolarProminences(
        1f, 0xFF150E20, 0xFF882439, 0xFFE35D36, 0xFFFBD585,
        PatternParameter("time", 1.1f, 0f..8f),
        PatternParameter("seed", 18f, 0f..10000f, integer = true)
    ),
    StellarBowShock(
        1f, 0xFF0B2232, 0xFF316671, 0xFFDA9374, 0xFFF7E8BD,
        PatternParameter("variability", 1f, 0f..2f),
        PatternParameter("seed", 76f, 0f..10000f, integer = true)
    ),
    HourglassNebula(
        1f, 0xFF0C132A, 0xFF306180, 0xFFB65F67, 0xFFF2CB96,
        PatternParameter("shapeAspect", 1f, 0.55f..1.45f),
        PatternParameter("seed", 32f, 0f..10000f, integer = true)
    ),
    RocheLobes(
        1f, 0xFF182F40, 0xFF4C7E83, 0xFFD7AD73, 0xFFF0E5C9,
        PatternParameter("balance", 0.32f, 0.15f..0.48f)
    ),
    BasaltColonnade(
        1f, 0xFF243942, 0xFF45636B, 0xFF779695, 0xFFD6CBB1,
        PatternParameter("variability", 0.95f, 0.1f..1.8f),
        PatternParameter("seed", 37f, 0f..10000f, integer = true)
    ),
    FaultedStrata(
        1f, 0xFF2F3B45, 0xFFA15E4B, 0xFFE8BB7D, 0xFFDCD7BC,
        PatternParameter("offset", 0.215f, 0.025f..0.42f),
        PatternParameter("seed", 4f, 0f..10000f, integer = true)
    ),
    GarnetSchist(
        1f, 0xFF283842, 0xFF82918B, 0xFF993D48, 0xFFE4CCAE,
        PatternParameter("radius", 1f, 0.5f..1.35f),
        PatternParameter("seed", 56f, 0f..10000f, integer = true)
    ),
    OoliticLimestone(
        1f, 0xFF584E45, 0xFFBA9F79, 0xFFE8D5A8, 0xFFA26950,
        PatternParameter("count", 4f, 2f..7f, integer = true),
        PatternParameter("seed", 43f, 0f..10000f, integer = true)
    ),
    TrilobiteImpression(
        1f, 0xFF494B44, 0xFF8B896F, 0xFFBDAA84, 0xFFE5D5AE,
        PatternParameter("intensity", 0.82f, 0.25f..1f),
        PatternParameter("seed", 26f, 0f..10000f, integer = true)
    ),
    DoubleSlit(
        1f, 0xFF172C45, 0xFF326A8A, 0xFF77CBC6, 0xFFFBE0A2,
        PatternParameter("radius", 0.115f, 0.045f..0.21f),
        PatternParameter("time", 1.8f, 0f..6.28f)
    ),
    LensCrossSection(
        1f, 0xFF183840, 0xFF5F9492, 0xFFDCE6C9, 0xFFED9D61,
        PatternParameter("offset", 0.35f, 0.2f..0.55f)
    ),
    IrisDiaphragm(
        1f, 0xFF18262A, 0xFF4A625F, 0xFFBDA785, 0xFFE8CBA0,
        PatternParameter("count", 9f, 6f..13f, integer = true),
        PatternParameter("radius", 0.145f, 0.045f..0.29f)
    ),
    EmissionSpectrum(
        1f, 0xFF0B1729, 0xFF4457B0, 0xFF44C4AF, 0xFFFDA966,
        PatternParameter("thickness", 0.095f, 0.045f..0.17f),
        PatternParameter("seed", 41f, 0f..10000f, integer = true)
    ),
    SchlierenPlume(
        1f, 0xFFECDFBF, 0xFF40586A, 0xFFC67A54, 0xFF718E86,
        PatternParameter("time", 1.7f, 0f..6.28f),
        PatternParameter("seed", 7f, 0f..10000f, integer = true)
    ),
    HandRadiograph(
        1f, 0xFF101F30, 0xFF344E5E, 0xFF91B1B5, 0xFFE2DDC5,
        PatternParameter("intensity", 0.92f, 0.24f..1f)
    ),
    LogisticBifurcation(
        1f, 0xFFF1E7CC, 0xFF9CA390, 0xFF966858, 0xFF294D55,
        PatternParameter("thickness", 0.003f, 0.001f..0.009f)
    );

    private constructor(
        frequency: Float,
        background: Long,
        primary: Long,
        secondary: Long,
        accent: Long,
        vararg parameters: PatternParameter
    ) : this(
        parameters.toList(), 4, frequency,
        listOf(background.toInt(), primary.toInt(), secondary.toInt(), accent.toInt())
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

    fun resolve(values: Map<String, Float>): Map<String, Float> =
        parameters.associate { parameter ->
            val value = values[parameter.name]?.takeIf { it.isFinite() } ?: parameter.defaultValue
            parameter.name to value.coerceIn(parameter.range)
                .let { if (parameter.integer) it.roundToInt().toFloat() else it }
        }
}

data class PatternParameter(
    val name: String,
    val defaultValue: Float,
    val range: ClosedFloatingPointRange<Float>,
    val integer: Boolean = false
)

data class PatternTextureParams(
    val values: Map<String, Float> = emptyMap(),
    val colors: List<Int> = listOf(-16777216, -1, -8355712, -16711681),
    val frequency: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f
)