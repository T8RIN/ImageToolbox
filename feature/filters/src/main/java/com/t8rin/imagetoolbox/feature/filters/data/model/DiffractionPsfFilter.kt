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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.DiffractionPsf.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.opencv_tools.photo_filters.OpticalEffects

@FilterDefinition(group = FilterGroups.LIGHT)
internal class DiffractionPsfFilter(
    override val value: Value
) : FilterTransformation, Filter.DiffractionPsf {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = OpticalEffects.diffractionPsf(
        bitmap = input,
        blades = value.blades,
        rotation = value.rotation,
        radius = value.radius,
        spectralSpread = value.spectralSpread,
        threshold = value.threshold,
        intensity = value.intensity
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info =
            FilterInfo(R.string.diffraction_psf, R.string.filter_help_diffraction_psf)

        override val parameters = structuredParameters {
            Value::blades.int(R.string.procedural_blades, 5, 3..12)
            Value::rotation.float(R.string.rotation, 12f, 0f..180f, roundTo = 1)
            Value::radius.float(R.string.radius, 50f, 3f..65f, roundTo = 1)
            Value::spectralSpread.float(R.string.spectral_spread, 0.7f, 0f..1f)
            Value::threshold.float(R.string.threshold, 0.54f, 0.08f..1f)
            Value::intensity.float(R.string.texture_intensity, 2.3f, 0f..5f)
        }
    }
}