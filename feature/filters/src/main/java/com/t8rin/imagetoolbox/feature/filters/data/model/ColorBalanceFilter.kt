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
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorBalanceFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

@FilterInject
internal class ColorBalanceFilter(
    override val value: FloatArray = floatArrayOf(
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f
    ),
) : GPUFilterTransformation(), Filter.ColorBalance {

    override val cacheKey: String
        get() = value.contentHashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageColorBalanceFilter().apply {
        setHighlights(value.take(3).toFloatArray())
        setMidtones(floatArrayOf(value[3], value[4], value[6]))
        setShowdows(value.takeLast(3).toFloatArray())
    }
}