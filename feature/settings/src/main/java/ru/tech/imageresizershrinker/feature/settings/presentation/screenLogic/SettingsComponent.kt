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

package ru.tech.imageresizershrinker.feature.settings.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.exifinterface.media.ExifInterface
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.ChecksumType
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility
import ru.tech.imageresizershrinker.core.domain.resource.ResourceManager
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.FastSettingsSide
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType
import ru.tech.imageresizershrinker.core.settings.presentation.model.Setting
import ru.tech.imageresizershrinker.core.settings.presentation.model.SettingsGroup
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import java.util.Locale

class SettingsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onTryGetUpdate: (Boolean, () -> Unit) -> Unit,
    @Assisted val onNavigate: (Screen) -> Unit,
    @Assisted val isUpdateAvailable: Value<Boolean>,
    @Assisted val onGoBack: (() -> Unit)?,
    @Assisted initialSearchQuery: String,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val settingsManager: SettingsManager,
    private val resourceManager: ResourceManager,
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
        componentScope.launch {
            _settingsState.value = settingsManager.getSettingsState().also {
                if (it.clearCacheOnLaunch) clearCache()
            }
            settingsManager.getSettingsStateFlow().onEach {
                _settingsState.value = it
            }.collect()
        }

        debounce {
            updateSearchKeyword(initialSearchQuery)
        }
    }

    fun getReadableCacheSize(): String = fileController.getReadableCacheSize()

    fun clearCache(onComplete: (String) -> Unit = {}) = fileController.clearCache(onComplete)

    fun toggleAddSequenceNumber() {
        componentScope.launch {
            settingsManager.toggleAddSequenceNumber()
        }
    }

    fun toggleAddOriginalFilename() {
        componentScope.launch {
            settingsManager.toggleAddOriginalFilename()
        }
    }

    fun setEmojisCount(count: Int) {
        componentScope.launch {
            settingsManager.setEmojisCount(count)
        }
    }

    fun setImagePickerMode(mode: Int) {
        componentScope.launch {
            settingsManager.setImagePickerMode(mode)
        }
    }

    fun toggleAddFileSize() {
        componentScope.launch {
            settingsManager.toggleAddFileSize()
        }
    }

    fun setEmoji(emoji: Int) {
        componentScope.launch {
            settingsManager.setEmoji(emoji)
        }
    }

    fun setFilenamePrefix(name: String) {
        componentScope.launch {
            settingsManager.setFilenamePrefix(name)
        }
    }

    fun setFilenameSuffix(name: String) {
        componentScope.launch {
            settingsManager.setFilenameSuffix(name)
        }
    }

    fun toggleShowUpdateDialog() {
        componentScope.launch {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setColorTuple(colorTuple: ColorTuple) {
        componentScope.launch {
            settingsManager.setColorTuple(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun toggleDynamicColors() {
        componentScope.launch {
            settingsManager.toggleDynamicColors()
        }
    }

    fun toggleLockDrawOrientation() {
        componentScope.launch {
            settingsManager.toggleLockDrawOrientation()
        }
    }

    fun setBorderWidth(width: Float) {
        componentScope.launch {
            settingsManager.setBorderWidth(width)
        }
    }

    fun toggleAllowImageMonet() {
        componentScope.launch {
            settingsManager.toggleAllowImageMonet()
        }
    }

    fun toggleAmoledMode() {
        componentScope.launch {
            settingsManager.toggleAmoledMode()
        }
    }

    fun setNightMode(nightMode: NightMode) {
        componentScope.launch {
            settingsManager.setNightMode(nightMode)
        }
    }

    fun setSaveFolderUri(uri: Uri?) {
        componentScope.launch {
            settingsManager.setSaveFolderUri(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun setColorTuples(colorTuples: List<ColorTuple>) {
        componentScope.launch {
            settingsManager.setColorTuples(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        componentScope.launch {
            settingsManager.setAlignment(align.toInt())
        }
    }

    fun setScreenOrder(data: List<Screen>) {
        componentScope.launch {
            settingsManager.setScreenOrder(data.joinToString("/") { it.id.toString() })
        }
    }

    fun toggleClearCacheOnLaunch() {
        componentScope.launch {
            settingsManager.toggleClearCacheOnLaunch()
        }
    }

    fun toggleGroupOptionsByType() {
        componentScope.launch {
            settingsManager.toggleGroupOptionsByTypes()
        }
    }

    fun toggleRandomizeFilename() {
        componentScope.launch {
            settingsManager.toggleRandomizeFilename()
        }
    }

    fun createBackup(
        uri: Uri,
        onResult: (SaveResult) -> Unit,
    ) {
        componentScope.launch(ioDispatcher) {
            fileController.writeBytes(
                uri = uri.toString(),
                block = { it.writeBytes(settingsManager.createBackupFile()) }
            ).also(onResult)
        }
    }

    fun exportFonts(
        uri: Uri,
        onResult: (SaveResult) -> Unit,
    ) {
        componentScope.launch(ioDispatcher) {
            fileController.writeBytes(
                uri = uri.toString(),
                block = { it.writeBytes(settingsManager.createCustomFontsExport()) }
            ).also(onResult)
        }
    }

    fun restoreBackupFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        componentScope.launch {
            withContext(ioDispatcher) {
                settingsManager.restoreFromBackupFile(
                    backupFileUri = uri.toString(),
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
        }
    }

    fun resetSettings() {
        componentScope.launch {
            settingsManager.resetSettings()
        }
    }

    fun createBackupFilename(): String = settingsManager.createBackupFilename()

    fun setFont(font: DomainFontFamily) {
        componentScope.launch {
            settingsManager.setFont(font)
        }
    }

    fun setFontScale(scale: Float) {
        componentScope.launch {
            settingsManager.setFontScale(scale)
        }
    }

    fun toggleAllowCollectCrashlytics() {
        componentScope.launch {
            settingsManager.toggleAllowCrashlytics()
        }
    }

    fun toggleAllowCollectAnalytics() {
        componentScope.launch {
            settingsManager.toggleAllowAnalytics()
        }
    }

    fun toggleAllowBetas() {
        componentScope.launch {
            settingsManager.toggleAllowBetas()
        }
    }

    fun toggleDrawContainerShadows() {
        componentScope.launch {
            settingsManager.toggleDrawContainerShadows()
        }
    }

    fun toggleDrawSwitchShadows() {
        componentScope.launch {
            settingsManager.toggleDrawSwitchShadows()
        }
    }

    fun toggleDrawSliderShadows() {
        componentScope.launch {
            settingsManager.toggleDrawSliderShadows()
        }
    }

    fun toggleDrawButtonShadows() {
        componentScope.launch {
            settingsManager.toggleDrawButtonShadows()
        }
    }

    fun toggleDrawFabShadows() {
        componentScope.launch {
            settingsManager.toggleDrawFabShadows()
        }
    }

    fun addColorTupleFromEmoji(emoji: String) {
        componentScope.launch {
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
                settingsManager.setColorTuples(settingsState.colorTupleList + "*" + colorTupleS)
                setThemeContrast(0f)
                setThemeStyle(0)
                if (settingsState.useEmojiAsPrimaryColor) toggleUseEmojiAsPrimaryColor()
                if (settingsState.isInvertThemeColors) toggleInvertColors()
            } else {
                imageGetter.getImage(data = emoji)
                    ?.extractPrimaryColor()
                    ?.let { primary ->
                        val colorTuple = ColorTuple(primary)
                        setColorTuple(colorTuple)
                        settingsManager.setColorTuples(
                            settingsState.colorTupleList + "*" + listOf(
                                colorTuple
                            ).asString()
                        )
                    }
            }
            if (settingsState.isDynamicColors) toggleDynamicColors()
        }
    }

    fun setThemeContrast(value: Float) {
        componentScope.launch {
            settingsManager.setThemeContrast(value.toDouble())
        }
    }

    fun setThemeStyle(value: Int) {
        componentScope.launch {
            settingsManager.setThemeStyle(value)
        }
    }

    fun toggleInvertColors() {
        componentScope.launch {
            settingsManager.toggleInvertColors()
        }
    }

    fun toggleScreenSearchEnabled() {
        componentScope.launch {
            settingsManager.toggleScreensSearchEnabled()
        }
    }

    fun toggleDrawAppBarShadows() {
        componentScope.launch {
            settingsManager.toggleDrawAppBarShadows()
        }
    }

    private fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) {
        componentScope.launch {
            settingsManager.setCopyToClipboardMode(copyToClipboardMode)
        }
    }

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

    fun setVibrationStrength(strength: Int) {
        componentScope.launch {
            settingsManager.setVibrationStrength(strength)
        }
    }

    fun toggleOverwriteFiles() {
        componentScope.launch {
            settingsManager.toggleOverwriteFiles()
        }
    }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        componentScope.launch {
            settingsManager.setDefaultImageScaleMode(imageScaleMode)
        }
    }

    fun setSwitchType(type: SwitchType) {
        componentScope.launch {
            settingsManager.setSwitchType(type)
        }
    }

    fun toggleMagnifierEnabled() {
        componentScope.launch {
            settingsManager.toggleMagnifierEnabled()
        }
    }

    fun toggleExifWidgetInitialState() {
        componentScope.launch {
            settingsManager.toggleExifWidgetInitialState()
        }
    }

    fun setScreensWithBrightnessEnforcement(screen: Screen) {
        componentScope.launch {
            val screens = settingsState.screenListWithMaxBrightnessEnforcement.let {
                if (screen.id in it) it - screen.id
                else it + screen.id
            }

            settingsManager.setScreensWithBrightnessEnforcement(
                screens.joinToString("/") { it.toString() }
            )
        }
    }

    fun toggleConfettiEnabled() {
        componentScope.launch {
            settingsManager.toggleConfettiEnabled()
        }
    }

    fun toggleSecureMode() {
        componentScope.launch {
            settingsManager.toggleSecureMode()
        }
    }

    fun toggleUseEmojiAsPrimaryColor() {
        componentScope.launch {
            settingsManager.toggleUseEmojiAsPrimaryColor()
        }
    }

    fun toggleUseRandomEmojis() {
        componentScope.launch {
            settingsManager.toggleUseRandomEmojis()
        }
    }

    fun setIconShape(iconShape: Int) {
        componentScope.launch {
            settingsManager.setIconShape(iconShape)
        }
    }

    fun setDragHandleWidth(width: Int) {
        componentScope.launch {
            settingsManager.setDragHandleWidth(width)
        }
    }

    fun setConfettiType(type: Int) {
        componentScope.launch {
            settingsManager.setConfettiType(type)
        }
    }

    fun toggleAllowAutoClipboardPaste() {
        componentScope.launch {
            settingsManager.toggleAllowAutoClipboardPaste()
        }
    }

    fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) {
        componentScope.launch {
            settingsManager.setConfettiHarmonizer(colorHarmonizer)
        }
    }

    fun setConfettiHarmonizationLevel(level: Float) {
        componentScope.launch {
            settingsManager.setConfettiHarmonizationLevel(level)
        }
    }

    fun toggleGeneratePreviews() {
        componentScope.launch {
            settingsManager.toggleGeneratePreviews()
        }
    }

    fun toggleSkipImagePicking() {
        componentScope.launch {
            settingsManager.toggleSkipImagePicking()
        }
    }

    fun toggleShowSettingsInLandscape() {
        componentScope.launch {
            settingsManager.toggleShowSettingsInLandscape()
        }
    }

    fun toggleUseFullscreenSettings() {
        componentScope.launch {
            settingsManager.toggleUseFullscreenSettings()
        }
    }

    fun setDefaultDrawLineWidth(value: Float) {
        componentScope.launch {
            settingsManager.setDefaultDrawLineWidth(value)
        }
    }

    fun toggleOpenEditInsteadOfPreview() {
        componentScope.launch {
            settingsManager.toggleOpenEditInsteadOfPreview()
        }
    }

    fun toggleCanEnterPresetsByTextField() {
        componentScope.launch {
            settingsManager.toggleCanEnterPresetsByTextField()
        }
    }

    fun setColorBlindScheme(value: Int?) {
        componentScope.launch {
            settingsManager.setColorBlindType(value)
        }
    }

    fun toggleIsLinksPreviewEnabled() {
        componentScope.launch {
            settingsManager.toggleIsLinkPreviewEnabled()
        }
    }

    fun setDefaultDrawColor(colorModel: ColorModel) {
        componentScope.launch {
            settingsManager.setDefaultDrawColor(colorModel)
        }
    }

    fun setDefaultDrawPathMode(mode: Int) {
        componentScope.launch {
            settingsManager.setDefaultDrawPathMode(mode)
        }
    }

    fun toggleAddTimestampToFilename() {
        componentScope.launch {
            settingsManager.toggleAddTimestampToFilename()
        }
    }

    fun toggleUseFormattedFilenameTimestamp() {
        componentScope.launch {
            settingsManager.toggleUseFormattedFilenameTimestamp()
        }
    }

    fun setDefaultResizeType(resizeType: ResizeType) {
        componentScope.launch {
            settingsManager.setDefaultResizeType(resizeType)
        }
    }

    fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility) {
        componentScope.launch {
            settingsManager.setSystemBarsVisibility(systemBarsVisibility)
        }
    }

    fun toggleIsSystemBarsVisibleBySwipe() {
        componentScope.launch {
            settingsManager.toggleIsSystemBarsVisibleBySwipe()
        }
    }

    fun toggleUseCompactSelectors() {
        componentScope.launch {
            settingsManager.toggleUseCompactSelectorsLayout()
        }
    }

    fun setMainScreenTitle(title: String) {
        componentScope.launch {
            settingsManager.setMainScreenTitle(title)
        }
    }

    fun setSliderType(sliderType: SliderType) {
        componentScope.launch {
            settingsManager.setSliderType(sliderType)
        }
    }

    fun toggleIsCenterAlignDialogButtons() {
        componentScope.launch {
            settingsManager.toggleIsCenterAlignDialogButtons()
        }
    }

    fun setFastSettingsSide(side: FastSettingsSide) {
        componentScope.launch {
            settingsManager.setFastSettingsSide(side)
        }
    }

    fun setChecksumTypeForFilename(type: ChecksumType?) {
        componentScope.launch {
            settingsManager.setChecksumTypeForFilename(type)
        }
    }

    fun importCustomFont(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        componentScope.launch {
            settingsManager.importCustomFont(uri.toString())?.let {
                settingsManager.setFont(it)
                onSuccess()
            } ?: onFailure()
        }
    }

    fun removeCustomFont(
        font: UiFontFamily.Custom
    ) {
        componentScope.launch {
            settingsManager.removeCustomFont(font.asDomain() as DomainFontFamily.Custom)
            settingsManager.setFont(DomainFontFamily.System)
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