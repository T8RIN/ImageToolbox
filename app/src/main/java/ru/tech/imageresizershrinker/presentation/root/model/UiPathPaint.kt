package ru.tech.imageresizershrinker.presentation.root.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode
import ru.tech.imageresizershrinker.domain.image.draw.PathPaint
import ru.tech.imageresizershrinker.domain.image.draw.Pt
import ru.tech.imageresizershrinker.domain.image.draw.pt
import ru.tech.imageresizershrinker.domain.model.IntegerSize

data class UiPathPaint(
    override val path: Path,
    override val strokeWidth: Pt,
    override val brushSoftness: Pt,
    override val drawColor: Color = Color.Transparent,
    override val isErasing: Boolean,
    override val drawMode: DrawMode = DrawMode.Pen,
    override val canvasSize: IntegerSize
) : PathPaint<Path, Color>

val PtSaver: Saver<Pt, Float> = Saver(
    save = {
        it.value
    },
    restore = {
        it.pt
    }
)