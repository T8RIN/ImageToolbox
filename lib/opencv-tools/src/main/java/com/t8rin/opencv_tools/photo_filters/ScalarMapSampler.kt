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

package com.t8rin.opencv_tools.photo_filters

import org.opencv.core.Mat
import kotlin.math.min

internal class ScalarMapSampler(
    private val values: FloatArray,
    private val width: Int,
    private val height: Int
) {

    constructor(source: Mat) : this(
        FloatArray(source.rows() * source.cols()).also { source.get(0, 0, it) },
        source.cols(),
        source.rows()
    )

    init {
        require(width > 0 && height > 0 && values.size.toLong() == width.toLong() * height)
    }

    // Match INTER_LINEAR's pixel-center mapping without allocating a full-resolution float image.
    fun readRow(y: Int, outputHeight: Int, output: FloatArray) {
        require(y in 0 until outputHeight && output.isNotEmpty())
        val sourceY = ((y + 0.5) * height / outputHeight - 0.5).coerceIn(0.0, height - 1.0)
        val top = sourceY.toInt()
        val bottom = min(top + 1, height - 1)
        val vertical = (sourceY - top).toFloat()
        val scale = width.toDouble() / output.size
        for (x in output.indices) {
            val sourceX = ((x + 0.5) * scale - 0.5).coerceIn(0.0, width - 1.0)
            val left = sourceX.toInt()
            val right = min(left + 1, width - 1)
            val horizontal = (sourceX - left).toFloat()
            val upper =
                values[top * width + left] * (1f - horizontal) + values[top * width + right] * horizontal
            val lower =
                values[bottom * width + left] * (1f - horizontal) + values[bottom * width + right] * horizontal
            output[x] = upper * (1f - vertical) + lower * vertical
        }
    }
}