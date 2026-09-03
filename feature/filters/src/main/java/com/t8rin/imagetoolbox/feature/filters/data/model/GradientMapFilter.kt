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
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.transformation.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterInject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

@FilterInject
internal class GradientMapFilter(
    override val value: GradientPalette = GradientPalette.Spectral
) : Transformation<Bitmap>, Filter.GradientMap {

    override val cacheKey: String
        get() = value.name

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val width = input.width
        val height = input.height
        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val colorMap = value.sampleColors(COLOR_MAP_SIZE).map { it.colorInt }
        val pixels = IntArray(width * ROW_BLOCK_SIZE.coerceAtMost(height))

        var top = 0
        while (top < height) {
            currentCoroutineContext().ensureActive()
            val rowCount = ROW_BLOCK_SIZE.coerceAtMost(height - top)
            val pixelCount = width * rowCount
            input.getPixels(pixels, 0, width, 0, top, width, rowCount)

            for (index in 0 until pixelCount) {
                val pixel = pixels[index]
                val red = pixel ushr 16 and 0xFF
                val green = pixel ushr 8 and 0xFF
                val blue = pixel and 0xFF
                val luminance = (red * 77 + green * 150 + blue * 29) ushr 8
                pixels[index] = (pixel and ALPHA_MASK) or
                        (colorMap[luminance] and RGB_MASK)
            }

            output.setPixels(pixels, 0, width, 0, top, width, rowCount)
            top += rowCount
        }

        return output
    }
}

private const val COLOR_MAP_SIZE = 256
private const val ROW_BLOCK_SIZE = 64
private const val ALPHA_MASK = -0x1000000
private const val RGB_MASK = 0x00FFFFFF
