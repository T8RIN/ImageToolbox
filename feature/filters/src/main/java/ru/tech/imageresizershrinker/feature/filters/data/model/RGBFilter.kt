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
import androidx.compose.ui.graphics.Color
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.blue
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.green
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.red
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.toModel
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterValueWrapper
import ru.tech.imageresizershrinker.core.filters.domain.model.wrap


internal class RGBFilter(
    private val context: Context,
    override val value: FilterValueWrapper<ColorModel> = Color.Green.toModel().wrap(),
) : GPUFilterTransformation(context), Filter.RGB {

    override val cacheKey: String
        get() = (value to context).hashCode().toString()

    override fun createFilter(): GPUImageFilter = GPUImageRGBFilter(
        value.wrapped.red, value.wrapped.green, value.wrapped.blue
    )
}