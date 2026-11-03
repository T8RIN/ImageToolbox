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
import com.t8rin.gmic.filters.MarkerBackground
import com.t8rin.gmic.filters.MarkerDrawing
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.MarkerDrawing.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class MarkerDrawingFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.MarkerDrawing {

    override fun createFilter(): GmicFilter = MarkerDrawing(
        length = value.settings[0].toInt(),
        radius = value.settings[1].toFloat(),
        opacity = value.settings[2].toFloat(),
        inertia = value.settings[3].toFloat(),
        curviness = value.settings[4].toFloat(),
        anisotropy = value.settings[5].toFloat(),
        smoothness = value.settings[6].toFloat(),
        coherence = value.settings[7].toFloat(),
        iterations = value.settings[8].toInt(),
        background = MarkerBackground.valueOf(value.settings[9]),
        backgroundBlur = value.settings[10].toFloat(),
        backgroundColor = value.settings[11].toInt()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_marker_drawing,
            R.string.filter_help_gmic_filter_marker_drawing
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicInteger(R.string.gmic_param_length, 70, 1f..256f),
                gmicNumber(R.string.radius, 4, 1f..32f),
                gmicNumber(R.string.opacity, 10, 0f..100f),
                gmicNumber(R.string.gmic_param_inertia, 15, 0f..100f),
                gmicNumber(R.string.gmic_param_curviness, 100, 0f..200f),
                gmicNumber(R.string.texture_anisotropy, 85, 0f..100f),
                gmicNumber(R.string.gmic_param_smoothness, 0.5f, 0f..5f),
                gmicNumber(R.string.gmic_param_coherence, 3, 0f..16f),
                gmicInteger(R.string.iterations, 3, 1f..64f),
                gmicSelection(R.string.gmic_param_background, "Color", listOf("Blur", "Color")),
                gmicNumber(R.string.gmic_param_background_blur, 20, 0f..128f),
                gmicColor(R.string.background_color, -1)
            )
        }
    }
}