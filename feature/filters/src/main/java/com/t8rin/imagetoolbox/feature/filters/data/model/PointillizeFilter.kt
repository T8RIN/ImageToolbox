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
import com.jhlabs.PointillizeFilter
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Pointillize.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class PointillizeFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Pointillize {

    override fun createFilter(): JhFilter = PointillizeFilter().apply {
        edgeThickness = value.borderThickness
        edgeColor = value.color.colorInt
        scale = value.scale
        randomness = value.randomness
        gridType = value.shape
        turbulence = value.turbulence
        angle = Math.toRadians(value.angle.toDouble()).toFloat()
        stretch = value.stretch
        amount = value.amount
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.pointillize, R.string.filter_help_pointillize)

        override val parameters = structuredParameters {
            Value::borderThickness.float(R.string.border_thickness, 0.4f, 0f..5f)
            Value::scale.float(R.string.scale, 32f, 1f..300f)
            Value::randomness.float(R.string.randomness, 5f, 0f..10f)
            Value::shape.int(R.string.shape, 0, 0..4)
            Value::turbulence.float(R.string.turbulence, 1f, 0f..1f)
            Value::angle.float(R.string.angle, 0f, 0f..360f, 0)
            Value::stretch.float(R.string.stretch, 1f, 1f..6f)
            Value::amount.float(R.string.amount, 1f, 0f..1f)
            Value::color.color(R.string.border_color, ColorModel(0xFF000000.toInt()))
        }
    }
}