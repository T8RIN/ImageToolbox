package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

class UiEnhancedDiamondPixelationFilter(
    override val value: Float = 48f,
) : UiFilter<Float>(
    title = R.string.enhanced_diamond_pixelation,
    value = value,
    valueRange = 20f..100f
), Filter.EnhancedDiamondPixelation<Bitmap>