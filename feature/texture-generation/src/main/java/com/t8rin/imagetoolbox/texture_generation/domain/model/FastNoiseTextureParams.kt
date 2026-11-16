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
                0.0035f, 10f, 0.065f, 0.32f, 4f, 1.1f,
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
                0.008f, 0.78f, 0.42f, 0.82f, 0.58f, 1.45f,
                0xFF120E20.toInt(), 0xFF7046A8.toInt(), 0xFF3ED7D1.toInt(), 0xFFF4DDA6.toInt()
            )

            TextureFilterType.PrismaticLight -> create(
                0.008f, 0.72f, 0.82f, 0.84f, 0.72f, 1.45f,
                0xFF07091A.toInt(), 0xFF246DD8.toInt(), 0xFFE94DA9.toInt(), 0xFFFFE47A.toInt()
            )

            TextureFilterType.StainedGlass -> create(
                0.008f, 0.54f, 0.48f, 0.84f, 0.38f, 1.35f,
                0xFF120F18.toInt(), 0xFF2E8BD2.toInt(), 0xFFE54472.toInt(), 0xFFFFD34E.toInt()
            )

            TextureFilterType.KelpForest -> create(
                0.006f, 0.72f, 0.78f, 0.68f, 0.64f, 1.45f,
                0xFF031C22.toInt(), 0xFF07594F.toInt(), 0xFF42A66E.toInt(), 0xFFD1F29B.toInt()
            )

            TextureFilterType.FrostFern -> create(
                0.008f, 0.78f, 0.62f, 0.82f, 0.68f, 1.45f,
                0xFF10273A.toInt(), 0xFF559AB4.toInt(), 0xFFB8E8ED.toInt(), 0xFFE8FBFF.toInt()
            )

            TextureFilterType.LiquidCrystal -> create(
                0.008f, 0.66f, 0.62f, 0.58f, 0.72f, 1.30f,
                0xFF090A18.toInt(), 0xFF18BFD2.toInt(), 0xFFE142A9.toInt(), 0xFFFFE96A.toInt()
            )

            TextureFilterType.DragonScales -> create(
                0.007f, 0.66f, 0.28f, 0.82f, 0.52f, 1.45f,
                0xFF071913.toInt(), 0xFF17664B.toInt(), 0xFF39B778.toInt(), 0xFFE0C96B.toInt()
            )

            TextureFilterType.FireflySwarm -> create(
                0.007f, 0.52f, 0.82f, 0.88f, 0.95f, 1.60f,
                0xFF020B12.toInt(), 0xFF0B4D49.toInt(), 0xFF80D85A.toInt(), 0xFFFFFFB0.toInt()
            )

            TextureFilterType.Mycelium -> create(
                0.008f, 0.78f, 0.72f, 0.80f, 0.66f, 1.55f,
                0xFF07100B.toInt(), 0xFF24452E.toInt(), 0xFFB7C985.toInt(), 0xFFF4E8C1.toInt()
            )

            TextureFilterType.Kintsugi -> create(
                0.008f, 0.60f, 0.48f, 0.82f, 0.70f, 1.35f,
                0xFF10131A.toInt(), 0xFF344158.toInt(), 0xFFD99B32.toInt(), 0xFFFFE7A3.toInt()
            )

            TextureFilterType.CarbonFiber -> create(
                0.010f, 0.72f, 0.30f, 0.82f, 0.34f, 1.65f,
                0xFF030405.toInt(), 0xFF15191C.toInt(), 0xFF343B40.toInt(), 0xFF88949B.toInt()
            )

            TextureFilterType.CircuitBoard -> create(
                0.008f, 0.72f, 0.32f, 0.86f, 0.60f, 1.55f,
                0xFF03170F.toInt(), 0xFF075A3B.toInt(), 0xFFC28B28.toInt(), 0xFF7BFFD1.toInt()
            )

            TextureFilterType.SoapFilm -> create(
                0.007f, 0.68f, 0.72f, 0.55f, 0.72f, 1.25f,
                0xFF10182B.toInt(), 0xFF20CDE2.toInt(), 0xFFF45CB4.toInt(), 0xFFFFE66D.toInt()
            )

            TextureFilterType.MoireGuilloche -> create(
                0.008f, 0.64f, 0.40f, 0.82f, 0.38f, 1.65f,
                0xFF070914.toInt(), 0xFF2747A5.toInt(), 0xFFBD4BC8.toInt(), 0xFFF2EAFB.toInt()
            )

            TextureFilterType.SnakeSkin -> create(
                0.009f, 0.62f, 0.40f, 0.72f, 0.40f, 1.35f,
                0xFF17170E.toInt(), 0xFF4E5A2D.toInt(), 0xFF9DA75A.toInt(), 0xFFB8D7A4.toInt()
            )

            TextureFilterType.Terrazzo -> create(
                0.008f, 0.48f, 0.42f, 0.78f, 0.20f, 1.35f,
                0xFFE5DCCF.toInt(), 0xFF315C72.toInt(), 0xFFC85554.toInt(), 0xFFE5A73D.toInt()
            )

            TextureFilterType.GalaxyFilaments -> create(
                0.007f, 0.78f, 0.70f, 0.84f, 0.72f, 1.65f,
                0xFF02030A.toInt(), 0xFF173B78.toInt(), 0xFF8137A6.toInt(), 0xFFD9EEFF.toInt()
            )

            TextureFilterType.VolcanicObsidian -> create(
                0.008f, 0.72f, 0.58f, 0.84f, 0.52f, 1.70f,
                0xFF020304.toInt(), 0xFF17141D.toInt(), 0xFF3B2744.toInt(), 0xFFEF6C3D.toInt()
            )

            TextureFilterType.MotherboardHeatmap -> create(
                0.008f, 0.62f, 0.38f, 0.76f, 0.68f, 1.45f,
                0xFF07120E.toInt(), 0xFF08705B.toInt(), 0xFFFF7B22.toInt(), 0xFFFFF1A8.toInt()
            )

            TextureFilterType.MicroscopicDiatoms -> create(
                0.007f, 0.62f, 0.30f, 0.74f, 0.58f, 1.35f,
                0xFF082431.toInt(), 0xFF1E7181.toInt(), 0xFF74D8C9.toInt(), 0xFFD9C8FF.toInt()
            )

            TextureFilterType.ReactionDiffusion -> create(
                0.009f, 0.82f, 0.74f, 0.80f, 0.50f, 1.60f,
                0xFF090B17.toInt(), 0xFF303785.toInt(), 0xFFE64C93.toInt(), 0xFF59EBB0.toInt()
            )

            TextureFilterType.CoralGrowth -> create(
                0.008f, 0.68f, 0.62f, 0.70f, 0.72f, 1.40f,
                0xFF071D2A.toInt(), 0xFFEA536E.toInt(), 0xFFFFA468.toInt(), 0xFFFFC2D8.toInt()
            )

            TextureFilterType.SlimeMold -> create(
                0.008f, 0.80f, 0.82f, 0.84f, 0.66f, 1.65f,
                0xFF08110A.toInt(), 0xFF596F17.toInt(), 0xFFD5D928.toInt(), 0xFFFFF5A0.toInt()
            )

            TextureFilterType.DendriticCrystal -> create(
                0.008f, 0.68f, 0.52f, 0.80f, 0.82f, 1.50f,
                0xFF071829.toInt(), 0xFF25678C.toInt(), 0xFFA8E5F0.toInt(), 0xFFDFFFFF.toInt()
            )

            TextureFilterType.ElectricArcField -> create(
                0.008f, 0.72f, 0.72f, 0.82f, 0.90f, 1.55f,
                0xFF030511.toInt(), 0xFF173A91.toInt(), 0xFF32C8FF.toInt(), 0xFFFFFFFF.toInt()
            )

            TextureFilterType.CloudChamber -> create(
                0.008f, 0.55f, 0.70f, 0.86f, 0.70f, 1.60f,
                0xFF080913.toInt(), 0xFF2D416A.toInt(), 0xFF9BC5E7.toInt(), 0xFFEAF2FF.toInt()
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
                0.008f, 0.76f, 0.82f, 0.84f, 0.64f, 1.65f,
                0xFF06120D.toInt(), 0xFF11684A.toInt(), 0xFF6BD87A.toInt(), 0xFFB8FFBA.toInt()
            )

            TextureFilterType.MagneticField -> create(
                0.008f, 0.68f, 0.42f, 0.74f, 0.72f, 1.40f,
                0xFF070A17.toInt(), 0xFF2452A0.toInt(), 0xFFE23D70.toInt(), 0xFFFFE5A6.toInt()
            )

            TextureFilterType.RiverDelta -> create(
                0.006f, 0.76f, 0.86f, 0.84f, 0.48f, 1.65f,
                0xFF25190E.toInt(), 0xFF8B693B.toInt(), 0xFF1688A5.toInt(), 0xFF8EDDE0.toInt()
            )

            TextureFilterType.LichenColony -> create(
                0.009f, 0.64f, 0.58f, 0.70f, 0.48f, 1.35f,
                0xFF29291A.toInt(), 0xFF66743A.toInt(), 0xFFB1B85B.toInt(), 0xFFC7D9A7.toInt()
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
                0.008f, 0.76f, 0.28f, 0.88f, 0.36f, 1.70f,
                0xFF06080B.toInt(), 0xFF2E343A.toInt(), 0xFF8E9AA3.toInt(), 0xFFF1D78A.toInt()
            )

            TextureFilterType.CymaticRosette -> create(
                0.001f, 0.68f, 0.44f, 0.78f, 0.76f, 1.45f,
                0xFF071426.toInt(), 0xFF176FB0.toInt(), 0xFF55D3DE.toInt(), 0xFFFF6500.toInt()
            )

            TextureFilterType.LichtenbergFigure -> create(
                0.008f, 0.72f, 0.66f, 0.84f, 0.90f, 1.60f,
                0xFF08030C.toInt(), 0xFF5E176E.toInt(), 0xFFE544A7.toInt(), 0xFFFFE9FF.toInt()
            )

            TextureFilterType.Quasicrystal -> create(
                0.008f, 0.70f, 0.36f, 0.76f, 0.62f, 1.45f,
                0xFF090B18.toInt(), 0xFF2854A0.toInt(), 0xFF27BEBB.toInt(), 0xFFFFD962.toInt()
            )

            TextureFilterType.Mandelbrot -> create(
                0.004f, 0.82f, 0.36f, 0.86f, 0.58f, 1.70f,
                0xFF02030B.toInt(), 0xFF173B72.toInt(), 0xFF7B2EA3.toInt(), 0xFFFFC85C.toInt()
            )

            TextureFilterType.BurningShip -> create(
                0.0035f, 0.82f, 0.44f, 0.86f, 0.62f, 1.70f,
                0xFF050207.toInt(), 0xFF5A1728.toInt(), 0xFFE54522.toInt(), 0xFFFFD56A.toInt()
            )

            TextureFilterType.JuliaSet -> create(
                0.004f, 0.82f, 0.58f, 0.86f, 0.60f, 1.70f,
                0xFF030713.toInt(), 0xFF214E96.toInt(), 0xFF8B46C7.toInt(), 0xFFFFE2A8.toInt()
            )

            TextureFilterType.KaleidoscopeCrystal -> create(
                0.008f, 0.80f, 0.58f, 0.84f, 0.56f, 1.60f,
                0xFF090B1C.toInt(), 0xFF2367B7.toInt(), 0xFFE04DA4.toInt(), 0xFF62F5CA.toInt()
            )

            TextureFilterType.SpectralPrism -> create(
                0.008f, 0.64f, 0.58f, 0.80f, 0.80f, 1.45f,
                0xFF080A12.toInt(), 0xFF225CD1.toInt(), 0xFFDD3DAD.toInt(), 0xFF72F5B3.toInt()
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
                0xFF160918.toInt(), 0xFF6A275F.toInt(), 0xFFE2576F.toInt(), 0xFF5CF277.toInt()
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
                0xFF07100D.toInt(), 0xFF1F6F4A.toInt(), 0xFFE0A733.toInt(), 0xFFFFD36A.toInt()
            )

            TextureFilterType.SierpinskiTriangle -> create(
                0.008f, 0.68f, 0.50f, 0.82f, 0.66f, 1.55f,
                0xFF050713.toInt(), 0xFF244B9A.toInt(), 0xFF46BFD0.toInt(), 0xFFFFE49A.toInt()
            )

            TextureFilterType.ApollonianGasket -> create(
                0.008f, 0.66f, 0.52f, 0.80f, 0.72f, 1.50f,
                0xFF080511.toInt(), 0xFF542778.toInt(), 0xFFD34EAD.toInt(), 0xFF75E8FF.toInt()
            )

            TextureFilterType.HyperbolicTiling -> create(
                0.008f, 0.62f, 0.72f, 0.80f, 0.74f, 1.50f,
                0xFF050A14.toInt(), 0xFF2454A0.toInt(), 0xFF30B9B0.toInt(), 0xFFFF7A8A.toInt()
            )

            TextureFilterType.MoebiusWeave -> create(
                0.008f, 0.66f, 0.62f, 0.76f, 0.68f, 1.45f,
                0xFF080A16.toInt(), 0xFF374A94.toInt(), 0xFFB34EC2.toInt(), 0xFF8FE3FF.toInt()
            )

            TextureFilterType.RorschachInkblot -> create(
                0.006f, 0.64f, 0.92f, 0.74f, 0.68f, 1.65f,
                0xFFF1EBDD.toInt(), 0xFF8B8590.toInt(), 0xFF3D283E.toInt(), 0xFF100C14.toInt()
            )

            TextureFilterType.SeismicInterference -> create(
                0.008f, 0.64f, 0.52f, 0.78f, 0.72f, 1.50f,
                0xFF070A12.toInt(), 0xFF315B8D.toInt(), 0xFFE36A42.toInt(), 0xFFE7F2FF.toInt()
            )

            TextureFilterType.RayleighBenard -> create(
                0.008f, 0.70f, 0.62f, 0.72f, 0.82f, 1.50f,
                0xFF180603.toInt(), 0xFF862208.toInt(), 0xFFE76A16.toInt(), 0xFFFFEDA0.toInt()
            )

            TextureFilterType.OrigamiFacets -> create(
                0.008f, 0.72f, 0.68f, 0.88f, 0.42f, 1.55f,
                0xFF10121C.toInt(), 0xFF58678B.toInt(), 0xFFB9C3D5.toInt(), 0xFFF08470.toInt()
            )

            TextureFilterType.FiberOpticBundle -> create(
                0.008f, 0.72f, 0.62f, 0.82f, 0.88f, 1.55f,
                0xFF02040D.toInt(), 0xFF183C88.toInt(), 0xFF36D7E1.toInt(), 0xFFE8FFFF.toInt()
            )

            TextureFilterType.KnittedFabric -> create(
                0.014f, 0.65f, 0.65f, 0.4f, 0.5f, 0.4f,
                0xFF21151D.toInt(), 0xFFA04E67.toInt(), 0xFFDC8B8A.toInt(), 0xFFFFE8D2.toInt()
            )

            TextureFilterType.WickerWeave -> create(
                0.014f, 0.8f, 0.6f, 0.3f, 0.5f, 0.5f,
                0xFF271B13.toInt(), 0xFF936435.toInt(), 0xFFC79954.toInt(), 0xFFF3DFAB.toInt()
            )

            TextureFilterType.Chainmail -> create(
                0.014f, 0.65f, 0.45f, 0.35f, 0.7f, 0.65f,
                0xFF080E18.toInt(), 0xFF4A6079.toInt(), 0xFFC89145.toInt(), 0xFFE9F1FC.toInt()
            )

            TextureFilterType.HerringboneParquet -> create(
                0.014f, 0.4f, 0.3f, 0.6f, 0.5f, 0.55f,
                0xFF21160F.toInt(), 0xFF794020.toInt(), 0xFFC58A45.toInt(), 0xFFF5DC9C.toInt()
            )

            TextureFilterType.RoofTiles -> create(
                0.02f, 0.6f, 0.25f, 0.8f, 0.5f, 0.6f,
                0xFF32150F.toInt(), 0xFFA34125.toInt(), 0xFFD57A43.toInt(), 0xFFF5BD87.toInt()
            )

            TextureFilterType.HammeredMetal -> create(
                0.028f, 0.7f, 0.8f, 0.8f, 0.25f, 0.3f,
                0xFF28140E.toInt(), 0xFFB66634.toInt(), 0xFF367E78.toInt(), 0xFFFFE2A6.toInt()
            )

            TextureFilterType.CorrugatedMetal -> create(
                0.018f, 0.5f, 0.65f, 0.8f, 0.25f, 0.2f,
                0xFF121D29.toInt(), 0xFF6B8297.toInt(), 0xFFA76032.toInt(), 0xFFECF6FC.toInt()
            )

            TextureFilterType.BubbleWrap -> create(
                0.02f, 0.7f, 0.7f, 0.75f, 0.45f, 0.8f,
                0xFF15364F.toInt(), 0xFF398AA6.toInt(), 0xFFA3DBE3.toInt(), 0xFFF0FFFF.toInt()
            )

            TextureFilterType.PenroseTiling -> create(
                0.018f, 0.0f, 0.22f, 0.3f, 0.4f, 0.4f,
                0xFF101E2C.toInt(), 0xFF247F91.toInt(), 0xFFD7A448.toInt(), 0xFFFFF0C8.toInt()
            )

            TextureFilterType.CairoTiling -> create(
                0.026f, 0.0f, 0.25f, 0.75f, 0.3f, 0.25f,
                0xFF172637.toInt(), 0xFF45778A.toInt(), 0xFFE3AE73.toInt(), 0xFFFFF1D6.toInt()
            )

            TextureFilterType.IslamicStarTiling -> create(
                0.012f, 0.6f, 0.5f, 0.45f, 0.3f, 0.35f,
                0xFF0C2336.toInt(), 0xFF146F88.toInt(), 0xFFC0964A.toInt(), 0xFFFFF0C8.toInt()
            )

            TextureFilterType.CelticKnotwork -> create(
                0.012f, 0.5f, 0.5f, 0.4f, 0.55f, 0.4f,
                0xFF0B2526.toInt(), 0xFF287666.toInt(), 0xFFC39140.toInt(), 0xFFFFE4A1.toInt()
            )

            TextureFilterType.Seigaiha -> create(
                0.014f, 0.5f, 0.3f, 0.5f, 0.55f, 0.3f,
                0xFF092340.toInt(), 0xFF1A658B.toInt(), 0xFF42A6BA.toInt(), 0xFFE6F3DA.toInt()
            )

            TextureFilterType.Asanoha -> create(
                0.012f, 0.0f, 0.3f, 0.4f, 0.25f, 0.2f,
                0xFF23182D.toInt(), 0xFF5F3969.toInt(), 0xFFCB8F55.toInt(), 0xFFFFE7BD.toInt()
            )

            TextureFilterType.Houndstooth -> create(
                0.008f, 0.5f, 0.5f, 0.55f, 0.3f, 0.25f,
                0xFF111923.toInt(), 0xFF435467.toInt(), 0xFFD9D3BF.toInt(), 0xFFFFF7DC.toInt()
            )

            TextureFilterType.Jigsaw -> create(
                0.01f, 0.6f, 0.5f, 0.6f, 0.4f, 0.5f,
                0xFF18243A.toInt(), 0xFF4589A4.toInt(), 0xFFE6AD72.toInt(), 0xFFFFF0CD.toInt()
            )

            TextureFilterType.TreeBark -> create(
                0.016f, 0.7f, 0.4f, 0.65f, 0.5f, 0.35f,
                0xFF221B16.toInt(), 0xFF796043.toInt(), 0xFF647647.toInt(), 0xFFC9B98B.toInt()
            )

            TextureFilterType.PineCone -> create(
                0.018f, 0.7f, 0.7f, 0.35f, 0.45f, 0.4f,
                0xFF24150E.toInt(), 0xFF865130.toInt(), 0xFFB78547.toInt(), 0xFFE8C994.toInt()
            )

            TextureFilterType.ZebraStripes -> create(
                0.01f, 0.5f, 0.55f, 0.4f, 0.45f, 0.5f,
                0xFF111721.toInt(), 0xFF3F4851.toInt(), 0xFFDBDCCB.toInt(), 0xFFFFF9E7.toInt()
            )

            TextureFilterType.LeopardSpots -> create(
                0.013f, 0.7f, 0.65f, 0.4f, 0.45f, 0.5f,
                0xFF211A16.toInt(), 0xFFC99344.toInt(), 0xFF8E5528.toInt(), 0xFFF4DB93.toInt()
            )

            TextureFilterType.LissajousRibbons -> create(
                0.012f, 3.0f, 4.0f, 0.12f, 0.45f, 0.65f,
                0xFF080E21.toInt(), 0xFF1CC6D0.toInt(), 0xFFEC589B.toInt(), 0xFFFFE594.toInt()
            )

            TextureFilterType.BismuthCrystal -> create(
                0.011f, 0.5f, 0.55f, 0.6f, 0.75f, 0.65f,
                0xFF211329.toInt(), 0xFF5B54B7.toInt(), 0xFF23B8B8.toInt(), 0xFFFFCD70.toInt()
            )

            TextureFilterType.HilbertRibbons -> create(
                0.01f, 4.0f, 0.65f, 0.6f, 0.0f, 0.7f,
                0xFF0C1429.toInt(), 0xFF278BD2.toInt(), 0xFFD747AB.toInt(), 0xFFFFD878.toInt()
            )

            TextureFilterType.Malachite -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF071F18.toInt(), 0xFF17613E.toInt(), 0xFF56AE70.toInt(), 0xFFD5F0B4.toInt()
            )

            TextureFilterType.Labradorite -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF101B27.toInt(), 0xFF273E54.toInt(), 0xFF338BBA.toInt(), 0xFFEDD188.toInt()
            )

            TextureFilterType.Seraphinite -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF071E1B.toInt(), 0xFF245541.toInt(), 0xFF9EBEAB.toInt(), 0xFFE7F2D4.toInt()
            )

            TextureFilterType.PorcelainCrackle -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF1D4157.toInt(), 0xFFD8E4DE.toInt(), 0xFF9FBFCA.toInt(), 0xFFFFF6DD.toInt()
            )

            TextureFilterType.RakuGlaze -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF171916.toInt(), 0xFF92553E.toInt(), 0xFF508D8A.toInt(), 0xFFEAD19C.toInt()
            )

            TextureFilterType.MotherOfPearl -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF172F3B.toInt(), 0xFF95CBCB.toInt(), 0xFFD7AECC.toInt(), 0xFFFFF4D8.toInt()
            )

            TextureFilterType.TemperedTitanium -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF10202D.toInt(), 0xFF6C66AD.toInt(), 0xFFB98839.toInt(), 0xFFD5F5EF.toInt()
            )

            TextureFilterType.EndGrainMosaic -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF25160F.toInt(), 0xFF81512A.toInt(), 0xFFB88A51.toInt(), 0xFFEAD7A1.toInt()
            )

            TextureFilterType.Cork -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF322114.toInt(), 0xFF967448.toInt(), 0xFFC6A274.toInt(), 0xFFEDD4AA.toInt()
            )

            TextureFilterType.CutCrystalGlass -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0A1D2D.toInt(), 0xFF3D7792.toInt(), 0xFFA6CED6.toInt(), 0xFFF7F5DA.toInt()
            )

            TextureFilterType.Sashiko -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0B1C37.toInt(), 0xFF244768.toInt(), 0xFFC6D5D7.toInt(), 0xFFF2DBB0.toInt()
            )

            TextureFilterType.Bargello -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF25213A.toInt(), 0xFFCE6473.toInt(), 0xFF3A8C9A.toInt(), 0xFFF2D998.toInt()
            )

            TextureFilterType.Kilim -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF281D26.toInt(), 0xFFB94B39.toInt(), 0xFF2C7477.toInt(), 0xFFEDD3A2.toInt()
            )

            TextureFilterType.Brocade -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF271425.toInt(), 0xFF74404C.toInt(), 0xFFCAA45F.toInt(), 0xFFF8DE99.toInt()
            )

            TextureFilterType.Batik -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF102A3B.toInt(), 0xFF397D85.toInt(), 0xFF9EB68B.toInt(), 0xFFF4D69E.toInt()
            )

            TextureFilterType.Ikat -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF1C2641.toInt(), 0xFF546A9E.toInt(), 0xFFCC7D68.toInt(), 0xFFF1DAB7.toInt()
            )

            TextureFilterType.EmbossedPaisley -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF201A30.toInt(), 0xFF73506F.toInt(), 0xFFC29863.toInt(), 0xFFF2D5A1.toInt()
            )

            TextureFilterType.ArtDecoFans -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF151F2A.toInt(), 0xFF37515A.toInt(), 0xFFAE8551.toInt(), 0xFFF1D7A1.toInt()
            )

            TextureFilterType.CloverLattice -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFFE8DDC9.toInt(), 0xFF386C79.toInt(), 0xFF9E656A.toInt(), 0xFFE7B96B.toInt()
            )

            TextureFilterType.GuillocheRosettes -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF101D38.toInt(), 0xFF258E9B.toInt(), 0xFFCC739F.toInt(), 0xFFF2DAA5.toInt()
            )

            TextureFilterType.GinkgoLeaves -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF17292D.toInt(), 0xFF6B7350.toInt(), 0xFFCAA44D.toInt(), 0xFFF4DD91.toInt()
            )

            TextureFilterType.MonsteraCanopy -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0A241F.toInt(), 0xFF1C5741.toInt(), 0xFF6C9B61.toInt(), 0xFFB8CE8F.toInt()
            )

            TextureFilterType.LotusPond -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF082B36.toInt(), 0xFF337765.toInt(), 0xFFC18C9D.toInt(), 0xFFE7DDAC.toInt()
            )

            TextureFilterType.SeaAnemone -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF071B30.toInt(), 0xFF286E83.toInt(), 0xFFCE7DAD.toInt(), 0xFFF2DAB6.toInt()
            )

            TextureFilterType.Jellyfish -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF051C32.toInt(), 0xFF2D7390.toInt(), 0xFFA98AD6.toInt(), 0xFFDDFAEC.toInt()
            )

            TextureFilterType.ButterflyWings -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF1B1B27.toInt(), 0xFFBB622C.toInt(), 0xFF477E9B.toInt(), 0xFFF3D69B.toInt()
            )

            TextureFilterType.FungalGills -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF241B29.toInt(), 0xFF785C72.toInt(), 0xFFBD9A8B.toInt(), 0xFFEFDABD.toInt()
            )

            TextureFilterType.Radiolaria -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF092634.toInt(), 0xFF28778B.toInt(), 0xFF8FCAC3.toInt(), 0xFFE7F1C9.toInt()
            )

            TextureFilterType.SlotCanyon -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF261B2A.toInt(), 0xFF864332.toInt(), 0xFFCE8B5A.toInt(), 0xFFF0D5A1.toInt()
            )

            TextureFilterType.GlacialCrevasses -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0A2942.toInt(), 0xFF2A718F.toInt(), 0xFF83BDD0.toInt(), 0xFFE4F0DF.toInt()
            )

            TextureFilterType.SpiralPearls -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF101C31.toInt(), 0xFF41879E.toInt(), 0xFFC279A0.toInt(), 0xFFF3DBAD.toInt()
            )

            TextureFilterType.HopfFibration -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0A1930.toInt(), 0xFF338DA5.toInt(), 0xFFD77B9D.toInt(), 0xFFEED293.toInt()
            )

            TextureFilterType.Spirograph -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF111A30.toInt(), 0xFF339DAD.toInt(), 0xFFDA7FA9.toInt(), 0xFFF4D48D.toInt()
            )

            TextureFilterType.ChiralPinwheels -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF132F3D.toInt(), 0xFF519399.toInt(), 0xFFC88370.toInt(), 0xFFF2D49E.toInt()
            )

            TextureFilterType.TruchetBlocks -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF112330.toInt(), 0xFF2F6679.toInt(), 0xFFCA9764.toInt(), 0xFFE6D8B4.toInt()
            )

            TextureFilterType.ImpossibleStairs -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF152B3B.toInt(), 0xFF507E91.toInt(), 0xFFC08C79.toInt(), 0xFFEADAB6.toInt()
            )

            TextureFilterType.WovenHexagons -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF132E35.toInt(), 0xFF2D7580.toInt(), 0xFFD19B61.toInt(), 0xFFF3DBA7.toInt()
            )

            TextureFilterType.Gyroid -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF102336.toInt(), 0xFF498C9F.toInt(), 0xFFB7839D.toInt(), 0xFFEDDCB0.toInt()
            )

            TextureFilterType.Hypercube -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF081B30.toInt(), 0xFF2C8AA4.toInt(), 0xFFD08BAB.toInt(), 0xFFF1D797.toInt()
            )

            TextureFilterType.LenticularWaves -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF162F49.toInt(), 0xFF4A96A8.toInt(), 0xFFD78E8D.toInt(), 0xFFF2DBAA.toInt()
            )

            TextureFilterType.StellarNursery -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF081627.toInt(), 0xFF34536A.toInt(), 0xFFC0836B.toInt(), 0xFFF6DBA9.toInt()
            )

            TextureFilterType.PulsarJets -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF07142B.toInt(), 0xFF336CB0.toInt(), 0xFFC078AD.toInt(), 0xFFF2E5B4.toInt()
            )

            TextureFilterType.GravitationalLensing -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF081528.toInt(), 0xFF3A7092.toInt(), 0xFFC7A374.toInt(), 0xFFEDF3D2.toInt()
            )

            TextureFilterType.NacreClouds -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF263650.toInt(), 0xFF789AB0.toInt(), 0xFFCA9FBB.toInt(), 0xFFF2D6AC.toInt()
            )

            TextureFilterType.MercuryDroplets -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF121B29.toInt(), 0xFF293A50.toInt(), 0xFFB3C8D6.toInt(), 0xFFF4FBFF.toInt()
            )

            TextureFilterType.SonicBoom -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0B2340.toInt(), 0xFF307C9C.toInt(), 0xFFB67CAD.toInt(), 0xFFF1E0AA.toInt()
            )

            TextureFilterType.RibbonVortex -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0C1C34.toInt(), 0xFF368EA4.toInt(), 0xFFD47D9F.toInt(), 0xFFEFD397.toInt()
            )

            TextureFilterType.IridescentFoam -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF0B263B.toInt(), 0xFF4C869E.toInt(), 0xFFC08DB5.toInt(), 0xFFECE4BD.toInt()
            )

            TextureFilterType.LavaCrust -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF141B24.toInt(), 0xFF493C42.toInt(), 0xFFDB692D.toInt(), 0xFFFFE0A0.toInt()
            )

            TextureFilterType.AbyssalVent -> create(
                0.008f, 0.55f, 0.48f, 0.55f, 0.65f, 1.05f,
                0xFF061D2C.toInt(), 0xFF254D5E.toInt(), 0xFF9C8070.toInt(), 0xFFC6D6B7.toInt()
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
