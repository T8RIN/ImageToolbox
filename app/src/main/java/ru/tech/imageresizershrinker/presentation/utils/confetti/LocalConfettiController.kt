package ru.tech.imageresizershrinker.presentation.utils.confetti

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.presentation.widget.other.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }