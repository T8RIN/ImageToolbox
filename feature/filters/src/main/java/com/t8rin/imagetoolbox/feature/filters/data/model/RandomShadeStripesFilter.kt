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
import com.t8rin.gmic.filters.RandomShadeStripes
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.GmicValueAction
import com.t8rin.gmic.filters.StripeOrientation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class RandomShadeStripesFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("30", "Vertical", "0.8", "1.3", "All", "None")
    )
) : GMICFilterTransformation(), Filter.RandomShadeStripes {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = RandomShadeStripes(
        frequency = value[0].toFloat(),
        orientation = StripeOrientation.valueOf(value[1]),
        darkness = value[2].toFloat(),
        lightness = value[3].toFloat(),
        channel = GmicChannel.valueOf(value[4]),
        valueAction = GmicValueAction.valueOf(value[5])
    )

}
