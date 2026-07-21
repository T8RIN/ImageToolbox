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

@UiFilterInject(group = UiFilterInject.Groups.PIXELATION)
class UiStringifyFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "2",
            "32",
            "20",
            "ThreePoints",
            "100",
            "32",
            "20",
            "25",
            "false",
            "-16777216"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_stringify,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..10f, isInteger = true),
        GmicParameterInfo.Number(R.string.levels, 2f..64f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_sampling_rate, 0f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_link,
            listOf("TwoPoints", "ThreePoints", "FourPoints", "AllPoints")
        ),
        GmicParameterInfo.Number(R.string.offset, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.threshold, 0f..1024f, isInteger = true),
        GmicParameterInfo.Number(R.string.opacity, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_normalization, 0f..100f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_fill_holes),
        GmicParameterInfo.Color(R.string.background_color)
    )
), Filter.Stringify
