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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LenticularPrint.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.DISTORTION)
internal class LenticularPrintFilter(
    override val value: Value
) : FilterTransformation, Filter.LenticularPrint {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.lenticularPrint(
        input = input,
        lensWidth = value.lensWidth,
        viewAngle = value.viewAngle,
        refraction = value.refraction,
        gloss = value.gloss,
        rotation = value.rotation,
        strength = value.strength
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(
            R.string.lenticular_print,
            R.string.filter_help_lenticular_print
        )

        override val parameters = structuredParameters {
            Value::lensWidth.float(R.string.lens_width, 16f, 4f..64f)
            Value::viewAngle.float(R.string.view_angle, 0f, -50f..50f)
            Value::refraction.float(R.string.refraction_index, 1.5f, 1f..2.2f)
            Value::gloss.float(R.string.gloss, 0.45f, 0f..1f)
            Value::rotation.float(R.string.rotation, 0f, -90f..90f)
            Value::strength.float(R.string.strength, 1f, 0f..1f)
        }
    }
}