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

@UiFilterInject(group = UiFilterInject.Groups.EFFECTS)
class UiDreamPaintingFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("4", "2", "1.7", "40", "true", "45", "35", "45", "35", "12")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_dream_painting,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_abstraction, 1f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_details_scale, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_color_strength, 0f..4f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_paint_smoothness,
            0f..1000f,
            isInteger = false
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_sharpen_shades),
        GmicParameterInfo.Number(R.string.gmic_param_bloom_strength, 0f..400f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_bloom_scale, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_bloom_smoothness, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.highlights, 0f..100f, isInteger = true),
        GmicParameterInfo.Number(R.string.contrast, 0f..100f, isInteger = false)
    )
), Filter.DreamPainting
