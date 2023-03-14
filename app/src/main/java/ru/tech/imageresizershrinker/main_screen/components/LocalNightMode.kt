package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalNightMode = compositionLocalOf<Int> { error("Not present Night Mode") }

@Composable
fun Int.toMode(): Boolean = when (LocalNightMode.current) {
    0 -> true
    1 -> false
    else -> isSystemInDarkTheme()
}