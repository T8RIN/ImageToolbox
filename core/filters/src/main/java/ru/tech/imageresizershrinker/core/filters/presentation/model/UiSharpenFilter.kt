package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.resources.R


class UiSharpenFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sharpen,
    value = value,
    valueRange = -4f..4f
), Filter.Sharpen<Bitmap>