package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiVibranceFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.vibrance,
    value = value,
    valueRange = -1f..1f
), Filter.Vibrance<Bitmap>