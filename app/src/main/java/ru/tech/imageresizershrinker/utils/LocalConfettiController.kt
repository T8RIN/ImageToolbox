package ru.tech.imageresizershrinker.utils

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.widget.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }