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
import com.t8rin.gmic.filters.Deblur
import com.t8rin.gmic.filters.DeblurRegularization
import com.t8rin.gmic.filters.GmicChannel
import com.t8rin.gmic.filters.GmicParallelism
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class DeblurFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("2", "10", "20", "0.1", "MeanCurvature", "All", "Auto", "24")
    )
) : GMICFilterTransformation(), Filter.Deblur {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Deblur(
        radius = value[0].toFloat(),
        iterations = value[1].toInt(),
        timeStep = value[2].toFloat(),
        smoothness = value[3].toFloat(),
        regularization = DeblurRegularization.valueOf(value[4]),
        channel = GmicChannel.valueOf(value[5]),
        parallelism = GmicParallelism.valueOf(value[6]),
        spatialOverlap = value[7].toInt()
    )

}
