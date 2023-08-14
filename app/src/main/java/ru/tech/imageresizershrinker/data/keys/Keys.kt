package ru.tech.imageresizershrinker.data.keys

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
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
    val RANDOMIZE_FILENAME = booleanPreferencesKey("rand-filename")
    val FONT = intPreferencesKey("font")
    val FONT_SCALE = floatPreferencesKey("font_scale")
}