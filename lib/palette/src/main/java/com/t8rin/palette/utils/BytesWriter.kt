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

import java.io.OutputStream
import java.nio.ByteBuffer

/**
 * Writer for binary data streams
 */
internal class BytesWriter(private val outputStream: OutputStream) {

    /**
     * Write bytes
     */
    fun writeData(data: ByteArray) {
        outputStream.write(data)
    }

    /**
     * Write UInt8
     */
    fun writeUInt8(value: UByte) {
        outputStream.write(value.toInt())
    }

    /**
     * Write byte
     */
    fun writeByte(value: Byte) {
        outputStream.write(value.toInt())
    }

    /**
     * Write Int16
     */
    fun writeInt16(value: Short, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val bb = ByteBuffer.allocate(2)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        bb.putShort(value)
        outputStream.write(bb.array())
    }

    /**
     * Write Int32
     */
    fun writeInt32(value: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val bb = ByteBuffer.allocate(4)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        bb.putInt(value)
        outputStream.write(bb.array())
    }

    /**
     * Write UInt16
     */
    fun writeUInt16(value: UShort, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val bb = ByteBuffer.allocate(2)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        bb.putShort(value.toShort())
        outputStream.write(bb.array())
    }

    /**
     * Write UInt32
     */
    fun writeUInt32(value: UInt, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val bb = ByteBuffer.allocate(4)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        bb.putInt(value.toInt())
        outputStream.write(bb.array())
    }

    /**
     * Write Float32
     */
    fun writeFloat32(value: Float, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val bb = ByteBuffer.allocate(4)
        bb.order(if (order == ByteOrder.BIG_ENDIAN) java.nio.ByteOrder.BIG_ENDIAN else java.nio.ByteOrder.LITTLE_ENDIAN)
        bb.putFloat(value)
        outputStream.write(bb.array())
    }

    /**
     * Write UTF-16 string (null-terminated)
     */
    fun writeStringUTF16NullTerminated(value: String, order: ByteOrder = ByteOrder.BIG_ENDIAN) {
        val charset = if (order == ByteOrder.BIG_ENDIAN)
            java.nio.charset.StandardCharsets.UTF_16BE
        else
            java.nio.charset.StandardCharsets.UTF_16LE
        val bytes = value.toByteArray(charset)
        outputStream.write(bytes)
        // Write null terminator
        outputStream.write(0)
        outputStream.write(0)
    }

    /**
     * Write ASCII string
     */
    fun writeStringASCII(value: String) {
        val bytes = value.toByteArray(java.nio.charset.StandardCharsets.US_ASCII)
        outputStream.write(bytes)
    }

    /**
     * Write Adobe Pascal-style string (UInt32 length in UTF-16 characters + UTF-16BE string)
     */
    fun writeAdobePascalStyleString(value: String) {
        val utf16Bytes = value.toByteArray(java.nio.charset.StandardCharsets.UTF_16BE)
        val length = utf16Bytes.size / 2 // UTF-16 is 2 bytes per character
        writeUInt32(length.toUInt(), ByteOrder.BIG_ENDIAN)
        if (utf16Bytes.isNotEmpty()) {
            outputStream.write(utf16Bytes)
        }
    }

    /**
     * Write ASCII string with length prefix (UInt32, little endian)
     */
    fun writeStringASCIIWithLength(value: String, order: ByteOrder = ByteOrder.LITTLE_ENDIAN) {
        val bytes = value.toByteArray(java.nio.charset.StandardCharsets.US_ASCII)
        writeUInt32(bytes.size.toUInt(), order)
        if (bytes.isNotEmpty()) {
            outputStream.write(bytes)
        }
    }

    /**
     * Write UTF-8 string (null-terminated)
     */
    fun writeStringUTF8NullTerminated(value: String) {
        val bytes = value.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        outputStream.write(bytes)
        outputStream.write(0) // null terminator
    }

    /**
     * Write UTF-8 string with length prefix (UInt32, little endian)
     */
    fun writeStringUTF8WithLength(value: String, order: ByteOrder = ByteOrder.LITTLE_ENDIAN) {
        val bytes = value.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        writeUInt32(bytes.size.toUInt(), order)
        if (bytes.isNotEmpty()) {
            outputStream.write(bytes)
        }
    }

    /**
     * Write pattern bytes
     */
    fun writePattern(vararg bytes: Int) {
        bytes.forEach { outputStream.write(it) }
    }

    /**
     * Write Pascal-style UTF-16 string (UInt16 length in characters + UTF-16 string)
     */
    fun writePascalStringUTF16(value: String, order: ByteOrder) {
        val charset = if (order == ByteOrder.BIG_ENDIAN)
            java.nio.charset.StandardCharsets.UTF_16BE
        else
            java.nio.charset.StandardCharsets.UTF_16LE
        val utf16Bytes = value.toByteArray(charset)
        val length = utf16Bytes.size / 2 // UTF-16 is 2 bytes per character
        if (length > 65535) {
            throw IllegalArgumentException("String too long for Pascal UTF-16 (max 65535 characters)")
        }
        writeUInt16(length.toUShort(), order)
        if (utf16Bytes.isNotEmpty()) {
            outputStream.write(utf16Bytes)
        }
    }

    /**
     * Write Float32 array
     */
    fun writeFloat32(values: List<Float>, order: ByteOrder) {
        values.forEach { writeFloat32(it, order) }
    }
}

