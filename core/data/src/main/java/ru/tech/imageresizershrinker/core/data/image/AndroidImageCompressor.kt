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



package ru.tech.imageresizershrinker.core.data.image

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import com.awxkee.jxlcoder.JxlCoder
import com.awxkee.jxlcoder.JxlColorSpace
import com.awxkee.jxlcoder.JxlCompressionOption
import com.awxkee.jxlcoder.JxlDecodingSpeed
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.fileSize
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.sizeTo
import ru.tech.imageresizershrinker.core.resources.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>
) : ImageCompressor<Bitmap> {

    override suspend fun compress(
        image: Bitmap,
        imageFormat: ImageFormat,
        quality: Quality
    ): ByteArray = withContext(Dispatchers.IO) {
        val heifCoder = HeifCoder(context)
        val jxlCoder = JxlCoder()

        when (imageFormat) {
            ImageFormat.Bmp -> BMPCompressor.compress(image)
            ImageFormat.Jpeg, ImageFormat.Jpg -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Png -> {
                val out = ByteArrayOutputStream()
                image.compress(
                    Bitmap.CompressFormat.PNG,
                    quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossless -> {
                val out = ByteArrayOutputStream()
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSLESS,
                        quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Webp.Lossy -> {
                val out = ByteArrayOutputStream()
                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    image.compress(
                        Bitmap.CompressFormat.WEBP_LOSSY,
                        quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                        out
                    )
                } else image.compress(
                    Bitmap.CompressFormat.WEBP,
                    quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    out
                )
                out.toByteArray()
            }

            ImageFormat.Avif -> {
                heifCoder.encodeAvif(
                    bitmap = image,
                    quality = quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange)
                )
            }

            ImageFormat.Heic -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                )
            }

            ImageFormat.Heif -> {
                heifCoder.encodeHeic(
                    bitmap = image,
                    quality = quality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                )
            }

            ImageFormat.Jxl.Lossy -> {
                val jxlQuality = quality as? Quality.Jxl ?: Quality.Jxl(quality.qualityValue)
                jxlCoder.encode(
                    bitmap = image,
                    colorSpace = JxlColorSpace.RGBA,
                    compressionOption = JxlCompressionOption.LOSSY,
                    quality = jxlQuality.qualityValue.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    effort = jxlQuality.effort.coerceIn(imageFormat.compressionTypes[1].compressionRange),
                    decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == jxlQuality.speed }
                )
            }

            ImageFormat.Jxl.Lossless -> {
                val jxlQuality = quality as? Quality.Jxl ?: Quality.Jxl(quality.qualityValue)
                jxlCoder.encode(
                    bitmap = image,
                    colorSpace = JxlColorSpace.RGBA,
                    compressionOption = JxlCompressionOption.LOSSLESS,
                    quality = 100,
                    effort = jxlQuality.effort.coerceIn(imageFormat.compressionTypes[0].compressionRange),
                    decodingSpeed = JxlDecodingSpeed.entries.first { it.ordinal == jxlQuality.speed }
                )
            }
        }
    }

    override suspend fun compressAndTransform(
        image: Bitmap,
        imageInfo: ImageInfo,
        onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
        applyImageTransformations: Boolean
    ): ByteArray = withContext(Dispatchers.IO) {
        val currentImage: Bitmap
        if (applyImageTransformations) {
            val size = imageInfo.originalUri?.let {
                imageLoader.execute(
                    ImageRequest.Builder(context)
                        .data(it)
                        .size(Size.ORIGINAL)
                        .build()
                ).drawable?.run { intrinsicWidth sizeTo intrinsicHeight }
            }
            currentImage = imageScaler.scaleImage(
                image = imageTransformer.rotate(
                    image = image.apply { setHasAlpha(true) },
                    degrees = imageInfo.rotationDegrees
                ),
                width = imageInfo.width,
                height = imageInfo.height,
                resizeType = imageInfo.resizeType.withOriginalSizeIfCrop(size),
                imageScaleMode = imageInfo.imageScaleMode
            ).let {
                imageTransformer.flip(
                    image = it,
                    isFlipped = imageInfo.isFlipped
                )
            }.let {
                onImageReadyToCompressInterceptor(it)
            }
        } else currentImage = onImageReadyToCompressInterceptor(image)

        return@withContext compress(
            image = currentImage,
            imageFormat = imageInfo.imageFormat,
            quality = imageInfo.quality
        )
    }

    override suspend fun calculateImageSize(
        image: Bitmap,
        imageInfo: ImageInfo
    ): Long = compressAndTransform(image, imageInfo).let {
        cacheByteArray(
            byteArray = it,
            filename = "temp.${imageInfo.imageFormat.extension}"
        )?.toUri()?.fileSize(context) ?: it.size
    }.toLong()

    private fun cacheByteArray(
        byteArray: ByteArray,
        filename: String
    ): String? {
        val imagesFolder = File(context.cacheDir, "files")
        return runCatching {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, filename)
            FileOutputStream(file).use {
                it.write(byteArray)
            }
            FileProvider.getUriForFile(
                context,
                context.getString(R.string.file_provider),
                file
            )
        }.getOrNull()?.toString()
    }

}