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
class UiMarkerDrawingFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("70", "4", "10", "15", "100", "85", "0.5", "3", "3", "Color", "20", "-1")
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_marker_drawing,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_length, 1f..256f, isInteger = true),
        GmicParameterInfo.Number(R.string.radius, 1f..32f, isInteger = false),
        GmicParameterInfo.Number(R.string.opacity, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_inertia, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_curviness, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.texture_anisotropy, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_coherence, 0f..16f, isInteger = false),
        GmicParameterInfo.Number(R.string.iterations, 1f..64f, isInteger = true),
        GmicParameterInfo.Selection(R.string.gmic_param_background, listOf("Blur", "Color")),
        GmicParameterInfo.Number(R.string.gmic_param_background_blur, 0f..128f, isInteger = false),
        GmicParameterInfo.Color(R.string.background_color)
    )
), Filter.MarkerDrawing
