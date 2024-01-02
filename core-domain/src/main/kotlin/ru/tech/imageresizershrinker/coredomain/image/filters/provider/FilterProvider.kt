package ru.tech.imageresizershrinker.coredomain.image.filters.provider

import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

interface FilterProvider<Image> {

    fun filterToTransformation(
        filter: Filter<Image, *>
    ): Transformation<Image>

}