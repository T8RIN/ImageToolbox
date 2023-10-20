package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class UiEnhancedDiamondPixelationFilter(
    override val value: Float = 48f,
) : UiFilter<Float>(
    title = R.string.enhanced_diamond_pixelation,
    value = value,
    valueRange = 20f..100f
), Filter.EnhancedDiamondPixelation<Bitmap>