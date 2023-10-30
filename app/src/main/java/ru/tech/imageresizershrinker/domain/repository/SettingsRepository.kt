package ru.tech.imageresizershrinker.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.domain.model.NightMode
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

    suspend fun setNightMode(nightMode: NightMode)

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

    suspend fun resetSettings()

    fun createBackupFilename(): String

    suspend fun setFont(font: FontFam)

    suspend fun setFontScale(scale: Float)

    suspend fun toggleAllowCrashlytics()

    suspend fun toggleAllowAnalytics()

    suspend fun toggleAllowBetas()

    suspend fun toggleAllowShowingShadowsInsteadOfBorders()

    suspend fun registerAppOpen()

    suspend fun toggleLockDrawOrientation()

    suspend fun setThemeStyle(value: Int)

    suspend fun setThemeContrast(value: Double)

    suspend fun toggleInvertColors()

    suspend fun toggleScreensSearchEnabled()
}