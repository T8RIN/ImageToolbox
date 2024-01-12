package ru.tech.imageresizershrinker.core.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.domain.model.FontFam
import ru.tech.imageresizershrinker.core.domain.model.NightMode
import ru.tech.imageresizershrinker.core.domain.model.SettingsState

interface SettingsRepository {

    suspend fun getSettingsState(): SettingsState

    fun getSettingsStateFlow(): Flow<SettingsState>

    suspend fun toggleAddSequenceNumber()

    suspend fun toggleAddOriginalFilename()

    suspend fun setEmojisCount(count: Int)

    suspend fun setImagePickerMode(mode: Int)

    suspend fun toggleAddFileSize()

    suspend fun setEmoji(emoji: Int)

    suspend fun setFilenamePrefix(name: String)

    suspend fun toggleShowDialog()

    suspend fun setColorTuple(colorTuple: String)

    suspend fun setPresets(newPresets: String)

    suspend fun toggleDynamicColors()

    suspend fun setBorderWidth(width: Float)

    suspend fun toggleAllowImageMonet()

    suspend fun toggleAmoledMode()

    suspend fun setNightMode(nightMode: NightMode)

    suspend fun setSaveFolderUri(uri: String?)

    suspend fun setColorTuples(colorTuples: String)

    suspend fun setAlignment(align: Int)

    suspend fun setScreenOrder(data: String)

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

    suspend fun toggleDrawContainerShadows()

    suspend fun toggleDrawButtonShadows()

    suspend fun toggleDrawSliderShadows()

    suspend fun toggleDrawSwitchShadows()

    suspend fun toggleDrawFabShadows()

    suspend fun registerAppOpen()

    suspend fun toggleLockDrawOrientation()

    suspend fun setThemeStyle(value: Int)

    suspend fun setThemeContrast(value: Double)

    suspend fun toggleInvertColors()

    suspend fun toggleScreensSearchEnabled()

    suspend fun toggleDrawAppBarShadows()

    suspend fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode)

    suspend fun setVibrationStrength(strength: Int)

    suspend fun toggleOverwriteFiles()

    suspend fun setFilenameSuffix(name: String)

    suspend fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode)

    suspend fun toggleUsePixelSwitch()
}