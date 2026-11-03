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
import com.t8rin.gmic.filters.DrosteMirror
import com.t8rin.gmic.filters.GmicBoundary
import com.t8rin.gmic.filters.StereographicProjection
import com.t8rin.gmic.filters.StereographicTransform
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.StereographicProjection.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class StereographicProjectionFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.StereographicProjection {

    override fun createFilter(): GmicFilter = StereographicProjection(
        transform = StereographicTransform.valueOf(value.settings[0]),
        centerX = value.settings[1].toFloat(),
        centerY = value.settings[2].toFloat(),
        radiusPointX = value.settings[3].toFloat(),
        radiusPointY = value.settings[4].toFloat(),
        horizonLeveling = value.settings[5].toFloat(),
        sideBlur = value.settings[6].toFloat(),
        dilation = value.settings[7].toFloat(),
        mirror = DrosteMirror.valueOf(value.settings[8]),
        boundary = GmicBoundary.valueOf(value.settings[9])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_stereographic_projection,
            R.string.filter_help_gmic_filter_stereographic_projection
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicSelection(
                    R.string.gmic_param_transform,
                    "Direct",
                    listOf("Direct", "Inverse")
                ),
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_radius_point_x, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_radius_point_y, 75, 0f..100f),
                gmicNumber(R.string.gmic_param_horizon_leveling, 0, -10f..10f),
                gmicNumber(R.string.gmic_param_side_blur, 0, 0f..20f),
                gmicNumber(R.string.dilation, 0, -2f..2f),
                gmicSelection(
                    R.string.tile_mode_mirror,
                    "None",
                    listOf("None", "XAxis", "YAxis", "BothAxes")
                ),
                gmicSelection(
                    R.string.gmic_param_boundary,
                    "Transparent",
                    listOf("Transparent", "Nearest", "Periodic", "Mirror")
                )
            )
        }
    }
}