package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter
import ru.tech.imageresizershrinker.coredomain.image.filters.FilterParam


class UiSobelEdgeDetectionFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.sobel_edge,
    value = value,
    paramsInfo = listOf(
        FilterParam(title = R.string.line_width, valueRange = 1f..25f, roundTo = 0)
    )
), Filter.SobelEdgeDetection<Bitmap>