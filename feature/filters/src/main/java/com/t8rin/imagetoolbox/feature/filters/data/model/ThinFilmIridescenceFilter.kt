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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.ThinFilmIridescence.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.LIGHT)
internal class ThinFilmIridescenceFilter(
    override val value: Value
) : FilterTransformation, Filter.ThinFilmIridescence {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.thinFilmIridescence(
        input = input,
        thickness = value.thickness,
        variation = value.variation,
        angle = value.angle,
        saturation = value.saturation,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.thin_film_iridescence,
            R.string.filter_help_thin_film_iridescence
        )

        override val parameters = structuredParameters {
            Value::thickness.float(R.string.film_thickness_nm, 480f, 80f..1400f)
            Value::variation.float(R.string.variation, 320f, 0f..1200f)
            Value::angle.float(R.string.angle, 35f, 0f..80f)
            Value::saturation.float(R.string.saturation, 0.8f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}