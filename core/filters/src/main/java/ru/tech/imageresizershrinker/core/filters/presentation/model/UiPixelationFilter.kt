package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiPixelationFilter(
    override val value: Float = 25f,
) : UiFilter<Float>(
    title = R.string.pixelation,
    value = value,
    valueRange = 5f..50f
), Filter.Pixelation<Bitmap>