@file:Suppress("LocalVariableName", "DEPRECATION")

package ru.tech.imageresizershrinker.data.image

import android.graphics.Bitmap
import android.os.Build
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.JxlColorSpace
import com.awxkee.jxlcoder.JxlCompressionOption
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import java.io.ByteArrayOutputStream

internal object ImageCompressor {

    private val heifCoder = HeifCoder()

    private val jxlCoder = JxlCoder()

    fun compress(image: Bitmap, imageFormat: ImageFormat, quality: Float): ByteArray {
        return when (imageFormat) {
            ImageFormat.Bmp -> BMPCompressor.compress(image)
            ImageFormat.Jpeg -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality.toInt().coerceIn(imageFormat.compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Jpg -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality.toInt().coerceIn(imageFormat.compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Png -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.PNG,
                    quality.toInt().coerceIn(imageFormat.compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossless -> {
                val out = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSLESS,
                        quality.toInt().coerceIn(imageFormat.compressionRange),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.toInt().coerceIn(imageFormat.compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossy -> {
                val out = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSY,
                        quality.toInt().coerceIn(imageFormat.compressionRange),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.toInt().coerceIn(imageFormat.compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Avif -> {
                heifCoder.encodeAvif(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(imageFormat.compressionRange)
                )
            }

            ImageFormat.Heic -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(imageFormat.compressionRange)
                )
            }

            ImageFormat.Heif -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(imageFormat.compressionRange)
                )
            }

            ImageFormat.Jxl.Lossless -> {
                jxlCoder.encode(
                    bitmap = image,
                    colorSpace = JxlColorSpace.RGBA,
                    compressionOption = JxlCompressionOption.LOSSLESS,
                    effort = quality.toInt().coerceIn(imageFormat.compressionRange)
                )
            }

            ImageFormat.Jxl.Lossy -> {
                jxlCoder.encode(
                    bitmap = image,
                    colorSpace = JxlColorSpace.RGBA,
                    compressionOption = JxlCompressionOption.LOSSY,
                    effort = quality.toInt().coerceIn(imageFormat.compressionRange)
                )
            }
        }
    }
}