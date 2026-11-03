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

import android.graphics.Bitmap
import com.awxkee.aire.Aire
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.EqualizeHistogramAdaptiveLUV.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import kotlin.math.roundToInt

@FilterDefinition(group = FilterGroups.LIGHT)
internal class EqualizeHistogramAdaptiveLUVFilter(
    override val value: Value
) : FilterTransformation, Filter.EqualizeHistogramAdaptiveLUV {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.equalizeHistAdaptiveLUV(
        bitmap = input,
        gridSizeHorizontal = value.gridSizeX.roundToInt(),
        gridSizeVertical = value.gridSizeY.roundToInt(),
        binsCount = value.binsCount.roundToInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.equalize_histogram_adaptive_luv,
            R.string.filter_help_equalize_histogram_adaptive_luv
        )

        override val parameters = structuredParameters {
            Value::gridSizeX.float(R.string.grid_size_x, 3f, 1f..100f, roundTo = 0)
            Value::gridSizeY.float(R.string.grid_size_y, 3f, 1f..100f, roundTo = 0)
            Value::binsCount.float(R.string.bins_count, 128f, 2f..256f, roundTo = 0)
        }
    }
}