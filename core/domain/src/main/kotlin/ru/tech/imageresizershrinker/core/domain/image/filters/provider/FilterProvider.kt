package ru.tech.imageresizershrinker.core.domain.image.filters.provider

import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter

interface FilterProvider<Image> {

    fun filterToTransformation(
        filter: Filter<Image, *>
    ): Transformation<Image>

}