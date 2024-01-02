package ru.tech.imageresizershrinker.coredomain.saving.model

import ru.tech.imageresizershrinker.coredomain.model.ImageFormat
import ru.tech.imageresizershrinker.coredomain.model.ImageInfo
import ru.tech.imageresizershrinker.coredomain.saving.SaveTarget

data class ImageSaveTarget<M>(
    val imageInfo: ImageInfo,
    override val originalUri: String,
    val sequenceNumber: Int?,
    val metadata: M? = null,
    override val filename: String? = null,
    override val imageFormat: ImageFormat = imageInfo.imageFormat,
    override val data: ByteArray
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageSaveTarget<*>

        if (imageInfo != other.imageInfo) return false
        if (originalUri != other.originalUri) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (filename != other.filename) return false
        if (imageFormat != other.imageFormat) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = imageInfo.hashCode()
        result = 31 * result + originalUri.hashCode()
        result = 31 * result + (sequenceNumber ?: 0)
        result = 31 * result + (filename?.hashCode() ?: 0)
        result = 31 * result + imageFormat.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}