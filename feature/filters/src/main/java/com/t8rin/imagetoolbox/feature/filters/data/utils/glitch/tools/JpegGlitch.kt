/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.filters.data.utils.glitch.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayOutputStream
import kotlin.math.floor

internal interface JpegGlitch {
    suspend fun jpegGlitch(
        input: Bitmap,
        amount: Int = 20,
        seed: Int = 15,
        iterations: Int = 9
    ): Bitmap = coroutineScope {
        val imageByteArray = ByteArrayOutputStream().use {
            input.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.toByteArray()
        }
        val jpgHeaderLength = getJpegHeaderSize(imageByteArray)
        repeat(
            times = iterations
        ) {
            glitchJpegBytes(
                pos = it,
                imageByteArray = imageByteArray,
                jpgHeaderLength = jpgHeaderLength,
                amount = amount,
                seed = seed,
                iterations = iterations
            )
        }
        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    companion object {
        private fun glitchJpegBytes(
            pos: Int,
            imageByteArray: ByteArray,
            jpgHeaderLength: Int,
            amount: Int = 20,
            seed: Int = 15,
            iterations: Int = 9
        ) {
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
}