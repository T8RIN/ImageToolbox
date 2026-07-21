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
import com.t8rin.gmic.filters.SmoothSkin
import com.t8rin.gmic.filters.SkinEstimation
import com.t8rin.gmic.filters.SkinSmoothnessType
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class SmoothSkinFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "Automatic",
            "0.5",
            "1",
            "1",
            "true",
            "50",
            "50",
            "5",
            "2",
            "0.2",
            "3",
            "Bilateral",
            "0.05"
        )
    )
) : GMICFilterTransformation(), Filter.SmoothSkin {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = SmoothSkin(
        skinEstimation = SkinEstimation.valueOf(value[0]),
        tolerance = value[1].toFloat(),
        maskSmoothness = value[2].toFloat(),
        threshold = value[3].toFloat(),
        preNormalize = value[4].toBoolean(),
        manualX = value[5].toFloat(),
        manualY = value[6].toFloat(),
        manualRadius = value[7].toFloat(),
        baseScale = value[8].toFloat(),
        fineScale = value[9].toFloat(),
        detailSmoothness = value[10].toFloat(),
        smoothnessType = SkinSmoothnessType.valueOf(value[11]),
        gain = value[12].toFloat()
    )

}
