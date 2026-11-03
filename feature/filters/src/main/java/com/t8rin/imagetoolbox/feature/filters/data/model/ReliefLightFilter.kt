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
import com.t8rin.gmic.filters.ReliefLight
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ReliefLight.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class ReliefLightFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.ReliefLight {

    override fun createFilter(): GmicFilter = ReliefLight(
        ambientLightness = value.settings[0].toFloat(),
        specularLightness = value.settings[1].toFloat(),
        specularSize = value.settings[2].toFloat(),
        darkness = value.settings[3].toFloat(),
        lightSmoothness = value.settings[4].toFloat(),
        lightX = value.settings[5].toFloat(),
        lightY = value.settings[6].toFloat(),
        lightZ = value.settings[7].toFloat(),
        zScale = value.settings[8].toFloat(),
        opacityAsHeightmap = value.settings[9].toBoolean(),
        imageSmoothness = value.settings[10].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_relief_light,
            R.string.filter_help_gmic_filter_relief_light
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_ambient_lightness, 0.3f, 0f..5f),
                gmicNumber(R.string.gmic_param_specular_lightness, 0.2f, 0f..2f),
                gmicNumber(R.string.gmic_param_specular_size, 0.2f, 0f..1f),
                gmicNumber(R.string.texture_darkness, 0, 0f..1f),
                gmicNumber(R.string.gmic_param_light_smoothness, 1, 0f..5f),
                gmicNumber(R.string.gmic_param_light_x, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_light_y, 50, 0f..100f),
                gmicNumber(R.string.gmic_param_light_z, 5, 0f..20f),
                gmicNumber(R.string.scale, 0.5f, 0f..3f),
                gmicToggle(R.string.gmic_param_opacity_as_heightmap, false),
                gmicNumber(R.string.gmic_param_image_smoothness, 0, 0f..10f)
            )
        }
    }
}