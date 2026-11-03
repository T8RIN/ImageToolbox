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

import android.graphics.PointF
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.blue
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.green
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.red
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Vignette.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter

@FilterDefinition(group = FilterGroups.LIGHT)
internal class VignetteFilter(
    override val value: Value
) : GPUFilterTransformation(), Filter.Vignette {

    override fun createFilter(): GPUImageFilter = GPUImageVignetteFilter(
        PointF(0.5f, 0.5f),
        floatArrayOf(value.color.red, value.color.green, value.color.blue),
        value.start,
        value.end
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.vignette, R.string.filter_help_vignette)

        override val parameters = structuredParameters {
            Value::start.float(R.string.start, 0.3f, -2f..2f)
            Value::end.float(R.string.end, 0.75f, -2f..2f)
            Value::color.color(R.string.color, ColorModel(0xFF000000.toInt()), allowAlpha = true)
        }
    }
}