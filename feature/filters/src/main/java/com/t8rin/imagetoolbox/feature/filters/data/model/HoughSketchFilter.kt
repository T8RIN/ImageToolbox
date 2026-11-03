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
import com.t8rin.gmic.filters.HoughSketch
import com.t8rin.gmic.filters.HoughSketchColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.HoughSketch.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.GMICFilterTransformation

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class HoughSketchFilter(
    override val value: Value
) : GMICFilterTransformation(), Filter.HoughSketch {

    override fun createFilter(): GmicFilter = HoughSketch(
        smoothness = value.settings[0].toFloat(),
        density = value.settings[1].toFloat(),
        radius = value.settings[2].toInt(),
        threshold = value.settings[3].toFloat(),
        opacity = value.settings[4].toFloat(),
        colorModel = HoughSketchColorModel.valueOf(value.settings[5])
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.gmic_filter_hough_sketch,
            R.string.filter_help_gmic_filter_hough_sketch
        )

        override val parameters = structuredParameters {
            Value::settings.gmic(
                gmicNumber(R.string.gmic_param_smoothness, 1.25f, 0f..10f),
                gmicNumber(R.string.density, 18, 0f..100f),
                gmicInteger(R.string.radius, 5, 0f..30f),
                gmicNumber(R.string.threshold, 70, 0f..100f),
                gmicNumber(R.string.opacity, 0.35f, 0f..1f),
                gmicSelection(
                    R.string.gmic_param_color_model,
                    "ColorOnWhite",
                    listOf(
                        "BlackOnWhite",
                        "WhiteOnBlack",
                        "BlackOnTransparentWhite",
                        "WhiteOnTransparentBlack",
                        "ColorOnWhite"
                    )
                )
            )
        }
    }
}