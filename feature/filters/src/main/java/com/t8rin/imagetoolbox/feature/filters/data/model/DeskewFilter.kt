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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Deskew.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.auto_straight.AutoStraighten
import com.t8rin.opencv_tools.auto_straight.model.StraightenMode

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class DeskewFilter(
    override val value: Value
) : FilterTransformation, Filter.Deskew {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = AutoStraighten.process(
        input = input,
        mode = StraightenMode.Deskew(
            maxSkew = value.max.toInt(),
            allowCrop = value.allowCrop
        )
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.deskew, R.string.filter_help_deskew)

        override val parameters = structuredParameters {
            Value::max.float(R.string.max, 15f, 0f..89f, roundTo = 0)
            Value::allowCrop.boolean(R.string.allow_crop, true)
        }
    }
}