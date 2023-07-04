package ru.tech.imageresizershrinker.domain.image

import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.stream.OutStream

interface ImageManager<T, M> {

    fun transform(image: T, transformations: List<Transformation<T>>): T

    fun getImage(uri: String, originalSize: Boolean = true): T

    fun rotate(image: T, degrees: T)

    fun flip(image: T)

    fun resize(width: Int, height: Int, resizeType: ResizeType)

    fun getImage(
        uri: String,
        originalSize: Boolean = true,
        onGetBitmap: (T) -> Unit,
        onGetMetadata: (M?) -> Unit,
        onGetMimeType: (MimeType) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun getImage(uri: String): Pair<T?, M?>

    fun getImageWithMime(uri: String): Pair<T?, MimeType>

    fun compress(image: T, mimeType: MimeType, quality: Number, out: OutStream)

}