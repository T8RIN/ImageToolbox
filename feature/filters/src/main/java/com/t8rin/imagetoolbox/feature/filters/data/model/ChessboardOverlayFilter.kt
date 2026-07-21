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
import com.t8rin.gmic.filters.ChessboardOverlay
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class ChessboardOverlayFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("64", "64", "0", "0", "0", "0.25", "-16777216", "-1")
    )
) : GMICFilterTransformation(), Filter.ChessboardOverlay {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = ChessboardOverlay(
        firstSize = value[0].toInt(),
        secondSize = value[1].toInt(),
        firstOffset = value[2].toInt(),
        secondOffset = value[3].toInt(),
        angle = value[4].toFloat(),
        opacity = value[5].toFloat(),
        firstColor = value[6].toInt(),
        secondColor = value[7].toInt()
    )

}
