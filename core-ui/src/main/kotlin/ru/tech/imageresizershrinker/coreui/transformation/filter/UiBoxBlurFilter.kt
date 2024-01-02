package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiBoxBlurFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.box_blur,
    value = value,
    valueRange = 0f..100f
), Filter.BoxBlur<Bitmap>