package ru.tech.imageresizershrinker.core.ui.widget.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import ru.tech.imageresizershrinker.core.ui.model.UiSettingsState

val LocalSettingsState = compositionLocalOf<UiSettingsState> { error("SettingsState not present") }

val LocalEditPresetsState = compositionLocalOf { mutableStateOf(false) }