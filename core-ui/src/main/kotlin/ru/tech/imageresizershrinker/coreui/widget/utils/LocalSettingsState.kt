package ru.tech.imageresizershrinker.coreui.widget.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import ru.tech.imageresizershrinker.coreui.model.UiSettingsState

val LocalSettingsState = compositionLocalOf<UiSettingsState> { error("SettingsState not present") }

val LocalEditPresetsState = compositionLocalOf { mutableStateOf(false) }