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

@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.feature.ai_tools.domain.model

import com.t8rin.imagetoolbox.core.resources.R

data class NeuralModel(
    val downloadLink: String,
    val name: String,
    val title: String,
    val description: Int?,
    val type: Type?,
    val speed: Speed?
) {
    val supportsStrength = name.contains("fbcnn_", true)
    val isImported = downloadLink == "imported"
    val isNonChunkable = name.contains("ddcolor") || type == Type.REMOVE_BG

    enum class Type {
        UPSCALE, REMOVE_BG, COLORIZE, DE_JPEG, DENOISE, ARTIFACTS, ENHANCE, ANIME, SCANS
    }

    sealed interface Speed {
        val speedValue: Float

        fun clone(value: Float): Speed = when (this) {
            is Fast -> copy(speedValue = value)
            is Normal -> copy(speedValue = value)
            is Slow -> copy(speedValue = value)
            is VeryFast -> copy(speedValue = value)
            is VerySlow -> copy(speedValue = value)
        }

        data class VeryFast(override val speedValue: Float) : Speed
        data class Fast(override val speedValue: Float) : Speed
        data class Normal(override val speedValue: Float) : Speed
        data class Slow(override val speedValue: Float) : Speed
        data class VerySlow(override val speedValue: Float) : Speed

        companion object {
            val entries by lazy {
                listOf(
                    VeryFast(0f),
                    Fast(0f),
                    Normal(0f),
                    Slow(0f),
                    VerySlow(0f)
                )
            }
        }
    }

    companion object {
        val entries: List<NeuralModel> by lazy {
            listOf(
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/fbcnn/fbcnn_color_fp16.onnx",
                    name = "fbcnn_color_fp16.onnx",
                    title = "FBCNN Color",
                    description = R.string.model_fbcnn_color_fp16,
                    type = Type.DE_JPEG,
                    speed = Speed.Fast(2.003f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/fbcnn/fbcnn_grey_fp16.onnx",
                    name = "fbcnn_grey_fp16.onnx",
                    title = "FBCNN Grayscale",
                    description = R.string.model_fbcnn_gray_fp16,
                    type = Type.DE_JPEG,
                    speed = Speed.VeryFast(1.992f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/fbcnn/fbcnn_gray_double_fp16.onnx",
                    name = "fbcnn_gray_double_fp16.onnx",
                    title = "FBCNN Grayscale Strong",
                    description = R.string.model_fbcnn_gray_double_fp16,
                    type = Type.DE_JPEG,
                    speed = Speed.VeryFast(1.934f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color-GAN.onnx",
                    name = "scunet_color-GAN.onnx",
                    title = "SCUNet Color GAN",
                    description = R.string.model_scunet_color_gan_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(2.715f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color-PSNR.onnx",
                    name = "scunet_color-PSNR.onnx",
                    title = "SCUNet Color PSNR",
                    description = R.string.model_scunet_color_psnr_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(2.633f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_15_fp16.onnx",
                    name = "scunet_gray_15_fp16.onnx",
                    title = "SCUNet Grayscale 15",
                    description = R.string.model_scunet_gray_15_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(3.048f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_25_fp16.onnx",
                    name = "scunet_gray_25_fp16.onnx",
                    title = "SCUNet Grayscale 25",
                    description = R.string.model_scunet_gray_25_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(3.033f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_50_fp16.onnx",
                    name = "scunet_gray_50_fp16.onnx",
                    title = "SCUNet Grayscale 50",
                    description = R.string.model_scunet_gray_50_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(3.058f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_15_fp16.onnx",
                    name = "scunet_color_15_fp16.onnx",
                    title = "SCUNet Color 15",
                    description = R.string.model_scunet_color_15_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(7.095f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_25_fp16.onnx",
                    name = "scunet_color_25_fp16.onnx",
                    title = "SCUNet Color 25",
                    description = R.string.model_scunet_color_25_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Fast(6.994f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_50_fp16.onnx",
                    name = "scunet_color_50_fp16.onnx",
                    title = "SCUNet Color 50",
                    description = R.string.model_scunet_color_50_fp16,
                    type = Type.DENOISE,
                    speed = Speed.Normal(7.229f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-AnimeUndeint-Compact-fp16.onnx",
                    name = "1x-AnimeUndeint-Compact-fp16.onnx",
                    title = "Anime Undeint",
                    description = R.string.model_anime_undeint,
                    type = Type.ANIME,
                    speed = Speed.VeryFast(0.497f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-BroadcastToStudio_Compact-fp16.onnx",
                    name = "1x-BroadcastToStudio_Compact-fp16.onnx",
                    title = "Broadcast To Studio",
                    description = R.string.model_broadcast,
                    type = Type.ARTIFACTS,
                    speed = Speed.VeryFast(0.625f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-RGB-max-Denoise-fp16.onnx",
                    name = "1x-RGB-max-Denoise-fp16.onnx",
                    title = "RGB Max Denoise",
                    description = R.string.model_rgb_max_denoise_fp16,
                    type = Type.DENOISE,
                    speed = Speed.VeryFast(0.172f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-WB-Denoise-fp16.onnx",
                    name = "1x-WB-Denoise-fp16.onnx",
                    title = "WB Denoise",
                    description = R.string.model_wb_denoise,
                    type = Type.DENOISE,
                    speed = Speed.VeryFast(0.177f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-span-anime-pretrain-fp16.onnx",
                    name = "1x-span-anime-pretrain-fp16.onnx",
                    title = "SPAN Anime Pretrain",
                    description = R.string.model_span_anime_pretrain,
                    type = Type.ANIME,
                    speed = Speed.VeryFast(0.399f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1xBook-Compact-fp16.onnx",
                    name = "1xBook-Compact-fp16.onnx",
                    title = "Book Scan",
                    description = R.string.model_book_scan,
                    type = Type.SCANS,
                    speed = Speed.VeryFast(0.452f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1xOverExposureCorrection_compact-fp16.onnx",
                    name = "1xOverExposureCorrection_compact-fp16.onnx",
                    title = "Overexposure Correction",
                    description = R.string.model_overexposure,
                    type = Type.ENHANCE,
                    speed = Speed.VeryFast(0.492f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-Anti-Aliasing-fp16.onnx",
                    name = "1x-Anti-Aliasing-fp16.onnx",
                    title = "Anti-Aliasing",
                    description = R.string.model_antialias,
                    type = Type.ARTIFACTS,
                    speed = Speed.Slow(14.806f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ColorizerV2_22000G-fp16.onnx",
                    name = "1x_ColorizerV2_22000G-fp16.onnx",
                    title = "Colorizer",
                    description = R.string.model_colorizer,
                    type = Type.COLORIZE,
                    speed = Speed.Slow(15.125f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeSharpen-fp16.onnx",
                    name = "1x_DeSharpen-fp16.onnx",
                    title = "DeSharpen",
                    description = R.string.model_desharpen,
                    type = Type.ENHANCE,
                    speed = Speed.VerySlow(15.593f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeEdge-fp16.onnx",
                    name = "1x_DeEdge-fp16.onnx",
                    title = "DeEdge",
                    description = R.string.model_deedge,
                    type = Type.ARTIFACTS,
                    speed = Speed.Normal(14.099f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_GainresV4-fp16.onnx",
                    name = "1x_GainresV4-fp16.onnx",
                    title = "GainRes",
                    description = R.string.model_gainres,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(14.415f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v4.onnx",
                    name = "1x-DeBink-v4.onnx",
                    title = "DeBink v4",
                    description = R.string.model_debink_v4,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(12.947f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v5.onnx",
                    name = "1x-DeBink-v5.onnx",
                    title = "DeBink v5",
                    description = R.string.model_debink_v5,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(13.597f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v6.onnx",
                    name = "1x-DeBink-v6.onnx",
                    title = "DeBink v6",
                    description = R.string.model_debink_v6,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(13.789f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-KDM003-scans-fp16.onnx",
                    name = "1x-KDM003-scans-fp16.onnx",
                    title = "KDM003 Scans",
                    description = R.string.model_kdm003_scans,
                    type = Type.SCANS,
                    speed = Speed.Slow(14.948f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    name = "1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    title = "NMKD Jaywreck3 Lite",
                    description = R.string.model_nmkd_jaywreck3_lite,
                    type = Type.ENHANCE,
                    speed = Speed.Fast(4.896f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-SpongeColor-Lite-fp16.onnx",
                    name = "1x-SpongeColor-Lite-fp16.onnx",
                    title = "SpongeColor Lite",
                    description = R.string.model_spongecolor_lite,
                    type = Type.COLORIZE,
                    speed = Speed.Fast(4.731f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-cinepak-fp16.onnx",
                    name = "1x-cinepak-fp16.onnx",
                    title = "Cinepak",
                    description = R.string.model_cinepak,
                    type = Type.ARTIFACTS,
                    speed = Speed.VerySlow(15.515f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    name = "1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    title = "BCGone Detailed V2",
                    description = R.string.model_bcgone_detailed_v2,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(14.694f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_BCGone_Smooth_110000_G-fp16.onnx",
                    name = "1x_BCGone_Smooth_110000_G-fp16.onnx",
                    title = "BCGone Smooth",
                    description = R.string.model_bcgone_smooth,
                    type = Type.ENHANCE,
                    speed = Speed.VerySlow(15.44f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Bandage-Smooth-fp16.onnx",
                    name = "1x_Bandage-Smooth-fp16.onnx",
                    title = "Bandage Smooth",
                    description = R.string.model_bandage_smooth,
                    type = Type.ARTIFACTS,
                    speed = Speed.Slow(15.176f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Bendel_Halftone-fp32.onnx",
                    name = "1x_Bendel_Halftone-fp32.onnx",
                    title = "Bendel Halftone",
                    description = R.string.model_bendel_halftone,
                    type = Type.ARTIFACTS,
                    speed = Speed.Fast(4.019f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DitherDeleterV3-Smooth-fp16.onnx",
                    name = "1x_DitherDeleterV3-Smooth-fp16.onnx",
                    title = "Dither Deleter V3 Smooth",
                    description = R.string.model_dither_deleter_v3_smooth,
                    type = Type.ENHANCE,
                    speed = Speed.VerySlow(15.618f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    name = "1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    title = "JPEG Destroyer V2",
                    description = R.string.model_jpeg_destroyer_v2,
                    type = Type.DE_JPEG,
                    speed = Speed.Slow(14.9f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKD-h264Texturize-fp16.onnx",
                    name = "1x_NMKD-h264Texturize-fp16.onnx",
                    title = "NMKD H264 Texturize",
                    description = R.string.model_nmkd_h264_texturize,
                    type = Type.ARTIFACTS,
                    speed = Speed.Slow(14.886f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/VHS-Sharpen-1x_46000_G-fp16.onnx",
                    name = "VHS-Sharpen-1x_46000_G-fp16.onnx",
                    title = "VHS Sharpen",
                    description = R.string.model_vhs_sharpen,
                    type = Type.ENHANCE,
                    speed = Speed.Slow(15.192f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_dithering_alsa-fp16.onnx",
                    name = "1x_artifacts_dithering_alsa-fp16.onnx",
                    title = "Artifacts Dithering ALSA",
                    description = R.string.model_artifacts_dithering_alsa,
                    type = Type.ARTIFACTS,
                    speed = Speed.VerySlow(15.392f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKD-BrightenRedux_200k-fp16.onnx",
                    name = "1x_NMKD-BrightenRedux_200k-fp16.onnx",
                    title = "NMKD Brighten Redux",
                    description = R.string.model_nmkd_brighten_redux,
                    type = Type.ENHANCE,
                    speed = Speed.Slow(14.863f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_nmkdbrighten_10000_G-fp16.onnx",
                    name = "1x_nmkdbrighten_10000_G-fp16.onnx",
                    title = "NMKD Brighten",
                    description = R.string.model_nmkd_brighten,
                    type = Type.ENHANCE,
                    speed = Speed.Slow(14.803f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKDDetoon_97500_G-fp16.onnx",
                    name = "1x_NMKDDetoon_97500_G-fp16.onnx",
                    title = "NMKD Detoon",
                    description = R.string.model_nmkd_detoon,
                    type = Type.ENHANCE,
                    speed = Speed.Slow(14.817f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Poisson-Detailed_108000_G-fp16.onnx",
                    name = "1x_NoiseToner-Poisson-Detailed_108000_G-fp16.onnx",
                    title = "Noise Toner Poisson Detailed",
                    description = R.string.model_noise_toner_poisson_detailed,
                    type = Type.DENOISE,
                    speed = Speed.Normal(14.463f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Poisson-Soft_101000_G-fp16.onnx",
                    name = "1x_NoiseToner-Poisson-Soft_101000_G-fp16.onnx",
                    title = "Noise Toner Poisson Soft",
                    description = R.string.model_noise_toner_poisson_soft,
                    type = Type.DENOISE,
                    speed = Speed.Normal(14.606f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Uniform-Detailed_100000_G-fp16.onnx",
                    name = "1x_NoiseToner-Uniform-Detailed_100000_G-fp16.onnx",
                    title = "Noise Toner Uniform Detailed",
                    description = R.string.model_noise_toner_uniform_detailed,
                    type = Type.DENOISE,
                    speed = Speed.Normal(14.802f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Uniform-Soft_100000_G-fp16.onnx",
                    name = "1x_NoiseToner-Uniform-Soft_100000_G-fp16.onnx",
                    title = "Noise Toner Uniform Soft",
                    description = R.string.model_noise_toner_uniform_soft,
                    type = Type.DENOISE,
                    speed = Speed.Slow(15.001f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Repainter_20000_G-fp16.onnx",
                    name = "1x_Repainter_20000_G-fp16.onnx",
                    title = "Repainter",
                    description = R.string.model_repainter,
                    type = Type.ENHANCE,
                    speed = Speed.Normal(14.775f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-Debandurh-FS-Ultra-lite-fp16.onnx",
                    name = "1x-Debandurh-FS-Ultra-lite-fp16.onnx",
                    title = "Debandurh FS Ultra Lite",
                    description = R.string.model_debandurh_fs_ultra_lite,
                    type = Type.ENHANCE,
                    speed = Speed.VeryFast(0.741f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_00-20.ort",
                    name = "1x_JPEG_00-20.ort",
                    title = "JPEG 0-20",
                    description = R.string.model_jpeg_0_20,
                    type = Type.DE_JPEG,
                    speed = Speed.VerySlow(15.994f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_20-40.ort",
                    name = "1x_JPEG_20-40.ort",
                    title = "JPEG 20-40",
                    description = R.string.model_jpeg_20_40,
                    type = Type.DE_JPEG,
                    speed = Speed.VerySlow(15.754f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_40-60.ort",
                    name = "1x_JPEG_40-60.ort",
                    title = "JPEG 40-60",
                    description = R.string.model_jpeg_40_60,
                    type = Type.DE_JPEG,
                    speed = Speed.VerySlow(15.959f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_60-80.ort",
                    name = "1x_JPEG_60-80.ort",
                    title = "JPEG 60-80",
                    description = R.string.model_jpeg_60_80,
                    type = Type.DE_JPEG,
                    speed = Speed.Normal(14.307f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_80-100.ort",
                    name = "1x_JPEG_80-100.ort",
                    title = "JPEG 80-100",
                    description = R.string.model_jpeg_80_100,
                    type = Type.DE_JPEG,
                    speed = Speed.Slow(15.018f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeBLR.ort",
                    name = "1x_DeBLR.ort",
                    title = "DeBLR",
                    description = R.string.model_deblr,
                    type = Type.ENHANCE,
                    speed = Speed.VerySlow(16.079f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_00_20_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_00_20_alsa-fp16.ort",
                    title = "JPEG Artifacts 0-20",
                    description = R.string.model_artifacts_jpg_0_20,
                    type = Type.DE_JPEG,
                    speed = Speed.Slow(15.041f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_20_40_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_20_40_alsa-fp16.ort",
                    title = "JPEG Artifacts 20-40",
                    description = R.string.model_artifacts_jpg_20_40,
                    type = Type.DE_JPEG,
                    speed = Speed.Normal(14.733f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_40_60_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_40_60_alsa-fp16.ort",
                    title = "JPEG Artifacts 40-60",
                    description = R.string.model_artifacts_jpg_40_60,
                    type = Type.DE_JPEG,
                    speed = Speed.Slow(14.973f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_60_80_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_60_80_alsa-fp16.ort",
                    title = "JPEG Artifacts 60-80",
                    description = R.string.model_artifacts_jpg_60_80,
                    type = Type.DE_JPEG,
                    speed = Speed.Normal(14.572f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_80_100_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_80_100_alsa-fp16.ort",
                    title = "JPEG Artifacts 80-100",
                    description = R.string.model_artifacts_jpg_80_100,
                    type = Type.DE_JPEG,
                    speed = Speed.VerySlow(15.404f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ReDetail_v2_126000_G-fp16.ort",
                    name = "1x_ReDetail_v2_126000_G-fp16.ort",
                    title = "ReDetail v2",
                    description = R.string.model_redetail_v2,
                    type = Type.ENHANCE,
                    speed = Speed.Slow(15.265f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-ITF-SkinDiffDetail-Lite-v1.ort",
                    name = "1x-ITF-SkinDiffDetail-Lite-v1.ort",
                    title = "ITF Skin DiffDetail Lite",
                    description = R.string.model_itf_skin_diffdetail_lite,
                    type = Type.ENHANCE,
                    speed = Speed.Fast(5.138f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_SBDV-DeJPEG-Lite_130000_G.ort",
                    name = "1x_SBDV-DeJPEG-Lite_130000_G.ort",
                    title = "SBDV DeJPEG",
                    description = R.string.model_sbdv_dejpeg,
                    type = Type.DE_JPEG,
                    speed = Speed.Fast(5.158f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ISO_denoise_v1.ort",
                    name = "1x_ISO_denoise_v1.ort",
                    title = "ISO Denoise v1",
                    description = R.string.model_iso_denoise_v1,
                    type = Type.DENOISE,
                    speed = Speed.VerySlow(15.463f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeJumbo.ort",
                    name = "1x_DeJumbo.ort",
                    title = "DeJumbo",
                    description = R.string.model_dejumbo,
                    type = Type.ARTIFACTS,
                    speed = Speed.VerySlow(15.717f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/ddcolor_paper_tiny.ort",
                    name = "ddcolor_paper_tiny.ort",
                    title = "DDColor Tiny",
                    description = R.string.model_ddcolor_tiny,
                    type = Type.COLORIZE,
                    speed = Speed.VeryFast(0.159f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN-x4v3.ort",
                    name = "RealESRGAN-x4v3.ort",
                    title = "RealESRGAN x4v3",
                    description = R.string.model_realesrgan_x4v3,
                    type = Type.UPSCALE,
                    speed = Speed.VeryFast(1.215f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x2plus.ort",
                    name = "RealESRGAN_x2plus.ort",
                    title = "RealESRGAN x2 Plus",
                    description = R.string.model_realesrgan_x2plus,
                    type = Type.UPSCALE,
                    speed = Speed.Fast(4.437f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus.ort",
                    name = "RealESRGAN_x4plus.ort",
                    title = "RealESRGAN x4 Plus",
                    description = R.string.model_realesrgan_x4plus,
                    type = Type.UPSCALE,
                    speed = Speed.VerySlow(18.018f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus_anime_6B.ort",
                    name = "RealESRGAN_x4plus_anime_6B.ort",
                    title = "RealESRGAN x4 Plus Anime 6B",
                    description = R.string.model_realesrgan_x4plus_anime,
                    type = Type.UPSCALE,
                    speed = Speed.Fast(5.793f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRNet_x4plus.ort",
                    name = "RealESRNet_x4plus.ort",
                    title = "RealESRNet x4 Plus",
                    description = R.string.model_realesrnet_x4plus,
                    type = Type.UPSCALE,
                    speed = Speed.VerySlow(17.11f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus_anime_4B32F.ort",
                    name = "RealESRGAN_x4plus_anime_4B32F.ort",
                    title = "RealESRGAN x4 Plus Anime 4B",
                    description = R.string.model_realesrgan_x4plus_anime_4b32f,
                    type = Type.UPSCALE,
                    speed = Speed.VeryFast(1.534f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/x4-UltraSharpV2_fp32_op17.ort",
                    name = "x4-UltraSharpV2_fp32_op17.ort",
                    title = "UltraSharp x4 V2",
                    description = R.string.model_ultrasharp_v2_x4,
                    type = Type.UPSCALE,
                    speed = Speed.VerySlow(36.695f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/x4-UltraSharpV2_Lite_fp16_op17.ort",
                    name = "x4-UltraSharpV2_Lite_fp16_op17.ort",
                    title = "UltraSharp x4 V2 Lite",
                    description = R.string.model_ultrasharp_v2_lite_x4,
                    type = Type.UPSCALE,
                    speed = Speed.Normal(7.718f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/RMBG_1.4.ort",
                    name = "RMBG_1.4.ort",
                    title = "RMBG 1.4",
                    description = R.string.model_rmbg_1_4,
                    type = Type.REMOVE_BG,
                    speed = Speed.Normal(0.603f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/inspyrenet.onnx",
                    name = "inspyrenet.onnx",
                    title = "InSPyReNet",
                    description = R.string.model_inspyrenet,
                    type = Type.REMOVE_BG,
                    speed = Speed.Slow(3.029f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/u2net.onnx",
                    name = "u2net.onnx",
                    title = "U2Net",
                    description = R.string.model_u2net,
                    type = Type.REMOVE_BG,
                    speed = Speed.Fast(0.19f)
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/bgremove/u2netp.onnx",
                    name = "u2netp.onnx",
                    title = "U2NetP",
                    description = R.string.model_u2netp,
                    type = Type.REMOVE_BG,
                    speed = Speed.VeryFast(0.074f)
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/ddcolor_modelscope.onnx?download=true",
                    name = "ddcolor_modelscope.onnx",
                    title = "DDColor",
                    description = R.string.model_ddcolor,
                    type = Type.COLORIZE,
                    speed = Speed.VeryFast(0.362f)
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/ddcolor_artistic.onnx?download=true",
                    name = "ddcolor_artistic.onnx",
                    title = "DDColor Artistic",
                    description = R.string.model_ddcolor_artistic,
                    type = Type.COLORIZE,
                    speed = Speed.VeryFast(0.334f)
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/birefnet_swin_tiny.ort?download=true",
                    name = "birefnet_swin_tiny.ort",
                    title = "BiRefNet",
                    description = R.string.model_birefnet,
                    type = Type.REMOVE_BG,
                    speed = Speed.VerySlow(4.117f)
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/isnet-general-use.onnx?download=true",
                    name = "isnet-general-use.onnx",
                    title = "ISNet",
                    description = R.string.model_isnet,
                    type = Type.REMOVE_BG,
                    speed = Speed.Normal(0.573f)
                )
            ).sortedBy { it.type?.ordinal }
        }

        fun find(name: String?): NeuralModel? = entries.find { model ->
            model.name.equals(name, true)
        }

        fun Imported(name: String): NeuralModel = NeuralModel(
            downloadLink = "imported",
            name = name,
            title = name.replace("_", " ").replace("-", " ").substringBefore('.'),
            description = null,
            type = null,
            speed = null
        )
    }

}