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
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.image.utils.SimpleCompressor
import ru.tech.imageresizershrinker.core.data.utils.fileSize
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.sizeTo
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.SettingsRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    settingsRepository: SettingsRepository
) : ImageCompressor<Bitmap> {

    private var overwriteFiles: Boolean = false

    init {
        settingsRepository
            .getSettingsStateFlow()
            .onEach {
                overwriteFiles = it.overwriteFiles
            }
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    override suspend fun compress(
        image: Bitmap,
        imageFormat: ImageFormat,
        quality: Quality
    ): ByteArray = SimpleCompressor
        .getInstance(
            imageFormat = imageFormat,
            context = context
        )
        .compress(
            image = image,
            quality = quality.coerceIn(imageFormat)
        )


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
            currentImage = imageScaler
                .scaleImage(
                    image = imageTransformer.rotate(
                        image = image.apply { setHasAlpha(true) },
                        degrees = imageInfo.rotationDegrees
                    ),
                    width = imageInfo.width,
                    height = imageInfo.height,
                    resizeType = imageInfo.resizeType.withOriginalSizeIfCrop(size),
                    imageScaleMode = imageInfo.imageScaleMode
                )
                .let {
                    imageTransformer.flip(
                        image = it,
                        isFlipped = imageInfo.isFlipped
                    )
                }
                .let {
                    onImageReadyToCompressInterceptor(it)
                }
        } else currentImage = onImageReadyToCompressInterceptor(image)

        val extension = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(
                imageInfo.originalUri?.let {
                    imageGetter.getExtension(it)
                }
            )

        val imageFormat = if (overwriteFiles && extension != null) {
            ImageFormat[extension]
        } else imageInfo.imageFormat

        return@withContext compress(
            image = currentImage,
            imageFormat = imageFormat,
            quality = imageInfo.quality
        )
    }

    override suspend fun calculateImageSize(
        image: Bitmap,
        imageInfo: ImageInfo
    ): Long = compressAndTransform(
        image = image,
        imageInfo = if (image.width * image.height > 512 * 512) {
            imageInfo.copy(
                width = 512,
                height = 512
            )
        } else imageInfo
    ).let {
        cacheByteArray(
            byteArray = it,
            filename = "temp.${imageInfo.imageFormat.extension}"
        )?.toUri()
            ?.fileSize(context)
            ?.let { sampledSize ->
                if (image.width * image.height > 512 * 512) {
                    val originalSize = imageInfo.width * imageInfo.height
                    val compressedSize = 512 * 512
                    val compressionRatio = originalSize / compressedSize.toFloat()

                    (sampledSize * compressionRatio).toLong()
                } else sampledSize.toLong()
            } ?: it.size.toLong()
    }

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
        }.getOrNull()
            ?.toString()
    }

}