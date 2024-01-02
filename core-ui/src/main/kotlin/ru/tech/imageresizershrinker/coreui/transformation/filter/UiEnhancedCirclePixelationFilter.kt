package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiEnhancedCirclePixelationFilter(
    override val value: Float = 32f,
) : UiFilter<Float>(
    title = R.string.enhanced_circle_pixelation,
    value = value,
    valueRange = 15f..100f
), Filter.EnhancedCirclePixelation<Bitmap>