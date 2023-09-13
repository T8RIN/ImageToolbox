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
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_ORIGINAL_NAME_TO_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_SEQ_NUM_TO_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.ADD_SIZE_TO_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.ALLOW_ANALYTICS
import ru.tech.imageresizershrinker.data.keys.Keys.ALLOW_BETAS
import ru.tech.imageresizershrinker.data.keys.Keys.ALLOW_CRASHLYTICS
import ru.tech.imageresizershrinker.data.keys.Keys.ALLOW_IMAGE_MONET
import ru.tech.imageresizershrinker.data.keys.Keys.ALLOW_SHADOWS_INSTEAD_OF_BORDERS
import ru.tech.imageresizershrinker.data.keys.Keys.AMOLED_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.APP_COLOR_TUPLE
import ru.tech.imageresizershrinker.data.keys.Keys.AUTO_CACHE_CLEAR
import ru.tech.imageresizershrinker.data.keys.Keys.BORDER_WIDTH
import ru.tech.imageresizershrinker.data.keys.Keys.COLOR_TUPLES
import ru.tech.imageresizershrinker.data.keys.Keys.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.data.keys.Keys.EMOJI_COUNT
import ru.tech.imageresizershrinker.data.keys.Keys.FAB_ALIGNMENT
import ru.tech.imageresizershrinker.data.keys.Keys.FILENAME_PREFIX
import ru.tech.imageresizershrinker.data.keys.Keys.FONT_SCALE
import ru.tech.imageresizershrinker.data.keys.Keys.GROUP_OPTIONS_BY_TYPE
import ru.tech.imageresizershrinker.data.keys.Keys.IMAGE_PICKER_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.NIGHT_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.PRESETS
import ru.tech.imageresizershrinker.data.keys.Keys.RANDOMIZE_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.SAVE_FOLDER_URI
import ru.tech.imageresizershrinker.data.keys.Keys.SCREEN_ORDER
import ru.tech.imageresizershrinker.data.keys.Keys.SELECTED_EMOJI_INDEX
import ru.tech.imageresizershrinker.data.keys.Keys.SELECTED_FONT_INDEX
import ru.tech.imageresizershrinker.data.keys.Keys.SHOW_UPDATE_DIALOG
import ru.tech.imageresizershrinker.domain.model.FontFam
import ru.tech.imageresizershrinker.domain.model.NightMode
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.domain.model.SettingsState
import ru.tech.imageresizershrinker.domain.repository.SettingsRepository
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override suspend fun getSettingsState(): SettingsState {
        val prefs = dataStore.data.first()
        return SettingsState(
            nightMode = NightMode.fromOrdinal(prefs[NIGHT_MODE]),
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: true,
            isAmoledMode = prefs[AMOLED_MODE] ?: false,
            appColorTuple = prefs[APP_COLOR_TUPLE] ?: "",
            borderWidth = prefs[BORDER_WIDTH] ?: 1f,
            showDialogOnStartup = prefs[SHOW_UPDATE_DIALOG] ?: true,
            selectedEmoji = prefs[SELECTED_EMOJI_INDEX] ?: 0,
            screenList = prefs[SCREEN_ORDER]?.split("/")?.map {
                it.toInt()
            } ?: emptyList(),
            emojisCount = prefs[EMOJI_COUNT] ?: 1,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: false,
            groupOptionsByTypes = prefs[GROUP_OPTIONS_BY_TYPE] ?: true,
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
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: false,
            font = FontFam.fromOrdinal(prefs[SELECTED_FONT_INDEX]),
            fontScale = (prefs[FONT_SCALE] ?: 1f).takeIf { it > 0f },
            allowCollectCrashlytics = prefs[ALLOW_CRASHLYTICS] ?: true,
            allowCollectAnalytics = prefs[ALLOW_ANALYTICS] ?: true,
            allowBetas = prefs[ALLOW_BETAS] ?: true,
            allowShowingShadowsInsteadOfBorders = prefs[ALLOW_SHADOWS_INSTEAD_OF_BORDERS] ?: true
        )
    }

    override fun getSettingsStateFlow(): Flow<SettingsState> = dataStore.data.map { prefs ->
        SettingsState(
            nightMode = NightMode.fromOrdinal(prefs[NIGHT_MODE]),
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: true,
            isAmoledMode = prefs[AMOLED_MODE] ?: false,
            appColorTuple = prefs[APP_COLOR_TUPLE] ?: "",
            borderWidth = prefs[BORDER_WIDTH] ?: 1f,
            showDialogOnStartup = prefs[SHOW_UPDATE_DIALOG] ?: true,
            selectedEmoji = prefs[SELECTED_EMOJI_INDEX] ?: 0,
            screenList = prefs[SCREEN_ORDER]?.split("/")?.map {
                it.toInt()
            } ?: emptyList(),
            emojisCount = prefs[EMOJI_COUNT] ?: 1,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: false,
            groupOptionsByTypes = prefs[GROUP_OPTIONS_BY_TYPE] ?: true,
            addSequenceNumber = prefs[ADD_SEQ_NUM_TO_FILENAME] ?: true,
            saveFolderUri = prefs[SAVE_FOLDER_URI],
            presets = Preset.createListFromInts(prefs[PRESETS]),
            colorTupleList = prefs[COLOR_TUPLES],
            allowChangeColorByImage = prefs[ALLOW_IMAGE_MONET] ?: true,
            imagePickerModeInt = prefs[IMAGE_PICKER_MODE] ?: 0,
            fabAlignment = prefs[FAB_ALIGNMENT] ?: 1,
            filenamePrefix = prefs[FILENAME_PREFIX] ?: "",
            addSizeInFilename = prefs[ADD_SIZE_TO_FILENAME] ?: false,
            addOriginalFilename = prefs[ADD_ORIGINAL_NAME_TO_FILENAME] ?: false,
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: false,
            font = FontFam.fromOrdinal(prefs[SELECTED_FONT_INDEX]),
            fontScale = (prefs[FONT_SCALE] ?: 1f).takeIf { it > 0f },
            allowCollectCrashlytics = prefs[ALLOW_CRASHLYTICS] ?: true,
            allowCollectAnalytics = prefs[ALLOW_ANALYTICS] ?: true,
            allowBetas = prefs[ALLOW_BETAS] ?: true,
            allowShowingShadowsInsteadOfBorders = prefs[ALLOW_SHADOWS_INSTEAD_OF_BORDERS] ?: true
        )
    }

    override suspend fun toggleAddSequenceNumber() {
        dataStore.edit {
            val v = it[ADD_SEQ_NUM_TO_FILENAME] ?: true
            it[ADD_SEQ_NUM_TO_FILENAME] = !v
        }
    }

    override suspend fun toggleAddOriginalFilename() {
        dataStore.edit {
            val v = it[ADD_ORIGINAL_NAME_TO_FILENAME] ?: false
            it[ADD_ORIGINAL_NAME_TO_FILENAME] = !v
        }
    }

    override suspend fun updateEmojisCount(count: Int) {
        dataStore.edit {
            it[EMOJI_COUNT] = count
        }
    }

    override suspend fun updateImagePickerMode(mode: Int) {
        dataStore.edit {
            it[IMAGE_PICKER_MODE] = mode
        }
    }

    override suspend fun toggleAddFileSize() {
        dataStore.edit {
            val v = it[ADD_SIZE_TO_FILENAME] ?: false
            it[ADD_SIZE_TO_FILENAME] = !v
        }
    }

    override suspend fun updateEmoji(emoji: Int) {
        dataStore.edit {
            it[SELECTED_EMOJI_INDEX] = emoji
        }
    }

    override suspend fun updateFilename(name: String) {
        dataStore.edit {
            it[FILENAME_PREFIX] = name
        }
    }

    override suspend fun toggleShowDialog() {
        dataStore.edit {
            val v = it[SHOW_UPDATE_DIALOG] ?: true
            it[SHOW_UPDATE_DIALOG] = !v
        }
    }

    override suspend fun updateColorTuple(colorTuple: String) {
        dataStore.edit {
            it[APP_COLOR_TUPLE] = colorTuple
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
            it[BORDER_WIDTH] = if (width > 0) width else -1f
        }
    }

    override suspend fun toggleAllowImageMonet() {
        dataStore.edit {
            val v = it[ALLOW_IMAGE_MONET] ?: true
            it[ALLOW_IMAGE_MONET] = !v
        }
    }

    override suspend fun toggleAmoledMode() {
        dataStore.edit {
            val v = it[AMOLED_MODE] ?: false
            it[AMOLED_MODE] = !v
        }
    }

    override suspend fun setNightMode(nightMode: NightMode) {
        dataStore.edit {
            it[NIGHT_MODE] = nightMode.ordinal
        }
    }

    override suspend fun updateSaveFolderUri(uri: String?) {
        dataStore.edit {
            it[SAVE_FOLDER_URI] = uri ?: ""
        }
    }

    override suspend fun updateColorTuples(colorTuples: String) {
        dataStore.edit {
            it[COLOR_TUPLES] = colorTuples
        }
    }

    override suspend fun setAlignment(align: Int) {
        dataStore.edit {
            it[FAB_ALIGNMENT] = align
        }
    }

    override suspend fun updateOrder(data: String) {
        dataStore.edit {
            it[SCREEN_ORDER] = data
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
            val v = it[GROUP_OPTIONS_BY_TYPE] ?: true
            it[GROUP_OPTIONS_BY_TYPE] = !v
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
            }.exceptionOrNull()?.let(onFailure) ?: suspend {
                onSuccess()
                updateSaveFolderUri(null)
            }.invoke()
        }
        toggleClearCacheOnLaunch()
        toggleClearCacheOnLaunch()
    }

    override suspend fun resetSettings() {
        File(
            context.filesDir,
            "datastore/image_resizer.preferences_pb"
        ).apply {
            delete()
            createNewFile()
        }
        toggleClearCacheOnLaunch()
        toggleClearCacheOnLaunch()
    }

    override fun createBackupFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        return "image_toolbox_$timeStamp.imtbx_backup"
    }

    override suspend fun setFont(font: FontFam) {
        dataStore.edit {
            it[SELECTED_FONT_INDEX] = font.ordinal
        }
    }

    override suspend fun setFontScale(scale: Float) {
        dataStore.edit {
            it[FONT_SCALE] = scale
        }
    }

    override suspend fun toggleAllowCrashlytics() {
        dataStore.edit {
            val v = it[ALLOW_CRASHLYTICS] ?: true
            it[ALLOW_CRASHLYTICS] = !v
        }
    }

    override suspend fun toggleAllowAnalytics() {
        dataStore.edit {
            val v = it[ALLOW_ANALYTICS] ?: true
            it[ALLOW_ANALYTICS] = !v
        }
    }

    override suspend fun toggleAllowBetas() {
        dataStore.edit {
            val v = it[ALLOW_BETAS] ?: true
            it[ALLOW_BETAS] = !v
        }
    }

    override suspend fun toggleAllowShowingShadowsInsteadOfBorders() {
        dataStore.edit {
            val v = it[ALLOW_SHADOWS_INSTEAD_OF_BORDERS] ?: true
            it[ALLOW_SHADOWS_INSTEAD_OF_BORDERS] = !v
        }
    }

    private fun InputStream.toByteArray(): ByteArray {
        val bytes = ByteArray(this.available())
        val dis = DataInputStream(this)
        dis.readFully(bytes)
        return bytes
    }

}