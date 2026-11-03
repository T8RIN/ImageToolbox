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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.BlackAndWhiteFilm
import com.t8rin.gmic.filters.BlackAndWhiteFilmGrainType
import com.t8rin.gmic.filters.BlackAndWhiteFilmType
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BlackAndWhiteFilm.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class BlackAndWhiteFilmFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.BlackAndWhiteFilm {

    override fun createFilter(): GmicFilter = BlackAndWhiteFilm(
        filmType = BlackAndWhiteFilmType.valueOf(value.settings[0]),
        redLevel = value.settings[1].toFloat(),
        redSmoothness = value.settings[2].toFloat(),
        greenLevel = value.settings[3].toFloat(),
        greenSmoothness = value.settings[4].toFloat(),
        blueLevel = value.settings[5].toFloat(),
        blueSmoothness = value.settings[6].toFloat(),
        gamma = value.settings[7].toFloat(),
        contrast = value.settings[8].toFloat(),
        brightness = value.settings[9].toFloat(),
        hue = value.settings[10].toFloat(),
        saturation = value.settings[11].toFloat(),
        grainShadows = value.settings[12].toFloat(),
        grainMidtones = value.settings[13].toFloat(),
        grainHighlights = value.settings[14].toFloat(),
        grainToneFading = value.settings[15].toFloat(),
        grainScale = value.settings[16].toFloat(),
        grainType = BlackAndWhiteFilmGrainType.valueOf(value.settings[17]),
        localContrast = value.settings[18].toFloat(),
        radius = value.settings[19].toInt(),
        contrastSmoothness = value.settings[20].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_black_and_white_film,
            R.string.filter_help_gmic_filter_black_and_white_film
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_film_type,
                    "KodakTriX",
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
                gmicNumber(R.string.gmic_param_red_level, 0.299f, 0f..1f),
                gmicNumber(R.string.gmic_param_red_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_green_level, 0.587f, 0f..1f),
                gmicNumber(R.string.gmic_param_green_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_blue_level, 0.114f, 0f..1f),
                gmicNumber(R.string.gmic_param_blue_smoothness, 0, 0f..10f),
                gmicNumber(R.string.gamma, 1.05f, 0.01f..5f),
                gmicNumber(R.string.contrast, 1.2f, 0f..4f),
                gmicNumber(R.string.gmic_param_brightness, 0, -255f..255f),
                gmicNumber(R.string.hue, 30, 0f..360f),
                gmicNumber(R.string.saturation, 0.08f, 0f..1f),
                gmicNumber(R.string.gmic_param_grain_shadows, 18, 0f..200f),
                gmicNumber(R.string.gmic_param_grain_midtones, 10, 0f..200f),
                gmicNumber(R.string.gmic_param_grain_highlights, 5, 0f..200f),
                gmicNumber(R.string.gmic_param_grain_tone_fading, 2, 0f..10f),
                gmicNumber(R.string.gmic_param_grain_scale, 0.8f, 0f..3f),
                gmicSelection(
                    R.string.gmic_param_grain_type,
                    "Gaussian",
                    listOf("Gaussian", "Uniform", "SaltAndPepper", "Poisson")
                ),
                gmicNumber(R.string.gmic_param_local_contrast, 8, 0f..60f),
                gmicInteger(R.string.radius, 16, 1f..512f),
                gmicNumber(R.string.gmic_param_contrast_smoothness, 4, 0f..10f)
            )
        }
    }
}