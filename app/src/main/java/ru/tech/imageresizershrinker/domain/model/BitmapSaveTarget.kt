package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.saving.SaveTarget

data class BitmapSaveTarget(
    val bitmapInfo: BitmapInfo,
    override val originalUri: String,
    val sequenceNumber: Int?,
    override val filename: String? = null,
    override val mimeType: MimeType = MimeType.Default(),
    override val data: ByteArray
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BitmapSaveTarget

        if (bitmapInfo != other.bitmapInfo) return false
        if (originalUri != other.originalUri) return false
        if (sequenceNumber != other.sequenceNumber) return false
        if (filename != other.filename) return false
        if (mimeType != other.mimeType) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitmapInfo.hashCode()
        result = 31 * result + originalUri.hashCode()
        result = 31 * result + (sequenceNumber ?: 0)
        result = 31 * result + (filename?.hashCode() ?: 0)
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}