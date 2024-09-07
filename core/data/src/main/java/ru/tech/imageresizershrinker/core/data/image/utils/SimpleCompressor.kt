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

package ru.tech.imageresizershrinker.core.data.image.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.awxkee.aire.Aire
import com.awxkee.aire.AireColorMapper
import com.awxkee.aire.AirePaletteDithering
import com.awxkee.aire.AireQuantize
import com.awxkee.jxlcoder.JxlChannelsConfiguration
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.JxlCompressionOption
import com.awxkee.jxlcoder.JxlDecodingSpeed
import com.awxkee.jxlcoder.JxlEffort
import com.gemalto.jp2.JP2Encoder
import com.radzivon.bartoshyk.avif.coder.AvifSpeed
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreciseMode
import com.t8rin.qoi_coder.QOIEncoder
import io.github.awxkee.jpegli.coder.IccStrategy
import io.github.awxkee.jpegli.coder.JpegliCoder
import io.github.awxkee.jpegli.coder.Scalar
import kotlinx.coroutines.coroutineScope
import org.beyka.tiffbitmapfactory.CompressionScheme
import org.beyka.tiffbitmapfactory.Orientation
import org.beyka.tiffbitmapfactory.TiffSaver
import org.beyka.tiffbitmapfactory.TiffSaver.SaveOptions
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder


internal abstract class SimpleCompressor {

    abstract suspend fun compress(
        image: Bitmap,
        quality: Quality
    ): ByteArray

    companion object {

        fun getInstance(
            imageFormat: ImageFormat,
            context: Context,
            imageScaler: ImageScaler<Bitmap>
        ): SimpleCompressor = when (imageFormat) {
            ImageFormat.Avif.Lossless -> AvifLossless(context)
            ImageFormat.Avif.Lossy -> AvifLossy(context)
            ImageFormat.Bmp -> Bmp
            ImageFormat.Heic.Lossless,
            ImageFormat.Heif.Lossless -> HeicLossless(context)

            ImageFormat.Heic.Lossy,
            ImageFormat.Heif.Lossy -> HeicLossy(context)

            ImageFormat.Jpeg,
            ImageFormat.Jpg -> Jpg

            ImageFormat.Png.Lossless -> PngLossless
            ImageFormat.Webp.Lossless -> WebpLossless
            ImageFormat.Webp.Lossy -> WebpLossy
            ImageFormat.MozJpeg -> MozJpeg
            ImageFormat.Jxl.Lossless -> JxlLossless
            ImageFormat.Jxl.Lossy -> JxlLossy
            ImageFormat.Png.Lossy -> PngLossy
            ImageFormat.Jpegli -> Jpegli
            ImageFormat.Jpeg2000.J2k -> J2k
            ImageFormat.Jpeg2000.Jp2 -> Jp2
            ImageFormat.Tif,
            ImageFormat.Tiff -> Tiff(context)

            ImageFormat.Qoi -> Qoi

            ImageFormat.Ico -> Ico(imageScaler)
        }

    }

    data object Jpg : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = ByteArrayOutputStream().apply {
            use { out ->
                image.copy(
                    Bitmap.Config.ARGB_8888, false
                ).compress(
                    Bitmap.CompressFormat.JPEG,
                    quality.qualityValue,
                    out
                )
            }
        }.toByteArray()

    }

    data object WebpLossless : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = ByteArrayOutputStream().apply {
            use { out ->
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSLESS,
                        quality.qualityValue,
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.qualityValue,
                    out
                )
            }
        }.toByteArray()

    }

    data object WebpLossy : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = ByteArrayOutputStream().apply {
            use { out ->
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSY,
                        quality.qualityValue,
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.qualityValue,
                    out
                )
            }
        }.toByteArray()

    }

    data object PngLossless : SimpleCompressor() {
        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = ByteArrayOutputStream().apply {
            use { out ->
                image.compress(
                    Bitmap.CompressFormat.PNG,
                    quality.qualityValue,
                    out
                )
            }
        }.toByteArray()

    }

    data object PngLossy : SimpleCompressor() {
        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val pngLossyQuality = quality as? Quality.PngLossy ?: Quality.PngLossy()
            return Aire.toPNG(
                bitmap = image,
                maxColors = pngLossyQuality.maxColors,
                quantize = AireQuantize.XIAOLING_WU,
                dithering = AirePaletteDithering.JARVIS_JUDICE_NINKE,
                colorMapper = AireColorMapper.COVER_TREE,
                compressionLevel = pngLossyQuality.compressionLevel.coerceIn(0..9)
            )
        }

    }

    data class HeicLossless(
        private val context: Context
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = HeifCoder(context).encodeHeic(
            bitmap = image,
            quality = quality.qualityValue,
            preciseMode = PreciseMode.LOSSLESS
        )

    }

    data class HeicLossy(
        private val context: Context
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = HeifCoder(context).encodeHeic(
            bitmap = image,
            quality = quality.qualityValue,
            preciseMode = PreciseMode.LOSSY
        )

    }

    data class AvifLossless(
        private val context: Context
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val heifQuality = quality as? Quality.Heif ?: Quality.Heif(quality.qualityValue)

            return HeifCoder(context).encodeAvif(
                bitmap = image,
                quality = heifQuality.qualityValue,
                preciseMode = PreciseMode.LOSSLESS,
                speed = AvifSpeed.entries.firstOrNull {
                    it.ordinal == 9 - heifQuality.effort
                } ?: AvifSpeed.SIX
            )
        }

    }

    data class AvifLossy(
        private val context: Context
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val heifQuality = quality as? Quality.Heif ?: Quality.Heif(quality.qualityValue)

            return HeifCoder(context).encodeAvif(
                bitmap = image,
                quality = heifQuality.qualityValue,
                preciseMode = PreciseMode.LOSSY,
                speed = AvifSpeed.entries.firstOrNull {
                    it.ordinal == 9 - heifQuality.effort
                } ?: AvifSpeed.SIX
            )
        }

    }

    data object MozJpeg : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = Aire.mozjpeg(
            bitmap = image,
            quality = quality.qualityValue
        )

    }

    data object Jpegli : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = ByteArrayOutputStream().use {
            JpegliCoder.compress(
                bitmap = image,
                quality = quality.qualityValue,
                background = Scalar.ZERO,
                progressive = true,
                strategy = IccStrategy.DEFAULT,
                outputStream = it
            )

            it.toByteArray()
        }

    }

    data object JxlLossy : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val jxlQuality = quality as? Quality.Jxl ?: Quality.Jxl(quality.qualityValue)
            return JxlCoder.encode(
                bitmap = if (jxlQuality.channels is Quality.Channels.Monochrome) {
                    Aire.grayscale(image)
                } else image,
                channelsConfiguration = when (jxlQuality.channels) {
                    Quality.Channels.RGBA -> JxlChannelsConfiguration.RGBA
                    Quality.Channels.RGB -> JxlChannelsConfiguration.RGB
                    Quality.Channels.Monochrome -> JxlChannelsConfiguration.MONOCHROME
                },
                compressionOption = JxlCompressionOption.LOSSY,
                quality = jxlQuality.qualityValue,
                effort = JxlEffort.entries.first { it.ordinal == jxlQuality.effort - 1 },
                decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == jxlQuality.speed }
            )
        }

    }

    data object JxlLossless : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val jxlQuality = quality as? Quality.Jxl ?: Quality.Jxl(quality.qualityValue)
            return JxlCoder.encode(
                bitmap = if (jxlQuality.channels is Quality.Channels.Monochrome) {
                    Aire.grayscale(image)
                } else image,
                channelsConfiguration = when (jxlQuality.channels) {
                    Quality.Channels.RGBA -> JxlChannelsConfiguration.RGBA
                    Quality.Channels.RGB -> JxlChannelsConfiguration.RGB
                    Quality.Channels.Monochrome -> JxlChannelsConfiguration.MONOCHROME
                },
                compressionOption = JxlCompressionOption.LOSSLESS,
                quality = 100,
                effort = JxlEffort.entries.first { it.ordinal == jxlQuality.effort - 1 },
                decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == jxlQuality.speed }
            )
        }

    }

    data object Jp2 : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = JP2Encoder(image)
            .setOutputFormat(JP2Encoder.FORMAT_JP2)
            .setVisualQuality(quality.qualityValue.toFloat())
            .encode()

    }

    data object J2k : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = JP2Encoder(image)
            .setOutputFormat(JP2Encoder.FORMAT_J2K)
            .setVisualQuality(quality.qualityValue.toFloat())
            .encode()

    }

    data class Tiff(
        private val context: Context
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            val tiffQuality = quality as? Quality.Tiff ?: Quality.Tiff(quality.qualityValue)

            val file = File(context.cacheDir, "temp")
            val options = SaveOptions().apply {
                compressionScheme = CompressionScheme.entries[tiffQuality.compressionScheme]
                orientation = Orientation.TOP_LEFT
            }
            TiffSaver.saveBitmap(file, image, options)

            return file.readBytes()
        }
    }

    data object Qoi : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = QOIEncoder(image).encode()

    }

    data class Ico(
        private val imageScaler: ImageScaler<Bitmap>
    ) : SimpleCompressor() {

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray = coroutineScope {
            val bitmap = if (image.width > 256 || image.height > 256) {
                imageScaler.scaleImage(
                    image = image,
                    width = 256,
                    height = 256,
                    resizeType = ResizeType.Flexible
                )
            } else image

            val width = bitmap.width
            val height = bitmap.height

            val outputStream = ByteArrayOutputStream()
            val header = ByteArray(6)
            val entry = ByteArray(16)
            val infoHeader = ByteArray(40)

            // ICO Header
            header[2] = 1 // Image type: Icon
            header[4] = 1 // Number of images

            outputStream.write(header)

            // Image entry
            entry[0] = if (width > 256) 0 else width.toByte()
            entry[1] = if (height > 256) 0 else height.toByte()
            entry[4] = 1 // Color planes
            entry[6] = 32 // Bits per pixel

            val andMaskSize = ((width + 31) / 32) * 4 * height
            val xorMaskSize = width * height * 4
            val imageDataSize = infoHeader.size + xorMaskSize + andMaskSize

            ByteBuffer.wrap(entry, 8, 4).order(ByteOrder.LITTLE_ENDIAN).putInt(imageDataSize)
            ByteBuffer.wrap(entry, 12, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(header.size + entry.size)

            outputStream.write(entry)

            // BITMAP INFO HEADER
            ByteBuffer.wrap(infoHeader).order(ByteOrder.LITTLE_ENDIAN).apply {
                putInt(40) // Header size
                putInt(width) // Width
                putInt(height * 2) // Height (XOR + AND masks)
                putShort(1) // Color planes
                putShort(32) // Bits per pixel
                putInt(0) // Compression (BI_RGB)
                putInt(xorMaskSize + andMaskSize) // Image size
            }

            outputStream.write(infoHeader)

            // XOR mask (pixel data)
            for (y in height - 1 downTo 0) {
                for (x in 0 until width) {
                    val pixel = bitmap.getPixel(x, y)
                    outputStream.write(pixel and 0xFF) // B
                    outputStream.write((pixel shr 8) and 0xFF) // G
                    outputStream.write((pixel shr 16) and 0xFF) // R
                    outputStream.write((pixel shr 24) and 0xFF) // A
                }
            }

            // AND mask (all 0 for no transparency mask)
            outputStream.write(ByteArray(andMaskSize))

            outputStream.toByteArray()
        }

    }

    data object Bmp : SimpleCompressor() {

        private const val BMP_WIDTH_OF_TIMES = 4
        private const val BYTE_PER_PIXEL = 3

        private fun writeInt(value: Int): ByteArray {
            val b = ByteArray(4)
            b[0] = (value and 0x000000FF).toByte()
            b[1] = (value and 0x0000FF00 shr 8).toByte()
            b[2] = (value and 0x00FF0000 shr 16).toByte()
            b[3] = (value and -0x1000000 shr 24).toByte()
            return b
        }

        private fun writeShort(value: Short): ByteArray {
            val b = ByteArray(2)
            b[0] = (value.toInt() and 0x00FF).toByte()
            b[1] = (value.toInt() and 0xFF00 shr 8).toByte()
            return b
        }

        override suspend fun compress(
            image: Bitmap,
            quality: Quality
        ): ByteArray {
            //image size
            val width = image.width
            val height = image.height

            //image dummy data size
            //reason : the amount of bytes per image row must be a multiple of 4 (requirements of bmp format)
            var dummyBytesPerRow: ByteArray? = null
            var hasDummy = false
            val rowWidthInBytes =
                BYTE_PER_PIXEL * width //source image width * number of bytes to encode one pixel.
            if (rowWidthInBytes % BMP_WIDTH_OF_TIMES > 0) {
                hasDummy = true
                //the number of dummy bytes we need to add on each row
                dummyBytesPerRow =
                    ByteArray(BMP_WIDTH_OF_TIMES - rowWidthInBytes % BMP_WIDTH_OF_TIMES)
                //just fill an array with the dummy bytes we need to append at the end of each row
                for (i in dummyBytesPerRow.indices) {
                    dummyBytesPerRow[i] = 0xFF.toByte()
                }
            }

            //an array to receive the pixels from the source image
            val pixels = IntArray(width * height)

            //the number of bytes used in the file to store raw image data (excluding file headers)
            val imageSize =
                (rowWidthInBytes + if (hasDummy) dummyBytesPerRow!!.size else 0) * height
            //file headers size
            val imageDataOffset = 0x36

            //final size of the file
            val fileSize = imageSize + imageDataOffset

            //Android Bitmap Image Data
            image.getPixels(pixels, 0, width, 0, 0, width, height)

            //ByteArrayOutputStream baos = new ByteArrayOutputStream(fileSize);
            val buffer = ByteBuffer.allocate(fileSize)
            /**
             * BITMAP FILE HEADER Write Start
             */
            buffer.put(0x42.toByte())
            buffer.put(0x4D.toByte())

            //size
            buffer.put(writeInt(fileSize))

            //reserved
            buffer.put(writeShort(0.toShort()))
            buffer.put(writeShort(0.toShort()))

            //image data start offset
            buffer.put(writeInt(imageDataOffset))
            /** BITMAP FILE HEADER Write End  */

            //*******************************************
            /** BITMAP INFO HEADER Write Start  */
            //size
            buffer.put(writeInt(0x28))

            //width, height
            //if we add 3 dummy bytes per row : it means we add a pixel (and the image width is modified.
            buffer.put(writeInt(width + if (hasDummy) (if (dummyBytesPerRow!!.size == 3) 1 else 0) else 0))
            buffer.put(writeInt(height))

            //planes
            buffer.put(writeShort(1.toShort()))

            //bit count
            buffer.put(writeShort(24.toShort()))

            //bit compression
            buffer.put(writeInt(0))

            //image data size
            buffer.put(writeInt(imageSize))

            //horizontal resolution in pixels per meter
            buffer.put(writeInt(0))

            //vertical resolution in pixels per meter (unreliable)
            buffer.put(writeInt(0))
            buffer.put(writeInt(0))
            buffer.put(writeInt(0))
            /** BITMAP INFO HEADER Write End  */
            var row = height
            var startPosition = (row - 1) * width
            var endPosition = row * width
            while (row > 0) {
                for (i in startPosition until endPosition) {
                    buffer.put((pixels[i] and 0x000000FF).toByte())
                    buffer.put((pixels[i] and 0x0000FF00 shr 8).toByte())
                    buffer.put((pixels[i] and 0x00FF0000 shr 16).toByte())
                }
                if (hasDummy) {
                    if (dummyBytesPerRow != null) {
                        buffer.put(dummyBytesPerRow)
                    }
                }
                row--
                endPosition = startPosition
                startPosition -= width
            }
            return buffer.array()
        }

    }

}