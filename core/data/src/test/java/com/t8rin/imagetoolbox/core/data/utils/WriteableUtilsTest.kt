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

package com.t8rin.imagetoolbox.core.data.utils

import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.ByteArrayOutputStream

class WriteableUtilsTest {
    @Test
    fun arrayWritesRemainBatchedAndSlicesPreserveBytes() {
        val writes = mutableListOf<ByteArray>()
        val destination = ByteArrayOutputStream()
        val writeable = object : Writeable {
            override fun writeBytes(byteArray: ByteArray) {
                writes += byteArray
                destination.write(byteArray)
            }

            override fun close() = Unit
        }
        val stream = writeable.outputStream()
        val bytes = ByteArray(8192) { it.toByte() }
        stream.write(bytes)
        stream.write(bytes, 0, bytes.size)
        stream.write(bytes, 13, 4096)
        stream.write(bytes, bytes.size, 0)
        stream.write(ByteArray(0))
        stream.write(255)

        assertEquals(4, writes.size)
        assertSame(bytes, writes[0])
        assertSame(bytes, writes[1])
        assertArrayEquals(
            bytes + bytes + bytes.copyOfRange(13, 4109) + byteArrayOf(-1),
            destination.toByteArray()
        )
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun invalidSliceFailsBeforeWriting() {
        val writeable = object : Writeable {
            override fun writeBytes(byteArray: ByteArray) = error("Unexpected write")
            override fun close() = Unit
        }
        writeable.outputStream().write(ByteArray(8), 4, Int.MAX_VALUE)
    }
}