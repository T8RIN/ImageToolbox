package ru.tech.imageresizershrinker.coredomain.saving

import ru.tech.imageresizershrinker.coredomain.saving.model.ImageSaveTarget

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