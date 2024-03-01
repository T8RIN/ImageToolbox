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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.utils.Pixelate
import ru.tech.imageresizershrinker.feature.filters.data.utils.PixelationLayer

internal class EnhancedPixelationFilter(
    override val value: Float = 48f,
) : Filter.EnhancedPixelation<Bitmap>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelationLayer.Builder(PixelationLayer.Shape.Square)
                    .setResolution(value)
                    .build(),
                PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
                    .setResolution(value / 4)
                    .setSize(value / 6)
                    .build(),
                PixelationLayer.Builder(PixelationLayer.Shape.Diamond)
                    .setResolution(value / 4)
                    .setSize(value / 6)
                    .setOffset(value / 8)
                    .build()
            )
        )
    }
}