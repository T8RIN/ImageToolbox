package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiEmbossFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.emboss,
    value = value,
    valueRange = 0f..4f
), Filter.Emboss<Bitmap>