package ru.tech.imageresizershrinker.core.ui.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.core.domain.image.draw.DrawMode

val DrawModeSaver: Saver<DrawMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawMode(it) }
)