package ru.tech.imageresizershrinker.presentation.root.widget.saver

import androidx.compose.runtime.saveable.Saver
import ru.tech.imageresizershrinker.domain.image.draw.DrawPathMode

val DrawPathModeSaver: Saver<DrawPathMode, Int> = Saver(
    save = { it.ordinal },
    restore = { DrawPathMode(it) }
)