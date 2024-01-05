package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiSepiaFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sepia,
    value = value,
    valueRange = 0f..1f
), Filter.Sepia<Bitmap>