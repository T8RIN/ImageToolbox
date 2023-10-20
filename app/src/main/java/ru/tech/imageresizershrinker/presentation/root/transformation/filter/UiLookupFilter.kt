package ru.tech.imageresizershrinker.presentation.root.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.filters.Filter


class UiLookupFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.lookup,
    value = value,
    valueRange = -10f..10f
), Filter.Lookup<Bitmap>