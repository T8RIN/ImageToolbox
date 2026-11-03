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
import com.jhlabs.ShearFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Shear.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class ShearFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Shear {

    override fun createFilter(): JhFilter = ShearFilter().apply {
        xAngle = Math.toRadians(value.xAngle.toDouble()).toFloat()
        yAngle = Math.toRadians(value.yAngle.toDouble()).toFloat()
        isResize = value.resize
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.shear, R.string.filter_help_shear)

        override val parameters = structuredParameters {
            Value::xAngle.float(R.string.x_angle, 15f, -89f..89f, 0)
            Value::yAngle.float(R.string.y_angle, 0f, -89f..89f, 0)
            Value::resize.boolean(R.string.resize, true)
        }
    }
}