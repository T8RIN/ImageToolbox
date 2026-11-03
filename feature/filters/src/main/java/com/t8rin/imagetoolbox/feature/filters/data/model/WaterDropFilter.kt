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
import com.jhlabs.WaterFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WaterDrop.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.max

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class WaterDropFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.WaterDrop {

    override fun createFilter(): JhFilter = WaterFilter()

    override fun createFilter(image: Bitmap): JhFilter = WaterFilter().apply {
        val imageRadius = max(image.width, image.height) / 2f
        wavelength = value.wavelength * imageRadius
        amplitude = value.amplitude * imageRadius
        phase = Math.toRadians(value.phase.toDouble()).toFloat()
        centreX = value.centreX
        centreY = value.centreY
        radius = value.radius * imageRadius
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.water_drop, R.string.filter_help_water_drop)

        override val parameters = structuredParameters {
            Value::wavelength.float(R.string.wavelength, 0.1f, 0.01f..1f)
            Value::amplitude.float(R.string.amplitude, 0.05f, 0f..0.5f)
            Value::phase.float(R.string.phase, 0f, 0f..360f, roundTo = 0)
            Value::centreX.float(R.string.center_x, 0.5f, 0f..1f)
            Value::centreY.float(R.string.center_y, 0.5f, 0f..1f)
            Value::radius.float(R.string.radius, 0.5f, 0.01f..1f)
        }
    }
}