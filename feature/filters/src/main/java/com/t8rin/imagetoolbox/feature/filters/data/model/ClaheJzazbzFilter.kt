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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ClaheJzazbz.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.LIGHT)
internal class ClaheJzazbzFilter(
    override val value: Value
) : FilterTransformation, Filter.ClaheJzazbz {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.claheJzazbz(
        bitmap = input,
        threshold = value.threshold,
        gridSizeHorizontal = value.gridSizeHorizontal,
        gridSizeVertical = value.gridSizeVertical,
        binsCount = value.binsCount
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.clahe_jzazbz, R.string.filter_help_clahe_jzazbz)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 0.5f, -10f..10f)
            Value::gridSizeHorizontal.int(R.string.grid_size_x, 8, 1..100)
            Value::gridSizeVertical.int(R.string.grid_size_y, 8, 1..100)
            Value::binsCount.int(R.string.bins_count, 128, 2..256)
        }
    }
}