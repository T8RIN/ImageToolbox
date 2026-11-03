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
import com.t8rin.gmic.filters.CubeFaceOrientation
import com.t8rin.gmic.filters.CubeFrame
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CubeFrame.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.FRAMES)
internal class CubeFrameFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.CubeFrame {

    override fun createFilter(): GmicFilter = CubeFrame(
        depth = value.settings[0].toFloat(),
        centerX = value.settings[1].toFloat(),
        centerY = value.settings[2].toFloat(),
        leftOrientation = CubeFaceOrientation.valueOf(value.settings[3]),
        rightOrientation = CubeFaceOrientation.valueOf(value.settings[4]),
        upperOrientation = CubeFaceOrientation.valueOf(value.settings[5]),
        lowerOrientation = CubeFaceOrientation.valueOf(value.settings[6])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_cube_frame, R.string.filter_help_gmic_filter_cube_frame)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.texture_depth, 3, 0f..30f),
                gmicNumber(R.string.center_x, 50, 0f..100f),
                gmicNumber(R.string.center_y, 50, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_left_orientation,
                    "Normal",
                    listOf("Normal", "MirrorX", "MirrorY", "MirrorXY")
                ),
                gmicSelection(
                    R.string.gmic_param_right_orientation,
                    "Normal",
                    listOf("Normal", "MirrorX", "MirrorY", "MirrorXY")
                ),
                gmicSelection(
                    R.string.gmic_param_upper_orientation,
                    "Normal",
                    listOf("Normal", "MirrorX", "MirrorY", "MirrorXY")
                ),
                gmicSelection(
                    R.string.gmic_param_lower_orientation,
                    "Normal",
                    listOf("Normal", "MirrorX", "MirrorY", "MirrorXY")
                )
            )
        }
    }
}