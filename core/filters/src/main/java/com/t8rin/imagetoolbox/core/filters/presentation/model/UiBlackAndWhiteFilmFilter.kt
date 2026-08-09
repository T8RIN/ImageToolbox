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

@UiFilterInject(group = UiFilterInject.Groups.COLOR)
class UiBlackAndWhiteFilmFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "KodakTriX",
            "0.299",
            "0",
            "0.587",
            "0",
            "0.114",
            "0",
            "1.05",
            "1.2",
            "0",
            "30",
            "0.08",
            "18",
            "10",
            "5",
            "2",
            "0.8",
            "Gaussian",
            "8",
            "16",
            "4"
        )
    )
) : GmicUiFilter(
    title = R.string.gmic_filter_black_and_white_film,
    value = value,
    gmicParamsInfo = listOf(
        GmicParameterInfo.Selection(
            R.string.gmic_param_film_type,
            listOf(
                "Manual",
                "Agfa200X",
                "Agfapan25",
                "Agfapan100",
                "Agfapan400",
                "IlfordDelta100",
                "IlfordDelta400",
                "IlfordDelta3200",
                "IlfordFp4",
                "IlfordHp4",
                "IlfordPanF",
                "IlfordSfx",
                "IlfordXp2Super",
                "KodakTmax100",
                "KodakTmax400",
                "KodakTriX"
            )
        ),
        GmicParameterInfo.Number(R.string.gmic_param_red_level, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_red_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_green_level, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_green_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_blue_level, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_blue_smoothness, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gamma, 0.01f..5f, isInteger = false),
        GmicParameterInfo.Number(R.string.contrast, 0f..4f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_brightness, -255f..255f, isInteger = false),
        GmicParameterInfo.Number(R.string.hue, 0f..360f, isInteger = false),
        GmicParameterInfo.Number(R.string.saturation, 0f..1f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_grain_shadows, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_grain_midtones, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_grain_highlights, 0f..200f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_grain_tone_fading, 0f..10f, isInteger = false),
        GmicParameterInfo.Number(R.string.gmic_param_grain_scale, 0f..3f, isInteger = false),
        GmicParameterInfo.Selection(
            R.string.gmic_param_grain_type,
            listOf("Gaussian", "Uniform", "SaltAndPepper", "Poisson")
        ),
        GmicParameterInfo.Number(R.string.gmic_param_local_contrast, 0f..60f, isInteger = false),
        GmicParameterInfo.Number(R.string.radius, 1f..512f, isInteger = true),
        GmicParameterInfo.Number(
            R.string.gmic_param_contrast_smoothness,
            0f..10f,
            isInteger = false
        )
    )
), Filter.BlackAndWhiteFilm
