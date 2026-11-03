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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SobelEdgeDetection.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class SobelEdgeDetectionFilter(
    override val value: Value,
) : GPUFilterTransformation(), Filter.SobelEdgeDetection {

    override fun createFilter(): GPUImageFilter = GPUImageSobelEdgeDetectionFilter().apply {
        setLineSize(value.lineWidth)
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.sobel_edge, R.string.filter_help_sobel_edge)

        override val parameters = structuredParameters {
            Value::lineWidth.float(R.string.line_width, 1f, 1f..25f, roundTo = 0)
        }
    }
}