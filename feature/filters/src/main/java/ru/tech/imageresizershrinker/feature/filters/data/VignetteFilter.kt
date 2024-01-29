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

package ru.tech.imageresizershrinker.feature.filters.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter


class VignetteFilter(
    private val context: Context,
    override val value: Triple<Float, Float, Color> = Triple(0.3f, 0.75f, Color.Black)
) : GPUFilterTransformation(context), Filter.Vignette<Bitmap, Color> {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageVignetteFilter(
        PointF(0.5f, 0.5f),
        floatArrayOf(value.third.red, value.third.green, value.third.blue),
        value.first,
        value.second
    )

}