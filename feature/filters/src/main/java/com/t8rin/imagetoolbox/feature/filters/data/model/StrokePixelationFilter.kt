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
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.data.image.utils.ColorUtils.toModel
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation.Pixelate
import com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation.PixelationLayer
import com.t8rin.trickle.Trickle

internal class StrokePixelationFilter(
    override val value: Pair<Float, ColorModel> = 20f to Color.Black.toModel(),
) : Transformation<Bitmap>, Filter.StrokePixelation {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val pixelSize = value.first
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelationLayer.Builder(PixelationLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 5)
                    .setOffset(pixelSize / 4)
                    .build(),
                PixelationLayer.Builder(PixelationLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 4)
                    .setOffset(pixelSize / 2)
                    .build(),
                PixelationLayer.Builder(PixelationLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 3)
                    .setOffset(pixelSize / 1.3f)
                    .build(),
                PixelationLayer.Builder(PixelationLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 4)
                    .setOffset(0f)
                    .build()
            )
        ).let {
            Trickle.drawColorBehind(
                input = it,
                color = value.second.colorInt
            )
        }
    }
}