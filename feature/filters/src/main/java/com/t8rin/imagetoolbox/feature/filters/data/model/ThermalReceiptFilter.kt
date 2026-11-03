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
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ThermalReceipt.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class ThermalReceiptFilter(
    override val value: Value
) : FilterTransformation, Filter.ThermalReceipt {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.thermalReceipt(
        input = input,
        size = value.size,
        heat = value.heat,
        heatRetention = value.heatRetention,
        defects = value.defects,
        fading = value.fading,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.thermal_receipt, R.string.filter_help_thermal_receipt)

        override val parameters = structuredParameters {
            Value::size.int(R.string.just_size, 4, 1..22)
            Value::heat.float(R.string.heat, 1.35f, 0.4f..2.5f)
            Value::heatRetention.float(R.string.heat_retention, 0.32f, 0f..0.9f)
            Value::defects.float(R.string.defects, 0.015f, 0f..0.15f, roundTo = 3)
            Value::fading.float(R.string.fading, 0.1f, 0f..0.8f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}