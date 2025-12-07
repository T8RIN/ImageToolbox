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

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal val SAVE_FOLDER_URI = stringPreferencesKey("saveFolder")
internal val NIGHT_MODE = intPreferencesKey("nightMode")
internal val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
internal val ALLOW_IMAGE_MONET = booleanPreferencesKey("imageMonet")
internal val AMOLED_MODE = booleanPreferencesKey("amoledMode")
internal val APP_COLOR_TUPLE = stringPreferencesKey("appColorTuple")
internal val BORDER_WIDTH = floatPreferencesKey("borderWidth")
internal val PRESETS = stringPreferencesKey("presets")
internal val COLOR_TUPLES = stringPreferencesKey("color_tuples")
internal val FAB_ALIGNMENT = intPreferencesKey("alignment")
internal val SHOW_UPDATE_DIALOG = booleanPreferencesKey("showDialog")
internal val FILENAME_PREFIX = stringPreferencesKey("filename")
internal val SELECTED_EMOJI_INDEX = intPreferencesKey("emoji")
internal val ADD_SIZE_TO_FILENAME = booleanPreferencesKey("add_size")
internal val IMAGE_PICKER_MODE = intPreferencesKey("picker_mode")
internal val SCREEN_ORDER = stringPreferencesKey("order")
internal val EMOJI_COUNT = intPreferencesKey("em_count")
internal val ADD_ORIGINAL_NAME_TO_FILENAME = booleanPreferencesKey("ADD_ORIGINAL_NAME")
internal val ADD_SEQ_NUM_TO_FILENAME = booleanPreferencesKey("ADD_SEQ_NUM")
internal val AUTO_CACHE_CLEAR = booleanPreferencesKey("auto_clear")
internal val GROUP_OPTIONS_BY_TYPE = booleanPreferencesKey("group_options")
internal val RANDOMIZE_FILENAME = booleanPreferencesKey("rand-filename")
internal val SELECTED_FONT = stringPreferencesKey("SELECTED_FONT")
internal val FONT_SCALE = floatPreferencesKey("font_scale")
internal val ALLOW_CRASHLYTICS = booleanPreferencesKey("allow_crashlytics")
internal val ALLOW_ANALYTICS = booleanPreferencesKey("allow_analytics")
internal val ALLOW_BETAS = booleanPreferencesKey("allow_betas")
internal val DRAW_CONTAINER_SHADOWS = booleanPreferencesKey("ALLOW_SHADOWS_INSTEAD_OF_BORDERS")
internal val APP_OPEN_COUNT = intPreferencesKey("APP_OPEN_COUNT")
internal val LOCK_DRAW_ORIENTATION = booleanPreferencesKey("LOCK_DRAW_ORIENTATION")
internal val THEME_CONTRAST_LEVEL = doublePreferencesKey("THEME_CONTRAST_LEVEL")
internal val THEME_STYLE = intPreferencesKey("THEME_STYLE")
internal val INVERT_THEME = booleanPreferencesKey("INVERT_THEME")
internal val SCREEN_SEARCH_ENABLED = booleanPreferencesKey("SCREEN_SEARCH_ENABLED")
internal val DRAW_BUTTON_SHADOWS = booleanPreferencesKey("DRAW_BUTTON_SHADOWS")
internal val DRAW_FAB_SHADOWS = booleanPreferencesKey("DRAW_FAB_SHADOWS")
internal val DRAW_SWITCH_SHADOWS = booleanPreferencesKey("DRAW_SWITCH_SHADOWS")
internal val DRAW_SLIDER_SHADOWS = booleanPreferencesKey("DRAW_SLIDER_SHADOWS")
internal val DRAW_APPBAR_SHADOWS = booleanPreferencesKey("DRAW_APPBAR_SHADOWS")
internal val COPY_TO_CLIPBOARD_MODE = intPreferencesKey("COPY_TO_CLIPBOARD_MODE")
internal val VIBRATION_STRENGTH = intPreferencesKey("VIBRATION_STRENGTH")
internal val OVERWRITE_FILE = booleanPreferencesKey("OVERWRITE_FILE")
internal val FILENAME_SUFFIX = stringPreferencesKey("FILENAME_SUFFIX")
internal val IMAGE_SCALE_MODE = intPreferencesKey("IMAGE_SCALE_MODE")
internal val MAGNIFIER_ENABLED = booleanPreferencesKey("MAGNIFIER_ENABLED")
internal val EXIF_WIDGET_INITIAL_STATE = booleanPreferencesKey("EXIF_WIDGET_INITIAL_STATE")
internal val INITIAL_OCR_CODES = stringPreferencesKey("INITIAL_OCR_CODES")
internal val SCREENS_WITH_BRIGHTNESS_ENFORCEMENT =
    stringPreferencesKey("SCREENS_WITH_BRIGHTNESS_ENFORCEMENT")
internal val CONFETTI_ENABLED = booleanPreferencesKey("CONFETTI_ENABLED")
internal val SECURE_MODE = booleanPreferencesKey("SECURE_MODE")
internal val USE_RANDOM_EMOJIS = booleanPreferencesKey("USE_RANDOM_EMOJIS")
internal val ICON_SHAPE = intPreferencesKey("ICON_SHAPE")
internal val USE_EMOJI_AS_PRIMARY_COLOR = booleanPreferencesKey("USE_EMOJI_AS_PRIMARY_COLOR")
internal val DRAG_HANDLE_WIDTH = intPreferencesKey("DRAG_HANDLE_WIDTH")
internal val CONFETTI_TYPE = intPreferencesKey("CONFETTI_TYPE")
internal val ALLOW_AUTO_PASTE = booleanPreferencesKey("ALLOW_AUTO_PASTE")
internal val CONFETTI_HARMONIZER = intPreferencesKey("CONFETTI_HARMONIZER")
internal val CONFETTI_HARMONIZATION_LEVEL = floatPreferencesKey("CONFETTI_HARMONIZATION_LEVEL")
internal val SKIP_IMAGE_PICKING = booleanPreferencesKey("SKIP_IMAGE_PICKER")
internal val GENERATE_PREVIEWS = booleanPreferencesKey("GENERATE_PREVIEWS")
internal val SHOW_SETTINGS_IN_LANDSCAPE = booleanPreferencesKey("SHOW_SETTINGS_IN_LANDSCAPE")
internal val USE_FULLSCREEN_SETTINGS = booleanPreferencesKey("USE_FULLSCREEN_SETTINGS")
internal val SWITCH_TYPE = intPreferencesKey("SWITCH_TYPE")
internal val DEFAULT_DRAW_LINE_WIDTH = floatPreferencesKey("DEFAULT_DRAW_LINE_WIDTH")
internal val ONE_TIME_SAVE_LOCATIONS = stringPreferencesKey("ONE_TIME_SAVE_LOCATIONS")
internal val OPEN_EDIT_INSTEAD_OF_PREVIEW = booleanPreferencesKey("OPEN_EDIT_INSTEAD_OF_PREVIEW")
internal val CAN_ENTER_PRESETS_BY_TEXT_FIELD =
    booleanPreferencesKey("CAN_ENTER_PRESETS_BY_TEXT_FIELD")
internal val DONATE_DIALOG_OPEN_COUNT = intPreferencesKey("DONATE_DIALOG_OPEN_COUNT")
internal val COLOR_BLIND_TYPE = intPreferencesKey("COLOR_BLIND_TYPE")
internal val FAVORITE_SCREENS = stringPreferencesKey("FAVORITE_SCREENS")
internal val IS_LINK_PREVIEW_ENABLED = booleanPreferencesKey("IS_LINK_PREVIEW_ENABLED")
internal val DEFAULT_DRAW_COLOR = intPreferencesKey("DEFAULT_DRAW_COLOR")
internal val DEFAULT_DRAW_PATH_MODE = intPreferencesKey("DEFAULT_DRAW_PATH_MODE")
internal val ADD_TIMESTAMP_TO_FILENAME = booleanPreferencesKey("ADD_TIMESTAMP_TO_FILENAME")
internal val USE_FORMATTED_TIMESTAMP = booleanPreferencesKey("USE_FORMATTED_TIMESTAMP")
internal val IS_TELEGRAM_GROUP_OPENED = booleanPreferencesKey("IS_TELEGRAM_GROUP_OPENED")
internal val DEFAULT_RESIZE_TYPE = intPreferencesKey("DEFAULT_RESIZE_TYPE")
internal val SYSTEM_BARS_VISIBILITY = intPreferencesKey("SYSTEM_BARS_VISIBILITY")
internal val IS_SYSTEM_BARS_VISIBLE_BY_SWIPE =
    booleanPreferencesKey("IS_SYSTEM_BARS_VISIBLE_BY_SWIPE")
internal val INITIAL_OCR_MODE = intPreferencesKey("INITIAL_OCR_MODE")
internal val USE_COMPACT_SELECTORS_LAYOUT = booleanPreferencesKey("USE_COMPACT_SELECTORS_LAYOUT")
internal val MAIN_SCREEN_TITLE = stringPreferencesKey("MAIN_SCREEN_TITLE")
internal val SLIDER_TYPE = intPreferencesKey("SLIDER_TYPE")
internal val CENTER_ALIGN_DIALOG_BUTTONS = booleanPreferencesKey("CENTER_ALIGN_DIALOG_BUTTONS")
internal val FAST_SETTINGS_SIDE = intPreferencesKey("FAST_SETTINGS_SIDE")
internal val SETTINGS_GROUP_VISIBILITY = stringSetPreferencesKey("SETTINGS_GROUP_VISIBILITY")
internal val CHECKSUM_TYPE_FOR_FILENAME = stringPreferencesKey("CHECKSUM_TYPE_FOR_FILENAME")
internal val CUSTOM_FONTS = stringSetPreferencesKey("CUSTOM_FONTS")
internal val ENABLE_TOOL_EXIT_CONFIRMATION = booleanPreferencesKey("ENABLE_TOOL_EXIT_CONFIRMATION")
internal val RECENT_COLORS = stringSetPreferencesKey("RECENT_COLORS")
internal val FAVORITE_COLORS = stringPreferencesKey("FAVORITE_COLORS_KEY")
internal val BACKGROUND_COLOR_FOR_NA_FORMATS = intPreferencesKey("BACKGROUND_COLOR_FOR_NA_FORMATS")
internal val IMAGE_SCALE_COLOR_SPACE = intPreferencesKey("IMAGE_SCALE_COLOR_SPACE")
internal val ADD_PRESET_TO_FILENAME = booleanPreferencesKey("ADD_PRESET_TO_FILENAME")
internal val ADD_SCALE_MODE_TO_FILENAME = booleanPreferencesKey("ADD_SCALE_MODE_TO_FILENAME")
internal val ALLOW_SKIP_IF_LARGER = booleanPreferencesKey("ALLOW_SKIP_IF_LARGER")
internal val ASCII_CUSTOM_GRADIENTS = stringSetPreferencesKey("ASCII_CUSTOM_GRADIENTS")
internal val IS_LAUNCHER_MODE = booleanPreferencesKey("IS_LAUNCHER_MODE")
internal val SPOT_HEAL_MODE = intPreferencesKey("SPOT_HEAL_MODE")