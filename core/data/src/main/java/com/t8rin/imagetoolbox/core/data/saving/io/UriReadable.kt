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

import android.content.Context
import android.net.Uri
import com.t8rin.imagetoolbox.core.data.utils.openWriteableStream
import com.t8rin.logger.makeLog
import io.ktor.utils.io.charsets.Charset
import java.io.ByteArrayOutputStream


class UriReadable(
    private val uri: Uri,
    private val context: Context
) : StreamReadable by StreamReadable(
    inputStream = context.contentResolver.openInputStream(uri) ?: ByteArray(0).inputStream()
)

class UriWriteable(
    private val uri: Uri,
    private val context: Context
) : StreamWriteable by StreamWriteable(
    outputStream = context.openWriteableStream(
        uri = uri,
        onFailure = {
            uri.makeLog("UriWriteable write")
            it.makeLog("UriWriteable write")
            throw it
        }
    ) ?: ByteArrayOutputStream(0)
)

class ByteArrayReadable(
    private val byteArray: ByteArray
) : StreamReadable by StreamReadable(
    inputStream = byteArray.inputStream()
)

class StringReadable(
    private val string: String,
    private val charset: Charset = Charsets.UTF_8
) : StreamReadable by ByteArrayReadable(
    byteArray = string.toByteArray(charset)
)