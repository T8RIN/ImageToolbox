package ru.tech.imageresizershrinker.coreui.transformation.filter

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter


class UiSketchFilter(
    override val value: Unit = Unit,
) : UiFilter<Unit>(
    title = R.string.sketch,
    value = value,
    valueRange = 0f..0f
), Filter.Sketch<Bitmap>