@file:Suppress("LocalVariableName", "DEPRECATION")

package ru.tech.imageresizershrinker.core.data.image

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.JxlColorSpace
import com.awxkee.jxlcoder.JxlCompressionOption
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor<Bitmap> {

    override suspend fun compress(
        image: Bitmap,
        imageFormat: ImageFormat,
        quality: Float
    ): ByteArray = withContext(Dispatchers.IO) {
        val heifCoder = HeifCoder(context)
        val jxlCoder = JxlCoder()

        return@withContext when (imageFormat) {
            ImageFormat.Bmp -> BMPCompressor.compress(image)
            ImageFormat.Jpeg, ImageFormat.Jpg -> {
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