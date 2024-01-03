package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSharpenFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.sharpen,
    value = value,
    valueRange = -4f..4f
), Filter.Sharpen<Bitmap>