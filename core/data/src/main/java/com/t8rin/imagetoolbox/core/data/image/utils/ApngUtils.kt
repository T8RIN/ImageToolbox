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

package com.t8rin.imagetoolbox.core.data.image.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.CRC32

fun ByteArray.withApngLoopCount(loopCount: Int): ByteArray = apply {
    var offset = 8
    while (offset + 12 <= size) {
        val length = ByteBuffer.wrap(this, offset, 4).order(ByteOrder.BIG_ENDIAN).int
        val typeOffset = offset + 4
        val dataOffset = typeOffset + 4
        if (dataOffset + length + 4 > size) return@apply
        if (decodeToString(typeOffset, dataOffset) == "acTL" && length >= 8) {
            ByteBuffer.wrap(this, dataOffset + 4, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(loopCount)
            val crc = CRC32().apply {
                update(this@withApngLoopCount, typeOffset, 4 + length)
            }
            ByteBuffer.wrap(this, dataOffset + length, 4)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt(crc.value.toInt())
            return@apply
        }
        offset = dataOffset + length + 4
    }
}
