package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.FilterParam


class UiSobelEdgeDetectionFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.sobel_edge,
    value = value,
    paramsInfo = listOf(
        FilterParam(title = R.string.line_width, valueRange = 1f..25f, roundTo = 0)
    )
), Filter.SobelEdgeDetection<Bitmap>