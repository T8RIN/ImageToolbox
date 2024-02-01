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

import kotlin.math.floor


internal class SimpleGlitcher(
    private val amount: Int = 20,
    private val seed: Int = 15,
    private val iterations: Int = 9
) {
    private lateinit var imageByte: ByteArray

    private var jpgHeaderLength = 0
    fun glitch(bitmap: ByteArray): ByteArray {
        imageByte = bitmap
        jpgHeaderLength = jpegHeaderSize
        repeat(times = iterations, action = ::glitchJpegBytes)
        return imageByte
    }

    private fun glitchJpegBytes(i: Int) {
        val maxIndex = imageByte.size - jpgHeaderLength - 4f
        val pxMin = maxIndex / iterations * i
        val pxMax = maxIndex / iterations * (i + 1)
        val delta = pxMax - pxMin
        var pxIndex = pxMin + delta * seed / 100f
        if (pxIndex > maxIndex) {
            pxIndex = maxIndex
        }
        val index = floor((jpgHeaderLength + pxIndex).toDouble()).toInt()
        imageByte[index] = floor((amount / 100f * 256f).toDouble()).toInt().toByte()
    }

    private val jpegHeaderSize: Int
        get() {
            var result = 417
            var i = 0
            val len = imageByte.size
            while (i < len) {
                if (imageByte[i].toInt() == 255 && imageByte[i + 1].toInt() == 218) {
                    result = i + 2
                    break
                }
                i++
            }
            return result
        }
}