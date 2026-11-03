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
import com.t8rin.gmic.filters.AnalogDamage
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AnalogDamage.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.GLITCH)
internal class AnalogDamageFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.AnalogDamage {

    override fun createFilter(): GmicFilter = AnalogDamage(
        dirt = value.settings[0].toFloat(),
        monochromeDirt = value.settings[1].toBoolean(),
        aberrationX = value.settings[2].toFloat(),
        aberrationY = value.settings[3].toFloat(),
        aberrationSmoothness = value.settings[4].toFloat(),
        jpegQuality = value.settings[5].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_analog_damage,
            R.string.filter_help_gmic_filter_analog_damage
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.texture_dirt, 18, 0f..100f),
                gmicToggle(R.string.gmic_param_monochrome_dirt, true),
                gmicNumber(R.string.gmic_param_aberration_x, 3, -32f..32f),
                gmicNumber(R.string.gmic_param_aberration_y, 1, -32f..32f),
                gmicNumber(R.string.softness, 0.6f, 0f..10f),
                gmicInteger(R.string.quality, 38, 1f..100f)
            )
        }
    }
}