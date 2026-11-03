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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.HuffmanColorSpace
import com.t8rin.gmic.filters.HuffmanGlitch
import com.t8rin.gmic.filters.HuffmanSplitMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HuffmanGlitch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class HuffmanGlitchFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.HuffmanGlitch {

    override fun createFilter(): GmicFilter = HuffmanGlitch(
        noiseLevel = value.settings[0].toFloat(),
        splitMode = HuffmanSplitMode.valueOf(value.settings[1]),
        blockSize = value.settings[2].toInt(),
        patchOverlap = value.settings[3].toFloat(),
        colorSpace = HuffmanColorSpace.valueOf(value.settings[4]),
        quantization = value.settings[5].toInt(),
        randomSeed = value.settings[6].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_huffman_glitch,
            R.string.filter_help_gmic_filter_huffman_glitch
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_noise_level, 30, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_split_mode,
                    "None",
                    listOf("None", "HorizontalBlocks", "VerticalBlocks", "Patches")
                ),
                gmicInteger(R.string.block_size, 25, 0f..100f),
                gmicNumber(R.string.gmic_param_patch_overlap, 0, 0f..50f),
                gmicSelection(
                    R.string.tag_color_space,
                    "Rgb",
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
                gmicInteger(R.string.gmic_param_quantization, 0, 0f..64f),
                gmicSeed(R.string.seed, 0, 0f..65536f)
            )
        }
    }
}