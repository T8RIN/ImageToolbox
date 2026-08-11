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
class UiBlocks3DFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "32",
            "0",
            "4",
            "1.5",
            "30",
            "60",
            "45",
            "50",
            "50",
            "0",
            "-50",
            "-100",
            "0.5",
            "0.7",
            "true",
            "true",
            "-2147483648"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_blocks_3d,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Number(R.string.resolution, 1f..128f, isInteger = true),
        GmicParameterInfo.Number(R.string.gmic_param_smoothness, 0f..40f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_elevation, -10f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.just_size, 0f..3f, isInteger = false),
        GmicParameterInfo.Number(R.string.angle, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_tilt, 0f..90f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_fov, 1f..90f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_x_centering, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_centering, 0f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_x_light, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_y_light, -100f..100f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_z_light, -100f..0f, isInteger = false),
        GmicParameterInfo.Number(
            R.string.gmic_param_specular_lightness,
            0f..1f,
            isInteger = false
        ),
        GmicParameterInfo.Number(
            R.string.gmic_param_specular_shininess,
            0f..3f,
            isInteger = false
        ),
        GmicParameterInfo.Toggle(R.string.gmic_param_use_light),
        GmicParameterInfo.Toggle(R.string.antialias),
        GmicParameterInfo.Color(R.string.outline_color)
    )
), Filter.Blocks3D
