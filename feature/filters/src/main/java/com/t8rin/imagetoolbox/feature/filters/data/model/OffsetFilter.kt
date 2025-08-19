/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.JhFilterTransformation
import kotlin.math.roundToInt

@FilterInject
internal class OffsetFilter(
    override val value: Pair<Float, Float> = 0.25f to 0.25f
) : JhFilterTransformation(), Filter.Offset {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): JhFilter = OffsetFilter()

    override fun createFilter(image: Bitmap): JhFilter = OffsetFilter().apply {
        xOffset = (value.first * image.width).roundToInt()
        yOffset = (value.second * image.height).roundToInt()
    }

}