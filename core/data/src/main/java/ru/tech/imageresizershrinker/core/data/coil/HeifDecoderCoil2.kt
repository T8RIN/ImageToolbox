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

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.Scale
import coil.size.pxOrElse
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import kotlinx.coroutines.runInterruptible
import okio.ByteString.Companion.encodeUtf8

internal class HeifDecoderCoil2(
    private val source: SourceResult,
    private val options: Options,
    private val exceptionLogger: ((Exception) -> Unit)? = null,
) : Decoder {

    private val coder = HeifCoder()

    override suspend fun decode(): DecodeResult? = runInterruptible {
        try {
            // ColorSpace is preferred to be ignored due to lib is trying to handle all color profile by itself
            val sourceData = source.source.source().readByteArray()

            var mPreferredColorConfig: PreferredColorConfig = when (options.config) {
                Bitmap.Config.ALPHA_8 -> PreferredColorConfig.RGBA_8888
                Bitmap.Config.RGB_565 -> if (options.allowRgb565) PreferredColorConfig.RGB_565 else PreferredColorConfig.DEFAULT
                Bitmap.Config.ARGB_8888 -> PreferredColorConfig.RGBA_8888
                else -> PreferredColorConfig.DEFAULT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && options.config == Bitmap.Config.RGBA_F16) {
                mPreferredColorConfig = PreferredColorConfig.RGBA_F16
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && options.config == Bitmap.Config.HARDWARE) {
                mPreferredColorConfig = PreferredColorConfig.HARDWARE
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && options.config == Bitmap.Config.RGBA_1010102) {
                mPreferredColorConfig = PreferredColorConfig.RGBA_1010102
            }

            if (options.size == coil.size.Size.ORIGINAL) {
                val originalImage =
                    coder.decode(
                        sourceData,
                        preferredColorConfig = mPreferredColorConfig
                    )
                return@runInterruptible DecodeResult(
                    BitmapDrawable(
                        options.context.resources,
                        originalImage
                    ), false
                )
            }

            val dstWidth = options.size.width.pxOrElse { 0 }
            val dstHeight = options.size.height.pxOrElse { 0 }
            val scaleMode = when (options.scale) {
                Scale.FILL -> ScaleMode.FILL
                Scale.FIT -> ScaleMode.FIT
            }

            val originalImage =
                coder.decodeSampled(
                    sourceData,
                    dstWidth,
                    dstHeight,
                    preferredColorConfig = mPreferredColorConfig,
                    scaleMode,
                )
            return@runInterruptible DecodeResult(
                BitmapDrawable(
                    options.context.resources,
                    originalImage
                ), true
            )
        } catch (e: Exception) {
            exceptionLogger?.invoke(e)
            return@runInterruptible null
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            return if (AVAILABLE_BRANDS.any {
                    result.source.source().rangeEquals(4, it)
                }) HeifDecoderCoil2(result, options) else null
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