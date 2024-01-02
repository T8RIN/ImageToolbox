package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBilaterialBlurFilter(
    override val value: Float = -8f,
) : UiFilter<Float>(
    title = R.string.bilaterial_blur,
    value = value,
    valueRange = -8f..30f
), Filter.BilaterialBlur<Bitmap>