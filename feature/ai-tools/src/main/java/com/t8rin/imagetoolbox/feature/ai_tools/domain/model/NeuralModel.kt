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
    val speed: Speed?,
    val downloadSize: Long,
    val checksum: String
) {
    val supportsStrength = name.contains("fbcnn_", true)
    val isImported = downloadLink == "imported"
    val isNonChunkable = name.contains("ddcolor") || type == Type.REMOVE_BG

    val pointerLink: String = downloadLink
        .replace("/raw/", "/blob/")
        .replace("/resolve/", "/blob/")
        .substringBefore("?download=true")

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
                    downloadSize = 143910675,
                    speed = Speed.Fast(2.003f),
                    checksum = "1a678ff4f721b557fd8a7e560b99cb94ba92f201545c7181c703e7808b93e922"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/fbcnn/fbcnn_grey_fp16.onnx",
                    name = "fbcnn_grey_fp16.onnx",
                    title = "FBCNN Grayscale",
                    description = R.string.model_fbcnn_gray_fp16,
                    type = Type.DE_JPEG,
                    downloadSize = 143903294,
                    speed = Speed.VeryFast(1.992f),
                    checksum = "e220b9637a9f2c34a36c98b275b2c9d2b9c2c029e365be82111072376afbec54"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/fbcnn/fbcnn_gray_double_fp16.onnx",
                    name = "fbcnn_gray_double_fp16.onnx",
                    title = "FBCNN Grayscale Strong",
                    description = R.string.model_fbcnn_gray_double_fp16,
                    type = Type.DE_JPEG,
                    downloadSize = 143903294,
                    speed = Speed.VeryFast(1.934f),
                    checksum = "17feadd8970772f5ff85596cb9fb152ae3c2b82bca4deb52a7c8b3ecb2f7ac14"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color-GAN.onnx",
                    name = "scunet_color-GAN.onnx",
                    title = "SCUNet Color GAN",
                    description = R.string.model_scunet_color_gan_fp16,
                    type = Type.DENOISE,
                    downloadSize = 91264256,
                    speed = Speed.Fast(2.715f),
                    checksum = "79ae6073c91c2d25d1f199137a67c8d0f0807df27219cdd7d890f3cc6d5b43e7"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color-PSNR.onnx",
                    name = "scunet_color-PSNR.onnx",
                    title = "SCUNet Color PSNR",
                    description = R.string.model_scunet_color_psnr_fp16,
                    type = Type.DENOISE,
                    downloadSize = 91264256,
                    speed = Speed.Fast(2.633f),
                    checksum = "b0f8c12f1575bb49e39a85924152f1c6d4b527a4aae0432c9e5c7397123465e3"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_15_fp16.onnx",
                    name = "scunet_gray_15_fp16.onnx",
                    title = "SCUNet Grayscale 15",
                    description = R.string.model_scunet_gray_15_fp16,
                    type = Type.DENOISE,
                    downloadSize = 37741895,
                    speed = Speed.Fast(3.048f),
                    checksum = "8e8740cea4306c9a61215194f315e5c0dc9e06c726a9ddea77d978d804da7663"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_25_fp16.onnx",
                    name = "scunet_gray_25_fp16.onnx",
                    title = "SCUNet Grayscale 25",
                    description = R.string.model_scunet_gray_25_fp16,
                    type = Type.DENOISE,
                    downloadSize = 37741895,
                    speed = Speed.Fast(3.033f),
                    checksum = "dec631fbdca7705bbff1fc779cf85a657dcb67f55359c368464dd6e734e1f2b7"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_gray_50_fp16.onnx",
                    name = "scunet_gray_50_fp16.onnx",
                    title = "SCUNet Grayscale 50",
                    description = R.string.model_scunet_gray_50_fp16,
                    type = Type.DENOISE,
                    downloadSize = 37741895,
                    speed = Speed.Fast(3.058f),
                    checksum = "48b7d07229a03d98b892d2b33aa4c572ea955301772e7fcb5fd10723552a1874"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_15_fp16.onnx",
                    name = "scunet_color_15_fp16.onnx",
                    title = "SCUNet Color 15",
                    description = R.string.model_scunet_color_15_fp16,
                    type = Type.DENOISE,
                    downloadSize = 42555584,
                    speed = Speed.Fast(7.095f),
                    checksum = "25a3a07de278867df9d29e9d08fe555523bb0f9f78f8956c4af943a4eeb8c934"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_25_fp16.onnx",
                    name = "scunet_color_25_fp16.onnx",
                    title = "SCUNet Color 25",
                    description = R.string.model_scunet_color_25_fp16,
                    type = Type.DENOISE,
                    downloadSize = 42555584,
                    speed = Speed.Fast(6.994f),
                    checksum = "34d25ec2187d24f9f25b9dc9d918e94e87217c129471adda8c9fdf2e5a1cb62a"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/scunet/scunet_color_50_fp16.onnx",
                    name = "scunet_color_50_fp16.onnx",
                    title = "SCUNet Color 50",
                    description = R.string.model_scunet_color_50_fp16,
                    type = Type.DENOISE,
                    downloadSize = 42555584,
                    speed = Speed.Normal(7.229f),
                    checksum = "1c6bdc6d9e0c1dea314cf22d41c261d4c744bf0ae1ae6c59b9505c4b4d50febb"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-AnimeUndeint-Compact-fp16.onnx",
                    name = "1x-AnimeUndeint-Compact-fp16.onnx",
                    title = "Anime Undeint",
                    description = R.string.model_anime_undeint,
                    type = Type.ANIME,
                    downloadSize = 2391605,
                    speed = Speed.VeryFast(0.497f),
                    checksum = "e2927fe5c09ad61975bfa52f7a879e3cf044a190d5b25f147a363540cd00ccd3"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-BroadcastToStudio_Compact-fp16.onnx",
                    name = "1x-BroadcastToStudio_Compact-fp16.onnx",
                    title = "Broadcast To Studio",
                    description = R.string.model_broadcast,
                    type = Type.ARTIFACTS,
                    downloadSize = 1200682,
                    speed = Speed.VeryFast(0.625f),
                    checksum = "52836c782140058bcc695e90102c3ef54961ebab2c12e66298eaba25d42570bc"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-RGB-max-Denoise-fp16.onnx",
                    name = "1x-RGB-max-Denoise-fp16.onnx",
                    title = "RGB Max Denoise",
                    description = R.string.model_rgb_max_denoise_fp16,
                    type = Type.DENOISE,
                    downloadSize = 310212,
                    speed = Speed.VeryFast(0.172f),
                    checksum = "1bb02e6444f306fd8ad17758ed474f6e21b909cceda1cba9606b6e923f65a102"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-WB-Denoise-fp16.onnx",
                    name = "1x-WB-Denoise-fp16.onnx",
                    title = "WB Denoise",
                    description = R.string.model_wb_denoise,
                    type = Type.DENOISE,
                    downloadSize = 310212,
                    speed = Speed.VeryFast(0.177f),
                    checksum = "50978ca2777b5720d1d57c8f284d5274c96746c8721c3c2fdccb1dfcea823af1"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1x-span-anime-pretrain-fp16.onnx",
                    name = "1x-span-anime-pretrain-fp16.onnx",
                    title = "SPAN Anime Pretrain",
                    description = R.string.model_span_anime_pretrain,
                    type = Type.ANIME,
                    downloadSize = 825768,
                    speed = Speed.VeryFast(0.399f),
                    checksum = "1311da8ad10af1d763b6b22797150429b377f36dda1fb26af5e3ec9bcc2701d2"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1xBook-Compact-fp16.onnx",
                    name = "1xBook-Compact-fp16.onnx",
                    title = "Book Scan",
                    description = R.string.model_book_scan,
                    type = Type.SCANS,
                    downloadSize = 1200682,
                    speed = Speed.VeryFast(0.452f),
                    checksum = "7305ec3a592c5209fc2887d1f12d9b79163fca37020a719f55c83344effdb3c0"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/nanomodels/1xOverExposureCorrection_compact-fp16.onnx",
                    name = "1xOverExposureCorrection_compact-fp16.onnx",
                    title = "Overexposure Correction",
                    description = R.string.model_overexposure,
                    type = Type.ENHANCE,
                    downloadSize = 2391605,
                    speed = Speed.VeryFast(0.492f),
                    checksum = "8fef8e73062d2eff56aacea17caa1162b094a8c8a8010f51084c7e2cf9403ded"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-Anti-Aliasing-fp16.onnx",
                    name = "1x-Anti-Aliasing-fp16.onnx",
                    title = "Anti-Aliasing",
                    description = R.string.model_antialias,
                    type = Type.ARTIFACTS,
                    downloadSize = 33456842,
                    speed = Speed.Slow(14.806f),
                    checksum = "acd4f12a59ec606772df496f422e27099d629316671b41793b7362e6e13fe8dd"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ColorizerV2_22000G-fp16.onnx",
                    name = "1x_ColorizerV2_22000G-fp16.onnx",
                    title = "Colorizer",
                    description = R.string.model_colorizer,
                    type = Type.COLORIZE,
                    downloadSize = 33456842,
                    speed = Speed.Slow(15.125f),
                    checksum = "c42520288f29a61d85107439ffea4a755129cb2f3eddfabb396598dc5d867f40"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeSharpen-fp16.onnx",
                    name = "1x_DeSharpen-fp16.onnx",
                    title = "DeSharpen",
                    description = R.string.model_desharpen,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.VerySlow(15.593f),
                    checksum = "3c08f894e9b05bba52f3b10cf1fb712a2370a5f352b88c3cdb246a3c295c6531"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeEdge-fp16.onnx",
                    name = "1x_DeEdge-fp16.onnx",
                    title = "DeEdge",
                    description = R.string.model_deedge,
                    type = Type.ARTIFACTS,
                    downloadSize = 33456842,
                    speed = Speed.Normal(14.099f),
                    checksum = "ead4873ec5f6870343e7dbe121f3054335941b12cf6fa4d0b54010c2cc3c0675"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_GainresV4-fp16.onnx",
                    name = "1x_GainresV4-fp16.onnx",
                    title = "GainRes",
                    description = R.string.model_gainres,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Normal(14.415f),
                    checksum = "a23d8157c6f809492ebbdd42632b865ce3e6c360221e7195aecc519ce610f3ac"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v4.onnx",
                    name = "1x-DeBink-v4.onnx",
                    title = "DeBink v4",
                    description = R.string.model_debink_v4,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Normal(12.947f),
                    checksum = "be52e251dff5a0c4504c559bb84d1b611c2e809edc99f1c261db646cd89a12fb"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v5.onnx",
                    name = "1x-DeBink-v5.onnx",
                    title = "DeBink v5",
                    description = R.string.model_debink_v5,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Normal(13.597f),
                    checksum = "f545222af1655b96f5b2aa3d46c40d8f256937cd2cbc2f77a6f89f29abf01e46"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-DeBink-v6.onnx",
                    name = "1x-DeBink-v6.onnx",
                    title = "DeBink v6",
                    description = R.string.model_debink_v6,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Normal(13.789f),
                    checksum = "e1c216804795a061218aa29617db52b91fb741c92bb00691da7caf9a64ef4f18"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-KDM003-scans-fp16.onnx",
                    name = "1x-KDM003-scans-fp16.onnx",
                    title = "KDM003 Scans",
                    description = R.string.model_kdm003_scans,
                    type = Type.SCANS,
                    downloadSize = 33456842,
                    speed = Speed.Slow(14.948f),
                    checksum = "85c740d5bdf8aa714b5b53eea1aa67c4c9e5483652702a31779a10f7a2ec9e45"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    name = "1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    title = "NMKD Jaywreck3 Lite",
                    description = R.string.model_nmkd_jaywreck3_lite,
                    type = Type.ENHANCE,
                    downloadSize = 10114140,
                    speed = Speed.Fast(4.896f),
                    checksum = "4f72d8532e0632e87bcb0d03ff611bdac348e615873bb7c990c6291b0f08a495"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-SpongeColor-Lite-fp16.onnx",
                    name = "1x-SpongeColor-Lite-fp16.onnx",
                    title = "SpongeColor Lite",
                    description = R.string.model_spongecolor_lite,
                    type = Type.COLORIZE,
                    downloadSize = 10112988,
                    speed = Speed.Fast(4.731f),
                    checksum = "c0b9df99d0cb0857eb96e0f8fb718c9010c2eeab9c30c17b0deacee79e2d2851"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-cinepak-fp16.onnx",
                    name = "1x-cinepak-fp16.onnx",
                    title = "Cinepak",
                    description = R.string.model_cinepak,
                    type = Type.ARTIFACTS,
                    downloadSize = 33456842,
                    speed = Speed.VerySlow(15.515f),
                    checksum = "55cd3df1d3700dd31f09309beedce65f4d178d4b6e7777ecce05818bb60a2c67"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    name = "1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    title = "BCGone Detailed V2",
                    description = R.string.model_bcgone_detailed_v2,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Normal(14.694f),
                    checksum = "a5237ef67f250871626020af3fc817bb6647888886cfc5b82a8e49718e52d3f4"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_BCGone_Smooth_110000_G-fp16.onnx",
                    name = "1x_BCGone_Smooth_110000_G-fp16.onnx",
                    title = "BCGone Smooth",
                    description = R.string.model_bcgone_smooth,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.VerySlow(15.44f),
                    checksum = "597ac90e1cc3105631b3a06c354cf055f1bccdfc440e4fdfe069918d228c3cdf"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Bandage-Smooth-fp16.onnx",
                    name = "1x_Bandage-Smooth-fp16.onnx",
                    title = "Bandage Smooth",
                    description = R.string.model_bandage_smooth,
                    type = Type.ARTIFACTS,
                    downloadSize = 33456842,
                    speed = Speed.Slow(15.176f),
                    checksum = "40f98955aba23e2360b7f9f2e800e896be0d6a8071a3ff0a4bbb6cf24cd8656d"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Bendel_Halftone-fp32.onnx",
                    name = "1x_Bendel_Halftone-fp32.onnx",
                    title = "Bendel Halftone",
                    description = R.string.model_bendel_halftone,
                    type = Type.ARTIFACTS,
                    downloadSize = 8660947,
                    speed = Speed.Fast(4.019f),
                    checksum = "6982ab0c770ac4c210dd6df435c96aca609dfbec8745d8ff6c57db6bae88eead"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DitherDeleterV3-Smooth-fp16.onnx",
                    name = "1x_DitherDeleterV3-Smooth-fp16.onnx",
                    title = "Dither Deleter V3 Smooth",
                    description = R.string.model_dither_deleter_v3_smooth,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.VerySlow(15.618f),
                    checksum = "55b0189c77caa8aa848d8b4c1dbf737a265d2a22f3ad0ea3805d9d6fc0881b3b"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    name = "1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    title = "JPEG Destroyer V2",
                    description = R.string.model_jpeg_destroyer_v2,
                    type = Type.DE_JPEG,
                    downloadSize = 33456842,
                    speed = Speed.Slow(14.9f),
                    checksum = "96734e021e1b616ab3e74376244895bbeed118e454e051a548f5b98a113305c9"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKD-h264Texturize-fp16.onnx",
                    name = "1x_NMKD-h264Texturize-fp16.onnx",
                    title = "NMKD H264 Texturize",
                    description = R.string.model_nmkd_h264_texturize,
                    type = Type.ARTIFACTS,
                    downloadSize = 33456842,
                    speed = Speed.Slow(14.886f),
                    checksum = "4291323f8f4eee0623d9c01416cb8f918b61285b6e4f4112f0dfc341c0f39397"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/VHS-Sharpen-1x_46000_G-fp16.onnx",
                    name = "VHS-Sharpen-1x_46000_G-fp16.onnx",
                    title = "VHS Sharpen",
                    description = R.string.model_vhs_sharpen,
                    type = Type.ENHANCE,
                    downloadSize = 33456842,
                    speed = Speed.Slow(15.192f),
                    checksum = "6d69cc4fb4fdcc34dd779a95032ddf5f32e99d5399c9dffcd557c0c3859ab866"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_dithering_alsa-fp16.onnx",
                    name = "1x_artifacts_dithering_alsa-fp16.onnx",
                    title = "Artifacts Dithering ALSA",
                    description = R.string.model_artifacts_dithering_alsa,
                    type = Type.ARTIFACTS,
                    downloadSize = 33421271,
                    speed = Speed.VerySlow(15.392f),
                    checksum = "668bf366457c774e10cb46a756bf9744165c53671180969d4bab47ce935cd520"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKD-BrightenRedux_200k-fp16.onnx",
                    name = "1x_NMKD-BrightenRedux_200k-fp16.onnx",
                    title = "NMKD Brighten Redux",
                    description = R.string.model_nmkd_brighten_redux,
                    type = Type.ENHANCE,
                    downloadSize = 33421271,
                    speed = Speed.Slow(14.863f),
                    checksum = "93f50904fa78948da69ff541b42fd9ce3e0fa096d87ca22f1f2f0ebb929f1c76"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_nmkdbrighten_10000_G-fp16.onnx",
                    name = "1x_nmkdbrighten_10000_G-fp16.onnx",
                    title = "NMKD Brighten",
                    description = R.string.model_nmkd_brighten,
                    type = Type.ENHANCE,
                    downloadSize = 33421271,
                    speed = Speed.Slow(14.803f),
                    checksum = "e1c354370c04bc0433bdb54fc81898c1ade0c2db02cc02878a58aac6195c7f52"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NMKDDetoon_97500_G-fp16.onnx",
                    name = "1x_NMKDDetoon_97500_G-fp16.onnx",
                    title = "NMKD Detoon",
                    description = R.string.model_nmkd_detoon,
                    type = Type.ENHANCE,
                    downloadSize = 33421271,
                    speed = Speed.Slow(14.817f),
                    checksum = "b75acf61a6067777dc591cb31c77a79fb73db3b72468796d69ceec73e58ac32d"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Poisson-Detailed_108000_G-fp16.onnx",
                    name = "1x_NoiseToner-Poisson-Detailed_108000_G-fp16.onnx",
                    title = "Noise Toner Poisson Detailed",
                    description = R.string.model_noise_toner_poisson_detailed,
                    type = Type.DENOISE,
                    downloadSize = 33421271,
                    speed = Speed.Normal(14.463f),
                    checksum = "ed6bb382f71385032df8fcf16ea0a1d0469de801338ed32abcc5a01557944460"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Poisson-Soft_101000_G-fp16.onnx",
                    name = "1x_NoiseToner-Poisson-Soft_101000_G-fp16.onnx",
                    title = "Noise Toner Poisson Soft",
                    description = R.string.model_noise_toner_poisson_soft,
                    type = Type.DENOISE,
                    downloadSize = 33421271,
                    speed = Speed.Normal(14.606f),
                    checksum = "e4d18cc399e4fbf2b5239b00b640c8ff0e80811bf88ded7f151eec6760eb0471"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Uniform-Detailed_100000_G-fp16.onnx",
                    name = "1x_NoiseToner-Uniform-Detailed_100000_G-fp16.onnx",
                    title = "Noise Toner Uniform Detailed",
                    description = R.string.model_noise_toner_uniform_detailed,
                    type = Type.DENOISE,
                    downloadSize = 33421271,
                    speed = Speed.Normal(14.802f),
                    checksum = "e5602a5e1aabb8ec6ee98969d834224ac1b4b366f89a4ec26b3fae93077a4dbf"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_NoiseToner-Uniform-Soft_100000_G-fp16.onnx",
                    name = "1x_NoiseToner-Uniform-Soft_100000_G-fp16.onnx",
                    title = "Noise Toner Uniform Soft",
                    description = R.string.model_noise_toner_uniform_soft,
                    type = Type.DENOISE,
                    downloadSize = 33421271,
                    speed = Speed.Slow(15.001f),
                    checksum = "54622791711ab457565fa0a750f668f209171a0194d0f94c206243d9a649f797"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_Repainter_20000_G-fp16.onnx",
                    name = "1x_Repainter_20000_G-fp16.onnx",
                    title = "Repainter",
                    description = R.string.model_repainter,
                    type = Type.ENHANCE,
                    downloadSize = 33421271,
                    speed = Speed.Normal(14.775f),
                    checksum = "202fdce20d84ab0da25ea0d348d2f2c29546a28509e778ef8d699fb82fcf873d"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-Debandurh-FS-Ultra-lite-fp16.onnx",
                    name = "1x-Debandurh-FS-Ultra-lite-fp16.onnx",
                    title = "Debandurh FS Ultra Lite",
                    description = R.string.model_debandurh_fs_ultra_lite,
                    type = Type.ENHANCE,
                    downloadSize = 1371141,
                    speed = Speed.VeryFast(0.741f),
                    checksum = "6aef5e015176c48984db80e95c31539d0d1e2c1fef9bac04b335253ce372b8a9"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_00-20.ort",
                    name = "1x_JPEG_00-20.ort",
                    title = "JPEG 0-20",
                    description = R.string.model_jpeg_0_20,
                    type = Type.DE_JPEG,
                    downloadSize = 35277480,
                    speed = Speed.VerySlow(15.994f),
                    checksum = "9230d501dc34fd6ef3c6dcc7640df95c20627482ff93cb236a93060bd0c9826b"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_20-40.ort",
                    name = "1x_JPEG_20-40.ort",
                    title = "JPEG 20-40",
                    description = R.string.model_jpeg_20_40,
                    type = Type.DE_JPEG,
                    downloadSize = 35277488,
                    speed = Speed.VerySlow(15.754f),
                    checksum = "bbdbd9cdb86d56a5a88b0ccf8ad833d6a1ebfc4202cc25c6d139bb09a6a2535d"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_40-60.ort",
                    name = "1x_JPEG_40-60.ort",
                    title = "JPEG 40-60",
                    description = R.string.model_jpeg_40_60,
                    type = Type.DE_JPEG,
                    downloadSize = 35277480,
                    speed = Speed.VerySlow(15.959f),
                    checksum = "dc248a7166cbcdcf8577d6cbd80a6d8aab3a3a7c9e2ebda29341353b4050664b"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_60-80.ort",
                    name = "1x_JPEG_60-80.ort",
                    title = "JPEG 60-80",
                    description = R.string.model_jpeg_60_80,
                    type = Type.DE_JPEG,
                    downloadSize = 35277488,
                    speed = Speed.Normal(14.307f),
                    checksum = "21ece5396cf88f39f35a264000baf2ff2ec084ca7fd41b125759d845b1bfb9c3"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_JPEG_80-100.ort",
                    name = "1x_JPEG_80-100.ort",
                    title = "JPEG 80-100",
                    description = R.string.model_jpeg_80_100,
                    type = Type.DE_JPEG,
                    downloadSize = 35277488,
                    speed = Speed.Slow(15.018f),
                    checksum = "1041060d1d151150e14e8d51c65cd5f6608606bb4bf7ac6b5271ae074e5c3913"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeBLR.ort",
                    name = "1x_DeBLR.ort",
                    title = "DeBLR",
                    description = R.string.model_deblr,
                    type = Type.ENHANCE,
                    downloadSize = 35277480,
                    speed = Speed.VerySlow(16.079f),
                    checksum = "261eb9690bc284d8cee5d81dd6156d92f9c3338c582019387a3852257c8a7b3a"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_00_20_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_00_20_alsa-fp16.ort",
                    title = "JPEG Artifacts 0-20",
                    description = R.string.model_artifacts_jpg_0_20,
                    type = Type.DE_JPEG,
                    downloadSize = 34729312,
                    speed = Speed.Slow(15.041f),
                    checksum = "8a416ffa7f1d78de7b3a8d211a123964b277fcb7c253f78b8e79970c7ed114bb"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_20_40_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_20_40_alsa-fp16.ort",
                    title = "JPEG Artifacts 20-40",
                    description = R.string.model_artifacts_jpg_20_40,
                    type = Type.DE_JPEG,
                    downloadSize = 34729312,
                    speed = Speed.Normal(14.733f),
                    checksum = "2abe485266cd1895e17b4166c5609f30a1d4d7283b9d8b2503c94f008a39244f"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_40_60_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_40_60_alsa-fp16.ort",
                    title = "JPEG Artifacts 40-60",
                    description = R.string.model_artifacts_jpg_40_60,
                    type = Type.DE_JPEG,
                    downloadSize = 34729312,
                    speed = Speed.Slow(14.973f),
                    checksum = "bd0116861368b83a459af687308a8acd7dc4c89aa8ba5c3dab76adb67ef9002f"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_60_80_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_60_80_alsa-fp16.ort",
                    title = "JPEG Artifacts 60-80",
                    description = R.string.model_artifacts_jpg_60_80,
                    type = Type.DE_JPEG,
                    downloadSize = 34729312,
                    speed = Speed.Normal(14.572f),
                    checksum = "5282f04a4520bcff4882836d134e99dbfc207f7fcaf4bf9166a99d623f579f4e"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_artifacts_jpg_80_100_alsa-fp16.ort",
                    name = "1x_artifacts_jpg_80_100_alsa-fp16.ort",
                    title = "JPEG Artifacts 80-100",
                    description = R.string.model_artifacts_jpg_80_100,
                    type = Type.DE_JPEG,
                    downloadSize = 34729312,
                    speed = Speed.VerySlow(15.404f),
                    checksum = "8d11451909d9318f54e1b292c0bf901856633bce835ee838bbcf4bfaf5cd174a"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ReDetail_v2_126000_G-fp16.ort",
                    name = "1x_ReDetail_v2_126000_G-fp16.ort",
                    title = "ReDetail v2",
                    description = R.string.model_redetail_v2,
                    type = Type.ENHANCE,
                    downloadSize = 34724216,
                    speed = Speed.Slow(15.265f),
                    checksum = "4a1790847f8e3b51ace425875abe928215d32e00d0b80af0ce90dd3481d6f272"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x-ITF-SkinDiffDetail-Lite-v1.ort",
                    name = "1x-ITF-SkinDiffDetail-Lite-v1.ort",
                    title = "ITF Skin DiffDetail Lite",
                    description = R.string.model_itf_skin_diffdetail_lite,
                    type = Type.ENHANCE,
                    downloadSize = 11070576,
                    speed = Speed.Fast(5.138f),
                    checksum = "813351fcee2447ff9037c86bf2807baee7a32be1db20352646c5a3eda8ced7b8"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_SBDV-DeJPEG-Lite_130000_G.ort",
                    name = "1x_SBDV-DeJPEG-Lite_130000_G.ort",
                    title = "SBDV DeJPEG",
                    description = R.string.model_sbdv_dejpeg,
                    type = Type.DE_JPEG,
                    downloadSize = 11070576,
                    speed = Speed.Fast(5.158f),
                    checksum = "3039271c69e3f5b3ca9af3673bea98749dffa9331930f88d057047b57d6001dc"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_ISO_denoise_v1.ort",
                    name = "1x_ISO_denoise_v1.ort",
                    title = "ISO Denoise v1",
                    description = R.string.model_iso_denoise_v1,
                    type = Type.DENOISE,
                    downloadSize = 35277480,
                    speed = Speed.VerySlow(15.463f),
                    checksum = "a16b9253a10a7e95aa10e451b2ad21b3d60124c1316ddaca746071f5843b312b"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/1x_DeJumbo.ort",
                    name = "1x_DeJumbo.ort",
                    title = "DeJumbo",
                    description = R.string.model_dejumbo,
                    type = Type.ARTIFACTS,
                    downloadSize = 35277488,
                    speed = Speed.VerySlow(15.717f),
                    checksum = "17202a453ad7f7c89c6969d2dd0cd844b01f6685584876e54092e59f589ad180"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/other-models/ddcolor_paper_tiny.ort",
                    name = "ddcolor_paper_tiny.ort",
                    title = "DDColor Tiny",
                    description = R.string.model_ddcolor_tiny,
                    type = Type.COLORIZE,
                    downloadSize = 220853232,
                    speed = Speed.VeryFast(0.159f),
                    checksum = "8186a8c21a5075c0c37860d7313043b6edb8a672067f73f3d8f5d362509f1bd3"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN-x4v3.ort",
                    name = "RealESRGAN-x4v3.ort",
                    title = "RealESRGAN x4v3",
                    description = R.string.model_realesrgan_x4v3,
                    type = Type.UPSCALE,
                    downloadSize = 2621440,
                    speed = Speed.VeryFast(1.215f),
                    checksum = "8dbd9c316f436c6e1d35a11ed29dbea0ce8f5b5b3ef082c9358ec93603a0398b"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x2plus.ort",
                    name = "RealESRGAN_x2plus.ort",
                    title = "RealESRGAN x2 Plus",
                    description = R.string.model_realesrgan_x2plus,
                    type = Type.UPSCALE,
                    downloadSize = 35456784,
                    speed = Speed.Fast(4.437f),
                    checksum = "cd6986ac4bd2d10d460c281dcd4f1df638fddb241f4810b53cb0eb98cfb420cd"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus.ort",
                    name = "RealESRGAN_x4plus.ort",
                    title = "RealESRGAN x4 Plus",
                    description = R.string.model_realesrgan_x4plus,
                    type = Type.UPSCALE,
                    downloadSize = 35437480,
                    speed = Speed.VerySlow(18.018f),
                    checksum = "110818e1a29309d1da6087e8bbe201f50e43ea7679483ebca1df95ab333d2a66"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus_anime_6B.ort",
                    name = "RealESRGAN_x4plus_anime_6B.ort",
                    title = "RealESRGAN x4 Plus Anime 6B",
                    description = R.string.model_realesrgan_x4plus_anime,
                    type = Type.UPSCALE,
                    downloadSize = 9488256,
                    speed = Speed.Fast(5.793f),
                    checksum = "e73004a743169923b28434b1487ed2f8c329fa99c057ffa15018502612b8bd36"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRNet_x4plus.ort",
                    name = "RealESRNet_x4plus.ort",
                    title = "RealESRNet x4 Plus",
                    description = R.string.model_realesrnet_x4plus,
                    type = Type.UPSCALE,
                    downloadSize = 35437480,
                    speed = Speed.VerySlow(17.11f),
                    checksum = "ed82b5cd61a6281db0eafa722c92803cecbf9eda8c88194c058e93e21407d38f"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/RealESRGAN_x4plus_anime_4B32F.ort",
                    name = "RealESRGAN_x4plus_anime_4B32F.ort",
                    title = "RealESRGAN x4 Plus Anime 4B",
                    description = R.string.model_realesrgan_x4plus_anime_4b32f,
                    type = Type.UPSCALE,
                    downloadSize = 5241720,
                    speed = Speed.VeryFast(1.534f),
                    checksum = "ca05a00f6cb42fb1fdf03e3b132ae792a83d748fc8df0c0fa62f67e798385fd7"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/x4-UltraSharpV2_fp32_op17.ort",
                    name = "x4-UltraSharpV2_fp32_op17.ort",
                    title = "UltraSharp x4 V2",
                    description = R.string.model_ultrasharp_v2_x4,
                    type = Type.UPSCALE,
                    downloadSize = 80518784,
                    speed = Speed.VerySlow(36.695f),
                    checksum = "6bbf8c91bced8c7c6fdacf841f34713b66b223fe5ccd5f579af04e077fa68302"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/enhance/upscale/x4-UltraSharpV2_Lite_fp16_op17.ort",
                    name = "x4-UltraSharpV2_Lite_fp16_op17.ort",
                    title = "UltraSharp x4 V2 Lite",
                    description = R.string.model_ultrasharp_v2_lite_x4,
                    type = Type.UPSCALE,
                    downloadSize = 16055408,
                    speed = Speed.Normal(7.718f),
                    checksum = "f65092e0ee88c41bdbb1eb633aa4014479599228c150f796e85d429aea4d43a0"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/RMBG_1.4.ort",
                    name = "RMBG_1.4.ort",
                    title = "RMBG 1.4",
                    description = R.string.model_rmbg_1_4,
                    type = Type.REMOVE_BG,
                    downloadSize = 88704616,
                    speed = Speed.Normal(0.603f),
                    checksum = "ecefc6e25e88a403762e74e2d112cd8f9dea4f4628c73f3da914ef169c91d86f"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/inspyrenet.onnx",
                    name = "inspyrenet.onnx",
                    title = "InSPyReNet",
                    description = R.string.model_inspyrenet,
                    type = Type.REMOVE_BG,
                    downloadSize = 395316574,
                    speed = Speed.Slow(3.029f),
                    checksum = "c108dd92b3ddfe3a2d9e9ac2b74730cc5acbb2ddc7ea863330c43f56ae832aa3"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/onnx/bgremove/u2net.onnx",
                    name = "u2net.onnx",
                    title = "U2Net",
                    description = R.string.model_u2net,
                    type = Type.REMOVE_BG,
                    downloadSize = 175997641,
                    speed = Speed.Fast(0.19f),
                    checksum = "8d10d2f3bb75ae3b6d527c77944fc5e7dcd94b29809d47a739a7a728a912b491"
                ),
                NeuralModel(
                    downloadLink = "https://github.com/T8RIN/ImageToolboxRemoteResources/raw/refs/heads/main/bgremove/u2netp.onnx",
                    name = "u2netp.onnx",
                    title = "U2NetP",
                    description = R.string.model_u2netp,
                    type = Type.REMOVE_BG,
                    downloadSize = 4574861,
                    speed = Speed.VeryFast(0.074f),
                    checksum = "309c8469258dda742793dce0ebea8e6dd393174f89934733ecc8b14c76f4ddd8"
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/ddcolor_modelscope.onnx?download=true",
                    name = "ddcolor_modelscope.onnx",
                    title = "DDColor",
                    description = R.string.model_ddcolor,
                    type = Type.COLORIZE,
                    downloadSize = 911801965,
                    speed = Speed.VeryFast(0.362f),
                    checksum = "cda896f3e61c0e489f2e11a657c6dfb711d0958364286335e3cd48fadbd621be"
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/ddcolor_artistic.onnx?download=true",
                    name = "ddcolor_artistic.onnx",
                    title = "DDColor Artistic",
                    description = R.string.model_ddcolor_artistic,
                    type = Type.COLORIZE,
                    downloadSize = 911801965,
                    speed = Speed.VeryFast(0.334f),
                    checksum = "e7f6d8e48d609be3f2615b70637fbc7019cbad545038279a7ba26f229503d61c"
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/birefnet_swin_tiny.ort?download=true",
                    name = "birefnet_swin_tiny.ort",
                    title = "BiRefNet",
                    description = R.string.model_birefnet,
                    type = Type.REMOVE_BG,
                    downloadSize = 247356800,
                    speed = Speed.VerySlow(4.117f),
                    checksum = "46512ae91e17171c09476e3154fa2f2f8b0a557b3c8e9c91c10d11f35bc3f70c"
                ),
                NeuralModel(
                    downloadLink = "https://huggingface.co/T8RIN/ddcolor-onnx/resolve/main/isnet-general-use.onnx?download=true",
                    name = "isnet-general-use.onnx",
                    title = "ISNet",
                    description = R.string.model_isnet,
                    type = Type.REMOVE_BG,
                    downloadSize = 178647984,
                    speed = Speed.Normal(0.573f),
                    checksum = "4fcc3f7f7af1d16565dd7ec767e6e2500565ed6ba76c5c30b9934116ca32153e"
                ),
            ).sortedBy { it.type?.ordinal }
        }

        fun find(name: String?): NeuralModel? = entries.find { model ->
            model.name.equals(name, true)
        }

        fun Imported(
            name: String,
            checksum: String
        ): NeuralModel = NeuralModel(
            downloadLink = "imported",
            name = name,
            title = name.replace("_", " ").replace("-", " ").substringBefore('.'),
            description = null,
            type = null,
            speed = null,
            downloadSize = 0,
            checksum = checksum
        )
    }

}