package ru.tech.imageresizershrinker.domain.saving

import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget

interface FileController {
    val savingPath: String

    suspend fun save(
        saveTarget: SaveTarget,
        keepMetadata: Boolean
    ): SaveResult

    fun getSize(uri: String): Long?

    fun constructImageFilename(saveTarget: ImageSaveTarget<*>): String

    fun clearCache(onComplete: (String) -> Unit = {})

    fun getReadableCacheSize(): String
}