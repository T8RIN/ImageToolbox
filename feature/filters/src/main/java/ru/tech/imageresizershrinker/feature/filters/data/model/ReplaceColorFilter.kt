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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.utils.ColorUtils.toColor
import ru.tech.imageresizershrinker.feature.filters.data.utils.ColorUtils.toModel
import kotlin.math.pow
import kotlin.math.sqrt


internal class ReplaceColorFilter(
    override val value: Triple<Float, ColorModel, ColorModel> = Triple(
        first = 0f,
        second = Color(red = 0.0f, green = 0.0f, blue = 0.0f, alpha = 1.0f).toModel(),
        third = Color(red = 1.0f, green = 1.0f, blue = 1.0f, alpha = 1.0f).toModel()
    ),
) : Transformation<Bitmap>, Filter.ReplaceColor {
    override val cacheKey: String
        get() = (value).hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = input.replaceColor(
        fromColor = value.second.toColor(),
        targetColor = value.third.toColor(),
        tolerance = value.first
    )
}


private fun Bitmap.replaceColor(
    fromColor: Color,
    targetColor: Color,
    tolerance: Float
): Bitmap {
    // Source image size
    val width = width
    val height = height
    val pixels = IntArray(width * height)
    //get pixels
    getPixels(pixels, 0, width, 0, 0, width, height)
    for (x in pixels.indices) {
        pixels[x] = if (Color(pixels[x]).distanceFrom(fromColor) <= tolerance) {
            targetColor.toArgb()
        } else pixels[x]
    }
    // create result bitmap output
    val result = Bitmap.createBitmap(width, height, config)
    //set pixels
    result.setPixels(pixels, 0, width, 0, 0, width, height)
    return result
}

private fun Color.distanceFrom(color: Color): Float {
    return sqrt((red - color.red).pow(2) + (green - color.green).pow(2) + (blue - color.blue).pow(2))
}