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
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.Scale
import coil3.size.Size
import coil3.size.pxOrElse
import com.t8rin.trickle.VvcDecoder
import com.t8rin.trickle.VvcScaleMode
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString

class VVCDecoder(
    private val source: SourceFetchResult,
    private val options: Options,
    private val exceptionLogger: ((Exception) -> Unit)? = null
) : Decoder {

    override suspend fun decode(): DecodeResult? = runInterruptible {
        try {
            val sourceData = source.source.source().readByteArray()

            if (options.size == Size.ORIGINAL) {
                return@runInterruptible DecodeResult(
                    image = VvcDecoder.decode(sourceData).asImage(),
                    isSampled = false
                )
            }

            val width = options.size.width.pxOrElse { 0 }
            val height = options.size.height.pxOrElse { 0 }
            val scaleMode = when (options.scale) {
                Scale.FILL -> VvcScaleMode.FILL
                Scale.FIT -> VvcScaleMode.FIT
            }

            DecodeResult(
                image = VvcDecoder.decodeSampled(
                    encoded = sourceData,
                    scaledWidth = width,
                    scaledHeight = height,
                    scaleMode = scaleMode
                ).asImage(),
                isSampled = true
            )
        } catch (e: Exception) {
            exceptionLogger?.invoke(e)
            null
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            val source = result.source.source()
            val isRawVvc = RAW_VVC_START_CODES.any { source.rangeEquals(0, it) }
            val isVvcInHeif = source.rangeEquals(4, FTYP) &&
                    source.indexOf(VVC_ITEM_TYPE, 0, SEARCH_LIMIT) >= 0

            return if (isRawVvc || isVvcInHeif) {
                VVCDecoder(
                    source = result,
                    options = options
                )
            } else null
        }

        private companion object {
            const val SEARCH_LIMIT = 64L * 1024L
            val FTYP = "ftyp".encodeUtf8()
            val VVC_ITEM_TYPE = "vvc1".encodeUtf8()
            val RAW_VVC_START_CODES = listOf(
                byteArrayOf(0, 0, 1).toByteString(),
                byteArrayOf(0, 0, 0, 1).toByteString()
            )
        }
    }
}