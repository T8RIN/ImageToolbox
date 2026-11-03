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
import com.jhlabs.PerspectiveFilter
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DistortPerspective.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterDefinition(group = FilterGroups.GEOMETRY)
internal class DistortPerspectiveFilter(
    override val value: Value
) : JhFilterTransformation(), Filter.DistortPerspective {

    override fun createFilter(): JhFilter = PerspectiveFilter()

    override fun createFilter(image: Bitmap): JhFilter = PerspectiveFilter().apply {
        setCorners(
            value.topLeft.x * image.width,
            value.topLeft.y * image.height,
            value.topRight.x * image.width,
            value.topRight.y * image.height,
            value.bottomRight.x * image.width,
            value.bottomRight.y * image.height,
            value.bottomLeft.x * image.width,
            value.bottomLeft.y * image.height
        )
        clip = value.clip
    }

    companion object : Filter.Schema.Custom<Value> {
        override val info =
            FilterInfo(R.string.distort_perspective, R.string.filter_help_distort_perspective)

        override val parameters = structuredParameters {
            Value::topLeft.point(control(R.string.top_left, 0f..1f)) { OffsetModel(0.2f, 0f) }
            Value::topRight.point(control(R.string.top_right, 0f..1f)) { OffsetModel(0.8f, 0f) }
            Value::bottomLeft.point(control(R.string.bottom_left, 0f..1f)) { OffsetModel(0f, 1f) }
            Value::bottomRight.point(control(R.string.bottom_right, 0f..1f)) { OffsetModel(1f, 1f) }
            Value::clip.boolean(R.string.clip, false)
        }
    }
}