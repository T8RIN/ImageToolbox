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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.SingleLineDrawing.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.DrawingEffects

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class SingleLineDrawingFilter(
    override val value: Value
) : FilterTransformation, Filter.SingleLineDrawing {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = DrawingEffects.singleLineDrawing(
        bitmap = input,
        pointCount = value.pointCount,
        densityCurve = value.densityCurve,
        lineWidth = value.lineWidth,
        ink = value.ink.colorInt,
        paper = value.paper.colorInt,
        optimization = value.optimization,
        seed = value.seed,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.single_line_drawing, R.string.filter_help_single_line_drawing)

        override val parameters = structuredParameters {
            Value::pointCount.int(R.string.point_count, 1500, 60..3500)
            Value::densityCurve.float(R.string.density_curve, 1.5f, 0.35f..4f)
            Value::lineWidth.float(R.string.line_width, 1.1f, 0.4f..5f, roundTo = 1)
            Value::ink.color(R.string.ink_color, ColorModel(0xFF282229.toInt()))
            Value::paper.color(R.string.texture_paper_color, ColorModel(0xFFFAF5E7.toInt()))
            Value::optimization.int(R.string.optimization, 2, 0..4)
            Value::seed.seed(R.string.seed, 0, 0..10000)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}