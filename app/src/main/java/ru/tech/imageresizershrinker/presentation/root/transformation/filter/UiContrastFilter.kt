package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiContrastFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.contrast,
    value = value,
    valueRange = 0f..4f
), Filter.Contrast<Bitmap>