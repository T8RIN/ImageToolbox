package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiPixelationFilter(
    override val value: Float = 25f,
) : UiFilter<Float>(
    title = R.string.pixelation,
    value = value,
    valueRange = 5f..50f
), Filter.Pixelation<Bitmap>