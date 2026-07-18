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

import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel

suspend fun HttpClient.getWithProgress(
    url: String,
    onProgress: suspend (bytesSentTotal: Long, contentLength: Long?) -> Unit,
    onOpen: suspend (ByteReadChannel) -> Unit
) {
    prepareGet(
        urlString = url,
        block = {
            onDownload(onProgress)
        }
    ).execute { response ->
        check(response.status.value in (200 until 400)) {
            "Download failed with HTTP ${response.status.value} ${response.status.description}"
        }
        onOpen(response.bodyAsChannel())
    }
}