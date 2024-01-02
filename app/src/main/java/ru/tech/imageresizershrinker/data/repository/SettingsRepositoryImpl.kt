package ru.tech.imageresizershrinker.data.repository

import android.annotation.SuppressLint
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
import ru.tech.imageresizershrinker.data.keys.Keys.AMOLED_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.APP_COLOR_TUPLE
import ru.tech.imageresizershrinker.data.keys.Keys.APP_OPEN_COUNT
import ru.tech.imageresizershrinker.data.keys.Keys.AUTO_CACHE_CLEAR
import ru.tech.imageresizershrinker.data.keys.Keys.BORDER_WIDTH
import ru.tech.imageresizershrinker.data.keys.Keys.COLOR_TUPLES
import ru.tech.imageresizershrinker.data.keys.Keys.COPY_TO_CLIPBOARD
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_APPBAR_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_BUTTON_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_CONTAINER_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_FAB_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_SLIDER_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DRAW_SWITCH_SHADOWS
import ru.tech.imageresizershrinker.data.keys.Keys.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.data.keys.Keys.EMOJI_COUNT
import ru.tech.imageresizershrinker.data.keys.Keys.FAB_ALIGNMENT
import ru.tech.imageresizershrinker.data.keys.Keys.FILENAME_PREFIX
import ru.tech.imageresizershrinker.data.keys.Keys.FILENAME_SUFFIX
import ru.tech.imageresizershrinker.data.keys.Keys.FONT_SCALE
import ru.tech.imageresizershrinker.data.keys.Keys.GROUP_OPTIONS_BY_TYPE
import ru.tech.imageresizershrinker.data.keys.Keys.IMAGE_PICKER_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.INVERT_THEME
import ru.tech.imageresizershrinker.data.keys.Keys.LOCK_DRAW_ORIENTATION
import ru.tech.imageresizershrinker.data.keys.Keys.NIGHT_MODE
import ru.tech.imageresizershrinker.data.keys.Keys.OVERWRITE_FILE
import ru.tech.imageresizershrinker.data.keys.Keys.PRESETS
import ru.tech.imageresizershrinker.data.keys.Keys.RANDOMIZE_FILENAME
import ru.tech.imageresizershrinker.data.keys.Keys.SAVE_FOLDER_URI
import ru.tech.imageresizershrinker.data.keys.Keys.SCREEN_ORDER
import ru.tech.imageresizershrinker.data.keys.Keys.SCREEN_SEARCH_ENABLED
import ru.tech.imageresizershrinker.data.keys.Keys.SELECTED_EMOJI_INDEX
import ru.tech.imageresizershrinker.data.keys.Keys.SELECTED_FONT_INDEX
import ru.tech.imageresizershrinker.data.keys.Keys.SHOW_UPDATE_DIALOG
import ru.tech.imageresizershrinker.data.keys.Keys.THEME_CONTRAST_LEVEL
import ru.tech.imageresizershrinker.data.keys.Keys.THEME_STYLE
import ru.tech.imageresizershrinker.data.keys.Keys.VIBRATION_STRENGTH
import ru.tech.imageresizershrinker.coredomain.model.FontFam
import ru.tech.imageresizershrinker.coredomain.model.NightMode
import ru.tech.imageresizershrinker.coredomain.model.Preset
import ru.tech.imageresizershrinker.coredomain.model.SettingsState
import ru.tech.imageresizershrinker.coredomain.repository.SettingsRepository
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

    private val default = SettingsState.Default()

    override suspend fun getSettingsState(): SettingsState = getSettingsStateFlow().first()

    override fun getSettingsStateFlow(): Flow<SettingsState> = dataStore.data.map { prefs ->
        SettingsState(
            nightMode = NightMode.fromOrdinal(prefs[NIGHT_MODE]),
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: default.isDynamicColors,
            isAmoledMode = prefs[AMOLED_MODE] ?: default.isAmoledMode,
            appColorTuple = prefs[APP_COLOR_TUPLE] ?: default.appColorTuple,
            borderWidth = prefs[BORDER_WIDTH] ?: default.borderWidth,
            showDialogOnStartup = prefs[SHOW_UPDATE_DIALOG] ?: default.showDialogOnStartup,
            selectedEmoji = prefs[SELECTED_EMOJI_INDEX] ?: default.selectedEmoji,
            screenList = prefs[SCREEN_ORDER]?.split("/")?.map {
                it.toInt()
            } ?: default.screenList,
            emojisCount = prefs[EMOJI_COUNT] ?: default.emojisCount,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: default.clearCacheOnLaunch,
            groupOptionsByTypes = prefs[GROUP_OPTIONS_BY_TYPE] ?: default.groupOptionsByTypes,
            addSequenceNumber = prefs[ADD_SEQ_NUM_TO_FILENAME] ?: default.addSequenceNumber,
            saveFolderUri = prefs[SAVE_FOLDER_URI],
            presets = Preset.createListFromInts(prefs[PRESETS]),
            colorTupleList = prefs[COLOR_TUPLES],
            allowChangeColorByImage = prefs[ALLOW_IMAGE_MONET] ?: default.allowChangeColorByImage,
            imagePickerModeInt = prefs[IMAGE_PICKER_MODE] ?: default.imagePickerModeInt,
            fabAlignment = prefs[FAB_ALIGNMENT] ?: default.fabAlignment,
            filenamePrefix = prefs[FILENAME_PREFIX] ?: default.filenamePrefix,
            addSizeInFilename = prefs[ADD_SIZE_TO_FILENAME] ?: default.addSizeInFilename,
            addOriginalFilename = prefs[ADD_ORIGINAL_NAME_TO_FILENAME]
                ?: default.addOriginalFilename,
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: default.randomizeFilename,
            font = FontFam.fromOrdinal(prefs[SELECTED_FONT_INDEX]),
            fontScale = (prefs[FONT_SCALE] ?: 1f).takeIf { it > 0f },
            allowCollectCrashlytics = prefs[ALLOW_CRASHLYTICS] ?: default.allowCollectCrashlytics,
            allowCollectAnalytics = prefs[ALLOW_ANALYTICS] ?: default.allowCollectAnalytics,
            allowBetas = prefs[ALLOW_BETAS] ?: default.allowBetas,
            drawContainerShadows = prefs[DRAW_CONTAINER_SHADOWS]
                ?: default.drawContainerShadows,
            drawFabShadows = prefs[DRAW_FAB_SHADOWS]
                ?: default.drawFabShadows,
            drawSwitchShadows = prefs[DRAW_SWITCH_SHADOWS]
                ?: default.drawSwitchShadows,
            drawSliderShadows = prefs[DRAW_SLIDER_SHADOWS]
                ?: default.drawSliderShadows,
            drawButtonShadows = prefs[DRAW_BUTTON_SHADOWS]
                ?: default.drawButtonShadows,
            drawAppBarShadows = prefs[DRAW_APPBAR_SHADOWS]
                ?: default.drawAppBarShadows,
            appOpenCount = prefs[APP_OPEN_COUNT] ?: default.appOpenCount,
            aspectRatios = default.aspectRatios,
            lockDrawOrientation = prefs[LOCK_DRAW_ORIENTATION] ?: default.lockDrawOrientation,
            themeContrastLevel = prefs[THEME_CONTRAST_LEVEL] ?: default.themeContrastLevel,
            themeStyle = prefs[THEME_STYLE] ?: default.themeStyle,
            isInvertThemeColors = prefs[INVERT_THEME] ?: default.isInvertThemeColors,
            screensSearchEnabled = prefs[SCREEN_SEARCH_ENABLED] ?: default.screensSearchEnabled,
            autoCopyToClipBoard = prefs[COPY_TO_CLIPBOARD] ?: default.autoCopyToClipBoard,
            hapticsStrength = prefs[VIBRATION_STRENGTH] ?: default.hapticsStrength,
            overwriteFiles = prefs[OVERWRITE_FILE] ?: default.overwriteFiles,
            filenameSuffix = prefs[FILENAME_SUFFIX] ?: default.filenameSuffix
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

    override suspend fun setEmojisCount(count: Int) {
        dataStore.edit {
            it[EMOJI_COUNT] = count
        }
    }

    override suspend fun setImagePickerMode(mode: Int) {
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

    override suspend fun setEmoji(emoji: Int) {
        dataStore.edit {
            it[SELECTED_EMOJI_INDEX] = emoji
        }
    }

    override suspend fun setFilenamePrefix(name: String) {
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

    override suspend fun setColorTuple(colorTuple: String) {
        dataStore.edit {
            it[APP_COLOR_TUPLE] = colorTuple
        }
    }

    override suspend fun setPresets(newPresets: String) {
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

    override suspend fun setSaveFolderUri(uri: String?) {
        dataStore.edit {
            it[SAVE_FOLDER_URI] = uri ?: ""
        }
    }

    override suspend fun setColorTuples(colorTuples: String) {
        dataStore.edit {
            it[COLOR_TUPLES] = colorTuples
        }
    }

    override suspend fun setAlignment(align: Int) {
        dataStore.edit {
            it[FAB_ALIGNMENT] = align
        }
    }

    override suspend fun setScreenOrder(data: String) {
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

    @SuppressLint("RestrictedApi")
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
                setSaveFolderUri(null)
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
            toggleAllowBetas()
            toggleAllowBetas()
        }
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

    override suspend fun toggleDrawContainerShadows() {
        dataStore.edit {
            val v = it[DRAW_CONTAINER_SHADOWS] ?: true
            it[DRAW_CONTAINER_SHADOWS] = !v
        }
    }

    override suspend fun toggleDrawButtonShadows() {
        dataStore.edit {
            val v = it[DRAW_BUTTON_SHADOWS] ?: true
            it[DRAW_BUTTON_SHADOWS] = !v
        }
    }

    override suspend fun toggleDrawSliderShadows() {
        dataStore.edit {
            val v = it[DRAW_SLIDER_SHADOWS] ?: true
            it[DRAW_SLIDER_SHADOWS] = !v
        }
    }

    override suspend fun toggleDrawSwitchShadows() {
        dataStore.edit {
            val v = it[DRAW_SWITCH_SHADOWS] ?: true
            it[DRAW_SWITCH_SHADOWS] = !v
        }
    }

    override suspend fun toggleDrawFabShadows() {
        dataStore.edit {
            val v = it[DRAW_FAB_SHADOWS] ?: true
            it[DRAW_FAB_SHADOWS] = !v
        }
    }

    override suspend fun registerAppOpen() {
        dataStore.edit {
            val v = it[APP_OPEN_COUNT] ?: 0
            it[APP_OPEN_COUNT] = v + 1
        }
    }

    override suspend fun toggleLockDrawOrientation() {
        dataStore.edit {
            val v = it[LOCK_DRAW_ORIENTATION] ?: true
            it[LOCK_DRAW_ORIENTATION] = !v
        }
    }

    override suspend fun setThemeStyle(value: Int) {
        dataStore.edit {
            it[THEME_STYLE] = value
        }
    }

    override suspend fun setThemeContrast(value: Double) {
        dataStore.edit {
            it[THEME_CONTRAST_LEVEL] = value
        }
    }

    override suspend fun toggleInvertColors() {
        dataStore.edit {
            val v = it[INVERT_THEME] ?: false
            it[INVERT_THEME] = !v
        }
    }

    override suspend fun toggleScreensSearchEnabled() {
        dataStore.edit {
            val v = it[SCREEN_SEARCH_ENABLED] ?: false
            it[SCREEN_SEARCH_ENABLED] = !v
        }
    }

    override suspend fun toggleDrawAppBarShadows() {
        dataStore.edit {
            val v = it[DRAW_APPBAR_SHADOWS] ?: true
            it[DRAW_APPBAR_SHADOWS] = !v
        }
    }

    override suspend fun toggleAutoPinClipboard() {
        dataStore.edit {
            val v = it[COPY_TO_CLIPBOARD] ?: false
            it[COPY_TO_CLIPBOARD] = !v
        }
    }

    override suspend fun setVibrationStrength(strength: Int) {
        dataStore.edit {
            it[VIBRATION_STRENGTH] = strength
        }
    }

    override suspend fun toggleOverwriteFiles() {
        dataStore.edit {
            val v = it[OVERWRITE_FILE] ?: false
            it[OVERWRITE_FILE] = !v

            it[IMAGE_PICKER_MODE] = 2
        }
    }

    override suspend fun setFilenameSuffix(name: String) {
        dataStore.edit {
            it[FILENAME_SUFFIX] = name
        }
    }

    private fun InputStream.toByteArray(): ByteArray {
        val bytes = ByteArray(this.available())
        val dis = DataInputStream(this)
        dis.readFully(bytes)
        return bytes
    }

}