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
import com.t8rin.gmic.filters.BandingDenoise
import com.t8rin.gmic.filters.BandingDenoiseSizeOfTiles
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.BandingDenoise.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class BandingDenoiseFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.BandingDenoise {

    override fun createFilter(): GmicFilter = BandingDenoise(
        vCutoff = value.settings[0].toFloat(),
        hCutoff = value.settings[1].toFloat(),
        space = value.settings[2].toFloat(),
        value = value.settings[3].toFloat(),
        sizeOfTiles = BandingDenoiseSizeOfTiles.valueOf(value.settings[4])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_banding_denoise,
            R.string.filter_help_gmic_filter_banding_denoise
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_v_cutoff, 5, 0f..50f),
                gmicNumber(R.string.gmic_param_h_cutoff, 5, 0f..50f),
                gmicNumber(R.string.texture_category_space, 5, 0f..20f),
                gmicNumber(R.string.gmic_param_value, 10, 0f..100f),
                gmicSelection(
                    R.string.gmic_param_size_of_tiles,
                    "NoTiling",
                    listOf("NoTiling", "Px64", "Px128", "Px256", "Px512", "Px1024", "Px2048")
                )
            )
        }
    }
}