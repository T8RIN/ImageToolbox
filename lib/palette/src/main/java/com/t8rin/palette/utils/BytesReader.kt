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

package com.t8rin.palette.utils

import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Byte order for reading/writing
 */
internal enum class ByteOrder {
    BIG_ENDIAN,
    LITTLE_ENDIAN
}

/**
 * Reader for binary data streams
 */
internal class BytesReader(inputStream: InputStream) {
    // Always read all data for seek support
    private val data: ByteArray = inputStream.readBytes()
    private var position: Int = 0

    constructor(data: ByteArray) : this(java.io.ByteArrayInputStream(data))

    /**
     * Read a specified number of bytes
     */
    fun readData(count: Int): ByteArray {
        if (position + count > data.size) {
            throw java.io.EOFException("Unexpected end of stream")
        }
        val result = data.copyOfRange(position, position + count)
        position += count
        return result
    }

    /**
     * Seek to absolute position
     */
    fun seekSet(pos: Int) {
        if (pos < 0 || pos > data.size) {
            throw java.io.EOFException("Position out of range")
        }
        position = pos
    }

    fun trySkipBytes(count: Int): Boolean {
        return if (position + count <= data.size) {
            position += count
            true
        } else {
            false
        }
    }

    fun findPattern(pattern: ByteArray): Int {
        outer@ for (i in position until data.size - pattern.size + 1) {
            for (j in pattern.indices) {
                if (data[i + j] != pattern[j]) continue@outer
            }
            return i
        }
        return -1
    }

    /**
     * Read UInt8
     */
    fun readUInt8(): UByte {
        if (position >= data.size) throw java.io.EOFException("Unexpected end of stream")
        return data[position++].toUByte()
    }

    /**
     * Read byte
     */
    fun readByte(): Byte {
        if (position >= data.size) throw java.io.EOFException("Unexpected end of stream")
        return data[position++]
    }

    /**
     * Read Int16
     */
    fun readInt16(order: ByteOrder = ByteOrder.BIG_ENDIAN): Short {
        val data = readData(2)
        val bb = ByteBuffer.wrap(data)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        return bb.short
    }

    /**
     * Read Int32
     */
    fun readInt32(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
        val data = readData(4)
        val bb = ByteBuffer.wrap(data)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        return bb.int
    }

    /**
     * Read UInt16
     */
    fun readUInt16(order: ByteOrder = ByteOrder.BIG_ENDIAN): UShort {
        val data = readData(2)
        val bb = ByteBuffer.wrap(data)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        return bb.short.toUShort()
    }

    /**
     * Read UInt32
     */
    fun readUInt32(order: ByteOrder = ByteOrder.BIG_ENDIAN): UInt {
        val data = readData(4)
        val bb = ByteBuffer.wrap(data)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        return bb.int.toUInt()
    }

    /**
     * Read Float32
     */
    fun readFloat32(order: ByteOrder = ByteOrder.BIG_ENDIAN): Float {
        val data = readData(4)
        val bb = ByteBuffer.wrap(data)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        return bb.float
    }

    /**
     * Read UTF-16 string (null-terminated)
     */
    fun readStringUTF16NullTerminated(order: ByteOrder = ByteOrder.BIG_ENDIAN): String {
        val bytes = mutableListOf<Byte>()
        while (position + 1 < data.size) {
            val b1 = data[position++].toInt().and(0xFF)
            val b2 = data[position++].toInt().and(0xFF)
            if (b1 == 0 && b2 == 0) break // null terminator
            bytes.add(b1.toByte())
            bytes.add(b2.toByte())
        }
        val charset = if (order == ByteOrder.BIG_ENDIAN)
            java.nio.charset.StandardCharsets.UTF_16BE
        else
            java.nio.charset.StandardCharsets.UTF_16LE
        return String(bytes.toByteArray(), charset)
    }

    /**
     * Read ASCII string with specified length
     */
    fun readStringASCII(length: Int): String {
        val data = readData(length)
        return String(data, java.nio.charset.StandardCharsets.US_ASCII)
    }

    /**
     * Read ISO Latin1 string with specified length
     */
    fun readStringISOLatin1(length: Int): String {
        val data = readData(length)
        return String(data, java.nio.charset.Charset.forName("ISO-8859-1"))
    }

    /**
     * Read Adobe Pascal-style string (UInt32 length in UTF-16 code units + UTF-16 string + 2 byte null)
     */
    fun readAdobePascalStyleString(): String {
        val length = readUInt32(ByteOrder.BIG_ENDIAN).toInt()
        if (length == 0) return ""
        val stringData = mutableListOf<Byte>()
        repeat(length) {
            if (position + 1 >= data.size) throw java.io.EOFException("Unexpected end of stream")
            stringData.add(data[position++])
            stringData.add(data[position++])
        }
        // Remove trailing null if present
        val result = String(stringData.toByteArray(), java.nio.charset.StandardCharsets.UTF_16BE)
        return result.trimEnd('\u0000')
    }

    /**
     * Read UTF-8 string (null-terminated)
     */
    fun readStringUTF8NullTerminated(): String {
        val bytes = mutableListOf<Byte>()
        while (position < data.size) {
            val b = data[position++]
            if (b.toInt() == 0) break
            bytes.add(b)
        }
        return String(bytes.toByteArray(), java.nio.charset.StandardCharsets.UTF_8)
    }

    fun seekToNextInstanceOfPattern(vararg pattern: Int) {
        val searchPattern = pattern.map { it.toByte() }.toByteArray()
        var patternIndex = 0
        val startPos = position

        while (position < data.size) {
            val b = data[position].toInt().and(0xFF)
            position++

            if (b == searchPattern[patternIndex].toInt().and(0xFF)) {
                patternIndex++
                if (patternIndex >= searchPattern.size) {
                    // нашли совпадение, откатываем на начало паттерна
                    position -= searchPattern.size
                    return
                }
            } else {
                patternIndex = 0
            }
        }

        // если не нашли — вернём позицию на место и бросим EOF
        position = startPos
        throw java.io.EOFException("Pattern not found")
    }

    fun seekToNextInstanceOfASCII(pattern: String) {
        val searchPattern = pattern.toByteArray(Charsets.US_ASCII)
        var patternIndex = 0
        val startPos = position

        while (position < data.size) {
            val b = data[position].toInt().and(0xFF)
            position++

            if (b == searchPattern[patternIndex].toInt().and(0xFF)) {
                patternIndex++
                if (patternIndex >= searchPattern.size) {
                    position -= searchPattern.size
                    return
                }
            } else {
                patternIndex = 0
            }
        }

        position = startPos
        throw java.io.EOFException("Pattern not found")
    }

    /**
     * Read UTF-8 string with byte count
     */
    fun readStringUTF8(byteCount: Int): String {
        val data = readData(byteCount)
        return String(data, java.nio.charset.StandardCharsets.UTF_8)
    }

    /**
     * Read UTF-16 string with specified length (in characters)
     */
    fun readStringUTF16(order: ByteOrder, length: Int): String {
        val byteCount = length * 2
        val data = readData(byteCount)
        val charset = if (order == ByteOrder.BIG_ENDIAN)
            java.nio.charset.StandardCharsets.UTF_16BE
        else
            java.nio.charset.StandardCharsets.UTF_16LE
        return String(data, charset)
    }

    /**
     * Read Pascal-style UTF-16 string (UInt16 length in characters + UTF-16 string)
     */
    fun readPascalStringUTF16(order: ByteOrder): String {
        val length = readUInt16(order).toInt()
        if (length == 0) return ""
        val byteCount = length * 2
        val stringData = readData(byteCount)
        val charset = if (order == ByteOrder.BIG_ENDIAN)
            java.nio.charset.StandardCharsets.UTF_16BE
        else
            java.nio.charset.StandardCharsets.UTF_16LE
        return String(stringData, charset)
    }

    /**
     * Read bytes count
     */
    fun readBytes(count: Int): ByteArray {
        return readData(count)
    }

    /**
     * Skip bytes
     */
    fun seek(count: Int) {
        if (position + count > data.size) {
            throw java.io.EOFException("Unexpected end of stream")
        }
        position += count
    }

    /**
     * Current read position
     */
    val readPosition: Long
        get() = position.toLong()
}

/**
 * Helper to read text from InputStream
 */
fun InputStream.readText(): String {
    return bufferedReader().use { it.readText() }
}

