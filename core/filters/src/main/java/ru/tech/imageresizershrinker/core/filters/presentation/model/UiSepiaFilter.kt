package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSepiaFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sepia,
    value = value,
    valueRange = 0f..1f
), Filter.Sepia<Bitmap>