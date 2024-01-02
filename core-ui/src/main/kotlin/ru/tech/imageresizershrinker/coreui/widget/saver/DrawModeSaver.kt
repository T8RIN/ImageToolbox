package ru.tech.imageresizershrinker.coreui.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.coredomain.image.draw.DrawMode

val DrawModeSaver: Saver<DrawMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawMode(it) }
)