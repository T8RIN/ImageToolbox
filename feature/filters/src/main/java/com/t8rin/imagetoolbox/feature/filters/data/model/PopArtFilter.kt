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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.PopArt.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PopArtBlendingMode
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.toMode
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.ARTISTIC)
internal class PopArtFilter(
    override val value: Value
) : FilterTransformation, Filter.PopArt {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Trickle.popArt(
        input = input,
        color = value.color.colorInt,
        strength = value.strength,
        blendMode = value.overlayMode.toMode()
    )

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.pop_art, R.string.filter_help_pop_art)

        override val parameters = structuredParameters {
            Value::strength.float(R.string.strength, 1f, 0f..1f)
            Value::color.color(R.string.color, ColorModel(0xFFFF0000.toInt()), allowAlpha = true)
            Value::overlayMode.enum(
                R.string.overlay_mode,
                label = { it.label }) { PopArtBlendingMode.MULTIPLY }
        }
    }
}