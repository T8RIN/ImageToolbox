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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.Decoder
import coil.fetch.SourceResult
import coil.request.Options
import coil.size.Scale
import coil.size.Size
import coil.size.pxOrElse
import com.awxkee.jxlcoder.JxlAnimatedImage
import com.awxkee.jxlcoder.JxlResizeFilter
import com.awxkee.jxlcoder.PreferredColorConfig
import com.awxkee.jxlcoder.ScaleMode
import com.awxkee.jxlcoder.animation.AnimatedDrawable
import com.awxkee.jxlcoder.animation.JxlAnimatedStore
import kotlinx.coroutines.runInterruptible
import okio.BufferedSource
import okio.ByteString.Companion.toByteString

internal class AnimatedJxlDecoderCoil2(
    private val source: SourceResult,
    private val options: Options,
    private val context: Context,
    private val preheatFrames: Int,
    private val exceptionLogger: ((Exception) -> Unit)? = null,
) : Decoder {

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

            if (options.size == Size.ORIGINAL) {
                val originalImage = JxlAnimatedImage(
                    byteArray = sourceData,
                    preferredColorConfig = mPreferredColorConfig
                )
                return@runInterruptible DecodeResult(
                    drawable = originalImage.drawable(),
                    isSampled = false
                )
            }

            val dstWidth = options.size.width.pxOrElse { 0 }
            val dstHeight = options.size.height.pxOrElse { 0 }
            val scaleMode = when (options.scale) {
                Scale.FILL -> ScaleMode.FILL
                Scale.FIT -> ScaleMode.FIT
            }

            val originalImage = JxlAnimatedImage(
                byteArray = sourceData,
                preferredColorConfig = mPreferredColorConfig,
                scaleMode = scaleMode,
                jxlResizeFilter = JxlResizeFilter.BILINEAR
            )

            DecodeResult(
                drawable = originalImage.drawable(
                    dstWidth = dstWidth,
                    dstHeight = dstHeight
                ),
                isSampled = true
            )
        } catch (e: Exception) {
            exceptionLogger?.invoke(e)
            return@runInterruptible null
        }
    }

    private fun JxlAnimatedImage.drawable(
        dstWidth: Int = 0,
        dstHeight: Int = 0
    ): Drawable = if (numberOfFrames > 1) {
        AnimatedDrawable(
            frameStore = JxlAnimatedStore(
                jxlAnimatedImage = this,
                targetWidth = dstWidth,
                targetHeight = dstHeight
            ),
            preheatFrames = preheatFrames,
            firstFrameAsPlaceholder = true
        )
    } else {
        BitmapDrawable(
            context.resources,
            getFrame(
                frame = 0,
                scaleWidth = dstWidth,
                scaleHeight = dstHeight
            )
        )
    }

    class Factory(
        private val context: Context,
        private val preheatFrames: Int = 6,
        private val exceptionLogger: ((Exception) -> Unit)? = null,
    ) : Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader,
        ) = if (isJXL(result.source.source())) {
            AnimatedJxlDecoderCoil2(
                source = result,
                options = options,
                context = context,
                preheatFrames = preheatFrames,
                exceptionLogger = exceptionLogger,
            )
        } else null

        companion object {
            private val MAGIC_1 = byteArrayOf(0xFF.toByte(), 0x0A).toByteString()
            private val MAGIC_2 = byteArrayOf(
                0x0.toByte(),
                0x0.toByte(),
                0x0.toByte(),
                0x0C.toByte(),
                0x4A,
                0x58,
                0x4C,
                0x20,
                0x0D,
                0x0A,
                0x87.toByte(),
                0x0A
            ).toByteString()

            private fun isJXL(source: BufferedSource): Boolean {
                return source.rangeEquals(0, MAGIC_1) || source.rangeEquals(
                    0,
                    MAGIC_2
                )
            }
        }
    }

}