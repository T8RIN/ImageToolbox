/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.image.loadBitmap
import com.t8rin.trickle.Trickle

internal class LUT512x512Filter(
    override val value: Pair<Float, ImageModel> = 1f to ImageModel(R.drawable.lookup),
) : Transformation<Bitmap>, Filter.LUT512x512 {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val lutBitmap = value.second.data.loadBitmap(512)?.takeIf {
            it.safeAspectRatio == 1f
        } ?: return input

        return Trickle.applyLut(
            input = input,
            lutBitmap = lutBitmap.scale(
                width = 512,
                height = 512
            ),
            intensity = value.first
        )
    }

}