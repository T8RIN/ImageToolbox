/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.settings.domain

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType

interface SettingsRepository : SettingsProvider, SettingsInteractor {

    override suspend fun getSettingsState(): SettingsState

    override fun getSettingsStateFlow(): Flow<SettingsState>

    override suspend fun toggleAddSequenceNumber()

    override suspend fun toggleAddOriginalFilename()

    override suspend fun setEmojisCount(count: Int)

    override suspend fun setImagePickerMode(mode: Int)

    override suspend fun toggleAddFileSize()

    override suspend fun setEmoji(emoji: Int)

    override suspend fun setFilenamePrefix(name: String)

    override suspend fun toggleShowDialog()

    override suspend fun setColorTuple(colorTuple: String)

    override suspend fun setPresets(newPresets: String)

    override suspend fun toggleDynamicColors()

    override suspend fun setBorderWidth(width: Float)

    override suspend fun toggleAllowImageMonet()

    override suspend fun toggleAmoledMode()

    override suspend fun setNightMode(nightMode: NightMode)

    override suspend fun setSaveFolderUri(uri: String?)

    override suspend fun setColorTuples(colorTuples: String)

    override suspend fun setAlignment(align: Int)

    override suspend fun setScreenOrder(data: String)

    override suspend fun toggleClearCacheOnLaunch()

    override suspend fun toggleGroupOptionsByTypes()

    override suspend fun toggleRandomizeFilename()

    override suspend fun createBackupFile(): ByteArray

    override suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    )

    override suspend fun resetSettings()

    override fun createBackupFilename(): String

    override suspend fun setFont(font: DomainFontFamily)

    override suspend fun setFontScale(scale: Float)

    override suspend fun toggleAllowCrashlytics()

    override suspend fun toggleAllowAnalytics()

    override suspend fun toggleAllowBetas()

    override suspend fun toggleDrawContainerShadows()

    override suspend fun toggleDrawButtonShadows()

    override suspend fun toggleDrawSliderShadows()

    override suspend fun toggleDrawSwitchShadows()

    override suspend fun toggleDrawFabShadows()

    override suspend fun registerAppOpen()

    override suspend fun toggleLockDrawOrientation()

    override suspend fun setThemeStyle(value: Int)

    override suspend fun setThemeContrast(value: Double)

    override suspend fun toggleInvertColors()

    override suspend fun toggleScreensSearchEnabled()

    override suspend fun toggleDrawAppBarShadows()

    override suspend fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode)

    override suspend fun setVibrationStrength(strength: Int)

    override suspend fun toggleOverwriteFiles()

    override suspend fun setFilenameSuffix(name: String)

    override suspend fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode)

    override suspend fun toggleMagnifierEnabled()

    override suspend fun toggleExifWidgetInitialState()

    override suspend fun setInitialOCRLanguageCodes(list: List<String>)

    override suspend fun getInitialOCRLanguageCodes(): List<String>

    override suspend fun setScreensWithBrightnessEnforcement(data: String)

    override suspend fun toggleConfettiEnabled()

    override suspend fun toggleSecureMode()

    override suspend fun toggleUseRandomEmojis()

    override suspend fun setIconShape(iconShape: Int)

    override suspend fun toggleUseEmojiAsPrimaryColor()

    override suspend fun setDragHandleWidth(width: Int)

    override suspend fun setConfettiType(type: Int)

    override suspend fun toggleAllowAutoClipboardPaste()

    override suspend fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer)

    override suspend fun setConfettiHarmonizationLevel(level: Float)

    override suspend fun toggleGeneratePreviews()

    override suspend fun toggleSkipImagePicking()

    override suspend fun toggleShowSettingsInLandscape()

    override suspend fun toggleUseFullscreenSettings()

    override suspend fun setSwitchType(type: SwitchType)

    override suspend fun setDefaultDrawLineWidth(value: Float)
}