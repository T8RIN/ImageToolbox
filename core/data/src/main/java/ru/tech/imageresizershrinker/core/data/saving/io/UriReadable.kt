/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.data.saving.io

import android.content.Context
import android.net.Uri
import io.ktor.utils.io.charsets.Charset
import ru.tech.imageresizershrinker.core.domain.saving.io.Readable


class UriReadable(
    private val uri: Uri,
    private val context: Context
) : Readable by StreamReadable(
    inputStream = context.contentResolver.openInputStream(uri) ?: ByteArray(0).inputStream()
)

class ByteArrayReadable(
    private val byteArray: ByteArray
) : Readable by StreamReadable(
    inputStream = byteArray.inputStream()
)

class StringReadable(
    private val string: String,
    private val charset: Charset = Charsets.UTF_8
) : Readable by ByteArrayReadable(
    byteArray = string.toByteArray(charset)
)