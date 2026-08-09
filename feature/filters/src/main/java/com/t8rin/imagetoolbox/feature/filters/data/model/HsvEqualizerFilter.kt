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
import com.t8rin.gmic.filters.HsvEqualizer
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class HsvEqualizerFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "25",
            "55",
            "8",
            "0.2",
            "0.04",
            "200",
            "70",
            "-6",
            "0.14",
            "-0.03",
            "330",
            "45",
            "4",
            "0.1",
            "0.06"
        )
    )
) : GMICFilterTransformation(), Filter.HsvEqualizer {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = HsvEqualizer(
        firstHueBand = value[0].toFloat(),
        firstBandWidth = value[1].toFloat(),
        firstHueShift = value[2].toFloat(),
        firstSaturationCorrection = value[3].toFloat(),
        firstValueCorrection = value[4].toFloat(),
        secondHueBand = value[5].toFloat(),
        secondBandWidth = value[6].toFloat(),
        secondHueShift = value[7].toFloat(),
        secondSaturationCorrection = value[8].toFloat(),
        secondValueCorrection = value[9].toFloat(),
        thirdHueBand = value[10].toFloat(),
        thirdBandWidth = value[11].toFloat(),
        thirdHueShift = value[12].toFloat(),
        thirdSaturationCorrection = value[13].toFloat(),
        thirdValueCorrection = value[14].toFloat()
    )
}
