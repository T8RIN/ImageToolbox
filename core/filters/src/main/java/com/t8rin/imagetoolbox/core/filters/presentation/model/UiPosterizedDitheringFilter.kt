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

@UiFilterInject(group = UiFilterInject.Groups.DITHERING)
class UiPosterizedDitheringFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("1", "1", "0", "0", "20", "1", "ColorDoping", "1")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_posterized_dithering,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gamma, 0.01f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, 0f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.brightness, -255f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_quantize_colors, 2f..255f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_smooth_colors, 0f..30f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_mixer_mode,
            listOf("ColorDoping", "Darken", "SoftLight", "GrainMerge", "Multiply", "Value")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_color_intensity, 0f..1f, isInteger = false)
    )
), Filter.PosterizedDithering
