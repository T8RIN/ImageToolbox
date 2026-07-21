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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.UiFilterInject
import com.t8rin.imagetoolbox.core.resources.R

@UiFilterInject(group = UiFilterInject.Groups.DISTORTION)
class UiHuffmanGlitchFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("30", "None", "25", "0", "Rgb", "0", "0")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_huffman_glitch,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_noise_level, 0f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_split_mode,
            listOf("None", "HorizontalBlocks", "VerticalBlocks", "Patches")
        ),
        GmicParameterInfo.Number(R.string.block_size, 0f..100f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_patch_overlap, 0f..50f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.tag_color_space,
            listOf(
                "Rgb",
                "Cmyk",
                "Hcy",
                "Hsi",
                "Hsl",
                "Hsv",
                "Jzazbz",
                "Lab",
                "Lch",
                "OkLab",
                "YCbCr",
                "Yiq"
            )
        ),
        GmicParameterInfo.Number(R.string.gmic_param_quantization, 0f..64f, isInteger = true),
        GmicParameterInfo.Number(R.string.seed, 0f..65536f, isInteger = true)
    )
), Filter.HuffmanGlitch
