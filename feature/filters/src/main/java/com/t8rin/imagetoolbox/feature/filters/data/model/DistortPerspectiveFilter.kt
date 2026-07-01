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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DistortPerspectiveParams
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation

@FilterInject
internal class DistortPerspectiveFilter(
    override val value: DistortPerspectiveParams = DistortPerspectiveParams.Default
) : JhFilterTransformation(), Filter.DistortPerspective {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): JhFilter = PerspectiveFilter()

    override fun createFilter(image: Bitmap): JhFilter = PerspectiveFilter().apply {
        setCorners(
            value.topLeft.first * image.width,
            value.topLeft.second * image.height,
            value.topRight.first * image.width,
            value.topRight.second * image.height,
            value.bottomRight.first * image.width,
            value.bottomRight.second * image.height,
            value.bottomLeft.first * image.width,
            value.bottomLeft.second * image.height
        )
        clip = value.clip
    }
}
