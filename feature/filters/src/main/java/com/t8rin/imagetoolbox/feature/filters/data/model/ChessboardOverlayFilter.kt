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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ChessboardOverlay.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class ChessboardOverlayFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ChessboardOverlay {

    override fun createFilter(): GmicFilter = ChessboardOverlay(
        firstSize = value.settings[0].toInt(),
        secondSize = value.settings[1].toInt(),
        firstOffset = value.settings[2].toInt(),
        secondOffset = value.settings[3].toInt(),
        angle = value.settings[4].toFloat(),
        opacity = value.settings[5].toFloat(),
        firstColor = value.settings[6].toInt(),
        secondColor = value.settings[7].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_chessboard_overlay,
            R.string.filter_help_gmic_filter_chessboard_overlay
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_first_size, 64, 1f..512f),
                gmicInteger(R.string.gmic_param_second_size, 64, 1f..512f),
                gmicInteger(R.string.gmic_param_first_offset, 0, 0f..512f),
                gmicInteger(R.string.gmic_param_second_offset, 0, 0f..512f),
                gmicNumber(R.string.angle, 0, 0f..180f),
                gmicNumber(R.string.opacity, 0.25f, 0f..1f),
                gmicColor(R.string.first_color, -16777216),
                gmicColor(R.string.second_color, -1)
            )
        }
    }
}