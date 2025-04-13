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

import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.HashingType
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.FastSettingsSide
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType

interface SettingsInteractor : SimpleSettingsInteractor {

    suspend fun toggleAddSequenceNumber()

    suspend fun toggleAddOriginalFilename()

    suspend fun setEmojisCount(count: Int)

    suspend fun setImagePickerMode(mode: Int)

    suspend fun toggleAddFileSize()

    suspend fun setEmoji(emoji: Int)

    suspend fun setFilenamePrefix(name: String)

    suspend fun toggleShowUpdateDialogOnStartup()

    suspend fun setColorTuple(colorTuple: String)

    suspend fun setPresets(newPresets: List<Int>)

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
        onFailure: (Throwable) -> Unit,
    )

    suspend fun resetSettings()

    fun createBackupFilename(): String

    suspend fun setFont(font: DomainFontFamily)

    suspend fun setFontScale(scale: Float)

    suspend fun toggleAllowCrashlytics()

    suspend fun toggleAllowAnalytics()

    suspend fun toggleAllowBetas()

    suspend fun toggleDrawContainerShadows()

    suspend fun toggleDrawButtonShadows()

    suspend fun toggleDrawSliderShadows()

    suspend fun toggleDrawSwitchShadows()

    suspend fun toggleDrawFabShadows()

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

    suspend fun toggleExifWidgetInitialState()

    suspend fun setInitialOCRLanguageCodes(list: List<String>)

    suspend fun setScreensWithBrightnessEnforcement(data: String)

    suspend fun toggleConfettiEnabled()

    suspend fun toggleSecureMode()

    suspend fun toggleUseRandomEmojis()

    suspend fun setIconShape(iconShape: Int)

    suspend fun toggleUseEmojiAsPrimaryColor()

    suspend fun setDragHandleWidth(width: Int)

    suspend fun setConfettiType(type: Int)

    suspend fun toggleAllowAutoClipboardPaste()

    suspend fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer)

    suspend fun setConfettiHarmonizationLevel(level: Float)

    suspend fun toggleGeneratePreviews()

    suspend fun toggleSkipImagePicking()

    suspend fun toggleShowSettingsInLandscape()

    suspend fun toggleUseFullscreenSettings()

    suspend fun setSwitchType(type: SwitchType)

    suspend fun setDefaultDrawLineWidth(value: Float)

    suspend fun toggleOpenEditInsteadOfPreview()

    suspend fun toggleCanEnterPresetsByTextField()

    suspend fun adjustPerformance(performanceClass: PerformanceClass)

    suspend fun registerDonateDialogOpen()

    suspend fun setNotShowDonateDialogAgain()

    suspend fun setColorBlindType(value: Int?)

    suspend fun toggleFavoriteScreen(screenId: Int)

    suspend fun toggleIsLinkPreviewEnabled()

    suspend fun setDefaultDrawColor(color: ColorModel)

    suspend fun setDefaultDrawPathMode(modeOrdinal: Int)

    suspend fun toggleAddTimestampToFilename()

    suspend fun toggleUseFormattedFilenameTimestamp()

    suspend fun registerTelegramGroupOpen()

    suspend fun setDefaultResizeType(resizeType: ResizeType)

    suspend fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility)

    suspend fun toggleIsSystemBarsVisibleBySwipe()

    suspend fun setInitialOcrMode(mode: Int)

    suspend fun toggleUseCompactSelectorsLayout()

    suspend fun setMainScreenTitle(title: String)

    suspend fun setSliderType(type: SliderType)

    suspend fun toggleIsCenterAlignDialogButtons()

    suspend fun setFastSettingsSide(side: FastSettingsSide)

    suspend fun setChecksumTypeForFilename(type: HashingType?)

    suspend fun setCustomFonts(fonts: List<DomainFontFamily.Custom>)

    suspend fun importCustomFont(uri: String): DomainFontFamily.Custom?

    suspend fun removeCustomFont(font: DomainFontFamily.Custom)

    suspend fun createCustomFontsExport(): String?

    suspend fun toggleEnableToolExitConfirmation()

    suspend fun createLogsExport(): String
}

fun SettingsInteractor.toSimpleSettingsInteractor(): SimpleSettingsInteractor =
    object : SimpleSettingsInteractor by this {}