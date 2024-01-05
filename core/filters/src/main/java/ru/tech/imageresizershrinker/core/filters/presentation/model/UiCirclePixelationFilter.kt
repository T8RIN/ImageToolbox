package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R

class UiCirclePixelationFilter(
    override val value: Float = 24f,
) : UiFilter<Float>(
    title = R.string.circle_pixelation,
    value = value,
    valueRange = 5f..100f
), Filter.CirclePixelation<Bitmap>