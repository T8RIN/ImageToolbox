package ru.tech.imageresizershrinker.domain.saving.model

import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.saving.SaveTarget

data class FileSaveTarget(
    override val originalUri: String,
    override val filename: String,
    override val mimeType: MimeType,
    override val data: ByteArray,
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileSaveTarget

        if (originalUri != other.originalUri) return false
        if (filename != other.filename) return false
        if (mimeType != other.mimeType) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originalUri.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}