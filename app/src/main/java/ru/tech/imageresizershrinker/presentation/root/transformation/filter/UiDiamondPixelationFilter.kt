package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class UiDiamondPixelationFilter(
    override val value: Float = 24f,
) : UiFilter<Float>(
    title = R.string.diamond_pixelation,
    value = value,
    valueRange = 10f..60f
), Filter.DiamondPixelation<Bitmap>