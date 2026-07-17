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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel

data class FastNoiseTextureParams(
    val seed: Int,
    val scale: Float,
    val values: List<Float>,
    val colors: List<ColorModel>
) {
    companion object {
        fun defaultFor(type: TextureFilterType): FastNoiseTextureParams = when (type) {
            TextureFilterType.Brick -> create(
                0.02f, 2.15f, 0.09f, 0.18f, 0.42f, 0.55f,
                -4674151, -9493480, -4830158, -2655659
            )

            TextureFilterType.Camouflage -> create(
                0.007f, 0.36f, 0.58f, 0.76f, 28f, 0.035f,
                -14670055, -11574988, -8887484, -4938633
            )

            TextureFilterType.Cell -> create(
                0.018f, 0.92f, 0.12f, 0.28f, 6f, 0.42f,
                -15721948, -14586248, -10825272, -3538956
            )

            TextureFilterType.Cloud -> create(
                0.0045f, 0.48f, 0.22f, 0.62f, 24f, 0.92f,
                -9459236, -6378052, -459777
            )

            TextureFilterType.Crack -> create(
                0.019f, 0.065f, 0.72f, 9f, 0.72f, 0.45f,
                -7502988, -4147547, -15330286, -11186619
            )

            TextureFilterType.Fabric -> create(
                0.018f, 28f, 28f, 0.14f, 0.48f, 0.1f,
                -11965566, -8874585, -14271413, -4797740
            )

            TextureFilterType.Foliage -> create(
                0.022f, 0.72f, 0.16f, 0.38f, 0.62f, 0.58f,
                -15717869, -14394331, -11625923, -5976475
            )

            TextureFilterType.Honeycomb -> create(
                0.018f, 0.095f, 0.5f, 0.12f, 0.72f, 0.35f,
                -14413051, -9487864, -1729774, -11172
            )

            TextureFilterType.Ice -> create(
                0.014f, 0.075f, 0.48f, 0.64f, 8f, 0.32f,
                -15377799, -8795940, -2558731, -655361
            )

            TextureFilterType.Lava -> create(
                0.008f, 32f, 1.35f, 0.55f, 0.52f, 0.72f,
                -15594742, -2807030, -11174
            )

            TextureFilterType.Nebula -> create(
                0.004f, 44f, 0.64f, 0.38f, 0.72f, 1.45f,
                -16447725, -11262601, -14456926, -875297
            )

            TextureFilterType.Paper -> create(
                0.012f, 72f, 0.24f, 0.16f, 0.14f, 0.35f,
                -1780562, -661047, -4875152, -7706559
            )

            TextureFilterType.Rust -> create(
                0.009f, 0.55f, 0.0f, 0.42f, 16f, 1.25f,
                -10919835, -12970229, -6603245, -2066398
            )

            TextureFilterType.Sand -> create(
                0.005f, 12f, 0.32f, 0.62f, 0.22f, 1.18f,
                -6659282, -2709414, -862835
            )

            TextureFilterType.Smoke -> create(
                0.006f, 34f, 0.56f, 2.2f, 1.35f, 0.5f,
                -15658216, -12301742, -2762275
            )

            TextureFilterType.Stone -> create(
                0.011f, 0.3f, 0.58f, 0.024f, 18f, 1.12f,
                -13355722, -5593185, -1909037
            )

            TextureFilterType.Terrain -> create(
                0.0045f, 0.34f, 0.7f, 0.46f, 0.58f, 0.86f,
                -15118989, -10516673, -9147299, -1446168
            )

            TextureFilterType.Topography -> create(
                0.0055f, 16f, 0.11f, 0.42f, 12f, 1.1f,
                -15254469, -4863606, -858442
            )

            TextureFilterType.WaterRipple -> create(
                0.0075f, 22f, 22f, 0.58f, 0.55f, 0.62f,
                -16305066, -15298898, -4589582
            )

            TextureFilterType.AdvancedWood -> create(
                0.0039f, 11.4f, 0.3f, 15f, 7.42f, 0.17f,
                -12969717, -4624846, -14873337
            )

            TextureFilterType.Grass -> create(
                0.012f, 1f, 50f, 1f, 0.35f, 0.45f,
                -12965096, -14857953, -11695566, -6568870
            )

            TextureFilterType.Dirt -> create(
                0.009f, 0.62f, 0.35f, 0.22f, 0.68f, 0.52f,
                -14018290, -9879002, -6195635, -5135732
            )

            TextureFilterType.Leather -> create(
                0.011f, 0.58f, 0.42f, 0.5f, 0.38f, 0.035f,
                -13625841, -8700635, -5018813, -14939128
            )

            TextureFilterType.Concrete -> create(
                0.014f, 0.45f, 0.28f, 0.65f, 0.14f, 1.08f,
                -10856107, -6711666, -3685447, -13290190
            )

            TextureFilterType.Asphalt -> create(
                0.02f, 0.72f, 0.44f, 0.28f, 0.48f, 1.3f,
                -15263462, -12697022, -7763067, -5199467
            )

            TextureFilterType.Moss -> create(
                0.018f, 0.74f, 0.58f, 0.38f, 0.62f, 0.46f,
                -14080746, -14072548, -10190780, -5128855
            )

            TextureFilterType.Fire -> create(
                0.006f, 8f, 36f, 0.78f, 0.18f, 0.62f,
                -15726585, -4908280, -34294, -5750
            )

            TextureFilterType.Aurora -> create(
                0.004f, 7f, 34f, 0.72f, 0.2f, 1.32f,
                -16379353, -12396128, -10099480, -5935646
            )

            TextureFilterType.OilSlick -> create(
                0.008f, 13f, 32f, 0.82f, 0.3f, 1.2f,
                -15724518, -2805093, -14297904, -998596
            )

            TextureFilterType.Watercolor -> create(
                0.006f, 0.72f, 0.65f, 0.38f, 0.22f, 0.62f,
                -726056, -13402189, -2597511, -11976842
            )

            TextureFilterType.AbstractFlow -> create(
                0.006f, 12f, 46f, 0.3f, 1.25f, 0.62f,
                -15658712, -11446823, -2208845, -8457497
            )

            TextureFilterType.Opal -> create(
                0.007f, 0.82f, 0.48f, 8f, 28f, 0.62f,
                -2234145, -11545135, -1019210, -14249
            )

            TextureFilterType.DamascusSteel -> create(
                0.006f, 22f, 0.72f, 34f, 0.58f, 1.4f,
                -15262688, -10063238, -3616558, -13611945
            )

            TextureFilterType.Lightning -> create(
                0.006f, 7f, 42f, 0.055f, 0.82f, 0.9f,
                -16579054, -14334296, -9909761, -852737
            )

            TextureFilterType.Velvet -> create(
                0.014f, 0.78f, 0.18f, 0.7f, 0.62f, 0.34f,
                -15268578, -10610838, -4110152, -1005864
            )

            TextureFilterType.InkMarbling -> create(
                0.006f, 14f, 48f, 0.52f, 0.5f, 1.25f,
                -792109, -15316104, -6477495, -14936015
            )

            TextureFilterType.HolographicFoil -> create(
                0.008f, 12f, 0.7f, 0.82f, 0.3f, 0.72f,
                -3287842, -11672094, -1484088, -529806
            )

            TextureFilterType.Bioluminescence -> create(
                0.009f, 0.72f, 0.58f, 30f, 0.86f, 0.62f,
                -16573926, -16035507, -14620990, -3866639
            )

            TextureFilterType.CosmicVortex -> create(
                0.008f, 5f, 12f, 0.52f, 0.28f, 0.82f,
                -16645365, -14202200, -7520574, -8800
            )

            TextureFilterType.LavaLamp -> create(
                0.007f, 6f, 0.24f, 32f, 0.58f, 1.3f,
                -14939863, -49791, -30172, -11172
            )

            TextureFilterType.EventHorizon -> create(
                0.008f, 0.72f, 0.15f, 0.065f, 0.82f, 0.22f,
                -16711417, -42472, -8029, -8632065
            )

            TextureFilterType.FractalBloom -> create(
                0.01f, 7f, 5f, 4.2f, 0.7f, 0.72f,
                -16185829, -10927914, -49517, -6006
            )

            TextureFilterType.ChromaticTunnel -> create(
                0.009f, 18f, 5.5f, 7f, 0.48f, 0.78f,
                -16579306, -16722433, -54116, -3656
            )

            TextureFilterType.EclipseCorona -> create(
                0.008f, 0.23f, 0.2f, 34f, 0.62f, 0.8f,
                -16645366, -9020417, -25787, -1
            )

            TextureFilterType.StrangeAttractor -> create(
                0.01f, 3f, 18f, 6f, 0.035f, 0.8f,
                -16579311, -15410747, -50567, -3659
            )

            TextureFilterType.FerrofluidCrown -> create(
                0.012f, 19f, 0.14f, 0.22f, 0.86f, 0.42f,
                -1515052, -16316148, -13288125, -2492417
            )

            TextureFilterType.Supernova -> create(
                0.01f, 0.27f, 0.075f, 0.72f, 0.68f, 0.24f,
                -16645365, -9886793, -45790, -3387
            )

            TextureFilterType.Iris -> create(
                0.014f, 0.12f, 0.38f, 46f, 0.72f, 0.82f,
                -16250355, -15583674, -13125979, -1657264
            )

            TextureFilterType.PeacockFeather -> create(
                0.012f, 0.24f, 54f, 0.58f, 0.8f, 0.42f,
                -16378609, -15303339, -15578948, -1786811
            )

            TextureFilterType.NautilusShell -> create(
                0.011f, 3.4f, 19f, 0.13f, 0.68f, 0.46f,
                -15392216, -11915998, -2511495, -3887
            )

            TextureFilterType.RingedPlanet -> create(
                0.009f, 0.24f, 0.72f, 0.16f, 0.62f, 0.25f,
                -16645107, -15391923, -1733534, -7770
            )

            TextureFilterType.Geode -> create(
                0.008f, 0.68f, 0.55f, 0.72f, 0.70f, 1.35f,
                0xFF120E20.toInt(), 0xFF7046A8.toInt(), 0xFF3ED7D1.toInt(), 0xFFF4DDA6.toInt()
            )

            TextureFilterType.PrismaticLight -> create(
                0.008f, 0.66f, 0.72f, 0.76f, 0.78f, 1.35f,
                0xFF07091A.toInt(), 0xFF246DD8.toInt(), 0xFFE94DA9.toInt(), 0xFFFFE47A.toInt()
            )

            TextureFilterType.StainedGlass -> create(
                0.008f, 0.62f, 0.38f, 0.75f, 0.52f, 1.25f,
                0xFF120F18.toInt(), 0xFF2E8BD2.toInt(), 0xFFE54472.toInt(), 0xFFFFD34E.toInt()
            )

            TextureFilterType.KelpForest -> create(
                0.006f, 0.64f, 0.68f, 0.62f, 0.76f, 1.35f,
                0xFF031C22.toInt(), 0xFF07594F.toInt(), 0xFF42A66E.toInt(), 0xFFD1F29B.toInt()
            )

            TextureFilterType.FrostFern -> create(
                0.008f, 0.72f, 0.48f, 0.70f, 0.78f, 1.30f,
                0xFF10273A.toInt(), 0xFF559AB4.toInt(), 0xFFB8E8ED.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.LiquidCrystal -> create(
                0.008f, 0.66f, 0.62f, 0.58f, 0.72f, 1.30f,
                0xFF090A18.toInt(), 0xFF18BFD2.toInt(), 0xFFE142A9.toInt(), 0xFFFFE96A.toInt()
            )

            TextureFilterType.DragonScales -> create(
                0.007f, 0.58f, 0.35f, 0.72f, 0.60f, 1.35f,
                0xFF071913.toInt(), 0xFF17664B.toInt(), 0xFF39B778.toInt(), 0xFFE0C96B.toInt()
            )

            TextureFilterType.FireflySwarm -> create(
                0.007f, 0.66f, 0.72f, 0.82f, 0.90f, 1.45f,
                0xFF020B12.toInt(), 0xFF0B4D49.toInt(), 0xFF80D85A.toInt(), 0xFFFFFFB0.toInt()
            )

            TextureFilterType.Mycelium -> create(
                0.008f, 0.70f, 0.62f, 0.72f, 0.74f, 1.45f,
                0xFF07100B.toInt(), 0xFF24452E.toInt(), 0xFFB7C985.toInt(), 0xFFF4E8C1.toInt()
            )

            TextureFilterType.Kintsugi -> create(
                0.008f, 0.60f, 0.48f, 0.82f, 0.70f, 1.35f,
                0xFF10131A.toInt(), 0xFF344158.toInt(), 0xFFD99B32.toInt(), 0xFFFFE7A3.toInt()
            )

            TextureFilterType.CarbonFiber -> create(
                0.010f, 0.58f, 0.22f, 0.74f, 0.42f, 1.50f,
                0xFF030405.toInt(), 0xFF15191C.toInt(), 0xFF343B40.toInt(), 0xFF88949B.toInt()
            )

            TextureFilterType.CircuitBoard -> create(
                0.008f, 0.64f, 0.25f, 0.80f, 0.72f, 1.45f,
                0xFF03170F.toInt(), 0xFF075A3B.toInt(), 0xFFC28B28.toInt(), 0xFF7BFFD1.toInt()
            )

            TextureFilterType.SoapFilm -> create(
                0.007f, 0.68f, 0.72f, 0.55f, 0.72f, 1.25f,
                0xFF10182B.toInt(), 0xFF20CDE2.toInt(), 0xFFF45CB4.toInt(), 0xFFFFE66D.toInt()
            )

            TextureFilterType.MoireGuilloche -> create(
                0.008f, 0.55f, 0.30f, 0.72f, 0.48f, 1.50f,
                0xFF070914.toInt(), 0xFF2747A5.toInt(), 0xFFBD4BC8.toInt(), 0xFFF2EAFB.toInt()
            )

            TextureFilterType.SnakeSkin -> create(
                0.009f, 0.62f, 0.40f, 0.72f, 0.40f, 1.35f,
                0xFF17170E.toInt(), 0xFF4E5A2D.toInt(), 0xFF9DA75A.toInt(), 0xFFD9D19A.toInt()
            )

            TextureFilterType.Terrazzo -> create(
                0.008f, 0.58f, 0.32f, 0.68f, 0.28f, 1.25f,
                0xFFE5DCCF.toInt(), 0xFF315C72.toInt(), 0xFFC85554.toInt(), 0xFFE5A73D.toInt()
            )

            TextureFilterType.GalaxyFilaments -> create(
                0.007f, 0.70f, 0.62f, 0.78f, 0.84f, 1.50f,
                0xFF02030A.toInt(), 0xFF173B78.toInt(), 0xFF8137A6.toInt(), 0xFFFFE7BE.toInt()
            )

            TextureFilterType.VolcanicObsidian -> create(
                0.008f, 0.64f, 0.48f, 0.75f, 0.64f, 1.55f,
                0xFF020304.toInt(), 0xFF17141D.toInt(), 0xFF3B2744.toInt(), 0xFFEF6C3D.toInt()
            )

            TextureFilterType.MotherboardHeatmap -> create(
                0.008f, 0.62f, 0.38f, 0.76f, 0.68f, 1.45f,
                0xFF07120E.toInt(), 0xFF08705B.toInt(), 0xFFFF7B22.toInt(), 0xFFFFF1A8.toInt()
            )

            TextureFilterType.MicroscopicDiatoms -> create(
                0.007f, 0.62f, 0.30f, 0.74f, 0.58f, 1.35f,
                0xFF082431.toInt(), 0xFF1E7181.toInt(), 0xFF74D8C9.toInt(), 0xFFFFE9A8.toInt()
            )

            TextureFilterType.ReactionDiffusion -> create(
                0.009f, 0.72f, 0.68f, 0.72f, 0.68f, 1.45f,
                0xFF090B17.toInt(), 0xFF303785.toInt(), 0xFFE64C93.toInt(), 0xFFFFD76A.toInt()
            )

            TextureFilterType.CoralGrowth -> create(
                0.008f, 0.68f, 0.62f, 0.70f, 0.72f, 1.40f,
                0xFF071D2A.toInt(), 0xFFEA536E.toInt(), 0xFFFFA468.toInt(), 0xFFFFE2B8.toInt()
            )

            TextureFilterType.SlimeMold -> create(
                0.008f, 0.72f, 0.72f, 0.76f, 0.74f, 1.50f,
                0xFF08110A.toInt(), 0xFF596F17.toInt(), 0xFFD5D928.toInt(), 0xFFFFF5A0.toInt()
            )

            TextureFilterType.DendriticCrystal -> create(
                0.008f, 0.68f, 0.52f, 0.80f, 0.82f, 1.50f,
                0xFF071829.toInt(), 0xFF25678C.toInt(), 0xFFA8E5F0.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.ElectricArcField -> create(
                0.008f, 0.72f, 0.72f, 0.82f, 0.90f, 1.55f,
                0xFF030511.toInt(), 0xFF173A91.toInt(), 0xFF32C8FF.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.CloudChamber -> create(
                0.008f, 0.68f, 0.58f, 0.75f, 0.82f, 1.45f,
                0xFF080913.toInt(), 0xFF2D416A.toInt(), 0xFF9BC5E7.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.TurbulentInk -> create(
                0.006f, 0.70f, 0.78f, 0.68f, 0.62f, 1.40f,
                0xFFF0E8D8.toInt(), 0xFF174A72.toInt(), 0xFF9E284F.toInt(), 0xFF171629.toInt()
            )

            TextureFilterType.CellularEmbryo -> create(
                0.008f, 0.62f, 0.48f, 0.72f, 0.58f, 1.35f,
                0xFF220E26.toInt(), 0xFF723259.toInt(), 0xFFE07B93.toInt(), 0xFFFFD0B3.toInt()
            )

            TextureFilterType.NeuralGarden -> create(
                0.008f, 0.70f, 0.72f, 0.78f, 0.78f, 1.50f,
                0xFF06120D.toInt(), 0xFF11684A.toInt(), 0xFF6BD87A.toInt(), 0xFFE7F58E.toInt()
            )

            TextureFilterType.MagneticField -> create(
                0.008f, 0.68f, 0.42f, 0.74f, 0.72f, 1.40f,
                0xFF070A17.toInt(), 0xFF2452A0.toInt(), 0xFFE23D70.toInt(), 0xFFFFE5A6.toInt()
            )

            TextureFilterType.RiverDelta -> create(
                0.006f, 0.68f, 0.78f, 0.76f, 0.62f, 1.50f,
                0xFF25190E.toInt(), 0xFF8B693B.toInt(), 0xFF1688A5.toInt(), 0xFF8EDDE0.toInt()
            )

            TextureFilterType.LichenColony -> create(
                0.009f, 0.64f, 0.58f, 0.70f, 0.48f, 1.35f,
                0xFF29291A.toInt(), 0xFF66743A.toInt(), 0xFFB1B85B.toInt(), 0xFFE0D59A.toInt()
            )

            TextureFilterType.BacterialCulture -> create(
                0.009f, 0.68f, 0.62f, 0.68f, 0.58f, 1.35f,
                0xFF160C20.toInt(), 0xFF663078.toInt(), 0xFFE05C9E.toInt(), 0xFFFFD37C.toInt()
            )

            TextureFilterType.FluidVorticity -> create(
                0.007f, 0.72f, 0.78f, 0.70f, 0.72f, 1.40f,
                0xFF071326.toInt(), 0xFF1655A2.toInt(), 0xFFE44574.toInt(), 0xFFFFD36E.toInt()
            )

            TextureFilterType.CrystalGrowth -> create(
                0.008f, 0.64f, 0.48f, 0.80f, 0.78f, 1.50f,
                0xFF101525.toInt(), 0xFF3F62A1.toInt(), 0xFFA9D8EE.toInt(), 0xFFFFF0C2.toInt()
            )

            TextureFilterType.GalacticWeb -> create(
                0.007f, 0.72f, 0.68f, 0.80f, 0.86f, 1.55f,
                0xFF010208.toInt(), 0xFF182B69.toInt(), 0xFF6B3EA0.toInt(), 0xFFFFE9CA.toInt()
            )

            TextureFilterType.VeinedLeaf -> create(
                0.008f, 0.68f, 0.52f, 0.78f, 0.62f, 1.40f,
                0xFF07180C.toInt(), 0xFF1E6732.toInt(), 0xFF75BC50.toInt(), 0xFFD6E782.toInt()
            )

            TextureFilterType.PorousSponge -> create(
                0.009f, 0.64f, 0.42f, 0.72f, 0.42f, 1.35f,
                0xFF5B3718.toInt(), 0xFFB16C27.toInt(), 0xFFE5B24E.toInt(), 0xFFFFE2A0.toInt()
            )

            TextureFilterType.RainOnGlass -> create(
                0.008f, 0.68f, 0.58f, 0.74f, 0.78f, 1.40f,
                0xFF071728.toInt(), 0xFF194C70.toInt(), 0xFF69BDD2.toInt(), 0xFFDFFBFF.toInt()
            )

            TextureFilterType.EmberField -> create(
                0.008f, 0.72f, 0.62f, 0.76f, 0.88f, 1.55f,
                0xFF100504.toInt(), 0xFF76200B.toInt(), 0xFFF05A14.toInt(), 0xFFFFE06B.toInt()
            )

            TextureFilterType.QuantumFoam -> create(
                0.008f, 0.72f, 0.78f, 0.72f, 0.78f, 1.50f,
                0xFF060616.toInt(), 0xFF353499.toInt(), 0xFF23C9CB.toInt(), 0xFFF66AC2.toInt()
            )

            TextureFilterType.ChladniPlate -> create(
                0.008f, 0.62f, 0.38f, 0.82f, 0.54f, 1.55f,
                0xFF06080B.toInt(), 0xFF2E343A.toInt(), 0xFF8E9AA3.toInt(), 0xFFF1D78A.toInt()
            )

            TextureFilterType.CymaticRosette -> create(
                0.008f, 0.68f, 0.44f, 0.78f, 0.76f, 1.45f,
                0xFF071426.toInt(), 0xFF176FB0.toInt(), 0xFF55D3DE.toInt(), 0xFFFFE9A5.toInt()
            )

            TextureFilterType.LichtenbergFigure -> create(
                0.008f, 0.72f, 0.66f, 0.84f, 0.90f, 1.60f,
                0xFF08030C.toInt(), 0xFF5E176E.toInt(), 0xFFE544A7.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.Quasicrystal -> create(
                0.008f, 0.70f, 0.36f, 0.76f, 0.62f, 1.45f,
                0xFF090B18.toInt(), 0xFF2854A0.toInt(), 0xFF27BEBB.toInt(), 0xFFFFD962.toInt()
            )

            TextureFilterType.Mandelbrot -> create(
                0.004f, 0.68f, 0.42f, 0.78f, 0.72f, 1.50f,
                0xFF02030B.toInt(), 0xFF173B72.toInt(), 0xFF7B2EA3.toInt(), 0xFFFFC85C.toInt()
            )

            TextureFilterType.BurningShip -> create(
                0.0035f, 0.72f, 0.48f, 0.80f, 0.78f, 1.55f,
                0xFF050207.toInt(), 0xFF5A1728.toInt(), 0xFFE54522.toInt(), 0xFFFFD56A.toInt()
            )

            TextureFilterType.JuliaSet -> create(
                0.008f, 0.68f, 0.52f, 0.78f, 0.76f, 1.50f,
                0xFF030713.toInt(), 0xFF214E96.toInt(), 0xFF8B46C7.toInt(), 0xFFFFE2A8.toInt()
            )

            TextureFilterType.KaleidoscopeCrystal -> create(
                0.008f, 0.72f, 0.46f, 0.76f, 0.72f, 1.45f,
                0xFF090B1C.toInt(), 0xFF2367B7.toInt(), 0xFFE04DA4.toInt(), 0xFFFFDF70.toInt()
            )

            TextureFilterType.SpectralPrism -> create(
                0.008f, 0.64f, 0.58f, 0.80f, 0.80f, 1.45f,
                0xFF080A12.toInt(), 0xFF225CD1.toInt(), 0xFFDD3DAD.toInt(), 0xFFFFE46A.toInt()
            )

            TextureFilterType.TopologicalKnot -> create(
                0.008f, 0.62f, 0.54f, 0.82f, 0.76f, 1.55f,
                0xFF040713.toInt(), 0xFF24579A.toInt(), 0xFFC245D6.toInt(), 0xFFFFE29A.toInt()
            )

            TextureFilterType.XRayBotanical -> create(
                0.008f, 0.66f, 0.48f, 0.76f, 0.74f, 1.45f,
                0xFF020A0B.toInt(), 0xFF0C4F50.toInt(), 0xFF5CC7A7.toInt(), 0xFFE8FFE0.toInt()
            )

            TextureFilterType.Chromatophore -> create(
                0.008f, 0.68f, 0.52f, 0.72f, 0.70f, 1.40f,
                0xFF160918.toInt(), 0xFF6A275F.toInt(), 0xFFE2576F.toInt(), 0xFFFFD45E.toInt()
            )

            TextureFilterType.BiomechanicalTissue -> create(
                0.008f, 0.66f, 0.58f, 0.78f, 0.62f, 1.50f,
                0xFF100C12.toInt(), 0xFF553444.toInt(), 0xFFB97472.toInt(), 0xFFF0CAB0.toInt()
            )

            TextureFilterType.GildedFiligree -> create(
                0.008f, 0.68f, 0.46f, 0.82f, 0.74f, 1.55f,
                0xFF0A1019.toInt(), 0xFF163B58.toInt(), 0xFFC38A2A.toInt(), 0xFFFFE49A.toInt()
            )

            TextureFilterType.AncientRunes -> create(
                0.008f, 0.62f, 0.54f, 0.78f, 0.72f, 1.50f,
                0xFF0B0C12.toInt(), 0xFF34364A.toInt(), 0xFFA85F35.toInt(), 0xFFFFD27A.toInt()
            )

            TextureFilterType.SolarGranulation -> create(
                0.008f, 0.72f, 0.58f, 0.74f, 0.86f, 1.50f,
                0xFF1C0502.toInt(), 0xFF8B2108.toInt(), 0xFFF06A12.toInt(), 0xFFFFF0A0.toInt()
            )

            TextureFilterType.LunarEjecta -> create(
                0.008f, 0.64f, 0.48f, 0.80f, 0.50f, 1.50f,
                0xFF0B0D11.toInt(), 0xFF353A42.toInt(), 0xFF8B9098.toInt(), 0xFFE6E1D5.toInt()
            )

            TextureFilterType.OceanCurrents -> create(
                0.007f, 0.70f, 0.72f, 0.76f, 0.68f, 1.45f,
                0xFF031629.toInt(), 0xFF075B8B.toInt(), 0xFF21B9C2.toInt(), 0xFFD9FFF5.toInt()
            )

            TextureFilterType.InkWashMountains -> create(
                0.006f, 0.62f, 0.58f, 0.72f, 0.42f, 1.40f,
                0xFFF1EBDD.toInt(), 0xFF9A9A91.toInt(), 0xFF444844.toInt(), 0xFF111716.toInt()
            )

            TextureFilterType.NeonCity -> create(
                0.008f, 0.68f, 0.52f, 0.80f, 0.86f, 1.55f,
                0xFF02040C.toInt(), 0xFF142D59.toInt(), 0xFFE638A1.toInt(), 0xFF57F5E9.toInt()
            )

            TextureFilterType.PhyllotaxisBloom -> create(
                0.008f, 0.68f, 0.62f, 0.82f, 0.72f, 1.50f,
                0xFF07100D.toInt(), 0xFF1F6F4A.toInt(), 0xFFE0A733.toInt(), 0xFFFFF0A0.toInt()
            )

            TextureFilterType.SierpinskiTriangle -> create(
                0.008f, 0.68f, 0.50f, 0.82f, 0.66f, 1.55f,
                0xFF050713.toInt(), 0xFF244B9A.toInt(), 0xFF46BFD0.toInt(), 0xFFFFE49A.toInt()
            )

            TextureFilterType.ApollonianGasket -> create(
                0.008f, 0.66f, 0.52f, 0.80f, 0.72f, 1.50f,
                0xFF080511.toInt(), 0xFF542778.toInt(), 0xFFD34EAD.toInt(), 0xFFFFE486.toInt()
            )

            TextureFilterType.HyperbolicTiling -> create(
                0.008f, 0.62f, 0.72f, 0.80f, 0.74f, 1.50f,
                0xFF050A14.toInt(), 0xFF2454A0.toInt(), 0xFF30B9B0.toInt(), 0xFFFFD86A.toInt()
            )

            TextureFilterType.MoebiusWeave -> create(
                0.008f, 0.66f, 0.62f, 0.76f, 0.68f, 1.45f,
                0xFF080A16.toInt(), 0xFF374A94.toInt(), 0xFFB34EC2.toInt(), 0xFFF5D77C.toInt()
            )

            TextureFilterType.RorschachInkblot -> create(
                0.006f, 0.70f, 0.82f, 0.68f, 0.52f, 1.45f,
                0xFFF1EBDD.toInt(), 0xFF8B8590.toInt(), 0xFF3D283E.toInt(), 0xFF100C14.toInt()
            )

            TextureFilterType.SeismicInterference -> create(
                0.008f, 0.64f, 0.52f, 0.78f, 0.72f, 1.50f,
                0xFF070A12.toInt(), 0xFF315B8D.toInt(), 0xFFE36A42.toInt(), 0xFFFFE39A.toInt()
            )

            TextureFilterType.RayleighBenard -> create(
                0.008f, 0.70f, 0.62f, 0.72f, 0.82f, 1.50f,
                0xFF180603.toInt(), 0xFF862208.toInt(), 0xFFE76A16.toInt(), 0xFFFFEDA0.toInt()
            )

            TextureFilterType.OrigamiFacets -> create(
                0.008f, 0.66f, 0.52f, 0.82f, 0.62f, 1.45f,
                0xFF10121C.toInt(), 0xFF58678B.toInt(), 0xFFB9C3D5.toInt(), 0xFFFFE4A8.toInt()
            )

            TextureFilterType.FiberOpticBundle -> create(
                0.008f, 0.72f, 0.62f, 0.82f, 0.88f, 1.55f,
                0xFF02040D.toInt(), 0xFF183C88.toInt(), 0xFF36D7E1.toInt(), 0xFFFFFFFF.toInt()
            )

            else -> error("Unsupported fast-noise texture type: $type")
        }

        private fun create(
            scale: Float,
            value1: Float,
            value2: Float,
            value3: Float,
            value4: Float,
            value5: Float,
            vararg colors: Int
        ) = FastNoiseTextureParams(
            seed = 1337,
            scale = scale,
            values = listOf(value1, value2, value3, value4, value5),
            colors = colors.map(::ColorModel)
        )
    }
}
