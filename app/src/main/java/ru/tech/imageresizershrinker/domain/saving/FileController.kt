package ru.tech.imageresizershrinker.domain.saving

interface FileController {
    val savingPath: String
    suspend fun save(saveTarget: SaveTarget, keepMetadata: Boolean)
    fun isExternalStorageWritable(): Boolean
    fun requestReadWritePermissions()
    fun getSize(uri: String): Long?
}