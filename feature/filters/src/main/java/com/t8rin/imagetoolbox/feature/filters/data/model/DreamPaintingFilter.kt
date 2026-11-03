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
import com.t8rin.gmic.filters.DreamPainting
import com.t8rin.gmic.model.GmicExecutionOptions
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DreamPainting.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class DreamPaintingFilter(
    override val value: Value
) : GMICFilterTransformation(GmicExecutionOptions(disableSmooth = false)), Filter.DreamPainting {

    override fun createFilter(): GmicFilter = DreamPainting(
        abstraction = value.settings[0].toInt(),
        detailsScale = value.settings[1].toFloat(),
        colorStrength = value.settings[2].toFloat(),
        paintSmoothness = value.settings[3].toFloat(),
        sharpenShades = value.settings[4].toBoolean(),
        bloomStrength = value.settings[5].toFloat(),
        bloomScale = value.settings[6].toFloat(),
        bloomSmoothness = value.settings[7].toFloat(),
        highlight = value.settings[8].toInt(),
        contrast = value.settings[9].toFloat()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_dream_painting,
            R.string.filter_help_gmic_filter_dream_painting
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_abstraction, 4, 1f..10f),
                gmicNumber(R.string.gmic_param_details_scale, 2, 0f..5f),
                gmicNumber(R.string.gmic_param_color_strength, 1.7f, 0f..4f),
                gmicNumber(R.string.gmic_param_paint_smoothness, 40, 0f..1000f),
                gmicToggle(R.string.gmic_param_sharpen_shades, true),
                gmicNumber(R.string.gmic_param_bloom_strength, 45, 0f..400f),
                gmicNumber(R.string.gmic_param_bloom_scale, 35, 0f..255f),
                gmicNumber(R.string.gmic_param_bloom_smoothness, 45, 0f..255f),
                gmicInteger(R.string.highlights, 35, 0f..100f),
                gmicNumber(R.string.contrast, 12, 0f..100f)
            )
        }
    }
}