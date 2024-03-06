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

@file:Suppress("SameParameterValue", "UNUSED_PARAMETER")

package ru.tech.imageresizershrinker.feature.main.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.logger.makeLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import ru.tech.imageresizershrinker.core.domain.APP_RELEASES
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.settings.domain.SettingsRepository
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.FontFam
import ru.tech.imageresizershrinker.core.settings.domain.model.Harmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import java.io.OutputStream
import java.net.URL
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

@HiltViewModel
class MainViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val navController = navController<Screen>(Screen.Main)

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _extraImageType = mutableStateOf<String?>(null)
    val extraImageType by _extraImageType

    private val _showSelectDialog = mutableStateOf(false)
    val showSelectDialog by _showSelectDialog

    private val _showUpdateDialog = mutableStateOf(false)
    val showUpdateDialog by _showUpdateDialog

    private val _updateAvailable = mutableStateOf(false)
    val updateAvailable by _updateAvailable

    private val _cancelledUpdate = mutableStateOf(false)

    private val _shouldShowDialog = mutableStateOf(true)
    val shouldShowDialog by _shouldShowDialog

    private val _tag = mutableStateOf("")
    val tag by _tag

    private val _changelog = mutableStateOf("")
    val changelog by _changelog

    val toastHostState = ToastHostState()

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

    fun setPresets(newPresets: List<Int>) {
        viewModelScope.launch {
            settingsRepository.setPresets(
                newPresets.joinToString("*")
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

    fun cancelledUpdate(showAgain: Boolean = false) {
        if (!showAgain) _cancelledUpdate.value = true
        _showUpdateDialog.value = false
    }

    fun tryGetUpdate(
        newRequest: Boolean = false,
        installedFromMarket: Boolean,
        onNoUpdates: () -> Unit = {}
    ) {
        if (settingsState.appOpenCount < 2 && !newRequest) return

        val showDialog = settingsState.showUpdateDialogOnStartup
        if (installedFromMarket) {
            if (showDialog) {
                _showUpdateDialog.value = newRequest
            }
        } else {
            if (!_cancelledUpdate.value || newRequest) {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        kotlin.runCatching {
                            val nodes =
                                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                                    URL("$APP_RELEASES.atom").openConnection().getInputStream()
                                )?.getElementsByTagName("feed")

                            if (nodes != null) {
                                for (i in 0 until nodes.length) {
                                    val element = nodes.item(i) as Element
                                    val title = element.getElementsByTagName("entry")
                                    val line = (title.item(0) as Element)
                                    _tag.value = (line.getElementsByTagName("title")
                                        .item(0) as Element).textContent
                                    _changelog.value = (line.getElementsByTagName("content")
                                        .item(0) as Element).textContent
                                }
                            }

                            if (
                                isNeedUpdate(
                                    currentName = BuildConfig.VERSION_NAME,
                                    updateName = tag
                                )
                            ) {
                                _updateAvailable.value = true
                                if (showDialog) {
                                    _showUpdateDialog.value = true
                                }
                            } else {
                                onNoUpdates()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isNeedUpdateTest() {
        if (BuildConfig.DEBUG) {
            val updateVersions = listOf(
                "2.6.0",
                "2.7.0",
                "2.6.0-rc1",
                "2.6.0-rc01",
                "3.0.0",
                "2.6.0-beta02",
                BuildConfig.VERSION_NAME,
                "2.6.1",
                "2.6.1-alpha01",
                "2.6.0-rc02",
                "2.5.1"
            )
            val currentVersions = listOf(
                BuildConfig.VERSION_NAME,
                "2.6.0-beta03",
                "2.5.0",
                "2.5.1",
                "2.6.0",
                "2.6.2"
            )
            val allowBetas = listOf(false, true)

            allowBetas.forEach { betas ->
                currentVersions.forEach { current ->
                    updateVersions.forEach { update ->
                        val needUpdate = isNeedUpdate(
                            currentName = current,
                            updateName = update,
                            allowBetas = betas
                        )
                        "$current -> $update = $needUpdate, for betaAllowed = $betas".makeLog("Test_Updates")
                    }
                }
            }
        }
    }

    private fun isNeedUpdate(
        currentName: String,
        updateName: String,
        allowBetas: Boolean = settingsState.allowBetas
    ): Boolean {

        val betaList = listOf(
            "alpha", "beta", "rc"
        )

        fun String.toVersionCodeString(): String {
            return replace(
                regex = Regex("0\\d"),
                transform = {
                    it.value.replace("0", "")
                }
            ).replace("-", "")
                .replace(".", "")
                .replace("_", "")
                .let { version ->
                    if (betaList.any { it in version }) version
                    else version + "4"
                }
                .replace("alpha", "1")
                .replace("beta", "2")
                .replace("rc", "3")
                .replace("foss", "")
                .replace("jxl", "")
        }

        val currentVersionCodeString = currentName.toVersionCodeString()
        val updateVersionCodeString = updateName.toVersionCodeString()

        val maxLength = maxOf(currentVersionCodeString.length, updateVersionCodeString.length)

        val currentVersionCode = currentVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1
        val updateVersionCode = updateVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1

        return if (!updateName.startsWith(currentName)) {
            if (betaList.all { it !in updateName }) {
                updateVersionCode > currentVersionCode
            } else {
                if (allowBetas || betaList.any { it in currentName }) {
                    updateVersionCode > currentVersionCode
                } else false
            }
        } else false
    }

    fun hideSelectDialog() {
        _showSelectDialog.value = false
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = uris

        if (!uris.isNullOrEmpty()) _showSelectDialog.value = true
    }

    fun updateExtraImageType(type: String?) {
        _extraImageType.value = type
    }

    fun showToast(
        message: String,
        icon: ImageVector? = null,
    ) {
        viewModelScope.launch {
            toastHostState.showToast(
                message = message, icon = icon
            )
        }
    }

    fun shouldShowExitDialog(b: Boolean) {
        _shouldShowDialog.value = b
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
        viewModelScope.launch {
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
            withContext(Dispatchers.IO) {
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

    fun setFont(font: FontFam) {
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

    fun toggleAllowBetas(installedFromMarket: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleAllowBetas()
            tryGetUpdate(
                newRequest = true,
                installedFromMarket = installedFromMarket
            )
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

    fun getColorTupleFromEmoji(
        emojiUri: String,
        callback: (ColorTuple?) -> Unit
    ) {
        viewModelScope.launch {
            callback(
                imageGetter
                    .getImage(data = emojiUri)
                    ?.extractPrimaryColor()
                    ?.let { ColorTuple(it) }
            )
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
                setFont(FontFam.DejaVu)
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

    fun toggleUsePixelSwitch() {
        viewModelScope.launch {
            settingsRepository.toggleUsePixelSwitch()
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

    private val _showGithubReviewSheet = mutableStateOf(false)
    val showGithubReviewSheet by _showGithubReviewSheet

    fun onWantGithubReview() {
        _showGithubReviewSheet.update { true }
    }

    fun hideReviewSheet() {
        _showGithubReviewSheet.update { false }
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

    fun toggleConfettiEnabled(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleConfettiEnabled()
        }
    }

    fun toggleSecureMode(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.toggleSecureMode()
        }
    }

    fun toggleUseEmojiAsPrimaryColor() {
        viewModelScope.launch {
            settingsRepository.toggleUseEmojiAsPrimaryColor()
        }
    }

    fun toggleUseRandomEmojis(value: Boolean) {
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

    fun setConfettiHarmonizer(harmonizer: Harmonizer) {
        viewModelScope.launch {
            settingsRepository.setConfettiHarmonizer(harmonizer)
        }
    }

    fun setConfettiHarmonizationLevel(level: Float) {
        viewModelScope.launch {
            settingsRepository.setConfettiHarmonizationLevel(level)
        }
    }

}