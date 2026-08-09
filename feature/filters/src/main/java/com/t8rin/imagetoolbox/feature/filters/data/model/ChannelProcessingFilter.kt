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
import com.t8rin.gmic.filters.ChannelProcessing
import com.t8rin.gmic.filters.ChannelProcessingTonesRange
import com.t8rin.gmic.filters.ChannelProcessingValueAction
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class ChannelProcessingFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "12",
            "0",
            "0",
            "NoneOption",
            "0",
            "100",
            "256",
            "false",
            "false",
            "Midtones",
            "2",
            "Luminance"
        )
    )
) : GMICFilterTransformation(), Filter.ChannelProcessing {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = ChannelProcessing(
        brightness = value[0].toFloat(),
        contrast = value[1].toFloat(),
        gamma = value[2].toFloat(),
        smoothness = value[3].toFloat(),
        valueAction = ChannelProcessingValueAction.valueOf(value[4]),
        lowValue = value[5].toFloat(),
        highValue = value[6].toFloat(),
        quantization = value[7].toInt(),
        equalization = value[8].toBoolean(),
        negation = value[9].toBoolean(),
        tonesRange = ChannelProcessingTonesRange.valueOf(value[10]),
        tonesSmoothness = value[11].toFloat(),
        channelS = GmicChannel.valueOf(value[12])
    )

}
