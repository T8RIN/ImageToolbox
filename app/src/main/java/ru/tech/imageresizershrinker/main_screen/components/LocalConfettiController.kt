package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.widget.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }