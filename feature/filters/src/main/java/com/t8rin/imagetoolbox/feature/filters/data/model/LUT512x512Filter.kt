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
import androidx.core.graphics.scale
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.LUT512x512.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import com.t8rin.imagetoolbox.feature.filters.data.utils.image.loadBitmap
import com.t8rin.trickle.Trickle

@FilterDefinition(group = FilterGroups.LUT)
internal class LUT512x512Filter(
    override val value: Value,
) : FilterTransformation, Filter.LUT512x512 {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val lutBitmap = value.image.data.loadBitmap(512)?.takeIf {
            it.safeAspectRatio == 1f
        } ?: return input

        return Trickle.applyLut(
            input = input,
            lutBitmap = lutBitmap.scale(
                width = 512,
                height = 512
            ),
            intensity = value.intensity
        )
    }

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.lut512x512, R.string.filter_help_lut512x512)

        override val parameters = structuredParameters {
            Value::intensity.float(R.string.strength, 1f, 0f..1f)
            Value::image.image(R.string.target_lut_image) { ImageModel(R.drawable.lookup) }
        }
    }
}