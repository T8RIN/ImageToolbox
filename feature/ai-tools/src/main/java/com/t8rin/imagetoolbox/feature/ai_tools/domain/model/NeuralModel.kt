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
                    downloadLink = "",
                    name = "1x-AnimeUndeint-Compact-fp16.onnx",
                    title = "Anime Undeint",
                    description = R.string.model_anime_undeint
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x-BroadcastToStudio_Compact-fp16.onnx",
                    title = "Broadcast To Studio",
                    description = R.string.model_broadcast
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x-RGB-max-Denoise-fp16.onnx",
                    title = "RGB Max Denoise",
                    description = R.string.model_rgb_max_denoise_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x-WB-Denoise-fp16.onnx",
                    title = "WB Denoise",
                    description = R.string.model_wb_denoise
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x-span-anime-pretrain-fp16.onnx",
                    title = "SPAN Anime Pretrain",
                    description = R.string.model_span_anime_pretrain
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1xBook-Compact-fp16.onnx",
                    title = "Book Scan",
                    description = R.string.model_book_scan
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1xOverExposureCorrection_compact-fp16.onnx",
                    title = "Overexposure Correction",
                    description = R.string.model_overexposure
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "fbcnn_color_fp16.onnx",
                    title = "FBCNN Color",
                    description = R.string.model_fbcnn_color_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "fbcnn_gray_fp16.onnx",
                    title = "FBCNN Grayscale",
                    description = R.string.model_fbcnn_gray_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "fbcnn_gray_double_fp16.onnx",
                    title = "FBCNN Grayscale Strong",
                    description = R.string.model_fbcnn_gray_double_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "scunet_color_real_gan_fp16.onnx",
                    title = "SCUNet Color GAN",
                    description = R.string.model_scunet_color_gan_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "scunet_color_real_psnr_fp16.onnx",
                    title = "SCUNet Color PSNR",
                    description = R.string.model_scunet_color_psnr_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "scunet_gray_15_fp16.onnx",
                    title = "SCUNet Grayscale 15",
                    description = R.string.model_scunet_gray_15_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "scunet_gray_25_fp16.onnx",
                    title = "SCUNet Grayscale 25",
                    description = R.string.model_scunet_gray_25_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "scunet_gray_50_fp16.onnx",
                    title = "SCUNet Grayscale 50",
                    description = R.string.model_scunet_gray_50_fp16
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x-Anti-Aliasing-fp16.onnx",
                    title = "Anti-Aliasing",
                    description = R.string.model_antialias
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x_ColorizerV2_22000G-fp16.onnx",
                    title = "Colorizer",
                    description = R.string.model_colorizer
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x_DeSharpen-fp16.onnx",
                    title = "DeSharpen",
                    description = R.string.model_desharpen
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x_DeEdge-fp16.onnx",
                    title = "DeEdge",
                    description = R.string.model_deedge
                ),
                NeuralModel(
                    downloadLink = "",
                    name = "1x_GainresV4-fp16.onnx",
                    title = "GainRes",
                    description = R.string.model_gainres
                )
            )
        }

        fun find(name: String?): NeuralModel? = entries.find { model ->
            model.name.equals(name, true)
        }
    }

}