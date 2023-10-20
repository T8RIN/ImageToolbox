package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiBrightnessFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.brightness,
    value = value,
    valueRange = (-1f)..1f
), Filter.Brightness<Bitmap>