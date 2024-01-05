package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.FilterParam


class UiDilationFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.dilation,
    value = value,
    paramsInfo = listOf(
        FilterParam(null, 0f..4f, 0)
    )
), Filter.Dilation<Bitmap>