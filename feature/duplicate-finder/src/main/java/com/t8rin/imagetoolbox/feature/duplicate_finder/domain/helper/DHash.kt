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

package com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper

data object DHash {
    const val WIDTH: Int = 9
    const val HEIGHT: Int = 8
    const val PIXEL_COUNT: Int = WIDTH * HEIGHT

    fun calculate(grayscalePixels: IntArray): Long {
        require(grayscalePixels.size == PIXEL_COUNT) {
            "dHash requires a ${WIDTH}x$HEIGHT grayscale image"
        }

        var hash = 0L
        for (y in 0 until HEIGHT) {
            val rowOffset = y * WIDTH
            for (x in 0 until WIDTH - 1) {
                hash = hash shl 1
                if (grayscalePixels[rowOffset + x] > grayscalePixels[rowOffset + x + 1]) {
                    hash = hash or 1L
                }
            }
        }
        return hash
    }

    fun calculateFromArgb(argbPixels: IntArray): Long {
        require(argbPixels.size == PIXEL_COUNT) {
            "dHash requires a ${WIDTH}x$HEIGHT ARGB image"
        }
        return calculate(
            grayscalePixels = IntArray(PIXEL_COUNT) { index ->
                val color = argbPixels[index]
                val red = color shr 16 and 0xFF
                val green = color shr 8 and 0xFF
                val blue = color and 0xFF
                (red * 299 + green * 587 + blue * 114) / 1000
            }
        )
    }

    fun hammingDistance(first: Long, second: Long): Int =
        java.lang.Long.bitCount(first xor second)
}
