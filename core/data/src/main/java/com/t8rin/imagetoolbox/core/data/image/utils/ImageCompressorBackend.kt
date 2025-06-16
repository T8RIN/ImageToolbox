/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.data.image.utils

import android.content.Context
import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.AvifBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.BmpBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.HeicBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.IcoBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.Jpeg2000Backend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.JpegliBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.JpgBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.JxlBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.MozJpegBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.PngLosslessBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.PngLossyBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.QoiBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.StaticGifBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.TiffBackend
import com.t8rin.imagetoolbox.core.data.image.utils.compressor.WebpBackend
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality


internal interface ImageCompressorBackend {

    suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray

    class Factory {
        fun create(
            imageFormat: ImageFormat,
            context: Context,
            imageScaler: ImageScaler<Bitmap>
        ): ImageCompressorBackend = when (imageFormat) {
            ImageFormat.Bmp -> BmpBackend
            ImageFormat.Png.Lossless -> PngLosslessBackend
            ImageFormat.Webp.Lossless -> WebpBackend(isLossless = true)
            ImageFormat.Webp.Lossy -> WebpBackend(isLossless = false)
            ImageFormat.MozJpeg -> MozJpegBackend
            ImageFormat.Jxl.Lossless -> JxlBackend(isLossless = true)
            ImageFormat.Jxl.Lossy -> JxlBackend(isLossless = false)
            ImageFormat.Png.Lossy -> PngLossyBackend
            ImageFormat.Jpegli -> JpegliBackend
            ImageFormat.Jpeg2000.J2k -> Jpeg2000Backend(isJ2K = true)
            ImageFormat.Jpeg2000.Jp2 -> Jpeg2000Backend(isJ2K = false)
            ImageFormat.Qoi -> QoiBackend
            ImageFormat.Ico -> IcoBackend(imageScaler)

            ImageFormat.Jpeg,
            ImageFormat.Jpg -> JpgBackend

            ImageFormat.Tif,
            ImageFormat.Tiff -> TiffBackend(context)

            ImageFormat.Heic.Lossless,
            ImageFormat.Heif.Lossless -> HeicBackend(isLossless = true)

            ImageFormat.Heic.Lossy,
            ImageFormat.Heif.Lossy -> HeicBackend(isLossless = false)

            ImageFormat.Avif.Lossless -> AvifBackend(isLossless = true)
            ImageFormat.Avif.Lossy -> AvifBackend(isLossless = false)
            ImageFormat.Gif -> StaticGifBackend
        }
    }

}