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
import java.io.InterruptedIOException
import java.security.MessageDigest

fun HashingType.computeBytesFromReadable(
    readable: Readable
): ByteArray = if (readable is StreamReadable) {
    computeBytesFromInputStream(readable.stream)
} else {
    computeBytesFromByteArray(readable.readBytes())
}

fun HashingType.computeFromReadable(
    readable: Readable
): String = computeBytesFromReadable(readable).toHexString()

internal fun HashingType.computeFromByteArray(
    byteArray: ByteArray
): String = computeBytesFromByteArray(byteArray).toHexString()

internal fun HashingType.computeBytesFromByteArray(
    byteArray: ByteArray
): ByteArray = MessageDigest.getInstance(digest).digest(byteArray)

internal fun HashingType.computeFromInputStream(
    inputStream: InputStream
): String = computeBytesFromInputStream(inputStream).toHexString()

internal fun HashingType.computeBytesFromInputStream(
    inputStream: InputStream
): ByteArray = inputStream.use {
    val digest = MessageDigest.getInstance(digest)
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    while (true) {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException()
        val read = it.read(buffer)
        if (read < 0) break
        digest.update(buffer, 0, read)
    }
    digest.digest()
}