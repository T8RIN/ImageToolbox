package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam


class UiZoomBlurFilter(
    override val value: Triple<Float, Float, Float> = Triple(0.5f, 0.5f, 5f),
) : UiFilter<Triple<Float, Float, Float>>(
    title = R.string.zoom_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.blur_center_x, 0f..1f, 2),
        FilterParam(R.string.blur_center_y, 0f..1f, 2),
        FilterParam(R.string.blur_size, 0f..10f, 2)
    )
), Filter.ZoomBlur<Bitmap>