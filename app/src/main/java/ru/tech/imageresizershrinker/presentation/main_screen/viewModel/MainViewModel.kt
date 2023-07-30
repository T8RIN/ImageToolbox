package ru.tech.imageresizershrinker.presentation.main_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.dynamic.theme.ColorTuple
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
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetAlignmentUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetBorderWidthUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.SetNightModeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddFileSizeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddOriginalFilenameUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAddSequenceNumberUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAllowImageMonetUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleAmoledModeUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleClearCacheOnLaunchUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleDynamicColorsUseCase
import ru.tech.imageresizershrinker.domain.use_case.edit_settings.ToggleGroupOptionsByTypesUseCase
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
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState
import java.net.URL
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

@HiltViewModel
class MainViewModel @Inject constructor(
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
    private val toggleRandomizeFilenameUseCase: ToggleRandomizeFilenameUseCase
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
        runBlocking {
            _settingsState.value = getSettingsStateUseCase()
        }
        getSettingsStateFlowUseCase().onEach {
            _settingsState.value = it
        }.launchIn(viewModelScope)
        tryGetUpdate(showDialog = settingsState.showDialogOnStartup)
    }

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

    fun toggleShowDialog() {
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

    fun setBorderWidth(width: Float) {
        viewModelScope.launch {
            setBorderWidthUseCase(width)
        }
    }

    fun updateAllowImageMonet() {
        viewModelScope.launch {
            toggleAllowImageMonetUseCase()
        }
    }

    fun updateAmoledMode() {
        viewModelScope.launch {
            toggleAmoledModeUseCase()
        }
    }

    fun setNightMode(mode: Int) {
        viewModelScope.launch {
            setNightModeUseCase(mode)
        }
    }

    fun cancelledUpdate(showAgain: Boolean = false) {
        if (!showAgain) _cancelledUpdate.value = true
        _showUpdateDialog.value = false
    }

    fun tryGetUpdate(
        newRequest: Boolean = false,
        showDialog: Boolean = true,
        onNoUpdates: () -> Unit = {}
    ) {
        if (!_cancelledUpdate.value || newRequest) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    kotlin.runCatching {
                        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
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

                        if (isNeedUpdate(tag)) {
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

    private fun isNeedUpdate(tag: String): Boolean {
        return (tag != BuildConfig.VERSION_NAME) && !tag.contains("beta") && !tag.contains("alpha") && !tag.contains(
            "rc"
        )
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

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        fun List<ColorTuple>.asString(): String = joinToString(separator = "*") {
            "${it.primary.toArgb()}/${it.secondary?.toArgb()}/${it.tertiary?.toArgb()}/${it.surface?.toArgb()}"
        }
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

}