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
import android.graphics.Matrix
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.applyCanvas
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.pixelation.Pixelate
import ru.tech.imageresizershrinker.feature.filters.data.pixelation.PixelateLayer

internal class StrokePixelationFilter(
    override val value: Pair<Float, Color> = 20f to Color.Black,
) : Filter.StrokePixelation<Bitmap, Color>, Transformation<Bitmap> {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(input: Bitmap, size: IntegerSize): Bitmap {
        val pixelSize = value.first
        return Pixelate.fromBitmap(
            input = input,
            layers = arrayOf(
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 5)
                    .setOffset(pixelSize / 4)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 4)
                    .setOffset(pixelSize / 2)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 3)
                    .setOffset(pixelSize / 1.3f)
                    .build(),
                PixelateLayer.Builder(PixelateLayer.Shape.Circle)
                    .setResolution(pixelSize)
                    .setSize(pixelSize / 4)
                    .setOffset(0f)
                    .build()
            )
        ).let {
            Bitmap.createBitmap(
                it.width,
                it.height,
                it.config
            ).applyCanvas {
                drawColor(value.second.toArgb())
                drawBitmap(it, Matrix(), Paint())
            }
        }
    }
}