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

package ru.tech.imageresizershrinker.feature.filters.data.glitch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.floor


internal class SimpleGlitcher(
    private val amount: Int = 20,
    private val seed: Int = 15,
    private val iterations: Int = 9
) {
    suspend fun glitch(bitmap: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        val imageByteArray = bitmap.clone()
        val jpgHeaderLength = getJpegHeaderSize(imageByteArray)
        repeat(
            times = iterations
        ) {
            glitchJpegBytes(
                pos = it,
                imageByteArray = imageByteArray,
                jpgHeaderLength = jpgHeaderLength
            )
        }
        imageByteArray
    }

    private fun glitchJpegBytes(pos: Int, imageByteArray: ByteArray, jpgHeaderLength: Int) {
        val maxIndex = imageByteArray.size - jpgHeaderLength - 4f
        val pxMin = maxIndex / iterations * pos
        val pxMax = maxIndex / iterations * (pos + 1)
        val delta = pxMax - pxMin
        var pxIndex = pxMin + delta * seed / 100f
        if (pxIndex > maxIndex) {
            pxIndex = maxIndex
        }
        val index = floor((jpgHeaderLength + pxIndex).toDouble()).toInt()
        imageByteArray[index] = floor((amount / 100f * 256f).toDouble()).toInt().toByte()
    }

    private fun getJpegHeaderSize(imageByteArray: ByteArray): Int {
        var result = 417
        var i = 0
        val len = imageByteArray.size
        while (i < len) {
            if (imageByteArray[i].toInt() == 255 && imageByteArray[i + 1].toInt() == 218) {
                result = i + 2
                break
            }
            i++
        }
        return result
    }
}