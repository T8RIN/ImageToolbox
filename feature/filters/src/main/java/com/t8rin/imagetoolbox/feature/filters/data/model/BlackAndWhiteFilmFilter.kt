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
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class BlackAndWhiteFilmFilter(
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
) : GMICFilterTransformation(), Filter.BlackAndWhiteFilm {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = BlackAndWhiteFilm(
        filmType = BlackAndWhiteFilmType.valueOf(value[0]),
        redLevel = value[1].toFloat(),
        redSmoothness = value[2].toFloat(),
        greenLevel = value[3].toFloat(),
        greenSmoothness = value[4].toFloat(),
        blueLevel = value[5].toFloat(),
        blueSmoothness = value[6].toFloat(),
        gamma = value[7].toFloat(),
        contrast = value[8].toFloat(),
        brightness = value[9].toFloat(),
        hue = value[10].toFloat(),
        saturation = value[11].toFloat(),
        grainShadows = value[12].toFloat(),
        grainMidtones = value[13].toFloat(),
        grainHighlights = value[14].toFloat(),
        grainToneFading = value[15].toFloat(),
        grainScale = value[16].toFloat(),
        grainType = BlackAndWhiteFilmGrainType.valueOf(value[17]),
        localContrast = value[18].toFloat(),
        radius = value[19].toInt(),
        contrastSmoothness = value[20].toFloat()
    )

}
