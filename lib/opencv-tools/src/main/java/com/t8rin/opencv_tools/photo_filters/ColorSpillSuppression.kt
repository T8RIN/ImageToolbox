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

import android.graphics.Bitmap
import kotlin.math.max
import kotlin.math.roundToInt

object ColorSpillSuppression {

    suspend fun apply(
        input: Bitmap,
        color: Int = 0,
        tolerance: Float = 0.08f,
        recovery: Float = 0.5f,
        strength: Float = 1f
    ): Bitmap = PhotoFilterRenderer.render(input, strength) { rgb ->
        val spill = if (color == 1) 2 else 1
        val other = if (spill == 1) 2 else 1
        val threshold = tolerance.takeIf(Float::isFinite)?.coerceIn(0f, 0.4f) ?: 0.08f
        val compensation = recovery.takeIf(Float::isFinite)?.coerceIn(0f, 1f) ?: 0.5f
        val luminanceWeight = if (spill == 1) 0.7152f else 0.0722f
        val result = mat(rgb.rows(), rgb.cols(), rgb.type())
        val row = ByteArray(rgb.cols() * 3)
        for (y in 0 until rgb.rows()) {
            checkpoint()
            rgb.get(y, 0, row)
            for (x in 0 until rgb.cols()) {
                val offset = x * 3
                val red = (row[offset].toInt() and 255) / 255f
                val alternate = (row[offset + other].toInt() and 255) / 255f
                val channel = (row[offset + spill].toInt() and 255) / 255f
                val excess = max(0f, channel - max(red, alternate) - threshold)
                // Restore luminance equally to RGB so recovery does not create the opposite color cast.
                val recovered = excess * luminanceWeight * compensation
                row[offset] = ((red + recovered).coerceIn(0f, 1f) * 255).roundToInt().toByte()
                row[offset + other] =
                    ((alternate + recovered).coerceIn(0f, 1f) * 255).roundToInt().toByte()
                row[offset + spill] =
                    ((channel - excess + recovered).coerceIn(0f, 1f) * 255).roundToInt().toByte()
            }
            result.put(y, 0, row)
        }
        result
    }
}