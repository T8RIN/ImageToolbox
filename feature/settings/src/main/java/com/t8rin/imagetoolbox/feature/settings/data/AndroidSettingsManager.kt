/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.settings.data

import android.content.Context
import android.graphics.Typeface
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.t8rin.imagetoolbox.core.data.utils.isInstalledFromPlayStore
import com.t8rin.imagetoolbox.core.data.utils.outputStream
import com.t8rin.imagetoolbox.core.domain.BackupFileExtension
import com.t8rin.imagetoolbox.core.domain.GlobalStorageName
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.PerformanceClass
import com.t8rin.imagetoolbox.core.domain.model.SystemBarsVisibility
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.ColorHarmonizer
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.FlingType
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.domain.model.SnowfallMode
import com.t8rin.imagetoolbox.core.settings.domain.model.SwitchType
import com.t8rin.imagetoolbox.core.utils.createZip
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.putEntry
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_ORIGINAL_NAME_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_PRESET_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_SCALE_MODE_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_SEQ_NUM_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_SIZE_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ADD_TIMESTAMP_TO_FILENAME
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_ANALYTICS
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_AUTO_PASTE
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_BETAS
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_CRASHLYTICS
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_IMAGE_MONET
import com.t8rin.imagetoolbox.feature.settings.data.keys.ALLOW_SKIP_IF_LARGER
import com.t8rin.imagetoolbox.feature.settings.data.keys.AMOLED_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.APP_COLOR_TUPLE
import com.t8rin.imagetoolbox.feature.settings.data.keys.APP_OPEN_COUNT
import com.t8rin.imagetoolbox.feature.settings.data.keys.ASCII_CUSTOM_GRADIENTS
import com.t8rin.imagetoolbox.feature.settings.data.keys.AUTO_CACHE_CLEAR
import com.t8rin.imagetoolbox.feature.settings.data.keys.BACKGROUND_COLOR_FOR_NA_FORMATS
import com.t8rin.imagetoolbox.feature.settings.data.keys.BORDER_WIDTH
import com.t8rin.imagetoolbox.feature.settings.data.keys.CAN_ENTER_PRESETS_BY_TEXT_FIELD
import com.t8rin.imagetoolbox.feature.settings.data.keys.CENTER_ALIGN_DIALOG_BUTTONS
import com.t8rin.imagetoolbox.feature.settings.data.keys.COLOR_BLIND_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.COLOR_TUPLES
import com.t8rin.imagetoolbox.feature.settings.data.keys.CONFETTI_ENABLED
import com.t8rin.imagetoolbox.feature.settings.data.keys.CONFETTI_HARMONIZATION_LEVEL
import com.t8rin.imagetoolbox.feature.settings.data.keys.CONFETTI_HARMONIZER
import com.t8rin.imagetoolbox.feature.settings.data.keys.CONFETTI_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.COPY_TO_CLIPBOARD_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.CUSTOM_FONTS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_DRAW_COLOR
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_DRAW_LINE_WIDTH
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_DRAW_PATH_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_IMAGE_FORMAT
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_QUALITY
import com.t8rin.imagetoolbox.feature.settings.data.keys.DEFAULT_RESIZE_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.DONATE_DIALOG_OPEN_COUNT
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAG_HANDLE_WIDTH
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_APPBAR_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_BUTTON_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_CONTAINER_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_FAB_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_SLIDER_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DRAW_SWITCH_SHADOWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.DYNAMIC_COLORS
import com.t8rin.imagetoolbox.feature.settings.data.keys.EMOJI_COUNT
import com.t8rin.imagetoolbox.feature.settings.data.keys.ENABLE_TOOL_EXIT_CONFIRMATION
import com.t8rin.imagetoolbox.feature.settings.data.keys.EXIF_WIDGET_INITIAL_STATE
import com.t8rin.imagetoolbox.feature.settings.data.keys.FAB_ALIGNMENT
import com.t8rin.imagetoolbox.feature.settings.data.keys.FAST_SETTINGS_SIDE
import com.t8rin.imagetoolbox.feature.settings.data.keys.FAVORITE_COLORS
import com.t8rin.imagetoolbox.feature.settings.data.keys.FAVORITE_SCREENS
import com.t8rin.imagetoolbox.feature.settings.data.keys.FILENAME_BEHAVIOR
import com.t8rin.imagetoolbox.feature.settings.data.keys.FILENAME_PATTERN
import com.t8rin.imagetoolbox.feature.settings.data.keys.FILENAME_PREFIX
import com.t8rin.imagetoolbox.feature.settings.data.keys.FILENAME_SUFFIX
import com.t8rin.imagetoolbox.feature.settings.data.keys.FLING_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.FONT_SCALE
import com.t8rin.imagetoolbox.feature.settings.data.keys.GENERATE_PREVIEWS
import com.t8rin.imagetoolbox.feature.settings.data.keys.GROUP_OPTIONS_BY_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.HIDDEN_FOR_SHARE_SCREENS
import com.t8rin.imagetoolbox.feature.settings.data.keys.ICON_SHAPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.IMAGE_PICKER_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.IMAGE_SCALE_COLOR_SPACE
import com.t8rin.imagetoolbox.feature.settings.data.keys.IMAGE_SCALE_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.INITIAL_OCR_CODES
import com.t8rin.imagetoolbox.feature.settings.data.keys.INITIAL_OCR_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.INVERT_THEME
import com.t8rin.imagetoolbox.feature.settings.data.keys.IS_LAUNCHER_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.IS_LINK_PREVIEW_ENABLED
import com.t8rin.imagetoolbox.feature.settings.data.keys.IS_SYSTEM_BARS_VISIBLE_BY_SWIPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.IS_TELEGRAM_GROUP_OPENED
import com.t8rin.imagetoolbox.feature.settings.data.keys.LOCK_DRAW_ORIENTATION
import com.t8rin.imagetoolbox.feature.settings.data.keys.MAGNIFIER_ENABLED
import com.t8rin.imagetoolbox.feature.settings.data.keys.MAIN_SCREEN_TITLE
import com.t8rin.imagetoolbox.feature.settings.data.keys.NIGHT_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.ONE_TIME_SAVE_LOCATIONS
import com.t8rin.imagetoolbox.feature.settings.data.keys.OPEN_EDIT_INSTEAD_OF_PREVIEW
import com.t8rin.imagetoolbox.feature.settings.data.keys.PRESETS
import com.t8rin.imagetoolbox.feature.settings.data.keys.RECENT_COLORS
import com.t8rin.imagetoolbox.feature.settings.data.keys.SAVE_FOLDER_URI
import com.t8rin.imagetoolbox.feature.settings.data.keys.SCREENS_WITH_BRIGHTNESS_ENFORCEMENT
import com.t8rin.imagetoolbox.feature.settings.data.keys.SCREEN_ORDER
import com.t8rin.imagetoolbox.feature.settings.data.keys.SCREEN_SEARCH_ENABLED
import com.t8rin.imagetoolbox.feature.settings.data.keys.SECURE_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SELECTED_EMOJI_INDEX
import com.t8rin.imagetoolbox.feature.settings.data.keys.SELECTED_FONT
import com.t8rin.imagetoolbox.feature.settings.data.keys.SETTINGS_GROUP_VISIBILITY
import com.t8rin.imagetoolbox.feature.settings.data.keys.SHAPES_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SHOW_SETTINGS_IN_LANDSCAPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SHOW_UPDATE_DIALOG
import com.t8rin.imagetoolbox.feature.settings.data.keys.SKIP_IMAGE_PICKING
import com.t8rin.imagetoolbox.feature.settings.data.keys.SLIDER_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SNOWFALL_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SPOT_HEAL_MODE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SWITCH_TYPE
import com.t8rin.imagetoolbox.feature.settings.data.keys.SYSTEM_BARS_VISIBILITY
import com.t8rin.imagetoolbox.feature.settings.data.keys.THEME_CONTRAST_LEVEL
import com.t8rin.imagetoolbox.feature.settings.data.keys.THEME_STYLE
import com.t8rin.imagetoolbox.feature.settings.data.keys.USE_COMPACT_SELECTORS_LAYOUT
import com.t8rin.imagetoolbox.feature.settings.data.keys.USE_EMOJI_AS_PRIMARY_COLOR
import com.t8rin.imagetoolbox.feature.settings.data.keys.USE_FORMATTED_TIMESTAMP
import com.t8rin.imagetoolbox.feature.settings.data.keys.USE_FULLSCREEN_SETTINGS
import com.t8rin.imagetoolbox.feature.settings.data.keys.USE_RANDOM_EMOJIS
import com.t8rin.imagetoolbox.feature.settings.data.keys.VIBRATION_STRENGTH
import com.t8rin.imagetoolbox.feature.settings.data.keys.toSettingsState
import com.t8rin.logger.Logger
import com.t8rin.logger.makeLog
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import kotlin.random.Random

internal class AndroidSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val shareProvider: Lazy<ShareProvider>,
    private val jsonParser: JsonParser,
    dispatchersHolder: DispatchersHolder,
    appScope: AppScope,
) : DispatchersHolder by dispatchersHolder, SettingsManager {

    init {
        appScope.launch {
            registerAppOpen()
        }
    }

    private val default = SettingsState.Default
    private val currentSettings: SettingsState get() = settingsState.value

    override suspend fun getSettingsState(): SettingsState = rawFlow().first()

    private fun rawFlow(): Flow<SettingsState> = dataStore.data.map {
        it.toSettingsState(
            default = default,
            jsonParser = jsonParser
        )
    }

    override val settingsState: StateFlow<SettingsState> = rawFlow().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = default
    )

    override suspend fun toggleAddSequenceNumber() = toggle(
        key = ADD_SEQ_NUM_TO_FILENAME,
        defaultValue = default.addSequenceNumber
    )

    override suspend fun toggleAddOriginalFilename() = toggle(
        key = ADD_ORIGINAL_NAME_TO_FILENAME,
        defaultValue = default.addOriginalFilename
    )

    override suspend fun setEmojisCount(count: Int) = edit {
        it[EMOJI_COUNT] = count
    }

    override suspend fun setImagePickerMode(mode: Int) = edit {
        it[IMAGE_PICKER_MODE] = mode
    }

    override suspend fun toggleAddFileSize() = toggle(
        key = ADD_SIZE_TO_FILENAME,
        defaultValue = default.addSizeInFilename
    )

    override suspend fun setEmoji(emoji: Int) = edit {
        it[SELECTED_EMOJI_INDEX] = emoji
    }

    override suspend fun setFilenamePrefix(name: String) = edit {
        it[FILENAME_PREFIX] = name
    }

    override suspend fun toggleShowUpdateDialogOnStartup() = toggle(
        key = SHOW_UPDATE_DIALOG,
        defaultValue = default.showUpdateDialogOnStartup
    )


    override suspend fun setColorTuple(colorTuple: String) = edit {
        it[APP_COLOR_TUPLE] = colorTuple
    }

    override suspend fun setPresets(newPresets: List<Int>) = edit { preferences ->
        if (newPresets.size > 3) {
            preferences[PRESETS] = newPresets
                .map { it.coerceIn(10..500) }
                .toSortedSet()
                .toList()
                .reversed()
                .joinToString("*")
        }
    }

    override suspend fun toggleDynamicColors() = edit {
        it.toggle(
            key = DYNAMIC_COLORS,
            defaultValue = default.isDynamicColors
        )
        if (it[DYNAMIC_COLORS] == true) {
            it[ALLOW_IMAGE_MONET] = false
        }
    }

    override suspend fun setBorderWidth(width: Float) = edit {
        it[BORDER_WIDTH] = if (width > 0) width else -1f
    }

    override suspend fun toggleAllowImageMonet() = toggle(
        key = ALLOW_IMAGE_MONET,
        defaultValue = default.allowChangeColorByImage
    )

    override suspend fun toggleAmoledMode() = toggle(
        key = AMOLED_MODE,
        defaultValue = default.isAmoledMode
    )

    override suspend fun setNightMode(nightMode: NightMode) = edit {
        it[NIGHT_MODE] = nightMode.ordinal
    }

    override suspend fun setSaveFolderUri(uri: String?) = edit {
        it[SAVE_FOLDER_URI] = uri ?: ""
    }

    override suspend fun setColorTuples(colorTuples: String) = edit {
        it[COLOR_TUPLES] = colorTuples
    }

    override suspend fun setAlignment(align: Int) = edit {
        it[FAB_ALIGNMENT] = align
    }

    override suspend fun setScreenOrder(data: String) = edit {
        it[SCREEN_ORDER] = data
    }

    override suspend fun toggleClearCacheOnLaunch() = toggle(
        key = AUTO_CACHE_CLEAR,
        defaultValue = default.clearCacheOnLaunch
    )

    override suspend fun toggleGroupOptionsByTypes() = toggle(
        key = GROUP_OPTIONS_BY_TYPE,
        defaultValue = default.groupOptionsByTypes
    )

    override suspend fun toggleRandomizeFilename() = toggleFilenameBehavior(
        behavior = FilenameBehavior.Random()
    )

    override suspend fun createBackupFile(): ByteArray =
        context.obtainDatastoreData(GlobalStorageName)

    override suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) = withContext(ioDispatcher) {
        context.restoreDatastore(
            fileName = GlobalStorageName,
            backupUri = backupFileUri.toUri(),
            onFailure = onFailure,
            onSuccess = {
                onSuccess()
                setSaveFolderUri(null)
            }
        )
        toggleClearCacheOnLaunch()
        toggleClearCacheOnLaunch()
    }

    override suspend fun resetSettings() = withContext(defaultDispatcher) {
        context.resetDatastore(GlobalStorageName)
        registerAppOpen()
    }

    override fun createBackupFilename(): String =
        "image_toolbox_${timestamp()}.$BackupFileExtension"

    override suspend fun setFont(font: DomainFontFamily) = edit {
        it[SELECTED_FONT] = font.asString()
    }

    override suspend fun setFontScale(scale: Float) = edit {
        it[FONT_SCALE] = scale
    }

    override suspend fun toggleAllowCrashlytics() = toggle(
        key = ALLOW_CRASHLYTICS,
        defaultValue = default.allowCollectCrashlytics
    )

    override suspend fun toggleAllowAnalytics() = toggle(
        key = ALLOW_ANALYTICS,
        defaultValue = default.allowCollectAnalytics
    )

    override suspend fun toggleAllowBetas() = toggle(
        key = ALLOW_BETAS,
        defaultValue = default.allowBetas
    )

    override suspend fun toggleDrawContainerShadows() = toggle(
        key = DRAW_CONTAINER_SHADOWS,
        defaultValue = default.drawContainerShadows
    )

    override suspend fun toggleDrawButtonShadows() = toggle(
        key = DRAW_BUTTON_SHADOWS,
        defaultValue = default.drawButtonShadows
    )

    override suspend fun toggleDrawSliderShadows() = toggle(
        key = DRAW_SLIDER_SHADOWS,
        defaultValue = default.drawSliderShadows
    )

    override suspend fun toggleDrawSwitchShadows() = toggle(
        key = DRAW_SWITCH_SHADOWS,
        defaultValue = default.drawSwitchShadows
    )

    override suspend fun toggleDrawFabShadows() = toggle(
        key = DRAW_FAB_SHADOWS,
        defaultValue = default.drawFabShadows
    )

    private suspend fun registerAppOpen() = edit {
        val v = it[APP_OPEN_COUNT] ?: default.appOpenCount
        it[APP_OPEN_COUNT] = v + 1
    }

    override suspend fun toggleLockDrawOrientation() = toggle(
        key = LOCK_DRAW_ORIENTATION,
        defaultValue = default.lockDrawOrientation
    )

    override suspend fun setThemeStyle(value: Int) = edit {
        it[THEME_STYLE] = value
    }

    override suspend fun setThemeContrast(value: Double) = edit {
        it[THEME_CONTRAST_LEVEL] = value
    }

    override suspend fun toggleInvertColors() = toggle(
        key = INVERT_THEME,
        defaultValue = default.isInvertThemeColors
    )

    override suspend fun toggleScreensSearchEnabled() = toggle(
        key = SCREEN_SEARCH_ENABLED,
        defaultValue = default.screensSearchEnabled
    )

    override suspend fun toggleDrawAppBarShadows() = toggle(
        key = DRAW_APPBAR_SHADOWS,
        defaultValue = default.drawAppBarShadows
    )

    override suspend fun setCopyToClipboardMode(
        copyToClipboardMode: CopyToClipboardMode
    ) = edit {
        it[COPY_TO_CLIPBOARD_MODE] = copyToClipboardMode.value
    }

    override suspend fun setVibrationStrength(strength: Int) = edit {
        it[VIBRATION_STRENGTH] = strength
    }

    override suspend fun toggleOverwriteFiles() = toggleFilenameBehavior(
        behavior = FilenameBehavior.Overwrite()
    )

    override suspend fun setSpotHealMode(mode: Int) = edit {
        it[SPOT_HEAL_MODE] = mode
    }

    override suspend fun setFilenameSuffix(name: String) = edit {
        it[FILENAME_SUFFIX] = name
    }

    override suspend fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) = edit {
        it[IMAGE_SCALE_MODE] = imageScaleMode.value
        it[IMAGE_SCALE_COLOR_SPACE] = imageScaleMode.scaleColorSpace.ordinal
    }

    override suspend fun toggleMagnifierEnabled() = toggle(
        key = MAGNIFIER_ENABLED,
        defaultValue = default.magnifierEnabled
    )

    override suspend fun toggleExifWidgetInitialState() = toggle(
        key = EXIF_WIDGET_INITIAL_STATE,
        defaultValue = default.exifWidgetInitialState
    )

    override suspend fun setInitialOCRLanguageCodes(list: List<String>) = edit {
        it[INITIAL_OCR_CODES] = list.joinToString(separator = "+")
    }

    override suspend fun createLogsExport(): String = withContext(ioDispatcher) {
        "Start Logs Export".makeLog("SettingsManager")

        val logsFile = Logger.getLogsFile().toFile()
        val settingsFile = createBackupFile()

        shareProvider.get().cacheData(
            writeData = { writeable ->
                writeable.outputStream().createZip { zip ->
                    zip.putEntry(
                        name = logsFile.name,
                        input = FileInputStream(logsFile)
                    )
                    zip.putEntry(
                        name = createBackupFilename(),
                        input = ByteArrayInputStream(settingsFile)
                    )
                }
            },
            filename = "image_toolbox_logs_${timestamp()}.zip"
        ) ?: ""
    }

    override suspend fun toggleAddPresetInfoToFilename() = toggle(
        key = ADD_PRESET_TO_FILENAME,
        defaultValue = default.addPresetInfoToFilename
    )

    override suspend fun toggleAddImageScaleModeInfoToFilename() = toggle(
        key = ADD_SCALE_MODE_TO_FILENAME,
        defaultValue = default.addImageScaleModeInfoToFilename
    )

    override suspend fun toggleAllowSkipIfLarger() = toggle(
        key = ALLOW_SKIP_IF_LARGER,
        defaultValue = default.allowSkipIfLarger
    )

    override suspend fun toggleIsScreenSelectionLauncherMode() = toggle(
        key = IS_LAUNCHER_MODE,
        defaultValue = default.isScreenSelectionLauncherMode
    )

    override suspend fun setScreensWithBrightnessEnforcement(data: List<Int>) =
        edit { preferences ->
            preferences[SCREENS_WITH_BRIGHTNESS_ENFORCEMENT] =
                data.joinToString("/") { it.toString() }
        }

    override suspend fun toggleConfettiEnabled() = toggle(
        key = CONFETTI_ENABLED,
        defaultValue = default.isConfettiEnabled
    )

    override suspend fun toggleSecureMode() = toggle(
        key = SECURE_MODE,
        defaultValue = default.isSecureMode
    )

    override suspend fun toggleUseRandomEmojis() = toggle(
        key = USE_RANDOM_EMOJIS,
        defaultValue = default.useRandomEmojis
    )

    override suspend fun setIconShape(iconShape: Int) = edit {
        it[ICON_SHAPE] = iconShape
    }

    override suspend fun toggleUseEmojiAsPrimaryColor() = toggle(
        key = USE_EMOJI_AS_PRIMARY_COLOR,
        defaultValue = default.useEmojiAsPrimaryColor
    )

    override suspend fun setDragHandleWidth(width: Int) = edit {
        it[DRAG_HANDLE_WIDTH] = width
    }

    override suspend fun setConfettiType(type: Int) = edit {
        it[CONFETTI_TYPE] = type
    }

    override suspend fun toggleAllowAutoClipboardPaste() = toggle(
        key = ALLOW_AUTO_PASTE,
        defaultValue = default.allowAutoClipboardPaste
    )

    override suspend fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) = edit {
        it[CONFETTI_HARMONIZER] = colorHarmonizer.ordinal
    }

    override suspend fun setConfettiHarmonizationLevel(level: Float) = edit {
        it[CONFETTI_HARMONIZATION_LEVEL] = level
    }

    override suspend fun toggleGeneratePreviews() = toggle(
        key = GENERATE_PREVIEWS,
        defaultValue = default.generatePreviews
    )

    override suspend fun toggleSkipImagePicking() = toggle(
        key = SKIP_IMAGE_PICKING,
        defaultValue = default.skipImagePicking
    )

    override suspend fun toggleShowSettingsInLandscape() = toggle(
        key = SHOW_SETTINGS_IN_LANDSCAPE,
        defaultValue = default.showSettingsInLandscape
    )

    override suspend fun toggleUseFullscreenSettings() = toggle(
        key = USE_FULLSCREEN_SETTINGS,
        defaultValue = default.useFullscreenSettings
    )

    override suspend fun setSwitchType(type: SwitchType) = edit {
        it[SWITCH_TYPE] = type.ordinal
    }

    override suspend fun setDefaultDrawLineWidth(value: Float) = edit {
        it[DEFAULT_DRAW_LINE_WIDTH] = value
    }

    override suspend fun setOneTimeSaveLocations(
        value: List<OneTimeSaveLocation>
    ) = edit { preferences ->
        preferences[ONE_TIME_SAVE_LOCATIONS] = value.filter {
            it.uri.isNotEmpty() && it.date != null
        }.distinctBy { it.uri }.joinToString(", ")
    }

    override suspend fun toggleRecentColor(
        color: ColorModel,
        forceExclude: Boolean,
    ) = edit { preferences ->
        val current = currentSettings.recentColors
        val newColors = if (color in current) {
            if (forceExclude) {
                current - color
            } else {
                listOf(color) + (current - color)
            }
        } else {
            listOf(color) + current
        }

        preferences[RECENT_COLORS] = newColors.take(30).map { it.colorInt.toString() }.toSet()
    }

    override suspend fun toggleFavoriteColor(
        color: ColorModel,
        forceExclude: Boolean
    ) = edit { preferences ->
        val current = currentSettings.favoriteColors
        val newColors = if (color in current) {
            if (forceExclude) {
                current - color
            } else {
                listOf(color) + (current - color)
            }
        } else {
            listOf(color) + current
        }

        preferences[FAVORITE_COLORS] = newColors.joinToString("/") { it.colorInt.toString() }
    }

    override suspend fun toggleOpenEditInsteadOfPreview() = toggle(
        key = OPEN_EDIT_INSTEAD_OF_PREVIEW,
        defaultValue = default.openEditInsteadOfPreview
    )

    override suspend fun toggleCanEnterPresetsByTextField() = toggle(
        key = CAN_ENTER_PRESETS_BY_TEXT_FIELD,
        defaultValue = default.canEnterPresetsByTextField
    )

    override suspend fun adjustPerformance(performanceClass: PerformanceClass) = edit {
        when (performanceClass) {
            PerformanceClass.Low -> {
                it[CONFETTI_ENABLED] = false
                it[DRAW_BUTTON_SHADOWS] = false
                it[DRAW_SWITCH_SHADOWS] = false
                it[DRAW_SLIDER_SHADOWS] = false
                it[DRAW_CONTAINER_SHADOWS] = false
                it[DRAW_APPBAR_SHADOWS] = false
            }

            PerformanceClass.Average -> {
                it[CONFETTI_ENABLED] = true
                it[DRAW_BUTTON_SHADOWS] = false
                it[DRAW_SWITCH_SHADOWS] = true
                it[DRAW_SLIDER_SHADOWS] = false
                it[DRAW_CONTAINER_SHADOWS] = false
                it[DRAW_APPBAR_SHADOWS] = true
            }

            PerformanceClass.High -> {
                it[CONFETTI_ENABLED] = true
                it[DRAW_BUTTON_SHADOWS] = true
                it[DRAW_SWITCH_SHADOWS] = true
                it[DRAW_SLIDER_SHADOWS] = true
                it[DRAW_CONTAINER_SHADOWS] = true
                it[DRAW_APPBAR_SHADOWS] = true
            }
        }
    }

    override suspend fun registerDonateDialogOpen() = edit {
        val value = it[DONATE_DIALOG_OPEN_COUNT] ?: default.donateDialogOpenCount

        if (value != -1) {
            it[DONATE_DIALOG_OPEN_COUNT] = value + 1
        }
    }

    override suspend fun setNotShowDonateDialogAgain() = edit {
        it[DONATE_DIALOG_OPEN_COUNT] = -1
    }

    override suspend fun setColorBlindType(value: Int?) = edit {
        it[COLOR_BLIND_TYPE] = value ?: -1
    }

    override suspend fun toggleFavoriteScreen(screenId: Int) = edit {
        val current = currentSettings.favoriteScreenList
        val newScreens = if (screenId in current) {
            current - screenId
        } else {
            current + screenId
        }

        it[FAVORITE_SCREENS] = newScreens.joinToString("/")
    }

    override suspend fun toggleIsLinkPreviewEnabled() = toggle(
        key = IS_LINK_PREVIEW_ENABLED,
        defaultValue = default.isLinkPreviewEnabled
    )

    override suspend fun setDefaultDrawColor(color: ColorModel) = edit {
        it[DEFAULT_DRAW_COLOR] = color.colorInt
    }

    override suspend fun setDefaultDrawPathMode(modeOrdinal: Int) = edit {
        it[DEFAULT_DRAW_PATH_MODE] = modeOrdinal
    }

    override suspend fun toggleAddTimestampToFilename() = toggle(
        key = ADD_TIMESTAMP_TO_FILENAME,
        defaultValue = default.addTimestampToFilename
    )

    override suspend fun toggleUseFormattedFilenameTimestamp() = toggle(
        key = USE_FORMATTED_TIMESTAMP,
        defaultValue = default.useFormattedFilenameTimestamp
    )

    override suspend fun registerTelegramGroupOpen() = edit {
        it[IS_TELEGRAM_GROUP_OPENED] = true
    }

    override suspend fun setDefaultResizeType(resizeType: ResizeType) = edit { preferences ->
        preferences[DEFAULT_RESIZE_TYPE] = ResizeType.entries.indexOfFirst {
            it::class.isInstance(resizeType)
        }
    }

    override suspend fun setSystemBarsVisibility(
        systemBarsVisibility: SystemBarsVisibility
    ) = edit {
        it[SYSTEM_BARS_VISIBILITY] = systemBarsVisibility.ordinal
    }

    override suspend fun toggleIsSystemBarsVisibleBySwipe() = toggle(
        key = IS_SYSTEM_BARS_VISIBLE_BY_SWIPE,
        defaultValue = default.isSystemBarsVisibleBySwipe
    )

    override suspend fun setInitialOcrMode(mode: Int) = edit {
        it[INITIAL_OCR_MODE] = mode
    }

    override suspend fun toggleUseCompactSelectorsLayout() = toggle(
        key = USE_COMPACT_SELECTORS_LAYOUT,
        defaultValue = default.isCompactSelectorsLayout
    )

    override suspend fun setMainScreenTitle(title: String) = edit {
        it[MAIN_SCREEN_TITLE] = title
    }

    override suspend fun setSliderType(type: SliderType) = edit {
        it[SLIDER_TYPE] = type.ordinal
    }

    override suspend fun toggleIsCenterAlignDialogButtons() = toggle(
        key = CENTER_ALIGN_DIALOG_BUTTONS,
        defaultValue = default.isCenterAlignDialogButtons
    )

    override fun isInstalledFromPlayStore(): Boolean = context.isInstalledFromPlayStore()

    override suspend fun toggleSettingsGroupVisibility(
        key: Int,
        value: Boolean
    ) = edit { preferences ->
        preferences[SETTINGS_GROUP_VISIBILITY] =
            currentSettings.settingGroupsInitialVisibility.toMutableMap().run {
                this[key] = value
                map {
                    "${it.key}:${it.value}"
                }.toSet()
            }
    }

    override suspend fun clearRecentColors() = edit {
        it[RECENT_COLORS] = emptySet()
    }

    override suspend fun updateFavoriteColors(
        colors: List<ColorModel>
    ) = edit { preferences ->
        preferences[FAVORITE_COLORS] = colors.joinToString("/") { it.colorInt.toString() }
    }

    override suspend fun setBackgroundColorForNoAlphaFormats(
        color: ColorModel
    ) = edit {
        it[BACKGROUND_COLOR_FOR_NA_FORMATS] = color.colorInt
    }

    override suspend fun setFastSettingsSide(side: FastSettingsSide) = edit {
        it[FAST_SETTINGS_SIDE] = side.ordinal
    }

    override suspend fun setChecksumTypeForFilename(type: HashingType?) = toggleFilenameBehavior(
        behavior = type?.let {
            FilenameBehavior.Checksum(type)
        } ?: FilenameBehavior.None()
    )

    override suspend fun setCustomFonts(fonts: List<DomainFontFamily.Custom>) = edit {
        it[CUSTOM_FONTS] = fonts.map(DomainFontFamily::asString).toSet()
    }

    override suspend fun importCustomFont(
        uri: String
    ): DomainFontFamily.Custom? = withContext(ioDispatcher) {
        val font = context.contentResolver.openInputStream(uri.toUri())?.use {
            it.buffered().readBytes()
        } ?: ByteArray(0)
        val filename = uri.toUri().filename(context) ?: "font${Random.nextInt()}.ttf"

        val directory = File(context.filesDir, "customFonts").apply {
            mkdir()
        }
        val file = File(directory, filename).apply {
            if (exists()) {
                val fontToRemove = DomainFontFamily.Custom(
                    name = nameWithoutExtension.replace("[:\\-_.,]".toRegex(), " "),
                    filePath = absolutePath
                )
                removeCustomFont(fontToRemove)
            }
            delete()
            createNewFile()

            outputStream().use {
                writeBytes(font)
            }
        }

        val typeface = runCatching {
            Typeface.createFromFile(file)
        }.getOrNull()

        if (typeface == null) {
            file.delete()
            return@withContext null
        }

        DomainFontFamily.Custom(
            name = file.nameWithoutExtension.replace("[:\\-_.,]".toRegex(), " "),
            filePath = file.absolutePath
        ).also {
            setCustomFonts(currentSettings.customFonts + it)
        }
    }

    override suspend fun removeCustomFont(
        font: DomainFontFamily.Custom
    ) = withContext(ioDispatcher) {
        File(font.filePath).delete()

        setCustomFonts(currentSettings.customFonts - font)
    }

    override suspend fun createCustomFontsExport(): String? = withContext(ioDispatcher) {
        shareProvider.get().cacheData(
            writeData = { writeable ->
                writeable.outputStream().createZip { zip ->
                    File(context.filesDir, "customFonts").listFiles()?.forEach { file ->
                        zip.putEntry(
                            name = file.name,
                            input = FileInputStream(file)
                        )
                    }
                }
            },
            filename = "fonts_export.zip"
        )
    }

    override suspend fun toggleEnableToolExitConfirmation() = toggle(
        key = ENABLE_TOOL_EXIT_CONFIRMATION,
        defaultValue = default.enableToolExitConfirmation
    )

    override suspend fun toggleCustomAsciiGradient(gradient: String) = edit {
        it[ASCII_CUSTOM_GRADIENTS] = (it[ASCII_CUSTOM_GRADIENTS] ?: emptySet()).toggle(gradient)
    }

    override suspend fun setSnowfallMode(snowfallMode: SnowfallMode) = edit {
        it[SNOWFALL_MODE] = snowfallMode.ordinal
    }

    override suspend fun setDefaultImageFormat(imageFormat: ImageFormat?) = edit {
        if (imageFormat == null) {
            it[DEFAULT_IMAGE_FORMAT] = ""
        } else {
            it[DEFAULT_IMAGE_FORMAT] = imageFormat.title
        }
    }

    override suspend fun setDefaultQuality(quality: Quality) = edit {
        jsonParser.toJson(quality, Quality::class.java)?.apply {
            it[DEFAULT_QUALITY] = this
        }
    }

    override suspend fun setShapesType(shapeType: ShapeType) = edit {
        jsonParser.toJson(shapeType, ShapeType::class.java)?.apply {
            it[SHAPES_TYPE] = this
        }
    }

    override suspend fun setFilenamePattern(pattern: String?) = edit {
        it[FILENAME_PATTERN] = pattern.orEmpty()
    }

    override suspend fun setFlingType(type: FlingType) = edit {
        it[FLING_TYPE] = type.ordinal
    }

    override suspend fun setHiddenForShareScreens(data: List<Int>) = edit { preferences ->
        preferences[HIDDEN_FOR_SHARE_SCREENS] = data.joinToString("/") { it.toString() }
    }

    private suspend fun toggleFilenameBehavior(
        behavior: FilenameBehavior
    ) = edit {
        val useToggle = behavior is FilenameBehavior.Checksum
                || !currentSettings.filenameBehavior::class.isInstance(behavior)

        if (useToggle) {
            if (behavior is FilenameBehavior.Overwrite) {
                it[IMAGE_PICKER_MODE] = 2
            }

            it[FILENAME_BEHAVIOR] =
                jsonParser.toJson(behavior, FilenameBehavior::class.java).orEmpty()
        } else {
            it[FILENAME_BEHAVIOR] = ""
        }
    }

    private fun MutablePreferences.toggle(
        key: Preferences.Key<Boolean>,
        defaultValue: Boolean,
    ) {
        val value = this[key] ?: defaultValue
        this[key] = !value
    }

    private suspend fun toggle(
        key: Preferences.Key<Boolean>,
        defaultValue: Boolean,
    ) = edit {
        it.toggle(
            key = key,
            defaultValue = defaultValue
        )
    }

    private suspend fun edit(
        transform: suspend (MutablePreferences) -> Unit
    ) {
        dataStore.edit(transform)
    }

}