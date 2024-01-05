package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter

class UiEnhancedDiamondPixelationFilter(
    override val value: Float = 48f,
) : UiFilter<Float>(
    title = R.string.enhanced_diamond_pixelation,
    value = value,
    valueRange = 20f..100f
), Filter.EnhancedDiamondPixelation<Bitmap>