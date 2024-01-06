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
import ru.tech.imageresizershrinker.core.domain.model.FontFam
import ru.tech.imageresizershrinker.core.domain.model.NightMode
import ru.tech.imageresizershrinker.core.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.use_case.backup_and_restore.CreateBackupFileUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.backup_and_restore.CreateBackupFilenameUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.backup_and_restore.RestoreFromBackupFileUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.RegisterAppOpenUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetAlignmentUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetBorderWidthUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetColorTupleUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetColorTuplesUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetDefaultImageScaleModeUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetEmojiUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetEmojisCountUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetFilenamePrefixUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetFilenameSuffixUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetFontScaleUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetFontUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetImagePickerModeUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetNightModeUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetPresetsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetSaveFolderUriUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetScreenOrderUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetThemeContrastUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetThemeStyleUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.SetVibrationStrengthUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAddFileSizeUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAddOriginalFilenameUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAddSequenceNumberUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAllowBetasUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAllowCollectAnalyticsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAllowCollectCrashlyticsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAllowImageMonetUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAmoledModeUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleAutoPinClipboardUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleClearCacheOnLaunchUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawAppBarShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawButtonShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawContainerShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawFabShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawSliderShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDrawSwitchShadowsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleDynamicColorsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleGroupOptionsByTypesUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleInvertColorsUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleLockDrawOrientationUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleOverwriteFilesUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleRandomizeFilenameUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleScreensSearchEnabledUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.edit_settings.ToggleShowDialogUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateFlowUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.get_settings_state.GetSettingsStateUseCase
import ru.tech.imageresizershrinker.core.domain.use_case.reset_settings.ResetSettingsUseCase
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
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
    private val toggleAddSequenceNumberUseCase: ToggleAddSequenceNumberUseCase,
    private val toggleAddOriginalFilenameUseCase: ToggleAddOriginalFilenameUseCase,
    private val setEmojisCountUseCase: SetEmojisCountUseCase,
    private val setImagePickerModeUseCase: SetImagePickerModeUseCase,
    private val toggleAddFileSizeUseCase: ToggleAddFileSizeUseCase,
    private val setEmojiUseCase: SetEmojiUseCase,
    private val setFilenamePrefixUseCase: SetFilenamePrefixUseCase,
    private val toggleShowDialogUseCase: ToggleShowDialogUseCase,
    private val setColorTupleUseCase: SetColorTupleUseCase,
    private val setPresetsUseCase: SetPresetsUseCase,
    private val toggleDynamicColorsUseCase: ToggleDynamicColorsUseCase,
    private val setBorderWidthUseCase: SetBorderWidthUseCase,
    private val toggleAllowImageMonetUseCase: ToggleAllowImageMonetUseCase,
    private val toggleAmoledModeUseCase: ToggleAmoledModeUseCase,
    private val setNightModeUseCase: SetNightModeUseCase,
    private val setSaveFolderUriUseCase: SetSaveFolderUriUseCase,
    private val setColorTuplesUseCase: SetColorTuplesUseCase,
    private val setAlignmentUseCase: SetAlignmentUseCase,
    private val setScreenOrderUseCase: SetScreenOrderUseCase,
    private val toggleClearCacheOnLaunchUseCase: ToggleClearCacheOnLaunchUseCase,
    private val toggleGroupOptionsByTypesUseCase: ToggleGroupOptionsByTypesUseCase,
    private val toggleRandomizeFilenameUseCase: ToggleRandomizeFilenameUseCase,
    private val createBackupFileUseCase: CreateBackupFileUseCase,
    private val restoreFromBackupFileUseCase: RestoreFromBackupFileUseCase,
    private val resetSettingsUseCase: ResetSettingsUseCase,
    private val createBackupFilenameUseCase: CreateBackupFilenameUseCase,
    private val setFontUseCase: SetFontUseCase,
    private val setFontScaleUseCase: SetFontScaleUseCase,
    private val toggleAllowCollectCrashlyticsUseCase: ToggleAllowCollectCrashlyticsUseCase,
    private val toggleAllowCollectAnalyticsUseCase: ToggleAllowCollectAnalyticsUseCase,
    private val toggleAllowBetasUseCase: ToggleAllowBetasUseCase,
    private val toggleDrawContainerShadowsUseCase: ToggleDrawContainerShadowsUseCase,
    private val toggleDrawButtonShadowsUseCase: ToggleDrawButtonShadowsUseCase,
    private val toggleDrawFabShadowsUseCase: ToggleDrawFabShadowsUseCase,
    private val toggleDrawSliderShadowsUseCase: ToggleDrawSliderShadowsUseCase,
    private val toggleDrawSwitchShadowsUseCase: ToggleDrawSwitchShadowsUseCase,
    private val registerAppOpenUseCase: RegisterAppOpenUseCase,
    private val toggleLockDrawOrientationUseCase: ToggleLockDrawOrientationUseCase,
    private val setThemeContrastUseCase: SetThemeContrastUseCase,
    private val setThemeStyleUseCase: SetThemeStyleUseCase,
    private val toggleInvertColorsUseCase: ToggleInvertColorsUseCase,
    private val toggleScreensSearchEnabledUseCase: ToggleScreensSearchEnabledUseCase,
    private val toggleDrawAppBarShadowsUseCase: ToggleDrawAppBarShadowsUseCase,
    private val toggleAutoPinClipboardUseCase: ToggleAutoPinClipboardUseCase,
    private val setVibrationStrengthUseCase: SetVibrationStrengthUseCase,
    private val toggleOverwriteFilesUseCase: ToggleOverwriteFilesUseCase,
    private val setFilenameSuffixUseCase: SetFilenameSuffixUseCase,
    private val setDefaultImageScaleModeUseCase: SetDefaultImageScaleModeUseCase
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default())
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
            registerAppOpenUseCase()
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
            toggleAddSequenceNumberUseCase()
        }
    }

    fun toggleAddOriginalFilename() {
        viewModelScope.launch {
            toggleAddOriginalFilenameUseCase()
        }
    }

    fun setEmojisCount(count: Int) {
        viewModelScope.launch {
            setEmojisCountUseCase(count)
        }
    }

    fun setImagePickerMode(mode: Int) {
        viewModelScope.launch {
            setImagePickerModeUseCase(mode)
        }
    }

    fun toggleAddFileSize() {
        viewModelScope.launch {
            toggleAddFileSizeUseCase()
        }
    }

    fun setEmoji(emoji: Int) {
        viewModelScope.launch {
            setEmojiUseCase(emoji)
        }
    }

    fun setFilenamePrefix(name: String) {
        viewModelScope.launch {
            setFilenamePrefixUseCase(name)
        }
    }

    fun setFilenameSuffix(name: String) {
        viewModelScope.launch {
            setFilenameSuffixUseCase(name)
        }
    }

    fun toggleShowUpdateDialog() {
        viewModelScope.launch {
            toggleShowDialogUseCase()
        }
    }

    fun setColorTuple(colorTuple: ColorTuple) {
        viewModelScope.launch {
            setColorTupleUseCase(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun setPresets(newPresets: List<Int>) {
        viewModelScope.launch {
            setPresetsUseCase(
                newPresets.joinToString("*")
            )
        }
    }

    fun toggleDynamicColors() {
        viewModelScope.launch {
            toggleDynamicColorsUseCase()
        }
    }

    fun toggleLockDrawOrientation() {
        viewModelScope.launch {
            toggleLockDrawOrientationUseCase()
        }
    }

    fun setBorderWidth(width: Float) {
        viewModelScope.launch {
            setBorderWidthUseCase(width)
        }
    }

    fun toggleAllowImageMonet() {
        viewModelScope.launch {
            toggleAllowImageMonetUseCase()
        }
    }

    fun toggleAmoledMode() {
        viewModelScope.launch {
            toggleAmoledModeUseCase()
        }
    }

    fun setNightMode(nightMode: NightMode) {
        viewModelScope.launch {
            setNightModeUseCase(nightMode)
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
            setSaveFolderUriUseCase(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        viewModelScope.launch {
            setColorTuplesUseCase(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        viewModelScope.launch {
            setAlignmentUseCase(align.toInt())
        }
    }

    fun updateOrder(data: List<Screen>) {
        viewModelScope.launch {
            setScreenOrderUseCase(data.joinToString("/") { it.id.toString() })
        }
    }

    fun toggleClearCacheOnLaunch() {
        viewModelScope.launch {
            toggleClearCacheOnLaunchUseCase()
        }
    }

    fun toggleGroupOptionsByType() {
        viewModelScope.launch {
            toggleGroupOptionsByTypesUseCase()
        }
    }

    fun toggleRandomizeFilename() {
        viewModelScope.launch {
            toggleRandomizeFilenameUseCase()
        }
    }

    fun createBackup(outputStream: OutputStream?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            outputStream?.use {
                it.write(createBackupFileUseCase())
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
                restoreFromBackupFileUseCase(uri.toString(), onSuccess, onFailure)
            }
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            resetSettingsUseCase()
        }
    }

    fun createBackupFilename(): String = createBackupFilenameUseCase()

    fun setFont(font: FontFam) {
        viewModelScope.launch {
            setFontUseCase(font)
        }
    }

    fun onUpdateFontScale(scale: Float) {
        viewModelScope.launch {
            setFontScaleUseCase(scale)
        }
    }

    fun toggleAllowCollectCrashlytics() {
        viewModelScope.launch {
            toggleAllowCollectCrashlyticsUseCase()
        }
    }

    fun toggleAllowCollectAnalytics() {
        viewModelScope.launch {
            toggleAllowCollectAnalyticsUseCase()
        }
    }

    fun toggleAllowBetas(installedFromMarket: Boolean) {
        viewModelScope.launch {
            toggleAllowBetasUseCase()
            tryGetUpdate(
                newRequest = true,
                installedFromMarket = installedFromMarket
            )
        }
    }

    fun toggleDrawContainerShadows() {
        viewModelScope.launch {
            toggleDrawContainerShadowsUseCase()
        }
    }

    fun toggleDrawSwitchShadows() {
        viewModelScope.launch {
            toggleDrawSwitchShadowsUseCase()
        }
    }

    fun toggleDrawSliderShadows() {
        viewModelScope.launch {
            toggleDrawSliderShadowsUseCase()
        }
    }

    fun toggleDrawButtonShadows() {
        viewModelScope.launch {
            toggleDrawButtonShadowsUseCase()
        }
    }

    fun toggleDrawFabShadows() {
        viewModelScope.launch {
            toggleDrawFabShadowsUseCase()
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
                setColorTuplesUseCase(settingsState.colorTupleList + "*" + colorTupleS)
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
                        setColorTuplesUseCase(settingsState.colorTupleList + "*" + colorTupleS)
                    }
            }
            if (settingsState.isDynamicColors) toggleDynamicColors()
        }
    }

    fun updateThemeContrast(value: Float) {
        viewModelScope.launch {
            setThemeContrastUseCase(value.toDouble())
        }
    }

    fun setThemeStyle(value: Int) {
        viewModelScope.launch {
            setThemeStyleUseCase(value)
        }
    }

    fun toggleInvertColors() {
        viewModelScope.launch {
            toggleInvertColorsUseCase()
        }
    }

    fun toggleScreenSearchEnabled() {
        viewModelScope.launch {
            toggleScreensSearchEnabledUseCase()
        }
    }

    fun toggleDrawAppBarShadows() {
        viewModelScope.launch {
            toggleDrawAppBarShadowsUseCase()
        }
    }

    fun toggleAutoPinClipboard() {
        viewModelScope.launch {
            toggleAutoPinClipboardUseCase()
        }
    }

    fun setVibrationStrength(strength: Int) {
        viewModelScope.launch {
            setVibrationStrengthUseCase(strength)
        }
    }

    fun toggleOverwriteFiles() {
        viewModelScope.launch {
            toggleOverwriteFilesUseCase()
        }
    }

    fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        viewModelScope.launch {
            setDefaultImageScaleModeUseCase(imageScaleMode)
        }
    }

}