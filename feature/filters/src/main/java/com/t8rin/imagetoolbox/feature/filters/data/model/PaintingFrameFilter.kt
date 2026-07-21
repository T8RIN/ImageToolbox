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
import com.t8rin.gmic.filters.PaintingFrame
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class PaintingFrameFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("10", "0.4", "6", "-1980296", "2", "400", "50", "10", "1", "0.5", "123456")
    )
) : GMICFilterTransformation(), Filter.PaintingFrame {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = PaintingFrame(
        size = value[0].toFloat(),
        contrast = value[1].toFloat(),
        smoothness = value[2].toFloat(),
        color = value[3].toInt(),
        vignetteSize = value[4].toFloat(),
        vignetteContrast = value[5].toFloat(),
        defectsContrast = value[6].toFloat(),
        defectsDensity = value[7].toFloat(),
        defectsSize = value[8].toFloat(),
        defectsSmoothness = value[9].toFloat(),
        serialNumber = value[10].toInt()
    )

}
