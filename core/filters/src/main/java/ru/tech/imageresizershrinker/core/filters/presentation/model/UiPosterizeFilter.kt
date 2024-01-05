package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter
import ru.tech.imageresizershrinker.coredomain.image.filters.FilterParam


class UiPosterizeFilter(
    override val value: Float = 10f,
) : UiFilter<Float>(
    title = R.string.posterize,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 1f..256f, 0)
    )
), Filter.Posterize<Bitmap>