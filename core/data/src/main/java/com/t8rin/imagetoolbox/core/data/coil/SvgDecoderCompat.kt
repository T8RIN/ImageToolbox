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

package com.t8rin.imagetoolbox.core.data.coil

import coil3.ImageLoader
import coil3.decode.DecodeResult
import coil3.decode.DecodeUtils
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.Size
import coil3.size.pxOrElse
import coil3.svg.SvgDecoder
import coil3.svg.isSvg
import com.hashsequence.coilresvg.ResvgDecoder
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.utils.makeLog

internal class SvgDecoderCompat(
    private val source: ImageSource,
    options: Options,
    minimumSize: Int? = null
) : Decoder {

    private val options = minimumSize?.let {
        options.copy(
            size = options.size.coerceAtLeast(minimumSize)
        )
    } ?: options

    private fun source() = ImageSource(
        file = source.file(),
        fileSystem = source.fileSystem,
        metadata = source.metadata
    )

    override suspend fun decode(): DecodeResult {
        if (!isResvgAvailable) return decodeDefault()

        return runSuspendCatching {
            decodeNative()
        }.onFailure {
            if (it is LinkageError) isResvgAvailable = false
        }.getOrNull() ?: decodeDefault()
    }

    private suspend fun decodeNative() = ResvgDecoder(
        source = source(),
        options = options
    ).decode()

    private suspend fun decodeDefault() = SvgDecoder(
        source = source(),
        options = options
    ).decode().also {
        "fallback coil-svg decoder".makeLog()
    }

    private fun Size.coerceAtLeast(size: Int): Size = Size(
        width = width.pxOrElse { 0 }.coerceAtLeast(size),
        height = height.pxOrElse { 0 }.coerceAtLeast(size)
    )

    class Factory(
        private val minimumSize: Int? = null
    ) : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            if (!isApplicable(result)) return null

            return SvgDecoderCompat(
                source = result.source,
                options = options,
                minimumSize = minimumSize
            )
        }

        private fun isApplicable(result: SourceFetchResult): Boolean {
            return result.mimeType == MIME_TYPE_SVG ||
                    result.mimeType == MIME_TYPE_XML ||
                    DecodeUtils.isSvg(result.source.source())
        }

        private companion object {
            private const val MIME_TYPE_SVG = "image/svg+xml"
            private const val MIME_TYPE_XML = "text/xml"
        }
    }

    private companion object {
        @Volatile
        private var isResvgAvailable = true
    }
}
