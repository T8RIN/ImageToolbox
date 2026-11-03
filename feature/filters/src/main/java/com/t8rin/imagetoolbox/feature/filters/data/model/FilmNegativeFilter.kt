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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FilmNegative.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.COLOR)
internal class FilmNegativeFilter(
    override val value: Value
) : FilterTransformation, Filter.FilmNegative {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.filmNegative(
        input = input,
        exposure = value.exposure,
        densityRange = value.densityRange,
        contrast = value.contrast,
        toneCurve = value.toneCurve,
        filmBase = value.filmBase.colorInt,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.film_negative, R.string.filter_help_film_negative)

        override val parameters = structuredParameters {
            Value::exposure.float(R.string.exposure, 0.65f, -3f..3f)
            Value::densityRange.float(R.string.density_range, 3f, 0.2f..6f)
            Value::contrast.float(R.string.contrast, 1f, 0.4f..2.4f)
            Value::toneCurve.float(R.string.tone_curve, 0.35f, 0f..1f)
            Value::filmBase.color(R.string.film_base_color, ColorModel(0xFFFFA352.toInt()))
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}