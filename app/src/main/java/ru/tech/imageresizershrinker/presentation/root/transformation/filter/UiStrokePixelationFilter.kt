package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class UiStrokePixelationFilter(
    override val value: Float = 32f,
) : UiFilter<Float>(
    title = R.string.stroke_pixelation,
    value = value,
    valueRange = 5f..75f
), Filter.StrokePixelation<Bitmap>