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
import com.t8rin.gmic.filters.RemovedTaquinTile
import com.t8rin.gmic.filters.Taquin
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Taquin.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class TaquinFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Taquin {

    override fun createFilter(): GmicFilter = Taquin(
        xTiles = value.settings[0].toInt(),
        yTiles = value.settings[1].toInt(),
        removedTile = RemovedTaquinTile.valueOf(value.settings[2]),
        relief = value.settings[3].toFloat(),
        borderThickness = value.settings[4].toFloat(),
        borderOutline = value.settings[5].toInt(),
        outlineColor = value.settings[6].toInt(),
        randomSeed = value.settings[7].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_taquin, R.string.filter_help_gmic_filter_taquin)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_x_tiles, 7, 1f..20f),
                gmicInteger(R.string.gmic_param_y_tiles, 7, 1f..20f),
                gmicSelection(
                    R.string.gmic_param_removed_tile,
                    "None",
                    listOf("None", "First", "Last", "Random")
                ),
                gmicNumber(R.string.gmic_param_relief, 50, 0f..255f),
                gmicNumber(R.string.border_thickness, 5, 0f..100f),
                gmicInteger(R.string.gmic_param_border_outline, 0, 0f..16f),
                gmicColor(R.string.outline_color, -16777216),
                gmicSeed(R.string.seed, 0, 0f..65535f)
            )
        }
    }
}