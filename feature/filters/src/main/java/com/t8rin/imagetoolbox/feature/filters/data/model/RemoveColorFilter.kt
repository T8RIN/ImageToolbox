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
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.RemoveColor.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation

@FilterDefinition(group = FilterGroups.COLOR)
internal class RemoveColorFilter(
    override val value: Value,
) : FilterTransformation, Filter.RemoveColor {
    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = ReplaceColorFilter(
        value = FilterValues.replaceColor(
            tolerance = value.tolerance,
            colorToReplace = value.colorToRemove,
            targetColor = ColorModel(0)
        )
    ).transform(input, size)

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.remove_color, R.string.filter_help_remove_color)

        override val parameters = structuredParameters {
            Value::tolerance.float(R.string.tolerance, 0f, 0f..1f)
            Value::colorToRemove.color(
                R.string.color_to_remove,
                ColorModel(0xFF000000.toInt()),
                allowAlpha = true
            )
        }
    }
}