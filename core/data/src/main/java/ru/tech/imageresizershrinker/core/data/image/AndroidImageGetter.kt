package ru.tech.imageresizershrinker.core.data.image

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import java.util.Locale
import javax.inject.Inject

class AndroidImageGetter @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context
) : ImageGetter<Bitmap, ExifInterface> {

    override suspend fun getImage(
        uri: String,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
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
            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
            fd?.close()
            ImageData(
                image = bitmap,
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat[getExtension(uri)]
                ),
                metadata = exif
            )
        }
    }

    override suspend fun getImage(data: Any, originalSize: Boolean): Bitmap? {
        return runCatching {
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

    override suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean
    ): ImageData<Bitmap, ExifInterface>? = withContext(Dispatchers.IO) {
        val request = ImageRequest
            .Builder(context)
            .data(uri)
            .transformations(
                transformations.map(::toCoil)
            )
            .apply {
                if (originalSize) size(Size.ORIGINAL)
            }
            .build()
        return@withContext runCatching {
            imageLoader.execute(request).drawable?.toBitmap()?.let { bitmap ->
                val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
                val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
                fd?.close()
                ImageData(
                    image = bitmap,
                    imageInfo = ImageInfo(
                        width = bitmap.width,
                        height = bitmap.height,
                        imageFormat = ImageFormat[getExtension(uri)]
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
        val bmp = kotlin.runCatching {
            imageLoader.enqueue(
                ImageRequest
                    .Builder(context)
                    .data(uri)
                    .apply {
                        if (originalSize) size(Size.ORIGINAL)
                    }
                    .target { drawable ->
                        drawable.toBitmap().let { bitmap ->
                            val fd = context.contentResolver.openFileDescriptor(uri.toUri(), "r")
                            val exif = fd?.fileDescriptor?.let { ExifInterface(it) }
                            fd?.close()
                            ImageData(
                                image = bitmap,
                                imageInfo = ImageInfo(
                                    width = bitmap.width,
                                    height = bitmap.height,
                                    imageFormat = ImageFormat[getExtension(uri)]
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

    private fun toCoil(transformation: Transformation<Bitmap>): coil.transform.Transformation {
        return object : coil.transform.Transformation {
            override val cacheKey: String
                get() = transformation.cacheKey

            override suspend fun transform(
                input: Bitmap,
                size: Size
            ): Bitmap = transformation.transform(input, size)
        }
    }

}