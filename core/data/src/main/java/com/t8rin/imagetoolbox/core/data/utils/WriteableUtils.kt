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

import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import java.io.OutputStream

fun Writeable.outputStream(): OutputStream = if (this is StreamWriteable) {
    stream
} else {
    object : OutputStream() {
        override fun write(b: Int) = writeBytes(byteArrayOf(b.toByte()))

        override fun write(b: ByteArray) = write(b, 0, b.size)

        override fun write(b: ByteArray, off: Int, len: Int) {
            if (off < 0 || len < 0 || off > b.size - len) throw IndexOutOfBoundsException()
            if (len == 0) return
            writeBytes(if (off == 0 && len == b.size) b else b.copyOfRange(off, off + len))
        }
    }
}