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
import com.t8rin.gmic.filters.MarkerDrawing
import com.t8rin.gmic.filters.MarkerBackground
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class MarkerDrawingFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("70", "4", "10", "15", "100", "85", "0.5", "3", "3", "Color", "20", "-1")
    )
) : GMICFilterTransformation(), Filter.MarkerDrawing {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = MarkerDrawing(
        length = value[0].toInt(),
        radius = value[1].toFloat(),
        opacity = value[2].toFloat(),
        inertia = value[3].toFloat(),
        curviness = value[4].toFloat(),
        anisotropy = value[5].toFloat(),
        smoothness = value[6].toFloat(),
        coherence = value[7].toFloat(),
        iterations = value[8].toInt(),
        background = MarkerBackground.valueOf(value[9]),
        backgroundBlur = value[10].toFloat(),
        backgroundColor = value[11].toInt()
    )

}
