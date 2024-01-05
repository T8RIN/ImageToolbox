package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBrightnessFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.brightness,
    value = value,
    valueRange = (-1f)..1f
), Filter.Brightness<Bitmap>