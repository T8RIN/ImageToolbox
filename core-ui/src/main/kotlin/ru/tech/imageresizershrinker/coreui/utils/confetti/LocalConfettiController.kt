package ru.tech.imageresizershrinker.coreui.utils.confetti

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.coreui.widget.other.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }