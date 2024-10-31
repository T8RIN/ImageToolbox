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

package ru.tech.imageresizershrinker.feature.settings.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.exifinterface.media.ExifInterface
import com.arkivanov.decompose.ComponentContext
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen

class SettingsViewModel @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
) : BaseViewModel(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    init {
        if (settingsState.clearCacheOnLaunch) clearCache()

        runBlocking {
            settingsManager.registerAppOpen()
            _settingsState.value = settingsManager.getSettingsState()
        }
        settingsManager.getSettingsStateFlow().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
    }

    fun getReadableCacheSize(): String = fileController.getReadableCacheSize()

    fun clearCache(onComplete: (String) -> Unit = {}) = fileController.clearCache(onComplete)

    fun toggleAddSequenceNumber() {
        viewModelScope.launch {
            settingsManager.toggleAddSequenceNumber()
        }
    }

    fun toggleAddOriginalFilename() {
        viewModelScope.launch {
            settingsManager.toggleAddOriginalFilename()
        }
    }

    fun setEmojisCount(count: Int) {
        viewModelScope.launch {
            settingsManager.setEmojisCount(count)
        }
    }

    fun setImagePickerMode(mode: Int) {
        viewModelScope.launch {
            settingsManager.setImagePickerMode(mode)
        }
    }

    fun toggleAddFileSize() {
        viewModelScope.launch {
            settingsManager.toggleAddFileSize()
        }
    }

    fun setEmoji(emoji: Int) {
        viewModelScope.launch {
            settingsManager.setEmoji(emoji)
        }
    }

    fun setFilenamePrefix(name: String) {
        viewModelScope.launch {
            settingsManager.setFilenamePrefix(name)
        }
    }

    fun setFilenameSuffix(name: String) {
        viewModelScope.launch {
            settingsManager.setFilenameSuffix(name)
        }
    }

    fun toggleShowUpdateDialog() {
        viewModelScope.launch {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setColorTuple(colorTuple: ColorTuple) {
        viewModelScope.launch {
            settingsManager.setColorTuple(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun toggleDynamicColors() {
        viewModelScope.launch {
            settingsManager.toggleDynamicColors()
        }
    }

    fun toggleLockDrawOrientation() {
        viewModelScope.launch {
            settingsManager.toggleLockDrawOrientation()
        }
    }

    fun setBorderWidth(width: Float) {
        viewModelScope.launch {
            settingsManager.setBorderWidth(width)
        }
    }

    fun toggleAllowImageMonet() {
        viewModelScope.launch {
            settingsManager.toggleAllowImageMonet()
        }
    }

    fun toggleAmoledMode() {
        viewModelScope.launch {
            settingsManager.toggleAmoledMode()
        }
    }

    fun setNightMode(nightMode: NightMode) {
        viewModelScope.launch {
            settingsManager.setNightMode(nightMode)
        }
    }

    fun updateSaveFolderUri(uri: Uri?) {
        viewModelScope.launch {
            settingsManager.setSaveFolderUri(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        viewModelScope.launch {
            settingsManager.setColorTuples(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        viewModelScope.launch {
            settingsManager.setAlignment(align.toInt())
        }
    }

    fun updateOrder(data: List<Screen>) {
        viewModelScope.launch {
            settingsManager.setScreenOrder(data.joinToString("/") { it.id.toString() })
        }
    }

    fun toggleClearCacheOnLaunch() {
        viewModelScope.launch {
            settingsManager.toggleClearCacheOnLaunch()
        }
    }

    fun toggleGroupOptionsByType() {
        viewModelScope.launch {
            settingsManager.toggleGroupOptionsByTypes()
        }
    }

    fun toggleRandomizeFilename() {
        viewModelScope.launch {
            settingsManager.toggleRandomizeFilename()
        }
    }

    fun createBackup(
        uri: Uri,
        onResult: (SaveResult) -> Unit,
    ) {
        viewModelScope.launch(ioDispatcher) {
            fileController.writeBytes(
                uri = uri.toString(),
                block = { it.writeBytes(settingsManager.createBackupFile()) }
            ).also(onResult)
        }
    }

    fun restoreBackupFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            settingsManager.resetSettings()
        }
    }

    fun createBackupFilename(): String = settingsManager.createBackupFilename()

    fun setFont(font: DomainFontFamily) {
        viewModelScope.launch {
            settingsManager.setFont(font)
        }
    }

    fun onUpdateFontScale(scale: Float) {
        viewModelScope.launch {
            settingsManager.setFontScale(scale)
        }
    }

    fun toggleAllowCollectCrashlytics() {
        viewModelScope.launch {
            settingsManager.toggleAllowCrashlytics()
        }
    }

    fun toggleAllowCollectAnalytics() {
        viewModelScope.launch {
            settingsManager.toggleAllowAnalytics()
        }
    }

    fun toggleAllowBetas() {
        viewModelScope.launch {
            settingsManager.toggleAllowBetas()
        }
    }

    fun toggleDrawContainerShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawContainerShadows()
        }
    }

    fun toggleDrawSwitchShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawSwitchShadows()
        }
    }

    fun toggleDrawSliderShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawSliderShadows()
        }
    }

    fun toggleDrawButtonShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawButtonShadows()
        }
    }

    fun toggleDrawFabShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawFabShadows()
        }
    }

    fun addColorTupleFromEmoji(
        getEmoji: (Int?) -> String,
        showShoeDescription: ((String) -> Unit)? = null,
    ) {
        viewModelScope.launch {
            val emojiUri = getEmoji(settingsState.selectedEmoji)
            if (emojiUri.contains("shoe", true) && showShoeDescription != null) {
                showShoeDescription(emojiUri)
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
                updateThemeContrast(0f)
                setThemeStyle(0)
                if (settingsState.useEmojiAsPrimaryColor) toggleUseEmojiAsPrimaryColor()
                if (settingsState.isInvertThemeColors) toggleInvertColors()
            } else {
                imageGetter.getImage(data = emojiUri)
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

    fun updateThemeContrast(value: Float) {
        viewModelScope.launch {
            settingsManager.setThemeContrast(value.toDouble())
        }
    }

    fun setThemeStyle(value: Int) {
        viewModelScope.launch {
            settingsManager.setThemeStyle(value)
        }
    }

    fun toggleInvertColors() {
        viewModelScope.launch {
            settingsManager.toggleInvertColors()
        }
    }

    fun toggleScreenSearchEnabled() {
        viewModelScope.launch {
            settingsManager.toggleScreensSearchEnabled()
        }
    }

    fun toggleDrawAppBarShadows() {
        viewModelScope.launch {
            settingsManager.toggleDrawAppBarShadows()
        }
    }

    private fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            settingsManager.setVibrationStrength(strength)
        }
    }

    fun toggleOverwriteFiles() {
        viewModelScope.launch {
            settingsManager.toggleOverwriteFiles()
        }
    }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        viewModelScope.launch {
            settingsManager.setDefaultImageScaleMode(imageScaleMode)
        }
    }

    fun setSwitchType(type: SwitchType) {
        viewModelScope.launch {
            settingsManager.setSwitchType(type)
        }
    }

    fun toggleMagnifierEnabled() {
        viewModelScope.launch {
            settingsManager.toggleMagnifierEnabled()
        }
    }

    fun toggleExifWidgetInitialState() {
        viewModelScope.launch {
            settingsManager.toggleExifWidgetInitialState()
        }
    }

    fun updateBrightnessEnforcementScreens(screen: Screen) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            settingsManager.toggleConfettiEnabled()
        }
    }

    fun toggleSecureMode() {
        viewModelScope.launch {
            settingsManager.toggleSecureMode()
        }
    }

    fun toggleUseEmojiAsPrimaryColor() {
        viewModelScope.launch {
            settingsManager.toggleUseEmojiAsPrimaryColor()
        }
    }

    fun toggleUseRandomEmojis() {
        viewModelScope.launch {
            settingsManager.toggleUseRandomEmojis()
        }
    }

    fun setIconShape(iconShape: Int) {
        viewModelScope.launch {
            settingsManager.setIconShape(iconShape)
        }
    }

    fun setDragHandleWidth(width: Int) {
        viewModelScope.launch {
            settingsManager.setDragHandleWidth(width)
        }
    }

    fun setConfettiType(type: Int) {
        viewModelScope.launch {
            settingsManager.setConfettiType(type)
        }
    }

    fun toggleAllowAutoClipboardPaste() {
        viewModelScope.launch {
            settingsManager.toggleAllowAutoClipboardPaste()
        }
    }

    fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) {
        viewModelScope.launch {
            settingsManager.setConfettiHarmonizer(colorHarmonizer)
        }
    }

    fun setConfettiHarmonizationLevel(level: Float) {
        viewModelScope.launch {
            settingsManager.setConfettiHarmonizationLevel(level)
        }
    }

    fun toggleGeneratePreviews() {
        viewModelScope.launch {
            settingsManager.toggleGeneratePreviews()
        }
    }

    fun toggleSkipImagePicking() {
        viewModelScope.launch {
            settingsManager.toggleSkipImagePicking()
        }
    }

    fun toggleShowSettingsInLandscape() {
        viewModelScope.launch {
            settingsManager.toggleShowSettingsInLandscape()
        }
    }

    fun toggleUseFullscreenSettings() {
        viewModelScope.launch {
            settingsManager.toggleUseFullscreenSettings()
        }
    }

    fun setDefaultDrawLineWidth(value: Float) {
        viewModelScope.launch {
            settingsManager.setDefaultDrawLineWidth(value)
        }
    }

    fun toggleOpenEditInsteadOfPreview() {
        viewModelScope.launch {
            settingsManager.toggleOpenEditInsteadOfPreview()
        }
    }

    fun toggleCanEnterPresetsByTextField() {
        viewModelScope.launch {
            settingsManager.toggleCanEnterPresetsByTextField()
        }
    }

    fun setColorBlindScheme(value: Int?) {
        viewModelScope.launch {
            settingsManager.setColorBlindType(value)
        }
    }

    fun toggleIsLinksPreviewEnabled() {
        viewModelScope.launch {
            settingsManager.toggleIsLinkPreviewEnabled()
        }
    }

    fun setDefaultDrawColor(colorModel: ColorModel) {
        viewModelScope.launch {
            settingsManager.setDefaultDrawColor(colorModel)
        }
    }

    fun setDefaultDrawPathMode(mode: Int) {
        viewModelScope.launch {
            settingsManager.setDefaultDrawPathMode(mode)
        }
    }

    fun toggleAddTimestampToFilename() {
        viewModelScope.launch {
            settingsManager.toggleAddTimestampToFilename()
        }
    }

    fun toggleUseFormattedFilenameTimestamp() {
        viewModelScope.launch {
            settingsManager.toggleUseFormattedFilenameTimestamp()
        }
    }

    fun setDefaultResizeType(resizeType: ResizeType) {
        viewModelScope.launch {
            settingsManager.setDefaultResizeType(resizeType)
        }
    }

    fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility) {
        viewModelScope.launch {
            settingsManager.setSystemBarsVisibility(systemBarsVisibility)
        }
    }

    fun toggleIsSystemBarsVisibleBySwipe() {
        viewModelScope.launch {
            settingsManager.toggleIsSystemBarsVisibleBySwipe()
        }
    }

    fun toggleUseCompactSelectors() {
        viewModelScope.launch {
            settingsManager.toggleUseCompactSelectorsLayout()
        }
    }

    fun setMainScreenTitle(title: String) {
        viewModelScope.launch {
            settingsManager.setMainScreenTitle(title)
        }
    }

    fun setSliderType(sliderType: SliderType) {
        viewModelScope.launch {
            settingsManager.setSliderType(sliderType)
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): SettingsViewModel
    }
}