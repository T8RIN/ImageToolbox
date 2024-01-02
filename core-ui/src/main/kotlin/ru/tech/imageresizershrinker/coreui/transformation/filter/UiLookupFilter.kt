package ru.tech.imageresizershrinker.coreui.transformation.filter


import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiLookupFilter(
    override val value: Float = 0f,
) : UiFilter<Float>(
    title = R.string.lookup,
    value = value,
    valueRange = -10f..10f
), Filter.Lookup<Bitmap>