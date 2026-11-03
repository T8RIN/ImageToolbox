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

import com.t8rin.gmic.GmicFilter
import com.t8rin.gmic.filters.CrtScanlines
import com.t8rin.gmic.filters.CrtWindowShape
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.CrtScanlines.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class CrtScanlinesFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.CrtScanlines {

    override val upscaleFactor: Int
        get() = value.settings[0].toInt()

    override fun createFilter(): GmicFilter = CrtScanlines(
        upscaleFactor = upscaleFactor,
        neighborhoodSize = value.settings[1].toInt(),
        bloomShape = CrtWindowShape.valueOf(value.settings[2]),
        bloomThreshold = value.settings[3].toFloat(),
        useLumaForBloom = value.settings[4].toBoolean(),
        fuzzShape = CrtWindowShape.valueOf(value.settings[5]),
        normalizeImage = value.settings[6].toBoolean()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_crt_scanlines,
            R.string.filter_help_gmic_filter_crt_scanlines
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_upscale_factor, 4, 1f..16f),
                gmicInteger(R.string.gmic_param_neighborhood_size, 16, 8f..64f),
                gmicSelection(
                    R.string.gmic_param_bloom_shape,
                    "Triangular",
                    listOf("Square", "Triangular", "Sine", "Gaussian")
                ),
                gmicNumber(R.string.threshold, 0, -1f..1f),
                gmicToggle(R.string.gmic_param_use_luma_for_bloom, true),
                gmicSelection(
                    R.string.gmic_param_fuzz_shape,
                    "Triangular",
                    listOf("Square", "Triangular", "Sine", "Gaussian")
                ),
                gmicToggle(R.string.gmic_param_normalize_image, false)
            )
        }
    }
}