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
import com.t8rin.gmic.filters.WaterReflection
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WaterReflection.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class WaterReflectionFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.WaterReflection {

    override fun createFilter(): GmicFilter = WaterReflection(
        height = value.settings[0].toFloat(),
        attenuation = value.settings[1].toFloat(),
        color = value.settings[2].toInt(),
        wavesAmplitude = value.settings[3].toFloat(),
        wavesSmoothness = value.settings[4].toFloat(),
        xAngle = value.settings[5].toFloat(),
        yAngle = value.settings[6].toFloat(),
        focalLength = value.settings[7].toFloat(),
        zoom = value.settings[8].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_water_reflection,
            R.string.filter_help_gmic_filter_water_reflection
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_height, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_attenuation, 1, 0.1f..4f),
                gmicColor(R.string.color, 1080991934),
                gmicNumber(R.string.amplitude, 0, 0f..100f),
                gmicNumber(R.string.softness, 1.5f, 0f..4f),
                gmicNumber(R.string.x_angle, 0, -10f..10f),
                gmicNumber(R.string.y_angle, -3.3f, -10f..10f),
                gmicNumber(R.string.tag_focal_length, 7, 0f..10f),
                gmicNumber(R.string.zoom, 1.5f, 1f..5f)
            )
        }
    }
}