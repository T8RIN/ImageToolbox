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
import com.t8rin.gmic.filters.ToneEnhance
import com.t8rin.gmic.filters.ToneEnhanceChannel
import com.t8rin.gmic.filters.ToneEnhanceValues
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class ToneEnhanceFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "0.6",
            "0.9",
            "0.5",
            "1.1",
            "128",
            "0.4",
            "1",
            "0.6",
            "0.2",
            "3",
            "YCbCr",
            "Cut",
            "false"
        )
    )
) : GMICFilterTransformation(), Filter.ToneEnhance {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = ToneEnhance(
        shadowsDetail = value[0].toFloat(),
        shadowsGamma = value[1].toFloat(),
        highlightsDetail = value[2].toFloat(),
        highlightsGamma = value[3].toFloat(),
        center = value[4].toInt(),
        midpointDetail = value[5].toFloat(),
        midpointGamma = value[6].toFloat(),
        recoveryBoost = value[7].toFloat(),
        recoverySmoothness = value[8].toFloat(),
        detailMaskSmoothness = value[9].toInt(),
        channel = ToneEnhanceChannel.valueOf(value[10]),
        values = ToneEnhanceValues.valueOf(value[11]),
        colorMedian = value[12].toBoolean()
    )

}
