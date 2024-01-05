package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiBilaterialBlurFilter(
    override val value: Float = -8f,
) : UiFilter<Float>(
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
), Filter.BilaterialBlur<Bitmap>