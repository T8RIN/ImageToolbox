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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.FilmHalation.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.OpticalEffects

@FilterDefinition(group = FilterGroups.LIGHT)
internal class FilmHalationFilter(
    override val value: Value
) : FilterTransformation, Filter.FilmHalation {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = OpticalEffects.filmHalation(
        bitmap = input,
        threshold = value.threshold,
        knee = value.knee,
        radius = value.radius,
        warmth = value.warmth,
        intensity = value.intensity
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.film_halation, R.string.filter_help_film_halation)

        override val parameters = structuredParameters {
            Value::threshold.float(R.string.threshold, 0.58f, 0.08f..1f)
            Value::knee.float(R.string.soft_knee, 0.2f, 0.01f..0.5f)
            Value::radius.float(R.string.radius, 15f, 1f..40f, roundTo = 1)
            Value::warmth.float(R.string.warmth, 0.75f, 0f..1f)
            Value::intensity.float(R.string.texture_intensity, 1.35f, 0f..5f)
        }
    }
}