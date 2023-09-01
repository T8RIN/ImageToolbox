@file:Suppress("LocalVariableName", "DEPRECATION")

package ru.tech.imageresizershrinker.data.image

import android.graphics.Bitmap
import android.os.Build
import com.awxkee.jxlcoder.JxlCoder
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
                    quality.toInt().coerceIn(0, 100),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Jpg -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality.toInt().coerceIn(0, 100),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Png -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.PNG,
                    quality.toInt().coerceIn(0, 100),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossless -> {
                val out = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSLESS,
                        quality.toInt().coerceIn(0, 100),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.toInt().coerceIn(0, 100),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossy -> {
                val out = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSY,
                        quality.toInt().coerceIn(0, 100),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.toInt().coerceIn(0, 100),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Avif -> {
                heifCoder.encodeAvif(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(0, 100)
                )
            }

            ImageFormat.Heic -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(0, 100)
                )
            }

            ImageFormat.Heif -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.toInt().coerceIn(0, 100)
                )
            }

            ImageFormat.Jxl.Lossless -> {
//                jxlCoder.encode(
//                    bitmap = image,
//                    compressionOption = if (quality.toInt().coerceIn(0, 100) == 100) {
//                        JxlCompressionOption.LOSSLESS
//                    } else JxlCompressionOption.LOSSY,
//                    lossyLevel = (15f - (0f..100f).convert(quality.coerceIn(0f, 100f), 0f..15f))
//                )
                jxlCoder.encode(
                    bitmap = image,
                    compressionOption = JxlCompressionOption.LOSSLESS,
                    lossyLevel = (15f - (0f..100f).convert(quality.coerceIn(0f, 100f), 0f..15f))
                )
            }

            ImageFormat.Jxl.Lossy -> {
                jxlCoder.encode(
                    bitmap = image,
                    compressionOption = JxlCompressionOption.LOSSY,
                    lossyLevel = (15f - (0f..100f).convert(quality.coerceIn(0f, 100f), 0f..15f))
                )
            }
        }
    }
}

private fun ClosedFloatingPointRange<Float>.convert(
    number: Float,
    target: ClosedFloatingPointRange<Float>
): Float {
    val ratio = number / (endInclusive - start)
    return (ratio * (target.endInclusive - target.start))
}