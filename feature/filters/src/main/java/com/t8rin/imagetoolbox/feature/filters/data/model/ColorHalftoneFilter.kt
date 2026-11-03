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

import com.jhlabs.ColorHalftoneFilter
import com.jhlabs.JhFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ColorHalftone.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.DITHERING)
internal class ColorHalftoneFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.ColorHalftone {

    override fun createFilter(): JhFilter = ColorHalftoneFilter().apply {
        setdotRadius(value.radius)
        cyanScreenAngle = Math.toRadians(value.cyan.toDouble()).toFloat()
        magentaScreenAngle = Math.toRadians(value.magenta.toDouble()).toFloat()
        yellowScreenAngle = Math.toRadians(value.yellow.toDouble()).toFloat()
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.color_halftone, R.string.filter_help_color_halftone)

        override val parameters = structuredParameters {
            Value::radius.float(R.string.radius, 2f, 0f..50f)
            Value::cyan.float(R.string.cyan, 108f, 0f..360f, roundTo = 0)
            Value::magenta.float(R.string.magenta, 162f, 0f..360f, roundTo = 0)
            Value::yellow.float(R.string.yellow, 90f, 0f..360f, roundTo = 0)
        }
    }
}