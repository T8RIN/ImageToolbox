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

package com.t8rin.imagetoolbox.core.data.saving.io

import com.t8rin.imagetoolbox.core.domain.saving.io.Readable
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.logger.makeLog
import java.io.InputStream
import java.io.OutputStream

private class StreamWriteableImpl(
    outputStream: OutputStream
) : StreamWriteable {

    override val stream = outputStream

    override fun writeBytes(byteArray: ByteArray) = stream.write(byteArray)

    override fun close() {
        stream.flush()
        stream.close()
    }

}

private class StreamReadableImpl(
    inputStream: InputStream
) : StreamReadable {

    override val stream = inputStream

    override fun readBytes(): ByteArray = stream.readBytes()

    override fun copyTo(writeable: Writeable) {
        if (writeable is StreamWriteable) {
            stream.copyTo(writeable.stream)
        } else {
            writeable.writeBytes(readBytes())
        }
    }

    override fun close() = stream.close()

}

interface StreamReadable : Readable {
    val stream: InputStream

    companion object {
        operator fun invoke(
            inputStream: InputStream
        ): StreamReadable = StreamReadableImpl(inputStream)
    }
}

interface StreamWriteable : Writeable {
    val stream: OutputStream

    companion object {
        operator fun invoke(
            outputStream: OutputStream
        ): StreamWriteable = StreamWriteableImpl(outputStream)
    }
}

fun StreamWriteable.shielded(): StreamWriteable = CloseShieldWriteable(this)

private class CloseShieldWriteable(wrapped: StreamWriteable) : StreamWriteable by wrapped {
    override fun close() {
        "can't be closed".makeLog("CloseShieldWriteable")
    }
}