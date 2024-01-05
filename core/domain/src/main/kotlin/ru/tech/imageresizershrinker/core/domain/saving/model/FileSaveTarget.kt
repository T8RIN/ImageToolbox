package ru.tech.imageresizershrinker.core.domain.saving.model

import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.saving.SaveTarget

data class FileSaveTarget(
    override val originalUri: String,
    override val filename: String,
    override val imageFormat: ImageFormat,
    override val data: ByteArray,
) : SaveTarget {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileSaveTarget

        if (originalUri != other.originalUri) return false
        if (filename != other.filename) return false
        if (imageFormat != other.imageFormat) return false
        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = originalUri.hashCode()
        result = 31 * result + filename.hashCode()
        result = 31 * result + imageFormat.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}