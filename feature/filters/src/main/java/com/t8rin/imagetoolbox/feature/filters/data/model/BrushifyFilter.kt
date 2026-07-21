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
import com.t8rin.gmic.filters.Brushify
import com.t8rin.gmic.filters.BrushLight
import com.t8rin.gmic.filters.BrushShape
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class BrushifyFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Ellipse",
            "0.25",
            "4",
            "64",
            "25",
            "12",
            "0",
            "2",
            "Full",
            "0.2",
            "0.5",
            "30",
            "1",
            "1",
            "1",
            "5",
            "0",
            "0.2"
        )
    )
) : GMICFilterTransformation(), Filter.Brushify {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Brushify(
        shape = BrushShape.valueOf(value[0]),
        ratio = value[1].toFloat(),
        numberOfSizes = value[2].toInt(),
        maximalSize = value[3].toInt(),
        minimalSize = value[4].toFloat(),
        orientations = value[5].toInt(),
        fuzziness = value[6].toFloat(),
        smoothness = value[7].toFloat(),
        light = BrushLight.valueOf(value[8]),
        lightStrength = value[9].toFloat(),
        opacity = value[10].toFloat(),
        density = value[11].toFloat(),
        contourCoherence = value[12].toFloat(),
        orientationCoherence = value[13].toFloat(),
        gradientSmoothness = value[14].toFloat(),
        structureSmoothness = value[15].toFloat(),
        primaryAngle = value[16].toFloat(),
        angleDispersion = value[17].toFloat()
    )

}
