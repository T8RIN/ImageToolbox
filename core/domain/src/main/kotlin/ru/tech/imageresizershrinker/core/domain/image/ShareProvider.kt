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

package ru.tech.imageresizershrinker.core.domain.image

import ru.tech.imageresizershrinker.core.domain.saving.io.Writeable

interface ShareProvider {

    suspend fun cacheByteArray(
        byteArray: ByteArray,
        filename: String
    ): String?

    suspend fun shareByteArray(
        byteArray: ByteArray,
        filename: String,
        onComplete: () -> Unit = {}
    )

    suspend fun cacheData(
        writeData: suspend (Writeable) -> Unit,
        filename: String
    ): String?

    suspend fun shareData(
        writeData: suspend (Writeable) -> Unit,
        filename: String,
        onComplete: () -> Unit = {}
    )

    suspend fun shareUri(
        uri: String,
        type: String? = null,
        onComplete: () -> Unit
    )

    suspend fun shareUris(
        uris: List<String>
    )

    fun shareText(
        value: String,
        onComplete: () -> Unit
    )

}