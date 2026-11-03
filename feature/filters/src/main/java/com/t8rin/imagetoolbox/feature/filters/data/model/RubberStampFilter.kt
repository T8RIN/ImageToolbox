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
import com.jhlabs.StampFilter
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RubberStamp.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.max

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class RubberStampFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.RubberStamp {

    override fun createFilter(): JhFilter = StampFilter()

    override fun createFilter(image: Bitmap): JhFilter = StampFilter().apply {
        threshold = value.threshold
        softness = value.softness
        radius = value.radius * (max(image.width, image.height) / 64f)
        white = value.firstColor.colorInt
        black = value.secondColor.colorInt
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.rubber_stmp, R.string.filter_help_rubber_stmp)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 0.3f, 0f..1f)
            Value::softness.float(R.string.brush_softness, 0.1f, 0f..1f)
            Value::radius.float(R.string.radius, 0.05f, 0f..2f)
            Value::firstColor.color(R.string.first_color, ColorModel(0xFF3FA08F.toInt()))
            Value::secondColor.color(R.string.second_color, ColorModel(0xFF003027.toInt()))
        }
    }
}