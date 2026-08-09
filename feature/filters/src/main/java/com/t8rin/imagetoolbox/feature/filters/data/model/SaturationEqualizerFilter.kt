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
import com.t8rin.gmic.filters.SaturationEqualizer
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class SaturationEqualizerFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "-12",
            "-6",
            "4",
            "8",
            "12",
            "8",
            "4",
            "-2",
            "-8",
            "8",
            "14",
            "6",
            "-4",
            "-8",
            "4",
            "10",
            "12",
            "8",
            "0"
        )
    )
) : GMICFilterTransformation(), Filter.SaturationEqualizer {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = SaturationEqualizer(
        black = value[0].toFloat(),
        nearBlack = value[1].toFloat(),
        darkGray = value[2].toFloat(),
        midDarkGray = value[3].toFloat(),
        middleGray = value[4].toFloat(),
        midLightGray = value[5].toFloat(),
        lightGray = value[6].toFloat(),
        highlights = value[7].toFloat(),
        white = value[8].toFloat(),
        hue0 = value[9].toFloat(),
        hue45 = value[10].toFloat(),
        hue90 = value[11].toFloat(),
        hue135 = value[12].toFloat(),
        hue180 = value[13].toFloat(),
        hue225 = value[14].toFloat(),
        hue270 = value[15].toFloat(),
        hue315 = value[16].toFloat(),
        hue360 = value[17].toFloat(),
        rotateHueBands = value[18].toFloat()
    )

}
