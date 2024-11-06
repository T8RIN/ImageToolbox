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
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen

class SettingsComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
) : BaseComponent(dispatchersHolder, componentContext) {

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
        }.launchIn(componentScope)
    }

    fun getReadableCacheSize(): String = fileController.getReadableCacheSize()

    fun clearCache(onComplete: (String) -> Unit = {}) = fileController.clearCache(onComplete)

    fun toggleAddSequenceNumber() {
        componentScope.debounced {
            settingsManager.toggleAddSequenceNumber()
        }
    }

    fun toggleAddOriginalFilename() {
        componentScope.debounced {
            settingsManager.toggleAddOriginalFilename()
        }
    }

    fun setEmojisCount(count: Int) {
        componentScope.debounced {
            settingsManager.setEmojisCount(count)
        }
    }

    fun setImagePickerMode(mode: Int) {
        componentScope.debounced {
            settingsManager.setImagePickerMode(mode)
        }
    }

    fun toggleAddFileSize() {
        componentScope.debounced {
            settingsManager.toggleAddFileSize()
        }
    }

    fun setEmoji(emoji: Int) {
        componentScope.debounced {
            settingsManager.setEmoji(emoji)
        }
    }

    fun setFilenamePrefix(name: String) {
        componentScope.debounced {
            settingsManager.setFilenamePrefix(name)
        }
    }

    fun setFilenameSuffix(name: String) {
        componentScope.debounced {
            settingsManager.setFilenameSuffix(name)
        }
    }

    fun toggleShowUpdateDialog() {
        componentScope.debounced {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setColorTuple(colorTuple: ColorTuple) {
        componentScope.debounced {
            settingsManager.setColorTuple(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun toggleDynamicColors() {
        componentScope.debounced {
            settingsManager.toggleDynamicColors()
        }
    }

    fun toggleLockDrawOrientation() {
        componentScope.debounced {
            settingsManager.toggleLockDrawOrientation()
        }
    }

    fun setBorderWidth(width: Float) {
        componentScope.debounced {
            settingsManager.setBorderWidth(width)
        }
    }

    fun toggleAllowImageMonet() {
        componentScope.debounced {
            settingsManager.toggleAllowImageMonet()
        }
    }

    fun toggleAmoledMode() {
        componentScope.debounced {
            settingsManager.toggleAmoledMode()
        }
    }

    fun setNightMode(nightMode: NightMode) {
        componentScope.debounced {
            settingsManager.setNightMode(nightMode)
        }
    }

    fun setSaveFolderUri(uri: Uri?) {
        componentScope.debounced {
            settingsManager.setSaveFolderUri(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun setColorTuples(colorTuples: List<ColorTuple>) {
        componentScope.debounced {
            settingsManager.setColorTuples(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        componentScope.debounced {
            settingsManager.setAlignment(align.toInt())
        }
    }

    fun setScreenOrder(data: List<Screen>) {
        componentScope.debounced {
            settingsManager.setScreenOrder(data.joinToString("/") { it.id.toString() })
        }
    }

    fun toggleClearCacheOnLaunch() {
        componentScope.debounced {
            settingsManager.toggleClearCacheOnLaunch()
        }
    }

    fun toggleGroupOptionsByType() {
        componentScope.debounced {
            settingsManager.toggleGroupOptionsByTypes()
        }
    }

    fun toggleRandomizeFilename() {
        componentScope.debounced {
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
        componentScope.debounced {
            settingsManager.resetSettings()
        }
    }

    fun createBackupFilename(): String = settingsManager.createBackupFilename()

    fun setFont(font: DomainFontFamily) {
        componentScope.debounced {
            settingsManager.setFont(font)
        }
    }

    fun setFontScale(scale: Float) {
        componentScope.debounced {
            settingsManager.setFontScale(scale)
        }
    }

    fun toggleAllowCollectCrashlytics() {
        componentScope.debounced {
            settingsManager.toggleAllowCrashlytics()
        }
    }

    fun toggleAllowCollectAnalytics() {
        componentScope.debounced {
            settingsManager.toggleAllowAnalytics()
        }
    }

    fun toggleAllowBetas() {
        componentScope.debounced {
            settingsManager.toggleAllowBetas()
        }
    }

    fun toggleDrawContainerShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawContainerShadows()
        }
    }

    fun toggleDrawSwitchShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawSwitchShadows()
        }
    }

    fun toggleDrawSliderShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawSliderShadows()
        }
    }

    fun toggleDrawButtonShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawButtonShadows()
        }
    }

    fun toggleDrawFabShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawFabShadows()
        }
    }

    fun addColorTupleFromEmoji(
        getEmoji: (Int?) -> String,
        showShoeDescription: ((String) -> Unit)? = null,
    ) {
        componentScope.launch {
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
                setThemeContrast(0f)
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

    fun setThemeContrast(value: Float) {
        componentScope.debounced {
            settingsManager.setThemeContrast(value.toDouble())
        }
    }

    fun setThemeStyle(value: Int) {
        componentScope.debounced {
            settingsManager.setThemeStyle(value)
        }
    }

    fun toggleInvertColors() {
        componentScope.debounced {
            settingsManager.toggleInvertColors()
        }
    }

    fun toggleScreenSearchEnabled() {
        componentScope.debounced {
            settingsManager.toggleScreensSearchEnabled()
        }
    }

    fun toggleDrawAppBarShadows() {
        componentScope.debounced {
            settingsManager.toggleDrawAppBarShadows()
        }
    }

    private fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) {
        componentScope.debounced {
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
        componentScope.debounced {
            settingsManager.setVibrationStrength(strength)
        }
    }

    fun toggleOverwriteFiles() {
        componentScope.debounced {
            settingsManager.toggleOverwriteFiles()
        }
    }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        componentScope.debounced {
            settingsManager.setDefaultImageScaleMode(imageScaleMode)
        }
    }

    fun setSwitchType(type: SwitchType) {
        componentScope.debounced {
            settingsManager.setSwitchType(type)
        }
    }

    fun toggleMagnifierEnabled() {
        componentScope.debounced {
            settingsManager.toggleMagnifierEnabled()
        }
    }

    fun toggleExifWidgetInitialState() {
        componentScope.debounced {
            settingsManager.toggleExifWidgetInitialState()
        }
    }

    fun setScreensWithBrightnessEnforcement(screen: Screen) {
        componentScope.debounced {
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
        componentScope.debounced {
            settingsManager.toggleConfettiEnabled()
        }
    }

    fun toggleSecureMode() {
        componentScope.debounced {
            settingsManager.toggleSecureMode()
        }
    }

    fun toggleUseEmojiAsPrimaryColor() {
        componentScope.debounced {
            settingsManager.toggleUseEmojiAsPrimaryColor()
        }
    }

    fun toggleUseRandomEmojis() {
        componentScope.debounced {
            settingsManager.toggleUseRandomEmojis()
        }
    }

    fun setIconShape(iconShape: Int) {
        componentScope.debounced {
            settingsManager.setIconShape(iconShape)
        }
    }

    fun setDragHandleWidth(width: Int) {
        componentScope.debounced {
            settingsManager.setDragHandleWidth(width)
        }
    }

    fun setConfettiType(type: Int) {
        componentScope.debounced {
            settingsManager.setConfettiType(type)
        }
    }

    fun toggleAllowAutoClipboardPaste() {
        componentScope.debounced {
            settingsManager.toggleAllowAutoClipboardPaste()
        }
    }

    fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) {
        componentScope.debounced {
            settingsManager.setConfettiHarmonizer(colorHarmonizer)
        }
    }

    fun setConfettiHarmonizationLevel(level: Float) {
        componentScope.debounced {
            settingsManager.setConfettiHarmonizationLevel(level)
        }
    }

    fun toggleGeneratePreviews() {
        componentScope.debounced {
            settingsManager.toggleGeneratePreviews()
        }
    }

    fun toggleSkipImagePicking() {
        componentScope.debounced {
            settingsManager.toggleSkipImagePicking()
        }
    }

    fun toggleShowSettingsInLandscape() {
        componentScope.debounced {
            settingsManager.toggleShowSettingsInLandscape()
        }
    }

    fun toggleUseFullscreenSettings() {
        componentScope.debounced {
            settingsManager.toggleUseFullscreenSettings()
        }
    }

    fun setDefaultDrawLineWidth(value: Float) {
        componentScope.debounced {
            settingsManager.setDefaultDrawLineWidth(value)
        }
    }

    fun toggleOpenEditInsteadOfPreview() {
        componentScope.debounced {
            settingsManager.toggleOpenEditInsteadOfPreview()
        }
    }

    fun toggleCanEnterPresetsByTextField() {
        componentScope.debounced {
            settingsManager.toggleCanEnterPresetsByTextField()
        }
    }

    fun setColorBlindScheme(value: Int?) {
        componentScope.debounced {
            settingsManager.setColorBlindType(value)
        }
    }

    fun toggleIsLinksPreviewEnabled() {
        componentScope.debounced {
            settingsManager.toggleIsLinkPreviewEnabled()
        }
    }

    fun setDefaultDrawColor(colorModel: ColorModel) {
        componentScope.debounced {
            settingsManager.setDefaultDrawColor(colorModel)
        }
    }

    fun setDefaultDrawPathMode(mode: Int) {
        componentScope.debounced {
            settingsManager.setDefaultDrawPathMode(mode)
        }
    }

    fun toggleAddTimestampToFilename() {
        componentScope.debounced {
            settingsManager.toggleAddTimestampToFilename()
        }
    }

    fun toggleUseFormattedFilenameTimestamp() {
        componentScope.debounced {
            settingsManager.toggleUseFormattedFilenameTimestamp()
        }
    }

    fun setDefaultResizeType(resizeType: ResizeType) {
        componentScope.debounced {
            settingsManager.setDefaultResizeType(resizeType)
        }
    }

    fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility) {
        componentScope.debounced {
            settingsManager.setSystemBarsVisibility(systemBarsVisibility)
        }
    }

    fun toggleIsSystemBarsVisibleBySwipe() {
        componentScope.debounced {
            settingsManager.toggleIsSystemBarsVisibleBySwipe()
        }
    }

    fun toggleUseCompactSelectors() {
        componentScope.debounced {
            settingsManager.toggleUseCompactSelectorsLayout()
        }
    }

    fun setMainScreenTitle(title: String) {
        componentScope.debounced {
            settingsManager.setMainScreenTitle(title)
        }
    }

    fun setSliderType(sliderType: SliderType) {
        componentScope.debounced {
            settingsManager.setSliderType(sliderType)
        }
    }

    fun toggleIsCenterAlignDialogButtons() {
        componentScope.debounced {
            settingsManager.toggleIsCenterAlignDialogButtons()
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): SettingsComponent
    }
}