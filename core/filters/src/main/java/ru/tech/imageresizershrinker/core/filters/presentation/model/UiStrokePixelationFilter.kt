package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter

class UiStrokePixelationFilter(
    override val value: Float = 32f,
) : UiFilter<Float>(
    title = R.string.stroke_pixelation,
    value = value,
    valueRange = 5f..75f
), Filter.StrokePixelation<Bitmap>