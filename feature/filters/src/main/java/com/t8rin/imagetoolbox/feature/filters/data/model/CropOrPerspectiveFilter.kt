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
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CropOrPerspective.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.auto_straight.AutoStraighten
import com.t8rin.opencv_tools.auto_straight.model.Corners
import com.t8rin.opencv_tools.auto_straight.model.PointD
import com.t8rin.opencv_tools.auto_straight.model.StraightenMode

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class CropOrPerspectiveFilter(
    override val value: Value
) : FilterTransformation, Filter.CropOrPerspective {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = AutoStraighten.process(
        input = input,
        mode = StraightenMode.Manual(
            corners = Corners(
                topLeft = PointD(
                    x = value.topLeft.x.toDouble(),
                    y = value.topLeft.y.toDouble()
                ),
                topRight = PointD(
                    x = value.topRight.x.toDouble(),
                    y = value.topRight.y.toDouble()
                ),
                bottomLeft = PointD(
                    x = value.bottomLeft.x.toDouble(),
                    y = value.bottomLeft.y.toDouble()
                ),
                bottomRight = PointD(
                    x = value.bottomRight.x.toDouble(),
                    y = value.bottomRight.y.toDouble()
                ),
                isAbsolute = value.isAbsolute
            )
        )
    )

    companion object : Filter.Schema.Custom<Value> {
        override val info =
            FilterInfo(R.string.crop_or_perspective, R.string.filter_help_crop_or_perspective)

        override val parameters = structuredParameters {
            Value::topLeft.point(control(R.string.top_left, 0f..0f)) { OffsetModel(0.1f, 0f) }
            Value::topRight.point(control(R.string.top_right, 0f..0f)) { OffsetModel(1f, 0f) }
            Value::bottomLeft.point(control(R.string.bottom_left, 0f..0f)) { OffsetModel(0f, 1f) }
            Value::bottomRight.point(control(R.string.bottom_right, 0f..0f)) {
                OffsetModel(
                    0.9f,
                    1f
                )
            }
            Value::isAbsolute.boolean(R.string.absolute, false)
        }
    }
}