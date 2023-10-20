package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


class UiPosterizeFilter(
    override val value: Float = 10f,
) : UiFilter<Float>(
    title = R.string.posterize,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 1f..256f, 0)
    )
), Filter.Posterize<Bitmap>