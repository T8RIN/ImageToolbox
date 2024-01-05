package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiEmbossFilter(
    override val value: Float = 1f,
) : UiFilter<Float>(
    title = R.string.emboss,
    value = value,
    valueRange = 0f..4f
), Filter.Emboss<Bitmap>