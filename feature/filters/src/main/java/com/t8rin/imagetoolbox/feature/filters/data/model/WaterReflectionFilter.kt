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
import com.t8rin.gmic.filters.WaterReflection
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class WaterReflectionFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("50", "1", "1080991934", "0", "1.5", "0", "-3.3", "7", "1.5")
    )
) : GMICFilterTransformation(), Filter.WaterReflection {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = WaterReflection(
        height = value[0].toFloat(),
        attenuation = value[1].toFloat(),
        color = value[2].toInt(),
        wavesAmplitude = value[3].toFloat(),
        wavesSmoothness = value[4].toFloat(),
        xAngle = value[5].toFloat(),
        yAngle = value[6].toFloat(),
        focalLength = value[7].toFloat(),
        zoom = value[8].toFloat()
    )

}
