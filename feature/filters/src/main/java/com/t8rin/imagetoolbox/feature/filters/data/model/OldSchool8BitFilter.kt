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
import com.t8rin.gmic.filters.OldSchool8Bit
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.OldSchool8Bit.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class OldSchool8BitFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.OldSchool8Bit {

    override fun createFilter(): GmicFilter = OldSchool8Bit(
        scale = value.settings[0].toFloat(),
        dithering = value.settings[1].toFloat(),
        levels = value.settings[2].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_old_school8_bit,
            R.string.filter_help_gmic_filter_old_school8_bit
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.scale, 25, 1f..100f),
                gmicNumber(R.string.dithering, 800, 0f..10000f),
                gmicInteger(R.string.levels, 16, 2f..256f)
            )
        }
    }
}