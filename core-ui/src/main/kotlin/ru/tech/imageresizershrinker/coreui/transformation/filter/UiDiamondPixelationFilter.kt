package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter

class UiDiamondPixelationFilter(
    override val value: Float = 24f,
) : UiFilter<Float>(
    title = R.string.diamond_pixelation,
    value = value,
    valueRange = 10f..60f
), Filter.DiamondPixelation<Bitmap>