package ru.tech.imageresizershrinker.coreui.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.coredomain.image.draw.DrawPathMode

val DrawPathModeSaver: Saver<DrawPathMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawPathMode(it) }
)