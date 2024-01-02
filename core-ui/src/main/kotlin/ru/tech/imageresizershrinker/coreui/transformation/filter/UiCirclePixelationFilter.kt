package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

class UiCirclePixelationFilter(
    override val value: Float = 24f,
) : UiFilter<Float>(
    title = R.string.circle_pixelation,
    value = value,
    valueRange = 5f..100f
), Filter.CirclePixelation<Bitmap>