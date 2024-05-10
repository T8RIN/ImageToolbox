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

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object SettingKeys {
    val SAVE_FOLDER_URI = stringPreferencesKey("saveFolder")
    val NIGHT_MODE = intPreferencesKey("nightMode")
    val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
    val ALLOW_IMAGE_MONET = booleanPreferencesKey("imageMonet")
    val AMOLED_MODE = booleanPreferencesKey("amoledMode")
    val APP_COLOR_TUPLE = stringPreferencesKey("appColorTuple")
    val BORDER_WIDTH = floatPreferencesKey("borderWidth")
    val PRESETS = stringPreferencesKey("presets")
    val COLOR_TUPLES = stringPreferencesKey("color_tuples")
    val FAB_ALIGNMENT = intPreferencesKey("alignment")
    val SHOW_UPDATE_DIALOG = booleanPreferencesKey("showDialog")
    val FILENAME_PREFIX = stringPreferencesKey("filename")
    val SELECTED_EMOJI_INDEX = intPreferencesKey("emoji")
    val ADD_SIZE_TO_FILENAME = booleanPreferencesKey("add_size")
    val IMAGE_PICKER_MODE = intPreferencesKey("picker_mode")
    val SCREEN_ORDER = stringPreferencesKey("order")
    val EMOJI_COUNT = intPreferencesKey("em_count")
    val ADD_ORIGINAL_NAME_TO_FILENAME = booleanPreferencesKey("ADD_ORIGINAL_NAME")
    val ADD_SEQ_NUM_TO_FILENAME = booleanPreferencesKey("ADD_SEQ_NUM")
    val AUTO_CACHE_CLEAR = booleanPreferencesKey("auto_clear")
    val GROUP_OPTIONS_BY_TYPE = booleanPreferencesKey("group_options")
    val RANDOMIZE_FILENAME = booleanPreferencesKey("rand-filename")
    val SELECTED_FONT_INDEX = intPreferencesKey("font")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val ALLOW_CRASHLYTICS = booleanPreferencesKey("allow_crashlytics")
    val ALLOW_ANALYTICS = booleanPreferencesKey("allow_analytics")
    val ALLOW_BETAS = booleanPreferencesKey("allow_betas")
    val DRAW_CONTAINER_SHADOWS = booleanPreferencesKey("ALLOW_SHADOWS_INSTEAD_OF_BORDERS")
    val APP_OPEN_COUNT = intPreferencesKey("APP_OPEN_COUNT")
    val LOCK_DRAW_ORIENTATION = booleanPreferencesKey("LOCK_DRAW_ORIENTATION")
    val THEME_CONTRAST_LEVEL = doublePreferencesKey("THEME_CONTRAST_LEVEL")
    val THEME_STYLE = intPreferencesKey("THEME_STYLE")
    val INVERT_THEME = booleanPreferencesKey("INVERT_THEME")
    val SCREEN_SEARCH_ENABLED = booleanPreferencesKey("SCREEN_SEARCH_ENABLED")
    val DRAW_BUTTON_SHADOWS = booleanPreferencesKey("DRAW_BUTTON_SHADOWS")
    val DRAW_FAB_SHADOWS = booleanPreferencesKey("DRAW_FAB_SHADOWS")
    val DRAW_SWITCH_SHADOWS = booleanPreferencesKey("DRAW_SWITCH_SHADOWS")
    val DRAW_SLIDER_SHADOWS = booleanPreferencesKey("DRAW_SLIDER_SHADOWS")
    val DRAW_APPBAR_SHADOWS = booleanPreferencesKey("DRAW_APPBAR_SHADOWS")
    val COPY_TO_CLIPBOARD_MODE = intPreferencesKey("COPY_TO_CLIPBOARD_MODE")
    val VIBRATION_STRENGTH = intPreferencesKey("VIBRATION_STRENGTH")
    val OVERWRITE_FILE = booleanPreferencesKey("OVERWRITE_FILE")
    val FILENAME_SUFFIX = stringPreferencesKey("FILENAME_SUFFIX")
    val IMAGE_SCALE_MODE = intPreferencesKey("IMAGE_SCALE_MODE")
    val MAGNIFIER_ENABLED = booleanPreferencesKey("MAGNIFIER_ENABLED")
    val EXIF_WIDGET_INITIAL_STATE = booleanPreferencesKey("EXIF_WIDGET_INITIAL_STATE")
    val INITIAL_OCR_CODES = stringPreferencesKey("INITIAL_OCR_CODES")
    val SCREENS_WITH_BRIGHTNESS_ENFORCEMENT =
        stringPreferencesKey("SCREENS_WITH_BRIGHTNESS_ENFORCEMENT")
    val CONFETTI_ENABLED = booleanPreferencesKey("CONFETTI_ENABLED")
    val SECURE_MODE = booleanPreferencesKey("SECURE_MODE")
    val USE_RANDOM_EMOJIS = booleanPreferencesKey("USE_RANDOM_EMOJIS")
    val ICON_SHAPE = intPreferencesKey("ICON_SHAPE")
    val USE_EMOJI_AS_PRIMARY_COLOR = booleanPreferencesKey("USE_EMOJI_AS_PRIMARY_COLOR")
    val DRAG_HANDLE_WIDTH = intPreferencesKey("DRAG_HANDLE_WIDTH")
    val CONFETTI_TYPE = intPreferencesKey("CONFETTI_TYPE")
    val ALLOW_AUTO_PASTE = booleanPreferencesKey("ALLOW_AUTO_PASTE")
    val CONFETTI_HARMONIZER = intPreferencesKey("CONFETTI_HARMONIZER")
    val CONFETTI_HARMONIZATION_LEVEL = floatPreferencesKey("CONFETTI_HARMONIZATION_LEVEL")
    val SKIP_IMAGE_PICKING = booleanPreferencesKey("SKIP_IMAGE_PICKER")
    val GENERATE_PREVIEWS = booleanPreferencesKey("GENERATE_PREVIEWS")
    val SHOW_SETTINGS_IN_LANDSCAPE = booleanPreferencesKey("SHOW_SETTINGS_IN_LANDSCAPE")
    val USE_FULLSCREEN_SETTINGS = booleanPreferencesKey("USE_FULLSCREEN_SETTINGS")
    val SWITCH_TYPE = intPreferencesKey("SWITCH_TYPE")
    val DEFAULT_DRAW_LINE_WIDTH = floatPreferencesKey("DEFAULT_DRAW_LINE_WIDTH")
    val ONE_TIME_SAVE_LOCATIONS = stringPreferencesKey("ONE_TIME_SAVE_LOCATIONS")
    val OPEN_EDIT_INSTEAD_OF_PREVIEW = booleanPreferencesKey("OPEN_EDIT_INSTEAD_OF_PREVIEW")
}