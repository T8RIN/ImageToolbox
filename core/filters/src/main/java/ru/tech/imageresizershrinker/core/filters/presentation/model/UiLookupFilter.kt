package ru.tech.imageresizershrinker.core.filters.presentation.model


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter


class UiLookupFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.lookup,
    value = value,
    valueRange = -10f..10f
), Filter.Lookup<Bitmap>