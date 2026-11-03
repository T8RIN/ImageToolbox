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
import com.t8rin.gmic.filters.PolaroidFrame
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PolaroidFrame.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.FRAMES)
internal class PolaroidFrameFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.PolaroidFrame {

    override fun createFilter(): GmicFilter = PolaroidFrame(
        frameSize = value.settings[0].toInt(),
        bottomSize = value.settings[1].toInt(),
        shadowX = value.settings[2].toFloat(),
        shadowY = value.settings[3].toFloat(),
        shadowSmoothness = value.settings[4].toFloat(),
        curvatureX = value.settings[5].toFloat(),
        curvatureY = value.settings[6].toFloat(),
        angle = value.settings[7].toFloat(),
        vignetteStrength = value.settings[8].toFloat(),
        vignetteMinRadius = value.settings[9].toFloat(),
        vignetteMaxRadius = value.settings[10].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_polaroid_frame,
            R.string.filter_help_gmic_filter_polaroid_frame
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_frame_size, 10, 0f..400f),
                gmicInteger(R.string.gmic_param_bottom_size, 20, 0f..400f),
                gmicNumber(R.string.gmic_param_x_shadow, 2, -20f..20f),
                gmicNumber(R.string.gmic_param_y_shadow, 3, -20f..20f),
                gmicNumber(R.string.gmic_param_smoothness, 3, 0f..5f),
                gmicNumber(R.string.gmic_param_x_curvature, 0, 0f..1f),
                gmicNumber(R.string.gmic_param_y_curvature, 0, 0f..1f),
                gmicNumber(R.string.angle, 8, -180f..180f),
                gmicNumber(R.string.gmic_param_vignette_strength, 50, 0f..255f),
                gmicNumber(R.string.gmic_param_vignette_min_radius, 70, 0f..100f),
                gmicNumber(R.string.gmic_param_vignette_max_radius, 95, 0f..100f)
            )
        }
    }
}