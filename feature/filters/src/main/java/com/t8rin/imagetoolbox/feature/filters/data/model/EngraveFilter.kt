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
import com.t8rin.gmic.filters.Engrave
import com.t8rin.gmic.filters.EngraveAntialiasing
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.Engrave.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class EngraveFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.Engrave {

    override fun createFilter(): GmicFilter = Engrave(
        radius = value.settings[0].toFloat(),
        density = value.settings[1].toFloat(),
        edges = value.settings[2].toFloat(),
        coherence = value.settings[3].toFloat(),
        threshold = value.settings[4].toFloat(),
        minimalArea = value.settings[5].toInt(),
        flatRegionsRemoval = value.settings[6].toFloat(),
        addColorBackground = value.settings[7].toBoolean(),
        quantization = value.settings[8].toFloat(),
        shading = value.settings[9].toInt(),
        hue = value.settings[10].toFloat(),
        saturation = value.settings[11].toFloat(),
        lightness = value.settings[12].toFloat(),
        antialiasing = EngraveAntialiasing.valueOf(value.settings[13])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.gmic_filter_engrave, R.string.filter_help_gmic_filter_engrave)

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.radius, 0.5f, 0f..2f),
                gmicNumber(R.string.density, 50, 0f..200f),
                gmicNumber(R.string.texture_edges, 0, 0f..10f),
                gmicNumber(R.string.gmic_param_coherence, 8, 0f..40f),
                gmicNumber(R.string.threshold, 40, 0f..100f),
                gmicInteger(R.string.gmic_param_minimal_area, 0, -256f..256f),
                gmicNumber(R.string.gmic_param_flat_regions_removal, 0, 0f..10f),
                gmicToggle(R.string.gmic_param_add_color_background, true),
                gmicNumber(R.string.gmic_param_quantization, 10, 0f..40f),
                gmicInteger(R.string.texture_shading, 1, 0f..5f),
                gmicNumber(R.string.hue, 0, -180f..180f),
                gmicNumber(R.string.saturation, 0, -100f..100f),
                gmicNumber(R.string.gmic_param_lightness, 0, -100f..100f),
                gmicSelection(
                    R.string.antialias,
                    "X1_5",
                    listOf("Disabled", "X1_5", "X2", "X3")
                )
            )
        }
    }
}