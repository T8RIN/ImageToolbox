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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PolkaDot.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.PIXELATION)
internal class PolkaDotFilter(
    override val value: Value
) : FilterTransformation, Filter.PolkaDot {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = if (value.backgroundColor.colorInt == 0) {
        Trickle.polkaDot(
            input = input,
            dotRadius = value.radius,
            spacing = value.spacing
        )
    } else {
        Trickle.drawColorBehind(
            input = Trickle.polkaDot(
                input = input,
                dotRadius = value.radius,
                spacing = value.spacing
            ),
            color = value.backgroundColor.colorInt
        )
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.polka_dot, R.string.filter_help_polka_dot)

        override val parameters = structuredParameters {
            Value::radius.int(R.string.radius, 10, 1..40)
            Value::spacing.int(R.string.spacing, 8, 1..40)
            Value::backgroundColor.color(
                R.string.background_color,
                ColorModel(0xFF000000.toInt()),
                allowAlpha = true
            )
        }
    }
}