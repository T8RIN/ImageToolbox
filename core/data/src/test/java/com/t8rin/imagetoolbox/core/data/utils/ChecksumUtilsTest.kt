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

import com.t8rin.imagetoolbox.core.domain.model.HashingType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.security.MessageDigest

class ChecksumUtilsTest {
    @Test
    fun streamAndByteArrayDigestsMatchForShortReadsAndBufferBoundaries() {
        for (size in listOf(0, 1, 8191, 8192, 8193, 100_000)) {
            val bytes = ByteArray(size) { (it * 37).toByte() }
            for (type in listOf(
                HashingType.MD5,
                HashingType.SHA_1,
                HashingType.SHA_256,
                HashingType.SHA_512
            )) {
                var closed = false
                val stream = object : ByteArrayInputStream(bytes) {
                    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
                        super.read(buffer, offset, minOf(7, length))

                    override fun close() {
                        closed = true
                    }
                }
                val expected = MessageDigest.getInstance(type.digest).digest(bytes).toHexString()
                assertEquals(expected, type.computeFromInputStream(stream))
                assertEquals(expected, type.computeFromByteArray(bytes))
                assertTrue(closed)
            }
        }
    }

    @Test
    fun interruptionStopsReadingAndClosesTheStream() {
        var reads = 0
        var closed = false
        val stream = object : InputStream() {
            override fun read(): Int {
                reads++; return 0
            }

            override fun close() {
                closed = true
            }
        }
        try {
            Thread.currentThread().interrupt()
            assertThrows(InterruptedIOException::class.java) {
                HashingType.SHA_256.computeFromInputStream(
                    stream
                )
            }
        } finally {
            Thread.interrupted()
        }
        assertEquals(0, reads)
        assertTrue(closed)
    }

    @Test
    fun failedReadsCloseTheStream() {
        var closed = false
        val stream = object : InputStream() {
            override fun read(): Int = throw IOException("read failed")
            override fun close() {
                closed = true
            }
        }
        assertThrows(IOException::class.java) { HashingType.SHA_256.computeFromInputStream(stream) }
        assertTrue(closed)
    }
}