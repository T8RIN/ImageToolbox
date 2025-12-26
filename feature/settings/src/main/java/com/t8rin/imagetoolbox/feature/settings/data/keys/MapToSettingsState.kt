/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.settings.data.keys

import androidx.datastore.preferences.core.Preferences
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.image.model.ScaleColorSpace
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.SystemBarsVisibility
import com.t8rin.imagetoolbox.core.settings.domain.model.ColorHarmonizer
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FastSettingsSide
import com.t8rin.imagetoolbox.core.settings.domain.model.NightMode
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.SliderType
import com.t8rin.imagetoolbox.core.settings.domain.model.SnowfallMode
import com.t8rin.imagetoolbox.core.settings.domain.model.SwitchType

internal fun Preferences.toSettingsState(
    default: SettingsState,
    jsonParser: JsonParser
): SettingsState = SettingsState(
    nightMode = NightMode.fromOrdinal(this[NIGHT_MODE]) ?: default.nightMode,
    isDynamicColors = this[DYNAMIC_COLORS] ?: default.isDynamicColors,
    isAmoledMode = this[AMOLED_MODE] ?: default.isAmoledMode,
    appColorTuple = this[APP_COLOR_TUPLE] ?: default.appColorTuple,
    borderWidth = this[BORDER_WIDTH] ?: default.borderWidth,
    showUpdateDialogOnStartup = this[SHOW_UPDATE_DIALOG]
        ?: default.showUpdateDialogOnStartup,
    selectedEmoji = this[SELECTED_EMOJI_INDEX] ?: default.selectedEmoji,
    screenList = this[SCREEN_ORDER]?.split("/")?.mapNotNull {
        it.toIntOrNull()
    }?.takeIf { it.isNotEmpty() } ?: default.screenList,
    emojisCount = this[EMOJI_COUNT] ?: default.emojisCount,
    clearCacheOnLaunch = this[AUTO_CACHE_CLEAR] ?: default.clearCacheOnLaunch,
    groupOptionsByTypes = this[GROUP_OPTIONS_BY_TYPE] ?: default.groupOptionsByTypes,
    addSequenceNumber = this[ADD_SEQ_NUM_TO_FILENAME] ?: default.addSequenceNumber,
    saveFolderUri = this[SAVE_FOLDER_URI],
    presets = Preset.createListFromInts(this[PRESETS]) ?: default.presets,
    colorTupleList = this[COLOR_TUPLES],
    allowChangeColorByImage = this[ALLOW_IMAGE_MONET] ?: default.allowChangeColorByImage,
    picturePickerModeInt = this[IMAGE_PICKER_MODE] ?: default.picturePickerModeInt,
    fabAlignment = this[FAB_ALIGNMENT] ?: default.fabAlignment,
    filenamePrefix = this[FILENAME_PREFIX] ?: default.filenamePrefix,
    addSizeInFilename = this[ADD_SIZE_TO_FILENAME] ?: default.addSizeInFilename,
    addOriginalFilename = this[ADD_ORIGINAL_NAME_TO_FILENAME]
        ?: default.addOriginalFilename,
    randomizeFilename = this[RANDOMIZE_FILENAME] ?: default.randomizeFilename,
    font = DomainFontFamily.fromString(this[SELECTED_FONT]) ?: default.font,
    fontScale = (this[FONT_SCALE] ?: 1f).takeIf { it > 0f },
    allowCollectCrashlytics = this[ALLOW_CRASHLYTICS] ?: default.allowCollectCrashlytics,
    allowCollectAnalytics = this[ALLOW_ANALYTICS] ?: default.allowCollectAnalytics,
    allowBetas = this[ALLOW_BETAS] ?: default.allowBetas,
    drawContainerShadows = this[DRAW_CONTAINER_SHADOWS]
        ?: default.drawContainerShadows,
    drawFabShadows = this[DRAW_FAB_SHADOWS]
        ?: default.drawFabShadows,
    drawSwitchShadows = this[DRAW_SWITCH_SHADOWS]
        ?: default.drawSwitchShadows,
    drawSliderShadows = this[DRAW_SLIDER_SHADOWS]
        ?: default.drawSliderShadows,
    drawButtonShadows = this[DRAW_BUTTON_SHADOWS]
        ?: default.drawButtonShadows,
    drawAppBarShadows = this[DRAW_APPBAR_SHADOWS]
        ?: default.drawAppBarShadows,
    appOpenCount = this[APP_OPEN_COUNT] ?: default.appOpenCount,
    aspectRatios = default.aspectRatios,
    lockDrawOrientation = this[LOCK_DRAW_ORIENTATION] ?: default.lockDrawOrientation,
    themeContrastLevel = this[THEME_CONTRAST_LEVEL] ?: default.themeContrastLevel,
    themeStyle = this[THEME_STYLE] ?: default.themeStyle,
    isInvertThemeColors = this[INVERT_THEME] ?: default.isInvertThemeColors,
    screensSearchEnabled = this[SCREEN_SEARCH_ENABLED] ?: default.screensSearchEnabled,
    copyToClipboardMode = this[COPY_TO_CLIPBOARD_MODE]?.let {
        CopyToClipboardMode.fromInt(it)
    } ?: default.copyToClipboardMode,
    hapticsStrength = this[VIBRATION_STRENGTH] ?: default.hapticsStrength,
    overwriteFiles = this[OVERWRITE_FILE] ?: default.overwriteFiles,
    filenameSuffix = this[FILENAME_SUFFIX] ?: default.filenameSuffix,
    defaultImageScaleMode = this.toDefaultImageScaleMode(default),
    magnifierEnabled = this[MAGNIFIER_ENABLED] ?: default.magnifierEnabled,
    exifWidgetInitialState = this[EXIF_WIDGET_INITIAL_STATE]
        ?: default.exifWidgetInitialState,
    initialOcrCodes = this[INITIAL_OCR_CODES]?.split("+") ?: default.initialOcrCodes,
    screenListWithMaxBrightnessEnforcement = this[SCREENS_WITH_BRIGHTNESS_ENFORCEMENT]?.split(
        "/"
    )?.mapNotNull {
        it.toIntOrNull()
    } ?: default.screenListWithMaxBrightnessEnforcement,
    isConfettiEnabled = this[CONFETTI_ENABLED] ?: default.isConfettiEnabled,
    isSecureMode = this[SECURE_MODE] ?: default.isSecureMode,
    useRandomEmojis = this[USE_RANDOM_EMOJIS] ?: default.useRandomEmojis,
    iconShape = (this[ICON_SHAPE] ?: default.iconShape)?.takeIf { it >= 0 },
    useEmojiAsPrimaryColor = this[USE_EMOJI_AS_PRIMARY_COLOR]
        ?: default.useEmojiAsPrimaryColor,
    dragHandleWidth = this[DRAG_HANDLE_WIDTH] ?: default.dragHandleWidth,
    confettiType = this[CONFETTI_TYPE] ?: default.confettiType,
    allowAutoClipboardPaste = this[ALLOW_AUTO_PASTE] ?: default.allowAutoClipboardPaste,
    confettiColorHarmonizer = this[CONFETTI_HARMONIZER]?.let {
        ColorHarmonizer.fromInt(it)
    } ?: default.confettiColorHarmonizer,
    confettiHarmonizationLevel = this[CONFETTI_HARMONIZATION_LEVEL]
        ?: default.confettiHarmonizationLevel,
    skipImagePicking = this[SKIP_IMAGE_PICKING]
        ?: default.skipImagePicking,
    generatePreviews = this[GENERATE_PREVIEWS]
        ?: default.generatePreviews,
    showSettingsInLandscape = this[SHOW_SETTINGS_IN_LANDSCAPE]
        ?: default.showSettingsInLandscape,
    useFullscreenSettings = this[USE_FULLSCREEN_SETTINGS]
        ?: default.useFullscreenSettings,
    switchType = this[SWITCH_TYPE]?.let {
        SwitchType.fromInt(it)
    } ?: default.switchType,
    defaultDrawLineWidth = this[DEFAULT_DRAW_LINE_WIDTH]
        ?: default.defaultDrawLineWidth,
    oneTimeSaveLocations = this[ONE_TIME_SAVE_LOCATIONS]?.split(", ")
        ?.mapNotNull { string ->
            OneTimeSaveLocation.fromString(string)?.takeIf {
                it.uri.isNotEmpty() && it.date != null
            }
        }
        ?.sortedWith(compareBy(OneTimeSaveLocation::count, OneTimeSaveLocation::date))
        ?.reversed()
        ?: default.oneTimeSaveLocations,
    openEditInsteadOfPreview = this[OPEN_EDIT_INSTEAD_OF_PREVIEW]
        ?: default.openEditInsteadOfPreview,
    canEnterPresetsByTextField = this[CAN_ENTER_PRESETS_BY_TEXT_FIELD]
        ?: default.canEnterPresetsByTextField,
    donateDialogOpenCount = this[DONATE_DIALOG_OPEN_COUNT]
        ?: default.donateDialogOpenCount,
    colorBlindType = this[COLOR_BLIND_TYPE]?.let {
        if (it < 0) null
        else it
    } ?: default.colorBlindType,
    favoriteScreenList = this[FAVORITE_SCREENS]?.split("/")?.mapNotNull {
        it.toIntOrNull()
    }?.takeIf { it.isNotEmpty() } ?: default.favoriteScreenList,
    isLinkPreviewEnabled = this[IS_LINK_PREVIEW_ENABLED] ?: default.isLinkPreviewEnabled,
    defaultDrawColor = this[DEFAULT_DRAW_COLOR]?.let { ColorModel(it) }
        ?: default.defaultDrawColor,
    defaultDrawPathMode = this[DEFAULT_DRAW_PATH_MODE] ?: default.defaultDrawPathMode,
    addTimestampToFilename = this[ADD_TIMESTAMP_TO_FILENAME]
        ?: default.addTimestampToFilename,
    useFormattedFilenameTimestamp = this[USE_FORMATTED_TIMESTAMP]
        ?: default.useFormattedFilenameTimestamp,
    favoriteColors = this[FAVORITE_COLORS]?.split("/")?.mapNotNull { color ->
        color.toIntOrNull()?.let { ColorModel(it) }
    } ?: default.favoriteColors,
    defaultResizeType = this[DEFAULT_RESIZE_TYPE]?.let {
        ResizeType.entries.getOrNull(it)
    } ?: default.defaultResizeType,
    systemBarsVisibility = SystemBarsVisibility.fromOrdinal(this[SYSTEM_BARS_VISIBILITY])
        ?: default.systemBarsVisibility,
    isSystemBarsVisibleBySwipe = this[IS_SYSTEM_BARS_VISIBLE_BY_SWIPE]
        ?: default.isSystemBarsVisibleBySwipe,
    isCompactSelectorsLayout = this[USE_COMPACT_SELECTORS_LAYOUT]
        ?: default.isCompactSelectorsLayout,
    mainScreenTitle = this[MAIN_SCREEN_TITLE] ?: default.mainScreenTitle,
    sliderType = this[SLIDER_TYPE]?.let {
        SliderType.fromInt(it)
    } ?: default.sliderType,
    isCenterAlignDialogButtons = this[CENTER_ALIGN_DIALOG_BUTTONS]
        ?: default.isCenterAlignDialogButtons,
    fastSettingsSide = this[FAST_SETTINGS_SIDE]?.let {
        FastSettingsSide.fromOrdinal(it)
    } ?: default.fastSettingsSide,
    settingGroupsInitialVisibility = this[SETTINGS_GROUP_VISIBILITY].toSettingGroupsInitialVisibility(
        default
    ),
    hashingTypeForFilename = HashingType.fromString(this[CHECKSUM_TYPE_FOR_FILENAME]),
    customFonts = this[CUSTOM_FONTS].toCustomFonts(),
    enableToolExitConfirmation = this[ENABLE_TOOL_EXIT_CONFIRMATION]
        ?: default.enableToolExitConfirmation,
    recentColors = this[RECENT_COLORS]?.mapNotNull { color ->
        color.toIntOrNull()?.let { ColorModel(it) }
    } ?: default.recentColors,
    backgroundForNoAlphaImageFormats = this[BACKGROUND_COLOR_FOR_NA_FORMATS]?.let { ColorModel(it) }
        ?: default.backgroundForNoAlphaImageFormats,
    addPresetInfoToFilename = this[ADD_PRESET_TO_FILENAME] ?: default.addPresetInfoToFilename,
    addImageScaleModeInfoToFilename = this[ADD_SCALE_MODE_TO_FILENAME]
        ?: default.addImageScaleModeInfoToFilename,
    allowSkipIfLarger = this[ALLOW_SKIP_IF_LARGER]
        ?: default.allowSkipIfLarger,
    customAsciiGradients = this[ASCII_CUSTOM_GRADIENTS]
        ?: default.customAsciiGradients,
    isScreenSelectionLauncherMode = this[IS_LAUNCHER_MODE] ?: default.isScreenSelectionLauncherMode,
    isTelegramGroupOpened = this[IS_TELEGRAM_GROUP_OPENED] ?: default.isTelegramGroupOpened,
    initialOcrMode = this[INITIAL_OCR_MODE] ?: default.initialOcrMode,
    spotHealMode = this[SPOT_HEAL_MODE] ?: default.spotHealMode,
    snowfallMode = this[SNOWFALL_MODE]?.let { SnowfallMode.entries[it] } ?: default.snowfallMode,
    defaultImageFormat = this[DEFAULT_IMAGE_FORMAT].let { title ->
        if (title.isNullOrBlank()) {
            null
        } else {
            ImageFormat.entries.find { it.title == title } ?: default.defaultImageFormat
        }
    },
    defaultQuality = this[DEFAULT_QUALITY]?.let {
        jsonParser.fromJson(
            json = it,
            type = Quality::class.java
        )
    } ?: default.defaultQuality,
)

private fun Preferences.toDefaultImageScaleMode(default: SettingsState): ImageScaleMode {
    val scaleMode = this[IMAGE_SCALE_MODE]?.let {
        ImageScaleMode.fromInt(it)
    } ?: default.defaultImageScaleMode

    val scaleColorSpace = this[IMAGE_SCALE_COLOR_SPACE]?.let {
        ScaleColorSpace.fromOrdinal(it)
    } ?: default.defaultImageScaleMode.scaleColorSpace

    return scaleMode.copy(scaleColorSpace)
}

private fun Set<String>?.toSettingGroupsInitialVisibility(
    default: SettingsState
): Map<Int, Boolean> =
    this?.associate { key ->
        key.split(":").let { it[0].toInt() to it[1].toBoolean() }
    } ?: default.settingGroupsInitialVisibility

private fun Set<String>?.toCustomFonts(): List<DomainFontFamily.Custom> = this?.map {
    val split = it.split(":")
    DomainFontFamily.Custom(
        name = split[0],
        filePath = split[1]
    )
} ?: emptyList()