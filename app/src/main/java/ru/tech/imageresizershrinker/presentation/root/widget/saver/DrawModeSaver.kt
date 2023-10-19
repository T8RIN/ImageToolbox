package ru.tech.imageresizershrinker.presentation.root.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode

val DrawModeSaver: Saver<DrawMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawMode(it) }
)