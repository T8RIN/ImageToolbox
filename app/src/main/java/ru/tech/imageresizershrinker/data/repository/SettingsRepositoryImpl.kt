package ru.tech.imageresizershrinker.data.repository

import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.PreferencesMapCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_ORIGINAL_NAME
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_SEQ_NUM
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_SIZE
import ru.tech.imageresizershrinker.data.keys.Keys.ALIGNMENT
import ru.tech.imageresizershrinker.data.keys.Keys.AMOLED_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.APP_COLOR
import ru.tech.imageresizershrinker.data.keys.Keys.AUTO_CACHE_CLEAR
import ru.tech.imageresizershrinker.data.keys.Keys.BORDER_WIDTH
import ru.tech.imageresizershrinker.data.keys.Keys.COLOR_TUPLES
import ru.tech.imageresizershrinker.data.keys.Keys.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.data.keys.Keys.EMOJI
import ru.tech.imageresizershrinker.data.keys.Keys.EMOJI_COUNT
import ru.tech.imageresizershrinker.data.keys.Keys.FILENAME_PREFIX
import ru.tech.imageresizershrinker.data.keys.Keys.GROUP_OPTIONS
import ru.tech.imageresizershrinker.data.keys.Keys.IMAGE_MONET
import ru.tech.imageresizershrinker.data.keys.Keys.NIGHT_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.ORDER
import ru.tech.imageresizershrinker.data.keys.Keys.PICKER_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.PRESETS
import ru.tech.imageresizershrinker.data.keys.Keys.RANDOMIZE_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.SAVE_FOLDER
import ru.tech.imageresizershrinker.data.keys.Keys.SHOW_DIALOG
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.InputStream
import javax.inject.Inject


class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
            addOriginalFilename = false,
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: false
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
            } ?: emptyList())).toSortedSet().reversed().toList(),
            colorTupleList = prefs[COLOR_TUPLES],
            allowChangeColorByImage = prefs[IMAGE_MONET] ?: true,
            imagePickerModeInt = prefs[PICKER_MODE] ?: 0,
            fabAlignment = prefs[ALIGNMENT] ?: 1,
            filenamePrefix = prefs[FILENAME_PREFIX] ?: "",
            addSizeInFilename = prefs[ADD_SIZE] ?: false,
            addOriginalFilename = prefs[ADD_ORIGINAL_NAME] ?: false,
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: false
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
        dataStore.edit { preferences ->
            if (newPresets.split("*").size > 3) preferences[PRESETS] =
                newPresets.split("*")
                    .map { it.toIntOrNull()?.coerceIn(10..500) ?: 0 }
                    .toSortedSet()
                    .toList().reversed()
                    .joinToString("*")
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

    override suspend fun toggleRandomizeFilename() {
        dataStore.edit {
            val v = it[RANDOMIZE_FILENAME] ?: true
            it[RANDOMIZE_FILENAME] = !v
        }
    }

    override suspend fun createBackupFile(): ByteArray {
        return File(context.filesDir, "datastore/image_resizer.preferences_pb").readBytes()
    }

    override suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val uri = backupFileUri.toUri()
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    byteArrayOutputStream.write(input.toByteArray())
                    try {
                        PreferencesMapCompat.readFrom(ByteArrayInputStream(byteArrayOutputStream.toByteArray()))
                    } catch (t: Throwable) {
                        throw Throwable(context.getString(R.string.corrupted_file_or_not_a_backup))
                    }
                    File(
                        context.filesDir,
                        "datastore/image_resizer.preferences_pb"
                    ).outputStream().use {
                        ByteArrayInputStream(byteArrayOutputStream.toByteArray()).copyTo(it)
                    }
                }
            }.exceptionOrNull()?.let(onFailure) ?: onSuccess()
        }
        toggleClearCacheOnLaunch()
        toggleClearCacheOnLaunch()
    }

    private fun InputStream.toByteArray(): ByteArray {
        val bytes = ByteArray(this.available())
        val dis = DataInputStream(this)
        dis.readFully(bytes)
        return bytes
    }
}