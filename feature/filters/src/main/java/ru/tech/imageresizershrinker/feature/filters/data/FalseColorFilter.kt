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
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter


class FalseColorFilter(
    private val context: Context,
    override val value: Pair<Color, Color> = Color(
        red = 0f,
        green = 0f,
        blue = 0.5f
    ) to Color(
        red = 1f,
        green = 0f,
        blue = 0f
    ),
) : GPUFilterTransformation(context),
    ru.tech.imageresizershrinker.core.filters.domain.model.Filter.FalseColor<Bitmap, Color> {
    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageFalseColorFilter(
        value.first.red,
        value.first.green,
        value.first.blue,
        value.second.red,
        value.second.green,
        value.second.blue
    )
}