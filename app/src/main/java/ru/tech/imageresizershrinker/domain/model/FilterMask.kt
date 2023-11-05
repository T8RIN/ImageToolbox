package ru.tech.imageresizershrinker.domain.model

import ru.tech.imageresizershrinker.domain.image.filters.Filter

interface FilterMask<Image, Path, Paint> {
    val path: Path
    val paint: Paint
    val filters: List<Filter<Image, *>>
}