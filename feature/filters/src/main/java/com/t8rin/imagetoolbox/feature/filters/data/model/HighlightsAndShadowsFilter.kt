/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.GPUImageHighlightShadowWideRangeFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

internal class HighlightsAndShadowsFilter(
    override val value: Float = 0.25f,
) : GPUFilterTransformation(), Filter.HighlightsAndShadows {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageHighlightShadowWideRangeFilter(value, 1f)
}