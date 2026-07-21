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
class UiPuzzleFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "5",
            "0.5",
            "0",
            "0",
            "0.3",
            "100",
            "0.2",
            "255",
            "100",
            "0",
            "0",
            "0",
            "false",
            "false"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_puzzle,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.gmic_param_x_tiles, 2f..32f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_y_tiles, 2f..32f, isInteger = true),
        GmicParameterInfo.Number(R.string.curvature, 0f..1.5f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_connector_centering,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_connector_variability,
            0f..2f,
            isInteger = false
        ),
        GmicParameterInfo.Number(R.string.gmic_param_relief_smoothness, 0f..3f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_relief_contrast, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_outline_smoothness, 0f..3f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_outline_contrast, 0f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.scale, 0f..150f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_scale_variation, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, -180f..180f, isInteger = false),
        GmicParameterInfo.Number(R.string.variation, 0f..180f, isInteger = false),
        GmicParameterInfo.Toggle(R.string.gmic_param_shuffle_pieces),
        GmicParameterInfo.Toggle(R.string.add_outline)
    )
), Filter.Puzzle
