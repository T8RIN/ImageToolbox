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
import com.t8rin.gmic.filters.Taquin
import com.t8rin.gmic.filters.RemovedTaquinTile
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class TaquinFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf("7", "7", "None", "50", "5", "0", "-16777216", "0")
    )
) : GMICFilterTransformation(), Filter.Taquin {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Taquin(
        xTiles = value[0].toInt(),
        yTiles = value[1].toInt(),
        removedTile = RemovedTaquinTile.valueOf(value[2]),
        relief = value[3].toFloat(),
        borderThickness = value[4].toFloat(),
        borderOutline = value[5].toInt(),
        outlineColor = value[6].toInt(),
        randomSeed = value[7].toInt()
    )

}
