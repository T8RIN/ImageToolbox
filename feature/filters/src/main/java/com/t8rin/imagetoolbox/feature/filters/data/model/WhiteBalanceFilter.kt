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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.WhiteBalance.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GPUFilterTransformation
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter

@FilterDefinition(group = FilterGroups.LIGHT)
internal class WhiteBalanceFilter(
    override val value: Value,
) : GPUFilterTransformation(), Filter.WhiteBalance {

    override fun createFilter(): GPUImageFilter =
        GPUImageWhiteBalanceFilter(value.temperature, value.tint)

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.white_balance, R.string.filter_help_white_balance)

        override val parameters = structuredParameters {
            Value::temperature.float(R.string.temperature, 7000.0f, 1000f..10000f, roundTo = 0)
            Value::tint.float(R.string.tint, 100f, -100f..100f)
        }
    }
}