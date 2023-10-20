package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter

class UiSaturationFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.saturation,
    value = value,
    valueRange = 0f..2f
), Filter.Saturation<Bitmap>