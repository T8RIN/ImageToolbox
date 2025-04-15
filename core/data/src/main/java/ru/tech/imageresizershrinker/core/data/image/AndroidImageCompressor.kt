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
import androidx.core.net.toUri
import com.t8rin.trickle.Trickle
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import ru.tech.imageresizershrinker.core.data.image.utils.ImageCompressorBackend
import ru.tech.imageresizershrinker.core.data.utils.fileSize
import ru.tech.imageresizershrinker.core.data.utils.toSoftware
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.image.model.alphaContainedEntries
import ru.tech.imageresizershrinker.core.domain.model.sizeTo
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: Lazy<ShareProvider>,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageCompressor<Bitmap> {

    private var settingsState: SettingsState = SettingsState.Default
    private val overwriteFiles: Boolean get() = settingsState.overwriteFiles

    init {
        settingsProvider
            .getSettingsStateFlow()
            .onEach {
                settingsState = it
            }
            .launchIn(CoroutineScope(defaultDispatcher))
    }

    override suspend fun compress(
        image: Bitmap,
        imageFormat: ImageFormat,
        quality: Quality
    ): ByteArray = withContext(encodingDispatcher) {
        val transformedImage = image.toSoftware().let {
            if (imageFormat !in ImageFormat.alphaContainedEntries) {
                withContext(defaultDispatcher) {
                    Trickle.drawColorBehind(
                        color = settingsState.backgroundForNoAlphaImageFormats.colorInt,
                        input = it
                    )
                }
            } else it
        }

        ImageCompressorBackend.Factory()
            .create(
                imageFormat = imageFormat,
                context = context,
                imageScaler = imageScaler
            )
            .compress(
                image = transformedImage,
                quality = quality.coerceIn(imageFormat)
            )
    }


    override suspend fun compressAndTransform(
        image: Bitmap,
        imageInfo: ImageInfo,
        onImageReadyToCompressInterceptor: suspend (Bitmap) -> Bitmap,
        applyImageTransformations: Boolean
    ): ByteArray = withContext(encodingDispatcher) {
        val currentImage: Bitmap
        if (applyImageTransformations) {
            val size = imageInfo.originalUri?.let {
                imageGetter.getImage(
                    data = it,
                    originalSize = true
                )?.run { width sizeTo height }
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

        val extension = imageInfo.originalUri?.let { imageGetter.getExtension(it) }

        val imageFormat = if (overwriteFiles && extension != null) {
            ImageFormat[extension]
        } else imageInfo.imageFormat

        yield()
        compress(
            image = currentImage,
            imageFormat = imageFormat,
            quality = imageInfo.quality
        )
    }

    override suspend fun calculateImageSize(
        image: Bitmap,
        imageInfo: ImageInfo
    ): Long = withContext(encodingDispatcher) {
        val newInfo = imageInfo.let {
            if (it.width == 0 || it.height == 0) {
                it.copy(
                    width = image.width,
                    height = image.height
                )
            } else it
        }
        compressAndTransform(
            image = image,
            imageInfo = newInfo
        ).let {
            shareProvider.get().cacheByteArray(
                byteArray = it,
                filename = "temp.${newInfo.imageFormat.extension}"
            )?.toUri()
                ?.fileSize(context) ?: it.size.toLong()
        }
    }

}