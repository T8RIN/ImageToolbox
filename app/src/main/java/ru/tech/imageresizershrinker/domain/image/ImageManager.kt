package ru.tech.imageresizershrinker.domain.image

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType

interface ImageManager<T, M> {

    suspend fun transform(
        image: T,
        transformations: List<Transformation<T>>,
        originalSize: Boolean = true
    ): T?

    suspend fun getImage(uri: String, originalSize: Boolean = true): T?

    fun getMimeTypeString(uri: String): String?

    fun rotate(image: T, degrees: Float): T

    fun flip(image: T, isFlipped: Boolean): T

    suspend fun resize(image: T, width: Int, height: Int, resizeType: ResizeType): T

    suspend fun createPreview(
        image: T,
        imageInfo: ImageInfo,
        onGetByteCount: (Int) -> Unit
    ): T

    fun getImageAsync(
        uri: String,
        originalSize: Boolean = true,
        onGetImage: (T) -> Unit,
        onGetMetadata: (M?) -> Unit,
        onGetMimeType: (MimeType) -> Unit,
        onError: (Throwable) -> Unit
    )

    suspend fun getImageWithMetadata(uri: String): Pair<T?, M?>

    suspend fun getImageWithMime(uri: String): Pair<T?, MimeType>

    suspend fun compress(image: T, imageInfo: ImageInfo): ByteArray

    suspend fun calculateImageSize(image: T, imageInfo: ImageInfo): Long

    suspend fun scaleUntilCanShow(image: T?): T?

    fun applyPresetBy(bitmap: Bitmap?, preset: Int, currentInfo: ImageInfo): ImageInfo

    fun canShow(image: T): Boolean

    suspend fun getSampledImage(uri: String, reqWidth: Int, reqHeight: Int): T?

    suspend fun scaleByMaxBytes(image: T, mimeType: MimeType, maxBytes: Long): Pair<T, Int>?

    suspend fun getImageWithTransformations(
        uri: String, transformations: List<Transformation<T>>,
        originalSize: Boolean = true
    ): T?

    fun overlayImage(image: T, overlay: T): T

    suspend fun shareImage(
        image: T,
        imageInfo: ImageInfo,
        onComplete: () -> Unit,
        name: String = "shared_image",
    )

    suspend fun cacheImage(
        image: T,
        imageInfo: ImageInfo,
        name: String = "shared_image"
    ): String?

    suspend fun shareImages(
        uris: List<String>,
        imageLoader: suspend (String) -> Pair<T, ImageInfo>?,
        onProgressChange: (Int) -> Unit
    )

    suspend fun shareFile(
        byteArray: ByteArray,
        filename: String,
        onComplete: () -> Unit
    )

}