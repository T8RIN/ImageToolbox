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

import com.jhlabs.ChannelMixFilter
import com.jhlabs.JhFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ChannelMix.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class ChannelMixFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.ChannelMix {

    override fun createFilter(): JhFilter = ChannelMixFilter().apply {
        blueGreen = value.blueGreen
        redBlue = value.redBlue
        greenRed = value.greenRed
        intoR = value.intoR
        intoG = value.intoG
        intoB = value.intoB
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.channel_mix, R.string.filter_help_channel_mix)

        override val parameters = structuredParameters {
            Value::blueGreen.int(R.string.blue_green, 255, 0..255)
            Value::redBlue.int(R.string.red_blue, 255, 0..255)
            Value::greenRed.int(R.string.green_red, 255, 0..255)
            Value::intoR.int(R.string.into_red, 255, 0..255)
            Value::intoG.int(R.string.into_green, 255, 0..255)
            Value::intoB.int(R.string.into_blue, 127, 0..255)
        }
    }
}