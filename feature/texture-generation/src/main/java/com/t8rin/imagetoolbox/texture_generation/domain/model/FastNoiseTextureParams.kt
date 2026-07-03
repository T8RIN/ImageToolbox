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
                0.0039f, 11.4f, 0.75f, 15f, 7.42f, 0.17f,
                -12969717, -4624846, -14873337
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