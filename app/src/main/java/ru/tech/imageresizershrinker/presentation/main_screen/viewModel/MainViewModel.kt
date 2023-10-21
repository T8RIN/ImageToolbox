@file:Suppress("SameParameterValue")

package ru.tech.imageresizershrinker.presentation.main_screen.viewModel

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
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.core.APP_RELEASES
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.domain.model.NightMode
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.use_case.backup_and_restore.CreateBackupFileUseCase
import ru.tech.imageresizershrinker.domain.use_case.backup_and_restore.CreateBackupFilenameUseCase
import ru.tech.imageresizershrinker.domain.use_case.backup_and_restore.RestoreFromBackupFileUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.AllowShowingShadowsInsteadOfBordersUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.RegisterAppOpenUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetAlignmentUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetBorderWidthUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetFontScaleUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetFontUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetNightModeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddFileSizeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddOriginalFilenameUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddSequenceNumberUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAllowBetasUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAllowCollectAnalyticsUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAllowCollectCrashlyticsUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAllowImageMonetUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAmoledModeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleClearCacheOnLaunchUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleDynamicColorsUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleGroupOptionsByTypesUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleLockDrawOrientationUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleRandomizeFilenameUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleShowDialogUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateColorTupleUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateColorTuplesUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateEmojiUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateEmojisCountUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateFilenameUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateImagePickerModeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateOrderUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdatePresetsUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.UpdateSaveFolderUriUseCase
import ru.tech.imageresizershrinker.domain.use_case.get_settings_state.GetSettingsStateFlowUseCase
import ru.tech.imageresizershrinker.domain.use_case.get_settings_state.GetSettingsStateUseCase
import ru.tech.imageresizershrinker.domain.use_case.reset_settings.ResetSettingsUseCase
import ru.tech.imageresizershrinker.presentation.root.utils.exception.GlobalExceptionHandler
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState
import java.io.OutputStream
import java.net.URL
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

@HiltViewModel
class MainViewModel @Inject constructor(
    private val imageManager: ImageManager<Bitmap, ExifInterface>,
    private val fileController: FileController,
    private val getSettingsStateUseCase: GetSettingsStateUseCase,
    getSettingsStateFlowUseCase: GetSettingsStateFlowUseCase,
    private val toggleAddSequenceNumberUseCase: ToggleAddSequenceNumberUseCase,
    private val toggleAddOriginalFilenameUseCase: ToggleAddOriginalFilenameUseCase,
    private val updateEmojisCountUseCase: UpdateEmojisCountUseCase,
    private val updateImagePickerModeUseCase: UpdateImagePickerModeUseCase,
    private val toggleAddFileSizeUseCase: ToggleAddFileSizeUseCase,
    private val updateEmojiUseCase: UpdateEmojiUseCase,
    private val updateFilenameUseCase: UpdateFilenameUseCase,
    private val toggleShowDialogUseCase: ToggleShowDialogUseCase,
    private val updateColorTupleUseCase: UpdateColorTupleUseCase,
    private val updatePresetsUseCase: UpdatePresetsUseCase,
    private val toggleDynamicColorsUseCase: ToggleDynamicColorsUseCase,
    private val setBorderWidthUseCase: SetBorderWidthUseCase,
    private val toggleAllowImageMonetUseCase: ToggleAllowImageMonetUseCase,
    private val toggleAmoledModeUseCase: ToggleAmoledModeUseCase,
    private val setNightModeUseCase: SetNightModeUseCase,
    private val updateSaveFolderUriUseCase: UpdateSaveFolderUriUseCase,
    private val updateColorTuplesUseCase: UpdateColorTuplesUseCase,
    private val setAlignmentUseCase: SetAlignmentUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
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
    private val allowShowingShadowsInsteadOfBordersUseCase: AllowShowingShadowsInsteadOfBordersUseCase,
    private val registerAppOpenUseCase: RegisterAppOpenUseCase,
    private val toggleLockDrawOrientationUseCase: ToggleLockDrawOrientationUseCase,
    val imageLoader: ImageLoader
) : ViewModel() {

    private val _settingsState = mutableStateOf(SettingsState.Default())
    val settingsState: SettingsState by _settingsState

    val navController = navController<Screen>(Screen.Main)

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

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

    fun updateEmojisCount(count: Int) {
        viewModelScope.launch {
            updateEmojisCountUseCase(count)
        }
    }

    fun updateImagePickerMode(mode: Int) {
        viewModelScope.launch {
            updateImagePickerModeUseCase(mode)
        }
    }

    fun toggleAddFileSize() {
        viewModelScope.launch {
            toggleAddFileSizeUseCase()
        }
    }

    fun updateEmoji(emoji: Int) {
        viewModelScope.launch {
            updateEmojiUseCase(emoji)
        }
    }

    fun updateFilename(name: String) {
        viewModelScope.launch {
            updateFilenameUseCase(name)
        }
    }

    fun toggleShowUpdateDialog() {
        viewModelScope.launch {
            toggleShowDialogUseCase()
        }
    }

    fun updateColorTuple(colorTuple: ColorTuple) {
        viewModelScope.launch {
            updateColorTupleUseCase(
                colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            )
        }
    }

    fun updatePresets(newPresets: List<Int>) {
        viewModelScope.launch {
            updatePresetsUseCase(
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

                            if (isNeedUpdate(nameFrom = BuildConfig.VERSION_NAME, nameTo = tag)) {
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

    private fun isNeedUpdate(nameFrom: String, nameTo: String): Boolean {
        fun String.toVersionCode(): Int {
            return replace("-", "")
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
            "beta", "alpha", "rc"
        )

        val tagVC = nameTo.toVersionCode()
        val buildVC = nameFrom.toVersionCode()
        return if (betaList.all { it !in nameTo }) {
            tagVC > buildVC
        } else {
            if (settingsState.allowBetas || betaList.any { it in nameFrom }) {
                tagVC > buildVC
            } else false
        }
    }

    fun hideSelectDialog() {
        _showSelectDialog.value = false
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = uris

        if (uris != null) _showSelectDialog.value = true
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
            updateSaveFolderUriUseCase(uri?.toString())
        }
    }

    private fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
        "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
    }

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        viewModelScope.launch {
            updateColorTuplesUseCase(colorTuples.asString())
        }
    }

    fun setAlignment(align: Float) {
        viewModelScope.launch {
            setAlignmentUseCase(align.toInt())
        }
    }

    fun updateOrder(data: List<Screen>) {
        viewModelScope.launch {
            updateOrderUseCase(data.joinToString("/") { it.id.toString() })
        }
    }

    fun updateClearCacheOnLaunch() {
        viewModelScope.launch {
            toggleClearCacheOnLaunchUseCase()
        }
    }

    fun updateGroupOptionsByTypes() {
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
            GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
        }
    }

    fun toggleAllowCollectAnalytics() {
        viewModelScope.launch {
            toggleAllowCollectAnalyticsUseCase()
            GlobalExceptionHandler.setAnalyticsCollectionEnabled(settingsState.allowCollectCrashlytics)
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

    fun toggleAllowShowingShadowsInsteadOfBorders() {
        viewModelScope.launch {
            allowShowingShadowsInsteadOfBordersUseCase()
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
                updateColorTuple(colorTuple)
                updateColorTuplesUseCase(settingsState.colorTupleList + "*" + colorTupleS)
            } else {
                imageManager.getImage(data = emojiUri)
                    ?.extractPrimaryColor()
                    ?.let { primary ->
                        val colorTuple = ColorTuple(primary)
                        val colorTupleS = listOf(colorTuple).asString()
                        updateColorTuple(colorTuple)
                        updateColorTuplesUseCase(settingsState.colorTupleList + "*" + colorTupleS)
                    }
            }
            if (settingsState.isDynamicColors) toggleDynamicColors()
        }
    }

}