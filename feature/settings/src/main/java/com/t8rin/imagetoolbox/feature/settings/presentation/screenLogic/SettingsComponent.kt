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

package com.t8rin.imagetoolbox.feature.settings.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.SystemBarsVisibility
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.smartJob
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.ColorHarmonizer
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.domain.model.SwitchType
import com.t8rin.imagetoolbox.core.settings.presentation.model.Setting
import com.t8rin.imagetoolbox.core.settings.presentation.model.SettingsGroup
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.ui.utils.BaseComponent
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.util.Locale

class SettingsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onTryGetUpdate: (Boolean, () -> Unit) -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    @Assisted val isUpdateAvailable: Value<Boolean>,
    @Assisted val onGoBack: (() -> Unit)?,
    @Assisted initialSearchQuery: String,
    private val imageGetter: ImageGetter<Bitmap>,
    private val fileController: FileController,
    private val settingsManager: SettingsManager,
    private val resourceManager: ResourceManager,
    private val shareProvider: ShareProvider,
    dispatchersHolder: DispatchersHolder,
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    private val _searchKeyword = mutableStateOf("")
    val searchKeyword by _searchKeyword

    private val _filteredSettings: MutableState<List<Pair<SettingsGroup, Setting>>?> =
        mutableStateOf(null)
    val filteredSettings by _filteredSettings

    private val _isFilteringSettings = mutableStateOf(false)
    val isFilteringSettings by _isFilteringSettings

    private var filterJob by smartJob()
    private fun filterSettings() {
        filterJob = componentScope.launch {
            delay(150)
            _isFilteringSettings.update { searchKeyword.isNotEmpty() }
            _filteredSettings.update {
                searchKeyword.takeIf { it.trim().isNotEmpty() }?.let {
                    val newList = mutableListOf<Pair<Pair<SettingsGroup, Setting>, Int>>()
                    SettingsGroup.entries.forEach { group ->
                        group.settingsList.forEach { setting ->
                            val keywords = mutableListOf<String>().apply {
                                add(resourceManager.getString(group.titleId))
                                add(resourceManager.getString(setting.title))
                                add(
                                    resourceManager.getStringLocalized(
                                        group.titleId,
                                        Locale.ENGLISH.language
                                    )
                                )
                                add(
                                    resourceManager.getStringLocalized(
                                        setting.title,
                                        Locale.ENGLISH.language
                                    )
                                )
                                setting.subtitle?.let {
                                    add(resourceManager.getString(it))
                                    add(
                                        resourceManager.getStringLocalized(
                                            it,
                                            Locale.ENGLISH.language
                                        )
                                    )
                                }
                            }

                            val substringStart = keywords
                                .joinToString()
                                .indexOf(
                                    string = searchKeyword,
                                    ignoreCase = true
                                ).takeIf { it != -1 }

                            substringStart?.plus(searchKeyword.length)?.let { substringEnd ->
                                newList.add(group to setting to (substringEnd - substringStart))
                            }
                        }
                    }
                    newList.sortedBy { it.second }.map { it.first }
                }
            }
            _isFilteringSettings.update { false }
        }
    }

    fun updateSearchKeyword(value: String) {
        _searchKeyword.update { value }
        filterSettings()
    }

    fun tryGetUpdate(
        isNewRequest: Boolean = false,
        onNoUpdates: () -> Unit = {}
    ) = onTryGetUpdate(isNewRequest, onNoUpdates)

    init {
        settingsScope {
            _settingsState.value = getSettingsState().also {
                if (it.clearCacheOnLaunch) clearCache()
            }
            settingsState.onEach {
                _settingsState.value = it
            }.collect()
        }

        debounce {
            updateSearchKeyword(initialSearchQuery)
        }
    }

    fun getReadableCacheSize(): String = fileController.getReadableCacheSize()

    fun clearCache(onComplete: (String) -> Unit = {}) = fileController.clearCache(onComplete)

    fun toggleAddSequenceNumber() = settingsScope { toggleAddSequenceNumber() }

    fun toggleAddOriginalFilename() = settingsScope { toggleAddOriginalFilename() }

    fun setEmojisCount(count: Int) = settingsScope { setEmojisCount(count) }

    fun setImagePickerMode(mode: Int) = settingsScope { setImagePickerMode(mode) }

    fun toggleAddFileSize() = settingsScope { toggleAddFileSize() }

    fun setEmoji(emoji: Int) = settingsScope { setEmoji(emoji) }

    fun setFilenamePrefix(name: String) = settingsScope { setFilenamePrefix(name) }

    fun setFilenameSuffix(name: String) = settingsScope { setFilenameSuffix(name) }

    fun toggleShowUpdateDialog() = settingsScope { toggleShowUpdateDialogOnStartup() }

    fun setColorTuple(colorTuple: ColorTuple) = settingsScope {
        setColorTuple(
            colorTuple.run {
                "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
            }
        )
    }

    fun toggleDynamicColors() = settingsScope { toggleDynamicColors() }

    fun toggleLockDrawOrientation() = settingsScope { toggleLockDrawOrientation() }

    fun setBorderWidth(width: Float) = settingsScope { setBorderWidth(width) }

    fun toggleAllowImageMonet() = settingsScope { toggleAllowImageMonet() }

    fun toggleAmoledMode() = settingsScope { toggleAmoledMode() }

    fun setNightMode(nightMode: NightMode) = settingsScope { setNightMode(nightMode) }

    fun setSaveFolderUri(uri: Uri?) = settingsScope { setSaveFolderUri(uri?.toString()) }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun setColorTuples(colorTuples: List<ColorTuple>) =
        settingsScope { setColorTuples(colorTuples.asString()) }

    fun setAlignment(align: Float) = settingsScope { setAlignment(align.toInt()) }

    fun setScreenOrder(data: List<Screen>) =
        settingsScope { setScreenOrder(data.joinToString("/") { it.id.toString() }) }

    fun toggleClearCacheOnLaunch() = settingsScope { toggleClearCacheOnLaunch() }

    fun toggleGroupOptionsByType() = settingsScope { toggleGroupOptionsByTypes() }

    fun toggleRandomizeFilename() = settingsScope { toggleRandomizeFilename() }

    fun createBackup(
        uri: Uri,
        onResult: (SaveResult) -> Unit,
    ) = settingsScope {
        fileController.writeBytes(
            uri = uri.toString(),
            block = { it.writeBytes(createBackupFile()) }
        ).also(onResult)
    }

    fun exportFonts(
        uri: Uri,
        onResult: (SaveResult) -> Unit,
    ) = settingsScope {
        fileController.transferBytes(
            fromUri = createCustomFontsExport().toString(),
            toUri = uri.toString()
        ).also(onResult)
    }

    fun restoreBackupFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) = settingsScope {
        restoreFromBackupFile(
            backupFileUri = uri.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun resetSettings() = settingsScope { resetSettings() }

    fun createBackupFilename(): String = settingsManager.createBackupFilename()

    fun setFont(font: DomainFontFamily) = settingsScope { setFont(font) }

    fun setFontScale(scale: Float) = settingsScope { setFontScale(scale) }

    fun toggleAllowCollectCrashlytics() = settingsScope { toggleAllowCrashlytics() }

    fun toggleAllowCollectAnalytics() = settingsScope { toggleAllowAnalytics() }

    fun toggleAllowBetas() = settingsScope { toggleAllowBetas() }

    fun toggleDrawContainerShadows() = settingsScope { toggleDrawContainerShadows() }

    fun toggleDrawSwitchShadows() = settingsScope { toggleDrawSwitchShadows() }

    fun toggleDrawSliderShadows() = settingsScope { toggleDrawSliderShadows() }

    fun toggleDrawButtonShadows() = settingsScope { toggleDrawButtonShadows() }

    fun toggleDrawFabShadows() = settingsScope { toggleDrawFabShadows() }

    fun addColorTupleFromEmoji(emoji: String) = settingsScope {
        if (emoji.contains("shoe", true)) {
            setFont(DomainFontFamily.DejaVu)
            val colorTuple = ColorTuple(
                primary = Color(0xFF6D216D),
                secondary = Color(0xFF240A95),
                tertiary = Color(0xFFFFFFA0),
                surface = Color(0xFF1D2D3D)
            )
            val colorTupleS = listOf(colorTuple).asString()
            setColorTuple(colorTuple)
            setColorTuples(this@SettingsComponent.settingsState.colorTupleList + "*" + colorTupleS)
            setThemeContrast(0f)
            setThemeStyle(0)
            if (this@SettingsComponent.settingsState.useEmojiAsPrimaryColor) toggleUseEmojiAsPrimaryColor()
            if (this@SettingsComponent.settingsState.isInvertThemeColors) toggleInvertColors()
        } else {
            imageGetter.getImage(data = emoji)
                ?.extractPrimaryColor()
                ?.let { primary ->
                    val colorTuple = ColorTuple(primary)
                    setColorTuple(colorTuple)
                    settingsManager.setColorTuples(
                        this@SettingsComponent.settingsState.colorTupleList + "*" + listOf(
                            colorTuple
                        ).asString()
                    )
                }
        }
        if (this@SettingsComponent.settingsState.isDynamicColors) toggleDynamicColors()
    }

    fun setThemeContrast(value: Float) = settingsScope { setThemeContrast(value.toDouble()) }

    fun setThemeStyle(value: Int) = settingsScope { setThemeStyle(value) }

    fun toggleInvertColors() = settingsScope { toggleInvertColors() }

    fun toggleScreenSearchEnabled() = settingsScope { toggleScreensSearchEnabled() }

    fun toggleDrawAppBarShadows() = settingsScope { toggleDrawAppBarShadows() }

    private fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) =
        settingsScope { setCopyToClipboardMode(copyToClipboardMode) }

    fun toggleAutoPinClipboard(value: Boolean) {
        val mode = if (value) {
            CopyToClipboardMode.Enabled.WithSaving
        } else {
            CopyToClipboardMode.Disabled
        }
        setCopyToClipboardMode(mode)
    }

    fun toggleAutoPinClipboardOnlyClip(value: Boolean) {
        val mode = if (value) {
            CopyToClipboardMode.Enabled.WithoutSaving
        } else {
            CopyToClipboardMode.Enabled.WithSaving
        }
        setCopyToClipboardMode(mode)
    }

    fun setVibrationStrength(strength: Int) = settingsScope { setVibrationStrength(strength) }

    fun toggleOverwriteFiles() = settingsScope { toggleOverwriteFiles() }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) =
        settingsScope { setDefaultImageScaleMode(imageScaleMode) }

    fun setSwitchType(type: SwitchType) = settingsScope { setSwitchType(type) }

    fun toggleMagnifierEnabled() = settingsScope { toggleMagnifierEnabled() }

    fun toggleExifWidgetInitialState() = settingsScope { toggleExifWidgetInitialState() }

    fun setScreensWithBrightnessEnforcement(screen: Screen) = settingsScope {
        val screens =
            this@SettingsComponent.settingsState.screenListWithMaxBrightnessEnforcement
                .toggle(screen.id)

        setScreensWithBrightnessEnforcement(
            screens.joinToString("/") { it.toString() }
        )
    }

    fun toggleConfettiEnabled() = settingsScope { toggleConfettiEnabled() }

    fun toggleSecureMode() = settingsScope { toggleSecureMode() }

    fun toggleUseEmojiAsPrimaryColor() = settingsScope { toggleUseEmojiAsPrimaryColor() }

    fun toggleUseRandomEmojis() = settingsScope { toggleUseRandomEmojis() }

    fun setIconShape(iconShape: Int) = settingsScope { setIconShape(iconShape) }

    fun setDragHandleWidth(width: Int) = settingsScope { setDragHandleWidth(width) }

    fun setConfettiType(type: Int) = settingsScope { setConfettiType(type) }

    fun toggleAllowAutoClipboardPaste() = settingsScope { toggleAllowAutoClipboardPaste() }

    fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) =
        settingsScope { setConfettiHarmonizer(colorHarmonizer) }

    fun setConfettiHarmonizationLevel(level: Float) =
        settingsScope { setConfettiHarmonizationLevel(level) }

    fun toggleGeneratePreviews() = settingsScope { toggleGeneratePreviews() }

    fun toggleSkipImagePicking() = settingsScope { toggleSkipImagePicking() }

    fun toggleShowSettingsInLandscape() = settingsScope { toggleShowSettingsInLandscape() }

    fun toggleUseFullscreenSettings() = settingsScope { toggleUseFullscreenSettings() }

    fun setDefaultDrawLineWidth(value: Float) = settingsScope { setDefaultDrawLineWidth(value) }

    fun toggleOpenEditInsteadOfPreview() = settingsScope { toggleOpenEditInsteadOfPreview() }

    fun toggleCanEnterPresetsByTextField() = settingsScope { toggleCanEnterPresetsByTextField() }

    fun setColorBlindScheme(value: Int?) = settingsScope { setColorBlindType(value) }

    fun toggleIsLinksPreviewEnabled() = settingsScope { toggleIsLinkPreviewEnabled() }

    fun setDefaultDrawColor(colorModel: ColorModel) =
        settingsScope { setDefaultDrawColor(colorModel) }

    fun setDefaultDrawPathMode(mode: Int) = settingsScope { setDefaultDrawPathMode(mode) }

    fun toggleAddTimestampToFilename() = settingsScope { toggleAddTimestampToFilename() }

    fun toggleUseFormattedFilenameTimestamp() =
        settingsScope { toggleUseFormattedFilenameTimestamp() }

    fun setDefaultResizeType(resizeType: ResizeType) =
        settingsScope { setDefaultResizeType(resizeType) }

    fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility) =
        settingsScope { setSystemBarsVisibility(systemBarsVisibility) }

    fun toggleIsSystemBarsVisibleBySwipe() = settingsScope { toggleIsSystemBarsVisibleBySwipe() }

    fun toggleUseCompactSelectors() = settingsScope { toggleUseCompactSelectorsLayout() }

    fun setMainScreenTitle(title: String) = settingsScope { setMainScreenTitle(title) }

    fun setSliderType(sliderType: SliderType) = settingsScope { setSliderType(sliderType) }

    fun toggleIsCenterAlignDialogButtons() = settingsScope { toggleIsCenterAlignDialogButtons() }

    fun setFastSettingsSide(side: FastSettingsSide) = settingsScope { setFastSettingsSide(side) }

    fun setChecksumTypeForFilename(type: HashingType?) =
        settingsScope { setChecksumTypeForFilename(type) }

    fun importCustomFont(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) = settingsScope {
        importCustomFont(uri.toString())?.let { font ->
            setFont(font)
            onSuccess()
        } ?: onFailure()
    }

    fun removeCustomFont(
        font: UiFontFamily.Custom
    ) = settingsScope {
        removeCustomFont(font.asDomain() as DomainFontFamily.Custom)
        setFont(DomainFontFamily.System)
    }

    fun toggleEnableToolExitConfirmation() = settingsScope { toggleEnableToolExitConfirmation() }

    fun shareLogs() = settingsScope {
        shareProvider.shareUri(
            uri = settingsManager.createLogsExport(),
            onComplete = {}
        )
    }

    fun toggleAddPresetInfoToFilename() = settingsScope { toggleAddPresetInfoToFilename() }

    fun toggleAddImageScaleModeInfoToFilename() =
        settingsScope { toggleAddImageScaleModeInfoToFilename() }

    fun toggleAllowSkipIfLarger() = settingsScope { toggleAllowSkipIfLarger() }

    fun toggleIsScreenSelectionLauncherMode() =
        settingsScope { toggleIsScreenSelectionLauncherMode() }

    private inline fun settingsScope(
        crossinline action: suspend SettingsManager.() -> Unit
    ) {
        componentScope.launch {
            settingsManager.action()
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onTryGetUpdate: (Boolean, () -> Unit) -> Unit,
            onNavigate: (Screen) -> Unit,
            isUpdateAvailable: Value<Boolean>,
            onGoBack: (() -> Unit)?,
            initialSearchQuery: String
        ): SettingsComponent
    }
}