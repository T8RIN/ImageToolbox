package ru.tech.imageresizershrinker.domain.image.draw

import ru.tech.imageresizershrinker.domain.Domain
import ru.tech.imageresizershrinker.domain.model.IntegerSize

interface PathPaint<Path, Color> : Domain {
    val path: Path
    val strokeWidth: Pt
    val brushSoftness: Pt
    val drawColor: Color
    val isErasing: Boolean
    val drawMode: DrawMode
    val canvasSize: IntegerSize
}