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

package com.t8rin.imagetoolbox.core.domain.utils

import java.io.InputStream

fun InputStream.withProgress(
    total: Long,
    onProgress: (Float) -> Unit
): InputStream = ProgressInputStream(
    source = this,
    total = total,
    onProgress = onProgress
)

private class ProgressInputStream(
    private val source: InputStream,
    private val total: Long,
    private val onProgress: (Float) -> Unit
) : InputStream() {

    private var readBytes = 0L

    override fun read(): Int {
        val r = source.read()
        if (r != -1) {
            readBytes++
            report()
        }
        return r
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val r = source.read(b, off, len)
        if (r > 0) {
            readBytes += r
            report()
        }
        return r
    }

    private fun report() {
        if (total > 0) {
            onProgress(readBytes.toFloat() / total)
        }
    }

    override fun close() = source.close()
}