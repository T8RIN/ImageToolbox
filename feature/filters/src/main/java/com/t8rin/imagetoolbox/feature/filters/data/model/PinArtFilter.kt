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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PinArt.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class PinArtFilter(
    override val value: Value
) : FilterTransformation, Filter.PinArt {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.pinArt(
        input = input,
        spacing = value.spacing,
        height = value.height,
        angle = value.angle,
        gloss = value.gloss,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.pin_art,
            R.string.filter_help_pin_art
        )

        override val parameters = structuredParameters {
            Value::spacing.float(R.string.spacing, 18f, 5f..40f)
            Value::height.float(R.string.gmic_param_height, 0.065f, 0.005f..0.2f, roundTo = 3)
            Value::angle.float(R.string.angle, 35f, 0f..70f)
            Value::gloss.float(R.string.gloss, 0.65f, 0f..1f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}