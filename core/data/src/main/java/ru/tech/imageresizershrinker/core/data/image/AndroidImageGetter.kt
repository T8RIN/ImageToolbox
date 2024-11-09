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

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.toBitmap
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageData
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import java.util.Locale
import javax.inject.Inject

internal class AndroidImageGetter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageGetter<Bitmap, ExifInterface> {

    private var settingsState: SettingsState = SettingsState.Default

    init {
        settingsProvider
            .getSettingsStateFlow()
            .onEach {
                settingsState = it
            }
            .launchIn(CoroutineScope(defaultDispatcher))
    }

    override suspend fun getImage(
        uri: String,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(defaultDispatcher) {
        getImage(
            data = uri,
            originalSize = originalSize
        )?.let { bitmap ->
            val newUri = uri.toUri().tryGetLocation(context)

            val fd = context.contentResolver.openFileDescriptor(newUri, "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            fd?.close()
            ImageData(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat[getExtension(uri)],
                    originalUri = uri,
                    resizeType = settingsState.defaultResizeType
                ),
                metadata = exif
            )
        }
    }

    override suspend fun getImage(
        data: Any,
        originalSize: Boolean
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(data)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()
    }

    override suspend fun getImage(
        data: Any,
        size: IntegerSize?
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(data)
                    .apply {
                        size(
                            size?.let {
                                Size(size.width, size.height)
                            } ?: Size.ORIGINAL
                        )
                    }
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()
    }

    override suspend fun getImage(
        data: Any,
        size: Int?
    ): Bitmap? = withContext(defaultDispatcher) {
        runCatching {
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(data)
                    .apply {
                        size?.let {
                            size(it)
                        } ?: size(Size.ORIGINAL)
                    }
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()
    }

    override suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(defaultDispatcher) {
        val request = ImageRequest
            .Builder(context)
            .data(uri)
            .transformations(
                transformations.map { it.toCoil() }
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()

        runCatching {
            imageLoader.execute(request).drawable?.toBitmap()?.let { bitmap ->
                val newUri = uri.toUri().tryGetLocation(context)
                val fd = context.contentResolver.openFileDescriptor(newUri, "r")
                val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
                fd?.close()
                ImageData(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = ImageFormat[getExtension(uri)],
                        originalUri = uri,
                        resizeType = settingsState.defaultResizeType
                    ),
                    metadata = exif
                )
            }
        }.getOrNull()
    }

    override suspend fun getImageWithTransformations(
        data: Any,
        transformations: List<Transformation<Bitmap>>,
        size: IntegerSize?
    ): Bitmap? = withContext(defaultDispatcher) {
        val request = ImageRequest
            .Builder(context)
            .data(data)
            .transformations(
                transformations.map { it.toCoil() }
            )
            .apply {
                if (size == null) size(Size.ORIGINAL)
                else size(size.width, size.height)
            }
            .build()

        runCatching {
            imageLoader.execute(request).drawable?.toBitmap()
        }.getOrNull()
    }

    override fun getImageAsync(
        uri: String,
        originalSize: Boolean,
        onGetImage: (ImageData<Bitmap, ExifInterface>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val bmp = runCatching {
            imageLoader.enqueue(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .target { drawable ->
                        drawable.toBitmap().let { bitmap ->
                            val newUri = uri.toUri().tryGetLocation(context)
                            val fd = context.contentResolver.openFileDescriptor(newUri, "r")
                            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
                            fd?.close()
                            ImageData(
                                image = bitmap,
                                imageInfo = ImageInfo(
                                    width = bitmap.width,
                                    height = bitmap.height,
                                    imageFormat = ImageFormat[getExtension(uri)],
                                    originalUri = uri,
                                    resizeType = settingsState.defaultResizeType
                                ),
                                metadata = exif
                            )
                        }.let(onGetImage)
                    }.build()
            )
        }
        bmp.exceptionOrNull()?.let(onFailure)
    }

    override fun getExtension(uri: String): String? {
        if (uri.endsWith(".jxl")) return "jxl"
        return if (ContentResolver.SCHEME_CONTENT == uri.toUri().scheme) {
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(
                    context.contentResolver.getType(uri.toUri())
                )
        } else {
            MimeTypeMap.getFileExtensionFromUrl(uri).lowercase(Locale.getDefault())
        }
    }

    private fun Uri.tryGetLocation(context: Context): Uri {
        val tempUri = this
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            runCatching {
                MediaStore.setRequireOriginal(this).also {
                    context.contentResolver.openFileDescriptor(it, "r")?.close()
                }
            }.getOrNull() ?: tempUri
        } else this
    }

}