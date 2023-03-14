package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalNightMode = compositionLocalOf<Int> { error("Not present Night Mode") }

val LocalDynamicColors = compositionLocalOf<Boolean> { error("Not present Dynamic Colors") }

val LocalAllowChangeColorByImage =
    compositionLocalOf<Boolean> { error("Not present Dynamic Colors") }

@Composable
fun Int.toMode(): Boolean = when (this) {
    0 -> true
    1 -> false
    else -> isSystemInDarkTheme()
}