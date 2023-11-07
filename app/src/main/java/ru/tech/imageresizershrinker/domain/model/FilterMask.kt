package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.image.draw.PathPaint
import ru.tech.imageresizershrinker.domain.image.filters.Filter

interface FilterMask<Image, Path, Color> {
    val maskPaints: List<PathPaint<Path, Color>>
    val filters: List<Filter<Image, *>>
}