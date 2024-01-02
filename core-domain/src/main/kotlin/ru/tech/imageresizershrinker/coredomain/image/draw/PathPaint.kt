package ru.tech.imageresizershrinker.coredomain.image.draw

import ru.tech.imageresizershrinker.coredomain.Domain
import ru.tech.imageresizershrinker.coredomain.model.IntegerSize

interface PathPaint<Path, Color> : Domain {
    operator fun component1() = path
    operator fun component2() = strokeWidth
    operator fun component3() = brushSoftness
    operator fun component4() = drawColor
    operator fun component5() = isErasing
    operator fun component6() = drawMode
    operator fun component7() = canvasSize
    operator fun component8() = drawPathMode


    val path: Path
    val strokeWidth: Pt
    val brushSoftness: Pt
    val drawColor: Color
    val isErasing: Boolean
    val drawMode: DrawMode
    val canvasSize: IntegerSize
    val drawPathMode: DrawPathMode
}