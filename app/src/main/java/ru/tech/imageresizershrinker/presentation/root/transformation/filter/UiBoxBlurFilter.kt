package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiBoxBlurFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.box_blur,
    value = value,
    valueRange = 0f..100f
), Filter.BoxBlur<Bitmap>