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

import android.graphics.Bitmap
import android.os.Build
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.allowHardware
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.size.Scale
import coil3.size.Size
import coil3.size.pxOrElse
import com.radzivon.bartoshyk.avif.coder.Coder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import com.t8rin.imagetoolbox.core.utils.makeLog
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8

class HeifDecoder(
    private val source: SourceFetchResult,
    private val options: Options,
    private val exceptionLogger: ((Exception) -> Unit)? = null,
) : Decoder {

    private val coder = Coder()

    override suspend fun decode(): DecodeResult? = runInterruptible {
        try {
            // ColorSpace is preferred to be ignored due to lib is trying to handle all color profile by itself
            val sourceData = source.source.source().readByteArray()

            var mPreferredColorConfig: PreferredColorConfig = when (options.bitmapConfig) {
                Bitmap.Config.ALPHA_8 -> PreferredColorConfig.RGBA_8888
                Bitmap.Config.RGB_565 -> if (options.allowRgb565) PreferredColorConfig.RGB_565 else PreferredColorConfig.DEFAULT
                Bitmap.Config.ARGB_8888 -> PreferredColorConfig.RGBA_8888
                else -> {
                    if (options.allowHardware) {
                        PreferredColorConfig.DEFAULT
                    } else {
                        PreferredColorConfig.RGBA_8888
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && options.bitmapConfig == Bitmap.Config.RGBA_F16) {
                mPreferredColorConfig = PreferredColorConfig.RGBA_F16
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && options.bitmapConfig == Bitmap.Config.HARDWARE) {
                mPreferredColorConfig = PreferredColorConfig.HARDWARE
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && options.bitmapConfig == Bitmap.Config.RGBA_1010102) {
                mPreferredColorConfig = PreferredColorConfig.RGBA_1010102
            }

            if (options.size == Size.ORIGINAL) {
                val originalImage = coder.decode(
                    byteArray = sourceData,
                    preferredColorConfig = mPreferredColorConfig
                )

                return@runInterruptible DecodeResult(
                    image = originalImage.asImage(),
                    isSampled = false
                )
            }

            val dstWidth = options.size.width.pxOrElse { 0 }
            val dstHeight = options.size.height.pxOrElse { 0 }
            val scaleMode = when (options.scale) {
                Scale.FILL -> ScaleMode.FILL
                Scale.FIT -> ScaleMode.FIT
            }

            val originalImage = coder.decodeSampled(
                byteArray = sourceData,
                scaledWidth = dstWidth,
                scaledHeight = dstHeight,
                preferredColorConfig = mPreferredColorConfig,
                scaleMode = scaleMode,
            )

            return@runInterruptible DecodeResult(
                image = originalImage.asImage(),
                isSampled = true
            )
        } catch (e: Exception) {
            e.makeLog("HeifDecoder")
            exceptionLogger?.invoke(e)
            return@runInterruptible null
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return if (
                AVAILABLE_BRANDS.any {
                    result.source.source().rangeEquals(4, it)
                }
            ) {
                HeifDecoder(
                    source = result,
                    options = options
                )
            } else null
        }

        companion object {
            private val MIF = "ftypmif1".encodeUtf8()
            private val MSF = "ftypmsf1".encodeUtf8()
            private val HEIC = "ftypheic".encodeUtf8()
            private val HEIX = "ftypheix".encodeUtf8()
            private val HEVC = "ftyphevc".encodeUtf8()
            private val HEVX = "ftyphevx".encodeUtf8()
            private val AVIF = "ftypavif".encodeUtf8()
            private val AVIS = "ftypavis".encodeUtf8()

            private val AVAILABLE_BRANDS = listOf(MIF, MSF, HEIC, HEIX, HEVC, HEVX, AVIF, AVIS)
        }
    }
}
