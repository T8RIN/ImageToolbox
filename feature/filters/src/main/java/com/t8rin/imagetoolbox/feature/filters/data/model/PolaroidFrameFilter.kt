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
import com.t8rin.gmic.filters.PolaroidFrame
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class PolaroidFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "20", "2", "3", "3", "0", "0", "8", "50", "70", "95")
    )
) : GMICFilterTransformation(), Filter.PolaroidFrame {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = PolaroidFrame(
        frameSize = value[0].toInt(),
        bottomSize = value[1].toInt(),
        shadowX = value[2].toFloat(),
        shadowY = value[3].toFloat(),
        shadowSmoothness = value[4].toFloat(),
        curvatureX = value[5].toFloat(),
        curvatureY = value[6].toFloat(),
        angle = value[7].toFloat(),
        vignetteStrength = value[8].toFloat(),
        vignetteMinRadius = value[9].toFloat(),
        vignetteMaxRadius = value[10].toFloat()
    )

}
