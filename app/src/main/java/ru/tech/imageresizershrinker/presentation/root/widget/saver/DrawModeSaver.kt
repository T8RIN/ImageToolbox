package ru.tech.imageresizershrinker.presentation.root.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawMode

val DrawModeSaver: Saver<DrawMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawMode(it) }
)