package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R

class UiDiamondPixelationFilter(
    override val value: Float = 24f,
) : UiFilter<Float>(
    title = R.string.diamond_pixelation,
    value = value,
    valueRange = 10f..60f
), Filter.DiamondPixelation<Bitmap>