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

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.blue
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.green
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.red
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.toModel
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFalseColorFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter


internal class FalseColorFilter(
    private val context: Context,
    override val value: Pair<ColorModel, ColorModel> = Color.Yellow.toModel() to Color.Magenta.toModel(),
) : GPUFilterTransformation(context), Filter.FalseColor {

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