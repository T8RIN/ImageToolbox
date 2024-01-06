package ru.tech.imageresizershrinker.core.domain.image

import kotlinx.coroutines.Job
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import ru.tech.imageresizershrinker.core.domain.model.CombiningParams
import ru.tech.imageresizershrinker.core.domain.model.ImageData
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.ImageWithSize
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.domain.model.ResizeType

interface ImageManager<I, M> {

    fun getFilterProvider(): FilterProvider<I>

    suspend fun transform(
        image: I,
        transformations: List<Transformation<I>>,
        originalSize: Boolean = true
    ): I?

    suspend fun filter(
        image: I,
        filters: List<Filter<I, *>>,
        originalSize: Boolean = true
    ): I?

    suspend fun transform(
        image: I,
        transformations: List<Transformation<I>>,
        size: IntegerSize
    ): I?

    suspend fun filter(
        image: I,
        filters: List<Filter<I, *>>,
        size: IntegerSize
    ): I?

    suspend fun getImage(
        uri: String,
        originalSize: Boolean = true
    ): ImageData<I, M>?

    fun rotate(image: I, degrees: Float): I

    fun flip(image: I, isFlipped: Boolean): I

    suspend fun resize(
        image: I,
        width: Int,
        height: Int,
        resizeType: ResizeType,
        imageScaleMode: ImageScaleMode
    ): I?

    suspend fun createPreview(
        image: I,
        imageInfo: ImageInfo,
        transformations: List<Transformation<I>> = emptyList(),
        onGetByteCount: (Int) -> Unit
    ): I

    suspend fun createFilteredPreview(
        image: I,
        imageInfo: ImageInfo,
        filters: List<Filter<I, *>> = emptyList(),
        onGetByteCount: (Int) -> Unit
    ): I

    fun getImageAsync(
        uri: String,
        originalSize: Boolean = true,
        onGetImage: (ImageData<I, M>) -> Unit,
        onError: (Throwable) -> Unit
    )

    suspend fun compress(
        imageData: ImageData<I, M>,
        onImageReadyToCompressInterceptor: suspend (I) -> I = { it },
        applyImageTransformations: Boolean = true
    ): ByteArray

    suspend fun calculateImageSize(imageData: ImageData<I, M>): Long

    suspend fun scaleUntilCanShow(image: I?): I?

    fun applyPresetBy(
        image: I?,
        preset: Preset,
        currentInfo: ImageInfo
    ): ImageInfo

    fun canShow(image: I): Boolean

    suspend fun getSampledImage(
        uri: String,
        reqWidth: Int,
        reqHeight: Int
    ): ImageData<I, M>?

    suspend fun scaleByMaxBytes(
        image: I,
        imageFormat: ImageFormat,
        imageScaleMode: ImageScaleMode,
        maxBytes: Long
    ): ImageData<I, M>?

    suspend fun getImageWithTransformations(
        uri: String,
        transformations: List<Transformation<I>>,
        originalSize: Boolean = true
    ): ImageData<I, M>?

    suspend fun getImageWithFiltersApplied(
        uri: String,
        filters: List<Filter<I, *>>,
        originalSize: Boolean = true
    ): ImageData<I, M>?

    suspend fun shareImage(
        imageData: ImageData<I, M>,
        onComplete: () -> Unit,
        name: String = "shared_image",
    )

    suspend fun cacheImage(
        image: I,
        imageInfo: ImageInfo,
        name: String = "shared_image"
    ): String?

    suspend fun shareImages(
        uris: List<String>,
        imageLoader: suspend (String) -> ImageData<I, M>?,
        onProgressChange: (Int) -> Unit
    )

    suspend fun shareFile(
        byteArray: ByteArray,
        filename: String,
        onComplete: () -> Unit
    )

    suspend fun getImage(data: Any, originalSize: Boolean = true): I?

    fun removeBackgroundFromImage(
        image: I,
        onSuccess: (I) -> Unit,
        onFailure: (Throwable) -> Unit,
        trimEmptyParts: Boolean = false
    )

    suspend fun trimEmptyParts(image: I): I

    suspend fun combineImages(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageScale: Float
    ): ImageData<I, M>

    suspend fun calculateCombinedImageDimensions(
        imageUris: List<String>,
        combiningParams: CombiningParams
    ): IntegerSize

    suspend fun createCombinedImagesPreview(
        imageUris: List<String>,
        combiningParams: CombiningParams,
        imageFormat: ImageFormat,
        quality: Float,
        onGetByteCount: (Int) -> Unit
    ): ImageWithSize<I>

    suspend fun convertImagesToPdf(
        imageUris: List<String>,
        onProgressChange: suspend (Int) -> Unit,
        scaleSmallImagesToLarge: Boolean
    ): ByteArray

    fun convertPdfToImages(
        pdfUri: String,
        pages: List<Int>?,
        preset: Preset.Numeric,
        onGetPagesCount: suspend (Int) -> Unit,
        onProgressChange: suspend (Int, String) -> Unit,
        onComplete: suspend () -> Unit = {}
    ): Job

    suspend fun shareUri(uri: String, type: String?)

    suspend fun shareImageUris(uris: List<String>)

    suspend fun getPdfPages(uri: String): List<Int>

    suspend fun getPdfPageSizes(uri: String): List<IntegerSize>

}