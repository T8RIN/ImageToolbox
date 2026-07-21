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
class UiPixelSortFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("Increasing", "X", "Red", "Criterion", "0", "100", "0", "false")
    )
) : GmicUiFilter(
    title = R.string.pixel_sort,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(R.string.order, listOf("Decreasing", "Increasing")),
        GmicParameterInfo.Selection(R.string.gmic_param_axis, listOf("X", "Y", "XThenY", "YThenX")),
        GmicParameterInfo.Selection(
            R.string.gmic_param_criterion,
            listOf(
                "Red",
                "Green",
                "Blue",
                "Intensity",
                "Luminance",
                "Lightness",
                "Hue",
                "Saturation",
                "Minimum",
                "Maximum",
                "Random"
            )
        ),
        GmicParameterInfo.Selection(R.string.mask, listOf("Criterion", "Contours", "Random")),
        GmicParameterInfo.Number(R.string.gmic_param_lower_threshold, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_upper_threshold, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.softness, 0f..5f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_invert_mask)
    )
), Filter.PixelSort
