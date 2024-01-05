package ru.tech.imageresizershrinker.core.ui.utils.confetti

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }