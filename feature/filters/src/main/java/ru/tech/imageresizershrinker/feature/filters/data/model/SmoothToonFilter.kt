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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSmoothToonFilter
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter


internal class SmoothToonFilter(
    private val context: Context,
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.2f, 10f),
) : GPUFilterTransformation(context), Filter.SmoothToon {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageSmoothToonFilter().apply {
        setBlurSize(value.first)
        setThreshold(value.second)
        setQuantizationLevels(value.third)
    }
}