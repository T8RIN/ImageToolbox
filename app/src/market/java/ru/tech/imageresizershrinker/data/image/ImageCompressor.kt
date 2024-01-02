@file:Suppress("LocalVariableName", "DEPRECATION")

package ru.tech.imageresizershrinker.data.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import ru.tech.imageresizershrinker.coredomain.model.ImageFormat
import java.io.ByteArrayOutputStream

internal object ImageCompressor {

    @SuppressLint("StaticFieldLeak")
    private val heifCoder = HeifCoder()

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
        }
    }
}