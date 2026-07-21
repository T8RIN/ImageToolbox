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

@UiFilterInject(group = UiFilterInject.Groups.LIGHT)
class UiSmoothSkinFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Automatic",
            "0.5",
            "1",
            "1",
            "true",
            "50",
            "50",
            "5",
            "2",
            "0.2",
            "3",
            "Bilateral",
            "0.05"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_smooth_skin,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_skin_estimation,
            listOf("None", "Manual", "Automatic")
        ),
        GmicParameterInfo.Number(R.string.tolerance, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_mask_smoothness, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.threshold, 0f..10f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_pre_normalize),
        GmicParameterInfo.Number(R.string.gmic_param_manual_x, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_manual_y, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.radius, 0f..25f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_base_scale, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fine_scale, 0f..0.8f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_detail_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_smoothness_type,
            listOf("Gaussian", "Bilateral")
        ),
        GmicParameterInfo.Number(R.string.gain, 0f..0.5f, isInteger = false)
    )
), Filter.SmoothSkin
