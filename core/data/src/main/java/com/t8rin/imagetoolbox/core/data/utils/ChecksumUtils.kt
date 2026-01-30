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

import com.t8rin.imagetoolbox.core.data.saving.io.StreamReadable
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.saving.io.Readable
import java.io.InputStream
import java.security.MessageDigest

private const val STREAM_BUFFER_LENGTH = 1024

fun HashingType.computeBytesFromReadable(
    readable: Readable
): ByteArray = if (readable is StreamReadable) {
    computeBytesFromInputStream(readable.stream)
} else {
    computeBytesFromByteArray(readable.readBytes())
}

fun HashingType.computeFromReadable(
    readable: Readable
): String = if (readable is StreamReadable) {
    computeFromInputStream(readable.stream)
} else {
    computeFromByteArray(readable.readBytes())
}

internal fun HashingType.computeFromByteArray(
    byteArray: ByteArray
): String = computeFromInputStream(byteArray.inputStream())

internal fun HashingType.computeFromInputStream(
    inputStream: InputStream
): String = inputStream.buffered().use {
    val byteArray = updateDigest(it).digest()
    val hexCode = encodeHex(byteArray, true)

    return@use String(hexCode)
}

internal fun HashingType.computeBytesFromByteArray(
    byteArray: ByteArray
): ByteArray = computeBytesFromInputStream(byteArray.inputStream())

internal fun HashingType.computeBytesFromInputStream(
    inputStream: InputStream
): ByteArray = inputStream.buffered().use {
    return@use updateDigest(it).digest()
}

/**
 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
 * The returned array will be double the length of the passed array, as it takes two characters to represent any
 * given byte.
 *
 * @param data a byte[] to convert to Hex characters
 * @param toLowerCase `true` converts to lowercase, `false` to uppercase
 * @return A char[] containing hexadecimal characters in the selected case
 */
internal fun encodeHex(
    data: ByteArray,
    toLowerCase: Boolean
): CharArray = encodeHex(
    data = data,
    toDigits = if (toLowerCase) {
        DIGITS_LOWER
    } else {
        DIGITS_UPPER
    }
)

/**
 * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
 * The returned array will be double the length of the passed array, as it takes two characters to represent any
 * given byte.
 *
 * @param data a byte[] to convert to Hex characters
 * @param toDigits the output alphabet (must contain at least 16 chars)
 * @return A char[] containing the appropriate characters from the alphabet
 *         For best results, this should be either upper- or lower-case hex.
 */
internal fun encodeHex(
    data: ByteArray,
    toDigits: CharArray
): CharArray {
    val l = data.size
    val out = CharArray(l shl 1)
    // two characters form the hex value.
    var i = 0
    var j = 0
    while (i < l) {
        out[j++] = toDigits[0xF0 and data[i].toInt() ushr 4]
        out[j++] = toDigits[0x0F and data[i].toInt()]
        i++
    }
    return out
}

/**
 * Reads through an InputStream and updates the digest for the data
 *
 * @param HashingType The ChecksumType to use (e.g. MD5)
 * @param data Data to digest
 * @return the digest
 */
private fun HashingType.updateDigest(
    data: InputStream
): MessageDigest {
    val digest = toDigest()

    val buffer = ByteArray(STREAM_BUFFER_LENGTH)
    var read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
    while (read > -1) {
        digest.update(buffer, 0, read)
        read = data.read(buffer, 0, STREAM_BUFFER_LENGTH)
    }
    return digest
}

/**
 * Used to build output as Hex
 */
private val DIGITS_LOWER =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 * Used to build output as Hex
 */
private val DIGITS_UPPER =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')


private fun HashingType.toDigest(): MessageDigest = MessageDigest.getInstance(digest)