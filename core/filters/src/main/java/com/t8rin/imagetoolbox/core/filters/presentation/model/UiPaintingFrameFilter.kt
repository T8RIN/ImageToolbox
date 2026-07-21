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
class UiPaintingFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "0.4", "6", "-1980296", "2", "400", "50", "10", "1", "0.5", "123456")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_painting_frame,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.just_size, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..30f, isInteger = false),
        GmicParameterInfo.Color(R.string.color),
        GmicParameterInfo.Number(R.string.gmic_param_vignette_size, 0f..50f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_vignette_contrast,
            0f..1000f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_defects_contrast, 0f..512f, isInteger = false),
        GmicParameterInfo.Number(R.string.density, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_defects_size, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_defects_smoothness,
            0f..20f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_serial_number, 0f..1000000f, isInteger = true)
    )
), Filter.PaintingFrame
