package ru.tech.imageresizershrinker.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.domain.model.SettingsState

interface SettingsRepository {

    suspend fun getSettingsState(): SettingsState

    fun getSettingsStateFlow(): Flow<SettingsState>

    suspend fun toggleAddSequenceNumber()

    suspend fun toggleAddOriginalFilename()

    suspend fun updateEmojisCount(count: Int)

    suspend fun updateImagePickerMode(mode: Int)

    suspend fun toggleAddFileSize()

    suspend fun updateEmoji(emoji: Int)

    suspend fun updateFilename(name: String)

    suspend fun toggleShowDialog()

    suspend fun updateColorTuple(colorTuple: String)

    suspend fun updatePresets(newPresets: String)

    suspend fun toggleDynamicColors()

    suspend fun setBorderWidth(width: Float)

    suspend fun toggleAllowImageMonet()

    suspend fun toggleAmoledMode()

    suspend fun setNightMode(mode: Int)

    suspend fun updateSaveFolderUri(uri: String?)

    suspend fun updateColorTuples(colorTuples: String)

    suspend fun setAlignment(align: Int)

    suspend fun updateOrder(data: String)

    suspend fun toggleClearCacheOnLaunch()

    suspend fun toggleGroupOptionsByTypes()

    suspend fun toggleRandomizeFilename()

    suspend fun createBackupFile(): ByteArray

    suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )
}