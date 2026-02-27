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

import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import io.ktor.client.HttpClient
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.supervisorScope

suspend fun HttpClient.getWithProgress(
    url: String,
    onFailure: suspend (Throwable) -> Unit = {},
    onProgress: suspend (bytesSentTotal: Long, contentLength: Long?) -> Unit,
    onOpen: suspend (ByteReadChannel) -> Unit
) = supervisorScope {
    runSuspendCatching {
        prepareGet(
            urlString = url,
            block = {
                onDownload(onProgress)
            }
        ).execute { response ->
            onOpen(response.bodyAsChannel())
        }
    }.onFailure {
        onFailure(it)
    }
}