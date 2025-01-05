/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.settings.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.data.utils.obtainDatastoreData
import ru.tech.imageresizershrinker.core.data.utils.resetDatastore
import ru.tech.imageresizershrinker.core.data.utils.restoreDatastore
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.core.domain.model.ChecksumType
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.ColorHarmonizer
import ru.tech.imageresizershrinker.core.settings.domain.model.CopyToClipboardMode
import ru.tech.imageresizershrinker.core.settings.domain.model.DomainFontFamily
import ru.tech.imageresizershrinker.core.settings.domain.model.FastSettingsSide
import ru.tech.imageresizershrinker.core.settings.domain.model.NightMode
import ru.tech.imageresizershrinker.core.settings.domain.model.OneTimeSaveLocation
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ADD_ORIGINAL_NAME_TO_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ADD_SEQ_NUM_TO_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ADD_SIZE_TO_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ADD_TIMESTAMP_TO_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ALLOW_ANALYTICS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ALLOW_AUTO_PASTE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ALLOW_BETAS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ALLOW_CRASHLYTICS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ALLOW_IMAGE_MONET
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.AMOLED_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.APP_COLOR_TUPLE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.APP_OPEN_COUNT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.AUTO_CACHE_CLEAR
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.BORDER_WIDTH
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CAN_ENTER_PRESETS_BY_TEXT_FIELD
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CENTER_ALIGN_DIALOG_BUTTONS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CHECKSUM_TYPE_FOR_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.COLOR_BLIND_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.COLOR_TUPLES
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CONFETTI_ENABLED
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CONFETTI_HARMONIZATION_LEVEL
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CONFETTI_HARMONIZER
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.CONFETTI_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.COPY_TO_CLIPBOARD_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DEFAULT_DRAW_COLOR
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DEFAULT_DRAW_LINE_WIDTH
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DEFAULT_DRAW_PATH_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DEFAULT_RESIZE_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DONATE_DIALOG_OPEN_COUNT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAG_HANDLE_WIDTH
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_APPBAR_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_BUTTON_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_CONTAINER_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_FAB_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_SLIDER_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DRAW_SWITCH_SHADOWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.DYNAMIC_COLORS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.EMOJI_COUNT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.EXIF_WIDGET_INITIAL_STATE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FAB_ALIGNMENT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FAST_SETTINGS_SIDE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FAVORITE_COLORS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FAVORITE_SCREENS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FILENAME_PREFIX
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FILENAME_SUFFIX
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.FONT_SCALE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.GENERATE_PREVIEWS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.GROUP_OPTIONS_BY_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ICON_SHAPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.IMAGE_PICKER_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.IMAGE_SCALE_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.INITIAL_OCR_CODES
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.INITIAL_OCR_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.INVERT_THEME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.IS_LINK_PREVIEW_ENABLED
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.IS_SYSTEM_BARS_VISIBLE_BY_SWIPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.IS_TELEGRAM_GROUP_OPENED
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.LOCK_DRAW_ORIENTATION
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.MAGNIFIER_ENABLED
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.MAIN_SCREEN_TITLE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.NIGHT_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.ONE_TIME_SAVE_LOCATIONS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.OPEN_EDIT_INSTEAD_OF_PREVIEW
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.OVERWRITE_FILE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.PRESETS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.RANDOMIZE_FILENAME
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SAVE_FOLDER_URI
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SCREENS_WITH_BRIGHTNESS_ENFORCEMENT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SCREEN_ORDER
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SCREEN_SEARCH_ENABLED
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SECURE_MODE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SELECTED_EMOJI_INDEX
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SELECTED_FONT_INDEX
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SETTINGS_GROUP_VISIBILITY
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SHOW_SETTINGS_IN_LANDSCAPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SHOW_UPDATE_DIALOG
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SKIP_IMAGE_PICKING
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SLIDER_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SWITCH_TYPE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.SYSTEM_BARS_VISIBILITY
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.THEME_CONTRAST_LEVEL
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.THEME_STYLE
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.USE_COMPACT_SELECTORS_LAYOUT
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.USE_EMOJI_AS_PRIMARY_COLOR
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.USE_FORMATTED_TIMESTAMP
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.USE_FULLSCREEN_SETTINGS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.USE_RANDOM_EMOJIS
import ru.tech.imageresizershrinker.feature.settings.data.SettingKeys.VIBRATION_STRENGTH
import ru.tech.imageresizershrinker.feature.settings.domain.SettingsDatastoreName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

internal class AndroidSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    dispatchersHolder: DispatchersHolder,
) : DispatchersHolder by dispatchersHolder, SettingsManager {

    init {
        CoroutineScope(ioDispatcher).launch {
            registerAppOpen()
        }
    }

    private val default = SettingsState.Default
    private var currentSettings: SettingsState = default

    override suspend fun getSettingsState(): SettingsState = withContext(defaultDispatcher) {
        getSettingsStateFlow().first()
    }

    override fun getSettingsStateFlow(): Flow<SettingsState> = dataStore.data.map { prefs ->
        SettingsState(
            nightMode = NightMode.fromOrdinal(prefs[NIGHT_MODE]) ?: default.nightMode,
            isDynamicColors = prefs[DYNAMIC_COLORS] ?: default.isDynamicColors,
            isAmoledMode = prefs[AMOLED_MODE] ?: default.isAmoledMode,
            appColorTuple = prefs[APP_COLOR_TUPLE] ?: default.appColorTuple,
            borderWidth = prefs[BORDER_WIDTH] ?: default.borderWidth,
            showUpdateDialogOnStartup = prefs[SHOW_UPDATE_DIALOG]
                ?: default.showUpdateDialogOnStartup,
            selectedEmoji = prefs[SELECTED_EMOJI_INDEX] ?: default.selectedEmoji,
            screenList = prefs[SCREEN_ORDER]?.split("/")?.mapNotNull {
                it.toIntOrNull()
            }?.takeIf { it.isNotEmpty() } ?: default.screenList,
            emojisCount = prefs[EMOJI_COUNT] ?: default.emojisCount,
            clearCacheOnLaunch = prefs[AUTO_CACHE_CLEAR] ?: default.clearCacheOnLaunch,
            groupOptionsByTypes = prefs[GROUP_OPTIONS_BY_TYPE] ?: default.groupOptionsByTypes,
            addSequenceNumber = prefs[ADD_SEQ_NUM_TO_FILENAME] ?: default.addSequenceNumber,
            saveFolderUri = prefs[SAVE_FOLDER_URI],
            presets = Preset.createListFromInts(prefs[PRESETS]) ?: default.presets,
            colorTupleList = prefs[COLOR_TUPLES],
            allowChangeColorByImage = prefs[ALLOW_IMAGE_MONET] ?: default.allowChangeColorByImage,
            picturePickerModeInt = prefs[IMAGE_PICKER_MODE] ?: default.picturePickerModeInt,
            fabAlignment = prefs[FAB_ALIGNMENT] ?: default.fabAlignment,
            filenamePrefix = prefs[FILENAME_PREFIX] ?: default.filenamePrefix,
            addSizeInFilename = prefs[ADD_SIZE_TO_FILENAME] ?: default.addSizeInFilename,
            addOriginalFilename = prefs[ADD_ORIGINAL_NAME_TO_FILENAME]
                ?: default.addOriginalFilename,
            randomizeFilename = prefs[RANDOMIZE_FILENAME] ?: default.randomizeFilename,
            font = DomainFontFamily.fromOrdinal(prefs[SELECTED_FONT_INDEX]) ?: default.font,
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
            copyToClipboardMode = prefs[COPY_TO_CLIPBOARD_MODE]?.let {
                CopyToClipboardMode.fromInt(it)
            } ?: default.copyToClipboardMode,
            hapticsStrength = prefs[VIBRATION_STRENGTH] ?: default.hapticsStrength,
            overwriteFiles = prefs[OVERWRITE_FILE] ?: default.overwriteFiles,
            filenameSuffix = prefs[FILENAME_SUFFIX] ?: default.filenameSuffix,
            defaultImageScaleMode = prefs[IMAGE_SCALE_MODE]?.let {
                ImageScaleMode.fromInt(it)
            } ?: default.defaultImageScaleMode,
            magnifierEnabled = prefs[MAGNIFIER_ENABLED] ?: default.magnifierEnabled,
            exifWidgetInitialState = prefs[EXIF_WIDGET_INITIAL_STATE]
                ?: default.exifWidgetInitialState,
            initialOcrCodes = prefs[INITIAL_OCR_CODES]?.split("+") ?: default.initialOcrCodes,
            screenListWithMaxBrightnessEnforcement = prefs[SCREENS_WITH_BRIGHTNESS_ENFORCEMENT]?.split(
                "/"
            )?.mapNotNull {
                it.toIntOrNull()
            } ?: default.screenListWithMaxBrightnessEnforcement,
            isConfettiEnabled = prefs[CONFETTI_ENABLED] ?: default.isConfettiEnabled,
            isSecureMode = prefs[SECURE_MODE] ?: default.isSecureMode,
            useRandomEmojis = prefs[USE_RANDOM_EMOJIS] ?: default.useRandomEmojis,
            iconShape = (prefs[ICON_SHAPE] ?: default.iconShape)?.takeIf { it >= 0 },
            useEmojiAsPrimaryColor = prefs[USE_EMOJI_AS_PRIMARY_COLOR]
                ?: default.useEmojiAsPrimaryColor,
            dragHandleWidth = prefs[DRAG_HANDLE_WIDTH] ?: default.dragHandleWidth,
            confettiType = prefs[CONFETTI_TYPE] ?: default.confettiType,
            allowAutoClipboardPaste = prefs[ALLOW_AUTO_PASTE] ?: default.allowAutoClipboardPaste,
            confettiColorHarmonizer = prefs[CONFETTI_HARMONIZER]?.let {
                ColorHarmonizer.fromInt(it)
            } ?: default.confettiColorHarmonizer,
            confettiHarmonizationLevel = prefs[CONFETTI_HARMONIZATION_LEVEL]
                ?: default.confettiHarmonizationLevel,
            skipImagePicking = prefs[SKIP_IMAGE_PICKING]
                ?: default.skipImagePicking,
            generatePreviews = prefs[GENERATE_PREVIEWS]
                ?: default.generatePreviews,
            showSettingsInLandscape = prefs[SHOW_SETTINGS_IN_LANDSCAPE]
                ?: default.showSettingsInLandscape,
            useFullscreenSettings = prefs[USE_FULLSCREEN_SETTINGS]
                ?: default.useFullscreenSettings,
            switchType = prefs[SWITCH_TYPE]?.let {
                SwitchType.fromInt(it)
            } ?: default.switchType,
            defaultDrawLineWidth = prefs[DEFAULT_DRAW_LINE_WIDTH]
                ?: default.defaultDrawLineWidth,
            oneTimeSaveLocations = prefs[ONE_TIME_SAVE_LOCATIONS]?.split(", ")
                ?.mapNotNull { string ->
                    OneTimeSaveLocation.fromString(string)?.takeIf {
                        it.uri.isNotEmpty() && it.date != null
                    }
                }
                ?.sortedWith(compareBy(OneTimeSaveLocation::count, OneTimeSaveLocation::date))
                ?.reversed()
                ?: default.oneTimeSaveLocations,
            openEditInsteadOfPreview = prefs[OPEN_EDIT_INSTEAD_OF_PREVIEW]
                ?: default.openEditInsteadOfPreview,
            canEnterPresetsByTextField = prefs[CAN_ENTER_PRESETS_BY_TEXT_FIELD]
                ?: default.canEnterPresetsByTextField,
            donateDialogOpenCount = prefs[DONATE_DIALOG_OPEN_COUNT]
                ?: default.donateDialogOpenCount,
            colorBlindType = prefs[COLOR_BLIND_TYPE]?.let {
                if (it < 0) null
                else it
            } ?: default.colorBlindType,
            favoriteScreenList = prefs[FAVORITE_SCREENS]?.split("/")?.mapNotNull {
                it.toIntOrNull()
            }?.takeIf { it.isNotEmpty() } ?: default.favoriteScreenList,
            isLinkPreviewEnabled = prefs[IS_LINK_PREVIEW_ENABLED] ?: default.isLinkPreviewEnabled,
            defaultDrawColor = prefs[DEFAULT_DRAW_COLOR]?.let { ColorModel(it) }
                ?: default.defaultDrawColor,
            defaultDrawPathMode = prefs[DEFAULT_DRAW_PATH_MODE] ?: default.defaultDrawPathMode,
            addTimestampToFilename = prefs[ADD_TIMESTAMP_TO_FILENAME]
                ?: default.addTimestampToFilename,
            useFormattedFilenameTimestamp = prefs[USE_FORMATTED_TIMESTAMP]
                ?: default.useFormattedFilenameTimestamp,
            favoriteColors = prefs[FAVORITE_COLORS]?.split("/")?.mapNotNull { color ->
                color.toIntOrNull()?.let { ColorModel(it) }
            } ?: default.favoriteColors,
            defaultResizeType = prefs[DEFAULT_RESIZE_TYPE]?.let {
                ResizeType.entries.getOrNull(it)
            } ?: default.defaultResizeType,
            systemBarsVisibility = SystemBarsVisibility.fromOrdinal(prefs[SYSTEM_BARS_VISIBILITY])
                ?: default.systemBarsVisibility,
            isSystemBarsVisibleBySwipe = prefs[IS_SYSTEM_BARS_VISIBLE_BY_SWIPE]
                ?: default.isSystemBarsVisibleBySwipe,
            isCompactSelectorsLayout = prefs[USE_COMPACT_SELECTORS_LAYOUT]
                ?: default.isCompactSelectorsLayout,
            mainScreenTitle = prefs[MAIN_SCREEN_TITLE] ?: default.mainScreenTitle,
            sliderType = prefs[SLIDER_TYPE]?.let {
                SliderType.fromInt(it)
            } ?: default.sliderType,
            isCenterAlignDialogButtons = prefs[CENTER_ALIGN_DIALOG_BUTTONS]
                ?: default.isCenterAlignDialogButtons,
            fastSettingsSide = prefs[FAST_SETTINGS_SIDE]?.let {
                FastSettingsSide.fromOrdinal(it)
            } ?: default.fastSettingsSide,
            settingGroupsInitialVisibility = prefs[SETTINGS_GROUP_VISIBILITY].toSettingGroupsInitialVisibility(),
            checksumTypeForFilename = ChecksumType.fromString(prefs[CHECKSUM_TYPE_FOR_FILENAME])
        )
    }.onEach { currentSettings = it }

    override fun getNeedToShowTelegramGroupDialog(): Flow<Boolean> = getSettingsStateFlow().map {
        it.appOpenCount % 6 == 0 && it.appOpenCount != 0 && (dataStore.data.first()[IS_TELEGRAM_GROUP_OPENED] != true)
    }

    override suspend fun toggleAddSequenceNumber() {
        dataStore.edit {
            it.toggle(
                key = ADD_SEQ_NUM_TO_FILENAME,
                defaultValue = default.addSequenceNumber
            )
        }
    }

    override suspend fun toggleAddOriginalFilename() {
        dataStore.edit {
            it.toggle(
                key = ADD_ORIGINAL_NAME_TO_FILENAME,
                defaultValue = default.addOriginalFilename
            )
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
            it.toggle(
                key = ADD_SIZE_TO_FILENAME,
                defaultValue = default.addSizeInFilename
            )
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

    override suspend fun toggleShowUpdateDialogOnStartup() {
        dataStore.edit {
            it.toggle(
                key = SHOW_UPDATE_DIALOG,
                defaultValue = default.showUpdateDialogOnStartup
            )
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
            it.toggle(
                key = DYNAMIC_COLORS,
                defaultValue = default.isDynamicColors
            )
            if (it[DYNAMIC_COLORS] == true) {
                it[ALLOW_IMAGE_MONET] = false
            }
        }
    }

    override suspend fun setBorderWidth(width: Float) {
        dataStore.edit {
            it[BORDER_WIDTH] = if (width > 0) width else -1f
        }
    }

    override suspend fun toggleAllowImageMonet() {
        dataStore.edit {
            it.toggle(
                key = ALLOW_IMAGE_MONET,
                defaultValue = default.allowChangeColorByImage
            )
        }
    }

    override suspend fun toggleAmoledMode() {
        dataStore.edit {
            it.toggle(
                key = AMOLED_MODE,
                defaultValue = default.isAmoledMode
            )
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
            it.toggle(
                key = AUTO_CACHE_CLEAR,
                defaultValue = default.clearCacheOnLaunch
            )
        }
    }

    override suspend fun toggleGroupOptionsByTypes() {
        dataStore.edit {
            it.toggle(
                key = GROUP_OPTIONS_BY_TYPE,
                defaultValue = default.groupOptionsByTypes
            )
        }
    }

    override suspend fun toggleRandomizeFilename() {
        dataStore.edit {
            it.toggle(
                key = RANDOMIZE_FILENAME,
                defaultValue = default.randomizeFilename
            )
        }
    }

    override suspend fun createBackupFile(): ByteArray =
        context.obtainDatastoreData(SettingsDatastoreName)

    @SuppressLint("RestrictedApi")
    override suspend fun restoreFromBackupFile(
        backupFileUri: String,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit,
    ) = withContext(defaultDispatcher) {
        context.restoreDatastore(
            fileName = SettingsDatastoreName,
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

    override suspend fun resetSettings(): Unit = withContext(defaultDispatcher) {
        context.resetDatastore(SettingsDatastoreName)
        registerAppOpen()
    }

    override fun createBackupFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        return "image_toolbox_$timeStamp.imtbx_backup"
    }

    override suspend fun setFont(font: DomainFontFamily) {
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
            it.toggle(
                key = ALLOW_CRASHLYTICS,
                defaultValue = default.allowCollectCrashlytics
            )
        }
    }

    override suspend fun toggleAllowAnalytics() {
        dataStore.edit {
            it.toggle(
                key = ALLOW_ANALYTICS,
                defaultValue = default.allowCollectAnalytics
            )
        }
    }

    override suspend fun toggleAllowBetas() {
        dataStore.edit {
            it.toggle(
                key = ALLOW_BETAS,
                defaultValue = default.allowBetas
            )
        }
    }

    override suspend fun toggleDrawContainerShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_CONTAINER_SHADOWS,
                defaultValue = default.drawContainerShadows
            )
        }
    }

    override suspend fun toggleDrawButtonShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_BUTTON_SHADOWS,
                defaultValue = default.drawButtonShadows
            )
        }
    }

    override suspend fun toggleDrawSliderShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_SLIDER_SHADOWS,
                defaultValue = default.drawSliderShadows
            )
        }
    }

    override suspend fun toggleDrawSwitchShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_SWITCH_SHADOWS,
                defaultValue = default.drawSwitchShadows
            )
        }
    }

    override suspend fun toggleDrawFabShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_FAB_SHADOWS,
                defaultValue = default.drawFabShadows
            )
        }
    }

    private suspend fun registerAppOpen() {
        dataStore.edit {
            val v = it[APP_OPEN_COUNT] ?: default.appOpenCount
            it[APP_OPEN_COUNT] = v + 1
        }
    }

    override suspend fun toggleLockDrawOrientation() {
        dataStore.edit {
            it.toggle(
                key = LOCK_DRAW_ORIENTATION,
                defaultValue = default.lockDrawOrientation
            )
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
            it.toggle(
                key = INVERT_THEME,
                defaultValue = default.isInvertThemeColors
            )
        }
    }

    override suspend fun toggleScreensSearchEnabled() {
        dataStore.edit {
            it.toggle(
                key = SCREEN_SEARCH_ENABLED,
                defaultValue = default.screensSearchEnabled
            )
        }
    }

    override suspend fun toggleDrawAppBarShadows() {
        dataStore.edit {
            it.toggle(
                key = DRAW_APPBAR_SHADOWS,
                defaultValue = default.drawAppBarShadows
            )
        }
    }

    override suspend fun setCopyToClipboardMode(copyToClipboardMode: CopyToClipboardMode) {
        dataStore.edit {
            it[COPY_TO_CLIPBOARD_MODE] = copyToClipboardMode.value
        }
    }

    override suspend fun setVibrationStrength(strength: Int) {
        dataStore.edit {
            it[VIBRATION_STRENGTH] = strength
        }
    }

    override suspend fun toggleOverwriteFiles() {
        dataStore.edit {
            it.toggle(
                key = OVERWRITE_FILE,
                defaultValue = default.overwriteFiles
            )

            it[IMAGE_PICKER_MODE] = 2
        }
    }

    override suspend fun setFilenameSuffix(name: String) {
        dataStore.edit {
            it[FILENAME_SUFFIX] = name
        }
    }

    override suspend fun setDefaultImageScaleMode(imageScaleMode: ImageScaleMode) {
        dataStore.edit {
            it[IMAGE_SCALE_MODE] = imageScaleMode.value
        }
    }

    override suspend fun toggleMagnifierEnabled() {
        dataStore.edit {
            it.toggle(
                key = MAGNIFIER_ENABLED,
                defaultValue = default.magnifierEnabled
            )
        }
    }

    override suspend fun toggleExifWidgetInitialState() {
        dataStore.edit {
            it.toggle(
                key = EXIF_WIDGET_INITIAL_STATE,
                defaultValue = default.exifWidgetInitialState
            )
        }
    }

    override suspend fun setInitialOCRLanguageCodes(list: List<String>) {
        dataStore.edit {
            it[INITIAL_OCR_CODES] = list.joinToString(separator = "+")
        }
    }

    override suspend fun getInitialOCRLanguageCodes(): List<String> {
        return dataStore.data.first().let { prefs ->
            prefs[INITIAL_OCR_CODES]?.split("+") ?: default.initialOcrCodes
        }
    }

    override suspend fun getInitialOcrMode(): Int {
        return dataStore.data.first().let { prefs ->
            prefs[INITIAL_OCR_MODE] ?: 1
        }
    }

    override suspend fun setScreensWithBrightnessEnforcement(data: String) {
        dataStore.edit {
            it[SCREENS_WITH_BRIGHTNESS_ENFORCEMENT] = data
        }
    }

    override suspend fun toggleConfettiEnabled() {
        dataStore.edit {
            it.toggle(
                key = CONFETTI_ENABLED,
                defaultValue = default.isConfettiEnabled
            )
        }
    }

    override suspend fun toggleSecureMode() {
        dataStore.edit {
            it.toggle(
                key = SECURE_MODE,
                defaultValue = default.isSecureMode
            )
        }
    }

    override suspend fun toggleUseRandomEmojis() {
        dataStore.edit {
            it.toggle(
                key = USE_RANDOM_EMOJIS,
                defaultValue = default.useRandomEmojis
            )
        }
    }

    override suspend fun setIconShape(iconShape: Int) {
        dataStore.edit {
            it[ICON_SHAPE] = iconShape
        }
    }

    override suspend fun toggleUseEmojiAsPrimaryColor() {
        dataStore.edit {
            it.toggle(
                key = USE_EMOJI_AS_PRIMARY_COLOR,
                defaultValue = default.useEmojiAsPrimaryColor
            )
        }
    }

    override suspend fun setDragHandleWidth(width: Int) {
        dataStore.edit {
            it[DRAG_HANDLE_WIDTH] = width
        }
    }

    override suspend fun setConfettiType(type: Int) {
        dataStore.edit {
            it[CONFETTI_TYPE] = type
        }
    }

    override suspend fun toggleAllowAutoClipboardPaste() {
        dataStore.edit {
            it.toggle(
                key = ALLOW_AUTO_PASTE,
                defaultValue = default.allowAutoClipboardPaste
            )
        }
    }

    override suspend fun setConfettiHarmonizer(colorHarmonizer: ColorHarmonizer) {
        dataStore.edit {
            it[CONFETTI_HARMONIZER] = colorHarmonizer.ordinal
        }
    }

    override suspend fun setConfettiHarmonizationLevel(level: Float) {
        dataStore.edit {
            it[CONFETTI_HARMONIZATION_LEVEL] = level
        }
    }

    override suspend fun toggleGeneratePreviews() {
        dataStore.edit {
            it.toggle(
                key = GENERATE_PREVIEWS,
                defaultValue = default.generatePreviews
            )
        }
    }

    override suspend fun toggleSkipImagePicking() {
        dataStore.edit {
            it.toggle(
                key = SKIP_IMAGE_PICKING,
                defaultValue = default.skipImagePicking
            )
        }
    }

    override suspend fun toggleShowSettingsInLandscape() {
        dataStore.edit {
            it.toggle(
                key = SHOW_SETTINGS_IN_LANDSCAPE,
                defaultValue = default.showSettingsInLandscape
            )
        }
    }

    override suspend fun toggleUseFullscreenSettings() {
        dataStore.edit {
            it.toggle(
                key = USE_FULLSCREEN_SETTINGS,
                defaultValue = default.useFullscreenSettings
            )
        }
    }

    override suspend fun setSwitchType(type: SwitchType) {
        dataStore.edit {
            it[SWITCH_TYPE] = type.ordinal
        }
    }

    override suspend fun setDefaultDrawLineWidth(value: Float) {
        dataStore.edit {
            it[DEFAULT_DRAW_LINE_WIDTH] = value
        }
    }

    override suspend fun setOneTimeSaveLocations(value: List<OneTimeSaveLocation>) {
        dataStore.edit { preferences ->
            preferences[ONE_TIME_SAVE_LOCATIONS] = value.filter {
                it.uri.isNotEmpty() && it.date != null
            }.distinctBy { it.uri }.joinToString(", ")
        }
    }

    override suspend fun toggleFavoriteColor(
        color: ColorModel,
        forceExclude: Boolean,
    ) {
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
        setFavoriteColors(newColors)
    }

    override suspend fun toggleOpenEditInsteadOfPreview() {
        dataStore.edit {
            it.toggle(
                key = OPEN_EDIT_INSTEAD_OF_PREVIEW,
                defaultValue = default.openEditInsteadOfPreview
            )
        }
    }

    override suspend fun toggleCanEnterPresetsByTextField() {
        dataStore.edit {
            it.toggle(
                key = CAN_ENTER_PRESETS_BY_TEXT_FIELD,
                defaultValue = default.canEnterPresetsByTextField
            )
        }
    }

    override suspend fun adjustPerformance(performanceClass: PerformanceClass) {
        when (performanceClass) {
            PerformanceClass.Low -> {
                dataStore.edit {
                    it[CONFETTI_ENABLED] = false
                    it[DRAW_BUTTON_SHADOWS] = false
                    it[DRAW_SWITCH_SHADOWS] = false
                    it[DRAW_SLIDER_SHADOWS] = false
                    it[DRAW_CONTAINER_SHADOWS] = false
                    it[DRAW_APPBAR_SHADOWS] = false
                }
            }

            PerformanceClass.Average -> {
                dataStore.edit {
                    it[CONFETTI_ENABLED] = true
                    it[DRAW_BUTTON_SHADOWS] = false
                    it[DRAW_SWITCH_SHADOWS] = true
                    it[DRAW_SLIDER_SHADOWS] = false
                    it[DRAW_CONTAINER_SHADOWS] = false
                    it[DRAW_APPBAR_SHADOWS] = true
                }
            }

            PerformanceClass.High -> {
                dataStore.edit {
                    it[CONFETTI_ENABLED] = true
                    it[DRAW_BUTTON_SHADOWS] = true
                    it[DRAW_SWITCH_SHADOWS] = true
                    it[DRAW_SLIDER_SHADOWS] = true
                    it[DRAW_CONTAINER_SHADOWS] = true
                    it[DRAW_APPBAR_SHADOWS] = true
                }
            }
        }
    }

    override suspend fun registerDonateDialogOpen() {
        dataStore.edit {
            val value = it[DONATE_DIALOG_OPEN_COUNT] ?: default.donateDialogOpenCount

            if (value != -1) {
                it[DONATE_DIALOG_OPEN_COUNT] = value + 1
            }
        }
    }

    override suspend fun setNotShowDonateDialogAgain() {
        dataStore.edit {
            it[DONATE_DIALOG_OPEN_COUNT] = -1
        }
    }

    override suspend fun setColorBlindType(value: Int?) {
        dataStore.edit {
            it[COLOR_BLIND_TYPE] = value ?: -1
        }
    }

    override suspend fun toggleFavoriteScreen(screenId: Int) {
        val current = currentSettings.favoriteScreenList
        val newScreens = if (screenId in current) {
            current - screenId
        } else {
            current + screenId
        }
        setFavoriteScreens(newScreens)
    }

    override suspend fun toggleIsLinkPreviewEnabled() {
        dataStore.edit {
            it.toggle(
                key = IS_LINK_PREVIEW_ENABLED,
                defaultValue = default.isLinkPreviewEnabled
            )
        }
    }

    override suspend fun setDefaultDrawColor(color: ColorModel) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_DRAW_COLOR] = color.colorInt
        }
    }

    override suspend fun setDefaultDrawPathMode(modeOrdinal: Int) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_DRAW_PATH_MODE] = modeOrdinal
        }
    }

    override suspend fun toggleAddTimestampToFilename() {
        dataStore.edit {
            it.toggle(
                key = ADD_TIMESTAMP_TO_FILENAME,
                defaultValue = default.addTimestampToFilename
            )
        }
    }

    override suspend fun toggleUseFormattedFilenameTimestamp() {
        dataStore.edit {
            it.toggle(
                key = USE_FORMATTED_TIMESTAMP,
                defaultValue = default.useFormattedFilenameTimestamp
            )
        }
    }

    override suspend fun registerTelegramGroupOpen() {
        dataStore.edit { prefs ->
            prefs[IS_TELEGRAM_GROUP_OPENED] = true
        }
    }

    override suspend fun setDefaultResizeType(resizeType: ResizeType) {
        dataStore.edit { prefs ->
            prefs[DEFAULT_RESIZE_TYPE] = ResizeType.entries.indexOfFirst {
                it::class.isInstance(resizeType)
            }
        }
    }

    override suspend fun setSystemBarsVisibility(systemBarsVisibility: SystemBarsVisibility) {
        dataStore.edit { prefs ->
            prefs[SYSTEM_BARS_VISIBILITY] = systemBarsVisibility.ordinal
        }
    }

    override suspend fun toggleIsSystemBarsVisibleBySwipe() {
        dataStore.edit {
            it.toggle(
                key = IS_SYSTEM_BARS_VISIBLE_BY_SWIPE,
                defaultValue = default.isSystemBarsVisibleBySwipe
            )
        }
    }

    override suspend fun setInitialOcrMode(mode: Int) {
        dataStore.edit {
            it[INITIAL_OCR_MODE] = mode
        }
    }

    override suspend fun toggleUseCompactSelectorsLayout() {
        dataStore.edit {
            it.toggle(
                key = USE_COMPACT_SELECTORS_LAYOUT,
                defaultValue = default.isCompactSelectorsLayout
            )
        }
    }

    override suspend fun setMainScreenTitle(title: String) {
        dataStore.edit {
            it[MAIN_SCREEN_TITLE] = title
        }
    }

    override suspend fun setSliderType(type: SliderType) {
        dataStore.edit {
            it[SLIDER_TYPE] = type.ordinal
        }
    }

    override suspend fun toggleIsCenterAlignDialogButtons() {
        dataStore.edit {
            it.toggle(
                key = CENTER_ALIGN_DIALOG_BUTTONS,
                defaultValue = default.isCenterAlignDialogButtons
            )
        }
    }

    override fun isInstalledFromPlayStore(): Boolean = context.isInstalledFromPlayStore()

    override suspend fun toggleSettingsGroupVisibility(
        key: Int,
        value: Boolean
    ) {
        dataStore.edit {
            it[SETTINGS_GROUP_VISIBILITY] =
                currentSettings.settingGroupsInitialVisibility.toMutableMap().run {
                    this[key] = value
                    map {
                        "${it.key}:${it.value}"
                    }.toSet()
                }
        }
    }

    override suspend fun setFastSettingsSide(side: FastSettingsSide) {
        dataStore.edit {
            it[FAST_SETTINGS_SIDE] = side.ordinal
        }
    }

    override suspend fun setChecksumTypeForFilename(type: ChecksumType?) {
        dataStore.edit {
            it[CHECKSUM_TYPE_FOR_FILENAME] = type?.digest ?: ""
        }
    }

    private suspend fun setFavoriteScreens(data: List<Int>) {
        dataStore.edit { prefs ->
            prefs[FAVORITE_SCREENS] = data.joinToString("/") { it.toString() }
        }
    }

    private suspend fun setFavoriteColors(data: List<ColorModel>) {
        dataStore.edit { prefs ->
            prefs[FAVORITE_COLORS] = data.take(30).joinToString("/") { it.colorInt.toString() }
        }
    }

    private fun MutablePreferences.toggle(
        key: Preferences.Key<Boolean>,
        defaultValue: Boolean,
    ) {
        val value = this[key] ?: defaultValue
        this[key] = !value
    }

    private fun Set<String>?.toSettingGroupsInitialVisibility(): Map<Int, Boolean> =
        this?.map { key ->
            key.split(":").let { it[0].toInt() to it[1].toBoolean() }
        }?.toMap() ?: default.settingGroupsInitialVisibility

}