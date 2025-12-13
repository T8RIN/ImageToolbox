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

package com.t8rin.imagetoolbox.feature.filters.data.utils.convolution

import kotlin.math.abs

internal object ExtendedConvolutionKernels {

    /**
     * Diamond blur
     */
    fun diamondBlur(kernelSize: Int): FloatArray {
        val c = kernelSize / 2
        var sum = 0f
        val k = FloatArray(kernelSize * kernelSize)

        for (y in 0 until kernelSize) {
            for (x in 0 until kernelSize) {
                val d = abs(x - c) + abs(y - c)
                if (d <= c) {
                    val v = (c - d + 1).toFloat()
                    k[y * kernelSize + x] = v
                    sum += v
                }
            }
        }
        return k.map { it / sum }.toFloatArray()
    }

    /**
     * Plus blur
     */
    fun plusBlur(kernelSize: Int): FloatArray {
        val c = kernelSize / 2
        var sum = 0f
        val k = FloatArray(kernelSize * kernelSize)

        for (i in 0 until kernelSize) {
            k[c * kernelSize + i] = 1f
            k[i * kernelSize + c] = 1f
            sum += 2f
        }
        k[c * kernelSize + c] = 1f
        sum -= 1f

        return k.map { it / sum }.toFloatArray()
    }

}