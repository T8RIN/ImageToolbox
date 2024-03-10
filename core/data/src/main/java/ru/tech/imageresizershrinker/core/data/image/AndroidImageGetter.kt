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
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.toCoil
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import java.util.Locale
import javax.inject.Inject

internal class AndroidImageGetter @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ImageGetter<Bitmap, ExifInterface> {

    override suspend fun getImage(
        uri: String,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(dispatcher) {
        runCatching {
            imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .build()
            ).drawable?.toBitmap()
        }.getOrNull()?.let { bitmap ->
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
                    originalUri = uri
                ),
                metadata = exif
            )
        }
    }

    override suspend fun getImage(
        data: Any,
        originalSize: Boolean
    ): Bitmap? = withContext(dispatcher) {
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
    ): Bitmap? = withContext(dispatcher) {
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

    override suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(dispatcher) {
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
                        originalUri = uri
                    ),
                    metadata = exif
                )
            }
        }.getOrNull()
    }

    override fun getImageAsync(
        uri: String,
        originalSize: Boolean,
        onGetImage: (ImageData<Bitmap, ExifInterface>) -> Unit,
        onError: (Throwable) -> Unit
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
                                    originalUri = uri
                                ),
                                metadata = exif
                            )
                        }.let(onGetImage)
                    }.build()
            )
        }
        bmp.exceptionOrNull()?.let(onError)
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

    private fun Drawable.toBitmap(): Bitmap {
        val drawable = this
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                getSuitableConfig()
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                getSuitableConfig()
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun getSuitableConfig(
        image: Bitmap? = null
    ): Bitmap.Config = image?.config ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Bitmap.Config.RGBA_1010102
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Bitmap.Config.RGBA_F16
    } else {
        Bitmap.Config.ARGB_8888
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