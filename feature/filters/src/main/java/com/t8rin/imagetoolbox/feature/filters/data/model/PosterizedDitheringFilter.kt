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
import com.t8rin.gmic.filters.PosterizedDithering
import com.t8rin.gmic.filters.PosterizedDitheringMixerMode
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PosterizedDithering.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DITHERING)
internal class PosterizedDitheringFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PosterizedDithering {

    override fun createFilter(): GmicFilter = PosterizedDithering(
        gamma = value.settings[0].toFloat(),
        contrast = value.settings[1].toFloat(),
        brightness = value.settings[2].toFloat(),
        smoothness = value.settings[3].toFloat(),
        quantizeColors = value.settings[4].toInt(),
        smoothColors = value.settings[5].toFloat(),
        mixerMode = PosterizedDitheringMixerMode.valueOf(value.settings[6]),
        colorIntensity = value.settings[7].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_posterized_dithering,
            R.string.filter_help_gmic_filter_posterized_dithering
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gamma, 1, 0.01f..5f),
                gmicNumber(R.string.contrast, 1, 0f..4f),
                gmicNumber(R.string.brightness, 0, -255f..255f),
                gmicNumber(R.string.gmic_param_smoothness, 0, 0f..10f),
                gmicInteger(R.string.gmic_param_quantize_colors, 20, 2f..255f),
                gmicNumber(R.string.gmic_param_smooth_colors, 1, 0f..30f),
                gmicSelection(
                    R.string.gmic_param_mixer_mode,
                    "ColorDoping",
                    listOf(
                        "ColorDoping",
                        "Darken",
                        "SoftLight",
                        "GrainMerge",
                        "Multiply",
                        "Value"
                    )
                ),
                gmicNumber(R.string.gmic_param_color_intensity, 1, 0f..1f)
            )
        }
    }
}