package ru.tech.imageresizershrinker.domain.image.filters.provider

import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.image.filters.Filter

interface FilterProvider<Image> {

    fun filterToTransformation(
        filter: Filter<Image, *>
    ): Transformation<Image>

}