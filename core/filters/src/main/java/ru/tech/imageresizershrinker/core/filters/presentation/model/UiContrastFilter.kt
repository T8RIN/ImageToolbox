package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiContrastFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.contrast,
    value = value,
    valueRange = 0f..4f
), Filter.Contrast<Bitmap>