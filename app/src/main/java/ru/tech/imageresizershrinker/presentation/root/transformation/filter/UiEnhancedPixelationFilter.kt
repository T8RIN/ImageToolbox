package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class UiEnhancedPixelationFilter(
    override val value: Float = 48f,
) : UiFilter<Float>(
    title = R.string.enhanced_pixelation,
    value = value,
    valueRange = 10f..100f
), Filter.EnhancedPixelation<Bitmap>