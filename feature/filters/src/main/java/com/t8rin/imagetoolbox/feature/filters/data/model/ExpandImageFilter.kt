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
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ExpandImage.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.expand.ImageExpander
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class ExpandImageFilter(
    override val value: Value
) : FilterTransformation, Filter.ExpandImage {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ImageExpander.expand(
        bitmap = input,
        top = value.top.roundToInt(),
        left = value.start.roundToInt(),
        right = value.end.roundToInt(),
        bottom = value.bottom.roundToInt(),
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.expand_image, R.string.filter_help_expand_image)

        override val parameters = structuredParameters {
            Value::top.float(R.string.top, 64f, 0f..1024f, roundTo = 0)
            Value::start.float(R.string.start, 64f, 0f..1024f, roundTo = 0)
            Value::end.float(R.string.end, 64f, 0f..1024f, roundTo = 0)
            Value::bottom.float(R.string.bottom, 64f, 0f..1024f, roundTo = 0)
        }
    }
}