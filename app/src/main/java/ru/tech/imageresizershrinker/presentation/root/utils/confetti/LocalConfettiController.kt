package ru.tech.imageresizershrinker.presentation.root.utils.confetti

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.presentation.root.widget.other.ToastHostState

val LocalConfettiController = compositionLocalOf { ToastHostState() }