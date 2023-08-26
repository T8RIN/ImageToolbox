package ru.tech.imageresizershrinker.presentation.draw_screen

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.DrawMode

val DrawModeSaver: Saver<DrawMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawMode(it) }
)