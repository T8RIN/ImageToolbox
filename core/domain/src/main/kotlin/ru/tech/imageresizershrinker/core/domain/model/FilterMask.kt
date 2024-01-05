package ru.tech.imageresizershrinker.core.domain.model

import ru.tech.imageresizershrinker.core.domain.image.draw.PathPaint
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter

interface FilterMask<Image, Path, Color> {
    val maskPaints: List<PathPaint<Path, Color>>
    val filters: List<Filter<Image, *>>
    val isInverseFillType: Boolean
}