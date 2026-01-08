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

package com.t8rin.imagetoolbox.feature.ai_tools.domain.model

import com.t8rin.imagetoolbox.core.resources.R

data class NeuralModel(
    val downloadLink: String,
    val name: String,
    val title: String,
    val description: Int
) {

    val supportsStrength = name.contains("fbcnn", true)

    companion object {
        val entries: List<NeuralModel> by lazy {
            listOf(
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1x-AnimeUndeint-Compact-fp16.onnx",
                    name = "1x-AnimeUndeint-Compact-fp16.onnx",
                    title = "Anime Undeint",
                    description = R.string.model_anime_undeint
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1x-BroadcastToStudio_Compact-fp16.onnx",
                    name = "1x-BroadcastToStudio_Compact-fp16.onnx",
                    title = "Broadcast To Studio",
                    description = R.string.model_broadcast
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1x-RGB-max-Denoise-fp16.onnx",
                    name = "1x-RGB-max-Denoise-fp16.onnx",
                    title = "RGB Max Denoise",
                    description = R.string.model_rgb_max_denoise_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1x-WB-Denoise-fp16.onnx",
                    name = "1x-WB-Denoise-fp16.onnx",
                    title = "WB Denoise",
                    description = R.string.model_wb_denoise
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1x-span-anime-pretrain-fp16.onnx",
                    name = "1x-span-anime-pretrain-fp16.onnx",
                    title = "SPAN Anime Pretrain",
                    description = R.string.model_span_anime_pretrain
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1xBook-Compact-fp16.onnx",
                    name = "1xBook-Compact-fp16.onnx",
                    title = "Book Scan",
                    description = R.string.model_book_scan
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/nanomodels/1xOverExposureCorrection_compact-fp16.onnx",
                    name = "1xOverExposureCorrection_compact-fp16.onnx",
                    title = "Overexposure Correction",
                    description = R.string.model_overexposure
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/fbcnn/fbcnn_color_fp16.onnx",
                    name = "fbcnn_color_fp16.onnx",
                    title = "FBCNN Color",
                    description = R.string.model_fbcnn_color_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/fbcnn/fbcnn_grey_fp16.onnx",
                    name = "fbcnn_gray_fp16.onnx",
                    title = "FBCNN Grayscale",
                    description = R.string.model_fbcnn_gray_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/fbcnn/fbcnn_gray_double_fp16.onnx",
                    name = "fbcnn_gray_double_fp16.onnx",
                    title = "FBCNN Grayscale Strong",
                    description = R.string.model_fbcnn_gray_double_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/scunet/scunet_color_real_gan_fp16.onnx",
                    name = "scunet_color_real_gan_fp16.onnx",
                    title = "SCUNet Color GAN",
                    description = R.string.model_scunet_color_gan_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/scunet/scunet_color_real_psnr_fp16.onnx",
                    name = "scunet_color_real_psnr_fp16.onnx",
                    title = "SCUNet Color PSNR",
                    description = R.string.model_scunet_color_psnr_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/scunet/scunet_gray_15_fp16.onnx",
                    name = "scunet_gray_15_fp16.onnx",
                    title = "SCUNet Grayscale 15",
                    description = R.string.model_scunet_gray_15_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/scunet/scunet_gray_25_fp16.onnx",
                    name = "scunet_gray_25_fp16.onnx",
                    title = "SCUNet Grayscale 25",
                    description = R.string.model_scunet_gray_25_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/scunet/scunet_gray_50_fp16.onnx",
                    name = "scunet_gray_50_fp16.onnx",
                    title = "SCUNet Grayscale 50",
                    description = R.string.model_scunet_gray_50_fp16
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-Anti-Aliasing-fp16.onnx",
                    name = "1x-Anti-Aliasing-fp16.onnx",
                    title = "Anti-Aliasing",
                    description = R.string.model_antialias
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_ColorizerV2_22000G-fp16.onnx",
                    name = "1x_ColorizerV2_22000G-fp16.onnx",
                    title = "Colorizer",
                    description = R.string.model_colorizer
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_DeSharpen-fp16.onnx",
                    name = "1x_DeSharpen-fp16.onnx",
                    title = "DeSharpen",
                    description = R.string.model_desharpen
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_DeEdge-fp16.onnx",
                    name = "1x_DeEdge-fp16.onnx",
                    title = "DeEdge",
                    description = R.string.model_deedge
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_GainresV4-fp16.onnx",
                    name = "1x_GainresV4-fp16.onnx",
                    title = "GainRes",
                    description = R.string.model_gainres
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-DeBink-v4.onnx",
                    name = "1x-DeBink-v4.onnx",
                    title = "DeBink v4",
                    description = R.string.model_debink_v4
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-DeBink-v5.onnx",
                    name = "1x-DeBink-v5.onnx",
                    title = "DeBink v5",
                    description = R.string.model_debink_v5
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-DeBink-v6.onnx",
                    name = "1x-DeBink-v6.onnx",
                    title = "DeBink v6",
                    description = R.string.model_debink_v6
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-KDM003-scans-fp16.onnx",
                    name = "1x-KDM003-scans-fp16.onnx",
                    title = "KDM003 Scans",
                    description = R.string.model_kdm003_scans
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    name = "1x-NMKD-Jaywreck3-Lite-fp16.onnx",
                    title = "NMKD Jaywreck3 Lite",
                    description = R.string.model_nmkd_jaywreck3_lite
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-SpongeColor-Lite-fp16.onnx",
                    name = "1x-SpongeColor-Lite-fp16.onnx",
                    title = "SpongeColor Lite",
                    description = R.string.model_spongecolor_lite
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x-cinepak-fp16.onnx",
                    name = "1x-cinepak-fp16.onnx",
                    title = "Cinepak",
                    description = R.string.model_cinepak
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    name = "1x_BCGone-DetailedV2_40-60_115000_G-fp16.onnx",
                    title = "BCGone Detailed V2",
                    description = R.string.model_bcgone_detailed_v2
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_BCGone_Smooth_110000_G-fp16.onnx",
                    name = "1x_BCGone_Smooth_110000_G-fp16.onnx",
                    title = "BCGone Smooth",
                    description = R.string.model_bcgone_smooth
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_Bandage-Smooth-fp16.onnx",
                    name = "1x_Bandage-Smooth-fp16.onnx",
                    title = "Bandage Smooth",
                    description = R.string.model_bandage_smooth
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_Bendel_Halftone-fp32.onnx",
                    name = "1x_Bendel_Halftone-fp32.onnx",
                    title = "Bendel Halftone",
                    description = R.string.model_bendel_halftone
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_DitherDeleterV3-Smooth-fp16.onnx",
                    name = "1x_DitherDeleterV3-Smooth-fp16.onnx",
                    title = "Dither Deleter V3 Smooth",
                    description = R.string.model_dither_deleter_v3_smooth
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    name = "1x_JPEGDestroyerV2_96000G-fp16.onnx",
                    title = "JPEG Destroyer V2",
                    description = R.string.model_jpeg_destroyer_v2
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/1x_NMKD-h264Texturize-fp16.onnx",
                    name = "1x_NMKD-h264Texturize-fp16.onnx",
                    title = "NMKD H264 Texturize",
                    description = R.string.model_nmkd_h264_texturize
                ),
                NeuralModel(
                    downloadLink = "https://raw.githubusercontent.com/T8RIN/ImageToolboxRemoteResources/main/onnx/enhance/other-models/VHS-Sharpen-1x_46000_G-fp16.onnx",
                    name = "VHS-Sharpen-1x_46000_G-fp16.onnx",
                    title = "VHS Sharpen",
                    description = R.string.model_vhs_sharpen
                )
            )
        }

        fun find(name: String?): NeuralModel? = entries.find { model ->
            model.name.equals(name, true)
        }
    }

}