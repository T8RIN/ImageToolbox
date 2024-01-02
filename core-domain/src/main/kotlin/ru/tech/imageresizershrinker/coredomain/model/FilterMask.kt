package ru.tech.imageresizershrinker.coredomain.model

import ru.tech.imageresizershrinker.coredomain.image.draw.PathPaint
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

interface FilterMask<Image, Path, Color> {
    val maskPaints: List<PathPaint<Path, Color>>
    val filters: List<Filter<Image, *>>
    val isInverseFillType: Boolean
}