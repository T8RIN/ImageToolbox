package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiOpacityFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.opacity,
    value = value,
    valueRange = 0f..1f
), Filter.Opacity<Bitmap>