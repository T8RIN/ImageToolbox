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

import com.jhlabs.JhFilter
import com.jhlabs.KaleidoscopeFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Kaleidoscope.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class KaleidoscopeFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Kaleidoscope {

    override fun createFilter(): JhFilter = KaleidoscopeFilter().apply {
        angle = value.angle
        angle2 = value.angle2
        centreX = value.centreX
        centreY = value.centreY
        sides = value.sides
        radius = value.radius
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.kaleidoscope, R.string.filter_help_kaleidoscope)

        override val parameters = structuredParameters {
            Value::angle.float(R.string.angle, 0f, 0f..360f, roundTo = 0)
            Value::angle2.float(R.string.secondary_angle, 0f, 0f..360f, roundTo = 0)
            Value::centreX.float(R.string.center_x, 0.5f, 0f..1f)
            Value::centreY.float(R.string.center_y, 0.5f, 0f..1f)
            Value::sides.int(R.string.sides, 5, 2..12)
            Value::radius.float(R.string.radius, 0f, 0f..100f)
        }
    }
}