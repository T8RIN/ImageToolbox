package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBilaterialBlurFilter(
    override val value: Float = -8f,
) : UiFilter<Float>(
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
), Filter.BilaterialBlur<Bitmap>