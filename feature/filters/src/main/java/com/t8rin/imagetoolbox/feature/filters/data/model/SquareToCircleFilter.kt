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
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.GmicInterpolation
import com.t8rin.gmic.filters.SquareCircleMode
import com.t8rin.gmic.filters.SquareToCircle
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SquareToCircle.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class SquareToCircleFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.SquareToCircle {

    override fun createFilter(): GmicFilter = SquareToCircle(
        mode = SquareCircleMode.valueOf(value.settings[0]),
        centerX = value.settings[1].toFloat(),
        centerY = value.settings[2].toFloat(),
        strength = value.settings[3].toFloat(),
        zoom = value.settings[4].toFloat(),
        angle = value.settings[5].toFloat(),
        interpolation = GmicInterpolation.valueOf(value.settings[6]),
        boundary = GmicBoundary.valueOf(value.settings[7]),
        adaptToImageRatio = value.settings[8].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_square_to_circle,
            R.string.filter_help_gmic_filter_square_to_circle
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_mode,
                    "SquareToCircle",
                    listOf("SquareToCircle", "CircleToSquare")
                ),
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicNumber(R.string.strength, 100, 0f..100f),
                gmicNumber(R.string.zoom, 0, -100f..100f),
                gmicNumber(R.string.angle, 0, -180f..180f),
                gmicSelection(
                    R.string.gmic_param_interpolation,
                    "Linear",
                    listOf("NearestNeighbor", "Linear")
                ),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Transparent",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                ),
                gmicToggle(R.string.gmic_param_adapt_to_image_ratio, true)
            )
        }
    }
}