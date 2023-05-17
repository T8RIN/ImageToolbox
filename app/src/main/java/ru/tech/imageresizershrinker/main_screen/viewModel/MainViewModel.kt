package ru.tech.imageresizershrinker.main_screen.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.dynamic.theme.ColorTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.main_screen.components.Screen
import ru.tech.imageresizershrinker.theme.asString
import ru.tech.imageresizershrinker.theme.defaultColorTuple
import ru.tech.imageresizershrinker.theme.toColorTupleList
import ru.tech.imageresizershrinker.utils.ADD_SIZE
import ru.tech.imageresizershrinker.utils.ALIGNMENT
import ru.tech.imageresizershrinker.utils.AMOLED_MODE
import ru.tech.imageresizershrinker.utils.APP_COLOR
import ru.tech.imageresizershrinker.utils.APP_RELEASES
import ru.tech.imageresizershrinker.utils.BORDER_WIDTH
import ru.tech.imageresizershrinker.utils.COLOR_TUPLES
import ru.tech.imageresizershrinker.utils.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.utils.EMOJI
import ru.tech.imageresizershrinker.utils.FILENAME_PREFIX
import ru.tech.imageresizershrinker.utils.IMAGE_MONET
import ru.tech.imageresizershrinker.utils.NIGHT_MODE
import ru.tech.imageresizershrinker.utils.PRESETS
import ru.tech.imageresizershrinker.utils.SAVE_FOLDER
import ru.tech.imageresizershrinker.utils.SHOW_DIALOG
import ru.tech.imageresizershrinker.widget.ToastHostState
import java.net.URL
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _addSizeInFilename = mutableStateOf(true)
    val addSizeInFilename by _addSizeInFilename

    private val _selectedEmoji = mutableStateOf(0)
    val selectedEmoji by _selectedEmoji

    private val _alignment = mutableStateOf(1)
    val alignment by _alignment

    private val _saveFolderUri = mutableStateOf<Uri?>(null)
    val saveFolderUri by _saveFolderUri

    private val _nightMode = mutableStateOf(2)
    val nightMode by _nightMode

    private val _dynamicColors = mutableStateOf(true)
    val dynamicColors by _dynamicColors

    private val _allowImageMonet = mutableStateOf(true)
    val allowImageMonet by _allowImageMonet

    private val _amoledMode = mutableStateOf(false)
    val amoledMode by _amoledMode

    private val _appColorTuple = mutableStateOf(defaultColorTuple)
    val appColorTuple by _appColorTuple

    private val _colorTupleList = mutableStateOf(emptyList<ColorTuple>())
    val colorTupleList by _colorTupleList

    private val _borderWidth = mutableStateOf(1f)
    val borderWidth by _borderWidth

    private val _localPresets = mutableStateOf(emptyList<Int>())
    val localPresets by _localPresets

    private val _filenamePrefix = mutableStateOf("")
    val filenamePrefix by _filenamePrefix

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

    private val _showDialogOnStartUp = mutableStateOf(true)
    val showDialogOnStartUp by _showDialogOnStartUp

    private val _tag = mutableStateOf("")
    val tag by _tag

    private val _changelog = mutableStateOf("")
    val changelog by _changelog

    val toastHostState = ToastHostState()

    init {
        runBlocking {
            dataStore.edit { prefs ->
                _nightMode.value = prefs[NIGHT_MODE] ?: 2
                _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
                _amoledMode.value = prefs[AMOLED_MODE] ?: false
                _appColorTuple.value = (prefs[APP_COLOR]?.let { tuple ->
                    val colorTuple = tuple.split("*")
                    ColorTuple(
                        primary = colorTuple.getOrNull(0)?.toIntOrNull()?.let { Color(it) }
                            ?: defaultColorTuple.primary,
                        secondary = colorTuple.getOrNull(1)?.toIntOrNull()?.let { Color(it) },
                        tertiary = colorTuple.getOrNull(2)?.toIntOrNull()?.let { Color(it) },
                        surface = colorTuple.getOrNull(3)?.toIntOrNull()?.let { Color(it) },
                    )
                }) ?: defaultColorTuple
                _borderWidth.value = prefs[BORDER_WIDTH]?.toFloatOrNull() ?: 1f
                _showDialogOnStartUp.value = prefs[SHOW_DIALOG] ?: true
                _selectedEmoji.value = prefs[EMOJI] ?: 0
            }
        }
        dataStore.data.onEach { prefs ->
            _saveFolderUri.value = prefs[SAVE_FOLDER]?.let { uri ->
                if (uri.isEmpty()) null
                else Uri.parse(uri)
            }
            _nightMode.value = prefs[NIGHT_MODE] ?: 2
            _dynamicColors.value = prefs[DYNAMIC_COLORS] ?: true
            _allowImageMonet.value = prefs[IMAGE_MONET] ?: true
            _amoledMode.value = prefs[AMOLED_MODE] ?: false
            _appColorTuple.value = (prefs[APP_COLOR]?.let { tuple ->
                val colorTuple = tuple.split("*")
                ColorTuple(
                    primary = colorTuple.getOrNull(0)?.toIntOrNull()?.let { Color(it) }
                        ?: defaultColorTuple.primary,
                    secondary = colorTuple.getOrNull(1)?.toIntOrNull()?.let { Color(it) },
                    tertiary = colorTuple.getOrNull(2)?.toIntOrNull()?.let { Color(it) },
                    surface = colorTuple.getOrNull(3)?.toIntOrNull()?.let { Color(it) },
                )
            }) ?: defaultColorTuple
            _borderWidth.value = prefs[BORDER_WIDTH]?.toFloatOrNull() ?: 1f
            _localPresets.value = ((prefs[PRESETS]?.split("*")?.map {
                it.toInt()
            } ?: emptyList()) + List(7) { 100 - it * 10 }).toSortedSet().reversed().toList()

            _colorTupleList.value = prefs[COLOR_TUPLES].toColorTupleList()

            _alignment.value = prefs[ALIGNMENT] ?: 1
            _showDialogOnStartUp.value = prefs[SHOW_DIALOG] ?: true
            _filenamePrefix.value = prefs[FILENAME_PREFIX] ?: ""
            _selectedEmoji.value = prefs[EMOJI] ?: 0
            _addSizeInFilename.value = prefs[ADD_SIZE] ?: true
        }.launchIn(viewModelScope)
        tryGetUpdate(showDialog = showDialogOnStartUp)
    }

    fun updateAddFileSize() {
        viewModelScope.launch {
            dataStore.edit {
                it[ADD_SIZE] = !_addSizeInFilename.value
            }
        }
    }

    fun updateEmoji(emoji: Int) {
        viewModelScope.launch {
            dataStore.edit {
                it[EMOJI] = emoji
            }
        }
    }

    fun updateFilename(name: String) {
        viewModelScope.launch {
            dataStore.edit {
                it[FILENAME_PREFIX] = name
            }
        }
    }

    fun updateShowDialog(show: Boolean) {
        viewModelScope.launch {
            dataStore.edit {
                it[SHOW_DIALOG] = show
            }
        }
    }

    fun updateColorTuple(colorTuple: ColorTuple) {
        viewModelScope.launch {
            dataStore.edit {
                it[APP_COLOR] = colorTuple.run {
                    "${primary.toArgb()}*${secondary?.toArgb()}*${tertiary?.toArgb()}*${surface?.toArgb()}"
                }
            }
        }
    }

    fun updatePresets(newPresets: List<Int>) {
        viewModelScope.launch {
            dataStore.edit {
                it[PRESETS] = newPresets.toSortedSet().toList().reversed().joinToString("*")
            }
        }
    }

    fun updateDynamicColors() {
        viewModelScope.launch {
            dataStore.edit {
                it[DYNAMIC_COLORS] = !dynamicColors
            }
        }
    }

    private var job: Job? = null
    fun setBorderWidth(width: Float) {
        job?.cancel()
        job = viewModelScope.launch {
            delay(10)
            dataStore.edit {
                it[BORDER_WIDTH] = width.toString()
            }
        }
    }

    fun updateAllowImageMonet() {
        viewModelScope.launch {
            dataStore.edit {
                it[IMAGE_MONET] = !allowImageMonet
            }
        }
    }

    fun updateAmoledMode() {
        viewModelScope.launch {
            dataStore.edit {
                it[AMOLED_MODE] = !amoledMode
            }
        }
    }

    fun setNightMode(mode: Int) {
        viewModelScope.launch {
            dataStore.edit {
                it[NIGHT_MODE] = mode
            }
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

                        if (tag != BuildConfig.VERSION_NAME) {
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
            dataStore.edit {
                it[SAVE_FOLDER] = uri?.toString() ?: ""
            }
        }
    }

    fun updateColorTuples(colorTuples: List<ColorTuple>) {
        viewModelScope.launch {
            dataStore.edit {
                it[COLOR_TUPLES] = colorTuples.asString()
            }
        }
    }

    fun setAlignment(align: Float) {
        viewModelScope.launch {
            dataStore.edit {
                it[ALIGNMENT] = align.toInt()
            }
        }
    }

}