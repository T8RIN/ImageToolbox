package ru.tech.imageresizershrinker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override suspend fun getSettingsState(): SettingsState {
        val prefs = dataStore.data.first()
        return SettingsState(
            nightMode = prefs[NIGHT_MODE] ?: 2,
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: true,
            isAmoledMode = prefs[AMOLED_MODE] ?: false,
            appColorTuple = prefs[APP_COLOR] ?: "",
            borderWidth = prefs[BORDER_WIDTH] ?: 1f,
            showDialogOnStartup = prefs[SHOW_DIALOG] ?: true,
            selectedEmoji = prefs[EMOJI] ?: 0,
            screenList = prefs[ORDER]?.split("/")?.map {
                it.toInt()
            } ?: emptyList(),
            emojisCount = prefs[EMOJI_COUNT] ?: 1,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: false,
            groupOptionsByTypes = prefs[GROUP_OPTIONS] ?: true,
            allowChangeColorByImage = true,
            presets = emptyList(),
            fabAlignment = 1,
            imagePickerModeInt = 0,
            colorTupleList = "",
            addSequenceNumber = true,
            saveFolderUri = null,
            filenamePrefix = "",
            addSizeInFilename = false,
            addOriginalFilename = false
        )
    }

    override fun getSettingsStateFlow(): Flow<SettingsState> = dataStore.data.map { prefs ->
        SettingsState(
            nightMode = prefs[NIGHT_MODE] ?: 2,
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: true,
            isAmoledMode = prefs[AMOLED_MODE] ?: false,
            appColorTuple = prefs[APP_COLOR] ?: "",
            borderWidth = prefs[BORDER_WIDTH] ?: 1f,
            showDialogOnStartup = prefs[SHOW_DIALOG] ?: true,
            selectedEmoji = prefs[EMOJI] ?: 0,
            screenList = prefs[ORDER]?.split("/")?.map {
                it.toInt()
            } ?: emptyList(),
            emojisCount = prefs[EMOJI_COUNT] ?: 1,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: false,
            groupOptionsByTypes = prefs[GROUP_OPTIONS] ?: true,
            addSequenceNumber = prefs[ADD_SEQ_NUM] ?: true,
            saveFolderUri = prefs[SAVE_FOLDER],
            presets = ((prefs[PRESETS]?.split("*")?.map {
                it.toInt()
            } ?: emptyList()) + List(7) { 100 - it * 10 }).toSortedSet().reversed().toList(),
            colorTupleList = prefs[COLOR_TUPLES],
            allowChangeColorByImage = prefs[IMAGE_MONET] ?: true,
            imagePickerModeInt = prefs[PICKER_MODE] ?: 0,
            fabAlignment = prefs[ALIGNMENT] ?: 1,
            filenamePrefix = prefs[FILENAME_PREFIX] ?: "",
            addSizeInFilename = prefs[ADD_SIZE] ?: false,
            addOriginalFilename = prefs[ADD_ORIGINAL_NAME] ?: false
        )
    }

    override suspend fun toggleAddSequenceNumber() {
        dataStore.edit {
            val v = it[ADD_SEQ_NUM] ?: true
            it[ADD_SEQ_NUM] = !v
        }
    }

    override suspend fun toggleAddOriginalFilename() {
        dataStore.edit {
            val v = it[ADD_ORIGINAL_NAME] ?: false
            it[ADD_ORIGINAL_NAME] = !v
        }
    }

    override suspend fun updateEmojisCount(count: Int) {
        dataStore.edit {
            it[EMOJI_COUNT] = count
        }
    }

    override suspend fun updateImagePickerMode(mode: Int) {
        dataStore.edit {
            it[PICKER_MODE] = mode
        }
    }

    override suspend fun toggleAddFileSize() {
        dataStore.edit {
            val v = it[ADD_SIZE] ?: false
            it[ADD_SIZE] = !v
        }
    }

    override suspend fun updateEmoji(emoji: Int) {
        dataStore.edit {
            it[EMOJI] = emoji
        }
    }

    override suspend fun updateFilename(name: String) {
        dataStore.edit {
            it[FILENAME_PREFIX] = name
        }
    }

    override suspend fun toggleShowDialog() {
        dataStore.edit {
            val v = it[SHOW_DIALOG] ?: true
            it[SHOW_DIALOG] = !v
        }
    }

    override suspend fun updateColorTuple(colorTuple: String) {
        dataStore.edit {
            it[APP_COLOR] = colorTuple
        }
    }

    override suspend fun updatePresets(newPresets: String) {
        dataStore.edit {
            it[PRESETS] = newPresets
        }
    }

    override suspend fun toggleDynamicColors() {
        dataStore.edit {
            val v = it[DYNAMIC_COLORS] ?: true
            it[DYNAMIC_COLORS] = !v
        }
    }

    override suspend fun setBorderWidth(width: Float) {
        dataStore.edit {
            it[BORDER_WIDTH] = width
        }
    }

    override suspend fun toggleAllowImageMonet() {
        dataStore.edit {
            val v = it[IMAGE_MONET] ?: true
            it[IMAGE_MONET] = !v
        }
    }

    override suspend fun toggleAmoledMode() {
        dataStore.edit {
            val v = it[AMOLED_MODE] ?: false
            it[AMOLED_MODE] = !v
        }
    }

    override suspend fun setNightMode(mode: Int) {
        dataStore.edit {
            it[NIGHT_MODE] = mode
        }
    }

    override suspend fun updateSaveFolderUri(uri: String?) {
        dataStore.edit {
            it[SAVE_FOLDER] = uri ?: ""
        }
    }

    override suspend fun updateColorTuples(colorTuples: String) {
        dataStore.edit {
            it[COLOR_TUPLES] = colorTuples
        }
    }

    override suspend fun setAlignment(align: Int) {
        dataStore.edit {
            it[ALIGNMENT] = align
        }
    }

    override suspend fun updateOrder(data: String) {
        dataStore.edit {
            it[ORDER] = data
        }
    }

    override suspend fun toggleClearCacheOnLaunch() {
        dataStore.edit {
            val v = it[AUTO_CACHE_CLEAR] ?: true
            it[AUTO_CACHE_CLEAR] = !v
        }
    }

    override suspend fun toggleGroupOptionsByTypes() {
        dataStore.edit {
            val v = it[GROUP_OPTIONS] ?: true
            it[GROUP_OPTIONS] = !v
        }
    }

    private companion object {
        val SAVE_FOLDER = stringPreferencesKey("saveFolder")
        val NIGHT_MODE = intPreferencesKey("nightMode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
        val IMAGE_MONET = booleanPreferencesKey("imageMonet")
        val AMOLED_MODE = booleanPreferencesKey("amoledMode")
        val APP_COLOR = stringPreferencesKey("appColorTuple")
        val BORDER_WIDTH = floatPreferencesKey("borderWidth")
        val PRESETS = stringPreferencesKey("presets")
        val COLOR_TUPLES = stringPreferencesKey("color_tuples")
        val ALIGNMENT = intPreferencesKey("alignment")
        val SHOW_DIALOG = booleanPreferencesKey("showDialog")
        val FILENAME_PREFIX = stringPreferencesKey("filename")
        val EMOJI = intPreferencesKey("emoji")
        val ADD_SIZE = booleanPreferencesKey("add_size")
        val PICKER_MODE = intPreferencesKey("picker_mode")
        val ORDER = stringPreferencesKey("order")
        val EMOJI_COUNT = intPreferencesKey("em_count")
        val ADD_ORIGINAL_NAME = booleanPreferencesKey("ADD_ORIGINAL_NAME")
        val ADD_SEQ_NUM = booleanPreferencesKey("ADD_SEQ_NUM")
        val AUTO_CACHE_CLEAR = booleanPreferencesKey("auto_clear")
        val GROUP_OPTIONS = booleanPreferencesKey("group_options")
    }
}