package ru.tech.imageresizershrinker.coreui.widget.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


val ColorSaver: Saver<Color, Int> = Saver(
    save = { it.toArgb() },
    restore = { Color(it) }
)