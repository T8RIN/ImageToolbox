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
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.AdaptiveBinarization.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.RestorationFilters

@FilterDefinition(group = FilterGroups.ENHANCEMENT)
internal class AdaptiveBinarizationFilter(
    override val value: Value
) : FilterTransformation, Filter.AdaptiveBinarization {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = RestorationFilters.adaptiveBinarization(
        bitmap = input,
        algorithm = value.algorithm,
        radius = value.radius,
        coefficient = value.coefficient,
        dynamicRange = value.dynamicRange,
        ink = value.ink.colorInt,
        paper = value.paper.colorInt,
        invert = value.invert
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.adaptive_binarization, R.string.filter_help_adaptive_binarization)

        override val parameters = structuredParameters {
            Value::algorithm.choice(
                R.string.algorithm, 0,
                R.string.sauvola,
                R.string.niblack,
                R.string.wolf,
                R.string.nick
            )
            Value::radius.float(R.string.radius, 20f, 1f..80f, roundTo = 1)
            Value::coefficient.float(R.string.threshold_coefficient, 0.22f, -1f..1f)
            Value::dynamicRange.float(R.string.dynamic_range, 0.5f, 0.02f..1f)
            Value::ink.color(R.string.ink_color, ColorModel(0xFF151515.toInt()))
            Value::paper.color(R.string.texture_paper_color, ColorModel(0xFFFFFFFF.toInt()))
            Value::invert.toggle(
                R.string.gmic_param_invert,
                false,
                R.string.normal,
                R.string.inverted
            )
        }
    }
}