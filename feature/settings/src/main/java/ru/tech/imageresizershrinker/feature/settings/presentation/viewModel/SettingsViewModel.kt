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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.di.IoDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.settings.domain.SettingsRepository
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val settingsRepository: SettingsRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    init {
        if (settingsState.clearCacheOnLaunch) clearCache()

        runBlocking {
            settingsRepository.registerAppOpen()
            _settingsState.value = settingsRepository.getSettingsState()
        }
        settingsRepository.getSettingsStateFlow().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
    }

    fun getReadableCacheSize(): String = fileController.getReadableCacheSize()

    fun clearCache(onComplete: (String) -> Unit = {}) = fileController.clearCache(onComplete)

    fun toggleAddSequenceNumber() {
        viewModelScope.launch {
            settingsRepository.toggleAddSequenceNumber()
        }
    }

    fun toggleAddOriginalFilename() {
        viewModelScope.launch {
            settingsRepository.toggleAddOriginalFilename()
        }
    }

    fun setEmojisCount(count: Int) {
        viewModelScope.launch {
            settingsRepository.setEmojisCount(count)
        }
    }

    fun setImagePickerMode(mode: Int) {
        viewModelScope.launch {
            settingsRepository.setImagePickerMode(mode)
        }
    }

    fun toggleAddFileSize() {
        viewModelScope.launch {
            settingsRepository.toggleAddFileSize()
        }
    }

    fun setEmoji(emoji: Int) {
        viewModelScope.launch {
            settingsRepository.setEmoji(emoji)
        }
    }

    fun setFilenamePrefix(name: String) {
        viewModelScope.launch {
            settingsRepository.setFilenamePrefix(name)
        }
    }

    fun setFilenameSuffix(name: String) {
        viewModelScope.launch {
            settingsRepository.setFilenameSuffix(name)
        }
    }

    fun toggleShowUpdateDialog() {
        viewModelScope.launch {
            settingsRepository.toggleShowDialog()
        }
    }

    fun setColorTuple(colorTuple: ColorTuple) {
        viewModelScope.launch {
            settingsRepository.setColorTuple(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun toggleDynamicColors() {
        viewModelScope.launch {
            settingsRepository.toggleDynamicColors()
        }
    }

    fun toggleLockDrawOrientation() {
        viewModelScope.launch {
            settingsRepository.toggleLockDrawOrientation()
        }
    }

    fun setBorderWidth(width: Float) {
        viewModelScope.launch {
            settingsRepository.setBorderWidth(width)
        }
    }

    fun toggleAllowImageMonet() {
        viewModelScope.launch {
            settingsRepository.toggleAllowImageMonet()
        }
    }

    fun toggleAmoledMode() {
        viewModelScope.launch {
            settingsRepository.toggleAmoledMode()
        }
    }

    fun setNightMode(nightMode: NightMode) {
        viewModelScope.launch {
            settingsRepository.setNightMode(nightMode)
        }
    }

    fun updateSaveFolderUri(uri: Uri?) {
        viewModelScope.launch {
            settingsRepository.setSaveFolderUri(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        viewModelScope.launch {
            settingsRepository.setColorTuples(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        viewModelScope.launch {
            settingsRepository.setAlignment(align.toInt())
        }
    }

    fun updateOrder(data: List<Screen>) {
        viewModelScope.launch {
            settingsRepository.setScreenOrder(data.joinToString("/") { it.id.toString() })
        }
    }

    fun toggleClearCacheOnLaunch() {
        viewModelScope.launch {
            settingsRepository.toggleClearCacheOnLaunch()
        }
    }

    fun toggleGroupOptionsByType() {
        viewModelScope.launch {
            settingsRepository.toggleGroupOptionsByTypes()
        }
    }

    fun toggleRandomizeFilename() {
        viewModelScope.launch {
            settingsRepository.toggleRandomizeFilename()
        }
    }

    fun createBackup(
        outputStream: OutputStream?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            outputStream?.use {
                it.write(settingsRepository.createBackupFile())
            }
            onSuccess()
        }
    }

    fun restoreBackupFrom(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                settingsRepository.restoreFromBackupFile(
                    backupFileUri = uri.toString(),
                    onSuccess = onSuccess,
                    onFailure = onFailure
                )
            }
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsRepository.resetSettings()
        }
    }

    fun createBackupFilename(): String = settingsRepository.createBackupFilename()

    fun setFont(font: DomainFontFamily) {
        viewModelScope.launch {
            settingsRepository.setFont(font)
        }
    }

    fun onUpdateFontScale(scale: Float) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale)
        }
    }

    fun toggleAllowCollectCrashlytics() {
        viewModelScope.launch {
            settingsRepository.toggleAllowCrashlytics()
        }
    }

    fun toggleAllowCollectAnalytics() {
        viewModelScope.launch {
            settingsRepository.toggleAllowAnalytics()
        }
    }

    fun toggleAllowBetas() {
        viewModelScope.launch {
            settingsRepository.toggleAllowBetas()
        }
    }

    fun toggleDrawContainerShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawContainerShadows()
        }
    }

    fun toggleDrawSwitchShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawSwitchShadows()
        }
    }

    fun toggleDrawSliderShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawSliderShadows()
        }
    }

    fun toggleDrawButtonShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawButtonShadows()
        }
    }

    fun toggleDrawFabShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawFabShadows()
        }
    }

    fun addColorTupleFromEmoji(
        getEmoji: (Int?) -> String,
        showShoeDescription: ((String) -> Unit)? = null
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
                settingsRepository.setColorTuples(settingsState.colorTupleList + "*" + colorTupleS)
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
                        settingsRepository.setColorTuples(
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
            settingsRepository.setThemeContrast(value.toDouble())
        }
    }

    fun setThemeStyle(value: Int) {
        viewModelScope.launch {
            settingsRepository.setThemeStyle(value)
        }
    }

    fun toggleInvertColors() {
        viewModelScope.launch {
            settingsRepository.toggleInvertColors()
        }
    }

    fun toggleScreenSearchEnabled() {
        viewModelScope.launch {
            settingsRepository.toggleScreensSearchEnabled()
        }
    }

    fun toggleDrawAppBarShadows() {
        viewModelScope.launch {
            settingsRepository.toggleDrawAppBarShadows()
        }
    }

    fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) {
        viewModelScope.launch {
            settingsRepository.setCopyToClipboardMode(copyToClipboardMode)
        }
    }

    fun setVibrationStrength(strength: Int) {
        viewModelScope.launch {
            settingsRepository.setVibrationStrength(strength)
        }
    }

    fun toggleOverwriteFiles() {
        viewModelScope.launch {
            settingsRepository.toggleOverwriteFiles()
        }
    }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        viewModelScope.launch {
            settingsRepository.setDefaultImageScaleMode(imageScaleMode)
        }
    }

    fun setSwitchType(type: SwitchType) {
        viewModelScope.launch {
            settingsRepository.setSwitchType(type)
        }
    }

    fun toggleMagnifierEnabled() {
        viewModelScope.launch {
            settingsRepository.toggleMagnifierEnabled()
        }
    }

    fun toggleExifWidgetInitialState() {
        viewModelScope.launch {
            settingsRepository.toggleExifWidgetInitialState()
        }
    }

    fun updateBrightnessEnforcementScreens(screen: Screen) {
        viewModelScope.launch {
            val screens = settingsState.screenListWithMaxBrightnessEnforcement.let {
                if (screen.id in it) it - screen.id
                else it + screen.id
            }

            settingsRepository.setScreensWithBrightnessEnforcement(
                screens.joinToString("/") { it.toString() }
            )
        }
    }

    fun toggleConfettiEnabled() {
        viewModelScope.launch {
            settingsRepository.toggleConfettiEnabled()
        }
    }

    fun toggleSecureMode() {
        viewModelScope.launch {
            settingsRepository.toggleSecureMode()
        }
    }

    fun toggleUseEmojiAsPrimaryColor() {
        viewModelScope.launch {
            settingsRepository.toggleUseEmojiAsPrimaryColor()
        }
    }

    fun toggleUseRandomEmojis() {
        viewModelScope.launch {
            settingsRepository.toggleUseRandomEmojis()
        }
    }

    fun setIconShape(iconShape: Int) {
        viewModelScope.launch {
            settingsRepository.setIconShape(iconShape)
        }
    }

    fun setDragHandleWidth(width: Int) {
        viewModelScope.launch {
            settingsRepository.setDragHandleWidth(width)
        }
    }

    fun setConfettiType(type: Int) {
        viewModelScope.launch {
            settingsRepository.setConfettiType(type)
        }
    }

    fun toggleAllowAutoClipboardPaste() {
        viewModelScope.launch {
            settingsRepository.toggleAllowAutoClipboardPaste()
        }
    }

    fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) {
        viewModelScope.launch {
            settingsRepository.setConfettiHarmonizer(colorHarmonizer)
        }
    }

    fun setConfettiHarmonizationLevel(level: Float) {
        viewModelScope.launch {
            settingsRepository.setConfettiHarmonizationLevel(level)
        }
    }

    fun toggleGeneratePreviews() {
        viewModelScope.launch {
            settingsRepository.toggleGeneratePreviews()
        }
    }

    fun toggleSkipImagePicking() {
        viewModelScope.launch {
            settingsRepository.toggleSkipImagePicking()
        }
    }

    fun toggleShowSettingsInLandscape() {
        viewModelScope.launch {
            settingsRepository.toggleShowSettingsInLandscape()
        }
    }

    fun toggleUseFullscreenSettings() {
        viewModelScope.launch {
            settingsRepository.toggleUseFullscreenSettings()
        }
    }

    fun setDefaultDrawLineWidth(value: Float) {
        viewModelScope.launch {
            settingsRepository.setDefaultDrawLineWidth(value)
        }
    }

}