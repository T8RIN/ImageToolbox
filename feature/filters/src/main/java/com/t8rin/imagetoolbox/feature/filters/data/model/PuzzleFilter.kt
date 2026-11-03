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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Puzzle.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class PuzzleFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Puzzle {

    override fun createFilter(): GmicFilter = Puzzle(
        xTiles = value.settings[0].toInt(),
        yTiles = value.settings[1].toInt(),
        curvature = value.settings[2].toFloat(),
        connectorCentering = value.settings[3].toFloat(),
        connectorVariability = value.settings[4].toFloat(),
        reliefSmoothness = value.settings[5].toFloat(),
        reliefContrast = value.settings[6].toFloat(),
        outlineSmoothness = value.settings[7].toFloat(),
        outlineContrast = value.settings[8].toFloat(),
        scale = value.settings[9].toFloat(),
        scaleVariation = value.settings[10].toFloat(),
        angle = value.settings[11].toFloat(),
        angleVariation = value.settings[12].toFloat(),
        shufflePieces = value.settings[13].toBoolean(),
        additionalOutline = value.settings[14].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_puzzle, R.string.filter_help_gmic_filter_puzzle)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_x_tiles, 5, 2f..32f),
                gmicInteger(R.string.gmic_param_y_tiles, 5, 2f..32f),
                gmicNumber(R.string.curvature, 0.5f, 0f..1.5f),
                gmicNumber(R.string.gmic_param_connector_centering, 0, 0f..1f),
                gmicNumber(R.string.gmic_param_connector_variability, 0, 0f..2f),
                gmicNumber(R.string.gmic_param_relief_smoothness, 0.3f, 0f..3f),
                gmicNumber(R.string.gmic_param_relief_contrast, 100, 0f..255f),
                gmicNumber(R.string.gmic_param_outline_smoothness, 0.2f, 0f..3f),
                gmicNumber(R.string.gmic_param_outline_contrast, 255, 0f..255f),
                gmicNumber(R.string.scale, 100, 0f..150f),
                gmicNumber(R.string.gmic_param_scale_variation, 0, 0f..100f),
                gmicNumber(R.string.angle, 0, -180f..180f),
                gmicNumber(R.string.variation, 0, 0f..180f),
                gmicToggle(R.string.gmic_param_shuffle_pieces, false),
                gmicToggle(R.string.add_outline, false)
            )
        }
    }
}