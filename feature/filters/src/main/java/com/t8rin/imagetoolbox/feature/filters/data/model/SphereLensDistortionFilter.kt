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

import android.graphics.Bitmap
import com.jhlabs.JhFilter
import com.jhlabs.SphereLensDistortionFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SphereLensDistortion.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.max

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class SphereLensDistortionFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.SphereLensDistortion {

    override fun createFilter(): JhFilter = SphereLensDistortionFilter()

    override fun createFilter(image: Bitmap): JhFilter = SphereLensDistortionFilter().apply {
        refractionIndex = value.refractionIndex
        radius = value.radius * (max(image.width, image.height) / 2f)
        centreX = value.centerX
        centreY = value.centerY
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.sphere_lensh_distortion,
            R.string.filter_help_sphere_lensh_distortion
        )

        override val parameters = structuredParameters {
            Value::refractionIndex.float(R.string.refraction_index, 2.5f, 1f..10f)
            Value::radius.float(R.string.radius, 0.5f, 0f..1f)
            Value::centerX.float(R.string.center_x, 0.5f, 0f..1f)
            Value::centerY.float(R.string.center_y, 0.5f, 0f..1f)
        }
    }
}