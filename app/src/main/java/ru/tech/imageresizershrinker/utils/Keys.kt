package ru.tech.imageresizershrinker.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

val SAVE_FOLDER = stringPreferencesKey("saveFolder")
val NIGHT_MODE = intPreferencesKey("nightMode")
val DYNAMIC_COLORS = booleanPreferencesKey("dynamicColors")
val IMAGE_MONET = booleanPreferencesKey("imageMonet")
val AMOLED_MODE = booleanPreferencesKey("amoledMode")
val APP_COLOR = stringPreferencesKey("appColorTuple")
val BORDER_WIDTH = stringPreferencesKey("borderWidth")
val PRESETS = stringPreferencesKey("presets")
val COLOR_TUPLES = stringPreferencesKey("color_tuples")
val ALIGNMENT = intPreferencesKey("alignment")
val SHOW_DIALOG = booleanPreferencesKey("showDialog")
val FILENAME_PREFIX = stringPreferencesKey("filename")
val EMOJI = intPreferencesKey("emoji")
val ADD_SIZE = booleanPreferencesKey("add_size")
val PICKER_MODE = intPreferencesKey("picker_mode")
val ORDER = stringPreferencesKey("order")