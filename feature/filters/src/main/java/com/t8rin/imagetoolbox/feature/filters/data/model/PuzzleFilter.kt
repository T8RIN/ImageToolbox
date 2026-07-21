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
import com.t8rin.gmic.filters.Puzzle
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterInject
internal class PuzzleFilter(
    override val value: GmicFilterParams = GmicFilterParams(
        values = listOf(
            "5",
            "5",
            "0.5",
            "0",
            "0",
            "0.3",
            "100",
            "0.2",
            "255",
            "100",
            "0",
            "0",
            "0",
            "false",
            "false"
        )
    )
) : GMICFilterTransformation(), Filter.Puzzle {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GmicFilter = Puzzle(
        xTiles = value[0].toInt(),
        yTiles = value[1].toInt(),
        curvature = value[2].toFloat(),
        connectorCentering = value[3].toFloat(),
        connectorVariability = value[4].toFloat(),
        reliefSmoothness = value[5].toFloat(),
        reliefContrast = value[6].toFloat(),
        outlineSmoothness = value[7].toFloat(),
        outlineContrast = value[8].toFloat(),
        scale = value[9].toFloat(),
        scaleVariation = value[10].toFloat(),
        angle = value[11].toFloat(),
        angleVariation = value[12].toFloat(),
        shufflePieces = value[13].toBoolean(),
        additionalOutline = value[14].toBoolean()
    )

}
