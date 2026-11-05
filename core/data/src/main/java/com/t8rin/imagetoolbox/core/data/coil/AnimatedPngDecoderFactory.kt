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
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.BufferedSource
import okio.ByteString.Companion.decodeHex
import oupson.apng.coil.AnimatedPngDecoder

internal object AnimatedPngDecoderFactory : Decoder.Factory {
    private val decoder = AnimatedPngDecoder.Factory()
    private val signature = "89504e470d0a1a0a".decodeHex()

    override fun create(
        result: SourceFetchResult,
        options: Options,
        imageLoader: ImageLoader
    ): Decoder? {
        val source = result.source.source()
        if (!source.rangeEquals(0, signature) || source.isStaticPng()) return null
        return decoder.create(result, options, imageLoader)
    }

    private fun BufferedSource.isStaticPng(): Boolean = peek().use { header ->
        header.skip(8)
        var remaining = 64 * 1024L - 8
        while (remaining >= 8 && header.request(8)) {
            val length = header.readInt().toLong() and 0xFFFFFFFFL
            when (header.readInt()) {
                0x6163544C -> return@use false // acTL
                0x49444154, 0x49454E44 -> return@use true // IDAT, IEND
            }
            remaining -= 8
            // Leave large ancillary chunks to the decoder instead of buffering them here.
            if (length + 4 > remaining || !header.request(length + 4)) return@use false
            header.skip(length + 4)
            remaining -= length + 4
        }
        false
    }
}
