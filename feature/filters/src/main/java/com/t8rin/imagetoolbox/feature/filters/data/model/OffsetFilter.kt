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
import com.jhlabs.OffsetFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Offset.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class OffsetFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.Offset {

    override fun createFilter(): JhFilter = OffsetFilter()

    override fun createFilter(image: Bitmap): JhFilter = OffsetFilter().apply {
        xOffset = (value.offsetX * image.width).roundToInt()
        yOffset = (value.offsetY * image.height).roundToInt()
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.offset, R.string.filter_help_offset)

        override val parameters = structuredParameters {
            Value::offsetX.float(R.string.offset_x, 0.25f, 0f..1f)
            Value::offsetY.float(R.string.offset_y, 0.25f, 0f..1f)
        }
    }
}