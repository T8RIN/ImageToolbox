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