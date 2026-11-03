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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FilmGrain.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.EFFECTS)
internal class FilmGrainFilter(
    override val value: Value
) : FilterTransformation, Filter.FilmGrain {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.filmGrain(
        input = input,
        size = value.size,
        intensity = value.intensity,
        sizeVariation = value.sizeVariation,
        exposureDependence = value.exposureDependence,
        colorGrain = value.colorGrain,
        seed = value.seed
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.film_grain, R.string.filter_help_film_grain)

        override val parameters = structuredParameters {
            Value::size.float(R.string.just_size, 2.1f, 0.65f..12f)
            Value::intensity.float(R.string.texture_intensity, 0.23f, 0f..0.7f)
            Value::sizeVariation.float(R.string.size_variation, 0.6f, 0f..1f)
            Value::exposureDependence.float(R.string.exposure_dependence, 0.72f, 0f..1f)
            Value::colorGrain.float(R.string.color_grain, 0.13f, 0f..1f)
            Value::seed.seed(R.string.seed, 0, 0..10000)
        }
    }
}