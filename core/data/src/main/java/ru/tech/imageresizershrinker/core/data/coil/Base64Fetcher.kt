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

package ru.tech.imageresizershrinker.core.data.coil

import android.util.Base64
import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.Buffer
import ru.tech.imageresizershrinker.core.domain.utils.isBase64
import ru.tech.imageresizershrinker.core.domain.utils.trimToBase64

internal class Base64Fetcher(
    private val options: Options,
    private val base64: String
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val byteArray = runCatching {
            Base64.decode(base64, Base64.DEFAULT)
        }.getOrNull() ?: return null

        return SourceFetchResult(
            source = ImageSource(
                source = Buffer().apply { write(byteArray) },
                fileSystem = options.fileSystem,
            ),
            mimeType = null,
            dataSource = DataSource.MEMORY,
        )
    }

    class Factory : Fetcher.Factory<Uri> {
        override fun create(
            data: Uri,
            options: Options,
            imageLoader: ImageLoader,
        ): Fetcher? {
            val stripped = data.toString().trimToBase64()
            return if (stripped.isBase64()) {
                Base64Fetcher(
                    options = options,
                    base64 = stripped
                )
            } else null
        }
    }
}