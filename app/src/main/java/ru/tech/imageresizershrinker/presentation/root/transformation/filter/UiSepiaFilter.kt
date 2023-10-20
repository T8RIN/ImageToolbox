package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiSepiaFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sepia,
    value = value,
    valueRange = 0f..1f
), Filter.Sepia<Bitmap>