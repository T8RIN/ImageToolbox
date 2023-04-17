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