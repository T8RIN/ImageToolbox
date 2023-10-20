package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiSharpenFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sharpen,
    value = value,
    valueRange = -4f..4f
), Filter.Sharpen<Bitmap>