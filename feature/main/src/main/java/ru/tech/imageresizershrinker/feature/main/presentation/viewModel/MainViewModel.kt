@file:Suppress("SameParameterValue")

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
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.domain.model.FontFam
import ru.tech.imageresizershrinker.core.domain.model.NightMode
import ru.tech.imageresizershrinker.core.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateFlowUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateUseCase
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import java.io.OutputStream
import java.net.URL
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

@HiltViewModel
class MainViewModel @Inject constructor(
    getSettingsStateFlowUseCase: GetSettingsStateFlowUseCase,
    val imageLoader: ImageLoader,
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val getSettingsStateUseCase: GetSettingsStateUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val navController = navController<Screen>(Screen.Main)

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _hasPdfUri = mutableStateOf<Uri?>(null)
    val hasPdfUri by _hasPdfUri

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
            _settingsState.value = getSettingsStateUseCase()
        }
        getSettingsStateFlowUseCase().onEach {
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

        val showDialog = settingsState.showDialogOnStartup
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

                            if (isNeedUpdate(
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

    private fun isNeedUpdate(currentName: String, updateName: String): Boolean {
        fun String.toVersionCode(): Int {
            return replace(
                regex = Regex("0\\d"),
                transform = {
                    it.value.replace("0", "")
                }
            ).replace("-", "")
                .replace(".", "")
                .replace("_", "")
                .replace("alpha", "1")
                .replace("beta", "2")
                .replace("rc", "3")
                .replace("foss", "")
                .replace("jxl", "")
                .toIntOrNull() ?: -1
        }

        val betaList = listOf(
            "alpha", "beta", "rc"
        )

        val updateVersionCode = updateName.toVersionCode()
        val currentVersionCode = currentName.toVersionCode()
        return if (!updateName.startsWith(currentName)) {
            if (betaList.all { it !in updateName }) {
                updateVersionCode > currentVersionCode
            } else {
                if (settingsState.allowBetas || betaList.any { it in currentName }) {
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

        if (uris != null) _showSelectDialog.value = true
    }

    fun updateHasPdfUri(uri: Uri?) {
        _hasPdfUri.value = uri

        if (uri != null) _showSelectDialog.value = true
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

    fun createBackup(outputStream: OutputStream?, onSuccess: () -> Unit) {
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
                settingsRepository.restoreFromBackupFile(uri.toString(), onSuccess, onFailure)
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

    fun addColorTupleFromEmoji(getEmoji: (Int?) -> String, showShoeDescription: (String) -> Unit) {
        viewModelScope.launch {
            val emojiUri = getEmoji(settingsState.selectedEmoji)
            if (emojiUri.contains("shoe", true)) {
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
                if (settingsState.isInvertThemeColors) toggleInvertColors()
            } else {
                imageManager.getImage(data = emojiUri)
                    ?.extractPrimaryColor()
                    ?.let { primary ->
                        val colorTuple = ColorTuple(primary)
                        val colorTupleS = listOf(colorTuple).asString()
                        setColorTuple(colorTuple)
                        settingsRepository.setColorTuples(settingsState.colorTupleList + "*" + colorTupleS)
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

}