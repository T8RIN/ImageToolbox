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
class UiBandingDenoiseFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("5", "5", "5", "10", "NoTiling")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_banding_denoise,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_v_cutoff, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_h_cutoff, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_category_space, 0f..20f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_value, 0f..100f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_size_of_tiles,
            listOf("NoTiling", "Px64", "Px128", "Px256", "Px512", "Px1024", "Px2048")
        )
    )
), Filter.BandingDenoise
