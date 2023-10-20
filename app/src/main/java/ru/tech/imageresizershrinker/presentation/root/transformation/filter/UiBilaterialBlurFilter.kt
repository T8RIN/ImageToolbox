package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiBilaterialBlurFilter(
    override val value: Float = -8f,
) : UiFilter<Float>(
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
), Filter.BilaterialBlur<Bitmap>