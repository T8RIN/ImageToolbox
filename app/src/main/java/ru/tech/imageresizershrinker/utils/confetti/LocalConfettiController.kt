package ru.tech.imageresizershrinker.utils.confetti

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.widget.other.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }